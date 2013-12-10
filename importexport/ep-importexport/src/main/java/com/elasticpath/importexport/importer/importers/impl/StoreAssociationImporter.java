package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.store.StoreAssociationDTO;
import com.elasticpath.domain.store.Store;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.service.store.StoreService;

/**
 * Importer for {@link com.elasticpath.importexport.exporter.exporters.impl.StoreAssociation}s. 
 */
public class StoreAssociationImporter  extends AbstractImporterImpl<Store, StoreAssociationDTO> {

	private StoreService storeService;  
	
	private DomainAdapter<Store, StoreAssociationDTO> storeAssociationAdapter;

	@Override
	public String getImportedObjectName() {
		return StoreAssociationDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final StoreAssociationDTO dto) {
		return dto.getStoreCode();
	}

	@Override
	protected DomainAdapter<Store, StoreAssociationDTO> getDomainAdapter() {
		return storeAssociationAdapter;
	}

	@Override
	protected Store findPersistentObject(final StoreAssociationDTO dto) {
		return storeService.findStoreWithCode(dto.getStoreCode());
	}

	@Override
	protected void setImportStatus(final StoreAssociationDTO object) {
		getStatusHolder().setImportStatus("(" + object.getStoreCode() + ")");
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

	/**
	 * @return the storeAssociationAdapter
	 */
	public DomainAdapter<Store, StoreAssociationDTO> getStoreAssociationAdapter() {
		return storeAssociationAdapter;
	}

	/**
	 * @param storeAssociationAdapter the storeAssociationAdapter to set
	 */
	public void setStoreAssociationAdapter(final DomainAdapter<Store, StoreAssociationDTO> storeAssociationAdapter) {
		this.storeAssociationAdapter = storeAssociationAdapter;
	}
	
	@Override
	public Class<? extends StoreAssociationDTO> getDtoClass() {
		return StoreAssociationDTO.class;
	}
}