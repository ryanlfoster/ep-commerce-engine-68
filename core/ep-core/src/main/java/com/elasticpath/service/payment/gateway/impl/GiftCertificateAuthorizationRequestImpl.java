package com.elasticpath.service.payment.gateway.impl;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.plugin.payment.transaction.impl.AuthorizationTransactionRequestImpl;
import com.elasticpath.service.payment.gateway.GiftCertificateAuthorizationRequest;

/**
 * Implementation of an authorization request for a gift certificate.
 */
public class GiftCertificateAuthorizationRequestImpl extends AuthorizationTransactionRequestImpl implements GiftCertificateAuthorizationRequest {
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
