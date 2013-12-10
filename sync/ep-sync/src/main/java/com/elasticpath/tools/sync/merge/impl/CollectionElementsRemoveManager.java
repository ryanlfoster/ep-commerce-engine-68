package com.elasticpath.tools.sync.merge.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Manages process of collection merging.
 */
class CollectionElementsRemoveManager {

	private final List< ? super Object> elementsToRemove = new ArrayList<Object>();
		
	/**
	 * Adds the given collection to the list of elements for deletion.
	 * 
	 * @param collection initial objects to remove
	 */
	public CollectionElementsRemoveManager(final Collection<?> collection) {
		elementsToRemove.addAll(collection);
	}

	/**
	 * Removes identical object from the list it matches the given one.
	 * 
	 * @param object to remove
	 */
	public void removeIdenticalObject(final Object object) {
		attemptToRemoveIdenticalObject(elementsToRemove, object);
	}
	
	/**
	 * Removes equal object from the list it matches the given one.
	 * 
	 * @param object to remove
	 */
	public void removeEqualObject(final Object object) {
		elementsToRemove.remove(object);
	}

	/**
	 * Removes matching objects from collection.
	 * 
	 * @param collection to clean
	 */
	public void removeSurplusObjectsFromCollection(final Collection< ? > collection) {
		for (Object object : elementsToRemove) {
			attemptToRemoveIdenticalObject(collection, object);
		}
	}

	/**
	 * Removes matching objects from map.
	 * 
	 * @param map to clean
	 */
	public void removeSurplusObjectsFromMap(final Map<?, ?> map) {
		removeSurplusObjectsFromCollection(map.values());
	}
	
	private void attemptToRemoveIdenticalObject(final Collection< ? > collection, final Object object) {
		Iterator< ? > iterator = collection.iterator();
		while (iterator.hasNext()) {
			Object objectToQualify = iterator.next();

			if (objectToQualify == object) {	// NOPMD
				iterator.remove();
				return;
			}
		}
	}
}
