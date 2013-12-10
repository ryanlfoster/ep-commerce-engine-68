package com.elasticpath.service.payment.gateway;

import com.elasticpath.domain.catalog.GiftCertificate;

/**
 * Represents a base gift certificate transaction request used for requests against the GiftCertificate payment gateway.
 */
public interface GiftCertificateTransactionRequest {
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
}
