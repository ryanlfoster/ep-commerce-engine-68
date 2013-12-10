/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.service.shoppingcart.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.cartorder.CartOrderService;

/**
 * Test {@link ShoppingCartCleanupServiceImpl} functionality.
 */
public class ShoppingCartCleanupServiceImplTest {

	private static final String DELETE_SHOPPING_CART_QUERY = "SHOPPING_CART_DELETE_BY_GUID";

	private static final String ABANDONED_CART_JPA_QUERY = "FIND_SHOPPING_CART_GUIDS_LAST_MODIFIED_BEFORE_DATE";

	private static final String CART_GUID_1 = "CART_GUID_1";

	private static final int EXPECTED_MAX_RESULTS = 1000;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final ShoppingCartCleanupServiceImpl shoppingCartCleanupService = new ShoppingCartCleanupServiceImpl();

	private final PersistenceEngine persistenceEngine = context.mock(PersistenceEngine.class);

	private final CartOrderService cartOrderService = context.mock(CartOrderService.class);

	/**
	 * Perform setup.
	 */
	@Before
	public void setUp() {
		shoppingCartCleanupService.setCartOrderService(cartOrderService);
		shoppingCartCleanupService.setPersistenceEngine(persistenceEngine);
	}

	/**
	 * Test a happy path where the service successfully finds candidates for removal.
	 */
	@Test
	public void testSuccessfullyFindsCandidatesForRemoval() {

		final Date expectedRemovalDate = new Date();
		final List<String> expectedShoppingCartGuids = new ArrayList<String>();
		expectedShoppingCartGuids.add(CART_GUID_1);

		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(ABANDONED_CART_JPA_QUERY,
						new Object[] { expectedRemovalDate },
						0,
						EXPECTED_MAX_RESULTS);
				will(returnValue(expectedShoppingCartGuids));
				allowing(persistenceEngine).executeNamedQueryWithList(DELETE_SHOPPING_CART_QUERY, "list", expectedShoppingCartGuids);
				allowing(cartOrderService).removeIfExistsByShoppingCartGuids(expectedShoppingCartGuids);
			}
		});

		int result = shoppingCartCleanupService.deleteAbandonedShoppingCarts(expectedRemovalDate, EXPECTED_MAX_RESULTS);

		assertEquals("Should state that a shopping cart record was removed.", 1, result);
	}

	/**
	 * Test a path where the service finds no candidates for removal.
	 */
	@Test
	public void testFindsNoCandidatesForRemoval() {

		final Date expectedRemovalDate = new Date();
		final List<String> expectedShoppingCartGuids = new ArrayList<String>();

		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(ABANDONED_CART_JPA_QUERY,
						new Object[] { expectedRemovalDate },
						0,
						EXPECTED_MAX_RESULTS);
				will(returnValue(expectedShoppingCartGuids));
			}
		});

		int result = shoppingCartCleanupService.deleteAbandonedShoppingCarts(expectedRemovalDate, EXPECTED_MAX_RESULTS);

		assertEquals("Should state that no shopping cart records were removed.", 0, result);
	}

	/**
	 * Test failure when the removal date is null.
	 */
	@Test(expected = EpServiceException.class)
	public void testRemovalDateIsNull() {
		final Date expectedRemovalDate = null;

		shoppingCartCleanupService.deleteAbandonedShoppingCarts(expectedRemovalDate, EXPECTED_MAX_RESULTS);
	}

}
