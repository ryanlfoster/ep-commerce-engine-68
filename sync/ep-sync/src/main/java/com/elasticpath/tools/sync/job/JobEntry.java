package com.elasticpath.tools.sync.job;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.processing.SerializableObject;


/**
 * Extends <code>JobDescriptorEntry</code> with source entity data. The entity is null for REMOVE command.
 */
public interface JobEntry extends TransactionJobDescriptorEntry, SerializableObject {

	/**
	 * Gets source persistable for ADD/UPDATE command.
	 * 
	 * @return source Persistable
	 */
	Persistable getSourceObject();
	
	/**
	 * Sets source persistable for ADD/UPDATE command.
	 * 
	 * @param sourceObject source Persistable
	 */
	void setSourceObject(Persistable sourceObject);
	
	/**
	 * Gets the parent transaction job unit name.
	 * 
	 * @return the transaction job unit name
	 */
	String getTransactionJobUnitName();
	
	/**
	 * Sets the parent's name of this entry.
	 * 
	 * @param name the transaction job unit name
	 */
	void setTransactionJobUnitName(String name);
}
