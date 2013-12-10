package com.elasticpath.commons.util.security.impl;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

/**
 * Very simple encryption class. Base 64 encoding is applied to all cyphertext after encryption; 
 * plaintext input is assumed to be base 64 encoded as well.
 * 
 * A little bit of history is in order. This class is essentially just jtilson's original 
 * StringEncrypter class. What's changed is that there's no dependency on ElasticpathImpl and
 * the pass-phrase stored in commerce-config.xml.
 * 
 * Our reasons for separating the basic encryption functionality from storage/retrieval of 
 * the pass phrase were threefold:
 * -first we wanted to reuse the encryption side of things in an external app. where 
 *  initializing ElasticPathImpl with a bean factory was going to be difficult
 * -second, when [PM-147] became an issue, it became clear that passwords might come 
 *  from a variety of different sources
 * -third, there probably will exist a need to have multiple encryption objects in the 
 *  system (each for a different purpose--i.e. credit card encryption, digital goods url 
 *  encryption, etc)           
 *   
 * Note that this class is not thread-safe!
 * 
 * See: http://www.msblabs.org/aes-crypt/AES.java for additional details.
 */
public class SimpleEncryption {
	
	private String keyCypher = "AES";
	private String dataCypher = "AES/CBC/PKCS5Padding";
	private String digestAlgo = "MD5";
	
	// initialization vector
	private final byte[] salt = { 
		(byte) 0x83, 	(byte) 0x0f,	(byte) 0x9d,	(byte) 0xa9,	
		(byte) 0xdc,	(byte) 0x03,	(byte) 0x03,	(byte) 0x83,	
		(byte) 0xe0,	(byte) 0xb6,	(byte) 0xf1,	(byte) 0x53,	
		(byte) 0x79,	(byte) 0x59,	(byte) 0x80,	(byte) 0xcb
	};
	
	private final Base64 base64;
	private Cipher ecipher;
	private Cipher dcipher;
	
	/**
	 * Initialize the encryption object for repeated use.
	 * @param passPhrase the pass phrase to use for encryption/decryption
	 */
	public SimpleEncryption(final String passPhrase)  {
		
		base64 = new Base64();
		IvParameterSpec ivSpec = new IvParameterSpec(salt);
		
		try {
			
			SecretKey key = new SecretKeySpec(md5sum(passPhrase.getBytes()), keyCypher);
			ecipher = Cipher.getInstance(dataCypher);
			dcipher = Cipher.getInstance(dataCypher);
			ecipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
	        dcipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
	        
		} catch (Exception ex) {
			throw new IllegalStateException("the cypher failed to intialize due to bad configuration", ex);
		}
	}
	
	/**
	 * Encrypt a plaintext string. 
	 * @param plaintext the plaintext
	 * @return cyphertext the cyphertext
	 * @throws GeneralSecurityException if there is a problem with encryption
	 */
	public String encrypt(final String plaintext) throws GeneralSecurityException {

		String cyphertext = null;
		
		if (StringUtils.isBlank(plaintext)) {
			throw new IllegalArgumentException("plaintext is undefined");
		}

        // encrypt and encode in base 64
        byte[] enc = ecipher.doFinal(plaintext.getBytes());
        cyphertext = new String(base64.encode(enc));

        return cyphertext;
	}
	
	/**
	 * Decrypt a string encrypted by an instance of this class. 
	 * @param cyphertext cyphertext produced by an instance of this class
	 * @return the plaintext the plaintext
	 * @throws GeneralSecurityException if there is a problem with decryption
	 */
	public String decrypt(final String cyphertext) throws GeneralSecurityException {

		String plaintext = null;
		
		if (StringUtils.isBlank(cyphertext)) {
			throw new IllegalArgumentException("cyphertext is undefined");
		}
		
        // decrypt and encode in base 64
		try {
			byte[] dec = dcipher.doFinal(base64.decode(cyphertext.getBytes()));
			plaintext = new String(dec);
		} catch (IllegalStateException ex) {
			assert false;
		}
        
        return plaintext;
	}
	
	private byte[] md5sum(final byte[] buffer) throws NoSuchAlgorithmException {
		final MessageDigest digest = MessageDigest.getInstance(digestAlgo);
		digest.update(buffer);
		return digest.digest();
	}

	/**
	 * @return the algo the key is intended for
	 */
	public String getKeyCypher() {
		return keyCypher;
	}
	/**
	 * @param keyCypher the algo the key is intended for
	 */
	public void setKeyCypher(final String keyCypher) {
		this.keyCypher = keyCypher;
	}
	/**
	 * @return the algo being used for encryption
	 */
	public String getDataCypher() {
		return dataCypher;
	}
	/**
	 * @param dataCypher the algo being used for encryption
	 */
	public void setDataCypher(final String dataCypher) {
		this.dataCypher = dataCypher;
	}
	/**
	 * @return the algorithm used to hash the pass phrase 
	 */
	public String getDigestAlgo() {
		return digestAlgo;
	}
	/**
	 * @param digestAlgo the algorithm used to hash the pass phrase
	 */
	public void setDigestAlgo(final String digestAlgo) {
		this.digestAlgo = digestAlgo;
	}
}