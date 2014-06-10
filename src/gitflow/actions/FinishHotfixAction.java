package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import git4idea.repo.GitRepository;
import gitflow.GitflowConfigurable;
import gitflow.git.GitflowGitCommandResult;
import gitflow.git.GitflowPerRepositoryReadConfig;
import gitflow.git.RepositoryConfig;
import gitflow.ui.NotifyUtil;
import gitflow.ui.PrettyFormat;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FinishHotfixAction extends GitflowAction {

    FinishHotfixAction() {
        super("Finish Hotfix");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        if (this.gitflowGitRepository.areAllGitRepositoriesOnSameAndValidBranch()) {
            showTagMessageInputDialog();
        } else {
            NotifyUtil.notifyError(this.myProject, "Error", "Your git repositories are on different branches.");
        }
    }

    protected void showTagMessageInputDialog() {
        final Set<String> uniqueHotfixNames = this.gitflowGitRepository.getUniqueHotfixNamesFromCurrentBranches();

        if (uniqueHotfixNames.size() != 1) {
            NotifyUtil.notifyError(this.myProject, "Error", "The tracked git repositories have different hotfixes.");
            return;
        }

        final String hotfixName = uniqueHotfixNames.iterator().next();

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

    protected boolean performFinishHotfixCommand(final String hotfixName, final String tagMessage) {
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(this.myProject);
        GitflowGitCommandResult result = this.myGitflow.finishHotfix(this.gitflowGitRepository, hotfixName, tagMessage, errorLineHandler);

        final boolean finishHotfixCommandWasSuccessful = result.success();

        if (finishHotfixCommandWasSuccessful) {
            notifyHotfixWasFinished(hotfixName);
        } else {
            notifyHotfixFinishingFailed(hotfixName, result);
        }

        this.gitflowGitRepository.update();

        return finishHotfixCommandWasSuccessful;
    }

    protected void notifyHotfixWasFinished(final String hotfixName) {
        final GitflowPerRepositoryReadConfig gitflowPerRepositoryReadConfig = this.gitflowGitRepository.getGitflowPerRepositoryReadConfig();

        final StringBuilder startedHotfixMessage = new StringBuilder("The hotfix '")
                .append(hotfixName)
                .append("' was merged into the following git repositories:<ul>");
        for (RepositoryConfig repositoryConfig : gitflowPerRepositoryReadConfig.repositoryConfigs()) {
            startedHotfixMessage.append("<li>")
                    .append(PrettyFormat.hotfixProductionDevelopmentBranchAndRepositoryName(repositoryConfig, hotfixName))
                    .append("</li>");
        }
        startedHotfixMessage.append("</ul>");

        NotifyUtil.notifySuccess(this.myProject, hotfixName, startedHotfixMessage.toString());
    }

    protected void notifyHotfixFinishingFailed(final String hotfixName, final GitflowGitCommandResult result) {
        final GitflowPerRepositoryReadConfig gitflowPerRepositoryReadConfig = this.gitflowGitRepository.getGitflowPerRepositoryReadConfig();

        final GitRepository[] failedGitRepositories = result.getFailedGitRepositories();

        final StringBuilder startedHotfixErrorMessage = new StringBuilder("Finishing the hotfix '")
                .append(hotfixName)
                .append("' resulted in an error in the following git repositories:<ul>");
        for (RepositoryConfig repositoryConfig : gitflowPerRepositoryReadConfig.repositoryConfigs(failedGitRepositories)) {
            startedHotfixErrorMessage.append("<li>")
                    .append(PrettyFormat.hotfixProductionDevelopmentBranchAndRepositoryName(repositoryConfig, hotfixName))
                    .append("</li>");
        }
        startedHotfixErrorMessage.append("</ul>Please also have a look at the Version Control console for more details.");

        NotifyUtil.notifyError(this.myProject, "Error", startedHotfixErrorMessage.toString());
    }

}