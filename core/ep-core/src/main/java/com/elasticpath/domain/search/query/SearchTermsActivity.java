package com.elasticpath.domain.search.query;

import java.util.Date;

import com.elasticpath.persistence.api.Persistable;

/**
 * Describes activity for {@link SearchTermsMemento}.
 */
public interface SearchTermsActivity extends Persistable {

	/**
	 * @return last time the associated {@link SearchTerms} were accessed
	 */
	Date getLastAccessDate();

	/**
	 * Sets the last access date of this activity.
	 * 
	 * @param lastAccessDate last access time
	 */
	void setLastAccessDate(Date lastAccessDate);

	/**
	 * @return {@link SearchTermsMemento} associated to this activity
	 */
	SearchTermsMemento getSearchTerms();

	/**
	 * Sets the {@link SearchTermsMemento} associated to this activity.
	 * 
	 * @param searchTerms a {@link SearchTermsMemento}
	 */
	void setSearchTerms(SearchTermsMemento searchTerms);
}
