package gitflow.models;

/**
 * Implements an enum to list the error condition when performing a gitflow finish feature command.
 *
 * @author <a href="mailto:mschulze@geneon.de">Manuel Schulze</a>
 * @since 20.06.14 - 23:12
 */
public enum FinishFeatureResultCode {
    /**
     * The finish feature command was successful. The feature branch is removed. All changes from the feature branch are
     * merged into the development branch.
     */
    SUCCESS,
    /**
     * The finish feature command failed for an unknown reason. The state of the repositories is unknown by the plugin.
     */
    FAILED,
    /**
     * The finish feature command failed due to merge conflicts. User intervention is required to solve the conflict(s).
     */
    MERGE_CONFLICT;
}
