package gitflow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import gitflow.GitflowConfigurable;
import gitflow.git.GitflowGitRepository;
import gitflow.models.ReleaseTagMessageOrCancelModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * Implements a util class to deal with common gitflow workflow situations.
 *
 * @author <a href="mailto:mschulze@geneon.de">Manuel Schulze</a>
 * @since 11.06.14 - 00:45
 */
public class WorkflowUtil {

    @NotNull
    public static ReleaseTagMessageOrCancelModel getReleaseTagMessage(@NotNull final Project project, @NotNull final String releaseName, @Nullable final String customtagMessage) {
        final String defaultTagMessage = getDefaultTagMessage(project, releaseName);

        boolean cancel = false;
        String tagMessage;
        if (GitflowConfigurable.dontTagRelease(project)) {
            tagMessage = "";
        } else if (customtagMessage != null) {
            //probably repeating the release finish after a merge
            tagMessage = customtagMessage;
        } else {
            String tagMessageDraft = Messages.showInputDialog(project, "Enter the tag message:", "Finish Release", Messages.getQuestionIcon(), defaultTagMessage, null);
            if (tagMessageDraft == null) {
                cancel = true;
                tagMessage = "";
            } else {

                tagMessage = tagMessageDraft;
            }
        }

        return new ReleaseTagMessageOrCancelModel(tagMessage, cancel);
    }

    @NotNull
    public static String getDefaultTagMessage(@NotNull final Project project, @NotNull final String releaseName) {
        final String defaultTagMessage = GitflowConfigurable.getCustomTagCommitMessage(project);
        return defaultTagMessage.replace("%name%", releaseName);
    }

    @Nullable
    public static String getUniqueRemoteBranchNameOrNotify(@NotNull final GitflowGitRepository gitflowGitRepository) {
        final Project project = gitflowGitRepository.getProject();
        final Collection<String> releaseBranchNames = gitflowGitRepository.getUniqueRemoteReleaseBranchNames();

        String branchName = null;

        if (releaseBranchNames.size() > 0) {
            branchName = showGitflowBranchChooseDialog(project, releaseBranchNames);
        } else {
            NotifyUtil.notifyError(project, "Error", "No remote branches");
        }

        return branchName;
    }

    @Nullable
    public static String showGitflowBranchChooseDialog(@NotNull final Project project, @NotNull final Collection<String> releaseBranchNames) {
        final GitflowBranchChooseDialog branchChooseDialog = new GitflowBranchChooseDialog(project, releaseBranchNames);

        String branchName = null;

        branchChooseDialog.show();
        if (branchChooseDialog.isOK()) {
            branchName = branchChooseDialog.getSelectedBranchName();
        }

        return branchName;
    }

    @Nullable
    public static String getUniqueHotfixNameOrNotify(@NotNull final GitflowGitRepository gitflowGitRepository) {
        final Project project = gitflowGitRepository.getProject();

        final Set<String> uniqueHotfixNames = gitflowGitRepository.getUniqueHotfixNamesFromCurrentBranches();

        if (uniqueHotfixNames.size() != 1) {
            NotifyUtil.notifyError(project, "Error", "The tracked git repositories have different hotfixes.");
            return null;
        }

        return uniqueHotfixNames.iterator().next();
    }

    @Nullable
    public static String getUniqueReleaseNameOrNotify(@NotNull final GitflowGitRepository gitflowGitRepository) {
        final Project project = gitflowGitRepository.getProject();

        final Set<String> uniqueReleaseNames = gitflowGitRepository.getUniqueReleaseNamesFromCurrentBranches();

        if (uniqueReleaseNames.size() != 1) {
            NotifyUtil.notifyError(project, "Error", "The tracked git repositories have different releases.");
            return null;
        }

        return uniqueReleaseNames.iterator().next();
    }

    @Nullable
    public static String getUniqueFeatureNameOrNotify(@NotNull final GitflowGitRepository gitflowGitRepository) {
        final Project project = gitflowGitRepository.getProject();

        final Set<String> uniqueFeatureNames = gitflowGitRepository.getUniqueFeatureNamesFromCurrentBranches();

        if (uniqueFeatureNames.size() != 1) {
            NotifyUtil.notifyError(project, "Error", "The tracked git repositories have different releases.");
            return null;
        }

        return uniqueFeatureNames.iterator().next();
    }

    public static boolean areAllGitRepositoriesOnSameAndValidBranchOrNotify(@NotNull final GitflowGitRepository gitflowGitRepository) {
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

    public static boolean isWorkflowUserInputNameValidOrNotify(@NotNull final GitflowGitRepository gitflowGitRepository, final String userInput, final String validationFailedMessage) {
        final Project project = gitflowGitRepository.getProject();
        final boolean isWorkflowUserInputNameValid = isWorkflowUserInputNameValid(userInput);

        if (!isWorkflowUserInputNameValid) {
            Messages.showWarningDialog(project, validationFailedMessage, "Whoops");
        }

        return isWorkflowUserInputNameValid;
    }

    public static boolean switchToDevelopmentBranchOrNotify(final GitflowGitRepository gitflowGitRepository) {
        final Project project = gitflowGitRepository.getProject();
        final boolean switchToDevelopmentBranchWasSuccessful = gitflowGitRepository.switchToDevelopmentBranch();

        if (!switchToDevelopmentBranchWasSuccessful) {
            NotifyUtil.notifyError(project, "Error", "Some repositories failed to switch to development branch.");
        }

        return switchToDevelopmentBranchWasSuccessful;
    }
}
