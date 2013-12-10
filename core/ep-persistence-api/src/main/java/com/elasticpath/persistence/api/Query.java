package com.elasticpath.persistence.api;

import java.util.Collection;
import java.util.List;
import java.util.Set;



/**
 * Represents a query in ElasticPath.
 */
public interface Query {

	/**
	 * Bind a value to a JDBC-style query parameter.
	 *
	 * @param position the position of the parameter in the query string, numbered from <tt>0</tt>.
	 * @param val the non-null parameter value
	 * @throws EpPersistenceException if no type could be determined for the parameter
	 */
	void setParameter(int position, Object val) throws EpPersistenceException;

	/**
	 * Return the query results as a <tt>List</tt>. If the query contains multiple results per row, the results are returned in an instance of
	 * <tt>Object[]</tt>.
	 *
	 * @param <T> the expected type of elements returned by the query
	 * @return the result list
	 * @throws EpPersistenceException in case of errors
	 */
	<T> List<T> list() throws EpPersistenceException;

	/**
	 * Set a fetch size for the underlying JDBC query.
	 *
	 * @param fetchSize the fetch size
	 */
	void setFetchSize(int fetchSize);

	/**
	 * Set the maximum number of rows to retrieve. If not set, there is no limit to the number of rows retrieved.
	 *
	 * @param maxResults the maximum number of rows
	 */
	void setMaxResults(int maxResults);
	
	/**
	 * Sets the first result for this query.
	 * 
	 * @param startPosistion the start position
	 */
	void setFirstResult(final int startPosistion);

	/**
	 * Set the set of fetch groups to be used for this query, or null for none.
	 *
	 * @param groups the set of fetch groups to add
	 */
	void setFetchGroups(Set<String> groups);

	/**
	 * Set the collection of fields to be included in the fetch group for this query, or null for none.
	 *
	 * @param fields the collection of fully qualified field names
	 */
	void setFetchGroupFields(Collection<String> fields);

	/**
	 * Execute the query as an update/insert.
	 * 
	 * @return the number of rows updated.
	 */
	int executeUpdate();

}
