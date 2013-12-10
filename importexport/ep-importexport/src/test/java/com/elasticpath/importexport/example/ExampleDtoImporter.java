package com.elasticpath.importexport.example;

import com.elasticpath.importexport.importer.importers.impl.AbstractImporterImpl;

/**
 * Example {@link com.elasticpath.importexport.importer.importers.Importer Importer} for {@link ExampleDTO} (for
 * extensibility tests).
 */
public class ExampleDtoImporter extends AbstractImporterImpl<ExamplePersistence, ExampleDTO> {

	@Override
	public Class<? extends ExampleDTO> getDtoClass() {
		return ExampleDTO.class;
	}

	@Override
	public String getImportedObjectName() {
		return ExampleDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final ExampleDTO dto) {
		return "guid";
	}

	@Override
	protected ExampleAdapter getDomainAdapter() {
		return new ExampleAdapter();
	}

	@Override
	protected ExamplePersistence findPersistentObject(final ExampleDTO dto) {
		ExampleAdapter adapter = getDomainAdapter();
		return adapter.buildDomain(dto, adapter.createDomainObject());
	}

	@Override
	protected void setImportStatus(final ExampleDTO object) {
		// do nothing
	}
}
