package com.elasticpath.service.shoppingcart.actions.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.order.impl.OrderReturnImpl;
import com.elasticpath.domain.shopper.impl.ShopperImpl;
import com.elasticpath.domain.shopper.impl.ShopperMementoImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.impl.CartItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;

public class PopulateOrderDataCheckoutActionTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
	private final ShopperImpl shopper = new ShopperImpl();
	private final Customer customer = context.mock(Customer.class);
	private final ProductSku sku = new ProductSkuImpl();
	private final OrderReturnImpl exchange = new OrderReturnImpl();
	private final CartItem item = new ShoppingItemImpl();
	private final Order order = new OrderImpl();
	private final OrderPaymentImpl orderPayment = new OrderPaymentImpl();

	private PopulateOrderDataCheckoutAction checkoutAction;
	private CheckoutActionContextImpl checkoutContext;

	@Before
	public void setUp() throws Exception {
		sku.setSkuCode("SKU");
		item.setProductSku(sku);
		shopper.setShopperMemento(new ShopperMementoImpl());
		shopper.setCustomer(customer);
		shopper.getCache().putItem("FOO", "BAR");

		context.checking(new Expectations() { {
			allowing(customer).getGender(); will(returnValue('F'));
			allowing(shoppingCart).getShopper(); will(returnValue(shopper));
			allowing(shoppingCart).getCartItems(); will(returnValue(Collections.singletonList(item)));
		} });

		final boolean isOrderExchange = false;
		final boolean awaitExchangeCompletion = false;
		checkoutContext = new CheckoutActionContextImpl(shoppingCart, orderPayment, isOrderExchange, awaitExchangeCompletion, exchange);
		checkoutContext.setOrder(order);

		checkoutAction = new PopulateOrderDataCheckoutAction();
	}

	@Test
	public void verifyExecuteHappyPathWithSimpleIndexedAndMappedProperties() throws Exception {
		// Given
		checkoutAction.setOrderDataProperties(createSampleOrderDataProperties());

		// When
		checkoutAction.execute(checkoutContext);

		// Then
		Map<String, String> orderData = order.getFieldValues();
		assertEquals("Simple properties should be copied into OrderData", "F", orderData.get("gender"));
		assertEquals("Indexed properties should be copied into OrderData", "SKU", orderData.get("skuCode"));
		assertEquals("Mapped properties should be copied into OrderData", "BAR", orderData.get("foo"));
	}

	@Test
	public void verifyExecuteWithNullValuedProperty() throws Exception {
		// Given
		checkoutAction.setOrderDataProperties(Collections.singletonMap("missing", "shoppingCart.shopper.cache.item(MISSING)"));

		// When
		checkoutAction.execute(checkoutContext);

		// Then
		Map<String, String> orderData = order.getFieldValues();
		assertFalse("Field should not exist in map", orderData.containsKey("missing"));
	}

	@Test(expected = EpServiceException.class)
	public void verifyExecutePukesIfUnknownPropertyIsSpecified() throws Exception {
		// Given
		checkoutAction.setOrderDataProperties(Collections.singletonMap("unknown", "shoppingCart.idonthavethisproperty"));

		// When
		checkoutAction.execute(checkoutContext);

		// Then - an exception should be thrown
	}

	@Test
	public void verifyExecuteWithAMapImportsAllTheMapPropertiesIntoTheOrderData() {
		// Given
		final Map<String, Long> limitedUsagePromotionRuleCodes = new HashMap<String, Long>();
		limitedUsagePromotionRuleCodes.put("foo", 1L);
		limitedUsagePromotionRuleCodes.put("bar", 2L);

		checkoutAction.setOrderDataProperties(Collections.singletonMap("ruleCodes", "shoppingCart.limitedUsagePromotionRuleCodes"));

		// Expectations
		context.checking(new Expectations() { {
			allowing(shoppingCart).getLimitedUsagePromotionRuleCodes();
			will(returnValue(limitedUsagePromotionRuleCodes));
		} });

		// When
		checkoutAction.execute(checkoutContext);

		// Then
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("ruleCodes.foo", "1");
		expected.put("ruleCodes.bar", "2");
		assertEquals("Should have copied the values from the limitedUsagePromotionRuleCodes map into the order data",
				expected, order.getFieldValues());
	}

	@Test
	public void testRollback() throws Exception {
		// Given
		checkoutAction.setOrderDataProperties(createSampleOrderDataProperties());

		// When
		checkoutAction.execute(checkoutContext);
		checkoutAction.rollback(checkoutContext);

		// Then
		Map<String, String> orderData = order.getFieldValues();
		assertEquals("Order Data should have been rolled back", Collections.emptyMap(), orderData);
	}

	private Map<String, String> createSampleOrderDataProperties() {
		Map<String, String> orderDataProperties = new HashMap<String, String>();
		orderDataProperties.put("gender",  "shoppingCart.shopper.customer.gender");           // Simple Property
		orderDataProperties.put("skuCode", "shoppingCart.cartItems[0].productSku.skuCode");   // Indexed Property
		orderDataProperties.put("foo",     "shoppingCart.shopper.cache.item(FOO)");           // Mapped Property

		return orderDataProperties;
	}

}
