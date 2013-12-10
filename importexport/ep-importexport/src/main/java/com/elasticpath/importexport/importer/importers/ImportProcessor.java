package com.elasticpath.importexport.importer.importers;

import java.io.InputStream;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.importer.context.ImportContext;

/**
 * Select concrete importer by XML tag name. Executes import operations common for all importers.
 */
public interface ImportProcessor {

	/**
	 * Processes import job.
	 * 
	 * @param entryToImport the entity to Import
	 * @param context the ImportContext 
	 * @throws ConfigurationException if case of problems during importer's creation 
	 */
	void process(final InputStream entryToImport, final ImportContext context) throws ConfigurationException;
}
