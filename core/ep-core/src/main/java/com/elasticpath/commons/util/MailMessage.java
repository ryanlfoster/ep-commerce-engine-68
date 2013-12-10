/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.util;

import com.elasticpath.domain.EpDomain;

/**
 * This bean holds the information needed by {@link Mailer} to send e-mails.
 *
 */
public interface MailMessage extends EpDomain {

	/**
	 * Gets the mail host.
	 *
	 * @return the mail host
	 */
	String getMailHost();

	/**
	 * Sets the mail host.
	 *
	 * @param mailHost the mail host
	 */
	void setMailHost(String mailHost);

	/**
	 * Gets the mail port.
	 *
	 * @return the mail port
	 */
	String getMailPort();

	/**
	 * Sets the mail port.
	 *
	 * @param mailPort the mail port number
	 */
	void setMailPort(String mailPort);

	/**
	 * Gets the TO address.
	 *
	 * @return the address
	 */
	String getAddressTo();

	/**
	 * Sets the TO address.
	 *
	 * @param addressTo the TO address
	 */
	void setAddressTo(String addressTo);

	/**
	 * Gets the BCC address.
	 *
	 * @return the BCC address
	 */
	String getAddressBCC();

	/**
	 * Sets the BCC address.
	 *
	 * @param addressBCC the BCC address
	 */
	void setAddressBCC(String addressBCC);

	/**
	 * Gets the CC address.
	 *
	 * @return the CC address
	 */
	String getAddressCC();

	/**
	 * Sets the CC address.
	 *
	 * @param addressCC the CC address
	 */
	void setAddressCC(String addressCC);

	/**
	 * Gets the FROM address.
	 *
	 * @return the FROM address
	 */
	String getAddressFrom();

	/**
	 * Sets the FROM address.
	 *
	 * @param addressFrom the FROM address
	 */
	void setAddressFrom(String addressFrom);

	/**
	 * Gets the subject.
	 *
	 * @return the subject
	 */
	String getSubject();

	/**
	 * Sets the subject.
	 *
	 * @param subject the subject
	 */
	void setSubject(String subject);

	/**
	 * Gets the body.
	 *
	 * @return the email body
	 */
	String getBody();

	/**
	 * Sets the email body.
	 *
	 * @param body the email body
	 */
	void setBody(String body);

	/**
	 * Gets the FROM field.
	 *
	 * @return the FROM name
	 */
	String getFromName();

	/**
	 * Sets the FROM field.
	 *
	 * @param fromName the from name
	 */
	void setFromName(String fromName);

	/**
	 * Gets TO name.
	 *
	 * @return the TO name
	 */
	String getToName();

	/**
	 * Sets the TO name.
	 *
	 * @param toName the TO name
	 */
	void setToName(String toName);

	/**
	 * Gets the HTML body.
	 *
	 * @return the HTML body
	 */
	String getBodyHtml();

	/**
	 * Sets the HTML body.
	 *
	 * @param bodyHtml the HTML body
	 */
	void setBodyHtml(String bodyHtml);

	/**
	 * Gets the return path.
	 *
	 * @return the return path
	 */
	String getReturnPath();

	/**
	 * Sets the return path.
	 *
	 * @param returnPath the return path
	 */
	void setReturnPath(String returnPath);

}