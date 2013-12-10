package com.elasticpath.service.store;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provides store-related services.
 */
public interface StoreService extends EpPersistenceService {

	/**
	 * Saves or updates a given <code>Store</code>.
	 *
	 * @param store the <code>Store</code> to save or update
	 *
	 * @return the merged store if it is merged, or the persisted object for save action
	 * @throws EpServiceException - in case of any errors
	 */
	Store saveOrUpdate(final Store store) throws EpServiceException;

	/**
	 * Deletes a store and all associated warehouses.
	 *
	 * @param store the warehouse to remove
	 * @throws EpServiceException in case of any errors
	 */
	void remove(final Store store) throws EpServiceException;

	/**
	 * Gets a <code>Store</code> with the given UID. Returns null if no matching records exist.
	 *
	 * @param storeUid the <code>Store</code> UID
	 * @return a store with the given UID
	 * @throws EpServiceException in case of any errors
	 */
	Store getStore(final long storeUid) throws EpServiceException;

	/**
	 * Determine whether a store has a unique URL within the confines of the given state.
	 *
	 * @param store the store to check
	 * @param state the state to
	 * @return true if there are no other stores in the given state with the same URL
	 * @throws EpServiceException in case of any errors
	 */
	boolean isStoreUrlUniqueForState(final Store store, final StoreState state) throws EpServiceException;

	/**
	 * Gets a <code>Store</code> with the given <code>storeCode</code>. Returns null if no matching records exist.
	 *
	 * @param storeCode the store code to look up
	 * @return a store with the given store code
	 * @throws EpServiceException in case of any errors
	 */
	Store findStoreWithCode(final String storeCode) throws EpServiceException;

	/**
	 * Gets a catalog code with the given <code>storeCode</code>. Returns null if no matching records exist.
	 * @param storeCode the store code to look up
	 * @return catalog code if found
	 * @throws EpServiceException if a store with the given code cannot be found
	 */
	String getCatalogCodeForStore(String storeCode)  throws EpServiceException;

	/**
	 * Gets a list of the UIDs of all Stores not under construction.
	 * @return the list of Store UIDs
	 * @throws EpServiceException in case of any errors
	 */
	List<Long> findAllCompleteStoreUids() throws EpServiceException;

	/**
	 * Gets a list of all stores not under constructions, where the fields of each store
	 * are populated according to default population rules.
	 * @return the list of stores
	 * @throws EpServiceException in case of any errors
	 */
	List<Store> findAllCompleteStores() throws EpServiceException;

	/**
	 * Gets a list of all stores not under construction, where the fields of each store
	 * are populated according to the given load tuner.
	 * @param loadTuner the load tuner
	 * @return the list of stores requested
	 * @throws EpServiceException in case of any errors
	 */
	List<Store> findAllCompleteStores(FetchGroupLoadTuner loadTuner) throws EpServiceException;

	/**
	 * Gets a list of all the store UIDs.
	 *
	 * @return a list of all store UIDs
	 * @throws EpServiceException in case of any error
	 */
	List<Long> findAllStoreUids() throws EpServiceException;

	/**
	 * Gets a list of all the stores.
	 *
	 * @return a list of all stores
	 * @throws EpServiceException in case of any error
	 */
	List<Store> findAllStores() throws EpServiceException;

	/**
	 * Gets a list of all stores. If a load tuner is given, tunes the results with the given load tuner.
	 *
	 * @param loadTuner the load tuner to use to tune results
	 * @return a list of all stores
	 * @throws EpServiceException in case of any error
	 */
	List<Store> findAllStores(FetchGroupLoadTuner loadTuner) throws EpServiceException;

	/**
	 * Gets a list of all COMPLETE stores for the user.
	 *
	 * @param user who requested stores.
	 * @return a list of all stores
	 * @throws EpServiceException in case of any error
	 * @deprecated use {@link #findAllCompleteStores()} or {@link #findAllStores()},
	 * and filter for authorization.
	 */
	@Deprecated
	List<Store> findAllStores(final CmUser user) throws EpServiceException;

	/**
	 * Check if store in use.
	 *
	 * @param storeUidPk the store uidPk
	 * @return true if store is in use false otherwise
	 * @throws EpServiceException - in case of any errors
	 */
	boolean storeInUse(final long storeUidPk) throws EpServiceException;

	/**
	 * Finds all the {@link Store}s that are associated with the given catalog UIDs.
	 *
	 * @param catalogUids a collection of catalog UIDs
	 * @return all the {@link Store}s that are associated with the given catalog UIDs
	 */
	Collection<Store> findStoresWithCatalogUids(final Collection<Long> catalogUids);

	/**
	 * Finds all the {@link Store}s that are associated with the given catalog code (guid).
	 *
	 * @param catalogCode code (guid) of catalog
	 * @return all the {@link Store}s that are associated with the given catalog code.
	 */
	Collection<Store> findStoresWithCatalogCode(String catalogCode);


	/**
	 * Gets a <code>Store</code> with the given UID. Returns null if no matching records exist.
	 *
	 * @param storeUid the <code>Store</code> UID
	 * @param loadTuner the load tuner user to fine tune what is loaded.
	 * @return a store with the given UID
	 * @throws EpServiceException in case of any errors
	 */
	Store getTunedStore(final long storeUid, final FetchGroupLoadTuner loadTuner) throws EpServiceException;

	/**
	 * Gets a <code>Store</code> with the given code. Returns null if no matching records exist.
	 *
	 * @param storeCode the <code>Store</code> code
	 * @param loadTuner the load tuner user to fine tune what is loaded.
	 * @return a store with the given UID
	 * @throws EpServiceException in case of any errors
	 */
	Store getTunedStore(final String storeCode, final FetchGroupLoadTuner loadTuner) throws EpServiceException;

	/**
	 * Get all the {@link Store}s in the list of store uids.
	 *
	 * @param storeUids a collection of store UIDs
	 * @param loadTuner the load tuner user to fine tune what is loaded.
	 * @return all the {@link Store}s that are in the list
	 * @throws EpServiceException in case of any errors
	 */
	Collection<Store> getTunedStores(final Collection<Long> storeUids, final FetchGroupLoadTuner loadTuner) throws EpServiceException;


	/**
	 * Gets all supported credit card types for all stores in the complete state.
	 * @return the names of the credit card types (e.g Visa, MasterCard etc.)
	 */
	Collection<String> findAllSupportedCreditCardTypes();
}
