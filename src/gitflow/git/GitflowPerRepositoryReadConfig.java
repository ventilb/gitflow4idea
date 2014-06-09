package gitflow.git;

import git4idea.repo.GitRepository;
import gitflow.GitflowConfigUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Provides a class to get read access to the gitflow configuration values. This class is intended to help with
 * situations where specific read access to the configuration values is required such as logging failed repositories.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 22:29
 */
public class GitflowPerRepositoryReadConfig {

    private final GitflowGitRepository gitflowGitRepository;

    private final Hashtable<GitRepository, RepositoryConfig> cache = new Hashtable<GitRepository, RepositoryConfig>();

    public GitflowPerRepositoryReadConfig(@NotNull final GitflowGitRepository gitflowGitRepository) {
        this.gitflowGitRepository = gitflowGitRepository;
    }

    /**
     * Returns the repository config for the specified git repository.
     *
     * @param gitRepository the git repository
     * @return repository config
     */
    @NotNull
    public RepositoryConfig getRepositoryConfig(@NotNull final GitRepository gitRepository) {
        final RepositoryConfig gitflowInitOptions;

        if (this.cache.containsKey(gitRepository)) {
            gitflowInitOptions = this.cache.get(gitRepository);
        } else {
            gitflowInitOptions = createRepositoryConfig(gitRepository);
            this.cache.put(gitRepository, gitflowInitOptions);
        }

        return gitflowInitOptions;
    }

    /**
     * Returns an iterable over all repository configs.
     *
     * @return iterable over repository configs
     */
    @NotNull
    public Iterable<RepositoryConfig> repositoryConfigs() {
        return new Iterable<RepositoryConfig>() {
            @Override
            public Iterator<RepositoryConfig> iterator() {
                return new RepositoryConfigsIterator(GitflowPerRepositoryReadConfig.this);
            }
        };
    }

    /**
     * Returns an iterable over the repository configs to the specified git repositories.
     *
     * @param gitRepositories the git repositories
     * @return iterable over repository configs
     */
    @NotNull
    public Iterable<RepositoryConfig> repositoryConfigs(final GitRepository... gitRepositories) {
        return new Iterable<RepositoryConfig>() {
            @Override
            public Iterator<RepositoryConfig> iterator() {
                return new RepositoryConfigsIterator(GitflowPerRepositoryReadConfig.this, Arrays.asList(gitRepositories).iterator());
            }
        };
    }

    @NotNull
    protected RepositoryConfig createRepositoryConfig(@NotNull final GitRepository gitRepository) {
        final RepositoryConfig repositoryConfig = new RepositoryConfig(gitRepository);
        repositoryConfig.setProductionBranch(GitflowConfigUtil.getMasterBranch(gitRepository));
        repositoryConfig.setDevelopmentBranch(GitflowConfigUtil.getDevelopBranch(gitRepository));

        repositoryConfig.setHotfixPrefix(GitflowConfigUtil.getHotfixPrefix(gitRepository));
        repositoryConfig.setFeaturePrefix(GitflowConfigUtil.getFeaturePrefix(gitRepository));
        repositoryConfig.setReleasePrefix(GitflowConfigUtil.getReleasePrefix(gitRepository));
        repositoryConfig.setSupportPrefix(GitflowConfigUtil.getSupportPrefix(gitRepository));

        return repositoryConfig;
    }

    protected static class RepositoryConfigsIterator implements Iterator<RepositoryConfig> {

        private final Iterator<GitRepository> gitRepositoryIterator;

        private final GitflowPerRepositoryReadConfig gitflowPerRepositoryReadConfig;

        public RepositoryConfigsIterator(final GitflowPerRepositoryReadConfig gitflowPerRepositoryReadConfig) {
            this(gitflowPerRepositoryReadConfig, gitflowPerRepositoryReadConfig.gitflowGitRepository.gitRepositories().iterator());
        }

        public RepositoryConfigsIterator(final GitflowPerRepositoryReadConfig gitflowPerRepositoryReadConfig, final Iterator<GitRepository> gitRepositoryIterator) {
            this.gitflowPerRepositoryReadConfig = gitflowPerRepositoryReadConfig;
            this.gitRepositoryIterator = gitRepositoryIterator;
        }

        @Override
        public boolean hasNext() {
            return this.gitRepositoryIterator.hasNext();
        }

        @Override
        public RepositoryConfig next() {
            final GitRepository nextGitRepository = this.gitRepositoryIterator.next();
            return this.gitflowPerRepositoryReadConfig.getRepositoryConfig(nextGitRepository);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("RepositoryConfigsIterator.remove() ist not supported");
        }
    }
}
