package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.tax.TaxJurisdictionDTO;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * An importer for {@link TaxJurisdiction}.
 */
public class TaxJurisdictionImporter extends AbstractImporterImpl<TaxJurisdiction, TaxJurisdictionDTO> {

	private DomainAdapter<TaxJurisdiction, TaxJurisdictionDTO> taxJurisdictionAdapter;

	private TaxJurisdictionService taxJurisdictionService;

	@Override
	protected CollectionsStrategy<TaxJurisdiction, TaxJurisdictionDTO> getCollectionsStrategy() {
		return new TaxJurisdictionCollectionsStrategy(getContext().getImportConfiguration().getImporterConfiguration(JobType.TAXJURISDICTION));
	}

	@Override
	public String getImportedObjectName() {
		return TaxJurisdictionDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final TaxJurisdictionDTO dto) {
		return dto.getGuid();
	}

	@Override
	protected DomainAdapter<TaxJurisdiction, TaxJurisdictionDTO> getDomainAdapter() {
		return taxJurisdictionAdapter;
	}

	@Override
	protected TaxJurisdiction findPersistentObject(final TaxJurisdictionDTO dto) {
		return taxJurisdictionService.findByGuid(dto.getGuid());
	}

	@Override
	protected void setImportStatus(final TaxJurisdictionDTO object) {
		getStatusHolder().setImportStatus("(" + object.getGuid() + ")");
	}

	/**
	 * Set the adapter to be used to turn {@code TaxJurisdiction} into {@code TaxJurisdictionDTO}s.
	 * 
	 * @param adapter DomainAdapter to be injected.
	 */
	public void setTaxJurisdictionAdapter(final DomainAdapter<TaxJurisdiction, TaxJurisdictionDTO> adapter) {
		this.taxJurisdictionAdapter = adapter;
	}

	public void setTaxJurisdictionService(final TaxJurisdictionService taxJurisdictionService) {
		this.taxJurisdictionService = taxJurisdictionService;
	}

	@Override
	public Class<? extends TaxJurisdictionDTO> getDtoClass() {
		return TaxJurisdictionDTO.class;
	}

}
