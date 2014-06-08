package gitflow.git;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.*;
import git4idea.GitUtil;
import git4idea.repo.GitRepositoryManager;
import gitflow.intellij.ProjectAndContentRoots;
import gitflow.test.TestUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Implements a test case to test the gitflow git repository utils. It especially provides tests for obtaining the
 * git roots in a project.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 13:21
 */
public class GitflowGitRepositoryUtilTest extends JavaCodeInsightFixtureTestCase {

    public void testGetAllProjectContentRoots() throws Exception {
        // Testfix erstellen

        // Test durchführen
        ProjectAndContentRoots allProjectContentRoots = GitflowGitRepositoryUtil.getAllProjectContentRoots(getProject());

        // Test auswerten
        assertThat(allProjectContentRoots.getAllContentRoots(), hasSize(2));
        assertThat(allProjectContentRoots.getProjectBaseDir(), is(this.projectBaseDir));
        assertThat(allProjectContentRoots.getContentRoots()[0], is(this.module1ContentRoot));
    }

    public void testGetAllGitRepositories() throws Exception {
        // Testfix erstellen
        ProjectAndContentRoots allProjectContentRoots = GitflowGitRepositoryUtil.getAllProjectContentRoots(getProject());
        TestUtils.initGitRepo(getProject(), allProjectContentRoots.getProjectBaseDir());
        TestUtils.initGitRepo(getProject(), allProjectContentRoots.getContentRoots()[0]);

        // Test durchführen
        GitflowGitRepository gitflowGitRepository = GitflowGitRepositoryUtil.getAllGitRepositories(allProjectContentRoots);

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