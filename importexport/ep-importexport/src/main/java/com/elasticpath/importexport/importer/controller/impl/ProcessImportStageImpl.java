/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.importexport.importer.controller.impl;

import java.io.InputStream;

import com.elasticpath.commons.ThreadLocalMap;
import com.elasticpath.commons.enums.OperationEnum;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.controller.ImportStage;
import com.elasticpath.importexport.importer.importers.ImportProcessor;

/**
 * The stage that does the actual data import.
 */
public class ProcessImportStageImpl implements ImportStage {

	private ImportProcessor importProcessor;
	
	private ThreadLocalMap<String, Object> metadataMap;

	private String stageId;
	
	/**
	 * Set metadata map.
	 * @param threadLocalMap the thread local map
	 */
	public void setMetadataMap(final ThreadLocalMap<String, Object> threadLocalMap) {
		this.metadataMap = threadLocalMap;
	}

	/**
	 * Process the import.
	 * 
	 * @param entryToImport the input entry to process
	 * @param context the import context
	 * @throws ConfigurationException on error
	 */
	public void execute(final InputStream entryToImport, final ImportContext context) throws ConfigurationException {
		importProcessor.process(entryToImport, context);
	}

	/**
	 * Gets the importProcessor which executes import.
	 * 
	 * @return the importProcessor
	 */
	public ImportProcessor getImportProcessor() {
		return importProcessor;
	}

	/**
	 * Sets the importProcessor to execute import.
	 * 
	 * @param importProcessor the importProcessor to set
	 */
	public void setImportProcessor(final ImportProcessor importProcessor) {
		this.importProcessor = importProcessor;
	}

	@Override
	@SuppressWarnings("PMD.PositionLiteralsFirstInComparisons")
	public boolean isActive() {
		return !OperationEnum.NONOPERATIONAL.equals(metadataMap.get("importOperation"));
	}

	@Override
	public String getName() {
		return "Process Data Import Stage";
	}
	
	@Override
	public String getId() {
		return stageId;
	}

	/**
	 *
	 * @param stageId the stageId to set
	 */
	public void setId(final String stageId) {
		this.stageId = stageId;
	}


}
