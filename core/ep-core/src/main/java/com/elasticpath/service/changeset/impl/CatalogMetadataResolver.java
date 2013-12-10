/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;

/**
 * Resolves metadata for saved condition objects.
 *
 * @since 6.2.2
 */
public class CatalogMetadataResolver extends AbstractNamedQueryMetadataResolverImpl {

	/**
	 * This resolver is only valid for "Catalog" objects.
	 *
	 * @param objectType the type of object being resolved
	 * @return true if this resolver is valid
	 */
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "Catalog".equals(objectType);
	}

	@Override
	protected String getNamedQueryForObjectName() {
		return "VIRTUAL_CATALOG_NAME_BY_GUID";
	}

}
