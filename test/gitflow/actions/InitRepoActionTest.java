package gitflow.actions;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.testFramework.fixtures.*;
import git4idea.GitUtil;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowInitOptions;
import gitflow.git.GitflowGitRepository;
import gitflow.test.GitflowAsserts;
import gitflow.test.TestUtils;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Implements a test case to test the {@link gitflow.actions.InitRepoAction} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 15:33
 */
public class InitRepoActionTest extends JavaCodeInsightFixtureTestCase {
    public void testPerformInitRepoCommand() throws Exception {
        // Testfix erstellen
        final GitRepositoryManager manager = GitUtil.getRepositoryManager(getProject());

        final GitRepository projectGitRepository = manager.getRepositoryForRoot(this.projectBaseDir);
        final GitRepository module1GitRepository = manager.getRepositoryForRoot(this.module1ContentRoot);

        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository();
        gitflowGitRepository.addGitRepository(projectGitRepository);
        gitflowGitRepository.addGitRepository(module1GitRepository);

        final GitflowInitOptions gitflowInitOptions = new GitflowInitOptions();
        gitflowInitOptions.setDevelopmentBranch("master");
        gitflowInitOptions.setProductionBranch("production");
        gitflowInitOptions.setFeaturePrefix("feature-");
        gitflowInitOptions.setHotfixPrefix("hotfix-");
        gitflowInitOptions.setReleasePrefix("release-");
        gitflowInitOptions.setSupportPrefix("support-");
        gitflowInitOptions.setVersionPrefix("");
        gitflowInitOptions.setUseDefaults(false);

        // Test durchführen
        InitRepoAction initRepoAction = new InitRepoAction();
        initRepoAction.setProject(getProject());
        initRepoAction.setVirtualFileMananger(VirtualFileManager.getInstance());
        initRepoAction.setBranchUtil(new GitflowBranchUtil(getProject()));
        initRepoAction.setGitflowGitRepository(gitflowGitRepository);

        initRepoAction.performInitReposCommand(gitflowGitRepository, gitflowInitOptions);

        // Test auswerten
        GitflowAsserts.assertGitflowBranchNames(projectGitRepository, "production", "master");
        GitflowAsserts.assertGitflowPrefixes(projectGitRepository, "feature-", "hotfix-", "release-", "support-");

        Collection<String> projectBranches = GitBranchUtil.getBranches(getProject(), this.projectBaseDir, true, false, null);
        assertThat(projectBranches, hasSize(2));
        assertThat(projectBranches, containsInAnyOrder("master", "production"));

        GitflowAsserts.assertGitflowBranchNames(module1GitRepository, "production", "master");
        GitflowAsserts.assertGitflowPrefixes(module1GitRepository, "feature-", "hotfix-", "release-", "support-");

        Collection<String> module1Branches = GitBranchUtil.getBranches(getProject(), this.module1ContentRoot, true, false, null);
        assertThat(module1Branches, hasSize(2));
        assertThat(module1Branches, containsInAnyOrder("master", "production"));

    }

    // Testfixture ////////////////////////////////////////////////////////////

    private VirtualFile projectBaseDir;

    private Module module1;

    private VirtualFile module1ContentRoot;

    @Override
    public void setUp() throws Exception {
        /*
        @see http://devnet.jetbrains.com/message/5492192#5492192
         */

        final TestFixtureBuilder<IdeaProjectTestFixture> projectBuilder = JavaTestFixtureFactory.createFixtureBuilder(getName());

        myFixture = JavaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(projectBuilder.getFixture());
        myFixture.setTestDataPath(getTestDataPath());

        // repeat the following line for each module
        final ModuleFixture moduleFixture1 = TestUtils.newProjectModuleFixture(myFixture, projectBuilder, "module1");

        /*
        Dieser Aufruf ist wichtig, da sonst der Test mit einem Assertion-Fehler abschmiert.
         */
        myFixture.setUp();

        this.projectBaseDir = getProject().getBaseDir();

        this.module1 = moduleFixture1.getModule();
        this.module1ContentRoot = TestUtils.getModuleContentRoot(this.module1);

        getProject().save();

        TestUtils.initGitRepo(getProject(), this.projectBaseDir);
        TestUtils.initGitRepo(getProject(), this.module1ContentRoot);
    }

    @Override
    public void tearDown() throws Exception {
        /*
        Wir müssen hier den GitRepositoryManager explizit wegwerfen da es sonst zu Fehlern beim Abräumen des
        Testfixtures kommt.
         */
        final GitRepositoryManager manager = GitUtil.getRepositoryManager(getProject());
        manager.dispose();

        myFixture.tearDown();

        TestUtils.deleteProjectGitDir(this.projectBaseDir);
        TestUtils.deleteModuleDir(this.module1ContentRoot);
    }

}