package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.tax.TaxCodeDTO;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.service.tax.TaxCodeService;

/**
 * An importer for {@link TaxCode}s.
 */
public class TaxCodeImporter extends AbstractImporterImpl<TaxCode, TaxCodeDTO> {

	private DomainAdapter<TaxCode, TaxCodeDTO> taxCodeAdapter;

	private TaxCodeService taxCodeService;

	@Override
	public String getImportedObjectName() {
		return TaxCodeDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final TaxCodeDTO dto) {
		return dto.getGuid();
	}

	@Override
	protected DomainAdapter<TaxCode, TaxCodeDTO> getDomainAdapter() {
		return taxCodeAdapter;
	}

	@Override
	protected TaxCode findPersistentObject(final TaxCodeDTO dto) {
		return taxCodeService.findByCode(dto.getCode());
	}

	@Override
	protected void setImportStatus(final TaxCodeDTO object) {
		getStatusHolder().setImportStatus(object.getGuid());
	}

	public void setTaxCodeAdapter(final DomainAdapter<TaxCode, TaxCodeDTO> taxCodeAdapter) {
		this.taxCodeAdapter = taxCodeAdapter;
	}

	public void setTaxCodeService(final TaxCodeService taxCodeService) {
		this.taxCodeService = taxCodeService;
	}

	@Override
	public Class<? extends TaxCodeDTO> getDtoClass() {
		return TaxCodeDTO.class;
	}
}
