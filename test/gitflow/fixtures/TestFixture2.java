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
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Implements a test fixture with an intellij project and one module. The test fixture creates two git repositories.
 * One for the project root and one for the module. It also creates a remote repository for each local repository and
 * starts the gitflow hotfix workflow for the repositories.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 16.06.14 - 20:44
 */
public class TestFixture2 {

    public ProjectAndModules projectAndModules;

    public GitflowGitRepository gitflowGitRepository;

    // Fixture-Daten für das Hauptprojekt
    public Project project;

    public VirtualFile projectBaseDir;

    public GitRepository projectGitRepository;

    // Fixture-Daten für ein Modul im Projekt
    public Module module1;

    public VirtualFile module1ContentRoot;

    public GitRepository module1GitRepository;

    // Allgemeine Test-Daten
    public final String name;

    public JavaCodeInsightTestFixture myFixture;

    public final JavaCodeInsightFixtureTestCase javaCodeInsightFixtureTestCase;

    // File-Handle auf die Remote Repositories für diese Test
    public File projectRepositoryRemoteRoot;

    public File moduleRepositoryRemoteRoot;

    public TestFixture2(JavaCodeInsightFixtureTestCase javaCodeInsightFixtureTestCase) {
        this.javaCodeInsightFixtureTestCase = javaCodeInsightFixtureTestCase;

        this.name = this.javaCodeInsightFixtureTestCase.getName();
    }

    public void setUpProjectRepository() throws IOException {
        this.projectRepositoryRemoteRoot = TestUtils.createDirectoryInTempDir("remote_repos/project");
        TestUtils.initBareGitRepo(this.projectRepositoryRemoteRoot);
        TestUtils.initGitRepo(this.project, this.projectBaseDir);
        TestUtils.setRemoteRepository(this.projectBaseDir, this.projectRepositoryRemoteRoot);
        TestUtils.addAndCommitTestfile(this.projectBaseDir);
        TestUtils.fetch(this.projectBaseDir);
        TestUtils.switchBranch(this.projectBaseDir, "master");
        TestUtils.createAndCommitGitignoreFile(this.projectBaseDir);
        TestUtils.push(this.projectBaseDir, "master");
    }

    public void setUpModuleRepository() throws IOException {
        this.moduleRepositoryRemoteRoot = TestUtils.createDirectoryInTempDir("remote_repos/module1");
        TestUtils.initBareGitRepo(this.moduleRepositoryRemoteRoot);
        TestUtils.initGitRepo(this.project, this.module1ContentRoot);
        TestUtils.setRemoteRepository(this.module1ContentRoot, this.moduleRepositoryRemoteRoot);
        TestUtils.addAndCommitTestfile(this.module1ContentRoot);
        TestUtils.fetch(this.module1ContentRoot);
        TestUtils.switchBranch(this.module1ContentRoot, "master");
        TestUtils.createAndCommitGitignoreFile(this.module1ContentRoot);
        TestUtils.push(this.module1ContentRoot, "master");
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
        this.projectAndModules = new ProjectAndModules(this.project, new Module[]{this.module1});

        setUpProjectRepository();
        setUpModuleRepository();

        // Die folgenden Zeilen sind wichtig, da Intellij sonst unsere Git Repositories nicht erkennt.
        TestUtils.syncFileSystem();
        final GitRepositoryManager manager = GitUtil.getRepositoryManager(this.project);
        manager.directoryMappingChanged();

        this.projectGitRepository = manager.getRepositoryForRoot(this.projectBaseDir);
        this.module1GitRepository = manager.getRepositoryForRoot(this.module1ContentRoot);

        this.gitflowGitRepository = new GitflowGitRepository(this.projectAndModules);
        this.gitflowGitRepository.addGitRepository(this.projectGitRepository);
        this.gitflowGitRepository.addGitRepository(this.module1GitRepository);

        // Gitflow anschalten
        GitflowInitOptions gitflowInitOptions = new GitflowInitOptions();
        gitflowInitOptions.setUseDefaults(true);

        TestUtils.enableGitflow(this.projectGitRepository, gitflowInitOptions);
        TestUtils.enableGitflow(this.module1GitRepository, gitflowInitOptions);

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
        TestUtils.deleteModuleDir(this.module1ContentRoot);

        FileUtils.forceDelete(this.projectRepositoryRemoteRoot);
        FileUtils.forceDelete(this.moduleRepositoryRemoteRoot);
    }
}
