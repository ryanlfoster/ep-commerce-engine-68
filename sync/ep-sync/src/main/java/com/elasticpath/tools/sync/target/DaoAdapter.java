package com.elasticpath.tools.sync.target;

import java.util.Collection;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * <code>DaoAdapter</code> delegates to appropriate remove or update methods of concrete Dao.
 * 
 * @param <E> Persistable to synchronize, e.g a Product.
 */
public interface DaoAdapter<E extends Persistable> {
	/**
	 * Updates a persistable in the target environment.
	 * 
	 * @param mergedPersistence a persistable synchronized by means of Sync Merge Engine.
	 * @return the resulted persistable
	 * @throws SyncToolRuntimeException in case if the persistable can not be updated. EpServiceException may typically be the cause
	 */
	E update(E mergedPersistence) throws SyncToolRuntimeException;
	
	/**
	 * Add a persistable to the target environment.
	 * 
	 * @param newPersistence a persistable synchronized by means of Sync Merge Engine.
	 * @throws SyncToolRuntimeException in case if the persistable can not be updated. EpServiceException may typically be the cause
	 */
	void add(E newPersistence) throws SyncToolRuntimeException;

	/**
	 * Removes a persistable in the target environment.
	 * 
	 * @param guid guid to locate a persistable in target environment to remove
	 * @return true if the persistable was removed
	 * @throws SyncToolRuntimeException in case if a persistable with the specified guid doesn't exist on target environment
	 */
	boolean remove(String guid) throws SyncToolRuntimeException;

	/**
	 * Gets a persistable by guid.
	 * 
	 * @param guid the guid
	 * @return found persistable or null if not found and new one should be created.
	 */
	E get(String guid);

	/**
	 * Creates blank persistable to add.
	 * 
	 * @param bean the bean of source system
	 * @return blank bean
	 */
	E createBean(E bean);
	
	/**
	 * Gets the collection of associated types.
	 * <p>
	 * An associated type is an implicit or explicit dependency on the parent.
	 * </p>
	 * 
	 * @return priorities of associated types
	 */
	Collection<Class< ? extends Persistable>> getAssociatedTypes();
}
