package com.elasticpath.importexport.exporter.exporters;

import java.util.List;
import java.util.Set;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.context.ExportContext;

/**
 * Provides interface to create a sequence of ready to use exporters by export context. 
 */
public interface ExporterFactory {

	/**
	 * Create the list of exporters for export job processing.
	 * 
	 * @param context the context with information about exporters to be created and initialized
	 * @throws ConfigurationException if exporters couldn't be configured
	 * @return the list of ready to use exporters
	 */
	List<Exporter> createExporterSequence(final ExportContext context) throws ConfigurationException;
	
	/**
	 * Returns all configured {@link com.elasticpath.importexport.exporter.exporters.Exporter}s.
	 * @param context the context to retrieve the {@link com.elasticpath.importexport.exporter.exporters.Exporter}s from.
	 * @return the retrieved {@link com.elasticpath.importexport.exporter.exporters.Exporter}s.
	 */
	Set<Exporter> getAllConfiguredExporters(final ExportContext context);
}
