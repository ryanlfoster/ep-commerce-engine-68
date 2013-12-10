package com.elasticpath.domain.discounts.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.discounts.TotallingApplier;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.rules.PromotionRuleExceptions;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for <code>CartCategoryAmountDiscountImpl</code>.
 */
public class CartCategoryAmountDiscountImplTest {
	
	private static final BigDecimal TWENTY = BigDecimal.valueOf(20);
	
	private static final BigDecimal FORTY = BigDecimal.valueOf(40);
	
	private static final String EXCEPTION_STR = "exceptionStr";
	
	private static final String CART_ITEM_PRODUCT_CODE = "product1";
	
	private static final long RULE_ID = 123L;
	
	private static final long ACTION_ID = 456L;
	
	private static final int MAX_ITEMS = 4;
	
	private static int cartItemCount = 0;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	private TotallingApplier totallingApplier;
	
	private DiscountItemContainer container;
	
	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of error happens
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		totallingApplier = context.mock(TotallingApplier.class);
		container = context.mock(DiscountItemContainer.class);
		context.checking(new Expectations() {
			{
				final PromotionRuleExceptions exceptions = context.mock(PromotionRuleExceptions.class);
				oneOf(beanFactory).getBean(ContextIdNames.PROMOTION_RULE_EXCEPTIONS);
				will(returnValue(exceptions));
				oneOf(beanFactory).getBean(ContextIdNames.TOTALLING_APPLIER);
				will(returnValue(totallingApplier));
				
				oneOf(exceptions).populateFromExceptionStr(EXCEPTION_STR);
				
				oneOf(totallingApplier).setDiscountItemContainer(container);
				oneOf(totallingApplier).setRuleId(RULE_ID);
				
				ignoring(container).getCatalog();
			}
		});
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	private CartCategoryAmountDiscountImpl createCartCategoryAmountDiscountImpl(final String ruleElementType,
			final long ruleId, final long actionId, final String categoryCode,
			final String amount, final int maxItems, final String exceptions) {
		return new CartCategoryAmountDiscountImpl(ruleElementType, ruleId, actionId, categoryCode, amount, exceptions, maxItems) {
			private static final long serialVersionUID = -9113278950008722024L;

			@Override
			protected boolean cartItemIsEligibleForPromotion(
					final DiscountItemContainer discountItemContainer, final ShoppingItem cartItem,
					final String productCode,
					final PromotionRuleExceptions promotionRuleExceptions) {
				return true;
			}
		};
	}

	/**
	 * Test case for apply() with empty cart.
	 */
	@Test
	public void testCalculateOnEmptyCart() {
		final int maxItems = 0;
		context.checking(new Expectations() {
			{
				oneOf(totallingApplier).getTotalDiscount();
				will(returnValue(BigDecimal.ZERO));
				oneOf(totallingApplier).setActuallyApply(false);
				oneOf(totallingApplier).initializeMaxItems(maxItems);
				oneOf(totallingApplier).setActionId(ACTION_ID);
									
				oneOf(container).getItemsLowestToHighestPrice();
				will(returnValue(Arrays.<ShoppingItem>asList()));
			}
		});
				
		CartCategoryAmountDiscountImpl discount = 
			createCartCategoryAmountDiscountImpl("", RULE_ID, ACTION_ID, CART_ITEM_PRODUCT_CODE, "1", maxItems, EXCEPTION_STR);

		BigDecimal total = discount.calculate(container);
		assertEquals("Empty cart should not have any discount amount.", BigDecimal.ZERO, total);
	}

	/**
	 * Test case for apply() with empty cart.
	 */
	@Test
	public void testApplyOnEmptyCart() {
		final int maxItems = 0;
		context.checking(new Expectations() {
			{
				oneOf(totallingApplier).getTotalDiscount();
				will(returnValue(BigDecimal.ZERO));
				oneOf(totallingApplier).setActuallyApply(true);
				oneOf(totallingApplier).initializeMaxItems(maxItems);
				oneOf(totallingApplier).setActionId(ACTION_ID);
									
				oneOf(container).getItemsLowestToHighestPrice();
				will(returnValue(Arrays.<ShoppingItem>asList()));
			}
		});
	
		CartCategoryAmountDiscountImpl discount = 
			createCartCategoryAmountDiscountImpl("", RULE_ID, ACTION_ID, CART_ITEM_PRODUCT_CODE, "1", maxItems, EXCEPTION_STR);
		BigDecimal total = discount.apply(container);
		assertEquals("Empty cart should not have any discount amount.", BigDecimal.ZERO, total);
	}

	/**
	 * Test case for apply() with 1 cart item.
	 */
	@Test
	public void testApplyOnSingleItem() {
		final ShoppingItem cartItem = context.mock(ShoppingItem.class, String.valueOf(cartItemCount++));
		final int maxItems = 0;
		final BigDecimal discountAmount = TWENTY;
		final BigDecimal totalDiscountAmount = FORTY;
		context.checking(new Expectations() {
			{
				oneOf(totallingApplier).setActuallyApply(true);
				oneOf(totallingApplier).initializeMaxItems(maxItems);
				oneOf(totallingApplier).getTotalDiscount();
				will(returnValue(totalDiscountAmount));
				oneOf(totallingApplier).apply(cartItem, discountAmount);
				oneOf(totallingApplier).setActionId(ACTION_ID);
									
				oneOf(container).getItemsLowestToHighestPrice();
				will(returnValue(Arrays.<ShoppingItem>asList(cartItem)));
			}
		});

		final CartCategoryAmountDiscountImpl discount = 
			createCartCategoryAmountDiscountImpl("", RULE_ID, ACTION_ID, CART_ITEM_PRODUCT_CODE, discountAmount.toString(), maxItems, EXCEPTION_STR);
		BigDecimal total = discount.apply(container);
		assertEquals("Amount does not matched expected value for cart with 1 item", totalDiscountAmount, total);
	}

	/**
	 * Test case for doApply() with 2 items.
	 */
	@Test
	public void testApplyOnTwoItems() {
		final ShoppingItem cartItem1 = context.mock(ShoppingItem.class, String.valueOf(cartItemCount++));
		final ShoppingItem cartItem2 = context.mock(ShoppingItem.class, String.valueOf(cartItemCount++));
		final BigDecimal discountAmount = BigDecimal.TEN;
		final BigDecimal totalDiscountAmount = FORTY;
		context.checking(new Expectations() {
			{
				oneOf(totallingApplier).setActuallyApply(true);
				oneOf(totallingApplier).initializeMaxItems(MAX_ITEMS);
				oneOf(totallingApplier).getTotalDiscount();
				will(returnValue(totalDiscountAmount));
				oneOf(totallingApplier).apply(cartItem1, discountAmount);
				oneOf(totallingApplier).apply(cartItem2, discountAmount);
				oneOf(totallingApplier).setActionId(ACTION_ID);
				
				oneOf(container).getItemsLowestToHighestPrice();
				will(returnValue(Arrays.<ShoppingItem>asList(cartItem1, cartItem2)));
			}
		});		

		CartCategoryAmountDiscountImpl discount =
			createCartCategoryAmountDiscountImpl("", RULE_ID, ACTION_ID, CART_ITEM_PRODUCT_CODE, discountAmount.toString(), MAX_ITEMS, EXCEPTION_STR);
		BigDecimal total = discount.apply(container);
		assertEquals("Amount does not matched expected value for cart with 2 items", FORTY, total);
	}
}
