/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.common.dto;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.impl.CouponConfigImpl;
import com.elasticpath.domain.rules.impl.CouponImpl;
import com.elasticpath.domain.rules.impl.CouponUsageImpl;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.dao.CouponDao;
import com.elasticpath.service.rules.dao.CouponUsageDao;
import com.elasticpath.service.rules.impl.CouponServiceImpl;
import com.elasticpath.service.rules.impl.CouponUsageServiceImpl;

/**
 * Test that the coupon usage dto mediator behaves as expected.
 */

public class CouponUsageDtoMediatorImplTest {
	private static final String COUPON_CODE1 = "CouponCode1";
	private static final String EMAIL_ADDRESS1 = "email.address@blog.com";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private CouponServiceImplDouble couponService;
	
	private CouponUsageServiceImplDouble couponUsageService;
	
	private CouponConfigService couponConfigService;
	
	private CouponUsageDtoMediatorImpl mediator;
	
	private BeanFactory beanFactory;
	
	private CouponDao couponDao;
	
	private CouponUsageDao couponUsageDao;


	/**
	 * Test double for verifying calls.
	 */
	private class CouponUsageServiceImplDouble extends CouponUsageServiceImpl {
		private CouponUsage updatedCouponUsage;
		
		@Override
		public CouponUsage add(final CouponUsage newCouponUsage) throws EpServiceException {
			this.updatedCouponUsage = newCouponUsage;
			return updatedCouponUsage;
		}
		
		public CouponUsage getUpdatedCouponUsage() {
			return updatedCouponUsage;
		}
	}
	
	/**
	 * Test double for verifying calls.
	 */
	private class CouponServiceImplDouble extends CouponServiceImpl {
		private Coupon updatedCoupon;
		
		@Override
		public Coupon update(final Coupon newCoupon) throws EpServiceException {
			this.updatedCoupon = newCoupon;
			return updatedCoupon;
		}
		
		@Override
		public Coupon add(final Coupon newCoupon) {
			this.updatedCoupon = newCoupon;
			return updatedCoupon;
		}
				
		@Override
		public Map<String, Coupon> findCouponsForCodes(final Collection<String> codes) {
			return new HashMap<String, Coupon>();
		}
		
		public Coupon getUpdatedCoupon() {
			return this.updatedCoupon;
		}
		
		@Override
		public Coupon findByCouponCode(final String couponCode) {
			return null;
		}
		
		@Override
		public boolean doesCouponCodeOnlyExistForThisRuleCode(final String couponCode, final String ruleCode) {
			return true;
		}
	};

	
	/**
	 * Set up required for each test.
	 * 
	 * @throws java.lang.Exception in case of error during setup
	 */
	@Before
	public void setUp() throws Exception {
		couponDao = context.mock(CouponDao.class);
		couponService = new CouponServiceImplDouble();
		couponService.setCouponDao(couponDao);
		couponUsageDao = context.mock(CouponUsageDao.class);
		couponUsageService = new CouponUsageServiceImplDouble();
		couponUsageService.setCouponUsageDao(couponUsageDao);
		couponConfigService = context.mock(CouponConfigService.class);
		beanFactory = context.mock(BeanFactory.class);
		mediator = new CouponUsageDtoMediatorImpl();
		mediator.setCouponConfigService(couponConfigService);
		mediator.setCouponService(couponService);
		mediator.setCouponUsageService(couponUsageService);
		mediator.setBeanFactory(beanFactory);
	}
	
	/**
	 * Test Adding Coupon given a list of Dtos and an associated CouponPk.
	 */
	@Test
	public void testAddCouponsForDtosAndCouponPk() {
		final String ruleCode = "RULE";
		final CouponConfig persistentCouponConfig = new CouponConfigImpl();
		persistentCouponConfig.setRuleCode(ruleCode);

		final CouponUsage persistentCouponUsage = new CouponUsageImpl();
		
		final Coupon persistentCoupon = new CouponImpl();
		persistentCoupon.setCouponCode(COUPON_CODE1);
		persistentCoupon.setSuspended(false);
		persistentCoupon.setCouponConfig(persistentCouponConfig);

		final Set<String> codes = new HashSet<String>();
		codes.add(COUPON_CODE1);
		
		Collection<CouponUsageModelDto> addedCouponUsages = new ArrayList<CouponUsageModelDto>();
		CouponUsageModelDto dto1 = new CouponUsageModelDto();
		dto1.setCouponCode(COUPON_CODE1);
		dto1.setSuspended(true);
		dto1.setEmailAddress(EMAIL_ADDRESS1);
		addedCouponUsages.add(dto1);
				
		context.checking(new Expectations() { {
			oneOf(couponConfigService).findByRuleCode(ruleCode); will(returnValue(persistentCouponConfig));
			oneOf(beanFactory).getBean(ContextIdNames.COUPON); will(returnValue(persistentCoupon));
			oneOf(beanFactory).getBean(ContextIdNames.COUPON_USAGE); will(returnValue(persistentCouponUsage));
		} });
		
		mediator.add(addedCouponUsages, ruleCode);
		Coupon coupon = couponService.getUpdatedCoupon();
		CouponUsage couponUsage = couponUsageService.getUpdatedCouponUsage();
		
		assertEquals("CouponUsage should have Suspended = True.", true, couponUsage.isSuspended());
		assertEquals("Coupon should have Suspended = False.", false, coupon.isSuspended());

		assertEquals("CouponUsage should have CouponCode = CouponCode1.", COUPON_CODE1, couponUsage.getCoupon().getCouponCode());
		assertEquals("CouponUsage should have email = " + EMAIL_ADDRESS1 + ".", EMAIL_ADDRESS1, couponUsage.getCustomerEmailAddress());
	}
}
