package com.elasticpath.commons.tree.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.commons.tree.TraversalMemento;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Keeps track of prices found for product sku. 
 */
public class ProductPriceMemento implements TraversalMemento {
	private final Map <String, Price> stack = new HashMap <String, Price>();

	/**
	 * @param sku product sku
	 * @param price price
	 */
	public void add(final ProductSku sku, final Price price) {
		this.stack.put(sku.getSkuCode(), price);
	}
	
	/**
	 * @return the saved stack of product sku code to price
	 */
	public Map <String, Price> getStack() {
		return this.stack;
	}
}
