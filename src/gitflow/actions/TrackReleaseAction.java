package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import gitflow.git.GitflowGitCommandResult;
import gitflow.ui.NotifyUtil;
import gitflow.ui.WorkflowUtil;
import org.jetbrains.annotations.NotNull;

public class TrackReleaseAction extends GitflowAction {

    public TrackReleaseAction() {
        super("Track Release");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        performTrackReleaseActionInBackground();
    }

    protected void performTrackReleaseActionInBackground() {
        final String remoteBranchName = WorkflowUtil.getUniqueRemoteReleaseBranchNameOrNotify(this.gitflowGitRepository);

        if (remoteBranchName != null) {
            new Task.Backgroundable(this.myProject, "Tracking release for remote branch " + remoteBranchName, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    performTrackReleaseCommand(remoteBranchName);
                }
            }.queue();
        }
    }

    protected boolean performTrackReleaseCommand(final String remoteBranchName) {
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(this.myProject);

        final GitflowGitCommandResult result = this.myGitflow.trackRelease(this.gitflowGitRepository, remoteBranchName, errorLineHandler);

        final boolean performTrackReleaseCommandWasSuccessful = result.success();

        if (performTrackReleaseCommandWasSuccessful) {
            NotifyUtil.notifyGitflowReleaseCommandSuccess(this.gitflowGitRepository, "Tracking the remote branch '%s' was successful:", remoteBranchName);
        } else {
            NotifyUtil.notifyGitflowReleaseCommandFailed(this.gitflowGitRepository, "Tracking the remote branch '%s' failed:", remoteBranchName, result);
        }

        this.gitflowGitRepository.update();

        return performTrackReleaseCommandWasSuccessful;
    }

}