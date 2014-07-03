package gitflow.git;

import com.intellij.openapi.module.Module;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import gitflow.fixtures.TestFixture1;
import gitflow.intellij.ProjectAndModules;
import gitflow.test.GitRepositoryStub;
import gitflow.test.ProjectStub;
import gitflow.test.TestUtils;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Implements a test case to test the VCS management of Intellij. The test is required to see how Intellij assigns
 * VCS roots to VCS systems. It's the same as the File %gt;  Settings %gt; Version Control dialog.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 08.06.14 - 14:46
 */
public class GitflowGitRepositoryTest extends JavaCodeInsightFixtureTestCase {

    @Test
    public void testAddGitRepository() throws Exception {
        // Testfix erstellen
        TestUtils.initGitRepo(this.testFixture1.project, this.testFixture1.projectBaseDir);
        TestUtils.initGitRepo(this.testFixture1.project, this.testFixture1.module1ContentRoot);

        final GitRepositoryManager manager = GitUtil.getRepositoryManager(this.testFixture1.project);

        final GitRepository projectGitRepository = manager.getRepositoryForRoot(this.testFixture1.projectBaseDir);
        final GitRepository module1GitRepository_1 = manager.getRepositoryForFile(this.testFixture1.module1ContentRoot);

        final GitRepository module1GitRepository_2 = manager.getRepositoryForFile(this.testFixture1.module1ContentRoot);

        // Test durchführen
        GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(this.testFixture1.projectAndModules);
        gitflowGitRepository.addGitRepository(projectGitRepository);
        gitflowGitRepository.addGitRepository(module1GitRepository_1);
        try {
            gitflowGitRepository.addGitRepository(null);
            fail("Should have caught IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        gitflowGitRepository.addGitRepository(module1GitRepository_2);
        gitflowGitRepository.addGitRepository(projectGitRepository);

        // Test auswerten
        assertThat(gitflowGitRepository.getRepositoryCount(), is(2));
    }

    public void testgetUniqueRemoteBranchNames_test1() throws Exception {
        // Testfixture erstellen
        final ProjectAndModules projectAndModules = new ProjectAndModules(new ProjectStub(), Module.EMPTY_ARRAY);

        final GitRepository gitRepository1 = new GitRepositoryStub();
        final GitRepository gitRepository2 = new GitRepositoryStub();
        final GitRepository gitRepository3 = new GitRepositoryStub();
        final GitRepository gitRepository4 = new GitRepositoryStub();

        final ArrayList<String> branchNames1 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames2 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames3 = new ArrayList<String>(Arrays.asList("branch2", "branch3"));
        final ArrayList<String> branchNames4 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));

        // Test durchführen
        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(projectAndModules) {
            @Override
            protected java.util.Collection<String> getRemoteBranchNames(GitRepository gitRepository) {
                if (gitRepository == gitRepository1) {
                    return branchNames1;
                } else if (gitRepository == gitRepository2) {
                    return branchNames2;
                } else if (gitRepository == gitRepository3) {
                    return branchNames3;
                } else if (gitRepository == gitRepository4) {
                    return branchNames4;
                } else {
                    fail();
                    return null;
                }
            }
        };
        gitflowGitRepository.addGitRepository(gitRepository1);
        gitflowGitRepository.addGitRepository(gitRepository2);
        gitflowGitRepository.addGitRepository(gitRepository3);
        gitflowGitRepository.addGitRepository(gitRepository4);

        final Set<String> uniqueRemoteBranchNames = gitflowGitRepository.getUniqueRemoteBranchNames();

        // Test auswerten
        assertThat(uniqueRemoteBranchNames, hasItem("branch2"));
    }

    public void testgetUniqueRemoteBranchNames_test2() throws Exception {
        // Testfixture erstellen
        final ProjectAndModules projectAndModules = new ProjectAndModules(new ProjectStub(), Module.EMPTY_ARRAY);

        final GitRepository gitRepository1 = new GitRepositoryStub();
        final GitRepository gitRepository2 = new GitRepositoryStub();
        final GitRepository gitRepository3 = new GitRepositoryStub();
        final GitRepository gitRepository4 = new GitRepositoryStub();

        final ArrayList<String> branchNames1 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames2 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames3 = new ArrayList<String>(Arrays.asList("branch4", "branch3"));
        final ArrayList<String> branchNames4 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));

        // Test durchführen
        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(projectAndModules) {
            @Override
            protected java.util.Collection<String> getRemoteBranchNames(GitRepository gitRepository) {
                if (gitRepository == gitRepository1) {
                    return branchNames1;
                } else if (gitRepository == gitRepository2) {
                    return branchNames2;
                } else if (gitRepository == gitRepository3) {
                    return branchNames3;
                } else if (gitRepository == gitRepository4) {
                    return branchNames4;
                } else {
                    fail();
                    return null;
                }
            }
        };
        gitflowGitRepository.addGitRepository(gitRepository1);
        gitflowGitRepository.addGitRepository(gitRepository2);
        gitflowGitRepository.addGitRepository(gitRepository3);
        gitflowGitRepository.addGitRepository(gitRepository4);

        final Set<String> uniqueRemoteBranchNames = gitflowGitRepository.getUniqueRemoteBranchNames();

        // Test auswerten
        assertThat(uniqueRemoteBranchNames, IsEmptyCollection.empty());
    }

    public void testgetUniqueRemoteBranchNames_test3() throws Exception {
        // Testfixture erstellen
        final ProjectAndModules projectAndModules = new ProjectAndModules(new ProjectStub(), Module.EMPTY_ARRAY);

        final GitRepository gitRepository1 = new GitRepositoryStub();
        final GitRepository gitRepository2 = new GitRepositoryStub();
        final GitRepository gitRepository3 = new GitRepositoryStub();
        final GitRepository gitRepository4 = new GitRepositoryStub();

        final ArrayList<String> branchNames1 = new ArrayList<String>(Arrays.asList("branch1", "branch2"));
        final ArrayList<String> branchNames2 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames3 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));
        final ArrayList<String> branchNames4 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));

        // Test durchführen
        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(projectAndModules) {
            @Override
            protected java.util.Collection<String> getRemoteBranchNames(GitRepository gitRepository) {
                if (gitRepository == gitRepository1) {
                    return branchNames1;
                } else if (gitRepository == gitRepository2) {
                    return branchNames2;
                } else if (gitRepository == gitRepository3) {
                    return branchNames3;
                } else if (gitRepository == gitRepository4) {
                    return branchNames4;
                } else {
                    fail();
                    return null;
                }
            }
        };
        gitflowGitRepository.addGitRepository(gitRepository1);
        gitflowGitRepository.addGitRepository(gitRepository2);
        gitflowGitRepository.addGitRepository(gitRepository3);
        gitflowGitRepository.addGitRepository(gitRepository4);

        final Set<String> uniqueRemoteBranchNames = gitflowGitRepository.getUniqueRemoteBranchNames();

        // Test auswerten
        assertThat(uniqueRemoteBranchNames, containsInAnyOrder("branch1", "branch2"));
    }

    public void testgetUniqueRemoteBranchNames_test4() throws Exception {
        // Testfixture erstellen
        final ProjectAndModules projectAndModules = new ProjectAndModules(new ProjectStub(), Module.EMPTY_ARRAY);

        final GitRepository gitRepository1 = new GitRepositoryStub();
        final GitRepository gitRepository2 = new GitRepositoryStub();
        final GitRepository gitRepository3 = new GitRepositoryStub();
        final GitRepository gitRepository4 = new GitRepositoryStub();

        final ArrayList<String> branchNames1 = new ArrayList<String>();
        final ArrayList<String> branchNames2 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames3 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));
        final ArrayList<String> branchNames4 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));

        // Test durchführen
        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(projectAndModules) {
            @Override
            protected java.util.Collection<String> getRemoteBranchNames(GitRepository gitRepository) {
                if (gitRepository == gitRepository1) {
                    return branchNames1;
                } else if (gitRepository == gitRepository2) {
                    return branchNames2;
                } else if (gitRepository == gitRepository3) {
                    return branchNames3;
                } else if (gitRepository == gitRepository4) {
                    return branchNames4;
                } else {
                    fail();
                    return null;
                }
            }
        };
        gitflowGitRepository.addGitRepository(gitRepository1);
        gitflowGitRepository.addGitRepository(gitRepository2);
        gitflowGitRepository.addGitRepository(gitRepository3);
        gitflowGitRepository.addGitRepository(gitRepository4);

        final Set<String> uniqueRemoteBranchNames = gitflowGitRepository.getUniqueRemoteBranchNames();

        // Test auswerten
        assertThat(uniqueRemoteBranchNames, IsEmptyCollection.empty());
    }

    public void testGetUniqueRemoteReleaseBranchNames_test1() throws Exception {
        // Testfix erstellen
        final ProjectAndModules projectAndModules = new ProjectAndModules(new ProjectStub(), Module.EMPTY_ARRAY);

        final GitRepository gitRepository1 = new GitRepositoryStub();
        final GitRepository gitRepository2 = new GitRepositoryStub();
        final GitRepository gitRepository3 = new GitRepositoryStub();
        final GitRepository gitRepository4 = new GitRepositoryStub();

        final ArrayList<String> branchNames1 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames2 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames3 = new ArrayList<String>(Arrays.asList("branch2", "branch3"));
        final ArrayList<String> branchNames4 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));

        // Test durchführen
        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(projectAndModules) {
            @Override
            protected java.util.Collection<String> getRemoteReleaseBranchNames(GitRepository gitRepository) {
                if (gitRepository == gitRepository1) {
                    return branchNames1;
                } else if (gitRepository == gitRepository2) {
                    return branchNames2;
                } else if (gitRepository == gitRepository3) {
                    return branchNames3;
                } else if (gitRepository == gitRepository4) {
                    return branchNames4;
                } else {
                    fail();
                    return null;
                }
            }
        };
        gitflowGitRepository.addGitRepository(gitRepository1);
        gitflowGitRepository.addGitRepository(gitRepository2);
        gitflowGitRepository.addGitRepository(gitRepository3);
        gitflowGitRepository.addGitRepository(gitRepository4);

        final Set<String> uniqueRemoteReleaseBranchNames = gitflowGitRepository.getUniqueRemoteReleaseBranchNames();

        // Test auswerten
        assertThat(uniqueRemoteReleaseBranchNames, hasItem("branch2"));
    }

    public void testGetUniqueRemoteReleaseBranchNames_test2() throws Exception {
        // Testfixture erstellen
        final ProjectAndModules projectAndModules = new ProjectAndModules(new ProjectStub(), Module.EMPTY_ARRAY);

        final GitRepository gitRepository1 = new GitRepositoryStub();
        final GitRepository gitRepository2 = new GitRepositoryStub();
        final GitRepository gitRepository3 = new GitRepositoryStub();
        final GitRepository gitRepository4 = new GitRepositoryStub();

        final ArrayList<String> branchNames1 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames2 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames3 = new ArrayList<String>(Arrays.asList("branch4", "branch3"));
        final ArrayList<String> branchNames4 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));

        // Test durchführen
        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(projectAndModules) {
            @Override
            protected java.util.Collection<String> getRemoteReleaseBranchNames(GitRepository gitRepository) {
                if (gitRepository == gitRepository1) {
                    return branchNames1;
                } else if (gitRepository == gitRepository2) {
                    return branchNames2;
                } else if (gitRepository == gitRepository3) {
                    return branchNames3;
                } else if (gitRepository == gitRepository4) {
                    return branchNames4;
                } else {
                    fail();
                    return null;
                }
            }
        };
        gitflowGitRepository.addGitRepository(gitRepository1);
        gitflowGitRepository.addGitRepository(gitRepository2);
        gitflowGitRepository.addGitRepository(gitRepository3);
        gitflowGitRepository.addGitRepository(gitRepository4);

        final Set<String> uniqueRemoteReleaseBranchNames = gitflowGitRepository.getUniqueRemoteReleaseBranchNames();

        // Test auswerten
        assertThat(uniqueRemoteReleaseBranchNames, IsEmptyCollection.empty());
    }

    public void testGetUniqueRemoteReleaseBranchNames_test3() throws Exception {
        // Testfixture erstellen
        final ProjectAndModules projectAndModules = new ProjectAndModules(new ProjectStub(), Module.EMPTY_ARRAY);

        final GitRepository gitRepository1 = new GitRepositoryStub();
        final GitRepository gitRepository2 = new GitRepositoryStub();
        final GitRepository gitRepository3 = new GitRepositoryStub();
        final GitRepository gitRepository4 = new GitRepositoryStub();

        final ArrayList<String> branchNames1 = new ArrayList<String>(Arrays.asList("branch1", "branch2"));
        final ArrayList<String> branchNames2 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames3 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));
        final ArrayList<String> branchNames4 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));

        // Test durchführen
        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(projectAndModules) {
            @Override
            protected java.util.Collection<String> getRemoteReleaseBranchNames(GitRepository gitRepository) {
                if (gitRepository == gitRepository1) {
                    return branchNames1;
                } else if (gitRepository == gitRepository2) {
                    return branchNames2;
                } else if (gitRepository == gitRepository3) {
                    return branchNames3;
                } else if (gitRepository == gitRepository4) {
                    return branchNames4;
                } else {
                    fail();
                    return null;
                }
            }
        };
        gitflowGitRepository.addGitRepository(gitRepository1);
        gitflowGitRepository.addGitRepository(gitRepository2);
        gitflowGitRepository.addGitRepository(gitRepository3);
        gitflowGitRepository.addGitRepository(gitRepository4);

        final Set<String> uniqueRemoteReleaseBranchNames = gitflowGitRepository.getUniqueRemoteReleaseBranchNames();

        // Test auswerten
        assertThat(uniqueRemoteReleaseBranchNames, containsInAnyOrder("branch1", "branch2"));
    }

    public void testGetUniqueRemoteReleaseBranchNames_test4() throws Exception {
        // Testfixture erstellen
        final ProjectAndModules projectAndModules = new ProjectAndModules(new ProjectStub(), Module.EMPTY_ARRAY);

        final GitRepository gitRepository1 = new GitRepositoryStub();
        final GitRepository gitRepository2 = new GitRepositoryStub();
        final GitRepository gitRepository3 = new GitRepositoryStub();
        final GitRepository gitRepository4 = new GitRepositoryStub();

        final ArrayList<String> branchNames1 = new ArrayList<String>();
        final ArrayList<String> branchNames2 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames3 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));
        final ArrayList<String> branchNames4 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));

        // Test durchführen
        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(projectAndModules) {
            @Override
            protected java.util.Collection<String> getRemoteReleaseBranchNames(GitRepository gitRepository) {
                if (gitRepository == gitRepository1) {
                    return branchNames1;
                } else if (gitRepository == gitRepository2) {
                    return branchNames2;
                } else if (gitRepository == gitRepository3) {
                    return branchNames3;
                } else if (gitRepository == gitRepository4) {
                    return branchNames4;
                } else {
                    fail();
                    return null;
                }
            }
        };
        gitflowGitRepository.addGitRepository(gitRepository1);
        gitflowGitRepository.addGitRepository(gitRepository2);
        gitflowGitRepository.addGitRepository(gitRepository3);
        gitflowGitRepository.addGitRepository(gitRepository4);

        final Set<String> uniqueRemoteReleaseBranchNames = gitflowGitRepository.getUniqueRemoteReleaseBranchNames();

        // Test auswerten
        assertThat(uniqueRemoteReleaseBranchNames, IsEmptyCollection.empty());
    }

    public void testGetUniqueRemoteFeatureBranchNames_test1() throws Exception {
        // Testfix erstellen
        final ProjectAndModules projectAndModules = new ProjectAndModules(new ProjectStub(), Module.EMPTY_ARRAY);

        final GitRepository gitRepository1 = new GitRepositoryStub();
        final GitRepository gitRepository2 = new GitRepositoryStub();
        final GitRepository gitRepository3 = new GitRepositoryStub();
        final GitRepository gitRepository4 = new GitRepositoryStub();

        final ArrayList<String> branchNames1 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames2 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames3 = new ArrayList<String>(Arrays.asList("branch2", "branch3"));
        final ArrayList<String> branchNames4 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));

        // Test durchführen
        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(projectAndModules) {
            @Override
            protected java.util.Collection<String> getRemoteFeatureBranchNames(GitRepository gitRepository) {
                if (gitRepository == gitRepository1) {
                    return branchNames1;
                } else if (gitRepository == gitRepository2) {
                    return branchNames2;
                } else if (gitRepository == gitRepository3) {
                    return branchNames3;
                } else if (gitRepository == gitRepository4) {
                    return branchNames4;
                } else {
                    fail();
                    return null;
                }
            }
        };
        gitflowGitRepository.addGitRepository(gitRepository1);
        gitflowGitRepository.addGitRepository(gitRepository2);
        gitflowGitRepository.addGitRepository(gitRepository3);
        gitflowGitRepository.addGitRepository(gitRepository4);

        final Set<String> uniqueRemoteFeatureBranchNames = gitflowGitRepository.getUniqueRemoteFeatureBranchNames();

        // Test auswerten
        assertThat(uniqueRemoteFeatureBranchNames, hasItem("branch2"));
    }

    public void testGetUniqueRemoteFeatureBranchNames_test2() throws Exception {
        // Testfixture erstellen
        final ProjectAndModules projectAndModules = new ProjectAndModules(new ProjectStub(), Module.EMPTY_ARRAY);

        final GitRepository gitRepository1 = new GitRepositoryStub();
        final GitRepository gitRepository2 = new GitRepositoryStub();
        final GitRepository gitRepository3 = new GitRepositoryStub();
        final GitRepository gitRepository4 = new GitRepositoryStub();

        final ArrayList<String> branchNames1 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames2 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames3 = new ArrayList<String>(Arrays.asList("branch4", "branch3"));
        final ArrayList<String> branchNames4 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));

        // Test durchführen
        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(projectAndModules) {
            @Override
            protected java.util.Collection<String> getRemoteFeatureBranchNames(GitRepository gitRepository) {
                if (gitRepository == gitRepository1) {
                    return branchNames1;
                } else if (gitRepository == gitRepository2) {
                    return branchNames2;
                } else if (gitRepository == gitRepository3) {
                    return branchNames3;
                } else if (gitRepository == gitRepository4) {
                    return branchNames4;
                } else {
                    fail();
                    return null;
                }
            }
        };
        gitflowGitRepository.addGitRepository(gitRepository1);
        gitflowGitRepository.addGitRepository(gitRepository2);
        gitflowGitRepository.addGitRepository(gitRepository3);
        gitflowGitRepository.addGitRepository(gitRepository4);

        final Set<String> uniqueRemoteFeatureBranchNames = gitflowGitRepository.getUniqueRemoteFeatureBranchNames();

        // Test auswerten
        assertThat(uniqueRemoteFeatureBranchNames, IsEmptyCollection.empty());
    }

    public void testGetUniqueRemoteFeatureBranchNames_test3() throws Exception {
        // Testfixture erstellen
        final ProjectAndModules projectAndModules = new ProjectAndModules(new ProjectStub(), Module.EMPTY_ARRAY);

        final GitRepository gitRepository1 = new GitRepositoryStub();
        final GitRepository gitRepository2 = new GitRepositoryStub();
        final GitRepository gitRepository3 = new GitRepositoryStub();
        final GitRepository gitRepository4 = new GitRepositoryStub();

        final ArrayList<String> branchNames1 = new ArrayList<String>(Arrays.asList("branch1", "branch2"));
        final ArrayList<String> branchNames2 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames3 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));
        final ArrayList<String> branchNames4 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));

        // Test durchführen
        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(projectAndModules) {
            @Override
            protected java.util.Collection<String> getRemoteFeatureBranchNames(GitRepository gitRepository) {
                if (gitRepository == gitRepository1) {
                    return branchNames1;
                } else if (gitRepository == gitRepository2) {
                    return branchNames2;
                } else if (gitRepository == gitRepository3) {
                    return branchNames3;
                } else if (gitRepository == gitRepository4) {
                    return branchNames4;
                } else {
                    fail();
                    return null;
                }
            }
        };
        gitflowGitRepository.addGitRepository(gitRepository1);
        gitflowGitRepository.addGitRepository(gitRepository2);
        gitflowGitRepository.addGitRepository(gitRepository3);
        gitflowGitRepository.addGitRepository(gitRepository4);

        final Set<String> uniqueRemoteFeatureBranchNames = gitflowGitRepository.getUniqueRemoteFeatureBranchNames();

        // Test auswerten
        assertThat(uniqueRemoteFeatureBranchNames, containsInAnyOrder("branch1", "branch2"));
    }

    public void testGetUniqueRemoteFeatureBranchNames_test4() throws Exception {
        // Testfixture erstellen
        final ProjectAndModules projectAndModules = new ProjectAndModules(new ProjectStub(), Module.EMPTY_ARRAY);

        final GitRepository gitRepository1 = new GitRepositoryStub();
        final GitRepository gitRepository2 = new GitRepositoryStub();
        final GitRepository gitRepository3 = new GitRepositoryStub();
        final GitRepository gitRepository4 = new GitRepositoryStub();

        final ArrayList<String> branchNames1 = new ArrayList<String>();
        final ArrayList<String> branchNames2 = new ArrayList<String>(Arrays.asList("branch1", "branch2", "branch3"));
        final ArrayList<String> branchNames3 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));
        final ArrayList<String> branchNames4 = new ArrayList<String>(Arrays.asList("branch2", "branch1"));

        // Test durchführen
        final GitflowGitRepository gitflowGitRepository = new GitflowGitRepository(projectAndModules) {
            @Override
            protected java.util.Collection<String> getRemoteFeatureBranchNames(GitRepository gitRepository) {
                if (gitRepository == gitRepository1) {
                    return branchNames1;
                } else if (gitRepository == gitRepository2) {
                    return branchNames2;
                } else if (gitRepository == gitRepository3) {
                    return branchNames3;
                } else if (gitRepository == gitRepository4) {
                    return branchNames4;
                } else {
                    fail();
                    return null;
                }
            }
        };
        gitflowGitRepository.addGitRepository(gitRepository1);
        gitflowGitRepository.addGitRepository(gitRepository2);
        gitflowGitRepository.addGitRepository(gitRepository3);
        gitflowGitRepository.addGitRepository(gitRepository4);

        final Set<String> uniqueRemoteFeatureBranchNames = gitflowGitRepository.getUniqueRemoteFeatureBranchNames();

        // Test auswerten
        assertThat(uniqueRemoteFeatureBranchNames, IsEmptyCollection.empty());
    }

    private TestFixture1 testFixture1;

    @Override
    public void setUp() throws Exception {
        this.testFixture1 = new TestFixture1(this);
        this.testFixture1.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        this.testFixture1.tearDown();
    }
}