/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.rules.DiscountType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleExceptionType;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/** Test cases for <code>AbstractRuleImpl</code>. */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.TooManyStaticImports" })
public class AbstractRuleImplTest {

	private static final String CAD = "CAD";

	private static final int NUM_ACTIONS = 6;

	private static final int SALIENCE_0 = 0;

	private static final int SALIENCE_M9 = -9;

	private static final int SALIENCE_M2 = -2;

	private static final int SALIENCE_9 = 9;

	private static final int SALIENCE_1 = 1;

	private static final int SALIENCE_3 = 3;

	private static final String RULE_NAME = "Apply discount";

	private static final String RULE_NAME_WITH_UNICODE = RULE_NAME + "\u201C\u201D\u201F";

	private static final String RULE_DESCRIPTION = "This is a rule description. This description describes the rule.";

	private static final String STORE_NAME = "Test Store Name";

	private static final String CM_USER_FIRST_NAME = "Elastic";

	private AbstractRuleImpl ruleImpl;

	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepare for each test.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean("randomGuid", RandomGuidImpl.class);


		ruleImpl = getRuleImpl();
		ruleImpl.initialize();
		ruleImpl.setName("Car Sale");
		ruleImpl.setDescription("This is a rule description.");
		ruleImpl.setEnabled(true);

		// Associate the rule with a Store
		Store store = new StoreImpl();
		store.setName(STORE_NAME);
		ruleImpl.setStore(store);

		// Associate the rule with a CmUser
		CmUser cmUser = new CmUserImpl();
		cmUser.setFirstName(CM_USER_FIRST_NAME);
		ruleImpl.setCmUser(cmUser);

		// Create a condition that the product is in a particular category
		RuleCondition categoryCondition = new ProductCategoryConditionImpl();
		categoryCondition.addParameter(new RuleParameterImpl(RuleParameter.CATEGORY_CODE_KEY, "8"));
		categoryCondition.addParameter(new RuleParameterImpl(RuleParameter.BOOLEAN_KEY, "true"));
		ruleImpl.addCondition(categoryCondition);

		// Create a condition that the currency is CAD
		RuleCondition currencyCondition = new CartCurrencyConditionImpl();
		RuleParameter currencyParam = new RuleParameterImpl();
		currencyParam.setKey(RuleParameter.CURRENCY_KEY);
		currencyParam.setValue(CAD);
		currencyCondition.addParameter(currencyParam);
		ruleImpl.addCondition(currencyCondition);

		// Create an action
		RuleAction discountAction = new CatalogCurrencyAmountDiscountActionImpl();
		RuleParameter discountParameter = new RuleParameterImpl();
		discountParameter.setKey(RuleParameter.DISCOUNT_AMOUNT_KEY);
		discountParameter.setValue("100");
		discountAction.addParameter(discountParameter);
		discountAction.addParameter(currencyParam);
		ruleImpl.addAction(discountAction);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	private AbstractRuleImpl getRuleImpl() { //NOPMD

		ruleImpl = new AbstractRuleImpl() {
			private static final long serialVersionUID = -2979931059608345332L;

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

			@Override
			public String getRuleCode() {
				return null;
			}

			@Override
			public RuleSet getRuleSet() {

				return new RuleSet() {
					private static final long serialVersionUID = 339330148969588440L;

					public void addRule(final Rule rule) { //NOPMD

					}

					public Set<String> getImports() { //NOPMD
						return null;
					}

					public Date getLastModifiedDate() { //NOPMD
						return null;
					}

					public String getName() { //NOPMD
						return null;
					}

					public String getRuleCode(final Store store) //NOPMD
							throws EpDomainException {
						return null;
					}

					public Set<Rule> getRules() { //NOPMD
						return null;
					}

					public int getScenario() {
						return RuleScenarios.CATALOG_BROWSE_SCENARIO;
					}

					public void setLastModifiedDate(final Date lastModifiedDate) { //NOPMD

					}

					public void setName(final String name) { //NOPMD

					}

					public void setRules(final Set<Rule> rules) { //NOPMD

					}

					public void setScenario(final int scenarioId) { //NOPMD

					}

					public void validate() throws EpDomainException { //NOPMD

					}

					public String getGuid() { //NOPMD
						return null;
					}

					public void setGuid(final String guid) { //NOPMD
					}

					public long getUidPk() { //NOPMD
						return 0;
					}

					public boolean isPersisted() { //NOPMD
						return false;
					}

					public void setUidPk(final long uidPk) { //NOPMD

					}

					public void initialize() { //NOPMD

					}
				};
			}

			public boolean isCouponEnabled() {
				return false;
			}

			public void setCouponEnabled(final boolean couponEnabled) { //NOPMD

			}
		};
		return ruleImpl;
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl.getLastModifiedDate()'.
	 */
	@Test
	public void testGetSetLastModifiedDate() {
		final GregorianCalendar date = new GregorianCalendar(2000, 01, 01);
		assertNull(ruleImpl.getLastModifiedDate());
		ruleImpl.setLastModifiedDate(date.getTime());
		assertNotNull(ruleImpl.getLastModifiedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl.getEndDate()'.
	 */
	@Test
	public void testGetSetEndDate() {
		assertNull(ruleImpl.getEndDate());
		ruleImpl.setEndDate(null);
		assertNull(ruleImpl.getEndDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl.getConditions()'.
	 */
	@Test
	public void testGetConditions() {
		assertNotNull(ruleImpl.getConditions());
	}


	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl.addCondition(RuleCondition)'.
	 */
	@Test
	public void testAddCondition() {
		final int numConditions = ruleImpl.getConditions().size();

		// Create a condition and add it to the rule
		RuleCondition currencyCondition = new CartCurrencyConditionImpl();
		RuleParameter currencyParam = new RuleParameterImpl();
		currencyParam.setKey(RuleParameter.CURRENCY_KEY);
		currencyParam.setValue(CAD);
		currencyCondition.addParameter(currencyParam);
		ruleImpl.addCondition(currencyCondition);
		assertEquals(numConditions + 1, ruleImpl.getConditions().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl.removeCondition(RuleCondition)'.
	 */
	@Test
	public void testRemoveCondition() {
		final int numConditions = ruleImpl.getConditions().size();

		// Create a condition and add it to the rule
		RuleCondition currencyCondition = new CartCurrencyConditionImpl();
		RuleParameter currencyParam = new RuleParameterImpl();
		currencyParam.setKey(RuleParameter.CURRENCY_KEY);
		currencyParam.setValue(CAD);
		currencyCondition.addParameter(currencyParam);
		ruleImpl.addCondition(currencyCondition);
		assertEquals(numConditions + 1, ruleImpl.getConditions().size());

		// Remove the condition
		ruleImpl.removeCondition(currencyCondition);
		assertEquals(numConditions, ruleImpl.getConditions().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl.addAction(RuleAction)'.
	 */
	@Test
	public void testAddAction() {
		final int numActions = ruleImpl.getActions().size();

		// Create an action and add it to the rule
		RuleAction discountAction = new CatalogCurrencyAmountDiscountActionImpl();
		RuleParameter discountParameter = new RuleParameterImpl();
		discountParameter.setKey(RuleParameter.DISCOUNT_AMOUNT_KEY);
		discountParameter.setValue("100");
		discountAction.addParameter(discountParameter);

		RuleParameter currencyParameter = new RuleParameterImpl();
		currencyParameter.setKey(RuleParameter.CURRENCY_KEY);
		currencyParameter.setValue(CAD);
		discountAction.addParameter(currencyParameter);

		ruleImpl.addAction(discountAction);
		assertEquals(numActions + 1, ruleImpl.getActions().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl.removeAction(RuleAction)'.
	 */
	@Test
	public void testRemoveAction() {
		int numActions = ruleImpl.getActions().size();

		// Create an action and add it to the rule
		RuleAction discountAction = new CatalogCurrencyAmountDiscountActionImpl();
		RuleParameter discountParameter = new RuleParameterImpl();
		discountParameter.setKey(RuleParameter.DISCOUNT_AMOUNT_KEY);
		discountParameter.setValue("100");
		discountAction.addParameter(discountParameter);

		RuleParameter currencyParameter = new RuleParameterImpl();
		currencyParameter.setKey(RuleParameter.CURRENCY_KEY);
		currencyParameter.setValue(CAD);
		discountAction.addParameter(currencyParameter);

		ruleImpl.addAction(discountAction);
		assertEquals(numActions + 1, ruleImpl.getActions().size());

		// Remove the action
		ruleImpl.removeAction(discountAction);
		assertEquals(numActions, ruleImpl.getActions().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl.getName()'.
	 */
	@Test
	public void testGetSetName() {
		ruleImpl.setName(RULE_NAME);
		assertEquals(ruleImpl.getName(), RULE_NAME);
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl.getName()' to sanitize for quotes.
	 */
	@Test
	public void testGetSetNameWithQuotes() {
		ruleImpl.setName(RULE_NAME_WITH_UNICODE);
		assertEquals(ruleImpl.getName(), RULE_NAME);
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl.getDescription()'.
	 */
	@Test
	public void testGetSetDescription() {
		ruleImpl.setDescription(RULE_DESCRIPTION);
		assertEquals(ruleImpl.getDescription(), RULE_DESCRIPTION);
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl.getStore()'.
	 */
	@Test
	public void testGetStore() {
		assertEquals(ruleImpl.getStore().getName(), STORE_NAME);
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl.getCmUser()'.
	 */
	@Test
	public void testGetCmUser() {
		assertEquals(ruleImpl.getCmUser().getFirstName(), CM_USER_FIRST_NAME);
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl.isEnabled()'.
	 */
	@Test
	public void testIsEnabled() {
		assertEquals(ruleImpl.isEnabled(), true);
		ruleImpl.setEnabled(false);
		assertEquals(false, ruleImpl.isEnabled());
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl'.
	 */
	@Test
	public void testGetSetConditionOperator() {
		ruleImpl.setConditionOperator(true);
		assertTrue(ruleImpl.getConditionOperator());

		ruleImpl.setConditionOperator(false);
		assertFalse(ruleImpl.getConditionOperator());
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleImpl.validate()'.
	 */
	@Test
	public void testValidate() {
		ruleImpl.validate();
	}

	/**
	 * Test that the validate() method checks that the rule has at least one action.
	 */
	@Test
	public void testValidateNoActions() {
		// first, remove all actions from the rule
		for (RuleAction currRuleAction : ruleImpl.getActions()) {
			ruleImpl.removeAction(currRuleAction);
		}
		assertEquals(0, ruleImpl.getActions().size());

		// validate
		try {
			ruleImpl.validate();
			fail("Expected EpDomainException");
		} catch (EpDomainException epde) {
			assertNotNull(epde);
		}
	}

	/**
	 * Test that the validate() method checks that end date cannot be before start date.
	 */
	@Test
	public void testValidateDates() {
		Date startDate = new Date();
		Date endDate = new Date();
		ruleImpl.setStartDate(startDate);
		// set the end date BEFORE the start date
		endDate.setTime(startDate.getTime() - 1);
		ruleImpl.setEndDate(endDate);

		try {
			ruleImpl.validate();
			fail("Expected EpDomainException");
		} catch (EpDomainException epde) {
			assertNotNull(epde);
		}
	}

	/**
	 * Test case for retrieving actions by salience.
	 */
	@Test
	public void testGetActionsBySalience() {
		RuleAction action3 = getAbstractRuleAction(SALIENCE_3);
		RuleAction action1 = getAbstractRuleAction(SALIENCE_1);
		RuleAction action9 = getAbstractRuleAction(SALIENCE_9);
		RuleAction actionM2 = getAbstractRuleAction(SALIENCE_M2);
		RuleAction actionM9 = getAbstractRuleAction(SALIENCE_M9);
		RuleAction action0 = getAbstractRuleAction(SALIENCE_0);

		ruleImpl = getRuleImpl();
		ruleImpl.initialize();
		assertEquals(0, ruleImpl.getActions().size());

		ruleImpl.addAction(action3);
		ruleImpl.addAction(action1);
		ruleImpl.addAction(action9);
		ruleImpl.addAction(actionM2);
		ruleImpl.addAction(actionM9);
		ruleImpl.addAction(action0);

		assertEquals(NUM_ACTIONS, ruleImpl.getActions().size());
		assertEquals(NUM_ACTIONS, ruleImpl.getActionsBySalience().size());

		Iterator<RuleAction> actionIter = ruleImpl.getActionsBySalience().iterator();
		assertEquals(SALIENCE_9, (actionIter.next()).getSalience());
		assertEquals(SALIENCE_3, (actionIter.next()).getSalience());
		assertEquals(SALIENCE_1, (actionIter.next()).getSalience());
		assertEquals(SALIENCE_0, (actionIter.next()).getSalience());
		assertEquals(SALIENCE_M2, (actionIter.next()).getSalience());
		assertEquals(SALIENCE_M9, (actionIter.next()).getSalience());
	}

	private RuleAction getAbstractRuleAction(final int salienceValue) {

		RuleAction abstractRuleActionImpl = new AbstractRuleActionImpl() {
			private static final long serialVersionUID = -5128504106540894316L;

			@Override
			protected String getElementKind() {
				return RuleAction.ACTION_KIND;
			}

			@Override
			public RuleElementType getElementType() {
				return null;
			}

			@Override
			public boolean appliesInScenario(final int scenarioId) {
				return false;
			}

			@Override
			public String[] getParameterKeys() {
				return null;
			}

			@Override
			public RuleExceptionType[] getAllowedExceptions() {
				return null;
			}

			@Override
			public String getRuleCode() throws EpDomainException {
				return null;
			}

			public DiscountType getDiscountType() {
				return null;
			}
		};

		abstractRuleActionImpl.setSalience(salienceValue);
		return abstractRuleActionImpl;
	}
}
