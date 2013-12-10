/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.tax.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.tax.TaxCode;

/** Test cases for <code>SalesTaxCodeImpl</code>.*/
public class TaxCodeImplTest {

	private TaxCodeImpl salesTaxCodeImpl; 
	
	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		this.salesTaxCodeImpl = new TaxCodeImpl();
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.tax.impl.SalesTaxCodeImpl.getCode()'.
	 */
	@Test
	public void testGetSetCode() {
		final String testCode = "Shipping";
		this.salesTaxCodeImpl.setCode(testCode);
		assertEquals(testCode, this.salesTaxCodeImpl.getCode());
	}

	/**
	 * Test method for 'com.elasticpath.domain.tax.impl.TaxCodeImpl.equals(Object)'.
	 */
	@Test
	public void testEquals() {
		final String gUid = "GUID";
		this.salesTaxCodeImpl.setGuid(gUid);
		TaxCode taxCodeToCompare = new TaxCodeImpl();
		taxCodeToCompare.setGuid(gUid);
		assertEquals(salesTaxCodeImpl, taxCodeToCompare);

		String anotherGuid = "Another_GUID";
		taxCodeToCompare.setGuid(anotherGuid);
		assertFalse(salesTaxCodeImpl.equals(taxCodeToCompare));
	}
}