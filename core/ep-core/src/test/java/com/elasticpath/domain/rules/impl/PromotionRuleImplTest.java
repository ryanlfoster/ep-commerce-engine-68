/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/** Test cases for <code>PromotionRuleImpl</code>. */
public class PromotionRuleImplTest {

	private static final String CAD = "CAD";
	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private BeanFactoryExpectationsFactory expectationsFactory;

	private static final String VALID_RULE_NAME = "Valid Rule Name";
	private static final long PROMOTION_RULE_UID = 12345654321L;
	private PromotionRuleImpl promotionRuleImpl;

	/**
	 * Prepare for each test.
	 * 
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		promotionRuleImpl = (PromotionRuleImpl) getTestPromotionRule();
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test for setting/getting the rule name.
	 */
	@Test
	public void testSetRuleName() {
		promotionRuleImpl.setName(VALID_RULE_NAME);
		assertSame(VALID_RULE_NAME, promotionRuleImpl.getName());
		promotionRuleImpl.setName(" \"Invalid\" Name ");
		String returnedName = promotionRuleImpl.getName();
		assertEquals(-1, returnedName.indexOf('\"'));
	}

	/**
	 * Creates a test promotion rule.
	 * @return a promotion rule
	 */
	public Rule getTestPromotionRule() {
		PromotionRuleImpl promotionRuleImpl = new PromotionRuleImpl() {
			private static final long serialVersionUID = -8201010936030693769L;
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
		promotionRuleImpl.setUidPk(PROMOTION_RULE_UID);

		// Create a condition that the product is in a particular category
		RuleCondition categoryCondition = new ProductCategoryConditionImpl();
		categoryCondition.addParameter(new RuleParameterImpl(RuleParameter.BOOLEAN_KEY, "true"));
		categoryCondition.addParameter(new RuleParameterImpl(RuleParameter.CATEGORY_CODE_KEY, "8"));
		promotionRuleImpl.addCondition(categoryCondition);

		// Create a condition that the currency is CAD
		RuleCondition currencyCondition = new CartCurrencyConditionImpl();
		currencyCondition.addParameter(new RuleParameterImpl(RuleParameter.CURRENCY_KEY, CAD));
		promotionRuleImpl.addCondition(currencyCondition);
				
		// Create an action
		RuleAction discountAction = new CatalogCurrencyAmountDiscountActionImpl();
		discountAction.addParameter(new RuleParameterImpl(RuleParameter.DISCOUNT_AMOUNT_KEY, "100"));
		discountAction.addParameter(new RuleParameterImpl(RuleParameter.CURRENCY_KEY, CAD));
		discountAction.setRuleId(PROMOTION_RULE_UID);
		promotionRuleImpl.addAction(discountAction);
		
		RuleSet ruleSetImpl = new RuleSetImpl();
		ruleSetImpl.setScenario(RuleScenarios.CATALOG_BROWSE_SCENARIO);
		promotionRuleImpl.setRuleSet(ruleSetImpl);		
		
		return promotionRuleImpl;
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.PromotionRuleImpl.getRuleCode()'.
	 */
	@Test
	public void testGetRuleCode() {
		String code = promotionRuleImpl.getRuleCode();
		
		assertTrue(code.indexOf("delegate") > 0);
		assertTrue(code.indexOf("cart") > 0);
		assertTrue(code.indexOf("product") > 0);
		assertTrue(code.indexOf("end") > 0);
		
		promotionRuleImpl.getRuleSet().setScenario(RuleScenarios.CART_SCENARIO);
		code = promotionRuleImpl.getRuleCode();
		assertEquals(-1, code.indexOf("Product ( available == true )"));
	}
	
	/**
	 * Test that the rule uid is being set properly.
	 */
	@Test
	public void testTraceability() {
		assertTrue(promotionRuleImpl.getActions().size() > 0);
		
		for (RuleAction currAction : promotionRuleImpl.getActions()) {
			final String currActionRuleCode = currAction.getRuleCode();
			assertTrue(currActionRuleCode.indexOf(String.valueOf(PROMOTION_RULE_UID)) > 0);
		}
		
//		for (Iterator ruleActionIter = promotionRuleImpl.getActions().iterator(); ruleActionIter.hasNext();) {
//			RuleAction currAction = (RuleAction) ruleActionIter.next();
//			assertTrue(currAction.getRuleCode().indexOf(PROMOTION_RULE_UID + "") > 0);
//		}
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.PromotionRuleImpl.getRuleCode()'.
	 */
	@Test(expected = EpDomainException.class)
	public void testGetRuleCodeFailure() {
		// first, remove all actions from the rule
		for (RuleAction currRuleAction : promotionRuleImpl.getActions()) {
			promotionRuleImpl.removeAction(currRuleAction);
		}
		assertEquals(0, promotionRuleImpl.getActions().size());
		
		promotionRuleImpl.getRuleCode();
	}


	/**
	 * Logical operator || should be used inside of eval() to combine conditions just like as eligibilities.
	 */
	@Test
	public void testConditionOr() {
		promotionRuleImpl.setConditionOperator(Rule.OR_OPERATOR);
		String code = promotionRuleImpl.getRuleCode();
		assertTrue(code.indexOf("||") > 0);		
	}
	
	
	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl'.
	 */
	@Test
	public void testValidDiscountAmount() {
		promotionRuleImpl.validate();
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl'.
	 */
	@Test(expected = EpDomainException.class)
	public void testInvalidDiscountAmount() {
		// first, remove all actions from the rule
		for (RuleAction currRuleAction : promotionRuleImpl.getActions()) {
			promotionRuleImpl.removeAction(currRuleAction);
		}
		assertEquals(0, promotionRuleImpl.getActions().size());

		// Create an action
		RuleAction discountAction = new CatalogCurrencyAmountDiscountActionImpl();
		discountAction.addParameter(new RuleParameterImpl(RuleParameter.DISCOUNT_AMOUNT_KEY, "-1"));
		discountAction.addParameter(new RuleParameterImpl(RuleParameter.CURRENCY_KEY, CAD));

		promotionRuleImpl.addAction(discountAction);

		promotionRuleImpl.validate();
	}
  
	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl'.
	 */
	@Test(expected = EpDomainException.class)
	public void testInvalidDiscountAmount2() {
		// first, remove all actions from the rule
		for (RuleAction currRuleAction : promotionRuleImpl.getActions()) {
			promotionRuleImpl.removeAction(currRuleAction);
		}
		assertEquals(0, promotionRuleImpl.getActions().size());

		// Create an action
		RuleAction discountAction = new CartSubtotalAmountDiscountActionImpl();
		discountAction.addParameter(new RuleParameterImpl(RuleParameter.DISCOUNT_AMOUNT_KEY, "0"));
		promotionRuleImpl.addAction(discountAction);

		promotionRuleImpl.validate();
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl'.
	 */
	@Test(expected = EpDomainException.class)
	public void testInvalidDiscountAmount3() {
		// first, remove all actions from the rule
		for (RuleAction currRuleAction : promotionRuleImpl.getActions()) {
			promotionRuleImpl.removeAction(currRuleAction);
		}
		assertEquals(0, promotionRuleImpl.getActions().size());

		// Create an action
		RuleAction discountAction = new CatalogCurrencyAmountDiscountActionImpl();
		discountAction.addParameter(new RuleParameterImpl(RuleParameter.DISCOUNT_AMOUNT_KEY, "101"));

		promotionRuleImpl.addAction(discountAction);

		promotionRuleImpl.validate();
	}
	
	/**
	 * Test that the display name gets the correct localized name and does not fall back to
	 * the marketing name.
	 */
	@Test
	public void testDisplayName() {
		final LocalizedPropertiesImpl localizedPropertiesImpl = new LocalizedPropertiesImpl();
		final RuleLocalizedPropertyValueImpl ruleLocalizedPropertyValueImpl = new RuleLocalizedPropertyValueImpl();
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.UTILITY); will(returnValue(new UtilityImpl()));
				oneOf(beanFactory).getBean(ContextIdNames.LOCALIZED_PROPERTIES); will(returnValue(localizedPropertiesImpl));
				oneOf(beanFactory).getBean(ContextIdNames.RULE_LOCALIZED_PROPERTY_VALUE); will(returnValue(ruleLocalizedPropertyValueImpl));
			}
		});
		
		promotionRuleImpl.setName("Marketing-only name");
		LocalizedProperties localizedProperties = promotionRuleImpl.getLocalizedProperties();
		localizedProperties.setValue(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.ENGLISH, "English Display Name");
		assertEquals("Display name should return the localized english name", "English Display Name", 
				promotionRuleImpl.getDisplayName(Locale.ENGLISH));
		assertNull("A non-localized display name should not fall back", promotionRuleImpl.getDisplayName(Locale.FRENCH));
	}
}
