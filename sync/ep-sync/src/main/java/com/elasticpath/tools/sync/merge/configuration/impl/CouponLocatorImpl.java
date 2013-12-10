/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * The coupon locator.
 */
public class CouponLocatorImpl extends AbstractEntityLocator {

	private CouponService couponService;
	
	/**
	 * @param couponService the couponService to set
	 */
	public void setCouponService(final CouponService couponService) {
		this.couponService = couponService;
	}
	
	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return Coupon.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz) throws SyncToolConfigurationException {
		return couponService.findByCouponCode(guid);
	}

}
