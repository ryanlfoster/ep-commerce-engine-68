package com.elasticpath.importexport.importer.importers;

import java.util.List;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.impl.ImportStatusHolder;
import com.elasticpath.persistence.api.Persistable;

/**
 * Interface for Importer.
 * 
 * @param <DOMAIN> {@link Persistable} class we are saving
 * @param <DTO> the DTO class
 */
public interface Importer<DOMAIN extends Persistable, DTO extends Dto> {

	/**
	 * Executes import job.
	 * 
	 * @param object DTO object for import
	 * @return true if object was actually imported
	 */
	boolean executeImport(DTO object);

	/**
	 * Initialize importer with services, import context and etc.
	 * 
	 * @param context import context to get configuration
	 * @param savingStrategy the saving strategy for importer
	 * @throws ConfigurationException in case importer can not be initialized properly
	 */
	void initialize(ImportContext context, SavingStrategy<DOMAIN, DTO> savingStrategy) throws ConfigurationException;

	/**
	 * Gets the JAXB-annotated DTO class used for importing.
	 * 
	 * @return the dto class
	 */
	Class<? extends DTO> getDtoClass();

	/**
	 * Returns a list of JAXB classes that should be passed to the
	 * {@link com.elasticpath.importexport.common.marshalling.XMLUnmarshaller XMLUmMarshaller} for use when unmarshalling the XML into the DTO.
	 *
	 * @return a list of JAXB classes (not including the DTO class returned from {@link #getDtoClass()}) to pass to the
	 * {@link com.elasticpath.importexport.common.marshalling.XMLUnmarshaller XMLUnmarshaller}.
	 * May be empty if no other classes are needed, but must not be <code>null</code>.
	 */
	List<Class<?>> getAuxiliaryJaxbClasses();

	/**
	 * Gets the savingStrategy.
	 * 
	 * @return the savingStrategy
	 */
	SavingStrategy<DOMAIN, DTO> getSavingStrategy();

	/**
	 * Sets the savingStrategy.
	 * 
	 * @param savingStrategy the savingStrategy to set
	 */
	void setSavingStrategy(SavingStrategy<DOMAIN, DTO> savingStrategy);

	/**
	 * Get the name of object served by import.
	 *
	 * @return domain object name
	 */
	String getImportedObjectName();

	/**
	 * Get the path of schema.
	 *
	 * @return schema path
	 */
	String getSchemaPath();

	/**
	 * Gets the quantity of imported objects by dto.
	 * 
	 * @param dto the dto
	 * @return qty of imported objects
	 */
	int getObjectsQty(final DTO dto);

	/**
	 * Gets the commit unit size for import operation.
	 * 
	 * @return the commit unit
	 */
	int getCommitUnit();

	/**
	 * This method calls after all objects were imported to database.
	 */
	void postProcessingImportHandling();

	/**
	 * Gets an import status holder. Holder usually contains object's code which is currently being imported.
	 * 
	 * @return status string
	 */
	ImportStatusHolder getStatusHolder();
}
