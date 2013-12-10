package com.elasticpath.domain.shipping.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.shipping.Region;

/** Test cases for <code>RegionImpl</code>.*/
public class RegionImplTest {

	private static final String CA_REGION_STR = "[CA(AB,BC)]";
	
	private static final String US_REGRION_STR = "[US()]";
	
	private static final String COUNTRY_CODE_CA = "CA";
	
	private static final String SUB_COUNTRY_CODE_AB = "AB";
	
	private static final String SUB_COUNTRY_CODE_BC = "BC";
	
	private static final String COUNTRY_CODE_US = "US";
	
	private static final String EP_DOMAIN_EXCEPTION_EXPECTED = "EpDomainException expected.";
	
	
	private RegionImpl regionImpl;
	
	/**
	 * Prepare for each test.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		this.regionImpl = new RegionImpl();
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.RegionImpl.getCountryCode()'.
	 */
	@Test
	public void testGetSetCountryCode() {
		this.regionImpl.setCountryCode(COUNTRY_CODE_CA);
		assertEquals(COUNTRY_CODE_CA, this.regionImpl.getCountryCode());
		
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.RegionImpl.getSubCountryCodeList()'.
	 */
	@Test
	public void testGetSetSubCountryCodeList() {
		List<String> emptyList = new ArrayList<String>();
		this.regionImpl.setSubCountryCodeList(emptyList); 
		assertEquals(emptyList, this.regionImpl.getSubCountryCodeList()); 
		
		try { 
			this.regionImpl.setSubCountryCodeList(null);
			fail(EP_DOMAIN_EXCEPTION_EXPECTED);
		} catch (final EpDomainException e) {
			assertNotNull(e);
			// Success!
		}
		
		this.regionImpl.setSubCountryCodeList(Arrays.asList(SUB_COUNTRY_CODE_AB, SUB_COUNTRY_CODE_BC));
		assertTrue(this.regionImpl.getSubCountryCodeList().contains(SUB_COUNTRY_CODE_AB));
		assertTrue(this.regionImpl.getSubCountryCodeList().contains(SUB_COUNTRY_CODE_BC));
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.RegionImpl.toString()'.
	 */
	@Test
	public void testToString() {
		Region caRegion = new RegionImpl(COUNTRY_CODE_CA, 
				 Arrays.asList(SUB_COUNTRY_CODE_AB, SUB_COUNTRY_CODE_BC));
		assertEquals(CA_REGION_STR, caRegion.toString());
		
		Region usRegion = new RegionImpl(COUNTRY_CODE_US);
		assertEquals(US_REGRION_STR, usRegion.toString());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.RegionImpl.fromString()'.
	 */
	@Test
	public void testFromString() {
		this.regionImpl.fromString(CA_REGION_STR);
		assertEquals(COUNTRY_CODE_CA, this.regionImpl.getCountryCode());
		assertTrue(this.regionImpl.getSubCountryCodeList().contains(SUB_COUNTRY_CODE_AB));
		assertTrue(this.regionImpl.getSubCountryCodeList().contains(SUB_COUNTRY_CODE_BC));
		
	
	}
}
