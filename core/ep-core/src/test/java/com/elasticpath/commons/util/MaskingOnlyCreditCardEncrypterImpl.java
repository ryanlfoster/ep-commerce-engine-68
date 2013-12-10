package com.elasticpath.commons.util;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.util.security.CreditCardEncrypter;

/**
 * Implementation of {@link CreditCardEncrypter} that only performs masking. Encrypt and decrypt simply return
 * the passed in value.
*/
public class MaskingOnlyCreditCardEncrypterImpl implements CreditCardEncrypter {

	private static final int LENGTH_OF_UNMASKED_PORTION = 4;

	@Override
	public String decryptAndMask(final String encryptedCardNumber) {
		return mask(encryptedCardNumber);
	}

	@Override
	public String mask(final String cardNumber) {
		String lastFourDigits = StringUtils.right(cardNumber, LENGTH_OF_UNMASKED_PORTION);
		return StringUtils.leftPad(lastFourDigits, cardNumber.length() - LENGTH_OF_UNMASKED_PORTION, '*');
	}

	@Override
	public String encrypt(final String cardNumber) {
		return cardNumber;
	}

	@Override
	public String decrypt(final String encryptedCardNumber) {
		return encryptedCardNumber;
	}
}
