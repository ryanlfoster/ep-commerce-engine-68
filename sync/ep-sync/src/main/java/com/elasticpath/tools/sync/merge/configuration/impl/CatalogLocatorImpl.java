package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 *
 * The calalog locator class.
 *
 */
public class CatalogLocatorImpl extends AbstractEntityLocator {

	private CatalogService catalogService;

	/**
	 * @param catalogService the catalogService to set
	 */
	public void setCatalogService(final CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return Catalog.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz)	throws SyncToolConfigurationException {
		return catalogService.findByCode(guid);
	}
	
	@Override
	public Persistable locatePersistentReference(final String guid, final Class<?> clazz) throws SyncToolConfigurationException {
		return catalogService.findByGuid(guid, getEmptyFetchGroupLoadTuner());
	}

}
