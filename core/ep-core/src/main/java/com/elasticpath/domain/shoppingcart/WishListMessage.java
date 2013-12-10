/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shoppingcart;

import com.elasticpath.domain.EpDomain;



/**
 * This bean represents forgotten password form.
 */
public interface WishListMessage extends EpDomain {

	/**
	 * Set the recipient's email addresses.
	 * @param recipientEmails the recipientEmails to set.
	 */
	void setRecipientEmails(String recipientEmails);

	/**
	 * Return the recipient's email addresses.
	 * @return the recipientEmail.
	 */
	String getRecipientEmails();

	/**
	 * Set the senders's name.
	 * @param senderName the senderName to set.
	 */
	void setSenderName(String senderName);

	/**
	 * Return the senders's name.
	 * @return the senderName.
	 */
	String getSenderName();

	/**
	 * Set the personal message.
	 * @param message the message to set.
	 */
	void setMessage(String message);

	/**
	 * Return the personal message.
	 * @return the message.
	 */
	String getMessage();
}
