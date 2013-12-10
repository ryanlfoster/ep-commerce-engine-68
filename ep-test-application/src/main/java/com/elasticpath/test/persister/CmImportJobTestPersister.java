package com.elasticpath.test.persister;

import java.util.List;
import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.dataimport.ImportService;

/**
 * Utility class for testing, to facilitate persisting ImportJobs and their associated Mappings. 
 * Typically used by Import/Export Fixture code. 
 */
public class CmImportJobTestPersister {

	private final BeanFactory beanFactory;

	private final ImportService cmImportJobService;
	
	/**
	 * Initializes the ImportService.
	 * @param beanFactory beanFactory
	 */
	public CmImportJobTestPersister(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		cmImportJobService = beanFactory.getBean(ContextIdNames.IMPORT_SERVICE);		
	}
	
	/**
	 * Removes all ImportJob records from the database.
	 */
	public void deleteAllImportJobs() {
		List <ImportJob> existingImportJobs = cmImportJobService.listImportJobs();
		for (ImportJob importJob : existingImportJobs) {
			cmImportJobService.remove(importJob);
		}
	}
	
	/**
	 * Assembles, persists and returns an <code>ImportJob</code> using the passed in params.
	 * 
	 * @param catalog catalog
	 * @param store store
	 * @param warehouse warehouse
	 * @param dataType dataType
	 * @param importType importType
	 * @param importName importName
	 * @param columnDelimiter columnDelimiter
	 * @param textDelimiter textDelimiter
	 * @param maxErrors maxErrors
	 * @param dataFieldMappings dataFieldMappings  Map <String, Integer>
	 * 
	 * @return ImportJob the created ImportJobImpl
	 * 
	 * @see com.elasticpath.service.dataimport.ImportService.saveOrUpdateImportJob
	 */
	//CHECKSTYLE:OFF
	public ImportJob createCmImportJob(final Catalog catalog, final Store store, final Warehouse warehouse, 
			final String importDataType, final ImportType importType, final String importName, final String csvFileName, 
			final char columnDelimiter, final char textDelimiter, 
			final int maxErrors, final Map <String, Integer> dataFieldMappings) {
	//CHECKSTYLE:ON	
		ImportJob cmImportJob = beanFactory.getBean(ContextIdNames.IMPORT_JOB);
		
		cmImportJob.setCatalog(catalog);
		cmImportJob.setStore(store);
		cmImportJob.setWarehouse(warehouse);
		cmImportJob.setGuid(importName);
		cmImportJob.setName(importName);
		cmImportJob.setImportType(importType);
		cmImportJob.setImportDataTypeName(importDataType);
		cmImportJob.setCsvFileName(csvFileName);
		cmImportJob.setCsvFileColDelimeter(columnDelimiter);
		cmImportJob.setCsvFileTextQualifier(textDelimiter);
		cmImportJob.setMaxAllowErrors(maxErrors);
		cmImportJob.setMappings(dataFieldMappings);
		
		return cmImportJobService.saveOrUpdateImportJob(cmImportJob);
	}
	
}
