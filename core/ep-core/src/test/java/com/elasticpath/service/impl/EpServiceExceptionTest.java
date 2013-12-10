/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.impl;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.commons.exception.DuplicateNameException;
import com.elasticpath.commons.exception.EmailExistException;
import com.elasticpath.commons.exception.EmailNonExistException;
import com.elasticpath.service.catalog.SkuOptionKeyExistException;
import com.elasticpath.service.cmuser.UserNameExistException;
import com.elasticpath.service.cmuser.UserRoleNameExistException;
import com.elasticpath.service.customer.GroupExistException;
import com.elasticpath.service.dataimport.ImportJobExistException;
import com.elasticpath.service.rules.DuplicatePromoCodeException;
import com.elasticpath.service.shipping.ShippingRegionExistException;
import com.elasticpath.service.tax.TaxCodeExistException;
import com.elasticpath.service.tax.TaxJurisdictionExistException;

/** Test Cases for all service exceptions. */
public class EpServiceExceptionTest {

	private static final String TEST = "Test.";

	/**
	 * Test method for 'com.elasticpath.base.exception.EpServiceException.EpServiceException(String)'.
	 */
	@Test
	public void testEpServiceException() {
		assertNotNull(new EpServiceException(TEST));
		assertNotNull(new EpServiceException(TEST, new IOException(TEST)));
	}

	/**
	 * Test method for 'com.elasticpath.base.exception.EpServiceException.EpServiceException(String)'.
	 */
	@Test
	public void testGroupExistException() {
		assertNotNull(new GroupExistException(TEST));
		assertNotNull(new GroupExistException(TEST, new IOException(TEST)));
	}

	/**
	 * Test method for 'com.elasticpath.base.exception.EpServiceException.EpServiceException(String)'.
	 */
	@Test
	public void testUserIdExistException() {
		assertNotNull(new UserNameExistException(TEST));
		assertNotNull(new UserNameExistException(TEST, new IOException(TEST)));
	}

	/**
	 * Test method for 'com.elasticpath.base.exception.EpServiceException.EpServiceException(String)'.
	 */
	@Test
	public void testEmailExistException() {
		assertNotNull(new EmailExistException(TEST));
		assertNotNull(new EmailExistException(TEST, new IOException(TEST)));
	}

	/**
	 * Test method for 'com.elasticpath.base.exception.EpServiceException.EpServiceException(String)'.
	 */
	@Test
	public void testEmailNotExistException() {
		assertNotNull(new EmailNonExistException(TEST));
		assertNotNull(new EmailNonExistException(TEST, new IOException(TEST)));
	}

	/**
	 * Test method for 'com.elasticpath.service.DuplicateKeyException()'.
	 */
	@Test
	public void testDuplicationKeyException() {
		assertNotNull(new DuplicateKeyException(TEST));
		assertNotNull(new DuplicateKeyException(TEST, new IOException(TEST)));
	}

	/**
	 * Test method for 'com.elasticpath.service.DuplicateNameException()'.
	 */
	@Test
	public void testDuplicationNameException() {
		assertNotNull(new DuplicateNameException(TEST));
		assertNotNull(new DuplicateNameException(TEST, new IOException(TEST)));
	}

	/**
	 * Test method for 'com.elasticpath.service.DuplicatePromoCodeException()'.
	 */
	@Test
	public void testDuplicationPromoCodeException() {
		assertNotNull(new DuplicatePromoCodeException(TEST));
		assertNotNull(new DuplicatePromoCodeException(TEST, new IOException(TEST)));
	}

	/**
	 * Test method for 'com.elasticpath.service.ImportJobExistException()'.
	 */
	@Test
	public void testImportJobExistException() {
		assertNotNull(new ImportJobExistException(TEST));
		assertNotNull(new ImportJobExistException(TEST, new IOException(TEST)));
	}

	/**
	 * Test method for 'com.elasticpath.service.ShippingRegionExistException()'.
	 */
	@Test
	public void testShippingRegionExistException() {
		assertNotNull(new ShippingRegionExistException(TEST));
		assertNotNull(new ShippingRegionExistException(TEST, new IOException(TEST)));
	}

	/**
	 * Test method for 'com.elasticpath.service.SkuOptionKeyExistException()'.
	 */
	@Test
	public void testSkuOptionKeyExistException() {
		assertNotNull(new SkuOptionKeyExistException(TEST));
		assertNotNull(new SkuOptionKeyExistException(TEST, new IOException(TEST)));
	}

	/**
	 * Test method for 'com.elasticpath.service.UserNameExistException()'.
	 */
	@Test
	public void testUserNameExistException() {
		assertNotNull(new UserNameExistException(TEST));
		assertNotNull(new UserNameExistException(TEST, new IOException(TEST)));
	}

	/**
	 * Test method for 'com.elasticpath.service.UserRoleNameExistException()'.
	 */
	@Test
	public void testUserRoleNameExistException() {
		assertNotNull(new UserRoleNameExistException(TEST));
		assertNotNull(new UserRoleNameExistException(TEST, new IOException(TEST)));
	}

	/**
	 * Test method for 'com.elasticpath.service.TaxCodeExistException()'.
	 */
	@Test
	public void testTaxCodeExistException() {
		assertNotNull(new TaxCodeExistException(TEST));
		assertNotNull(new TaxCodeExistException(TEST, new IOException(TEST)));
	}

	/**
	 * Test method for 'com.elasticpath.service.TaxJurisdictionExistException()'.
	 */
	@Test
	public void testTaxJurisdictionExistException() {
		assertNotNull(new TaxJurisdictionExistException(TEST));
		assertNotNull(new TaxJurisdictionExistException(TEST, new IOException(TEST)));
	}
}
