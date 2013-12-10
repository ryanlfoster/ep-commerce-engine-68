/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.commons.pagination;

import java.io.Serializable;

/**
 * Directed sorting field.
 */
public class DirectedSortingField implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 500000000001L;
	
	private final SortingField sortingField;
	private final SortingDirection sortingDirection;
	
	/**
	 *
	 * @param sortingField the sorting field
	 * @param sortingDirection the sorting direction
	 */
	public DirectedSortingField(final SortingField sortingField, final SortingDirection sortingDirection) {
		if (sortingField == null) {
			throw new IllegalArgumentException("Sorting field is required.");
		}
		if (sortingDirection == null) {
			throw new IllegalArgumentException("Sorting direction is required");
		}
		this.sortingField = sortingField;
		this.sortingDirection = sortingDirection;
	}
	
	/**
	 *
	 * @return the sortingField
	 */
	public SortingField getSortingField() {
		return sortingField;
	}
	/**
	 *
	 * @return the sortingDirection
	 */
	public SortingDirection getSortingDirection() {
		return sortingDirection;
	}
	
	
}
