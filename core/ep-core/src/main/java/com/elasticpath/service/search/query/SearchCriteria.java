package com.elasticpath.service.search.query;

import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.service.search.IndexType;

/**
 * Represents an abstract search criteria. A concrete search criteria needs to extend this
 * interface.
 */
public interface SearchCriteria extends Cloneable {
	/**
	 * Returns the <code>Locale</code>.
	 *
	 * @return the <code>Locale</code>
	 */
	Locale getLocale();

	/**
	 * Sets the <code>Locale</code>. Some contents are indexed in multiple locales. The
	 * <code>Locale</code> given here decides what contents get searched.
	 * <p>
	 * e.g. If you give <code>en</code>, only English contents get searched. You can give
	 * <code>null</code> to search on system default locale.
	 *
	 * @param locale the <code>Locale</code>
	 */
	void setLocale(final Locale locale);

	/**
	 * Returns the {@link Currency}.
	 *
	 * @return the {@link Currency}
	 */
	Currency getCurrency();

	/**
	 * Sets the {@link Currency}.
	 *
	 * @param currency the {@link Currency}
	 */
	void setCurrency(final Currency currency);

	/**
	 * Returns whether or not a fuzzy search should be performed for this query.
	 *
	 * @return true if fuzzy search is disabled, false otherwise
	 */
	boolean isFuzzySearchDisabled();

	/**
	 * Sets whether or not a fuzzy search should be performed for this query.
	 *
	 * @param fuzzySearchDisabled whether or not fuzzy search is disabled
	 */
	void setFuzzySearchDisabled(final boolean fuzzySearchDisabled);

	/**
	 * Optimizes a search criteria by removing unnecessary information.
	 */
	void optimize();

	/**
	 * Returns whether the search criteria matches all results. This doesn't modify, but overrides
	 * any search criteria.
	 *
	 * @return whether the search criteria matches all the results
	 */
	boolean isMatchAll();

	/**
	 * Sets whether the search criteria matches all results. This doesn't modify, but overrides
	 * any search criteria.
	 *
	 * @param matchAll whether the search criteria matches all the results
	 */
	void setMatchAll(final boolean matchAll);

	/**
	 * Gets the type of sorting for this search criteria.
	 *
	 * @return the type of sorting for this search criteria
	 */
	SortBy getSortingType();

	/**
	 * Sets the type of sorting for this search criteria.
	 *
	 * @param sortingType the type of sorting for this search criteria
	 */
	void setSortingType(final SortBy sortingType);

	/**
	 * Gets the sort order of this search criteria.
	 *
	 * @return the sort order of this search criteria
	 */
	SortOrder getSortingOrder();

	/**
	 * Gets the sort order of this search criteria.
	 *
	 * @param sortingOrder the sort order of this search criteria
	 */
	void setSortingOrder(final SortOrder sortingOrder);

	/**
	 * Gets {@link Filter}s used to filter search results.
	 *
	 * @return a list of {@link Filter}s
	 */
	List<Filter< ? >> getFilters();

	/**
	 * Sets {@link Filter}s used to filter search results.
	 *
	 * @param filters a list of {@link Filter}s
	 */
	void setFilters(final List<Filter< ? >> filters);

	/**
	 * Gets the list of UIDs that should be filtered out of the results (they should not be in the
	 * result set).
	 *
	 * @return the list of UIDs that should filtered
	 */
	Set<Long> getFilteredUids();

	/**
	 * Sets the list of UIDs that should be filtered out of the results (they should not be in the
	 * result set).
	 *
	 * @param filterUids the list of UIDs that should filtered
	 */
	void setFilterUids(final Set<Long> filterUids);

	/**
	 * Clones this search criteria.
	 * @return the clone of this
	 * @throws CloneNotSupportedException when clone is not supported
	 */
	@SuppressWarnings("PMD.CloneMethodMustImplementCloneable")
	SearchCriteria clone() throws CloneNotSupportedException;

	/**
	 * Returns the index type this criteria deals with.
	 * @return the index type this criteria deals with.
	 */
	IndexType getIndexType();

	/**
	 * Adds a search hint to this search criteria.
	 *
	 * @param <T> the type of the search hint value
	 * @param searchHint the search hint
	 */
	<T> void addSearchHint(SearchHint<T> searchHint);

	/**
	 * Gets a specific search hint by its hint ID.
	 *
	 * @param <T> the type of the search hint value
	 * @param hintId the hint ID
	 * @return the search hint with the given ID or null if not found
	 */
	<T> SearchHint<T> getSearchHint(String hintId);
}
