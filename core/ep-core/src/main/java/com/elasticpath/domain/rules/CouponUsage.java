package com.elasticpath.domain.rules;

import java.util.Date;

import com.elasticpath.persistence.api.Entity;

/**
 * Represents the usage count of a coupon.
 */
public interface CouponUsage extends Entity {
	/**
	 * @return The number of times this coupon has been used by any customer. 
	 */
	int getUseCount();
	
	/**
	 * 
	 * @param useCount The use count.
	 */
	void setUseCount(int useCount);
	
	/**
	 * 
	 * @return The related coupon.
	 */
	Coupon getCoupon();
	
	/**
	 * 
	 * @param coupon The related coupon.
	 */
	void setCoupon(Coupon coupon);
	
	/**
	 * 
	 * @return The related customer.
	 */
	String getCustomerEmailAddress();
	
	/**
	 * 
	 * @param email The related customer email address.
	 */
	void setCustomerEmailAddress(String email);
	
	/**
	 * Set whether the coupon should be applied to the user's shopping cart.
	 * 
	 * @param activeInCart True if the user has selected the coupon to be applied against cart contents.
	 */
	void setActiveInCart(boolean activeInCart);
	
	/**
	 * Get whether coupon is active against the user's shopping cart.
	 * 
	 * @return True if the user has selected the coupon to be applied against cart contents.
	 */
	boolean isActiveInCart();
	
	/**
	 * Get the limited duration end date.
	 * 
	 * @return For limited duration coupons returns the date when the coupon has finished. 
	 */
	Date getLimitedDurationEndDate();

	/**
	 * Set the start date for the limited duration on the coupon.
	 * 
	 * @param date the start date
	 */
	void setLimitedDurationStartDate(final Date date);
	
	/**
	 * @return true if coupon is suspended.
	 */
	boolean isSuspended();
	
	/**
	 * @param suspended is true if coupon usage should be suspended.
	 */
	void setSuspended(final boolean suspended);
	
}
