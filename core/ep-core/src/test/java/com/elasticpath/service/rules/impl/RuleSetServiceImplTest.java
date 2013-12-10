/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.rules.impl.CartSubtotalAmountDiscountActionImpl;
import com.elasticpath.domain.rules.impl.PromotionRuleImpl;
import com.elasticpath.domain.rules.impl.RuleParameterImpl;
import com.elasticpath.domain.rules.impl.RuleScenariosImpl;
import com.elasticpath.domain.rules.impl.RuleSetImpl;
import com.elasticpath.service.rules.RuleSetService;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test cases for <code>RuleSetServiceImpl</code>.
 */
public class RuleSetServiceImplTest extends AbstractEPServiceTestCase {

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private RuleSetService ruleSetService;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		ruleSetService = new RuleSetServiceImpl();
		ruleSetService.setPersistenceEngine(getPersistenceEngine());

		stubGetBean(ContextIdNames.RULE_SCENARIOS, RuleScenariosImpl.class);
		stubGetBean(ContextIdNames.RULE_SET, RuleSetImpl.class);
	}


	/**
	 * Test behaviour when the persistence engine is not set.
	 */
	@Test
	public void testPersistenceEngineIsNull() {
		ruleSetService.setPersistenceEngine(null);
		try {
			ruleSetService.add(createRuleSet());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.RuleSetServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(ruleSetService.getPersistenceEngine());
	}

	private RuleSet createRuleSet() {
		RuleSetImpl ruleSet = new RuleSetImpl();
		ruleSet.addRule(createRule());
		return ruleSet;
	}

	private Rule createRule() {
		Rule rule = new PromotionRuleImpl() {
			private static final long serialVersionUID = -7283226597967477194L;
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
		rule.initialize();

		RuleAction action = new CartSubtotalAmountDiscountActionImpl();
		action.addParameter(new RuleParameterImpl(RuleParameter.DISCOUNT_AMOUNT_KEY, "100"));
		rule.addAction(action);

		return rule;
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleSetServiceImpl.add(RuleSet)'.
	 */
	@Test
	public void testAdd() {
		final RuleSet ruleSet = createRuleSet();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).save(with(same(ruleSet)));
			}
		});
		ruleSetService.add(ruleSet);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleSetServiceImpl.add(RuleSet)'.
	 */
	@Test
	public void testAddFail() {
		final RuleSet ruleSet = new RuleSetImpl();
		PromotionRuleImpl invalidRule = new PromotionRuleImpl() {
			private static final long serialVersionUID = 5914031303465847884L;
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
		ruleSet.addRule(invalidRule);
		try {
			ruleSetService.add(ruleSet);
			fail("Expected a domain exception");
		} catch (Exception e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleSetServiceImpl.update(RuleSet)'.
	 */
	@Test
	public void testUpdate() {
		final RuleSet ruleSet = createRuleSet();
		final RuleSet updatedRuleSet = createRuleSet();
		final long uidPk = 123456;
		final String name = "updatedRuleSet";
		ruleSet.setUidPk(uidPk);
		ruleSet.setName(name);
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).merge(with(same(ruleSet)));
				will(returnValue(updatedRuleSet));
			}
		});
		final RuleSet returnedRuleSet = ruleSetService.update(ruleSet);
		assertSame(returnedRuleSet, updatedRuleSet);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleSetServiceImpl.delete(RuleSet)'.
	 */
	@Test
	public void testRemove() {
		final RuleSet ruleSet = createRuleSet();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).delete(with(same(ruleSet)));
			}
		});
		ruleSetService.remove(ruleSet);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleSetServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		final long uid = 1234L;
		final RuleSet ruleSet = createRuleSet();
		ruleSet.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).load(RuleSetImpl.class, uid);
				will(returnValue(ruleSet));
			}
		});
		final RuleSet loadedRuleSet = ruleSetService.load(uid);
		assertSame(ruleSet, loadedRuleSet);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleSetServiceImpl.findByScenarioId(int)'.
	 */
	@Test
	public void testFindByScenarioId() {
		final int uid = 1234;
		final RuleSet ruleSet = createRuleSet();
		ruleSet.setUidPk(uid);
		final List<RuleSet> ruleSetList = new ArrayList<RuleSet>();
		ruleSetList.add(ruleSet);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(ruleSetList));
			}
		});
		final RuleSet loadedRuleSet = ruleSetService.findByScenarioId(uid);
		assertSame(ruleSet, loadedRuleSet);
	}

	/**
	 * Test method for 'com.elasticpath.service.RuleSetServiceImpl.get(Long)'.
	 */
	@Test
	public void testGet() {
		final long uid = 1234L;
		final RuleSet ruleSet = createRuleSet();
		ruleSet.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).get(RuleSetImpl.class, uid);
				will(returnValue(ruleSet));
			}
		});
		final RuleSet loadedRuleSet = ruleSetService.get(uid);
		assertSame(ruleSet, loadedRuleSet);
	}

	/**
	 * Test for: Generic get method for all persistable domain models.
	 */
	@Test
	public void testGetObject() {
		final long uid = 1234L;
		final RuleSet ruleSet = createRuleSet();
		ruleSet.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).get(RuleSetImpl.class, uid);
				will(returnValue(ruleSet));
			}
		});
		final RuleSet loadedRuleSet = (RuleSet) ruleSetService.getObject(uid);
		assertSame(ruleSet, loadedRuleSet);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleSetServiceImpl.findByName(String)'.
	 */
	@Test
	public void testFindByName() {
		final String name = "catalog";
		final RuleSet ruleSet = createRuleSet();
		ruleSet.setName(name);
		final List<RuleSet> ruleSetList = new ArrayList<RuleSet>();
		ruleSetList.add(ruleSet);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("RULESET_FIND_BY_NAME", name);
				will(returnValue(ruleSetList));
			}
		});
		final RuleSet loadedRuleSet = ruleSetService.findByName(name);
		assertSame(ruleSet, loadedRuleSet);
	}


}
