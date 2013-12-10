package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * 
 * The store locator class.
 *
 */
public class StoreLocatorImpl extends AbstractEntityLocator {
	
	private StoreService storeService;
	
	/**
	 * @param storeService the storeService to set
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}
	
	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return Store.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz)
			throws SyncToolConfigurationException {
		return storeService.findStoreWithCode(guid);
	}

}
