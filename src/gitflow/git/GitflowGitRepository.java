package gitflow.git;

import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
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

    private GitflowPerRepositoryReadConfig gitflowPerRepositoryReadConfigInstance = null;

    public void addGitRepository(@NotNull final GitRepository gitRepository) {
        if (!this.gitRepositories.contains(gitRepository)) {
            this.gitRepositories.add(gitRepository);
        }
    }

    public GitflowPerRepositoryReadConfig getGitflowPerRepositoryReadConfig() {
        if (this.gitflowPerRepositoryReadConfigInstance == null) {
            this.gitflowPerRepositoryReadConfigInstance = new GitflowPerRepositoryReadConfig(this);
        }
        return this.gitflowPerRepositoryReadConfigInstance;
    }

    /**
     * Returns an iterable of the managed git repositories for use in foreach-loops.
     *
     * @return the iterable
     */
    @NotNull
    public Iterable<GitRepository> gitRepositories() {
        return this.gitRepositories;
    }

    /**
     * Returns an immutable collection of the managed git repositories.
     *
     * @return git repositories
     */
    @NotNull
    public Collection<GitRepository> getGitRepositories() {
        return Collections.unmodifiableCollection(this.gitRepositories);
    }

    /**
     * Gets repository count.
     *
     * @return the repository count
     */
    public int getRepositoryCount() {
        return this.gitRepositories.size();
    }

    /**
     * Performs an update operation on each managed repository. This operation rereads the git configuration from the
     * filesystem.
     *
     * @see git4idea.repo.GitRepository#update()
     */
    public void update() {
        for (GitRepository gitRepository : this.gitRepositories) {
            gitRepository.update();
        }
    }
}
