/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.objectgroup;

import com.elasticpath.persistence.api.Entity;

/**
 * A business object group member refers to an object 
 * of specific type by its identifier. A member is always part of a group.
 */
public interface BusinessObjectGroupMember extends Entity {

	/**
	 * Gets the group ID to which this member belongs to.
	 * 
	 * @return the group identifier
	 */
	String getGroupId();
	
	/**
	 * Returns the object type of this group member (e.g. Product, SKU, ...).
	 * 
	 * @return the object type
	 */
	String getObjectType();
	
	/**
	 * Gets the object identifier of the object referenced by this group member.
	 * 
	 * @return the object identifier
	 */
	String getObjectIdentifier();

	/**
	 *
	 * @param groupId the groupId to set
	 */
	void setGroupId(String groupId);

	/**
	 *
	 * @param objectIdentifier the objectIdentifier to set
	 */
	void setObjectIdentifier(String objectIdentifier);

	/**
	 *
	 * @param objectType the objectType to set
	 */
	void setObjectType(String objectType);
}
