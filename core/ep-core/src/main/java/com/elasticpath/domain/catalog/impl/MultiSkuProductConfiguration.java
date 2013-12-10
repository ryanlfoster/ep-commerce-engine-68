package com.elasticpath.domain.catalog.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * Represents the configuration aspect of a multi-SKU product.
 */
public class MultiSkuProductConfiguration {
	private final Collection<SkuConfiguration> skus;

	/**
	 * Instantiates a new multi sku product configuration.
	 * 
	 * @param skuConfigurations the SKU configurations
	 */
	public MultiSkuProductConfiguration(final Collection<SkuConfiguration> skuConfigurations) {
		this.skus = skuConfigurations;
	}


	/**
	 * Gets the SKUs that are compatible with the selected value keys, i.e. have the same values for the given options.
	 *
	 * @param selectValues the selected values
	 * @return the compatible SKUs
	 */
	public Collection<SkuConfiguration> getMatchingSkuConfigurations(final Collection<SkuOptionValue> selectValues) {
		Collection<SkuConfiguration> compatibleSkus = new ArrayList<SkuConfiguration>();
		for (SkuConfiguration sku : skus) {
			if (sku.isCompatibleWithSelection(selectValues)) {
				compatibleSkus.add(sku);
			}
		}
		return compatibleSkus;
	}

	/**
	 * Gets the available SKU option values for the option with the provided key, given the selections.
	 *
	 * @param optionKey the option key
	 * @param selectedValues the selected values
	 * @return the available values for option key
	 */
	public Set<SkuOptionValue> getAvailableValuesForOptionKey(final String optionKey, final Collection<SkuOptionValue> selectedValues) {
		Set<SkuOptionValue> availableValues = new HashSet<SkuOptionValue>();
		Collection<SkuConfiguration> compatibleSkus = getMatchingSkuConfigurations(selectedValues);
		for (SkuConfiguration sku : compatibleSkus) {
			availableValues.add(sku.getOptionValueForOptionKey(optionKey));
		}
		return availableValues;
	}

}
