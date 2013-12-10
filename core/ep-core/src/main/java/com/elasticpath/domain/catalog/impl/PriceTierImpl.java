/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;

/**
 * <code>PriceTier</code> represents different price for different minimum quantity A <code>Price</code> should at least have a
 * <code>PriceTier</code> When shopping a product, if quantity is provided, <code>Price</code> should get the nearest <code>PriceTier </code>
 * with the minimum quantity equal or less than the given quantity. If no quantity is provided, the <code>Price</code> will retrieve the first
 * price tier, i.e. the one with smallest minimum quantity.
 */
public class PriceTierImpl extends AbstractEpDomainImpl implements PriceTier {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final Logger LOG = Logger.getLogger(PriceTierImpl.class);

	private int minQty;

	private BigDecimal listPrice;

	private BigDecimal salePrice;

	private BigDecimal computedPrice;
	
	private String priceListGuid;

	/**
	 * Get the computed price of the price tier.
	 *
	 * @return the computed price of the price tier
	 */
	public BigDecimal getComputedPrice() {
		return computedPrice;
	}

	/**
	 * Set the computed price of the price tier. If a computed price already exists, the specified computed price will only be set if it is lower
	 * than the previously existing computed price.
	 *
	 * @param computedPrice the computed price of the price tier
	 */
	public void setComputedPrice(final BigDecimal computedPrice) {
		BigDecimal newComputedPrice = computedPrice;

		// Don't set the computed price unless it is lower than the current price
		if (this.computedPrice != null && newComputedPrice != null 
				&& this.computedPrice.compareTo(computedPrice) < 0) {
			return;
		}

		// If the given price is less than zero, set the computed price to zero
		this.computedPrice = getNonNegativePrice(computedPrice);
	}

	private BigDecimal getNonNegativePrice(final BigDecimal adjustedPrice) {
		if (adjustedPrice != null && adjustedPrice.compareTo(BigDecimal.ZERO) < 0) {
			LOG.warn("Attempt to set a negative price. Price set to zero instead.");
			return BigDecimal.ZERO;
		}
		return adjustedPrice;
	}
	
	
	/**
	 * Get the list price of the price tier.
	 *
	 * @return the list price of the price tier
	 */
	public BigDecimal getListPrice() {
		return listPrice;
	}

	/**
	 * Set the list price for the price tier.
	 *
	 * @param listPrice the list price of the price tier
	 */
	public void setListPrice(final BigDecimal listPrice) {
		// If the given price is less than zero, set the computed price to zero
		this.listPrice = getNonNegativePrice(listPrice);
	}

	/**
	 * Get the minimum quantity of the price tier.
	 *
	 * @return the minimum quantity of the price tier
	 */
	public int getMinQty() {
		return minQty;
	}

	/**
	 * Set the minimum quantity for the price tier.
	 *
	 * @param minQty the minimum quantity of the price tier
	 */
	public void setMinQty(final int minQty) {
		this.minQty = minQty;
	}

	/**
	 * Get the sale price of the price tier.
	 *
	 * @return the sale price of the price tier
	 */
	public BigDecimal getSalePrice() {
		return salePrice;
	}

	/**
	 * Set the sale price for the price tier.
	 *
	 * @param salePrice the sale price of the price tier
	 */
	public void setSalePrice(final BigDecimal salePrice) {
		// If the given price is less than zero, set the computed price to zero
		this.salePrice = getNonNegativePrice(salePrice);
	}

	/**
	 * Clear the computed price of the price tier, will set the computed price to null.
	 */
	public void clearComputedPrice() {
		this.computedPrice = null;
	}

	/**
	 * set default values for price tier. This method will be used by JUnit.
	 */
	@Override
	public void initialize() {
		setMinQty(1);
	}


	/**
	 * Get the pre-promotion price to which promotions are to be applied. This is currently the lower of the sale price and the list price.
	 *
	 * @return the lower of the sale price and the list price of the price tier
	 */
	public BigDecimal getPrePromotionPrice() {
		if (getSalePrice() == null) {
			return getListPrice();
		}
		return getSalePrice();
	}

	/**
	 * Get the minimum quantity of the price tier.
	 *
	 * @return the minimum quantity of the price tier
	 */
	public Integer getMinQtyAsInteger() {
		return Integer.valueOf(getMinQty());
	}

	/**
	 * Set the minimum quantity for the price tier.
	 *
	 * @param minQty the minimum quantity of the price tier
	 */
	public void setMinQtyAsInteger(final Integer minQty) {
		if (minQty != null) {
			setMinQty(minQty.intValue());
		}
	}
	
	@Override
	public BigDecimal getLowestPrice() {		
		BigDecimal lower = getLower(getSalePrice(), getListPrice());	    
        return getLower(lower, getComputedPrice());
	}

	private BigDecimal getLower(final BigDecimal price1, final BigDecimal price2) {
		if (price1 == null && price2 == null) {
			return null;
		} else if (price1 != null && price2 == null) {
			return price1;
		} else if (price1 == null && price2 != null) {
			return price2;
		} else if (price1.compareTo(price2) > 0) {
			return price2;
		} else {
			return price1;
		}		
	}

	/**
	 * Compare the lowestPrice against another PriceTier.
	 * @param other the PriceTier to compare against.
	 * @return -1, 0, or 1
	 */
    public int compareTo(final PriceTier other) {
        final BigDecimal lowestPrice = getLowestPrice();
        final BigDecimal otherLowestPrice = other.getLowestPrice();
        
        if (lowestPrice == null) {
            if (otherLowestPrice == null) {
                return 0;
            }
            return -1;
        }
        if (otherLowestPrice == null) {
            return 1;
        }
        
        return lowestPrice.compareTo(otherLowestPrice);
    }

    @Override
	public String getPriceListGuid() {
		return priceListGuid;
	}

	@Override
	public void setPriceListGuid(final String priceListGuid) {
		this.priceListGuid = priceListGuid;
	}
	
	
}
