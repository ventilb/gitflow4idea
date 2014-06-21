package gitflow.actions;

import com.intellij.openapi.vfs.VirtualFileManager;
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

/**
 * Implements a test case to test the {@link gitflow.actions.StartFeatureAction} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 20.06.14 - 22:38
 */
public class StartFeatureActionTest extends JavaCodeInsightFixtureTestCase {

    @Test
    public void testPerformStartFeatureCommand() throws Exception {
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
        final StartFeatureAction startFeatureAction = new StartFeatureAction();
        startFeatureAction.setProject(this.testFixture1.project);
        startFeatureAction.setBranchUtil(new GitflowBranchUtil(this.testFixture1.project));
        startFeatureAction.setGitflowGitRepository(gitflowGitRepository);

        startFeatureAction.performStartFeatureCommand("StartFeatureActionTest");

        // Test auswerten
        GitflowAsserts.assertDefaultGitflowBranchNamesAndFeature(projectGitRepository, "StartFeatureActionTest");
        GitflowAsserts.assertDefaultGitflowBranchNamesAndFeature(module1GitRepository, "StartFeatureActionTest");

        GitflowAsserts.assertDefaultCurrentFeatureBranchName(projectGitRepository, "StartFeatureActionTest");
        GitflowAsserts.assertDefaultCurrentFeatureBranchName(module1GitRepository, "StartFeatureActionTest");
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