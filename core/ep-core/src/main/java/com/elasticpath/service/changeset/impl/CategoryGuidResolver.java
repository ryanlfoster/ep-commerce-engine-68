/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.changeset.impl;

import com.elasticpath.commons.util.CategoryGuidUtil;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.changeset.ObjectGuidResolver;

/**
 * This class resolves the combination of category guid and catalog guid. 
 */
public class CategoryGuidResolver implements ObjectGuidResolver {

	private CategoryGuidUtil categoryGuidUtil;
	
	/**
	 * combine category guid with catalog guid using "|" as delimiter.
	 * @param object the instance of category
	 * @return the combination of guid
	 */
	public String resolveGuid(final Object object) {
		if (object instanceof Category) {
			Category category = (Category) object;
			return getCategoryGuidUtil().get(category.getGuid(), category.getCatalog().getGuid());
		}
		return null;
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

	@Override
	public boolean isSupportedObject(final Object object) {
		return object instanceof Category;
	}
	
}
