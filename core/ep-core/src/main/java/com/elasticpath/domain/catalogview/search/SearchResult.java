package com.elasticpath.domain.catalogview.search;

import java.util.List;

import com.elasticpath.domain.catalogview.CatalogViewResult;

/**
 * Represents a search result.
 */
public interface SearchResult extends CatalogViewResult {

	/**
	 * Returns a list of alternate query suggestions.
	 *
	 * @return the list of suggestions
	 */
	List<String> getSuggestions();

	/**
	 * Sets the list of alternate query suggestions.
	 *
	 * @param suggestions the list of suggestions
	 */
	void setSuggestions(final List<String> suggestions);
}
