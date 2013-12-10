package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * 
 * The sku option locator class.
 *
 */
public class SkuOptionLocatorImpl extends AbstractEntityLocator {
	
	private SkuOptionService skuOptionService;

	/**
	 * @param skuOptionService the skuOptionService to set
	 */
	public void setSkuOptionService(final SkuOptionService skuOptionService) {
		this.skuOptionService = skuOptionService;
	}
	
	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return SkuOption.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz)
			throws SyncToolConfigurationException {
		return skuOptionService.findByKey(guid);
	}

}
