package com.elasticpath.domain.catalogview.search;

import java.util.List;
import java.util.Map;

import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.Filter;

/**
 * Represents an advanced search request.
 *
 */
public interface AdvancedSearchRequest extends CatalogViewRequest {
	
	/**
	 * Get the query properties.
	 * The query properties is another way to save query string
	 * It saves the http request parameter and value in the map
	 *  
	 * @return the query properties
	 */
	Map<String, String> getQueryProperties();
	
	/**
	 * Adds a filter.
	 * 
	 * @param filter the filter to add
	 */
	void addFilter(Filter< ? > filter);
	
	
	/**
	 * Returns the list of filters only used in advanced search request (i.e. effectively excluding the ones appended by filtered nav).
	 *
	 * @return the list of <code>Filter</code>
	 */	
	List<Filter< ? >> getAdvancedSearchFilters();
}
