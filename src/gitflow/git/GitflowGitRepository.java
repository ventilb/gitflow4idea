package gitflow.git;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchUtil;
import git4idea.branch.GitBrancher;
import git4idea.repo.GitRepository;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowConfigUtil;
import gitflow.intellij.ProjectAndModules;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Implements a git repository model for the gitflow plugin. For cleaner api design we hold all repository information
 * in this model and pass this model around.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 11:21
 */
public class GitflowGitRepository {

    private final ProjectAndModules projectAndModules;

    private final List<GitRepository> gitRepositories = new LinkedList<GitRepository>();

    private GitflowPerRepositoryReadConfig gitflowPerRepositoryReadConfigInstance = null;

    public GitflowGitRepository(@NotNull final ProjectAndModules projectAndModules) {
        this.projectAndModules = projectAndModules;
    }

    @NotNull
    public Project getProject() {
        return this.projectAndModules.getProject();
    }

    public void addGitRepository(@NotNull final GitRepository gitRepository) {
        if (!this.gitRepositories.contains(gitRepository)) {
            this.gitRepositories.add(gitRepository);
        }
    }

    /**
     * Switches all repositories to their configured development branch. Returns TRUE if all repositories are on the
     * same branch after the switch, FALSE otherwise.
     *
     * @return TRUE if all repositories are on the same branch, FALSE otherwise
     */
    public boolean switchToDevelopmentBranch() {
        final GitBrancher brancher = ServiceManager.getService(this.projectAndModules.getProject(), GitBrancher.class);
        for (GitRepository gitRepository : gitRepositories()) {
            brancher.checkout(GitflowConfigUtil.getDevelopBranch(gitRepository), Collections.singletonList(gitRepository), null);
        }
        return areAllGitRepositoriesOnSameAndValidBranch();
    }

    /**
     * Returns the unique remote release branch names from all repository. It's the intersection of all remote release
     * branch names of all git repositories.
     *
     * @return unique remote release branch names
     */
    @NotNull
    public Set<String> getUniqueRemoteReleaseBranchNames() {
        return getUniqueBranchNamesImpl(ALL_REMOTE_RELEASE_BRANCH_NAMES);
    }

    /**
     * Returns the unique remote branch names from all repository. It's the intersection of all remote branch names
     * of all git repositories.
     *
     * @return unique remote branch names
     */
    @NotNull
    public Set<String> getUniqueRemoteBranchNames() {
        return getUniqueBranchNamesImpl(ALL_REMOTE_BRANCH_NAMES);
    }

    /**
     * Returns all remote release branch names for the specified git repository. The branch prefix is determined by
     * the repository configuration.
     *
     * @param gitRepository the git repository
     * @return remote release branch names
     */
    @NotNull
    protected Collection<String> getRemoteReleaseBranchNames(@NotNull final GitRepository gitRepository) {
        final String repositoryReleasePrefix = GitflowConfigUtil.getReleasePrefix(gitRepository);
        final Collection<String> allRemoteBranchNames = getRemoteBranchNames(gitRepository);

        return GitflowBranchUtil.filterBranchListByPrefix(allRemoteBranchNames, repositoryReleasePrefix);
    }

    /**
     * Returns all remote branch names for the specified git repository.
     *
     * @param gitRepository the git repository
     * @return remote branch names
     */
    @NotNull
    protected Collection<String> getRemoteBranchNames(@NotNull final GitRepository gitRepository) {
        return GitflowBranchUtil.getRemoteBranchNames(gitRepository);
    }

    @NotNull
    protected Set<String> getUniqueBranchNamesImpl(@NotNull final BranchNamesSource branchNamesSource) {
        final Set<String> uniqueRemoteBranchNames = new HashSet<String>();
        final Set<String>[] remoteBranchesPerRepository = new Set[getRepositoryCount()];

        int remoteBranchesPerRepositoryIndex = 0;
        for (GitRepository gitRepository : gitRepositories()) {
            Set<String> remoteBranchNames = new HashSet<String>(branchNamesSource.getBranchNames(this, gitRepository));
            uniqueRemoteBranchNames.addAll(remoteBranchNames);

            remoteBranchesPerRepository[remoteBranchesPerRepositoryIndex] = remoteBranchNames;

            remoteBranchesPerRepositoryIndex++;
        }

        for (int i = 0; i < remoteBranchesPerRepositoryIndex; i++) {
            uniqueRemoteBranchNames.retainAll(remoteBranchesPerRepository[i]);
        }
        return uniqueRemoteBranchNames;
    }

    /**
     * Returns TRUE if all git repositories have checked out the same and valid branch, FALSE otherwise.
     * <p>
     * A branch is considered not valid if:
     * </p>
     * <ul>
     * <li>Rebasing is in progress for at least one repository,</li>
     * <li>One repository is not on any branch,</li>
     * <li>For an unknown reason the branch name is empty or NULL.</li>
     * </ul>
     *
     * @return TRUE if all git repositories have checked out the same and valid branch, FALSE otherwise
     */
    public boolean areAllGitRepositoriesOnSameAndValidBranch() {
        final Set<String> gitRepositoryBranchNames = new HashSet<String>();

        GitLocalBranch gitLocalBranch;
        String branchName;
        for (GitRepository gitRepository : gitRepositories()) {
            if (gitRepository.isRebaseInProgress()) {
                // Rebasing in progress
                return false;
            }

            gitLocalBranch = gitRepository.getCurrentBranch();
            if (gitLocalBranch == null) {
                // We are not on any branch
                return false;
            }

            branchName = gitLocalBranch.getName();

            if (branchName == null || branchName.isEmpty()) {
                // Branch name is empty
                return false;
            }

            gitRepositoryBranchNames.add(branchName);
        }

        return gitRepositoryBranchNames.size() == 1;
    }

    /**
     * Returns all unique hotfix names from all repositories.
     * <p>
     * Normally this method returns a set with only one element. It should not happen that the managed git repositories
     * work on different hotfixes.
     * </p>
     * <p>
     * The behaviour is unspecified if any of the repositories is not on a hotfix branch.
     * </p>
     *
     * @return all unique hotfix names
     */
    @NotNull
    public Set<String> getUniqueHotfixNamesFromCurrentBranches() {
        final Set<String> distinctHotfixNames = new HashSet<String>();

        String hotfixName;
        String currentBranchName;
        for (GitRepository gitRepository : gitRepositories()) {
            currentBranchName = GitBranchUtil.getBranchNameOrRev(gitRepository);
            hotfixName = GitflowConfigUtil.getHotfixNameFromBranch(gitRepository, currentBranchName);

            distinctHotfixNames.add(hotfixName);
        }

        return distinctHotfixNames;
    }

    /**
     * Returns all unique release names from all repositories.
     * <p>
     * Normally this method returns a set with only one element. It should not happen that the managed git repositories
     * work on different releases.
     * </p>
     * <p>
     * The behaviour is unspecified if any of the repositories is not on a release branch.
     * </p>
     *
     * @return all unique release names
     */
    @NotNull
    public Set<String> getUniqueReleaseNamesFromCurrentBranches() {
        final Set<String> distinctReleaseNames = new HashSet<String>();

        String releaseName;
        String currentBranchName;
        for (GitRepository gitRepository : gitRepositories()) {
            currentBranchName = GitBranchUtil.getBranchNameOrRev(gitRepository);
            releaseName = GitflowConfigUtil.getReleaseNameFromBranch(gitRepository, currentBranchName);

            distinctReleaseNames.add(releaseName);
        }

        return distinctReleaseNames;
    }

    /**
     * Returns all unique feature names from all repositories.
     * <p>
     * Normally this method returns a set with only one element. It should not happen that the managed git repositories
     * work on different features.
     * </p>
     * <p>
     * The behaviour is unspecified if any of the repositories is not on a feature branch.
     * </p>
     *
     * @return all unique feature names
     */
    @NotNull
    public Set<String> getUniqueFeatureNamesFromCurrentBranches() {
        final Set<String> distinctFeatureNames = new HashSet<String>();

        String featureName;
        String currentBranchName;
        for (GitRepository gitRepository : gitRepositories()) {
            currentBranchName = GitBranchUtil.getBranchNameOrRev(gitRepository);
            featureName = GitflowConfigUtil.getFeatureNameFromBranch(gitRepository, currentBranchName);

            distinctFeatureNames.add(featureName);
        }

        return distinctFeatureNames;
    }

    @NotNull
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

    // Helpers ////////////////////////////////////////////////////////////////

    /*
    The interface and it's static implementations are helping us to reduce code redundancy.
     */
    protected final static BranchNamesSource ALL_REMOTE_BRANCH_NAMES = new BranchNamesSource() {
        @Override
        public Collection<String> getBranchNames(final GitflowGitRepository gitflowGitRepository, final GitRepository gitRepository) {
            return gitflowGitRepository.getRemoteBranchNames(gitRepository);
        }
    };

    protected final static BranchNamesSource ALL_REMOTE_RELEASE_BRANCH_NAMES = new BranchNamesSource() {
        @Override
        public Collection<String> getBranchNames(final GitflowGitRepository gitflowGitRepository, final GitRepository gitRepository) {
            return gitflowGitRepository.getRemoteReleaseBranchNames(gitRepository);
        }
    };

    protected interface BranchNamesSource {
        public Collection<String> getBranchNames(GitflowGitRepository gitflowGitRepository, GitRepository gitRepository);
    }
}
