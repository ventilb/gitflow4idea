package gitflow.git;

import com.intellij.testFramework.fixtures.*;
import gitflow.fixtures.TestFixture1;
import gitflow.intellij.ProjectAndModules;
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
        ProjectAndModules allProjectContentRoots = GitflowGitRepositoryUtil.getAllProjectContentRoots(this.testFixture1.project);

        // Test auswerten
        assertThat(allProjectContentRoots.getAllContentRoots(), hasSize(2));
        assertThat(allProjectContentRoots.getProjectBaseDir(), is(this.testFixture1.projectBaseDir));
        assertThat(allProjectContentRoots.getModuleContentRoots().get(0), is(this.testFixture1.module1ContentRoot));
    }

    public void testGetAllGitRepositories() throws Exception {
        // Testfix erstellen
        ProjectAndModules allProjectContentRoots = GitflowGitRepositoryUtil.getAllProjectContentRoots(this.testFixture1.project);
        TestUtils.initGitRepo(this.testFixture1.project, allProjectContentRoots.getProjectBaseDir());
        TestUtils.initGitRepo(this.testFixture1.project, allProjectContentRoots.getModuleContentRoots().get(0));

        // Test durchführen
        GitflowGitRepository gitflowGitRepository = GitflowGitRepositoryUtil.getAllGitRepositories(allProjectContentRoots);

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