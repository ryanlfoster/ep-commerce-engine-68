package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.warehouse.WarehouseDTO;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.store.WarehouseService;

/**
 * Exporter for warehouses and their address.
 */
public class WarehouseExporter extends AbstractExporterImpl<Warehouse, WarehouseDTO, String> {

	private static final Logger LOG = Logger.getLogger(WarehouseExporter.class);

	private ImportExportSearcher importExportSearcher;

	private WarehouseService warehouseService;

	private List<String> warehouseCodes;

	private DomainAdapter<Warehouse, WarehouseDTO> warehouseAdapter;

	public JobType getJobType() {
		return JobType.WAREHOUSE;
	}

	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { Warehouse.class };
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		warehouseCodes = new ArrayList<String>();
		warehouseCodes.addAll(
				getImportExportSearcher().searchGuids(
						getContext().getSearchConfiguration(),
						EPQueryType.WAREHOUSE));

		LOG.info("The list for " + warehouseCodes.size() + " warehouse(s) is retrieved from the database.");

	}

	@Override
	protected Class<? extends WarehouseDTO> getDtoClass() {
		return WarehouseDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(Warehouse.class)) {
			warehouseCodes.addAll(getContext().getDependencyRegistry().getDependentGuids(Warehouse.class));
		}

		return warehouseCodes;
	}

	@Override
	protected List<Warehouse> findByIDs(final List<String> subList) {

		List<Warehouse> warehouses = new ArrayList<Warehouse>();

		for (String code : subList) {
			warehouses.add(warehouseService.findByCode(code));
		}

		return warehouses;
	}

	@Override
	protected DomainAdapter<Warehouse, WarehouseDTO> getDomainAdapter() {
		return this.warehouseAdapter;
	}

	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

	public void setWarehouseService(final WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	public void setWarehouseAdapter(final DomainAdapter<Warehouse, WarehouseDTO> warehouseAdapter) {
		this.warehouseAdapter = warehouseAdapter;
	}

}