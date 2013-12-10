package com.elasticpath.importexport.importer.importers.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.runtime.ImportDuplicateEntityRuntimeException;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.Importer;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.persistence.api.Persistable;

/**
 * Abstract class for importer.
 *
 * @param <DTO> the DTO class
 * @param <DOMAIN> the domain class that must implement <code>Persistable</code> interface
 */
public abstract class AbstractImporterImpl<DOMAIN extends Persistable, DTO extends Dto> implements Importer<DOMAIN, DTO> {

	private static final Logger LOG = Logger.getLogger(AbstractImporterImpl.class);

	private static final int DEFAULT_COMMIT_UNIT = 100;

	private int defaultCommitUnit = DEFAULT_COMMIT_UNIT;

	private SavingStrategy<DOMAIN, DTO> savingStrategy;

	private String schemaPath;

	private ImportContext context;

	private ImportStatusHolder statusHolder;

	private Set<String> processedObjectGuids;

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<DOMAIN, DTO> savingStrategy) {
		this.context = context;
		this.savingStrategy = savingStrategy;

		if (savingStrategy == null) {
			throw new ImportRuntimeException("IE-30500");
		}
		savingStrategy.setDomainAdapter(getDomainAdapter());
		savingStrategy.setCollectionsStrategy(getCollectionsStrategy());
		setProcessedObjectGuids(new HashSet<String>());
	}

	@Override
	public boolean executeImport(final DTO object) {
		sanityCheck();
		LOG.debug("Executing import for object: " + object);
		DOMAIN persistable = findPersistentObject(object);
		setImportStatus(object);
		checkDuplicateGuids(object, persistable);

		return getSavingStrategy().populateAndSaveObject(persistable, object) != null;
	}

	/**
	 * Checks the object before import that it was not imported yet.
	 *
	 * @param object the dto to import
	 * @param persistable the concerned with dto persistable object.
	 * @throws ImportDuplicateEntityRuntimeException in case object with given guid was already imported
	 */
	protected void checkDuplicateGuids(final DTO object, final DOMAIN persistable) {
		final String dtoGuid = getDtoGuid(object);
		if (getSavingStrategy().isImportRequired(persistable)) {
			if (dtoGuid != null && getProcessedObjectGuids().contains(dtoGuid)) {
				throw new ImportDuplicateEntityRuntimeException("IE-30502", dtoGuid);
			}

			getProcessedObjectGuids().add(dtoGuid);
		}
	}

	@Override
	public List<Class<?>> getAuxiliaryJaxbClasses() {
		return Collections.emptyList();
	}

	/**
	 * Gets the guid of given DTO.
	 *
	 * @param dto the data transfer object
	 * @return the dto guid
	 */
	protected abstract String getDtoGuid(final DTO dto);

	/**
	 * Checks saving strategy initialization.
	 *
	 * @throws ImportRuntimeException in case savingStrategy is not initialized
	 */
	protected void sanityCheck() {
		if (getSavingStrategy() == null) {
			throw new ImportRuntimeException("IE-30501");
		}
	}

	/**
	 * Gets the collections strategy for domain object, default implementation returns null so each importer that needs to use collections strategy
	 * should override this method.
	 *
	 * @return null in default implementation
	 */
	protected CollectionsStrategy<DOMAIN, DTO> getCollectionsStrategy() {
		return null;
	}

	/**
	 * Gets the appropriate domain adapter.
	 *
	 * @return the domain adapter
	 */
	protected abstract DomainAdapter<DOMAIN, DTO> getDomainAdapter();

	/**
	 * Finds persistent object by guid.
	 *
	 * @param dto the guid for object
	 * @return persistent object or null if it does not exist in database
	 */
	protected abstract DOMAIN findPersistentObject(final DTO dto);

	/**
	 * Sets the status of the import. This method need to be called when executeImport method is overridden. Usually status describes current object
	 * which is being imported.
	 *
	 * @param object dto
	 */
	protected abstract void setImportStatus(final DTO object);

	/**
	 * Gets the quantity of objects. If import of value objects occurs, appropriate importer can override this method to count imported objects
	 * correctly.
	 *
	 * @param dto the dto
	 * @return 1 in default implementation
	 */
	@Override
	public int getObjectsQty(final DTO dto) {
		return 1;
	}

	@Override
	public void postProcessingImportHandling() {
		// do nothing in default implementation
	}

	@Override
	public String getSchemaPath() {
		return schemaPath;
	}

	/**
	 * Sets the schemaPath.
	 *
	 * @param schemaPath the schemaPath to set
	 */
	public void setSchemaPath(final String schemaPath) {
		this.schemaPath = schemaPath;
	}

	@Override
	public SavingStrategy<DOMAIN, DTO> getSavingStrategy() {
		return savingStrategy;
	}

	@Override
	public void setSavingStrategy(final SavingStrategy<DOMAIN, DTO> savingStrategy) {
		this.savingStrategy = savingStrategy;
	}

	/**
	 * Sets status holder.
	 *
	 * @param statusHolder status holder
	 */
	public void setStatusHolder(final ImportStatusHolder statusHolder) {
		this.statusHolder = statusHolder;
	}

	@Override
	public ImportStatusHolder getStatusHolder() {
		return statusHolder;
	}

	/**
	 * Gets the processed object guids.
	 *
	 * @return the processed object guids
	 */
	public Set<String> getProcessedObjectGuids() {
		return processedObjectGuids;
	}

	/**
	 * Sets the processed object guids.
	 *
	 * @param processedObjectGuids the new processed object guids
	 */
	public void setProcessedObjectGuids(final Set<String> processedObjectGuids) {
		this.processedObjectGuids = processedObjectGuids;
	}

	/**
	 * Gets the import context.
	 *
	 * @return the context
	 */
	public ImportContext getContext() {
		return context;
	}

	/**
	 * Sets the commit unit size that will be used by default.
	 *
	 * @param commitUnit the commit unit
	 */
	public void setCommitUnit(final int commitUnit) {
		this.defaultCommitUnit = commitUnit;
	}

	@Override
	public int getCommitUnit() {
		return defaultCommitUnit;
	}

}
