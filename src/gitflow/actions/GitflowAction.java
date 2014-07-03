package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import gitflow.Gitflow;
import gitflow.GitflowBranchUtil;
import gitflow.git.GitflowGitRepository;
import gitflow.git.GitflowGitRepositoryUtil;
import gitflow.intellij.ProjectAndModules;

public class GitflowAction extends DumbAwareAction {
    Project myProject;
    Gitflow myGitflow = ServiceManager.getService(Gitflow.class);

    protected GitflowGitRepository gitflowGitRepository;

    GitflowBranchUtil branchUtil;

    GitflowAction(String actionName){
        super(actionName);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        this.myProject = e.getProject();
        branchUtil=new GitflowBranchUtil(myProject);

        final ProjectAndModules projectAndModules = GitflowGitRepositoryUtil.getAllProjectContentRoots(this.myProject);
        this.gitflowGitRepository = GitflowGitRepositoryUtil.getAllGitRepositories(projectAndModules);
    }

    public void setProject(Project myProject) {
        this.myProject = myProject;
    }

    public void setBranchUtil(GitflowBranchUtil branchUtil) {
        this.branchUtil = branchUtil;
    }

    public void setGitflowGitRepository(GitflowGitRepository gitflowGitRepository) {
        this.gitflowGitRepository = gitflowGitRepository;
    }

}
