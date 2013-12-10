/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.impl.CartSubtotalAmountDiscountActionImpl;
import com.elasticpath.domain.rules.impl.RuleParameterImpl;
import com.elasticpath.domain.rules.impl.RuleScenariosImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.rules.RuleActionService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for <code>RuleActionServiceImpl</code>.
 */
public class RuleActionServiceImplTest {

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private RuleActionService ruleActionService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PersistenceEngine mockPersistenceEngine;

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;


	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		ruleActionService = new RuleActionServiceImpl();
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		ruleActionService.setPersistenceEngine(mockPersistenceEngine);
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.RULE_SCENARIOS);
				will(returnValue(new RuleScenariosImpl()));
			}
		});
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test behaviour when the persistence engine is not set.
	 */
	@Test
	public void testPersistenceEngineIsNull() {
		ruleActionService.setPersistenceEngine(null);
		try {
			ruleActionService.add(createRuleAction());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleActionServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(ruleActionService.getPersistenceEngine());
	}

	private RuleAction createRuleAction() {
		CartSubtotalAmountDiscountActionImpl ruleAction = new CartSubtotalAmountDiscountActionImpl();
		ruleAction.initialize();
		ruleAction.addParameter(new RuleParameterImpl(RuleParameter.DISCOUNT_AMOUNT_KEY, "100"));
		return ruleAction;
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleActionServiceImpl.add(RuleAction)'.
	 */
	@Test
	public void testAdd() {
		final RuleAction ruleAction = createRuleAction();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).save(with(same(ruleAction)));
			}
		});
		ruleActionService.add(ruleAction);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleActionServiceImpl.update(RuleAction)'.
	 */
	@Test
	public void testUpdate() {
		final RuleAction ruleAction = createRuleAction();
		final RuleAction updatedRuleAction = createRuleAction();
		final long uidPk = 123456;
		final String type = "updatedType";
		ruleAction.setUidPk(uidPk);
		ruleAction.setType(type);
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).update(with(same(ruleAction)));
				will(returnValue(updatedRuleAction));
			}
		});
		final RuleAction returnedRuleAction = ruleActionService.update(ruleAction);
		assertSame(returnedRuleAction, updatedRuleAction);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleActionServiceImpl.delete(RuleAction)'.
	 */
	@Test
	public void testRemove() {
		final RuleAction ruleAction = createRuleAction();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).delete(with(same(ruleAction)));
			}
		});
		ruleActionService.remove(ruleAction);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleActionServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		final long uid = 1234L;
		final RuleAction ruleAction = createRuleAction();
		ruleAction.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).load(RuleAction.class, uid);
				will(returnValue(ruleAction));
			}
		});
		final RuleAction loadedRuleAction = ruleActionService.load(uid);
		assertSame(ruleAction, loadedRuleAction);
	}

	/**
	 * Test method for 'com.elasticpath.service.RuleActionServiceImpl.get(Long)'.
	 */
	@Test
	public void testGet() {
		final long uid = 1234L;
		final RuleAction ruleAction = createRuleAction();
		ruleAction.setUidPk(uid);
		// expectationss
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).get(RuleAction.class, uid);
				will(returnValue(ruleAction));
			}
		});
		final RuleAction loadedRuleAction = ruleActionService.get(uid);
		assertSame(ruleAction, loadedRuleAction);
	}

	/**
	 * Test for: Generic get method for all persistable domain models.
	 */
	@Test
	public void testGetObject() {
		final long uid = 1234L;
		final RuleAction ruleAction = createRuleAction();
		ruleAction.setUidPk(uid);
		// expectationss
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).get(RuleAction.class, uid);
				will(returnValue(ruleAction));
			}
		});
		final RuleAction loadedRuleAction = (RuleAction) ruleActionService.getObject(uid);
		assertSame(ruleAction, loadedRuleAction);
	}

}
