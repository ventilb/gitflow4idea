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
import org.jetbrains.annotations.NotNull;

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

    /**
     * Notifies the user that a gitflow workflow was successful. The {@code messageIntro} parameter should state which
     * gitflow command was successful.
     *
     * @param gitflowGitRepository the gitflow git repository
     * @param messageIntro         the message intro
     */
    public static void notifyGitflowWorkflowCommandSuccess(final GitflowGitRepository gitflowGitRepository, final String messageIntro) {
        final Project project = gitflowGitRepository.getProject();
        final GitflowPerRepositoryReadConfig gitflowPerRepositoryReadConfig = gitflowGitRepository.getGitflowPerRepositoryReadConfig();

        final StringBuilder workflowStartedMessage = new StringBuilder(messageIntro);

        workflowStartedMessage.append("<ul>");
        for (RepositoryConfig repositoryConfig : gitflowPerRepositoryReadConfig.repositoryConfigs()) {
            workflowStartedMessage.append("<li>")
                    .append(PrettyFormat.repositoryName(repositoryConfig))
                    .append("</li>");
        }
        workflowStartedMessage.append("</ul>");

        NotifyUtil.notifySuccess(project, "Success", workflowStartedMessage.toString());
    }

    /**
     * Notifies the user that a gitflow workflow has failed. The {@code messageIntro} parameter should state which
     * gitflow command failed.
     *
     * @param gitflowGitRepository   the gitflow git repository
     * @param messageIntro           the message intro
     * @param result                 the gitflow command result object
     */
    public static void notifyGitflowWorkflowCommandFailed(@NotNull final GitflowGitRepository gitflowGitRepository, @NotNull final String messageIntro, @NotNull final GitflowGitCommandResult result) {
        final Project project = gitflowGitRepository.getProject();
        final GitflowPerRepositoryReadConfig gitflowPerRepositoryReadConfig = gitflowGitRepository.getGitflowPerRepositoryReadConfig();

        final GitRepository[] failedGitRepositories = result.getFailedGitRepositories();

        final StringBuilder workflowFailedMessage = new StringBuilder(messageIntro);

        workflowFailedMessage.append("<ul>");
        for (RepositoryConfig repositoryConfig : gitflowPerRepositoryReadConfig.repositoryConfigs(failedGitRepositories)) {
            workflowFailedMessage.append("<li>")
                    .append(PrettyFormat.repositoryName(repositoryConfig))
                    .append("</li>");
        }
        workflowFailedMessage.append("</ul>Please also have a look at the Version Control console for more details.");

        NotifyUtil.notifyError(project, "Error", workflowFailedMessage.toString());
    }

    /**
     * Notifies the user that a gitflow workflow was successful. The {@code messageIntro} parameter should state which
     * gitflow command was successful. The string must contain a replacement parameter %s which will be replaced by
     * the {@code workflowUserInputValue} value such as a branch name.
     *
     * @param gitflowGitRepository   the gitflow git repository
     * @param messageIntro           the message intro
     * @param workflowUserInputValue the workflow user input name
     */
    public static void notifyGitflowWorkflowCommandSuccess(@NotNull final GitflowGitRepository gitflowGitRepository, @NotNull final String messageIntro, @NotNull final String workflowUserInputValue) {
        final Project project = gitflowGitRepository.getProject();
        final GitflowPerRepositoryReadConfig gitflowPerRepositoryReadConfig = gitflowGitRepository.getGitflowPerRepositoryReadConfig();

        final StringBuilder workflowStartedMessage = new StringBuilder(String.format(messageIntro, workflowUserInputValue));

        workflowStartedMessage.append("<ul>");
        for (RepositoryConfig repositoryConfig : gitflowPerRepositoryReadConfig.repositoryConfigs()) {
            workflowStartedMessage.append("<li>")
                    .append(PrettyFormat.hotfixBranchAndRepositoryName(repositoryConfig, workflowUserInputValue))
                    .append("</li>");
        }
        workflowStartedMessage.append("</ul>");

        NotifyUtil.notifySuccess(project, workflowUserInputValue, workflowStartedMessage.toString());
    }

    /**
     * Notifies the user that a gitflow workflow has failed. The {@code messageIntro} parameter should state which
     * gitflow command failed. The string must contain a replacement parameter %s which will be replaced by
     * the {@code workflowUserInputValue} value such as a branch name.
     *
     * @param gitflowGitRepository   the gitflow git repository
     * @param messageIntro           the message intro
     * @param workflowUserInputValue the workflow user input name
     * @param result                 the gitflow command result object
     */
    public static void notifyGitflowWorkflowCommandFailed(@NotNull final GitflowGitRepository gitflowGitRepository, @NotNull final String messageIntro, @NotNull final String workflowUserInputValue, @NotNull final GitflowGitCommandResult result) {
        final Project project = gitflowGitRepository.getProject();
        final GitflowPerRepositoryReadConfig gitflowPerRepositoryReadConfig = gitflowGitRepository.getGitflowPerRepositoryReadConfig();

        final GitRepository[] failedGitRepositories = result.getFailedGitRepositories();

        final StringBuilder workflowFailedMessage = new StringBuilder(String.format(messageIntro, workflowUserInputValue));

        workflowFailedMessage.append("<ul>");
        for (RepositoryConfig repositoryConfig : gitflowPerRepositoryReadConfig.repositoryConfigs(failedGitRepositories)) {
            workflowFailedMessage.append("<li>")
                    .append(PrettyFormat.hotfixBranchAndRepositoryName(repositoryConfig, workflowUserInputValue))
                    .append("</li>");
        }
        workflowFailedMessage.append("</ul>Please also have a look at the Version Control console for more details.");

        NotifyUtil.notifyError(project, "Error", workflowFailedMessage.toString());
    }
}
