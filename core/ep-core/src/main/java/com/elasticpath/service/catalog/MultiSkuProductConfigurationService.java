package com.elasticpath.service.catalog;

import java.util.Collection;

import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * Provides information about the options of a product, and finds SKUs based on selected options.
 */
public interface MultiSkuProductConfigurationService {
	/**
	 * Returns the set of SkuOptionsValues with of the option with the given option key, given the selected values.
	 * 
	 * @param product the product
	 * @param skuOptionKey the SKU option key
	 * @param selectedOptionValues the selected option values
	 * @return the available options for option value key
	 */
	Collection<SkuOptionValue> getAvailableOptionValuesForOption(StoreProduct product, String skuOptionKey,
			Collection<SkuOptionValue> selectedOptionValues);

	/**
	 * Gets the SKUs that matches the specific option value codes.
	 * 
	 * @param storeProduct the store product
	 * @param selectedOptionValueKeys the option value codes
	 * @return the sku with matching option values, 0 if no SKU is found
	 */
	Collection<Long> findSkuUidsMatchingSelectedOptions(StoreProduct storeProduct, Collection<SkuOptionValue> selectedOptionValueKeys);

	/**
	 * Gets the SKUs that matches the specific option value codes.
	 * 
	 * @param storeProduct the store product
	 * @param selectedOptionValueKeys the option value codes
	 * @return the sku with matching option values, 0 if no SKU is found
	 */
	Collection<String> findSkuGuidsMatchingSelectedOptions(StoreProduct storeProduct, Collection<SkuOptionValue> selectedOptionValueKeys);

}
