package com.elasticpath.sfweb.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.service.shipping.impl.ShippingServiceLevelServiceImpl;
import com.elasticpath.sfweb.service.EntityCache;

/**
 * Extension of the ShippingServiceLevelService that caches retrievals.
 */
public class CachingShippingServiceLevelServiceImpl extends ShippingServiceLevelServiceImpl {

	private EntityCache<ShippingServiceLevel> entityCache;

	@Override
	public ShippingServiceLevel get(final long shippingServiceLevelUid) throws EpServiceException {
		ShippingServiceLevel cached = getEntityCache().get("uidPk", shippingServiceLevelUid);
		if (cached != null) {
			return cached;
		}

		ShippingServiceLevel found = super.get(shippingServiceLevelUid);
		if (found != null) {
			cached = getEntityCache().put(found);
		}

		return cached;
	}

	@Override
	public ShippingServiceLevel findByCode(final String code) throws EpServiceException {
		ShippingServiceLevel cached = getEntityCache().get("code", code);
		if (cached != null) {
			return cached;
		}

		ShippingServiceLevel found = super.findByCode(code);
		if (found != null) {
			cached = getEntityCache().put(found);
		}

		return cached;
	}

	@Override
	public ShippingServiceLevel findByGuid(final String guid) throws EpServiceException {
		ShippingServiceLevel cached = getEntityCache().get("guid", guid);
		if (cached != null) {
			return cached;
		}

		ShippingServiceLevel found = super.findByGuid(guid);
		if (found != null) {
			cached = getEntityCache().put(found);
		}

		return cached;
	}

	@Override
	public List<ShippingServiceLevel> findByStoreAndState(final String storeCode, final boolean active) throws EpServiceException {
		List<Long> sslUids = findUidsByStoreAndState(storeCode, active);
		List<ShippingServiceLevel> serviceLevels = new ArrayList<ShippingServiceLevel>(sslUids.size());
		for (Long uidPk : sslUids) {
			serviceLevels.add(get(uidPk));
		}

		return serviceLevels;
	}

	public void setEntityCache(final EntityCache<ShippingServiceLevel> entityCache) {
		this.entityCache = entityCache;
	}

	protected EntityCache<ShippingServiceLevel> getEntityCache() {
		return entityCache;
	}
}
