package com.elasticpath.sfweb.util.impl;

import org.apache.log4j.Logger;

import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.sfweb.util.FilenameUtils;

/**
 * This retrieval strategy loads files from the Store Assets Folder under the Assets directory.
 * It is Store aware through the store config object, and will retrieve resources in store subfolders
 * according to the Store Code.
 * <pre>
 *   i.e. A Storefront request for /template-resources/power-reviews/power.js
 *   		will be located under
 *   	  ASSETS_FOLDER/storeassets/SNAPITUP/template-resources/power-reviews/power.js
 *   	
 *   Fallback to default stores is not supported. 
 * </pre>
 * 
 */
public class StoreAssetResourceRetrievalStrategy 
	extends AbstractResourceRetrievalStrategy {

	private static final Logger LOG = Logger.getLogger(StoreAssetResourceRetrievalStrategy.class);

	private StoreConfig storeConfig;
	
	/**
	 * @param storeConfig the store config to set
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}
	
	/**
	 * Return the store config.
	 *
	 * @return the store config.
	 */
	public StoreConfig getStoreConfig() {
		return this.storeConfig;
	}
	
	/**
	 * Resolve a resource on a store-specific basis, if it doesn't exist there is no fallback.
	 *
	 * @param resourcePath the path to the resource
	 * @return a file system path to the resource, could be null if no store-specific resource exists.
	 */
	@Override
	public String getFullPath(final String resourcePath) {
		String fullPath = FilenameUtils.formPath(getAssetRepository().getStoreAssetsPath(), getStoreConfig().getStoreCode(), resourcePath);
		LOG.debug(fullPath);
		return fullPath;
	}
}
