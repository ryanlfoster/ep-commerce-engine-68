package com.elasticpath.sellingchannel.presentation.impl;

import java.util.Collection;
import java.util.Locale;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.service.pricing.impl.PaymentScheduleHelperImpl;

/**
 * Class to generate a String of filtered sku option values.
 */
public class FilteredSkuOptionDisplay {

	private static final String SKU_OPTION_VALUE_DISPLAY_NAME = "skuOptionValueDisplayName";
	
	/**
	 * Gets a String representation of the sku option values, excluding frequency.
	 * @param productSku the sku to use
	 * @param locale the locale to use
	 * @return a String.
	 */
	public String getFilteredSkuDisplay(final ProductSku productSku, final Locale locale) {
		if (productSku == null) {
			return "";
		}
		Collection<SkuOptionValue> optionValues = productSku.getOptionValues();
		StringBuilder result = new StringBuilder();
		int counter = 0;
		for (SkuOptionValue value : optionValues) {
			String optionKey = value.getSkuOption().getOptionKey();
			if (!PaymentScheduleHelperImpl.FREQUENCY_OPTION_KEY.equals(optionKey)) {
				if (counter > 0) {
					result.append(", ");
				}
				result.append(value.getLocalizedProperties().getValue(SKU_OPTION_VALUE_DISPLAY_NAME, locale));
				counter++;
			}
		}
		return result.toString();
	}
}
