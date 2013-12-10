package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.elasticpath.domain.catalog.impl.MultiSkuProductConfiguration;
import com.elasticpath.domain.catalog.impl.MultiSkuProductConfigurationFactory;
import com.elasticpath.domain.catalog.impl.SkuConfiguration;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.service.catalog.MultiSkuProductConfigurationService;

/**
 * Provides information about the options of a product, and finds SKUs based on selected options.
 */
public class MultiSkuProductConfigurationServiceImpl implements MultiSkuProductConfigurationService {
	
	private MultiSkuProductConfigurationFactory multiSkuProductConfigurationFactory;

	@Override
	public Collection<SkuOptionValue> getAvailableOptionValuesForOption(final StoreProduct product, final String skuOptionKey, 
			final Collection<SkuOptionValue> selectedOptionValueCodes) {
		MultiSkuProductConfiguration productConfiguration = multiSkuProductConfigurationFactory.createMultiSkuProduct(product);
		return productConfiguration.getAvailableValuesForOptionKey(skuOptionKey, selectedOptionValueCodes);
	}
	
	@Override
	public Collection<Long> findSkuUidsMatchingSelectedOptions(final StoreProduct storeProduct, 
			final Collection<SkuOptionValue> selectedOptionValues) {
		Collection<SkuConfiguration> configurations = getSkuConfigurationsForSelectedOptions(storeProduct, selectedOptionValues);
		Collection<Long> uids = new ArrayList<Long>(configurations.size());
		for (SkuConfiguration sku : configurations) {
			uids.add(sku.getSkuUid());
		}
		return uids;
	}

	@Override
	public Collection<String> findSkuGuidsMatchingSelectedOptions(final StoreProduct storeProduct, 
			final Collection<SkuOptionValue> selectedOptionValues) {
		Collection<SkuConfiguration> configurations = getSkuConfigurationsForSelectedOptions(storeProduct, selectedOptionValues);
		Collection<String> guids = new ArrayList<String>(configurations.size());
		for (SkuConfiguration sku : configurations) {
			guids.add(sku.getSkuGuid());
		}
		return guids;
	}

	private Collection<SkuConfiguration> getSkuConfigurationsForSelectedOptions(final StoreProduct storeProduct, 
			final Collection<SkuOptionValue> skuOptionValues) {
		MultiSkuProductConfiguration productConfiguration = multiSkuProductConfigurationFactory.createMultiSkuProduct(storeProduct);
		return productConfiguration.getMatchingSkuConfigurations(skuOptionValues);
	}
	

	public void setMultiSkuProductConfigurationFactory(final MultiSkuProductConfigurationFactory multiSkuProductConfigurationFactory) {
		this.multiSkuProductConfigurationFactory = multiSkuProductConfigurationFactory;
	}

	protected MultiSkuProductConfigurationFactory getMultiSkuProductConfigurationFactory() {
		return multiSkuProductConfigurationFactory;
	}
}
