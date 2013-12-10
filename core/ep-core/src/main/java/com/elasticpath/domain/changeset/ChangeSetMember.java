package com.elasticpath.domain.changeset;

import java.io.Serializable;
import java.util.Map;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * Change Set Member.
 */
public interface ChangeSetMember extends Serializable {
	
	/**
	 * Get the business descriptor.
	 * @return the instance of object descriptor
	 */
	BusinessObjectDescriptor getBusinessObjectDescriptor();
	
	
	/**
	 * The map of meta data of the business object.
	 * @return the map of meta data
	 */
	Map<String, String> getMetadata();
	
}
