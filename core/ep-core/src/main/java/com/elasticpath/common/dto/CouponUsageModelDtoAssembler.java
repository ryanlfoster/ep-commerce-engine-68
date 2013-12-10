/**
 * 
 */
package com.elasticpath.common.dto;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponUsage;

/**
 * An assembler for BaseAmount for CSV import.
 *
 */
public class CouponUsageModelDtoAssembler {
	
	private final BeanFactory beanFactory;
	
	/** 
	 * @param beanFactory the bean factory used to create coupons 
	 */
	public CouponUsageModelDtoAssembler(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	/**
	 * convert couponUsageModelDto to couponUsage.
	 * 
	 * @param source the couponUsageModelDto
	 * @param coupon the coupon for this usage
	 * @return the couponUsage
	 */
	public CouponUsage assembleDomain(final CouponUsageModelDto source, final Coupon coupon) {
		if (!source.getCouponCode().equalsIgnoreCase(coupon.getCouponCode())) {
			throw new IllegalArgumentException();
		}
			
		CouponUsage couponUsage = beanFactory.getBean(ContextIdNames.COUPON_USAGE);
		couponUsage.setCoupon(coupon);
		couponUsage.setCustomerEmailAddress(source.getEmailAddress());
		couponUsage.setSuspended(source.isSuspended());

		return couponUsage;
	}

}
