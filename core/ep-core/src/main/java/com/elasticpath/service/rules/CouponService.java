package com.elasticpath.service.rules;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Service for managing {@code Coupon}s.
 */
public interface CouponService {
	/**
	 * Save a Coupon.
	 *  
	 * @param coupon the <code>Coupon</code> to update or save if not persisted
	 * @return persisted Coupon from DB
	 * @throws com.elasticpath.service.rules.DuplicateCouponException if a Coupon equal to the given Coupon already exists.
	 * @throws EpServiceException in case of other errors
	 */
	Coupon add(final Coupon coupon) throws EpServiceException;	
	
	/**
	 * Save a Coupon and update its couponCode based on a given prefix.
	 * The couponCode suffix is generated.
	 *
	 * @param coupon the <code>Coupon</code> to update or save if not persisted
	 * @param couponCodePrefix the prefix to the unique CouponCode;
	 * @return persisted Coupon from DB
	 * @throws com.elasticpath.service.rules.DuplicateCouponException if a Coupon equal to the given Coupon already exists.
	 * @throws EpServiceException in case of other errors
	 */
	Coupon addAndGenerateCode(final Coupon coupon, final String couponCodePrefix);

	/**
	 * Update a Coupon with a new Coupon.
	 *  
	 * @param newCoupon the updated <code>Coupon</code>
	 * @return persisted Coupon from DB
	 * @throws EpServiceException in case of other errors
	 */
	Coupon update(final Coupon newCoupon) throws EpServiceException;
	
	/**
	 * Delete the Coupon.
	 *  
	 * @param coupon to delete
	 * @throws EpServiceException in case of error
	 */
	void delete(final Coupon coupon) throws EpServiceException;

	/**
	 * Find the collection of Coupons associated with the given rule code.
	 * 
	 * @param ruleCode the code whose coupons should be found
	 * @return the collection of Coupons
	 */
	Collection<Coupon> findCouponsForRuleCode(String ruleCode);
	
	/**
	 * Find the Coupons for the given coupon code.
	 * @param couponCode The coupon code.
	 * @return The coupon
	 */
	Coupon findByCouponCode(final String couponCode);
	
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
	 * @param ruleId the rule whose coupons should be found
	 * @param couponCodes a collection of coupon codes to match
	 * @return the collection of Coupons
	 */
	Collection<Coupon> findCouponsForRuleFromCouponCodes(long ruleId, Set<String> couponCodes);
	
	/**
	 * Deletes all coupons attached to a certain coupon config, including deleting all coupon usages.
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
	Collection<String> findExistingCouponCodes(final Collection<String> codes);

	/**
	 * Find any existing coupon codes from the given collection.
	 * 
	 * @param codes a collection of coupon codes
	 * @param exceptionRuleCode Find those associated with any OTHER rule code.
	 * @return any coupon codes that already exist
	 */
	Collection<String> findExistingCouponCodes(final Collection<String> codes, String exceptionRuleCode);

	/**
	 * Determine if this coupon code | email pair exists for this Rule.
	 *
	 * @param couponCode the couponCode.
	 * @param email the email.
	 * @param ruleCode the ruleCode.
	 * @return true if the pair is in the db this Rule.
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
	 * Find the collection of Coupons associated with the given rule code.
	 * The database query is sorted by {@code orderingFields} and only the records
	 * starting at {@code startIndex} up to {@code startIndex} + pageSize are returned.
	 * 
	 * @param configId the uidpk of the coupon config the coupons are assigned to
	 * @param searchCriteria the criteria to search by.
	 * @param startIndex The starting index
	 * @param pageSize The size of the page
	 * @param orderingFields The fields to order by
	 * @return the collection of Coupons
	 */
	Collection<Coupon> findCouponsForCouponConfigId(long configId, SearchCriterion[] searchCriteria, int startIndex,
			int pageSize, DirectedSortingField[] orderingFields);

	/**
	 * Returns the number of coupons which match the search criteria (e.g. {@code ruleCode}).
	 * @param couponConfigId The rule code to search for.
	 * @param searchCriteria the criteria to search by. 
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
	 * @param uids the collection of coupon uids
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

}
