package gitflow.git;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.*;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
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
        TestUtils.initGitRepo(getProject(), this.projectBaseDir);
        TestUtils.initGitRepo(getProject(), this.module1ContentRoot);

        final GitRepositoryManager manager = GitUtil.getRepositoryManager(getProject());

        final GitRepository projectGitRepository = manager.getRepositoryForRoot(this.projectBaseDir);
        final GitRepository module1GitRepository_1 = manager.getRepositoryForFile(this.module1ContentRoot);

        final GitRepository module1GitRepository_2 = manager.getRepositoryForFile(this.module1ContentRoot);

        // Test durchführen
        GitflowGitRepository gitflowGitRepository = new GitflowGitRepository();
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