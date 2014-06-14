package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import gitflow.git.GitflowGitCommandResult;
import gitflow.models.FinishReleaseResultCode;
import gitflow.models.ReleaseTagMessageOrCancelModel;
import gitflow.ui.NotifyUtil;
import gitflow.ui.WorkflowUtil;
import org.jetbrains.annotations.NotNull;

public class FinishReleaseAction extends GitflowAction {

    FinishReleaseAction() {
        super("Finish Release");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        if (WorkflowUtil.areAllGitRepositoriesOnSameAndValidBranchOrNotify(this.gitflowGitRepository)) {
            showTagMessageInputDialog();
        }
    }

    protected void showTagMessageInputDialog() {
        final String releaseName = WorkflowUtil.getUniqueReleaseNameOrNotify(this.gitflowGitRepository);

        final ReleaseTagMessageOrCancelModel tagMessageModel = WorkflowUtil.getReleaseTagMessage(this.myProject, releaseName, null);

        final String tagMessage = tagMessageModel.getTagMessage();
        final boolean cancelAction = tagMessageModel.isCancel();

        if (!cancelAction) {
            performFinishReleaseCommandInBackground(releaseName, tagMessage);
        }
    }

    protected void performFinishReleaseCommandInBackground(final String releaseName, final String tagMessage) {
        new Task.Backgroundable(this.myProject, "Finishing release " + releaseName, false) {

            private FinishReleaseResultCode finishReleaseResultCode;

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                this.finishReleaseResultCode = performFinishReleaseCommand(releaseName, tagMessage);
            }

            @Override
            public void onSuccess() {
                super.onSuccess();
                // merge conflicts if necessary
                mergesBranchesIfMergeConflictOccured(this.finishReleaseResultCode, releaseName, tagMessage);
            }

        }.queue();
    }

    protected FinishReleaseResultCode performFinishReleaseCommand(final String releaseName, final String tagMessage) {
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(this.myProject);

        final GitflowGitCommandResult result = this.myGitflow.finishRelease(this.gitflowGitRepository, releaseName, tagMessage, errorLineHandler);

        if (result.success()) {
            NotifyUtil.notifyGitflowReleaseCommandSuccess(this.gitflowGitRepository, "The release '%s' was merged into the following git repositories:", releaseName);
            return FinishReleaseResultCode.SUCCESS;
        } else if (errorLineHandler.hasMergeError) {
            // (merge errors are handled in the onSuccess handler)
            return FinishReleaseResultCode.MERGE_CONFLICT;
        } else {
            NotifyUtil.notifyGitflowReleaseCommandFailed(this.gitflowGitRepository, "Finishing the release '%s' resulted in an error in the following git repositories:", releaseName, result);
            return FinishReleaseResultCode.FAILED;
        }
    }

    protected void mergesBranchesIfMergeConflictOccured(final FinishReleaseResultCode finishReleaseResultCode, final String releaseName, final String tagMessage) {
        if (finishReleaseResultCode == FinishReleaseResultCode.MERGE_CONFLICT) {

            final boolean mergeOfAllBranchesAndRepositoriesWasSuccessful = this.myGitflow.mergeBranches(this.gitflowGitRepository);

            if (mergeOfAllBranchesAndRepositoriesWasSuccessful) {
                performFinishReleaseCommandInBackground(releaseName, tagMessage);
            }
        }
    }

}