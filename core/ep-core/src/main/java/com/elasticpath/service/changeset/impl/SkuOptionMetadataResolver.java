/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.service.changeset.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * Resolves metadata for sku options objects.
 */
public class SkuOptionMetadataResolver extends AbstractMetadataResolverImpl {
	
	private SkuOptionService skuOptionService;
	
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "Sku Option".equals(objectType);
	}

	@Override
	public Map<String, String> resolveMetaDataInternal(final BusinessObjectDescriptor objectDescriptor) {
		Map<String, String> metadata = new HashMap<String, String>();
		SkuOption skuOptionValue = getSkuOptionService().findByKey(objectDescriptor.getObjectIdentifier());
		if (skuOptionValue == null) {
			metadata.put("objectName", objectDescriptor.getObjectIdentifier());
		} else {
			String displayName = skuOptionValue.getDisplayName(skuOptionValue.getCatalog().getDefaultLocale(), true);
			metadata.put("objectName", displayName);
		}
		
		return metadata;
	}

	/**
	 *
	 * @return the skuOptionService
	 */
	public SkuOptionService getSkuOptionService() {
		return skuOptionService;
	}

	/**
	 *
	 * @param skuOptionService the skuOptionService to set
	 */
	public void setSkuOptionService(final SkuOptionService skuOptionService) {
		this.skuOptionService = skuOptionService;
	}

}
