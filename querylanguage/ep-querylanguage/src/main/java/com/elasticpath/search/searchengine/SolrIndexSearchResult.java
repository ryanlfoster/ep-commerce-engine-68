package com.elasticpath.search.searchengine;

import java.util.List;

import com.elasticpath.ql.parser.EPQueryType;

/**
 * <code>SolrIndexSearchResult</code> encapsulates found UIDs as well as some search parameters used during search.
 * @param <T> the type of return object 
 */
public class SolrIndexSearchResult<T> implements EpQlSearchResult<T> {
	
	private static final long serialVersionUID = 641L;

	private int numFound = -1;

	private int startIndex = -1;
	
	private List<T> results;

	private EPQueryType epQueryType;

	/**
	 * Sets the number of found UIDs.
	 * 
	 * @param numFound number of found uids.
	 */
	public void setNumFound(final int numFound) {
		this.numFound = numFound;
	}
	
	/**
	 * Sets the result UID list.  Note: this is not
	 * part of the IndexSearchResult interface and not intended for external
	 * use.
	 * 
	 * @param results the list of uids that that should be used
	 */
	public void setResultUids(final List<T> results) {
		this.results = results;
	}

	/**
	 * Sets start from index.
	 * @param startFromIndex startFromIndex
	 */
	public void setStartIndex(final int startFromIndex) {
		this.startIndex = startFromIndex;
	}
	
	/**
	 * Returns the start index of the taken search. 
	 * 
	 * @return the start index of the taken search
	 */
	@Override
	public int getStartIndex() {
		return startIndex;
	}

	/**
	 * Gets the list of found objects' UIDs.
	 * 
	 * @return list of found objects' UIDs 
	 */
	@Override
	public List<T> getSearchResults() {
		return results;
	}

	/**
	 * Sets the list of found objects' UIDs.
	 * 
	 * @param uids list of found objects' UIDs 
	 */
	public void setSearchResults(final List<T> uids) {
		this.results = uids;
	}

	/**
	 * Gets the number of found UIDs.
	 * 
	 * @return number of found UIDs
	 */
	@Override
	public int getNumFound() {
		return numFound;
	}

	/**
	 * Gets the type of EP QL query.
	 * 
	 * @return the epQueryType
	 */
	@Override
	public EPQueryType getEpQueryType() {
		return epQueryType;
	}

	/**
	 * @param epQueryType the epQueryType to set
	 */
	@Override
	public void setEpQueryType(final EPQueryType epQueryType) {
		this.epQueryType = epQueryType;
	}
}
