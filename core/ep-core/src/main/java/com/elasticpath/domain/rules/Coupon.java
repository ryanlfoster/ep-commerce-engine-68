package com.elasticpath.domain.rules;

import com.elasticpath.persistence.api.Entity;

/**
 * Represents a coupon code that can be used with a Coupon rule element.
 */
public interface Coupon extends Entity {

	
	/**
	 * @return The coupon code.
	 */
	String getCouponCode();
	
	/**
	 * @param couponCode The coupon code.
	 */
	void setCouponCode(String couponCode);
	
	/**
	 * Get the coupon configuration to which this coupon belongs.
	 * 
	 * @return the {@link CouponConfig}
	 */
	CouponConfig getCouponConfig();

	/**
	 * Set the coupon configuration for this coupon.
	 *  
	 * @param couponConfig the {@link CouponConfig}
	 */
	void setCouponConfig(final CouponConfig couponConfig);
	
	/**
	 * Suspended flag.
	 * @return true if coupon suspended
	 */
	boolean isSuspended();

	/**
	 * Set suspended flag.
	 * @param suspended flag. 
	 */
	void setSuspended(boolean suspended);
}
