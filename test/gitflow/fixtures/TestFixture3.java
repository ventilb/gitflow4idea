package gitflow.fixtures;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.*;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import gitflow.GitflowInitOptions;
import gitflow.git.GitflowGitRepository;
import gitflow.intellij.ProjectAndModules;
import gitflow.test.TestUtils;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Implements a test fixture with only an intellij project. The test fixture creates one git flow enabled git repository
 * for the project.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 19:32
 */
public class TestFixture3 {

    public ProjectAndModules projectAndModules;

    public GitflowGitRepository gitflowGitRepository;

    // Fixture-Daten für das Hauptprojekt
    public Project project;

    public GitRepository projectGitRepository;

    public VirtualFile projectBaseDir;

    // Allgemeine Test-Daten
    public String name;

    public JavaCodeInsightTestFixture myFixture;

    public final JavaCodeInsightFixtureTestCase javaCodeInsightFixtureTestCase;

    public File projectTestFile;

    public TestFixture3(JavaCodeInsightFixtureTestCase javaCodeInsightFixtureTestCase) {
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

        this.project.save();
        this.projectAndModules = new ProjectAndModules(this.project, new Module[]{});

        TestUtils.initGitRepo(this.project, this.projectBaseDir);
        TestUtils.createAndCommitGitignoreFile(this.projectBaseDir);

        // Die folgenden Zeilen sind wichtig, da Intellij sonst unsere Git Repositories nicht erkennt.
        TestUtils.syncFileSystem();
        final GitRepositoryManager manager = GitUtil.getRepositoryManager(this.project);
        manager.directoryMappingChanged();

        this.projectGitRepository = manager.getRepositoryForRoot(this.projectBaseDir);

        this.gitflowGitRepository = new GitflowGitRepository(this.projectAndModules);
        this.gitflowGitRepository.addGitRepository(this.projectGitRepository);

        this.projectTestFile = TestUtils.addAndCommitTestfile(this.projectGitRepository.getRoot());

        // Gitflow anschalten
        final GitflowInitOptions gitflowInitOptions = new GitflowInitOptions();
        gitflowInitOptions.setUseDefaults(true);

        TestUtils.enableGitflow(this.projectGitRepository, gitflowInitOptions);

        Field myFixtureField = JavaCodeInsightFixtureTestCase.class.getDeclaredField("myFixture");
        myFixtureField.setAccessible(true);
        myFixtureField.set(this.javaCodeInsightFixtureTestCase, this.myFixture);
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
    }

}
