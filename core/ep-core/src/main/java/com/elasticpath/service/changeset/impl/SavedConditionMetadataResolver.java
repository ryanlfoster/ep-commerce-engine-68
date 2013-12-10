/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;

/**
 * Resolves metadata for saved condition objects.
 *
 * @since 6.2.2
 */
public class SavedConditionMetadataResolver extends AbstractNamedQueryMetadataResolverImpl {

	/**
	 * This resolver is only valid for "Saved Condition" objects.
	 *
	 * @param objectType the type of object being resolved
	 * @return true if this resolver is valid
	 */
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "Saved Condition".equals(objectType);
	}

	@Override
	protected String getNamedQueryForObjectName() {
		return "CONDITIONAL_EXPRESSION_NAME_BY_GUID";
	}

}
