package com.elasticpath.service.changeset.impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.elasticpath.commons.util.CategoryGuidUtil;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.changeset.ChangeSetDependencyResolver;

/**
 * the class to resolve the change set dependent objects of product bundle.
 */
public class CategoryChangeSetDependencyResolverImpl implements
		ChangeSetDependencyResolver {

	private CategoryService categoryService;
	
	private CategoryGuidUtil categoryGuidUtil;
	
	/**
	 * Set category guid util.
	 * 
	 * @param categoryGuidUtil the category guid util
	 */
	public void setCategoryGuidUtil(final CategoryGuidUtil categoryGuidUtil) {
		this.categoryGuidUtil = categoryGuidUtil;
	}

	/**
	 * Set category service. 
	 * 
	 * @param categoryService the category service
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@Override
	public Category getObject(final BusinessObjectDescriptor businessObjectDescriptor, final Class< ? > objectClass) {
		if (Category.class.isAssignableFrom(objectClass)) {
			String categoryGuid = categoryGuidUtil.parseCategoryGuid(businessObjectDescriptor.getObjectIdentifier());
			String catalogGuid = categoryGuidUtil.parseCatalogGuid(businessObjectDescriptor.getObjectIdentifier());
			return categoryService.findByGuid(categoryGuid, catalogGuid);
		}
		return null;
	}
	
	@Override
	public Set< ? > getChangeSetDependency(final Object object) {
		if (object instanceof Category) {
			Set<Object> dependents = new LinkedHashSet<Object>(); 
			Category category = (Category) object;
			if (category.getParent() != null) {
				dependents.add(category.getParent());
			}
			
			if (category.isLinked()) {
				dependents.add(category.getMasterCategory());
			}
			dependents.add(category.getCategoryType());
			
			return dependents;
		}
		return Collections.emptySet();
	}

}
