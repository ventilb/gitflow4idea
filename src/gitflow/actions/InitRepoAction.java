package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import gitflow.GitflowInitOptions;
import gitflow.GitflowInitRepoLineHandler;
import gitflow.git.GitflowGitCommandResult;
import gitflow.ui.GitflowInitOptionsDialog;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class InitRepoAction extends GitflowAction {

    InitRepoAction() {
        super("Init Repo");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        showGitflowInitOptionsDialog();
    }

    protected void showGitflowInitOptionsDialog() {
        GitflowInitOptionsDialog optionsDialog = new GitflowInitOptionsDialog(myProject, branchUtil.getLocalBranchNames());
        optionsDialog.show();

        if (optionsDialog.isOK()) {
            final GitflowInitOptions initOptions = optionsDialog.getOptions();

            new Task.Backgroundable(this.myProject, "Initializing repo", false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    performInitReposCommand(initOptions);
                }
            }.queue();
        }
    }

    protected boolean performInitReposCommand(final GitflowInitOptions initOptions) {
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(this.myProject);
        final GitflowInitRepoLineHandler localLineHandler = new GitflowInitRepoLineHandler ();

        final GitflowGitCommandResult result = this.myGitflow.initRepos(this.gitflowGitRepository, initOptions);

        if (result.success()) {
            NotifyUtil.notifyGitflowWorkflowCommandSuccess(this.gitflowGitRepository, "Initialized the following gitflow repositories:");
        } else {
            NotifyUtil.notifyGitflowWorkflowCommandFailed(this.gitflowGitRepository, "Initializing one or more gitflow repositories failed:", result);
        }

        notifyRepositoriesHaveChanged();

        // update the widget
        this.gitflowGitRepository.update();

        return result.success();
    }

    protected void notifyRepositoriesHaveChanged() {
        final GitRepositoryChangeListener gitRepositoryChangeListener = this.myProject.getMessageBus().syncPublisher(GitRepository.GIT_REPO_CHANGE);

        for (GitRepository gitRepository : this.gitflowGitRepository.gitRepositories()) {
            gitRepositoryChangeListener.repositoryChanged(gitRepository);
        }
    }
}