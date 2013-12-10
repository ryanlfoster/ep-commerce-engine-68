/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.util.security;


/**
 * Interface for encrypting and decrypting card numbers.
 * Include credit card and gift card.
 *
 */
public interface CardEncrypter extends StringEncrypter, MaskUtility<String> {

	/**
	 * Decrypts and masks a credit card number.
	 * @param encryptedCreditCardNumber - the number to decrypt and mask
	 * @return the decrypted and masked credit card number.
	 */
	String decryptAndMask(final String encryptedCreditCardNumber);
}
