package gitflow.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Implements an abstraction to hold an intellij project and it's project dir and content roots.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 12:51
 */
public class ProjectAndContentRoots {

    private final Project project;

    private final VirtualFile projectBaseDir;

    private final VirtualFile[] contentRoots;

    public ProjectAndContentRoots(final Project project, final VirtualFile projectBaseDir, final VirtualFile[] contentRoots) {
        this.project = project;
        this.contentRoots = contentRoots;
        this.projectBaseDir = projectBaseDir;
    }

    /**
     * Gets the project.
     *
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Gets the project base dir.
     *
     * @return the project base dir
     */
    public VirtualFile getProjectBaseDir() {
        return projectBaseDir;
    }

    /**
     * Gets the module content roots.
     *
     * @return the virtual file [ ]
     */
    public VirtualFile[] getContentRoots() {
        return contentRoots;
    }

    /**
     * All content roots including the project base.
     *
     * @return the collection
     */
    public Collection<VirtualFile> getAllContentRoots() {
        final Collection<VirtualFile> allContentRoots = new LinkedList<VirtualFile>();
        allContentRoots.add(this.projectBaseDir);
        allContentRoots.addAll(Arrays.asList(this.contentRoots));
        return allContentRoots;
    }
}
