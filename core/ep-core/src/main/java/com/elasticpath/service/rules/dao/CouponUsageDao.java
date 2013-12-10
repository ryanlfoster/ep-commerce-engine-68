package com.elasticpath.service.rules.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.persistence.api.EpPersistenceException;

/**
 * Data access object for CouponUsage.
 */
public interface CouponUsageDao {
	
	/**
	 * Get CouponUsage for uid.
	 *
	 * @param couponUsageUid the uid of the object to retrieve.
	 * @return the {@link CouponUsage}
	 * @throws EpPersistenceException in case of persistence exception.
	 */
	CouponUsage get(long couponUsageUid) throws EpPersistenceException;
	
	/**
	 * Deletes {@code couponUse}.
	 * @param couponUsage The object to delete.
	 */
	void delete(final CouponUsage couponUsage);
	
	/**
	 * Adds {@code couponUse} to the database.
	 * @param couponUsage The object to add.
	 * @return The persisted object.
	 */
	CouponUsage add(final CouponUsage couponUsage);
	
	/**
	 * Update {@code updateCouponUse} in the database.
	 * @param updatedCouponUse The object to update. 
	 * @return The persisted object.
	 */
	CouponUsage update(final CouponUsage updatedCouponUse);

	/**
	 * Finds a {@link CouponUsage} by the coupon code.
	 * 
	 * @param couponCode the coupon code.
	 * @return a list of {@link CouponUsage}.
	 */
	List<CouponUsage> findByCode(String couponCode);

	/**
	 * Gets the use count of the coupon usage having the coupon code.
	 * 
	 * @param couponCode the coupon code.
	 * @param emailAddress the customer address.
	 * @return the sum of the use count. 
	 */
	int getUseCountByCodeAndEmailAddress(String couponCode, String emailAddress);
	
	/**
	 * Gets the coupon usages for the rule specified by ruleCode.
	 * @param ruleCode The rule code.
	 * @return coupon usages. Empty collection if none found.
	 */
	Collection<CouponUsage> findByRuleCode(final String ruleCode);

	/**
	 * Finds the CouponUsage by the couponCode and customerEmailAddress.
	 * @param couponCode The couponCode.
	 * @param customerEmailAddress The customerEmailAddress.
	 * @return The couponUsage or null if not found.
	 */
	CouponUsage findByCouponCodeAndEmail(String couponCode, String customerEmailAddress);

	/**
	 * Finds coupon usages by rule code and email address.
	 * 
	 * @param ruleCode the rule code
	 * @param customerEmailAddress the email address
	 * @return a collection of coupon usage records for the given rule code and email
	 */
	Collection<CouponUsage> findByRuleCodeAndEmail(String ruleCode, String customerEmailAddress);

	/**
	 * Finds not expired till given date and email address coupon usages .
	 * 
	 * @param emailAddress the email address
	 * @param expirationDate expiration date. 
	 * @param storeUidPk current store uid pk.
	 * @return a collection of coupon usage records for the given email
	 */	
	Collection<CouponUsage> findEligibleUsagesByEmailAddressInStore(String emailAddress, Date expirationDate, Long storeUidPk);

	/**
	 * Deletes all coupon usages by giving a coupon config guid.
	 * 
	 * @param couponConfigGuid coupon config guid.
	 */
	void deleteAllUsagesByCouponConfigGuid(String couponConfigGuid);

	/**
	 * Find the collection of Coupon Usages associated with the given rule code.
	 * The database query is sorted by {@code orderingFields} and only the records
	 * starting at {@code startIndex} up to {@code startIndex} + pageSize are returned.
	 * 
	 * @param couponConfigId the id of the coupon config whose coupons should be found
	 * @param searchCriteria the criteria to search by.
	 * @param startIndex The starting index
	 * @param pageSize The size of the page
	 * @param orderingFields The fields to order by
	 * @return the collection of Coupons
	 */
	Collection<CouponUsage> findByCouponConfigId(long couponConfigId,
			SearchCriterion[] searchCriteria, int startIndex, int pageSize,
			DirectedSortingField[] orderingFields);

	/**
	 * Returns the number of coupon usages which match the search criteria (e.g. {@code ruleCode}).
	 * @param couponConfigId The coupon config to search for.
	 * @param searchCriteria the criteria to search by. 
	 * @return The count.
	 */
	long getCountForSearchCriteria(long couponConfigId,
			SearchCriterion[] searchCriteria);

	/**
	 * Find a collection of usages by uid.
	 * 
	 * @param uids the uids of the records to get
	 * @return a clelction of {@link CouponUsage} objects
	 */
	List<CouponUsage> findByUids(Collection<Long> uids);
}
