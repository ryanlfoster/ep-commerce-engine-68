/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.view.helpers;

import java.util.List;

import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalogview.impl.ThreadLocalStorageImpl;

/**
 * Helper for determining the top categories in a catalog.
 */
public class TopCategoriesHelper {

	private static final long DEFAULT_CACHE_TIMEOUT_VALUE = 30000;
	
	private final SimpleTimeoutCache<String, List<Category>> topCategoryCache = 
						new SimpleTimeoutCache<String, List<Category>>(DEFAULT_CACHE_TIMEOUT_VALUE);
	
	private ThreadLocalStorageImpl storeConfig;
	
	private CategoryService categoryService;
	
	/**
	 * Creates a map with all the store codes mapped to their top categories.
	 * 
	 * @return The top categories associated with the <code>storeCode</code>.
	 */
	public List<Category> resolveTopCategories() {
		// Attempt to get the store code from the store config
		String storeCode = storeConfig.getStoreCode();
		
		List<Category> topCategories = topCategoryCache.get(storeCode);
		// If the store code does not exist in the cache, then get it from the database
		// and update the cache
		if (topCategories == null) {
			topCategories = categoryService.listRootCategories(storeConfig.getStore().getCatalog(), true);
			topCategoryCache.put(storeCode, topCategories);
		}
		
		return topCategories;
	}
	
	/**
	 * Sets the cache timeout in milliseconds.
	 *
	 * @param cacheTimeoutMillis the cacheTimeoutMillis to set
	 */
	public void setCacheTimeoutMillis(final long cacheTimeoutMillis) {
		topCategoryCache.setTimeout(cacheTimeoutMillis);
	}
	
	/**
	 * Sets the store config instance.
	 * 
	 * @param storeConfig the store config
	 */
	public void setStoreConfig(final ThreadLocalStorageImpl storeConfig) {
		this.storeConfig = storeConfig;
	}
	
	/**
	 * Sets the category service.
	 * 
	 * @param categoryService the category service
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}
}
