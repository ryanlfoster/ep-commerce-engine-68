package com.elasticpath.commons.util.email;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import com.elasticpath.domain.misc.EmailProperties;

/**
 * Class for composing an email message.
 */
public interface EmailComposer {

	/**
	 * Composes an email using the provided email properties.
	 * 
	 * @param emailProperties the properties used to create the email
	 * @return an Email message.
	 * @throws EmailException an email exception
	 */
	Email composeMessage(final EmailProperties emailProperties) throws EmailException;

}
