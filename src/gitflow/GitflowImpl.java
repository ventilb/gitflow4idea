package gitflow;

import git4idea.commands.*;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import gitflow.git.GitflowGitCommandResult;
import gitflow.git.GitflowGitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */


public class GitflowImpl extends GitImpl implements Gitflow {

    //we must use reflection to add this command, since the git4idea implementation doesn't expose it
    private GitCommand GitflowCommand() {
        Method m = null;
        try {
            m = GitCommand.class.getDeclaredMethod("write", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        //m.invoke(d);//exception java.lang.IllegalAccessException
        m.setAccessible(true);//Abracadabra

        GitCommand command = null;

        try {
            command = (GitCommand) m.invoke(null, "flow");//now its ok
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return command;
    }

    //we must use reflection to add this command, since the git4idea implementation doesn't expose it
    private static GitCommandResult run(@org.jetbrains.annotations.NotNull git4idea.commands.GitLineHandler handler) {
        Method m = null;
        try {
            m = GitImpl.class.getDeclaredMethod("run", GitLineHandler.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        m.setAccessible(true);//Abracadabra

        GitCommandResult result = null;

        try {
            result = (GitCommandResult) m.invoke(null, handler);//now its ok
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public GitflowGitCommandResult initRepos(@NotNull GitflowGitRepository gitflowGitRepository, GitflowInitOptions initOptions, @Nullable GitLineHandlerListener... listeners) {
        final GitflowGitCommandResult gitflowGitCommandResult = new GitflowGitCommandResult();

        GitCommandResult gitCommandResult;
        for (GitRepository gitRepository : gitflowGitRepository.gitRepositories()) {
            gitCommandResult = initRepo(gitRepository, initOptions, listeners);
            gitflowGitCommandResult.setGitCommandResultForGitRepository(gitRepository, gitCommandResult);
        }

        return gitflowGitCommandResult;
    }

    public GitCommandResult initRepo(@NotNull GitRepository repository,
                                     GitflowInitOptions initOptions, @Nullable GitLineHandlerListener... listeners) {
        if (!initOptions.isUseDefaults()) {
            configureBranches(repository, initOptions);
        }

        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        h.setSilent(false);

        h.addParameters("init");
        h.addParameters("-d");

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        GitCommandResult result = run(h);

        if (result.success() && !initOptions.isUseDefaults()) {
            configurePrefixes(repository, initOptions);
        }

        return result;
    }

    /**
     * Configures the gitflow branches.
     *
     * @param gitRepository The git repository to configure
     * @param initOptions   The gitflow options to use
     */
    private void configureBranches(final GitRepository gitRepository, final GitflowInitOptions initOptions) {
        GitflowConfigUtil.setMasterBranch(gitRepository, initOptions.getProductionBranch());
        GitflowConfigUtil.setDevelopBranch(gitRepository, initOptions.getDevelopmentBranch());
    }

    /**
     * Configures the gitflox branch and version prefixes.
     *
     * @param gitRepository The git repository to configure
     * @param initOptions   The gitflow options to use
     */
    private void configurePrefixes(final GitRepository gitRepository, final GitflowInitOptions initOptions) {
        GitflowConfigUtil.setFeaturePrefix(gitRepository, initOptions.getFeaturePrefix());
        GitflowConfigUtil.setReleasePrefix(gitRepository, initOptions.getReleasePrefix());
        GitflowConfigUtil.setHotfixPrefix(gitRepository, initOptions.getHotfixPrefix());
        GitflowConfigUtil.setSupportPrefix(gitRepository, initOptions.getSupportPrefix());
        GitflowConfigUtil.setVersionPrefix(gitRepository, initOptions.getVersionPrefix());
    }

    //feature

    public GitCommandResult startFeature(@NotNull GitRepository repository,
                                         @NotNull String featureName,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        h.setSilent(false);

        h.addParameters("feature");
        h.addParameters("start");
        h.addParameters(featureName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    public GitCommandResult finishFeature(@NotNull GitRepository repository,
                                          @NotNull String featureName,
                                          @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitflowCommand());

        setUrl(h, repository);
        h.setSilent(false);

        h.addParameters("feature");
        h.addParameters("finish");
        h.addParameters(featureName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }


    public GitCommandResult publishFeature(@NotNull GitRepository repository,
                                           @NotNull String featureName,
                                           @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);

        h.addParameters("feature");
        h.addParameters("publish");
        h.addParameters(featureName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }


    // feature pull seems to be kind of useless. see
    // http://stackoverflow.com/questions/18412750/why-doesnt-git-flow-feature-pull-track
    public GitCommandResult pullFeature(@NotNull GitRepository repository,
                                        @NotNull String featureName,
                                        @NotNull GitRemote remote,
                                        @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);
        h.addParameters("feature");
        h.addParameters("pull");
        h.addParameters(remote.getName());
        h.addParameters(featureName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    public GitCommandResult trackFeature(@NotNull GitRepository repository,
                                         @NotNull String featureName,
                                         @NotNull GitRemote remote,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);
        h.addParameters("feature");
        h.addParameters("track");
        h.addParameters(featureName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }


    //release

    @Override
    public GitflowGitCommandResult startRelease(@NotNull GitflowGitRepository gitflowGitRepository, @NotNull String releaseName, @Nullable GitLineHandlerListener... listeners) {
        final GitflowGitCommandResult gitflowGitCommandResult = new GitflowGitCommandResult();

        GitCommandResult gitCommandResult;
        for (GitRepository gitRepository : gitflowGitRepository.gitRepositories()) {
            gitCommandResult = startRelease(gitRepository, releaseName, listeners);
            gitflowGitCommandResult.setGitCommandResultForGitRepository(gitRepository, gitCommandResult);
        }

        return gitflowGitCommandResult;
    }

    public GitCommandResult startRelease(@NotNull GitRepository repository,
                                         @NotNull String releaseName,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("start");
        h.addParameters(releaseName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    public GitCommandResult finishRelease(@NotNull GitRepository repository,
                                          @NotNull String releaseName,
                                          @NotNull String tagMessage,
                                          @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("finish");
        if (GitflowConfigurable.pushOnReleaseFinish(repository.getProject())) {
            h.addParameters("-p");
        }

        if (GitflowConfigurable.dontTagRelease(repository.getProject())) {
            h.addParameters("-n");
        } else {
            h.addParameters("-m");
            h.addParameters(tagMessage);
        }

        h.addParameters(releaseName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }


    public GitCommandResult publishRelease(@NotNull GitRepository repository,
                                           @NotNull String releaseName,
                                           @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);

        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("publish");
        h.addParameters(releaseName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    public GitCommandResult trackRelease(@NotNull GitRepository repository,
                                         @NotNull String releaseName,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);

        h.addParameters("release");
        h.addParameters("track");
        h.addParameters(releaseName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }


    //hotfix

    public GitflowGitCommandResult startHotfix(@NotNull GitflowGitRepository gitflowGitRepository, @NotNull String hotfixName,
                            @Nullable GitLineHandlerListener... listeners) {
        final GitflowGitCommandResult gitflowGitCommandResult = new GitflowGitCommandResult();

        GitCommandResult gitCommandResult;
        for (GitRepository gitRepository : gitflowGitRepository.gitRepositories()) {
            gitCommandResult = startHotfix(gitRepository, hotfixName, listeners);
            gitflowGitCommandResult.setGitCommandResultForGitRepository(gitRepository, gitCommandResult);
        }

        return gitflowGitCommandResult;
    }

    public GitCommandResult startHotfix(@NotNull GitRepository repository,
                                        @NotNull String hotfixName,
                                        @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitflowCommand());
        h.setSilent(false);

        h.addParameters("hotfix");
        h.addParameters("start");
        h.addParameters(hotfixName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    @Override
    public GitflowGitCommandResult finishHotfix(@NotNull GitflowGitRepository gitflowGitRepository, @NotNull String hotfixName, @NotNull String tagMessage, @Nullable GitLineHandlerListener... listeners) {
        final GitflowGitCommandResult gitflowGitCommandResult = new GitflowGitCommandResult();

        GitCommandResult gitCommandResult;
        for (GitRepository gitRepository : gitflowGitRepository.gitRepositories()) {
            gitCommandResult = finishHotfix(gitRepository, hotfixName, tagMessage, listeners);
            gitflowGitCommandResult.setGitCommandResultForGitRepository(gitRepository, gitCommandResult);
        }

        return gitflowGitCommandResult;
    }

    public GitCommandResult finishHotfix(@NotNull GitRepository repository,
                                         @NotNull String hotfixName,
                                         @NotNull String tagMessage,
                                         @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);
        h.setSilent(false);

        h.addParameters("hotfix");
        h.addParameters("finish");
        if (GitflowConfigurable.pushOnHotfixFinish(repository.getProject())) {
            h.addParameters("-p");
        }
        h.addParameters("-m");
        h.addParameters(tagMessage);
        h.addParameters(hotfixName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    public GitCommandResult publishHotfix(@NotNull GitRepository repository,
                                          @NotNull String hotfixName,
                                          @Nullable GitLineHandlerListener... listeners) {
        final GitLineHandlerPasswordRequestAware h = new GitLineHandlerPasswordRequestAware(repository.getProject(), repository.getRoot(), GitflowCommand());
        setUrl(h, repository);

        h.setSilent(false);

        h.addParameters("hotfix");
        h.addParameters("publish");
        h.addParameters(hotfixName);

        for (GitLineHandlerListener listener : listeners) {
            h.addLineListener(listener);
        }
        return run(h);
    }

    private void setUrl(GitLineHandlerPasswordRequestAware h, GitRepository repository) {
        ArrayList<GitRemote> remotes = new ArrayList(repository.getRemotes());

        //make sure a remote repository is available
        if (!remotes.isEmpty()) {
            h.setUrl(remotes.iterator().next().getFirstUrl());
        }
    }

}
