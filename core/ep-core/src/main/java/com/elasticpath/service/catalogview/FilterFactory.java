package com.elasticpath.service.catalogview;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalogview.AdvancedSearchFilteredNavSeparatorFilter;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.DisplayableFilter;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.EpService;

/**
 * A factory which will create catalog <code>Filter</code>s (groupings, facets, ...) 
 * for filtering views on a catalog.
 */
public interface FilterFactory extends EpService {

	/**
	 * Creates and returns a <code>Filter</code> based on the given identifier string.
	 *
	 * @param idStr the identifier string
	 * @param store the store for which the filter will be operational. The store's Code must be populated,
	 * and the store's Catalog must be populated if the Category filter is requested.
	 * @return a {@link Filter}
	 * @throws EpCatalogViewRequestBindException when the given identifier string is invalid
	 * @throws com.elasticpath.base.exception.EpServiceException if the store's code has not been set, or the store's Catalog has not been set for a
	 * Category filter.
	 */
	Filter< ? > getFilter(String idStr, Store store) throws EpCatalogViewRequestBindException;

	/**
	 * Get the filter from the range filter cache with the given temporary filter.
	 *
	 * @param filter the temporary created filter.
	 * @param storeCode the code for the Store for which the Filter was created
	 * @return the cached range filter if exists, else return the temporary filter.
	 */
	Filter< ? > getFilter(Filter< ? > filter, String storeCode);
	
	/**
	 * Creates a new {@link CategoryFilter} from the given category Code and Catalog.
	 *
	 * @param categoryCode the category Code
	 * @param catalog the catalog for which to create the filter
	 * @return a {@link CategoryFilter}
	 */
	CategoryFilter createCategoryFilter(String categoryCode, Catalog catalog);
	
	/**
	 * Creates a new {@link BrandFilter} from the given brand code.
	 * Left for compatibility.
	 *
	 * @param brandCode the brand code
	 * @return a {@link BrandFilter}
	 */
	BrandFilter createBrandFilter(String brandCode);
	
	/**
	 * Creates a new {@link BrandFilter} from the given brand codes.
	 *
	 * @param brandCodes the brand codes
	 * @return a {@link BrandFilter}
	 */
	BrandFilter createBrandFilter(String[] brandCodes);

	/**
	 * Create an attribute range filter for the given key (in the given locale) with
	 * the given lower and upper values.
	 * 
	 * @param attributeKey the key of the attribute which will have a range of values
	 * @param locale the locale
	 * @param lowerValue the lower value of the range
	 * @param upperValue the upper value of the range
	 * @return an {@code AttributeRangeFilter}
	 */
	AttributeRangeFilter createAttributeRangeFilter(String attributeKey, Locale locale, String lowerValue, String upperValue);
	
	/**
	 * Create an attribute value filter for the given key (in the given locale) with the given value.
	 * 
	 * @param attributeKey the attribute key
	 * @param locale the locale
	 * @param valueString the attribute value as a string
	 * @return an {@code AttributeFilter}
	 */
	AttributeValueFilter createAttributeValueFilter(String attributeKey, Locale locale, String valueString);

	/**
	 * Create a displayable filter for the given store code.
	 * 
	 * @param storeCode the store code
	 * @return a {@code DisplayableFilter}
	 */
	DisplayableFilter createDisplayableFilter(String storeCode);
	
	/**
	 * Create a price filter in the given currency within the given lower, upper value range.
	 * 
	 * @param currency the currency of the price filter
	 * @param lowerValue the minimum price
	 * @param upperValue the maximum price
	 * @return a {@code PriceFilter}
	 */
	PriceFilter createPriceFilter(Currency currency, BigDecimal lowerValue, BigDecimal upperValue);

	/**
	 * Create a dummy filter .
	 * 
	 * @return a {@code AdvancedSearchFilteredNavSeparatorFilter}
	 */	
	AdvancedSearchFilteredNavSeparatorFilter createAdvancedSearchFiteredNavSeparatorFilter(); 
	/**
	 * Returns all defined attribute simple value filters as a <code>Map</code>.
	 * <p>
	 * The filter id will be used as the map key .
	 * <p>
	 * And the value will be a <code>AttributeFilter</code>.
	 * 
	 * @param storeCode The Store to look u p the ranges against
	 * @return all defined attribute filters as a <code>Map</code>.
	 */
	Map<String, AttributeValueFilter> getAllSimpleValuesMap(String storeCode);

	/**
	 * Returns all the attribute keys that have simple attribute values defined.  
	 * @param storeCode The store code to search against
	 * @return A set of attribute keys that have simple attribute values defined in the configuration.
	 */
	Set<String> getSimpleAttributeKeys(String storeCode);

	/**
	 * Finds user defined {@link AttributeRangeFilter} objects with the same Attribute identified by
	 * <code>attribute</code> for the given store with <code>storeCode</code>.
	 * User-defined attribute ranges elements do not have "children" in the advanced search config file.
	 * 
	 * @param storeCode The store code
	 * @return A list of the AttributeRangeFilters that match the criteria
	 */
	List<AttributeRangeFilter> getAttributeRangeFiltersWithoutPredefinedRanges(final String storeCode);
	
	/**
	 * Gets the defined set of brands list from the configuration with the given the <code>storeCode</code>.
	 * @param storeCode The store code
	 * @return A set of the brand codes defined in the configuration. 
	 */
	Set<String> getDefinedBrandCodes(String storeCode);
	
	/**
	 * Gets the separator in token.  This token is used within one token to separate multiple values.
	 *
	 * @return the separator in token
	 */
	String getSeparatorInToken();

	/**
	 * Gets the filter bean.
	 *
	 * @param <T> the filter
	 * @param filterBeanName the filter bean name
	 * @return the filter bean
	 */
	<T extends Filter< ? >> T getFilterBean(String filterBeanName);
}
