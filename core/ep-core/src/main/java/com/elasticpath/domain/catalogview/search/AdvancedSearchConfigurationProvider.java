package com.elasticpath.domain.catalogview.search;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;

/**
 * Class that provides configured data using the filter factory.
 * 
 */
public interface AdvancedSearchConfigurationProvider {

	/**
	 * Returns a list of pairings between the Attribute (key) and its associated AttributeValue,
	 * represented as a list of AttributeValueFilters (value).
	 * @param storeCode The store code
	 * @param locale The locale
	 * @return A Map of the attributes (represented as an AttributeValueFilter) as keys and its associated 
	 * AttributeValueFilters as its values.
	 */
	Map<Attribute, List<AttributeValueFilter>> getAttributeValueFilterMap(
			final String storeCode, final Locale locale);

	/**
	 * Returns the list of brands that are defined in the configuration which exist in the system.
	 * @param locale The locale
	 * @param storeCode The store code
	 * @return A list of the brands that are predefined in the configuration that exist in the system, sorted
	 * by the name in the <code>locale</code> given
	 */
	List<Brand> getBrandListSortedByName(final Locale locale, final String storeCode);

	/**
	 * Finds user defined {@link AttributeRangeFilter} objects with the same Attribute identified by
	 * <code>attribute</code> for the given store with <code>storeCode</code>.
	 * User-defined attribute ranges elements do not have "children" in the advanced search config file.
	 * 
	 * @param storeCode The store code
	 * @return A list of the AttributeRangeFilters that match the criteria
	 */
	List<AttributeRangeFilter> getAttributeRangeFiltersWithoutPredefinedRanges(String storeCode);

}
