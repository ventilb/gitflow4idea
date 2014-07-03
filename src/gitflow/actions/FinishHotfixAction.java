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

            final String tagMessage;
            if (GitflowConfigurable.dontTagHotfix(myProject)) {
                tagMessage = "";
            } else {
                tagMessage = Messages.showInputDialog(this.myProject, "Enter the tag message:", "Finish Hotfix", Messages.getQuestionIcon(), defaultTagMessage, null);
            }

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
        /*
        We must switch to the development branch before finish hotfix because git fails for for hotfix branches if no
        commit occured. In a multi module repository this can happen often because you don't touch all modules at the
        same time.
         */
        if (!WorkflowUtil.switchToDevelopmentBranchOrNotify(this.gitflowGitRepository)) {
            return false;
        }

        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(this.myProject);
        final GitflowGitCommandResult result = this.myGitflow.finishHotfix(this.gitflowGitRepository, hotfixName, tagMessage, errorLineHandler);

        final boolean finishHotfixCommandWasSuccessful = result.success();

        if (finishHotfixCommandWasSuccessful) {
            NotifyUtil.notifyGitflowHotfixCommandSuccess(this.gitflowGitRepository, "The hotfix '%s' was merged into the following git repositories:", hotfixName);
        } else {
            NotifyUtil.notifyGitflowHotfixCommandFailed(this.gitflowGitRepository, "Finishing the hotfix '%s' resulted in an error in the following git repositories:", hotfixName, result);
        }

        this.gitflowGitRepository.update();

        return finishHotfixCommandWasSuccessful;
    }

}