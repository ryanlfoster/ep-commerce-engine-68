package com.elasticpath.importexport.example;

import com.elasticpath.importexport.common.adapters.DomainAdapter;

/**
 * Example {@link com.elasticpath.importexport.exporter.exporters.Exporter Exporter} for {@link ExampleExtDTO} (for
 * extensibility tests).
 */
public class ExampleExtDtoExporter extends ExampleDtoExporter {

	/**
	 * Creates a new exporter using the given data. Any calls to fetch data will return all objects in this list.
	 *
	 * @param allObjects {@link ExamplePersistenceExt} objects in the datastore
	 */
	public ExampleExtDtoExporter(final ExamplePersistenceExt... allObjects) {
		super(allObjects);
	}

	@Override
	protected Class<? extends ExampleExtDTO> getDtoClass() {
		return ExampleExtDTO.class;
	}

	@Override
	protected DomainAdapter<ExamplePersistence, ExampleDTO> getDomainAdapter() {
		return new ExampleAdapterExt();
	}
}