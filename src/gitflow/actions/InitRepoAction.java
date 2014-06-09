package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.util.Key;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import gitflow.GitflowInitOptions;
import gitflow.git.GitflowGitRepository;
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
                    performInitReposCommand(gitflowGitRepository, initOptions);
                }
            }.queue();
        }
    }

    protected void performInitReposCommand(final GitflowGitRepository repo, final GitflowInitOptions initOptions) {
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);
        final LineHandler localLineHandler = new LineHandler();

        for (GitRepository gitRepository : repo.gitRepositories()) {
            performInitRepoCommand(gitRepository, initOptions, localLineHandler, errorLineHandler);
        }
    }

    protected void performInitRepoCommand(final GitRepository repo, final GitflowInitOptions initOptions, final GitflowLineHandler localLineHandler, final GitflowLineHandler errorLineHandler) {
        final GitCommandResult result = this.myGitflow.initRepo(repo, initOptions, errorLineHandler, localLineHandler);

        if (result.success()) {
            String publishedFeatureMessage = String.format("Initialized gitflow repo %s", repo.getRoot().getCanonicalPath());
            NotifyUtil.notifySuccess(this.myProject, "", publishedFeatureMessage);
        } else {
            NotifyUtil.notifyError(this.myProject, "Error", "Please have a look at the Version Control console for more details");
        }

        //update the widget
        this.myProject.getMessageBus().syncPublisher(GitRepository.GIT_REPO_CHANGE).repositoryChanged(repo);
        repo.update();
    }

    private class LineHandler extends GitflowLineHandler {
        @Override
        public void onLineAvailable(String line, Key outputType) {
            if (line.contains("Already initialized for gitflow")) {
                myErrors.add("Repo already initialized for gitflow");
            }

        }
    }

}