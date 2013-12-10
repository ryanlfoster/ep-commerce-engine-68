/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;

/**
 * Resolves metadata for dynamic content objects.
 *
 * @since 6.2.2
 */
public class DynamicContentMetadataResolver extends AbstractNamedQueryMetadataResolverImpl {

	/**
	 * This resolver is only valid for "Dynamic Content" objects.
	 *
	 * @param objectType the type of object being resolved
	 * @return true if this resolver is valid
	 */
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "Dynamic Content".equals(objectType);
	}

	@Override
	protected String getNamedQueryForObjectName() {
		return "DYNAMIC_CONTENT_NAME_BY_GUID";
	}

}
