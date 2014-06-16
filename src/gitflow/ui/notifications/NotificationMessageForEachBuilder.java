package gitflow.ui.notifications;

/**
 * Specifies an interface which will be called in {@link NotificationMessageBuilder#forEach(Iterable, NotificationMessageForEachBuilder)}
 * calls for each iterated item. Can be used to implement the behaviour for each item.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 15.06.14 - 01:24
 */
public interface NotificationMessageForEachBuilder<T> {

    public void forEachItem(T o, NotificationMessageBuilder notificationMessageBuilder);
}
