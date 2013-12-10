/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.rules;

import com.elasticpath.domain.rules.Coupon;

/**
 * Interface for Generators for unique coupon codes given a prefix.
 */
public interface CouponCodeGenerator {

	/**
	 * Generate Coupon Code.
	 *
	 * @param coupon the coupon.
	 * @param couponCodePrefix the given prefix.
	 * @return the generated code.
	 */
	String generateCouponCode(final Coupon coupon, final String couponCodePrefix);
}
