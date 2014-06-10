package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import git4idea.repo.GitRepository;
import git4idea.validators.GitNewBranchNameValidator;
import gitflow.GitflowBranchUtil;
import gitflow.git.GitflowGitCommandResult;
import gitflow.git.GitflowPerRepositoryReadConfig;
import gitflow.git.RepositoryConfig;
import gitflow.ui.NotifyUtil;
import gitflow.ui.PrettyFormat;
import org.jetbrains.annotations.NotNull;

public class StartHotfixAction extends GitflowAction {

    StartHotfixAction() {
        super("Start Hotfix");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        if (this.gitflowGitRepository.areAllGitRepositoriesOnSameAndValidBranch()) {
            showHotfixNameInputDialog();
        } else {
            // TODO redundant code
            NotifyUtil.notifyError(this.myProject, "Error", "Your git repositories are on different branches.");
        }
    }

    protected void showHotfixNameInputDialog() {
        final GitNewBranchNameValidator gitNewBranchNameValidator = GitflowBranchUtil.createGitNewBranchNameValidator(this.gitflowGitRepository);

        final String hotfixName = Messages.showInputDialog(this.myProject, "Enter the name of the new hotfix:", "New Hotfix", Messages.getQuestionIcon(), "", gitNewBranchNameValidator);

        if (isHotfixNameValid(hotfixName)) {
            new Task.Backgroundable(this.myProject, "Starting hotfix " + hotfixName, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    performStartHotfixCommand(hotfixName);
                }
            }.queue();
        } else {
            Messages.showWarningDialog(this.myProject, "You must provide a name for the hotfix", "Whoops");
        }
    }

    protected void performStartHotfixCommand(final String hotfixName) {
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(this.myProject);

        final GitflowGitCommandResult result = this.myGitflow.startHotfix(this.gitflowGitRepository, hotfixName, errorLineHandler);

        if (result.success()) {
            notifyHotfixWasCreated(hotfixName);
        } else {
            notifyHotfixCreationFailed(hotfixName, result);
        }

        this.gitflowGitRepository.update();
    }

    protected boolean isHotfixNameValid(final String hotfixName) {
        return hotfixName != null && !hotfixName.trim().isEmpty();
    }

    protected void notifyHotfixWasCreated(final String hotfixName) {
        final GitflowPerRepositoryReadConfig gitflowPerRepositoryReadConfig = this.gitflowGitRepository.getGitflowPerRepositoryReadConfig();

        final StringBuilder startedHotfixMessage = new StringBuilder("A new hotfix '")
                .append(hotfixName)
                .append("' was created in the following git repositories:<ul>");
        for (RepositoryConfig repositoryConfig : gitflowPerRepositoryReadConfig.repositoryConfigs()) {
            startedHotfixMessage.append("<li>")
                    .append(PrettyFormat.hotfixBranchAndRepositoryName(repositoryConfig, hotfixName))
                    .append("</li>");
        }
        startedHotfixMessage.append("</ul>");

        NotifyUtil.notifySuccess(this.myProject, hotfixName, startedHotfixMessage.toString());
    }

    protected void notifyHotfixCreationFailed(final String hotfixName, final GitflowGitCommandResult result) {
        final GitflowPerRepositoryReadConfig gitflowPerRepositoryReadConfig = this.gitflowGitRepository.getGitflowPerRepositoryReadConfig();

        final GitRepository[] failedGitRepositories = result.getFailedGitRepositories();

        final StringBuilder startedHotfixErrorMessage = new StringBuilder("Starting a new hotfix '")
                .append(hotfixName)
                .append("' resulted in an error in the following git repositories:<ul>");
        for (RepositoryConfig repositoryConfig : gitflowPerRepositoryReadConfig.repositoryConfigs(failedGitRepositories)) {
            startedHotfixErrorMessage.append("<li>")
                    .append(PrettyFormat.hotfixBranchAndRepositoryName(repositoryConfig, hotfixName))
                    .append("</li>");
        }
        startedHotfixErrorMessage.append("</ul>Please also have a look at the Version Control console for more details.");

        NotifyUtil.notifyError(this.myProject, "Error", startedHotfixErrorMessage.toString());
    }
}