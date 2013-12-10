/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.util.impl;

import com.elasticpath.commons.util.MailMessage;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;

/**
 * The bean holds the information needed by the Mailer to send emails.
 */
public class MailMessageImpl extends AbstractEpDomainImpl implements MailMessage {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private String mailHost;

	private String mailPort = "25";

	private String addressTo;

	private String addressBCC;

	private String addressCC;

	private String addressFrom;

	private String subject;

	private String body;

	private String fromName;

	private String toName;

	private String bodyHtml;

	private String returnPath;

	/**
	 * Gets the mail host.
	 *
	 * @return the mail host
	 */
	public String getMailHost() {
		return mailHost;
	}

	/**
	 * Sets the mail host.
	 *
	 * @param mailHost the mail host
	 */
	public void setMailHost(final String mailHost) {
		this.mailHost = mailHost;
	}

	/**
	 * Gets the mail port.
	 *
	 * @return the mail port
	 */
	public String getMailPort() {
		return mailPort;
	}

	/**
	 * Sets the mail port.
	 *
	 * @param mailPort the mail port number
	 */
	public void setMailPort(final String mailPort) {
		this.mailPort = mailPort;
	}

	/**
	 * Gets the TO address.
	 *
	 * @return the address
	 */
	public String getAddressTo() {
		return addressTo;
	}

	/**
	 * Sets the TO address.
	 *
	 * @param addressTo the TO address
	 */
	public void setAddressTo(final String addressTo) {
		this.addressTo = addressTo;
	}

	/**
	 * Gets the BCC address.
	 *
	 * @return the BCC address
	 */
	public String getAddressBCC() {
		return addressBCC;
	}

	/**
	 * Sets the BCC address.
	 *
	 * @param addressBCC the BCC address
	 */
	public void setAddressBCC(final String addressBCC) {
		this.addressBCC = addressBCC;
	}

	/**
	 * Gets the CC address.
	 *
	 * @return the CC address
	 */
	public String getAddressCC() {
		return addressCC;
	}

	/**
	 * Sets the CC address.
	 *
	 * @param addressCC the CC address
	 */
	public void setAddressCC(final String addressCC) {
		this.addressCC = addressCC;
	}

	/**
	 * Gets the FROM address.
	 *
	 * @return the FROM address
	 */
	public String getAddressFrom() {
		return addressFrom;
	}

	/**
	 * Sets the FROM address.
	 *
	 * @param addressFrom the FROM address
	 */
	public void setAddressFrom(final String addressFrom) {
		this.addressFrom = addressFrom;
	}

	/**
	 * Gets the subject.
	 *
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the subject.
	 *
	 * @param subject the subject
	 */
	public void setSubject(final String subject) {
		this.subject = subject;
	}

	/**
	 * Gets the body.
	 *
	 * @return the email body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Sets the email body.
	 *
	 * @param body the email body
	 */
	public void setBody(final String body) {
		this.body = body;
	}

	/**
	 * Gets the FROM field.
	 *
	 * @return the FROM name
	 */
	public String getFromName() {
		return fromName;
	}

	/**
	 * Sets the FROM field.
	 *
	 * @param fromName the from name
	 */
	public void setFromName(final String fromName) {
		this.fromName = fromName;
	}

	/**
	 * Gets TO name.
	 *
	 * @return the TO name
	 */
	public String getToName() {
		return toName;
	}

	/**
	 * Sets the TO name.
	 *
	 * @param toName the TO name
	 */
	public void setToName(final String toName) {
		this.toName = toName;
	}

	/**
	 * Gets the HTML body.
	 *
	 * @return the HTML body
	 */
	public String getBodyHtml() {
		return bodyHtml;
	}

	/**
	 * Sets the HTML body.
	 *
	 * @param bodyHtml the HTML body
	 */
	public void setBodyHtml(final String bodyHtml) {
		this.bodyHtml = bodyHtml;
	}

	/**
	 * Gets the return path.
	 *
	 * @return the return path
	 */
	public String getReturnPath() {
		return returnPath;
	}

	/**
	 * Sets the return path.
	 *
	 * @param returnPath the return path
	 */
	public void setReturnPath(final String returnPath) {
		this.returnPath = returnPath;
	}


}
