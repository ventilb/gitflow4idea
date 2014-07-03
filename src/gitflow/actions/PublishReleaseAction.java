package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.commands.GitCommandResult;
import gitflow.GitflowConfigUtil;
import gitflow.git.GitflowGitCommandResult;
import gitflow.ui.NotifyUtil;
import gitflow.ui.WorkflowUtil;
import org.jetbrains.annotations.NotNull;

public class PublishReleaseAction extends GitflowAction {

    public PublishReleaseAction() {
        super("Publish Release");
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        if (WorkflowUtil.areAllGitRepositoriesOnSameAndValidBranchOrNotify(this.gitflowGitRepository)) {
            performAsyncPublishReleaseAction();
        }
    }

    protected void performAsyncPublishReleaseAction() {
        final String releaseName = WorkflowUtil.getUniqueReleaseNameOrNotify(this.gitflowGitRepository);

        if (releaseName != null) {
            new Task.Backgroundable(myProject, "Publishing release " + releaseName, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    performPublishReleaseCommand(releaseName);
                }
            }.queue();
        }
    }

    protected boolean performPublishReleaseCommand(final String releaseName) {
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);

        final GitflowGitCommandResult result = this.myGitflow.publishRelease(this.gitflowGitRepository, releaseName, errorLineHandler);

        final boolean publishReleaseCommandWasSuccessful = result.success();

        if (publishReleaseCommandWasSuccessful) {
            NotifyUtil.notifyGitflowReleaseCommandSuccess(this.gitflowGitRepository, "The release '%s' was published to the remote branches:", releaseName);
        } else {
            NotifyUtil.notifyGitflowReleaseCommandFailed(this.gitflowGitRepository, "Publishing the hotfix '%s' to the remote branches failed for the remote branches:", releaseName, result);
        }

        this.gitflowGitRepository.update();

        return publishReleaseCommandWasSuccessful;
    }
}