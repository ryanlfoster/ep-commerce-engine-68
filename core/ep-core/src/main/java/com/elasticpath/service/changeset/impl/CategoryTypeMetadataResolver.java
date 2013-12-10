/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.service.changeset.impl;

import java.util.Map;

import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.catalog.CategoryTypeService;

/**
 * Resolves metadata for category type objects.
 */
public class CategoryTypeMetadataResolver extends AbstractNamedQueryMetadataResolverImpl {
	
	private CategoryTypeService categoryTypeService;
	
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "CategoryType".equals(objectType);
	}

	@Override
	protected String getNamedQueryForObjectName() {
		return "CATEGORY_TYPE_NAME_FIND_BY_GUID";
	}

	@Override
	public Map<String, String> resolveMetaDataInternal(final BusinessObjectDescriptor objectDescriptor) {
		Map<String, String> metadata = super.resolveMetaDataInternal(objectDescriptor);
		CategoryType categoryType = categoryTypeService.findByGuid(objectDescriptor.getObjectIdentifier());
		String catalogGuid = categoryType.getCatalog().getGuid();
		
		metadata.put("catalogGuid", catalogGuid);
		return metadata;
	}

	/**
	 * Sets the category service.
	 * @param categoryTypeService the category service
	 */
	public void setCategoryTypeService(final CategoryTypeService categoryTypeService) {
		this.categoryTypeService = categoryTypeService;
	}

}
