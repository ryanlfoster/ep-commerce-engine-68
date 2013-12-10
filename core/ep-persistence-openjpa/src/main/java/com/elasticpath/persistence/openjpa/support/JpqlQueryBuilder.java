package com.elasticpath.persistence.openjpa.support;

import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup.ConjunctionType;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides a class for building JPQL queries.
 * 
 * Once a query is constructed, call {@link #toString()} to get the actual query.
 */
public class JpqlQueryBuilder {
	private static final String CLOSE_BRACKET = ")";
	private static final String OPEN_BRACKET = "(";
	private static final String COUNT = "count";
	private static final String QUERY_JOIN = " JOIN ";
	private static final String QUERY_LEFT_JOIN = " LEFT JOIN ";
	private static final String QUERY_AS = " AS ";
	private static final String QUERY_FROM = " FROM ";
	private static final String QUERY_SELECT = "SELECT ";
	private static final String QUERY_DISTINCT = "DISTINCT ";
	private static final String QUERY_WHERE = " WHERE ";
	private static final String QUERY_AND = " AND ";
	private static final char QUERY_SPACE = ' ';

	private final String selectFields;
	private final JpqlTable rootTable = new JpqlTable();
	private final List<JpqlTable> joinTables = new ArrayList<JpqlTable>();
	private final Set<String> tableAliases = new HashSet<String>();
	private final JpqlQueryBuilderWhereGroup defaultWhereGroup =
		new JpqlQueryBuilderWhereGroup(ConjunctionType.AND);
	private final List<JpqlOrderByClause> orderByClauses = new ArrayList<JpqlOrderByClause>();
	private final List<JpqlGroupByClause> groupByClauses = new ArrayList<JpqlGroupByClause>();
	private boolean countOnly;
	private boolean distinctOnly;

	/**
	 * Table join type.
	 */
	private enum JpqlJoinTypeEnum {
		INNER_JOIN,

		LEFT_JOIN
	}

	/**
	 * Constructor containing root table name (i.e. {@code OrderImpl}) and root table alias (i.e. {@code o}). Assumes that the
	 * entire root table object should be returned.
	 * 
	 * @param tableName name of the root table (i.e. {@code OrderImpl})
	 * @param alias for the root table (i.e. {@code o})
	 */
	public JpqlQueryBuilder(final String tableName, final String alias) {
		this(tableName, alias, false);
	}

	/**
	 * Constructor containing root table name (i.e. {@code OrderImpl}) and root table alias (i.e. {@code o}). Assumes
	 * that the entire root table object should be returned.
	 * 
	 * @param tableName name of the root table (i.e. {@code OrderImpl})
	 * @param alias for the root table (i.e. {@code o})
	 * @param countOnly whether to perform a count of the results only
	 */
	public JpqlQueryBuilder(final String tableName, final String alias, final boolean countOnly) {
		rootTable.tableName = tableName;
		rootTable.alias = alias;
		tableAliases.add(alias);
		this.selectFields = alias;
		this.countOnly = countOnly;
	}

	/**
	 * Constructor containing root table name (i.e. {@code OrderImpl}), root table alias (i.e. {@code o}), and select fields (i.e.
	 * {@code o.uidPk, o.customer.userId}).
	 * 
	 * @param tableName name of the root table (i.e. {@code OrderImpl})
	 * @param alias for the root table (i.e. {@code o})
	 * @param selectFields the fields to return in the results
	 */
	public JpqlQueryBuilder(final String tableName, final String alias, final String selectFields) {
		rootTable.tableName = tableName;
		rootTable.alias = alias;
		tableAliases.add(alias);
		this.selectFields = selectFields;
	}

	/**
	 * Fetch distinct objects i.e. add DISTINCT clause to select statement.
	 */
	public void distinct() {
		this.distinctOnly = true;
	}
	
	/**
	 * Add an inner join to the query. Generally used for one-to-many relationships.
	 * 
	 * @param tableName name of the table to join (i.e. {@code o.shipments})
	 * @param alias for the joined table (i.e. {@code os})
	 */
	public void appendInnerJoin(final String tableName, final String alias) {
		if (!tableAliases.contains(alias)) {
			JpqlTable joinTable = new JpqlTable();
			joinTable.tableName = tableName;
			joinTable.alias = alias;
			joinTable.joinType = JpqlJoinTypeEnum.INNER_JOIN;
			joinTables.add(joinTable);
			tableAliases.add(alias);
		}
	}

	/**
	 * Add an inner join to the query using theta-join syntax. Generally used for one-to-many relationships in which the
	 * other objects in the query do not define a direct jpa annotation relationship.
	 * 
	 * @param tableName name of the object to join (i.e. {@code PhysicalOrderShipmentImpl})
	 * @param alias for the joined table (i.e. {@code phys})
	 * @param onFields the fields to use for the join (i.e. {@code os.uidPk = phys.orderInternal.uidPk})
	 */
	public void appendInnerJoin(final String tableName, final String alias, final String onFields) {
		if (!tableAliases.contains(alias)) {
			JpqlTable joinTable = new JpqlTable();
			joinTable.tableName = tableName;
			joinTable.alias = alias;
			joinTable.onFields = onFields;
			joinTable.joinType = JpqlJoinTypeEnum.INNER_JOIN;
			joinTables.add(joinTable);
			tableAliases.add(alias);
		}
	}

	/**
	 * Add a left outer join to the query. Generally used for one-to-many relationships.
	 * 
	 * @param tableName name of the table to join (i.e. {@code o.shipments})
	 * @param alias for the joined table (i.e. {@code os})
	 */
	public void appendLeftJoin(final String tableName, final String alias) {
		if (!tableAliases.contains(alias)) {
			JpqlTable joinTable = new JpqlTable();
			joinTable.tableName = tableName;
			joinTable.alias = alias;
			joinTable.joinType = JpqlJoinTypeEnum.LEFT_JOIN;
			joinTables.add(joinTable);
			tableAliases.add(alias);
		}
	}

	/**
	 * Add a left outer to the query using theta-join syntax. Generally used for one-to-many relationships in which the
	 * other objects in the query do not define a direct jpa annotation relationship.
	 * 
	 * @param tableName name of the object to join (i.e. {@code PhysicalOrderShipmentImpl})
	 * @param alias for the joined table (i.e. {@code phys})
	 * @param onFields the fields to use for the join (i.e. {@code os.uidPk = phys.orderInternal.uidPk})
	 */
	public void appendLeftJoin(final String tableName, final String alias, final String onFields) {
		if (!tableAliases.contains(alias)) {
			JpqlTable joinTable = new JpqlTable();
			joinTable.tableName = tableName;
			joinTable.alias = alias;
			joinTable.onFields = onFields;
			joinTable.joinType = JpqlJoinTypeEnum.LEFT_JOIN;
			joinTables.add(joinTable);
			tableAliases.add(alias);
		}
	}

	/**
	 * Add an order by clause.
	 *
	 * @param fieldName the name of the field to sort on
	 * @param ascending set to true for ascending sort, set to false for descending sort
	 */
	public void appendOrderBy(final String fieldName, final boolean ascending) {
		JpqlOrderByClause orderBy = new JpqlOrderByClause();
		orderBy.fieldName = fieldName;
		if (ascending) {
			orderBy.direction = "ASC";
		} else {
			orderBy.direction = "DESC";
		}
		orderByClauses.add(orderBy);
	}
	
	/**
	 * Add a GROUP BY clause.
	 *
	 * @param fieldName the name of the field to add in the GROUP BY clause
	 */
	public void appendGroupBy(final String fieldName) {
		JpqlGroupByClause groupBy = new JpqlGroupByClause();
		groupBy.fieldName = fieldName;
		groupByClauses.add(groupBy);
	}

	/**
	 * The default where group for this query builder (uses AND as a conjunction).
	 *
	 * @return whereGroup object which can be used to add conditionals
	 */
	public JpqlQueryBuilderWhereGroup getDefaultWhereGroup() {
		return defaultWhereGroup;
	}

	/**
	 * The JPQL query generated using the constructors and append methods.
	 * 
	 * @return JPQL query string
	 */
	@Override
	public String toString() {
		boolean whereUsed = false;
		final StringBuffer query = new StringBuffer();
		query.append(QUERY_SELECT);
		if (this.distinctOnly) {
			query.append(QUERY_DISTINCT);
		}

		if (countOnly) {
			query.append(COUNT).append(OPEN_BRACKET).append(selectFields).append(CLOSE_BRACKET);
		} else {
			query.append(selectFields);
		}
		query.append(QUERY_FROM).append(rootTable.tableName).append(QUERY_AS).append(rootTable.alias);

		for (JpqlTable joinTable : joinTables) {
			if (JpqlJoinTypeEnum.LEFT_JOIN == joinTable.joinType) {
				query.append(QUERY_LEFT_JOIN).append(joinTable.tableName).append(QUERY_AS).append(joinTable.alias);
			} else if (joinTable.onFields == null) {
				query.append(QUERY_JOIN).append(joinTable.tableName).append(QUERY_AS).append(joinTable.alias);
			} else {
				query.append(", ").append(joinTable.tableName).append(QUERY_AS).append(joinTable.alias);
			}
		}
		for (JpqlTable joinTable : joinTables) {
			if (joinTable.onFields != null) {
				if (whereUsed) {
					query.append(QUERY_AND);
				} else {
					query.append(QUERY_WHERE);
					whereUsed = true;
				}
				query.append(joinTable.onFields);
			}
		}
		String whereClause = defaultWhereGroup.toQueryString();
		if (!StringUtils.isEmpty(whereClause)) {
			if (whereUsed) {
				query.append(QUERY_AND);
			} else {
				query.append(QUERY_WHERE);
				whereUsed = true;
			}
			query.append(whereClause);
		}
		
		appendClauseToSelect(query, groupByClauses);
		appendClauseToSelect(query, orderByClauses);
		return query.toString();
	}

	private boolean appendClauseToSelect(final StringBuffer query, final List<? extends JpqlClause> clausesList) {
		boolean clauseUsed = false;
		for (JpqlClause clauseField : clausesList) {
			if (clauseUsed) {
				query.append(", ");
			} else {
				query.append(clauseField.getSqlClause());
				clauseUsed = true;
			}
			query.append(clauseField.getSqlClauseFieldFormat());
		}
		return clauseUsed;
	}
	
	/**
	 * The parameters that must be passed to the persistence engine with the JPQL query.
	 * 
	 * @return List of parameters representing each of the placeholders in the JPQL query string
	 */
	public List<Object> getParameterList() {
		return defaultWhereGroup.getParameterList();
	}

	/**
	 * Data structure for storing table join information.
	 */
	private class JpqlTable {

		/**
		 * Name of the JPA entity to use as the root or join.
		 */
		private String tableName;

		/**
		 * Name of the alias to use when referring to fields of the JPA entity.
		 */
		private String alias;

		/**
		 * Clause to use for joining the JPA entity to other JPA entities (if using theta-join style).
		 */
		private String onFields;

		/**
		 * Type of join to use for the table (left outer, inner, etc).
		 */
		private JpqlJoinTypeEnum joinType;

	}
	
	/**
	 * 
	 * Abstract class used for marking the common things for clauses.
	 */
	private interface  JpqlClause {
		/**
		 * Get the format for the sql for this object. 
		 *
		 * @return string with the format to be used in the sql
		 */
		String getSqlClauseFieldFormat(); 
		
		/**
		 * SQL clause text to be used.
		 *
		 * @return SQL clause
		 */
		String getSqlClause();
	}
	
	/**
	 * Data structure for storing order by clause information.
	 */
	private class JpqlOrderByClause implements JpqlClause {
		/**
		 * Name of the field to use for sorting.
		 */
		private String fieldName;

		/**
		 * Sort direction.
		 */
		private String direction;
		
		@Override
		public String getSqlClauseFieldFormat() {
			final StringBuilder builder = new StringBuilder();
			builder.append(fieldName).append(QUERY_SPACE).append(direction);
			return builder.toString();
		}
		
		@Override
		public String getSqlClause() {
			return " ORDER BY ";
		}
	}
	
	/**
	 * Data structure for storing order by clause information.
	 */
	private class JpqlGroupByClause implements JpqlClause {
		/**
		 * Name of the field to use for group by.
		 */
		private String fieldName;
		
		@Override
		public String getSqlClauseFieldFormat() {
			return fieldName;
		}
		
		@Override
		public String getSqlClause() {
			return " GROUP BY ";
		}
	}
	
	/**
	 * Create a new where group for this query builder (uses AND as a conjunction).
	 *
	 * @return whereGroup object which can be used to add conditionals
	 */
	public JpqlQueryBuilderWhereGroup createNewWhereGroup() {
		return new JpqlQueryBuilderWhereGroup(ConjunctionType.AND);
	}
}
