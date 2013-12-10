/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.service.rules.dao.impl;

import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.dao.impl.AbstractDaoImpl;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.rules.dao.CouponConfigDao;

/**
 * Implementation of data access object for {@code CouponConfig}.
 */
public class CouponConfigDaoImpl extends AbstractDaoImpl implements CouponConfigDao {

	@Override
	public CouponConfig add(final CouponConfig couponConfig) throws EpPersistenceException {
		CouponConfig updatedCouponConfig = null;
		try {
			updatedCouponConfig = getPersistenceEngine().saveOrUpdate(couponConfig);
		} catch (Exception ex) {
			throw new EpPersistenceException("Exception on adding Coupon.", ex);
		}
		return updatedCouponConfig;		
	}
	
	@Override
	public CouponConfig get(final long couponConfigUid) throws EpPersistenceException {
		return getPersistentBeanFinder().get(ContextIdNames.COUPON_CONFIG, couponConfigUid);
	}
	
	@Override
	public CouponConfig update(final CouponConfig couponConfig) throws EpPersistenceException {
		if (!couponConfig.isPersisted()) {
			throw new EpPersistenceException("Object is not persistent");
		}
		return getPersistenceEngine().saveOrUpdate(couponConfig);
	}
	
	@Override
	public void delete(final CouponConfig couponConfig) {
		getPersistenceEngine().delete(couponConfig);
	}

	@Override
	public CouponConfig findByRuleCode(final String ruleCode) {
		sanityCheck();
		if (ruleCode == null) {
			throw new EpServiceException("Cannot retrieve null coupon code.");
		}

		final List<CouponConfig> results = getPersistenceEngine().retrieveByNamedQuery("COUPON_CONFIG_BY_RULE_CODE", ruleCode);
		CouponConfig couponConfig = null;
		if (results.size() == 1) {
			couponConfig = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate coupon config exist -- " + ruleCode);
		}
		
		return couponConfig;
	}
	
	/**
	 * Sanity check of this service instance.
	 * 
	 * @throws EpServiceException - if something goes wrong.
	 */
	public void sanityCheck() throws EpServiceException {
		if (getPersistenceEngine() == null) {
			throw new EpServiceException("The persistence engine is not correctly initialized.");
		}
	}

	@Override
	public CouponConfig findByCode(final String couponConfigCode) {
		sanityCheck();
		List<CouponConfig> couponConfigs = getPersistenceEngine().retrieveByNamedQuery("COUPON_CONFIG_BY_CODE", couponConfigCode);
		if (couponConfigs.isEmpty()) {
			return null;
		}
		return couponConfigs.get(0);
	}

	@Override
	public String findGuidByRuleCode(final String ruleCode) {
		sanityCheck();
		List<String> guids = getPersistenceEngine().retrieveByNamedQuery("COUPON_CONFIG_GUID_BY_RULE_CODE", ruleCode);
		if (guids.isEmpty()) {
			return null;
		}
		return guids.get(0);
	}

}
