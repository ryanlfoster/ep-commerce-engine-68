/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.shipping.Region;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/** Test cases for <code>ShippingRegionImpl</code>.*/
public class ShippingRegionImplTest {

	private static final String REGION_STR = "[CA(AB,BC)][US()]";

	private static final int REGION_MAP_SIZE = 2;

	private static final String COUNTRY_CODE_CA = "CA";

	private static final String SUB_COUNTRY_CODE_AB = "AB";

	private static final String SUB_COUNTRY_CODE_BC = "BC";

	private static final String SUB_COUNTRY_CODE_ON = "ON";

	private static final String COUNTRY_CODE_US = "US";

	private static final String COUNTRY_CODE_CN = "CN";

	private ShippingRegionImpl shippingRegionImpl;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepares for tests.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean("region", RegionImpl.class);
		this.shippingRegionImpl = new ShippingRegionImpl();
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingRegionImpl.getName()'.
	 */
	@Test
	public void testGetSetName() {
		final String testName = "Canada BC";
		this.shippingRegionImpl.setName(testName);
		assertEquals(testName, this.shippingRegionImpl.getName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingRegionImpl.getRegionMap()'.
	 */
	@Test
	public void testGetRegionMap() {
		this.shippingRegionImpl.setRegionStr(REGION_STR);
		Map<String, Region> regionMap = this.shippingRegionImpl.getRegionMap();
		assertEquals(REGION_MAP_SIZE, regionMap.size());
		assertEquals(COUNTRY_CODE_CA, regionMap.get(COUNTRY_CODE_CA).getCountryCode());
		List<String> caRegionSubCountryList = regionMap.get(COUNTRY_CODE_CA).getSubCountryCodeList();
		assertTrue(caRegionSubCountryList.contains(SUB_COUNTRY_CODE_AB));
		assertTrue(caRegionSubCountryList.contains(SUB_COUNTRY_CODE_BC));
		assertEquals(COUNTRY_CODE_US, regionMap.get(COUNTRY_CODE_US).getCountryCode());
		assertEquals(0, regionMap.get(COUNTRY_CODE_US).getSubCountryCodeList().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingRegionImpl.setRegionMap()'.
	 */
	@Test
	public void testSetRegionMap() {
		Map<String, Region> regionMap = new LinkedHashMap<String, Region>();
		Region caRegion = new RegionImpl(COUNTRY_CODE_CA,
										 Arrays.asList(SUB_COUNTRY_CODE_AB, SUB_COUNTRY_CODE_BC));
		regionMap.put(COUNTRY_CODE_CA, caRegion);

		regionMap.put(COUNTRY_CODE_US, new RegionImpl("US"));

		this.shippingRegionImpl.setRegionMap(regionMap);
		assertSame(regionMap, this.shippingRegionImpl.getRegionMap());

		//Check the regionStr
		String regionString = this.shippingRegionImpl.getRegionStr();
		assertTrue(regionString.contains("[CA(AB,BC)]"));
		assertTrue(regionString.contains("[US()]"));
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingRegionImpl.testIsInShippingRegion()'.
	 */
	@Test
	public void testIsInShippingRegion() {
		Address testAddress = new CustomerAddressImpl();
		testAddress.setCountry(COUNTRY_CODE_CN);

		this.shippingRegionImpl.setRegionStr(REGION_STR);
		assertFalse(this.shippingRegionImpl.isInShippingRegion(testAddress));

		testAddress.setCountry(COUNTRY_CODE_US);
		assertTrue(this.shippingRegionImpl.isInShippingRegion(testAddress));

		testAddress.setCountry(COUNTRY_CODE_CA);
		testAddress.setSubCountry(SUB_COUNTRY_CODE_BC);
		assertTrue(this.shippingRegionImpl.isInShippingRegion(testAddress));

		testAddress.setSubCountry(SUB_COUNTRY_CODE_ON);
		assertFalse(this.shippingRegionImpl.isInShippingRegion(testAddress));
	}
}
