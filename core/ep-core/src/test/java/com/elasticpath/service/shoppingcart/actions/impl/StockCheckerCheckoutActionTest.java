package com.elasticpath.service.shoppingcart.actions.impl;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.ShoppingCartEmptyException;

/**
 * Test class for {@link StockCheckerCheckoutAction}.
 */
public class StockCheckerCheckoutActionTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private StockCheckerCheckoutAction checkoutAction;

	private ShoppingCart shoppingCart;

	@Before
	public void setUp() throws Exception {
		shoppingCart = context.mock(ShoppingCart.class);
		context.checking(new Expectations() {
			{
				allowing(shoppingCart).getGuid();
				will(returnValue("SHOPCART1"));
			}
		});

		checkoutAction = new StockCheckerCheckoutAction();
	}


	/**
	 * Verifies an exception is thrown when the Shopping Cart is empty.
	 */
	@Test(expected = ShoppingCartEmptyException.class)
	public void testExecuteThrowsExceptionWhenShoppingCartEmpty() throws Exception {
		context.checking(new Expectations() {
			{
				allowing(shoppingCart).getAllItems();
				will(returnValue(Collections.emptyList()));
				allowing(shoppingCart).getCartItems();
				will(returnValue(Collections.emptyList()));
				allowing(shoppingCart).getNumItems();
				will(returnValue(0));
			}
		});

		checkoutAction.execute(new CheckoutActionContextImpl(shoppingCart, null, false, false, null));
	}

}