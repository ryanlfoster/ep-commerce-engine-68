/* :::: BEGIN COPYRIGHT ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 * THIS IS COPYRIGHT SOFTWARE
 * Removal of this section is a violation of license and terms of use.
 * Source code Copyright 1999-2004 Ekkon Business Group Ltd. (http://www.ekkon.com)
 * All Rights Reserved.
 * MerchantSpace and MerchantSpace Commerce are trademarks of Ekkon Business Group Ltd.
 * More information can be obtained at http://www.merchantspace.com
 * :::: END COPYRIGHT ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 */
package com.elasticpath.commons.util.security.impl;

import com.elasticpath.commons.util.security.CreditCardEncrypter;

/**
 * Encrypter / Decrypter for credit card numbers. This implementation decorates a CardEncrypter
 * to provide an additional method that partially masks a given String with asterisks.
 */
public class CreditCardEncrypterImpl extends CardEncrypterImpl implements CreditCardEncrypter {
	
	private static final int START_OF_LAST_FOUR_DIGITS_AMERICAN_EXPRESS = 11;

	private static final String CREDIT_CARD_MASK_AMERICAN_EXPRESS = "***********";

	private static final int NUMBER_OF_DIGITS_IN_AMERICAN_EXPRESS = 15;

	/**
	 * Replaces the first 11 or 12 digits of a credit card with *.
	 * @param stringToMask - the credit card number to hide.
	 * @return - the masked credit card number: ************5381
	 */
	public String mask(final String stringToMask) {
		if (stringToMask.length() == NUMBER_OF_DIGITS_IN_AMERICAN_EXPRESS) {
			StringBuffer maskedCcNumber = new StringBuffer(CREDIT_CARD_MASK_AMERICAN_EXPRESS);
			maskedCcNumber.append(stringToMask.substring(START_OF_LAST_FOUR_DIGITS_AMERICAN_EXPRESS));
			return maskedCcNumber.toString();
		}
//		default 16 digit credit card
		return super.mask(stringToMask);
	}
}
