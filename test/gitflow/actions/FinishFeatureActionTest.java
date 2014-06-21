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
import gitflow.models.FinishFeatureResultCode;
import gitflow.test.GitflowAsserts;
import gitflow.test.TestUtils;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertThat;

/**
 * Implements a test case to test the {@link gitflow.actions.FinishFeatureAction} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 20.06.14 - 23:35
 */
public class FinishFeatureActionTest extends JavaCodeInsightFixtureTestCase {

    @Test
    public void testPerformFinishFeatureCommand() throws Exception {
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

        TestUtils.startFeature(projectGitRepository, "Test-Feature");
        TestUtils.startFeature(module1GitRepository, "Test-Feature");

        TestUtils.changeFileContentTo(projectTestFile, "The project test file has changed");
        TestUtils.changeFileContentTo(moduleTestFile, "The module test file has changed");

        TestUtils.commit(projectGitRepository.getRoot(), "* The testfile has changed");
        TestUtils.commit(module1GitRepository.getRoot(), "* The testfile has changed");

        TestUtils.switchBranch(projectGitRepository, GitflowConfigUtil.DEFAULT_BRANCH_DEVELOP);
        TestUtils.switchBranch(module1GitRepository, GitflowConfigUtil.DEFAULT_BRANCH_DEVELOP);

        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(this.testFixture1.projectAndModules);
        gitflowGitRepository.addGitRepository(projectGitRepository);
        gitflowGitRepository.addGitRepository(module1GitRepository);

        // Test durchführen
        final FinishFeatureAction finishFeatureAction = new FinishFeatureAction();
        finishFeatureAction.setProject(this.testFixture1.project);
        finishFeatureAction.setBranchUtil(new GitflowBranchUtil(this.testFixture1.project));
        finishFeatureAction.setGitflowGitRepository(gitflowGitRepository);

        final FinishFeatureResultCode finishFeatureResultCode = finishFeatureAction.performFinishFeatureCommand("Test-Feature");

        // Test auswerten
        assertThat(finishFeatureResultCode, Matchers.is(FinishFeatureResultCode.SUCCESS));

        GitflowAsserts.assertDefaultGitflowBranchNames(projectGitRepository);
        GitflowAsserts.assertDefaultGitflowBranchNames(module1GitRepository);

        // Die Änderungen wurden in den development-Branch gemerged
        GitflowAsserts.assertFileContentInDevelopmentBranch(projectGitRepository, projectTestFile, "The project test file has changed");
        GitflowAsserts.assertFileContentInDevelopmentBranch(module1GitRepository, moduleTestFile, "The module test file has changed");

        // Die Änderungen vom Feature sind nicht im production-Branch sichtbar
        GitflowAsserts.assertFileContentInProductionBranch(projectGitRepository, projectTestFile, "");
        GitflowAsserts.assertFileContentInProductionBranch(module1GitRepository, moduleTestFile, "");
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