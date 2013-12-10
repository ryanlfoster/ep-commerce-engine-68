package com.elasticpath.service.store;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.EpService;

/**
 *	Used to retrieve Stores.
 */
public interface StoreRetrieveStrategy extends EpService {

	/**
	 * Retrieve a <code>store</code> with the given store uid. Return <code>null</code> if
	 * a store with the given uid doesn't exist.
	 * <p>
	 *
	 * @param storeUid a store uid
	 * @param storeLoadTuner the store load tuner
	 * @return a <code>store</code> with the given store uid.
	 */
	Store retrieveStore(long storeUid, final FetchGroupLoadTuner storeLoadTuner);

	/**
	 * Retrieve a list of <code>store</code> of the given store uids.
	 *
	 * @param storeUids a collection of store uids
	 * @param storeLoadTuner the store load tuner
	 * @return a list of <code>store</code>s.
	 */
	List<Store> retrieveStores(Collection<Long> storeUids, FetchGroupLoadTuner storeLoadTuner);

	/**
	 * Retrieve a <code>store</code> with the given store code. Return <code>null</code> if
	 * a store with the given code doesn't exist.
	 * <p>
	 *
	 * @param storeCode a store code
	 * @param storeLoadTuner the store load tuner
	 * @return a <code>store</code> with the given store code.
	 */
	Store retrieveStore(String storeCode, LoadTuner storeLoadTuner);

	/**
	 * Adds a Store to the cache.
	 * @param store the Store to cache.
	 * @param loadTuner the LoadTuner to cache with.
	 */
	void cacheStore(Store store, LoadTuner loadTuner);
}
