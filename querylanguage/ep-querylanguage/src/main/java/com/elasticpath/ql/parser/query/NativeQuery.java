package com.elasticpath.ql.parser.query;

/**
 * This interface represents native query, each object that will be used for constructing specific query from epql query should implement this
 * interface.
 */
public interface NativeQuery {
	
	/**
	 * Gets string representation of native query.
	 * 
	 * @return the string representation of native query
	 */
	String getNativeQuery();

}
