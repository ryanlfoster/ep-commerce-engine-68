package com.elasticpath.domain.changeset;

import java.util.Map;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * Allows for changing private data of a {@link ChangeSetMember}.
 */
public interface ChangeSetMemberMutator {
	/**
	 * Set the business descriptor.
	 * @param businessObjectDescriptor the instance of object descriptor
	 */
	 void setBusinessObjectDescriptor(BusinessObjectDescriptor businessObjectDescriptor);
	
	
	/**
	 * The map of meta data of the business object.
	 * @param metadataMap the map of meta data
	 */
	 void setMetadata(Map<String, String> metadataMap);
	
}
