package com.elasticpath.service.security.impl;

import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;

import com.elasticpath.service.security.SaltFactory;

/**
 * A factory for creating Salt strings. This will create a secure random byte array of the 
 * specified number of bytes, and then a string hex representation of that array.
 */
public class HexStringSaltFactoryImpl implements SaltFactory<String> {

	private int numberOfBytes;
	
	@Override
	public String createSalt() {
	    SecureRandom random = new SecureRandom();
	    byte[] bytes = new byte[getNumberOfBytes()];
	    random.nextBytes(bytes);
	    return Hex.encodeHexString(bytes);
	}

	protected int getNumberOfBytes() {
		return numberOfBytes;
	}

	public void setNumberOfBytes(final int numberOfBytes) {
		this.numberOfBytes = numberOfBytes;
	}

}
