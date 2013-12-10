package com.elasticpath.domain.catalogview.impl;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.SortUtility;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * This is an abstract implementation of <code>CatalogViewRequest</code>. It can be extended to create a concrete catalog view request, such as a
 * search request or a catalog browsing request.
 */
public abstract class AbstractCatalogViewRequestImpl extends AbstractEpDomainImpl implements CatalogViewRequest, Cloneable {
	private static final Logger LOG = Logger.getLogger(AbstractCatalogViewRequestImpl.class);
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private long categoryUid;

	/**
	 * Do the sanity check.
	 */
	protected abstract void sanityCheck();

	/**
	 * Represent a simple bean which aggregate some query strings for a filter.
	 */
	public static class BreadcrumbImpl implements Breadcrumb {

		private String displayName;

		private String urlFragmentToThisCrumb;

		private String urlFragmentWithoutThisCrumb;

		/**
		 * Sets the filter display name.
		 *
		 * @param displayName the filter display name
		 */
		public void setDisplayName(final String displayName) {
			this.displayName = displayName;
		}

		/**
		 * Sets the url down to this filter.
		 *
		 * @param urlFragment the url down to this filter
		 */
		public void setUrlFramgent(final String urlFragment) {
			urlFragmentToThisCrumb = urlFragment;
		}

		/**
		 * Sets the url without this crumb.
		 *
		 * @param urlFragmentWithoutThisCrumb the url without this filter
		 */
		public void setUrlFragmentWithoutThisCrumb(final String urlFragmentWithoutThisCrumb) {
			this.urlFragmentWithoutThisCrumb = urlFragmentWithoutThisCrumb;
		}

		/**
		 * Returns the query string without this crumb.
		 *
		 * @return the query string without this crumb.
		 */
		public String getUrlFragmentWithoutThisCrumb() {
			return urlFragmentWithoutThisCrumb;
		}

		/**
		 * Returns the query string down to this crumb.
		 *
		 * @return the query string down to this crumb.
		 */
		public String getUrlFragment() {
			return urlFragmentToThisCrumb;
		}

		/**
		 * Returns the display name.
		 *
		 * @return the display name.
		 */
		public String getDisplayName() {
			return displayName;
		}

	}

	private List<Filter< ? >> filters = new ArrayList<Filter< ? >>();

	private Currency currency;

	private Locale locale;

	private transient FilterFactory filterFactory;

	private SortOrder sortOrder;

	private SortBy sortType;

	/**
	 * Composes and returns a list of {@link Breadcrumb}s for all filters specified in this catalog view request.
	 *
	 * @return a list of {@link Breadcrumb}s
	 */
	public List<Breadcrumb> getFilterQueryStrings() {
		final Locale locale = getLocale();
		final List<Breadcrumb> filterQueryStrings = new ArrayList<Breadcrumb>();

		final StringBuilder queryStringForAllFilters = new StringBuilder();
		List<Filter< ? >> filters = getFilters();
		for (int i = 0; i < filters.size(); i++) {
			final Filter< ? > filter = filters.get(i);
			queryStringForAllFilters.append(filter.getId()).append('+');
		}

		final StringBuilder sbfQueryStringDownToThisFilter = new StringBuilder();
		for (int i = 0; i < filters.size(); i++) {
			final Filter< ? > filter = filters.get(i);
			sbfQueryStringDownToThisFilter.append(filter.getId()).append('+');
			final String name = filter.getDisplayName(locale);
			final String queryStringDownToThisFilter = sbfQueryStringDownToThisFilter.substring(0, sbfQueryStringDownToThisFilter.length() - 1);

			final StringBuilder sbfQueryStringWithoutThisFilter = new StringBuilder(queryStringForAllFilters.toString());
			int startPos = sbfQueryStringDownToThisFilter.length() - filter.getId().length() - 1;
			int endPos = sbfQueryStringDownToThisFilter.length();
			sbfQueryStringWithoutThisFilter.replace(startPos, endPos, "");

			startPos = 0;
			endPos = sbfQueryStringWithoutThisFilter.length() - 1;
			if (endPos < 0) {
				endPos = 0;
			}

			final String queryStringWithoutThisFilter = sbfQueryStringWithoutThisFilter.substring(startPos, endPos);
			final Breadcrumb breadcrumb = new BreadcrumbImpl();
			breadcrumb.setDisplayName(name);
			breadcrumb.setUrlFramgent(queryStringDownToThisFilter);
			breadcrumb.setUrlFragmentWithoutThisCrumb(queryStringWithoutThisFilter);
			filterQueryStrings.add(breadcrumb);
		}

		return filterQueryStrings;
	}

	/**
	 * Creates the part of the query string that involves Sorting attributes. If this instance has no sort type, the
	 * returns sort type will be "FeaturedCategory" and the returned SortOrder will be "Descending". Calls
	 * {@link #getSortType() to determine the Sort Type. Calls {@link #getSortTypeOrderString()} to create part of the
	 * string.
	 *
	 * @return the Sorting part of the query string
	 *
	 */
	protected String createSortingQueryString() {
		StringBuffer sbf = new StringBuffer();
		// keep the old values so that we don't modify anything
		SortOrder tmpOrder = getSortOrder();
		SortBy tmpType = getSortType();

		if (getSortType() == null) {
			setSortOrder(SortOrder.DESCENDING);
			setSortType(StandardSortBy.FEATURED_CATEGORY);
		}

		sbf.append(WebConstants.REQUEST_SORTER).append(WebConstants.SYMBOL_EQUAL).append(getSortTypeOrderString());

		setSortOrder(tmpOrder);
		setSortType(tmpType);
		return sbf.toString();
	}

	/**
	 * Returns the list of <code>Filter</code> specified in the search request.
	 *
	 * @return the list of <code>Filter</code>
	 */
	public List<Filter< ? >> getFilters() {
		return filters;
	}

	/**
	 * Sets the search filters that should be applied to this CatalogViewRequest, by means of
	 * their string identifiers.
	 *
	 * This implementation expects the string to be a series of space-delimited filter IDs.
	 * It creates a Filter for each of the filterIDs specified in the given filter string,
	 * and adds the filters to the collection of filters contained in this instance.
	 *
	 * @param filtersIdStr the space-delimited string identifying the filters to be applied
	 * to this ViewRequest.
	 * @param store the store to which the filters will be applied
	 * @throws EpCatalogViewRequestBindException in case the given filters identifier strings are invalid
	 * @throws NullPointerException if the Store is null
	 */
	@SuppressWarnings("PMD.AvoidThrowingNullPointerException")
	public void setFiltersIdStr(final String filtersIdStr, final Store store) throws EpCatalogViewRequestBindException {
		if (!getFilters().isEmpty()) {
			throw new EpDomainException("Filters can only be set once.");
		}
		if (store == null) {
			throw new NullPointerException("Filters cannot be created unless the Store to which they apply has been supplied.");
		}

		if (filtersIdStr == null) {
			return;
		}

		// Load the filter based on the identifier
		final String[] filterIds = filtersIdStr.split("\\s+");

		List<Filter< ? >> filters = createFilters(filterIds, store);
		for (Filter< ? > filter : filters) {
			// avoid same filter being applied several times
			if (!getFilters().contains(filter)) {
				getFilters().add(filter);
			}
		}
	}

	/**
	 * Creates a list of {@link Filter}s using the given {@code filterids}. The order in the retuend list must
	 * correspond <em>exactly</em> to the order of {@code filterids}.
	 *
	 * @param filterids array of filter ids to create {@link Filter}s for
	 * @param store {@link Store} to create a filter for
	 * @return list of {@link Filter}s
	 */
	protected List<Filter< ? >> createFilters(final String[] filterids, final Store store) {
		List<Filter< ? >> result = new ArrayList<Filter< ? >>(filterids.length);
		for (int i = 0; i < filterids.length; ++i) {
			if (!StringUtils.isEmpty(filterids[i])) {
				result.add(getFilterFactory().getFilter(filterids[i], store));
			}
		}
		return result;
	}

	/**
	 * Sets the sorter identifier string.
	 *
	 * @param sorterIdStr the sorter identifier string.
	 * @throws EpCatalogViewRequestBindException in case the given sorter identifier is invalid
	 */
	public void parseSorterIdStr(final String sorterIdStr) throws EpCatalogViewRequestBindException {
		try {
			sortOrder = SortUtility.extractSortOrder(sorterIdStr);
			sortType = SortUtility.extractSortType(sorterIdStr);
		} catch (EpBindException e) {
			throw new EpCatalogViewRequestBindException(String.format("Unable to convert <%2$s> into a sort order and type",
					sortOrder, sorterIdStr), e);
		}
	}

	/**
	 * Sets the search filter factory.
	 *
	 * @param filterFactory the search filter factory
	 */
	public void setFilterFactory(final FilterFactory filterFactory) {
		this.filterFactory = filterFactory;
	}

	/**
	 * Returns the currency.
	 *
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * Sets the currency.
	 *
	 * @param currency the currency to set
	 */
	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	/**
	 * Returns the locale.
	 *
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Sets the locale.
	 *
	 * @param locale the locale to set
	 */
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	/**
	 * Returns a space-delimited <code>String</code> of the list of <code>Filter</code> IDs.
	 * Calls {@link #getFilterIdList()}.
	 *
	 * @return a space-delimited <code>String</code> of the list of <code>Filter</code> IDs
	 */
	public String getFilterIds() {
		return StringUtils.join(getFilterIdList(), ' ');
	}

	/**
	 * Calls {@link #getFilters()} and adds each filter's ID to a List
	 * of Strings.
	 * @return list of FilterID Strings
	 */
	protected List<String> getFilterIdList() {
		List<String> filterIds = new ArrayList<String>();
		for (Filter< ? > filter : getFilters()) {
			filterIds.add(filter.getId());
		}
		return filterIds;
	}

	/**
	 * Returns the filter factory.
	 * @return The filter factory
	 */
	protected FilterFactory getFilterFactory() {
		if (filterFactory == null) {
			filterFactory = getBean("filterFactory");
		}

		return filterFactory;
	}

	/**
	 * Returns the category uid specified in the catalog view request.
	 *
	 * @return the category uid
	 */
	public long getCategoryUid() {
		return categoryUid;
	}

	/**
	 * Sets the category uid.
	 *
	 * @param categoryUid the category uid to set
	 * @throws EpCatalogViewRequestBindException in case the given category uid is invalid
	 */
	public void setCategoryUid(final long categoryUid) throws EpCatalogViewRequestBindException {
		if (this.categoryUid != 0) {
			throw new EpDomainException("Category uid can only be set once.");
		}

		this.categoryUid = categoryUid;
	}

	/**
	 * Returns the order in which to sort elements. This is only valid when {@link #getSortType()} is valid.
	 *
	 * @return the order in which to sort elements
	 */
	public SortOrder getSortOrder() {
		return sortOrder;
	}

	/**
	 * Returns the type of sorting to be done.
	 *
	 * @return the type of sorting to be done
	 */
	public SortBy getSortType() {
		return sortType;
	}

	/**
	 * Sets the order in which to sort elements. This is only valid when {@link #getSortType()} is valid.
	 *
	 * @param sortOrder the order in which to sort elements
	 */
	protected void setSortOrder(final SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	/**
	 * Sets the type of sorting to be done.
	 *
	 * @param sortType the type of sorting to be done
	 */
	protected void setSortType(final SortBy sortType) {
		this.sortType = sortType;
	}

	/**
	 * Returns the sorting type and order as a single string.
	 *
	 * @return the sorting type and order as a single string
	 */
	public String getSortTypeOrderString() {
		return SortUtility.constructSortTypeOrderString(sortType, sortOrder);
	}

	@Override
	public AbstractCatalogViewRequestImpl clone() throws CloneNotSupportedException {
		return (AbstractCatalogViewRequestImpl) super.clone();
	}

	/**
	 * Creates and returns a more specific search request by adding the given filter.
	 *
	 * @param <T> the type of filter
	 * @param filter the search filter to add
	 * @return a new more specific search request
	 */
	public <T extends Filter<T>> CatalogViewRequest createRefinedRequest(final Filter<T> filter) {
		LOG.trace("creating Refined Request");
		AbstractCatalogViewRequestImpl newRequest;
		try {
			newRequest = clone();
		} catch (CloneNotSupportedException e) {
			// should never get here
			throw new EpDomainException("Clone should be supported!", e);
		}
		final List<Filter< ? >> filters = getFilters();
		newRequest.filters = new ArrayList<Filter< ? >>(filters.size() + 1);
		newRequest.getFilters().addAll(filters);
		newRequest.getFilters().add(filter);
		return newRequest;
	}

	/**
	 * Convenience method for getting the sort type/order as a string with the additional http
	 * parameter. I.e. returns &amp;sorter={@link #getSortTypeOrderString() customSorter}.
	 *
	 * @return the sorting type and order as a single string with http parameter prepended
	 */
	public String getSorterAsString() {
		if (getSortType() != null) {
			return '&' + WebConstants.REQUEST_SORTER + '=' + getSortTypeOrderString();
		}
		return "";
	}

	/**
	 * Check if all filters are category filters in the browsing request.
	 * @return true if all are category filters, otherwise false
	 */
	public boolean isAllCategoryFilters() {

		for (Filter< ? > filter : getFilters()) {
			String idStr = filter.getId();
			if (idStr.startsWith(SeoConstants.ATTRIBUTE_FILTER_PREFIX)
				 || idStr.startsWith(SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX)
				 || idStr.startsWith(SeoConstants.PRICE_FILTER_PREFIX)
				 || idStr.startsWith(SeoConstants.BRAND_FILTER_PREFIX)) {
					 return false;
				 }

		}
		return true;
	}
}
