package com.elasticpath.ql.parser.querybuilder.impl;

import java.util.List;

import com.elasticpath.ql.parser.EpQLOperator;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.ql.parser.query.JPQLQuery;
import com.elasticpath.ql.parser.query.NativeQuery;
import com.elasticpath.ql.parser.querybuilder.SubQueryBuilder;

/**
 * Represents jpql sub query builder.
 */
public class JPQLSubQueryBuilder implements SubQueryBuilder {
	
	private static final String JPQL_NOT_EQUAL = "<>";

	@Override
	public NativeQuery buildQuery(final NativeResolvedTerm nativeResolvedTerm, final EpQLTerm epQLTerm) throws ParseException {
		final List<String> resolvedValues = nativeResolvedTerm.getResolvedValues();
		if (resolvedValues.size() != 1) {
			throw new ParseException("Multiple values are not allowed for field " + epQLTerm.getEpQLField());
		}
		String jpqlOperator = convertEPQLOperatorToJPQLOperator(epQLTerm);
		return new JPQLQuery(nativeResolvedTerm.getResolvedField(), resolvedValues.get(0), jpqlOperator);
	}

	/**
	 * Converts EPQL operator to JPQL operator.
	 * @param epQLTerm EpQLTerm
	 * @return string representation of JPQL operator for this sub query
	 */
	private String convertEPQLOperatorToJPQLOperator(final EpQLTerm epQLTerm) {
		final EpQLOperator operator = epQLTerm.getOperator();
		String jpqlOperator = operator.asString();
		if (EpQLOperator.NOT_EQUAL == operator) {
			jpqlOperator = JPQL_NOT_EQUAL;
		}
		return jpqlOperator;
	}
}
