package com.elasticpath.service.store.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.elasticpath.domain.store.Store;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.store.StoreAssociationService;
import com.elasticpath.service.store.StoreService;

/**
 * Implementation of {@link StoreAssociationService}.
 * {@inheritDoc}
 *
 */
public class StoreAssociationServiceImpl implements StoreAssociationService {

	private StoreService storeService;
	
	@Override 
	public Set<String> getAllAssociatedStoreCodes(final String startingStoreCode) throws EpServiceException {
		if (startingStoreCode == null) {
			throw new EpServiceException("Initial store code cannot be null.");
		}

		Set<String> initialStoreCodes = new HashSet<String>();
		initialStoreCodes.add(startingStoreCode);
		return getAssociationGraphStoreCodes(initialStoreCodes);
	}
	
	@Override
	public Set<String> getAllAssociatedStoreCodes(final Set<String> initialStoreCodes) throws EpServiceException {
		if (initialStoreCodes == null) {
			throw new EpServiceException("Initial store code set cannot be null.");
		}
		
		if (initialStoreCodes.isEmpty()) {
			return Collections.emptySet();
		}
		
		return getAssociationGraphStoreCodes(initialStoreCodes);
	}

	@Override
	public Set<String> getDirectlyAssociatedStoreCodes(final String storeCode) throws EpServiceException {
		Store currentStore = getStoreWithCode(storeCode);
		
		Collection<Long> associatedStoreUids = getAssociatedStoreUids(currentStore);
	
		Set<String> associatedStoreCodes = convertStoreUidsToCodes(associatedStoreUids);
		
		return associatedStoreCodes;
	}
	
	/**
	 * Returns the codes from all {@link Store}s linked by Store Associations to the submitted set of {@link Store}s.
	 * Note that the initial set of store codes is removed from the 
	 * @param initialStoreCodes the set of all {@Store} codes to search
	 * @return set of all associated Store Codes, minus the initial search set.
	 */
	private Set<String> getAssociationGraphStoreCodes(final Set<String> initialStoreCodes) {
		Queue<String> storeCodeSearchQueue = new LinkedList<String>();
		Set<String> associatedStoreCodes = new HashSet<String>();
		
		storeCodeSearchQueue.addAll(initialStoreCodes);
		
		while (!storeCodeSearchQueue.isEmpty()) {
			String currentStoreCode = storeCodeSearchQueue.remove();
			associatedStoreCodes.add(currentStoreCode);
			
			Set<String> directlyAssociatedStoreCodes = getDirectlyAssociatedStoreCodes(currentStoreCode);
			
			for (String currentAssociatedStoreCode : directlyAssociatedStoreCodes) {
				if (!associatedStoreCodes.contains(currentAssociatedStoreCode)) {
					storeCodeSearchQueue.add(currentAssociatedStoreCode);
				}
			}
		}
		
		associatedStoreCodes.removeAll(initialStoreCodes);
		
		return associatedStoreCodes;
	}
	
	private Store getStoreWithCode(final String storeCode) {
		Store retrievedStore = storeService.findStoreWithCode(storeCode);

		if (retrievedStore == null) {
			throw new EpServiceException("No store exists with code: " + storeCode);
		}

		return retrievedStore;
	}

	private Collection<Long> getAssociatedStoreUids(final Store store) {

		Collection <Long> associatedStoreUids = store.getAssociatedStoreUids();
		
		if (associatedStoreUids == null) {
			return Collections.emptySet();
		}
		
		return associatedStoreUids;
	}
	
	private Set<String> convertStoreUidsToCodes(final Collection<Long> storeUids) {
		
		if (storeUids.isEmpty()) {
			return Collections.emptySet();
		}
		
		Set <String> storeCodesFromUids = new HashSet<String>();
		
		for (long storeUid : storeUids) {
			String storeCodeFromUid = convertStoreUidToCode(storeUid);
			storeCodesFromUids.add(storeCodeFromUid);
		}
		
		return storeCodesFromUids;
	}
	
	private String convertStoreUidToCode(final long storeUid) {
		Store retrievedStore = getStoreWithUid(storeUid);
		return retrievedStore.getCode();
	}

	private Store getStoreWithUid(final Long storeUid) {
		Store retrievedStore = storeService.getStore(storeUid);

		if (retrievedStore == null) {
			throw new EpServiceException("No store exists with uid: " + Long.toString(storeUid));
		}

		return retrievedStore;
	}

	/**
	 * @return the storeService
	 */
	public StoreService getStoreService() {
		return storeService;
	}

	/**
	 * @param storeService the storeService to set
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}
	
}
