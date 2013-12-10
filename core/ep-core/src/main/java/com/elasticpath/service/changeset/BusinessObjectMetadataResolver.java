/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset;

import java.util.Map;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * Defines functionality for resolving specific metadata for a business object.
 *  
 * @since 6.2.2
 */
public interface BusinessObjectMetadataResolver {

	/**
	 * Resolve metadata for the given business object.
	 * 
	 * @param objectDescriptor the descriptor of the business object to resolve metadata for
	 * @return a metadata map.
	 */
	Map<String, String> resolveMetaData(final BusinessObjectDescriptor objectDescriptor);
	
}
