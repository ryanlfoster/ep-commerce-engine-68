package com.elasticpath.service.search.query;

/**
 * Represents the order of the sorting.
 */
public enum SortOrder {
	/** Sorts in ascending order (lower to higher). */
	ASCENDING("asc"),

	/** Sorts in descending order (higher to lower). */
	DESCENDING("desc");
	
	private String sortString;
	
	private SortOrder(final String sortString) {
		this.sortString = sortString;
	}
	
	/**
	 * Returns the string representation of this sort order.
	 *
	 * @return the string representation of this sort order
	 */
	public String getSortString() {
		return sortString;
	}
	
	/**
	 * Returns the string representation of this sort order.
	 *
	 * @return the string representation of this sort order
	 */
	@Override
	public String toString() {
		return getSortString();
	}

	/**
	 * Returns the reverse sort order.
	 * 
	 * @return the reverse sort order
	 */
	public SortOrder reverse() {
		if (this == ASCENDING) {
			return DESCENDING;
		}
		return ASCENDING;
	}
	
	/**
	 * Attempts to parse a sorting string into a {@link SortOrder}. Returns <code>null</code>
	 * if there are no sort order matches.
	 * 
	 * @param sortString the sort string
	 * @return a {@link SortOrder} representation of the string or <code>null</code> if there is none
	 */
	public static SortOrder parseSortString(final String sortString) {
		for (SortOrder sortOrder : SortOrder.values()) {
			if (sortOrder.sortString.equals(sortString)) {
				return sortOrder;
			}
		}
		return null;
	}
}