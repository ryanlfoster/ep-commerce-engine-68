/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import java.math.BigDecimal;

import com.elasticpath.domain.EpDomain;

/**
 * <code>PriceTier</code> represents different price for different minimum quantity A <code>Price</code> should at least have a
 * <code>PriceTier</code> When shopping a product, if quantity is provided, <code>Price</code> should get the nearest <code>PriceTier </code>
 * with the minimum quantity equal or less than the given quantity. If no quantity is provided, the <code>Price</code> will retrieve the first
 * price tier, i.e. the one with smallest minimum quantity.
 */
public interface PriceTier extends EpDomain, Comparable<PriceTier> {

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
	 * Get the minimum quantity of the price tier.
	 *
	 * @return the minimum quantity of the price tier
	 */
	Integer getMinQtyAsInteger();

	/**
	 * Set the minimum quantity for the price tier.
	 *
	 * @param minQty the minimum quantity of the price tier
	 */
	void setMinQtyAsInteger(Integer minQty);

	/**
	 * Get the list price of the price tier.
	 *
	 * @return the list price of the price tier
	 */
	BigDecimal getListPrice();

	/**
	 * Set the list price for the price tier.  If passed in value is negative, 
	 * then listPrice is set to BigDecimal.ZERO.
	 *
	 * @param listPrice the list price of the price tier
	 */
	void setListPrice(final BigDecimal listPrice);

	/**
	 * Get the sale price of the price tier.
	 *
	 * @return the sale price of the price tier
	 */
	BigDecimal getSalePrice();

	/**
	 * Set the sale price for the price tier.  If passed in value is negative, 
	 * then salePrice is set to BigDecimal.ZERO.
	 *
	 * @param salePrice the sale price of the price tier
	 */
	void setSalePrice(final BigDecimal salePrice);

	/**
	 * Get the computed price of the price tier.
	 *
	 * @return the computed price of the price tier
	 */
	BigDecimal getComputedPrice();

	/**
	 * Set the computed price of the price tier. If a computed price already exists, the
	 * specified computed price will only be set if it is lower than the previously
	 * existing computed price. If passed in value is negative, then computedPrice is set 
	 * to BigDecimal.ZERO.
	 *
	 * @param computedPrice the computed price of the price tier
	 */
	void setComputedPrice(final BigDecimal computedPrice);

	/**
	 * Clear the computed price of the price tier, will set the computed price to null.
	 */
	void clearComputedPrice();

	/**
	 * Get the pre-promotion price to which promotions are to be applied.
	 * This is currently the lower of the sale price and the list price.
	 *
	 * @return the lower of the sale price and the list price of the price tier
	 */
	BigDecimal getPrePromotionPrice();

	/**
	 * Return the lowest of sale or list price, or null if neither is set.
	 * @return the lowest price, or null.
	 */
    BigDecimal getLowestPrice();
    
    /**
     * Gets the priceListGuid associated with this price tier.
     * @return the priceListGuid.
     */
	String getPriceListGuid();

	/**
	 * Sets the priceListGuid for this price tier.
	 * @param priceListGuid the priceListGuid of the price tier.
	 */
	void setPriceListGuid(String priceListGuid);
}
