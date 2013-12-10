package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.test.db.DbTestCase;

/**
 * Integration tests for the StoreService.
 */
public class StoreServiceTest extends DbTestCase {

	/** The main object under test. */
	@Autowired
	private StoreService storeService;

	@Autowired
	private CmUserService cmUserService;
	
	/**
	 * Test the FindAllCompleteStores method and query.
	 */
	@DirtiesDatabase
	@Test
	public void testFindAllCompleteStores() {
		Store completeStore = scenario.getStore();
		completeStore.setStoreState(StoreState.OPEN);
		completeStore = storeService.saveOrUpdate(completeStore);
		
		Store incompleteStore = persisterFactory.getStoreTestPersister().persistStore(
				scenario.getCatalog(), scenario.getWarehouse(), "incomplete", "USD");
		incompleteStore.setStoreState(StoreState.UNDER_CONSTRUCTION);
		incompleteStore = storeService.saveOrUpdate(incompleteStore);
		
		List<Store> persistedStores = storeService.findAllCompleteStores();
		assertEquals(1, persistedStores.size());
		assertEquals(completeStore, persistedStores.get(0));
	}
	
	/**
	 * Test the FindAllCompleteStoreUids method and query.
	 */
	@DirtiesDatabase
	@Test
	public void testFindAllCompleteStoreUids() {
		Store completeStore = scenario.getStore();
		completeStore.setStoreState(StoreState.OPEN);
		completeStore = storeService.saveOrUpdate(completeStore);
		
		Store incompleteStore = persisterFactory.getStoreTestPersister().persistStore(
				scenario.getCatalog(), scenario.getWarehouse(), "incomplete", "USD");
		incompleteStore.setStoreState(StoreState.UNDER_CONSTRUCTION);
		incompleteStore = storeService.saveOrUpdate(incompleteStore);
		
		assertEquals(2, storeService.findAllStoreUids().size());
		
		List<Long> persistedStoreUids = storeService.findAllCompleteStoreUids();
		assertEquals(1, persistedStoreUids.size());
		assertEquals(completeStore.getUidPk(), persistedStoreUids.get(0).longValue());
	}
	
	/**
	 * Test that a store is in use if it's assigned to a CMUSER.
	 */
	@DirtiesDatabase
	@Test
	public void testStoreInUseWhenAssignedToCmUser() {
		CmUser cmUser = persisterFactory.getStoreTestPersister().persistDefaultCmUser();
		cmUser.addStore(scenario.getStore());
		cmUserService.update(cmUser);
		storeService.storeInUse(scenario.getStore().getUidPk());
	}
	
	/**
	 * Assert that a Store's subCountry can be null.
	 * This is important for import/export where Clients can have stores in countries which have no subCountries.
	 */
	@DirtiesDatabase
	@Test
	public void testSubCountryCanBeNull() {
		Store completeStore = scenario.getStore();
		completeStore.setSubCountry(null);
		storeService.saveOrUpdate(completeStore);
	}
	
}
