package com.elasticpath.service.shoppingcart.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Currency;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.impl.CustomerProfileValueImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.customer.impl.CustomerProfileImpl;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.impl.OrderAddressImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.impl.ShipmentTypeShoppingCartVisitor;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartMementoImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.order.impl.ShoppingItemHasRecurringPricePredicate;
import com.elasticpath.service.shoppingcart.OrderSkuFactory;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.test.factory.TestCustomerProfileFactory;
import com.elasticpath.test.factory.TestShopperFactory;
import com.elasticpath.test.factory.TestShoppingCartFactory;

/**
 * Test that {@link OrderFactoryImpl} behaves as expected.
 */
public class OrderFactoryImplTest {

	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();

	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private final OrderService orderService = context.mock(OrderService.class);
	private final CartOrderService cartOrderService = context.mock(CartOrderService.class);
	private final CustomerService customerService = context.mock(CustomerService.class);
	private final OrderSkuFactory orderSkuFactory = context.mock(OrderSkuFactory.class);

	private final BeanFactoryExpectationsFactory beanExpectations = new BeanFactoryExpectationsFactory(context, beanFactory);

	private OrderFactoryImpl orderFactory;

	/**
	 * Setup required for each test.
	 *
	 * @throws Exception in case of error
	 */
	@Before
	public void setUp() throws Exception {
		orderFactory = new OrderFactoryImpl();
		orderFactory.setBeanFactory(beanFactory);
		orderFactory.setOrderService(orderService);
		orderFactory.setCartOrderService(cartOrderService);
		orderFactory.setOrderSkuFactory(orderSkuFactory);

		final EventOriginatorHelper eventOriginatorHelper = context.mock(EventOriginatorHelper.class);
		context.checking(new Expectations() {
			{
				ignoring(eventOriginatorHelper).getCustomerOriginator(with(any(Customer.class)));
				ignoring(cartOrderService).findByShoppingCartGuid(with(any(String.class))); will(returnValue(null));
				ignoring(orderSkuFactory);
			}
		});

		final ShoppingItemHasRecurringPricePredicate recurringPricePredicate = new ShoppingItemHasRecurringPricePredicate();
		final ShipmentTypeShoppingCartVisitor shoppingCartVisitor = new ShipmentTypeShoppingCartVisitor(recurringPricePredicate);

		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.EVENT_ORIGINATOR_HELPER, eventOriginatorHelper);
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_SERVICE, customerService);
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.SHOPPING_CART_MEMENTO, ShoppingCartMementoImpl.class);
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.ORDER_ADDRESS, OrderAddressImpl.class);
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.SHIPMENT_TYPE_SHOPPING_CART_VISITOR, shoppingCartVisitor);

	}

	@After
	public void tearDown() {
		beanExpectations.close();
	}

	/**
	 * Test that an anonymous customer gets profile information copied from the billing address.
	 */
	@Test
	public void testUpdateAnonymousCustomer() {
		final Customer customer = createCustomer();
		customer.setAnonymous(true);

		final ShoppingCart shoppingCart = createShoppingCart();
		CustomerAddress billingAddress = addBillingAddressToCart(shoppingCart);

		setupOrder();

		context.checking(new Expectations() {
			{
				oneOf(customerService).update(customer); will(returnValue(customer));
			}
		});

		Order newOrder = orderFactory.createAndPersistNewOrderFromShoppingCart(customer, shoppingCart, false, false, null);
		assertEquals("The customer first name should have been set", billingAddress.getFirstName(), newOrder.getCustomer().getFirstName());
		assertEquals("The customer last name should have been set", billingAddress.getLastName(), newOrder.getCustomer().getLastName());
		assertEquals("The customer phone number should have been set", billingAddress.getPhoneNumber(), newOrder.getCustomer().getPhoneNumber());
	}

	/**
	 * Test that existing profile information on an anonymous customer doesn't get overwritten.
	 */
	@Test
	public void testUpdateAnonymousCustomerWithExistingProfile() {
		final Customer customer = createCustomer();
		customer.setAnonymous(true);
		customer.setFirstName("Jean-Luc");
		customer.setLastName("Picard");
		customer.setPhoneNumber("555-639-8436");

		final ShoppingCart shoppingCart = createShoppingCart();
		addBillingAddressToCart(shoppingCart);

		setupOrder();

		context.checking(new Expectations() {
			{
				never(customerService).update(customer);
			}
		});

		Order newOrder = orderFactory.createAndPersistNewOrderFromShoppingCart(customer, shoppingCart, false, false, null);
		assertEquals("The customer first name should have been preserved", "Jean-Luc", newOrder.getCustomer().getFirstName());
		assertEquals("The customer last name should have been preserved", "Picard", newOrder.getCustomer().getLastName());
		assertEquals("The customer phone number should have been preserved", "555-639-8436", newOrder.getCustomer().getPhoneNumber());
	}

	@Test
	public void testUpdateAnonymousCustomerWithNullBillingAddress() {
		final Customer customer = createCustomer();
		customer.setAnonymous(true);
		final ShoppingCart shoppingCart = createShoppingCart();
		setupOrder();

		context.checking(new Expectations() {
			{
				never(customerService).update(customer);
			}
		});

		Order newOrder = orderFactory.createAndPersistNewOrderFromShoppingCart(customer, shoppingCart, false, false, null);
		assertNull("The customer first name should be null", newOrder.getCustomer().getFirstName());
		assertNull("The customer last name should be null", newOrder.getCustomer().getLastName());
		assertNull("The customer phone number should be null", newOrder.getCustomer().getPhoneNumber());
	}

	private Customer createCustomer() {
		final Customer customer = new CustomerImpl();

		final AttributeService attributeService = context.mock(AttributeService.class);

		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_SERVICE, attributeService);
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_PROFILE, CustomerProfileImpl.class);
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_PROFILE_VALUE, CustomerProfileValueImpl.class);

		context.checking(new Expectations() {
			{
				allowing(attributeService).getCustomerProfileAttributesMap(); will(returnValue(new TestCustomerProfileFactory().getProfile()));
			}
		});

		return customer;
	}

	private ShoppingCart createShoppingCart() {
		final Shopper shopper = TestShopperFactory.getInstance().createNewShopperWithMemento();
		final Store store = context.mock(Store.class);
		context.checking(new Expectations() {
			{
				allowing(store).getCode(); will(returnValue("store"));
				allowing(store).getDefaultCurrency(); will(returnValue(Currency.getInstance("CAD")));
				allowing(store).getDefaultLocale(); will(returnValue(Locale.ENGLISH));
			}
		});
		final ShoppingCart shoppingCart = TestShoppingCartFactory.getInstance().createNewCartWithMemento(shopper, store);

		return shoppingCart;
	}

	private CustomerAddress addBillingAddressToCart(final ShoppingCart shoppingCart) {
		final CustomerAddress billingAddress = new CustomerAddressImpl();
		billingAddress.setFirstName("James");
		billingAddress.setLastName("Kirk");
		billingAddress.setPhoneNumber("555-873-5867");
		shoppingCart.setBillingAddress(billingAddress);
		return billingAddress;
	}

	private void setupOrder() {
		final Order order = new OrderImpl();
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.ORDER); will(returnValue(order));
				allowing(orderService).add(order); will(returnValue(order));
			}
		});
	}

}
