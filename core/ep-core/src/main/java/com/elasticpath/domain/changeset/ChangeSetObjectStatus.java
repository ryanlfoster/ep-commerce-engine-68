package com.elasticpath.domain.changeset;

import java.io.Serializable;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * Represents a ChangeSet status.
 */
public interface ChangeSetObjectStatus extends Serializable {

	/**
	 * Check to see if the object is a member of a change set.
	 * 
	 * @param changeSetGuid the GUID of the change set to be verified against
	 * @return true if the object represented by this status is a member of this change set
	 */
	boolean isMember(String changeSetGuid);
	
	/**
	 * Whether the object is available to be added to a change set.
	 * 
	 * @param changeSetGuid the GUID of the change set to be verified against
	 * @return whether the object is available to a change set
	 */
	boolean isAvailable(String changeSetGuid);

	/**
	 * Get the object descriptor.
	 * 
	 * @return object descriptor
	 */
	BusinessObjectDescriptor getObjectDescriptor();
	
	/**
	 * Is the object descriptor locked to any change set.
	 * @return true if object descriptor is locked to any change set.
	 */
	boolean isLocked();
}
