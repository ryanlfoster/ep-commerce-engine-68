/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.payment;

import java.math.BigDecimal;
import java.util.Date;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.persistence.api.Persistable;

/**
 * <code>GiftCertificateTransaction</code> gift certificate transaction.
 */
public interface GiftCertificateTransaction extends Persistable {
	/**
	 * Get the authorization code.
	 * 
	 * @return the authorization code
	 */
	String getAuthorizationCode();

	/**
	 * Set the authorization code.
	 * 
	 * @param authorizationCode the authorization code
	 */
	void setAuthorizationCode(final String authorizationCode);

	/**
	 * Get the payment transaction type, i.e. "Authorization", "Sale" or "Credit".
	 * 
	 * @return the payment transaction type
	 */
	String getTransactionType();

	/**
	 * Get the payment transaction type, i.e. "Authorization", "Sale" or "Credit".
	 * 
	 * @param transactionType the payment transaction type
	 */
	void setTransactionType(final String transactionType);

	/**
	 * Get the amount of this payment.
	 * 
	 * @return the amount
	 */
	BigDecimal getAmount();

	/**
	 * Set the amount of this payment.
	 * 
	 * @param amount the amount
	 */
	void setAmount(final BigDecimal amount);

	/**
	 * Get the gift certificate for the payment.
	 * 
	 * @return the giftCertificate
	 */
	GiftCertificate getGiftCertificate();

	/**
	 * Set the gift certificate for the payment.
	 * 
	 * @param giftCertificate the giftCertificate to set
	 */
	void setGiftCertificate(final GiftCertificate giftCertificate);

	/**
	 * Get the date that this order was created on.
	 * 
	 * @return the created date
	 */
	Date getCreatedDate();

	/**
	 * Set the date that the order is created.
	 * 
	 * @param createdDate the start date
	 */
	void setCreatedDate(final Date createdDate);
}
