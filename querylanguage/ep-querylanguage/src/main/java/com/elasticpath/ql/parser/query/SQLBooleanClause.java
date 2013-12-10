package com.elasticpath.ql.parser.query;


/**
 * Represents sql boolean clause.
 */
public class SQLBooleanClause implements NativeBooleanClause {
	
	private final String operator;
	
	private final SQLQuery query;
	
	/**
	 * Constructs the SQLBooleanClause object.
	 * 
	 * @param query the sql query
	 * @param operator the operator
	 */
	public SQLBooleanClause(final SQLQuery query, final String operator) {
		this.query = query;
		this.operator = operator;
	}
	
	@Override
	public String toString() {
		return operator + " " + query.getNativeQuery();
	}
	
	/**
	 * Gets the jpql operator.
	 * 
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * Gets sql query. 
	 * 
	 * @return the query
	 */
	public SQLQuery getQuery() {
		return query;
	}

}
