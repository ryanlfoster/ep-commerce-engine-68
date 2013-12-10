package com.elasticpath.service.catalog.impl;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalogview.ProductWrapper;

/**
 * <cade>BundleIdentifierImpl</code> is a helper class to make conversion to bundles more convenient. 
 */
public class BundleIdentifierImpl implements BundleIdentifier {

	@Override
	public boolean isBundle(final Product product) {
		return typeSafeAsProductBundle(product) != null;
	}

	@Override
	public boolean isCalculatedBundle(final Product product) {
		ProductBundle bundle = typeSafeAsProductBundle(product);
		if (bundle == null) {
			return false;
		}
		return bundle.isCalculated();
	}

	@Override
	public boolean isCalculatedBundle(final ProductSku productSku) {
		return isCalculatedBundle(productSku.getProduct());
	}

	/**
	 * @param product the product to be casted
	 * @return a ProductBundle
	 * @throws ClassCastException if the product is not a bundle
	 */
	public ProductBundle asProductBundle(final Product product) {
		return (ProductBundle) getWrappedProduct(product); 
	}

	/**
	 * Get the original product instance. If the product is a product wrapper, e.g. an IndexProduct, 
	 * this method returns the wrapped product.
	 * @param product the product
	 * @return the inner-most product in a product wrapper
	 */
	protected Product getWrappedProduct(final Product product) {
		Product wrappedProduct = product;
		while (wrappedProduct instanceof ProductWrapper) {
			wrappedProduct = ((ProductWrapper) wrappedProduct).getWrappedProduct();
		}
		return wrappedProduct;
	}
	
	/**
	 * Casts the product to productBundle.
	 * @param product the product
	 * @return <code>null</code> if the product is not a bundle, the ProductBundle otherwise.
	 */
	protected ProductBundle typeSafeAsProductBundle(final Product product) {
		Product wrappedProduct = getWrappedProduct(product);
		if (wrappedProduct instanceof ProductBundle) {
			return (ProductBundle) wrappedProduct;
		}
		return null;
	}
	
}
