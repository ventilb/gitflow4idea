package gitflow;

import com.intellij.openapi.project.Project;
import git4idea.GitLocalBranch;
import git4idea.GitRemoteBranch;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import git4idea.validators.GitNewBranchNameValidator;
import gitflow.git.GitflowGitRepository;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */
public class GitflowBranchUtil {

    Project myProject;
    GitRepository repo;

    String currentBranchName;
    String branchnameMaster;
    String prefixFeature;
    String prefixRelease;
    String prefixHotfix;

    @Deprecated
    public GitflowBranchUtil(Project project) {
        myProject = project;
        repo = GitBranchUtil.getCurrentRepository(project);

        currentBranchName = GitBranchUtil.getBranchNameOrRev(repo);

        branchnameMaster = GitflowConfigUtil.getMasterBranch(project);
        prefixFeature = GitflowConfigUtil.getFeaturePrefix(project);
        prefixRelease = GitflowConfigUtil.getReleasePrefix(project);
        prefixHotfix = GitflowConfigUtil.getHotfixPrefix(project);
    }

    public GitflowBranchUtil(final GitRepository gitRepository) {
        this.repo = gitRepository;
        this.myProject = gitRepository.getProject();

        this.currentBranchName = GitBranchUtil.getBranchNameOrRev(this.repo);
        branchnameMaster = GitflowConfigUtil.getMasterBranch(this.repo);
        // TODO Alles prüfen
    }

    /**
     * Factory method to create a {@link git4idea.validators.GitNewBranchNameValidator} for a
     * {@link gitflow.git.GitflowGitRepository}.
     *
     * @param gitflowGitRepository the gitflow git repository
     * @return git new branch name validator
     */
    public static GitNewBranchNameValidator createGitNewBranchNameValidator(GitflowGitRepository gitflowGitRepository) {
        final Collection<GitRepository> gitRepositories = gitflowGitRepository.getGitRepositories();
        final GitNewBranchNameValidator gitNewBranchNameValidator = GitNewBranchNameValidator.newInstance(gitRepositories);

        return gitNewBranchNameValidator;
    }

    public boolean hasGitflow() {
        boolean hasGitflow = false;
        hasGitflow = GitflowConfigUtil.getMasterBranch(myProject) != null
                && GitflowConfigUtil.getDevelopBranch(myProject) != null
                && GitflowConfigUtil.getFeaturePrefix(myProject) != null
                && GitflowConfigUtil.getReleasePrefix(myProject) != null
                && GitflowConfigUtil.getHotfixPrefix(myProject) != null;

        return hasGitflow;
    }

    public boolean isCurrentBranchMaster() {
        return currentBranchName.startsWith(branchnameMaster);
    }

    public boolean isCurrentBranchFeature() {
        return currentBranchName.startsWith(prefixFeature);
    }


    public boolean isCurrentBranchRelease() {
        return currentBranchName.startsWith(prefixRelease);
    }

    public boolean isCurrentBranchHotfix() {
        return currentBranchName.startsWith(prefixHotfix);
    }

    //checks whether the current branch also exists on the remote
    public boolean isCurrentBranchPublished() {
        return getRemoteBranchesWithPrefix(currentBranchName).isEmpty() == false;
    }


    //if no prefix specified, returns all remote branches
    public ArrayList<String> getRemoteBranchesWithPrefix(String prefix) {
        ArrayList<String> remoteBranches = getRemoteBranchNames();
        ArrayList<String> selectedBranches = new ArrayList<String>();

        for (Iterator<String> i = remoteBranches.iterator(); i.hasNext(); ) {
            String branch = i.next();
            if (branch.contains(prefix)) {
                selectedBranches.add(branch);
            }
        }

        return selectedBranches;
    }


    public static Collection<String> filterBranchListByPrefix(Collection<String> inputBranches, String prefix) {
        ArrayList<String> outputBranches = new ArrayList<String>();

        for (Iterator<String> i = inputBranches.iterator(); i.hasNext(); ) {
            String branch = i.next();
            if (branch.contains(prefix)) {
                outputBranches.add(branch);
            }
        }

        return outputBranches;
    }

    @NotNull
    public static Collection<String> removePrefixFromBranchNames(@NotNull final Collection<String> inputBranches, @NotNull final String prefix) {
        final Set<String> outputBranches = new HashSet<String>();

        final int prefixLength = prefix.length();

        String branchName;
        for (String inputBranch : inputBranches) {
            if (inputBranch.startsWith(prefix)) {
                branchName = inputBranch.substring(prefixLength);
                outputBranches.add(branchName);
            }
        }

        return outputBranches;
    }

    public ArrayList<String> getRemoteBranchNames() {
        ArrayList<GitRemoteBranch> remoteBranches = new ArrayList<GitRemoteBranch>(repo.getBranches().getRemoteBranches());
        ArrayList<String> branchNameList = new ArrayList<String>();

        for (Iterator<GitRemoteBranch> i = remoteBranches.iterator(); i.hasNext(); ) {
            GitRemoteBranch branch = i.next();
            branchNameList.add(branch.getName());
        }

        return branchNameList;
    }

    public static Collection<String> getRemoteBranchNames(final GitRepository gitRepository) {
        ArrayList<GitRemoteBranch> remoteBranches = new ArrayList<GitRemoteBranch>(gitRepository.getBranches().getRemoteBranches());
        ArrayList<String> branchNameList = new ArrayList<String>();

        for (Iterator<GitRemoteBranch> i = remoteBranches.iterator(); i.hasNext(); ) {
            GitRemoteBranch branch = i.next();
            branchNameList.add(branch.getName());
        }

        return branchNameList;
    }


    public ArrayList<String> getLocalBranchNames() {
        ArrayList<GitLocalBranch> localBranches = new ArrayList<GitLocalBranch>(repo.getBranches().getLocalBranches());
        ArrayList<String> branchNameList = new ArrayList<String>();

        for (Iterator<GitLocalBranch> i = localBranches.iterator(); i.hasNext(); ) {
            GitLocalBranch branch = i.next();
            branchNameList.add(branch.getName());
        }

        return branchNameList;
    }


    public GitRemote getRemoteByBranch(String branchName) {
        GitRemote remote = null;

        ArrayList<GitRemoteBranch> remoteBranches = new ArrayList<GitRemoteBranch>(repo.getBranches().getRemoteBranches());

        for (Iterator<GitRemoteBranch> i = remoteBranches.iterator(); i.hasNext(); ) {
            GitRemoteBranch branch = i.next();
            if (branch.getName() == branchName) {
                remote = branch.getRemote();
                break;
            }
        }

        return remote;
    }

    public boolean areAllBranchesTracked(String prefix) {


        Collection<String> localBranches = filterBranchListByPrefix(getLocalBranchNames(), prefix);

        //to avoid a vacuous truth value. That is, when no branches at all exist, they shouldn't be
        //considered as "all pulled"
        if (localBranches.isEmpty()) {
            return false;
        }

        ArrayList<String> remoteBranches = getRemoteBranchNames();

        //check that every local branch has a matching remote branch
        for (Iterator<String> i = localBranches.iterator(); i.hasNext(); ) {
            String localBranch = i.next();
            boolean hasMatchingRemoteBranch = false;

            for (Iterator<String> j = remoteBranches.iterator(); j.hasNext(); ) {
                String remoteBranch = j.next();

                if (remoteBranch.contains(localBranch)) {
                    hasMatchingRemoteBranch = true;
                    break;
                }
            }

            //at least one matching branch wasn't found
            if (hasMatchingRemoteBranch == false) {
                return false;
            }
        }

        return true;
    }
}
