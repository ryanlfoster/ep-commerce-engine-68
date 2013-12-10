package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.store.StoreDTO;
import com.elasticpath.domain.store.Store;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.service.store.StoreService;

/**
 * An importer for {@link Store}s.
 */
public class StoreImporter extends AbstractImporterImpl<Store, StoreDTO> {

	private DomainAdapter<Store, StoreDTO> storeAdapter;

	private StoreService storeService;

	@Override
	protected CollectionsStrategy<Store, StoreDTO> getCollectionsStrategy() {
		return new StoreCollectionsStrategy(getContext().getImportConfiguration().getImporterConfiguration(JobType.STORE));
	}

	@Override
	public String getImportedObjectName() {
		return StoreDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final StoreDTO dto) {
		return dto.getCode();
	}

	@Override
	protected DomainAdapter<Store, StoreDTO> getDomainAdapter() {
		return storeAdapter;
	}

	@Override
	protected Store findPersistentObject(final StoreDTO dto) {
		return storeService.findStoreWithCode(dto.getCode());
	}

	@Override
	protected void setImportStatus(final StoreDTO object) {
		getStatusHolder().setImportStatus("(" + object.getCode() + ")");
	}

	public void setStoreAdapter(final DomainAdapter<Store, StoreDTO> storeAdapter) {
		this.storeAdapter = storeAdapter;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	@Override
	public Class<? extends StoreDTO> getDtoClass() {
		return StoreDTO.class;
	}
}
