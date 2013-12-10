package com.elasticpath.service.rules;

import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.persistence.api.EpPersistenceException;

/**
 * Service methods to manage {@link CouponConfig}.
 */
public interface CouponConfigService {

	/**
	 * Add the given CouponConfig to the database.
	 * 
	 * @param couponConfig the coupon config to save.
	 * @return the saved object
	 * @throws EpPersistenceException in case of error
	 */
	CouponConfig add(final CouponConfig couponConfig) throws EpPersistenceException;

	/**
	 * Get the coupon config with the given uid.
	 * 
	 * @param couponConfigUid the id of the coupon config to get
	 * @return the {@link CouponConfig}.
	 */
	CouponConfig get(final long couponConfigUid);
	
	/**
	 * Get the coupon config by code.
	 * 
	 * @param couponConfigCode coupon config code.
	 * @return the {@link CouponConfig}.
	 */
	CouponConfig findByCode(final String couponConfigCode);
	
	/**
	 * Delete the given coupon configuration.
	 * 
	 * @param couponConfig the object to delete
	 */
	void delete(final CouponConfig couponConfig);

	/**
	 * Update the given coupon configuration.
	 * 
	 * @param couponConfig the config to update
	 * @return the updated object
	 */
	CouponConfig update(final CouponConfig couponConfig);

	/**
	 * Finds a coupon config by its corresponding rule code.
	 * 
	 * @param ruleCode the rule code.
	 * @return {@link CouponConfig} or null.
	 */
	CouponConfig findByRuleCode(String ruleCode);

	/**
	 * Find the guid of a coupon config object by its corresponding rule code.
	 * 
	 * @param ruleCode the rule code. 
	 * @return the guid of the {@link CouponConfig}
	 */
	String findGuidByRuleCode(String ruleCode);

}