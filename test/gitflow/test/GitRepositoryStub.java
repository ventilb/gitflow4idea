package gitflow.test;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchesCollection;
import git4idea.repo.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Implements a stub to intellij's {@link git4idea.repo.GitRepository} interface.
 *
 * @author <a href="mailto:mschulze@geneon.de">Manuel Schulze</a>
 * @since 23.06.14 - 01:35
 */
public class GitRepositoryStub implements GitRepository {
    @NotNull
    @Override
    public VirtualFile getGitDir() {
        return null;
    }

    @NotNull
    @Override
    public GitUntrackedFilesHolder getUntrackedFilesHolder() {
        return null;
    }

    @NotNull
    @Override
    public GitRepoInfo getInfo() {
        return null;
    }

    @Nullable
    @Override
    public GitLocalBranch getCurrentBranch() {
        return null;
    }

    @NotNull
    @Override
    public GitBranchesCollection getBranches() {
        return null;
    }

    @NotNull
    @Override
    public Collection<GitRemote> getRemotes() {
        return null;
    }

    @NotNull
    @Override
    public Collection<GitBranchTrackInfo> getBranchTrackInfos() {
        return null;
    }

    @Override
    public boolean isRebaseInProgress() {
        return false;
    }

    @Override
    public boolean isOnBranch() {
        return false;
    }

    @NotNull
    @Override
    public VirtualFile getRoot() {
        return null;
    }

    @NotNull
    @Override
    public String getPresentableUrl() {
        return null;
    }

    @NotNull
    @Override
    public Project getProject() {
        return null;
    }

    @NotNull
    @Override
    public State getState() {
        return null;
    }

    @Nullable
    @Override
    public String getCurrentRevision() {
        return null;
    }

    @Override
    public boolean isFresh() {
        return false;
    }

    @Override
    public void update() {

    }

    @NotNull
    @Override
    public String toLogString() {
        return null;
    }
}
