/**
 * 
 */
package com.elasticpath.commons.util;

import java.security.SecureRandom;

/**
 * Generates random 16-digit alphanumeric codes for use as Gift Certificate codes.
 */
public class GiftCertificateCodeGeneratorImpl implements GiftCertificateCodeGenerator {

	private static final int GIFT_CERTIFICATE_CODE_LENGTH = 16;
	
	private static final int ASCII_CHARACTERS = 35;
	
	private static final int DIGITS = 9;

	private static final int ASCII_DIGITS = 48;

	private static final int ASCII_UPPERCASE = 55;
	
	/**
	 * Generates a random 16-digit alphanumeric code to use for gift certificates.
	 * This implementation uses the {@link SecureRandom} generator by SUN, and prepends it
	 * with the letters "GC".
	 * @return the generated code
	 */
	public String generateCode() {
		// Use the SHA1PRNG pseudo-random number generation algorithm supplied by SUN
		SecureRandom randomGenerator = new SecureRandom();

		// Create an array of characters and populate each spot with a random character
		char[]  alphanumericCharArray = new char[GIFT_CERTIFICATE_CODE_LENGTH];
		for (int i = 0; i < GIFT_CERTIFICATE_CODE_LENGTH; i++) {
			alphanumericCharArray[i] = randomAlphanumericChar(randomGenerator);
		}

		return "GC" + new String(alphanumericCharArray);
	}
	
	/**
	 * Pseudo-randomly generate an alphanumeric character.
	 *
	 * @param randomGenerator
	 * @return ascii character
	 */
	private char randomAlphanumericChar(final java.util.Random randomGenerator) {
		// Generate a random number from 0 to 35
		int randomNumber = randomGenerator.nextInt(ASCII_CHARACTERS);

		// If the number is from 0-9, add 48 to convert it to its corresponding
		// ascii number. Otherwise, add 55 to convert it to its corresponding
		// ascii upper case letter.
		if (randomNumber <= DIGITS) {
			return (char) (randomNumber + ASCII_DIGITS);
		}
		return (char) (randomNumber + ASCII_UPPERCASE);
	}
}
