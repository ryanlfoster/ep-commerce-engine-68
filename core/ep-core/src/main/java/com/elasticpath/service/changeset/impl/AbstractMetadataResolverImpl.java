/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;

import java.util.Collections;
import java.util.Map;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.changeset.BusinessObjectMetadataResolver;

/**
 * Abstract class for metadata resolvers which validates the resolver is appropriate for the
 * object type before performing metadata resolution.
 * 
 * @since 6.2.2
 */
public abstract class AbstractMetadataResolverImpl implements BusinessObjectMetadataResolver {

	/**
	 * Resolve metadata by calling internal resolve method after validating that this resolver
	 * is appropriate for the given object descriptor. Calls {@code isValidResolverForObjectType()}
	 * to do the validation.
	 * 
	 * @param objectDescriptor the descriptor of the business object to resolve metadata for
	 * @return a metadata map.
	 */
	public Map<String, String> resolveMetaData(final BusinessObjectDescriptor objectDescriptor) {
		if (!isValidResolverForObjectType(objectDescriptor.getObjectType())) {
			return Collections.emptyMap();
		}
		
		return resolveMetaDataInternal(objectDescriptor);
	}

	/**
	 * Resolve metadata for the given business object.
	 * 
	 * @param objectDescriptor the descriptor of the business object to resolve metadata for
	 * @return a metadata map.
	 */
	protected abstract Map<String, String> resolveMetaDataInternal(final BusinessObjectDescriptor objectDescriptor);

	/**
	 * Determine whether the resolver is valid for the given object type.
	 * 
	 * @param objectType the type of object being resolved
	 * @return true if this resolver is valid
	 */
	protected abstract boolean isValidResolverForObjectType(final String objectType);

}
