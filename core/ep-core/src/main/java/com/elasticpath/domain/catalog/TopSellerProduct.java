/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import com.elasticpath.persistence.api.Persistable;

/**
 * <code>TopSellerProduct</code> represents a top seller product.
 */
public interface TopSellerProduct extends Persistable {

	/**
	 * Get the Product Uid.
	 * @return the productUid
	 */
	long getProductUid();
	/**
	 * Set the Product Uid.
	 * @param productUid the productUid to set
	 */
	void setProductUid(final long productUid);

	/**
	 * Get the sales count.
	 * @return the salesCount
	 */
	int getSalesCount();

	/**
	 * Set the sales count.
	 * @param salesCount the salesCount to set
	 */
	void setSalesCount(final int salesCount);
}
