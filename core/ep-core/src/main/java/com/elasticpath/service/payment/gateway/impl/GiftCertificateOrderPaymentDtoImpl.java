package com.elasticpath.service.payment.gateway.impl;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.plugin.payment.dto.impl.OrderPaymentDtoImpl;
import com.elasticpath.service.payment.gateway.GiftCertificateOrderPaymentDto;

/**
 * Extension of OrderPaymentDto to support the Gift Certificate payment gateway plugin.
 */
public class GiftCertificateOrderPaymentDtoImpl extends OrderPaymentDtoImpl implements GiftCertificateOrderPaymentDto {
	private GiftCertificate giftCertificate;

	@Override
	public GiftCertificate getGiftCertificate() {
		return giftCertificate;
	}

	@Override
	public void setGiftCertificate(final GiftCertificate giftCertificate) {
		this.giftCertificate = giftCertificate;
	}

}
