package gitflow.git;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import gitflow.intellij.ProjectAndModules;
import org.jetbrains.annotations.NotNull;

/**
 * Provides utility methods to access the project and vcs information of a intellij project.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 11:40
 */
public class GitflowGitRepositoryUtil {

    public static String getHumanReadableRepositoryName(@NotNull final GitRepository gitRepository) {
        final VirtualFile gitRepositoryRoot = gitRepository.getRoot();
        return gitRepositoryRoot.getPresentableName();
    }

    public static ProjectAndModules getAllProjectContentRoots(final Project project) {
        final ModuleManager moduleManager = ModuleManager.getInstance(project);

        final Module[] modules = moduleManager.getModules();
        return new ProjectAndModules(project, modules);
    }

    public static GitflowGitRepository getAllGitRepositories(final ProjectAndModules projectAndModules) {
        final Project project = projectAndModules.getProject();
        final GitRepositoryManager manager = GitUtil.getRepositoryManager(project);

        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(projectAndModules);

        for (VirtualFile virtualFile : projectAndModules.getAllContentRoots()) {
            GitRepository repositoryForRoot = manager.getRepositoryForRoot(virtualFile);

            gitflowGitRepository.addGitRepository(repositoryForRoot);
        }

        return gitflowGitRepository;
    }
}
