package com.elasticpath.search.searchengine;

import java.io.Serializable;
import java.util.List;

import com.elasticpath.ql.parser.EPQueryType;

/**
 * Search result interface.
 *
 * @param <T> the type of the returned results
 */
public interface EpQlSearchResult<T> extends Serializable {

	/**
	 * Returns the start index of the taken search.
	 * 
	 * @return the start index of the taken search
	 */
	int getStartIndex();

	/**
	 * Gets the list of found objects' UIDs.
	 * 
	 * @return list of found objects' UIDs
	 */
	List<T> getSearchResults();
	
	/**
	 * Gets the type of EP QL query.
	 * 
	 * @return the epQueryType
	 */
	EPQueryType getEpQueryType();
	
	/**
	 * Gets the number of found UIDs.
	 * 
	 * @return number of found UIDs
	 */
	int getNumFound();
	
	/**
	 * Sets the query type.
	 * 
	 * @param queryType the query type
	 */
	void setEpQueryType(final EPQueryType queryType);

}