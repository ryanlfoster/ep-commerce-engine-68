/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.misc.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.persistence.PropertiesDao;
import com.elasticpath.persistence.impl.PropertiesDaoImpl;
import com.elasticpath.service.misc.PropertyService;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/** Test cases for <code>PropertyServiceImpl</code>. */
public class PropertyServiceImplTest extends AbstractEPTestCase {

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private PropertyService propertyService;


	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();

		final PropertiesDao propertiesDao = new PropertiesDaoImpl();
		propertiesDao.setPropertiesLocation("conf/resources");
		propertyService = new PropertyServiceImpl();
		propertyService.setPropertiesDao(propertiesDao);
	}

	/**
	 * Test method for PropertyServiceImpl.setPropertiesDao().
	 */
	@Test
	public void testSetPropertiesDao() {
		propertyService.setPropertiesDao(null);
		try {
			propertyService.getPropertiesMap();
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

}
