package com.elasticpath.domain.catalog.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;

/**
 * A factory for creating {@link MultiSkuProductConfiguration} objects.
 */
public class MultiSkuProductConfigurationFactory {
	
	/**
	 * Creates a new MultiSkuProductConfiguration object.
	 *
	 * @param product the product
	 * @return the multi sku product configuration
	 */
	public MultiSkuProductConfiguration createMultiSkuProduct(final StoreProduct product) {
		Collection<SkuConfiguration> skuConfigurations = new ArrayList<SkuConfiguration>();
		for (ProductSku sku : product.getProductSkus().values()) {
			if (isSkuDisplayable(product, sku)) {
				skuConfigurations.add(createSkuConfiguration(sku));
			}
		}
		return new MultiSkuProductConfiguration(skuConfigurations);
	}


	/**
	 * Determines whether the SKU should be considered a displayable configuration for the product.
	 *
	 * @param product the product
	 * @param sku the sku
	 * @return <code>true</code>, iff the SKU is displayable
	 */
	protected boolean isSkuDisplayable(final StoreProduct product, final ProductSku sku) {
		return sku.isWithinDateRange();
	}


	/**
	 * Creates a new MultiSkuProductConfiguration object.
	 *
	 * @param sku the sku
	 * @return the sku configuration
	 */
	protected SkuConfiguration createSkuConfiguration(final ProductSku sku) {
		return new SkuConfiguration(sku.getGuid(), sku.getUidPk(), sku.getOptionValues());
	}
	
	
}
