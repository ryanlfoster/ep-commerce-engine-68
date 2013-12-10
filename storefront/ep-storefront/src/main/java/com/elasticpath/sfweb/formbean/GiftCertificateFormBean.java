/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.formbean;

import java.math.BigDecimal;
import java.util.Map;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Form bean for creating new gift certificates.
 */
public interface GiftCertificateFormBean extends EpFormBean {
	/**
	 * @return the recipientEmail
	 */
	String getRecipientEmail();

	/**
	 * @param recipientEmail the recipientEmail to set
	 */
	void setRecipientEmail(final String recipientEmail);

	/**
	 * @return the confirmEmail
	 */
	String getConfirmEmail();

	/**
	 * @param confirmEmail the confirmEmail to set
	 */
	void setConfirmEmail(final String confirmEmail);
	
	/**
	 * @return the sender's name
	 */
	String getSenderName();

	/**
	 * @param senderName sets the senders name
	 */
	void setSenderName(final String senderName);
	
	/**
	 * @return the purchase amount
	 */
	BigDecimal getPurchaseAmount();

	/**
	 * @param purchaseAmount sets the purchase amount
	 */
	void setPurchaseAmount(final BigDecimal purchaseAmount);
	
	/**
	 * @return the message from sender
	 */
	String getMessage();

	/**
	 * @param message sets the message from sender
	 */
	void setMessage(final String message);
	
	/**
	 * @return the recipient name
	 */
	String getRecipientName();

	/**
	 * @param recipientName sets the recipient name
	 */
	void setRecipientName(final String recipientName);

	/**
	 * Returns a map of the customizable fields for {@code ShoppingItem}.
	 *  
	 * @return The fields
	 */
	Map<String, String> getAsItemFields();

	/**
	 * Initializes all fields from the fields in the {@code ShoppingItem}.
	 * 
	 * @param shoppingItem The item.
	 */
	void initFromShoppingItem(ShoppingItem shoppingItem);
	
	/**
	 * Initializes all fields from the fields in the {@code ShoppingItem}.
	 * 
	 * @param shoppingItem The item.
	 */
	void initFromShoppingItemDto(ShoppingItemDto shoppingItem);


}
