package com.elasticpath.plugin.payment.dto;

/**
 * The result of checking if a credit card is enrolled for Payer
 * Authentication (3D Secure).  Required values are included if the card is
 * enrolled.
 */
public interface PayerAuthenticationEnrollmentResultDto {
	/**
	 * Gets the Payer Authentication Request (PaREQ) value, which should be
	 * passed to the issuer's ACS.
	 * @return String PaREQ value.
	 */
	String getPaREQ();

	/**
	 * Sets the Payer Authentication Request (PaREQ) value, which should be
	 * passed to the issuer's ACS.
	 * @param pAREQ value from response.
	 */
	void setPaREQ(final String pAREQ);

	/**
	 * Whether the account is enrolled with the 3D Secure scheme or not.
	 * @return true if it is enrolled, false otherwise.
	 */
	boolean is3DSecureEnrolled();

	/**
	 * Sets the enrolled boolean value.
	 * @param enrolled account enrolled value.
	 */
	void setEnrolled(final boolean enrolled);

	/**
	 * Gets Access Control Server (ACSURL) from payment gateway, used in
	 * redirecting the client browser to the card issuer's 3D Secure host.
	 * @return URL of issuing bank.
	 */
	String getAcsURL();

	/**
	 * Sets Access Control Server URL (ACSURL), used in
	 * redirecting the client browser to the card issuer's 3D Secure host.
	 * @param url the URL for issuing bank.
	 */
	void setAcsURL(final String url);

	/**
	 * Gets MerchantData for Authentication.
	 * @return MerchantData Merchant Data.
	 */
	String getMerchantData();

	/**
	 * Sets MerchantData.
	 * @param merchantData Merchant data.
	 */
	void setMerchantData(final String merchantData);

	/**
	 * Gets TermURL for return.
	 * @return termURL the url for return.
	 */
	String getTermURL();

	/**
	 * Sets the termURL for return.
	 * @param termURL return url.
	 */
	void setTermURL(final String termURL);

}
