package com.elasticpath.service.query;

import java.io.Serializable;

/**
 * Defines how a relation joins to another object.
 */
public interface RelationJoin extends Serializable {

	/**
	 * Gets the join field.
	 *
	 * @return the join field
	 */
	String getJoinField();

	/**
	 * Gets the join alias.
	 *
	 * @return the join alias
	 */
	String getJoinAlias();

	/**
	 * Gets the field name to include in a where clause.
	 *
	 * @return the clause field
	 */
	String getClauseField();

	/**
	 * Gets the join clause.
	 *
	 * @return the join clause
	 */
	String getJoinClause();

	/**
	 * Gets the name of another relation to join with.
	 *
	 * @return the join object
	 */
	RelationJoin getJoinRelation();

}