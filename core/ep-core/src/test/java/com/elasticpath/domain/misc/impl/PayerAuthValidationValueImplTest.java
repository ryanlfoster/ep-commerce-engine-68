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

public class PayerAuthValidationValueImplTest {
	private PayerAuthValidationValueImpl payerAuthValidationValueImpl;

	@Before
	public void setUp() throws Exception {
		payerAuthValidationValueImpl = new PayerAuthValidationValueImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.getAAV()'.
	 */
	@Test
	public void testGetAAV() {
		assertEquals("Check get AAV", payerAuthValidationValueImpl.getAAV(), null);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.setAAV()'.
	 */
	@Test
	public void testSetAAV() {
		final String aav = "test";
		payerAuthValidationValueImpl.setAAV(aav);
		assertEquals("Check set AAV", payerAuthValidationValueImpl.getAAV(), aav);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.getCAVV()'.
	 */
	@Test
	public void testGetCAVV() {
		assertEquals("Check get CAVV", payerAuthValidationValueImpl.getCAVV(), null);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.setCAVV()'.
	 */
	@Test
	public void testSetCAVV() {
		final String cavv = "test cavv";
		payerAuthValidationValueImpl.setCAVV(cavv);
		assertEquals("Check set CAVV", payerAuthValidationValueImpl.getCAVV(), cavv);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.getCommerceIndicator()'.
	 */
	@Test
	public void testGetCommerceIndicator() {
		assertEquals("Check get CommerceIndicator", payerAuthValidationValueImpl.getCommerceIndicator(), null);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.setCommerceIndicator()'.
	 */
	@Test
	public void testSetCommerceIndicator() {
		final String commerceIndicator = "test CommerceIndicator";
		payerAuthValidationValueImpl.setCommerceIndicator(commerceIndicator);
		assertEquals("Check set commerceIndicator", payerAuthValidationValueImpl.getCommerceIndicator(), commerceIndicator);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.getECI()'.
	 */
	@Test
	public void testGetECI() {
		assertEquals("Check get ECI", payerAuthValidationValueImpl.getECI(), null);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.setECI()'.
	 */
	@Test
	public void testSetECI() {
		final String eci = "test ECI";
		payerAuthValidationValueImpl.setECI(eci);
		assertEquals("Check set ECI", payerAuthValidationValueImpl.getECI(), eci);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.getXID()'.
	 */
	@Test
	public void testGetXID() {
		assertEquals("Check get XID", payerAuthValidationValueImpl.getXID(), null);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.setXID()'.
	 */
	@Test
	public void testSetXID() {
		final String xid = "test XID";
		payerAuthValidationValueImpl.setXID(xid);
		assertEquals("Check set XID", payerAuthValidationValueImpl.getXID(), xid);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.isValidated()'.
	 */
	@Test
	public void testIsValidated() {
		assertFalse("Check is 3D validated?", payerAuthValidationValueImpl.isValidated());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.setValidated()'.
	 */
	@Test
	public void testSetValidated() {
		payerAuthValidationValueImpl.setValidated(true);
		assertTrue("Check set validated", payerAuthValidationValueImpl.isValidated());
	}
		
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.getPaRES()'.
	 */
	@Test
	public void testGetPaRES() {
		assertEquals("Check get PaRES", payerAuthValidationValueImpl.getPaRES(), null);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.setPaRES()'.
	 */
	@Test
	public void testSetMerchantData() {
		final String paRES = "pares test";
		payerAuthValidationValueImpl.setPaRES(paRES);
		assertSame("Check set PaRES", payerAuthValidationValueImpl.getPaRES(), paRES);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.getUcafCollectionIndicator()'.
	 */
	@Test
	public void testGetUcafCollectionIndicator() {
		assertEquals("Check get UcafCollectionIndicator", payerAuthValidationValueImpl.getUcafCollectionIndicator(), null);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl.setUcafCollectionIndicator()'.
	 */
	@Test
	public void testSetTermURL() {
		final String ucafCollectionIndicator = "UcafCollectionIndicator test";
		payerAuthValidationValueImpl.setUcafCollectionIndicator(ucafCollectionIndicator);
		assertEquals("Check set UcafCollectionIndicator", payerAuthValidationValueImpl.getUcafCollectionIndicator(), ucafCollectionIndicator);
	}
}
