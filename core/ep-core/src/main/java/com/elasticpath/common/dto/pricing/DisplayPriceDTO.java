package com.elasticpath.common.dto.pricing;

import java.io.Serializable;
import java.math.BigDecimal;

import com.elasticpath.domain.misc.Money;

/**
 * DTO for displaying a price of a certain price tier and price list.
 * Used as a read-only object.
 */
public interface DisplayPriceDTO extends Serializable {

	/**
	 * @return the price list name
	 */
	String getPriceListName();

	/**
	 * @return the price list guid
	 */
	String getPriceListGuid();

	/**
	 * @return the guid of object (e.g. SKU)
	 */
	String getObjectGuid();

	/**
	 * @return the type of object (e.g. SKU, Product)
	 */
	String getObjectType();

	/**
	 * @return the list price
	 */
	BigDecimal getListPrice();

	/**
	 * @return the price tier
	 */
	BigDecimal getQuantity();

	/**
	 * @return the sale price
	 */
	BigDecimal getSalePrice();

	/**
	 * @return the lowest from sale price and promoted price, or sale price
	 */
	Money getLowestPrice();
	
	/**
	 * Sets the lowest price.
	 * @param lowestPrice the lowest price
	 */
	void setLowestPrice(Money lowestPrice);
}