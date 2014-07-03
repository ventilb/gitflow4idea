package gitflow.actions;

import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import gitflow.fixtures.TestFixture5;
import gitflow.test.GitflowAsserts;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Implements a test case to test the {@link gitflow.actions.TrackFeatureAction} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 03.07.14 - 23:44
 */
public class TrackFeatureActionTest extends JavaCodeInsightFixtureTestCase {

    @Test
    public void testPerformTrackFeatureCommand() throws Exception {
        // Testfix erstellen

        // Test durchführen
        final TrackFeatureAction trackFeatureAction = new TrackFeatureAction();
        trackFeatureAction.setProject(this.testFixture5.project);
        trackFeatureAction.setGitflowGitRepository(this.testFixture5.gitflowGitRepository);

        final boolean trackFeatureCommandWasSuccessful = trackFeatureAction.performTrackFeatureCommand("feature/Test-Feature");

        // Test auswerten
        assertThat(trackFeatureCommandWasSuccessful, is(true));

        GitflowAsserts.assertDefaultGitflowBranchNamesAndFeature(this.testFixture5.projectGitRepository, "Test-Feature");
        GitflowAsserts.assertDefaultGitflowBranchNamesAndFeature(this.testFixture5.module1GitRepository, "Test-Feature");

        GitflowAsserts.assertDefaultCurrentFeatureBranchName(this.testFixture5.projectGitRepository, "Test-Feature");
        GitflowAsserts.assertDefaultCurrentFeatureBranchName(this.testFixture5.module1GitRepository, "Test-Feature");
    }

    private TestFixture5 testFixture5;

    @Override
    public void setUp() throws Exception {
        this.testFixture5 = new TestFixture5(this);
        this.testFixture5.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        this.testFixture5.tearDown();
    }

}