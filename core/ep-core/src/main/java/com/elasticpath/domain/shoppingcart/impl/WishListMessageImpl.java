/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shoppingcart.impl;

import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.shoppingcart.WishListMessage;

/**
 * The default implementation of <code>ForgottenPasswordFormBean</code>.
 */
public class WishListMessageImpl extends AbstractEpDomainImpl implements WishListMessage {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private String recipientEmails;
	private String senderName;
	private String message;

	/**
	 * Set the recipient's email addresses.
	 * @param recipientEmails the recipientEmails to set.
	 */
	public void setRecipientEmails(final String recipientEmails) {
		this.recipientEmails = recipientEmails;
	}

	/**
	 * Return the recipient's email addresses.
	 * @return the recipientEmail.
	 */
	public String getRecipientEmails() {
		return recipientEmails;
	}

	/**
	 * Set the senders's name.
	 * @param senderName the senderName to set.
	 */
	public void setSenderName(final String senderName) {
		this.senderName = senderName;
	}

	/**
	 * Return the senders's name.
	 * @return the senderName.
	 */
	public String getSenderName() {
		return senderName;
	}

	/**
	 * Set the personal message.
	 * @param message the message to set.
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

	/**
	 * Return the personal message.
	 * @return the message.
	 */
	public String getMessage() {
		return message;
	}

}
