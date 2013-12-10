package com.elasticpath.service.store;

import java.util.Set;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Service providing information about associations between {@link Store}s. 
 */
public interface StoreAssociationService {

	/**
	 * Returns codes of all {@link Store}s connected by associations to the specified {@link Store}.
	 * Note that the initial store code is always excluded from the set of associated store codes.
	 * @param storeCode the desired {@Store}'s code.
	 * @return codes of all {@Store} associated with the desired {@Store}. 
	 * @throws EpServiceException on error.
	 */
	Set <String> getAllAssociatedStoreCodes(String storeCode) throws EpServiceException;
	
	/**
	 * Returns codes of all {@link Store}s connected by associations for the specified set of {@link Store}s.
	 * Note that the initial storeCodes are always excluded from the set of associated store codes.
	 * @param storeCodes the set of {@Store} codes to search.
	 * @return codes of all {@Store} associated with the desired {@Store}. 
	 * @throws EpServiceException on error.
	 */
	Set <String> getAllAssociatedStoreCodes(Set <String> storeCodes) throws EpServiceException;

	/**
	 * Returns codes of all {@link Store}s directly associated to the specified{@link Store}.
	 * Note that the initial storeCode is excluded from the set of associated store codes.
	 * @param storeCode the {@Store} code to search.
	 * @return codes of all {@Store} associated with the desired {@Store}. 
	 * @throws EpServiceException on error.
	 */
	Set<String> getDirectlyAssociatedStoreCodes(final String storeCode);
	
}
