package com.elasticpath.tools.sync.merge.impl;

import java.util.IdentityHashMap;
import java.util.Map;

import com.elasticpath.persistence.api.Persistable;

/**
 * This class is responsible for managing cyclic dependencies.
 */
class CyclicDependencyManager {

	private final Map<Persistable, Persistable> dependencyContainer = new IdentityHashMap<Persistable, Persistable>();

	/**
	 * Registers the processed object.
	 * 
	 * @param sourceObject the reference on processed source object
	 * @param targetObject the corresponding target object
	 */
	public void registerProcessedObject(final Persistable sourceObject, final Persistable targetObject) {
		dependencyContainer.put(sourceObject, targetObject);
	}

	/**
	 * Checks if the given object forms cyclic dependency.
	 * 
	 * @param sourceTarget object to check
	 * @return true if object forms cyclic dependency
	 */
	/**
	 * Clear all information about processed objects.
	 */
	public void clearDependencies() {
		dependencyContainer.clear();
	}

	/**
	 * Checks if the given object forms a cycle.
	 * 
	 * @param sourceTarget object from source to check
	 * @return true if object forms cycle in source object tree
	 */
	public boolean isCyclicDependency(final Persistable sourceTarget) {
		return dependencyContainer.containsKey(sourceTarget);
	}

	/**
	 * Gets a reference to a target object corresponding to the given cyclic object.
	 * 
	 * @param sourceTarget object repeated in the source
	 * @return corresponding object from the target
	 */
	public Persistable getTargetReference(final Persistable sourceTarget) {
		return dependencyContainer.get(sourceTarget); 
	}
}
