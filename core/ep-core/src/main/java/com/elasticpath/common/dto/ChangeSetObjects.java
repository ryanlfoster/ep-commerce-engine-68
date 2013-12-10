package com.elasticpath.common.dto;

import java.io.Serializable;
import java.util.List;

/**
 * A collection of changes changes coming from the client side containing objects to be updated/removed/added.
 * @param <T> instance of object
 */
public interface ChangeSetObjects <T> extends Serializable {

	/**
	 * Gets the addition list.
	 *
	 * @return list of <code>Object</code>
	 */
	List<T> getAdditionList();

	/**
	 * Adds new <code>Object</code> to the addition list.
	 *
	 * @param object to be added to the addition list
	 */
	void addToAdditionList(final T object);

	/**
	 * Gets the update list.
	 *
	 * @return list of <code>Object</code>
	 */
	List<T> getUpdateList();

	/**
	 * Adds new <code>Object</code> to the update list.
	 *
	 * @param object to be added to the update list
	 */
	void addToUpdateList(final T object);


	/**
	 * Gets the removal list.
	 *
	 * @return list of <code>Object</code>
	 */
	List<T> getRemovalList();

	/**
	 * Adds new <code>Object</code> to the removal list.
	 *
	 * @param object to be added to the removal list
	 */
	void addToRemovalList(final T object);

	/**
	 * Checks if the provided <code>CategoryType</code> is in one of the change set lists.
	 *
	 * @param object the object to be checked
	 * @return true if the object is in the change set
	 */
	boolean isInChangeSet(final T object);
	
	/**
	 * Clears all additions, updates, and deletions.
	 */
	void clear();
}
