package com.elasticpath.service.changeset.impl;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.changeset.ObjectGuidResolver;

/**
 * For ProductSku we want to use Sku Code not GUID.
 */
public class ProductSkuGuidResolver implements ObjectGuidResolver {

	@Override
	public String resolveGuid(final Object object) {
		ProductSku sku = (ProductSku) object;
		return sku.getSkuCode();
	}

	@Override
	public boolean isSupportedObject(final Object object) {
		return object instanceof ProductSku;
	}



}
