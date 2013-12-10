package com.elasticpath.persistence.openjpa;

import com.elasticpath.persistence.api.Persistable;

/**
 * Package internal interface to {@code JpaPersistenceEngineImpl} to allow testing.
 */
public interface JpaPersistenceEngineInternal extends JpaPersistenceEngine {
	
	/**
	 * Begins a single operation.
	 * 
	 * @param object the object being change.
	 * @param changeType The change type.
	 */
	void beginSingleOperation(final Persistable object, final ChangeType changeType);
	
	/**
	 * Ends a single operation.
	 * @param object the object being chagned.
	 * @param changeType The change type.
	 */
	void endSingleOperation(final Persistable object, final ChangeType changeType);

}
