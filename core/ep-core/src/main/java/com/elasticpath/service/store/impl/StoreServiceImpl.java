/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.store.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.domain.store.CreditCardType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.persistence.api.FlushMode;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.ShippingServiceLevelSearchCriteria;
import com.elasticpath.service.store.StoreService;

/**
 * Default implementation of <code>StoreService</code>.
*/
@SuppressWarnings("PMD.TooManyMethods")
public class StoreServiceImpl extends AbstractEpPersistenceServiceImpl implements StoreService {

	private static final String PLACEHOLDER_FOR_LIST = "list";

	private FetchPlanHelper fetchPlanHelper;

	private IndexNotificationService indexNotificationService;

	/**
	 * Saves or updates a given <code>Store</code>.
	 * <p>
	 * This implementation calls {@link #buildUpdateSearchCriteriaList(Store)} before persisting the Store, and if the method return not empty list
	 * of search criteria, then it calls {@link #notifyObjectsByUpdateCriteria(List<SearchCriteria>)} after persisting the store in order to send
	 * update notifications based on search criteria from given list.
	 *
	 * @param store the <code>Store</code> to save or update
	 * @return the merged store if it is merged, or the persisted object for save action
	 * @throws EpServiceException - in case of any errors
	 */
	public Store saveOrUpdate(final Store store) throws EpServiceException {
		sanityCheck();

		final List<SearchCriteria> updateSearchCriteriaList = buildUpdateSearchCriteriaList(store);

		final Store result = getPersistenceEngine().saveOrUpdate(store);

		notifyObjectsByUpdateCriteria(updateSearchCriteriaList);
		return result;
	}

	/**
	 * Sends the notifications to update appropriate objects based on given list of search criteria.
	 *
	 * @param updateSearchCriteriaList list of update search criteria
	 */
	protected void notifyObjectsByUpdateCriteria(final List<SearchCriteria> updateSearchCriteriaList) {
		for (SearchCriteria searchCriteria : updateSearchCriteriaList) {
			if (searchCriteria != null) {
				indexNotificationService.addViaQuery(UpdateType.UPDATE, searchCriteria, false);
			}
		}
	}

	/**
	 * Builds update search criteria list that will be used for sending notification for updating appropriate object.
	 *
	 * @param store the store
	 * @return the list of update search criteria
	 */
	protected List<SearchCriteria> buildUpdateSearchCriteriaList(final Store store) {
		final List<SearchCriteria> updateSearchCriteriaList = new ArrayList<SearchCriteria>();
		updateSearchCriteriaList.add(buildProductUpdateCriteria(store));
		updateSearchCriteriaList.add(buildShippingServiceLevelUpdateCriteria(store));
		return updateSearchCriteriaList;
	}

	/**
	 * Builds the shipping service level update criteria if it is necessary to update shipping service levels which belong to given store.
	 *
	 * @param storeBeforePersistence the given store
	 * @return update criteria that determined the shipping service levels for which update is required or null if there are no shipping service
	 *         levels which should be updated
	 */
	protected SearchCriteria buildShippingServiceLevelUpdateCriteria(final Store storeBeforePersistence) {
		boolean updateShippingServiceLevelsRequired = storeBeforePersistence.isPersisted() && !isIncompleteStore(storeBeforePersistence);
		if (updateShippingServiceLevelsRequired) {
			final String persistedStoreName = getPersistedStoreName(storeBeforePersistence.getUidPk());
			if (storeBeforePersistence.getName().equals(persistedStoreName)) {
				return null;
			}

			ShippingServiceLevelSearchCriteria shippingSearchCriteria = getBean(ContextIdNames.SHIPPING_SERVICE_LEVEL_SEACRH_CRITERIA);
			shippingSearchCriteria.setStore(persistedStoreName);
			return shippingSearchCriteria;
		}
		return null;
	}

	/**
	 * Builds the product update criteria if it is necessary to update products which belong to given store.
	 *
	 * @param storeBeforePersistence the given store
	 * @return update criteria that determined the products for which update is required or null if there are no products which should be updated
	 */
	protected SearchCriteria buildProductUpdateCriteria(final Store storeBeforePersistence) {
		ProductSearchCriteria searchCriteria = null;
		if (updateProductsNotificationRequired(storeBeforePersistence)) {
			searchCriteria = getBean(ContextIdNames.PRODUCT_SEARCH_CRITERIA);
			// we just need a locale here, it isn't going to be used
			searchCriteria.setLocale(Locale.US);
			searchCriteria.setCatalogCode(storeBeforePersistence.getCatalog().getCode());
		}
		return searchCriteria;
	}

	/**
	 * Hook method called before a store is created or updated in the persistence layer. This implementation returns true if the given Store is not
	 * currently persisted (is a new Store) and it is complete store, or if the update of complete store occurs.
	 *
	 * @param storeBeforePersistence the {@code Store} object that is to be persisted
	 * @return true if a notification method should be called after saving or updating
	 */
	protected boolean updateProductsNotificationRequired(final Store storeBeforePersistence) {
		// New store that's configured to display out of stock products
		if (isIncompleteStore(storeBeforePersistence)) { //incomplete store
			return false;
		}

		return !storeBeforePersistence.isPersisted() // new complete store
				|| isStoreDisplayabilityDifferentThanPersistedStoreDisplayability(storeBeforePersistence) // Displayability changed in store
				|| isGivenStoreBecomingActive(storeBeforePersistence);
	}

	/**
	 * @param store a Store
	 * @return true if the given store is NOT under construction while the persisted version of the given store IS under construction.
	 */
	boolean isGivenStoreBecomingActive(final Store store) {
		return !isIncompleteStore(store) && isPersistedStoreIncomplete(store.getUidPk());
	}

	/**
	 * @param store a Store
	 * @return true if the store is in an under construction (incomplete) state.
	 */
	boolean isIncompleteStore(final Store store) {
		return store.getStoreState().isIncomplete();
	}

	/**
	 * @param store a Store
	 * @return true if the given store's "displayOutOfStock" field is different than the persisted version
	 * of the given store's "displayOutOfStock" field.
	 */
	boolean isStoreDisplayabilityDifferentThanPersistedStoreDisplayability(final Store store) {
		return store.isDisplayOutOfStock() ^ isPersistedStoreConfiguredToDisplayOutOfStockProducts(store.getUidPk());
	}

	/**
	 * Convenience method.
	 * @param storeUidPk a Store UIDPK
	 * @return true if the persisted store with the given UIDPK is under construction
	 */
	boolean isPersistedStoreIncomplete(final long storeUidPk) {
		return isIncompleteStore(getStore(storeUidPk));
	}

	/**
	 * Convenience method.
	 * @param storeUidPk a Store UIDPK
	 * @return true if the store with the given UIDPK is configured to display out of stock items
	 */
	boolean isPersistedStoreConfiguredToDisplayOutOfStockProducts(final long storeUidPk) {
		return getPersistenceEngine().<Boolean>retrieveByNamedQuery("STORE_DISPLAYABLE_FLAG", storeUidPk).get(0);
	}

	/**
	 * Convenience method the returns the persisted store name by given uid.
	 * @param storeUidPk a Store UIDPK
	 * @return the persisted store name
	 */
	String getPersistedStoreName(final long storeUidPk) {
		return getPersistenceEngine().<String>retrieveByNamedQuery("STORE_NAME", storeUidPk).get(0);
	}

	/**
	 * Deletes a store and all associated warehouses.
	 *
	 * @param store the warehouse to remove
	 * @throws EpServiceException in case of any errors
	 */
	public void remove(final Store store) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(store);
	}

	/**
	 * Gets a <code>Store</code> with the given UID. Returns null if no matching records exist.
	 *
	 * @param storeUid the <code>Store</code> UID
	 * @return a store with the given UID
	 * @throws EpServiceException in case of any errors
	 */
	public Store getStore(final long storeUid) throws EpServiceException {
		return getTunedStore(storeUid, null);
	}

	/**
	 * Gets a <code>Store</code> with the given UID. Returns null if no matching records exist.
	 *
	 * @param storeUid the <code>Store</code> UID
	 * @param loadTuner the load tuner user to fine tune what is loaded.
	 * @return a store with the given UID
	 * @throws EpServiceException in case of any errors
	 */
	public Store getTunedStore(final long storeUid,  final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		sanityCheck();
		Store store = null;
		if (storeUid <= 0) {
			store = getBean(ContextIdNames.STORE);
		} else {

			if (loadTuner == null) {
				store = getPersistentStore(storeUid);

			} else {
				fetchPlanHelper.clearFetchPlan();
				fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner, false);
				store = getPersistentStore(storeUid);
				fetchPlanHelper.clearFetchPlan();
			}
		}
		return store;
	}

	/**
	 * Get all the {@link Store}s in the list of store uids.
	 *
	 * @param storeUids a collection of store UIDs
	 * @param loadTuner the load tuner user to fine tune what is loaded.
	 * @return all the {@link Store}s that are in the list
	 * @throws EpServiceException in case of any errors
	 */
	public Collection<Store> getTunedStores(final Collection<Long> storeUids, final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		sanityCheck();
		fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);
		List<Store> result = getPersistenceEngine().retrieveByNamedQueryWithList("STORE_WITH_UIDS", PLACEHOLDER_FOR_LIST, storeUids);
		fetchPlanHelper.clearFetchPlan();
		return result;
	}

	/**
	 * Retrieves the persistent store bean with the given UIDPK.
	 * @param uidPk the store's uidpk
	 * @return the store
	 */
	Store getPersistentStore(final long uidPk) {
		return getPersistentBeanFinder().get(ContextIdNames.STORE, uidPk);
	}

	/**
	 * Gets a <code>Store</code> with the given <code>storeCode</code>. Returns null if no matching records exist.
	 *
	 * @param storeCode the store code to look up
	 * @return a store with the given store code
	 * @throws EpServiceException in case of any errors
	 */
	public Store findStoreWithCode(final String storeCode) throws EpServiceException {
		sanityCheck();
		List<Store> storeList = getPersistenceEngine().retrieveByNamedQuery(
				"FIND_STORE_WITH_CODE", FlushMode.COMMIT, storeCode);
		if (!storeList.isEmpty()) {
		  return storeList.get(0);
		}
		return null;
	}

	/**
	 * Gets a catalog code with the given <code>storeCode</code>. Returns null if no matching records exist.
	 * @param storeCode the store code to look up
	 * @return catalog code if found
	 * @throws EpServiceException if a store with the given code cannot be found
	 */
	public String getCatalogCodeForStore(final String storeCode)  throws EpServiceException {
		List<String> catalogCodes = getPersistenceEngine().retrieveByNamedQuery("FIND_CATALOG_CODE_WITH_STORE_CODE", storeCode);
		return catalogCodes.get(0);
	}

	/**
	 * Determine whether a store has a unique URL within the confines of the given state.
	 *
	 * @param store the store to check
	 * @param state the state to
	 * @return true if there are no other stores in the given state with the same URL
	 * @throws EpServiceException in case of any errors
	 */
	public boolean isStoreUrlUniqueForState(final Store store, final StoreState state) throws EpServiceException {
		if (store == null || StringUtils.isEmpty(store.getUrl())) {
			return true;
		}
		List<Store> retrievedStores = findStoresWithState(state);
		String newUrl = StringUtils.removeEnd(store.getUrl(), "/");
		for (Store retrievedStore : retrievedStores) {
			if (retrievedStore.equals(store)) {
				continue;
			}
			if (newUrl.equalsIgnoreCase(StringUtils.removeEnd(retrievedStore.getUrl(), "/"))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Finds all persistent stores with the given state.
	 * @param state the state
	 * @return the stores found
	 */
	List<Store> findStoresWithState(final StoreState state) {
		return getPersistenceEngine().retrieveByNamedQuery("FIND_STORES_WITH_STATE", state);
	}

	/**
	 * Gets a list of the UIDs of all Stores not under construction.
	 * @return the list of Store UIDs
	 * @throws EpServiceException in case of any errors
	 */
	public List<Long> findAllCompleteStoreUids() throws EpServiceException {
		return getPersistenceEngine().retrieveByNamedQuery("FIND_ALL_COMPLETE_STORE_UIDS", StoreState.UNDER_CONSTRUCTION, StoreState.RESTRICTED);
	}

	/**
	 * Gets a list of all stores not under constructions, where the fields of each store
	 * are populated according to default population rules.
	 * This implementation calls {@link #findAllCompleteStores(FetchGroupLoadTuner)} with
	 * a null FetchGroupLoadTuner.
	 * @return the list of stores
	 * @throws EpServiceException in case of any errors
	 */
	public List<Store> findAllCompleteStores() throws EpServiceException {
		return findAllCompleteStores(null);
	}

	/**
	 * Gets a list of all stores not under construction, where the fields of each store
	 * are populated according to the given load tuner.
	 * @param loadTuner the load tuner
	 * @return the list of stores requested
	 * @throws EpServiceException in case of any errors
	 */
	public List<Store> findAllCompleteStores(final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		getFetchPlanHelper().configureFetchGroupLoadTuner(loadTuner);
		List<Store> result = getPersistenceEngine().retrieveByNamedQuery("FIND_ALL_COMPLETE_STORES",
				StoreState.UNDER_CONSTRUCTION,
				StoreState.RESTRICTED);
		getFetchPlanHelper().clearFetchPlan();
		return result;
	}

	/**
	 * Gets a list of all the store UIDs.
	 *
	 * @return a list of all store UIDs
	 * @throws EpServiceException in case of any error
	 */
	public List<Long> findAllStoreUids() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FIND_ALL_STORE_UIDS");
	}

	/**
	 * Gets a list of all the stores.
	 *
	 * @return a list of all stores
	 * @throws EpServiceException in case of any error
	 */
	public List<Store> findAllStores() throws EpServiceException {
		return findAllStores((FetchGroupLoadTuner) null);
	}

	/**
	 * Gets a list of all stores. If a load tuner is given, tunes the results with the given load tuner.
	 *
	 * @param loadTuner the load tuner to use to tune results
	 * @return a list of all stores
	 * @throws EpServiceException in case of any error
	 */
	public List<Store> findAllStores(final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		sanityCheck();
		fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);
		List<Store> result = getPersistenceEngine().retrieveByNamedQuery("FIND_ALL_STORES");
		fetchPlanHelper.clearFetchPlan();
		return result;
	}

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
	public List<Store> findAllStores(final CmUser user) throws EpServiceException {
		sanityCheck();
		if (user == null) {
			return new ArrayList<Store>();
		}

		if (user.isSuperUser() || user.isAllStoresAccess()) {
			return findAllCompleteStores();
		}

		return new ArrayList<Store>(user.getStores());
	}

	/**
	 * Generic get method for a store.
	 *
	 * @param uid the persisted store UID
	 * @return the persisted instance of a <code>Store</code> if it exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return getStore(uid);
	}

	/**
	 * Check if store in use.
	 *
	 * @param storeUidPk the store uidPk
	 * @return true if store is in use false otherwise
	 * @throws EpServiceException - in case of any errors
	 */
	public boolean storeInUse(final long storeUidPk) throws EpServiceException {
		sanityCheck();

		return !getPersistenceEngine().retrieveByNamedQuery("STORE_WITH_ORDER_IN_USE", storeUidPk).isEmpty()
				|| !getPersistenceEngine().retrieveByNamedQuery("STORE_WITH_USER_IN_USE", storeUidPk).isEmpty()
				|| !getPersistenceEngine().retrieveByNamedQuery("STORE_WITH_SHIPPING_SERVICE_LEVEL_IN_USE", storeUidPk).isEmpty()
				|| !getPersistenceEngine().retrieveByNamedQuery("STORE_WITH_PROMOTION_IN_USE", storeUidPk).isEmpty()
				|| !getPersistenceEngine().retrieveByNamedQuery("STORE_WITH_IMPORTJOB_IN_USE", storeUidPk).isEmpty()
				|| !getPersistenceEngine().retrieveByNamedQuery("STORE_WITH_STORE_ASSOCIATION_IN_USE", storeUidPk).isEmpty();

	}

	/**
	 * Finds all the {@link Store}s that are associated with the given catalog UIDs.
	 *
	 * @param catalogUids a collection of catalog UIDs
	 * @return all the {@link Store}s that are associated with the given catalog UIDs
	 */
	public Collection<Store> findStoresWithCatalogUids(final Collection<Long> catalogUids) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQueryWithList("STORE_WITH_CATALOG_UID", PLACEHOLDER_FOR_LIST, catalogUids);
	}

	/**
	 * Finds all the {@link Store}s that are associated with the given catalog code (guid).
	 *
	 * @param catalogCode code (guid) of catalog
	 * @return all the {@link Store}s that are associated with the given catalog code.
	 */
	public Collection<Store> findStoresWithCatalogCode(final String catalogCode) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("STORE_WITH_CATALOG_GUID", catalogCode);
	}


	/**
	 * Sets the {@link FetchPlanHelper} instance to use.
	 *
	 * @param fetchPlanHelper the {@link FetchPlanHelper} instance to use
	 */
	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}

	/**
	 * Sets the {@link IndexNotificationService} instance to use.
	 *
	 * @param indexNotificationService the {@link IndexNotificationService} instance to use
	 */
	public void setIndexNotificationService(final IndexNotificationService indexNotificationService) {
		this.indexNotificationService = indexNotificationService;
	}

	/**
	 * @return the fetch plan helper
	 */
	FetchPlanHelper getFetchPlanHelper() {
		return fetchPlanHelper;
	}

	@Override
	public Set<String> findAllSupportedCreditCardTypes() {

		Set<String> creditCardTypes = new HashSet<String>();
		List <Store> stores = findAllCompleteStores();

		for (Store store : stores) {
			for (CreditCardType type : store.getCreditCardTypes()) {
				creditCardTypes.add(type.getCreditCardType());
			}
		}
		return creditCardTypes;
	}

	@Override
	public Store getTunedStore(final String storeCode, final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		sanityCheck();
		fetchPlanHelper.clearFetchPlan();
		fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner, false);
		Store store = findStoreWithCode(storeCode);
		fetchPlanHelper.clearFetchPlan();
		return store;
	}
}
