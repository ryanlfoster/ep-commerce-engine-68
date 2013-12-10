/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;


/**
 * Resolves metadata for Product objects.
 *
 * @since 6.2.2
 */
public class ProductMetadataResolver extends AbstractNamedQueryMetadataResolverImpl {

	/**
	 * This resolver is only valid for "Product" and "Product Bundle" objects.
	 *
	 * @param objectType the type of object being resolved
	 * @return true if this resolver is valid
	 */
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return ("Product".equals(objectType) || "Product Bundle".equals(objectType));
	}

	@Override
	protected String getNamedQueryForObjectName() {
		return "PRODUCT_NAME_IN_DEFAULT_LOCALE_BY_CODE";
	}

}
