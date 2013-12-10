package com.elasticpath.persistence.openjpa;

import com.elasticpath.persistence.api.Persistable;
import org.apache.openjpa.event.LifecycleEvent;

/**
 * Interface which receives events from the {@code JpaPersistenceEngineImpl}. <br/>
 * The contract for this listener has two scenarios: one for changes to a single object and one for
 * changes provided in a bulk update query string.
 * 
 * <h1>Single Changes</h1>
 * <ol>
 * <li>{@code beginSingleOperation} is called.</li>
 * 
 * <li>During the execution of the change then {@code eventOccurred} will be called for all of {@code beforePersist}, {@code afterPersist}, 
 * {@code beforeAttach}, {@code afterAttach}, {@code beforeDelete}, {@code afterDelete} events which the broker sends.<br/></li>
 * 
 * <li>Once the change is complete then: {@code endSingleOperation} is called</li>
 * </ol>
 */
public interface PersistenceEngineEntityListener {
	
	/**
	 * Will be called for each and every beforeAttach, beforePersist, beforeDelete, afterAttach, afterPersist and afterDelete
	 * event from the broker.
	 * 
	 * @param event The event from the broker.
	 */
	void eventOccurred(LifecycleEvent event);
	
	/**
	 * Will be called when the persistence engine is starting an operation (merge, save, delete) on a single entity.
	 * 
	 * @param object The object that will be persisted.
	 * @param type The type of change.
	 */
	void beginSingleOperation(final Persistable object, final ChangeType type);

	/**
	 * Will be called when the persistence engine is starting a bulk operation (merge, save, delete).
	 * 
	 * @param queryName the query name or null if not a named query.
	 * @param queryString the string that is being executed.
	 * @param parameters The parameters provided or null if none.
	 * @param type The type of change.
	 */
	void beginBulkOperation(final String queryName, final String queryString, final String parameters, final ChangeType type);
	
	/**
	 * Will be called when the persistence engine is ending an operation (merge, save, delete) on a single entity.
	 * 
	 * @param object The object that will be persisted.
	 * @param type The type of change.
	 */
	void endSingleOperation(final Persistable object, final ChangeType type);
	
	/**
	 * Will be called when the persistence engine is ending a bulk operation (merge, save, delete).
	 */
	void endBulkOperation();
}
