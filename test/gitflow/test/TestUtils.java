package gitflow.test;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import com.intellij.testFramework.fixtures.ModuleFixture;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import gitflow.Gitflow;
import gitflow.GitflowInitOptions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.fail;

/**
 * Provides some generic test helper methods.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 14:55
 */
public class TestUtils {
    private static final Logger log = Logger.getInstance(TestUtils.class);

    /**
     * Contains a map of files which were created during a test grouped by git repository root.
     */
    private static final Hashtable<VirtualFile, List<File>> testFilesCreatedPerRepositoryRoot = new Hashtable<VirtualFile, List<File>>();

    public static ModuleFixture newProjectModuleFixture(final JavaCodeInsightTestFixture testFixture, final TestFixtureBuilder<IdeaProjectTestFixture> ideaProjectBuilder, final String moduleName) throws IOException {
        final JavaModuleFixtureBuilder moduleFixtureBuilder = ideaProjectBuilder.addModule(JavaModuleFixtureBuilder.class);
        moduleFixtureBuilder.setLanguageLevel(LanguageLevel.JDK_1_6);
        final String contentRoot = testFixture.getTempDirPath();// + File.pathSeparator + moduleName;

        final File file = new File(contentRoot);
        if (!file.exists()) {
            assertThat(file.mkdirs(), is(true));
        }

        final ModuleFixture moduleFixture = moduleFixtureBuilder.addContentRoot(contentRoot).getFixture();
        return moduleFixture;
    }

    public static VirtualFile getModuleContentRoot(final Module module) {
        return ModuleRootManager.getInstance(module).getContentRoots()[0];
    }

    public static void changeFileContentTo(final File file, final String newContent) throws IOException {
        FileWriter fw = new FileWriter(file);
        fw.write(newContent);
        fw.flush();
        fw.close();
    }

    public static void registerFileCreatedInTest(final VirtualFile repositoryRoot, final File fileCreated) {
        final List<File> filesCreated;
        if (testFilesCreatedPerRepositoryRoot.containsKey(repositoryRoot)) {
            filesCreated = testFilesCreatedPerRepositoryRoot.get(repositoryRoot);
        } else {
            filesCreated = new LinkedList<File>();
            testFilesCreatedPerRepositoryRoot.put(repositoryRoot, filesCreated);
        }

        filesCreated.add(fileCreated);
    }

    public static void deleteFilesCreatedInTest(final VirtualFile repositoryRoot) throws IOException {
        if (testFilesCreatedPerRepositoryRoot.containsKey(repositoryRoot)) {
            final List<File> filesCreatedInTest = testFilesCreatedPerRepositoryRoot.get(repositoryRoot);

            for (File file : filesCreatedInTest) {
                if (file.exists()) {
                    FileUtils.forceDelete(file);
                }
            }

            filesCreatedInTest.clear();
            testFilesCreatedPerRepositoryRoot.remove(repositoryRoot);
        }
    }

    // Gitflow helpers ////////////////////////////////////////////////////////

    public static void enableGitflow(final GitRepository gitRepository, final GitflowInitOptions gitflowInitOptions) {
        Gitflow gitflow = ServiceManager.getService(Gitflow.class);
        gitflow.initRepo(gitRepository, gitflowInitOptions);
    }

    public static void startHotfix(final GitRepository gitRepository, final String hotfixName) {
        Gitflow gitflow = ServiceManager.getService(Gitflow.class);
        gitflow.startHotfix(gitRepository, hotfixName);
    }

    // Git command helpers ////////////////////////////////////////////////////

    public static String listGitConfig(final VirtualFile repositoryRoot) throws IOException {
        return performConsoleGitCommand(repositoryRoot.getCanonicalPath(), "config", "--list");
    }

    public static void switchBranch(final GitRepository gitRepository, final String targetBranch) throws IOException {
        final VirtualFile repositoryRoot = gitRepository.getRoot();
        switchBranch(repositoryRoot, targetBranch);
    }

    public static void switchBranch(final VirtualFile repositoryRoot, final String targetBranch) throws IOException {
        performConsoleGitCommand(repositoryRoot.getCanonicalPath(), "checkout", targetBranch);

        /*
        Wir haben hier an Intellij vorbei ganz simpel einen Git-Befehl als Prozess abgesetzt. Daher kann das virtuelle
        Dateisystem von Intellij und das echte Dateisystem nun nicht mehr synchron sein. Für unseren Test müssen wir
        die Dateisysteme abgleichen.
         */
        syncFileSystem();
    }

    public static void createBranch(final GitRepository gitRepository, final String branchName) throws IOException {
        final VirtualFile repositoryRoot = gitRepository.getRoot();
        performConsoleGitCommand(repositoryRoot.getCanonicalPath(), "branch", branchName);
    }

    public static File addAndCommitTestfile(final VirtualFile repositoryRoot) throws IOException {
        final File testfileCreated = new File(repositoryRoot.getCanonicalPath(), "ATestFile.txt");
        assertThat(testfileCreated.createNewFile(), is(true));
        testfileCreated.deleteOnExit();

        add(repositoryRoot, testfileCreated);
        commit(repositoryRoot, "'* addAndCommitTestfile() performed'");

        registerFileCreatedInTest(repositoryRoot, testfileCreated);

        return testfileCreated;
    }

    public static void add(final VirtualFile repositoryRoot, final File fileToAdd) throws IOException {
        performConsoleGitCommand(repositoryRoot.getCanonicalPath(), "add", fileToAdd.getCanonicalPath());
        /*
        Wir haben hier an Intellij vorbei ganz simpel einen Git-Befehl als Prozess abgesetzt. Daher kann das virtuelle
        Dateisystem von Intellij und das echte Dateisystem nun nicht mehr synchron sein. Für unseren Test müssen wir
        die Dateisysteme abgleichen.
         */
        syncFileSystem();
    }

    public static void commit(final VirtualFile repositoryRoot, final String oneLineCommitMessage) throws IOException {
        performConsoleGitCommand(repositoryRoot.getCanonicalPath(), "commit", "-a", "-m", oneLineCommitMessage);

        /*
        Wir haben hier an Intellij vorbei ganz simpel einen Git-Befehl als Prozess abgesetzt. Daher kann das virtuelle
        Dateisystem von Intellij und das echte Dateisystem nun nicht mehr synchron sein. Für unseren Test müssen wir
        die Dateisysteme abgleichen.
         */
        syncFileSystem();
    }

    public static void initGitRepo(final Project project, final VirtualFile virtualFile) throws IOException {
        initGitRepo(virtualFile);

        ProjectLevelVcsManager projectLevelVcsManager = ProjectLevelVcsManager.getInstance(project);
        projectLevelVcsManager.setDirectoryMapping(virtualFile.getCanonicalPath(), "Git");
    }

    public static void initGitRepo(final VirtualFile repositoryRoot) throws IOException {
        final String canonicalPath = repositoryRoot.getCanonicalPath();

        performConsoleGitCommand(canonicalPath, "init", canonicalPath);
        performConsoleGitCommand(canonicalPath, "config", "user.name", "Unit Testcase");
        performConsoleGitCommand(canonicalPath, "config", "user.email", "unit_testcase@nonexistent.com");

        createAndCommitGitignoreFile(repositoryRoot);

        /*
        Wir haben hier an Intellij vorbei ganz simpel einen Git-Befehl als Prozess abgesetzt. Daher kann das virtuelle
        Dateisystem von Intellij und das echte Dateisystem nun nicht mehr synchron sein. Für unseren Test müssen wir
        die Dateisysteme abgleichen.
         */
        syncFileSystem();
    }

    public static void createAndCommitGitignoreFile(final VirtualFile repositoryRoot) throws IOException {
        final String canonicalPath = repositoryRoot.getCanonicalPath();

        final File gitignoreFile = new File(canonicalPath, ".gitignore");
        final FileWriter gitignoreFileWriter = new FileWriter(gitignoreFile);

        /*
        Ich muss alles was es findet auf gitignore setzen, da ich keine Möglichkeit gefunden habe die Intellij Projekte
        in ein anderes Verzeichnis als /tmp zu erstellen. Da git aber dann wegen "Unstaged Changes" meckert ignoriere
        ich alles.
         */
        final Collection<File> filesToIgnore = FileUtils.listFilesAndDirs(new File(canonicalPath), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File fileToIgnore : filesToIgnore) {
            if (!".gitignore".equals(fileToIgnore.getName())) {
                gitignoreFileWriter.append(fileToIgnore.getName()).append("\n");
            }
        }

        gitignoreFileWriter.flush();
        gitignoreFileWriter.close();

        add(repositoryRoot, gitignoreFile);
        commit(repositoryRoot, "* .gitignore added");

        registerFileCreatedInTest(repositoryRoot, gitignoreFile);
    }

    public static String performConsoleGitCommand(final String gitDir, @NotNull final String command, @NotNull String... arguments) throws IOException {
        /*
        Wir erzwingen hier die Default-SPrache für Git, da wir für Workarounds die Ausgabe verarbeiten müssen. Je nach
        Rechner würde Git nativ in einer anderen Sprache antworten. Daher fallen wir auf das Default zurück.
         */
        final String[] environment = new String[]{
                "LC_ALL", "C"
        };

        final List<String> args = new LinkedList<String>();
        args.add("git");
        args.add(command);
        args.addAll(Arrays.asList(arguments));
        Process p = Runtime.getRuntime().exec(args.toArray(new String[args.size()]), environment, new File(gitDir));

        final StringBuilder stdinValue = new StringBuilder();
        final InputStream stdin = p.getInputStream();
        BufferedReader stdinBufferedReader = new BufferedReader(new InputStreamReader(stdin));
        String line;
        while ((line = stdinBufferedReader.readLine()) != null) {
            stdinValue.append(line).append("\n");
        }

        final StringBuilder stderrValue = new StringBuilder();
        final InputStream stderr = p.getErrorStream();
        BufferedReader stderrBufferedReader = new BufferedReader(new InputStreamReader(stderr));
        while ((line = stderrBufferedReader.readLine()) != null) {
            stderrValue.append(line).append("\n");
        }

        /*
        Workaround: Git schreibt das erfolgreiche Wechseln eines Branches nach stderr. Daher prüfen wir den Fehlerstring
        und behandeln erfolgreiches Wechseln nicht als Fehler.

        Einzige gefundene Quelle dazu: http://git.661346.n2.nabble.com/Bugreport-Git-responds-with-stderr-instead-of-stdout-td4959280.html
         */
        String stderrValueString = stderrValue.toString();
        if (stderrValueString.startsWith("Switched to branch")) {
            stdinValue.append(stderrValueString);
            stderrValueString = "";
        }

        assertThat(stderrValueString, is(isEmptyString()));

        try {
            p.waitFor();
        } catch (InterruptedException e) {
            fail("Should not have caught InterruptedException");
        }

        return stdinValue.toString();
    }

    public static void deleteProjectGitDir(final VirtualFile repositoryRoot) throws IOException {
        deleteFilesCreatedInTest(repositoryRoot);

        final File projectGitDir = new File(repositoryRoot.getCanonicalPath(), GitUtil.DOT_GIT);
        if (projectGitDir.exists()) {
            FileUtils.forceDelete(projectGitDir);
        }
    }

    public static void deleteModuleDir(final VirtualFile repositoryRoot) throws IOException {
        deleteFilesCreatedInTest(repositoryRoot);

        final File projectGitDir = new File(repositoryRoot.getCanonicalPath());
        if (projectGitDir.exists()) {
            FileUtils.forceDelete(projectGitDir);
        }
    }

    public static void syncFileSystem() {
        final VirtualFileManager virtualFileMananger = VirtualFileManager.getInstance();

        virtualFileMananger.syncRefresh();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            log.error("InterruptedException occured", e);
        }
    }


}
