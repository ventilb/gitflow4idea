package gitflow.git;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import gitflow.intellij.ProjectAndContentRoots;

/**
 * Provides utility methods to access the project and vcs information of a intellij project.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 11:40
 */
public class GitflowGitRepositoryUtil {

    public static ProjectAndContentRoots getAllProjectContentRoots(final Project project) {
        final VirtualFile projectBaseDir = project.getBaseDir();
        final VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();

        return new ProjectAndContentRoots(project, projectBaseDir, contentRoots);
    }

    public static GitflowGitRepository getAllGitRepositories(final ProjectAndContentRoots projectAndContentRoots) {
        final Project project = projectAndContentRoots.getProject();
        final GitRepositoryManager manager = GitUtil.getRepositoryManager(project);

        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository();

        for (VirtualFile virtualFile : projectAndContentRoots.getAllContentRoots()) {
            GitRepository repositoryForRoot = manager.getRepositoryForRoot(virtualFile);

            gitflowGitRepository.addGitRepository(repositoryForRoot);
        }


        return gitflowGitRepository;
    }
}
