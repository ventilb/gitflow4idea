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

public class StartFeatureAction extends GitflowAction {

    public StartFeatureAction() {
        super("Start Feature");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        if (WorkflowUtil.areAllGitRepositoriesOnSameAndValidBranchOrNotify(this.gitflowGitRepository)) {
            showFeatureNameInputDialog();
        }
    }

    protected void showFeatureNameInputDialog() {
        final GitNewBranchNameValidator gitNewBranchNameValidator = GitflowBranchUtil.createGitNewBranchNameValidator(this.gitflowGitRepository);

        final String featureName = Messages.showInputDialog(this.myProject, "Enter the name of new feature:", "New Feature", Messages.getQuestionIcon(), "", gitNewBranchNameValidator);

        if (WorkflowUtil.isWorkflowUserInputNameValidOrNotify(this.gitflowGitRepository, featureName, "You must provide a name for the feature")) {
            new Task.Backgroundable(this.myProject, "Starting feature " + featureName, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    performStartFeatureCommand(featureName);
                }
            }.queue();
        }
    }

    protected boolean performStartFeatureCommand(final String featureName) {
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(this.myProject);

        final GitflowGitCommandResult result = this.myGitflow.startFeature(this.gitflowGitRepository, featureName, errorLineHandler);

        final boolean startFeatureCommandWasSuccessful = result.success();

        if (startFeatureCommandWasSuccessful) {
            NotifyUtil.notifyGitflowFeatureCommandSuccess(this.gitflowGitRepository, "A new feature '%s' was created in the following git repositories:", featureName);
        } else {
            NotifyUtil.notifyGitflowFeatureCommandFailed(this.gitflowGitRepository, "Starting a new feature '%s' resulted in an error in the following git repositories:", featureName, result);
        }

        this.gitflowGitRepository.update();

        return startFeatureCommandWasSuccessful;
    }
}