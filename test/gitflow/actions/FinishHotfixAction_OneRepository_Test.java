package gitflow.actions;

import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import gitflow.GitflowBranchUtil;
import gitflow.fixtures.TestFixture3;
import gitflow.test.GitflowAsserts;
import gitflow.test.TestUtils;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Implements a test case to test the {@link gitflow.actions.FinishHotfixAction} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 22.06.14 - 21:42
 */
public class FinishHotfixAction_OneRepository_Test extends JavaCodeInsightFixtureTestCase {

    @Test
    public void testPerformFinishHotfixCommand_with_no_file_change() throws Exception {
        // Testfix erstellen
        TestUtils.startHotfix(this.testFixture3.projectGitRepository, "Test-Hotfix");

        // Test durchführen
        final FinishHotfixAction finishHotfixAction = new FinishHotfixAction();
        finishHotfixAction.setProject(this.testFixture3.project);
        finishHotfixAction.setBranchUtil(new GitflowBranchUtil(this.testFixture3.project));
        finishHotfixAction.setGitflowGitRepository(this.testFixture3.gitflowGitRepository);

        final boolean finishHotfixCommandWasSuccessful = finishHotfixAction.performFinishHotfixCommand("Test-Hotfix", "* Finished hotfix");

        // Test auswerten
        assertThat(finishHotfixCommandWasSuccessful, is(true));
        GitflowAsserts.assertDefaultGitflowBranchNames(this.testFixture3.projectGitRepository);
    }

    @Test
    public void testPerformFinishHotfixCommand_a_file_has_changed() throws Exception {
        // Testfix erstellen
        TestUtils.startHotfix(this.testFixture3.projectGitRepository, "Test-Hotfix");

        TestUtils.changeFileContentTo(this.testFixture3.projectTestFile, "The project test file has changed");

        TestUtils.commit(this.testFixture3.projectGitRepository, "* The testfile has changed");

        // Test durchführen
        final FinishHotfixAction finishHotfixAction = new FinishHotfixAction();
        finishHotfixAction.setProject(this.testFixture3.project);
        finishHotfixAction.setBranchUtil(new GitflowBranchUtil(this.testFixture3.project));
        finishHotfixAction.setGitflowGitRepository(this.testFixture3.gitflowGitRepository);

        final boolean finishHotfixCommandWasSuccessful = finishHotfixAction.performFinishHotfixCommand("Test-Hotfix", "* Finished hotfix");

        // Test auswerten
        assertThat(finishHotfixCommandWasSuccessful, is(true));
        GitflowAsserts.assertDefaultGitflowBranchNames(this.testFixture3.projectGitRepository);

        GitflowAsserts.assertFileContentInDefaultGitflowBranches(this.testFixture3.projectGitRepository, this.testFixture3.projectTestFile, "The project test file has changed");
    }

    private TestFixture3 testFixture3;

    @Override
    public void setUp() throws Exception {
        this.testFixture3 = new TestFixture3(this);
        this.testFixture3.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        this.testFixture3.tearDown();
    }

}