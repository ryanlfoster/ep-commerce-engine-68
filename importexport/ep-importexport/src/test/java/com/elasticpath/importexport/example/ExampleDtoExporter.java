package com.elasticpath.importexport.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.impl.AbstractExporterImpl;

/**
 * Example {@link com.elasticpath.importexport.exporter.exporters.Exporter Exporter} for {@link ExampleDTO} (for
 * extensibility tests).
 */
public class ExampleDtoExporter extends AbstractExporterImpl<ExamplePersistence, ExampleDTO, Long> {
	private final List<ExamplePersistence> allObjects;

	/**
	 * Creates a new exporter using the given data. Any calls to fetch data will return all objects in this list.
	 *
	 * @param allObjects {@link ExamplePersistence} objects in the datastore
	 */
	public ExampleDtoExporter(final ExamplePersistence... allObjects) {
		this.allObjects = Arrays.asList(allObjects);
	}

	@Override
	public JobType getJobType() {
		return JobType.PRODUCT;
	}

	@Override
	public Class<?>[] getDependentClasses() {
		return null;
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		// do nothing
	}

	@Override
	protected Class<? extends ExampleDTO> getDtoClass() {
		return ExampleDTO.class;
	}

	@Override
	protected List<Long> getListExportableIDs() {
		// just return zero so findByIds can return everything
		return Collections.singletonList(0L);
	}

	@Override
	protected List<ExamplePersistence> findByIDs(final List<Long> subList) {
		return new ArrayList<ExamplePersistence>(allObjects); // clone it
	}

	@Override
	protected DomainAdapter<ExamplePersistence, ExampleDTO> getDomainAdapter() {
		return new ExampleAdapter();
	}
}