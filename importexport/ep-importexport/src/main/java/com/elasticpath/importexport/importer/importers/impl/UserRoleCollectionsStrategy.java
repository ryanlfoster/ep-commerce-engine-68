package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.cmuser.UserRoleDTO;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
/**
 * CollectionsStrategy for {@link UserRole}.
 *
 */
public class UserRoleCollectionsStrategy implements CollectionsStrategy<UserRole, UserRoleDTO> {
	
	private final boolean clearPermissions;
	/**
	 * Constructor for {@link UserRole} {@link CollectionsStrategy}.
	 * @param importerConfiguration {@link ImporterConfiguration} containing collection strategy settings. 
	 */
	public UserRoleCollectionsStrategy(final ImporterConfiguration importerConfiguration) {

		clearPermissions = importerConfiguration.getCollectionStrategyType(DependentElementType.USER_PERMISSIONS).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
	}
	
	@Override
	public void prepareCollections(final UserRole domainObject, final UserRoleDTO dto) {
		
		if (clearPermissions) {
			domainObject.getUserPermissions().clear();
			return;
		}
	}
	
	public boolean isForPersistentObjectsOnly() {
		return true;
	}
	
}
