package gitflow.ui.notifications;

import gitflow.git.RepositoryConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Specifies an interface which can be implemented to create notification message builders for certain use cases. This
 * interface is meant to abstract from the concrete output. In example current implementations can build notification
 * messages as html fragments. Clean text or other output formats are possible.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 15.06.14 - 01:16
 */
public interface NotificationMessageBuilder {
    public NotificationMessageBuilder addMessage(@NotNull String message);

    public NotificationMessageBuilder addMessage(@NotNull String message, @NotNull String... replacement);

    public NotificationMessageBuilder addRepositoryName(@NotNull RepositoryConfig repositoryConfig);

    public NotificationMessageBuilder addHotfixBranchAndRepositoryName(@NotNull RepositoryConfig repositoryConfig, String hotfixName);

    public NotificationMessageBuilder addReleaseBranchAndRepositoryName(@NotNull RepositoryConfig repositoryConfig, String releaseName);

    public NotificationMessageBuilder newLine();

    public NotificationMessageBuilder startUnorderedList();

    public NotificationMessageBuilder endUnorderedList();

    public NotificationMessageBuilder startListItem();

    public NotificationMessageBuilder endListItem();

    public NotificationMessageBuilder forEach(final Iterable<?> iterable, NotificationMessageForEachBuilder notificationMessageForEachBuilder);


}
