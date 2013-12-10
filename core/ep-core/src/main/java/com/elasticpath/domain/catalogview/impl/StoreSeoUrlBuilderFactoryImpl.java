 /*
 * Copyright (c) Elastic Path Software Inc., 2006
 */

package com.elasticpath.domain.catalogview.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.catalogview.StoreSeoUrlBuilderFactory;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.StoreConfig;

/**
 * StoreSeoUrlBuilderFactory maintains a list of seoUrlBuilders keyed on store code.  
 */
public class StoreSeoUrlBuilderFactoryImpl implements StoreSeoUrlBuilderFactory {	
	
	/* Our list of seo url builders keyed on store code. */
	private Map<String, SeoUrlBuilder> storeSeoUrlBuilders = new ConcurrentHashMap<String, SeoUrlBuilder>();
	
	private BeanFactory beanFactory;
	
	private StoreConfig storeConfig;
	
	private String fieldSeparator = SeoConstants.DEFAULT_SEPARATOR_BETWEEN_TOKENS;
	
	/**
	 * Set bean factory.
	 * @param beanFactory is an instance of the bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
		
	/**
	 * Get the list of store seo url builders.
	 * @return list of seourlbuilders
	 */
	Map<String, SeoUrlBuilder> getStoreSeoUrlBuilders() {
		return storeSeoUrlBuilders;
	}

	/**
	 * Set the list of store seo url builders.
	 * @param storeSeoUrlBuilders the list of seo url builders
	 */
	void setStoreSeoUrlBuilders(
			final Map<String, SeoUrlBuilder> storeSeoUrlBuilders) {
		this.storeSeoUrlBuilders = storeSeoUrlBuilders;
	}
	
	/**
	 * Returns a specific store SeoUrlBuilder from the current list.
	 * 
	 * @return SeoUrlBuilder based on the store code
	 */
	public SeoUrlBuilder getStoreSeoUrlBuilder() {

		Store store = storeConfig.getStore();

		if (store == null) {
			throw new IllegalArgumentException("Store is null");
		}

		String storeCode = store.getCode();

		if (!storeSeoUrlBuilders.containsKey(storeCode)) {

			SeoUrlBuilder seoUrlBuilder = createSeoUrlBuilder(store);

			// Put it in the seo url builder map
			storeSeoUrlBuilders.put(storeCode, seoUrlBuilder);

			return seoUrlBuilder;
		}

		return storeSeoUrlBuilders.get(storeCode);
	}
	
	/**
	 * Create seoUrlBuilder using the prototype bean factory.
	 * 
	 * @param store
	 *            is the store
	 * @return the seoUrlBuilder
	 */
	private SeoUrlBuilder createSeoUrlBuilder(final Store store) {
		
		// Create a new coreSeoUrlBuilder	
		SeoUrlBuilder seoUrlBuilder = beanFactory.getBean(ContextIdNames.SEO_URL_BUILDER);
		seoUrlBuilder.setStore(store);
		seoUrlBuilder.setFieldSeparator(fieldSeparator);
		return seoUrlBuilder;
	}

	/**
	 * Set store config.
	 * @param storeConfig is the store config used to retrieve the store
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}
	
	@Override
	public void resetFieldSeparator(final String newTokenSeparator) {
		if (!fieldSeparator.equals(newTokenSeparator)) {
			fieldSeparator = newTokenSeparator;
			
			for (SeoUrlBuilder builder : storeSeoUrlBuilders.values()) {
				builder.setFieldSeparator(newTokenSeparator);
			}
		}
	}
	

	
}
