package com.elasticpath.ql.parser.query;

/**
 * Represent SQL query.
 */
public class SQLQuery implements NativeQuery {

	private String query;

	/**
	 * Constructs empty object.
	 */
	protected SQLQuery() {
		// do nothing
	}

	/**
	 * Constructs sql query.
	 * 
	 * @param field the field
	 * @param value the value
	 * @param conj the operator
	 */
	public SQLQuery(final String field, final String value, final String conj) {
		query = field + conj + value;
	}

	/**
	 * Constructs sql query specifying subquery directly.
	 * 
	 * @param subQuery native SQL subquery
	 */
	public SQLQuery(final String subQuery) {
		query = subQuery;
	}

	@Override
	public String getNativeQuery() {
		return query;
	}
}
