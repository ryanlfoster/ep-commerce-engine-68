package com.elasticpath.service.notification;

/**
 * Listener on notifications.
 */
public interface NotificationListener {
	/**
	 * @param notificationType {@code NotificationType}
	 * @param data {@code NotificationData}
	 */
	void onNotify(NotificationType notificationType, NotificationData data);
}
