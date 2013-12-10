/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.util.security.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.util.security.StringEncrypter;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.settings.SettingsService;

/**
 * Encrypts and Decrypts String objects using an encryption key retrieved from
 * the {@link SettingsService} and 
 * the {@link SimpleEncryption} class as an encryption / decryption strategy.
 * 
 * This implementation requires a minimum encryption key length of 24 characters.
 */
public class StringEncrypterImpl implements StringEncrypter {
	
	private static final Logger LOG = Logger.getLogger(StringEncrypterImpl.class);

	private SimpleEncryption encryption;
	
	private SettingsService settingsService;

    private String encryptionKeyPath;

    /**
     * The minimum length of the encryption key.
     */
    private static final int MINIMUM_KEY_LENGTH = 24;

    /**
     * Error message thrown when encryption key is too short.
     */
    private static final String ERROR_ENCRYPTION_KEY_TOO_SHORT = 
    	"Encryption keys must be greater than " + MINIMUM_KEY_LENGTH + " characters";
	
	/**
	 * Encrypts a given string.
	 *
	 * @param unencryptedString the string to encrypt
	 * @return the encrypted string
	 * @throws EpDomainException if the unencrypted string is null or is a zero-length string.
	 */
	public String encrypt(final String unencryptedString) {

		if (unencryptedString == null || unencryptedString.trim().length() == 0) {
			throw new EpDomainException("param 'unencryptedString' is null");
		}
		if (unencryptedString.trim().length() == 0) {
			throw new EpDomainException("param 'unencryptedString' is zero length. " 
					+ "Can not encrypt zero length strings.");
		}

		init();

		try {
			synchronized (encryption) {	return encryption.encrypt(unencryptedString); }
		} catch (Exception ex) {
			throw new EpDomainException("StringEncrypterImpl.encrypt(byte[] plainText)", ex);
		}
	}

	/**
	 * Decrypts a given string.
	 *
	 * @param encryptedString the string to decrypt
	 * @return the decrypted string
	 * @throws EpDomainException if the encrypted string is null or is a zero-length string.
	 */
	public String decrypt(final String encryptedString) {

		if (encryptedString == null) {
			throw new EpDomainException("param 'encryptedString' is null");
		} else if (encryptedString.trim().length() == 0) {
			throw new EpDomainException("param 'encryptedString' is zero " 
					+ "length. Can not decrypt zero length strings.");
		}

		init();

		try {
			synchronized (encryption) { return encryption.decrypt(encryptedString); }
		} catch (Exception ex) {
			throw new EpDomainException("StringEncrypterImpl.decrypt(byte[] cypherText)", ex);
		}
	}

	/**
	 * Only needs to run once, the first time the application is called an
	 * encrypt/decrypt method. Sets up the shared base64, IvParameterSpc and
	 * SecretKey variables.
	 * @throws EpDomainException if the encryption key cannot be retrieved, or
	 * the encryption key is too short, or there is a problem initializing the 
	 * encryption strategy.
	 */
	private void init() {

		// Synchronize on the class because the cipher and related variables 
		// are static and calls to two different instances could create two
		// cipher objects.
		synchronized (this.getClass()) {
			
			// only ever run once
			if (encryption == null) {
				
				StringBuffer encryptionKey = new StringBuffer(getEncryptionKey());
				
				// NEVER NEVER NEVER USE A String for sensitive data.
				// Use StringBuffers to insure the garbage collector actually
				// frees the data
				if (encryptionKey.length() < MINIMUM_KEY_LENGTH) {
					throw new EpDomainException(ERROR_ENCRYPTION_KEY_TOO_SHORT);
				}

				try {
					// toString(), yah yah, getKeyFromPassword was previously called, and therein
					//  was a call to toString(). the literal-pool has copies of sensitive data all the 
					//  way up the stack (OrderPaymentImpl uses String for credit card data)...
					encryption = new SimpleEncryption(encryptionKey.toString());
				} catch (Exception ex) {
					throw new EpDomainException("Initialization error", ex);
				}
			}
		}		
	}
	
	/**
	 * Retrieves the encryption key from the SettingsService.
	 * @return the encryption key
	 * @throws EpDomainException if the key cannot be found
	 */
	String getEncryptionKey() {
		String encryptionKeyPath = getEncryptionKeyPath();

		try {
			if (StringUtils.isEmpty(encryptionKeyPath)) {
				throw new EpDomainException("Encryption key setting path (encryptionKeyPath) not set.");
			}
		} catch (EpDomainException e) {
			LOG.error("Encryption key setting path (encryptionKeyPath) not set.", e);
			
			throw e;
		}
			
		try {
			return settingsService.getSettingValue(getEncryptionKeyPath()).getValue();
		} catch (EpServiceException ex) { //thrown if the setting value doesn't exist
			throw new EpDomainException("Error retrieving encryption key using setting path: " 
					+ getEncryptionKeyPath(), ex);
		}
	}
	
	/**
	 * @param settingsService the SettingsService that will retrieve the encryption key.
	 */
	public void setSettingsService(final SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	/**
	 * Returns the setting path for the encryption key.
	 *  
	 * @return The setting path string.
	 */
	public String getEncryptionKeyPath() {
		return encryptionKeyPath;
	}

	/**
	 * Set the setting path for the encryption key.
	 * 
	 * @param encryptionKeyPath The setting path string.
	 */
	public void setEncryptionKeyPath(final String encryptionKeyPath) {
		this.encryptionKeyPath = encryptionKeyPath;
	}
}
