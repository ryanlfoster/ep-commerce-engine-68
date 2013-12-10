/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.util;

import com.elasticpath.base.exception.EpSystemException;

/**
 * Provides the ability to send emails to a given email host.
 */
public interface Mailer {

	/**
	 * Send a text email.
	 * 
	 * @param contentEncoding the content encoding for the text email.
	 * @param mailMessage the mailer bean holding the email related information
	 * @throws EpSystemException on failure
	 */
	void sendEmail(String contentEncoding, MailMessage mailMessage) throws EpSystemException;

	/**
	 * Send an html email.
	 * 
	 * @param mailMessage the mailer bean holding the email related information
	 * @throws EpSystemException on failure
	 */
	void sendHtmlEmail(MailMessage mailMessage) throws EpSystemException;

}