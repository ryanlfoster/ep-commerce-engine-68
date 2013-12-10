package com.elasticpath.util.cryptotool;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.util.security.impl.SimpleEncryption;

/**
 * Test the SimpleFilter class.
 * @author lstewart
 */
public class SimpleFilterTest {

	private final String[] keys = {"this must be changed in production"};
	private final String[] data = {"1234123412341234", "ask not what", "", null, "75349879573968573452735767%^$%^$^%*^8763"};
	private final String[] masked = {"************1234"};
	private SimpleEncryption enc;
	
	/**
	 * Setup.
	 */
	@Before
	public void setUp() {
		enc = new SimpleEncryption(keys[0]);
	}
	
	/**
	 * @throws Exception if there's a problem with encryption 
	 */
	@Test
	public void testApplyToEncrypt() throws Exception {
		
		// only encrypts
		SimpleFilter filter = new SimpleFilter(null, enc, false);
		assertEquals(filter.applyTo(data[0]), enc.encrypt(data[0]));
	}
	
	/**
	 * @throws Exception if there's a problem with decryption
	 */
	@Test
	public void testApplyToDecrypt() throws Exception {
		
		SimpleFilter filter = new SimpleFilter(enc, null, false);
		
		// only decrypts
		assertEquals(filter.applyTo(enc.encrypt(data[0])), data[0]);
	}

	/**
	 * @throws Exception if there's a problem with decryption/decryption
	 */
	@Test
	public void testApplyToEncryptDecrypt() throws Exception {
		
		SimpleFilter filter = new SimpleFilter(enc, enc, false);
		
		// decrypts, then encrypts
		assertEquals(filter.applyTo(enc.encrypt(data[0])), enc.encrypt(data[0]));
	}
	
	/**
	 * Test the mask part of the filter.
	 */
	@Test
	public void testApplyToMask() {
		SimpleFilter filter = new SimpleFilter(null, null, true);
		
		// only masks
		assertEquals(filter.applyTo(data[0]), masked[0]);
	}
	
//	public void testApplyToReplace() {
//		SimpleFilter filter = new SimpleFilter(enc, enc, null, null, false);
//		
//	}
	
}
