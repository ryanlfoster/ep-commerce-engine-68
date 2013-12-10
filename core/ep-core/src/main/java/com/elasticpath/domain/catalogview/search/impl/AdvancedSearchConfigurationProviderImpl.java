package com.elasticpath.domain.catalogview.search.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.search.AdvancedSearchConfigurationProvider;
import com.elasticpath.domain.misc.impl.BrandComparatorImpl;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalogview.FilterFactory;

/**
 * Concrete implementation of AdvancedSearchConfigurationProvider.
 */
public class AdvancedSearchConfigurationProviderImpl implements AdvancedSearchConfigurationProvider {
	
	private static final Logger LOG = Logger.getLogger(AdvancedSearchConfigurationProviderImpl.class);

	/** The filter factory instance that uses the advanced search configuration loader. */
	private FilterFactory filterFactory;

	private AttributeService attributeService;
	
	private BrandService brandService;

	@Override
	public Map<Attribute, List<AttributeValueFilter>> getAttributeValueFilterMap(
			final String storeCode, final Locale locale) {
		
		Map<Attribute, List<AttributeValueFilter>> attributeValueFilterMap = 
			new HashMap<Attribute, List<AttributeValueFilter>>();
		
		for (AttributeValueFilter filter : filterFactory.getAllSimpleValuesMap(storeCode).values()) {
			if (filter.getAttributeValue() == null) { // 'parent' or 'root' filters don't have values
				LOG.debug("Skipping root filter " + filter.getId());
				continue;
			} else if (filter.isLocalized() || filter.getAttribute().isLocaleDependant()) {
				// we know we are dealing with localized filter/attribute, the filter MUST have a locale
				if (filter.getLocale() == null) {
					LOG.warn(String.format("Filter locale is null for attribute <%s>. "
							+ "This usually indicates a problem with the configuration setup (attribute declared as "
							+ "multi-language, but configuration declares as non-localized", filter.getAttributeKey()));
					continue;
				} else if (!filter.getLocale().equals(locale)) {
					// should always happen if filter localized with values with at least 2 different locales
					LOG.debug("Skipping filter " + filter.getId() + " with different locale");
					continue;
				}
			}

			if (!attributeValueFilterMap.containsKey(filter.getAttribute())) {
				attributeValueFilterMap.put(filter.getAttribute(), new ArrayList<AttributeValueFilter>());
			}
			attributeValueFilterMap.get(filter.getAttribute()).add(filter);
		}

		return attributeValueFilterMap;
	}
	
	@Override
	public List<Brand> getBrandListSortedByName(final Locale locale, final String storeCode) {
		Map<String, Brand> brands = mapByCodes(getBrandService().getBrandInUseList());
		Set<String> enabledBrandCodes = filterFactory.getDefinedBrandCodes(storeCode);
		List<Brand> result = new ArrayList<Brand>();
		
		for (String enabledBrandCode : enabledBrandCodes) {
			if (brands.containsKey(enabledBrandCode)) {
				result.add(brands.get(enabledBrandCode));
			} else {
				LOG.warn("Cannot find brand with code: " + enabledBrandCode + " in the system. This is likely a configuration" 
						+ " issue. Skipping this brand.");
			}
		}

		Collections.sort(result, new BrandComparatorImpl(locale));
		
		return result;
	}	
	
	/**
	 * @param brands the collection of brands to convert to a map.
	 * @return a map of brand codes to brands.
	 */
	private Map<String, Brand> mapByCodes(final Collection<Brand> brands) {
		Map<String, Brand> brandsMap = new HashMap<String, Brand>();
		
		for (Brand brand : brands) {
			brandsMap.put(brand.getCode(), brand);
		}
		return brandsMap;
	}
	
	@Override
	public List<AttributeRangeFilter> getAttributeRangeFiltersWithoutPredefinedRanges(final String storeCode) {
		return filterFactory.getAttributeRangeFiltersWithoutPredefinedRanges(storeCode);
	}
	
	/**
	 * Sets the filter factory.
	 * @param filterFactory The filter factory to set.
	 */
	public void setFilterFactory(final FilterFactory filterFactory) {
		this.filterFactory = filterFactory;
	}
	/**
	 * @return the filterFactory
	 */
	protected FilterFactory getFilterFactory() {
		return filterFactory;
	}

	/**
	 * Sets the attribute Service.
	 * @param attributeService The attribute service to set.
	 */
	public void setAttributeService(final AttributeService attributeService) {
		this.attributeService = attributeService;
	}

	/**
	 * @return the attributeService
	 */
	protected AttributeService getAttributeService() {
		return attributeService;
	}
	
	/**
	 * @return the brandService
	 */
	public BrandService getBrandService() {
		return brandService;
	}

	/**
	 * @param brandService the brandService to set
	 */
	public void setBrandService(final BrandService brandService) {
		this.brandService = brandService;
	}
}

