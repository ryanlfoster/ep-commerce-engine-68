package com.elasticpath.ql.parser.querybuilder;

import java.util.List;

import com.elasticpath.ql.parser.query.NativeBooleanClause;
import com.elasticpath.ql.parser.query.NativeQuery;

/**
 * This interface provides methods for building query.
 */
public interface CompleteQueryBuilder {

	/**
	 * Glues collected clauses to a single boolean query.
	 * 
	 * @param clauses a list of Boolean clauses
	 * @return Boolean query
	 */
	NativeQuery getBooleanQuery(final List<NativeBooleanClause> clauses);

	/**
	 * Adds Boolean Clause to the list of clauses considering operator.
	 * 
	 * @param clauses list of all collected clauses.
	 * @param conj either AND or OR
	 * @param query the sub query
	 * @param operator either "=" or "!="
	 */
	void addBooleanClause(final List<NativeBooleanClause> clauses, final int conj, final NativeQuery query, final String operator);
	
	/**
	 * Checks the correctness processed native query and modifies it if it is necessary.
	 * 
	 * @param nativeQuery the native query
	 * @return checked native query
	 */
	NativeQuery checkProcessedQuery(final NativeQuery nativeQuery);
	
	/**
	 * Sets the query prefix.
	 * 
	 * @param prefix the prefix to set
	 */
	void setQueryPrefix(final String prefix);
	
	/**
	 * Sets the query postfix.
	 * 
	 * @param postfix the postfix to set
	 */
	void setQueryPostfix(final String postfix);

}
