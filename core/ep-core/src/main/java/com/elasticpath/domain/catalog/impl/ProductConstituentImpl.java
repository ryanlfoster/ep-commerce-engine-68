/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.catalog.impl;

import java.util.Date;
import java.util.Locale;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductConstituent;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * A bundle constituent item that represents a {@code Product}.
 */
public class ProductConstituentImpl implements ProductConstituent {

	/** Serial version id. */
	private static final long serialVersionUID = 6300000001L;

	private Product product;
	
	/**
	 * @return the product code
	 */
	public String getCode() {
		return product.getCode();
	}

	@Override
	public String getDisplayName(final Locale locale) {
		return product.getDisplayName(locale);
	}

	/**
	 * @return the product represented by this constituent.
	 */
	public Product getProduct() {
		return product;
	}
	
	/**
	 * @return the default sku of the product represented by this constituent.
	 */
	public ProductSku getProductSku() {
		return product.getDefaultSku();
	}

	/**
	 * @return true as this constituent is a Product.
	 */
	public boolean isProduct() {
		return true;
	}

	/**
	 * @return false as this constituent is not a SKU.
	 */
	public boolean isProductSku() {
		return false;
	}
	
	@Override
	public boolean isBundle() {
		return product instanceof ProductBundle;
	}

	@Override
	public void setProduct(final Product product) {
		this.product = product;
	}

	@Override
	public Date getEndDate() {
		return product.getEndDate();
	}

	@Override
	public Date getStartDate() {
		return product.getStartDate();
	}

}
