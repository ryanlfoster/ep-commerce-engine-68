package com.elasticpath.domain.audit;


/**
 * A <code>ChangeOperation</code> that operates on a bulk query.
 */
public interface BulkChangeOperation extends ChangeOperation {

	/**
	 * @return Query String.
	 */
	String getQueryString();

	/**
	 * @param queryString query string.
	 */
	void setQueryString(final String queryString);

	/**
	 * @return parameters.
	 */
	String getParameters();

	/**
	 * @param parameters parameters.
	 */
	void setParameters(final String parameters);

}