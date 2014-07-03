package gitflow.actions;

import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import gitflow.fixtures.TestFixture4;
import gitflow.test.GitflowAsserts;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Implements a test case to test the {@link gitflow.actions.TrackReleaseAction} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 03.07.14 - 21:41
 */
public class TrackReleaseActionTest extends JavaCodeInsightFixtureTestCase {

    @Test
    public void testPerformTrackReleaseCommand() throws Exception {
        // Testfix erstellen

        // Test durchführen
        final TrackReleaseAction trackReleaseAction = new TrackReleaseAction();
        trackReleaseAction.setProject(this.testFixture4.project);
        trackReleaseAction.setGitflowGitRepository(this.testFixture4.gitflowGitRepository);

        final boolean trackReleaseCommandWasSuccessful = trackReleaseAction.performTrackReleaseCommand("release/Test-Release");

        // Test auswerten
        assertThat(trackReleaseCommandWasSuccessful, is(true));

        GitflowAsserts.assertDefaultGitflowBranchNamesAndRelease(this.testFixture4.projectGitRepository, "Test-Release");
        GitflowAsserts.assertDefaultGitflowBranchNamesAndRelease(this.testFixture4.module1GitRepository, "Test-Release");

        GitflowAsserts.assertDefaultCurrentReleaseBranchName(this.testFixture4.projectGitRepository, "Test-Release");
        GitflowAsserts.assertDefaultCurrentReleaseBranchName(this.testFixture4.module1GitRepository, "Test-Release");
    }

    private TestFixture4 testFixture4;

    @Override
    public void setUp() throws Exception {
        this.testFixture4 = new TestFixture4(this);
        this.testFixture4.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        this.testFixture4.tearDown();
    }

}