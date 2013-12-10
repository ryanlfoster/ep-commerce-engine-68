/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.formbean.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.sfweb.formbean.GiftCertificateFormBean;

/**
 * Form bean for creating new gift certificates.
 */
public class GiftCertificateFormBeanImpl extends EpFormBeanImpl implements GiftCertificateFormBean {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000002L;

	private String confirmEmail;

	private String recipientEmail;

	private String senderName;

	private BigDecimal purchaseAmount = BigDecimal.ZERO;

	private String message;

	private String recipientName;

	@Override
	public String getConfirmEmail() {
		return confirmEmail;
	}

	@Override
	public void setConfirmEmail(final String confirmEmail) {
		this.confirmEmail = confirmEmail;
	}
	
	@Override
	public String getRecipientEmail() {
		return recipientEmail;
	}

	@Override
	public void setRecipientEmail(final String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}
	
	@Override
	public String getSenderName() {
		return senderName;
	}

	@Override
	public void setSenderName(final String senderName) {
		this.senderName = senderName;
	}
	
	@Override
	public BigDecimal getPurchaseAmount() {
		return purchaseAmount;
	}

	@Override
	public void setPurchaseAmount(final BigDecimal purchaseAmount) {
		this.purchaseAmount = purchaseAmount;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(final String message) {
		this.message = message;
	}

	@Override
	public String getRecipientName() {
		return recipientName;
	}

	@Override
	public void setRecipientName(final String recipientName) {
		this.recipientName = recipientName;	
	}
	
	@Override
	public Map<String, String> getAsItemFields() {
		Map<String, String> returnMap = new HashMap<String, String>();
		if (recipientEmail != null)  { //[BB-1480]since it's a required field for a GC put into the cart, it should be present only if a GC is being
			//purchased. Otherwise it's a regular product and there's no need to copy the GC fields into the SI 
			returnMap.put(GiftCertificate.KEY_RECIPIENT_EMAIL, recipientEmail);
			returnMap.put(GiftCertificate.KEY_RECIPIENT_NAME, recipientName);
			returnMap.put(GiftCertificate.KEY_SENDER_NAME, senderName);
			returnMap.put(GiftCertificate.KEY_MESSAGE, message);
		}
		return returnMap;
	}
	
	@Override
	public void initFromShoppingItem(final ShoppingItem shoppingItem) {
		this.recipientEmail = shoppingItem.getFieldValue(GiftCertificate.KEY_RECIPIENT_EMAIL);
		this.confirmEmail = shoppingItem.getFieldValue(GiftCertificate.KEY_RECIPIENT_EMAIL);
		this.recipientName = shoppingItem.getFieldValue(GiftCertificate.KEY_RECIPIENT_NAME);
		this.senderName = shoppingItem.getFieldValue(GiftCertificate.KEY_SENDER_NAME);
		// TODO uncomment this when the price can be persisted in some fashion.
		//this.purchaseAmount = new BigDecimal(shoppingItem.getFieldValue("giftCertificate.purchaseAmount"));
		this.message = shoppingItem.getFieldValue(GiftCertificate.KEY_MESSAGE);
	}
	
	@Override
	public void initFromShoppingItemDto(final ShoppingItemDto shoppingItem) {
		if (shoppingItem == null) {
			return;
		}
		this.recipientEmail = shoppingItem.getItemFields().get(GiftCertificate.KEY_RECIPIENT_EMAIL);
		this.confirmEmail = shoppingItem.getItemFields().get(GiftCertificate.KEY_RECIPIENT_EMAIL);
		this.recipientName = shoppingItem.getItemFields().get(GiftCertificate.KEY_RECIPIENT_NAME);
		this.senderName = shoppingItem.getItemFields().get(GiftCertificate.KEY_SENDER_NAME);
		this.message = shoppingItem.getItemFields().get(GiftCertificate.KEY_MESSAGE);
	}
}
