/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.csvimport.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.ImportConstants;
import com.elasticpath.csvimport.CsvReadResult;
import com.elasticpath.csvimport.CsvReaderConfiguration;
import com.elasticpath.csvimport.DependentDtoImporter;
import com.elasticpath.csvimport.DtoCsvLineReader;
import com.elasticpath.csvimport.ImportValidRow;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.misc.ImportEmailPropertyHelper;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.dataimport.ImportJobRunner;
import com.elasticpath.service.dataimport.ImportJobStatusHandler;
import com.elasticpath.service.dataimport.ImportService;

/**
 * <p>{@code ImportJobRunner} to import objects of some type T 
 * by marshaling them from CSV files, assembling them into Domain objects, and 
 * adding them with the Service layer. The objects are assumed to be dependent on 
 * another object in the system (e.g. products imported into a catalog, BaseAmounts 
 * imported into a PriceList, etc).</p> 
 * 
 * <p>This implementation uses a {@code DtoCsvLineReader} to read in the objects from 
 * the CSV file designated in the {@code ImportJob} (an InputStream is created from the file
 * on validation and on import), then uses a {@link DependentDtoImporter} to assemble the 
 * domain objects from the objects and import them.</p> 
 * @param <T> The type of object being imported (e.g. BaseAmountDTO)
 * @param <V> The type of object being a helper for imported object, (e.q. PriceListDescriptorDTO)
 */
public class ImportDtoJobRunnerCsvImpl<T, V> implements ImportJobRunner {
	
	private BeanFactory beanFactory;
	private ImportJob importJob;
	private ImportService importService;

	private DependentDtoImporter<T, V> insertUpdateDtoImporter;
	private DependentDtoImporter<T, V> updateDtoImporter;
	private DependentDtoImporter<T, V> insertDtoImporter;
	private DependentDtoImporter<T, V> deleteDtoImporter;
		
	private DtoCsvLineReader<T> dtoCsvLineReader; //parses CSV input to DTOs
	private ImportJobStatusHandler importJobStatusHandler;
	private ImportJobRequest importJobRequest;
	private String importJobProcessId;
	private ChangeSetService changeSetService;
	private Boolean changeSetEnabled;
	private Map<String, Object> persistenceListenerMetadataMap;

	/**
	 * Sets the import job to be run.
	 * @param importJob the import job to set
	 */
	public void setImportJob(final ImportJob importJob) {
		this.importJob = importJob;
	}
	
	/**
	 * @return the import job to run
	 */
	protected ImportJob getImportJob() {
		return this.importJob;
	}

	/**
	 * <p>Validates all the rows in the current import job, and returns a list of {@link ImportBadRow} objects
	 * for every record in the input that is does not meet validation criteria.</p>
	 * 
	 * <p>This implementation calls {@link #getDtoCsvLineReader()} and uses it to parse the input stream 
	 * retrieved from {@link #getInputStream()}, using the criteria retrieved from {@link #getCsvReaderConfiguration()}.
	 * It also counts the total number of input records and sets them on the {@link ImportJob} by calling
	 * {@link #getImportJob()}.</p>
	 * 
	 * @param locale not used in this implementation
	 * @return a list of bad rows in the import job
	 */
	public List<ImportBadRow> validate(final Locale locale) {
		//TODO: Ideally this would be performed by an ImportValidator class of some sort.
		DtoCsvLineReader<T> reader = getDtoCsvLineReader();
		reader.setInputStream(createInputStream());
		reader.setConfiguration(getCsvReaderConfiguration());
		reader.open();
		CsvReadResult<T> readResult = null;
		try {
			//skip header row by reading it
			reader.readDtos(1, false);
			//read the rest of the rows
			readResult = reader.readDtos(-1, false);
		} finally {
			reader.close();
		}
		List<ImportBadRow> badRows = new ArrayList<ImportBadRow>(0);
		if (readResult != null) {
			badRows = readResult.getBadRows();
			if (badRows.isEmpty()) {
				for (ImportValidRow< ? > validRow : readResult.getValidRows()) {
					validateChangeSetStatus(validRow.getDto(), validRow.getRowNumber(), badRows);
				}
			}
		}
		return badRows;
	}

	/**
	 *
	 * @param object the object to check
	 * @param rowNumber the row number
	 * @param badRows the list of bad rows to use
	 */
	protected void validateChangeSetStatus(final Object object, final int rowNumber, final List<ImportBadRow> badRows) {
		// verify the change set status of the object in case change sets are enabled
		if (!checkChangeSetStatus(object, getImportJobRequest().getChangeSetGuid())) {
			// verify the changeset status only if the verification of the line was successful
			ImportBadRow badRow = getBeanFactory().getBean(ContextIdNames.IMPORT_BAD_ROW);
			// report error
			final ImportFault importFault = getBeanFactory().getBean(ContextIdNames.IMPORT_FAULT);
			importFault.setCode("import.csvFile.badRow.unavailableForChangeSet");
			importFault.setArgs(new Object[] { rowNumber, getImportJobRequest().getChangeSetGuid() });
			badRow.addImportFault(importFault);
			badRows.add(badRow);
		}
	}

	/**
	 * Checks the status of the given object for whether it could be added to a change set.
	 * 
	 * @param object the object
	 * @param changeSetGuid the change set GUID
	 * @return true if the change set 
	 */
	protected boolean checkChangeSetStatus(final Object object, final String changeSetGuid) {
		if (!isChangeSetEnabled()) {
			return true;
		}
		ChangeSetObjectStatus status = getChangeSetService().getStatus(object);
		// check if the object could be resolved to an object descriptor
		// if the object descriptor is null then it isn't a supported object by the change set framework
		if (status.getObjectDescriptor() != null) {
			return status.isAvailable(changeSetGuid);
		}
		return true;
	}

	/**
	 * Creates new reader criteria from the current import job.
	 * Calls {@link #getBeanFactory()}.	
	 * @return the reader criteria
	 */
	protected CsvReaderConfiguration getCsvReaderConfiguration() {
		CsvReaderConfiguration configuration = getBeanFactory().getBean(ContextIdNames.CSV_READER_CONFIGURATION);
		configuration.setDelimiter(getCsvColDelimiter());
		configuration.setTextQualifier(getCsvTextQualifier());
		configuration.setFieldColumnIndexMapping(getFieldColumnIndexMappings());
		return configuration;
	}
	
	/**
	 * Calls {@link #getImportJob()}.
	 * @return the CSV column delimiter
	 */
	protected char getCsvColDelimiter() {
		return getImportJob().getCsvFileColDelimeter();
	}
	
	/**
	 * Calls {@link #getImportJob()}.
	 * @return the CSV text qualifier (which encloses entries that should be treated as text by the CSV reader, 
	 * in case an entry contains a delimiter character within it).
	 */
	protected char getCsvTextQualifier() {
		return getImportJob().getCsvFileTextQualifier();
	}
	
	/**
	 *Calls {@link #getImportJob()}. 
	 * @return the name of the CSV file to be imported
	 */
	protected String getCsvFileName() {
		return importJobRequest.getImportSource();
	}
	
	/**
	 * Calls {@link #getCsvFileName()} to retrieve the name of the CSV file to be imported,
	 * and creates an {@link InputStream} from it.
	 * @return the InputStream
	 * @throws EpPersistenceException if the file is not found
	 */
	protected InputStream createInputStream() {
		String filename = getCsvFileName();
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(filename);
			
		} catch (FileNotFoundException ex) {
			throw new EpPersistenceException("File not found:" + filename, ex);
		}
		return inputStream;
	}

	/**
	 * Calls {@link #getImportJob()}.
	 * @return a map of DTO field names to CSV column indexes
	 */
	protected Map<String, Integer> getFieldColumnIndexMappings() {
		return getImportJob().getMappings();
	}
	
	/**
	 * Gets the {@code ImportDataType} prototype bean corresponding to the given name.
	 * This implementation gets it from the ImportService.
	 * Calls {@link #getImportService()}.
	 * @param importDataTypeName the identifier of the implementation bean
	 * @return the {@code ImportDataType} prototype bean corresponding to the given string.
	 */
	protected ImportDataType getImportDataType(final String importDataTypeName) {
		return getImportService().findImportDataType(importDataTypeName);
	}
	
	/**
	 * @return the bean factory
	 */
	public BeanFactory getBeanFactory() {
		return this.beanFactory;
	}
	
	/**
	 * Sets the bean factory.
	 * @param beanFactory the bean factory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Performs an import and runs post-import tasks.
	 * Calls {@link #readAndImport()} and {@link #postImportTasks()}.
	 */
	public void run() {
        prepareAuditData();
		prepareChangeSetProcessing();
		readAndImport();
	}
	
	/**
	 * Adds the metadata for enabling the change set processing by the change set persistence listener.
	 */
	protected void prepareChangeSetProcessing() {
		if (isChangeSetEnabled()) {
			// enable change set persistence listener
			getPersistenceListenerMetadataMap().put("changeSetGuid", getImportJobRequest().getChangeSetGuid());
			getPersistenceListenerMetadataMap().put("changeSetUserGuid", getImportJobRequest().getInitiator().getGuid());
			getPersistenceListenerMetadataMap().put("activeImportStage", "stage2");
			getPersistenceListenerMetadataMap().put("addToChangeSetFlag", "true");
		}
	}

    /**
     * Adds the metadata for auditing.
     */
    protected void prepareAuditData() {
        getPersistenceListenerMetadataMap().put("userGuid", getImportJobRequest().getInitiator().getGuid());
    }

	/**
	 * Checks if the change set feature is enabled.
	 * For performance reasons the value is cached so that no additional SQL queries are performed.
	 * The import job runner is a bean of type 'prototype' which means that this value will be cached per import job run.
	 * 
	 * @return true if the change set feature is enabled
	 */
	protected boolean isChangeSetEnabled() {
		if (changeSetEnabled == null) {
			changeSetEnabled = getChangeSetService().isChangeSetEnabled();
		}
		return changeSetEnabled;
	}

	
	/**
	 * Reads from an input and performs an import.
	 * This implementation using the following steps:
	 * <ol><li>Use the {@link DtoCsvLineReader} to marshal the CSV entries in the {@link InputStream} into 
	 * {@code Dto} objects.</li>
	 * <li>Use the {@link DependentDtoImporter} to assemble the domain objects from the DTOs and import them.</li></ol>
	 * The import is performed in chunks of a size given by a call to {@link #getChunkSize()} to avoid over-consumption of memory.
	 * The currently running import job is retrieved from {@link #getRunningJob()) and is updated with failed rows, successful 
	 * rows, etc., then is set to finished when the import is complete.
	 * 
	 * A transaction will be triggered for each row of import (every service call) in this implementation.
	 * Performance can be improved by manually starting a new transaction for each chunk of import data and letting it
	 * propagate to the service. Gains are not significant unless chunk size is large, i.e. 10% gain for chunk size of 1000.
	 * See AbstractImportJobRunnerImpl for example of starting manual transactions.
	 */
	protected void readAndImport() {
		DtoCsvLineReader<T> reader = getDtoCsvLineReader();
		reader.setInputStream(createInputStream());
		reader.setConfiguration(getCsvReaderConfiguration());
		ImportJobState finalImportJobState = ImportJobState.FINISHED;
		int totalRows = 0;
		try {
			reader.open();
			//skip header row by reading it, and ignore any errors
			reader.readDtos(1, false);
			V dependentObjectGuid = getDependentObjectGuid();
			while (!reader.isInputStreamFinished()) {
				List<ImportBadRow> badRows = new ArrayList<ImportBadRow>();
				//read in the DTOs in chunks
				CsvReadResult<T> readResult = reader.readDtos(getChunkSize(), true);
				badRows.addAll(readResult.getBadRows());
				//convert the DTOs to domain objects and persist them
				List<ImportBadRow> badRowsReturned = getDependentDtoImporter().importDtos(readResult.getValidRows(), dependentObjectGuid);
				badRows.addAll(badRowsReturned);
				totalRows += readResult.getTotalRows();
				
				importJobStatusHandler.reportCurrentRow(importJobProcessId, totalRows);
				importJobStatusHandler.reportFailedRows(importJobProcessId, badRowsReturned.size());
				importJobStatusHandler.reportBadRows(importJobProcessId, badRows.toArray(new ImportBadRow[badRows.size()]));
					
				if (!importJobStatusHandler.verifyImportJobFailedRows(importJobProcessId, importJobRequest.getMaxAllowedFailedRows())) {
					finalImportJobState = ImportJobState.FAILED;
					break;
				}
				
				if (importJobStatusHandler.isImportJobCancelled(importJobProcessId)) {
					finalImportJobState = ImportJobState.CANCELLED;
					break;
				}
			}
		} finally {
			reader.close();
			importJobStatusHandler.reportImportJobState(importJobProcessId, finalImportJobState);
		}
	}

	/**
	 * Gets the identifier of the object that imported objects are dependent upon
	 * (e.g. BaseAmounts are imported into a PriceListDescriptor, so the import job
	 * runner requires the identifier of the PLD.)
	 * This implementation calls {@link #getImportJob()} to retrieve the identifier
	 * from the import job definition. 
	 * 
	 * @return the identifier
	 */
	@SuppressWarnings("unchecked")
	protected V getDependentObjectGuid() {
		return (V) getImportJob().getDependentPriceListGuid();
	}
	
	/**
	 * @return the ImportEmailPropertyHelper
	 */
	ImportEmailPropertyHelper getImportEmailPropertyHelper() {
		return getBeanFactory().getBean(ContextIdNames.EMAIL_PROPERTY_HELPER_IMPORT);
	}
	
	/**
	 * Calls {@link #getImportService()}.
	 * @return the currently running job
	 */
	protected ImportJobStatus getRunningJob() {
		return getImportService().getImportJobStatus(importJobProcessId);
	}
	
	/**
	 * Gets the number of records to import at a time.
	 * This implementation returns the constant {@link ImportConstants#COMMIT_UNIT}.
	 * @return the number of records to import at a time.
	 */
	protected int getChunkSize() {
		return ImportConstants.COMMIT_UNIT;
	}
	
	/**
	 * <p>Gets the DependentDtoImporter instance appropriate to the type of Import
	 * being performed.</p>
	 * <p>This implementation calls {@link #getImportType()} to retrieve the type
	 * of import that was configured and calls one of the getters for the four known
	 * importer types. Subclasses can override this method to force a particular importer.</p>
	 * @return the importer instance
	 * @throws EpServiceException if the configured import type is not supported
	 * (no DependentDtoImporter instance has been set for that type of import).
	 */
	protected DependentDtoImporter<T, V> getDependentDtoImporter() {
		ImportType importType = getImportJob().getImportType();
		DependentDtoImporter<T, V> importer = null;
		if (AbstractImportTypeImpl.UPDATE_TYPE.equals(importType)) {
			importer = getUpdateDtoImporter();
		}
		if (AbstractImportTypeImpl.INSERT_TYPE.equals(importType)) {
			importer = getInsertDtoImporter();
		}
		if (AbstractImportTypeImpl.DELETE_TYPE.equals(importType)) {
			importer = getDeleteDtoImporter();
		}
		if (AbstractImportTypeImpl.INSERT_UPDATE_TYPE.equals(importType)) {
			importer = getInsertUpdateDtoImporter();
		}
		if (importer == null) {
			throw new EpServiceException("Importer class for ImportType=" + importType + " has not been set.");
		}
		return importer;
	}
	
	/**
	 * @return the type of the import being performed (e.g. insert, update, etc).
	 */
	ImportType getImportType() {
		return getImportJob().getImportType();
	}
	
	/**
	 * @return the importService
	 */
	public ImportService getImportService() {
		return importService;
	}

	/**
	 * @param importService the importService to set
	 */
	public void setImportService(final ImportService importService) {
		this.importService = importService;
	}

	/**
	 * @return the csv line reader
	 */
	public DtoCsvLineReader<T> getDtoCsvLineReader() {
		return this.dtoCsvLineReader;
	}
	
	/**
	 * Sets the csv line reader.
	 * @param dtoCsvLineReader the csv line reader
	 */
	public void setDtoCsvLineReader(final DtoCsvLineReader<T> dtoCsvLineReader) {
		this.dtoCsvLineReader = dtoCsvLineReader;
	}

	/**
	 * @return the insertUpdateDtoImporter
	 */
	public DependentDtoImporter<T, V> getInsertUpdateDtoImporter() {
		return insertUpdateDtoImporter;
	}

	/**
	 * @param insertUpdateDtoImporter the insertUpdateDtoImporter to set
	 */
	public void setInsertUpdateDtoImporter(final DependentDtoImporter<T, V> insertUpdateDtoImporter) {
		this.insertUpdateDtoImporter = insertUpdateDtoImporter;
	}

	/**
	 * @return the updateDtoImporter
	 */
	public DependentDtoImporter<T, V> getUpdateDtoImporter() {
		return updateDtoImporter;
	}

	/**
	 * @param updateDtoImporter the updateDtoImporter to set
	 */
	public void setUpdateDtoImporter(final DependentDtoImporter<T, V> updateDtoImporter) {
		this.updateDtoImporter = updateDtoImporter;
	}

	/**
	 * @return the insertDtoImporter
	 */
	public DependentDtoImporter<T, V> getInsertDtoImporter() {
		return insertDtoImporter;
	}

	/**
	 * @param insertDtoImporter the insertDtoImporter to set
	 */
	public void setInsertDtoImporter(final DependentDtoImporter<T, V> insertDtoImporter) {
		this.insertDtoImporter = insertDtoImporter;
	}

	/**
	 * @return the deleteDtoImporter
	 */
	public DependentDtoImporter<T, V> getDeleteDtoImporter() {
		return deleteDtoImporter;
	}

	/**
	 * @param deleteDtoImporter the deleteDtoImporter to set
	 */
	public void setDeleteDtoImporter(final DependentDtoImporter<T, V> deleteDtoImporter) {
		this.deleteDtoImporter = deleteDtoImporter;
	}

	/**
	 * Initialises this runner.
	 * 
	 * @param importJobRequest the request the job was initiated with
	 * @param importJobProcessId the import job process ID
	 */
	public void init(final ImportJobRequest importJobRequest, final String importJobProcessId) {
		this.importJobRequest = importJobRequest;
		this.importJobProcessId = importJobProcessId;
		setImportJob(importJobRequest.getImportJob());
	}

	/**
	 *
	 * @return the importJobStatusHandler
	 */
	protected ImportJobStatusHandler getImportJobStatusHandler() {
		return importJobStatusHandler;
	}

	/**
	 *
	 * @param importJobStatusHandler the importJobStatusHandler to set
	 */
	public void setImportJobStatusHandler(final ImportJobStatusHandler importJobStatusHandler) {
		this.importJobStatusHandler = importJobStatusHandler;
	}

	@Override
	public int getTotalRows() {
		DtoCsvLineReader<T> reader = getDtoCsvLineReader();
		reader.setInputStream(createInputStream());
		reader.setConfiguration(getCsvReaderConfiguration());

		reader.open();
		int totalRows = 0;
		try {
			//read all the rows
			CsvReadResult<T> readResult = reader.readDtos(-1, false);
			totalRows = readResult.getTotalRows();
		} finally {
			reader.close();
		}
		// excluding the title row
		return totalRows - 1;
	}


	/**
	 * @return the importJobRequest
	 */
	protected ImportJobRequest getImportJobRequest() {
		return importJobRequest;
	}

	/**
	 * @return the importJobProcessId
	 */
	protected String getImportJobProcessId() {
		return importJobProcessId;
	}

	/**
	 *
	 * @return the changeSetService
	 */
	protected ChangeSetService getChangeSetService() {
		return changeSetService;
	}

	/**
	 *
	 * @param changeSetService the changeSetService to set
	 */
	public void setChangeSetService(final ChangeSetService changeSetService) {
		this.changeSetService = changeSetService;
	}
	
	/**
	 *
	 * @param metadataMap the persistence listener metadata map
	 */
	public void setPersistenceListenerMetadataMap(final Map<String, Object> metadataMap) {
		this.persistenceListenerMetadataMap = metadataMap;
	}

	/**
	 *
	 * @return the persistenceListenerMetadataMap
	 */
	protected Map<String, Object> getPersistenceListenerMetadataMap() {
		return persistenceListenerMetadataMap;
	}

}
