package com.elasticpath.ql.parser.query;


/**
 * Represents jpql boolean clause.
 */
public class JPQLBooleanClause implements NativeBooleanClause {
	
	private final String operator;
	
	private final JPQLQuery query;
	
	/**
	 * Constructs the JPQLBooleanClause object.
	 * 
	 * @param query the jpql query
	 * @param operator the operator
	 */
	public JPQLBooleanClause(final JPQLQuery query, final String operator) {
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
	 * Gets jpql query. 
	 * 
	 * @return the query
	 */
	public JPQLQuery getQuery() {
		return query;
	}

}
