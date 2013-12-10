/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.service.changeset.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.catalog.BrandService;

/**
 * Resolves metadata for brand objects.
 */
public class BrandMetadataResolver extends AbstractMetadataResolverImpl {

	private BrandService brandService;

	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "Brand".equals(objectType);
	}

	@Override
	public Map<String, String> resolveMetaDataInternal(final BusinessObjectDescriptor objectDescriptor) {
		final Map<String, String> metadata = new HashMap<String, String>();
		final Brand brand = getBrandService().findByCode(objectDescriptor.getObjectIdentifier());
		if (brand == null) {
			metadata.put("objectName", objectDescriptor.getObjectIdentifier());
		} else {
			final String displayName = brand.getDisplayName(brand.getCatalog().getDefaultLocale(), true);
			metadata.put("objectName", displayName);
		}

		return metadata;
	}

	/**
	 * Sets the brand service.
	 * @param brandService the brand service
	 */
	public void setBrandService(final BrandService brandService) {
		this.brandService = brandService;
	}

	/**
	 *
	 * @return the brandService
	 */
	public BrandService getBrandService() {
		return brandService;
	}

}
