/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;


/**
 * Resolves metadata for promotions objects.
 *
 * @since 6.2.2
 */
public class PromotionMetadataResolver extends AbstractNamedQueryMetadataResolverImpl {

	/**
	 * This resolver is only valid for "Promotion" objects.
	 *
	 * @param objectType the type of object being resolved
	 * @return true if this resolver is valid
	 */
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "Promotion".equals(objectType);
	}

	@Override
	protected String getNamedQueryForObjectName() {
		return "PROMOTION_NAME_BY_GUID";
	}

}
