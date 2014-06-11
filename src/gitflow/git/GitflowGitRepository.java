package gitflow.git;

import com.intellij.openapi.project.Project;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
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
     * Returns TRUE if all git repositories have checked out the same and valid branch, FALSE otherwise.
     * <p>
     * A branch is considered not valid if its name is empty. This may happen if a git repository is checked out to a
     * specific commit instead of a branch.
     * </p>
     *
     * @return TRUE if all git repositories have checked out the same and valid branch, FALSE otherwise
     */
    public boolean areAllGitRepositoriesOnSameAndValidBranch() {
        final Set<String> gitRepositoryBranchNames = new HashSet<String>();

        String branchName;
        for (GitRepository gitRepository : gitRepositories()) {
            branchName = GitBranchUtil.getBranchNameOrRev(gitRepository);

            if (branchName.isEmpty()) {
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
