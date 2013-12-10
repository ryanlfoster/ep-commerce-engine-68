package com.elasticpath.domain.catalogview;

import com.elasticpath.base.exception.EpSystemException;

/**
 * Represents the type of range filter. Gives information on whether the upper or lower bound is
 * set.
 */
@SuppressWarnings({"PMD.MissingBreakInSwitch", "fallthrough" })
public enum RangeFilterType {
	/** The range filter has neither the lower nor the upper bound set. */
	ALL,
	
	/** The range filter has both the lower and upper values set. */
	BETWEEN,
	
	/** The range filter has only the upper value set. */
	LESS_THAN,
	
	/** The range filter has only the lower value set. */
	MORE_THAN;
	
	private static final String NOT_IMPLEMENTED = "Enum type not implemented";
	
	/**
	 * Returns the new type of filter if the lower bound was removed.
	 *
	 * @return the new type of filter if the lower bound was removed
	 */
	public RangeFilterType removeLowerBound() {
		switch (this) {
		case BETWEEN:
		case LESS_THAN:
			return LESS_THAN;
		case ALL:
		case MORE_THAN:
			return ALL;
		default:
			// should never get here
			throw new EpSystemException(NOT_IMPLEMENTED);
		}
	}
	
	/**
	 * Returns the new type of filter if the upper bound was removed.
	 *
	 * @return the new type of filter if the upper bound was removed
	 */
	public RangeFilterType removeUpperBound() {
		switch (this) {
		case BETWEEN:
		case MORE_THAN:
			return MORE_THAN;
		case ALL:
		case LESS_THAN:
			return ALL;
		default:
			// should never get here
			throw new EpSystemException(NOT_IMPLEMENTED);
		}
	}
	
	/**
	 * Returns the new type of filter if the lower bound was added.
	 *
	 * @return the new type of filter if the lower bound was added
	 */
	public RangeFilterType addLowerBound() {
		switch (this) {
		case ALL:
		case MORE_THAN:
			return MORE_THAN;
		case BETWEEN:
		case LESS_THAN:
			return BETWEEN;
		default:
			// should never get here
			throw new EpSystemException(NOT_IMPLEMENTED);
		}
	}
	
	/**
	 * Returns the new type of filter if the upper bound was added.
	 *
	 * @return the new type of filter if the upper bound was added
	 */
	public RangeFilterType addUpperBound() {
		switch (this) {
		case ALL:
		case LESS_THAN:
			return LESS_THAN;
		case BETWEEN:
		case MORE_THAN:
			return BETWEEN;
		default:
			// should never get here
			throw new EpSystemException(NOT_IMPLEMENTED);
		}
	}
}
