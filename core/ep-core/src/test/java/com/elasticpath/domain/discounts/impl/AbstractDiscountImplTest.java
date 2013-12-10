package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;

/**
 * Test cases for <code>AbstractDiscountImpl</code>.
 */
public class AbstractDiscountImplTest {

	private static final long RULE_ID = 123L;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private DiscountItemContainer container;

	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of error happens
	 */
	@Before
	public void setUp() throws Exception {
		container = context.mock(DiscountItemContainer.class);
	}
	
	private AbstractDiscountImpl createAbstractDiscountImpl(final String ruleElementType, final long ruleId) {
		AbstractDiscountImpl discount = new AbstractDiscountImpl(ruleElementType, ruleId) {
			private static final long serialVersionUID = -8392296642713908540L;

			@Override
			protected BigDecimal doApply(final boolean actuallyApply, final DiscountItemContainer discountItemContainer) {
				return BigDecimal.ONE;
			}
		};
		return discount;
	}
	
	/**
	 * Test case for recordRuleApplied method with actually apply = true.
	 */
	@Test
	public void testRecordRuleApplied() {
		final boolean actuallyApply = true;
		final BigDecimal discountAmount = new BigDecimal("3.39");
		final ShoppingItem shoppingItem = new ShoppingItemImpl();
		context.checking(new Expectations() {
			{
				oneOf(container).recordRuleApplied(RULE_ID, 1L, shoppingItem, discountAmount, 0);
			}
		});
		
		AbstractDiscountImpl discount = 
			createAbstractDiscountImpl("", RULE_ID);
		discount.recordRuleApplied(container, actuallyApply, RULE_ID, 1L, shoppingItem, discountAmount, 0);

	}
	
	/**
	 * Test case for recordRuleApplied method with actually apply = false.
	 */
	@Test
	public void testRecordRuleAppliedNot() {
		final boolean actuallyApply = false;

		AbstractDiscountImpl discount = 
			createAbstractDiscountImpl("", RULE_ID);
		discount.recordRuleApplied(container, actuallyApply, RULE_ID, 0L, null, null, 0);
		
	}
	
	/**
	 * Test case for getItemPrice(container, cartItem) method.
	 */
	@Test
	public void testGetItemPrice() {
		final ShoppingItem cartItem = context.mock(ShoppingItem.class);
		final BigDecimal price = BigDecimal.TEN;
		context.checking(new Expectations() {
			{
				oneOf(container).getPriceAmount(cartItem);
				will(returnValue(price));
			}
		});
		
		AbstractDiscountImpl discount = 
			createAbstractDiscountImpl("", RULE_ID);
		BigDecimal price2 = discount.getItemPrice(container, cartItem);
		Assert.assertEquals("The price returned does not match expected value.", price, price2);
	}
}
