/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of the public API of <code>CustomerAddressImpl</code>.
 */
public class AddressImplTest {
	private CustomerAddressImpl addressImpl;

	@Before
	public void setUp() throws Exception {
		addressImpl = new CustomerAddressImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.getCountry()'.
	 */
	@Test
	public void testGetFirstName() {
		assertEquals("Check get name", addressImpl.getFirstName(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.setCountry(String)'.
	 */
	@Test
	public void testSetFirstName() {
		final String name = "test name";
		addressImpl.setFirstName(name);
		assertSame("Check set name", addressImpl.getFirstName(), name);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.getCountry()'.
	 */
	@Test
	public void testGetLastName() {
		assertEquals("Check get name", addressImpl.getLastName(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.setCountry(String)'.
	 */
	@Test
	public void testSetLastName() {
		final String name = "test name";
		addressImpl.setLastName(name);
		assertSame("Check set name", addressImpl.getLastName(), name);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.getStreet()'.
	 */
	@Test
	public void testGetStreet1() {
		assertEquals("Check get street", addressImpl.getStreet1(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.setStreet(String)'.
	 */
	@Test
	public void testSetStreet1() {
		final String street = "test street";
		addressImpl.setStreet1(street);
		assertSame("Check set street", addressImpl.getStreet1(), street);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.getStreet()'.
	 */
	@Test
	public void testGetStreet2() {
		assertEquals("Check get street", addressImpl.getStreet2(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.setStreet(String)'.
	 */
	@Test
	public void testSetStreet2() {
		final String street = "test street";
		addressImpl.setStreet2(street);
		assertSame("Check set street", addressImpl.getStreet2(), street);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.getCountry()'.
	 */
	@Test
	public void testGetCountry() {
		assertEquals("Check get country", addressImpl.getCountry(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.setCountry(String)'.
	 */
	@Test
	public void testSetCountry() {
		final String country = "test country";
		addressImpl.setCountry(country);
		assertSame("Check set country", addressImpl.getCountry(), country);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.getPhoneNumber()'.
	 */
	@Test
	public void testGetPhoneNumber() {
		assertEquals("Check get phone number", addressImpl.getPhoneNumber(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.setPhoneNumber(String)'.
	 */
	@Test
	public void testSetPhoneNumber() {
		final String phoneNumber = "888-8888-888";
		addressImpl.setPhoneNumber(phoneNumber);
		assertSame("Check set phone number", addressImpl.getPhoneNumber(), phoneNumber);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.getFaxNumber()'.
	 */
	@Test
	public void testGetFaxNumber() {
		assertEquals("Check get fax number", addressImpl.getFaxNumber(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.setFaxNumber(String)'.
	 */
	@Test
	public void testSetFaxNumber() {
		final String faxNumber = "999-9999-999";
		addressImpl.setFaxNumber(faxNumber);
		assertSame("Check set fax number", addressImpl.getFaxNumber(), faxNumber);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.getCity()'.
	 */
	@Test
	public void testGetCity() {
		assertEquals("Check get city", addressImpl.getCity(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.setCity(String)'.
	 */
	@Test
	public void testSetCity() {
		final String city = "test city";
		addressImpl.setCity(city);
		assertSame("Check set city", addressImpl.getCity(), city);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.getStateOrProvince()'.
	 */
	@Test
	public void testGetSubCountry() {
		assertEquals("Check get state/province", addressImpl.getSubCountry(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.setStateOrProvince(String)'.
	 */
	@Test
	public void testSetSubCountry() {
		final String stateOrProvince = "test state or province";
		addressImpl.setSubCountry(stateOrProvince);
		assertSame("Check set state/province", addressImpl.getSubCountry(), stateOrProvince);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.getZipOrPostalCode()'.
	 */
	@Test
	public void testGetZipOrPostalCode() {
		assertEquals("Check get zip/postal code", addressImpl.getZipOrPostalCode(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.setZipOrPostalCode(String)'.
	 */
	@Test
	public void testSetZipOrPostalCode() {
		final String zipOrPostalCode = "test zip or postal code";
		addressImpl.setZipOrPostalCode(zipOrPostalCode);
		assertSame("Check set zip/postal code", addressImpl.getZipOrPostalCode(), zipOrPostalCode);
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.getCommercialAddress()' and
	 * 'com.elasticpath.domain.impl.CustomerAddressImpl.setCommercialAddress()'.
	 */
	@Test
	public void testGetCommercialAddress() {
		assertEquals("Check if commercial address", addressImpl.isCommercialAddress(), false);
		addressImpl.setCommercialAddress(true);
		assertEquals("Check if commercial address", addressImpl.isCommercialAddress(), true);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.getOrganization()'.
	 */
	public void testGetOrganization() {
		assertEquals("Check get organization", addressImpl.getOrganization(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerAddressImpl.setOrganization(String)'.
	 */
	public void testSetOrganization() {
		final String organization = "test organization";
		addressImpl.setOrganization(organization);
		assertSame("Check set organization", addressImpl.getOrganization(), organization);
	}
}
