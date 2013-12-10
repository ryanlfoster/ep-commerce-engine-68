/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview.impl;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.SkuOptionValueFilter;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * The implementation of AttributeFilter.
 */
public class SkuOptionValueFilterImpl extends AbstractFilterImpl<SkuOptionValueFilter> implements SkuOptionValueFilter {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private final Set<SkuOptionValue> skuOptionValues = new LinkedHashSet<SkuOptionValue>();

	@Override
	public String getDisplayName(final Locale locale) {
		if (skuOptionValues.isEmpty()) {
			return null;
		}
		
		StringBuilder skuOptionValueDisplayNameBuilder = new StringBuilder();
		for (SkuOptionValue skuOptionValue : skuOptionValues) {
			if (skuOptionValueDisplayNameBuilder.length() > 0) {
				skuOptionValueDisplayNameBuilder.append(",");
			}
			skuOptionValueDisplayNameBuilder.append(skuOptionValue.getDisplayName(locale, true));
		}
		
		StringBuilder builder = new StringBuilder();
		SkuOption skuOption = skuOptionValues.iterator().next().getSkuOption();
		builder.append(skuOption.getDisplayName(locale, true));
		if (skuOptionValueDisplayNameBuilder.length() > 0) {
			builder.append(":");
		}
		builder.append(skuOptionValueDisplayNameBuilder.toString());

		return builder.toString();
	}

	@Override
	public String getSeoName(final Locale locale) {
		final String displayName = this.getDisplayName(locale);
		if (displayName == null) {
			return null;
		}
		return getUtility().escapeName2UrlFriendly(displayName, locale);
	}
	
	/**
	 * Returns the SEO identifier of the filter.
	 *
	 * @return the SEO identifier of the filter.
	 */
	@Override
	public String getSeoId() {
		StringBuilder seoIdBuilder = new StringBuilder();
		for (SkuOptionValue skuOptionValue : skuOptionValues) {
			if (seoIdBuilder.length() > 0) {
				seoIdBuilder.append(getSeparatorInToken());
			}
			seoIdBuilder.append(skuOptionValue.getOptionValueKey());
		}
		seoIdBuilder.insert(0, SeoConstants.SKU_OPTION_VALUE_PREFIX);
		return seoIdBuilder.toString();
	}

	@Override
	public Map<String, Object> parseFilterString(final String filterIdStr) throws EpCatalogViewRequestBindException {
		Map<String, Object> tokenMap = new HashMap<String, Object>();
		final String idWithoutPrefix = filterIdStr.substring(filterIdStr.indexOf(SeoConstants.SKU_OPTION_VALUE_PREFIX)
				+ SeoConstants.SKU_OPTION_VALUE_PREFIX.length());
		
		final String[] tokens = idWithoutPrefix.split(getSeparatorInToken());
		
		final SkuOptionService skuOptionService = getSkuOptionService();
		final Set<SkuOptionValue> values = new LinkedHashSet<SkuOptionValue>();
		for (String optionValueKey : tokens) {
			SkuOptionValue skuOptionValue = null;
			try {
				skuOptionValue = skuOptionService.findOptionValueByKey(optionValueKey);	
				values.add(skuOptionValue);
			} catch (RuntimeException e) {
				throw new EpCatalogViewRequestBindException("cannot find sku option value key for " + optionValueKey, e);
			}
		}
		tokenMap.put(SKU_OPTION_VALUES_PROPERTY_KEY, values);
		return tokenMap;
	}

	private void addSkuOptionValue(final SkuOptionValue skuOptionValue) {
		if (skuOptionValue == null) {
			return;
		}
		if (!skuOptionValues.isEmpty()) {
			SkuOption skuOption = skuOptionValues.iterator().next().getSkuOption();
			if (!skuOption.equals(skuOptionValue.getSkuOption())) {
				throw new EpCatalogViewRequestBindException(
						"the sku option values don't belong to the same sku option");
			}
		}
		skuOptionValues.add(skuOptionValue);
	}
	
	@Override
	public void initialize(final Map<String, Object> properties) {
		@SuppressWarnings("unchecked")
		Set<SkuOptionValue> skuOptionValues = (Set<SkuOptionValue>) properties.get(SKU_OPTION_VALUES_PROPERTY_KEY);
		for (SkuOptionValue skuOptionValue : skuOptionValues) {
			addSkuOptionValue(skuOptionValue);
		}
	}

	/**
	 * Get sku option service.
	 * 
	 * @return the sku option service
	 */
	protected SkuOptionService getSkuOptionService() {
		return getBean(ContextIdNames.SKU_OPTION_SERVICE);
	}

	@Override
	public int compareTo(final SkuOptionValueFilter other) {
		return 0;
	}

	@Override
	public Set<SkuOptionValue> getSkuOptionValues() {
		return this.skuOptionValues;
	}

	
	
}
