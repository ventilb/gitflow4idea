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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Implements a test case to test the {@link gitflow.actions.StartHotfixAction} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 14:46
 */
public class StartHotfixActionTest extends JavaCodeInsightFixtureTestCase {

    private final Set<File> filesCreatedDuringTest = new HashSet<File>();

    @Test
    public void testPerformStartHotfixCommand() throws Exception {
        // Testfix erstellen
        final GitRepositoryManager manager = GitUtil.getRepositoryManager(getProject());

        final GitRepository projectGitRepository = manager.getRepositoryForRoot(this.testFixture1.projectBaseDir);
        final GitRepository module1GitRepository = manager.getRepositoryForRoot(this.testFixture1.module1ContentRoot);

        TestUtils.enableGitflow(projectGitRepository, this.gitflowInitOptions);
        TestUtils.enableGitflow(module1GitRepository, this.gitflowInitOptions);

        this.filesCreatedDuringTest.add(TestUtils.addAndCommitTestfile(projectGitRepository.getRoot()));
        this.filesCreatedDuringTest.add(TestUtils.addAndCommitTestfile(module1GitRepository.getRoot()));

        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository();
        gitflowGitRepository.addGitRepository(projectGitRepository);
        gitflowGitRepository.addGitRepository(module1GitRepository);

        // Test durchführen
        final StartHotfixAction startHotfixAction = new StartHotfixAction();
        startHotfixAction.setProject(this.testFixture1.project);
        startHotfixAction.setVirtualFileMananger(VirtualFileManager.getInstance());
        startHotfixAction.setBranchUtil(new GitflowBranchUtil(this.testFixture1.project));
        startHotfixAction.setGitflowGitRepository(gitflowGitRepository);

        startHotfixAction.performStartHotfixCommand("StartHotfixActionTest");

        // Test auswerten
        GitflowAsserts.assertDefaultGitflowBranchNamesAndHotfix(projectGitRepository, "StartHotfixActionTest");
        GitflowAsserts.assertDefaultGitflowBranchNamesAndHotfix(module1GitRepository, "StartHotfixActionTest");

        GitflowAsserts.assertDefaultCurrentHotfixBranchName(projectGitRepository, "StartHotfixActionTest");
        GitflowAsserts.assertDefaultCurrentHotfixBranchName(module1GitRepository, "StartHotfixActionTest");
    }

    private TestFixture1 testFixture1;

    private GitflowInitOptions gitflowInitOptions;

    @Override
    public void setUp() throws Exception {
        this.testFixture1 = new TestFixture1(this);
        this.testFixture1.setUp();

        this.gitflowInitOptions = new GitflowInitOptions();
        this.gitflowInitOptions.setUseDefaults(true);
//        gitflowInitOptions.setDevelopmentBranch("master");
//        gitflowInitOptions.setProductionBranch("production");
//        gitflowInitOptions.setFeaturePrefix("feature-");
//        gitflowInitOptions.setHotfixPrefix("hotfix-");
//        gitflowInitOptions.setReleasePrefix("release-");
//        gitflowInitOptions.setSupportPrefix("support-");
//        gitflowInitOptions.setVersionPrefix("");
//        gitflowInitOptions.setUseDefaults(false);

    }

    @Override
    public void tearDown() throws Exception {
        this.testFixture1.tearDown();

//        for (File file : this.filesCreatedDuringTest) {
//            if (file.exists()) {
//                file.delete();
//            }
//        }
    }

}