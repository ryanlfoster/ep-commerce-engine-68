package com.elasticpath.domain.search;

import java.util.Date;

import com.elasticpath.persistence.api.Persistable;

/**
 * Represents the persistent log information about a search query that
 * has been issued to the storefront.
 */
public interface SfSearchLog extends Persistable {
	/**
	 * Returns the date that this search was executed.
	 * 
	 * @return the date this search was executed
	 */
	Date getSearchTime();

	/**
	 * Sets the date that this search was executed.
	 * 
	 * @param searchTime the date this search was executed
	 */
	void setSearchTime(final Date searchTime);

	/**
	 * Gets the keywords that were used to search.
	 * 
	 * @return the keywords that were used to search
	 */
	String getKeywords();

	/**
	 * Sets the keywords that were used to search.
	 * 
	 * @param keywords the keywords that were used to search
	 */
	void setKeywords(final String keywords);

	/**
	 * Gets the number of results returned by the search.
	 * 
	 * @return the number of results returned by the search
	 */
	int getResultCount();

	/**
	 * Sets the number of results returned by the search.
	 * 
	 * @param resultCount the number of results returned by the search
	 */
	void setResultCount(final int resultCount);

	/**
	 * Gets whether or not suggestions were generated for this search.
	 * 
	 * @return whether or not suggestions were generated for this search
	 */
	@SuppressWarnings("PMD.BooleanGetMethodName")
	boolean getSuggestionsGenerated();

	/**
	 * Sets whether or not suggestions were generated for this search.
	 * 
	 * @param suggestionsGenerated whether or not suggestions were generated for this search
	 */
	void setSuggestionsGenerated(final boolean suggestionsGenerated);

	/**
	 * Gets the category restriction for this search.
	 * 
	 * @return the category restriction for this search
	 */
	long getCategoryRestriction();

	/**
	 * Sets the category restriction for this search.
	 * 
	 * @param categoryRestriction the category restriction for this search
	 */
	void setCategoryRestriction(final long categoryRestriction);
}
