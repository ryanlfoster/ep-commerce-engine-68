package com.elasticpath.service.notification.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.service.notification.NotificationData;
import com.elasticpath.service.notification.NotificationListener;
import com.elasticpath.service.notification.NotificationService;
import com.elasticpath.service.notification.NotificationType;

/**
 * This is the stub class which don't do anything at this moment.
 *
 */
public class NotificationServiceImpl implements NotificationService {
	
	private static final Logger LOG = Logger.getLogger(NotificationServiceImpl.class);

	private List <NotificationListener> listeners = new ArrayList <NotificationListener>();
	
	@Override
	public boolean sendNotification(final NotificationType notificationType,
			final NotificationData data) {
		boolean aggregateSuccess = true;
		for (NotificationListener listener : listeners) {
			try {
				listener.onNotify(notificationType, data);
			} catch (Exception e) {
				aggregateSuccess = false;
				LOG.error("Exception caught in notification listener", e);
			}
		}
		return aggregateSuccess;
	}

	/**
	 * @param listeners .
	 */
	public void setNotificationListeners(final List <NotificationListener> listeners) {
		this.listeners = listeners;
	}
}
