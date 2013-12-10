package com.elasticpath.service.rules.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.persistence.api.EpPersistenceException;

/**
 * Data access object for Coupon.
 */
public interface CouponDao {
	
	/**
	 * get Coupon given the uid.
	 *
	 * @param couponUid the uid.
	 * @return The coupon.
	 * @throws EpPersistenceException for persistence exceptions.
	 */
	Coupon get(final long couponUid) throws EpPersistenceException;

	/**
	 * Deletes {@code couponCode}.
	 * @param coupon The object to delete.
	 */
	void delete(final Coupon coupon);
	
	/**
	 * Adds {@code couponCode} to the database.
	 * @param coupon The object to add.
	 * @return The persisted object.
	 */
	Coupon add(final Coupon coupon);
	
	/**
	 * Update {@code updateCouponCode} in the database.
	 * @param updatedCouponCode The object to update. 
	 * @return The persisted object.
	 */
	Coupon update(final Coupon updatedCouponCode);

	/**
	 * Find the Coupons which are associated with the rule code.
	 * 
	 * @param ruleCode the code of the rule whose coupons to find
	 * @return the collection of coupons
	 */
	Collection<Coupon> findByRuleCode(String ruleCode);
	
	/**
	 * Find the collection of Coupons associated with the given rule code.
	 * The database query is sorted by {@code orderingFields} and only the records
	 * starting at {@code startIndex} up to {@code startIndex} + pageSize are returned.
	 * 
	 * @param configId the coupon config id to filter against
	 * @param searchCriteria The search criteria
	 * @param startIndex The starting index
	 * @param pageSize The size of the page
	 * @param orderingFields The fields to order by
	 * @return the collection of Coupons
	 */
	Collection<Coupon> findByCouponConfigId(long configId, SearchCriterion[] searchCriteria, int startIndex,
			int pageSize, DirectedSortingField[] orderingFields);
	
	/**
	 * Finds the Coupon which match {@code couponCode}.
	 * 
	 * @param couponCode The coupon code to match.
	 * @return The collection of matching {@code couponCode}s
	 */
	Collection<Coupon> findByCouponCode(String couponCode);

	/**
	 * Find the collection of user-specific coupons, which are unused by the user id,
	 *  for rule {@code ruleCode} for {@code userId}.
	 * 
	 * @param ruleCode The rule code to match.
	 * @param customerEmailAddress the user id to check
	 * @return The collection of matching {@code couponCode}s
	 */
	Collection<Coupon> findUnusedCouponsForRuleAndUser(
			String ruleCode, String customerEmailAddress);
	
	/**
	 * Find coupons for the given rule code that correspond to coupon codes in the given set.
	 * 
	 * @param ruleCode the code of the rule whose coupons should be found
	 * @param couponCodes a collection of coupon codes to match
	 * @return the collection of Coupons
	 */
	Collection<Coupon> findCouponsForRuleCodeFromCouponCodes(String ruleCode, Set<String> couponCodes);

	/**
	 * Find coupons for the given rule uid that correspond to coupon codes in the given set.
	 * 
	 * @param ruleId the id of the rule whose coupons should be found
	 * @param couponCodes a collection of coupon codes to match
	 * @return the collection of Coupons
	 */
	Collection<Coupon> findCouponsForRuleFromCouponCodes(final long ruleId, final Set<String> couponCodes);

	/**
	 * Deletes all coupons attached to a certain coupon config.
	 * 
	 * @param couponConfigGuid the guid of the coupon
	 */
	void deleteCouponsByCouponConfigGuid(String couponConfigGuid);

	/**
	 * Find any existing coupon codes from the given collection.
	 * 
	 * @param codes a collection of coupon codes
	 * @return any coupon codes that already exist
	 */
	Collection<String> findExistingCouponCodes(Collection<String> codes);

	/**
	 * Find any existing coupon codes from the given collection.
	 * 
	 * @param codes a collection of coupon codes
	 * @param exceptionRuleCode check only coupons for rules other than this.
	 * @return any coupon codes that already exist
	 */
	Collection<String> findExistingCouponCodes(Collection<String> codes, String exceptionRuleCode);

	/**
	 * Determine if this coupon code | email pair exists for this RuleCode.
	 *
	 * @param couponCode the couponCode.
	 * @param email the email.
	 * @param ruleCode the ruleCode.
	 * @return true if the pair is in the db for this rule code.
	 */
	boolean doesCouponCodeEmailPairExistForThisRuleCode(String couponCode, String email, String ruleCode);
	
	/**
	 * Determine if this coupon code exists for any rule code other than this one.
	 *
	 * @param couponCode the couponCode.
	 * @param ruleCode the ruleCode.
	 * @return true if the coupon code is in the db for other rule codes.
	 */
	boolean doesCouponCodeOnlyExistForThisRuleCode(String couponCode, String ruleCode);
		
	/**
	 * Returns the number of coupons which match the search criteria (e.g. {@code ruleCode}).
	 * @param couponConfigId The rule code to search for.
	 * @param searchCriteria The criteria to use. 
	 * @return The count.
	 */
	long getCountForSearchCriteria(long couponConfigId, SearchCriterion[] searchCriteria);

	/**
	 * Return the collection of coupon codes that are associated with the given rule code.
	 * 
	 * @param ruleCode the rule code
	 * @return the list of coupon codes associated with the rule
	 */
	List<String> findCouponCodesByRuleCode(String ruleCode);

	/**
	 * Return a collection of coupons that are identified by the given collection of uids.
	 *  
	 * @param uids the collection of uids
	 * @return the coupons
	 */
	List<Coupon> findByUids(Collection<Long> uids);

	/**
	 * Return a map of coupon codes to coupons.
	 * 
	 * @param codes the codes of the coupons to retrieve
	 * @return a map connecting the code to the coupon
	 */
	Map<String, Coupon> findCouponsForCodes(Collection<String> codes);
	
	/**
	 * Return a lastest coupon matching prefix.
	 * 
	 * @param couponCodePrefix prefix to match against
	 * @return the latest coupon found
	 */
	Coupon getLastestCoupon(String couponCodePrefix);	
}
