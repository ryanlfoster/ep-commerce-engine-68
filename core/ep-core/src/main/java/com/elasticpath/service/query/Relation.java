package com.elasticpath.service.query;

import java.io.Serializable;
import java.util.Collection;

/**
 * Defines a query relationship.
 *
 * @param <T> the generic type of this relation object
 */
public interface Relation<T> extends Serializable {

	/**
	 * Gets the bean name.
	 *
	 * @return the bean name
	 */
	String getBeanName();

	/**
	 * Gets the supported identifiers.
	 *
	 * @return the supported identifiers
	 */
	Collection<IdentifierType> getSupportedIdentifiers();

	/**
	 * Gets the identifier column.
	 *
	 * @param idType the id type
	 * @return the identifier column
	 */
	String getIdentifierColumn(IdentifierType idType);

	/**
	 * Gets the alias.
	 *
	 * @return the alias
	 */
	String getAlias();

	/**
	 * Gets the relation class.
	 *
	 * @return the relation class
	 */
	Class<? extends T> getRelationClass();

	/**
	 * Get the join details for a relationship with the given class.
	 *
	 * @param relationClass the relation class
	 * @return the relation join
	 */
	RelationJoin relationWith(Class<?> relationClass);

	/**
	 * Checks for values for identifier.
	 *
	 * @param idType the id type
	 * @return true, if successful
	 */
	boolean hasValuesForIdentifier(IdentifierType idType);

	/**
	 * Gets the values for identifier.
	 *
	 * @param idType the id type
	 * @return the values for identifier
	 */
	Collection<?> getValuesForIdentifier(IdentifierType idType);

	/**
	 * Checks for like values for identifier.
	 *
	 * @param idType the identifier type
	 * @return true, if successful
	 */
	boolean hasLikeValueForIdentifier(IdentifierType idType);

	/**
	 * Gets the like value for identifier.
	 *
	 * @param idType the identifier type
	 * @return the like value for identifier
	 */
	String getLikeValueForIdentifier(IdentifierType idType);

}
