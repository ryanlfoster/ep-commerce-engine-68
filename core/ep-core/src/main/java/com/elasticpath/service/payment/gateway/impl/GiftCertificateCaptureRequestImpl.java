package com.elasticpath.service.payment.gateway.impl;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.plugin.payment.transaction.impl.CaptureTransactionRequestImpl;
import com.elasticpath.service.payment.gateway.GiftCertificateCaptureRequest;

/**
 * Gift certificate implementation for a capture request.
 */
public class GiftCertificateCaptureRequestImpl extends CaptureTransactionRequestImpl implements GiftCertificateCaptureRequest {
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
