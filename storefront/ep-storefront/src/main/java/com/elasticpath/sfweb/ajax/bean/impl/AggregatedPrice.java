package com.elasticpath.sfweb.ajax.bean.impl;

/**
 * AggregatedPrice was used as dto to transfer data to ajax json object.
 *
 */
public class AggregatedPrice {
	private int minQty;
	private String priceString;
	
	/**
	 * @return the minQty
	 */
	public int getMinQty() {
		return minQty;
	}

	/**
	 * @param minQty the minQty to set
	 */
	public void setMinQty(final int minQty) {
		this.minQty = minQty;
	}

	/**
	 * @return the priceString
	 */
	public String getPriceString() {
		return priceString;
	}

	/**
	 * @param priceString the priceString to set
	 */
	public void setPriceString(final String priceString) {
		this.priceString = priceString;
	}
		
}
