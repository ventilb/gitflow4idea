package gitflow.intellij;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.*;

/**
 * Implements an abstraction to hold an intellij project and it's modules.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 12:51
 */
public class ProjectAndModules {

    private final Project project;

    private final Module[] modules;

    private VirtualFile projectBaseDir;

    private final List<VirtualFile> moduleContentRoots = new LinkedList<VirtualFile>();

    public ProjectAndModules(final Project project, final Module[] modules) {
        this.project = project;
        this.modules = modules;

        this.projectBaseDir = project.getBaseDir();

        initModuleContentRoots();
    }

    protected void initModuleContentRoots() {
        this.moduleContentRoots.clear();

        ModuleRootManager moduleRootManager;
        VirtualFile[] moduleContentRoots;

        for (Module module : this.modules) {
            moduleRootManager = ModuleRootManager.getInstance(module);
            moduleContentRoots = moduleRootManager.getContentRoots();

            this.moduleContentRoots.add(moduleContentRoots[0]);
        }
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
    public List<VirtualFile> getModuleContentRoots() {
        return Collections.unmodifiableList(this.moduleContentRoots);
    }

    /**
     * All content roots including the project base.
     *
     * @return the collection
     */
    public Collection<VirtualFile> getAllContentRoots() {
        final Collection<VirtualFile> allContentRoots = new LinkedList<VirtualFile>();
        allContentRoots.add(this.projectBaseDir);
        allContentRoots.addAll(this.moduleContentRoots);
        return allContentRoots;
    }
}
