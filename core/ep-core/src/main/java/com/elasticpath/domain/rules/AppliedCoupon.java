package com.elasticpath.domain.rules;

import com.elasticpath.persistence.api.Persistable;


/**
 * 
 * Represents a Coupon that has been applied to an Order.  Generally managed as a collection
 * on an AppliedRule class. Semantics generally reflect the state of things when the order was made.
 *
 */
public interface AppliedCoupon extends Persistable {
	
	/**
	 * Set the coupon code.
	 * @param couponCode used
	 */
	void setCouponCode(final String couponCode);
	
	/**
	 * Get the coupon code.
	 * 
	 * @return the coupon code.
	 */
	String getCouponCode();
	
	/**
	 * The usage count for this coupon and this user at the time of the order.
	 * If the usage was for a LIMIT_PER_COUPON promotion then this would be a snapshot of 
	 * usage count for everyone.
	 * 
	 * @param usageCount used
	 */
	void setUsageCount(final int usageCount);
	
	/**
	 * Get the usage count.
	 * 
	 * @return the usage count.
	 */
	int getUsageCount();
}
