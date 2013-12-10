/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.tools.sync.target.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.target.AssociatedDaoAdapter;

/**
 * Coupon Dao Adapter.
 */
public class CouponConfigDaoAdapterImpl extends AbstractDaoAdapter<CouponConfig> implements AssociatedDaoAdapter<CouponConfig> {

	private BeanFactory beanFactory;

	private CouponConfigService couponConfigService;


	@Override
	public void add(final CouponConfig newPersistence) throws SyncToolRuntimeException {
		CouponConfig persistentConfig = couponConfigService.findByRuleCode(newPersistence.getRuleCode());
		if (persistentConfig == null) {
			couponConfigService.add(newPersistence);
		}
	}

	@Override
	public CouponConfig createBean(final CouponConfig couponConfig) {
		return beanFactory.getBean(ContextIdNames.COUPON_CONFIG);
	}

	@Override
	public CouponConfig get(final String guid) {
		try {		
			return (CouponConfig) getEntityLocator().locatePersistence(guid, CouponConfig.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate CouponConfig persistence", e);
		}
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		final CouponConfig couponConfig = couponConfigService.findByRuleCode(guid);
		if (couponConfig == null) {
			return false;
		}

		couponConfigService.delete(couponConfig);
		return true;
	}

	@Override
	public CouponConfig update(final CouponConfig mergedPersistence) throws SyncToolRuntimeException {
		return couponConfigService.update(mergedPersistence);
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

	@Override
	public List<String> getAssociatedGuids(final Class< ? > clazz, final String guid) {
		if (Rule.class.isAssignableFrom(clazz)) {
			// Use the rule code as the guid for the coupon config
			List<String> couponConfigGuids = new ArrayList<String>();
			CouponConfig config = couponConfigService.findByRuleCode(guid);
			if (config != null) {
				couponConfigGuids.add(config.getRuleCode());
			}
			return couponConfigGuids;
		}
		return Collections.emptyList();
	}

	@Override
	public Class< ? > getType() {
		return CouponConfig.class;
	}

}