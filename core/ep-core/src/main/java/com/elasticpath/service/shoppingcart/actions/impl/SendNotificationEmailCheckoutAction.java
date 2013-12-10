package com.elasticpath.service.shoppingcart.actions.impl;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.order.Order;
import com.elasticpath.service.notification.NotificationData;
import com.elasticpath.service.notification.NotificationService;
import com.elasticpath.service.notification.NotificationType;
import com.elasticpath.service.shoppingcart.actions.FinalizeCheckoutAction;
import com.elasticpath.service.shoppingcart.actions.FinalizeCheckoutActionContext;

/**
 * CheckoutAction to send confirmation email to the customer.
 */
public class SendNotificationEmailCheckoutAction implements FinalizeCheckoutAction {

	private static final Logger LOG = Logger.getLogger(SendNotificationEmailCheckoutAction.class);

	private NotificationService notificationService;

	@Override
	public void execute(final FinalizeCheckoutActionContext context) throws EpSystemException {
		final Order order = context.getOrder();

		// Send notification via messaging system
		boolean orderConfirmationSentSuccessfully = false;
		try {
			final NotificationData data = new NotificationData(order.getOrderNumber(), null, false);

			orderConfirmationSentSuccessfully = notificationService.sendNotification(NotificationType.NEW_ORDER_NOTIFICATION_TYPE,
					data);
		} catch (final Exception e) {
			LOG.error("Can't send the order notification " + order.getOrderNumber(), e);
		}

		context.setEmailFailed(!orderConfirmationSentSuccessfully);
	}

	/**
	 * Gets the notifications service.
	 * @return the notifications service.
	 * */
	protected NotificationService getNotificationService() {
		return notificationService;
	}

	/**
	 * Sets the notifications service.
	 * @param notificationService the notifications service.
	 * */
	public void setNotificationService(final NotificationService notificationService) {
		this.notificationService = notificationService;
	}

}