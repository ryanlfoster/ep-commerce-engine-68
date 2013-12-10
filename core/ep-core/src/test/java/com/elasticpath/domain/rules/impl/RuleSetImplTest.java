/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.test.factory.RuleSetTestUtility;

/** Test cases for <code>RuleSetImpl</code>. */
public class RuleSetImplTest {

	private static final String THEN = "then";

	private static final String WHEN = "when";

	private static final String SHOPPING_CART_IMPORT = "com.elasticpath.domain.shoppingcart.ShoppingCart";

	private static final String RULE_SET_NAME = "com.elasticpath.promotionrules";

	private RuleSet ruleSet;

	@Before
	public void setUp() throws Exception {
		ruleSet = createMockCatalogRuleSet();
	}

	/**
	 * Test method for rule set code generation.
	 */
	@Test
	public void testGetCatalogRuleCode() {
		String code = ruleSet.getRuleCode(RuleSetTestUtility.STORE);
		basicCodeCheck(code);
	}

	
	/**
	 * Test method for rule set code generation.
	 */
	@Test
	public void testGetOrderRuleCode() {
		String code = createMockOrderRuleSet().getRuleCode(RuleSetTestUtility.STORE);
		basicCodeCheck(code);
	}

	/**
	 * Test method for rule set code generation.
	 */
	@Test
	public void testBuy3Get1FreeRuleCode() {
		String code = RuleSetTestUtility.createBuyTwoGetOneFreeRuleSet().getRuleCode(RuleSetTestUtility.STORE);
		basicCodeCheck(code);
	}

	/**
	 * Test method for rule set code generation.
	 */
	@Test
	public void testcreateCartCategoryDiscountRuleSet() {
		String code = RuleSetTestUtility.createCartCategoryDiscountRuleSet().getRuleCode(RuleSetTestUtility.STORE);
		basicCodeCheck(code);
	}

	/**
	 * Test method for rule set code generation.
	 */
	@Test
	public void testCreateBuyOneYGetZFreeItemsOfX() {
		String code = RuleSetTestUtility.createBuyOneYGetZFreeItemsOfX().getRuleCode(RuleSetTestUtility.STORE);
		basicCodeCheck(code);
	}

	private void basicCodeCheck(final String code) {
		assertTrue(code.indexOf(RULE_SET_NAME) > 0);
		assertTrue(code.indexOf(SHOPPING_CART_IMPORT) > 0);
		assertTrue(code.indexOf(WHEN) > 0);
		assertTrue(code.indexOf(THEN) > 0);
	}

	private RuleSet createMockCatalogRuleSet() {

		RuleSet ruleSet = new RuleSetImpl();
		ruleSet.setName(RULE_SET_NAME);
		ruleSet.setScenario(RuleScenarios.CATALOG_BROWSE_SCENARIO);

		Rule promotionRule = new PromotionRuleImpl() {
			private static final long serialVersionUID = -3370693769057123352L;
			private final Set<RuleAction> actions = new HashSet<RuleAction>();
			
			@Override
			public void addAction(final RuleAction ruleAction) { //NOPMD
				this.actions.add(ruleAction);
			}
			
			@Override
			public void removeAction(final RuleAction ruleAction) {
				this.actions.remove(ruleAction);
			}
			
			@Override
			public Set<RuleAction> getActions() {
				return this.actions;
			}
			
		};

		promotionRule.setName("Car Sale");
		promotionRule.setCatalog(new CatalogImpl());
		
		// Create a condition that the product is in a particular category
		RuleCondition categoryCondition = new ProductCategoryConditionImpl();
		categoryCondition.addParameter(new RuleParameterImpl(RuleParameter.CATEGORY_CODE_KEY, "8"));
		categoryCondition.addParameter(new RuleParameterImpl(RuleParameter.BOOLEAN_KEY, "true"));
		promotionRule.addCondition(categoryCondition);

		// Create a condition that the currency is CAD
		RuleCondition currencyCondition = new CartCurrencyConditionImpl();
		RuleParameter currencyParam = new RuleParameterImpl();
		currencyParam.setKey(RuleParameter.CURRENCY_KEY);
		currencyParam.setValue("CAD");
		currencyCondition.addParameter(currencyParam);
		promotionRule.addCondition(currencyCondition);

		// Create an action
		RuleAction discountAction = new CatalogCurrencyAmountDiscountActionImpl();
		RuleParameter discountParameter = new RuleParameterImpl();
		discountParameter.setKey(RuleParameter.DISCOUNT_AMOUNT_KEY);
		discountParameter.setValue("100");
		discountAction.addParameter(discountParameter);
		discountAction.addParameter(currencyParam);
		promotionRule.addAction(discountAction);

		// promotionRule.addAction(discountAction);
		promotionRule.setRuleSet(ruleSet);
		ruleSet.addRule(promotionRule);
		return ruleSet;
	}

	private RuleSet createMockOrderRuleSet() {

		RuleSet ruleSet = new RuleSetImpl();
		ruleSet.setName(RULE_SET_NAME);
		ruleSet.setScenario(RuleScenarios.CART_SCENARIO);

		Rule promotionRule = new PromotionRuleImpl() {
			private static final long serialVersionUID = 514782683364386900L;
			private final Set<RuleAction> actions = new HashSet<RuleAction>();
		
			@Override
			public void addAction(final RuleAction ruleAction) { //NOPMD
				this.actions.add(ruleAction);
			}
			
			@Override
			public void removeAction(final RuleAction ruleAction) {
				this.actions.remove(ruleAction);
			}
			
			@Override
			public Set<RuleAction> getActions() {
				return this.actions;
			}
			
		};
		
		promotionRule.setName("Order Sale");
		
		promotionRule.setStore(new StoreImpl());
		
		// Create a condition that the product is in a particular category
		RuleCondition subtotalCondition = new CartSubtotalConditionImpl();
		RuleParameter subtotalAmountParam = new RuleParameterImpl();
		subtotalAmountParam.setKey(RuleParameter.SUBTOTAL_AMOUNT_KEY);
		subtotalAmountParam.setValue("10000");
		subtotalCondition.addParameter(subtotalAmountParam);
		promotionRule.addCondition(subtotalCondition);

		// Create a condition that the currency is CAD
		RuleCondition currencyCondition = new CartCurrencyConditionImpl();
		RuleParameter currencyParam = new RuleParameterImpl();
		currencyParam.setKey(RuleParameter.CURRENCY_KEY);
		currencyParam.setValue("CAD");
		currencyCondition.addParameter(currencyParam);
		promotionRule.addCondition(currencyCondition);

		// Create an action
		RuleAction discountAction = new CartSubtotalAmountDiscountActionImpl();
		RuleParameter discountParameter = new RuleParameterImpl();
		discountParameter.setKey(RuleParameter.DISCOUNT_AMOUNT_KEY);
		discountParameter.setValue("1000");
		discountAction.addParameter(discountParameter);
		promotionRule.addAction(discountAction);

		// promotionRule.addAction(discountAction);
		promotionRule.setRuleSet(ruleSet);
		ruleSet.addRule(promotionRule);
		return ruleSet;
	}


	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.RuleSetImpl.getName()'.
	 */
	@Test
	public void testGetSetName() {
		assertEquals(ruleSet.getName(), RULE_SET_NAME);
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.RuleSetImpl.getRules()'.
	 */
	@Test
	public void testGetRules() {
		assertTrue(ruleSet.getRules().size() > 0);
	}

	/**
	 * Test method for get and set last modified date.
	 */
	@Test
	public void testGetSetLastModifiedDate() {
		Date date = new Date();
		ruleSet.setLastModifiedDate(date);
		assertEquals(date, ruleSet.getLastModifiedDate());
	}
	
}
