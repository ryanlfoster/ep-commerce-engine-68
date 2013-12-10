package com.elasticpath.service.catalog.impl;

import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.catalog.DefaultSkuStrategy;

/**
 * Uses the {@link Product#getDefaultSku()} to pick the default SKU.
 */
public class DefaultSkuStrategyImpl implements DefaultSkuStrategy {

	@Override
	public ProductSku getDefaultSku(final Product product, final Shopper shopper) {
		return product.getDefaultSku();
	}

	@Override
	public String getDefaultSkuCode(final Product product, final Shopper shopper) {
		return getDefaultSku(product, shopper).getSkuCode();
	}

	@Override
	public String getDefaultSkuCode(final ConstituentItem constituentItem, final Shopper shopper) {
		if (constituentItem.isProduct()) {
			return getDefaultSkuCode(constituentItem.getProduct(), shopper);
		}
		return constituentItem.getProductSku().getSkuCode();
	}


}
