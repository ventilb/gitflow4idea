package gitflow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import gitflow.git.GitflowGitRepository;

/**
 * Implements a util class to deal with common gitflow workflow situations.
 *
 * @author <a href="mailto:mschulze@geneon.de">Manuel Schulze</a>
 * @since 11.06.14 - 00:45
 */
public class WorkflowUtil {
    public static boolean areAllGitRepositoriesOnSameAndValidBranchOrNotify(final GitflowGitRepository gitflowGitRepository) {
        final Project project = gitflowGitRepository.getProject();

        final boolean areAllGitRepositoriesOnSameAndValidBranch = gitflowGitRepository.areAllGitRepositoriesOnSameAndValidBranch();

        if (!areAllGitRepositoriesOnSameAndValidBranch) {
            NotifyUtil.notifyError(project, "Error", "Your git repositories are on different branches.");
        }

        return areAllGitRepositoriesOnSameAndValidBranch;
    }

    public static boolean isWorkflowUserInputNameValid(final String userInput) {
        return userInput != null && !userInput.trim().isEmpty();
    }

    public static boolean isWorkflowUserInputNameValidOrNotify(final GitflowGitRepository gitflowGitRepository, final String userInput, final String validationFailedMessage) {
        final Project project = gitflowGitRepository.getProject();
        final boolean isWorkflowUserInputNameValid = isWorkflowUserInputNameValid(userInput);

        if (!isWorkflowUserInputNameValid) {
            Messages.showWarningDialog(project, validationFailedMessage, "Whoops");
        }

        return isWorkflowUserInputNameValid;
    }
}
