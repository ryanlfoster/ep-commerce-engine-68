package com.elasticpath.ql.custom.setting;

import java.util.List;

import com.elasticpath.ql.parser.EpQLField;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.ql.parser.query.NativeQuery;
import com.elasticpath.ql.parser.query.SQLQuery;
import com.elasticpath.ql.parser.querybuilder.impl.SQLSubQueryBuilder;

/**
 * <code>SQLSettingSubQueryBuilder</code> extends conventional <code>SQLSubQueryBuilder</code> to provide custom behavior. The requirement
 * to search a setting definition by metadata implies necessity to resolve EP QL's MetadataKey field into two SQL's fields.
 */
public class SQLSettingSubQueryBuilder extends SQLSubQueryBuilder {

	private static final int LIKE_MATCH_NUMBER = 2;
	private static final int CHILD_MATCH_NUMBER = 1;
	private static final int EXACT_MATCH_NUMBER = 3;
	private static final String NOT_LIKE = " NOT LIKE ";
	private static final String LIKE = " LIKE ";
	private static final String AND = " AND ";
	private static final String OR = " OR "; //NOPMD

	@Override
	public NativeQuery buildQuery(final NativeResolvedTerm nativeResolvedTerm, final EpQLTerm epQLTerm) throws ParseException {
		if (epQLTerm.getEpQLField() == EpQLField.METADATAKEY) {
			return buildSubQueryForMetadata(nativeResolvedTerm, epQLTerm);			
		} else if (epQLTerm.getEpQLField() == EpQLField.NAMESPACE) {
			return buildSubQueryForNamespace(nativeResolvedTerm, epQLTerm);
		} else {
			return super.buildQuery(nativeResolvedTerm, epQLTerm);
		}

	}
	
	/**
	 * Builds SQL subquery for metadata which will be either single SQL term or combination of two terms depending on
	 * the number of resolved values.
	 * 
	 * @param nativeResolvedTerm NativeResolvedTerm
	 * @param epQLTerm EpQLTerm
	 * @return native SQL subquery
	 */
	NativeQuery buildSubQueryForMetadata(final NativeResolvedTerm nativeResolvedTerm, final EpQLTerm epQLTerm) {
		String sqlOperator = convertEPQLOperatorToSQLOperator(epQLTerm);
		
		final List<String> resolvedValues = nativeResolvedTerm.getResolvedValues();
		
		final StringBuffer subQuery = new StringBuffer();
		subQuery.append(nativeResolvedTerm.getResolvedMultiField()[0]).append(SQL_EQUAL).append(resolvedValues.get(0));
		
		if (hasMetadataValueSpecified(resolvedValues)) {
			subQuery.append(AND).append(nativeResolvedTerm.getResolvedMultiField()[1]).append(sqlOperator).append(resolvedValues.get(1));
		}
		return new SQLQuery(subQuery.toString());
	}
	
	private boolean hasMetadataValueSpecified(final List<String> resolvedValues) {
		return resolvedValues.size() > 1;
	}
	
	/**
	 * Builds SQL subquery for EP QL's namespace subquery. Depending on the number of resolved values for a namespace 
	 * constitute either single term or compound SQL subquery. <br>
	 * Example:<br>
	 * for namespace='A/B' will yield SQL: namespace='A/B'<br>
	 * for namespace='A/B/' will yield SQL: namespace LIKE 'A/B/%'<br>
	 * for namespace='A/B/%' will yield SQL: namespace LIKE 'A/B/%' AND namespace NOT LIKE 'A/B/%/%'<br>
	 * for namespace='A/B%' will yield SQL: namespace LIKE 'A/B%' AND namespace NOT LIKE 'A/B%/%'<br>
	 * for namespace!='A/B' will yield SQL: namespace<>'A/B'<br>
	 * for namespace!='A/B/' will yield SQL: namespace NOT LIKE 'A/B/%'<br>
	 * for namespace!='A/B/%' will yield SQL: namespace NOT LIKE 'A/B/%' OR namespace LIKE 'A/B/%/%'<br>
	 * for namespace!='A/B%' will yield SQL: namespace NOT LIKE 'A/B%' OR namespace LIKE 'A/B%/%'<br>
	 * @param nativeResolvedTerm NativeResolvedTerm
	 * @param epQLTerm EpQLTerm
	 * @return native SQL subquery
	 * @throws ParseException in case of errors
	 */
	NativeQuery buildSubQueryForNamespace(final NativeResolvedTerm nativeResolvedTerm, final EpQLTerm epQLTerm) throws ParseException {
		final StringBuffer subQuery = new StringBuffer();
		subQuery.append(nativeResolvedTerm.getResolvedField());
			
		switch (epQLTerm.getOperator()) {
		case EQUAL:
			buildEqualsPart(nativeResolvedTerm, subQuery);
			break;
		case NOT_EQUAL:
			buildNotEqualsPart(nativeResolvedTerm, subQuery);
			break;
		default:
			throw new ParseException("Unsupported operator: " + epQLTerm.getOperator());
		}
		return new SQLQuery(subQuery.toString());
	}

	private void buildNotEqualsPart(final NativeResolvedTerm nativeResolvedTerm, final StringBuffer subQuery) throws ParseException {
		final List<String> resolvedValues = nativeResolvedTerm.getResolvedValues();
		
		if (resolvedValues.size() == CHILD_MATCH_NUMBER) {
			subQuery.append(NOT_LIKE).append(resolvedValues.get(0));
		} else if (resolvedValues.size() == LIKE_MATCH_NUMBER) {
			subQuery.append(NOT_LIKE).append(resolvedValues.get(0)).append(OR).append(nativeResolvedTerm.getResolvedField()).append(LIKE);
			subQuery.append(resolvedValues.get(1));
		} else if (resolvedValues.size() == EXACT_MATCH_NUMBER) {
			subQuery.append(SQL_NOT_EQUAL).append(resolvedValues.get(0));
			resolvedValues.remove(2);
			resolvedValues.remove(1);
			nativeResolvedTerm.setResolvedValues(resolvedValues);
		} else {
			throw new ParseException("Wrong number of values passed: " + resolvedValues.size() + ". Maximum is " + EXACT_MATCH_NUMBER);
		}
	}

	private void buildEqualsPart(final NativeResolvedTerm nativeResolvedTerm, final StringBuffer subQuery) throws ParseException {
		final List<String> resolvedValues = nativeResolvedTerm.getResolvedValues();
		
		if (resolvedValues.size() == CHILD_MATCH_NUMBER) {
			subQuery.append(LIKE).append(resolvedValues.get(0));
		} else if (resolvedValues.size() == LIKE_MATCH_NUMBER) {
			subQuery.append(LIKE).append(resolvedValues.get(0)).append(AND).append(nativeResolvedTerm.getResolvedField()).append(NOT_LIKE);
			subQuery.append(resolvedValues.get(1));
		} else if (resolvedValues.size() == EXACT_MATCH_NUMBER) {
			subQuery.append(SQL_EQUAL).append(resolvedValues.get(0));
			resolvedValues.remove(2);
			resolvedValues.remove(1);
			nativeResolvedTerm.setResolvedValues(resolvedValues);
		} else {
			throw new ParseException("Wrong number of values passed: " + resolvedValues.size() + ". Maximum is " + EXACT_MATCH_NUMBER);
		}
	}
}
