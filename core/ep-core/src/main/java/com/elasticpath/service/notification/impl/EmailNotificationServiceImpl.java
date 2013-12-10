package com.elasticpath.service.notification.impl;

import org.apache.commons.mail.EmailException;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.EmailService;
import com.elasticpath.service.notification.NotificationData;
import com.elasticpath.service.notification.NotificationListener;
import com.elasticpath.service.notification.NotificationType;

/**
 * This is the service to send notifications by email.
 */
public class EmailNotificationServiceImpl implements NotificationListener {
	
	private EmailService emailService;

	/**
	 * Set email Service.
	 * @param emailService instance of emailService
	 */
	public void setEmailService(final EmailService emailService) {
		this.emailService = emailService;
	}

	/**
	 * Send notification by email.	 
	 * @param notificationType is the notification type
	 * @param data is the notification data
	 */
	public void onNotify(final NotificationType notificationType, final NotificationData data) {
		try {
			if (notificationType.equals(NotificationType.NEW_ORDER_NOTIFICATION_TYPE)) {
				String orderNumber = data.getProperty(NotificationData.ORDER_NUMBER);
				emailService.sendOrderConfirmationEmail(orderNumber);
				emailService.sendGiftCertificateEmails(orderNumber);
			} else if (notificationType.equals(NotificationType.ORDER_SHIPPED_NOTIFICATION_TYPE)) {
				Object sendEmailFlag = data.get(NotificationData.SEND_EMAIL);
				if (sendEmailFlag != null && (Boolean) sendEmailFlag) {
					emailService.sendOrderShippedEmail(data);
				}
			}
		} catch (EmailException e) {
			throw new EpServiceException("Exception on send email", e);
		}
	}
}
