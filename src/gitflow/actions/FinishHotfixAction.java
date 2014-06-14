package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import gitflow.GitflowConfigurable;
import gitflow.git.GitflowGitCommandResult;
import gitflow.ui.NotifyUtil;
import gitflow.ui.WorkflowUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FinishHotfixAction extends GitflowAction {

    FinishHotfixAction() {
        super("Finish Hotfix");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        if (WorkflowUtil.areAllGitRepositoriesOnSameAndValidBranchOrNotify(this.gitflowGitRepository)) {
            showTagMessageInputDialog();
        }
    }

    protected void showTagMessageInputDialog() {
        final String hotfixName = WorkflowUtil.getUniqueHotfixNameOrNotify(this.gitflowGitRepository);

        if (hotfixName != null) {
            String defaultTagMessage = GitflowConfigurable.getCustomHotfixTagCommitMessage(this.myProject);
            defaultTagMessage = defaultTagMessage.replace("%name%", hotfixName);

            final String tagMessage = Messages.showInputDialog(this.myProject, "Enter the tag message:", "Finish Hotfix", Messages.getQuestionIcon(), defaultTagMessage, null);

            if (tagMessage != null) {
                new Task.Backgroundable(this.myProject, "Finishing hotfix " + hotfixName, false) {

                    public void run(@NotNull ProgressIndicator indicator) {
                        performFinishHotfixCommand(hotfixName, tagMessage);
                    }
                }.queue();
            }
        }
    }

    protected boolean performFinishHotfixCommand(final String hotfixName, final String tagMessage) {
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(this.myProject);
        GitflowGitCommandResult result = this.myGitflow.finishHotfix(this.gitflowGitRepository, hotfixName, tagMessage, errorLineHandler);

        final boolean finishHotfixCommandWasSuccessful = result.success();

        if (finishHotfixCommandWasSuccessful) {
            NotifyUtil.notifyGitflowWorkflowCommandSuccess(this.gitflowGitRepository, "The hotfix '%s' was merged into the following git repositories:", hotfixName);
        } else {
            NotifyUtil.notifyGitflowWorkflowCommandFailed(this.gitflowGitRepository, "Finishing the hotfix '%s' resulted in an error in the following git repositories:", hotfixName, result);
        }

        this.gitflowGitRepository.update();

        return finishHotfixCommandWasSuccessful;
    }

}