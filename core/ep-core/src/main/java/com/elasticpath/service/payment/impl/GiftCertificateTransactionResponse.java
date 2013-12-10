package com.elasticpath.service.payment.impl;

/**
 * Response returned from the {@link GiftCertificateTransactionService} upon completion of a transaction.
 */
public interface GiftCertificateTransactionResponse {
	/**
	 * Gets the authorization code.
	 *
	 * @return the authorization code
	 */
	String getAuthorizationCode();

	/**
	 * Sets the authorization code.
	 *
	 * @param authorizationCode the new authorization code
	 */
	void setAuthorizationCode(String authorizationCode);

	/**
	 * Gets the gift certificate code.
	 *
	 * @return the gift certificate code
	 */
	String getGiftCertificateCode();

	/**
	 * Sets the gift certificate code.
	 *
	 * @param giftCertificateCode the new gift certificate code
	 */
	void setGiftCertificateCode(String giftCertificateCode);
}