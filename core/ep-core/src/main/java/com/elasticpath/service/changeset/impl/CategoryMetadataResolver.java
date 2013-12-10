/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;

import com.elasticpath.commons.util.CategoryGuidUtil;

/**
 * Resolves metadata for Category objects.
 */
public class CategoryMetadataResolver extends AbstractNamedQueryMetadataResolverImpl {

	private CategoryGuidUtil categoryGuidUtil;
	
	/**
	 * Name retrieval for category first requires parsing the category code
	 * from the identifier.
	 * 
	 * @param identifier the category identifier
	 * @return the category name
	 */
	@Override
	protected String retrieveName(final String identifier) {
		String categoryCode = getCategoryGuidUtil().parseCategoryGuid(identifier);
		return super.retrieveName(categoryCode);
	}
	
	@Override
	protected String getNamedQueryForObjectName() {
		return "CATEGORY_NAME_IN_DEFAULT_LOCALE_BY_CODE";
	}

	/**
	 * This resolver is only valid for "Category" objects.
	 * 
	 * @param objectType the type of object being resolved
	 * @return true if this resolver is valid
	 */
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "Category".equals(objectType);
	}

	/**
	 *
	 * @return the categoryGuidUtil
	 */
	protected CategoryGuidUtil getCategoryGuidUtil() {
		return categoryGuidUtil;
	}

	/**
	 *
	 * @param categoryGuidUtil the categoryGuidUtil to set
	 */
	public void setCategoryGuidUtil(final CategoryGuidUtil categoryGuidUtil) {
		this.categoryGuidUtil = categoryGuidUtil;
	}
}
