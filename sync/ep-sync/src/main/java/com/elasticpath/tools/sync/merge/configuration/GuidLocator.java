package com.elasticpath.tools.sync.merge.configuration;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * Interface for locating GUIDs.
 */
public interface GuidLocator {

	/**
	 * Retrieves guid for the given object.
	 * 
	 * @param object the given object
	 * @return guid
	 * @throws SyncToolConfigurationException if configuration provided insufficient to resolve Guid information for the specified object
	 */
	String locateGuid(final Persistable object)
			throws SyncToolConfigurationException;

	/**
	 * Checks if guid can be supplied for any object of a given class.
	 * 
	 * @param clazz Class to check
	 * @return true if guid is accessible
	 */
	boolean canQualifyByGuid(final Class< ? > clazz);

}