package gitflow.actions;

import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowConfigUtil;
import gitflow.GitflowInitOptions;
import gitflow.fixtures.TestFixture2;
import gitflow.git.GitflowGitRepository;
import gitflow.test.GitflowAsserts;
import gitflow.test.TestUtils;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Implements a test case to test the {@link gitflow.actions.PublishReleaseAction} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 19.06.14 - 22:04
 */
public class PublishReleaseActionTest extends JavaCodeInsightFixtureTestCase {

    @Test
    public void testPerformPublishReleaseCommand() throws Exception {
        // Testfix erstellen
        TestUtils.startRelease(this.testFixture2.projectGitRepository, "Test-Release");
        TestUtils.startRelease(this.testFixture2.module1GitRepository, "Test-Release");

        // Test durchführen
        final PublishReleaseAction publishReleaseAction = new PublishReleaseAction();
        publishReleaseAction.setProject(this.testFixture2.project);
        publishReleaseAction.setVirtualFileMananger(VirtualFileManager.getInstance());
        publishReleaseAction.setBranchUtil(new GitflowBranchUtil(this.testFixture2.project));
        publishReleaseAction.setGitflowGitRepository(this.testFixture2.gitflowGitRepository);

        final boolean publishReleaseCommandWasSuccessful = publishReleaseAction.performPublishReleaseCommand("Test-Release");

        // Test auswerten
        assertThat(publishReleaseCommandWasSuccessful, is(true));

        /*
        Wir haben den develop-Branch nicht gepushed. Daher ist er remote nicht verfügbar sondern nur master und unser
        Release-Branch.
         */
        GitflowAsserts.assertBranchNames(this.testFixture2.projectRepositoryRemoteRoot, "master", GitflowConfigUtil.DEFAULT_PREFIX_RELEASE + "Test-Release");
        GitflowAsserts.assertBranchNames(this.testFixture2.moduleRepositoryRemoteRoot, "master", GitflowConfigUtil.DEFAULT_PREFIX_RELEASE + "Test-Release");
    }

    private TestFixture2 testFixture2;

    @Override
    public void setUp() throws Exception {
        this.testFixture2 = new TestFixture2(this);
        this.testFixture2.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        this.testFixture2.tearDown();
    }

}