package com.elasticpath.service.changeset.impl;

import java.util.Set;

/**
 * A configured resolver which will allow an injected named query to be run against all valid object types.
 * 
 * @see AbstractNamedQueryMetadataResolverImpl
 */
public class ConfiguredNamedQueryMetadataResolver extends AbstractNamedQueryMetadataResolverImpl {

	private String namedQuery;

	private Set<String> objectTypes;

	/**
	 * Gets the named query for this resolver.
	 * 
	 * @param namedQuery named query for this resolver
	 */
	public void setNamedQuery(final String namedQuery) {
		this.namedQuery = namedQuery;
	}

	/**
	 * Sets the object type this resolver can handle.
	 * 
	 * @param objectTypes object type this resolver can handle
	 */
	public void setObjectTypes(final Set<String> objectTypes) {
		this.objectTypes = objectTypes;
	}

	@Override
	protected String getNamedQueryForObjectName() {
		return namedQuery;
	}

	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return objectTypes.contains(objectType);
	}
}
