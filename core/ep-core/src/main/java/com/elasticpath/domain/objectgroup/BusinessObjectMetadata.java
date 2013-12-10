/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.objectgroup;

import com.elasticpath.persistence.api.Persistable;

/**
 * A business object group member refers to an object 
 * of specific type by its identifier. A member is always part of a group.
 */
public interface BusinessObjectMetadata extends Persistable {

	/**
	 * Gets business object group member.
	 *
	 * @return the instance of business object group member
	 */
	BusinessObjectGroupMember getBusinessObjectGroupMember();
	
	
	/**
	 * Sets business object group member.
	 * @param businessObjectGroupMember the instance of business object group member
	 */
	void setBusinessObjectGroupMember(BusinessObjectGroupMember businessObjectGroupMember);
	
	
	/**
	 * Gets meta data key.
	 * @return the key of meta data
	 */
	String getMetadataKey();
	
	/**
	 * Get meta data value.
	 * @return the value of meta data
	 */
	String getMetadataValue();
	
	/**
	 * Sets meta data key.
	 *
	 * @param metadataKey the key of meta data
	 */
	void setMetadataKey(String metadataKey);
	
	/**
	 * Sets meta data value.
	 *
	 * @param metadataValue the value of meta data
	 */
	void setMetadataValue(String metadataValue);
	
	
}
