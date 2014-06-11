package gitflow.ui;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowId;
import git4idea.repo.GitRepository;
import gitflow.git.GitflowGitCommandResult;
import gitflow.git.GitflowGitRepository;
import gitflow.git.GitflowPerRepositoryReadConfig;
import gitflow.git.RepositoryConfig;

public class NotifyUtil {
    private static final NotificationGroup TOOLWINDOW_NOTIFICATION = NotificationGroup.toolWindowGroup(
            "Gitflow Errors", ToolWindowId.VCS, true);
    private static final NotificationGroup STICKY_NOTIFICATION = new NotificationGroup(
            "Gitflow Errors", NotificationDisplayType.STICKY_BALLOON, true);
    private static final NotificationGroup BALLOON_NOTIFICATION = new NotificationGroup(
            "Gitflow Notifications", NotificationDisplayType.BALLOON, true);

    public static void notifySuccess(Project project, String title, String message) {
        notify(NotificationType.INFORMATION, BALLOON_NOTIFICATION, project, title, message);
    }

    public static void notifyInfo(Project project, String title, String message) {
        notify(NotificationType.INFORMATION, TOOLWINDOW_NOTIFICATION, project, title, message);
    }

    public static void notifyError(Project project, String title, String message) {
        notify(NotificationType.ERROR, TOOLWINDOW_NOTIFICATION, project, title, message);
    }

    public static void notifyError(Project project, String title, Exception exception) {
        notify(NotificationType.ERROR, STICKY_NOTIFICATION, project, title, exception.getMessage());
    }

    private static void notify(NotificationType type, NotificationGroup group, Project project, String title, String message) {
        group.createNotification(title, message, type, null).notify(project);
    }

    public static void notifyGitflowWorkflowStarted(final GitflowGitRepository gitflowGitRepository, final String messageIntro, final String workflowUserInputName) {
        final Project project = gitflowGitRepository.getProject();
        final GitflowPerRepositoryReadConfig gitflowPerRepositoryReadConfig = gitflowGitRepository.getGitflowPerRepositoryReadConfig();

        final StringBuilder workflowStartedMessage = new StringBuilder(String.format(messageIntro, workflowUserInputName));

        workflowStartedMessage.append("<ul>");
        for (RepositoryConfig repositoryConfig : gitflowPerRepositoryReadConfig.repositoryConfigs()) {
            workflowStartedMessage.append("<li>")
                    .append(PrettyFormat.hotfixBranchAndRepositoryName(repositoryConfig, workflowUserInputName))
                    .append("</li>");
        }
        workflowStartedMessage.append("</ul>");

        NotifyUtil.notifySuccess(project, workflowUserInputName, workflowStartedMessage.toString());
    }

    public static void notifyGitflowWorkflowFailed(final GitflowGitRepository gitflowGitRepository, final String messageIntro, final String workflowUserInputName, final GitflowGitCommandResult result) {
        final Project project = gitflowGitRepository.getProject();
        final GitflowPerRepositoryReadConfig gitflowPerRepositoryReadConfig = gitflowGitRepository.getGitflowPerRepositoryReadConfig();

        final GitRepository[] failedGitRepositories = result.getFailedGitRepositories();

        final StringBuilder workflowFailedMessage = new StringBuilder(String.format(messageIntro, workflowUserInputName));

        workflowFailedMessage.append("<ul>");
        for (RepositoryConfig repositoryConfig : gitflowPerRepositoryReadConfig.repositoryConfigs(failedGitRepositories)) {
            workflowFailedMessage.append("<li>")
                    .append(PrettyFormat.hotfixBranchAndRepositoryName(repositoryConfig, workflowUserInputName))
                    .append("</li>");
        }
        workflowFailedMessage.append("</ul>Please also have a look at the Version Control console for more details.");

        NotifyUtil.notifyError(project, "Error", workflowFailedMessage.toString());
    }


}
