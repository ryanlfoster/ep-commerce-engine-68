package com.elasticpath.ql.parser.querybuilder.impl;

import java.util.List;

import com.elasticpath.ql.parser.EpQLOperator;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.ql.parser.query.NativeQuery;
import com.elasticpath.ql.parser.query.SQLQuery;
import com.elasticpath.ql.parser.querybuilder.SubQueryBuilder;

/**
 * Represents sql sub query builder.
 */
public class SQLSubQueryBuilder implements SubQueryBuilder {
	/** <>. */
	public static final String SQL_NOT_EQUAL = "<>";

	/** =. */
	public static final String SQL_EQUAL = "=";

	@Override
	public NativeQuery buildQuery(final NativeResolvedTerm nativeResolvedTerm, final EpQLTerm epQLTerm) throws ParseException {
		final List<String> resolvedValues = nativeResolvedTerm.getResolvedValues();
		if (resolvedValues.size() != 1) {
			throw new ParseException("Multiple values are not allowed for field " + epQLTerm.getEpQLField());
		}
		final String sqlOperator = convertEPQLOperatorToSQLOperator(epQLTerm);
		return new SQLQuery(nativeResolvedTerm.getResolvedField(), resolvedValues.get(0), sqlOperator);
	}

	/**
	 * Converts EPQL operator to SQL operator.
	 * 
	 * @param epQLTerm EpQLTerm
	 * @return string representation of SQL operator for this sub query
	 */
	protected String convertEPQLOperatorToSQLOperator(final EpQLTerm epQLTerm) {
		final EpQLOperator operator = epQLTerm.getOperator();
		String sqlOperator = operator.asString();
		if (EpQLOperator.NOT_EQUAL == operator) {
			sqlOperator = SQL_NOT_EQUAL;
		}
		return sqlOperator;
	}
}
