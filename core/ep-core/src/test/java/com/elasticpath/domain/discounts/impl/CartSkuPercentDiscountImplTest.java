package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

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
 * Test cases for <code>CartSkuPercentDiscountImpl</code>.
 */
public class CartSkuPercentDiscountImplTest {
	
	private static final BigDecimal TWENTY = BigDecimal.valueOf(20);
	
	private static final BigDecimal FORTY = BigDecimal.valueOf(40);
	
	private static final String EXCEPTION_STR = "exceptionStr";
	
	private static final String CART_ITEM_PRODUCT_CODE = "product1";
	
	private static final BigDecimal HUNDRED_PERCENT = BigDecimal.valueOf(100);
	
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
				
				oneOf(totallingApplier).setDiscountItemContainer(container);
				oneOf(totallingApplier).setRuleId(RULE_ID);
				oneOf(totallingApplier).setActionId(ACTION_ID);
				
				oneOf(exceptions).populateFromExceptionStr(EXCEPTION_STR);
				
				ignoring(container).getCatalog();
			}
		});
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	private CartSkuPercentDiscountImpl createCartSkuPercentDiscountImpl(final String ruleElementType,
			final long ruleId, final long actionId, final String skuCode,
			final String percent, final int maxItems, final String exceptions) {
		CartSkuPercentDiscountImpl discount = 
			new CartSkuPercentDiscountImpl(ruleElementType, ruleId, actionId, skuCode, percent, exceptions, maxItems) {
				private static final long serialVersionUID = 5788553421390383057L;

				@Override
				protected boolean cartItemIsEligibleForPromotion(
						final DiscountItemContainer discountItemContainer, final ShoppingItem cartItem,
						final String skuCode,
						final PromotionRuleExceptions promotionRuleExceptions) {
					return true;
				}
			};
		return discount;
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
									
				oneOf(container).getItemsLowestToHighestPrice();
				will(returnValue(Arrays.<ShoppingItem>asList()));
			}
		});
		
		CartSkuPercentDiscountImpl discount = 
			createCartSkuPercentDiscountImpl("", RULE_ID, ACTION_ID, CART_ITEM_PRODUCT_CODE, "50", maxItems, EXCEPTION_STR);
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
									
				oneOf(container).getItemsLowestToHighestPrice();
				will(returnValue(Arrays.<ShoppingItem>asList()));
			}
		});
		
		CartSkuPercentDiscountImpl discount = 
			createCartSkuPercentDiscountImpl("", RULE_ID, ACTION_ID, CART_ITEM_PRODUCT_CODE, "50", maxItems, EXCEPTION_STR);
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
		final BigDecimal discountPercent = new BigDecimal("0.25");
		final BigDecimal itemPrice = BigDecimal.TEN;
		final BigDecimal totalDiscountAmount = TWENTY;
		context.checking(new Expectations() {
			{
				oneOf(totallingApplier).setActuallyApply(true);
				oneOf(totallingApplier).initializeMaxItems(maxItems);
				oneOf(totallingApplier).getTotalDiscount();
				will(returnValue(totalDiscountAmount));
				oneOf(totallingApplier).apply(cartItem, itemPrice.multiply(discountPercent));
									
				oneOf(container).getItemsLowestToHighestPrice();
				will(returnValue(Arrays.<ShoppingItem>asList(cartItem)));
				oneOf(container).getPriceAmount(cartItem);
				will(returnValue(itemPrice));
			}
		});
		
		final CartSkuPercentDiscountImpl discount =	createCartSkuPercentDiscountImpl("", RULE_ID, 
				ACTION_ID, CART_ITEM_PRODUCT_CODE, discountPercent.multiply(HUNDRED_PERCENT).toString(), maxItems, EXCEPTION_STR);
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
		final BigDecimal discountPercent = new BigDecimal("0.35");
		final BigDecimal itemPrice1 = BigDecimal.ONE;
		final BigDecimal itemPrice2 = BigDecimal.TEN;
		final BigDecimal totalDiscountAmount = FORTY;
		context.checking(new Expectations() {
			{
				oneOf(totallingApplier).setActuallyApply(true);
				oneOf(totallingApplier).initializeMaxItems(MAX_ITEMS);
				oneOf(totallingApplier).getTotalDiscount();
				will(returnValue(totalDiscountAmount));
				oneOf(totallingApplier).apply(cartItem1, itemPrice1.multiply(discountPercent));
				oneOf(totallingApplier).apply(cartItem2, itemPrice2.multiply(discountPercent));
				
				oneOf(container).getItemsLowestToHighestPrice();
				will(returnValue(Arrays.<ShoppingItem>asList(cartItem1, cartItem2)));
				oneOf(container).getPriceAmount(cartItem1);
				will(returnValue(itemPrice1));
				oneOf(container).getPriceAmount(cartItem2);
				will(returnValue(itemPrice2));
			}
		});
		
		final CartSkuPercentDiscountImpl discount = createCartSkuPercentDiscountImpl("", RULE_ID, ACTION_ID, 
				CART_ITEM_PRODUCT_CODE, discountPercent.multiply(HUNDRED_PERCENT).toString(), MAX_ITEMS, EXCEPTION_STR);
		BigDecimal total = discount.apply(container);
		assertEquals("Amount does not matched expected value for cart with 2 items", totalDiscountAmount, total);
	}
}
