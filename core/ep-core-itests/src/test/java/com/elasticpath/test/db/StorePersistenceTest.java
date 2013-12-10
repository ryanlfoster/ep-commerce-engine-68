package com.elasticpath.test.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests store persisting.
 */
public class StorePersistenceTest extends DbTestCase {

	@Autowired
	private StoreService storeService;
	
	/**
	 * Tests order saving.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveStore() {
		final Store store = createStore();

		final Store savedStore = getTxTemplate().execute(new TransactionCallback<Store>() {
			public Store doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(store);
				return store;
			}
		});

		assertSame("Saved store not the same object.", store, savedStore);
		assertSame("Saved catalog not the same object.", store.getCatalog(), savedStore.getCatalog());
	}
	
	/**
	 * Tests order saving.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveStoreWithCatalogModification() {
		final Store store = createStore();
		store.getCatalog().setMaster(true);

		final Store savedStore = getTxTemplate().execute(new TransactionCallback<Store>() {
			public Store doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(store);
				return store;
			}
		});

		assertSame("Saved store not the same object.", store, savedStore);
		assertSame("Saved catalog not the same object.", store.getCatalog(), savedStore.getCatalog());
	}

	/**
	 * Test that there is no cascading delete on associated stores.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveStoreWithStoreAssociations() {
		final Store store = createStore();
		final Store store2 = createStore();
		store.getCatalog().setMaster(true);
		storeService.saveOrUpdate(store);
		storeService.saveOrUpdate(store2);

		Set<Long> storeAssociations = new HashSet<Long>();
		storeAssociations.add(storeService.getStore(store2.getUidPk()).getUidPk());
		Store retrievedStore = storeService.getStore(store.getUidPk());
		retrievedStore.getAssociatedStoreUids().addAll(storeAssociations);
		storeService.saveOrUpdate(retrievedStore);

		FetchGroupLoadTuner loadTuner = getBeanFactory().getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		loadTuner.addFetchGroup(FetchGroupConstants.STORE_SHARING);
		
		Store updatedStore = storeService.getTunedStore(store.getUidPk(), loadTuner);
		assertNotNull("Store had no associations.", updatedStore.getAssociatedStoreUids());
		storeService.remove(updatedStore);
		assertNotNull("Associated store was removed.", storeService.getStore(store2.getUidPk()));
	}

	/**
	 * Tests updating store operation.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateStoreWithStoreAssociations() {
		final Store store = createStore();
		final Store store2 = createStore();
		store.getCatalog().setMaster(true);

		final Store savedStore = getTxTemplate().execute(new TransactionCallback<Store>() {
			public Store doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(store);
				return store;
			}
		});
		final Store savedStore2 = getTxTemplate().execute(new TransactionCallback<Store>() {
			public Store doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(store2);
				return store;
			}
		});

		final Store retrievedStore = storeService.getStore(savedStore.getUidPk());
		final Store retrievedStore2 = storeService.getStore(savedStore2.getUidPk());
		retrievedStore.getAssociatedStoreUids().add(retrievedStore2.getUidPk());

		getTxTemplate().execute(new TransactionCallback<Store>() {
			public Store doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().update(retrievedStore);
				return retrievedStore;
			}
		});

		FetchGroupLoadTuner loadTuner = getBeanFactory().getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		loadTuner.addFetchGroup(FetchGroupConstants.STORE_SHARING);
		
		final Store reRetrievedStore = storeService.getTunedStore(savedStore.getUidPk(), loadTuner);
		assertNotNull("Retrieved store doesn't contain associations.", reRetrievedStore.getAssociatedStoreUids());
	}

	/**
	 * Tests retrieving store operation.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveStore() {
		final Store store = createStore();

		getTxTemplate().execute(new TransactionCallback<Store>() {
			public Store doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(store);
				return store;
			}
		});

		assertNotNull("Store not retrieved - null", getPersistenceEngine().get(StoreImpl.class, store.getUidPk()));
		assertNotNull("Catalog not retrieved - null", getPersistenceEngine().get(CatalogImpl.class, store.getCatalog().getUidPk()));

	}

	/**
	 * Tests retrieving store operation.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveStoreWithCatalogModification() {
		final Store store = createStore();
		getTxTemplate().execute(new TransactionCallback<Store>() {
			public Store doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(store);
				return store;
			}
		});

		assertNotNull("Store not retrieved - null", getPersistenceEngine().get(StoreImpl.class, store.getUidPk()));
		assertNotNull("Catalog not retrieved - null", getPersistenceEngine().get(CatalogImpl.class, store.getCatalog().getUidPk()));
	}

	/**
	 * Tests merging store operation.
	 */
	@DirtiesDatabase
	@Test
	public void testMergeStore() {
		final Store store = createStore();

		getTxTemplate().execute(new TransactionCallback<Store>() {
			public Store doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(store);
				return store;
			}
		});

		final Store retrievedStore = getPersistenceEngine().get(StoreImpl.class, store.getUidPk());
//d
		final Store mergedStore = getTxTemplate().execute(new TransactionCallback<Store>() {
			public Store doInTransaction(final TransactionStatus arg0) {
				return getPersistenceEngine().merge(retrievedStore);
			}
		});

		assertNotSame("Merged store is the same object.", retrievedStore, mergedStore);
		assertNotSame("Merged catalog is not the same object.", retrievedStore.getCatalog(), mergedStore.getCatalog());
	}
	
	/**
	 * Tests merging store operation.
	 */
	@DirtiesDatabase
	@Test
	public void testMergeStoreWithCatalogModification() {
		final Store store = createStore();

		getTxTemplate().execute(new TransactionCallback<Store>() {
			public Store doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(store);
				return store;
			}
		});

		final Store retrievedStore = getPersistenceEngine().get(StoreImpl.class, store.getUidPk());
		retrievedStore.getCatalog().setDefaultLocale(Locale.CHINA);

		final Store mergedStore = getTxTemplate().execute(new TransactionCallback<Store>() {
			public Store doInTransaction(final TransactionStatus arg0) {
				return getPersistenceEngine().merge(retrievedStore);
			}
		});

		assertNotSame("Merged store is the same object.", retrievedStore, mergedStore);
		assertNotSame("Merged catalog is not the same object.", retrievedStore.getCatalog(), mergedStore.getCatalog());
	}
	
}
