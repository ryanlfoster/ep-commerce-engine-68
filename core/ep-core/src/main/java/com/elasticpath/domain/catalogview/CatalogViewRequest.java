package com.elasticpath.domain.catalogview;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;

/**
 * Represents a search request.
 */
public interface CatalogViewRequest extends EpDomain, Cloneable {

	/**
	 * A single breadcrumb element.
	 */
	interface Breadcrumb {
		/**
		 * Sets the breadcrumb display name.
		 *
		 * @param displayName the breadcrumb display name
		 */
		void setDisplayName(final String displayName);

		/**
		 * Sets the Url fragment to this crumb.
		 *
		 * @param urlFragment the Url fragment to this crumb.
		 */
		void setUrlFramgent(final String urlFragment);

		/**
		 * Sets the Url fragment without this crumb.
		 *
		 * @param urlFragmentWithoutThisCrumb the Url fragment without this crumb.
		 */
		void setUrlFragmentWithoutThisCrumb(final String urlFragmentWithoutThisCrumb);

		/**
		 * Returns the Url to this crumb.
		 *
		 * @return the Url to this crumb.
		 */
		String getUrlFragment();

		/**
		 * Returns the Url fragment without this crumb.
		 *
		 * @return the Url fragment without this crumb.
		 */
		String getUrlFragmentWithoutThisCrumb();

		/**
		 * Returns the display name.
		 *
		 * @return the display name
		 */
		String getDisplayName();
	}

	/**
	 * Returns the order in which to sort elements. This is only valid when {@link #getSortType()} is valid.
	 *
	 * @return the order in which to sort elements
	 */
	SortOrder getSortOrder();

	/**
	 * Returns the type of sorting to be done.
	 *
	 * @return the type of sorting to be done
	 */
	SortBy getSortType();

	/**
	 * Parses the sorter identifier string into {@link SortBy} and {@link SortOrder} objects.
	 *
	 * @param sorterIdStr the sorter identifier string
	 * @throws EpCatalogViewRequestBindException in case the given sorter identifier is invalid
	 */
	void parseSorterIdStr(String sorterIdStr) throws EpCatalogViewRequestBindException;

	/**
	 * Returns the list of <code>Filter</code> specified in the search request.
	 *
	 * @return the list of <code>Filter</code>
	 */
	List<Filter< ? >> getFilters();

	/**
	 * Returns a <code>String</code> of the list of <code>Filter</code>.
	 *
	 * @return a <code>String</code> of the list of <code>Filter</code>
	 */
	String getFilterIds();

	/**
	 * Sets the search filters that should be applied to this CatalogViewRequest, by means of
	 * their string identifiers.
	 *
	 * @param filtersIdStr the string identifying the filters to be applied to the view request.
	 * @param store the store to which the filters will be applied
	 * @throws EpCatalogViewRequestBindException in case the given filters identifier strings are invalid
	 * @throws NullPointerException if the given store is null
	 */
	void setFiltersIdStr(String filtersIdStr, Store store) throws EpCatalogViewRequestBindException;

	/**
	 * Returns the query string.
	 *
	 * @return the query string.
	 */
	String getQueryString();

	/**
	 * Creates and returns a more specific search request by adding the given filter.
	 *
	 * @param <T> the type of filter
	 * @param filter the search filter to add
	 * @return a new more specific search request
	 */
	<T extends Filter<T>> CatalogViewRequest createRefinedRequest(Filter<T> filter);

	/**
	 * Returns the currency.
	 *
	 * @return the currency
	 */
	Currency getCurrency();

	/**
	 * Sets the currency.
	 *
	 * @param currency the currency to set
	 */
	void setCurrency(Currency currency);

	/**
	 * Compares this search request with the given search request.
	 *
	 * @param searchRequest the search request to compare
	 * @return 0 if this search request and the given search request has same key words and filters.
	 *         <p>
	 *         1 if this search request and the given search request has same key words, but has more filters.
	 *         <p>
	 *         -1 if this search request and the given search request has same key words, but has unmatching filters.
	 *         <p>
	 * @throws CatalogViewRequestUnmatchException when this search request and the given search request have different key words
	 */
	int compare(CatalogViewRequest searchRequest) throws CatalogViewRequestUnmatchException;

	/**
	 * Returns the locale.
	 *
	 * @return the locale
	 */
	Locale getLocale();

	/**
	 * Sets the locale.
	 *
	 * @param locale the locale to set
	 */
	void setLocale(Locale locale);

	/**
	 * Composes and returns a list of <code>FilterQueryString</code>s for all filters specified the search request.
	 *
	 * @return a list of <code>FilterQueryString</code>s
	 */
	List<Breadcrumb> getFilterQueryStrings();

	/**
	 * Returns the category uid specified in the catalog view request.
	 *
	 * @return the category uid
	 */
	long getCategoryUid();

	/**
	 * Sets the category uid.
	 *
	 * @param categoryUid the category uid to set
	 * @throws EpCatalogViewRequestBindException in case the given category uid is invalid
	 */
	void setCategoryUid(long categoryUid) throws EpCatalogViewRequestBindException;

	/**
	 * Returns the sorting type and order as a single string.
	 *
	 * @return the sorting type and order as a single string
	 */
	String getSortTypeOrderString();

	/**
	 * Clones this catalog view request.
	 * @return the clone of this
	 * @throws CloneNotSupportedException when clone is not supported
	 */
	@SuppressWarnings("PMD.CloneMethodMustImplementCloneable")
	CatalogViewRequest clone() throws CloneNotSupportedException;

	/**
	 * Check if all filters are category filters in the browsing request.
	 * @return true if all are category filters, otherwise false
	 */
	boolean isAllCategoryFilters();
}
