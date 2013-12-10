package com.elasticpath.importexport.exporter.controller;

import java.io.InputStream;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;

/**
 * Export Controller provides interface to export functionalities.
 */
public interface ExportController {

	/**
	 * Loads configuration from ExportConfiguration SearchConfiguartiob objects.
	 * 
	 * @param exportConfiguration the export configuration that will be used during the export
	 * @param searchConfiguration the search configuration that will be used for finding necessary objects for export
	 * @throws ConfigurationException if the configuration is invalid
	 */
	void loadConfiguration(final ExportConfiguration exportConfiguration, final SearchConfiguration searchConfiguration)
			throws ConfigurationException;

	/**
	 * Loads configuration from input stream. The search configuration will be automatically loaded based on path to search configuration file.
	 * 
	 * @param configurationStream stream with configuration XML
	 * @param searchCriteriaStream stream with search criteria for export
	 * @throws ConfigurationException if the configuration is invalid
	 */
	void loadConfiguration(final InputStream configurationStream, final InputStream searchCriteriaStream) throws ConfigurationException;

	/**
	 * Actual export job processing. Configuration must be loaded before.
	 * 
	 * @throws ConfigurationException in case export configuration is incorrect
	 * @return <code>Summary</code> object containing information about occurred export
	 */
	Summary executeExport() throws ConfigurationException;
	
	/**
	 * Check whether failures occurred during export job execution or not.
	 * 
	 * @return true if at least one failure occurred
	 */
	boolean failuresExist();
}
