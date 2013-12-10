/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * The coupon config locator class.
 */
public class CouponConfigLocatorImpl extends AbstractEntityLocator {

	
	private CouponConfigService couponConfigService;
	
	/**
	 * @param couponConfigService the coupon config service to set
	 */
	public void setCouponConfigService(final CouponConfigService couponConfigService) {
		this.couponConfigService = couponConfigService;
	}
	
	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return CouponConfig.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz) throws SyncToolConfigurationException {
		return couponConfigService.findByRuleCode(guid);
	}

}
