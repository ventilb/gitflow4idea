package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import git4idea.validators.GitNewBranchNameValidator;
import gitflow.GitflowBranchUtil;
import gitflow.git.GitflowGitCommandResult;
import gitflow.ui.NotifyUtil;
import gitflow.ui.WorkflowUtil;
import org.jetbrains.annotations.NotNull;

public class StartHotfixAction extends GitflowAction {

    StartHotfixAction() {
        super("Start Hotfix");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        if (WorkflowUtil.areAllGitRepositoriesOnSameAndValidBranchOrNotify(this.gitflowGitRepository)) {
            showHotfixNameInputDialog();
        }
    }

    protected void showHotfixNameInputDialog() {
        final GitNewBranchNameValidator gitNewBranchNameValidator = GitflowBranchUtil.createGitNewBranchNameValidator(this.gitflowGitRepository);

        final String hotfixName = Messages.showInputDialog(this.myProject, "Enter the name of the new hotfix:", "New Hotfix", Messages.getQuestionIcon(), "", gitNewBranchNameValidator);

        if (WorkflowUtil.isWorkflowUserInputNameValidOrNotify(this.gitflowGitRepository, hotfixName, "You must provide a name for the hotfix")) {
            new Task.Backgroundable(this.myProject, "Starting hotfix " + hotfixName, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    performStartHotfixCommand(hotfixName);
                }
            }.queue();
        }
    }

    protected void performStartHotfixCommand(final String hotfixName) {
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(this.myProject);

        final GitflowGitCommandResult result = this.myGitflow.startHotfix(this.gitflowGitRepository, hotfixName, errorLineHandler);

        if (result.success()) {
            NotifyUtil.notifyGitflowWorkflowCommandSuccess(this.gitflowGitRepository, "A new hotfix '%s' was created in the following git repositories:", hotfixName);
        } else {
            NotifyUtil.notifyGitflowWorkflowCommandFailed(this.gitflowGitRepository, "Starting a new hotfix '%s' resulted in an error in the following git repositories:", hotfixName, result);
        }

        this.gitflowGitRepository.update();
    }

}