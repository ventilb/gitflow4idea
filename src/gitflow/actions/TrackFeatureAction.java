package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import gitflow.git.GitflowGitCommandResult;
import gitflow.ui.NotifyUtil;
import gitflow.ui.WorkflowUtil;
import org.jetbrains.annotations.NotNull;

public class TrackFeatureAction extends GitflowAction {

    public TrackFeatureAction() {
        super("Track Feature");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        performTrackFeatureActionInBackground();
    }

    protected void performTrackFeatureActionInBackground() {
        final String featureBranchName = WorkflowUtil.getUniqueRemoteFeatureBranchNameOrNotify(this.gitflowGitRepository);

        if (featureBranchName != null) {
            new Task.Backgroundable(this.myProject, "Tracking feature for remote branch " + featureBranchName, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    performTrackFeatureCommand(featureBranchName);
                }
            }.queue();
        }
    }

    protected boolean performTrackFeatureCommand(final String remoteBranchName) {
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(this.myProject);

        final GitflowGitCommandResult result = this.myGitflow.trackFeature(this.gitflowGitRepository, remoteBranchName, errorLineHandler);

        final boolean trackFeatureCommandWasSuccessful = result.success();

        if (trackFeatureCommandWasSuccessful) {
            NotifyUtil.notifyGitflowFeatureCommandSuccess(this.gitflowGitRepository, "Tracking the remote branch '%s' was successful:", remoteBranchName);
        } else {
            NotifyUtil.notifyGitflowFeatureCommandFailed(this.gitflowGitRepository, "Tracking the remote branch '%s' failed:", remoteBranchName, result);
        }

        this.gitflowGitRepository.update();

        return trackFeatureCommandWasSuccessful;
    }
}