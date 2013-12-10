package com.elasticpath.service.cartorder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerPaymentMethods;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerCreditCardImpl;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.ShoppingCartService;

/**
 * New JUnit4 tests for {@code CartOrderPopulationStrategyImplTest}.
 */
public class CartOrderPopulationOnCustomerProfileStrategyImplTest {
	private static final String CUSTOMER_ADDRESS_GUID = "CUSTOMER_ADDRESS_GUID";

	private static final String CART_GUID = "CART_GUID";

	private static final String CREDIT_CARD_GUID = "CREDIT_CARD_GUID";

	private final CartOrderPopulationOnCustomerProfileStrategyImpl strategy = new CartOrderPopulationOnCustomerProfileStrategyImpl();

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final BeanFactory prototypeBeanFactory = context.mock(BeanFactory.class);

	private final ShoppingCartService shoppingCartService = context.mock(ShoppingCartService.class);

	private final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);

	private final Shopper shopper = context.mock(Shopper.class);

	private final Customer customer = context.mock(Customer.class);

	private final CustomerAddress customerAddress = new CustomerAddressImpl();

	/**
	 * Setup.
	 */
	@Before
	public void setUp() {
		strategy.setPrototypeBeanFactory(prototypeBeanFactory);
		strategy.setShoppingCartService(shoppingCartService);

		customerAddress.setGuid(CUSTOMER_ADDRESS_GUID);

		context.checking(new Expectations() {
			{
				oneOf(prototypeBeanFactory).getBean(ContextIdNames.CART_ORDER);
				will(returnValue(new CartOrderImpl()));
				oneOf(shoppingCartService).findByGuid(CART_GUID);
				will(returnValue(shoppingCart));
				oneOf(shoppingCart).getShopper();
				will(returnValue(shopper));
				oneOf(shopper).getCustomer();
				will(returnValue(customer));
			}
		});
	}

	/**
	 * Test happy path to create a cart order with default billing address GUID and credit card GIUD.
	 */
	@Test
	public void testCreateCartOrder() {
		shouldContainBillingAddress(customerAddress);
		shouldContainShippingAddress(customerAddress);
		CustomerCreditCard defaultCreditCard = createDefaultCreditCard();
		shouldHaveDefaultCreditCard(defaultCreditCard);
		shouldHaveDefaultPaymentToken(null);

		CartOrder result = strategy.createCartOrder(CART_GUID);
		assertEquals("Customer address GUIDs should be equal.", CUSTOMER_ADDRESS_GUID, result.getBillingAddressGuid());
		assertEquals("Persisted payment method should be the customer's default payment method.", defaultCreditCard,
				result.getPaymentMethod());
	}

	/**
	 * Test create cart order without any credit cards listed for the customer.
	 */
	@Test
	public void testCreateCartOrderWithNoCreditCards() {
		shouldContainBillingAddress(customerAddress);
		shouldContainShippingAddress(customerAddress);
		shouldHaveDefaultCreditCard(null);
		shouldHaveDefaultPaymentToken(null);

		CartOrder result = strategy.createCartOrder(CART_GUID);
		assertEquals("Customer address GUIDs should be equal.", CUSTOMER_ADDRESS_GUID, result.getBillingAddressGuid());
		assertNull("The persisted payment method should be null.", result.getPaymentMethod());
	}

	/**
	 * Test create cart order without default billing address.
	 */
	@Test
	public void testCreateCartOrderWithoutDefaultBillingAddress() {
		shouldContainBillingAddress(null);
		shouldContainShippingAddress(customerAddress);
		CustomerCreditCard defaultCreditCard = createDefaultCreditCard();
		shouldHaveDefaultCreditCard(defaultCreditCard);
		shouldHaveDefaultPaymentToken(null);

		CartOrder result = strategy.createCartOrder(CART_GUID);
		assertNull("Customer billing address GUID should be null.", result.getBillingAddressGuid());
		assertEquals("Persisted payment method should be the customer's default payment method.", defaultCreditCard,
				result.getPaymentMethod());
	}

	/**
	 * Test create cart order without default shipping address.
	 */
	@Test
	public void testCreateCartOrderWithoutDefaultShippingAddress() {
		shouldContainBillingAddress(customerAddress);
		shouldContainShippingAddress(null);
		CustomerCreditCard defaultCreditCard = createDefaultCreditCard();
		shouldHaveDefaultCreditCard(defaultCreditCard);
		shouldHaveDefaultPaymentToken(null);

		CartOrder result = strategy.createCartOrder(CART_GUID);
		assertNull("Customer shipping address GUID should be null.", result.getShippingAddressGuid());
		assertEquals("Persisted payment method should be the customer's default payment method.", defaultCreditCard,
				result.getPaymentMethod());
	}
	
	/**
	 * Test create cart order with payment token as default.
	 */
	@Test
	public void testCreateCartOrderWithPaymentTokenAsDefault() {
		shouldContainBillingAddress(customerAddress);
		shouldContainShippingAddress(customerAddress);
		shouldHaveDefaultCreditCard(null);
		PaymentToken defaultToken = new PaymentTokenImpl.TokenBuilder().build();
		shouldHaveDefaultPaymentToken(defaultToken);
		CartOrder result = strategy.createCartOrder(CART_GUID);
		assertEquals("Customer address GUIDs should be equal.", CUSTOMER_ADDRESS_GUID, result.getBillingAddressGuid());
		assertEquals("Persisted payment method should be the customer's payment token.", defaultToken,	result.getPaymentMethod());
	}

	private void shouldContainBillingAddress(final CustomerAddress customerAddress) {
		context.checking(new Expectations() {
			{
				oneOf(customer).getPreferredBillingAddress();
				will(returnValue(customerAddress));
			}
		});
	}

	private void shouldContainShippingAddress(final CustomerAddress customerAddress) {
		context.checking(new Expectations() {
			{
				oneOf(customer).getPreferredShippingAddress();
				will(returnValue(customerAddress));
			}
		});
	}

	private void shouldHaveDefaultCreditCard(final CustomerCreditCard defaultCard) {
		context.checking(new Expectations() {
			{
				allowing(customer).getPreferredCreditCard();
				will(returnValue(defaultCard));
			}
		});
	}
	
	private CustomerCreditCard createDefaultCreditCard() {
		final CustomerCreditCard creditCard = new CustomerCreditCardImpl();
		creditCard.setDefaultCard(true);
		creditCard.setGuid(CREDIT_CARD_GUID);
		return creditCard;
	}
	
	
	private void shouldHaveDefaultPaymentToken(final PaymentToken token) {
		final CustomerPaymentMethods customerPaymentMethods = context.mock(CustomerPaymentMethods.class);
		context.checking(new Expectations() {
			{
				atLeast(1).of(customer).getPaymentMethods();
				will(returnValue(customerPaymentMethods));
				atLeast(1).of(customerPaymentMethods).getDefault();
				will(returnValue(token));
			}
		});
	}
}
