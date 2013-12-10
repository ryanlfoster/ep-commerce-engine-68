package com.elasticpath.service.search.query.impl;

import com.elasticpath.domain.search.query.SearchTermsMemento;
import com.elasticpath.service.search.query.SearchTermsActivityStrategy;

/**
 * A no-operation implementation of {@link SearchTermsActivityStrategy}.
 */
public class NoopSearchTermsActivityStrategy implements SearchTermsActivityStrategy {

	@Override
	public void logSearchTerm(final SearchTermsMemento memento) {
		// do nothing, this is a no-op class
	}
}
