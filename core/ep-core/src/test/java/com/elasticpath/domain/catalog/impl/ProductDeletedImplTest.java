/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.EpDomainException;

/**
 * Test <code>ProductDeletedImpl</code>.
 */
public class ProductDeletedImplTest {

	private ProductDeletedImpl productDeleted;

	private static final String DOMAIN_EXCEPTION_EXPECTED = "EpDomainException expected.";

	@Before
	public void setUp() throws Exception {
		productDeleted = new ProductDeletedImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductDeletedImpl.getProductUid()'.
	 */
	@Test
	public void testGetSetProductUid() {
		assertEquals(0, productDeleted.getProductUid());
		productDeleted.setProductUid(1);
		assertEquals(1, productDeleted.getProductUid());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductDeletedImpl.getDeletedDate()'.
	 */
	@Test
	public void testGetSetDeletedDate() {
		assertNull(productDeleted.getDeletedDate());
		final Date date = new Date();
		productDeleted.setDeletedDate(date);
		assertSame(date, productDeleted.getDeletedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductDeletedImpl.setDeletedDate()'.
	 */
	@Test
	public void testSetDeletedDateWithNull() {
		try {
			productDeleted.setDeletedDate(null);
			fail(DOMAIN_EXCEPTION_EXPECTED);
		} catch (final EpDomainException e) {
			assertNotNull(e);
		}
	}

}
