/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


/**
 * Test <code>ElasticPathServiceImpl</code>.
 */
public class ElasticPathServiceImplTest {

	private ElasticPathServiceImpl elasticPathService;

	@Before
	public void setUp() throws Exception {
		elasticPathService = new ElasticPathServiceImpl();
	}

	/**
	 * Happy path. No change expected.
	 */
	@Test
	public void testSetStorefrontContextUrlNoChange() {
		elasticPathService.setStorefrontContextUrl("/storefront");
		assertEquals("/storefront", elasticPathService.getStorefrontContextUrl());
	}
	
	/**
	 * A null should convert to empty string.
	 */
	@Test
	public void testSetStorefrontContextUrlNull() {
		elasticPathService.setStorefrontContextUrl(null);
		assertEquals("", elasticPathService.getStorefrontContextUrl());
	}
	
	/**
	 * A string without a preceeding slash should have one added.
	 */
	@Test
	public void testSetStorefrontContextUrlAddSlash() {
		elasticPathService.setStorefrontContextUrl("storefront");
		assertEquals("/storefront", elasticPathService.getStorefrontContextUrl());
	}
}
