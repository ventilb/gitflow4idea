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
import git4idea.commands.GitCommandResult;
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
    private static final Hashtable<String, List<File>> testFilesCreatedPerRepositoryRoot = new Hashtable<String, List<File>>();

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

    public static void registerFileCreatedInTest(final String repositoryRoot, final File fileCreated) {
        final List<File> filesCreated;
        if (testFilesCreatedPerRepositoryRoot.containsKey(repositoryRoot)) {
            filesCreated = testFilesCreatedPerRepositoryRoot.get(repositoryRoot);
        } else {
            filesCreated = new LinkedList<File>();
            testFilesCreatedPerRepositoryRoot.put(repositoryRoot, filesCreated);
        }

        filesCreated.add(fileCreated);
    }

    public static File createDirectoryInTempDir(final String directoryToCreateName) throws IOException {
        final File directoryToCreate = new File(FileUtils.getTempDirectory(), directoryToCreateName);
        FileUtils.forceMkdir(directoryToCreate);

        directoryToCreate.deleteOnExit();

        return directoryToCreate;
    }

    public static void deleteFilesCreatedInTest(final VirtualFile repositoryRoot) throws IOException {
        final String canonicalPath = repositoryRoot.getCanonicalPath();

        if (testFilesCreatedPerRepositoryRoot.containsKey(canonicalPath)) {
            final List<File> filesCreatedInTest = testFilesCreatedPerRepositoryRoot.get(canonicalPath);

            for (File file : filesCreatedInTest) {
                if (file.exists()) {
                    FileUtils.forceDelete(file);
                }
            }

            testFilesCreatedPerRepositoryRoot.remove(canonicalPath);
        }
    }

    // Gitflow helpers ////////////////////////////////////////////////////////

    public static void enableGitflow(final GitRepository gitRepository, final GitflowInitOptions gitflowInitOptions) {
        final Gitflow gitflow = ServiceManager.getService(Gitflow.class);
        final GitCommandResult gitCommandResult = gitflow.initRepo(gitRepository, gitflowInitOptions);

        assertThat(gitCommandResult.success(), is(true));
    }

    public static void startHotfix(final GitRepository gitRepository, final String hotfixName) {
        final Gitflow gitflow = ServiceManager.getService(Gitflow.class);
        final GitCommandResult gitCommandResult = gitflow.startHotfix(gitRepository, hotfixName);

        assertThat(gitCommandResult.success(), is(true));
    }

    public static void startRelease(final GitRepository gitRepository, final String releaseName) {
        final Gitflow gitflow = ServiceManager.getService(Gitflow.class);
        final GitCommandResult gitCommandResult = gitflow.startRelease(gitRepository, releaseName);

        assertThat(gitCommandResult.success(), is(true));
    }

    // Git command helpers ////////////////////////////////////////////////////

    public static String listGitConfig(final VirtualFile repositoryRoot) throws IOException {
        return performConsoleGitCommand(repositoryRoot.getCanonicalPath(), "config", "--list");
    }

    public static String[] listLocalBranchNames(final File repositoryRoot) throws IOException {
        final String canonicalPath = repositoryRoot.getCanonicalPath();

        final String branchNamesAsString = performConsoleGitCommand(canonicalPath, "branch", "-a", "--no-color");
        final String[] branchNames = branchNamesAsString.split("\\n");

        /*
        Ich habe keinen Weg gefunden, die Markierung des aktiven Branch nicht auszugeben. Daher filtern wir hier diese
         Markierung.
         */
        for (int i = 0; i < branchNames.length; i++) {
            if (branchNames[i].startsWith("*")) {
                branchNames[i] = branchNames[i].substring(1);
            }
            branchNames[i] = branchNames[i].trim();
        }

        return branchNames;
    }

    public static void cloneRemote(final VirtualFile localRepositoryRoot, final File remoteRepositoryRoot) throws IOException {
        final String localRepositoryCanonicalPath = localRepositoryRoot.getCanonicalPath();
        final String remoteRepositoryCanonicalPath = remoteRepositoryRoot.getCanonicalPath();
        performConsoleGitCommand(localRepositoryCanonicalPath, "clone", remoteRepositoryCanonicalPath, localRepositoryCanonicalPath);

        /*
        Wir haben hier an Intellij vorbei ganz simpel einen Git-Befehl als Prozess abgesetzt. Daher kann das virtuelle
        Dateisystem von Intellij und das echte Dateisystem nun nicht mehr synchron sein. Für unseren Test müssen wir
        die Dateisysteme abgleichen.
         */
        syncFileSystem();
    }

    public static void push(final VirtualFile repositoryRoot, final String branchNameToPush) throws IOException {
        final String canonicalPath = repositoryRoot.getCanonicalPath();

        performConsoleGitCommand(canonicalPath, "push", "origin", branchNameToPush);

        /*
        Wir haben hier an Intellij vorbei ganz simpel einen Git-Befehl als Prozess abgesetzt. Daher kann das virtuelle
        Dateisystem von Intellij und das echte Dateisystem nun nicht mehr synchron sein. Für unseren Test müssen wir
        die Dateisysteme abgleichen.
         */
        syncFileSystem();
    }

    public static void pull(final VirtualFile repositoryRoot, final String branchNameToPull) throws IOException {
        final String canonicalPath = repositoryRoot.getCanonicalPath();

        performConsoleGitCommand(canonicalPath, "pull", "origin", branchNameToPull);

        /*
        Wir haben hier an Intellij vorbei ganz simpel einen Git-Befehl als Prozess abgesetzt. Daher kann das virtuelle
        Dateisystem von Intellij und das echte Dateisystem nun nicht mehr synchron sein. Für unseren Test müssen wir
        die Dateisysteme abgleichen.
         */
        syncFileSystem();
    }

    public static void fetch(final VirtualFile repositoryRoot) throws IOException {
        final String canonicalPath = repositoryRoot.getCanonicalPath();

        performConsoleGitCommand(canonicalPath, "fetch");

        /*
        Wir haben hier an Intellij vorbei ganz simpel einen Git-Befehl als Prozess abgesetzt. Daher kann das virtuelle
        Dateisystem von Intellij und das echte Dateisystem nun nicht mehr synchron sein. Für unseren Test müssen wir
        die Dateisysteme abgleichen.
         */
        syncFileSystem();
    }

    public static void setRemoteRepository(final VirtualFile localRepositoryRoot, final File remoteRepositoryRoot) throws IOException {
        final String localRepositoryCanonicalPath = localRepositoryRoot.getCanonicalPath();
        final String remoteRepositoryCanonicalPath = remoteRepositoryRoot.getCanonicalPath();

        setRemoteRepository(localRepositoryCanonicalPath, remoteRepositoryCanonicalPath);

        /*
        Wir haben hier an Intellij vorbei ganz simpel einen Git-Befehl als Prozess abgesetzt. Daher kann das virtuelle
        Dateisystem von Intellij und das echte Dateisystem nun nicht mehr synchron sein. Für unseren Test müssen wir
        die Dateisysteme abgleichen.
         */
        syncFileSystem();
    }

    public static void setRemoteRepository(final File localRepositoryRoot, final File remoteRepositoryRoot) throws IOException {
        final String localRepositoryCanonicalPath = localRepositoryRoot.getCanonicalPath();
        final String remoteRepositoryCanonicalPath = remoteRepositoryRoot.getCanonicalPath();

        setRemoteRepository(localRepositoryCanonicalPath, remoteRepositoryCanonicalPath);
    }

    public static void setRemoteRepository(final String localRepositoryRoot, final String remoteRepositoryRoot) throws IOException {
        performConsoleGitCommand(localRepositoryRoot, "remote", "add", "origin", remoteRepositoryRoot);
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

    public static File addAndCommitTestfile(final File repositoryRoot) throws IOException {
        final String canonicalPath = repositoryRoot.getCanonicalPath();
        return addAndCommitTestfile(canonicalPath);
    }

    public static File addAndCommitTestfile(final VirtualFile repositoryRoot) throws IOException {
        return addAndCommitTestfile(repositoryRoot.getCanonicalPath());
    }

    public static File addAndCommitTestfile(final String repositoryRoot) throws IOException {
        final File testfileCreated = new File(repositoryRoot, "ATestFile.txt");
        assertThat(testfileCreated.createNewFile(), is(true));
        testfileCreated.deleteOnExit();

        add(repositoryRoot, testfileCreated);
        commit(repositoryRoot, "'* addAndCommitTestfile() performed'");

        registerFileCreatedInTest(repositoryRoot, testfileCreated);

        return testfileCreated;
    }

    public static void add(final String repositoryRoot, final File fileToAdd) throws IOException {
        performConsoleGitCommand(repositoryRoot, "add", fileToAdd.getCanonicalPath());
    }

    public static void add(final VirtualFile repositoryRoot, final File fileToAdd) throws IOException {
        add(repositoryRoot.getCanonicalPath(), fileToAdd);

        /*
        Wir haben hier an Intellij vorbei ganz simpel einen Git-Befehl als Prozess abgesetzt. Daher kann das virtuelle
        Dateisystem von Intellij und das echte Dateisystem nun nicht mehr synchron sein. Für unseren Test müssen wir
        die Dateisysteme abgleichen.
         */
        syncFileSystem();
    }

    public static void commit(final String repositoryRoot, final String oneLineCommitMessage) throws IOException {
        performConsoleGitCommand(repositoryRoot, "commit", "-a", "-m", oneLineCommitMessage);
    }

    public static void commit(final VirtualFile repositoryRoot, final String oneLineCommitMessage) throws IOException {
        commit(repositoryRoot.getCanonicalPath(), oneLineCommitMessage);

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

        /*
        Wir haben hier an Intellij vorbei ganz simpel einen Git-Befehl als Prozess abgesetzt. Daher kann das virtuelle
        Dateisystem von Intellij und das echte Dateisystem nun nicht mehr synchron sein. Für unseren Test müssen wir
        die Dateisysteme abgleichen.
         */
        syncFileSystem();
    }

    public static void initBareGitRepo(final File repositoryRoot) throws IOException {
        final String canonicalPath = repositoryRoot.getCanonicalPath();
        initBareGitRepo(canonicalPath);
    }

    public static void initBareGitRepo(final String repositoryRoot) throws IOException {
        performConsoleGitCommand(repositoryRoot, "--bare", "init", repositoryRoot);
        setInitRepoGitConfig(repositoryRoot);
    }

    public static void initGitRepo(final File repositoryRoot) throws IOException {
        final String canonicalPath = repositoryRoot.getCanonicalPath();
        initGitRepo(canonicalPath);
    }

    public static void initGitRepo(final VirtualFile repositoryRoot) throws IOException {
        final String canonicalPath = repositoryRoot.getCanonicalPath();
        initGitRepo(canonicalPath);
    }

    public static void initGitRepo(final String repositoryRoot) throws IOException {
        performConsoleGitCommand(repositoryRoot, "init", repositoryRoot);
        setInitRepoGitConfig(repositoryRoot);
    }

    public static void setInitRepoGitConfig(final String repositoryRoot) throws IOException {
        performConsoleGitCommand(repositoryRoot, "config", "user.name", "Unit Testcase");
        performConsoleGitCommand(repositoryRoot, "config", "user.email", "unit_testcase@nonexistent.com");
    }

    public static void createAndCommitGitignoreFile(final VirtualFile repositoryRoot) throws IOException {
        final String canonicalPath = repositoryRoot.getCanonicalPath();
        createAndCommitGitignoreFile(canonicalPath);
    }

    public static void createAndCommitGitignoreFile(final String repositoryRoot) throws IOException {
        final File gitignoreFile = new File(repositoryRoot, ".gitignore");
        final FileWriter gitignoreFileWriter = new FileWriter(gitignoreFile);

        /*
        Ich muss alles was es findet auf gitignore setzen, da ich keine Möglichkeit gefunden habe die Intellij Projekte
        in ein anderes Verzeichnis als /tmp zu erstellen. Da git aber dann wegen "Unstaged Changes" meckert ignoriere
        ich alles.
         */
        final Collection<File> filesToIgnore = FileUtils.listFilesAndDirs(new File(repositoryRoot), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
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
        } else if (stderrValueString.startsWith("From") && stderrValueString.contains("[new branch]")) {
            stdinValue.append(stderrValueString);
            stderrValueString = "";
        } else if (stderrValueString.startsWith("To") && stderrValueString.contains("[new branch]")) {
            stdinValue.append(stderrValueString);
            stderrValueString = "";
        } else if (stderrValueString.startsWith("Already on ")) {
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
