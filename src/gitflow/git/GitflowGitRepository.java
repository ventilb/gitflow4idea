package gitflow.git;

import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements a git repository model for the gitflow plugin. For cleaner api design we hold all repository information
 * in this model and pass this model around.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 11:21
 */
public class GitflowGitRepository {

    private final List<GitRepository> gitRepositories = new LinkedList<GitRepository>();

    public void addGitRepository(@NotNull final GitRepository gitRepository) {
        if (!this.gitRepositories.contains(gitRepository)) {
            this.gitRepositories.add(gitRepository);
        }
    }

    public Iterable<GitRepository> gitRepositories() {
        return this.gitRepositories;
    }

    @NotNull
    public GitRepository getFirstGitRepository() {
        return this.gitRepositories.get(0);
    }

    public int getRepositoryCount() {
        return this.gitRepositories.size();
    }
}
