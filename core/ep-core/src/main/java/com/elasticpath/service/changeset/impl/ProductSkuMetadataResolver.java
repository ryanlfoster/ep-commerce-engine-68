/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.catalog.ProductSkuService;

/**
 * Resolves metadata for ProductSku objects.
 */
public class ProductSkuMetadataResolver extends AbstractMetadataResolverImpl {

	private ProductSkuService skuService;

	private static final String NAME_KEY = "objectName";
	
	/**
	 * This resolver is only valid for "Product SKU" objects.
	 * 
	 * @param objectType the type of object being resolved
	 * @return true if this resolver is valid
	 */
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "Product SKU".equals(objectType);
	}

	/**
	 * Get the sku name metadata by retrieving the sku as the "name" will be made up of
	 * the parent product's name plus the SKU configuration, which is too complex to be
	 * able to retrieve via a named query.
	 * 
	 * @param objectDescriptor the object descriptor
	 * @return the metadata
	 */
	@Override
	protected Map<String, String> resolveMetaDataInternal(final BusinessObjectDescriptor objectDescriptor) {
		Map<String, String> metaData = new HashMap<String, String>();
		ProductSku sku = getSkuService().findBySkuCode(objectDescriptor.getObjectIdentifier());
		if (sku != null) {
			Product product = sku.getProduct();
			Locale defaultLocale = product.getMasterCatalog().getDefaultLocale();
			StringBuilder name = new StringBuilder(product.getDisplayName(defaultLocale));
			if (product.hasMultipleSkus()) {
				name.append(" - ");
				name.append(sku.getDisplayName(defaultLocale));
			}
			metaData.put(NAME_KEY, name.toString());
		}
		return metaData;
	}

	/**
	 *
	 * @param skuService the skuService to set
	 */
	public void setSkuService(final ProductSkuService skuService) {
		this.skuService = skuService;
	}

	/**
	 *
	 * @return the skuService
	 */
	public ProductSkuService getSkuService() {
		return skuService;
	}

}
