package com.elasticpath.common.dto.category;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.common.dto.ChangeSetObjects;

/**
 * Category type change set object. It holds category type objects to be added to the change set.
 * @param <T> the parameterized type
 */
public class ChangeSetObjectsImpl<T> implements ChangeSetObjects <T> {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private final List<T> additionList;
	private final List<T> updateList;
	private final List<T> removalList;

	/**
	 * Constructs the change set objects with empty change lists.
	 */
	public ChangeSetObjectsImpl() {
		this.additionList = new ArrayList<T>();
		this.updateList = new ArrayList<T>();
		this.removalList = new ArrayList<T>();
	}

	/**
	 * Gets the addition list.
	 *
	 * @return list of <code>T</code>
	 */
	public List<T> getAdditionList() {
		return this.additionList;
	}

	/**
	 * Adds new <code>T</code> to the addition list.
	 *
	 * @param object T to be added to the addition list
	 */
	public void addToAdditionList(final T object) {
		this.additionList.add(object);
	}

	/**
	 * Gets the update list.
	 *
	 * @return list of <code>T</code>
	 */
	public List<T> getUpdateList() {
		return this.updateList;
	}

	/**
	 * Adds new <code>T</code> to the update list.
	 *
	 * @param object T to be added to the update list
	 */
	public void addToUpdateList(final T object) {
		this.updateList.add(object);
	}


	/**
	 * Gets the removal list.
	 *
	 * @return list of <code>T</code>
	 */
	public List<T> getRemovalList() {
		return removalList;
	}

	/**
	 * Adds new <code>T</code> to the removal list.
	 *
	 * @param object T to be added to the removal list
	 */
	public void addToRemovalList(final T object) {
		this.removalList.add(object);
	}
	
	/**
	 * Checks if the provided <code>T</code> is in one of the change set lists.
	 *
	 * @param object the T
	 * @return true if the object is in a change set 
	 */
	public boolean isInChangeSet(final T object) {
		return additionList.contains(object) || updateList.contains(object)  || removalList.contains(object);
	}

	@Override
	public void clear() {
		additionList.clear();
		updateList.clear();
		removalList.clear();
	}

	
	
}
