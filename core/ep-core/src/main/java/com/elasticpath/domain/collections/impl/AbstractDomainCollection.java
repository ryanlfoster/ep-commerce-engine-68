package com.elasticpath.domain.collections.impl;

import java.util.Collection;

import com.elasticpath.domain.collections.predicates.Predicate;
import com.elasticpath.persistence.api.Entity;

/**
 * Collection of domain entities with behaviour for filtering and lookup by different types of criteria.
 * Filtering is done by passing in an instance of a Selector, such that only if an entity in the collection
 * matches the criteria in the Selector is it included in the results.
 *
 * @param <T>
 */
public abstract class AbstractDomainCollection<T extends Entity> {

	@SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
	private Collection<T> entities;
	
	/**
	 * Returns the product sku with guid matching <code>guid</code>.
	 * 
	 * @param guid The guid to lookup the product sku with
	 * @return Product sku with matching guid or null if not found.
	 */
	public T byGuid(final String guid) {
		for (T entity : entities) {
			if (entity.getGuid().equals(guid)) {
				return entity;
			}
		}
		return null;
	}	

	/**
	 * Indicates whether the collection contains a product sku with the given <code>guid</code>.
	 * 
	 * @param guid The guid to lookup the product sku with
	 * @return true if the collection contains a product sku with <code>guid</code>; false otherwise
	 */
	public boolean contains(final String guid) {
		for (T entity : entities) {
			if (entity.getGuid().equals(guid)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns ProductSkus collection that match the selector.
	 * 
	 * @param predicate Selection critera for which product skus to include in the result.
	 * @return Collection containing ProductSkus matching the criteria of the Selector 
	 */
	public AbstractDomainCollection<T> filter(final Predicate predicate) {
		AbstractDomainCollection<T> collector = newInstance();
		for (T entity : getEntities()) {
			if (predicate.apply(entity)) {
				collector.add(entity);
			}
		}
		return collector;
	}
	
	
	/**
	 * Returns of new empty instance of this collection.
	 * 
	 * @return a new empty instance of this collection
	 */
	abstract AbstractDomainCollection<T> newInstance();

	/**
	 * Adds a new entity to the collection of entities.
	 * 
	 * @param entity The entity to add to the collection.
	 */
	private void add(final T entity) {
		entities.add(entity);
	}

	/**
	 * Returns collection of entities.
	 * 
	 * @return collection of entities.
	 */
	public Collection<T> getEntities() {
		return entities;
	}
	
	/**
	 * Sets new collection of entities into <code>entities</code>.
	 * 
	 * @param entities New collection of entities
	 */
	public void setEntities(final Collection<T> entities) {
		this.entities = entities;
	}

}
