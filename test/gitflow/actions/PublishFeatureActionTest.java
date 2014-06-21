package gitflow.actions;

import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowConfigUtil;
import gitflow.fixtures.TestFixture2;
import gitflow.test.GitflowAsserts;
import gitflow.test.TestUtils;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Implements a test case to test the {@link gitflow.actions.PublishFeatureAction} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 21.06.14 - 13:34
 */
public class PublishFeatureActionTest extends JavaCodeInsightFixtureTestCase {

    @Test
    public void testPerformPublishFeatureAction() throws Exception {
        // Testfix erstellen
        TestUtils.startFeature(this.testFixture2.projectGitRepository, "Test-Feature");
        TestUtils.startFeature(this.testFixture2.module1GitRepository, "Test-Feature");

        // Test durchführen
        final PublishFeatureAction publishFeatureAction = new PublishFeatureAction();
        publishFeatureAction.setProject(this.testFixture2.project);
        publishFeatureAction.setVirtualFileMananger(VirtualFileManager.getInstance());
        publishFeatureAction.setBranchUtil(new GitflowBranchUtil(this.testFixture2.project));
        publishFeatureAction.setGitflowGitRepository(this.testFixture2.gitflowGitRepository);

        final boolean publishHotfixCommandWasSuccessful = publishFeatureAction.performPublishFeatureAction("Test-Feature");

        // Test auswerten
        assertThat(publishHotfixCommandWasSuccessful, is(true));

        /*
        Wir haben den develop-Branch nicht gepushed. Daher ist er remote nicht verfügbar sondern nur master und unser
        Feature-Branch.
         */
        GitflowAsserts.assertBranchNames(this.testFixture2.projectRepositoryRemoteRoot, "master", GitflowConfigUtil.DEFAULT_PREFIX_FEATURE + "Test-Feature");
        GitflowAsserts.assertBranchNames(this.testFixture2.moduleRepositoryRemoteRoot, "master", GitflowConfigUtil.DEFAULT_PREFIX_FEATURE + "Test-Feature");
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