/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.tools.sync.target.impl;

import java.util.Collections;
import java.util.List;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.target.AssociatedDaoAdapter;

/**
 * Coupon Config Dao Adapter.
 */
public class CouponDaoAdapterImpl extends AbstractDaoAdapter<Coupon> implements AssociatedDaoAdapter<Coupon> {


	private BeanFactory beanFactory;

	private CouponConfigService couponConfigService;

	private CouponService couponService;
	
	@Override
	public void add(final Coupon newPersistence) throws SyncToolRuntimeException {
		// sanity check.. it may be there already
		Coupon persistentCoupon = couponService.findByCouponCode(newPersistence.getCouponCode());
		if (persistentCoupon == null) {
			resolveConfig(newPersistence);
			couponService.add(newPersistence);
		}
	}

	@Override
	public Coupon createBean(final Coupon bean) {
		return beanFactory.getBean(ContextIdNames.COUPON);
	}

	@Override
	public Coupon get(final String guid) {
		try {		
			return (Coupon) getEntityLocator().locatePersistence(guid, Coupon.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate Coupon persistence", e);
		}
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		final Coupon coupon = couponService.findByCouponCode(guid);
		if (coupon == null) {
			return false;
		}

		couponService.delete(coupon);
		return true;
	}

	@Override
	public Coupon update(final Coupon mergedPersistence) throws SyncToolRuntimeException {
		resolveConfig(mergedPersistence);
		return couponService.update(mergedPersistence);
	}
	
	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	/**
	 * @param couponConfigService the couponConfigService to set
	 */
	public void setCouponConfigService(final CouponConfigService couponConfigService) {
		this.couponConfigService = couponConfigService;
	}
	
	/**
	 * @param couponService the couponService to set
	 */
	public void setCouponService(final CouponService couponService) {
		this.couponService = couponService;
	}
	
	/**
	 * Find the CouponConfig to attach to the coupon.
	 *
	 * @param mergedPersistence
	 */
	private void resolveConfig(final Coupon mergedPersistence) {
		String ruleCode = mergedPersistence.getCouponConfig().getRuleCode();
		CouponConfig config = couponConfigService.findByRuleCode(ruleCode);
		if (config == null) {
			throw new SyncToolRuntimeException("Could not find CouponConfig for Coupon on Target system. " 
					+ "The CouponConfig must always be persisted first");
		}
		mergedPersistence.setCouponConfig(config);
	}

	@Override
	public List<String> getAssociatedGuids(final Class< ? > clazz, final String guid) {
		if (Rule.class.isAssignableFrom(clazz)) {
			return couponService.findCouponCodesByRuleCode(guid);
		}
		return Collections.emptyList();
	}

	@Override
	public Class< ? > getType() {
		return Coupon.class;
	}

}
