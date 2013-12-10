/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview;

import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * The SkuOptionValueFilter represents the filter on sku option values.
 */
public interface SkuOptionValueFilter extends Filter<SkuOptionValueFilter> {
	
	/**
	 * The constant for sku option values property key.
	 */
	String SKU_OPTION_VALUES_PROPERTY_KEY = "skuOptionValues";

	/**
	 * Get the set of sku option value.
	 * 
	 * @return the set of sku option value
	 */
	Set<SkuOptionValue> getSkuOptionValues();

	/**
	 * Initialize with the given sku option value.
	 *  
	 * @param properties the properties
	 */
	void initialize(Map<String, Object> properties);

}
