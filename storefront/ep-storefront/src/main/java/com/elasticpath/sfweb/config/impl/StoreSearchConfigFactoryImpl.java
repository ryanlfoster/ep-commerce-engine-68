/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.config.impl;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.search.impl.AbstractSettingsSearchConfigFactory;

/**
 *  A factory for getting the <code>SearchConfig</code> that is store aware.
 */
public class StoreSearchConfigFactoryImpl extends AbstractSettingsSearchConfigFactory {
	
	private StoreConfig storeConfig;
	
	/**
	 * Gets the search configuration for the given index name and the current store.
	 * If no configuration is found for the given index name, returns the default configuration.
	 * 
	 * @param indexName the name of the index whose search configuration should be retrieved
	 * @return a search configuration
	 */
	public SearchConfig getSearchConfig(final String indexName) {
		final String context = "STORE/" + getStoreConfig().getStoreCode() + "/" + indexName;
		return getSearchConfig(indexName, context);
	}

	/**
	 * Get the store configuration.
	 * 
	 * @return a <code>StoreConfig</code> object.
	 */
	public StoreConfig getStoreConfig() {
		return storeConfig;
	}

	/**
	 * Set the store configuration.
	 * 
	 * @param storeConfig the <code>StoreConfig</code> to set
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}

}
