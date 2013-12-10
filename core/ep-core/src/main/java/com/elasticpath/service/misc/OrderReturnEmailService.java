package com.elasticpath.service.misc;

import org.apache.commons.mail.EmailException;

/**
 * 
 * The service interface responsible for sending out Order Return emails.
 */
public interface OrderReturnEmailService {
	
	/**
	 * Send the email for order returns.
	 * @param orderReturnUid - the order return uid
	 * @param emailRecipient - the email address to send the email to. Provide a value for this only
	 * if you want not to use the original customer's email address otherwise use null.
	 * @return whether the email was sent successfully
	 * @throws EmailException on email exception
	 */
	boolean sendOrderReturnEmail(final long orderReturnUid, final String emailRecipient) throws EmailException;

}
