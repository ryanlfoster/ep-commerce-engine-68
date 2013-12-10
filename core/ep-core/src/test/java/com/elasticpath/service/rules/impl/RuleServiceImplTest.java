/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.junit.Test;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateNameException;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.rules.impl.CartSubtotalAmountDiscountActionImpl;
import com.elasticpath.domain.rules.impl.CartSubtotalConditionImpl;
import com.elasticpath.domain.rules.impl.PromotionRuleImpl;
import com.elasticpath.domain.rules.impl.RuleParameterImpl;
import com.elasticpath.domain.rules.impl.RuleScenariosImpl;
import com.elasticpath.domain.rules.impl.RuleSetImpl;
import com.elasticpath.domain.rules.impl.SkuExceptionImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.ProcessingHook;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.RuleSetService;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.test.jmock.AbstractCatalogDataTestCase;

/**
 * Test cases for <code>RuleServiceImpl</code>.
 * Obsolete. Use <code>RuleServiceImpl2Test</code> instead.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class RuleServiceImplTest extends AbstractCatalogDataTestCase {

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private static final Date MOCKED_CURRENT_TIME = new Date();

	private RuleServiceImpl ruleServiceImpl;

	private PersistenceEngine mockPersistenceEngine;

	private RuleSetService mockRuleSetService;

	private TimeService mockTimeService;

	private IndexNotificationService mockIndexNotificationService;

	private FetchPlanHelper mockFetchPlanHelper;


	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		stubGetBean(ContextIdNames.PROMOTION_RULE, PromotionRuleImpl.class);
		stubGetBean(ContextIdNames.RULE_SCENARIOS, RuleScenariosImpl.class);
		stubGetBean(ContextIdNames.CART_SUBTOTAL_COND, CartSubtotalConditionImpl.class);

		mockFetchPlanHelper = getFetchPlanHelper();

		ruleServiceImpl = new RuleServiceImpl();
		mockPersistenceEngine = getMockPersistenceEngine();
		mockIndexNotificationService = context.mock(IndexNotificationService.class);
		ruleServiceImpl.setPersistenceEngine(mockPersistenceEngine);
		ruleServiceImpl.setIndexNotificationService(mockIndexNotificationService);
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).bulkUpdate(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(1));
			}
		});

		mockTimeService = context.mock(TimeService.class);
		context.checking(new Expectations() {
			{
				allowing(mockTimeService).getCurrentTime();
				will(returnValue(MOCKED_CURRENT_TIME));
			}
		});
		ruleServiceImpl.setTimeService(mockTimeService);

		// Mock up the rule set service
		mockRuleSetService = context.mock(RuleSetService.class);
		ruleServiceImpl.setRuleSetService(mockRuleSetService);

		ruleServiceImpl.setFetchPlanHelper(getMockFetchPlanHelper());
		context.checking(new Expectations() {
			{
				allowing(getMockFetchPlanHelper()).configureFetchGroupLoadTuner(with(aNull(FetchGroupLoadTuner.class)));
			}
		});
	}

	/**
	 * Test behaviour when the persistence engine is not set.
	 */
	@Test
	public void testPersistenceEngineIsNull() {
		ruleServiceImpl.setPersistenceEngine(null);
		try {
			ruleServiceImpl.add(createRule());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.RuleServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(ruleServiceImpl.getPersistenceEngine());
	}

	private Rule createRule() {
		Rule rule = new PromotionRuleImpl() {
			private static final long serialVersionUID = -2630300498172549097L;
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

		RuleSet ruleSet = new RuleSetImpl();
		rule.setRuleSet(ruleSet);
		return rule;
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleServiceImpl.add(Rule)'.
	 */
	@Test
	public void testAdd() {
		final Rule rule = createRule();

		context.checking(new Expectations() {
			{
				// Validate that save method is called
				oneOf(mockPersistenceEngine).save(with(same(rule)));

				// Validate method should be called for check for duplicate names and then duplicate promo codes

				atLeast(1).of(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(new ArrayList<Rule>()));

				oneOf(mockIndexNotificationService).addNotificationForEntityIndexUpdate(IndexType.PROMOTION, rule.getUidPk());
				// Validate set of RuleSet's last modified time
				oneOf(mockRuleSetService).updateLastModifiedTime(with(same(rule.getRuleSet())));
			}
		});

		ruleServiceImpl.add(rule);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleServiceImpl.add(Rule)'.
	 */
	@Test
	public void testAddDuplicateName() {
		final Rule rule = createRule();

		final List<Rule> ruleList = new ArrayList<Rule>();
		final Rule existingRule = createRule();
		existingRule.setUidPk(1);
		ruleList.add(existingRule);

		// expectations
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(ruleList));
			}
		});

		try {
			ruleServiceImpl.add(rule);
			fail("Expected a duplicate name exception");
		} catch (DuplicateNameException dne) {
			// success
			assertNotNull(dne);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleServiceImpl.add(Rule)'.
	 */
	@Test
	public void testAddFail() {
		final Rule rule = new PromotionRuleImpl();
		try {
			ruleServiceImpl.add(rule);
			fail("Expected a service exception");
		} catch (EpServiceException epde) {
			assertNotNull(epde);
		}
	}

	/**
	 * Test method for {@link RuleServiceImpl#add(Rule)} with {@link ProcessingHook}s.
	 */
	@Test
	public void testAddWithHooks() {
		final Rule rule = createRule();
		context.checking(new Expectations() {
			{
				final Sequence ruleSequence = context.sequence("testAddWithHooks ruleSequence");

				final ProcessingHook mockHook = context.mock(ProcessingHook.class);
				oneOf(mockHook).preAdd(with(same(rule)));
				inSequence(ruleSequence);
				ruleServiceImpl.setProcessingHook(mockHook);

				// Validate that save method is called
				oneOf(mockPersistenceEngine).save(with(same(rule)));
				inSequence(ruleSequence);

				oneOf(mockHook).postAdd(with(same(rule)));
				inSequence(ruleSequence);

				// Validate method should be called for check for duplicate names and then duplicate promo codes

				atLeast(1).of(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(new ArrayList<Rule>()));

				// Validate set of RuleSet's last modified time
				oneOf(mockRuleSetService).updateLastModifiedTime(with(same(rule.getRuleSet())));
				oneOf(mockIndexNotificationService).addNotificationForEntityIndexUpdate(IndexType.PROMOTION, rule.getUidPk());
			}
		});

		ruleServiceImpl.add(rule);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleServiceImpl.update(Rule)'.
	 */
	@Test
	public void testUpdate() {
		final Rule rule = createRule();
		final Rule updatedRule = createRule();
		final long uidPk = 123456;
		final String name = "updatedRule";
		rule.setUidPk(uidPk);
		rule.setName(name);

		// Need to load previous rule for hookable interface
		context.checking(new Expectations() {
			{
				final Sequence ruleSequence = context.sequence("testUpdate ruleSequence");

				oneOf(mockPersistenceEngine).loadWithNewSession(PromotionRuleImpl.class, uidPk);
				will(returnValue(rule));
				inSequence(ruleSequence);

				oneOf(mockPersistenceEngine).merge(with(same(rule)));
				will(returnValue(updatedRule));
				inSequence(ruleSequence);

				// Validate method should be called for check for duplicate names and then duplicate promo codes
				atLeast(1).of(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(new ArrayList<Rule>()));

				// Validate set of RuleSet's last modified time
				oneOf(mockRuleSetService).updateLastModifiedTime(with(same(updatedRule.getRuleSet())));
				oneOf(mockIndexNotificationService).addNotificationForEntityIndexUpdate(IndexType.PROMOTION, updatedRule.getUidPk());
			}
		});

		final Rule returnedRule = ruleServiceImpl.update(rule);
		assertSame(returnedRule, updatedRule);
	}

	/**
	 * Test method for {@link RuleServiceImpl#update(Rule)} with {@link ProcessingHook}s.
	 */
	@Test
	public void testUpdateWithHooks() {
		final Rule mockOldRule = context.mock(Rule.class);
		final Rule oldRule = mockOldRule;
		final Rule rule = createRule();
		final Rule updatedRule = createRule();
		final long uidPk = 123456;
		final String name = "updatedRule";
		rule.setUidPk(uidPk);
		rule.setName(name);

		context.checking(new Expectations() {
			{
				// Need to load previous rule for hookable interface
				oneOf(mockPersistenceEngine).loadWithNewSession(PromotionRuleImpl.class, uidPk);
				will(returnValue(oldRule));

				final Sequence ruleSequence = context.sequence("testUpdateWithHooks ruleSequence");

				final ProcessingHook mockHook = context.mock(ProcessingHook.class);
				oneOf(mockHook).preUpdate(with(same(oldRule)), with(same(rule)));
				ruleServiceImpl.setProcessingHook(mockHook);
				inSequence(ruleSequence);

				oneOf(mockPersistenceEngine).merge(with(same(rule)));
				will(returnValue(updatedRule));
				inSequence(ruleSequence);

				oneOf(mockHook).postUpdate(with(same(oldRule)), with(same(updatedRule)));
				inSequence(ruleSequence);

				// Validate method should be called for check for duplicate names and then duplicate promo codes
				atLeast(1).of(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(new ArrayList<Rule>()));

				oneOf(mockIndexNotificationService).addNotificationForEntityIndexUpdate(IndexType.PROMOTION, updatedRule.getUidPk());

				// Validate set of RuleSet's last modified time
				oneOf(mockRuleSetService).updateLastModifiedTime(with(same(updatedRule.getRuleSet())));
			}
		});

		final Rule returnedRule = ruleServiceImpl.update(rule);
		assertSame(returnedRule, updatedRule);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleServiceImpl.delete(Rule)'.
	 */
	@Test
	public void testRemove() {
		final CouponConfigService mockCouponConfigService = context.mock(CouponConfigService.class);
		ruleServiceImpl.setCouponConfigService(mockCouponConfigService);

		final Rule rule = createRule();

		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).delete(with(same(rule)));
				oneOf(mockRuleSetService).updateLastModifiedTime(with(same(rule.getRuleSet())));

				oneOf(mockCouponConfigService).findByRuleCode(with(same(rule.getCode())));
				will((returnValue(null)));
			}
		});
		ruleServiceImpl.remove(rule);
	}

	/**
	 * Test method for {@link RuleServiceImpl#remove(Rule)} with {@link ProcessingHook}s.
	 */
	@Test
	public void testRemoveWithHooks() {
		final CouponConfigService mockCouponConfigService = context.mock(CouponConfigService.class);
		ruleServiceImpl.setCouponConfigService(mockCouponConfigService);
		final Rule rule = createRule();
		context.checking(new Expectations() {
			{
				final Sequence ruleSequence = context.sequence("testRemoveWithHooks ruleSequence");
				final ProcessingHook mockHook = context.mock(ProcessingHook.class);
				oneOf(mockHook).preDelete(with(same(rule)));
				inSequence(ruleSequence);

				oneOf(mockPersistenceEngine).delete(with(same(rule)));
				inSequence(ruleSequence);

				oneOf(mockRuleSetService).updateLastModifiedTime(with(same(rule.getRuleSet())));

				oneOf(mockHook).postDelete(with(same(rule)));
				inSequence(ruleSequence);

				ruleServiceImpl.setProcessingHook(mockHook);

				oneOf(mockCouponConfigService).findByRuleCode(with(same(rule.getCode())));
				will((returnValue(null)));
			}
		});

		ruleServiceImpl.remove(rule);
	}

	/**
	 * Test method for 'com.elasticpath.service.ruleServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		final long uid = 1234L;
		final Rule rule = createRule();
		rule.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).load(PromotionRuleImpl.class, uid);
				will(returnValue(rule));
			}
		});
		final Rule loadedRule = ruleServiceImpl.load(uid);
		assertSame(rule, loadedRule);
	}

	/**
	 * Test method for 'com.elasticpath.service.RuleServiceImpl.get(Long)'.
	 */
	@Test
	public void testGet() {
		final long uid = 1234L;
		final Rule rule = createRule();
		rule.setUidPk(uid);
		context.checking(new Expectations() {
			{
				oneOf(getMockFetchPlanHelper()).clearFetchPlan();

				allowing(mockPersistenceEngine).get(PromotionRuleImpl.class, uid);
				will(returnValue(rule));
			}
		});
		final Rule loadedRule = ruleServiceImpl.get(uid);
		assertSame(rule, loadedRule);
	}

	/**
	 * Test for: Generic get method for all persistable domain models.
	 */
	@Test
	public void testGetObject() {
		final long uid = 1234L;
		final Rule rule = createRule();
		rule.setUidPk(uid);
		// expectationss
		context.checking(new Expectations() {
			{
				oneOf(getMockFetchPlanHelper()).clearFetchPlan();

				allowing(mockPersistenceEngine).get(PromotionRuleImpl.class, uid);
				will(returnValue(rule));
			}
		});
		final Rule loadedRule = (Rule) ruleServiceImpl.getObject(uid);
		assertSame(rule, loadedRule);
	}

	/**
	 * Test for: Get all the available conditions configured in the system.
	 */
	@Test
	public void testGetAllConditionsMap() {
		List<String> conditionsSet = new ArrayList<String>();
		conditionsSet.add("cartSubtotalCondition");
		ruleServiceImpl.setAllConditions(conditionsSet);
		Map<Integer, List<RuleCondition>> conditionMap = ruleServiceImpl.getAllConditionsMap();

		List<RuleCondition> returnedConditionList = conditionMap.get(Integer.valueOf(RuleScenarios.CART_SCENARIO));
		assertNotNull(returnedConditionList);
		assertTrue(returnedConditionList.get(0) instanceof CartSubtotalConditionImpl);
	}

	/**
	 * Test for: Get all the available actions configured in the system.
Tested by FIT?
	public void testGetAllActionsMap() {
		List<String> actionsSet = new ArrayList<String>();
		actionsSet.add("cartNFreeSkusAction");
		ruleServiceImpl.setAllActions(actionsSet);
		Map<Integer, List<RuleAction>> actionMap = ruleServiceImpl.getAllActionsMap();

		List<RuleAction> returnedActionList = actionMap.get(Integer.valueOf(RuleScenarios.CART_SCENARIO));
		assertNotNull(returnedActionList);
		assertTrue(returnedActionList.get(0) instanceof CartNFreeSkusActionImpl);
	}	 */

	/**
	 * Test for: Get all the available exceptions configured in the system.
	 */
	@Test
	public void testGetAllExceptionsMap() {
		stubGetBean(ContextIdNames.SKU_EXCEPTION, SkuExceptionImpl.class);

		List<String> exceptionsSet = new ArrayList<String>();
		exceptionsSet.add("skuException");
		ruleServiceImpl.setAllExceptions(exceptionsSet);
		Map<Integer, List<RuleException>> exceptionMap = ruleServiceImpl.getAllExceptionsMap();

		List<RuleException> returnedExceptionList = exceptionMap.get(Integer.valueOf(RuleScenarios.CART_SCENARIO));
		assertEquals(returnedExceptionList.size(), 1);
	}

	/**
	 * Test method for {@link RuleServiceImpl#findChangedPromoUids(Date, int)}.
	 */
	@Test
	public void testFindChangedPromoUids() {
		final Date date = new Date();

		// give time so that current date is definitely after our date
		try {
			final int ten = 10;
			Thread.sleep(ten);
		} catch (InterruptedException e) {
			// do nothing
		}

		final List<Long> engineResult = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with("RULE_UIDS_SCOPE_CHANGED"), with(any(Object[].class)));
				will(returnValue(engineResult));
			}
		});
		assertEquals(engineResult, ruleServiceImpl.findChangedPromoUids(date, 1));

		// test short-cutting a DB call because we exclude everything
		Collection<Long> result = ruleServiceImpl.findChangedPromoUids(new Date(Long.MAX_VALUE), 1);
		assertTrue(result.isEmpty());
	}

	/**
	 * Test method for {@link RuleServiceImpl#get(long, FetchGroupLoadTuner).
	 */
	@Test
	public void testGetWithFGLoadTuner() {
		final long uid = 1234L;
		final Rule mockRule = context.mock(Rule.class);
		context.checking(new Expectations() {
			{
				allowing(mockRule).getUidPk();
				will(returnValue(uid));
			}
		});
		final Rule rule = mockRule;

		final FetchGroupLoadTuner mockFGLoadTuner = context.mock(FetchGroupLoadTuner.class);
		final FetchGroupLoadTuner fGLoadTuner = mockFGLoadTuner;
		context.checking(new Expectations() {
			{

				oneOf(mockPersistenceEngine).get(PromotionRuleImpl.class, uid);
				will(returnValue(rule));

				oneOf(getMockFetchPlanHelper()).configureFetchGroupLoadTuner(with(same(fGLoadTuner)));
				oneOf(getMockFetchPlanHelper()).clearFetchPlan();
			}
		});

		assertSame(rule, ruleServiceImpl.get(uid, fGLoadTuner));

		final long nonExistUid = 3456L;
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).get(PromotionRuleImpl.class, nonExistUid);
				will(returnValue(null));

				oneOf(getMockFetchPlanHelper()).configureFetchGroupLoadTuner(with(same(fGLoadTuner)));
				oneOf(getMockFetchPlanHelper()).clearFetchPlan();
			}
		});

		assertNull(ruleServiceImpl.get(nonExistUid, fGLoadTuner));

		assertEquals(0, ruleServiceImpl.get(0, fGLoadTuner).getUidPk());
	}

	/**
	 * Test that method is null safe for codes.
	 */
	@Test
	public void testGetPromotionNamesForCouponCodesWithNullCodes() {
		final Map<String, String> res = ruleServiceImpl.getPromotionNamesForCouponCodes(Locale.ENGLISH, null);
		assertNotNull(res);
		assertEquals(0, res.size());
	}

	/**
	 * Test that method is returning empty map for empty collection of codes.
	 */
	@Test
	public void testGetPromotionNamesForCouponCodesWithEmptyCodes() {
		final Map<String, String> res = ruleServiceImpl.getPromotionNamesForCouponCodes(Locale.ENGLISH, Collections.<String>emptySet());
		assertNotNull(res);
		assertEquals(0, res.size());
	}

	/**
	 * Test that method is returning map of codes for given promo codes collection.
	 */
	@Test
	public void testGetPromotionNamesForCouponCodes() {

		final String promo1 = "promo1";
		final String name1 = "name1";
		final String promo2 = "promo2";
		final Rule mockedRule1 = context.mock(Rule.class, "rule1");
		final Rule rule1 = mockedRule1;

		final List<String> codes = new ArrayList<String>();
		codes.add(promo1);
		codes.add(promo2);

		final List<Object[]> codeRuleMap = new ArrayList<Object[]>();
		codeRuleMap.add(new Object[]{promo1, rule1});
		context.checking(new Expectations() {
			{
				exactly(2).of(mockedRule1).getDisplayName(with(same(Locale.ENGLISH)));
				will(returnValue(name1));

				oneOf(mockPersistenceEngine).retrieveByNamedQueryWithList(
						with("RULES_MAPPED_BY_COUPON_CODE"), with("list"), with(codes), with(any(Object[].class)));
				will(returnValue(codeRuleMap));
			}
		});

		final Map<String, String> res = ruleServiceImpl.getPromotionNamesForCouponCodes(Locale.ENGLISH, codes);
		assertNotNull("Display names should have been found", res);
		assertEquals("There should be 2 display names", 2, res.size());
		assertEquals("The first promo should map to the English display name", name1, res.get(promo1));
		assertEquals("The second promo should map to an empty string", StringUtils.EMPTY, res.get(promo2));
	}

	/**
	 * Test that getDisplayName is returning empty string for names where there is no localized value.
	 */
	@Test
	public void testGetEmptyPromotionNamesForCouponCodes() {

		final String promo1 = "promo1";
		final Rule mockedRule1 = context.mock(Rule.class, "rule1");
		final Rule rule1 = mockedRule1;

		final List<String> codes = new ArrayList<String>();
		codes.add(promo1);

		final List<Object[]> codeRuleMap = new ArrayList<Object[]>();
		codeRuleMap.add(new Object[]{promo1, rule1});
		context.checking(new Expectations() {
			{

				oneOf(mockedRule1).getDisplayName(with(same(Locale.ENGLISH)));
				will(returnValue(null));

				oneOf(mockPersistenceEngine).retrieveByNamedQueryWithList(
						with("RULES_MAPPED_BY_COUPON_CODE"), with("list"), with(codes), with(any(Object[].class)));
				will(returnValue(codeRuleMap));
			}
		});

		final Map<String, String> res = ruleServiceImpl.getPromotionNamesForCouponCodes(Locale.ENGLISH, codes);
		assertNotNull("Display names should have been found", res);
		assertEquals("There should be 1 display name", 1, res.size());
		assertEquals("The promo should map to an empty string display name", StringUtils.EMPTY, res.get(promo1));
	}

	public FetchPlanHelper getMockFetchPlanHelper() {
		return mockFetchPlanHelper;
	}

}
