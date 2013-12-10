/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.commons.pagination;

/**
 * Sorting direction enumeration.
 */
public enum SortingDirection {

	/**
	 * Ascending.
	 */
	ASCENDING("ASC"),
	
	/**
	 * Descending.
	 */
	DESCENDING("DESC");

	private final String name;
	
	/**
	 * Constructor.
	 * 
	 * @param name the String representation of the enumeration element
	 */
	SortingDirection(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	
}
