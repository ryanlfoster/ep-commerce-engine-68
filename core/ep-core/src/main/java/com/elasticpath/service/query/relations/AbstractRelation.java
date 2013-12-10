/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.query.relations;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.service.query.IdentifierType;
import com.elasticpath.service.query.Relation;
import com.elasticpath.service.query.RelationJoin;

/**
 * Common functionality for query relationships.
 *
 * @param <T> the generic type
 */
public abstract class AbstractRelation<T> implements Relation<T> {

	private static final long serialVersionUID = 1L;

	private final Map<Class<?>, RelationJoin> relationMap = initializeRelationMap();

	private final Map<IdentifierType, String> identifierMap = initializeIdentifierMap();

	/**
	 * Stores query segments that will use exact equality to determine resulting matches. <br>
	 * The Collection is composed of the potential values to match against.
	 */
	private final Map<IdentifierType, Collection<?>> valueMap = new HashMap<IdentifierType, Collection<?>>();

	/**
	 * Stores query segments that will use "like" to determine resulting matches. <br>
	 * The String value is the token to match against.
	 */
	private final Map<IdentifierType, String> likeValueMap = new HashMap<IdentifierType, String>();

	/**
	 * Initialize identifier map for this object.
	 *
	 * @return the map
	 */
	protected abstract Map<IdentifierType, String> initializeIdentifierMap();

	/**
	 * Initialize relation map.
	 *
	 * @return the map
	 */
	protected abstract Map<Class<?>, RelationJoin> initializeRelationMap();

	@Override
	public Collection<IdentifierType> getSupportedIdentifiers() {
		return getIdentifierMap().keySet();
	}

	@Override
	public String getIdentifierColumn(final IdentifierType idType) {
		return getIdentifierMap().get(idType);
	}

	@Override
	public boolean hasValuesForIdentifier(final IdentifierType idType) {
		return getValueMap().containsKey(idType);
	}

	@Override
	public Collection<?> getValuesForIdentifier(final IdentifierType idType) {
		return getValueMap().get(idType);
	}

	protected Map<IdentifierType, String> getIdentifierMap() {
		return identifierMap;
	}

	@Override
	public boolean hasLikeValueForIdentifier(final IdentifierType idType) {
		return getLikeValueMap().containsKey(idType);
	}

	@Override
	public String getLikeValueForIdentifier(final IdentifierType idType) {
		return getLikeValueMap().get(idType);
	}

	@Override
	public RelationJoin relationWith(final Class<?> relationClass) {
		return getRelationMap().get(relationClass);
	}

	protected Map<Class<?>, RelationJoin> getRelationMap() {
		return relationMap;
	}

	protected Map<IdentifierType, Collection<?>> getValueMap() {
		return valueMap;
	}

	protected Map<IdentifierType, String> getLikeValueMap() {
		return likeValueMap;
	}

	/**
	 * A null-safe collection. If a null is passed in for a collection, make it an empty collection instead.
	 *
	 * @param <V> the generic type
	 * @param values the values
	 * @return the collection
	 */
	protected <V> Collection<V> safeCollection(final Collection<V> values) {
		Collection<V> safeValues = values;
		if (safeValues == null) {
			safeValues = Collections.emptyList();
		}
		return safeValues;
	}

	/**
	 * Adds values for the given identifier.
	 *
	 * @param idType the identifier type
	 * @param values the values
	 */
	protected void addValuesForIdentifier(final IdentifierType idType, final Collection<?> values) {
		getValueMap().put(idType, safeCollection(values));
	}

	/**
	 * Adds a like value for the given identifier.
	 *
	 * @param idType the identifier type
	 * @param value the value
	 */
	protected void addLikeValueForIdentifier(final IdentifierType idType, final String value) {
		getLikeValueMap().put(idType, value);
	}

	/**
	 * Defines the relationships with other objects.
	 */
	protected class RelationInfo implements RelationJoin {

		private static final long serialVersionUID = 1L;

		private final String joinField;

		private final String joinAlias;

		private final String joinClause;

		private final String clauseField;

		private final RelationJoin joinRelation;

		/**
		 * Instantiates a new relation info.
		 *
		 * @param joinField the join field
		 */
		public RelationInfo(final String joinField) {
			this(joinField, null, null, null, null);
		}

		/**
		 * Instantiates a new relation info.
		 *
		 * @param joinField a field on the original object to join with
		 * @param joinAlias an alias to use for the join field
		 * @param joinClause a join clause that relates the objects
		 * @param clauseField a field to include in the where clause for this relationship
		 * @param joinRelation another relation object to join to
		 */
		public RelationInfo(final String joinField,
				final String joinAlias,
				final String joinClause,
				final String clauseField,
				final RelationJoin joinRelation) {
			super();
			this.joinField = joinField;
			this.joinAlias = joinAlias;
			this.joinClause = joinClause;
			this.clauseField = clauseField;
			this.joinRelation = joinRelation;
		}

		@Override
		public String getJoinField() {
			return joinField;
		}

		@Override
		public String getJoinClause() {
			return joinClause;
		}

		@Override
		public String getClauseField() {
			return clauseField;
		}

		@Override
		public String getJoinAlias() {
			return joinAlias;
		}

		@Override
		public RelationJoin getJoinRelation() {
			return joinRelation;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getRelationClass().getName())
				.append(valueMap)
				.toHashCode();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AbstractRelation<T> other = (AbstractRelation<T>) obj;
		return new EqualsBuilder()
				.append(getRelationClass().getName(), other.getRelationClass().getName())
				.append(valueMap, other.valueMap)
				.isEquals();
	}

}
