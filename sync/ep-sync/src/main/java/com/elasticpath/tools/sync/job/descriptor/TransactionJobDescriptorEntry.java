package com.elasticpath.tools.sync.job.descriptor;

import com.elasticpath.tools.sync.job.Command;
import com.elasticpath.tools.sync.processing.SerializableObject;


/**
 * A holder for operation, guid and business object type for the object under synchronization.
 */
public interface TransactionJobDescriptorEntry extends SerializableObject {

	/**
	 * @return guid of described object
	 */
	String getGuid();

	/**
	 * @param guid guid of described object
	 */
	void setGuid(final String guid);

	/**
	 * @return type of described object
	 */
	Class< ? > getType();

	/**
	 * @param type type of described object
	 */
	void setType(final Class< ? > type);

	/**
	 * @return operation on this object during synchronization
	 */
	Command getCommand();

	/**
	 * @param command operation on this object during synchronization
	 */
	void setCommand(final Command command);
}
