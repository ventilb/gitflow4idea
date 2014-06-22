package gitflow.actions;

import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import gitflow.GitflowBranchUtil;
import gitflow.fixtures.TestFixture3;
import gitflow.models.FinishFeatureResultCode;
import gitflow.models.FinishReleaseResultCode;
import gitflow.test.GitflowAsserts;
import gitflow.test.TestUtils;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Implements a test case to test the {@link gitflow.actions.FinishReleaseAction} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 22.06.14 - 23:54
 */
public class FinishReleaseAction_OneRepository_Test extends JavaCodeInsightFixtureTestCase {

    @Test
    public void testPerformFinishReleaseCommand_with_no_file_change() throws Exception {
        // Testfix erstellen

        // Test durchführen
        final FinishReleaseAction finishReleaseAction = new FinishReleaseAction();
        finishReleaseAction.setProject(this.testFixture3.project);
        finishReleaseAction.setGitflowGitRepository(this.testFixture3.gitflowGitRepository);

        final FinishReleaseResultCode finishReleaseResultCode = finishReleaseAction.performFinishReleaseCommand("Test-Release", "* Finished release");

        // Test auswerten
        assertThat(finishReleaseResultCode, Matchers.is(FinishReleaseResultCode.SUCCESS));

        GitflowAsserts.assertDefaultGitflowBranchNames(this.testFixture3.projectGitRepository);
    }

    @Test
    public void testPerformFinishReleaseCommand_a_file_has_changed() throws Exception {
        // Testfix erstellen
        TestUtils.changeFileContentTo(this.testFixture3.projectTestFile, "The project test file has changed");

        TestUtils.commit(this.testFixture3.projectGitRepository, "* The testfile has changed");

        // Test durchführen
        final FinishReleaseAction finishReleaseAction = new FinishReleaseAction();
        finishReleaseAction.setProject(this.testFixture3.project);
        finishReleaseAction.setGitflowGitRepository(this.testFixture3.gitflowGitRepository);

        final FinishReleaseResultCode finishReleaseResultCode = finishReleaseAction.performFinishReleaseCommand("Test-Release", "* Finished release");

        // Test auswerten
        assertThat(finishReleaseResultCode, Matchers.is(FinishReleaseResultCode.SUCCESS));

        GitflowAsserts.assertDefaultGitflowBranchNames(this.testFixture3.projectGitRepository);

        GitflowAsserts.assertFileContentInDefaultGitflowBranches(this.testFixture3.projectGitRepository, this.testFixture3.projectTestFile, "The project test file has changed");
    }

    private TestFixture3 testFixture3;

    @Override
    public void setUp() throws Exception {
        this.testFixture3 = new TestFixture3(this);
        this.testFixture3.setUp();

        TestUtils.startRelease(this.testFixture3.projectGitRepository, "Test-Release");
        GitflowAsserts.assertDefaultCurrentReleaseBranchName(this.testFixture3.projectGitRepository, "Test-Release");
    }

    @Override
    public void tearDown() throws Exception {
        this.testFixture3.tearDown();
    }


}