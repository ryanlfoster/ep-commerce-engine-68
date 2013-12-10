package com.elasticpath.service.reporting.impl;

import java.util.Collection;
import java.util.List;

import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.reporting.ReportService;

/**
 * Generic Reporting service that can execute a prepared statement given the statement
 * and its parameters.
 */
public class ReportServiceImpl extends AbstractEpPersistenceServiceImpl implements ReportService {
	
	/**
	 * Executes a given prepared statement against the database.
	 * 
	 * @param statement the prepared statement
	 * @param parameters the parameters for the prepared statement
	 * contains the prepared statement and its parameters
	 * 
	 * @return a List of rows of data
	 */
	public List<Object[]> execute(final String statement, final Object[] parameters) {
		
		return getPersistenceEngine().retrieveWithNewSession(statement, parameters);
	}
	
	/**
	 * Executes a given prepared statement against the database. The prepared statement
	 * contains a list parameter, and the parameters contains a List.
	 * 
	 * @param statement the prepared statement
	 * @param values the collection of values
	 * @param listParameterName the name of the parameter for the list values
	 * @param parameters the parameters for the prepared statement
	 * contains the prepared statement and its parameters
	 * 
	 * @return a List of rows of data
	 */
	public List<Object[]> executeWithList(final String statement, final String listParameterName, 
			final Collection<String> values, final Object[] parameters) {
		
		return getPersistenceEngine().retrieveWithListWithNewSession(statement, listParameterName, values, parameters);
	}
	
	/**
	 * Executes a given prepared statement against the database, use when 
	 * want to return a whole object instead of rows of data.
	 * 
	 * @param statement the prepared statement
	 * @param parameters the parameters for the prepared statement
	 * contains the prepared statement and its parameters
	 * 
	 * @return a List of object
	 */
	public List<Object> excuteGetObject(final String statement, final Object[] parameters) {
		
		return getPersistenceEngine().retrieveWithNewSession(statement, parameters);
	}
	
	
	/**
	 * Executes a given prepared statement against the database, use when 
	 * want to return a whole object instead of rows of data and the parameters 
	 * contains a List.
	 * 
	 * @param statement the prepared statement
	 * @param values the collection of values
	 * @param listParameterName the name of the parameter for the list values
	 * @param parameters the parameters for the prepared statement
	 * contains the prepared statement and its parameters
	 * 
	 * @return a List of object
	 */
	public List<Object> excuteGetObject(final String statement, final String listParameterName, 
			final Collection<String> values, final Object[] parameters) {
		return getPersistenceEngine().retrieveWithListWithNewSession(statement, listParameterName, values, parameters);
	}
	
	/**
	 * Execute a given sql statement using JPA persistence engine.
	 * 
	 * @param sql the SQL statement
	 * @return result set as list
	 */
	public List<Object> executeWithSQL(final String sql) {
		return getPersistenceEngine().getPersistenceSession().createSQLQuery(sql).list();
	}

	/**
	 * Throws an exception - Not a valid call with the ReportService.
	 * 
	 * @param uid invalid parameter
	 * @return nothing
	 * @throws UnsupportedOperationException always
	 */
	public Object getObject(final long uid) {
		throw new UnsupportedOperationException();
	}
	
	
}
