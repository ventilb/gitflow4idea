package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import gitflow.git.GitflowGitCommandResult;
import gitflow.ui.NotifyUtil;
import gitflow.ui.WorkflowUtil;
import org.jetbrains.annotations.NotNull;

public class PublishFeatureAction extends GitflowAction {
    public PublishFeatureAction() {
        super("Publish Feature");
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);

        if (WorkflowUtil.areAllGitRepositoriesOnSameAndValidBranchOrNotify(this.gitflowGitRepository)) {
            performPublishFeatureActionInBackground();
        }
    }

    protected void performPublishFeatureActionInBackground() {
        final String featureName = WorkflowUtil.getUniqueFeatureNameOrNotify(this.gitflowGitRepository);

        if (featureName != null) {
            new Task.Backgroundable(this.myProject, "Publishing feature " + featureName, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    performPublishFeatureAction(featureName);
                }
            }.queue();
        }
    }

    protected boolean performPublishFeatureAction(final String featureName) {
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(this.myProject);

        final GitflowGitCommandResult result = this.myGitflow.publishFeature(this.gitflowGitRepository, featureName, errorLineHandler);

        final boolean publishFeatureCommandWasSuccessful = result.success();

        if (publishFeatureCommandWasSuccessful) {
            NotifyUtil.notifyGitflowHotfixCommandSuccess(this.gitflowGitRepository, "The feature '%s' was published to the remote branches:", featureName);
        } else {
            NotifyUtil.notifyGitflowHotfixCommandFailed(this.gitflowGitRepository, "Publishing the feature '%s' to the remote branches failed for the remote branches:", featureName, result);
        }

        this.gitflowGitRepository.update();

        return publishFeatureCommandWasSuccessful;
    }
}