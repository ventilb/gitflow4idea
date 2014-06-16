package gitflow.ui.notifications;

import gitflow.git.RepositoryConfig;
import gitflow.ui.PrettyFormat;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Implements a {@link gitflow.ui.notifications.NotificationMessageBuilder} to build messages in the html format. The
 * messages created are html fragments not complete valid html pages.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 15.06.14 - 00:06
 */
public class HtmlFragmentNotificationBuilder implements NotificationMessageBuilder {

    private final StringBuilder notificationBuffer = new StringBuilder();

    @Override
    public HtmlFragmentNotificationBuilder addMessage(@NotNull final String message) {
        this.notificationBuffer.append(message);
        return this;
    }

    @Override
    public HtmlFragmentNotificationBuilder addMessage(@NotNull final String message, @NotNull final String... replacement) {
        this.notificationBuffer.append(String.format(message, replacement));
        return this;
    }

    @Override
    public HtmlFragmentNotificationBuilder addRepositoryName(@NotNull final RepositoryConfig repositoryConfig) {
        this.notificationBuffer.append(PrettyFormat.repositoryName(repositoryConfig));
        return this;
    }

    @Override
    public HtmlFragmentNotificationBuilder addHotfixBranchAndRepositoryName(@NotNull final RepositoryConfig repositoryConfig, final String hotfixName) {
        this.notificationBuffer.append(PrettyFormat.hotfixBranchAndRepositoryName(repositoryConfig, hotfixName));
        return this;
    }

    @Override
    public HtmlFragmentNotificationBuilder addReleaseBranchAndRepositoryName(@NotNull final RepositoryConfig repositoryConfig, final String releaseName) {
        this.notificationBuffer.append(PrettyFormat.releaseBranchAndRepositoryName(repositoryConfig, releaseName));
        return this;
    }

    @Override
    public NotificationMessageBuilder newLine() {
        this.notificationBuffer.append("<br/>");
        return this;
    }

    public NotificationMessageBuilder startUnorderedList() {
        this.notificationBuffer.append("<ul>");
        return this;
    }

    public NotificationMessageBuilder endUnorderedList() {
        this.notificationBuffer.append("</ul>");
        return this;
    }

    public NotificationMessageBuilder startListItem() {
        this.notificationBuffer.append("<li>");
        return this;
    }

    public NotificationMessageBuilder endListItem() {
        this.notificationBuffer.append("</li>");
        return this;
    }

    @Override
    public NotificationMessageBuilder forEach(Iterable<?> iterable, NotificationMessageForEachBuilder notificationMessageForEachBuilder) {
        for (Object o : iterable) {
            notificationMessageForEachBuilder.forEachItem(o, this);
        }
        return this;
    }

    @Override
    public String toString() {
        return this.notificationBuffer.toString();
    }
}
