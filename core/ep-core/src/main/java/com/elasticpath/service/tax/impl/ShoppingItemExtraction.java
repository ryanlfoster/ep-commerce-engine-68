package com.elasticpath.service.tax.impl;

import java.math.BigDecimal;

/**
 * Shopping item attributes required by sorting algorithm.
 *
 */
public class ShoppingItemExtraction {
	private final String guid;
	private final BigDecimal amount;
	private final String skuCode;

	/**
	 * @param guid guid
	 * @param skuCode skuCode
	 * @param amount amount
	 */
	public ShoppingItemExtraction(final String guid, final String skuCode, final BigDecimal amount) {
		this.guid = guid;
		this.amount = amount;
		this.skuCode = skuCode;
	}

	/**
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @return the skuCode
	 */
	public String getSkuCode() {
		return skuCode;
	}
}