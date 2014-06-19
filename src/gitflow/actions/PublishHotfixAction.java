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

public class PublishHotfixAction extends GitflowAction {
    PublishHotfixAction() {
        super("Publish Hotfix");
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        if (WorkflowUtil.areAllGitRepositoriesOnSameAndValidBranchOrNotify(this.gitflowGitRepository)) {
            performAsyncPublishHotfixAction();
        }
    }

    protected void performAsyncPublishHotfixAction() {
        final String hotfixName = WorkflowUtil.getUniqueHotfixNameOrNotify(this.gitflowGitRepository);

        if (hotfixName != null) {
            new Task.Backgroundable(this.myProject, "Publishing hotfix " + hotfixName, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    performPublishHotfixCommand(hotfixName);
                }
            }.queue();
        }
    }

    protected boolean performPublishHotfixCommand(final String hotfixName) {
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(this.myProject);

        final GitflowGitCommandResult result = this.myGitflow.publishHotfix(this.gitflowGitRepository, hotfixName, errorLineHandler);

        final boolean publishHotfixCommandWasSuccessful = result.success();

        if (publishHotfixCommandWasSuccessful) {
            NotifyUtil.notifyGitflowHotfixCommandSuccess(this.gitflowGitRepository, "The hotfix '%s' was published to the remote branches:", hotfixName);
        } else {
            NotifyUtil.notifyGitflowHotfixCommandFailed(this.gitflowGitRepository, "Publishing the hotfix '%s' to the remote branches failed for the remote branches:", hotfixName, result);
        }

        this.gitflowGitRepository.update();

        return publishHotfixCommandWasSuccessful;
    }
}