package com.elasticpath.ql.parser.querybuilder.impl;

import java.util.Arrays;
import java.util.List;

import com.elasticpath.ql.parser.EpQueryParser;
import com.elasticpath.ql.parser.query.JPQLBooleanClause;
import com.elasticpath.ql.parser.query.JPQLBooleanQuery;
import com.elasticpath.ql.parser.query.JPQLQuery;
import com.elasticpath.ql.parser.query.NativeBooleanClause;
import com.elasticpath.ql.parser.query.NativeQuery;
import com.elasticpath.ql.parser.querybuilder.CompleteQueryBuilder;

/**
 * Represents query jpql query builder.
 */
public class JPQLQueryBuilder implements CompleteQueryBuilder {
	
	private String queryPrefix = "";
	
	private String queryPostfix = "";

	private static final String WHERE = "where";

	private static final String JPQL_AND = "AND";

	private static final String JPQL_OR = "OR";

	private static final String JPQL_NOT = "NOT";

	@Override
	public void addBooleanClause(final List<NativeBooleanClause> clauses, final int conj, final NativeQuery query, final String operator) {
		String jpqlOperation = "";
		switch (conj) {
		case EpQueryParser.CONJ_AND:
			jpqlOperation = JPQL_AND;
			break;
		case EpQueryParser.CONJ_OR:
			jpqlOperation = JPQL_OR;
			break;
		case EpQueryParser.CONJ_NOT:
			jpqlOperation = JPQL_NOT;
			break;
		default:
		}

		clauses.add(new JPQLBooleanClause((JPQLQuery) query, jpqlOperation));
	}

	@Override
	public JPQLQuery getBooleanQuery(final List<NativeBooleanClause> clauses) {
		if (clauses.isEmpty()) {
			return null; // all clause words were filtered away by the analyzer.
		}

		final JPQLBooleanQuery query = new JPQLBooleanQuery();
		query.addClauses(Arrays.asList(clauses.toArray(new JPQLBooleanClause[clauses.size()])));
		return query;
	}
	
	@Override
	public void setQueryPrefix(final String prefix) {
		this.queryPrefix = prefix;
	}

	@Override
	public NativeQuery checkProcessedQuery(final NativeQuery nativeQuery) {
		final StringBuffer command = new StringBuffer(queryPrefix).append(" ");
		JPQLBooleanQuery booleanQuery = (JPQLBooleanQuery) nativeQuery;
		if (booleanQuery == null) {
			booleanQuery = new JPQLBooleanQuery();
		} else {
			command.append(WHERE);
		}
		booleanQuery.setPrefix(command.toString());
		booleanQuery.setPostfix(queryPostfix);
		return booleanQuery;
	}

	@Override
	public void setQueryPostfix(final String postfix) {
		this.queryPostfix = postfix;
	}
}
