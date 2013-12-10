package com.elasticpath.service.rules.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.rules.EpRuleBase;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.impl.EpRuleBaseImpl;
import com.elasticpath.domain.rules.impl.PromotionRuleImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for <code>RuleServiceImpl</code>.
 * <code>RuleServiceImplTest</code> is obsolete
 */
public class RuleServiceImpl2Test {

	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final PersistenceEngine persistenceEngine = context.mock(PersistenceEngine.class);

	private final RuleServiceImpl ruleService = new RuleServiceImpl();

	private static final String FIND_RULE_BY_CODE = "RULE_FIND_BY_CODE";

	/**
	 * Common initialization.
	 */
	@Before
	public void setUp() {
		ruleService.setPersistenceEngine(persistenceEngine);
	}

	/**
	 * <code>RuleService</code> must validate <code>Rule</code> when update,
	 * catch <code>EpDomainException</code> and throw <code>EpServiceException</code> if rule is invalid.
	 */
	@Test(expected = EpServiceException.class)
	public void testUpdate() {
		final Rule mockRule = context.mock(Rule.class);

		context.checking(new Expectations() { {
			oneOf(mockRule).validate(); will(throwException(new EpDomainException("Whatever is incorrect")));
		} });

		ruleService.update(mockRule);
	}

	/**
	 * Checks that if UID is less or equal to 0 then new <code>Rule</code> bean is created.
	 */
	@Test
	public void testLoad() {
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		BeanFactoryExpectationsFactory expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		try {
			final Rule rule = new PromotionRuleImpl();

			context.checking(new Expectations() { {
				exactly(2).of(beanFactory).getBean(with(same(ContextIdNames.PROMOTION_RULE)));
				will(returnValue(rule));
			} });

			assertEquals(rule, ruleService.load(0));
			assertEquals(rule, ruleService.load(-1));
		} finally {
			expectationsFactory.close();
		}
	}

	/**
	 * Checks that if <code>Rule</code> with the given code doens't exist then
	 * <code>RuleService</code> returns null.
	 */
	@Test
	public void testFindByRuleCode() {
		final String ruleCode = "Christmas discount";

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(FIND_RULE_BY_CODE, ruleCode);
			will(returnValue(null));
			oneOf(persistenceEngine).retrieveByNamedQuery(FIND_RULE_BY_CODE, ruleCode);
			will(returnValue(Collections.emptyList()));
		} });

		assertNull(ruleService.findByRuleCode(ruleCode));
		assertNull(ruleService.findByRuleCode(ruleCode));
	}

	/**
	 * Checks that if there are more then one <code>Rule</code> objects corresponding
	 * to the given code then <code>EpServiceException</code> is thrown.
	 */
	@Test(expected = EpServiceException.class)
	public void testFindByRuleCode2() {
		final String ruleCode = "Progressive discount";

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(FIND_RULE_BY_CODE, ruleCode);
			will(returnValue(Arrays.asList(new PromotionRuleImpl(), new PromotionRuleImpl())));
		} });

		ruleService.findByRuleCode(ruleCode);
	}

	/**
	 * Checks successful case of searching for <code>Rule</code> by its code.
	 */
	@Test
	public void testFindByRuleCode3() {
		final String ruleCode = "Progressive discount";
		final Rule promotionRule = new PromotionRuleImpl();

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(FIND_RULE_BY_CODE, ruleCode);
			will(returnValue(Arrays.asList(promotionRule)));
		} });

		assertEquals(promotionRule, ruleService.findByRuleCode(ruleCode));
	}

	/**
	 * <code>RuleService</code> should return empty collection if no one <code>Rule</code> has been found.
	 */
	@Test
	public void testFindByUids() {
		assertEquals(Collections.<Rule>emptyList(), ruleService.findByUids(null));
		assertEquals(Collections.<Rule>emptyList(), ruleService.findByUids(Collections.<Long>emptyList()));
	}

	/**
	 * Checks that Store or Catalog must be provided to find scenario.
	 */
	@Test(expected = EpServiceException.class)
	public void testFindRuleBaseByScenario1() {
		ruleService.findRuleBaseByScenario(null, null, 1);
	}

	/**
	 * Rule base by catalog.
	 * Finds nothing. Should return null.
	 */
	@Test
	public void testFindRuleBaseByScenario2() {
		final Catalog catalog = context.mock(Catalog.class);
		final long catalogUid = 100001L;
		final int scenarioUid = 1;

		context.checking(new Expectations() { {
			oneOf(catalog).getUidPk(); will(returnValue(catalogUid));
			oneOf(persistenceEngine).retrieveByNamedQuery("EP_RULE_BASE_FIND_BY_CATALOG_SCENARIO",
					catalogUid, scenarioUid);
			will(returnValue(null));
		} });

		assertNull(ruleService.findRuleBaseByScenario(null, catalog, scenarioUid));
	}

	/**
	 * Rule base by store.
	 * Query returns more then one <code>Rule</code>. Should throw an exception.
	 */
	@Test(expected = EpServiceException.class)
	public void testFindRuleBaseByScenario3() {
		final Store store = context.mock(Store.class);
		final long storeUid = 10001L;
		final int scenarioUid = 2;

		context.checking(new Expectations() { {
			oneOf(store).getUidPk(); will(returnValue(storeUid));
			oneOf(persistenceEngine).retrieveByNamedQuery("EP_RULE_BASE_FIND_BY_STORE_SCENARIO",
					storeUid, scenarioUid);
			will(returnValue(Arrays.asList(new EpRuleBaseImpl(), new EpRuleBaseImpl())));
		} });

		ruleService.findRuleBaseByScenario(store, null, scenarioUid);
	}

	/**
	 * Both store and catalog are provided.
	 * Successfully finds appropriate <code>Rule</code>.
	 */
	@Test
	public void testFindRuleBaseByScenario4() {
		final EpRuleBase ruleBase = new EpRuleBaseImpl();
		final Store store = context.mock(Store.class);
		final Catalog catalog = context.mock(Catalog.class);
		final long storeUid = 10001L;
		final long catalogUid = 10050L;
		final int scenarioUid = 3;

		context.checking(new Expectations() { {
			oneOf(store).getUidPk(); will(returnValue(storeUid));
			oneOf(catalog).getUidPk(); will(returnValue(catalogUid));
			oneOf(persistenceEngine).retrieveByNamedQuery("EP_RULE_BASE_FIND_BY_STORE_CATALOG_SCENARIO",
					storeUid, catalogUid, scenarioUid);
			will(returnValue(Arrays.asList(ruleBase)));
		} });

		assertEquals(ruleBase, ruleService.findRuleBaseByScenario(store, catalog, scenarioUid));
	}
	
	/**
	 * Verifies that if the query returns more than 1 result that the exception is thrown with the expected message.
	 */
	@Test
	public void testFindChangedStoreRuleBases() {
		final List<EpRuleBase> returnList = new ArrayList<EpRuleBase>();
		returnList.add(new EpRuleBaseImpl());
		returnList.add(new EpRuleBaseImpl());

		context.checking(new Expectations() { {
				oneOf(persistenceEngine).retrieveByNamedQuery(
						"EP_RULE_BASE_FIND_CHANGED_STORECODE_SCENARIO",
						"storeCode", 1, null);
				will(returnValue(returnList));
		} });

		boolean expectedExceptionCaught = false;
		try {
			ruleService.findChangedStoreRuleBases("storeCode", 1, null);
		} catch (EpServiceException e) {
			expectedExceptionCaught = true;
			assertEquals(
					"Inconsistent data, found more than 1 item, expected 1 with store code storeCode, no catalog and scenario 1",
					e.getMessage());
		}

		assertTrue("Expect an EpServiceException", expectedExceptionCaught);

	}
}
