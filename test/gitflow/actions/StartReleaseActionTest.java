package gitflow.actions;

import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowInitOptions;
import gitflow.fixtures.TestFixture1;
import gitflow.git.GitflowGitRepository;
import gitflow.test.GitflowAsserts;
import gitflow.test.TestUtils;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Implements a test case to test the {@link gitflow.actions.StartReleaseAction} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 11.06.14 - 01:18
 */
public class StartReleaseActionTest extends JavaCodeInsightFixtureTestCase {

    @Test
    public void testPerformStartReleaseCommand() throws Exception {
        // Testfix erstellen
        final GitRepositoryManager manager = GitUtil.getRepositoryManager(getProject());

        final GitRepository projectGitRepository = manager.getRepositoryForRoot(this.testFixture1.projectBaseDir);
        final GitRepository module1GitRepository = manager.getRepositoryForRoot(this.testFixture1.module1ContentRoot);

        TestUtils.enableGitflow(projectGitRepository, this.gitflowInitOptions);
        TestUtils.enableGitflow(module1GitRepository, this.gitflowInitOptions);

        TestUtils.addAndCommitTestfile(projectGitRepository.getRoot());
        TestUtils.addAndCommitTestfile(module1GitRepository.getRoot());

        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(this.testFixture1.projectAndModules);
        gitflowGitRepository.addGitRepository(projectGitRepository);
        gitflowGitRepository.addGitRepository(module1GitRepository);

        // Test durchführen
        final StartReleaseAction startHotfixAction = new StartReleaseAction();
        startHotfixAction.setProject(this.testFixture1.project);
        startHotfixAction.setBranchUtil(new GitflowBranchUtil(this.testFixture1.project));
        startHotfixAction.setGitflowGitRepository(gitflowGitRepository);

        final boolean performStartReleaseCommandWasSuccessfull = startHotfixAction.performStartReleaseCommand("StartReleaseActionTest");

        // Test auswerten
        assertThat(performStartReleaseCommandWasSuccessfull, is(true));

        GitflowAsserts.assertDefaultGitflowBranchNamesAndRelease(projectGitRepository, "StartReleaseActionTest");
        GitflowAsserts.assertDefaultGitflowBranchNamesAndRelease(module1GitRepository, "StartReleaseActionTest");

        GitflowAsserts.assertDefaultCurrentReleaseBranchName(projectGitRepository, "StartReleaseActionTest");
        GitflowAsserts.assertDefaultCurrentReleaseBranchName(module1GitRepository, "StartReleaseActionTest");
    }

    private TestFixture1 testFixture1;

    private GitflowInitOptions gitflowInitOptions;

    @Override
    public void setUp() throws Exception {
        this.testFixture1 = new TestFixture1(this);
        this.testFixture1.setUp();

        this.gitflowInitOptions = new GitflowInitOptions();
        this.gitflowInitOptions.setUseDefaults(true);
    }

    @Override
    public void tearDown() throws Exception {
        this.testFixture1.tearDown();
    }
}