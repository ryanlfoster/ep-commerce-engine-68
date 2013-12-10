package com.elasticpath.domain.changeset;

import java.util.Collection;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * Represents a ChangeSet status.
 */
public interface ChangeSetObjectStatusMutator {

	/**
	 * Sets the change set codes.
	 * 
	 * @param changeSetGuids the change set GUIDs 
	 */
	void setChangeSetGuids(Collection<String> changeSetGuids);
	
	/**
	 * Sets the object descriptor.
	 * 
	 * @param objectDescriptor object descriptor
	 */
	void setObjectDescriptor(BusinessObjectDescriptor objectDescriptor);
}
