/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.service.customer.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.shopper.ShopperCleanupService;

/**
 * Test for {@link AnonymousCustomerCleanupServiceImpl}.
 */
public class AnonymousCustomerCleanupServiceImplTest {
	private static final int BATCH_SIZE = 10;
	private AnonymousCustomerCleanupServiceImpl anonymousCustomerCleanupServiceImpl;
	private PersistenceEngine persistenceEngine;
	private ShopperCleanupService shopperCleanupService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Initialize object under test with mocks.
	 */
	@Before
	public void initializeObjectUnderTestWithMocks() {
		persistenceEngine = context.mock(PersistenceEngine.class);
		shopperCleanupService = context.mock(ShopperCleanupService.class);
		anonymousCustomerCleanupServiceImpl = new AnonymousCustomerCleanupServiceImpl();
		anonymousCustomerCleanupServiceImpl.setPersistenceEngine(persistenceEngine);
		anonymousCustomerCleanupServiceImpl.setShopperCleanupService(shopperCleanupService);
	}

	/**
	 * Ensure null removal date causes exception.
	 */
	@Test(expected = EpServiceException.class)
	public void ensureNullRemovalDateCausesException() {
		anonymousCustomerCleanupServiceImpl.deleteAnonymousCustomers(null, BATCH_SIZE);
	}

	/**
	 * Ensure empty customer list is not processed.
	 */
	@Test
	public void ensureEmptyCustomerListIsNotProcessed() {
		Date now = new Date();
		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)),
						with(any(Integer.class)), with(any(Integer.class)));
				will(returnValue(Collections.emptyList()));
			}
		});
		anonymousCustomerCleanupServiceImpl.deleteAnonymousCustomers(now, BATCH_SIZE);
	}

	/**
	 * Ensure customer list is deleted.
	 */
	@Test
	public void ensureCustomerListIsDeleted() {
		Date now = new Date();
		final List<Long> customerUidsToDelete = Arrays.asList(1L, 2L, 3L, 4L);
		final List<Long> shopperUidsToDelete = Arrays.asList(1L, 2L, 3L);

		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(
						with("FIND_UIDS_OF_ANONYMOUS_CUSTOMERS_WITHOUT_ORDERS_AND_LAST_MODIFIED_BEFORE_DATE"),
						with(any(Object[].class)),
						with(any(Integer.class)),
						with(any(Integer.class)));

				will(returnValue(customerUidsToDelete));

				allowing(persistenceEngine).retrieveByNamedQueryWithList("FIND_SHOPPER_UIDS_BY_CUSTOMER_UIDS", "list", customerUidsToDelete);
				will(returnValue(shopperUidsToDelete));

				allowing(shopperCleanupService).removeShoppersByUidListAndTheirDependents(shopperUidsToDelete);
				will(returnValue(shopperUidsToDelete.size()));

				allowing(persistenceEngine).executeNamedQueryWithList("DELETE_CUSTOMER_BY_UID_LIST", "list", customerUidsToDelete);
				will(returnValue(customerUidsToDelete.size()));

			}
		});
		int deletedCustomerCount = anonymousCustomerCleanupServiceImpl.deleteAnonymousCustomers(now, BATCH_SIZE);
		assertEquals("Incorrect deleted customers count. ", customerUidsToDelete.size(), deletedCustomerCount);
	}


}

