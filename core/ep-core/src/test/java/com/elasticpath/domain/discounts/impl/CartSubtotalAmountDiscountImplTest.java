package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Test cases for <code>CartSubtotalAmountDiscountImpl</code>.
 */
public class CartSubtotalAmountDiscountImplTest {
	
	private static final long RULE_ID = 123L;
	private static final long ACTION_ID = 345L;
	
	private static final BigDecimal TWENTY = BigDecimal.valueOf(20);
	
	private static final BigDecimal FORTY = BigDecimal.valueOf(40);
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private DiscountItemContainer createSubtotalDiscountItemContainer(final BigDecimal amount, final boolean actuallyApply, 
			final boolean empty) {
		final DiscountItemContainer container = context.mock(DiscountItemContainer.class);
		List<ShoppingItem> siList = null;
		if (!empty) {
			siList = Arrays.asList(context.mock(ShoppingItem.class));
		}
		final List<ShoppingItem> siList2 = siList;
		context.checking(new Expectations() { {
			if (actuallyApply) {				
				oneOf(container).applySubtotalDiscount(amount, RULE_ID, ACTION_ID);
			}
			allowing(container).getItemsLowestToHighestPrice(); will(returnValue(siList2));
		} });
		return container;
	}
	
	/**
	 * Test case for apply() with empty cart.
	 */
	@Test
	public void testCalculateOnSubtotalAmountDiscount() {
		final DiscountItemContainer container = createSubtotalDiscountItemContainer(FORTY, false, false);
		
		CartSubtotalAmountDiscountImpl discount = new CartSubtotalAmountDiscountImpl("", RULE_ID, ACTION_ID, "40");

		BigDecimal total = discount.calculate(container);
		Assert.assertEquals(FORTY, total);
	}

	/**
	 * Test case for apply() with empty cart.
	 */
	@Test
	public void testApplyOnSubtotalAmountDiscount() {

		final DiscountItemContainer container = createSubtotalDiscountItemContainer(TWENTY, true, false);
		
		CartSubtotalAmountDiscountImpl discount = new CartSubtotalAmountDiscountImpl("", RULE_ID, ACTION_ID, "20");

		BigDecimal total = discount.apply(container);
		Assert.assertEquals(TWENTY, total);
	}

	/**
	 * Test case for apply() with 1 cart item.
	 */
	@Test
	public void testApplyOnSubtotalAmountDiscountZero() {

		final DiscountItemContainer container = createSubtotalDiscountItemContainer(BigDecimal.ZERO, true, false);
		
		CartSubtotalAmountDiscountImpl discount = new CartSubtotalAmountDiscountImpl("", RULE_ID, ACTION_ID, "0");

		BigDecimal total = discount.apply(container);
		Assert.assertEquals(BigDecimal.ZERO, total);
	}

	/**
	 * Test case for apply() with 1 cart item, when the cart item cannot receive the promotion.
	 */
	@Test
	public void testApplyOnSubtotalAmountNonDiscountable() {
		
		final DiscountItemContainer container = createSubtotalDiscountItemContainer(BigDecimal.TEN, false, true);
		
		CartSubtotalAmountDiscountImpl discount = new CartSubtotalAmountDiscountImpl("", RULE_ID, ACTION_ID, "0");
		
		BigDecimal total = discount.apply(container);
		Assert.assertEquals(BigDecimal.ZERO, total);
	}
}
