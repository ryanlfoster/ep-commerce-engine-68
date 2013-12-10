/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.FinalizeCheckoutActionContext;

/**
 * Test for {@link ClearShoppingCartCheckoutAction}.
 */
public class ClearShoppingCartCheckoutActionTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final CartOrderService cartOrderService = context.mock(CartOrderService.class);
	private final ShoppingCartService shoppingCartService = context.mock(ShoppingCartService.class);
	private ClearShoppingCartCheckoutAction clearShoppingCartCheckoutAction;
	private FinalizeCheckoutActionContext checkoutContext;
	private ShoppingCart shoppingCart;   
	
	/**
	 * Initialize object under test.
	 */
	@Before
	public void setUp() {
		clearShoppingCartCheckoutAction = new ClearShoppingCartCheckoutAction();
		clearShoppingCartCheckoutAction.setCartOrderService(cartOrderService);
		clearShoppingCartCheckoutAction.setShoppingCartService(shoppingCartService);
		shoppingCart = context.mock(ShoppingCart.class);
	}

	/**
	 * Test checkout action on order exchange.
	 */
	@Test
	public void testCheckoutActionOnOrderExchange() {
		shouldHaveCheckoutContextAsOrderExchange();
		context.checking(new Expectations() {
			{
				oneOf(shoppingCart).clearItems();
				oneOf(cartOrderService).removeIfExistsByShoppingCart(shoppingCart);
			}
		});
		clearShoppingCartCheckoutAction.execute(checkoutContext);
	}

	/**
	 * Test basic checkout action.
	 */
	@Test
	public void testDefaultCheckoutAction() {
		shouldHaveDefaultCheckoutContext();
		context.checking(new Expectations() {
			{
				oneOf(shoppingCart).clearItems();
				oneOf(shoppingCartService).saveOrUpdate(shoppingCart);
				will(returnValue(shoppingCart));
				oneOf(cartOrderService).removeIfExistsByShoppingCart(shoppingCart);
			}
		});
		clearShoppingCartCheckoutAction.execute(checkoutContext);
	}
	
	
	private void shouldHaveCheckoutContextAsOrderExchange() {
		createCheckoutContext(true);
	}

	private void shouldHaveDefaultCheckoutContext() {
		createCheckoutContext(false);
	}

	private void createCheckoutContext(final boolean isOrderExchange) {
		CheckoutActionContext checkoutActionContext = new CheckoutActionContextImpl(shoppingCart, null, isOrderExchange, false, null);
		checkoutContext = new FinalizeCheckoutActionContextImpl(checkoutActionContext);
	}
}
