package com.elasticpath.ql.parser;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.ql.parser.query.NativeQuery;

/**
 * Wrapper class around parsed Lucene query and some other configuration parameters.
 */
public class EpQuery {

	/** Value means that LIMIT parameter was not provided. */
	public static final int LIMIT_NOT_SPECIFIED = Integer.MAX_VALUE;

	private NativeQuery nativeQuery;

	private Integer limit = LIMIT_NOT_SPECIFIED;

	private Integer startIndex = Integer.valueOf(0);

	private EPQueryType queryType;

	private FetchType fetchType;

	private boolean validateOnly;

	private final List<Object> params = new ArrayList<Object>();

	/**
	 * Gets Ep query's fetch type type.
	 * 
	 * @return the fetchType
	 */
	public FetchType getFetchType() {
		return fetchType;
	}

	/**
	 * Sets Ep query's fetch type type.
	 * 
	 * @param fetchType the fetchType to set
	 */
	public void setFetchType(final FetchType fetchType) {
		this.fetchType = fetchType;
	}

	/**
	 * Gets native query.
	 * 
	 * @return the nativeQuery
	 */
	public NativeQuery getNativeQuery() {
		return nativeQuery;
	}

	/**
	 * Sets native query.
	 * 
	 * @param nativeQuery the nativeQuery to set
	 */
	public void setNativeQuery(final NativeQuery nativeQuery) {
		this.nativeQuery = nativeQuery;
	}

	/**
	 * Gets maximum limit.
	 * 
	 * @return the limit
	 */
	public Integer getLimit() {
		return limit;
	}

	/**
	 * Sets maximum limit.
	 * 
	 * @param limit the limit to set
	 */
	public void setLimit(final Integer limit) {
		this.limit = limit;
	}

	@Override
	public String toString() {
		if (nativeQuery == null) {
			return "query absent";
		}
		return nativeQuery.getNativeQuery();
	}

	/**
	 * Determines if this query is supposed to keep indermediate information.
	 * 
	 * @return true if EP QL is supposed to be validated only.
	 */
	public boolean isValidateOnly() {
		return validateOnly;
	}

	/**
	 * Sets flag determining if the EP QL is supposed to be validated only.
	 * 
	 * @param validateOnly validate only flag
	 */
	public void setValidateOnly(final boolean validateOnly) {
		this.validateOnly = validateOnly;
	}

	/**
	 * Sets start index.
	 * 
	 * @return start index.
	 */
	public Integer getStartIndex() {
		return startIndex;
	}

	/**
	 * Sets start index.
	 * 
	 * @param startIndex the startIndex to set
	 */
	public void setStartIndex(final Integer startIndex) {
		this.startIndex = startIndex;
	}

	/**
	 * Gets query's type.
	 * 
	 * @return the queryType
	 */
	public EPQueryType getQueryType() {
		return queryType;
	}

	/**
	 * Sets query's type.
	 * 
	 * @param queryType the queryType to set
	 */
	public void setQueryType(final EPQueryType queryType) {
		this.queryType = queryType;
	}

	/**
	 * Adds param to list of parameters.
	 * 
	 * @param param the parameter
	 */
	public void addParam(final Object param) {
		params.add(param);
	}

	/**
	 * Gets list of specific parameters. This is a holder that contains parameters for building query.
	 * 
	 * @return the parameters list
	 */
	public List<Object> getParams() {
		return params;
	}
}
