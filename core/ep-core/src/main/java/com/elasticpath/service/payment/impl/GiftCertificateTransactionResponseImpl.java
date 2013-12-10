package com.elasticpath.service.payment.impl;

/**
 * Simple POJO implementation of {@link GiftCertificateTransactionResponse}.
 */
public class GiftCertificateTransactionResponseImpl implements GiftCertificateTransactionResponse {
	private String authorizationCode = null;
	private String giftCertificateCode = null;
	
	@Override
	public String getAuthorizationCode() {
		return authorizationCode;
	}
	
	@Override
	public void setAuthorizationCode(final String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}
	
	@Override
	public String getGiftCertificateCode() {
		return giftCertificateCode;
	}
	
	@Override
	public void setGiftCertificateCode(final String giftCertificateCode) {
		this.giftCertificateCode = giftCertificateCode;
	}
}
