/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.test.integration.promotions;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.PromotionTestPersister;

/**
 * Test that the coupon config service and related domain objects behave correctly when
 * we involve the persistence layer and database.
 */
public class CouponConfigServiceImplTest extends DbTestCase {

	@Autowired private CouponConfigService couponConfigService;
	@Autowired private RuleService ruleService;
	private PromotionTestPersister promoPersister;

	/**
	 * Set up required for each test.
	 * 
	 * @throws java.lang.Exception in case of error during setup
	 */
	@Before
	public void setUp() throws Exception {
		promoPersister = getTac().getPersistersFactory().getPromotionTestPersister();
	}
	
	/**
	 * Test adding a coupon config and confirm that the limited use coupon condition
	 * is added to the rule.
	 */
	@DirtiesDatabase
	@Test
	public void testAddCreatesCondition() {
		final int usageLimit = 3;
		
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("test promo 1", scenario.getStore().getCode(), "test1", true);
		
		CouponConfig couponConfig = createCouponConfig();
		couponConfig.initialize();
		couponConfig.setRuleCode(rule.getCode());
		couponConfig.setUsageLimit(usageLimit);
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_COUPON);
		CouponConfig updatedConfig = couponConfigService.add(couponConfig);
		
		CouponConfig loadedConfig = couponConfigService.get(updatedConfig.getUidPk());
		assertEquals("The loaded coupon config should be equal to the one we created", updatedConfig, loadedConfig);
		
		Rule loadedRule = ruleService.get(rule.getUidPk());
		assertRuleContainsCondition(loadedRule);
		
	}
	
	/**
	 * Test adding a coupon config and confirm that the limited use coupon condition
	 * isn't added to the rule a second time.
	 */
	@DirtiesDatabase
	@Test
	public void testAddDoesNotAddConditionMoreThanOnce() {
		final int usageLimit = 3;
		
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("test promo 2", scenario.getStore().getCode(), "test2", false);
		RuleCondition couponCondition = getBeanFactory().getBean(ContextIdNames.LIMITED_USE_COUPON_CODE_COND);
		rule.addCondition(couponCondition);
		rule = ruleService.update(rule);
		assertRuleContainsCondition(rule);
		
		CouponConfig couponConfig = createCouponConfig();
		couponConfig.initialize();
		couponConfig.setRuleCode(rule.getCode());
		couponConfig.setUsageLimit(usageLimit);
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_COUPON);
		CouponConfig updatedConfig = couponConfigService.add(couponConfig);
		
		CouponConfig loadedConfig = couponConfigService.get(updatedConfig.getUidPk());
		assertEquals("The loaded coupon config should be equal to the one we created", updatedConfig, loadedConfig);
		
		Rule loadedRule = ruleService.get(rule.getUidPk());
		assertRuleHasNoDuplicateConditions(loadedRule);
		
	}
	
	/**
	 * Test we get the expected exception when we try to configure coupons for a non-existent rule.

	@DirtiesDatabase
	@Test
(expected = RuleNotFoundException.class)
	public void testAddForNonExistentRule() {
		final int usageLimit = 3;
		
		CouponConfig couponConfig = beanFactory.getBean(ContextIdNames.COUPON_CONFIG);
		couponConfig.setDefaultValues();
		couponConfig.setRuleCode("NOSUCHCODE");
		couponConfig.setUsageLimit(usageLimit);
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_COUPON);
		couponConfigService.add(couponConfig);
	}
	 */
	
	/**
	 * Test updating a config writes the change to the DB.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdate() {
		final int initialUsageLimit = 1;
		final int newUsageLimit = 5;
		
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("test promo 3", scenario.getStore().getCode(), "test3", true);
		
		CouponConfig couponConfig = createCouponConfig();
		couponConfig.initialize();
		couponConfig.setRuleCode(rule.getCode());
		couponConfig.setUsageLimit(initialUsageLimit);
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_COUPON);
		couponConfig.setMultiUsePerOrder(true);
		couponConfigService.add(couponConfig);
		
		assertEquals("The coupon config should have a usage limit of 1", initialUsageLimit, couponConfig.getUsageLimit());
		assertTrue("Use Per Order should be true.", couponConfig.isMultiUsePerOrder());

		couponConfig.setUsageLimit(newUsageLimit);
		couponConfig.setMultiUsePerOrder(false);
		CouponConfig updatedConfig = couponConfigService.update(couponConfig);
		assertFalse("Use Per Order should be false.", updatedConfig.isMultiUsePerOrder());
		
		CouponConfig loadedConfig = couponConfigService.get(updatedConfig.getUidPk());
		assertEquals("The coupon usage limit should have changed", newUsageLimit, loadedConfig.getUsageLimit());
		assertFalse("Use Per Order should be false.", loadedConfig.isMultiUsePerOrder());
	}
	
	/**
	 * Test deleting a coupon config removes that config from the DB.
	 */
	@DirtiesDatabase
	@Test
	public void testDelete() {
		final int usageLimit = 3;
		
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("test promo 4", scenario.getStore().getCode(), "test4", true);
		CouponConfig couponConfig = createCouponConfig();
		couponConfig.initialize();
		couponConfig.setRuleCode(rule.getCode());
		couponConfig.setUsageLimit(usageLimit);
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_COUPON);
		CouponConfig updatedCouponConfig = couponConfigService.add(couponConfig);
		
		assertTrue("The coupon config should be persistent", updatedCouponConfig.isPersisted());
		
		long uidPk = updatedCouponConfig.getUidPk();
		couponConfigService.delete(couponConfig);
		
		CouponConfig loadedCouponConfig = couponConfigService.get(uidPk);
		assertNull("The coupon config should no longer exist", loadedCouponConfig);
	}
	
	private CouponConfig createCouponConfig() {
		return getBeanFactory().getBean(ContextIdNames.COUPON_CONFIG);
	}
	

	private void assertRuleContainsCondition(final Rule rule) {
		boolean conditionFound = false;
		for (RuleCondition condition : rule.getConditions()) {
			if (RuleElementType.LIMITED_USE_COUPON_CODE_CONDITION.getPropertyKey().equals(condition.getType())) {
				conditionFound = true;
			}
		}
		assertTrue("The condition should have been present on the rule", conditionFound);
	}
	
	private void assertRuleHasNoDuplicateConditions(final Rule rule) {
		int conditionCount = 0;
		for (RuleCondition condition : rule.getConditions()) {
			if (RuleElementType.LIMITED_USE_COUPON_CODE_CONDITION.getPropertyKey().equals(condition.getType())) {
				conditionCount++;
			}
		}
		assertEquals("The condition should have been found once", 1, conditionCount);
	}

}
