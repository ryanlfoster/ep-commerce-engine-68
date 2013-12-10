package com.elasticpath.service.shoppingcart.actions.impl;

import java.util.Collections;
import java.util.Currency;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.customer.impl.CustomerProfileImpl;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartMementoImpl;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

public class SubscriptionCreditCheckCheckoutActionTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	protected StoreService storeService;
	@Mock
	protected BeanFactory beanFactory;
	@Mock
	protected PaymentGateway gateway;

	private StoreImpl store;
	private SubscriptionCreditCheckCheckoutAction checkoutAction;
	private CheckoutActionContextImpl checkoutActionContext;
	private OrderPaymentImpl orderPayment;
	private ShoppingCartImpl cart;
	private OrderImpl order;
	private PhysicalOrderShipmentImpl shipment;
	private CustomerImpl customer;
	private BeanFactoryExpectationsFactory bfef;

	@Before
	public void setUp() throws Exception {
		bfef = new BeanFactoryExpectationsFactory(context, beanFactory);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.ORDER_PAYMENT, OrderPaymentImpl.class);

		store = new StoreImpl();
		store.setCode("store");
		store.setPaymentGateways(Collections.<PaymentGateway>singleton(gateway));

		context.checking(new Expectations() { {
			allowing(gateway).getPaymentType(); will(returnValue(PaymentType.GIFT_CERTIFICATE));
			allowing(storeService).findStoreWithCode(store.getCode()); will(returnValue(store));
		} });

		customer = new CustomerImpl();
		customer.setCustomerProfile(new CustomerProfileImpl());
		cart = new MockRecurringShoppingCart();
		cart.setShoppingCartMemento(new ShoppingCartMementoImpl());
		cart.setStore(store);

		order = new OrderImpl();
		order.setOrderNumber("123");
		order.setStoreCode(store.getCode());
		order.setCustomer(customer);
		order.setCurrency(Currency.getInstance("CAD"));
		shipment = new PhysicalOrderShipmentImpl();
		shipment.setOrder(order);
		orderPayment = new OrderPaymentImpl();
		orderPayment.setOrderShipment(shipment);
		orderPayment.setPaymentMethod(PaymentType.GIFT_CERTIFICATE);

		checkoutActionContext = new CheckoutActionContextImpl(cart, orderPayment, false, false, null);
		checkoutActionContext.setOrder(order);

		checkoutAction = new SubscriptionCreditCheckCheckoutAction();
		checkoutAction.setBeanFactory(beanFactory);
		checkoutAction.setStoreService(storeService);
	}

	@Test
	public void testExecuteCompletesWhenOrderStoreIsNull() throws Exception {
		TaxCalculationResult taxResult = new TaxCalculationResultImpl();
		taxResult.setDefaultCurrency(Currency.getInstance("CAD"));
		cart.setTaxCalculationResult(taxResult);

		context.checking(new Expectations() { {
			oneOf(gateway).preAuthorize(with(any(OrderPayment.class)), with(aNull(Address.class)));
			oneOf(gateway).reversePreAuthorization(with(any(OrderPayment.class)));
		} });
		checkoutAction.execute(checkoutActionContext);
	}

	private static class MockRecurringShoppingCart extends ShoppingCartImpl {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean hasRecurringPricedShoppingItems() {
			return true;
		}
	}
}
