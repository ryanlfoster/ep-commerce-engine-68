/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.service.changeset.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.persistence.dao.ProductTypeDao;

/**
 * Resolves metadata for productType objects.
 */
public class ProductTypeMetadataResolver extends AbstractMetadataResolverImpl {


	private ProductTypeDao productTypeDao;

	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "ProductType".equals(objectType);
	}

	@Override
	public Map<String, String> resolveMetaDataInternal(final BusinessObjectDescriptor objectDescriptor) {
		final Map<String, String> metadata = new HashMap<String, String>();
		final ProductType productType = getProductTypeDao().findByGuid(objectDescriptor.getObjectIdentifier());
		if (productType == null) {
			metadata.put("objectName", objectDescriptor.getObjectIdentifier());
		} else {
			final String name = productType.getName();
			metadata.put("objectName", name);
		}

		return metadata;
	}

	/**
	 * Sets the productTypeDao.
	 * @param productTypeDao the productTypeDao
	 */
	public void setProductTypeDao(final ProductTypeDao productTypeDao) {
		this.productTypeDao = productTypeDao;
	}

	/**
	 *
	 * @return the productTypeDao
	 */
	public ProductTypeDao getProductTypeDao() {
		return productTypeDao;
	}
}
