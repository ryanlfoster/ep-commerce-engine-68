/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.service.rules.impl;

import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.dao.CouponConfigDao;
import com.elasticpath.service.rules.dao.CouponDao;
import com.elasticpath.service.rules.dao.CouponUsageDao;

/**
 * Implementation of {@link CouponConfigService}. 
 */
public class CouponConfigServiceImpl implements CouponConfigService {

	private CouponConfigDao couponConfigDao;
	
	private CouponUsageDao couponUsageDao;
	
	private CouponDao couponDao;
	
	@Override
	public CouponConfig add(final CouponConfig couponConfig) throws EpPersistenceException {
		CouponConfig newCouponConfig = null;
		try {
			newCouponConfig = getCouponConfigDao().add(couponConfig);
		} catch (Exception ex) {
			throw new EpServiceException("Failed to add " + couponConfig, ex);
		}
		return newCouponConfig;
	}

	@Override
	public CouponConfig get(final long couponConfigUid) {
		return getCouponConfigDao().get(couponConfigUid);
	}
	
	@Override
	public void delete(final CouponConfig couponConfig) {
		getCouponUsageDao().deleteAllUsagesByCouponConfigGuid(couponConfig.getGuid());
		getCouponDao().deleteCouponsByCouponConfigGuid(couponConfig.getGuid());
		getCouponConfigDao().delete(couponConfig);
	}

	@Override
	public CouponConfig update(final CouponConfig couponConfig) {
		return getCouponConfigDao().update(couponConfig);
	}

	/**
	 * Set the DAO to use for coupon configuration persistence.
	 * 
	 * @param couponConfigDao the coupon config dao
	 */
	public void setCouponConfigDao(final CouponConfigDao couponConfigDao) {
		this.couponConfigDao = couponConfigDao;
	}

	/**
	 * Get the DAO to use for coupon configuration persistence.
	 * 
	 * @return the coupon config dao
	 */
	public CouponConfigDao getCouponConfigDao() {
		return couponConfigDao;
	}

	@Override
	public CouponConfig findByRuleCode(final String ruleCode) {
		return getCouponConfigDao().findByRuleCode(ruleCode);
	}

	@Override
	public CouponConfig findByCode(final String couponConfigCode) {
		return getCouponConfigDao().findByCode(couponConfigCode);
	}

	@Override
	public String findGuidByRuleCode(final String ruleCode) {
		return getCouponConfigDao().findGuidByRuleCode(ruleCode);
	}
	
	/**
	 * Set the coupon usage DAO.
	 * 
	 * @param couponUsageDao the couponUsageDao to set
	 */
	public void setCouponUsageDao(final CouponUsageDao couponUsageDao) {
		this.couponUsageDao = couponUsageDao;
	}

	/**
	 * Get the coupon usage DAO.
	 * 
	 * @return the couponUsageDao
	 */
	public CouponUsageDao getCouponUsageDao() {
		return couponUsageDao;
	}

	/**
	 * Set the coupon DAO.
	 * 
	 * @param couponDao the couponDao to set
	 */
	public void setCouponDao(final CouponDao couponDao) {
		this.couponDao = couponDao;
	}

	/**
	 * Get the coupon Dao.
	 * 
	 * @return the couponDao
	 */
	public CouponDao getCouponDao() {
		return couponDao;
	}

}
