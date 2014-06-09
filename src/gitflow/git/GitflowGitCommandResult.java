package gitflow.git;

import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Implements a container to hold the git command results when a gitflow command are performed on multiple repositories.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 19:15
 */
public class GitflowGitCommandResult {

    private final Hashtable<GitRepository, GitCommandResult> gitRepositoryGitCommandResults = new Hashtable<GitRepository, GitCommandResult>();

    public void setGitCommandResultForGitRepository(@NotNull final GitRepository gitRepository, @NotNull final GitCommandResult gitCommandResult) {
        this.gitRepositoryGitCommandResults.put(gitRepository, gitCommandResult);
    }

    /**
     * Returns TRUE if and only if all executed git commands were executed successful, FALSE otherwise.
     *
     * @return TRUE if and only if all executed git commands were executed successful, FALSE otherwise
     */
    public boolean success() {
        for (GitCommandResult gitCommandResult : this.gitRepositoryGitCommandResults.values()) {
            if (!gitCommandResult.success()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns an array of the git repositories which failed when executing a git command. Returns an empty array if
     * all git repositories succeeded or if there is no command result.
     *
     * @return array of the failed git repositories
     */
    @NotNull
    public GitRepository[] getFailedGitRepositories() {
        final List<GitRepository> failedGitRepositories = new ArrayList<GitRepository>(this.gitRepositoryGitCommandResults.size());

        GitCommandResult gitCommandResult;
        for (GitRepository gitRepository : this.gitRepositoryGitCommandResults.keySet()) {
            gitCommandResult = this.gitRepositoryGitCommandResults.get(gitRepository);
            if (!gitCommandResult.success()) {
                failedGitRepositories.add(gitRepository);
            }
        }

        return failedGitRepositories.toArray(new GitRepository[failedGitRepositories.size()]);
    }
}
