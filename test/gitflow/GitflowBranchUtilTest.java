package gitflow;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Implements a test cases to test the {@link gitflow.GitflowBranchUtil} class.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 30.06.14 - 21:58
 */
public class GitflowBranchUtilTest {

    @Test
    public void testRemovePrefixFromBranchNames() throws Exception {
        // Testfix erstellen
        final Collection<String> inputBranches = Arrays.asList("release-today", "hotfix-urgent", "release-yesterday", "migrated");
        final Collection<String> inputBranchesEmpty = new LinkedList<String>();

        // Test durchführen
        final Collection<String> outputBranchesByReleasePrefix = GitflowBranchUtil.removePrefixFromBranchNames(inputBranches, "release-");
        final Collection<String> outputBranchesByHotfixPrefix = GitflowBranchUtil.removePrefixFromBranchNames(inputBranches, "hotfix-");
        final Collection<String> outputBranchesWithoutEffect = GitflowBranchUtil.removePrefixFromBranchNames(inputBranches, "");
        final Collection<String> outputBranchesEmpty = GitflowBranchUtil.removePrefixFromBranchNames(inputBranchesEmpty, "hotfix-");

        // Test auswerten
        assertThat(outputBranchesByHotfixPrefix, is(not(sameInstance(inputBranches))));
        assertThat(outputBranchesEmpty, is(not(sameInstance(inputBranchesEmpty))));

        assertThat(outputBranchesByReleasePrefix, hasSize(2));
        assertThat(outputBranchesByReleasePrefix, containsInAnyOrder("today", "yesterday"));

        assertThat(outputBranchesByHotfixPrefix, hasSize(1));
        assertThat(outputBranchesByHotfixPrefix, containsInAnyOrder("urgent"));

        assertThat(outputBranchesWithoutEffect, hasSize(4));
        assertThat(outputBranchesWithoutEffect, containsInAnyOrder("release-today", "hotfix-urgent", "release-yesterday", "migrated"));

        assertThat(outputBranchesEmpty, hasSize(0));
    }

}