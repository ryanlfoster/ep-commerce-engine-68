/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.service.changeset.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * Resolves metadata for sku option value objects.
 */
public class SkuOptionValueMetadataResolver extends AbstractMetadataResolverImpl {
	
	/**
	 * Metadata sku option guid key. 
	 */
	public static final String SKU_OPTION_GUID_KEY = "SKU_OPTION_GUID_KEY";
	
	private SkuOptionService skuOptionService;
	
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "Sku Option Value".equals(objectType);
	}

	@Override
	public Map<String, String> resolveMetaDataInternal(final BusinessObjectDescriptor objectDescriptor) {
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("objectName", objectDescriptor.getObjectIdentifier());
		SkuOptionValue skuOptionValue = skuOptionService.findOptionValueByKey(objectDescriptor.getObjectIdentifier());
		metadata.put(SKU_OPTION_GUID_KEY, skuOptionValue.getSkuOption().getGuid());
		return metadata;
	}

	/**
	 * Sets the sku option service.
	 * @param skuOptionService the service 
	 */
	public void setSkuOptionService(final SkuOptionService skuOptionService) {
		this.skuOptionService = skuOptionService;
	}

}
