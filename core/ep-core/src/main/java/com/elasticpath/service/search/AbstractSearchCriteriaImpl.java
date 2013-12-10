package com.elasticpath.service.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SearchHint;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * An abstract implementation of <code>SearchCriteria</code>.
 */
public abstract class AbstractSearchCriteriaImpl extends AbstractPersistableImpl implements SearchCriteria, Cloneable {
	private static final long serialVersionUID = -8987848890679140022L;

	private Locale locale;
	
	private Currency currency;

	private boolean fuzzySearchDisabled;
	
	private boolean matchAll = false;

	private long uidPk;
	
	private List<Filter< ? >> filters;

	private SortBy sortingType = StandardSortBy.RELEVANCE;
	
	private SortOrder sortingOrder = SortOrder.DESCENDING;
	
	private Set<Long> filterUids;

	private final Map<String, SearchHint< ? >> searchHintsMap = new HashMap<String, SearchHint< ? >>();
	
	/**
	 * Override in base classes to return a list of potential misspelled words.
	 * 
	 * @return an empty list
	 */
	public Set<String> getPotentialMisspelledStrings() {
		return Collections.emptySet();
	}

	/**
	 * Returns the <code>Locale</code>.
	 * 
	 * @return the <code>Locale</code>
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Sets the <code>Locale</code>. Some contents are indexed in multiple locales. The
	 * <code>Locale</code> given here decides what contents get searched.
	 * <p>
	 * e.g. If you give <code>en</code>, only English contents get searched. You can give
	 * <code>null</code> to search on system default locale.
	 * 
	 * @param locale the <code>Locale</code>
	 */
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}
	
	/**
	 * Returns the {@link Currency}.
	 *
	 * @return the {@link Currency}
	 */
	public Currency getCurrency() {
		return currency;
	}
	
	/**
	 * Sets the {@link Currency}.
	 *
	 * @param currency the {@link Currency}
	 */
	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	/**
	 * Returns whether or not a fuzzy search should be performed for this query.
	 * 
	 * @return true if fuzzy search is disabled, false otherwise
	 */
	public boolean isFuzzySearchDisabled() {
		return fuzzySearchDisabled;
	}

	/**
	 * Sets whether or not a fuzzy search should be performed for this query.
	 * 
	 * @param fuzzySearchDisabled whether or not fuzzy search is disabled
	 */
	public void setFuzzySearchDisabled(final boolean fuzzySearchDisabled) {
		this.fuzzySearchDisabled = fuzzySearchDisabled;
	}
	
	/**
	 * Returns whether the search criteria matches all results. This doesn't modify, but overrides
	 * any search criteria.
	 * 
	 * @return whether the search criteria matches all the results
	 */
	public boolean isMatchAll() {
		return matchAll;
	}

	/**
	 * Sets whether the search criteria matches all results. This doesn't modify, but overrides
	 * any search criteria.
	 * 
	 * @param matchAll whether the search criteria matches all the results
	 */
	public void setMatchAll(final boolean matchAll) {
		this.matchAll = matchAll;
	}
	
	/**
	 * Gets the type of sorting for this search criteria. The default sorting is
	 * {@link StandardSortBy#RELEVANCE}.
	 * 
	 * @return the type of sorting for this search criteria
	 */
	public SortBy getSortingType() {
		return sortingType;
	}

	/**
	 * Sets the type of sorting for this search criteria. The default sorting is
	 * {@link StandardSortBy#RELEVANCE}.
	 *
	 * @param sortingType the type of sorting for this search criteria
	 */
	public void setSortingType(final SortBy sortingType) {
		if (sortingType == null) {
			this.sortingType = StandardSortBy.RELEVANCE;
		}
		this.sortingType = sortingType;
	}
	
	/**
	 * Gets the sort order of this search criteria. The default is {@link SortOrder#DESCENDING}.
	 * 
	 * @return the sort order of this search criteria
	 */
	public SortOrder getSortingOrder() {
		return sortingOrder;
	}
	
	/**
	 * Gets the sort order of this search criteria. The default is {@link SortOrder#DESCENDING} if
	 * passed null.
	 * 
	 * @param sortingOrder the sort order of this search criteria
	 */
	public void setSortingOrder(final SortOrder sortingOrder) {
		if (sortingOrder == null) {
			this.sortingOrder = SortOrder.DESCENDING;
		}
		this.sortingOrder = sortingOrder;
	}

	@Override
	public SearchCriteria clone() throws CloneNotSupportedException {
		return (SearchCriteria) super.clone();
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
	
	/**
	 * Gets {@link Filter}s used to filter search results.
	 *
	 * @return a list of {@link Filter}s
	 */
	public List<Filter< ? >> getFilters() {
		if (filters == null) {
			filters = new ArrayList<Filter< ? >>();
		}
		return filters;
	}
	
	/**
	 * Sets {@link Filter}s used to filter search results.
	 *
	 * @param filters a list of {@link Filter}s
	 */
	public void setFilters(final List<Filter< ? >> filters) {
		this.filters = filters;
	}
	
	/**
	 * Gets the list of UIDs that should be filtered out of the results (they should not be in the
	 * result set).
	 * 
	 * @return the list of UIDs that should filtered
	 */
	public Set<Long> getFilteredUids() {
		return filterUids;
	}
	
	/**
	 * Sets the list of UIDs that should be filtered out of the results (they should not be in the
	 * result set).
	 * 
	 * @param filterUids the list of UIDs that should filtered
	 */
	public void setFilterUids(final Set<Long> filterUids) {
		this.filterUids = filterUids;
	}
	
	/**
	 * Returns whether the given UID is valid.
	 *
	 * @param uid the UID
	 * @return whether the given UID is valid
	 */
	protected boolean isUidValid(final Long uid) {
		return uid != null && uid > 0;
	}
	
	/**
	 * Returns whether the given string is valid.
	 *
	 * @param str the string
	 * @return whether the given string is valid
	 */
	protected boolean isStringValid(final String str) {
		return str != null && str.trim().length() != 0;
	}

	/**
	 * Adds a search hint to this search criteria.
	 * 
	 * @param <T> the type of the search hint value
	 * @param searchHint the search hint
	 */
	public <T> void addSearchHint(final SearchHint<T> searchHint) {
		searchHintsMap.put(searchHint.getHintId(), searchHint);
	}

	/**
	 * Gets a specific search hint by its hint ID.
	 * 
	 * @param <T> the type of the search hint value
	 * @param hintId the hint ID
	 * @return the search hint with the given ID or null if not found
	 */
	@SuppressWarnings("unchecked")
	public <T> SearchHint<T> getSearchHint(final String hintId) {
		return (SearchHint<T>) searchHintsMap.get(hintId);
	}

}
