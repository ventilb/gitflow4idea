package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import gitflow.GitflowConfigUtil;
import gitflow.GitflowConfigurable;
import gitflow.models.ReleaseTagMessageOrCancelModel;
import gitflow.ui.NotifyUtil;
import gitflow.ui.WorkflowUtil;
import org.jetbrains.annotations.NotNull;

public class FinishReleaseAction extends GitflowAction {

    String customReleaseName = null;
    String customtagMessage = null;

    FinishReleaseAction() {
        super("Finish Release");
    }

    FinishReleaseAction(String name, String tagMessage) {
        super("Finish Release");
        customReleaseName = name;
        customtagMessage = tagMessage;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        if (WorkflowUtil.areAllGitRepositoriesOnSameAndValidBranchOrNotify(this.gitflowGitRepository)) {
            showTagMessageInputDialog();
        }
    }

    protected void showTagMessageInputDialog() {
        final AnActionEvent event = null;//e;

        final String releaseName = getReleaseName();

        final ReleaseTagMessageOrCancelModel tagMessageModel = WorkflowUtil.getReleaseTagMessage(this.myProject, releaseName, customtagMessage);

        final String tagMessage = tagMessageModel.getTagMessage();
        final boolean cancelAction = tagMessageModel.isCancel();

        if (!cancelAction) {

            new Task.Backgroundable(myProject, "Finishing release " + releaseName, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);

                    GitCommandResult result = myGitflow.finishRelease(repo, releaseName, tagMessage, errorLineHandler);

                    if (result.success()) {
                        String finishedReleaseMessage = String.format("The release branch '%s%s' was merged into '%s' and '%s'", featurePrefix, releaseName, developBranch, masterBranch);
                        NotifyUtil.notifySuccess(myProject, releaseName, finishedReleaseMessage);
                    } else if (errorLineHandler.hasMergeError) {
                        // (merge errors are handled in the onSuccess handler)
                    } else {
                        NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
                    }


                }

                @Override
                public void onSuccess() {
                    super.onSuccess();

                    //merge conflicts if necessary
//                    if (errorLineHandler.hasMergeError) {
//                        if (handleMerge()) {
//                            FinishReleaseAction completeFinisReleaseAction = new FinishReleaseAction(releaseName, tagMessage);
//                            completeFinisReleaseAction.actionPerformed(event);
//                        }
//                    }
                }

            }.queue();

        }
    }

    protected String getReleaseName() {
        final String releaseName = WorkflowUtil.getUniqueReleaseNameOrNotify(this.gitflowGitRepository);

         // Check if a release name was specified, otherwise take name from current branch
        return this.customReleaseName != null ? this.customReleaseName : releaseName;
    }

}