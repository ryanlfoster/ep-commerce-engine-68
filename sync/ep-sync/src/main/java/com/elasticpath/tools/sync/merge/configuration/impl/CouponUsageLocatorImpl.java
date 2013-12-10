/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.tools.sync.merge.configuration.impl;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * The coupon usage locator.
 */
public class CouponUsageLocatorImpl extends AbstractEntityLocator {

	private CouponUsageService couponUsageService;
	
	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return CouponUsage.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz) throws SyncToolConfigurationException {
		Pair<String, String> guidPair = splitGuid(guid);
		// We are only interested in finding coupon usage with associated email
		if (StringUtils.isEmpty(guidPair.getSecond())) {
			return null;
		}
		return couponUsageService.findByCouponCodeAndEmail(guidPair.getFirst(), guidPair.getSecond());
	}
	
	private Pair<String, String> splitGuid(final String guid) {
		String[] guidParts = StringUtils.split(guid, "|");
		String email = null;
		if (guidParts.length == 2) {
			email = guidParts[1];
		}
		return new Pair<String, String>(guidParts[0], email);
	}

	/**
	 * @param couponUsageService the couponUsageService to set
	 */
	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}

}
