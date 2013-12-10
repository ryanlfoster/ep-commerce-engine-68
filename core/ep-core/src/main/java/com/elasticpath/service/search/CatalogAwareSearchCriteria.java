/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.search;

import java.util.Set;

import com.elasticpath.service.search.query.SearchCriteria;

/**
 * Search Criteria that may be catalog-aware (e.g. Product searches) will implement this interface.
 */
public interface CatalogAwareSearchCriteria extends SearchCriteria {

	/**
	 * Gets the set of catalog code.
	 * 
	 * @return the set of catalog codes
	 */
	Set<String> getCatalogCodes();
	
	/**
	 * Sets the set of catalog code.
	 * 
	 * @param catalogCodes the set of catalog code
	 */
	void setCatalogCodes(final Set<String> catalogCodes);
	
	/**
	 * Gets the catalog code if there is only one catalog code in the search criteria.
	 * 
	 * @return the only one catalog code
	 */
	String getCatalogCode();

	/**
	 * Clear the current set of catalog codes and adds the catalog code into the set.
	 * 
	 * @param catalogCode the catalog code
	 */
	void setCatalogCode(final String catalogCode);

}
