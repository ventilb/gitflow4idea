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

public class StartReleaseAction extends GitflowAction {

    StartReleaseAction() {
        super("Start Release");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        if (WorkflowUtil.areAllGitRepositoriesOnSameAndValidBranchOrNotify(this.gitflowGitRepository)) {
            showReleaseNameInputDialog();
        }
    }

    protected void showReleaseNameInputDialog() {
        final GitNewBranchNameValidator gitNewBranchNameValidator = GitflowBranchUtil.createGitNewBranchNameValidator(this.gitflowGitRepository);

        final String releaseName = Messages.showInputDialog(this.myProject, "Enter the name of new release:", "New Release", Messages.getQuestionIcon(), "", gitNewBranchNameValidator);

        if (WorkflowUtil.isWorkflowUserInputNameValidOrNotify(this.gitflowGitRepository, releaseName, "You must provide a name for the release")) {
            new Task.Backgroundable(this.myProject, "Starting release " + releaseName, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    performStartReleaseCommand(releaseName);
                }
            }.queue();
        }
    }

    protected boolean performStartReleaseCommand(final String releaseName) {
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(this.myProject);

        final GitflowGitCommandResult result = this.myGitflow.startRelease(this.gitflowGitRepository, releaseName, errorLineHandler);

        final boolean startReleaseHotfixCommandWasSuccessful = result.success();

        if (startReleaseHotfixCommandWasSuccessful) {
            NotifyUtil.notifyGitflowReleaseCommandSuccess(this.gitflowGitRepository, "A new release '%s' was created in the following git repositories:", releaseName);
        } else {
            NotifyUtil.notifyGitflowReleaseCommandFailed(this.gitflowGitRepository, "Starting a new release '%s' resulted in an error in the following git repositories:", releaseName, result);
        }

        this.gitflowGitRepository.update();

        return startReleaseHotfixCommandWasSuccessful;
    }

}