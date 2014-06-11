package gitflow.actions;

import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.testFramework.fixtures.*;
import git4idea.GitUtil;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowInitOptions;
import gitflow.fixtures.TestFixture1;
import gitflow.git.GitflowGitRepository;
import gitflow.test.GitflowAsserts;

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
        final GitRepositoryManager manager = GitUtil.getRepositoryManager(this.testFixture1.project);

        final GitRepository projectGitRepository = manager.getRepositoryForRoot(this.testFixture1.projectBaseDir);
        final GitRepository module1GitRepository = manager.getRepositoryForRoot(this.testFixture1.module1ContentRoot);

        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(this.testFixture1.projectAndModules);
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
        initRepoAction.setProject(this.testFixture1.project);
        initRepoAction.setVirtualFileMananger(VirtualFileManager.getInstance());
        initRepoAction.setBranchUtil(new GitflowBranchUtil(this.testFixture1.project));
        initRepoAction.setGitflowGitRepository(gitflowGitRepository);

        initRepoAction.performInitReposCommand(gitflowGitRepository, gitflowInitOptions);

        // Test auswerten
        GitflowAsserts.assertGitflowBranchNames(projectGitRepository, "production", "master");
        GitflowAsserts.assertGitflowPrefixes(projectGitRepository, "feature-", "hotfix-", "release-", "support-");

        Collection<String> projectBranches = GitBranchUtil.getBranches(this.testFixture1.project, this.testFixture1.projectBaseDir, true, false, null);
        assertThat(projectBranches, hasSize(2));
        assertThat(projectBranches, containsInAnyOrder("master", "production"));

        GitflowAsserts.assertGitflowBranchNames(module1GitRepository, "production", "master");
        GitflowAsserts.assertGitflowPrefixes(module1GitRepository, "feature-", "hotfix-", "release-", "support-");

        Collection<String> module1Branches = GitBranchUtil.getBranches(this.testFixture1.project, this.testFixture1.module1ContentRoot, true, false, null);
        assertThat(module1Branches, hasSize(2));
        assertThat(module1Branches, containsInAnyOrder("master", "production"));

    }

    private TestFixture1 testFixture1;

    @Override
    public void setUp() throws Exception {
        this.testFixture1 = new TestFixture1(this);
        this.testFixture1.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        this.testFixture1.tearDown();
    }

}