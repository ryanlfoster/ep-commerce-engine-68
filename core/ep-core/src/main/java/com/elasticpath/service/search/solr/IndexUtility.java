package com.elasticpath.service.search.solr;

import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.persistence.api.Persistable;

/**
 * Helper methods for indexing and searching.
 */
public interface IndexUtility {
	/**
	 * Returns the field ID name of a locale aware field.
	 * 
	 * @param name the original name of the field
	 * @param locale the locale
	 * @return the new name of the new field ID
	 */
	String createLocaleFieldName(final String name, final Locale locale);

	/**
	 * Returns the price field ID name of a currency aware field.
	 * 
	 * @param name the original name of the field
	 * @param catalogCode the Catalog's code
	 * @param priceListGuid the price list GUID
	 * @return the new name of the new field ID
	 */
	String createPriceFieldName(final String name, final String catalogCode, final String priceListGuid);
	
	/**
	 * Returns the displayable field ID name of a store aware field.
	 *
	 * @param name the original name of the field
	 * @param storeCode the store's Code
	 * @return the new name of the field ID
	 */
	String createDisplayableFieldName(final String name, final String storeCode);
	
	/**
	 * Returns the currency of a price field.
	 *
	 * @param fieldName the field name
	 * @return the currency of the field name
	 */
	Currency extractPriceCurrency(final String fieldName);

	/**
	 * Creates an attribute field name for the given {@link Attribute}. If the attribute is not
	 * locale dependant, the locale that is passed is not used.
	 * 
	 * @param attribute the {@link Attribute}
	 * @param locale the locale that is used if the {@link Attribute} is locale dependant
	 * @param stringTypeOnly whether to cast the attribute type to a string
	 * @param minimalStringAnalysis whether to analyze strings minimally
	 * @return the new name of the field ID
	 */
	String createAttributeFieldName(final Attribute attribute, final Locale locale, final boolean stringTypeOnly,
			final boolean minimalStringAnalysis);
	
	/**
	 * Creates a product's featured field for a particular category UID.
	 *
	 * @param categoryUid the category UID
	 * @return a product's featured field
	 */
	String createFeaturedField(final long categoryUid);

	/**
	 * Retrieves the boost values for a locale field. If no locale field is available for the
	 * given locale, the variant, country and then the language itself are removed sequentially to
	 * find it's inherited value. I.e. 'en_US_WIN' -> 'en_US' -> 'en' -> ''
	 * 
	 * @param searchConfig the search configuration to use
	 * @param fieldName the name of the field to lookup
	 * @param locale the locale to search for
	 * @return the boost value for the locale field, one of it's inherited values if that does
	 *         exist otherwise null.
	 */
	float getLocaleBoostWithFallback(final SearchConfig searchConfig, final String fieldName, final Locale locale);
	
	/**
	 * Sorts a list of {@link PersistenceInterceptor} objects such that they are in the same order as the
	 * given <code>uidList</code>. Behavior is undefined for lists of different sizes or if one
	 * contains UIDs that the other does not.
	 * 
	 * @param <T> the type of list to sort
	 * @param uidList the proper ordering UID list
	 * @param persistenceList the list of {@link Persistable} objects to sort
	 * @return a sorted list of {@link Persistable} objects
	 */
	<T extends Persistable> List<T> sortDomainList(final List<Long> uidList, final Collection<T> persistenceList);
	
	/**
	 * Retrieves the boost value for an attribute field.
	 *
	 * @param searchConfig the search configuration to use
	 * @param attribute the attribute
	 * @return the boost value
	 */
	float getAttributeBoost(final SearchConfig searchConfig, final Attribute attribute);
	
	/**
	 * Retrieves the boost value for a locale-dependent attribute field. If no locale field is
	 * available for the given locale, the variant, country and then the language itself are
	 * removed sequentially to find it's inherited value. I.e. 'en_US_WIN' -> 'en_US' -> 'en' -> ''
	 * 
	 * @param searchConfig the search configuration to use
	 * @param attribute the attribute
	 * @param locale the locale to search for
	 * @return the boost value of the locale-dependent attribute field or one of it's inherited
	 *         values if none is defined for the given locale
	 */
	float getAttributeBoostWithFallback(final SearchConfig searchConfig, final Attribute attribute, final Locale locale);

	/**
	 * Builds the product category field name with a catalog code.
	 * 
	 * @param fieldName the product category field name
	 * @param catalogCode the catalog code
	 * @return the full name of the index field
	 */
	String createProductCategoryFieldName(String fieldName, String catalogCode);

	/**
	 * Builds a price sort field name out of the given arguments.
	 * 
	 * @param name the name of the field prefix
	 * @param catalogCode the catalog code
	 * @param priceListStack the price list stack
	 * @return the full field name
	 */
	String createPriceSortFieldName(String name, String catalogCode, List<String> priceListStack);

	/**
	 * Creates sku option field name. 
	 * 
	 * @param locale the locale
	 * @param skuOptionKey the sku option key 
	 * @return the field name used in index.
	 */
	String createSkuOptionFieldName(final Locale locale, final String skuOptionKey);
}
