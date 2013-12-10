package com.elasticpath.persistence.openjpa.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a where clause group (statements within parentheses) for the {@link JpqlQueryBuilder}.
 */
public class JpqlQueryBuilderWhereGroup {
	private static final String QUERY_IN = "IN";
	private static final String QUERY_LIKE = "LIKE";
	private static final String QUERY_AND = " AND ";
	private static final String QUERY_OR = " OR ";
	private static final String QUERY_IS_EMPTY = " IS EMPTY";
	private static final String QUERY_TIMESTAMP = "CURRENT_TIMESTAMP";
	private static final String QUERY_IS_NULL = " IS NULL";
	private static final char QUERY_SPACE = ' ';
	private static final char QUERY_PARAMETERPREFIX = '?';
	private static final char QUERY_WILDCARD = '%';

	private final ConjunctionType conjunctionType;

	private final List<JpqlWhereClause> whereClauses = new ArrayList<JpqlWhereClause>();

	private final List<JpqlQueryBuilderWhereGroup> whereGroups = new ArrayList<JpqlQueryBuilderWhereGroup>();

	/**
	 * Constructor which indicates the group conjunction type.
	 * 
	 * @param conjunctionType the type of conjunction to use in this group (AND or OR)
	 */
	public JpqlQueryBuilderWhereGroup(final ConjunctionType conjunctionType) {
		this.conjunctionType = conjunctionType;
	}

	/**
	 * Add a where group with a particular conjunction type to the query.
	 * 
	 * @param whereGroup the group to append to the query
	 */
	public void appendWhereGroup(final JpqlQueryBuilderWhereGroup whereGroup) {
		whereGroups.add(whereGroup);
	}

	/**
	 * Add a comparison to the query.
	 * 
	 * @param fieldName the name of the field to evaluate against
	 * @param operator the type of operator to use for the comparison
	 * @param value the value to compare to the fieldName
	 * @param matchType the match type to use for the value
	 */
	public void appendWhere(final String fieldName, final String operator, final Object value, final JpqlMatchType matchType) {
		JpqlWhereClause whereClause = new JpqlWhereClause();
		whereClause.fieldName = fieldName;
		whereClause.operator = operator;
		whereClause.value = value;
		whereClause.valueType = matchType;
		whereClauses.add(whereClause);
	}

	/**
	 * Add an empty check to the query.
	 * 
	 * @param fieldName the name of the field to evaluate against
	 */
	public void appendWhereEmpty(final String fieldName) {
		appendWhere(fieldName, QUERY_IS_EMPTY, null, JpqlMatchType.AS_IS);
	}

	/**
	 * Add an equals comparison to the query.
	 * 
	 * @param fieldName the name of the field to evaluate against
	 * @param value the value to compare to the fieldName
	 */
	public void appendWhereEquals(final String fieldName, final Object value) {
		appendWhere(fieldName, "=", value, JpqlMatchType.AS_IS);
	}

	/**
	 * Add a substring comparison to the query. All rows with fieldName containing value.
	 * 
	 * @param fieldName the name of the field to evaluate against
	 * @param value the value to compare to the fieldName
	 */
	public void appendLikeWithWildcards(final String fieldName, final String value) {
		appendWhere(fieldName, QUERY_LIKE, value, JpqlMatchType.SUBSTRING);
	}

	/**
	 * Add a prefix comparison to the query. All rows with fieldName starting with value.
	 * 
	 * @param fieldName the name of the field to evaluate against
	 * @param value the value to compare to the fieldName
	 */
	public void appendLikeWildcardOnEnd(final String fieldName, final String value) {
		appendWhere(fieldName, QUERY_LIKE, value, JpqlMatchType.STRING_PREFIX);
	}

	/**
	 * Add a postfix comparison to the query. All rows with fieldName ending with value.
	 * 
	 * @param fieldName the name of the field to evaluate against
	 * @param value the value to compare to the fieldName
	 */
	public void appendLikeWildcardOnStart(final String fieldName, final String value) {
		appendWhere(fieldName, QUERY_LIKE, value, JpqlMatchType.STRING_POSTFIX);
	}

	/**
	 * Add an equals comparison against a collection of values. All rows that equal one of the values.
	 * 
	 * @param fieldName the name of the field to evaluate against
	 * @param values the values to compare to the fieldName
	 */
	public void appendWhereInCollection(final String fieldName, final Collection< ? > values) {
		if (values != null && values.size() == 1) {
			appendWhere(fieldName, "=", values.iterator().next(), JpqlMatchType.AS_IS);
		} else {
			appendWhere(fieldName, QUERY_IN, values, JpqlMatchType.AS_IS);
		}
	}

	/**
	 * The JPQL query generated using the constructor and append methods.
	 * 
	 * @return JPQL query string
	 */
	public String toQueryString() {
		return toQueryString(1);
	}

	/**
	 * The JPQL query generated using the constructor and append methods.
	 * 
	 * @param startingParameterIndex the starting number to use when indexing parameters
	 * @return JPQL query string
	 */
	protected String toQueryString(final int startingParameterIndex) {
		StringBuffer query = new StringBuffer();
		int parameterIndex = startingParameterIndex;
		boolean conjunctionNeeded = false;
		for (JpqlWhereClause whereClause : whereClauses) {
			if (conjunctionNeeded) {
				if (conjunctionType == ConjunctionType.AND) {
					query.append(QUERY_AND);
				} else if (conjunctionType == ConjunctionType.OR) {
					query.append(QUERY_OR);
				}
			} else {
				conjunctionNeeded = true;
			}
			if (whereClause.valueType == JpqlMatchType.IS_EMPTY) {
				query.append(whereClause.fieldName).append(QUERY_IS_EMPTY);
			} else if (whereClause.valueType == JpqlMatchType.TIMESTAMP) {
				query.append(whereClause.fieldName).append(QUERY_SPACE).append(whereClause.operator).append(QUERY_SPACE)
					.append(QUERY_TIMESTAMP);
			} else if (whereClause.value == null) {
					query.append(whereClause.fieldName).append(QUERY_IS_NULL);
			} else if (whereClause.operator.equalsIgnoreCase(QUERY_IN)) {
				query.append(whereClause.fieldName).append(QUERY_SPACE).append(whereClause.operator).append(QUERY_SPACE)
					.append('(').append(QUERY_PARAMETERPREFIX).append(parameterIndex).append(')');
				parameterIndex++;
			} else {
				query.append(whereClause.fieldName).append(QUERY_SPACE).append(whereClause.operator).append(QUERY_SPACE)
					.append(QUERY_PARAMETERPREFIX).append(parameterIndex);
				parameterIndex++;
			}
		}
		for (JpqlQueryBuilderWhereGroup whereGroup : whereGroups) {
			if (parameterIndex > startingParameterIndex) {
				if (conjunctionType == ConjunctionType.AND) {
					query.append(QUERY_AND);
				} else if (conjunctionType == ConjunctionType.OR) {
					query.append(QUERY_OR);
				}
			}
			query.append('(');
			query.append(whereGroup.toQueryString(parameterIndex));
			query.append(')');
			parameterIndex += whereGroup.getParameterList().size();
		}
		return query.toString();
	}

	/**
	 * The parameters that must be passed to the persistence engine with the JPQL query.
	 * 
	 * @return List of parameters representing each of the placeholders in the JPQL query string
	 */
	public List<Object> getParameterList() {
		final List<Object> parameterList = new ArrayList<Object>();
		for (JpqlWhereClause whereClause : whereClauses) {
			if (whereClause.value != null
					&& whereClause.valueType != JpqlMatchType.IS_EMPTY
					&& whereClause.valueType != JpqlMatchType.TIMESTAMP) {
				if (whereClause.operator.equalsIgnoreCase(QUERY_LIKE)) {
					if (whereClause.valueType == JpqlMatchType.AS_IS) {
						parameterList.add(whereClause.value);
					} else if (whereClause.valueType == JpqlMatchType.SUBSTRING) {
						// TODO: Escape any wildcard characters in value.
						parameterList.add(QUERY_WILDCARD + ((String) whereClause.value) + QUERY_WILDCARD);
					} else if (whereClause.valueType == JpqlMatchType.STRING_PREFIX) {
						// TODO: Escape any wildcard characters in value.
						parameterList.add(((String) whereClause.value) + QUERY_WILDCARD);
					} else if (whereClause.valueType == JpqlMatchType.STRING_POSTFIX) {
						// TODO: Escape any wildcard characters in value.
						parameterList.add(QUERY_WILDCARD + ((String) whereClause.value));
					}
				} else {
					parameterList.add(whereClause.value);
				}
			}
		}
		for (JpqlQueryBuilderWhereGroup whereGroup : whereGroups) {
			parameterList.addAll(whereGroup.getParameterList());
		}
		return parameterList;
	}

	/**
	 * Data structure for storing where clause information.
	 */
	private class JpqlWhereClause {
		/**
		 * Name of the field to use for the where clause comparison.
		 */
		private String fieldName;
		/**
		 * Operator to use for the where clause comparison.
		 */
		private String operator;
		/**
		 * Value to compare to the fieldName.
		 */
		private Object value;

		/**
		 * Value type to use for the where clause.  Indicates whether to modify the value with wildcard characters, do an EMPTY match,
		 * use the current timestamp, etc.
		 */
		private JpqlMatchType valueType;

	}

	/**
	 * Conjunction type for where clauses.
	 */
	public static enum ConjunctionType {
		/** Join where clause conditions using AND logical conjunction. */
		AND,

		/** Join where clause conditions using OR logical conjunction. */
		OR
	}

	/**
	 * Where clause match type enum.
	 */
	public static enum JpqlMatchType {
		/**Do an exact match on the value - make no modifications to the value string. */
		AS_IS,

		/** Do a substring match on the value - add wildcard characters to both sides of the value string. */
		SUBSTRING,

		/** Do a prefix match on the value - add a wildcard character to the end of the value string. */
		STRING_PREFIX,

		/** Do a postfix match on the value - add a wildcard character to the start of the value string. */
		STRING_POSTFIX,

		/** Do an empty comparison on the field. */
		IS_EMPTY,

		/** Compare to current timestamp. */
		TIMESTAMP
	}
}
