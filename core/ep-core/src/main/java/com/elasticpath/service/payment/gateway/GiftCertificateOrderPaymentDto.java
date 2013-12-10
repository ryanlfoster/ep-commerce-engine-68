package com.elasticpath.service.payment.gateway;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;

/**
 * Extension of OrderPaymentDto to support the Gift Certificate payment gateway plugin.
 */
public interface GiftCertificateOrderPaymentDto extends OrderPaymentDto {
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
