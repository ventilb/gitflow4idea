package gitflow.test;

import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import gitflow.GitflowConfigUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Provides assertions to test gitflow specific features.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 16:02
 */
public class GitflowAsserts {

    public static void assertFileContentInDefaultGitflowBranches(final GitRepository gitRepository, final File fileToTest, final String expectedFileContent) throws Exception {
        TestUtils.switchBranch(gitRepository, GitflowConfigUtil.DEFAULT_BRANCH_MASTER);
        final String actualFileContentInProductionBranch = FileUtils.readFileToString(fileToTest);

        TestUtils.switchBranch(gitRepository, GitflowConfigUtil.DEFAULT_BRANCH_DEVELOP);
        final String actualFileContentInDevelopBranch = FileUtils.readFileToString(fileToTest);

        assertThat(actualFileContentInProductionBranch, is(expectedFileContent));
        assertThat(actualFileContentInDevelopBranch, is(expectedFileContent));
    }

    public static void assertDefaultCurrentHotfixBranchName(final GitRepository gitRepository, final String hotfixName) throws Exception {
        final GitLocalBranch currentBranch = gitRepository.getCurrentBranch();
        final String expectedHotfixBranchName = GitflowConfigUtil.DEFAULT_PREFIX_HOTFIX + hotfixName;

        assertThat(currentBranch.getName(), is(expectedHotfixBranchName));
    }

    public static void assertDefaultGitflowBranchNamesAndHotfix(final GitRepository gitRepository, final String hotfixName) throws Exception {
        Collection<String> branches = GitBranchUtil.getBranches(gitRepository.getProject(), gitRepository.getRoot(), true, false, null);
        assertThat(branches, hasSize(3));
        assertThat(branches, containsInAnyOrder(GitflowConfigUtil.DEFAULT_BRANCH_MASTER, GitflowConfigUtil.DEFAULT_BRANCH_DEVELOP, GitflowConfigUtil.DEFAULT_PREFIX_HOTFIX + hotfixName));
    }

    public static void assertDefaultGitflowBranchNames(final GitRepository gitRepository) throws Exception {
        Collection<String> branches = GitBranchUtil.getBranches(gitRepository.getProject(), gitRepository.getRoot(), true, false, null);
        assertThat(branches, hasSize(2));
        assertThat(branches, containsInAnyOrder(GitflowConfigUtil.DEFAULT_BRANCH_MASTER, GitflowConfigUtil.DEFAULT_BRANCH_DEVELOP));
    }

    public static void assertGitflowBranchNames(final GitRepository gitRepository, final String expectedGiflowProductionBranchName, final String expectedGiflowDevelopmentBranchName) throws Exception {
        final VirtualFile gitDir = gitRepository.getGitDir();

        String actualGitflowProductionBranchName = TestUtils.performConsoleGitCommand(gitDir.getCanonicalPath(), "config", "--get", GitflowConfigUtil.BRANCH_MASTER);
        String actualGitflowDevelopmentBranchName = TestUtils.performConsoleGitCommand(gitDir.getCanonicalPath(), "config", "--get", GitflowConfigUtil.BRANCH_DEVELOP);

        actualGitflowProductionBranchName = removeTrailingNewline(actualGitflowProductionBranchName);
        actualGitflowDevelopmentBranchName = removeTrailingNewline(actualGitflowDevelopmentBranchName);

        assertThat(actualGitflowProductionBranchName, is(expectedGiflowProductionBranchName));
        assertThat(actualGitflowDevelopmentBranchName, is(expectedGiflowDevelopmentBranchName));
    }

    public static void assertGitflowPrefixes(final GitRepository gitRepository, final String expectedFeaturePrefix, final String expectedHotfixPrefix, final String expectedReleasePrefix, final String expectedSupportPrefix) throws Exception {
        assertGitflowPrefix(gitRepository, GitflowConfigUtil.PREFIX_FEATURE, expectedFeaturePrefix);
        assertGitflowPrefix(gitRepository, GitflowConfigUtil.PREFIX_RELEASE, expectedReleasePrefix);
        assertGitflowPrefix(gitRepository, GitflowConfigUtil.PREFIX_HOTFIX, expectedHotfixPrefix);
        assertGitflowPrefix(gitRepository, GitflowConfigUtil.PREFIX_SUPPORT, expectedSupportPrefix);
    }

    public static void assertGitflowPrefix(final GitRepository gitRepository, final String gitConfigPrefixKeyToAssert, final String expectedPrefixValue) throws IOException {
        final VirtualFile gitDir = gitRepository.getGitDir();

        String actualPrefixValue = TestUtils.performConsoleGitCommand(gitDir.getCanonicalPath(), "config", "--get", gitConfigPrefixKeyToAssert);
        actualPrefixValue = removeTrailingNewline(actualPrefixValue);

        assertThat(actualPrefixValue, is(expectedPrefixValue));
    }

    protected static String removeTrailingNewline(String aString) {
        if (aString.endsWith("\n")) {
            aString = aString.substring(0, aString.length() - "\n".length());
        }

        return aString;
    }
}
