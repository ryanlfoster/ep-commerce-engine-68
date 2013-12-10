package com.elasticpath.domain.search.query;

import java.util.Date;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.domain.search.query.SearchTermsMemento.SearchTermsId;

/**
 * Provides a summary description of {@link SearchTermsActivity}.
 */
public interface SearchTermsActivitySummary extends EpDomain {

	/**
	 * @return search count for the associated {@link SearchTerms}
	 */
	int getSearchCount();

	/**
	 * @return last time the {@link SearchTerms} were accessed
	 */
	Date getLastAccessDate();

	/**
	 * Gets the search terms associate to this summary. This method is provided as a convenience when used in
	 * conjunction with {@link #getSearchTermsId()} as using the ID to fetch the terms will cause an additional search
	 * count. This method does <em>not</em> affect the search count.
	 * 
	 * @return search terms associated to this summary
	 * @see #getSearchTermsId()
	 */
	String getSearchTerms();

	/**
	 * Gets the {@link SearchTermsId} associated to this summary. Take note that if you try to fetch the
	 * {@link SearchTerms} using this ID then you will affect the search count. If this is undesired, use
	 * {@link #getSearchTerms()} instead.
	 * 
	 * @return {@link SearchTermsId} associated to this summary
	 * @see #getSearchTerms()
	 */
	SearchTermsId getSearchTermsId();
}
