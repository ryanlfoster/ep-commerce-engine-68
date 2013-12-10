/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.tools.sync.target.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.target.AssociatedDaoAdapter;

/**
 * Coupon Usage DAO Adapter.
 */
public class CouponUsageDaoAdapterImpl extends AbstractDaoAdapter<CouponUsage> implements AssociatedDaoAdapter<CouponUsage> {

	private BeanFactory beanFactory;

	private CouponUsageService couponUsageService;
	
	private CouponService couponService;
	
	@Override
	public List<String> getAssociatedGuids(final Class< ? > clazz, final String guid) {
		if (Rule.class.isAssignableFrom(clazz)) {
			List<String> guids = new ArrayList<String>();
			Collection<CouponUsage> usages = couponUsageService.findByRuleCode(guid);
			for (CouponUsage usage : usages) {
				if (!StringUtils.isEmpty(usage.getCustomerEmailAddress())) {
					guids.add(usage.getGuid());
				}
			}
			return guids;
		}
		return Collections.emptyList();
	}

	@Override
	public Class< ? > getType() {
		return CouponUsage.class;
	}

	@Override
	public void add(final CouponUsage newPersistence) throws SyncToolRuntimeException {
		// sanity check.. it may be there already
		CouponUsage persistentUsage = couponUsageService.findByCouponCodeAndEmail(newPersistence.getCoupon().getCouponCode(),
				newPersistence.getCustomerEmailAddress());
		if (persistentUsage == null) {
			resolveCoupon(newPersistence);
			couponUsageService.add(newPersistence);
		}		
	}

	private void resolveCoupon(final CouponUsage mergedPersistence) {
		String couponCode = mergedPersistence.getCoupon().getCouponCode();
		Coupon coupon = couponService.findByCouponCode(couponCode);
		if (coupon == null) {
			throw new SyncToolRuntimeException("Could not find Coupon on Target system: " + couponCode  
					+ ". The Coupon must always be persisted first");
		}
		mergedPersistence.setCoupon(coupon);
	}

	@Override
	public CouponUsage createBean(final CouponUsage bean) {
		return beanFactory.getBean(ContextIdNames.COUPON_USAGE);
	}

	@Override
	public CouponUsage get(final String guid) {
		try {		
			return (CouponUsage) getEntityLocator().locatePersistence(guid, CouponUsage.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate Coupon Usage persistence", e);
		}
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		CouponUsage couponUsage = (CouponUsage) getEntityLocator().locatePersistence(guid, CouponUsage.class);
		if (couponUsage == null) {
			return false;
		}
		couponUsageService.delete(couponUsage);
		return true;
	}
	
	@Override
	public CouponUsage update(final CouponUsage mergedPersistence) throws SyncToolRuntimeException {
		resolveCoupon(mergedPersistence);
		return couponUsageService.update(mergedPersistence);
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	/**
	 * @param couponUsageService the couponUsageService to set
	 */
	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}
	
	/**
	 * @param couponService the coupon service to set
	 */
	public void setCouponService(final CouponService couponService) {
		this.couponService = couponService;
	}

}
