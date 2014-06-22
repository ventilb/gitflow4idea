package gitflow.actions;

import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
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
 * Implements a test case to test the {@link gitflow.actions.FinishFeatureAction} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 23.06.14 - 00:45
 */
public class FinishFeatureAction_OneRepository_Test extends JavaCodeInsightFixtureTestCase {

    @Test
    public void testPerformFinishFeatureCommand_with_no_file_change() throws Exception {
        // Testfix erstellen

        // Test durchführen
        final FinishFeatureAction finishFeatureAction = new FinishFeatureAction();
        finishFeatureAction.setProject(this.testFixture3.project);
        finishFeatureAction.setGitflowGitRepository(this.testFixture3.gitflowGitRepository);

        final FinishFeatureResultCode finishFeatureResultCode = finishFeatureAction.performFinishFeatureCommand("Test-Feature");

        // Test auswerten
        assertThat(finishFeatureResultCode, Matchers.is(FinishFeatureResultCode.SUCCESS));

        GitflowAsserts.assertDefaultGitflowBranchNames(this.testFixture3.projectGitRepository);
    }

    @Test
    public void testPerformFinishFeatureCommand_a_file_has_changed() throws Exception {
        // Testfix erstellen
        TestUtils.changeFileContentTo(this.testFixture3.projectTestFile, "The project test file has changed");

        TestUtils.commit(this.testFixture3.projectGitRepository, "* The testfile has changed");

        // Test durchführen
        final FinishFeatureAction finishFeatureAction = new FinishFeatureAction();
        finishFeatureAction.setProject(this.testFixture3.project);
        finishFeatureAction.setGitflowGitRepository(this.testFixture3.gitflowGitRepository);

        final FinishFeatureResultCode finishFeatureResultCode = finishFeatureAction.performFinishFeatureCommand("Test-Feature");

        // Test auswerten
        assertThat(finishFeatureResultCode, Matchers.is(FinishFeatureResultCode.SUCCESS));

        GitflowAsserts.assertDefaultGitflowBranchNames(this.testFixture3.projectGitRepository);

        // Die Änderungen wurden in den development-Branch gemerged
        GitflowAsserts.assertFileContentInDevelopmentBranch(this.testFixture3.projectGitRepository, this.testFixture3.projectTestFile, "The project test file has changed");

        // Die Änderungen vom Feature sind nicht im production-Branch sichtbar
        GitflowAsserts.assertFileContentInProductionBranch(this.testFixture3.projectGitRepository, this.testFixture3.projectTestFile, "");
    }

    private TestFixture3 testFixture3;

    @Override
    public void setUp() throws Exception {
        this.testFixture3 = new TestFixture3(this);
        this.testFixture3.setUp();

        TestUtils.startFeature(this.testFixture3.projectGitRepository, "Test-Feature");
        GitflowAsserts.assertDefaultCurrentFeatureBranchName(this.testFixture3.projectGitRepository, "Test-Feature");
    }

    @Override
    public void tearDown() throws Exception {
        this.testFixture3.tearDown();
    }

}