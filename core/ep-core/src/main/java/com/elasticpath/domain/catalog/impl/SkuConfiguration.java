package com.elasticpath.domain.catalog.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * Represents the configuration aspect of a ProductSKU, i.e. can answer queries about {@link com.elasticpath.domain.skuconfiguration.SkuOption}s and
 * {@link SkuOptionValue}s.
 */
public class SkuConfiguration {
	private final Map<String, SkuOptionValue> optionValues;

	private final String skuGuid;

	private final long skuUid;

	/**
	 * Instantiates a new multi sku product sku.
	 * 
	 * @param skuGuid the sku guid
	 * @param skuUid the sku uid
	 * @param options the options
	 */
	public SkuConfiguration(final String skuGuid, final long skuUid, final Collection<SkuOptionValue> options) {
		this.skuGuid = skuGuid;
		this.skuUid = skuUid;
		this.optionValues = new HashMap<String, SkuOptionValue>(options.size());
		for (SkuOptionValue sov : options) {
			optionValues.put(sov.getSkuOption().getOptionKey(), sov);
		}
	}

	/**
	 * Checks if the product configuration has the same option values as the selected options.
	 * 
	 * @param selectedValues the selected option values
	 * @return <code>true</code>, iff the selected options are the same as this SKU's.
	 */
	public boolean isCompatibleWithSelection(final Collection<SkuOptionValue> selectedValues) {
		for (SkuOptionValue value : selectedValues) {
			if (!isCompatibleWithOptionValue(value)) {
				return false;
			}
		}
		return true;
	}

	private boolean isCompatibleWithOptionValue(final SkuOptionValue optionValue) {
		SkuOptionValue myValue = getOptionValueForOptionKey(optionValue.getSkuOption().getOptionKey());
		if (myValue != null && myValue.getSkuOption().equals(optionValue.getSkuOption())) {
			// need to manually check for GUID equality, because there are two implementations of the interface.
			// we don't really care for the type, but the equals() methods are picky.
			return myValue.getGuid().equals(optionValue.getGuid());
		}
		return false;
	}

	/**
	 * Returns the SkuOptionValue that is related to the sku option with the given key.
	 * 
	 * @param optionKey the option key
	 * @return the SkuOptionValue if one is found, <code>null</code> otherwise.
	 */
	public SkuOptionValue getOptionValueForOptionKey(final String optionKey) {
		return optionValues.get(optionKey);
	}

	public String getSkuGuid() {
		return skuGuid;
	}

	public long getSkuUid() {
		return skuUid;
	}

	public Collection<SkuOptionValue> getOptionValues() {
		return optionValues.values();
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(getSkuGuid());
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof SkuConfiguration) {
			SkuConfiguration other = (SkuConfiguration) obj;
			return ObjectUtils.equals(getSkuGuid(), other.getSkuGuid());
		}
		return false;
	}

}
