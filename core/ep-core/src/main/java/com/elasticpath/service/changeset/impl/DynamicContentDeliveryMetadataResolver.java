/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;

/**
 * Resolves metadata for dynamic content delivery objects.
 *
 * @since 6.2.2
 */
public class DynamicContentDeliveryMetadataResolver extends AbstractNamedQueryMetadataResolverImpl {

	/**
	 * This resolver is only valid for "Dynamic Content Delivery" objects.
	 *
	 * @param objectType the type of object being resolved
	 * @return true if this resolver is valid
	 */
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "Dynamic Content Delivery".equals(objectType);
	}

	@Override
	protected String getNamedQueryForObjectName() {
		return "DYNAMIC_CONTENT_DELIVERY_NAME_BY_GUID";
	}

}
