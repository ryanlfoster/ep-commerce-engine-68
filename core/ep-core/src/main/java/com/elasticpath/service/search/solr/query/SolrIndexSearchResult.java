package com.elasticpath.service.search.solr.query;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.FilterOption;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.service.search.index.IndexSearchResult;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.solr.SolrIndexSearcherImpl;

/**
 * A SOLR implementation of <code>IndexSearchResult</code>.
 */
public class SolrIndexSearchResult implements IndexSearchResult, Serializable {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Sets whether to try and find a non-empty page when searching. This can happen when you
	 * search for one of the later pages and find no results (i.e. the results were deleted just
	 * prior to your search).
	 */
	private static final boolean PREFER_NON_EMPTY_PAGE = true;

	private SearchCriteria searchCriteria;

	private transient SolrIndexSearcherImpl indexSearcher;

	private int numFound = -1;

	private int startIndex = -1;

	private List<Long> results;

	private List<Long> pageResults;

	private List<FilterOption<CategoryFilter>> categoryFilterOptions;

	private List<FilterOption<BrandFilter>> brandFilterOptions;

	private List<FilterOption<PriceFilter>> priceFilterOptions;

	private Map<Attribute, List<FilterOption<AttributeValueFilter>>> attributeValueFilterOptions;

	private Map<Attribute, List<FilterOption<AttributeRangeFilter>>> attributeRangeFilterOptions;

	private boolean rememeberOptions = false;

	/**
	 * Gets the list of results with an initial index to search from. Attempts to always return a
	 * non-empty list (when the <code>startIndex</code> is out of bounds). If there truly is
	 * nothing, will return an empty list.
	 *
	 * @param startIndex the index to start searching from
	 * @param maxResults the maximum results to return from given start index (maximum per page)
	 * @return a list of results
	 */
	public List<Long> getResults(final int startIndex, final int maxResults) {
		this.startIndex = startIndex;
		findNonEmptyPage(maxResults);
		return results;
	}

	/**
	 * Set the result uid list.  Note: this is not
	 * part of the IndexSearchResult interface and not intended for external
	 * use.
	 *
	 * @param results the list of uids that that should be used
	 */
	public void setResultUids(final List<Long> results) {
		this.results = results;
	}

	/**
	 * Return the current result uid list.  Note: this is not part of the
	 * IndexSearchResult interface and not intended for external use.
	 *
	 * @return the current result uid list.
	 */
	public List<Long> getResultUids() {
		return results;
	}

	/**
	 * Retrieves all of the results without pagination.
	 *
	 * @return a list of all results without pagination
	 */
	public List<Long> getAllResults() {
		// Use a two stage search here to get the number of results found and then use that as
		// the limit. This is due to Solr requiring a limit. Just using a large number may put
		// restrictions on the requirements of the JVM (and takes longer to compile search
		// from a query).
		final int numFound = getNumFound();
		return getResults(0, numFound);
	}

	/**
	 * Gets the last page of results. The last page will contain the maximum number of allowable
	 * results unless there are less than that many.
	 *
	 * @param maxResults the maximum results to return (maximum per page)
	 * @return a list of results
	 */
	public List<Long> getLastPage(final int maxResults) {
		startIndex = getLastNumFound() - maxResults;
		findNonEmptyPage(maxResults);
		return results;
	}

	private void findNonEmptyPage(final int maxResults) {
		clearResults();
		clearAllFilterOptions();

		if (startIndex < 0) {
			results = Collections.emptyList();
			return;
		}
		indexSearcher.search(searchCriteria, startIndex, maxResults, this);
		if (results.isEmpty()) {
			startIndex -= maxResults;
		}
		while (PREFER_NON_EMPTY_PAGE && results.isEmpty() && startIndex > 0) {
			indexSearcher.search(searchCriteria, startIndex, maxResults, this);
			startIndex -= maxResults;
		}
	}

	/**
	 * Sets the <code>SearchCriteria</code> instance.
	 *
	 * @param searchCriteria the <code>SearchCriteria</code> instance
	 */
	public void setSearchCriteria(final SearchCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;
		searchCriteria.optimize();
	}

	/**
	 * Sets the {@link SolrIndexSearcherImpl} instance.
	 *
	 * @param indexSearcher the {@link SolrIndexSearcherImpl} instance
	 */
	public void setIndexSearcher(final SolrIndexSearcherImpl indexSearcher) {
		this.indexSearcher = indexSearcher;
	}

	public void setNumFound(final int numFound) {
		this.numFound = numFound;
	}

	/**
	 * Gets the number of results found from the last search. If no search has taken place, a
	 * search will be requested initially to get an accurate number of results.
	 *
	 * @return the number of results
	 */
	public int getLastNumFound() {
		// always make sure this number is valid, trigger a search if invalid
		if (numFound < 0) {
			getResults(0, 0);
		}
		return numFound;
	}

	/**
	 * Gets the number of results found. This does not necessarily reflect the number of results
	 * found in the last search. This overrides the cached number of results found.
	 *
	 * @return the number of results found
	 */
	public int getNumFound() {
		getResults(0, 0);
		return numFound;
	}

	/**
	 * Returns the start index of the previous search result. If no search has taken place, a
	 * negative number will be returned.
	 *
	 * @return the start index of the previous search result
	 */
	public int getStartIndex() {
		return startIndex;
	}

	/**
	 * Gets the list of category {@link FilterOption}s for the previous search. This is only
	 * valid if faceting is enabled for the search.
	 *
	 * @return the list of category {@link FilterOption}s for the previous search
	 */
	public List<FilterOption<CategoryFilter>> getCategoryFilterOptions() {
		if (categoryFilterOptions == null) {
			return Collections.emptyList();
		}
		return categoryFilterOptions;
	}

	/**
	 * Sets the list of category {@link FilterOption}s for the previous search. This is only
	 * valid if faceting is enabled for the search.
	 *
	 * @param categoryFilterOptions the list of category {@link FilterOptions}s for the previous
	 *            search
	 */
	public void setCategoryFilterOptions(final List<FilterOption<CategoryFilter>> categoryFilterOptions) {
		this.categoryFilterOptions = categoryFilterOptions;
	}

	/**
	 * Gets the list of brand {@link FilterOption}s for the previous search. This is only valid
	 * if faceting is enabled for the search.
	 *
	 * @return the list of brand {@link FilterOption}s for the previous search
	 */
	public List<FilterOption<BrandFilter>> getBrandFilterOptions() {
		if (brandFilterOptions == null) {
			return Collections.emptyList();
		}
		return brandFilterOptions;
	}

	/**
	 * Sets the list of brand {@link FilterOption}s for the previous search. This is only valid
	 * if faceting is enabled for the search.
	 *
	 * @param brandFilterOptions the list of brand {@link FilterOption}s for the previous search
	 */
	public void setBrandFilterOptions(final List<FilterOption<BrandFilter>> brandFilterOptions) {
		this.brandFilterOptions = brandFilterOptions;
	}

	/**
	 * Gets the list of price {@link FilterOption}s for the previous search. This is only valid
	 * if faceting is enabled for the search.
	 *
	 * @return the list of price {@link FilterOptions}s for the previous search
	 */
	public List<FilterOption<PriceFilter>> getPriceFilterOptions() {
		if (priceFilterOptions == null) {
			return Collections.emptyList();
		}
		return priceFilterOptions;
	}

	/**
	 * Sets the list of price {@link FilterOption}s for the previous search. This is only valid
	 * if faceting is enabled for the search.
	 *
	 * @param priceFilterOptions the list of price {@link FilterOption}s for the previous search
	 */
	public void setPriceFilterOptions(final List<FilterOption<PriceFilter>> priceFilterOptions) {
		this.priceFilterOptions = priceFilterOptions;
	}

	/**
	 * Gets the list of attribute value {@link FilterOption}s for the previous search. This is
	 * only valid if faceting is enabled for the search.
	 *
	 * @return the list of attribute value {@link FilterOptions}s for the previous search
	 */
	public Map<Attribute, List<FilterOption<AttributeValueFilter>>> getAttributeValueFilterOptions() {
		if (attributeValueFilterOptions == null) {
			return Collections.emptyMap();
		}
		return attributeValueFilterOptions;
	}

	/**
	 * Sets the list of attribute value {@link FilterOption}s for the previous search. This is
	 * only valid if faceting is enabled for the search.
	 *
	 * @param attributeFilterOptions the list of attribute value {@link FilterOptions}s for the
	 *            previous search
	 */
	public void setAttributeValueFilterOptions(final Map<Attribute, List<FilterOption<AttributeValueFilter>>> attributeFilterOptions) {
		this.attributeValueFilterOptions = attributeFilterOptions;
	}

	/**
	 * Gets the list of attribute range {@link FilterOption}s for the previous search. This is
	 * only valid if faceting is enabled for the search.
	 *
	 * @return the list of attribute range {@link FilterOptions}s for the previous search
	 */
	public Map<Attribute, List<FilterOption<AttributeRangeFilter>>> getAttributeRangeFilterOptions() {
		if (attributeRangeFilterOptions == null) {
			return Collections.emptyMap();
		}
		return attributeRangeFilterOptions;
	}

	/**
	 * Sets the list of attribute range {@link FilterOption}s for the previous search. This is
	 * only valid if faceting is enabled for the search.
	 *
	 * @param attributeRangeFilterOptions the list of attribute range {@link FilterOptions}s for
	 *            the previous search
	 */
	public void setAttributeRangeFilterOptions(
			final Map<Attribute, List<FilterOption<AttributeRangeFilter>>> attributeRangeFilterOptions) {
		this.attributeRangeFilterOptions = attributeRangeFilterOptions;
	}

	/**
	 * Clears results by setting the result to an empty list.
	 */
	private void clearResults() {
		results = Collections.emptyList();

	}

	/**
	 * Clears all filter options by setting them to empty collections.
	 */
	private void clearAllFilterOptions() {
		if (!rememeberOptions) {
			categoryFilterOptions = Collections.emptyList();
			brandFilterOptions = Collections.emptyList();
			priceFilterOptions = Collections.emptyList();
			attributeValueFilterOptions = Collections.emptyMap();
			attributeRangeFilterOptions = Collections.emptyMap();
		}
	}

	/**
	 * filter uids by start index and page size.
	 *
	 * @param startIndex start index
	 * @param pageSize page size
	 */
	public void filterUids(final int startIndex, final int pageSize) {
		this.pageResults = getResults(startIndex, pageSize);
	}

	/**
	 * Gets page result.
	 *
	 * @return list of uids within the page
	 */
	public List<Long> getPageResults() {
		return this.pageResults;
	}

	/**
	 * Sets the flag to remember existing options.
	 *
	 * @param rememberOptions the flag to remember existing filter options
	 */
	public void setRememberOptions(final boolean rememberOptions) {
		this.rememeberOptions = rememberOptions;
	}

	/**
	 * Checks if the flag to remember existing filter options is set.  If true,
	 *
	 * @return true if remembering existing filter options.  The implication is that filter options have
	 * already been set and do not need to be retrieved and set again.
	 */
	public boolean isRememberOptions() {
		return rememeberOptions;
	}

	@Override
	public boolean isEmpty() {
		return results.isEmpty();
	}
}
