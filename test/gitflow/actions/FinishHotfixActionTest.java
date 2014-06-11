package gitflow.actions;

import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowConfigUtil;
import gitflow.GitflowInitOptions;
import gitflow.fixtures.TestFixture1;
import gitflow.git.GitflowGitRepository;
import gitflow.test.GitflowAsserts;
import gitflow.test.TestUtils;

import java.io.File;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Implements a test case to test the {@link gitflow.actions.FinishHotfixAction} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 09.06.14 - 22:43
 */
public class FinishHotfixActionTest extends JavaCodeInsightFixtureTestCase {

    public void testPerformFinishHotfixCommand() throws Exception {
        // Testfix erstellen
        final GitRepositoryManager manager = GitUtil.getRepositoryManager(getProject());

        final GitRepository projectGitRepository = manager.getRepositoryForRoot(this.testFixture1.projectBaseDir);
        final GitRepository module1GitRepository = manager.getRepositoryForRoot(this.testFixture1.module1ContentRoot);

        /*
        Wir müssen die Testdatei vor dem Anschalten von Gitflow erstellen, damit sie im Develop- als auch im
        Production-Branch erscheint. Andernfalls müssten wir erst ein Release durchführen damit die Änderungen aus dem
        Develop-Branch in den Production-Branch wandern.
         */
        final File projectTestFile = TestUtils.addAndCommitTestfile(projectGitRepository.getRoot());
        final File moduleTestFile = TestUtils.addAndCommitTestfile(module1GitRepository.getRoot());

        TestUtils.enableGitflow(projectGitRepository, this.gitflowInitOptions);
        TestUtils.enableGitflow(module1GitRepository, this.gitflowInitOptions);

        TestUtils.startHotfix(projectGitRepository, "Test-Hotfix");
        TestUtils.startHotfix(module1GitRepository, "Test-Hotfix");

        TestUtils.changeFileContentTo(projectTestFile, "The project test file has changed");
        TestUtils.changeFileContentTo(moduleTestFile, "The module test file has changed");

        TestUtils.commit(projectGitRepository.getRoot(), "* The testfile has changed");
        TestUtils.commit(module1GitRepository.getRoot(), "* The testfile has changed");

        /*
        Zurück in den Develop-Branch wechseln, da gitflow sonst den Hotfix nicht abschließen kann
         */
        TestUtils.switchBranch(projectGitRepository, GitflowConfigUtil.DEFAULT_BRANCH_DEVELOP);
        TestUtils.switchBranch(module1GitRepository, GitflowConfigUtil.DEFAULT_BRANCH_DEVELOP);

        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(this.testFixture1.projectAndModules);
        gitflowGitRepository.addGitRepository(projectGitRepository);
        gitflowGitRepository.addGitRepository(module1GitRepository);

        // Test durchführen
        final FinishHotfixAction finishHotfixAction = new FinishHotfixAction();
        finishHotfixAction.setProject(this.testFixture1.project);
        finishHotfixAction.setVirtualFileMananger(VirtualFileManager.getInstance());
        finishHotfixAction.setBranchUtil(new GitflowBranchUtil(this.testFixture1.project));
        finishHotfixAction.setGitflowGitRepository(gitflowGitRepository);

        final boolean finishHotfixCommandWasSuccessful = finishHotfixAction.performFinishHotfixCommand("Test-Hotfix", "* Finished hotfix");

        // Test auswerten
        assertThat(finishHotfixCommandWasSuccessful, is(true));
        GitflowAsserts.assertDefaultGitflowBranchNames(projectGitRepository);
        GitflowAsserts.assertDefaultGitflowBranchNames(module1GitRepository);

        GitflowAsserts.assertFileContentInDefaultGitflowBranches(projectGitRepository, projectTestFile, "The project test file has changed");
        GitflowAsserts.assertFileContentInDefaultGitflowBranches(module1GitRepository, moduleTestFile, "The module test file has changed");
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