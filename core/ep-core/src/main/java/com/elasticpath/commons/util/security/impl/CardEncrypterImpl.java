/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.util.security.impl;

import com.elasticpath.commons.util.security.CardEncrypter;

/**
 * Extends {@link StringEncrypterImpl} to mask decrypted credit card numbers.
 */
public class CardEncrypterImpl extends StringEncrypterImpl implements CardEncrypter {

	private static final String MASKING_SYMBOL = "*";
	
	/**
	 * The number of symbols in the end of card number that should be visible.
	 */
	private static final int VISIBLE_SYMBOLS_NUMBER = 4;

	/**
	 *
	 * Decrypts and hides the first 12 digits of the credit card number.
	 * @param encryptedCcNumber - the credit card number to decrypt.
	 * @return - the decrypted and masked credit card number: ************5381
	 */
	public String decryptAndMask(final String encryptedCcNumber) {
		final String decryptedCcNumber = super.decrypt(encryptedCcNumber);
		return mask(decryptedCcNumber);
	}

	/**
	 * Replaces all stringToMask except last VISIBLE_SYMBOLS_NUMBER symbols with *. If stringToMask
	 *  has less than VISIBLE_SYMBOLS_NUMBER symbols then returns stringToMask without masking. 
	 * @param stringToMask - the card number to hide.
	 * @return - the masked card number. For example, **************5648.
	 */
	public String mask(final String stringToMask) {
		if (stringToMask == null || stringToMask.length() <= VISIBLE_SYMBOLS_NUMBER) {
			return stringToMask;
		}
		StringBuffer maskedCcNumber = new StringBuffer();
		for (int i = 0; i < stringToMask.length() - VISIBLE_SYMBOLS_NUMBER; i++) {
			maskedCcNumber.append(MASKING_SYMBOL);
		}
		maskedCcNumber.append(stringToMask.substring(stringToMask.length() - VISIBLE_SYMBOLS_NUMBER));
		return maskedCcNumber.toString();
	}
}
