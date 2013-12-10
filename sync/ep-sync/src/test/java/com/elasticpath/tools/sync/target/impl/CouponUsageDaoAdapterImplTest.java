/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.tools.sync.target.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.impl.CouponImpl;
import com.elasticpath.domain.rules.impl.CouponUsageImpl;
import com.elasticpath.service.rules.CouponUsageService;

/**
 * Test that the coupon usage dao adapter behaves as expected.
 */

public class CouponUsageDaoAdapterImplTest {

	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private CouponUsageDaoAdapterImpl couponUsageDaoAdapterImpl;
	
	private CouponUsageService couponUsageService;
	
	/**
	 * Setup required for tests.
	 * 
	 * @throws Exception in case of error during setup
	 */
	@Before
	public void setUp() throws Exception {
		couponUsageService = context.mock(CouponUsageService.class);
		couponUsageDaoAdapterImpl = new CouponUsageDaoAdapterImpl();
		couponUsageDaoAdapterImpl.setCouponUsageService(couponUsageService);
	}

	/**
	 * Test that getting associated usage guids will return expected guids (i.e. only guids that have email address).
	 */
	@Test
	public void testGetAssociatedGuids() {
		final String ruleCode = "RULE";
		final String couponCode = "COUPON";
		final String email = "test@test.com";
		
		Coupon coupon = new CouponImpl();
		coupon.setCouponCode(couponCode);
		
		CouponUsage usageWithEmail = new CouponUsageImpl();
		usageWithEmail.setCoupon(coupon);
		usageWithEmail.setCustomerEmailAddress(email);
				
		CouponUsage usageWithNullEmail = new CouponUsageImpl();
		usageWithNullEmail.setCoupon(coupon);
		usageWithNullEmail.setCustomerEmailAddress(null);
		
		CouponUsage usageWithEmptyEmail = new CouponUsageImpl();
		usageWithNullEmail.setCoupon(coupon);
		usageWithNullEmail.setCustomerEmailAddress(StringUtils.EMPTY);
		
		final List<CouponUsage> usageList = new ArrayList<CouponUsage>();
		usageList.add(usageWithEmail);
		usageList.add(usageWithNullEmail);
		usageList.add(usageWithEmptyEmail);
		
		context.checking(new Expectations() {
			{
				oneOf(couponUsageService).findByRuleCode(ruleCode); will(returnValue(usageList));
			}
		});
		List<String> usageGuids = couponUsageDaoAdapterImpl.getAssociatedGuids(Rule.class, ruleCode);
		assertEquals("There should only be one guid returned", 1, usageGuids.size());
		assertEquals("The usage guid should be the code and email", usageWithEmail.getGuid(), usageGuids.get(0));
	}

}
