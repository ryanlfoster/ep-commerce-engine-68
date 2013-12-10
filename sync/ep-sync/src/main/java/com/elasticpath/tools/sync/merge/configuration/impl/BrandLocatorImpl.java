package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * 
 * The brand locator class.
 *
 */
public class BrandLocatorImpl extends AbstractEntityLocator {
	
	private BrandService brandService;
	
	/**
	 * @param brandService the brandService to set
	 */
	public void setBrandService(final BrandService brandService) {
		this.brandService = brandService;
	}
	
	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return Brand.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz)
			throws SyncToolConfigurationException {
		return brandService.findByCode(guid);
	}

}
