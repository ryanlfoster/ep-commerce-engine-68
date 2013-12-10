package com.elasticpath.ql.parser.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents JPQL boolean query.
 */
public class JPQLBooleanQuery extends JPQLQuery {

	private static final char SPACE = ' ';

	private String prefix = "";
	
	private String postfix = "";

	private final List<JPQLBooleanClause> clauses;

	/**
	 * Constructs jpql query.
	 */
	public JPQLBooleanQuery() {
		clauses = new ArrayList<JPQLBooleanClause>();
	}

	@Override
	public String getNativeQuery() {
		StringBuffer buffer = new StringBuffer(prefix).append(SPACE);

		for (int i = 0; i < clauses.size(); i++) {
			JPQLBooleanClause clause = clauses.get(i);
			buffer.append(clause.getOperator());
			buffer.append(SPACE);

			final String nativeQuery = clause.getQuery().getNativeQuery();

			buffer.append('(');
			buffer.append(nativeQuery);
			buffer.append(')');

			if (i != clauses.size() - 1) {
				buffer.append(' ');
			}
		}

		buffer.append(SPACE);
		buffer.append(postfix);
		return buffer.toString();
	}

	/**
	 * Adds clause to query.
	 * 
	 * @param clause the clause
	 */
	public void addClause(final JPQLBooleanClause clause) {
		clauses.add(clause);
	}

	/**
	 * Adds clause list to query.
	 * 
	 * @param clauseList the clause list
	 */
	public void addClauses(final List<JPQLBooleanClause> clauseList) {
		clauses.addAll(clauseList);
	}

	/**
	 * Sets the prefix for query.
	 * 
	 * @param prefix the query prefix
	 */
	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * Sets the postfix for query.
	 * 
	 * @param postfix the query postfix
	 */
	public void setPostfix(final String postfix) {
		this.postfix = postfix;
	}
}
