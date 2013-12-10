package com.elasticpath.domain.collections.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Collection of product skus with behaviour for filtering and lookup by different types of criteria.
 */
public final class ProductSkus extends AbstractDomainCollection<ProductSku> {

	/**
	 * Creates an instance of <code>ProductSkus</code> populated with the given <code>productSkus</code>.
	 * 
	 * @param productSkus collection of product skus to load into the object
	 */
	private ProductSkus(final Collection<ProductSku> productSkus) {
		setEntities(productSkus);
	}
	
	/**
	 * Returns the <code>ProductSkus</code> collection containing the given <code>product</code>'s skus.
	 * 
	 * @param product The product with skus to build the <code>ProductSkus</code> collection from
	 * @return <code>ProductSkus</code> collection with <code>product</code>'s skus.
	 */
	public static ProductSkus skusFor(final Product product) {
		return new ProductSkus(product.getProductSkus().values());
	}

	@Override
	ProductSkus newInstance() {
		return new ProductSkus(new ArrayList<ProductSku>());
	}
	
}
