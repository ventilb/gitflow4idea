package gitflow.models;

/**
 * Simple model to hold a tag message and the information that a user cancelled a release or not.
 * <p>
 * This class is required for cleaner API design.
 * </p>
 *
 * @author <a href="mailto:mschulze@geneon.de">Manuel Schulze</a>
 * @since 12.06.14 - 00:27
 */
public class ReleaseTagMessageOrCancelModel {

    private String tagMessage;

    private boolean cancel;

    public ReleaseTagMessageOrCancelModel() {
    }

    public ReleaseTagMessageOrCancelModel(String tagMessage, boolean cancel) {
        this.tagMessage = tagMessage;
        this.cancel = cancel;
    }

    public String getTagMessage() {
        return tagMessage;
    }

    public void setTagMessage(String tagMessage) {
        this.tagMessage = tagMessage;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }
}
