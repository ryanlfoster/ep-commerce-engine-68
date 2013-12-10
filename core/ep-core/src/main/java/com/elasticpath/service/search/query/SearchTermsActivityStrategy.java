package com.elasticpath.service.search.query;

import com.elasticpath.domain.search.query.SearchTermsMemento;

/**
 * A strategy for logging search terms.
 */
public interface SearchTermsActivityStrategy {

	/**
	 * Requests that the given {@link SearchTermsMemento} should be logged.
	 * 
	 * @param memento {@link SearchTermsMemento} to be logged
	 */
	void logSearchTerm(SearchTermsMemento memento);
}
