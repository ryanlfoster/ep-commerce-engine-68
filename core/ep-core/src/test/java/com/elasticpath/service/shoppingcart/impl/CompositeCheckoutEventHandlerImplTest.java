package com.elasticpath.service.shoppingcart.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.CheckoutEventHandler;

/**
 * Test class for {@link CompositeCheckoutEventHandlerImplTest}.
 */
public class CompositeCheckoutEventHandlerImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private ShoppingCart shoppingCart;
	private OrderPayment orderPayment;
	private Order order;

	private CompositeCheckoutEventHandlerImpl compositeCheckoutEventHandler;

	/**
	 * Test setup.
	 * @throws Exception on failure
	 */
	@Before
	public void setUp() throws Exception {
		order = context.mock(Order.class);
		orderPayment = context.mock(OrderPayment.class);
		shoppingCart = context.mock(ShoppingCart.class);

		compositeCheckoutEventHandler = new CompositeCheckoutEventHandlerImpl();
	}

	/**
	 * Test teardown.
	 * @throws Exception on failure
	 */
	@After
	public void tearDown() throws Exception {
		compositeCheckoutEventHandler = null;
	}

	/**
	 * Test that CheckoutEventHandler getters and setters are immutable.
	 * @throws Exception on failure
	 */
	@Test
	public void testGetAndSetCheckoutEventHandlersIsImmutable() throws Exception {
		final List<CheckoutEventHandler> delegatedHandlers = new ArrayList<CheckoutEventHandler>();
		delegatedHandlers.add(context.mock(CheckoutEventHandler.class));

		compositeCheckoutEventHandler.setCheckoutEventHandlers(delegatedHandlers);

		delegatedHandlers.clear();
		assertEquals("Clearing the original input list should not affect the stored list", 1,
				compositeCheckoutEventHandler.getCheckoutEventHandlers().size());

		compositeCheckoutEventHandler.getCheckoutEventHandlers().clear();
		assertEquals("Clearing the returned list should not affect the stored list", 1,
				compositeCheckoutEventHandler.getCheckoutEventHandlers().size());
	}

	/**
	 * Test empty list of CheckoutEventHandlers.
	 * @throws Exception on failure
	 */
	@Test
	public void testAllOperationsPerformNoOpForUnsetAndEmptyListOfCheckoutEventHandlers()
	throws Exception {
		compositeCheckoutEventHandler.preCheckout(shoppingCart, orderPayment);
		compositeCheckoutEventHandler.preCheckoutOrderPersist(shoppingCart,
				Collections.singletonList(orderPayment), order);
		compositeCheckoutEventHandler.postCheckout(shoppingCart, orderPayment, order);

		compositeCheckoutEventHandler.setCheckoutEventHandlers(Collections
				.<CheckoutEventHandler> emptyList());

		compositeCheckoutEventHandler.preCheckout(shoppingCart, orderPayment);
		compositeCheckoutEventHandler.preCheckoutOrderPersist(shoppingCart,
				Collections.singletonList(orderPayment), order);
		compositeCheckoutEventHandler.postCheckout(shoppingCart, orderPayment, order);
	}

	/**
	 * Test preCheckout delegates.
	 * @throws Exception on failure
	 */
	@Test
	public void testPreCheckoutDelegates() throws Exception {
		final List<CheckoutEventHandler> delegatedHandlers = getMockedCheckoutEventHandlerList();

		context.checking(new Expectations() {

			{
				for (final CheckoutEventHandler checkoutEventHandler : delegatedHandlers) {
					oneOf(checkoutEventHandler).preCheckout(shoppingCart, orderPayment);
				}
			}

		});

		compositeCheckoutEventHandler.setCheckoutEventHandlers(delegatedHandlers);
		compositeCheckoutEventHandler.preCheckout(shoppingCart, orderPayment);
	}

	/**
	 * Test preCheckoutOrderPersist delegates.
	 * @throws Exception on failure
	 */
	@Test
	public void testPreCheckoutOrderPersistDelegates() throws Exception {
		final List<CheckoutEventHandler> delegatedHandlers = getMockedCheckoutEventHandlerList();
		final Collection<OrderPayment> orderPayments = Collections.singletonList(orderPayment);

		context.checking(new Expectations() {

			{
				for (final CheckoutEventHandler checkoutEventHandler : delegatedHandlers) {
					oneOf(checkoutEventHandler).preCheckoutOrderPersist(shoppingCart, orderPayments, order);
				}
			}

		});

		compositeCheckoutEventHandler.setCheckoutEventHandlers(delegatedHandlers);
		compositeCheckoutEventHandler.preCheckoutOrderPersist(shoppingCart, orderPayments, order);
	}

	/**
	 * Test postCheckout delegates.
	 * @throws Exception on failure
	 */
	@Test
	public void testPostCheckoutDelegates() throws Exception {
		final List<CheckoutEventHandler> delegatedHandlers = getMockedCheckoutEventHandlerList();

		context.checking(new Expectations() {

			{
				for (final CheckoutEventHandler checkoutEventHandler : delegatedHandlers) {
					oneOf(checkoutEventHandler).postCheckout(shoppingCart, orderPayment, order);
				}
			}

		});

		compositeCheckoutEventHandler.setCheckoutEventHandlers(delegatedHandlers);
		compositeCheckoutEventHandler.postCheckout(shoppingCart, orderPayment, order);
	}

	private List<CheckoutEventHandler> getMockedCheckoutEventHandlerList() {
		final CheckoutEventHandler checkoutEventHandler1 = context.mock(CheckoutEventHandler.class,
				"checkoutEventHandler1");
		final CheckoutEventHandler checkoutEventHandler2 = context.mock(CheckoutEventHandler.class,
		"checkoutEventHandler2");

		final List<CheckoutEventHandler> mockedHandlers = new ArrayList<CheckoutEventHandler>();
		mockedHandlers.add(checkoutEventHandler1);
		mockedHandlers.add(checkoutEventHandler2);

		return mockedHandlers;
	}
}