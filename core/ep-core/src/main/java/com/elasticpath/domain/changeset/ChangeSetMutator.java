/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.changeset;

import java.util.Collection;

/**
 * Allows for changing private data of a {@link ChangeSet}.
 */
public interface ChangeSetMutator {

	/**
	 * Sets the available member objects.
	 * 
	 * @param memberObjects the member objects collection to set
	 */
	void setMemberObjects(Collection<ChangeSetMember> memberObjects);
	
	/**
	 * Sets the object group ID.
	 * 
	 * @param objectGroupId the group ID to set
	 */
	void setObjectGroupId(String objectGroupId);
}
