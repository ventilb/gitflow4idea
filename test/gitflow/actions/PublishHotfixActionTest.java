package gitflow.actions;

import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowConfigUtil;
import gitflow.GitflowInitOptions;
import gitflow.fixtures.TestFixture2;
import gitflow.git.GitflowGitRepository;
import gitflow.test.GitflowAsserts;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Implements a test case to test the {@link gitflow.actions.PublishHotfixAction} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 16.06.14 - 20:31
 */
public class PublishHotfixActionTest extends JavaCodeInsightFixtureTestCase {

    @Test
    public void testPerformPublishHotfixCommand() throws Exception {
        // Testfix erstellen
        final GitRepositoryManager manager = GitUtil.getRepositoryManager(getProject());

        final GitRepository projectGitRepository = manager.getRepositoryForRoot(this.testFixture2.projectBaseDir);
        final GitRepository module1GitRepository = manager.getRepositoryForRoot(this.testFixture2.module1ContentRoot);

        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(this.testFixture2.projectAndModules);
        gitflowGitRepository.addGitRepository(projectGitRepository);
        gitflowGitRepository.addGitRepository(module1GitRepository);

        // Test durchführen
        final PublishHotfixAction publishHotfixAction = new PublishHotfixAction();
        publishHotfixAction.setProject(this.testFixture2.project);
        publishHotfixAction.setVirtualFileMananger(VirtualFileManager.getInstance());
        publishHotfixAction.setBranchUtil(new GitflowBranchUtil(this.testFixture2.project));
        publishHotfixAction.setGitflowGitRepository(gitflowGitRepository);

        final boolean publishHotfixCommandWasSuccessful = publishHotfixAction.performPublishHotfixCommand("Test-Hotfix");

        // Test auswerten
        assertThat(publishHotfixCommandWasSuccessful, is(true));

        /*
        Wir haben den develop-Branch nicht gepushed. Daher ist er remote nicht verfügbar sondern nur master und unser
        Hotfix-Branch.
         */
        GitflowAsserts.assertBranchNames(this.testFixture2.projectRepositoryRemoteRoot, "master", GitflowConfigUtil.DEFAULT_PREFIX_HOTFIX + "Test-Hotfix");
        GitflowAsserts.assertBranchNames(this.testFixture2.moduleRepositoryRemoteRoot, "master", GitflowConfigUtil.DEFAULT_PREFIX_HOTFIX + "Test-Hotfix");
    }

    private TestFixture2 testFixture2;

    private GitflowInitOptions gitflowInitOptions;

    @Override
    public void setUp() throws Exception {
        this.testFixture2 = new TestFixture2(this);
        this.testFixture2.setUp();

        this.gitflowInitOptions = new GitflowInitOptions();
        this.gitflowInitOptions.setUseDefaults(true);
    }

    @Override
    public void tearDown() throws Exception {
        this.testFixture2.tearDown();
    }

}