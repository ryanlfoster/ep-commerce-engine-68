package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.pricing.PriceListAssignmentAdapter;
import com.elasticpath.importexport.common.dto.pricing.PriceListAssignmentDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.pricing.PriceListAssignmentService;

/**
 * An exporter for PLAs.
 */
public class PriceListAssignmentExporter extends AbstractExporterImpl<PriceListAssignment, PriceListAssignmentDTO, String> {

	private PriceListAssignmentService priceListAssignmentService;

	private ImportExportSearcher importExportSearcher;

	private PriceListAssignmentAdapter priceListAssignmentAdapter;

	private List<String> plaGuids;

	private static final Logger LOG = Logger.getLogger(PriceListAssignmentExporter.class);

	@Override
	protected List<PriceListAssignment> findByIDs(final List<String> subList) {
		List<PriceListAssignment> plas = new ArrayList<PriceListAssignment>();
		for (String guid : subList) {
			plas.add(getPriceListAssignmentService().findByGuid(guid));
		}
		return plas;
	}

	@Override
	protected DomainAdapter<PriceListAssignment, PriceListAssignmentDTO> getDomainAdapter() {
		return getPriceListAssignmentAdapter();
	}

	@Override
	protected Class<? extends PriceListAssignmentDTO> getDtoClass() {
		return PriceListAssignmentDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {

		if (getContext().getDependencyRegistry().supportsDependency(PriceListAssignment.class)) {
			plaGuids.addAll(getContext().getDependencyRegistry().getDependentGuids(PriceListAssignment.class));
		}

		return plaGuids;
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		plaGuids = new ArrayList<String>();
		plaGuids.addAll(getImportExportSearcher().searchGuids(getContext().getSearchConfiguration(), EPQueryType.PRICELISTASSIGNMENT));
		LOG.info("Price List Assignment Guids found: " + plaGuids.size() + " GUIDs: [" + plaGuids + "]");
	}

	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { PriceListAssignment.class };
	}

	public JobType getJobType() {
		return JobType.PRICELISTASSIGNMENT;
	}

	/**
	 * @param priceListAssignmentService the priceListAssignmentService to set
	 */
	public void setPriceListAssignmentService(final PriceListAssignmentService priceListAssignmentService) {
		this.priceListAssignmentService = priceListAssignmentService;
	}

	/**
	 * @return the priceListAssignmentService
	 */
	public PriceListAssignmentService getPriceListAssignmentService() {
		return priceListAssignmentService;
	}

	/**
	 * @return The importExportSearcher.
	 */
	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	/**
	 * @param importExportSearcher The ImportExportSearcher.
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

	/**
	 * @param priceListAssignmentAdapter the priceListAssignmentAdapter to set
	 */
	public void setPriceListAssignmentAdapter(final PriceListAssignmentAdapter priceListAssignmentAdapter) {
		this.priceListAssignmentAdapter = priceListAssignmentAdapter;
	}

	/**
	 * @return the priceListAssignmentAdapter
	 */
	public PriceListAssignmentAdapter getPriceListAssignmentAdapter() {
		return priceListAssignmentAdapter;
	}

}