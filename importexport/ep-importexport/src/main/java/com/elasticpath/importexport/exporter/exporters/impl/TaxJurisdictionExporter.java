package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.tax.TaxJurisdictionDTO;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * Exporter for TaxJurisdiction (and TaxCategory, TaxRegion and TaxCode).
 */
public class TaxJurisdictionExporter extends AbstractExporterImpl<TaxJurisdiction, TaxJurisdictionDTO, String> {

	private static final Logger LOG = Logger.getLogger(TaxJurisdictionExporter.class);

	private ImportExportSearcher importExportSearcher;
	private DomainAdapter<TaxJurisdiction, TaxJurisdictionDTO> taxJurisdictionAdapter;
	private List<String> taxJurisdictionGuidList = Collections.emptyList();
	private TaxJurisdictionService taxJurisdictionService;

	@Override
	public JobType getJobType() {
		return JobType.TAXJURISDICTION;
	}

	@Override
	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { TaxJurisdiction.class };
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		taxJurisdictionGuidList = new ArrayList<String>();
		taxJurisdictionGuidList.addAll(
				getImportExportSearcher().searchGuids(
						getContext().getSearchConfiguration(),
						EPQueryType.TAXJURISDICTION));
		
		/*
		 * Eventually: taxJurisdictionUidPkList = importExportSearcher.searchUids(getContext().getSearchConfiguration(),
		 * EPQueryType.TAXJURISDICTION);
		 */

		LOG.info("The list for " + taxJurisdictionGuidList.size() + " tax jurisdictions retrieved from the database.");
	}

	@Override
	protected Class<? extends TaxJurisdictionDTO> getDtoClass() {
		return TaxJurisdictionDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(TaxJurisdiction.class)) {
			taxJurisdictionGuidList.addAll(getContext().getDependencyRegistry().getDependentGuids(TaxJurisdiction.class));
		}

		return taxJurisdictionGuidList;

	}

	@Override
	protected List<TaxJurisdiction> findByIDs(final List<String> subList) {
		return taxJurisdictionService.findByGuids(subList);
	}

	@Override
	protected DomainAdapter<TaxJurisdiction, TaxJurisdictionDTO> getDomainAdapter() {
		return this.taxJurisdictionAdapter;
	}

	/**
	 * Set the adapter to be used to turn {@code TaxJurisdiction} into {@code TaxJurisdictionDTO}s.
	 * 
	 * @param adapter injected DomainAdapter.
	 */
	public void setTaxJurisdictionAdapter(final DomainAdapter<TaxJurisdiction, TaxJurisdictionDTO> adapter) {
		this.taxJurisdictionAdapter = adapter;
	}

	/**
	 * Inject a {@code TaxJurisdictionService} used to find tax jurisdictions.
	 * 
	 * @param taxJurisdictionService injected service
	 */
	public void setTaxJurisdictionService(final TaxJurisdictionService taxJurisdictionService) {
		this.taxJurisdictionService = taxJurisdictionService;
	}

	private ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}
	
	/**
	 * @param importExportSearcher The ImportExportSearcher to use.
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

}