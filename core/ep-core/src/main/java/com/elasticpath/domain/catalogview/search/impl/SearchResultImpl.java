package com.elasticpath.domain.catalogview.search.impl;

import java.util.List;

import com.elasticpath.domain.catalogview.impl.AbstractCatalogViewResultImpl;
import com.elasticpath.domain.catalogview.search.SearchResult;

/**
 * Represents a default implementation of <code>SearchResult</code>.
 */
public class SearchResultImpl extends AbstractCatalogViewResultImpl implements SearchResult {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private List<String> suggestions;

	/**
	 * Returns a list of alternate query suggestions.
	 *
	 * @return the list of suggestions
	 */
	public List<String> getSuggestions() {
		return suggestions;
	}

	/**
	 * Sets the list of alternate query suggestions.
	 *
	 * @param suggestions the list of suggestions
	 */
	public void setSuggestions(final List<String> suggestions) {
		this.suggestions = suggestions;
	}
}
