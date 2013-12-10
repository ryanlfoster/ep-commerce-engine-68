package com.elasticpath.service.changeset.impl;

/**
 * Metadata resolver for {@link com.elasticpath.domain.attribute.Attribute}.
 * Used in Change sets.
 */
public class AttributeMetadataResolver extends AbstractNamedQueryMetadataResolverImpl {

	@Override
	protected String getNamedQueryForObjectName() {
		return "ATTRIBUTE_NAME_BY_KEY";
	}

	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "Attribute".equals(objectType);
	}

}
