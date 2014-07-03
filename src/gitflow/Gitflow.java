package gitflow;

import git4idea.commands.Git;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitLineHandlerListener;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import gitflow.git.GitflowGitCommandResult;
import gitflow.git.GitflowGitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */
public interface Gitflow extends Git {

    public GitflowGitCommandResult initRepos(@NotNull GitflowGitRepository gitflowGitRepository,
                                             GitflowInitOptions initOptions, @Nullable GitLineHandlerListener... listeners);

    public GitCommandResult initRepo(@NotNull GitRepository repository,
                                     GitflowInitOptions initOptions, @Nullable GitLineHandlerListener... listeners);


    // feature

    GitflowGitCommandResult startFeature(@NotNull GitflowGitRepository repository,
                                         @NotNull String featureName,
                                         @Nullable GitLineHandlerListener... listeners);

    GitCommandResult startFeature(@NotNull GitRepository repository,
                                  @NotNull String featureName,
                                  @Nullable GitLineHandlerListener... listeners);

    GitflowGitCommandResult finishFeature(@NotNull GitflowGitRepository repository,
                                          @NotNull String featureName,
                                          @Nullable GitLineHandlerListener... listeners);

    GitCommandResult finishFeature(@NotNull GitRepository repository,
                                   @NotNull String featureName,
                                   @Nullable GitLineHandlerListener... listeners);

    GitflowGitCommandResult publishFeature(@NotNull GitflowGitRepository repository,
                                           @NotNull String featureName,
                                           @Nullable GitLineHandlerListener... listeners);

    GitCommandResult publishFeature(@NotNull GitRepository repository,
                                    @NotNull String featureName,
                                    @Nullable GitLineHandlerListener... listeners);

    GitCommandResult pullFeature(@NotNull GitRepository repository,
                                 @NotNull String featureName,
                                 @NotNull GitRemote remote,
                                 @Nullable GitLineHandlerListener... listeners);

    GitCommandResult trackFeature(@NotNull GitRepository repository,
                                  @NotNull String featureName,
                                  @NotNull GitRemote remote,
                                  @Nullable GitLineHandlerListener... listeners);

    // release

    GitflowGitCommandResult startRelease(@NotNull GitflowGitRepository repository,
                                         @NotNull String releaseName,
                                         @Nullable GitLineHandlerListener... listeners);

    GitCommandResult startRelease(@NotNull GitRepository repository,
                                  @NotNull String releaseName,
                                  @Nullable GitLineHandlerListener... listeners);


    GitflowGitCommandResult finishRelease(@NotNull GitflowGitRepository repository,
                                          @NotNull String releaseName,
                                          @NotNull String tagMessage,
                                          @Nullable GitLineHandlerListener... listeners);

    GitCommandResult finishRelease(@NotNull GitRepository repository,
                                   @NotNull String releaseName,
                                   @NotNull String tagMessage,
                                   @Nullable GitLineHandlerListener... listeners);


    GitflowGitCommandResult publishRelease(@NotNull GitflowGitRepository repository,
                                           @NotNull String releaseName,
                                           @Nullable GitLineHandlerListener... listeners);

    GitCommandResult publishRelease(@NotNull GitRepository repository,
                                    @NotNull String releaseName,
                                    @Nullable GitLineHandlerListener... listeners);

    GitflowGitCommandResult trackRelease(@NotNull GitflowGitRepository repository,
                                         @NotNull String remoteBranchName,
                                         @Nullable GitLineHandlerListener... listeners);

    GitCommandResult trackRelease(@NotNull GitRepository repository,
                                  @NotNull String releaseName,
                                  @Nullable GitLineHandlerListener... listeners);

    // hotfix

    GitflowGitCommandResult startHotfix(@NotNull GitflowGitRepository repository,
                                        @NotNull String hotfixName,
                                        @Nullable GitLineHandlerListener... listeners);

    GitCommandResult startHotfix(@NotNull GitRepository repository,
                                 @NotNull String hotfixName,
                                 @Nullable GitLineHandlerListener... listeners);

    GitflowGitCommandResult finishHotfix(@NotNull GitflowGitRepository repository,
                                         @NotNull String hotfixName,
                                         @NotNull String tagMessage,
                                         @Nullable GitLineHandlerListener... listeners);

    GitflowGitCommandResult publishHotfix(@NotNull GitflowGitRepository repository,
                                          @NotNull String hotfixName,
                                          @Nullable GitLineHandlerListener... listeners);

    GitCommandResult publishHotfix(@NotNull GitRepository repository,
                                   @NotNull String hotfixName,
                                   @Nullable GitLineHandlerListener... listeners);

    // Merge

    public boolean mergeBranches(@NotNull GitflowGitRepository repository);
}
