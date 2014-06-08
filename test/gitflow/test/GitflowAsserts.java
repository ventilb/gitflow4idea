package gitflow.test;

import com.intellij.openapi.vfs.VirtualFile;
import git4idea.repo.GitRepository;
import gitflow.GitflowConfigUtil;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Provides assertions to test gitflow specific features.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 16:02
 */
public class GitflowAsserts {

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
