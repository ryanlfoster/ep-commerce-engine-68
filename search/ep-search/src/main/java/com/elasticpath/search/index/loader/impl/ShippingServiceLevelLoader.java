package com.elasticpath.search.index.loader.impl;

import java.util.Collection;

import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.service.shipping.ShippingServiceLevelService;

/**
 * Fetches a batch of {@link ShippingServiceLevel}s.
 */
public class ShippingServiceLevelLoader extends AbstractEntityLoader<ShippingServiceLevel> {

	private ShippingServiceLevelService shippingServiceLevelService;

	/**
	 * Loads the {@link ShippingServiceLevel}s for the batched ids and loads each batch in bulk.
	 * 
	 * @return the loaded {@link ShippingServiceLevel}s
	 */
	public Collection<ShippingServiceLevel> loadBatch() {

		final Collection<ShippingServiceLevel> loadedShippingServiceLevelBatch = getShippingServiceLevelService().findByUids(getUidsToLoad());

		return loadedShippingServiceLevelBatch;
	}

	/**
	 * @param shippingServiceLevelService the shippingServiceLevelService to set
	 */
	public void setShippingServiceLevelService(final ShippingServiceLevelService shippingServiceLevelService) {
		this.shippingServiceLevelService = shippingServiceLevelService;
	}

	/**
	 * @return the shippingServiceLevelService
	 */
	public ShippingServiceLevelService getShippingServiceLevelService() {
		return shippingServiceLevelService;
	}

}
