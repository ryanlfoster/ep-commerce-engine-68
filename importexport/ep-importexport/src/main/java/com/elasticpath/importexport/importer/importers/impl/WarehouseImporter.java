package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.warehouse.WarehouseDTO;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.service.store.WarehouseService;

/**
 * An importer for {@link Warehouse}s.
 */
public class WarehouseImporter extends AbstractImporterImpl<Warehouse, WarehouseDTO> {

	private DomainAdapter<Warehouse, WarehouseDTO> warehouseAdapter;

	private WarehouseService warehouseService;

	@Override
	public String getImportedObjectName() {
		return WarehouseDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final WarehouseDTO dto) {
		return dto.getCode();
	}

	@Override
	protected DomainAdapter<Warehouse, WarehouseDTO> getDomainAdapter() {
		return warehouseAdapter;
	}

	@Override
	protected Warehouse findPersistentObject(final WarehouseDTO dto) {
		return warehouseService.findByCode(dto.getCode());
	}

	@Override
	protected void setImportStatus(final WarehouseDTO object) {
		getStatusHolder().setImportStatus("(" + object.getCode() + ")");
	}

	public void setWarehouseAdapter(final DomainAdapter<Warehouse, WarehouseDTO> warehouseAdapter) {
		this.warehouseAdapter = warehouseAdapter;
	}

	public void setWarehouseService(final WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	@Override
	public Class<? extends WarehouseDTO> getDtoClass() {
		return WarehouseDTO.class;
	}

}
