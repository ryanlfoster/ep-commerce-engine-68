/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.catalog;

import java.util.Date;
import java.util.List;

/**
 * <code>ProductBundle</code> is a <code>Product</code>.
 */
public interface ProductBundle extends Product {
	
	/**
	 * Get the start date that this product bundle will become available to customers.
	 *
	 * @return the start date
	 */
	Date getBundleStartDate();
	
	/**
	 * Get the end date. After the end date, the product bundle will change to unavailable to customers.
	 *
	 * @return the end date
	 */
	Date getBundleEndDate();
	
	/**
	 * Returns true if the product bundle should not be displayed (e.g. in its category or as a search result).
	 *
	 * @return true if the product bundle should not be displayed
	 */
	boolean isBundleHidden();
	
	/**
	 * Gets the <code>ProductBundleConstituent</code>s associated with this <code>ProductBundle</code>.
	 * 
	 * @return the list of product constituents.
	 */
	List<BundleConstituent> getConstituents();

	/**
	 * Adds a constituent item to the bundle.
	 * 
	 * @param constituent a constituent item to be added
	 */
	void addConstituent(BundleConstituent constituent);

	/**
	 * Removes a constituent item to the bundle.
	 * 
	 * @param constituent a constituent item to be removed
	 */
	void removeConstituent(BundleConstituent constituent);

	/**
	 * Moves a constituent item up one index in the bundle constituents list. Do nothing if the constituent doesn't exist.
	 * 
	 * @param constituent a constituent item to be moved
	 */
	void moveConstituentUp(BundleConstituent constituent);

	/**
	 * Moves a constituent item down one index in the bundle constituents list. Do nothing if the constituent doesn't exist.
	 * 
	 * @param constituent a constituent item to be moved
	 */
	void moveConstituentDown(BundleConstituent constituent);
	
	/**
	 * Removes all constituents in the product bundle.
	 */
	void removeAllConstituents();
	/**
	 * Verifies if a given product is on this bundle's constituent graph. 
	 * @param product product to search.
	 * @return if a given product is on this bundle's constituent graph.
	 */
	boolean hasDescendant(final Product product);

	/**
	 * Sets the selection rule for the bundle.
	 * 
	 * @param rule {@link SelectionRule}
	 */
	void setSelectionRule(SelectionRule rule);

	/**
	 * @return the selection rule for the bundle
	 */
	SelectionRule getSelectionRule();
	
	
	/**
	 * @return whether the bundle price should be calculated based on constituents. True if it should be calculated based on the constituents, 
	 * False otherwise. (the price should come from the price list)
	 */
	Boolean isCalculated();
	
	/**
	 * Sets the bundle price calculation mechanism. 
	 * @param calculated whether the bundle price should be calculated based on constituents
	 */
	void setCalculated(Boolean calculated);
	
}
