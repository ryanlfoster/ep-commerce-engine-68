/*
 * Copyright (c) Elastic Path Software Inc., 2009.
 */

package com.elasticpath.domain.catalog;

import java.util.List;

import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.persistence.api.Entity;

/**
 * This is a join table representation of ProductBundle and Constituents with some additional attributes attached.
 */
public interface BundleConstituent extends Entity {
	
    /**
     * @return the constituent item
     */
    ConstituentItem getConstituent();

    /**
     * Sets the constituent product.
     *
     * @param product the product to set as constituent
     */
    void setConstituent(final Product product);

    /**
     * Sets the constituent sku.
     *
     * @param productSku the sku to set as constituent
     */
    void setConstituent(final ProductSku productSku);
    
    /**
     * @return the quantity of the constituent item
     */
    Integer getQuantity();

    /**
     * Sets the quantity of the constituent item.
     *
     * @param quantity the integer quantity to be set
     */
    void setQuantity(final Integer quantity);
    
    /**
     * @param ordering the relative order of the constituent within the bundle.
     */
    void setOrdering(final Integer ordering);
    
    /**
     * @return the relative order of this constituent within the bundle.
     */
    Integer getOrdering();
    
    /**
     * @return the list of price adjustments assigned to this bundle item.
     */
    List<PriceAdjustment> getPriceAdjustments();
    
    /**
     * Adds a {@link PriceAdjustment} to this bundle item.
     * 
     * @param adjustment {@link PriceAdjustment}
     */
    void addPriceAdjustment(PriceAdjustment adjustment);
    
    /**
     * Removes given {@link PriceAdjustment} from this bundle item.
     * 
     * @param adjustment {@link PriceAdjustment}
     */
    void removePriceAdjustment(PriceAdjustment adjustment);
    
    
    /**
     * Retrieve the price adjustment for the given price list guid.
     * 
     * If no matching price adjustment is found, it will return null.
     * 
     * 
     * @param plGuid the price list guid
     * @return the matching price adjustment for the given price list guid
     */
    PriceAdjustment getPriceAdjustmentForPriceList(String plGuid);

}
