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
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.impl.CartSubtotalConditionImpl;
import com.elasticpath.domain.rules.impl.RuleParameterImpl;
import com.elasticpath.domain.rules.impl.RuleScenariosImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.rules.RuleConditionService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for <code>RuleConditionServiceImpl</code>.
 */
public class RuleConditionServiceImplTest {

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private RuleConditionService ruleConditionService;

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
		ruleConditionService = new RuleConditionServiceImpl();
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		ruleConditionService.setPersistenceEngine(mockPersistenceEngine);
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
		ruleConditionService.setPersistenceEngine(null);
		try {
			ruleConditionService.add(createRuleCondition());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.RuleConditionServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(ruleConditionService.getPersistenceEngine());
	}

	private RuleCondition createRuleCondition() {
		CartSubtotalConditionImpl ruleCondition = new CartSubtotalConditionImpl();
		ruleCondition.initialize();
		ruleCondition.addParameter(new RuleParameterImpl(RuleParameter.DISCOUNT_AMOUNT_KEY, "100"));
		return ruleCondition;
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleConditionServiceImpl.add(RuleCondition)'.
	 */
	@Test
	public void testAdd() {
		final RuleCondition ruleCondition = createRuleCondition();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).save(with(same(ruleCondition)));
			}
		});
		ruleConditionService.add(ruleCondition);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleConditionServiceImpl.update(RuleCondition)'.
	 */
	@Test
	public void testUpdate() {
		final RuleCondition ruleCondition = createRuleCondition();
		final RuleCondition updatedRuleCondition = createRuleCondition();
		final long uidPk = 123456;
		final String type = "updatedRuleConditionType";
		ruleCondition.setUidPk(uidPk);
		ruleCondition.setType(type);
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).update(with(same(ruleCondition)));
				will(returnValue(updatedRuleCondition));
			}
		});
		final RuleCondition returnedRuleCondition = ruleConditionService.update(ruleCondition);
		assertSame(returnedRuleCondition, updatedRuleCondition);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleConditionServiceImpl.delete(RuleCondition)'.
	 */
	@Test
	public void testRemove() {
		final RuleCondition ruleCondition = createRuleCondition();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).delete(with(same(ruleCondition)));
			}
		});
		ruleConditionService.remove(ruleCondition);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleConditionServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		final long uid = 1234L;
		final RuleCondition ruleCondition = createRuleCondition();
		ruleCondition.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).load(RuleCondition.class, uid);
				will(returnValue(ruleCondition));
			}
		});
		final RuleCondition loadedRuleCondition = ruleConditionService.load(uid);
		assertSame(ruleCondition, loadedRuleCondition);
	}

	/**
	 * Test method for 'com.elasticpath.service.RuleConditionServiceImpl.get(Long)'.
	 */
	@Test
	public void testGet() {
		final long uid = 1234L;
		final RuleCondition ruleCondition = createRuleCondition();
		ruleCondition.setUidPk(uid);
		// expectationss
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).get(RuleCondition.class, uid);
				will(returnValue(ruleCondition));
			}
		});
		final RuleCondition loadedRuleCondition = ruleConditionService.get(uid);
		assertSame(ruleCondition, loadedRuleCondition);
	}

	/**
	 * Test for: Generic get method for all persistable domain models.
	 */
	@Test
	public void testGetObject() {
		final long uid = 1234L;
		final RuleCondition ruleCondition = createRuleCondition();
		ruleCondition.setUidPk(uid);
		// expectationss
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).get(RuleCondition.class, uid);
				will(returnValue(ruleCondition));
			}
		});
		final RuleCondition loadedRuleCondition = (RuleCondition) ruleConditionService.getObject(uid);
		assertSame(ruleCondition, loadedRuleCondition);
	}

}
