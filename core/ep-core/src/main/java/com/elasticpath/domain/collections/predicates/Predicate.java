package com.elasticpath.domain.collections.predicates;

import com.elasticpath.persistence.api.Entity;
/**
 * Selector for matching an entity against some selection criteria.
 *
 */
public interface Predicate {
	
	/**
	 * Indicates whether an entity matches the selection criteria. 
	 * 
	 * @param entity The entity to match against the selection criteria
	 * @return true if the entity passed in matches the selection criteria; false otherwise.
	 */
	boolean apply(final Entity entity);
}
