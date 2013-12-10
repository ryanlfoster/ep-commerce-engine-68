package com.elasticpath.domain.catalogview;

import java.util.Locale;

import com.elasticpath.domain.EpDomain;

/**
 * A partition of Filtered Navigation results that has:
 * <ul>
 *    <li>the count of the items within this partition</li>
 *    <li>a localised display name for this partition</li>
 *    <li>a query string that, when used, makes this partition the superset
 *        of the results (which may be further partitioned)</li>
 * </ul>
 *
 * Each of the Filtered Navigation links in the demo store is represented by a different
 * instance of a <code>FilterOption</code>.
 * Could also be known as a Facet (e.g. SLR Cameras).
 * @param <T> the type of filter
 */
public interface FilterOption<T extends Filter<T>> extends EpDomain {
	/**
	 * Returns the number of items matched by this filter option.
	 *
	 * @return the number of items matched by this filter option.
	 */
	int getHitsNumber();

	/**
	 * Sets the number of items matched by this filter option.
	 *
	 * @param hitsNumber the number of items matched by this filter option.
	 */
	void setHitsNumber(int hitsNumber);

	/**
	 * Returns the display name of this filter option in the given locale.
	 *
	 * @param locale the locale to get the filter option display name in.
	 * @return the localised display name of this filter option.
	 */
	String getDisplayName(Locale locale);

	/**
	 * Returns the query string that would make this filter option's results the
	 * superset result partition.
	 *
	 * @return the query string that would make this filter option's results the
	 * superset result partition.
	 */
	String getQueryString();

	/**
	 * Sets the query string to activate this filter option.
	 *
	 * @param queryString the query string to activate this filter option.
	 */
	void setQueryString(String queryString);

	/**
	 * Sets the seo url fragment to activate this filter option.
	 *
	 * @param seoUrlFragment the seo url fragment to activate this filter option.
	 */
	void setSeoUrl(String seoUrlFragment);

	/**
	 * Sets the search filter.
	 *
	 * @param filter the search filter.
	 */
	void setFilter(T filter);

	/**
	 * Gets the search filter.
	 *
	 * @return the search filter
	 */
	T getFilter();
}
