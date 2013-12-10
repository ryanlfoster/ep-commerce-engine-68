/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.importexport.importer.controller;

import java.io.InputStream;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.importer.context.ImportContext;

/**
 * Import stage is a separation for the global import process 
 * where each import stage has its own responsibility.
 */
public interface ImportStage {

	/**
	 * Executes the import stage.
	 * 
	 * @param entryToImport the input entry
	 * @param context the import context
	 * @throws ConfigurationException the configuration exception
	 * @throws ImportStageFailedException when the import stage failed to execute properly
	 */
	void execute(InputStream entryToImport, ImportContext context) throws ConfigurationException, ImportStageFailedException;

	/**
	 * Gets the flag of whether this stage is active.
	 * 
	 * @return true if the stage is active
	 */
	boolean isActive();

	/**
	 * Gets the import stage display name.
	 * 
	 * @return the import stage name
	 */
	String getName();
	
	/**
	 * Gets the stage ID.
	 * 
	 * @return the stage ID
	 */
	String getId();
}
