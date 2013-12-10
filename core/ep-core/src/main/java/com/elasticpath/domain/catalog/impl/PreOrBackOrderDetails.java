/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.catalog.impl;

import com.elasticpath.domain.impl.AbstractEpDomainImpl;

/**
 * Holds details of Pre-Orders and Back Orders.
 */
public class PreOrBackOrderDetails extends AbstractEpDomainImpl {

	private static final long serialVersionUID = 1L;

	private final String skuCode;
	private final int limit;
	private final int quantity;


	/**
	 *
	 * @param skuCode the code of the ProductSku these details apply to
	 * @param limit the pre or back order limit
	 * @param quantity the pre or back order quantity
	 */
	public PreOrBackOrderDetails(final String skuCode, final int limit, final int quantity) {
		super();
		this.skuCode = skuCode;
		this.limit = limit;
		this.quantity = quantity;
	}

	/**
	 * Get the code of the ProductSku.
	 *
	 * @return the skuCode
	 */
	public String getSkuCode() {
		return skuCode;
	}

	/**
	 * Get the Pre or Back Order limit.
	 *
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * Get the Pre or Back Order Quantity.
	 *
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}
}
