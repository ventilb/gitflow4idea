package gitflow;

import com.intellij.openapi.util.Key;
import gitflow.actions.GitflowLineHandler;

/**
 * Implements a {@link gitflow.actions.GitflowLineHandler} to deal with gitflow responses when the user wants to
 * enable a git repository to gitflow.
 *
 * @author <a href="mailto:mschulze@geneon.de">Manuel Schulze</a>
 * @since 11.06.14 - 21:41
 */
public class GitflowInitRepoLineHandler extends GitflowLineHandler {
    @Override
    public void onLineAvailable(String line, Key outputType) {
        if (line.contains("Already initialized for gitflow")) {
            this.myErrors.add("Repo already initialized for gitflow");
        }

    }
}
