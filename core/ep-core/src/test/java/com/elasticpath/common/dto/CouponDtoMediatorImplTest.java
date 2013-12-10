/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.common.dto;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.impl.CouponConfigImpl;
import com.elasticpath.domain.rules.impl.CouponImpl;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.dao.CouponDao;
import com.elasticpath.service.rules.impl.CouponServiceImpl;

/**
 * Test that the coupon dto mediator behaves as expected.
 */

public class CouponDtoMediatorImplTest {

	private static final String COUPON_CODE1 = "CouponCode1";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private CouponServiceImplDouble couponService;
	
	private CouponConfigService couponConfigService;
	
	private CouponDtoMediatorImpl mediator;
	
	private BeanFactory beanFactory;
	
	private CouponDao couponDao;

	/**
	 * Test double for verifying calls.
	 */
	private class CouponServiceImplDouble extends CouponServiceImpl {
		private Coupon updatedCoupon;
		private Collection<Long> uids;
		
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
		public List<Coupon> findByUids(final Collection<Long> uids) {
			this.uids = uids;
			return super.findByUids(uids);
		}
		
		public Coupon getUpdatedCoupon() {
			return this.updatedCoupon;
		}
		
		public Collection<Long> getCouponUids() {
			return this.uids;
		}
		
		@Override
		public Coupon findByCouponCode(final String couponCode) {
			return null;
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
		couponConfigService = context.mock(CouponConfigService.class);
		beanFactory = context.mock(BeanFactory.class);
		mediator = new CouponDtoMediatorImpl();
		mediator.setCouponConfigService(couponConfigService);
		mediator.setCouponService(couponService);
		mediator.setBeanFactory(beanFactory);
	}
	
	/**
	 * Test Adding Coupon given a list of Dtos and an associated CouponPk.
	 */
	@Test
	public void testAddCouponsForDtosAndCouponPk() {

		final Coupon persistentCoupon = new CouponImpl();

		final CouponConfig persistentCouponConfig = new CouponConfigImpl();
		final String ruleCode = "RULE";
		persistentCouponConfig.setRuleCode(ruleCode);
		
		Collection<CouponModelDto> addedCoupon = new ArrayList<CouponModelDto>();
		CouponModelDto dto1 = new CouponModelDto();
		dto1.setCouponCode(COUPON_CODE1);
		dto1.setSuspended(true);
		addedCoupon.add(dto1);
		
		context.checking(new Expectations() { {
			oneOf(couponConfigService).findByRuleCode(ruleCode); will(returnValue(persistentCouponConfig));
			oneOf(beanFactory).getBean(ContextIdNames.COUPON); will(returnValue(persistentCoupon));
		} });

		mediator.add(addedCoupon, ruleCode);
		Coupon coupon = couponService.getUpdatedCoupon();
		assertEquals("Should have Suspended = True.", true, coupon.isSuspended());
		assertEquals("Should have CouponCode = CouponCode1.", COUPON_CODE1, coupon.getCouponCode());
	}

	/**
	 * Test Updating Coupon given a list of Dtos and an associated CouponPk.
	 */
	@Test
	public void testUpdatingCouponsForDtosAndCouponPk() {
		final Coupon persistentCoupon = new CouponImpl();
		final long couponUid = 30L;
		persistentCoupon.setUidPk(couponUid);
		
		final Collection<Long> expectedUids = new ArrayList<Long>();
		expectedUids.add(couponUid);
		
		final Collection<Coupon> persistentCoupons = new ArrayList<Coupon>();
		persistentCoupons.add(persistentCoupon);
		
		Collection<CouponModelDto> addedCoupon = new ArrayList<CouponModelDto>();
		CouponModelDto dto1 = new CouponModelDto(couponUid, COUPON_CODE1, true);
		addedCoupon.add(dto1);
		
		context.checking(new Expectations() { {
			oneOf(couponDao).findByUids(with(Collections.singleton(couponUid))); will(returnValue(persistentCoupons));
		} });

		mediator.update(addedCoupon);
		
		Coupon coupon = couponService.getUpdatedCoupon();
		assertTrue("uids being retrieved should match the set in the dto collection", 
				CollectionUtils.isEqualCollection(couponService.getCouponUids(), expectedUids));
		assertEquals("Should have Suspended = True.", true, coupon.isSuspended());
		assertEquals("Should have CouponCode = CouponCode1.", COUPON_CODE1, coupon.getCouponCode());
	}

}
