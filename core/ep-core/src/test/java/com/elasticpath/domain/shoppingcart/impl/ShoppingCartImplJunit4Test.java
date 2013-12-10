package com.elasticpath.domain.shoppingcart.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.impl.CartNFreeSkusActionImpl;
import com.elasticpath.domain.rules.impl.CartSubtotalPercentDiscountActionImpl;
import com.elasticpath.domain.rules.impl.PromotionRuleImpl;
import com.elasticpath.domain.rules.impl.ShippingAmountDiscountActionImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartMemento;
import com.elasticpath.domain.shoppingcart.ShoppingCartMementoHolder;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.order.impl.ShoppingItemHasRecurringPricePredicate;
import com.elasticpath.service.pricing.PriceLookupService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Tests how {@code ShoppingCartImpl} interacts with {@code ShoppingItem}. New test created instead of 
 * modifying {@code ShoppingCartImplTest} because of the latter's complexity and reliance on ElasticPathTestCase.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ShoppingCartImplJunit4Test {

	private static final String DISCOUNT_0_50 = "0.50";
	private static final int QUANTITY_APPLIED_TO_3 = 3;
	private static final int ACTION_UID2 = 5;
	private static final String RANDOM_DISCOUNT_AMOUNT = "12.34";
	private static final int ACTION_UID = 4;
	private static final int RULE_UID = 3;
	private static final String CART_ITEM_1_GUID = "11111";
	private static final String CART_ITEM_2_GUID = "22222";
	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Test that initializing the ShoppingCart creates a {@code ShoppingItem} and retrieves price.
	 */
	@Test
	public void testInitialization() {
		
		final Shopper shopper = context.mock(Shopper.class);
		final ShoppingCartMemento memento = context.mock(ShoppingCartMemento.class);
		final ShoppingItem scItem1 = context.mock(ShoppingItem.class);
		final ShoppingItem scItem2 = context.mock(ShoppingItem.class, "item2");
		final List<ShoppingItem> items = new ArrayList<ShoppingItem>();
		items.add(scItem1);
		items.add(scItem2);
		
		final ProductSku productSku1 = context.mock(ProductSku.class);
		final ProductSku productSku2 = context.mock(ProductSku.class, "productSku2");
		
		final PriceLookupService priceLookupService = context.mock(PriceLookupService.class);
		
		final Store store = context.mock(Store.class);
		final Catalog catalog = context.mock(Catalog.class);
		
		ShoppingCart cart = new ShoppingCartImpl() {
			
			{
				setShoppingItemHasRecurringPricePredicate(new ShoppingItemHasRecurringPricePredicate()); 
			}

			private static final long serialVersionUID = 7525519626908393907L;

			@Override
			public PriceLookupService getPriceLookupService() {
				return priceLookupService;
			}
			@Override
			public Currency getCurrency() {
				return Currency.getInstance("CAD");
			}
			
			@Override
			public Store getStore() {
				return store;
			}
		};
		
		
		// Expect that a sku ShoppingItem to be created and set on each shopping cart item.
		context.checking(new Expectations() {
			{
				allowing(memento).getAllItems(); will(returnValue(items));
				
				allowing(scItem1).getProductSku(); will(returnValue(productSku1));
				allowing(scItem2).getProductSku(); will(returnValue(productSku2));
				
				allowing(store).getCatalog(); will(returnValue(catalog));
				
				allowing(memento).setShopper(shopper); 
				// FIXME: Remove after logging.
				allowing(memento).getUidPk();
			}
		});
		((ShoppingCartMementoHolder) cart).setShoppingCartMemento(memento);
		cart.setShopper(shopper);
				
	}
	
	/**
	 * Tests that you can retrieve a shopping cart item from a cart by specifying its GUID.
	 */
	@Test
	public void testGetItemByGuid() {
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		BeanFactoryExpectationsFactory expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		try {
			ShoppingCart shoppingCart = new ShoppingCartImpl() {

				{
					setShoppingItemHasRecurringPricePredicate(new ShoppingItemHasRecurringPricePredicate() {
						private static final long serialVersionUID = -5127530597906325001L;

						public boolean evaluate(final ShoppingItem shoppingItem) {
							return false;
						}
					});
				}

				private static final long serialVersionUID = -5295285904220022476L;

				@Override
				public void fireRules() {
					// don't care about the rule logic
				}
			};

			final ShoppingItem cartItem1 = context.mock(ShoppingItem.class);
			final ShoppingItem cartItem2 = context.mock(ShoppingItem.class, "cartItem2");

			final ShoppingCartMemento shoppingCartMemento = new ShoppingCartMementoImpl();

			final ProductSku productSku1 = context.mock(ProductSku.class);
			final ProductSku productSku2 = context.mock(ProductSku.class, "sku2");

			context.checking(new Expectations() { {
				allowing(beanFactory).getBean("shoppingCartMemento"); will(returnValue(shoppingCartMemento));
				allowing(cartItem1).isConfigurable(); will(returnValue(false));
				allowing(cartItem2).isConfigurable(); will(returnValue(false));
				allowing(cartItem1).getProductSku(); will(returnValue(productSku1));
				allowing(cartItem2).getProductSku(); will(returnValue(productSku2));
				allowing(productSku1).getSkuCode(); will(returnValue("33333"));
				allowing(productSku2).getSkuCode(); will(returnValue("44444"));
				allowing(cartItem1).getGuid(); will(returnValue(CART_ITEM_1_GUID));
				allowing(cartItem2).getGuid(); will(returnValue(CART_ITEM_2_GUID));
			} });

			shoppingCart.addCartItem(cartItem1);
			shoppingCart.addCartItem(cartItem2);

			final String expected = new String("11111"); // NOPMD: to test correctness of string comparison we need an object
			ShoppingItem returnedCartItem1 = shoppingCart.getCartItemByGuid(expected);

			assertEquals("The item found should be the same as the item added.", cartItem1, returnedCartItem1);
		} finally {
			expectationsFactory.close();
		}
	}

	/**
	 * Tests that a discount record can be found for a rule that has one action applied once.
	 */
	@Test
	public void testGetDiscountRecordForRuleAndAction() {
		ShoppingCart shoppingCart = new ShoppingCartImpl();
		ShoppingItem discountedItem = new ShoppingItemImpl();
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new CartNFreeSkusActionImpl();
		ruleAction.setUidPk(ACTION_UID);
		shoppingCart.ruleApplied(RULE_UID, ACTION_UID, discountedItem, new BigDecimal(RANDOM_DISCOUNT_AMOUNT), 2);
		
		ItemDiscountRecordImpl itemDiscountRecordImpl = (ItemDiscountRecordImpl) shoppingCart.getDiscountRecordForRuleAndAction(rule, ruleAction);
		assertEquals(new BigDecimal(RANDOM_DISCOUNT_AMOUNT), itemDiscountRecordImpl.getDiscountAmount());
		assertEquals(discountedItem, itemDiscountRecordImpl.getShoppingItem());
		assertEquals(2, itemDiscountRecordImpl.getQuantityAppliedTo());
	}
	
	/**
	 * Tests that a discount record can be found for a rule that has two actions applied once each.
	 */
	@Test
	public void testGetDiscountRecordForRuleAndActionTwoActions() {
		ShoppingCart shoppingCart = new ShoppingCartImpl();
		ShoppingItem discountedItem = new ShoppingItemImpl();
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new CartNFreeSkusActionImpl();
		ruleAction.setUidPk(ACTION_UID);
		shoppingCart.ruleApplied(RULE_UID, ACTION_UID, discountedItem, new BigDecimal(RANDOM_DISCOUNT_AMOUNT), 2);
		
		RuleAction ruleAction2 = new CartNFreeSkusActionImpl();
		ruleAction2.setUidPk(ACTION_UID2);
		shoppingCart.ruleApplied(RULE_UID, ACTION_UID2, discountedItem, new BigDecimal("52.34"), QUANTITY_APPLIED_TO_3);
		
		ItemDiscountRecordImpl itemDiscountRecordImpl = (ItemDiscountRecordImpl) shoppingCart.getDiscountRecordForRuleAndAction(rule, ruleAction);
		assertEquals(new BigDecimal(RANDOM_DISCOUNT_AMOUNT), itemDiscountRecordImpl.getDiscountAmount());
		assertEquals(discountedItem, itemDiscountRecordImpl.getShoppingItem());
		assertEquals(2, itemDiscountRecordImpl.getQuantityAppliedTo());
		
		ItemDiscountRecordImpl discountRecord2 = (ItemDiscountRecordImpl) shoppingCart.getDiscountRecordForRuleAndAction(rule, ruleAction2);
		assertEquals(new BigDecimal("52.34"), discountRecord2.getDiscountAmount());
		assertEquals(discountedItem, discountRecord2.getShoppingItem());
		assertEquals(QUANTITY_APPLIED_TO_3, discountRecord2.getQuantityAppliedTo());
	}
	
	/**
	 * Tests that a discount record can be found for a rule that has the same action applied from two different
	 * shopping items.
	 */
	@Test
	public void testGetDiscountRecordForRuleAndActionTwoShoppingItems() {
		ShoppingCart shoppingCart = new ShoppingCartImpl();
		ShoppingItem discountedItem = new ShoppingItemImpl();
		ShoppingItem discountedItem2 = new ShoppingItemImpl();
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new CartNFreeSkusActionImpl();
		ruleAction.setUidPk(ACTION_UID);
		shoppingCart.ruleApplied(RULE_UID, ACTION_UID, discountedItem, new BigDecimal(RANDOM_DISCOUNT_AMOUNT), 2);
		
		shoppingCart.ruleApplied(RULE_UID, ACTION_UID, discountedItem2, new BigDecimal(RANDOM_DISCOUNT_AMOUNT), QUANTITY_APPLIED_TO_3);
		
		ItemDiscountRecordImpl itemDiscountRecordImpl = (ItemDiscountRecordImpl) shoppingCart.getDiscountRecordForRuleAndAction(rule, ruleAction);
		assertEquals(new BigDecimal(RANDOM_DISCOUNT_AMOUNT), itemDiscountRecordImpl.getDiscountAmount());
		assertEquals(discountedItem, itemDiscountRecordImpl.getShoppingItem());
		assertEquals(2 + QUANTITY_APPLIED_TO_3, itemDiscountRecordImpl.getQuantityAppliedTo());
		
	}
	
	/**
	 * Tests that calling setSubtotalDiscount updates the appliedRuleIds and the discount record.
	 */
	@Test
	public void testSetSubtotalDiscountUpdatesRecords() {
		ShoppingCart shoppingCart = new ShoppingCartImpl() {
			{
				setShoppingItemHasRecurringPricePredicate(new ShoppingItemHasRecurringPricePredicate()); 
			}

			private static final long serialVersionUID = -3967509335388001024L;

			@Override
			public BigDecimal getSubtotal() {
				return BigDecimal.ONE;
			}
		};
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new CartSubtotalPercentDiscountActionImpl();
		ruleAction.setUidPk(ACTION_UID);
				
		shoppingCart.setSubtotalDiscount(new BigDecimal(DISCOUNT_0_50), RULE_UID, ACTION_UID);
		
		assertTrue("ShoppingCart should have applied the rule", shoppingCart.getAppliedRules().contains(new Long(RULE_UID)));
		DiscountRecord discountRecord = shoppingCart.getDiscountRecordForRuleAndAction(rule, ruleAction);
		assertNotNull("Should have a discount record", discountRecord);
		assertTrue("Must be for a subtotal", discountRecord instanceof SubtotalDiscountRecordImpl);
	}
	
	/**
	 * Tests that calling shippingRuleApplied updates the appliedRuleIds and the discount record.
	 */
	@Test
	public void testShippingRuleAppliedUpdatesRecords() {
		ShoppingCart shoppingCart = new ShoppingCartImpl();
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new ShippingAmountDiscountActionImpl();
		ruleAction.setUidPk(ACTION_UID);
		
		BigDecimal discountAmount = new BigDecimal(DISCOUNT_0_50);
		shoppingCart.shippingRuleApplied(RULE_UID, ACTION_UID, discountAmount);
		
		assertTrue("ShoppingCart should have applied the rule", shoppingCart.getAppliedRules().contains(new Long(RULE_UID)));
		DiscountRecord discountRecord = shoppingCart.getDiscountRecordForRuleAndAction(rule, ruleAction);
		assertNotNull("Should have a discount record", discountRecord);
		assertTrue("Must be for a subtotal", discountRecord instanceof ShippingDiscountRecordImpl);
	}
	
	/**
	 * Tests that calling setSubtotalDiscount updates the appliedRuleIds and the discount record.
	 * The discount which is biggest is the one that should be recorded.
	 */
	@Test
	public void testSetSubtotalDiscountOverrideLowerPrevious() {
		ShoppingCart shoppingCart = new ShoppingCartImpl() {

			{
				setShoppingItemHasRecurringPricePredicate(new ShoppingItemHasRecurringPricePredicate()); 
			}

			private static final long serialVersionUID = -7761482988063818208L;

			@Override
			public BigDecimal getSubtotal() {
				return BigDecimal.ONE;
			}
		};
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new CartSubtotalPercentDiscountActionImpl();
		ruleAction.setUidPk(ACTION_UID);
		
		RuleAction ruleAction2 = new CartSubtotalPercentDiscountActionImpl();
		ruleAction2.setUidPk(ACTION_UID2);
				
		shoppingCart.setSubtotalDiscount(new BigDecimal(DISCOUNT_0_50), RULE_UID, ACTION_UID);
		
		shoppingCart.setSubtotalDiscount(new BigDecimal("1.00"), RULE_UID, ACTION_UID2);
		
		assertTrue("ShoppingCart should have applied the rule", shoppingCart.getAppliedRules().contains(new Long(RULE_UID)));
		DiscountRecord discountRecord = shoppingCart.getDiscountRecordForRuleAndAction(rule, ruleAction2);
		assertNotNull("Should have a discount record for the biggest discount", discountRecord);
		assertTrue("Must be for a subtotal", discountRecord instanceof SubtotalDiscountRecordImpl);
		
		DiscountRecord discountRecord2 = shoppingCart.getDiscountRecordForRuleAndAction(rule, ruleAction);
		assertTrue("Discount record for the smallest discount should be superceded.", discountRecord2.isSuperceded());
	}
	
	/**
	 * Tests that calling setSubtotalDiscount updates the appliedRuleIds and the discount record.
	 * The discount which is biggest is the one that should be recorded.
	 * This test reverses the order of the set subtotal discount.
	 */
	@Test
	public void testSetSubtotalDiscountNotOverrideHigherPrevious() {
		ShoppingCart shoppingCart = new ShoppingCartImpl() {

			{
				setShoppingItemHasRecurringPricePredicate(new ShoppingItemHasRecurringPricePredicate()); 
			}

			private static final long serialVersionUID = 7390415668349322015L;

			@Override
			public BigDecimal getSubtotal() {
				return BigDecimal.ONE;
			}
		};
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new CartSubtotalPercentDiscountActionImpl();
		ruleAction.setUidPk(ACTION_UID);
		
		RuleAction ruleAction2 = new CartSubtotalPercentDiscountActionImpl();
		ruleAction2.setUidPk(ACTION_UID2);
		
		shoppingCart.setSubtotalDiscount(new BigDecimal("1.00"), RULE_UID, ACTION_UID2);
		
		shoppingCart.setSubtotalDiscount(new BigDecimal(DISCOUNT_0_50), RULE_UID, ACTION_UID);
				
		assertTrue("ShoppingCart should have applied the rule", shoppingCart.getAppliedRules().contains(new Long(RULE_UID)));
		DiscountRecord discountRecord = shoppingCart.getDiscountRecordForRuleAndAction(rule, ruleAction2);
		assertNotNull("Should have a discount record for the biggest discount", discountRecord);
		assertTrue("Must be for a subtotal", discountRecord instanceof SubtotalDiscountRecordImpl);
		
		DiscountRecord discountRecord2 = shoppingCart.getDiscountRecordForRuleAndAction(rule, ruleAction);
		assertTrue("Discount record for the smallest should be superceded (even though applied afterwards).", discountRecord2.isSuperceded());
	}
	
	/**
	 * Tests that calling shippingRuleApplied updates the appliedRuleIds and the discount record.
	 */
	@Test
	public void testShippingRuleAppliesOverridesPrevious() {
		ShoppingCart shoppingCart = new ShoppingCartImpl();
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new ShippingAmountDiscountActionImpl();
		ruleAction.setUidPk(ACTION_UID);
		
		RuleAction ruleAction2 = new ShippingAmountDiscountActionImpl();
		ruleAction2.setUidPk(ACTION_UID2);
		
		BigDecimal discountAmount = new BigDecimal(DISCOUNT_0_50);
		shoppingCart.shippingRuleApplied(RULE_UID, ACTION_UID, discountAmount);
		
		BigDecimal discountAmount2 = new BigDecimal("0.25");
		shoppingCart.shippingRuleApplied(RULE_UID, ACTION_UID2, discountAmount2);
		
		assertTrue("ShoppingCart should have applied the rule", shoppingCart.getAppliedRules().contains(new Long(RULE_UID)));
		DiscountRecord discountRecord = shoppingCart.getDiscountRecordForRuleAndAction(rule, ruleAction2);
		assertNotNull("Should have the last discount record", discountRecord);
		assertTrue("Must be for a subtotal", discountRecord instanceof ShippingDiscountRecordImpl);
		
		DiscountRecord discountRecord2 = shoppingCart.getDiscountRecordForRuleAndAction(rule, ruleAction);
		assertTrue("The first discount record should be superceded", discountRecord2.isSuperceded());
	}
}




