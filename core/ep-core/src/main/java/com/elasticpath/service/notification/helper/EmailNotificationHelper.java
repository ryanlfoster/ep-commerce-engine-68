package com.elasticpath.service.notification.helper;
import java.util.List;

import com.elasticpath.domain.misc.EmailProperties;

/**
 * EmailNotificationHelper interface. 
 * EmailNotificationHelper a notification helper class to encapsulate the dependencies and functionality
 * required to send an email.
 */
public interface EmailNotificationHelper {
	
	/**
	 * Get email properties for a confirmation email.
	 * @param orderNumber is the order number
	 * @return whether the email was sent successfully
	 */
	EmailProperties getOrderEmailProperties(final String orderNumber);
	
	/**
	 * Get email properties for gift certificate email.
	 * @param orderNumber is the order number
	 * @return whether the email was sent successfully
	 */
	List<EmailProperties> getGiftCertificateEmailProperties(String orderNumber);

	/**
	 * Get email properties for order shipped email.
	 * @param orderNumber is the order number
	 * @param shipmentNumber is the shipment number
	 * @return whether the email was sent successfully
	 */
	EmailProperties getShipmentConfirmationEmailProperties(String orderNumber, String shipmentNumber);
		
}