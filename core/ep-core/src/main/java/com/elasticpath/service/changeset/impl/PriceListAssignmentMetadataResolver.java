/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;

/**
 * Resolves metadata for price list assignment objects.
 *
 * @since 6.2.2
 */
public class PriceListAssignmentMetadataResolver extends AbstractNamedQueryMetadataResolverImpl {

	/**
	 * This resolver is only valid for "Price List Assignment" objects.
	 *
	 * @param objectType the type of object being resolved
	 * @return true if this resolver is valid
	 */
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "Price List Assignment".equals(objectType);
	}

	@Override
	protected String getNamedQueryForObjectName() {
		return "PRICE_LIST_ASSIGNMENT_NAME_BY_GUID";
	}

}
