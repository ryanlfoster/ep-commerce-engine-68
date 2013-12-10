package com.elasticpath.importexport.common.adapters.cmimportjob.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.cmimportjob.CmImportJobDTO;
import com.elasticpath.importexport.common.dto.cmimportjob.CmImportMappingDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.store.WarehouseService;

/**
 * Transforms data between DTO and domain objects for CM Client import jobs. 
 */
public class CmImportJobAdapter extends AbstractDomainAdapterImpl<ImportJob, CmImportJobDTO> {

	private CatalogService catalogService;
	private StoreService storeService;
	private WarehouseService warehouseService;
	
	@Override
	public void populateDomain(final CmImportJobDTO source, final ImportJob target) {
		if (StringUtils.isNotBlank(source.getColDelimeter())) {
			target.setCsvFileColDelimeter(source.getColDelimeter().charAt(0));			
		}
		
		target.setCsvFileName(source.getCsvFileName());
		
		if (StringUtils.isNotBlank(source.getTextQualifier())) {
			target.setCsvFileTextQualifier(source.getTextQualifier().charAt(0));
		}
		
		target.setDependentPriceListGuid(source.getDependentObjGuid());
		target.setGuid(source.getGuid());
		target.setImportDataTypeName(source.getDataTypeName());
		target.setImportType(AbstractImportTypeImpl.getInstance(source.getImportTypeId()));
		target.setMaxAllowErrors(source.getMaxAllowErrors());
		target.setName(source.getName());
		
		// catalog
		if (StringUtils.isBlank(source.getCatalogueGuid())) {
			target.setCatalog(null);
		} else {
			Catalog catalog = catalogService.findByCode(source.getCatalogueGuid());
			if (catalog == null) {
				throw new PopulationRollbackException("IE-10103", source.getCatalogueGuid());
			}
			target.setCatalog(catalog);
		}
		
		// store
		if (StringUtils.isBlank(source.getStoreGuid())) {
			target.setStore(null);
		} else {
			Store store = storeService.findStoreWithCode(source.getStoreGuid());
			if (store == null) {
				throw new PopulationRollbackException("IE-31100", source.getStoreGuid());
			}
			target.setStore(store);
		}
		
		// warehouse
		if (StringUtils.isBlank(source.getWarehouseGuid())) {
			target.setWarehouse(null);
		} else {
			Warehouse warehouse = warehouseService.findByCode(source.getWarehouseGuid());
			if (warehouse == null) {
				throw new PopulationRollbackException("IE-10500", source.getWarehouseGuid());
			}
			target.setWarehouse(warehouse);
		}
		
		target.setMappings(getMappings(source.getImportMappingDto()));
	}

	/**
	 * Restores the import job field mappings from the dto.
	 */
	private Map<String, Integer> getMappings(final Set<CmImportMappingDTO> importMappingDtos) {
		Map<String, Integer> mappings = new HashMap<String, Integer>();
		for (CmImportMappingDTO dto : importMappingDtos) {
			mappings.put(dto.getImportFieldName(), dto.getColNumber());
		}
		return mappings;
	}

	@Override
	public void populateDTO(final ImportJob source, final CmImportJobDTO target) {	
		
		target.setColDelimeter(Character.toString(source.getCsvFileColDelimeter()));
		target.setCsvFileName(source.getCsvFileName());
		target.setDataTypeName(source.getImportDataTypeName());
		target.setGuid(source.getGuid());
		target.setImportType(source.getImportType().getTypeId());
		target.setMaxAllowErrors(source.getMaxAllowErrors());
		target.setName(source.getName());
		target.setTextQualifier(Character.toString(source.getCsvFileTextQualifier()));
		
		if (source.getCatalog() != null) {
			target.setCatalogueGuid(source.getCatalog().getGuid());
		}
		
		if (source.getDependentPriceListGuid() != null) {
			target.setDependentObjGuid(source.getDependentPriceListGuid());
		}
		
		if (source.getStore() != null) {
			target.setStoreGuid(source.getStore().getCode());
		}
		
		if (source.getWarehouse() != null) {
			target.setWarehouseGuid(source.getWarehouse().getCode());
		}
		
		if (source.getMappings() != null && source.getMappings().size() > 0) {
			for (String fieldName : source.getMappings().keySet()) {
				Integer fieldNumber = source.getMappings().get(fieldName);
				CmImportMappingDTO importMappingDTO = new CmImportMappingDTO();
				importMappingDTO.setColNumber(fieldNumber);
				importMappingDTO.setImportFieldName(fieldName);
				target.getImportMappingDto().add(importMappingDTO);
			}
		}
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(final CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public StoreService getStoreService() {
		return storeService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	public WarehouseService getWarehouseService() {
		return warehouseService;
	}

	public void setWarehouseService(final WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	@Override
	public CmImportJobDTO createDtoObject() {
		return new CmImportJobDTO();
	}

	@Override
	public ImportJob createDomainObject() {
		return getBean(ContextIdNames.IMPORT_JOB);
	}
}
