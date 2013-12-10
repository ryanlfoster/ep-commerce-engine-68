package com.elasticpath.service.cartorder.impl;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.cartorder.CartOrderPopulationStrategy;
import com.elasticpath.service.cartorder.CartOrderShippingInformationSanitizer;
import com.elasticpath.service.cartorder.dao.CartOrderDao;
import com.elasticpath.service.customer.dao.CustomerAddressDao;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * New JUnit4 tests for {@code CartOrderServiceImplTest}.
 */
public class CartOrderServiceImplTest {

	private static final String NAMED_QUERY_STORE_CODE_BY_CART_ORDER_GUID = "STORE_CODE_BY_CART_ORDER_GUID";
	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";
	private static final String STORE_CODE = "storeCode";
	private static final int SHIPPING_SERVICE_LEVEL_UID = 999;
	private static final String SHIPPING_SERVICE_LEVEL_GUID = "shippingServiceLevelGuid";
	private static final String SHIPPING_ADDRESS_GUID = "shippingAddressGuid";
	private static final String BILLING_ADDRESS_GUID = "billingAddressGuid";
	private static final String CART_GUID = "CART_GUID";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private CartOrderServiceImpl cartOrderService;
	private CartOrderPopulationStrategy strategy;
	private CartOrderDao cartOrderDao;
	private CartOrder cartOrder;
	private ShoppingCart shoppingCart;
	private ShoppingCartService shoppingCartService;
	private ShippingServiceLevelService shippingServiceLevelService;
	private CartOrderShippingInformationSanitizer cartOrderShippingInformationSanitizer;
	private ShippingServiceLevel expectedShippingServiceLevel;
	private Address billingAddress;
	private Address shippingAddress;
	private CustomerAddressDao addressDao;
	private Store store;
	private PersistenceEngine persistenceEngine;

	/**
	 * Mock required services and objects.
	 */
	@Before
	public void mockRequiredServicesAndObjects() {
		cartOrderService = new CartOrderServiceImpl();
		expectedShippingServiceLevel = new ShippingServiceLevelImpl();
		strategy = context.mock(CartOrderPopulationStrategy.class);
		cartOrderDao = context.mock(CartOrderDao.class);
		addressDao = context.mock(CustomerAddressDao.class);
		cartOrder = context.mock(CartOrder.class);
		shoppingCart = context.mock(ShoppingCart.class);
		billingAddress = context.mock(Address.class, "billingAddress");
		shippingAddress = context.mock(Address.class, "shippingAddress");
		shoppingCartService = context.mock(ShoppingCartService.class);
		shippingServiceLevelService = context.mock(ShippingServiceLevelService.class);
		cartOrderShippingInformationSanitizer = context.mock(CartOrderShippingInformationSanitizer.class);
		store = context.mock(Store.class);
		persistenceEngine = context.mock(PersistenceEngine.class);
		cartOrderService.setCartOrderPopulationStrategy(strategy);
		cartOrderService.setCartOrderDao(cartOrderDao);
		cartOrderService.setCustomerAddressDao(addressDao);
		cartOrderService.setShoppingCartService(shoppingCartService);
		cartOrderService.setShippingServiceLevelService(shippingServiceLevelService);
		cartOrderService.setCartOrderShippingInformationSanitizer(cartOrderShippingInformationSanitizer);
		cartOrderService.setPersistenceEngine(persistenceEngine);
	}

	/**
	 * Test expected response when a cart order does not exist.
	 */
	@Test
	public void testCreateIfNotExists() {
		context.checking(new Expectations() {
			{
				allowing(cartOrder).getShoppingCartGuid();
				will(returnValue(null));
				allowing(shoppingCartService).findByGuid(null);
				will(returnValue(null));
				oneOf(cartOrderDao).findByShoppingCartGuid(CART_GUID);
				will(returnValue(null));
				oneOf(strategy).createCartOrder(CART_GUID);
				will(returnValue(cartOrder));
				oneOf(cartOrderDao).saveOrUpdate(cartOrder);
			}
		});

		boolean response = cartOrderService.createIfNotExists(CART_GUID);
		assertTrue("Expect that cart order did not exist.", response);
	}

	/**
	 * Test expected response when a cart order exists.
	 */
	@Test
	public void testCartOrderExistsOnCreateIfNotExists() {
		context.checking(new Expectations() {
			{
				oneOf(cartOrderDao).findByShoppingCartGuid(CART_GUID);
				will(returnValue(cartOrder));
			}
		});

		boolean response = cartOrderService.createIfNotExists(CART_GUID);
		assertFalse("Expect that cart order did exist.", response);
	}

	/**
	 * Test population of shopping cart with valid address and shipping transient fields.
	 */
	@Test
	public void testPopulateAddressAndShippingFields() {
		expectedShippingServiceLevel.setGuid(SHIPPING_SERVICE_LEVEL_GUID);
		expectedShippingServiceLevel.setUidPk(SHIPPING_SERVICE_LEVEL_UID);
		final List<ShippingServiceLevel> shippingServiceLevels = new ArrayList<ShippingServiceLevel>();
		shippingServiceLevels.add(expectedShippingServiceLevel);

		context.checking(new Expectations() {
			{
				oneOf(shippingServiceLevelService).retrieveShippingServiceLevel(STORE_CODE, shippingAddress);
				will(returnValue(shippingServiceLevels));

				oneOf(cartOrder).getBillingAddressGuid();
				will(returnValue(BILLING_ADDRESS_GUID));

				oneOf(cartOrder).getShippingAddressGuid();
				will(returnValue(SHIPPING_ADDRESS_GUID));

				oneOf(cartOrder).getShippingServiceLevelGuid();
				will(returnValue(SHIPPING_SERVICE_LEVEL_GUID));

				oneOf(addressDao).findByGuid(BILLING_ADDRESS_GUID);
				will(returnValue(billingAddress));

				oneOf(addressDao).findByGuid(SHIPPING_ADDRESS_GUID);
				will(returnValue(shippingAddress));

				oneOf(shoppingCart).setBillingAddress(billingAddress);
				oneOf(shoppingCart).setShippingAddress(shippingAddress);
				never(shoppingCart).calculateShoppingCartTaxAndBeforeTaxPrices();
				oneOf(shoppingCart).setSelectedShippingServiceLevelUid(SHIPPING_SERVICE_LEVEL_UID);
				oneOf(shoppingCart).setShippingServiceLevelList(shippingServiceLevels);
				oneOf(shoppingCart).getStore();
				will(returnValue(store));

				oneOf(store).getCode();
				will(returnValue(STORE_CODE));
			}
		});

		cartOrderService.populateAddressAndShippingFields(shoppingCart, cartOrder);
	}

	/**
	 * Test population of shopping cart with null shipping address.
	 */
	@Test
	public void testPopulationOfShoppingCartWithNullShippingAddress() {
		expectedShippingServiceLevel.setGuid(SHIPPING_SERVICE_LEVEL_GUID);
		expectedShippingServiceLevel.setUidPk(SHIPPING_SERVICE_LEVEL_UID);
		final List<ShippingServiceLevel> shippingServiceLevels = new ArrayList<ShippingServiceLevel>();
		shippingServiceLevels.add(expectedShippingServiceLevel);

		context.checking(new Expectations() {
			{
				oneOf(shippingServiceLevelService).retrieveShippingServiceLevel(STORE_CODE, null);
				will(returnValue(Collections.emptyList()));

				oneOf(cartOrder).getBillingAddressGuid();
				will(returnValue(BILLING_ADDRESS_GUID));

				oneOf(cartOrder).getShippingAddressGuid();
				will(returnValue("nonexistent-guid"));

				oneOf(addressDao).findByGuid(BILLING_ADDRESS_GUID);
				will(returnValue(billingAddress));

				oneOf(addressDao).findByGuid("nonexistent-guid");
				will(returnValue(null));

				oneOf(shoppingCart).setBillingAddress(billingAddress);
				oneOf(shoppingCart).setShippingAddress(null);
				never(shoppingCart).calculateShoppingCartTaxAndBeforeTaxPrices();
				never(shoppingCart).setSelectedShippingServiceLevelUid(with(any(Long.class)));
				oneOf(shoppingCart).getStore();
				will(returnValue(store));

				oneOf(store).getCode();
				will(returnValue(STORE_CODE));
			}
		});

		cartOrderService.populateAddressAndShippingFields(shoppingCart, cartOrder);

	}
	
	/**
	 * Ensure sanitation of retrieved cart order.
	 */
	@Test
	public void ensureSanitationAndPersistenceOfRetrievedCartOrder() {
		context.checking(new Expectations() {
			{
				oneOf(cartOrderDao).findByGuid(CART_ORDER_GUID);
				will(returnValue(cartOrder));
				oneOf(cartOrder).getGuid();
				will(returnValue(CART_ORDER_GUID));
				oneOf(cartOrderShippingInformationSanitizer).sanitize(STORE_CODE, cartOrder);
				will(returnValue(true));
				oneOf(cartOrderDao).saveOrUpdate(cartOrder);
				oneOf(persistenceEngine).retrieveByNamedQuery(with(NAMED_QUERY_STORE_CODE_BY_CART_ORDER_GUID), with(any(Object[].class)));
				will(returnValue(Arrays.asList(STORE_CODE)));
			}
		});
		
		cartOrderService.findByGuid(CART_ORDER_GUID);
	}
	
	/**
	 * Ensure sanitation of retrieved cart order.
	 */
	@Test
	public void ensureSanitationAndNoPersistenceOfUnchangedRetrievedCartOrder() {
		context.checking(new Expectations() {
			{
				oneOf(cartOrder).getGuid();
				will(returnValue(CART_ORDER_GUID));
				oneOf(cartOrderDao).findByGuid(CART_ORDER_GUID);
				will(returnValue(cartOrder));
				oneOf(cartOrderShippingInformationSanitizer).sanitize(STORE_CODE, cartOrder);
				will(returnValue(false));
				oneOf(persistenceEngine).retrieveByNamedQuery(with(NAMED_QUERY_STORE_CODE_BY_CART_ORDER_GUID), with(any(Object[].class)));
				will(returnValue(Arrays.asList(STORE_CODE)));
			}
		});
		
		cartOrderService.findByGuid(CART_ORDER_GUID);
	}
	
	/**
	 * Ensure non existent cart order not sanitized.
	 */
	@Test
	public void ensureNonExistentCartOrderNotSanitized() {
		context.checking(new Expectations() {
			{
				oneOf(cartOrderDao).findByGuid(CART_ORDER_GUID);
				will(returnValue(null));
				never(cartOrderShippingInformationSanitizer).sanitize(STORE_CODE, cartOrder);
			}
		});
		
		cartOrderService.findByGuid(CART_ORDER_GUID);
	}
}
