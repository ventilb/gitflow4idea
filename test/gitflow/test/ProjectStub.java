package gitflow.test;

import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.picocontainer.PicoContainer;

/**
 * Implements a stub to intellij's {@link com.intellij.openapi.project.Project} interface.
 *
 * @author <a href="mailto:mschulze@geneon.de">Manuel Schulze</a>
 * @since 23.06.14 - 01:38
 */
public class ProjectStub implements Project {
    @NotNull
    @Override
    public String getName() {
        return null;
    }

    @Override
    public VirtualFile getBaseDir() {
        return null;
    }

    @Override
    public String getBasePath() {
        return null;
    }

    @Nullable
    @Override
    public VirtualFile getProjectFile() {
        return null;
    }

    @NotNull
    @Override
    public String getProjectFilePath() {
        return null;
    }

    @Nullable
    @Override
    public String getPresentableUrl() {
        return null;
    }

    @Nullable
    @Override
    public VirtualFile getWorkspaceFile() {
        return null;
    }

    @NotNull
    @Override
    public String getLocationHash() {
        return null;
    }

    @Override
    public void save() {

    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public BaseComponent getComponent(@NotNull String s) {
        return null;
    }

    @Override
    public <T> T getComponent(@NotNull Class<T> tClass) {
        return null;
    }

    @Override
    public <T> T getComponent(@NotNull Class<T> tClass, T t) {
        return null;
    }

    @Override
    public boolean hasComponent(@NotNull Class aClass) {
        return false;
    }

    @NotNull
    @Override
    public <T> T[] getComponents(@NotNull Class<T> tClass) {
        return null;
    }

    @NotNull
    @Override
    public PicoContainer getPicoContainer() {
        return null;
    }

    @NotNull
    @Override
    public MessageBus getMessageBus() {
        return null;
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    @NotNull
    @Override
    public <T> T[] getExtensions(@NotNull ExtensionPointName<T> tExtensionPointName) {
        return null;
    }

    @NotNull
    @Override
    public Condition getDisposed() {
        return null;
    }

    @Override
    public void dispose() {

    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> tKey) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> tKey, @Nullable T t) {

    }
}
