package com.elasticpath.tools.sync.merge.configuration;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * Interface for Locating Entities.
 */
public interface EntityLocator {

	/**
	 * Retrieves Object Entity using source value.<br>
	 * Precondition: passed object should be an entity and <code>GuidRetriever</code> should handle guid derivation for the entity.
	 *
	 * @param sourceValue the source value
	 * @return Entity instance if it can be found
	 * @throws SyncToolConfigurationException in configuration is incorrect
	 */
	Persistable locatePersistence(final Persistable sourceValue) throws SyncToolConfigurationException;

	   /**
     * Retrieves Object Entity with a minimal object graph using source value.<br>
     * Precondition: passed object should be an entity and <code>GuidRetriever</code> should handle guid derivation for the entity.
     *
     * @param sourceValue the source value
     * @return Entity instance reference if it can be found. This will use an empty fetch group load tuner to get just the id field.
     * @throws SyncToolConfigurationException in configuration is incorrect
     */
    Persistable locatePersistentReference(final Persistable sourceValue) throws SyncToolConfigurationException;

    /**
     * Retrieves Object Entity with a minimal object graph using the guid and class.<br>
     *
     * @param guid the guid of object
     * @param clazz the class of entity
     * @return Entity instance reference if it can be found. This will use an empty fetch group load tuner to get just the id field.
     * @throws SyncToolConfigurationException in configuration is incorrect
     */
    Persistable locatePersistentReference(final String guid, final Class< ? > clazz) throws SyncToolConfigurationException;


	/**
	 * Retrieves Object Entity from the system using guid and entity's class.<br>
	 *
	 * @param guid the guid of object
	 * @param clazz the class of entity
	 * @return Entity instance if it can be found
	 * @throws SyncToolConfigurationException in configuration is incorrect
	 */
	Persistable locatePersistence(final String guid, final Class< ? > clazz) throws SyncToolConfigurationException;

	/**
	 * Retrieves Object Entity from the system using guid and entity's class.
	 * This method is used specifically for sorting to optimize entity retrieval speed by only loading the information needed for sorting.
	 *
	 * @param guid the guid of object
	 * @param clazz the class of entity
	 * @return Entity instance if it can be found
	 * @throws SyncToolConfigurationException in configuration is incorrect
	 */
	Persistable locatePersistenceForSorting(final String guid, final Class< ? > clazz) throws SyncToolConfigurationException;

	/**
	 * Determine if the locator is responsible for the class passed in.
	 *
	 * @param clazz the class
	 * @return true if the locator is responsible for the class
	 */
	boolean isResponsibleFor(final Class< ? > clazz);

	/**
	 * Determines if the entity exists.
	 *
	 * @param guid the guid of the object.
	 * @param clazz the class of entity.
	 * @return true if the entity exits, false otherwise.
	 * @throws SyncToolConfigurationException if configuration is incorrect.
	 */
	boolean entityExists(final String guid, final Class< ? > clazz) throws SyncToolConfigurationException;


}