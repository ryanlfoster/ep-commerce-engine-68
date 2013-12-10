/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.changeset;

import java.util.Collection;
import java.util.Date;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.persistence.api.Entity;

/**
 * A change set is a holder of references to objects of the system that
 * are being changed.
 */
public interface ChangeSet extends Entity {
	
	/**
	 * Gets the change set name.
	 * 
	 * @return the name of the change set
	 */
	String getName();
	
	/**
	 * Sets the change set name.
	 * 
	 * @param name the name to set
	 */
	void setName(String name);

	/**
	 * Gets the description.
	 * 
	 * @return the description to use
	 */
	String getDescription();
	
	/**
	 * Gets the member objects of this change set.
	 * 
	 * @return the member objects
	 */
	Collection<BusinessObjectDescriptor> getMemberObjects();

	/**
	 * Gets the members of this change set.
	 * 
	 * @return the member {@link ChangeSetMember}
	 */
	Collection<ChangeSetMember> getChangeSetMembers();

	/**
	 *
	 * @param description the description to set
	 */
	void setDescription(String description);

	/**
	 * Returns the object group ID.
	 * 
	 * @return the group ID or null if it hasn't been generated and set yet.
	 */
	String getObjectGroupId();
	
	/**
	 * Get the CmUser GUID who created this {@link ChangeSet}.
	 * 
	 * @return CmUser the CmUser who created this {@link ChangeSet}.
	 */
	String getCreatedByUserGuid();

	/**
	 * Set the CmUser GUID creating this {@link ChangeSet}.
	 * 
	 * @param createdByUserGuid the CmUser GUID creating this {@link ChangeSet}.
	 */
	void setCreatedByUserGuid(final String createdByUserGuid);
	
	/**
	 * Get the date that this Change Set was created on.
	 * 
	 * @return the created date
	 */
	Date getCreatedDate();

	/**
	 * Set the date that this Change Set is created.
	 * 
	 * @param createdDate the created date
	 */
	void setCreatedDate(Date createdDate);
	
	/**
	 * Add assigned user specifying the user type.
	 * @param userGuid is the user guid
	 */
	void addAssignedUser(final String userGuid);
		
	/**
	 * Remove assigned user.
	 * @param userGuid is the user to remove
	 */
	void removeAssignedUser(final String userGuid);
	
	/**
	 * Get the change set user guids .
	 * @return a collection of user guids as strings
	 */
	Collection<String> getAssignedUserGuids();
	
	/**
	 * Gets the change set state code.
	 * 
	 * @return the state code
	 */
	ChangeSetStateCode getStateCode();
	
	/**
	 * Sets the change set state code.
	 * 
	 * @param stateCode the state code
	 */
	void setStateCode(ChangeSetStateCode stateCode);

}
