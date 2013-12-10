package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.cmuser.CmUserDTO;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * Import/export tool importer for CmUser.  
 *
 */
public class CmUserImporter extends AbstractImporterImpl<CmUser, CmUserDTO> {

	private DomainAdapter<CmUser, CmUserDTO> cmUserAdapter;

	private CmUserService cmUserService;
	
	@Override
	protected CollectionsStrategy<CmUser, CmUserDTO> getCollectionsStrategy() {
		return new CmUserCollectionsStrategy(getContext().getImportConfiguration().getImporterConfiguration(JobType.CMUSER));
	}

	@Override
	public String getImportedObjectName() {
		return CmUserDTO.ROOT_ELEMENT;
	}
	
	@Override
	protected String getDtoGuid(final CmUserDTO dto) {
		return dto.getGuid();
	}
	
	@Override
	protected DomainAdapter<CmUser, CmUserDTO> getDomainAdapter() {
		return cmUserAdapter;
	}
	
	@Override
	protected CmUser findPersistentObject(final CmUserDTO dto) {
		return cmUserService.findByGuid(dto.getGuid());
	}

	@Override
	protected void setImportStatus(final CmUserDTO object) {
		getStatusHolder().setImportStatus("(" + object.getGuid() + ")");
	}

	/**
	 * Set the adapter to be used to turn {@code CmUser} into {@code CmUserDTO}s.
	 * @param adapter to set
	 */
	public void setCmUserAdapter(final DomainAdapter<CmUser, CmUserDTO> adapter) {
		this.cmUserAdapter = adapter;
	}

	/**
	 * Set the {@link CmUserService} to be used to manage {@code CmUser}s.
	 * @param cmUserService to set
	 */	
	public void setCmUserService(final CmUserService cmUserService) {
		this.cmUserService = cmUserService;
	}

	@Override
	public Class<? extends CmUserDTO> getDtoClass() {
		return CmUserDTO.class;
	}
}
