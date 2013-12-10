package com.elasticpath.sfweb.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.store.StoreRetrieveStrategy;
import com.elasticpath.service.store.impl.StoreServiceImpl;

/**
 * Extension of the StoreService that allows caching.
 */
public class CachingStoreServiceImpl extends StoreServiceImpl {

	private StoreRetrieveStrategy storeCache;

	@Override
	public Store getTunedStore(final long storeUid,  final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		Store store = getStoreCache().retrieveStore(storeUid, loadTuner);
		if (store == null) {
			store = super.getTunedStore(storeUid, loadTuner);
			store = cacheStore(loadTuner, store);
		}
		return store;
	}

	@Override
	public Collection<Store> getTunedStores(final Collection<Long> storeUids, final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		final List<Long> uidsToFind = new ArrayList<Long>();
		uidsToFind.addAll(storeUids);

		final Collection<Store> stores = getStoreCache().retrieveStores(storeUids, loadTuner);
		for (final Store store : stores) {
			uidsToFind.remove(store.getUidPk());
		}

		if (!uidsToFind.isEmpty()) {
			final Collection<Store> storesFromDb = super.getTunedStores(uidsToFind, loadTuner);
			for (final Store store : storesFromDb) {
				final Store cached = cacheStore(loadTuner, store);
				stores.add(cached);
			}
		}

		return stores;
	}

	@Override
	public Store findStoreWithCode(final String storeCode) throws EpServiceException {
		Store store = getStoreCache().retrieveStore(storeCode, null);
		if (store == null) {
			store = super.findStoreWithCode(storeCode);
			store = cacheStore(null, store);
		}
		return store;
	}

	private Store cacheStore(final FetchGroupLoadTuner loadTuner, final Store store) {
		Store detached = getPersistenceEngine().detach(store);
		getStoreCache().cacheStore(detached, loadTuner);

		return detached;
	}

	public void setStoreCache(final StoreRetrieveStrategy storeCache) {
		this.storeCache = storeCache;
	}

	protected StoreRetrieveStrategy getStoreCache() {
		return storeCache;
	}
}
