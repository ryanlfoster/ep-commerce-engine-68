package com.elasticpath.service.notification;


/**
 * The notification service acts as a central point into the integration framework.
 * This is used for enterprise integration and development.    
 */
public interface NotificationService {

	/**
	 * Send out a notification of a certain type with the appropriate data.
	 * @param notificationType is the type of notification being sent
	 * @param data is the notification data to send
	 * @return the notification result	 
	 */
	boolean sendNotification(final NotificationType notificationType, final NotificationData data);
}
