package gitflow.models;

/**
 * Implements an enum to list the error condition when performing a gitflow finish release command.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 14.06.14 - 22:18
 */
public enum FinishReleaseResultCode {
    /**
     * The finish release command was successful. The release branch is removed. All changes from the release branch are
     * merged in the production and development branch.
     */
    SUCCESS,
    /**
     * The finish release command failed for an unknown reason. The state of the repositories is unknown by the plugin.
     */
    FAILED,
    /**
     * The finish release command failed due to merge conflicts. User intervention is required to solve the conflict(s).
     */
    MERGE_CONFLICT;
}
