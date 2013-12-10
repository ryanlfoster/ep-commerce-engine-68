package com.elasticpath.domain.rules;

import com.elasticpath.persistence.api.Entity;

/**
 * Represents the configuration of a rule's set of coupons.
 */
public interface CouponConfig  extends Entity {

	/**
	 * The Unlimited keyword.
	 */
	String UNLIMITED = "unlimited";
		
	/**
	 * Set the rule code that this coupon configuration applies to.
	 *  
	 * @param ruleCode the code of the {@link Rule}
	 */
	void setRuleCode(final String ruleCode);

	/**
	 * Get the code of the rule that this coupon configuration applies to.
	 * 
	 * @return a {@link Rule} code
	 */
	String getRuleCode();

	/**
	 * Set the usage limit for this rule's coupons. This should be the
	 * maximum usages allowed per coupon code OR the maximum uses
	 * per user (specified by the {@code isUserSpecific} property).
	 * 
	 * @param usageLimit the usage limit
	 */
	void setUsageLimit(final int usageLimit);

	/**
	 * Get the usage limit for this rule's coupons. This will be the
	 * maximum usages allowed per coupon code OR the maximum uses
	 * per user (specified by the {@code isUserSpecific} property).
	 *
	 * @return the usage limit
	 */
	int getUsageLimit();
	
	/**
	 * Returns true if the UsageLimit is set to unlimited.
	 *
	 * @return true if unlimited limit.
	 */
	boolean isUnlimited();
	
	/**
	 * Set the Config to be unlimited.
	 */
	void setUnlimited();

	/**
	 * Set the usage type for this rule's coupons.
	 * 
	 * @param couponUsageType the {@link CouponUsageType}
	 */
	void setUsageType(final CouponUsageType couponUsageType);
	
	/**
	 * Get the usage type for this rule's coupons.
	 * 
	 * @return the {@link CouponUsageType}
	 */
	CouponUsageType getUsageType();

	/**
	 * 
	 * @return True if this coupon has a limited duration. See {@code getDurationDays}.
	 */
	boolean isLimitedDuration();
	
	/**
	 * 
	 * @param limitedDuration The limited duration flag value to set.
	 */
	void setLimitedDuration(boolean limitedDuration);
	
	/**
	 * 
	 * @return The number of days, from when the coupon is first used, that the coupon is active for.
	 */
	int getDurationDays();
	
	/**
	 * 
	 * @param durationDays The number of days to set. See {@code getDurationDays}.
	 */
	void setDurationDays(int durationDays);
	
	/**
	 * @return True if the coupon can be used multiple times per order.
	 */
	boolean isMultiUsePerOrder();
	
	/**
	 * 
	 * @param multiUsePerOrder Multiple use-per-Order flag.
	 */
	void setMultiUsePerOrder(final boolean multiUsePerOrder);

}