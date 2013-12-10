package com.elasticpath.service.rules;

import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;

/**
 * 
 * Rule reporting service.
 *
 */
public interface ReportingRuleService {
	
	/**
	 * Collect promotion usage report data.
	 * 
	 * @param storeUids store uids to wich user has access.
	 * @param currency currency code or null for all currencies
	 * @param startDate start date or null 
	 * @param endDate end date. Not nullable parameter 
	 * @param withCouponCodesOnly true in case if need promotions with configured coupons codes only. Not nullable parameter.
	 * @return list of report data.	
	 */
	List<Object[]> getPromotionUsageData(
			Collection<Long> storeUids,
			Currency currency,
			Date startDate,
			Date endDate,
			Boolean withCouponCodesOnly);
	
	/**
	 * Get promotion details from orders.
	 *  
	 * @param storeUid the store uidpk
	 * @param currency currency. can be null.
	 * @param startDate start date to check order creation. can be null.
	 * @param endDate end date
	 * @param ruleCode promotion rule code
	 * @param couponCode coupon applied code. can be null.
	 * @return list of array data.
	 */
	List<Object[]> getPromotionDetailsData(final long storeUid, final Currency currency, final Date startDate, 
			final Date endDate, final String ruleCode, final String couponCode);
}
