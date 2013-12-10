package com.elasticpath.commons.util.email;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

/**
 * Class for sending an email.
 */
public interface EmailSender {
	/**
	 * Send an email message.
	 * 
	 * @param email the email message
	 * @return the message id of the underlying <code>MimeMessage</code>.
	 * @throws EmailException the sending failed
	 */
	String sendEmail(Email email) throws EmailException;
}
