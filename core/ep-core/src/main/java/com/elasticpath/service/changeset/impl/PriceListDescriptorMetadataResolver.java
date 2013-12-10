/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;

/**
 * Resolves metadata for price list descriptor objects.
 *
 * @since 6.2.2
 */
public class PriceListDescriptorMetadataResolver extends AbstractNamedQueryMetadataResolverImpl {

	/**
	 * This resolver is only valid for "Price List Descriptor" objects.
	 *
	 * @param objectType the type of object being resolved
	 * @return true if this resolver is valid
	 */
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "Price List Descriptor".equals(objectType);
	}

	@Override
	protected String getNamedQueryForObjectName() {
		return "PRICE_LIST_DESCRIPTOR_NAME_BY_GUID";
	}

}
