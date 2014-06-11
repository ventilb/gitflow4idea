package gitflow.fixtures;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.*;
import git4idea.GitUtil;
import git4idea.repo.GitRepositoryManager;
import gitflow.intellij.ProjectAndModules;
import gitflow.test.TestUtils;

import java.lang.reflect.Field;

/**
 * Implements a test fixture with an intellij project and one module. The test fixture creates two git repositories.
 * One for the project root and one for the module.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 19:32
 */
public class TestFixture1 {

    public ProjectAndModules projectAndModules;

    public Project project;

    public VirtualFile projectBaseDir;

    public Module module1;

    public VirtualFile module1ContentRoot;

    private String name;

    public JavaCodeInsightTestFixture myFixture;

    private final JavaCodeInsightFixtureTestCase javaCodeInsightFixtureTestCase;

    public TestFixture1(JavaCodeInsightFixtureTestCase javaCodeInsightFixtureTestCase) {
        this.javaCodeInsightFixtureTestCase = javaCodeInsightFixtureTestCase;

        this.name = this.javaCodeInsightFixtureTestCase.getName();
    }

    public void setUp() throws Exception {
        /*
        @see http://devnet.jetbrains.com/message/5492192#5492192
         */

        final TestFixtureBuilder<IdeaProjectTestFixture> projectBuilder = JavaTestFixtureFactory.createFixtureBuilder(this.name);

        this.myFixture = JavaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(projectBuilder.getFixture());

        // repeat the following line for each module
        final ModuleFixture moduleFixture1 = TestUtils.newProjectModuleFixture(this.myFixture, projectBuilder, "module1");

        /*
        Dieser Aufruf ist wichtig, da sonst der Test mit einem Assertion-Fehler abschmiert.
         */
        this.myFixture.setUp();

        this.project = this.myFixture.getProject();
        this.projectBaseDir = this.project.getBaseDir();

        this.module1 = moduleFixture1.getModule();
        this.module1ContentRoot = TestUtils.getModuleContentRoot(this.module1);

        this.project.save();

        TestUtils.initGitRepo(this.project, this.projectBaseDir);
        TestUtils.initGitRepo(this.project, this.module1ContentRoot);

        Field myFixtureField = JavaCodeInsightFixtureTestCase.class.getDeclaredField("myFixture");
        myFixtureField.setAccessible(true);
        myFixtureField.set(this.javaCodeInsightFixtureTestCase, this.myFixture);

        this.projectAndModules = new ProjectAndModules(this.project, new Module[]{this.module1});
    }

    public void tearDown() throws Exception {
        /*
        Wir müssen hier den GitRepositoryManager explizit wegwerfen da es sonst zu Fehlern beim Abräumen des
        Testfixtures kommt.
         */
        final GitRepositoryManager manager = GitUtil.getRepositoryManager(this.project);
        manager.dispose();

        this.myFixture.tearDown();
        Field myFixtureField = JavaCodeInsightFixtureTestCase.class.getDeclaredField("myFixture");
        myFixtureField.setAccessible(true);
        myFixtureField.set(this.javaCodeInsightFixtureTestCase, null);

        TestUtils.deleteProjectGitDir(this.projectBaseDir);
        TestUtils.deleteModuleDir(this.module1ContentRoot);
    }

}
