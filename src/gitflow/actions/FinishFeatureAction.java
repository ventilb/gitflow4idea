package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import gitflow.GitflowConfigUtil;
import gitflow.git.GitflowGitCommandResult;
import gitflow.models.FinishFeatureResultCode;
import gitflow.models.FinishReleaseResultCode;
import gitflow.ui.NotifyUtil;
import gitflow.ui.WorkflowUtil;
import org.jetbrains.annotations.NotNull;

public class FinishFeatureAction extends GitflowAction {

    FinishFeatureAction() {
        super("Finish Feature");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        if (WorkflowUtil.areAllGitRepositoriesOnSameAndValidBranchOrNotify(this.gitflowGitRepository)) {
            prepareFinishFeatureCommand();
        }
    }

    protected void prepareFinishFeatureCommand() {
        final String featureName = WorkflowUtil.getUniqueFeatureNameOrNotify(this.gitflowGitRepository);

        performFinishFeatureActionInBackground(featureName);
    }

    protected void performFinishFeatureActionInBackground(final String featureName) {
        new Task.Backgroundable(this.myProject, "Finishing feature " + featureName, false) {

            private FinishFeatureResultCode finishFeatureResultCode;

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                this.finishFeatureResultCode = performFinishFeatureCommand(featureName);
            }

            @Override
            public void onSuccess() {
                super.onSuccess();

                //merge conflicts if necessary
                mergeBranchesIfMergeConflictOccured(this.finishFeatureResultCode, featureName);
            }
        }.queue();
    }

    protected FinishFeatureResultCode performFinishFeatureCommand(final String featureName) {
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(this.myProject);

        final GitflowGitCommandResult result = this.myGitflow.finishFeature(this.gitflowGitRepository, featureName, errorLineHandler);

        if (result.success()) {
            NotifyUtil.notifyGitflowFeatureCommandSuccess(this.gitflowGitRepository, "The feature '%s' was merged into the following git repositories:", featureName);
            return FinishFeatureResultCode.SUCCESS;
        } else if (errorLineHandler.hasMergeError) {
            // (merge errors are handled in the onSuccess handler)
            return FinishFeatureResultCode.MERGE_CONFLICT;
        } else {
            NotifyUtil.notifyGitflowFeatureCommandFailed(this.gitflowGitRepository, "Finishing the feature '%s' resulted in an error in the following git repositories:", featureName, result);
            return FinishFeatureResultCode.FAILED;
        }
    }

    protected void mergeBranchesIfMergeConflictOccured(final FinishFeatureResultCode finishFeatureResultCode, final String featureName) {
        if (finishFeatureResultCode == FinishFeatureResultCode.MERGE_CONFLICT) {

            final boolean mergeOfAllBranchesAndRepositoriesWasSuccessful = this.myGitflow.mergeBranches(this.gitflowGitRepository);

            if (mergeOfAllBranchesAndRepositoriesWasSuccessful) {
                performFinishFeatureActionInBackground(featureName);
            }
        }
    }

}