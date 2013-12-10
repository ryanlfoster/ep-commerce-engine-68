/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.cartorder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.service.customer.dao.CustomerAddressDao;
import com.elasticpath.service.shipping.ShippingServiceLevelService;

/**
 * Test functionality of the {@link ClearingCartOrderShippingInformationSanitizer}.
 */
public class ClearingCartOrderShippingInformationSanitizerTest {

	private static final String LEVEL_GUID_SHOULD_BE_CLEARED = "The cart order shipping service level guid should be cleared.";

	private static final String ADDRESS_GUID_SHOULD_BE_CLEARED = "The cart order shipping address guid should be cleared.";

	private static final String LEVEL_GUID_SHOULD_BE_UNCHANGED = "The shipping service level guid should remain unchanged.";

	private static final String ADDRESS_GUID_SHOULD_BE_UNCHANGED = "The shipping address guid should remain unchanged.";

	private static final String CART_ORDER_SHOULD_BE_UNCHANGED = "CartOrder should be unchanged.";

	private static final String CART_ORDER_SHOULD_BE_CHANGED = "CartOrder should be changed.";

	private static final String SHIPPING_ADDRESS_GUID = "SHIPPING_ADDRESS_GUID";

	private static final String SHIPPING_SERVICE_LEVEL_GUID = "SHIPPING_SERVICE_LEVEL_GUID";

	private static final String NON_EXISTENT_SHIPPING_ADDRESS_GUID = "NON_EXISTENT_SHIPPING_ADDRESS_GUID";

	private static final String NON_EXISTENT_SHIPPING_SERVICE_LEVEL_GUID = "NON_EXISTENT_SHIPPING_SERVICE_LEVEL_GUID";

	private static final String STORE_CODE = "STORE_CODE";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final CustomerAddressDao customerAddressDao = context.mock(CustomerAddressDao.class);

	private final ShippingServiceLevelService shippingServiceLevelService = context.mock(ShippingServiceLevelService.class);

	private ClearingCartOrderShippingInformationSanitizer clearingCartOrderShippingInformationSanitizer;

	/**
	 * Set up the test environment.
	 */
	@Before
	public void setUp() {
		clearingCartOrderShippingInformationSanitizer = new ClearingCartOrderShippingInformationSanitizer();
		clearingCartOrderShippingInformationSanitizer.setCustomerAddressDao(customerAddressDao);
		clearingCartOrderShippingInformationSanitizer.setShippingServiceLevelService(shippingServiceLevelService);
	}

	/**
	 * Ensure clearing of address and level on cart order with non existent shipping address.
	 */
	@Test
	public void ensureClearingOfAddressAndLevelOnCartOrderWithNonExistentShippingAddress() {
		shouldReturnCustomerAddressFromFindByGuidForGuid(null, NON_EXISTENT_SHIPPING_ADDRESS_GUID);

		CartOrder cartOrder = createCartOrder(NON_EXISTENT_SHIPPING_ADDRESS_GUID, SHIPPING_SERVICE_LEVEL_GUID);
		boolean cartOrderWasUpdated = clearingCartOrderShippingInformationSanitizer.sanitize(STORE_CODE, cartOrder);

		assertNull(ADDRESS_GUID_SHOULD_BE_CLEARED, cartOrder.getShippingAddressGuid());
		assertNull(LEVEL_GUID_SHOULD_BE_CLEARED, cartOrder.getShippingServiceLevelGuid());
		
		assertTrue(CART_ORDER_SHOULD_BE_CHANGED, cartOrderWasUpdated);
	}

	/**
	 * Ensure unchanged cart order when shipping service level not set.
	 */
	@Test
	public void ensureUnchangedCartOrderWhenShippingServiceLevelNotSet() {
		shouldReturnCustomerAddressFromFindByGuidForGuid(createCustomerAddress(SHIPPING_ADDRESS_GUID), SHIPPING_ADDRESS_GUID);
		CartOrder cartOrder = createCartOrder(SHIPPING_ADDRESS_GUID, null);
		boolean cartOrderWasUpdated = clearingCartOrderShippingInformationSanitizer.sanitize(STORE_CODE, cartOrder);

		assertEquals(ADDRESS_GUID_SHOULD_BE_UNCHANGED, SHIPPING_ADDRESS_GUID, cartOrder.getShippingAddressGuid());
		assertNull("The initial cart order should not populate a default shipping service level if it is not populated.",
				cartOrder.getShippingServiceLevelGuid());
		assertFalse(CART_ORDER_SHOULD_BE_UNCHANGED, cartOrderWasUpdated);
	}

	/**
	 * Ensure level cleared on cart order when shipping service level does not exist.
	 */
	@Test
	public void ensureLevelClearedOnCartOrderWhenShippingServiceLevelDoesNotExist() {
		shouldReturnCustomerAddressFromFindByGuidForGuid(createCustomerAddress(SHIPPING_ADDRESS_GUID), SHIPPING_ADDRESS_GUID);

		context.checking(new Expectations() {
			{
				oneOf(shippingServiceLevelService).findByGuid(NON_EXISTENT_SHIPPING_SERVICE_LEVEL_GUID);
				will(returnValue(null));
			}
		});
		CartOrder cartOrder = createCartOrder(SHIPPING_ADDRESS_GUID, NON_EXISTENT_SHIPPING_SERVICE_LEVEL_GUID);
		boolean cartOrderWasUpdated = clearingCartOrderShippingInformationSanitizer.sanitize(STORE_CODE, cartOrder);

		assertEquals(ADDRESS_GUID_SHOULD_BE_UNCHANGED, SHIPPING_ADDRESS_GUID, cartOrder.getShippingAddressGuid());
		assertNull("The initial cart order should not populate a default shipping service level if it is not populated.",
				cartOrder.getShippingServiceLevelGuid());
		assertTrue(CART_ORDER_SHOULD_BE_CHANGED, cartOrderWasUpdated);
	}

	/**
	 * Ensure level cleared on cart order when shipping service level is not valid.
	 */
	@Test
	public void ensureLevelClearedOnCartOrderWhenShippingServiceLevelIsNotValid() {
		shouldReturnCustomerAddressFromFindByGuidForGuid(createCustomerAddress(SHIPPING_ADDRESS_GUID), SHIPPING_ADDRESS_GUID);
		shouldValidateShippingServiceLevel(false);

		CartOrder cartOrder = createCartOrder(SHIPPING_ADDRESS_GUID, SHIPPING_SERVICE_LEVEL_GUID);
		boolean cartOrderWasUpdated = clearingCartOrderShippingInformationSanitizer.sanitize(STORE_CODE, cartOrder);

		assertEquals(ADDRESS_GUID_SHOULD_BE_UNCHANGED, SHIPPING_ADDRESS_GUID, cartOrder.getShippingAddressGuid());
		assertNull("The initial cart order should clear the shipping service level if it is invalid.",
				cartOrder.getShippingServiceLevelGuid());
		assertTrue(CART_ORDER_SHOULD_BE_CHANGED, cartOrderWasUpdated);
	}

	/**
	 * Ensure unchanged cart order when all shipping info is valid.
	 */
	@Test
	public void ensureUnchangedCartOrderWhenAllShippingInfoIsValid() {
		shouldReturnCustomerAddressFromFindByGuidForGuid(createCustomerAddress(SHIPPING_ADDRESS_GUID), SHIPPING_ADDRESS_GUID);
		shouldValidateShippingServiceLevel(true);

		CartOrder cartOrder = createCartOrder(SHIPPING_ADDRESS_GUID, SHIPPING_SERVICE_LEVEL_GUID);
		boolean cartOrderWasUpdated = clearingCartOrderShippingInformationSanitizer.sanitize(STORE_CODE, cartOrder);

		assertEquals(ADDRESS_GUID_SHOULD_BE_UNCHANGED, SHIPPING_ADDRESS_GUID, cartOrder.getShippingAddressGuid());
		assertEquals(LEVEL_GUID_SHOULD_BE_UNCHANGED, SHIPPING_SERVICE_LEVEL_GUID, cartOrder.getShippingServiceLevelGuid());
		assertFalse(CART_ORDER_SHOULD_BE_UNCHANGED, cartOrderWasUpdated);
	}

	private void shouldReturnCustomerAddressFromFindByGuidForGuid(final CustomerAddress customerAddress, final String customerAddressGuid) {
		context.checking(new Expectations() {
			{
				oneOf(customerAddressDao).findByGuid(customerAddressGuid);
				will(returnValue(customerAddress));
			}
		});
	}

	private void shouldValidateShippingServiceLevel(final boolean validShippingServiceLevel) {
		context.checking(new Expectations() {
			{
				ShippingServiceLevel shippingServiceLevel = context.mock(ShippingServiceLevel.class);

				oneOf(shippingServiceLevelService).findByGuid(SHIPPING_SERVICE_LEVEL_GUID);
				will(returnValue(shippingServiceLevel));

				oneOf(shippingServiceLevel).isApplicable(with(any(String.class)), with(any(Address.class)));
				will(returnValue(validShippingServiceLevel));
			}
		});
	}

	private CartOrder createCartOrder(final String shippingAddressGuid, final String shippingServiceLevelGuid) {
		CartOrder initialCartOrder = new CartOrderImpl();
		initialCartOrder.setShippingAddressGuid(shippingAddressGuid);
		initialCartOrder.setShippingServiceLevelGuid(shippingServiceLevelGuid);
		return initialCartOrder;
	}

	private CustomerAddress createCustomerAddress(final String guid) {
		CustomerAddress customerAddress = new CustomerAddressImpl();
		customerAddress.setGuid(guid);
		return customerAddress;
	}

}
