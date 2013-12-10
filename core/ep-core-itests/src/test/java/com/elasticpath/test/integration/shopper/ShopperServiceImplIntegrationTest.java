package com.elasticpath.test.integration.shopper;
/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.integration.cart.AbstractCartIntegrationTestParent;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * TODO.
 */
public class ShopperServiceImplIntegrationTest extends AbstractCartIntegrationTestParent {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ShopperService shopperService;

	/**
	 * Tests trying to find by Customer and Store.
	 *
	 * 1st time should create it.
	 * 2nd time to retrieve it.
	 *
	 * Checks that the guids match.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCustomerAndStore() {
		// Create a persistent customer and store.
		SimpleStoreScenario scenario = (SimpleStoreScenario) getTac().getScenario(SimpleStoreScenario.class);
		final String storeCode = scenario.getStore().getCode();

		Customer customer = getBeanFactory().getBean(ContextIdNames.CUSTOMER);
		customer.setEmail("testFindByCustomerAndStore@ShopperServiceImplIntegrationTest.com");
		customer.setStoreCode(storeCode);
		customer = customerService.add(customer);

		// Test
		final Shopper newShopper = shopperService.findOrCreateShopper(customer, storeCode);
		final Shopper persistedShopper = shopperService.findOrCreateShopper(customer, storeCode);

		// Verify
		assertEquals(String.format("newShopper GUID (%s) does not match persistedShopper GUID (%s)", newShopper.getGuid(), persistedShopper.getGuid()),
				newShopper.getGuid(), persistedShopper.getGuid());
	}

}
