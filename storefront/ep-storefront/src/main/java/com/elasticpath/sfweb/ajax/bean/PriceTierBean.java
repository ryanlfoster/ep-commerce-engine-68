package com.elasticpath.sfweb.ajax.bean;

import java.math.BigDecimal;

/**
 * This PriceTierBean is used as a dto for ajax. 
 * @author mren
 *
 */
public interface PriceTierBean {
	/**
	 * Get the minimum quantity of the price tier.
	 *
	 * @return the minimum quantity of the price tier
	 */
	int getMinQty();
	
	/**
	 * Set the minimum quantity for the price tier.
	 *
	 * @param minQty the minimum quantity of the price tier
	 */
	void setMinQty(int minQty);
	
	/**
	 * Get the price of the price tier.
	 *
	 * @return the price of the price tier
	 */
	BigDecimal getPrice();

	/**
	 * Set the price for the price tier.
	 *
	 * @param price the list price of the price tier
	 */
	void setPrice(final BigDecimal price);
}
