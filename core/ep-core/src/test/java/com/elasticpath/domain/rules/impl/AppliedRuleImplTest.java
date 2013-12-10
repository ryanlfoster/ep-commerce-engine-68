/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.Rule;

/** Test cases for <code>AppliedRuleImpl</code>. */
public class AppliedRuleImplTest {

	private static final String RULE_CODE = "Rule Code";
	private static final String RULE_NAME = "RuleName";
	private AppliedRule appliedRule;

	@Before
	public void setUp() throws Exception {
		appliedRule = new AppliedRuleImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AppliedRuleImpl.initializeFromRule(Rule)'.
	 */
	@Test
	public void testInitializeFromRule() {
		Rule rule = new PromotionRuleImplTest().getTestPromotionRule();
		rule.setName(RULE_NAME);
		appliedRule.initialize(rule);
		assertEquals(RULE_NAME, appliedRule.getRuleName());
		assertTrue(appliedRule.getRuleCode().length() > 0);
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AppliedRuleImpl.getRuleName()'.
	 */
	@Test
	public void testGetRuleName() {
		appliedRule.setRuleName(RULE_NAME);
		assertEquals(RULE_NAME, appliedRule.getRuleName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AppliedRuleImpl.getRuleCode()'.
	 */
	@Test
	public void testGetRuleCode() {
		appliedRule.setRuleCode(RULE_CODE);
		assertEquals(RULE_CODE, appliedRule.getRuleCode());
		
	}

	/**
	 * Test that get on coupons returns empty collection.
	 */
	@Test
	public void testGetEmptyCoupons() {
		Rule rule = new PromotionRuleImplTest().getTestPromotionRule();
		rule.setName(RULE_NAME);
		appliedRule.initialize(rule);
		assertNotNull("Applied coupon collection should always exist.", appliedRule.getAppliedCoupons());
		assertEquals("Applied coupon collection should be empty.", 0, appliedRule.getAppliedCoupons().size());
	}
	
	/**
	 * Test that coupons can be set.
	 */
	@Test
	public void testSetCoupons() {
		final int usageCount = 12;
		Rule rule = new PromotionRuleImplTest().getTestPromotionRule();
		rule.setName(RULE_NAME);
		appliedRule.initialize(rule);
		AppliedCoupon coupon = new AppliedCouponImpl();
		coupon.setCouponCode("CouponCode");
		coupon.setUsageCount(usageCount);
		appliedRule.getAppliedCoupons().add(coupon);
		Iterator<AppliedCoupon> iter = appliedRule.getAppliedCoupons().iterator();
		coupon = iter.next();
		
		assertEquals("Usage Count should be that set.", usageCount, coupon.getUsageCount());
		assertEquals("CouponCode should be as set.", "CouponCode", coupon.getCouponCode());
	}

}
