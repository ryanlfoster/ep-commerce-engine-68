package com.elasticpath.service.asset.impl;

import java.util.Map;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductSkuService;

/**
 * Image discovery service for Product Skus.
 */
public class SkuImageDiscoveryServiceImpl extends AbstractImageDiscoveryServiceImpl<ProductSku> {

	private ProductSkuService productSkuService;

	@Override
	protected String getDefaultImageKey() {
		return "defaultSkuImage";
	}

	@Override
	protected Map<String, AttributeValue> getAttributeValueMap(final ProductSku sku) {
		return sku.getAttributeValueMap();
	}

	@Override
	protected String getDefaultImage(final ProductSku sku) {
		return sku.getImage();
	}

	@Override
	protected ProductSku loadByCode(final String catalogObjectCode) {
		return getProductSkuService().findBySkuCode(catalogObjectCode);
	}

	protected ProductSkuService getProductSkuService() {
		return productSkuService;
	}

	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}

}
