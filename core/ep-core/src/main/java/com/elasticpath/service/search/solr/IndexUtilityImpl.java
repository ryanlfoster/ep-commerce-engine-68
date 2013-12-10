package com.elasticpath.service.search.solr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Default implementation of {@link IndexUtility}.
 */
public class IndexUtilityImpl implements IndexUtility {

	/**
	 * This constant must be the same as the one used in search server's ProductPriceSortField class.
	 */
	private static final String PRICE_LIST_SEPARATOR = "#";

	private static final Logger LOG = Logger.getLogger(IndexUtilityImpl.class);
	
	private static final String DEFAULT_ATTRIBUTE_TYPE = "_st";
	
	/** String type of ints, floats, doubles, etc. */
	private static final String NON_ANALYZED_STRING_TYPE_FOR_NONSTRING = "_s";

	private static final String ANALYZED_STRING_TYPE_FOR_NONSTRING = "_stringForDismax";
	
	private static final String MINIMAL_STRING_ANALYSIS_TYPE = "_code";

	/** Separator to put between a Lucene field and the locale. */
	private static final char SEPARATOR = '_';
	
	/** Separator surrounding locale strings. */
	private static final char LOCALE_SEPARATOR = '|';

	private Map<String, String> solrAttributeTypeExt;
	
	private CategoryService categoryService;
		
	private BeanFactory beanFactory;

	/**
	 * Returns the field ID name of a locale aware field.
	 * 
	 * @param name the original name of the field
	 * @param locale the locale
	 * @return the new name of the new field ID
	 */
	public String createLocaleFieldName(final String name, final Locale locale) {
		return createLocaleFieldName(name, locale, false);
	}
	
	/**
	 * Generates the name of a currency-aware index field (the Price field).
	 * A Price is specific to a particular currency and store, and since an index
	 * may contain prices for all the stores, the index must distinguish between
	 * them.
	 *
	 * @param name the original name of the field (usually {@link SolrIndexConstants#PRICE})
	 * @param priceListGuid the price list GUID
	 * @param catalogCode the code for the catalog in which the price is valid
	 * @return the full name of the index field
	 * @throws NullPointerException if the store code is null
	 */
	@SuppressWarnings("PMD.AvoidThrowingNullPointerException")
	public String createPriceFieldName(final String name, final String catalogCode, final String priceListGuid) {
		if (catalogCode == null) {
			throw new NullPointerException("The store code cannot be null for a store-aware field.");
		}
		return name + SEPARATOR + catalogCode + SEPARATOR + priceListGuid;
	}
	
	/**
	 * Generates the name of a pricelist-aware index field (the Price field).
	 * A Price is specific to a particular price list, and since an index
	 * may contain prices for all the price lists, the index must distinguish between
	 * them.
	 *
	 * @param name the original name of the field (usually {@link SolrIndexConstants#PRICE})
	 * @param catalogCode the code of the catalog
	 * @param priceListGuids list of guids of the price lists which are active
	 * @return the full name of the index field
	 * @throws NullPointerException if the store code is null
	 */
	public String createPriceSortFieldName(final String name, final String catalogCode, final List<String> priceListGuids) {
		if (priceListGuids == null) {
			throw new IllegalArgumentException("The price list guid cannot be null for a price field.");
		}
		String fieldName = name + "_" + catalogCode;
		for (String priceListGuid : priceListGuids) {
			fieldName += PRICE_LIST_SEPARATOR + priceListGuid;
		}
		return fieldName;
	}
	
	/**
	 * Builds the product category field name with a catalog code.
	 * 
	 * @param fieldName the product category field name
	 * @param catalogCode the catalog code
	 * @return the full name of the index field
	 */
	public String createProductCategoryFieldName(final String fieldName, final String catalogCode) {
		if (catalogCode == null) {
			throw new IllegalArgumentException("The catalog code cannot be null for a catalog-aware field.");
		}
		return fieldName + SEPARATOR + catalogCode;
	}

	/**
	 * Returns the displayable field ID name of a store aware field.
	 *
	 * @param name the original name of the field
	 * @param storeCode the Code of the store
	 * @return the new name of the field ID
	 */
	public String createDisplayableFieldName(final String name, final String storeCode) {
		return name + SEPARATOR + escapeFieldValue(storeCode);
	}
	
	/**
	 * @param fieldValue field value
	 * @return whitespace escaped value of the given field
	 */
	protected String escapeFieldValue(final String fieldValue) {
		return fieldValue.replaceAll("[^\\d\\w]+", "");
	}

	/**
	 * Returns the currency of a price field.
	 *
	 * @param fieldName the field name
	 * @return the currency of the field name
	 */
	public Currency extractPriceCurrency(final String fieldName) {
		final String currencyStr = fieldName.substring(fieldName.lastIndexOf('_') + 1);
		return Currency.getInstance(currencyStr);
	}

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
	public String createAttributeFieldName(final Attribute attribute, final Locale locale, final boolean stringTypeOnly,
			final boolean minimalStringAnalysis) {
		StringBuffer strBuf = new StringBuffer();
		final String fieldName = SolrIndexConstants.ATTRIBUTE_PREFIX + attribute.getKey();
		if (attribute.isLocaleDependant()) {
			strBuf.append(createLocaleFieldName(fieldName, locale));
		} else {
			strBuf.append(fieldName);
		}

		String attributeStorageType = attribute.getAttributeType().getStorageType();
		String attributeType = DEFAULT_ATTRIBUTE_TYPE;
		if (stringTypeOnly && !isStringType(attribute)) {
			// We want a string datatypes only, but the attribute type isn't a string.
			// The string equivalent copy of these types (i.e. floats, ints, etc.) is used instead.
			if (minimalStringAnalysis) {
				// For faceting and the general case
				attributeType = NON_ANALYZED_STRING_TYPE_FOR_NONSTRING;
			} else {
				// Workaround for keyword search due to a bug in solr requiring all query fields
				// to be analyzed and filtered with the stopword filter
				// See: https://issues.apache.org/jira/browse/SOLR-3085
				attributeType = ANALYZED_STRING_TYPE_FOR_NONSTRING;
			}
		} else if (minimalStringAnalysis && isStringType(attribute)) {
			attributeType = MINIMAL_STRING_ANALYSIS_TYPE;
		} else if (solrAttributeTypeExt.get(attributeStorageType) == null) {
			LOG.warn(String.format("No SOLR datatype defined for '%1$S', using default type '%2$S'",
					attributeStorageType, attributeType));
		} else {
			attributeType = solrAttributeTypeExt.get(attributeStorageType);
		}
		strBuf.append(attributeType);

		return strBuf.toString();
	}
	

	@Override
	public String createSkuOptionFieldName(final Locale locale, final String skuOptionKey) {
		StringBuffer strBuf = new StringBuffer();
		final String fieldName = SolrIndexConstants.SKU_OPTION_PREFIX + skuOptionKey;
		strBuf.append(createLocaleFieldName(fieldName, locale));
		return strBuf.toString();
	}
	
	/**
	 * Creates a product's featured field for a particular category UID.
	 * This implementation loads the category from the category service using a load tuner
	 * that ensures the Catalog is loaded with the Category because the field name is composed
	 * of the category code and the catalog code to uniquely identify the category in the index.
	 *
	 * TODO: Fortification: Ideally the categoryUid would not be passed into here; instead the
	 * entire application stack should use the Codes to uniquely identify the category. This would
	 * avoid the necessity of a database lookup to generate this field name.
	 *
	 * @param categoryUid the category UID
	 * @return a product's featured index field name
	 */
	public String createFeaturedField(final long categoryUid) {
		Category category = categoryService.load(categoryUid, getCategoryLoadTuner());
		return SolrIndexConstants.FEATURED_FIELD + category.getCode() + SEPARATOR + category.getCatalog().getCode();
	}
	
	/**
	 * Gets the load tuner to be used when loading a product's Category so that it loads the Catalog as well.
	 * 
	 * @return the load tuner to use when loading categories with their associated Catalogs
	 */
	private FetchGroupLoadTuner getCategoryLoadTuner() {
		FetchGroupLoadTuner loadTuner = beanFactory.getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		loadTuner.addFetchGroup(FetchGroupConstants.CATALOG);
		loadTuner.addFetchGroup(FetchGroupConstants.CATEGORY_INDEX);
		return loadTuner;
	}
	
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
	public float getLocaleBoostWithFallback(final SearchConfig searchConfig, final String fieldName, final Locale locale) {
		Locale tempLocale = locale;

		String localeString = createLocaleFieldName(fieldName, tempLocale, true);

		if (searchConfig.getBoostValue(localeString) != SearchConfig.BOOST_DEFAULT) {
			return searchConfig.getBoostValue(localeString);
		}

		// try to remove the variant
		tempLocale = new Locale(tempLocale.getLanguage(), tempLocale.getCountry());
		localeString = createLocaleFieldName(fieldName, tempLocale, true);
		if (searchConfig.getBoostValue(localeString) != SearchConfig.BOOST_DEFAULT) {
			return searchConfig.getBoostValue(localeString);
		}

		// try to remove the language
		tempLocale = new Locale(tempLocale.getLanguage());
		localeString = createLocaleFieldName(fieldName, tempLocale, true);
		if (searchConfig.getBoostValue(localeString) != SearchConfig.BOOST_DEFAULT) {
			return searchConfig.getBoostValue(localeString);
		}

		return searchConfig.getBoostValue(fieldName);
	}
	
	/**
	 * Sorts a list of {@link Persistable} objects such that they are in the same order as the
	 * given <code>uidList</code>. Behavior is undefined for lists of different sizes or if one
	 * contains UIDs that the other does not.
	 * 
	 * @param <T> the type of list to sort
	 * @param uidList the proper ordering UID list
	 * @param persistenceList the list of {@link Persistable} objects to sort
	 * @return a sorted list of {@link Persistable} objects
	 */
	public <T extends Persistable> List<T> sortDomainList(final List<Long> uidList, final Collection<T> persistenceList) {
		final Map<Long, T> sortMap = new HashMap<Long, T>(persistenceList.size());
		for (T persistenceObj : persistenceList) {
			sortMap.put(persistenceObj.getUidPk(), persistenceObj);
		}
		
		final List<T> sortedObjs = new ArrayList<T>(uidList.size());
		for (long uid : uidList) {
			T persistenceObj = sortMap.get(uid);
			if (persistenceObj != null) {
				sortedObjs.add(persistenceObj);
			}
		}
		
		return sortedObjs;
	}
	
	/**
	 * Retrieves the boost value for an attribute field.
	 *
	 * @param searchConfig the search configuration to use
	 * @param attribute the attribute key
	 * @return the boost value
	 */
	public float getAttributeBoost(final SearchConfig searchConfig, final Attribute attribute) {
		return searchConfig.getBoostValue(attribute.getKey());
	}
	
	/**
	 * Retrieves the boost value for a locale-dependent attribute field. If no locale field is
	 * available for the given locale, the variant, country and then the language itself are
	 * removed sequentially to find it's inherited value. I.e. 'en_US_WIN' -> 'en_US' -> 'en' -> ''
	 * 
	 * @param searchConfig the search configuration to use
	 * @param attribute the attribute key
	 * @param locale the locale to search for
	 * @return the boost value of the locale-dependent attribute field or one of it's inherited
	 *         values if none is defined for the given locale
	 */
	public float getAttributeBoostWithFallback(final SearchConfig searchConfig, final Attribute attribute, final Locale locale) {
		return getLocaleBoostWithFallback(searchConfig, attribute.getKey(), locale);
	}
	
	private boolean isStringType(final Attribute attribute) {
		return attribute.getAttributeType() == AttributeType.SHORT_TEXT || attribute
				.getAttributeType() == AttributeType.LONG_TEXT;
	}
	
	private String createLocaleFieldName(final String fieldName, final Locale locale, final boolean forBoosts) {
		if (forBoosts) {
			return fieldName + SEPARATOR + locale.toString();
		}
		return fieldName + LOCALE_SEPARATOR + locale.toString() + LOCALE_SEPARATOR;
	}

	/**
	 * Sets the attribute to SOLR extension {@link Map}.
	 * 
	 * @param solrAttributeTypeExt attribute to SOLR extension {@link Map}
	 */
	public void setSolrAttributeTypeExt(final Map<String, String> solrAttributeTypeExt) {
		this.solrAttributeTypeExt = solrAttributeTypeExt;
	}
	
	/**
	 * Set the category service.
	 * @param categoryService the service
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	/**
	 * Sets the bean factory object.
	 * 
	 * @param beanFactory the bean factory instance.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
