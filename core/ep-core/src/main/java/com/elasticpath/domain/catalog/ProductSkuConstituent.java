/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.catalog;

/**
 * Methods that are specific to a SKU constituent.
 */
public interface ProductSkuConstituent extends ConstituentItem {

	/**
	 * Set the SKU that this constituent represents.
	 * 
	 * @param sku the {@code ProductSku}
	 */
	void setProductSku(ProductSku sku);
}
