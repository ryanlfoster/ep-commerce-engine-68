package com.elasticpath.common.dto;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;

/**
 * assembler for Coupon.
 */
public class CouponModelDtoAssembler {
	private final BeanFactory beanFactory;
	
	/** 
	 * @param beanFactory the bean factory used to create coupons 
	 */
	public CouponModelDtoAssembler(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	/**
	 * convert CouponModelDto to Coupon.
	 * 
	 * @param source the CoupoModelDto
	 * @param couponConfig the coupon configuration for this coupon
	 * @return the Coupon
	 */
	public Coupon assembleDomain(final CouponModelDto source, final CouponConfig couponConfig) {
		Coupon coupon = beanFactory.getBean(ContextIdNames.COUPON);
		coupon.setCouponCode(source.getCouponCode());
		coupon.setCouponConfig(couponConfig);
		if (!CouponUsageModelDto.class.isAssignableFrom(source.getClass())) {
			//do not suspend Coupon based on CouponUsage evidence.
			coupon.setSuspended(source.isSuspended());
		}
		return coupon;
	}
}