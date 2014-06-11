package gitflow.git;

import com.intellij.testFramework.fixtures.*;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import gitflow.fixtures.TestFixture1;
import gitflow.test.TestUtils;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Implements a test case to test the VCS management of Intellij. The test is required to see how Intellij assigns
 * VCS roots to VCS systems. It's the same as the File %gt;  Settings %gt; Version Control dialog.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 14:46
 */
public class GitflowGitRepositoryTest extends JavaCodeInsightFixtureTestCase {

    @Test
    public void testAddGitRepository() throws Exception {
        // Testfix erstellen
        TestUtils.initGitRepo(this.testFixture1.project, this.testFixture1.projectBaseDir);
        TestUtils.initGitRepo(this.testFixture1.project, this.testFixture1.module1ContentRoot);

        final GitRepositoryManager manager = GitUtil.getRepositoryManager(this.testFixture1.project);

        final GitRepository projectGitRepository = manager.getRepositoryForRoot(this.testFixture1.projectBaseDir);
        final GitRepository module1GitRepository_1 = manager.getRepositoryForFile(this.testFixture1.module1ContentRoot);

        final GitRepository module1GitRepository_2 = manager.getRepositoryForFile(this.testFixture1.module1ContentRoot);

        // Test durchführen
        GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(this.testFixture1.projectAndModules);
        gitflowGitRepository.addGitRepository(projectGitRepository);
        gitflowGitRepository.addGitRepository(module1GitRepository_1);
        try {
            gitflowGitRepository.addGitRepository(null);
            fail("Should have caught IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        gitflowGitRepository.addGitRepository(module1GitRepository_2);
        gitflowGitRepository.addGitRepository(projectGitRepository);

        // Test auswerten
        assertThat(gitflowGitRepository.getRepositoryCount(), is(2));
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