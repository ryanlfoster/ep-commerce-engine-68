/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.util.cryptotool;

import java.security.GeneralSecurityException;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.util.security.impl.SimpleEncryption;

/**
 * Encrypt, decrypt, and mask depending on whether certain parameters ave been set.
 */
@SuppressWarnings("PMD.SystemPrintln")
public class SimpleFilter implements StringFilter {

	// Character to use for masking the card number
	private String maskChar = "*";

	// Specified the number of trailing characters to leave unmasked
	private static final int UNMASKED = 4;

	// For decrypting, encrypting, in case we need to change the key
	private SimpleEncryption encryption;

	private SimpleEncryption decryption;

	private boolean doMask = false;

	@Override
	public String applyTo(final String str) {
		String nVal = str;

		if (decryption != null) {
			nVal = decrypt(nVal); // NOPMD
			if (nVal == null) {
				return str;
			}
		}

		if (doMask) {
			nVal = mask(nVal); // NOPMD
			if (nVal == null) {
				return str;
			}
		}

		if (encryption != null) {
			nVal = encrypt(nVal); // NOPMD
			if (nVal == null) {
				return str;
			}
		}

		return nVal;
	}

	/**
	 * SimpleFilter constructor.
	 * 
	 * @param dec decryption object
	 * @param enc encryption object
	 * @param doMask true if the result should be masked
	 */
	public SimpleFilter(final SimpleEncryption dec, final SimpleEncryption enc, final boolean doMask) {
		this.encryption = enc;
		this.decryption = dec;
		this.doMask = doMask;
	}

	/**
	 * Encrypt the string.
	 * 
	 * @param str the string to encrypt
	 * @return the encrypted string
	 */
	private String encrypt(final String str) {
		assert encryption != null;
		try {
			return encryption.encrypt(str);
		} catch (GeneralSecurityException gse) {
			System.err.println(str + " encryption error");
		} catch (IllegalArgumentException ex) {
			System.err.println(str + " invalid ciphertext");
		}
		return null;
	}

	private String decrypt(final String str) {
		assert decryption != null;
		try {
			return decryption.decrypt(str);
		} catch (GeneralSecurityException gse) {
			System.err.println(str + " decryption error");
		} catch (IllegalArgumentException ex) {
			System.err.println(str + " invalid ciphertext");
		}
		return null;
	}

	private String mask(final String cardNumber) {
		if (cardNumber == null) {
			return null;
		}

		String maskNumber = null;

		try {
			int len = cardNumber.length();
			maskNumber = StringUtils.leftPad(cardNumber.substring(len - SimpleFilter.UNMASKED), len, maskChar);
		} catch (IndexOutOfBoundsException ex) {
			System.err.println(cardNumber + " too short to mask");
		}

		return maskNumber;
	}

	/**
	 * @return the maskChar
	 */
	public String getMaskChar() {
		return maskChar;
	}

	/**
	 * @param maskChar the maskChar to set
	 */
	public void setMaskChar(final String maskChar) {
		this.maskChar = maskChar;
	}

	/**
	 * @return the encryption
	 */
	public SimpleEncryption getEncryption() {
		return encryption;
	}

	/**
	 * @param encryption the encryption to set
	 */
	public void setEncryption(final SimpleEncryption encryption) {
		this.encryption = encryption;
	}

	/**
	 * @return the decryption
	 */
	public SimpleEncryption getDecryption() {
		return decryption;
	}

	/**
	 * @param decryption the decryption to set
	 */
	public void setDecryption(final SimpleEncryption decryption) {
		this.decryption = decryption;
	}

	/**
	 * @return the doMask
	 */
	public boolean isDoMask() {
		return doMask;
	}

	/**
	 * @param doMask the doMask to set
	 */
	public void setDoMask(final boolean doMask) {
		this.doMask = doMask;
	}
}