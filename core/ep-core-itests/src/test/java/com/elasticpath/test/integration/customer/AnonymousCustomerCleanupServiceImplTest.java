/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.test.integration.customer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.customer.AnonymousCustomerCleanupService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.customer.impl.AnonymousCustomerCleanupServiceImpl;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.OrderTestPersister;
import com.elasticpath.test.persister.StoreTestPersister;
import com.elasticpath.test.persister.testscenarios.ShoppingCartSimpleStoreScenario;

/**
 * Integration tests of {@link AnonymousCustomerCleanupServiceImpl}.
 */
public final class AnonymousCustomerCleanupServiceImplTest extends BasicSpringContextTest {

	private static final String ANONYMOUS_ID = "public@ep-cortex.com";
	private static final int EXPECTED_MAX_HISTORY = 60;

	@Autowired
	private PersistenceEngineTestEntityListenerConfigurer persistenceEngineTestEntityListenerConfigurer;

	@Autowired
	private AnonymousCustomerCleanupService anonymousCustomerCleanupService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ShopperService shopperService;

	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private WishListService wishListService;

	@Autowired
	private CustomerSessionService customerSessionService;

	@Autowired
	private CartOrderService cartOrderService; 

	private Date afterRemovalDate;

	private Date removalDate;

	private Date beforeRemovalDate;

	private ShoppingCartSimpleStoreScenario scenario;

	/**
	 * Get a reference to TestApplicationContext for use within the test. <br>
	 * Setup scenarios.
	 * @throws Exception on exception
	 */
	@Before
	public void setUp() throws Exception {
		afterRemovalDate = new Date();
		removalDate = getAdjustedDate(afterRemovalDate, EXPECTED_MAX_HISTORY);
		beforeRemovalDate = getAdjustedDate(afterRemovalDate, EXPECTED_MAX_HISTORY + 1);
		scenario = getTac().useScenario(ShoppingCartSimpleStoreScenario.class);
		persistenceEngineTestEntityListenerConfigurer.disableLastModifiedListenerOnTypes(Customer.class);
	}

	/**
	 * Test that the max history applied to a date removes the anonymous customers, but not the non-anonymous customers.
	 */
	@DirtiesDatabase
	@Test
	public void testCandidateRemovalByDateBoundariesAndAnonymity() {
		Customer anonymousCustomerDatedAfterRemovalDate = getPersistedAnonymousCustomerWithLastModifiedDateOf(afterRemovalDate);
		Customer anonymousCustomerDatedWithRemovalDate = getPersistedAnonymousCustomerWithLastModifiedDateOf(removalDate);
		Customer anonymousCustomerDatedBeforeRemovalDate = getPersistedAnonymousCustomerWithLastModifiedDateOf(beforeRemovalDate);
		Customer registeredCustomerDatedWithRemovalDate = getPersistRegisteredCustomerWithLastModifiedDateOf(beforeRemovalDate);

		int result = anonymousCustomerCleanupService.deleteAnonymousCustomers(removalDate, 1);
		assertEquals("There should have been one customer removed.", 1, result);

		assertDatabaseStillHasCustomer(anonymousCustomerDatedAfterRemovalDate);
		assertDatabaseStillHasCustomer(anonymousCustomerDatedWithRemovalDate);
		assertDatabaseStillHasCustomer(registeredCustomerDatedWithRemovalDate);
		assertDatabaseHasDeletedCustomer(anonymousCustomerDatedBeforeRemovalDate);
	}

	/**
	 * Ensure that the associated referential entities are removed with anonymous customer removal.
	 */
	@DirtiesDatabase
	@Test
	public void ensureAssociatedReferentialEntitiesAreRemovedWithAnonymousCustomerRemoval() {
		Customer customer = getPersistedAnonymousCustomerWithLastModifiedDateOf(beforeRemovalDate);
		CustomerSession customerSession = getTac().getPersistersFactory().getStoreTestPersister().persistCustomerSessionWithAssociatedEntities(customer);
		Shopper shopper = customerSession.getShopper();
		WishList wishList = wishListService.createWishList(shopper);
		ShoppingCart shoppingCart =  shopper.getCurrentShoppingCart();
		String shoppingCartGuid = shoppingCart.getGuid();
		cartOrderService.createIfNotExists(shoppingCartGuid);

		int deletedCustomerCount = anonymousCustomerCleanupService.deleteAnonymousCustomers(removalDate, 1);
		assertEquals("There should have been one customer removed.", 1, deletedCustomerCount);

		CustomerSession retrievedCustomerSession = customerSessionService.findByGuid(customerSession.getGuid());
		assertNull("The associated customer session should have been removed", retrievedCustomerSession);

		Shopper retrievedShopper = shopperService.get(shopper.getUidPk());
		assertNull("The associated shopper should have been removed.", retrievedShopper);

		ShoppingCart retrievedShoppingCart = shoppingCartService.findByGuid(shoppingCartGuid);
		assertNull("The associated shopping cart should have been removed.", retrievedShoppingCart);

		CartOrder retrievedCartOrder = cartOrderService.findByShoppingCartGuid(shoppingCartGuid);
		assertNull("The associated cart order should have been removed.", retrievedCartOrder);
		
		WishList retrievedWishList = wishListService.get(wishList.getUidPk());
		assertNull("The associated wish list should have been removed.", retrievedWishList);

		Customer retrievedCustomer = customerService.findByGuid(customer.getGuid());
		assertNull("The candidate customer which is before the removal date should not exist in database.", retrievedCustomer);
	}

	/**
	 * Test removal ignores anonymous customer with order.
	 */
	@DirtiesDatabase
	@Test
	public void testRemovalIgnoresAnonymousCustomerWithOrder() {
		Customer anonymousCustomerWithOrderBeforeRemovalDate = getPersistedAnonymousCustomerWithLastModifiedDateOf(beforeRemovalDate);
		addOrderToCustomer(anonymousCustomerWithOrderBeforeRemovalDate);

		int deletedCustomerCount = anonymousCustomerCleanupService.deleteAnonymousCustomers(removalDate, 1);
		assertEquals("There should be no customers removed.", 0, deletedCustomerCount);

		assertDatabaseStillHasCustomer(anonymousCustomerWithOrderBeforeRemovalDate);
	}

	/**
	 * Test removal restricted to maximum amount.
	 */
	@DirtiesDatabase
	@Test
	public void testRemovalRestrictedToMaximumAmount() {
		int expiredAnonymousCustomerCount = 6;
		List<Long> addedCustomerUids = new ArrayList<Long>();

		for (int i = 0; i < expiredAnonymousCustomerCount; ++i) {
			Customer anonymousCustomerDatedAfterRemovalDate = getPersistedAnonymousCustomerWithLastModifiedDateOf(beforeRemovalDate);
			addedCustomerUids.add(anonymousCustomerDatedAfterRemovalDate.getUidPk());
		}

		int removalBatchSize = expiredAnonymousCustomerCount - 1;
		int deletedCustomerCount = anonymousCustomerCleanupService.deleteAnonymousCustomers(removalDate, removalBatchSize);
		assertEquals("There should be " + removalBatchSize + " customers removed.", removalBatchSize, deletedCustomerCount);
		List<Customer> retrievedCustomers = customerService.findByUids(addedCustomerUids);
		assertEquals("Number of remaining customers is incorrect.", expiredAnonymousCustomerCount - removalBatchSize, retrievedCustomers.size());
	}

	private Customer getPersistedAnonymousCustomerWithLastModifiedDateOf(final Date lastModifiedDate) {
		return createAndPersistCustomer(true, lastModifiedDate);
	}

	private Customer getPersistRegisteredCustomerWithLastModifiedDateOf(final Date lastModifiedDate) {
		return createAndPersistCustomer(false, lastModifiedDate);
	}

	private Customer createAndPersistCustomer(final boolean anonymous, final Date lastModifiedDate) {
		StoreTestPersister storeTestPersister = getTac().getPersistersFactory().getStoreTestPersister();
		Customer customer = storeTestPersister.createDefaultCustomer(scenario.getStore());
		customer.setAnonymous(anonymous);

		customer.setEmail(ANONYMOUS_ID);
		if (customer.getUserId() == null) {
			customer.setUserId(ANONYMOUS_ID);
		}

		customer.setLastModifiedDate(lastModifiedDate);
		return customerService.update(customer);
	}

	private void assertDatabaseStillHasCustomer(final Customer customer) {
		Customer retrievedCustomer = customerService.findByGuid(customer.getGuid());
		assertEquals("The customer should still exist in database.", retrievedCustomer.getGuid(), customer.getGuid());
	}

	private void assertDatabaseHasDeletedCustomer(final Customer customer) {
		Customer retrievedCustomer = customerService.findByGuid(customer.getGuid());
		assertNull("The customer should not exist in database.", retrievedCustomer);
	}

	private void addOrderToCustomer(final Customer customer) {
		OrderTestPersister orderTestPersister = getTac().getPersistersFactory().getOrderTestPersister();
		orderTestPersister.createOrderForCustomerWithSkusQuantity(customer, scenario.getStore(), 1, scenario.getNonShippableProducts()
				.get(0)
				.getDefaultSku());
	}

	private Date getAdjustedDate(final Date now, final int adjustment) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.DAY_OF_YEAR, - adjustment);
		return calendar.getTime();
	}
}
