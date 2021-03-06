package gitflow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.branch.GitBranchUtil;
import git4idea.config.GitConfigUtil;
import git4idea.repo.GitRepository;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */

//TODO maybe have this as a singleton instead of static

public class GitflowConfigUtil {

    public static final String DEFAULT_BRANCH_MASTER = "master";

    public static final String DEFAULT_BRANCH_DEVELOP = "develop";

    public static final String DEFAULT_PREFIX_HOTFIX = "hotfix/";

    public static final String DEFAULT_PREFIX_RELEASE = "release/";

    public static final String DEFAULT_PREFIX_FEATURE= "feature/";

    public static final String BRANCH_MASTER = "gitflow.branch.master";
    public static final String BRANCH_DEVELOP = "gitflow.branch.develop";
    public static final String PREFIX_FEATURE = "gitflow.prefix.feature";
    public static final String PREFIX_RELEASE = "gitflow.prefix.release";
    public static final String PREFIX_HOTFIX = "gitflow.prefix.hotfix";
    public static final String PREFIX_SUPPORT = "gitflow.prefix.support";
    public static final String PREFIX_VERSIONTAG = "gitflow.prefix.versiontag";

    @Deprecated
    public static String getMasterBranch(Project project) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);
        VirtualFile root = repo.getRoot();

        String masterBranch = null;
        try {
            masterBranch = GitConfigUtil.getValue(project, root, BRANCH_MASTER);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }

        return masterBranch;
    }

    public static String getMasterBranch(final GitRepository repo) {
        final Project project = repo.getProject();
        final VirtualFile root = repo.getRoot();

        String masterBranch = null;
        try {
            masterBranch = GitConfigUtil.getValue(project, root, BRANCH_MASTER);
        } catch (VcsException e) {
            configErrorInGitRepository(repo, BRANCH_MASTER, e);
        }

        return masterBranch;
    }

    @Deprecated
    public static String getDevelopBranch(Project project) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);
        VirtualFile root = repo.getRoot();

        String developBranch = null;
        try {
            developBranch = GitConfigUtil.getValue(project, root, BRANCH_DEVELOP);
        } catch (VcsException e) {
            configErrorInGitRepository(repo, BRANCH_MASTER, e);
        }

        return developBranch;
    }

    public static String getDevelopBranch(final GitRepository repo) {
        final Project project = repo.getProject();
        final VirtualFile root = repo.getRoot();

        String developBranch = null;
        try {
            developBranch = GitConfigUtil.getValue(project, root, BRANCH_DEVELOP);
        } catch (VcsException e) {
            configErrorInGitRepository(repo, BRANCH_MASTER, e);
        }

        return developBranch;
    }

    @Deprecated
    public static String getFeaturePrefix(Project project) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);
        VirtualFile root = repo.getRoot();

        String featurePrefix = null;

        try {
            featurePrefix = GitConfigUtil.getValue(project, root, PREFIX_FEATURE);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
        return featurePrefix;
    }

    public static String getFeaturePrefix(final GitRepository repo) {
        return getGitflowPrefix(repo, PREFIX_FEATURE);
    }

    @Deprecated
    public static String getReleasePrefix(Project project) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);
        VirtualFile root = repo.getRoot();

        String releasePrefix = null;

        try {
            releasePrefix = GitConfigUtil.getValue(project, root, PREFIX_RELEASE);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
        return releasePrefix;
    }

    public static String getReleasePrefix(final GitRepository repo) {
        return getGitflowPrefix(repo, PREFIX_RELEASE);
    }

    @Deprecated
    public static String getHotfixPrefix(Project project) {
        GitRepository repo = GitBranchUtil.getCurrentRepository(project);
        VirtualFile root = repo.getRoot();

        String hotfixPrefix = null;

        try {
            hotfixPrefix = GitConfigUtil.getValue(project, root, PREFIX_HOTFIX);
        } catch (VcsException e) {
            NotifyUtil.notifyError(project, "Config error", e);
        }
        return hotfixPrefix;
    }

    public static String getHotfixPrefix(final GitRepository repo) {
        return getGitflowPrefix(repo, PREFIX_HOTFIX);
    }

    public static String getSupportPrefix(final GitRepository repo) {
        return getGitflowPrefix(repo, PREFIX_SUPPORT);
    }

    public static String getFeatureNameFromBranch(Project project, String branchName) {
        String featurePrefix = GitflowConfigUtil.getFeaturePrefix(project);
        return branchName.substring(branchName.indexOf(featurePrefix) + featurePrefix.length(), branchName.length());
    }

    public static String getFeatureNameFromBranch(final GitRepository gitRepository, final String branchName) {
        final String featurePrefix = GitflowConfigUtil.getFeaturePrefix(gitRepository);
        return branchName.substring(branchName.indexOf(featurePrefix) + featurePrefix.length(), branchName.length());
    }

    /**
     * Returns the release name for the specified git repository and branch name. The release name is determined by
     * evaluating the gitflow configuration for the git repository.
     *
     * @param gitRepository the git repository
     * @param branchName the branch name
     * @return release name from branch name
     */
    @NotNull
    public static String getReleaseNameFromBranchName(@NotNull final GitRepository gitRepository, @NotNull final String branchName) {
        final String releasePrefix = GitflowConfigUtil.getReleasePrefix(gitRepository);
        return branchName.substring(branchName.indexOf(releasePrefix) + releasePrefix.length(), branchName.length());
    }

    public static String getHotfixNameFromBranch(final GitRepository gitRepository, final String branchName) {
        final String hotfixPrefix = GitflowConfigUtil.getHotfixPrefix(gitRepository);
        return branchName.substring(branchName.indexOf(hotfixPrefix) + hotfixPrefix.length(), branchName.length());
    }

    /**
     * Sets the gitflow production branch name. The configured master branch is used to hold the releases of a gitflow
     * enabled project.
     *
     * @param repo       The git repository to set the production branch
     * @param branchName The branch name to set
     */
    public static void setMasterBranch(final GitRepository repo, final String branchName) {
        final Project project = repo.getProject();
        final VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, BRANCH_MASTER, branchName);
        } catch (VcsException e) {
            configErrorInGitRepository(repo, BRANCH_MASTER, e);
        }
    }

    /**
     * Sets the gitflow development branch name.
     *
     * @param repo       The git repository to set the master branch
     * @param branchName The branch name to set
     */
    public static void setDevelopBranch(final GitRepository repo, final String branchName) {
        final Project project = repo.getProject();
        final VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, BRANCH_DEVELOP, branchName);
        } catch (VcsException e) {
            configErrorInGitRepository(repo, BRANCH_DEVELOP, e);
        }
    }

    public static void setReleasePrefix(final GitRepository repo, final String prefix) {
        setGitflowPrefix(repo, PREFIX_RELEASE, prefix);
    }

    public static void setFeaturePrefix(final GitRepository repo, final String prefix) {
        setGitflowPrefix(repo, PREFIX_FEATURE, prefix);
    }

    public static void setHotfixPrefix(final GitRepository repo, final String prefix) {
        setGitflowPrefix(repo, PREFIX_HOTFIX, prefix);
    }

    public static void setSupportPrefix(final GitRepository repo, final String prefix) {
        setGitflowPrefix(repo, PREFIX_SUPPORT, prefix);
    }

    public static void setVersionPrefix(final GitRepository repo, final String prefix) {
        setGitflowPrefix(repo, PREFIX_VERSIONTAG, prefix);
    }

    /**
     * Sets the specified gitflow prefix key to the specified prefix value for the specified git repository.
     *
     * @param repo        The git repository
     * @param prefixKey   The prefix key
     * @param prefixValue The prefix value
     */
    protected static void setGitflowPrefix(final GitRepository repo, final String prefixKey, final String prefixValue) {
        final Project project = repo.getProject();
        final VirtualFile root = repo.getRoot();

        try {
            GitConfigUtil.setValue(project, root, prefixKey, prefixValue);
        } catch (VcsException e) {
            configErrorInGitRepository(repo, prefixKey, e);
        }
    }

    /**
     * Gets the gitflow prefix value to the specified gitflow prefix key in the specified git repository. Returns NULL
     * if the value was not found.
     *
     * @param repo      The git repository
     * @param prefixKey The prefix key
     * @return The prefix value
     */
    protected static String getGitflowPrefix(final GitRepository repo, final String prefixKey) {
        final Project project = repo.getProject();
        final VirtualFile root = repo.getRoot();

        String featureValue = null;

        try {
            featureValue = GitConfigUtil.getValue(project, root, prefixKey);
        } catch (VcsException e) {
            configErrorInGitRepository(repo, prefixKey, e);
        }
        return featureValue;
    }

    protected static void configErrorInGitRepository(final GitRepository repo, String gitflowKey, final Exception cause) {
        final Project project = repo.getProject();
        final VirtualFile root = repo.getRoot();

        final String errMsg = String.format("Config error in gitflow key '%s' in git repository root '%s'", gitflowKey, root.getCanonicalPath());
        NotifyUtil.notifyError(project, errMsg, cause);
    }
}
