package gitflow.ui;

import git4idea.repo.GitRepository;
import gitflow.git.GitflowGitRepositoryUtil;
import gitflow.git.RepositoryConfig;
import org.jetbrains.annotations.NotNull;

/**
 * Provides utility methods to format gitflow objects.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 09.06.14 - 16:35
 */
public class PrettyFormat {

    public static String hotfixBranchAndRepositoryName(@NotNull final RepositoryConfig repositoryConfig, @NotNull final String hotfixName) {
        final GitRepository gitRepository = repositoryConfig.getGitRepository();

        final String hotfixPrefix = trimStringOrEmptyWhenNull(repositoryConfig.getHotfixPrefix());
        final String repositoryName = trimStringOrEmptyWhenNull(GitflowGitRepositoryUtil.getHumanReadableRepositoryName(gitRepository));

        return String.format("%s%s - %s", hotfixPrefix, hotfixName, repositoryName);
    }

    public static String hotfixProductionDevelopmentBranchAndRepositoryName(@NotNull final RepositoryConfig repositoryConfig, @NotNull final String hotfixName) {
        final GitRepository gitRepository = repositoryConfig.getGitRepository();

        final String productionBranchName = trimStringOrEmptyWhenNull(repositoryConfig.getProductionBranch());
        final String developmentBranchName = trimStringOrEmptyWhenNull(repositoryConfig.getDevelopmentBranch());

        final String hotfixPrefix = trimStringOrEmptyWhenNull(repositoryConfig.getHotfixPrefix());
        final String repositoryName = trimStringOrEmptyWhenNull(GitflowGitRepositoryUtil.getHumanReadableRepositoryName(gitRepository));

        return String.format("%s%s - %s (%s, %s)", hotfixPrefix, hotfixName, repositoryName, productionBranchName, developmentBranchName);
    }

    public static String trimStringOrEmptyWhenNull(final String aString) {
        return (aString == null ? "" : aString.trim());
    }
}
