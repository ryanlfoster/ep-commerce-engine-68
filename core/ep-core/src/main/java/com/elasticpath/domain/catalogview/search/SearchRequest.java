package com.elasticpath.domain.catalogview.search;

import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;



/**
 * Represents a search request.
 */
public interface SearchRequest extends CatalogViewRequest {

	/**
	 * Returns the keywords specified in the search request.
	 * 
	 * @return the keywords
	 */
	String getKeyWords();

	/**
	 * Sets the keywords.
	 * 
	 * @param keyWords the keywords to set
	 * @throws EpCatalogViewRequestBindException in case the given keywords is invalid
	 */
	void setKeyWords(String keyWords) throws EpCatalogViewRequestBindException;

	/**
	 * Returns the url-encoded key words.
	 * 
	 * @return the url-encoded key words.
	 */
	String getEncodedKeyWords();

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
	void setFuzzySearchDisabled(boolean fuzzySearchDisabled);
}
