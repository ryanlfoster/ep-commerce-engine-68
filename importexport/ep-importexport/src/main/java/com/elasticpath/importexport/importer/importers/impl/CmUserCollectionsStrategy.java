package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.cmuser.CmUserDTO;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;

/**
 * CollectionsStrategy for {@link CmUser}.
 *
 */
public class CmUserCollectionsStrategy implements CollectionsStrategy<CmUser, CmUserDTO> {

	private final boolean clearUserRoles, clearAccessiblePriceLists, clearAccessibleCatalogs, 
		clearAccessibleStores, clearAccessibleWarehouses, clearUserPasswordHistory;

	/**
	 * Constructor for {@link CmUser} {@link CollectionsStrategy}.
	 * @param importerConfiguration {@link ImporterConfiguration} containing collection strategy settings. 
	 */
	public CmUserCollectionsStrategy(final ImporterConfiguration importerConfiguration) {
		clearUserRoles = importerConfiguration.getCollectionStrategyType(DependentElementType.USER_ROLES).equals(
				CollectionStrategyType.CLEAR_COLLECTION);

		clearAccessiblePriceLists = importerConfiguration.getCollectionStrategyType(DependentElementType.PRICE_LISTS).equals(
				CollectionStrategyType.CLEAR_COLLECTION);

		clearAccessibleCatalogs = importerConfiguration.getCollectionStrategyType(DependentElementType.CATALOGS).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
		
		clearAccessibleStores = importerConfiguration.getCollectionStrategyType(DependentElementType.STORES).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
		
		clearAccessibleWarehouses = importerConfiguration.getCollectionStrategyType(DependentElementType.WAREHOUSES).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
		
		clearUserPasswordHistory = importerConfiguration.getCollectionStrategyType(DependentElementType.USER_PASSWORD_HISTORY).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
	}	

	@Override
	public void prepareCollections(final CmUser domainObject, final CmUserDTO dto) {
		
		if (clearUserRoles) {
			domainObject.getUserRoles().clear();
		}
		
		if (clearAccessiblePriceLists) {
			domainObject.getPriceLists().clear();
		}
		
		if (clearAccessibleCatalogs) {
			domainObject.getCatalogs().clear();
		}

		if (clearAccessibleStores) {
			domainObject.getStores().clear();
		}
		
		if (clearAccessibleWarehouses) {
			domainObject.getWarehouses().clear();
		}
		
		if (clearUserPasswordHistory) {
			domainObject.getPasswordHistoryItems().clear();
		}
		
	}
	
	public boolean isForPersistentObjectsOnly() {
		return true;
	}
	
}