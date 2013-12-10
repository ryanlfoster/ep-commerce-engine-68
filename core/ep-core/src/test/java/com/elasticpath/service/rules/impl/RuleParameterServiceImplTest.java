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
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.impl.RuleParameterImpl;
import com.elasticpath.domain.rules.impl.RuleScenariosImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.rules.RuleParameterService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for <code>RuleParameterServiceImpl</code>.
 */
public class RuleParameterServiceImplTest {

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private RuleParameterService ruleParameterService;

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
		ruleParameterService = new RuleParameterServiceImpl();
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		ruleParameterService.setPersistenceEngine(mockPersistenceEngine);
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.RULE_SCENARIOS);
				will(returnValue(new RuleScenariosImpl()));

				allowing(beanFactory).getBeanImplClass(ContextIdNames.RULE_PARAMETER);
				will(returnValue(RuleParameterImpl.class));
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
		ruleParameterService.setPersistenceEngine(null);
		try {
			ruleParameterService.add(createRuleParameter());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.RuleParameterServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(ruleParameterService.getPersistenceEngine());
	}

	private RuleParameter createRuleParameter() {
		return new RuleParameterImpl(RuleParameter.DISCOUNT_AMOUNT_KEY, "100");
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleParameterServiceImpl.add(RuleParameter)'.
	 */
	@Test
	public void testAdd() {
		final RuleParameter ruleParameter = createRuleParameter();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).save(with(same(ruleParameter)));
			}
		});
		ruleParameterService.add(ruleParameter);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleParameterServiceImpl.update(RuleParameter)'.
	 */
	@Test
	public void testUpdate() {
		final RuleParameter ruleParameter = createRuleParameter();
		final RuleParameter updatedRuleParameter = createRuleParameter();
		final long uidPk = 123456;
		final String displayText = "updatedRuleParameter";
		ruleParameter.setUidPk(uidPk);
		ruleParameter.setDisplayText(displayText);
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).update(with(same(ruleParameter)));
				will(returnValue(updatedRuleParameter));
			}
		});
		final RuleParameter returnedRuleParameter = ruleParameterService.update(ruleParameter);
		assertSame(returnedRuleParameter, updatedRuleParameter);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleParameterServiceImpl.delete(RuleParameter)'.
	 */
	@Test
	public void testRemove() {
		final RuleParameter ruleParameter = createRuleParameter();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).delete(with(same(ruleParameter)));
			}
		});
		ruleParameterService.remove(ruleParameter);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleParameterServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		final long uid = 1234L;
		final RuleParameter ruleParameter = createRuleParameter();
		ruleParameter.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).load(RuleParameterImpl.class, uid);
				will(returnValue(ruleParameter));
			}
		});
		final RuleParameter loadedRuleParameter = ruleParameterService.load(uid);
		assertSame(ruleParameter, loadedRuleParameter);
	}

	/**
	 * Test method for 'com.elasticpath.service.RuleParameterServiceImpl.get(Long)'.
	 */
	@Test
	public void testGet() {
		final long uid = 1234L;
		final RuleParameter ruleParameter = createRuleParameter();
		ruleParameter.setUidPk(uid);
		// expectationss
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).get(RuleParameterImpl.class, uid);
				will(returnValue(ruleParameter));
			}
		});
		final RuleParameter loadedRuleParameter = ruleParameterService.get(uid);
		assertSame(ruleParameter, loadedRuleParameter);
	}

	/**
	 * Test for: Generic get method for all persistable domain models.
	 */
	@Test
	public void testGetObject() {
		final long uid = 1234L;
		final RuleParameter ruleParameter = createRuleParameter();
		ruleParameter.setUidPk(uid);
		// expectationss
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).get(RuleParameterImpl.class, uid);
				will(returnValue(ruleParameter));
			}
		});
		final RuleParameter loadedRuleParameter = (RuleParameter) ruleParameterService.getObject(uid);
		assertSame(ruleParameter, loadedRuleParameter);
	}

}
