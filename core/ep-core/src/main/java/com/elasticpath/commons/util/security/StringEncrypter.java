/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.util.security;


/**
 * A StringEncrypter encrypts and decrypts Strings.
 */
public interface StringEncrypter {

	/**
	 * Encrypts a given string.
	 * @param unencryptedString - the string to encrypt
	 * @return the encrypted string
	 */
	String encrypt(final String unencryptedString);

	/**
	 * Decrypts a given string.
	 * @param encryptedString - the string to decrypt
	 * @return the decrypted string
	 */
	String decrypt(final String encryptedString);
}