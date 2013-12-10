package com.elasticpath.service.shoppingcart.actions.impl;

import static org.junit.Assert.assertTrue;


import java.util.Currency;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.customer.impl.CustomerProfileImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.store.StoreService;

public class PopulateTemplateOrderPaymentCheckoutActionTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	protected StoreService storeService;

	private StoreImpl store;
	private PopulateTemplateOrderPaymentCheckoutAction checkoutAction;
	private CheckoutActionContextImpl checkoutActionContext;
	private OrderPaymentImpl orderPayment;
	private ShoppingCartImpl cart;
	private OrderImpl order;
	private PhysicalOrderShipmentImpl shipment;
	private CustomerImpl customer;

	@Before
	public void setUp() throws Exception {
		store = new StoreImpl();
		store.setCode("store");

		context.checking(new Expectations() { {
			allowing(storeService).findStoreWithCode(store.getCode()); will(returnValue(store));
		} });

		customer = new CustomerImpl();
		customer.setCustomerProfile(new CustomerProfileImpl());
		cart = new ShoppingCartImpl();

		order = new OrderImpl();
		order.setOrderNumber("123");
		order.setStoreCode(store.getCode());
		order.setCustomer(customer);
		order.setCurrency(Currency.getInstance("CAD"));
		shipment = new PhysicalOrderShipmentImpl();
		shipment.setOrder(order);
		orderPayment = new OrderPaymentImpl();
		orderPayment.setOrderShipment(shipment);

		checkoutActionContext = new CheckoutActionContextImpl(cart, orderPayment, false, false, null);
		checkoutActionContext.setOrder(order);

		checkoutAction = new PopulateTemplateOrderPaymentCheckoutAction();
		checkoutAction.setStoreService(storeService);
	}

	@Test
	public void testExecute() throws Exception {
		store.setStoreFullCreditCardsEnabled(true);

		checkoutAction.execute(checkoutActionContext);

		assertTrue("Credit card settings should be retrieved from store", orderPayment.isShouldStoreEncryptedCreditCard());
	}
}
