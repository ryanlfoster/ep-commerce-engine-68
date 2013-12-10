package com.elasticpath.domain.misc.impl;

import com.elasticpath.domain.misc.CouponUsageByCouponCodeComparator;
import com.elasticpath.domain.rules.CouponUsage;

/**
 * Comparator for ordering CouponUsage objects by coupon code.
 */
public class CouponUsageByCouponCodeComparatorImpl implements CouponUsageByCouponCodeComparator {

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(final CouponUsage couponUsage1, final CouponUsage couponUsage2) {
		return couponUsage1.getCoupon().getCouponCode().compareTo(couponUsage2.getCoupon().getCouponCode());

	}

}
