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
 * Test cases for <code>CartSubtotalPercentDiscountImpl</code>.
 */
public class CartSubtotalPercentDiscountImplTest {
	
	private static final long RULE_ID = 123L;
	
	private static final long ACTION_ID = 456L;
	
	private static final BigDecimal TWENTY = new BigDecimal("20.00");
	
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
			ignoring(container).calculateSubtotal(); will(returnValue(FORTY));

		} });
		return container;
	}
	
	/**
	 * Test case for apply() with empty cart.
	 */
	@Test
	public void testCalculateOnSubtotalAmountDiscount() {
		final DiscountItemContainer container = createSubtotalDiscountItemContainer(TWENTY, false, false);
		
		CartSubtotalPercentDiscountImpl discount = new CartSubtotalPercentDiscountImpl("", RULE_ID, ACTION_ID, "50");

		BigDecimal total = discount.calculate(container);
		Assert.assertEquals(TWENTY, total);
	}

	/**
	 * Test case for apply() with empty cart.
	 */
	@Test
	public void testApplyOnSubtotalAmountDiscount() {

		final DiscountItemContainer container = createSubtotalDiscountItemContainer(BigDecimal.TEN.setScale(2), true, false);
		
		CartSubtotalPercentDiscountImpl discount = new CartSubtotalPercentDiscountImpl("", RULE_ID, ACTION_ID, "25");

		BigDecimal total = discount.apply(container);
		Assert.assertEquals(BigDecimal.TEN.setScale(2), total);
	}

	/**
	 * Test case for apply() with 1 cart item.
	 */
	@Test
	public void testApplyOnSubtotalAmountDiscountZero() {

		final DiscountItemContainer container = createSubtotalDiscountItemContainer(BigDecimal.ZERO.setScale(2), true, false);
		
		CartSubtotalPercentDiscountImpl discount = new CartSubtotalPercentDiscountImpl("", RULE_ID, ACTION_ID, "0");

		BigDecimal total = discount.apply(container);
		Assert.assertEquals(BigDecimal.ZERO.setScale(2), total);
	}
	
	/**
	 * Test to ensure calculateDiscount doesn't prematurely round.
	 */
	@Test
	public void testCalculateDiscountRounding() {
		BigDecimal subtotal = new BigDecimal("231.99");
		String discountRate = "10.30";
		BigDecimal expectedDiscount = new BigDecimal("23.89");
		CartSubtotalPercentDiscountImpl discount = new CartSubtotalPercentDiscountImpl("", RULE_ID, ACTION_ID, "0");
		BigDecimal result = discount.calculateDiscount(subtotal, discountRate);
		Assert.assertEquals(expectedDiscount, result);
	}

	/**
	 * Test case for apply() with 1 cart item, when the cart item cannot receive the promotion.
	 */
	@Test
	public void testApplyOnSubtotalAmountNonDiscountable() {
		
		final DiscountItemContainer container = createSubtotalDiscountItemContainer(BigDecimal.TEN, false, true);
		
		CartSubtotalPercentDiscountImpl discount = new CartSubtotalPercentDiscountImpl("", RULE_ID, ACTION_ID, "0");
		
		BigDecimal total = discount.apply(container);
		Assert.assertEquals(BigDecimal.ZERO, total);
	}

	
}
