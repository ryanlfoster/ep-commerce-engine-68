/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.dataimport.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.ImportConstants;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.exception.EpInvalidGuidBindException;
import com.elasticpath.commons.exception.EpNonNullBindException;
import com.elasticpath.commons.exception.EpProductInUseException;
import com.elasticpath.commons.exception.EpTooLongBindException;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.DateUtils;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.CatalogImportField;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.persistence.CsvFileReader;
import com.elasticpath.persistence.PrintWriter;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Query;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.service.dataimport.ImportJobRunner;
import com.elasticpath.service.dataimport.ImportJobStatusHandler;
import com.elasticpath.service.dataimport.ImportService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.TimeService;

/**
 * A skeleton implementation of <code>ImportJobRunner</code> of the GoF  method pattern. 
 * A concrete must override the abstract methods to provide concrete behaviour.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.TooManyMethods", "PMD.ExcessiveClassLength" })
public abstract class AbstractImportJobRunnerImpl extends AbstractEpPersistenceServiceImpl implements ImportJobRunner, EpPersistenceService {

	private static final String CAN_NOT_FIND_MESSAGE = "Cannot find entity with the given guid:";
	
	private static final String CAN_NOT_UPDATE_MESSAGE = "Cannot update entity because it was not exist";

	private static final Logger LOG = Logger.getLogger(AbstractImportJobRunnerImpl.class);

	private static final int TOTAL_ERRORS_MAX = 100;

	private ImportService importService;
	
	private ImportJobStatusHandler importJobStatusHandler;

	/**
	 * The import job to run.
	 */
	private ImportJob importJob;

	private Integer guidColNumber;

	private Object baseObject;

	private Map<ImportField, Integer> mappings;

	private Utility utility;

	private ImportGuidHelper importGuidHelper;

	private final Set<String> importedEntityGuids = new HashSet<String>();

	private ImportDataType importDataType;

	private ImportJobRequest request;

	private String importJobProcessId;

	private ChangeSetService changeSetService;

	private Map<String, Object> persistenceListenerMetadataMap;

	private Boolean changeSetEnabled;
	
	private TimeService timeService;

	/**
	 * Find the entity with the given guid.
	 * 
	 * @param guid the guid
	 * @return the entity with the given guid if it exists, otherwise <code>null</code>.
	 */
	protected abstract Entity findEntityByGuid(String guid);

	/**
	 * Creates a new entity.
	 * 
	 * @param baseObject the base object might be used to determine entity type, such as <code>ProductType</code> etc.
	 * @return the newly created entity
	 */
	protected abstract Entity createNewEntity(Object baseObject);

	/**
	 * Update the entity before it get saved.
	 * 
	 * @param entity the entity to save
	 */
	protected abstract void updateEntityBeforeSave(Entity entity);

	/**
	 * Returns the commit unit.
	 * 
	 * @return the commit unit.
	 */
	protected abstract int getCommitUnit();

	/**
	 * Validate the import job.
	 * @param locale of the result messages
	 * @return a list of <code>ImportBadRow</code>, or a empty list if there is no errors.
	 */
	public List<ImportBadRow> validate(final Locale locale) {
		final CsvFileReader csvFileReader = getCsvFileReader();
		final List<ImportBadRow> importBadRows = new ArrayList<ImportBadRow>();
		// skip the title line
		String[] nextLine = csvFileReader.readNext();
		int firstLineCount = nextLine.length;

		int rowNumber = 0;
		
		int totalErrorsNumber = 0;
		
		// check 1st line against title line
		nextLine = csvFileReader.readNext();
		if (nextLine == null) {
			throw new EpDomainException("The import file has no data rows.");
		}
		if (firstLineCount != nextLine.length) {
			throw new EpDomainException("The title line and the 1st data line do not have the same " + "number of columns: " + firstLineCount
					+ " vs. " + nextLine.length);
		}

		rowNumber++;
		if (!validateOneRow(nextLine, rowNumber, importBadRows)) {
			totalErrorsNumber += importBadRows.get(importBadRows.size() - 1).getImportErrors(locale).size(); 
		}

		while ((nextLine = csvFileReader.readNext()) != null) {
			rowNumber++;
			if (!validateOneRow(nextLine, rowNumber, importBadRows)) {
				totalErrorsNumber += importBadRows.get(importBadRows.size() - 1).getImportErrors(locale).size(); 
			}
			// could break (out of memory) if there are too many rows
			if (totalErrorsNumber >= TOTAL_ERRORS_MAX) {
				break;
			}
		}

		csvFileReader.close();
		return importBadRows;
	}

	/**
	 * Validates one row.
	 * @param nextLine the row to validate
	 * @param rowNumber the row number being validated
	 * @param importBadRows the list of bad rows to which this row should be added if it's bad
	 * @return true if the row is valid
	 */
	boolean validateOneRow(final String[] nextLine, final int rowNumber, final List<ImportBadRow> importBadRows) {
		final List<ImportFault> faults = new ArrayList<ImportFault>();
		Entity entity = null;

		if (importDataType.isEntityImport()) {
			// If this importDataType is an entity we need to create a new
			// instance of it for the persistence layer.
			entity = createNewEntity(baseObject);
		}
		
		final Persistable persistenceObject = getPersistableObject(entity);
		
		for (Entry<ImportField, Integer> entry : mappings.entrySet()) {
			final ImportField importField = entry.getKey();
			final Integer colNum = entry.getValue();
			try {
				checkField(nextLine[colNum], persistenceObject, importField);
			} catch (EpInvalidGuidBindException e) {
				final ImportFault importFault = getImportFaultWarning();
				importFault.setCode("import.csvFile.badRow.wrongGuid");
				importFault.setArgs(new Object[] { importField.getName(), importField.getType(), String.valueOf(colNum.intValue()),
						nextLine[colNum.intValue()] });
				faults.add(importFault);
			} catch (EpNonNullBindException e) {
				final ImportFault importFault = getImportFaultError();
				importFault.setCode("import.csvFile.badRow.notNull");
				importFault.setArgs(new Object[] { importField.getName(), importField.getType(), String.valueOf(colNum.intValue()),
						nextLine[colNum.intValue()] });
				faults.add(importFault);
			} catch (EpTooLongBindException e) {
				final ImportFault importFault = getImportFaultError();
				importFault.setCode("import.csvFile.badRow.tooLong");
				importFault.setArgs(new Object[] { importField.getName(), importField.getType(), String.valueOf(colNum.intValue()),
						nextLine[colNum.intValue()] });
				faults.add(importFault);
			} catch (EpBindException e) {
				final ImportFault importFault = getImportFaultError();
				importFault.setCode("import.csvFile.badRow.bindError");
				importFault.setArgs(new Object[] { importField.getName(), importField.getType(), String.valueOf(colNum.intValue()),
						nextLine[colNum.intValue()] });
				faults.add(importFault);
			} catch (IllegalArgumentException e) {
				final ImportFault importFault = getImportFaultError();
				importFault.setCode("import.csvFile.badRow.badValue");
				importFault.setArgs(new Object[] { importField.getName(), importField.getType(), String.valueOf(colNum.intValue()),
						nextLine[colNum.intValue()] });
				faults.add(importFault);
			}
		}

		validateChangeSetStatus(nextLine, rowNumber, faults, persistenceObject);
		
		if (!faults.isEmpty()) {
			final ImportBadRow importBadRow = getBean(ContextIdNames.IMPORT_BAD_ROW);
			importBadRow.setRowNumber(rowNumber);
			importBadRow.setRow(nextLine[0]);
			importBadRow.addImportFaults(faults);
			importBadRows.add(importBadRow);
			return false;
		}
		return true;
	}

	/**
	 * Verifies that the object represented by the given CSV row data is not in any other change set if
	 * it is applicable to be added to change sets. A suitable error message is returned in case the object
	 * is unavailable.
	 * 
	 * @param nextLine the line data
	 * @param rowNumber the row number
	 * @param faults the faults list
	 * @param persistenceObject the persistable object
	 */
	protected void validateChangeSetStatus(final String[] nextLine, final int rowNumber, final List<ImportFault> faults,
			final Persistable persistenceObject) {
		// verify the changeset status only if the verification of the line was successful
		if (faults.isEmpty() && isChangeSetEnabled()) {

			// populate the object's fields in order to be able to 
			updateContent(nextLine, persistenceObject);
			
			// verify the change set status of the object in case change sets are enabled
			if (!checkChangeSetStatus(persistenceObject, getRequest().getChangeSetGuid())) {
				// report error
				final ImportFault importFault = getImportFaultError();
				importFault.setCode("import.csvFile.badRow.unavailableForChangeSet");
				importFault.setArgs(new Object[] { rowNumber, getRequest().getChangeSetGuid() });
				faults.add(importFault);
			}
		}
	}

	/**
	 * Checks the status of the given object for whether it could be added to a change set.
	 * 
	 * @param persistenceObject the object
	 * @param changeSetGuid the change set GUID
	 * @return true if the change set 
	 */
	protected boolean checkChangeSetStatus(final Persistable persistenceObject, final String changeSetGuid) {
		ChangeSetObjectStatus status = changeSetService.getStatus(persistenceObject);
		// check if the object could be resolved to an object descriptor
		// if the object descriptor is null then it isn't a supported object by the change set framework
		if (status.getObjectDescriptor() != null) {
			return status.isAvailable(changeSetGuid);
		}
		return true;
	}

	/**
	 * Validates the import field value.
	 * 
	 * @param fieldValue the value of the cell
	 * @param persistenceObject the persistable entity object
	 * @param importField the import field
	 */
	protected void checkField(final String fieldValue, final Persistable persistenceObject, final ImportField importField) {
		if (importField.isCatalogObject()) {
			((CatalogImportField) importField).setCatalog(getImportJob().getCatalog());
		}
		importField.checkStringValue(persistenceObject, fieldValue, getImportGuidHelper());
	}


	private Persistable getPersistableObject(final Entity entity) {
		Persistable persistenceObject = null;
		if (importDataType.isValueObjectImport()) {
			// Create a new value object
			persistenceObject = importDataType.createValueObject();
		} else {
			persistenceObject = entity;
		}
		return persistenceObject;
	}

	/**
	 * Loads the entity represented by the given GUID.
	 * Calls the abstract method {@link #findEntityByGuid(String)}.
	 * @param guid the guid
	 * @return the Entity, or null if it could not be found (or if the given GUID was empty).
	 */
	protected Entity loadEntityByGuid(final String guid) {
		Entity entity = null;
		// Load the entity if the guid is given
		if (guid != null && guid.length() > 0) {
			entity = findEntityByGuid(guid);
		}
		return entity;
	}

	/**
	 * Read and return the guid field from the import file.
	 * @param nextLine the import file line to read the guid from.
	 * @return the guid.
	 */
	protected String readGuid(final String[] nextLine) {
		// Get guid
		String guid = null;
		if (guidColNumber != null) {
			guid = nextLine[guidColNumber.intValue()];
		}
		return guid;
	}

	/**
	 * Creates and returns an import fault error.
	 * 
	 * @return an import fault error
	 */
	protected ImportFault getImportFaultError() {
		final ImportFault importFault = getBean(ContextIdNames.IMPORT_FAULT);
		importFault.setLevel(ImportFault.ERROR);
		return importFault;
	}

	/**
	 * Creates and returns an import fault warning.
	 * 
	 * @return an import fault warning
	 */
	protected ImportFault getImportFaultWarning() {
		final ImportFault importFault = getBean(ContextIdNames.IMPORT_FAULT);
		importFault.setLevel(ImportFault.WARNING);
		return importFault;
	}

	/**
	 * Sets the utility.
	 * 
	 * @param utility the utility to set
	 */
	public void setUtility(final Utility utility) {
		this.utility = utility;
	}

	/**
	 * Returns the utility.
	 * 
	 * @return the utility
	 */
	public Utility getUtility() {
		return this.utility;
	}

	/**
	 * Run an import job.
	 */
	@SuppressWarnings({ "PMD.CyclomaticComplexity" })
	public void run() {
	    prepareAuditData();
		prepareChangeSetProcessing();
		
		final CsvFileReader csvFileReader = getCsvFileReader();

		// skip the title line
		String[] nextLine = csvFileReader.readNext();

		int rowNumber = 0;
		final int commitUnit = getCommitUnit();

		PersistenceSession session = null;
		Transaction transaction = null;

		int startRowNumber = -1;
		int endRowNumber = -1;
		ImportJobState finalImportJobState = ImportJobState.FINISHED;

		for (List<String[]> rows = csvFileReader.getTopLines(commitUnit); !rows.isEmpty(); rows = csvFileReader.getTopLines(commitUnit)) {

			startRowNumber = rowNumber + 1;
			endRowNumber = startRowNumber + rows.size();

			try {
				session = getPersistenceEngine().getSharedPersistenceSession();
				
				// Tell the subclass we are starting a new commit unit of work.
				preCommitUnitTransactionCreate();
				transaction = session.beginTransaction();
				
				// Tell the persistence engine to batch mode for this transaction
				if (getPersistenceEngine().isCacheEnabled()) {
					getPersistenceEngine().setLargeTransaction(true);
				}
				
				for (int i = 0; i < rows.size(); i++) {
					nextLine = rows.get(i);
					rowNumber++;
					importJobStatusHandler.reportCurrentRow(importJobProcessId, rowNumber);
					importOneRow(nextLine, session);
				}

				transaction.commit();
				postCommitUnitTransactionCommit();

				if (importJobStatusHandler.isImportJobCancelled(importJobProcessId)) {
					finalImportJobState = ImportJobState.CANCELLED;
					break;
				}

			} catch (Exception e) {

				ImportBadRow badRow = logBadRow(request.getImportSource(), nextLine, rowNumber, startRowNumber, endRowNumber, e);
				if (transaction != null) {
					try {
						transaction.rollback();
					} catch (Exception ee) {
						// should log and handle errors as normal below.
						LOG.error("Exception during commit or rollback.", ee);
					}
					// Tell the sub-class that the commit unit has been rolled 
					// back  
					postCommitUnitTransactionRollback();
				} 
				// Forward to the import area end
				rowNumber = endRowNumber - 1;
				importJobStatusHandler.reportBadRows(importJobProcessId, badRow);
				importJobStatusHandler.reportCurrentRow(importJobProcessId, rowNumber);
				importJobStatusHandler.reportFailedRows(importJobProcessId, endRowNumber - startRowNumber);
				// Break if reach max allow errors
				if (!importJobStatusHandler.verifyImportJobFailedRows(importJobProcessId, request.getMaxAllowedFailedRows())) {
					finalImportJobState = ImportJobState.FAILED;
					break;
				}

			} finally {
				if (session != null) {
					session.close();
				}
			}
		}

		csvFileReader.close();
		importJobStatusHandler.reportImportJobState(importJobProcessId, finalImportJobState);
		ImportJobStatus importJobStatus = importJobStatusHandler.getImportJobStatus(importJobProcessId);
		postHandlings(importJobStatus, getInitiator(), getLocale());
	}

	/**
	 * Adds the metadata for enabling the change set processing by the change set persistable listener.
	 */
	protected void prepareChangeSetProcessing() {
		if (isChangeSetEnabled()) {
			// enable change set persistable listener
			getPersistenceListenerMetadataMap().put("changeSetGuid", getRequest().getChangeSetGuid());
			getPersistenceListenerMetadataMap().put("changeSetUserGuid", getRequest().getInitiator().getGuid());
			getPersistenceListenerMetadataMap().put("activeImportStage", "stage2");
			getPersistenceListenerMetadataMap().put("addToChangeSetFlag", "true");
		}
	}

	/**
	 * Adds the metadata for auditing.
	 */
    protected void prepareAuditData() {
        getPersistenceListenerMetadataMap().put("userGuid", getRequest().getInitiator().getGuid());
    }

    /**
     * Checks if the change set feature is enabled. For performance reasons the value is cached so that no additional SQL queries are performed. The
     * import job runner is a bean of type 'prototype' which means that this value will be cached per import job run.
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
	 * Gets the initiator of the import.
	 * 
	 * @return the initiator
	 */
	protected CmUser getInitiator() {
		return request.getInitiator();
	}

	/**
	 * The reporting locale.
	 * 
	 * @return the locale
	 */
	protected Locale getLocale() {
		return request.getReportingLocale();
	}
	
	/**
	 * Initialise this runner.
	 * 
	 * @param request the import job request
	 * @param importJobProcessId the import job process GUID
	 */
	public void init(final ImportJobRequest request, final String importJobProcessId) {
		if (request == null) {
			throw new IllegalArgumentException("The request object parameter is required");
		}
		if (importJobProcessId == null) {
			throw new IllegalArgumentException("The import job process GUID is required");
		}
		this.request = request;
		this.importJobProcessId = importJobProcessId;
		setImportJob(request.getImportJob());
	}

	/**
	 * Gets the import job request.
	 * 
	 * @return the request the request
	 */
	protected ImportJobRequest getRequest() {
		return request;
	}

	/**
	 * Load the catalog object - needs to be done within the transaction boundary for JPA.
	 * 
	 * @param session the persistence session
	 * @return the <code>Catalog</code>
	 */
	protected Catalog loadCatalogObject(final PersistenceSession session) {
		Query query = session.createNamedQuery("FIND_CATALOG_BY_UID");
		Catalog importIntoCatalog = importJob.getCatalog();
		query.setParameter(1, importIntoCatalog.getUidPk());

		final List<Catalog> results = query.list();

		if (results != null && !results.isEmpty()) {
			return results.get(0);
		}
		return null;
	}

	/**
	 * Returns the warehouse object from importJob - does not need to be done within the transaction boundary for JPA since object it retrieved from
	 * importJob.
	 * 
	 * @return the <code>Warehouse</code>
	 */
	protected Warehouse getWarehouseObject() {
		return importJob.getWarehouse();
	}
	
	private ImportBadRow logBadRow(final String csvFileName, final String[] nextLine, final int rowNumber, final int startRowNumber, 
			final int endRowNumber,	final Exception exception) {		
		LOG.error("Unexpected Import Error happened in csv file -- " + csvFileName + ", at line -- " + rowNumber);
		LOG.error("Start row number -- " + startRowNumber + ", End row number -- " + endRowNumber);
		//LOG.error("Raw data : " + nextLine[rowNumber]); //OutOfBounds 
		LOG.error("Cause : ", exception);

		final ImportBadRow importBadRow = getBean(ContextIdNames.IMPORT_BAD_ROW);
		importBadRow.setRowNumber(rowNumber);
		importBadRow.setRow(nextLine[0]);

		final ImportFault importFault = getImportFaultError();
		if (exception instanceof EpProductInUseException) {
			importFault.setCode("import_csvFile_badRow_productCannotBeDeleted");
			importFault.setArgs(new Object[] {rowNumber});
		} else {
			importFault.setCode("import.unexpected.error");
			importFault.setSource(exception.getMessage());
			importFault.setArgs(new Object[] {exception.getLocalizedMessage()});
		}

		importBadRow.addImportFault(importFault);
		return importBadRow;
	}

	/**
	 * Import a single row using the specified persistence session.
	 * Calls {@link #insertAndUpdate(String[], PersistenceSession, String, Entity)},
	 * {@link #update(String[], PersistenceSession, String, Entity)},
	 * {@link #delete(PersistenceSession, String, Entity)},
	 * {@link #insert(String[], PersistenceSession, String, Entity)}.
	 * Calls {@link #readGuid(String[])} to determine the Entity's GUID.
	 * 
	 * @param nextLine the input row to import.
	 * @param session the session to do persistable through.
	 * @throws EpServiceException if there is a problem importing the row.
	 */
	protected void importOneRow(final String[] nextLine, final PersistenceSession session) throws EpServiceException {

		final String guid = readGuid(nextLine);
		Entity entity = loadEntityByGuid(guid);
		ImportType importType = this.getRequest().getImportType();
		if (importType.equals(AbstractImportTypeImpl.UPDATE_TYPE)) {
			entity = update(nextLine, session, guid, entity);
		}
		if (importType.equals(AbstractImportTypeImpl.INSERT_TYPE)) {
			entity = insert(nextLine, session, guid, entity);
		}
		if (importType.equals(AbstractImportTypeImpl.INSERT_UPDATE_TYPE)) {
			entity = insertAndUpdate(nextLine, session, guid, entity);
		}
		if (importType.equals(AbstractImportTypeImpl.DELETE_TYPE)) {
			delete(session, guid, entity);
		}
	}

	/**
	 * Deletes the given Entity and logs the given GUID (which is assumed to be the Entity's GUID).
	 * Before the entity is deleted, all of its value objects are cleared with a call to
	 * {@link #clearValueObjectsFromEntity(Entity)}.
	 * The actual deletion is handled by a call to the Entity's specific handler via 
	 * {@link ImportDataType#deleteEntity(Entity)}.
	 * Calls {@link #recordImportedEntityGuid(String)}, 
	 * and {@link #saveEntity(PersistenceSession, Entity, Persistable)} to persist the Entity.
	 * @param session the persistence session to use
	 * @param guid the GUID of the entity to be recorded
	 * @param entity the entity to be deleted
	 */
	protected void delete(final PersistenceSession session, final String guid, final Entity entity) {
		if (entity == null) {
			// Report an error if it's this import data type is importing
			// value objects and the given guid is not associated to an entity yet.
			// throw new EpServiceException(CAN_NOT_FIND_MESSAGE + guid);
			// It Is not necessary because, what if user cancel delete and want to continue? 
			return;
		}
		final Persistable persistenceObject = getPersistableObject(entity);
		if (importDataType.isValueObjectImport() && !isEntityAlreadyImported(guid)) {
			clearValueObjectsFromEntity(entity);
			recordImportedEntityGuid(guid);
			saveEntity(session, entity, persistenceObject);
		}
		importDataType.deleteEntity(entity);
	}

	/**
	 * Creates and inserts the given Entity as a new record, recording the given GUID 
	 * (assumes that it's the Entity's GUID).
	 * Calls {@link #updateContent(String[], Persistable)} to populate the Entity with its fields,
	 * and {@link #saveEntity(PersistenceSession, Entity, Persistable)} to persist the Entity.
	 * @param nextLine the array of Entity fields
	 * @param session the persistence session to use
	 * @param guid the GUID of the Entity
	 * @param entity the Entity
	 * @throws EpServiceException if an entity with the given GUID already exists, or if the specific handler 
	 * for the object being imported (the {@link ImportDataType}) is not an Entity
	 * @return the Entity that was inserted
	 */
	protected Entity insert(final String[] nextLine, final PersistenceSession session, final String guid, final Entity entity) {

		if (entity != null) {
			throw new EpServiceException("There is already entity with the given guid:" + guid);
		}
		Entity newEntity = null;
		if (importDataType.isEntityImport()) {
			// New an entity if it's this import data type is importing
			// entity.
			newEntity = createNewEntity(baseObject);
		} else {
			// Report an error if it's this import data type is importing
			// value objects
			// and the given guid is not associated to an entity yet.
			throw new EpServiceException(CAN_NOT_FIND_MESSAGE + guid);
		}
		final Persistable persistenceObject = getPersistableObject(newEntity);

		updateContent(nextLine, persistenceObject);
		saveEntity(session, newEntity, persistenceObject);
		return newEntity;
	}

	/**
	 * Updates the record for the given Entity in the persistence layer, and records the given GUID (assumes
	 * that it's the Entity's GUID) as being modified during the current import session.
	 * Calls {@link #updateContent(String[], Persistable)} to populate the Entity with its fields,
	 * and {@link #saveEntity(PersistenceSession, Entity, Persistable)} to persist the Entity.
	 * @param nextLine the array of the Entity's fields
	 * @param session the persistence session to use
	 * @param guid the Entity's GUID
	 * @param entity the Entity to update
	 * @throws EpServiceException if the given Entity is null.
	 * @return the updated Entity
	 */
	protected Entity update(final String[] nextLine, final PersistenceSession session, final String guid, final Entity entity) {
		if (entity == null) {			
			throw new EpServiceException(CAN_NOT_UPDATE_MESSAGE);
		}
		final Persistable persistenceObject = getPersistableObject(entity);

		if (importDataType.isValueObjectImport() && !isEntityAlreadyImported(guid)) {
			clearValueObjectsFromEntity(entity);
			recordImportedEntityGuid(guid);
		}

		updateContent(nextLine, persistenceObject);
		saveEntity(session, entity, persistenceObject);
		return entity;
	}
	
	/**
	 * Inserts the given Entity into the persistence layer or, if an Entity with the same GUID already exists, then
	 * updates it with the fields in the given array. Records the given GUID as the Entity's GUID.
	 * If the current {@link ImportDataType} signifies that it's a {@code ValueObject} being imported,
	 * then unless other {@code ValueObject}s for the Entity represented by the same GUID are being imported during this
	 * Import session all value objects will first be cleared from the given Entity before it's updated with the
	 * fields specified.
	 * 
	 * @param nextLine the array of entity fields
	 * @param session the persistence session to use
	 * @param guid the entity's GUID. If a new Entity is created, this will become its GUID,
	 * @param entity the Entity to import. If null, a new Entity will be created.
	 * @throws EpServiceException if the current {@link ImportDataType} is not an Entity.
	 * @return the persisted Entity
	 */
	protected Entity insertAndUpdate(final String[] nextLine, final PersistenceSession session, final String guid, final Entity entity) {
		if (entity == null) {
			Entity newEntity = null;
			if (importDataType.isEntityImport()) {
				// New an entity if it's this import data type is importing
				// entity.
				newEntity = createNewEntity(baseObject);
			} else {
				// Report an error if it's this import data type is
				// importing value objects
				// and the given guid is not associated to an entity yet.
				throw new EpServiceException(CAN_NOT_FIND_MESSAGE + guid);
			}
			final Persistable persistenceObject = getPersistableObject(newEntity);

			if (importDataType.isValueObjectImport() && !isEntityAlreadyImported(guid)) {
				// If it's replacing import, remove value objects at the first
				// time
				clearValueObjectsFromEntity(newEntity);
				recordImportedEntityGuid(guid);
			}

			updateContent(nextLine, persistenceObject);
			saveEntity(session, newEntity, persistenceObject);
			return newEntity;
		}
		final Persistable persistenceObject = getPersistableObject(entity);

		if (importDataType.isValueObjectImport() && !this.isEntityAlreadyImported(guid)) {
			clearValueObjectsFromEntity(entity);
			recordImportedEntityGuid(guid);
		}

		updateContent(nextLine, persistenceObject);
		saveEntity(session, entity, persistenceObject);
		return entity;
	}

	/**
	 * <p>Clears the value objects (pertaining to the object being imported) 
	 * from an Entity by calling importDataType.clearValueObjects().</p>
	 * 
	 * <p>This method may be overridden by subclasses that need to do something
	 * special before clearing the value objects (e.g. clear the value objects
	 * only pertaining to the catalog into which the data is being imported).</p>
	 *  
	 * @param entity the entity to which the value objects are attached
	 */
	protected void clearValueObjectsFromEntity(final Entity entity) {
		importDataType.clearValueObjects(entity);
	}

	/**
	 * Save entity.
	 * 
	 * @param session session.
	 * @param entity entity for save.
	 * @param persistenceObject persistable object.
	 * @return the entity after being updated/saved.
	 */
	protected Entity saveEntity(final PersistenceSession session, final Entity entity, final Persistable persistenceObject) {
		if (importDataType.isValueObjectImport()) {
			// Associate the value object with its entity.
			importDataType.saveOrUpdate(entity, persistenceObject);
		}

		updateEntityBeforeSave(entity);
		return saveEntityHelper(session, entity);
	}
	
	/**
	 * Called by saveEntity(). Don't call directly.
	 * This method exists so that subclasses can override it.
	 * 
	 * @param session session.
	 * @param entity entity for save.
	 * @return the entity after being updated/saved.
	 */
	protected Entity saveEntityHelper(final PersistenceSession session, final Entity entity) {
		if (entity.isPersisted()) {
			return session.update(entity);
		}
		
		session.save(entity);
		return entity;
	}
	
	/**
	 * Populate entity.
	 * 
	 * @param nextLine next line.
	 * @param persistenceObject persistent Object.
	 */
	protected void updateContent(final String[] nextLine, final Persistable persistenceObject) {
		for (Entry<ImportField, Integer> entry : mappings.entrySet()) {
			final ImportField importField = entry.getKey();
			// this has to happen before, otherwise
			final Integer colNum = entry.getValue();
			if (importField.isCatalogObject()) {
				((CatalogImportField) importField).setCatalog(importJob.getCatalog());
			}
			importField.setStringValue(persistenceObject, nextLine[colNum.intValue()], importGuidHelper);
		}
	}

	/**
	 * Performs post handlings after the import job.
	 * 
	 * @param runningJob the running import job
	 * @param cmUser the user
	 * @param locale the locale to use 
	 */
	protected void postHandlings(final ImportJobStatus runningJob, final CmUser cmUser, final Locale locale) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Enter post handlings...");
			LOG.debug("Total rows:" + runningJob.getTotalRows());
			LOG.debug("Current row:" + runningJob.getCurrentRow());
			LOG.debug("Failed rows:" + runningJob.getFailedRows());
			LOG.debug("Left rows:" + runningJob.getLeftRows());
		}

		final String idStr = runningJob.getImportJob().getName() + DateUtils.toFormattedString(new Date());

		// Write leftover rows to a file
		writeLeftOver(runningJob, idStr);
		
		// If change sets are used we need to update the metadata (e.g. object name) here
		// since we can't do it until after the objects are imported.
		if (isChangeSetEnabled()) {
			getChangeSetService().updateResolvedMetadata(request.getChangeSetGuid());
		}
	}

	private void writeLeftOver(final ImportJobStatus runningJob, final String idstr) {
		final List<Integer> leftOverRows = retriveLeftOver(runningJob);

		// Do nothing if there is no leftover
		if (leftOverRows.isEmpty()) {
			return;
		}

		final StringBuffer sbf = new StringBuffer();
		sbf.append(getElasticPath().getWebInfPath()).append(File.separator).append(ImportConstants.IMPORT_DIRECTORY_NAME).append(File.separator);
		new File(sbf.toString()).mkdirs();
		sbf.append(idstr).append(ImportConstants.IMPORT_LEFT_OVER_SUFFIX);
		final String fileName = sbf.toString();

		final PrintWriter printWriter = getBean(ContextIdNames.PRINT_WRITER);
		printWriter.open(fileName);
		final CsvFileReader csvFileReader = getCsvFileReader();

		// Output the title line
		int rowNumber = 0;
		String[] nextLine = csvFileReader.readNext();
		printWriter.println(nextLine[0]);

		final Iterator<Integer> leftOverIterator = leftOverRows.iterator();
		Integer nextLeftOverRowNum = leftOverIterator.next();

		while ((nextLine = csvFileReader.readNext()) != null) {
			rowNumber++;

			if (rowNumber == nextLeftOverRowNum.intValue()) {
				printWriter.println(nextLine[0]);
				if (!leftOverIterator.hasNext()) {
					break;
				}
				nextLeftOverRowNum = leftOverIterator.next();
			}
		}
		csvFileReader.close();
		printWriter.close();
	}

	private List<Integer> retriveLeftOver(final ImportJobStatus runningJob) {
		final List<Integer> leftOver = new ArrayList<Integer>();

		for (int j = runningJob.getCurrentRow() + 1; j <= runningJob.getTotalRows(); j++) {
			leftOver.add(Integer.valueOf(j));
		}

		return leftOver;
	}

	/**
	 * Initialize the import job runner.
	 */
	protected void init() {
		this.importGuidHelper = getBean("importGuidHelper");
	}

	/**
	 * Returns the csv file reader.
	 * 
	 * @return the csv file reader
	 */
	protected CsvFileReader getCsvFileReader() {
		final CsvFileReader csvFileReader = getBean(ContextIdNames.CSV_FILE_READER);
		csvFileReader.open(request.getImportSource(), 
				request.getImportSourceColDelimiter(), 
				request.getImportSourceTextQualifier());
		return csvFileReader;
	}
	
	@Override
	public int getTotalRows() {
		CsvFileReader reader = getCsvFileReader();
		int totalRows;
		try {
			totalRows = reader.getTotalRows();
		} finally {
			reader.close();
		}
		return totalRows;
	}

	private Map<ImportField, Integer> createMappings(final ImportDataType importDataType) {
		// Generate mappings from import fields to column numbers
		final Map<ImportField, Integer> mappings = new LinkedHashMap<ImportField, Integer>();
		importService.initImportDataTypeLocalesAndCurrencies(importDataType, importJob);
		Map<String, Integer> importJobMappings = importJob.getMappings();
		for (Map.Entry<String, ImportField> importField : importDataType.getImportFields().entrySet()) {
			final String fieldName = importField.getKey();
			final Integer colNum = importJobMappings.get(fieldName);
			if (colNum != null) {
				mappings.put(importField.getValue(), colNum);
			}
		}
		return mappings;
	}

	/**
	 * Set the import job service.
	 * 
	 * @param importService the import job service to set
	 */
	public void setImportService(final ImportService importService) {
		this.importService = importService;
	}

	/**
	 * Returns the embedded import job service.
	 * 
	 * @return the embedded import job service
	 */
	protected ImportService getImportService() {
		return this.importService;
	}

	/**
	 * Not used.
	 * 
	 * @param uid not used.
	 * @return nothing.
	 * @throws EpServiceException - in case of called
	 */
	public Object getObject(final long uid) throws EpServiceException {
		throw new EpServiceException("Should not be called.");
	}

	/**
	 * Sets the import job.
	 * 
	 * @param importJob the import job to set.
	 */
	protected void setImportJob(final ImportJob importJob) {
		this.importJob = importJob;
		
		this.importDataType = getImportDataType(importJob.getImportDataTypeName());
		
		final String guidFieldName = importDataType.getGuidFieldName();
		this.guidColNumber = importJob.getMappings().get(guidFieldName);

		this.baseObject = importDataType.getMetaObject();
		this.mappings = createMappings(importDataType);
	}
	
	/**
	 * Gets the ImportDataType with the given name.
	 * @param importDataTypeName the name of the ImportDataType object
	 * @return the ImportDataType object with the given name
	 */
	protected ImportDataType getImportDataType(final String importDataTypeName) {
		return importService.findImportDataType(importDataTypeName);
	}

	/**
	 * Returns the import guid helper service.
	 * 
	 * @return the import guid helper service
	 */
	public ImportGuidHelper getImportGuidHelper() {
		return importGuidHelper;
	}

	/**
	 * Sets the import guid helper service.
	 * 
	 * @param importGuidHelper the import guid helper service
	 */
	public void setImportGuidHelper(final ImportGuidHelper importGuidHelper) {
		this.importGuidHelper = importGuidHelper;
	}

	/**
	 * Returns the guid column number.
	 * 
	 * @return the guid column number
	 */
	protected Integer getGuidColNumber() {
		return guidColNumber;
	}

	/**
	 * Returns the import job to run.
	 * 
	 * @return the import job to run
	 */
	protected ImportJob getImportJob() {
		return importJob;
	}

	/**
	 * Returns the base object of the import data type.
	 * 
	 * @return the base object of the import data type.
	 */
	protected Object getBaseObject() {
		return baseObject;
	}

	/**
	 * Returns the mappings.
	 * 
	 * @return the mappings.
	 */
	protected Map<ImportField, Integer> getMappings() {
		return mappings;
	}
	
	/**
	 * Returns the import data type.
	 * 
	 * @return the import data type
	 */
	protected ImportDataType getImportDataType() {
		return importDataType;
	}
	
	/**
	 * Record a guid as having been imported in this import session.  
	 * @param guid the guid to record.
	 */
	protected void recordImportedEntityGuid(final String guid) {
		importedEntityGuids.add(guid);
	}
	
	/**
	 * Check if a guid has been imported in this import session.
	 * @param guid the guid to check for.
	 * @return true if the guid has been imported in this import session.
	 */
	protected boolean isEntityAlreadyImported(final String guid) {
		return importedEntityGuids.contains(guid);
	}
	
	///////////////////////////
	// Below are a set of hook methods that allow implementations to do extra
	// work whilst importing data.
	///////////////////////////
	
	/**
	 * This method is called before the transaction for a commit unit is 
	 * created.
	 * 
	 * No exceptions should be thrown from this method as this would interrupt
	 * the import job.
	 */
	protected void preCommitUnitTransactionCreate() {
		// No operation
	}
	
	/**
	 * This method is called after the transaction for a commit unit has been 
	 * successfully committed.  Implementing this allows sub-classes to pass 
	 * messages to services with the knowledge that the data in the commit 
	 * unit has been successfully committed to the database. 
     *
	 * No exceptions should be thrown from this method as this would interrupt
	 * the import job.
	 */
	protected void postCommitUnitTransactionCommit() {
		// No operation
	}

	/**
	 * This method is called after the transaction for a commit unit has been 
	 * rolled back due to some problem committing the transaction.
	 * 
	 * No exceptions should be thrown from this method as this would interrupt
	 * the import job. 
	 */
	protected void postCommitUnitTransactionRollback() {
		// No operation
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

	/**
	 *
	 * @return the importJobProcessId
	 */
	public String getImportJobProcessId() {
		return importJobProcessId;
	}

	/**
	 *
	 * @return the changeSetService
	 */
	public ChangeSetService getChangeSetService() {
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
	 * @param metadataMap the persistable listener metadata map
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

	/**
	 * @return the timeService
	 */
	protected TimeService getTimeService() {
		return timeService;
	}

	/**
	 * @param timeService the timeService to set
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}
}
