package com.elasticpath.domain.misc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of the Object on Domain/Misc.
 */
public class PayerAuthenticationEnrollmentResultImplTest {

	private PayerAuthenticationEnrollmentResultImpl payerAuthenticationEnrollmentResultImpl;

	@Before
	public void setUp() throws Exception {
		payerAuthenticationEnrollmentResultImpl = new PayerAuthenticationEnrollmentResultImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthenticationEnrollmentResultImpl.getRaREQ()'.
	 */
	@Test
	public void testGetPaREQ() {
		assertEquals("Check get PaREQ", payerAuthenticationEnrollmentResultImpl.getPaREQ(), null);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthenticationEnrollmentResultImpl.setRaREQ()'.
	 */
	@Test
	public void testSetPaREQ() {
		final String pareq = "test";
		payerAuthenticationEnrollmentResultImpl.setPaREQ(pareq);
		assertEquals("Check set PaEQ", payerAuthenticationEnrollmentResultImpl.getPaREQ(), pareq);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthenticationEnrollmentResultImpl.getAcsURL()'.
	 */
	@Test
	public void testGetAcsURL() {
		assertEquals("Check get AcsURL", payerAuthenticationEnrollmentResultImpl.getAcsURL(), null);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthenticationEnrollmentResultImpl.setAcsURL()'.
	 */
	@Test
	public void testSetAcsURL() {
		final String acsURL = "www.citibank.com";
		payerAuthenticationEnrollmentResultImpl.setAcsURL(acsURL);
		assertEquals("Check set AcsURL", payerAuthenticationEnrollmentResultImpl.getAcsURL(), acsURL);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthenticationEnrollmentResultImpl.is3DSecureEnrolled()'.
	 */
	@Test
	public void testIs3DSecureEnrolled() {
		assertFalse("Check is 3D enrolled?", payerAuthenticationEnrollmentResultImpl.is3DSecureEnrolled());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthenticationEnrollmentResultImpl.setEnrolled()'.
	 */
	@Test
	public void testSetEnrolled() {
		payerAuthenticationEnrollmentResultImpl.setEnrolled(true);
		assertTrue("Check set enrolled", payerAuthenticationEnrollmentResultImpl.is3DSecureEnrolled());
	}
		
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthenticationEnrollmentResultImpl.getMerchantData()'.
	 */
	@Test
	public void testGetMerchantData() {
		assertEquals("Check get merchantData", payerAuthenticationEnrollmentResultImpl.getMerchantData(), null);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthenticationEnrollmentResultImpl.setMerchantData()'.
	 */
	@Test
	public void testSetMerchantData() {
		final String merchantData = "123456789";
		payerAuthenticationEnrollmentResultImpl.setMerchantData(merchantData);
		assertSame("Check set merchantData", payerAuthenticationEnrollmentResultImpl.getMerchantData(), merchantData);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthenticationEnrollmentResultImpl.getTermURL()'.
	 */
	@Test
	public void testGetTermURL() {
		assertEquals("Check get termURL", payerAuthenticationEnrollmentResultImpl.getTermURL(), null);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthenticationEnrollmentResultImpl.setTermURL()'.
	 */
	@Test
	public void testSetTermURL() {
		final String termURL = "www.elasticapth.com";
		payerAuthenticationEnrollmentResultImpl.setTermURL(termURL);
		assertEquals("Check set termURL", payerAuthenticationEnrollmentResultImpl.getTermURL(), termURL);
	}
}
