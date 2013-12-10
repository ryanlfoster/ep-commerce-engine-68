package com.elasticpath.service.search.index;

import java.util.List;
import java.util.Map;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.FilterOption;
import com.elasticpath.domain.catalogview.PriceFilter;

/**
 * Represents a object that holds search information including the results (for a particular
 * page).
 */
public interface IndexSearchResult {
	/**
	 * Gets the list of results with an initial index to search from.
	 * 
	 * @param startIndex the index to start searching from
	 * @param maxResults the maximum results to return from given start index (maximum per page)
	 * @return a list of results
	 */
	List<Long> getResults(final int startIndex, final int maxResults);

	/**
	 * Gets the last page of results. The last page will contain the maximum number of allowable
	 * results unless there are less than that many.
	 * 
	 * @param maxResults the maximum results to return (maximum per page)
	 * @return a list of results
	 */
	List<Long> getLastPage(final int maxResults);
	
	/**
	 * Retrieves all of the results without pagination.
	 *
	 * @return a list of all results without pagination
	 */
	List<Long> getAllResults();

	/**
	 * Returns the start index of the previous search result.
	 * 
	 * @return the start index of the previous search result
	 */
	int getStartIndex();

	/**
	 * Gets the cached number of results found for the last search.
	 * 
	 * @return the cached number of results found for the last search
	 */
	int getLastNumFound();
	
	/**
	 * Gets page result.
	 *
	 * @return list of uids within the page
	 */
	List<Long> getPageResults();
	
	/**
	 * Gets the number of results found. This does not necessarily reflect the number of results
	 * found in the last search.
	 *
	 * @return the number of results found
	 */
	int getNumFound();
	
	/**
	 * Gets the list of category {@link FilterOption}s. This is only valid if faceting is enabled
	 * for the search.
	 * 
	 * @return the list of category {@link FilterOption}s
	 */
	List<FilterOption<CategoryFilter>> getCategoryFilterOptions();
	
	/**
	 * Gets the list of brand {@link FilterOption}s for the previous search. This is only
	 * valid if faceting is enabled for the search.
	 * 
	 * @return the list of brand {@link FilterOption}s for the previous search
	 */
	List<FilterOption<BrandFilter>> getBrandFilterOptions();
	
	/**
	 * Gets the list of price {@link FilterOption}s for the previous search. This is only
	 * valid if faceting is enabled for the search.
	 * 
	 * @return the list of price {@link FilterOption}s for the previous search
	 */
	List<FilterOption<PriceFilter>> getPriceFilterOptions();
	
	/**
	 * Gets the list of attribute value {@link FilterOption}s for the previous search. This is
	 * only valid if faceting is enabled for the search.
	 * 
	 * @return the list of attribute {@link FilterOption}s for the previous search
	 */
	Map<Attribute, List<FilterOption<AttributeValueFilter>>> getAttributeValueFilterOptions();
	
	/**
	 * Gets the list of attribute range {@link FilterOptions}s for the previous search. This is
	 * only valid if faceting is enabled for the search.
	 * 
	 * @return the list of attribute value {@link FilterOption}s for the previous search
	 */
	Map<Attribute, List<FilterOption<AttributeRangeFilter>>> getAttributeRangeFilterOptions();
	
	/**
	 * filter uids by startIndex and pageSize.
	 *
	 * @param startIndex start index
	 * @param pageSize page size
	 */
	void filterUids(int startIndex, int pageSize);
	
	/**
	 * Sets the flag to remember existing options.  
	 *
	 * @param rememeberOptions the flag to remember existing filter options
	 */
	void setRememberOptions(boolean rememeberOptions);
	
	/**
	 * Checks if the flag to remember existing filter options is set.  If true, the implication is that filter options have
	 * already been set and do not need to be retrieved and set again.
	 *
	 * @return true, if keeping existing filter options
	 */
	boolean isRememberOptions();

	/**
	 * Check if this search result is empty, ie. no results were found.
	 * @return true if the search result is empty, false otherwise
	 */
	boolean isEmpty();
}
