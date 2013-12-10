package com.elasticpath.importexport.importer.controller;

import java.io.InputStream;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;

/**
 * Import Controller provides interface to import functionalities.
 */
public interface ImportController {

	/**
	 * Loads import configuration from ImportConfiguration object.
	 * 
	 * @param importConfiguration the export configuration that will be used during the import
	 */
	void loadConfiguration(final ImportConfiguration importConfiguration);

	/**
	 * Loads import configuration from input stream.
	 * 
	 * @param configurationStream stream with configuration XML
	 * @throws ConfigurationException if configuration is invalid
	 */
	void loadConfiguration(final InputStream configurationStream) throws ConfigurationException;
	
	/**
	 * Actual import job processing. Configuration must be loaded before.
	 * 
	 * @throws ConfigurationException in case import configuration is incorrect
	 * @return <code>Summary</code> object containing information about occurred import
	 */
	Summary executeImport() throws ConfigurationException;
	
	/**
	 * Check whether failures occurred during import job execution or not.
	 * 
	 * @return true if at least one failure occurred
	 */
	boolean failuresExist();
}
