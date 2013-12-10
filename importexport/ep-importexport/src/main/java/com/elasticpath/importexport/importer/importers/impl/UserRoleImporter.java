package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.cmuser.UserRoleDTO;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.service.cmuser.UserRoleService;
/**
 * Importer for {@link com.elasticpath.domain.cmuser.UserRole} objects.
 */
public class UserRoleImporter extends AbstractImporterImpl<UserRole, UserRoleDTO> {

	private DomainAdapter<UserRole, UserRoleDTO> userRoleAdapter;

	private UserRoleService userRoleService;
	
	@Override
	protected CollectionsStrategy<UserRole, UserRoleDTO> getCollectionsStrategy() {
		return new UserRoleCollectionsStrategy(getContext().getImportConfiguration().getImporterConfiguration(JobType.USER_ROLE));
	}

	@Override
	public String getImportedObjectName() {
		return UserRoleDTO.ROOT_ELEMENT;
	}
	
	@Override
	protected String getDtoGuid(final UserRoleDTO dto) {
		return dto.getGuid();
	}
	
	@Override
	protected DomainAdapter<UserRole, UserRoleDTO> getDomainAdapter() {
		return userRoleAdapter;
	}

	@Override
	protected UserRole findPersistentObject(final UserRoleDTO dto) {
		return userRoleService.findByGuid(dto.getGuid());
	}

	@Override
	protected void setImportStatus(final UserRoleDTO object) {
		getStatusHolder().setImportStatus("(" + object.getGuid() + ")");
	}

	/**
	 * @param userRoleAdapter the userRoleAdapter to set
	 */
	public void setUserRoleAdapter(final DomainAdapter<UserRole, UserRoleDTO> userRoleAdapter) {
		this.userRoleAdapter = userRoleAdapter;
	}

	/**
	 * @param userRoleService the userRoleService to set
	 */
	public void setUserRoleService(final UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}

	@Override
	public Class<? extends UserRoleDTO> getDtoClass() {
		return UserRoleDTO.class;
	}
	
}
