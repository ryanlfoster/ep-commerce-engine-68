package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.catalogs.AttributeDTO;
import com.elasticpath.service.attribute.AttributeService;

/**
 * Importer for {@link com.elasticpath.importexport.exporter.exporters.impl.CustomerProfileAttribute} objects; used in importexport.
 */
public class CustomerProfileAttributeImporter extends AbstractImporterImpl<Attribute, AttributeDTO> {

	private DomainAdapter<Attribute, AttributeDTO> attributeAdapter;

	private AttributeService attributeService;

	@Override
	public String getImportedObjectName() {
		return AttributeDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final AttributeDTO dto) {
		return dto.getKey();
	}

	@Override
	protected DomainAdapter<Attribute, AttributeDTO> getDomainAdapter() {
		return attributeAdapter;
	}

	@Override
	protected Attribute findPersistentObject(final AttributeDTO dto) {
		return attributeService.findByKey(dto.getKey());
	}

	@Override
	protected void setImportStatus(final AttributeDTO object) {
		getStatusHolder().setImportStatus("(" + object.getKey() + ")");
	}

	public void setAttributeAdapter(final DomainAdapter<Attribute, AttributeDTO> attributeAdapter) {
		this.attributeAdapter = attributeAdapter;
	}

	public void setAttributeService(final AttributeService attributeService) {
		this.attributeService = attributeService;
	}
	
	@Override
	public Class<? extends AttributeDTO> getDtoClass() {
		return AttributeDTO.class;
	}
}