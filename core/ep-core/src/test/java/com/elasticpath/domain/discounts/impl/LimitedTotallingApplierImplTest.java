package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Test cases for <code>LimitedTotallingApplierImpl</code>.
 */
public class LimitedTotallingApplierImplTest {
	
	private static final int MAX_ITEMS = 4;
	
	private static final int MAX_ITEMS2 = 11;
	
	private static final int NO_MAX_ITEMS = 0;
	
	private static final int TWO = 2;
	
	private static final int FIVE = 5;
	
	private static final BigDecimal DISCOUNT_AMOUNT = new BigDecimal("2.50");
	
	private static int cartItemCount = 0;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final DiscountItemContainer discountItemContainer = context.mock(DiscountItemContainer.class);
	
	/**
	 * Test case for calculating discount to 1 item.
	 */
	@Test
	public void testCalculateDiscountToOneItemWithQuantity() {				
		testApplyDiscountToOneItemWithQuantity(false, FIVE, NO_MAX_ITEMS, DISCOUNT_AMOUNT, DISCOUNT_AMOUNT.multiply(BigDecimal.valueOf(FIVE)));
	}

	/**
	 * Test case for applying discount to 1 item.
	 */
	@Test
	public void testApplyDiscountToOneItemWithQuantity() {	
		testApplyDiscountToOneItemWithQuantity(true, TWO, NO_MAX_ITEMS, DISCOUNT_AMOUNT, DISCOUNT_AMOUNT.multiply(BigDecimal.valueOf(TWO)));
	}
	
	private void testApplyDiscountToOneItemWithQuantity(final boolean actuallyApply, final int quantity, final int maxItems, 
			final BigDecimal amount, final BigDecimal totalDiscount) { 
		final ShoppingItem cartItem = context.mock(ShoppingItem.class);
		context.checking(new Expectations() {
			{
				if (actuallyApply) {
					oneOf(cartItem).applyDiscount(totalDiscount);
					oneOf(discountItemContainer).recordRuleApplied(1, 0L, cartItem, totalDiscount, quantity);
				}
			}
		});
		
		LimitedTotallingApplierImpl applier = new LimitedTotallingApplierImpl();		
		applier.initializeMaxItems(maxItems);
		applier.setActuallyApply(actuallyApply);
		applier.setDiscountItemContainer(discountItemContainer);
		applier.setRuleId(1);
		applier.apply(cartItem, amount, quantity);
		Assert.assertEquals("TotalDiscount does not match discount applied on 1 cart item", totalDiscount, applier.getTotalDiscount());
	}
	
	/**
	 * Test case for calculating discount to 1 item without limit.
	 */
	@Test
	public void testCalculateDiscountToOneItemWithoutLimit() {
		final boolean actuallyApply = true;
		final int maxItems = NO_MAX_ITEMS;
		final BigDecimal amount = DISCOUNT_AMOUNT;
		BigDecimal totalDiscount = amount.multiply(BigDecimal.valueOf(FIVE));			
		
		ShoppingItem cartItem1 = createCartItemAndDefineExpectations(actuallyApply, FIVE, amount.multiply(BigDecimal.valueOf(FIVE)), FIVE);
		LimitedTotallingApplierImpl applier = 
			initializeApplierAndApplyDiscount(actuallyApply, maxItems, amount, cartItem1);
		
		Assert.assertEquals("TotalDiscount does not match discount * max item quantity", totalDiscount, applier.getTotalDiscount());
	}

	/**
	 * Test case for calculating discount to 1 item with quantity > limit.
	 */
	@Test
	public void testApplyDiscountToOneItemWithLimit() {
		final boolean actuallyApply = true;
		final int maxItems = MAX_ITEMS;
		final BigDecimal amount = DISCOUNT_AMOUNT;
		BigDecimal totalDiscount = amount.multiply(BigDecimal.valueOf(MAX_ITEMS));
		
		ShoppingItem cartItem1 = createCartItemAndDefineExpectations(actuallyApply, FIVE, amount.multiply(BigDecimal.valueOf(MAX_ITEMS)), MAX_ITEMS);
		LimitedTotallingApplierImpl applier = 
			initializeApplierAndApplyDiscount(actuallyApply, maxItems, amount, cartItem1);
		
		Assert.assertEquals("TotalDiscount does not match discount * max item quantity", totalDiscount, applier.getTotalDiscount());
	}
	
	/**
	 * Test case for calculating discount to 1 item with quantity < limit.
	 */
	@Test
	public void testApplyDiscountToOneItemWithLimit2() {
		final boolean actuallyApply = true;
		final int maxItems = MAX_ITEMS;
		final BigDecimal amount = DISCOUNT_AMOUNT;
		BigDecimal totalDiscount = DISCOUNT_AMOUNT.multiply(BigDecimal.valueOf(TWO));
		
		ShoppingItem cartItem1 = createCartItemAndDefineExpectations(actuallyApply, TWO, amount.multiply(BigDecimal.valueOf(TWO)), TWO);
		LimitedTotallingApplierImpl applier = 
			initializeApplierAndApplyDiscount(actuallyApply, maxItems, amount, cartItem1);
		
		Assert.assertEquals("TotalDiscount does not match discount * cart item quantity", totalDiscount, applier.getTotalDiscount());
	}
	
	/**
	 * Test case for calculating discount to 3 items without limit.
	 */
	@Test
	public void testApplyDiscountToThreeItemWithoutLimit() {
		final boolean actuallyApply = true;
		final int maxItems = NO_MAX_ITEMS;
		final BigDecimal amount = DISCOUNT_AMOUNT;
		BigDecimal totalDiscount = DISCOUNT_AMOUNT.multiply(BigDecimal.valueOf(TWO + FIVE + TWO));
		
		ShoppingItem cartItem1 = createCartItemAndDefineExpectations(actuallyApply, TWO, amount.multiply(BigDecimal.valueOf(TWO)), TWO);
		ShoppingItem cartItem2 = createCartItemAndDefineExpectations(actuallyApply, FIVE, amount.multiply(BigDecimal.valueOf(FIVE)), FIVE);
		ShoppingItem cartItem3 = createCartItemAndDefineExpectations(actuallyApply, TWO, amount.multiply(BigDecimal.valueOf(TWO)), TWO);
		LimitedTotallingApplierImpl applier = 
			initializeApplierAndApplyDiscount(actuallyApply, maxItems, amount, cartItem1, cartItem2, cartItem3);
		
		Assert.assertEquals("TotalDiscount does not match discount * sum of (quantity of 3 cart items)", totalDiscount, applier.getTotalDiscount());
	}
	
	/**
	 * Test case for calculating discount to 3 item with quantity of 1st 2 items > limit.
	 */
	@Test
	public void testApplyDiscountToThreeItemWithLimit() {
		final boolean actuallyApply = true;
		final int maxItems = MAX_ITEMS;
		final BigDecimal amount = DISCOUNT_AMOUNT;
		BigDecimal totalDiscount = DISCOUNT_AMOUNT.multiply(BigDecimal.valueOf(maxItems));
		
		ShoppingItem cartItem1 = createCartItemAndDefineExpectations(actuallyApply, TWO, amount.multiply(BigDecimal.valueOf(TWO)), TWO);
		ShoppingItem cartItem2 = createCartItemAndDefineExpectations(actuallyApply, FIVE, amount.multiply(BigDecimal.valueOf(TWO)), TWO);
		ShoppingItem cartItem3 = createCartItemAndDefineExpectations(actuallyApply, TWO, amount.multiply(BigDecimal.ZERO), 0);
		LimitedTotallingApplierImpl applier = 
			initializeApplierAndApplyDiscount(actuallyApply, maxItems, amount, cartItem1, cartItem2, cartItem3);
		
		Assert.assertEquals("TotalDiscount does not match discount times max item quantity", totalDiscount, applier.getTotalDiscount());
	}
	
	/**
	 * Test case for calculating discount to 3 item with quantity of 3 items > limit.
	 */
	@Test
	public void testApplyDiscountToThreeItemWithLimit2() {
		final boolean actuallyApply = true;
		final int maxItems = MAX_ITEMS2;
		final BigDecimal amount = DISCOUNT_AMOUNT;
		BigDecimal totalDiscount = DISCOUNT_AMOUNT.multiply(BigDecimal.valueOf(maxItems));
		
		ShoppingItem cartItem1 = createCartItemAndDefineExpectations(actuallyApply, FIVE, amount.multiply(BigDecimal.valueOf(FIVE)), FIVE);
		ShoppingItem cartItem2 = createCartItemAndDefineExpectations(actuallyApply, FIVE, amount.multiply(BigDecimal.valueOf(FIVE)), FIVE);
		ShoppingItem cartItem3 = createCartItemAndDefineExpectations(actuallyApply, TWO, amount.multiply(BigDecimal.ONE), 1);
		LimitedTotallingApplierImpl applier = 
			initializeApplierAndApplyDiscount(actuallyApply, maxItems, amount, cartItem1, cartItem2, cartItem3);
		
		Assert.assertEquals("TotalDiscount does not match discount times max item quantity", totalDiscount, applier.getTotalDiscount());
	}
	
	/**
	 * Test case for calculating discount to 3 item with quantity of 3 items < limit.
	 */
	@Test
	public void testApplyDiscountToThreeItemWithLimit3() {
		final boolean actuallyApply = true;
		final int maxItems = MAX_ITEMS2;
		final BigDecimal amount = DISCOUNT_AMOUNT;
		BigDecimal totalDiscount = DISCOUNT_AMOUNT.multiply(BigDecimal.valueOf(TWO + FIVE + TWO));
		
		ShoppingItem cartItem1 = createCartItemAndDefineExpectations(actuallyApply, TWO, amount.multiply(BigDecimal.valueOf(TWO)), TWO);
		ShoppingItem cartItem2 = createCartItemAndDefineExpectations(actuallyApply, FIVE, amount.multiply(BigDecimal.valueOf(FIVE)), FIVE);
		ShoppingItem cartItem3 = createCartItemAndDefineExpectations(actuallyApply, TWO, amount.multiply(BigDecimal.valueOf(TWO)), TWO);
		LimitedTotallingApplierImpl applier = 
			initializeApplierAndApplyDiscount(actuallyApply, maxItems, amount, cartItem1, cartItem2, cartItem3);
		
		Assert.assertEquals("TotalDiscount does not match discount * sum of (quantity of 3 cart items)", totalDiscount, applier.getTotalDiscount());
	}
	
	private ShoppingItem createCartItemAndDefineExpectations(
			final boolean actuallyApply, final int quantity,
			final BigDecimal totalDiscount, final int expectedQuantityAppliedTo) { 
		final ShoppingItem cartItem = context.mock(ShoppingItem.class, String.valueOf(cartItemCount++));

		context.checking(new Expectations() {
			{
				if (actuallyApply) {
					oneOf(cartItem).applyDiscount(totalDiscount);
					oneOf(discountItemContainer).recordRuleApplied(1, 0L, cartItem, totalDiscount, expectedQuantityAppliedTo);
				}
				oneOf(cartItem).getQuantity();
				will(returnValue(quantity));
			}
		});
		return cartItem;
	}
	
	private LimitedTotallingApplierImpl initializeApplierAndApplyDiscount(
			final boolean actuallyApply, final int maxItems, 
			final BigDecimal amount, final ShoppingItem... cartItems) {
		LimitedTotallingApplierImpl applier = new LimitedTotallingApplierImpl();		
		applier.initializeMaxItems(maxItems);
		applier.setActuallyApply(actuallyApply);
		applier.setDiscountItemContainer(discountItemContainer);
		applier.setRuleId(1);
		for (ShoppingItem cartItem : cartItems) {
			applier.apply(cartItem, amount);
		}
		return applier;
	}
}
