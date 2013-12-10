package com.elasticpath.service.catalog.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.elasticpath.domain.catalog.ItemCharacteristics;
import com.elasticpath.domain.catalog.ItemConfigurationMemento.ItemConfigurationId;
import com.elasticpath.service.catalog.ItemCharacteristicsService;

/**
 * Caching decorator around the item characteristics service.
 */
public class CachingItemCharacteristicsServiceImpl implements ItemCharacteristicsService {

	private final Map<ItemConfigurationId, ItemCharacteristics> cache = new ConcurrentHashMap<ItemConfigurationId, ItemCharacteristics>();

	private ItemCharacteristicsService delegate;
	
	/**
	 * Gets the item characteristics by first looking in a cache.
	 *
	 * @param itemConfigurationId the item configuration id
	 * @return the item characteristics
	 */
	@Override
	public ItemCharacteristics getItemCharacteristics(final ItemConfigurationId itemConfigurationId) {
		if (!cache.containsKey(itemConfigurationId)) {
			ItemCharacteristics itemCharacteristics = delegate.getItemCharacteristics(itemConfigurationId);
			cache.put(itemConfigurationId, itemCharacteristics);
		}
		return cache.get(itemConfigurationId);
	}

	/**
	 * Sets the delegate service to use when items are not found in the cache.
	 *
	 * @param delegate the new delegate
	 */
	public void setDelegate(final ItemCharacteristicsService delegate) {
		this.delegate = delegate;
	}

	/**
	 * Gets the delegate service to use when items are not found in the cache.
	 *
	 * @return the delegate
	 */
	public ItemCharacteristicsService getDelegate() {
		return delegate;
	}

}
