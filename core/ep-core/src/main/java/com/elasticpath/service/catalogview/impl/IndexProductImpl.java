/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.catalogview.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.catalogview.IndexProduct;

/**
 * Implementation for the index product.
 */
public class IndexProductImpl extends AbstractWrappedProductImpl implements IndexProduct {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private final Map<String, Boolean> availabilityMap = new HashMap<String, Boolean>();
	private final Map<String, Boolean> displayabilityMap = new HashMap<String, Boolean>();
	
	/**
	 * Constructor.
	 * 
	 * @param wrappedProduct the wrapped product
	 */
	protected IndexProductImpl(final Product wrappedProduct) {
		super(wrappedProduct);
	}

	/**
	 * Returns availability for a specific store with a storeCode.
	 * 
	 * @param storeCode the store code
	 * @return true if product is available
	 */
	public boolean isAvailable(final String storeCode) {
		return availabilityMap.get(storeCode);
	}

	/**
	 * Returns whether a product is displayable for a specific store with a storeCode.
	 * 
	 * @param storeCode the store code
	 * @return true if product is displayable
	 */
	public boolean isDisplayable(final String storeCode) {
		return displayabilityMap.get(storeCode);
	}
	
	/**
	 * Sets the displayability of this product.
	 * 
	 * @param storeCode the store code
	 * @param displayable true if the product is displayable
	 */
	public void setDisplayable(final String storeCode, final boolean displayable) {
		displayabilityMap.put(storeCode, displayable);
	}

	/**
	 * Sets the availability of this product.
	 * 
	 * @param storeCode the store code
	 * @param displayable true if the product is available
	 */
	public void setAvailable(final String storeCode, final boolean displayable) {
		availabilityMap.put(storeCode, displayable);
	}

}
