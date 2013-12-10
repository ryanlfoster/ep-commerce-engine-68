/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.persistence.api.PersistenceEngine;

/**
 * Abstract class for metadata resolvers which uses a named query to retrieve the object name
 * metadata for an object.
 * 
 * @since 6.2.2
 */
public abstract class AbstractNamedQueryMetadataResolverImpl extends AbstractMetadataResolverImpl {

	private PersistenceEngine persistenceEngine;
	
	private static final String NAME_KEY = "objectName";

	/**
	 * Resolve metadata by calling internal resolve method after validating that this resolver
	 * is appropriate for the given object descriptor. Calls {@code isValidResolverForObjectType()}
	 * to do the validation.
	 * 
	 * @param objectDescriptor the descriptor of the business object to resolve metadata for
	 * @return a metadata map.
	 */
	@Override
	public Map<String, String> resolveMetaDataInternal(final BusinessObjectDescriptor objectDescriptor) {
		String name = retrieveName(objectDescriptor.getObjectIdentifier());
		if (name == null) {
			return Collections.emptyMap();
		}
		Map<String, String> metaData = new HashMap<String, String>();
		metaData.put(NAME_KEY, name);
		return metaData;
	}
	
	/**
	 * Get the name using a named query.
	 * 
	 * @param identifier the identifier of the object whose name is being retrieved.
	 * @return the object's name or null if not found
	 */
	protected String retrieveName(final String identifier) {
		List<String> results = getPersistenceEngine().retrieveByNamedQuery(getNamedQueryForObjectName(), identifier);
		String name = null;
		if (!results.isEmpty()) {
			name = results.get(0);
		}
		return name;
	}

	/**
	 * Set the persistence engine.
	 * 
	 * @param persistenceEngine the persistenceEngine to set
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	/**
	 * Get the persistence engine.
	 * 
	 * @return the persistenceEngine
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	/**
	 * Get the named query used to get a name for the object resolved by this resolver.
	 * 
	 * @return the named query
	 */
	protected abstract String getNamedQueryForObjectName();
	
}
