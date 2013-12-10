package com.elasticpath.service.rules;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Service for managing {@code CouponUsage}s.
 */
public interface CouponUsageService {
	/**
	 * Save a CouponUsage.
	 *  
	 * @param couponUsage the <code>CouponUsage</code> to update or save if not persisted
	 * @return persisted CouponUsage from DB
	 * @throws EpServiceException in case of other errors
	 */
	CouponUsage add(final CouponUsage couponUsage) throws EpServiceException;

	/**
	 * Update a CouponUsage with a new CouponUsage.
	 *  
	 * @param newCouponUse the updated <code>CouponUsage</code>
	 * @return persisted CouponUsage from DB
	 * @throws EpServiceException in case of other errors
	 */
	CouponUsage update(final CouponUsage newCouponUse) throws EpServiceException;
	
	/**
	 * Delete the CouponUsage.
	 *  
	 * @param couponUsage to delete
	 * @throws EpServiceException in case of error
	 */
	void delete(final CouponUsage couponUsage) throws EpServiceException;

	/**
	 * Finds a list of coupon usage by code.
	 * 
	 * @param couponCode the coupon code.
	 * @throws EpServiceException in case of error.
	 * @return a list of {@link CouponUsage}.
	 */
	List<CouponUsage> findByCode(String couponCode) throws EpServiceException;
	
	/**
	 * Gets the use count of the coupon usage having the coupon code and customer email
	 * address.
	 * 
	 * @param couponCode the coupon code.
	 * @param emailAddress The customer email address.
	 * @return the sum of the use count. 
	 * @throws EpServiceException in case of exception retrieving the count.
	 */
	int getUseCountByCodeAndEmailAddress(String couponCode, String emailAddress) throws EpServiceException;
	
	/**
	 * Updates the use count on a limited usage coupon.   
	 * 
	 * @param shoppingCart the cart that contains the information needed for updating coupon usage.
	 * @param appliedRules the applied rules object.
	 */
	void updateLimitedUsageCouponCurrentNumbers(final ShoppingCart shoppingCart, final Set<AppliedRule> appliedRules);

	/**
	 * @param ruleCode The rule code to search by.
	 * @return The coupon usages for that rule.
	 */
	Collection<CouponUsage> findByRuleCode(String ruleCode);
	
	/**
	 * Finds the CouponUsage by the couponCode and customerEmailAddress.
	 * @param couponCode The couponCode.
	 * @param customerEmailAddress The customerEmailAddress.
	 * @return The couponUsage or null if not found.
	 */
	CouponUsage findByCouponCodeAndEmail(String couponCode, String customerEmailAddress);
	
	/**
	 * Checks if the promo code is valid. 
	 * 
	 * @param customerEmailAddress The email address.
	 * @param coupon The coupon.
	 * @param couponConfig the coupon config
	 * @param couponUsage the coupon usage
	 * @return True if the couponCode is valid.
	 */
	boolean isValidPromoCode(final String customerEmailAddress,
			final Coupon coupon, final CouponConfig couponConfig, final CouponUsage couponUsage);
	
	/**
	 * Find the usage - taking into consideration coupon type.
	 * @param couponConfig CouponConfig.
	 * @param couponCode Code of coupon under test.
	 * @param customerEmailAddress Email of user.
	 * @return CouponUsage number.
	 */
	CouponUsage findByCodeAndType(final CouponConfig couponConfig, final String couponCode, final String customerEmailAddress);
	

	/**
	 * Finds coupon usages by rule code and email address.
	 * 
	 * @param ruleCode the rule code
	 * @param customerEmailAddress the email address
	 * @return a collection of coupon usage records for the given rule code and email
	 */
	Collection<CouponUsage> findByRuleCodeAndEmail(String ruleCode, String customerEmailAddress);
	
	/**
	 * Assign coupons to customers who are entitled to receive a coupon as a result of a promotion.
	 * 
	 * @param appliedRuleUids The list of rule ids which have been applied.
	 * @param customerEmailAddress The email address for the customer.
	 */
	void processCouponCustomerAssignments(Set<Long> appliedRuleUids, final String customerEmailAddress);

	/**
	 * Finds all eligible coupon usages for the given email address.
	 * CouponUsages must be valid for use in the store.
	 * 
	 * @param emailAddress the email address
	 * @param storeUidPk the store to look in
	 * @return a collection of coupon usage records for the given email and store
	 */
	Collection<CouponUsage> findEligibleUsagesByEmailAddress(String emailAddress, Long storeUidPk);
	
	/**
	 * Finds not expired till given date and email address coupon usages .
	 * 
	 * @param emailAddress the email address
	 * @param expirationDate expiration date. 
	 * @param storeUidPk store uidPk
	 * @return a collection of coupon usage records for the given email and store
	 */	
	Collection<CouponUsage> findAllUsagesByEmailAddress(String emailAddress, Date expirationDate, Long storeUidPk);	
	
	/**
	 * Deletes all coupon usages by giving a coupon config guid.
	 * 
	 * @param couponConfigGuid coupon config guid.
	 */
	void deleteAllUsagesByCouponConfigGuid(String couponConfigGuid);

	/**
	 * Resolves all the coupons in the cart that will take affect. It will calculate the coupon usage against its limit and the result only contains
	 * coupons that trigger the best case scenario. For example, coupon1 will trigger 25% off all items in the cart and coupon2 will trigger 10% off
	 * all items in the cart. Only coupon1 will be included in the return result since the customer will get more discount.
	 * 
	 * @param shoppingCart {@link ShoppingCart}.
	 * @return the coupon codes that are applied.
	 */
	Set<String> resolveCouponsInCart(final ShoppingCart shoppingCart);
	
	/**
	 * Find the collection of Coupon Usages associated with the given rule code.
	 * The database query is sorted by {@code orderingFields} and only the records
	 * starting at {@code startIndex} up to {@code startIndex} + pageSize are returned.
	 * 
	 * @param ruleCode the code whose coupons should be found
	 * @param searchCriteria the criteria to search by.
	 * @param startIndex The starting index
	 * @param pageSize The size of the page
	 * @param orderingFields The fields to order by
	 * @return the collection of Coupons
	 */
	Collection<CouponUsage> findCouponUsagesForCouponConfigId(long ruleCode, SearchCriterion[] searchCriteria, int startIndex,
			int pageSize, DirectedSortingField[] orderingFields);
	
	/**
	 * Returns the number of coupons which match the search criteria (e.g. {@code ruleCode}).
	 * @param couponConfigId The coupon config to search for.
	 * @param searchCriteria the criteria to search by. 
	 * @return The count.
	 */
	long getCountForSearchCriteria(long couponConfigId, SearchCriterion[] searchCriteria);

	/**
	 * Find a collection of usages by uid.
	 * 
	 * @param uids the uids of the records to get
	 * @return a clelction of {@link CouponUsage} objects
	 */
	List<CouponUsage> findByUids(Collection<Long> uids);
}
