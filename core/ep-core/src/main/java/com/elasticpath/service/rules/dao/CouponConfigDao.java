package com.elasticpath.service.rules.dao;

import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.persistence.api.EpPersistenceException;

/**
 * Implementation of data access object for {@code CouponConfig}.
 */
public interface CouponConfigDao {

	/**
	 * Adds a {@link CouponConfig} to the database.
	 * 
	 * @param couponConfig the object to add
	 * @return the object
	 * @throws EpPersistenceException in case of persistence exception
	 */
	CouponConfig add(final CouponConfig couponConfig) throws EpPersistenceException;

	/**
	 * Get the coupon config with the given uid.
	 * 
	 * @param couponConfigUid the uid of the object to retrieve
	 * @return the {@link CouponConfig}
	 * @throws EpPersistenceException in case of persistence exception
	 */
	CouponConfig get(long couponConfigUid) throws EpPersistenceException;
	
	/**
	 * Update the database with changes to the given {@link CouponConfig}.
	 * 
	 * @param couponConfig the object to update
	 * @return the updated object
	 * @throws EpPersistenceException in case of persistence exception
	 */
	CouponConfig update(final CouponConfig couponConfig) throws EpPersistenceException;

	/**
	 * Delete the give {@link CouponCode}.
	 * 
	 * @param couponConfig the object to delete
	 */
	void delete(final CouponConfig couponConfig);

	/**
	 * Finds a coupon config by its corresponding rule code.
	 * 
	 * @param ruleCode the rule code.
	 * @return {@link CouponConfig} or null.
	 */
	CouponConfig findByRuleCode(String ruleCode);


	/**
	 * Get the coupon config by code.
	 * 
	 * @param couponConfigCode coupon config code.
	 * @return the {@link CouponConfig}.
	 */
	CouponConfig findByCode(String couponConfigCode);

	/**
	 * Find the guid of a coupon config object by its corresponding rule code.
	 * 
	 * @param ruleCode the rule code. 
	 * @return the guid of the {@link CouponConfig}
	 */
	String findGuidByRuleCode(String ruleCode);

}