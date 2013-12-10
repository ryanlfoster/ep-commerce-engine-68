/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.EpDomain;

/**
 * Represents a filter in a <code>CatalogViewRequest</code>.
 *
 * @param <T> the type of comparable filter
 */
public interface Filter<T> extends EpDomain, Comparable<T> {

	/**
	 * Define the separator used in price filter id string.
	 */
	char SEPARATOR = '_';

	/**
	 * Define the regular expression of the given separator.
	 */
	String SEPARATOR_IN_REGEX = "\\_";

	/**
	 * Returns the id of the filter. Every filter has a unique id.
	 *
	 * @return the id of the filter
	 */
	String getId();

	/**
	 * Set the id of the filter.
	 * Every filter has a unique id.
	 *
	 * @param filterId the id of the filter
	 */
	void setId(final String filterId);

	/**
	 * Sets the filter id and initialize the filter.
	 *
	 * @param filterId the id to set
	 * @throws EpCatalogViewRequestBindException when the given filter id is invalid
	 */
	void initialize(final String filterId) throws EpCatalogViewRequestBindException;

	/**
	 * Returns the display name of the filter with the given locale.
	 *
	 * @param locale the locale
	 * @return the display name of the filter with the given locale.
	 */
	String getDisplayName(final Locale locale);

	/**
	 * Returns the SEO name of the filter with the given locale.
	 *
	 * @param locale the locale
	 * @return the SEO name of the filter with the given locale.
	 */
	String getSeoName(final Locale locale);

	/**
	 * Returns the SEO identifier of the filter.
	 *
	 * @return the SEO identifier of the filter.
	 */
	String getSeoId();

	/**
	 * Check the filter is localized.
	 *
	 * @return the localized
	 */
	boolean isLocalized();

	/**
	 * Set the filter is localized.
	 *
	 * @param localized the localized to set
	 */
	void setLocalized(final boolean localized);

	/**
	 * Parse the given filter string.
	 *
	 * @param filterIdStr the filter string to parse
	 * @return a map of property name to value
	 */
	Map<String, Object> parseFilterString(final String filterIdStr);

	/**
	 * Initialize the filter with the given set of properties.
	 *
	 * @param properties a map of property name to value
	 */
	void initialize(final Map<String, Object> properties);

	/**
	 * Sets the separator in token.  This token is used within one token to separate multiple values.
	 *
	 * @param separatorInToken the new separator in token
	 */
	void setSeparatorInToken(String separatorInToken);

	/**
	 * Gets the separator in token.  This token is used within one token to separate multiple values.
	 *
	 * @return the separator in token
	 */
	String getSeparatorInToken();

}