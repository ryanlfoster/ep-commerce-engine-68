/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalog.impl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.elasticpath.domain.catalog.GiftCertificate;

/**
 * Test cases for GiftCertificateImpl.
 */
public class GiftCertificateImplTest {
	
	/**
	 * Test specifically for equality between transient and persistent instances.
	 */
	@SuppressWarnings("PMD.PositionLiteralsFirstInComparisons")
	@Test
	public void testHashcodeAndEquals() {
		
		// Test with two transient instances.
		GiftCertificate cert1 = new GiftCertificateImpl();
		GiftCertificate cert2 = new GiftCertificateImpl();

		assertFalse("Shouldn't equal null", cert1.equals(null)); // NOPMD - we are specifically checking the equals method
		assertFalse("Shouldn't equal a string", cert1.equals("certificate"));
		assertFalse("Shouldn't equal a random object", cert1.equals(new Object()));
		assertEquals("Two transient objects should be equals", cert1, cert2);
		assertEquals("The cert should always be equal to itself", cert1, cert1);
		assertEquals("Consecutive hashCode calls should be consistent", cert1.hashCode(), cert1.hashCode());
		
		// Create and test with a persistent instance		
		GiftCertificate cert3 = new GiftCertificateImpl();
		cert3.setUidPk(1);
		
		// Create and tests with a duplicate persistent instance.
		GiftCertificate cert4 = new GiftCertificateImpl();
		cert4.setUidPk(1);
		
		assertEquals("persistent instance should equal a duplicate instance", cert4, cert3);
		assertEquals("persistent instance should equal a duplicate instance (associative)", cert3, cert4);
		assertEquals("hashCodes of equal persistent objects are the same", cert3.hashCode(), cert4.hashCode());
		
		
		// Now test with a new unique persistent instance
		GiftCertificate cert5 = new GiftCertificateImpl();
		cert5.setUidPk(2);
		assertEquals("should equal itself", cert5, cert5);
		
		assertFalse("two persistent instance are not equal if their uidpks are not equal", cert5.equals(cert4));
	}
	
	/**
	 * Tests displayGiftCertificate() in case no code has been set and
	 * if the code has been set.
	 */
	@Test
	public void testDisplayGiftCertificate() {
		GiftCertificate giftc = new GiftCertificateImpl();
		
		try {
			giftc.displayGiftCertificateCode();
			fail("Should throw exception in case no GC code has been set");
		} catch (Exception e) {
			assertNotNull(e);
		}
		
		final String gcCode = "code1";
		giftc.setGiftCertificateCode(gcCode);
		assertEquals(gcCode, giftc.displayGiftCertificateCode());
	}
}
