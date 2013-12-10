/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.catalogview.filterednavigation;

import java.util.Currency;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.PriceFilter;

/**
 * Defines the methods relevant to filtered navigation.
 */
public interface FilteredNavigationConfiguration {

	/**
	 * Returns all defined price ranges as a <code>Map</code>.
	 * <p>
	 * The key will be like : "price-between-USD-90-and-100".
	 * <p>
	 * And the value will be a <code>PriceFilter</code>.
	 * 
	 * @return all defined price ranges as a <code>Map</code>.
	 */
	Map<String, PriceFilter> getAllPriceRanges();
	
	/**
	 * Clears all price range information, including bottom level price ranges.
	 */
	void clearAllPriceRanges();

	/**
	 * Returns price ranges of the given currency, which are defined at bottom level of the price-range-tree. This is a lookup map for a
	 * <code>Product</code> to decide which price range it should belongs to.
	 * <p>
	 * The key and the value will be the same <code>PriceFilter</code>.
	 * 
	 * @param currency the currency
	 * @return price ranges of the given currency, which are defined at bottom level
	 */
	SortedMap<PriceFilter, PriceFilter> getBottomLevelPriceRanges(Currency currency);

	/**
	 * Returns all defined attribute ranges as a <code>Map</code>.
	 * <p>
	 * The attribute code will be used as the map key .
	 * <p>
	 * And the value will be a <code>AttributeRangeFilter</code>.
	 * 
	 * @return all defined attribute ranges as a <code>Map</code>.
	 */
	Map<String, AttributeRangeFilter> getAllAttributeRanges();

	/**
	 * Clears all attribute range information, including bottom level attribute ranges.
	 */
	void clearAllAttributeRanges();

	/**
	 * Returns attribute ranges of the given attributeCode, which are defined at bottom level of the attribute-range-tree. This is a lookup map for a
	 * <code>Product</code> to decide which attribute range it should belongs to.
	 * <p>
	 * The key and the value will be the same <code>AttributeRangeFilter</code>.
	 * 
	 * @param attributeCode the attributeCode
	 * @return attribute ranges of the given attribute code, which are defined at bottom level
	 */
	SortedMap<AttributeRangeFilter, AttributeRangeFilter> getBottomLevelAttributeRanges(final String attributeCode);

	/**
	 * Returns all defined attribute simple value filters as a <code>Map</code>.
	 * <p>
	 * The filter id will be used as the map key .
	 * <p>
	 * And the value will be a <code>AttributeFilter</code>.
	 * 
	 * @return all defined attribute filters as a <code>Map</code>.
	 */
	Map<String, AttributeValueFilter> getAllAttributeSimpleValues();

	/**
	 * Clears all attribute simple value information.
	 */
	void clearAllAttributeSimpleValues();

	/**
	 * Returns attribute values of the given attributeCode, which are defined at the attribute section. The key will be the attribute value and the
	 * value will be the <code>AttributeValueFilter</code>.
	 * 
	 * @param attributeCode the attributeCode
	 * @return attribute ranges of the given attribute code, which are defined at bottom level
	 */
	SortedMap<String, AttributeValueFilter> getAttributeSimpleValuesMap(final String attributeCode);

	/**
	 * This map contains all the attributes defined in the filtered navigation configuration. 
	 * The key will be the attribute code, and the value will be the <code>Attribute</code>.
	 * 
	 * @return All the attributes.
	 */
	Map<String, Attribute> getAllAttributesMap();
	
	/**
	 * Gets all of the attribute keys that have simple attribute values defined in the search configuration.
	 * @return A set of attribute keys that have simple attribute values defined.
	 */
	Set<String> getAllSimpleAttributeKeys();

	/**
	 * Gets all the brand codes defined in the configuration file which exist in the system.
	 * @return A set of the brand codes that is defined in the configuration (and exist in the system).
	 */
	Set<String> getAllBrandCodes();

	/**
	 * Clears all the brand codes defined previously.
	 */
	void clearAllBrandCodes();

}
