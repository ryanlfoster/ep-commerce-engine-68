package com.elasticpath.sfweb.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import com.elasticpath.commons.util.InvalidatableCache;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.store.StoreRetrieveStrategy;

/**
 * Implementation of the Store Cache.
 */
public class EhCacheStoreRetrieveStrategyImpl extends AbstractEpServiceImpl implements StoreRetrieveStrategy, InvalidatableCache {

	private Ehcache storeCache;

	@Override
	public void invalidate() {
		storeCache.removeAll();
	}

	@Override
	public void invalidate(final Object objectUid) {
		storeCache.remove(objectUid);
	}

	@Override
	public Store retrieveStore(final long storeUid, final FetchGroupLoadTuner storeLoadTuner) {
		final Pair<Store, LoadTuner> cacheRecord = findInCache(storeUid);
		if (isValid(cacheRecord, storeLoadTuner)) {
			return cacheRecord.getFirst();
		}
		return null;
	}

	@Override
	public List<Store> retrieveStores(final Collection<Long> storeUids, final FetchGroupLoadTuner storeLoadTuner) {

		final List<Store> stores = new ArrayList<Store>(storeUids.size());
		for (final Long uid : storeUids) {
			final Store store = retrieveStore(uid, storeLoadTuner);
			if (store != null) {
				stores.add(store);
			}
		}
		return stores;
	}

	@Override
	public Store retrieveStore(final String storeCode, final LoadTuner storeLoadTuner) {
		final Pair<Store, LoadTuner> cacheRecord = findInCache(storeCode);
		if (isValid(cacheRecord, storeLoadTuner)) {
			return cacheRecord.getFirst();
		}
		return null;
	}

	private boolean isValid(final Pair<Store, LoadTuner> cacheRecord, final LoadTuner loadTuner) {
		return cacheRecord != null
			&& (cacheRecord.getSecond() == loadTuner || cacheRecord.getSecond().contains(loadTuner));
	}

	@SuppressWarnings("unchecked")
	private Pair<Store, LoadTuner> findInCache(final Object cacheKey) {
		final Element cacheElement = storeCache.get(cacheKey);
		if (cacheElement != null && !cacheElement.isExpired()) {
			final Object cacheValue = cacheElement.getValue();
			if (cacheValue instanceof Long) {
				return findInCache(cacheValue);
			}

			return (Pair<Store, LoadTuner>) cacheValue;
		}
		return null;
	}

	@Override
	public void cacheStore(final Store store, final LoadTuner loadTuner) {
		final Pair<Store, LoadTuner> storeWithTuner = new Pair<Store, LoadTuner>(store, loadTuner);
		final Element cacheByUid =  new Element(store.getUidPk(), storeWithTuner);
		final Element cacheByCode = new Element(store.getCode(), store.getUidPk());
		storeCache.put(cacheByUid);
		storeCache.put(cacheByCode);
	}

	/**
	 * Sets the EhCache .
	 * @param storeCache the cache of stores.
	 */
	public void setStoreCache(final Ehcache storeCache) {
		this.storeCache = storeCache;
	}
}
