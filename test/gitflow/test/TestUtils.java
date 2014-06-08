package gitflow.test;

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
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Provides some generic test helper methods.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 14:55
 */
public class TestUtils {
    private static final Logger log = Logger.getInstance(TestUtils.class);

    public static ModuleFixture newProjectModuleFixture(final JavaCodeInsightTestFixture testFixture, final TestFixtureBuilder<IdeaProjectTestFixture> ideaProjectBuilder, final String moduleName) throws IOException {
        final JavaModuleFixtureBuilder moduleFixtureBuilder = ideaProjectBuilder.addModule(JavaModuleFixtureBuilder.class);
        moduleFixtureBuilder.setLanguageLevel(LanguageLevel.JDK_1_6);
        final String contentRoot = testFixture.getTempDirPath() + File.pathSeparator + moduleName;

        final File file = new File(contentRoot);
        if (!file.exists()) {
            assertThat(file.mkdirs(), is(true));
        }

        final ModuleFixture moduleFixture = moduleFixtureBuilder.addContentRoot(contentRoot).addSourceRoot("src").getFixture();
        return moduleFixture;
    }

    public static VirtualFile getModuleContentRoot(final Module module) {
        return ModuleRootManager.getInstance(module).getContentRoots()[0];
    }

    // Gitflow helpers ////////////////////////////////////////////////////////

    // Git command helpers ////////////////////////////////////////////////////

    public static String listGitConfig(final VirtualFile gitDir) throws IOException {
        return performConsoleGitCommand(gitDir.getCanonicalPath(), "config", "--list");
    }

    public static void initGitRepo(final Project project, final VirtualFile virtualFile) throws IOException {
        final String canonicalPath = virtualFile.getCanonicalPath();
        initGitRepo(canonicalPath);

        /*
        Wir haben hier an Intellij vorbei ganz simpel einen Git-Befehl als Prozess abgesetzt. Daher kann das virtuelle
        Dateisystem von Intellij und das echte Dateisystem nun nicht mehr synchron sein. Für unseren Test müssen wir
        die Dateisysteme abgleichen.
         */
        syncFileSystem();

        ProjectLevelVcsManager projectLevelVcsManager = ProjectLevelVcsManager.getInstance(project);
        projectLevelVcsManager.setDirectoryMapping(virtualFile.getCanonicalPath(), "Git");
    }

    public static void initGitRepo(final String canonicalPath) throws IOException {
        performConsoleGitCommand(canonicalPath + "/.git", "init", canonicalPath);
    }

    public static String performConsoleGitCommand(final String gitDir, @NotNull final String command, @NotNull String... arguments) throws IOException {
        final List<String> args = new LinkedList<String>();
        args.add("git");
        args.add("--git-dir");
        args.add(gitDir);
        args.add(command);
        args.addAll(Arrays.asList(arguments));
        Process p = Runtime.getRuntime().exec(args.toArray(new String[args.size()]));

        StringBuilder sb = new StringBuilder();

        final InputStream stdin = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(stdin));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }

        try {
            p.waitFor();
        } catch (InterruptedException e) {
            throw new IOException("Process interrupted", e);
        }

        return sb.toString();
    }

    public static void deleteProjectGitDir(final VirtualFile projectBaseDir) throws IOException {
        final File projectGitDir = new File(projectBaseDir.getCanonicalPath(), GitUtil.DOT_GIT);
        if (projectGitDir.exists()) {
            FileUtils.forceDelete(projectGitDir);
        }
    }

    public static void deleteModuleDir(final VirtualFile moduleContentRoot) throws IOException {
        final File projectGitDir = new File(moduleContentRoot.getCanonicalPath());
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
