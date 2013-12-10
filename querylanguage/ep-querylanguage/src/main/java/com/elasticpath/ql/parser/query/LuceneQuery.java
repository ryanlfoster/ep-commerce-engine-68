package com.elasticpath.ql.parser.query;

import org.apache.lucene.search.Query;



/**
 * Represents the wrapper for lucene query.
 */
public class LuceneQuery implements NativeQuery {

	private final Query query;

	/**
	 * Constructs the wrapper object for lucene query with given arguments.
	 * 
	 * @param query the lucene query
	 */
	public LuceneQuery(final Query query) {
		this.query = query;
	}

	@Override
	public String getNativeQuery() {
		return query.toString();
	}
	
	/**
	 * Gets the lucene query.
	 * 
	 * @return the lucene query
	 */
	public Query getQuery() {
		return query;
	}
}
