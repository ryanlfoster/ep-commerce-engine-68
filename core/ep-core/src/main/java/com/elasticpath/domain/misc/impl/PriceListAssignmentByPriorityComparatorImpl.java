package com.elasticpath.domain.misc.impl;

import java.io.Serializable;
import java.util.Comparator;

import com.elasticpath.domain.pricing.PriceListAssignment;

/**
 * Compares its two price list assignment for order. Comparison based on priority and uidPk.
 */
public class PriceListAssignmentByPriorityComparatorImpl implements Comparator<PriceListAssignment>, Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20090810L;

	/**
	 * Compares its two price list assignment depending on the priority of the assignment in the ascending
	 * order. If priorities are the same then a second comparison is done on uidPkbased on
	 * priority and uidPk.
	 *
	 * @param priceListAssignment1 the first price list assignment to be compared.
	 * @param priceListAssignment2 the second price list assignment to be compared.
	 * @return a negative integer, zero, or a positive integer if the first argument is less than, equal to, or greater than the second.
	 * @throws ClassCastException if the arguments' types prevent them from being compared by this Comparator.
	 */
	public int compare(final PriceListAssignment priceListAssignment1, final PriceListAssignment priceListAssignment2) {
		final int preCompareResult = Integer.valueOf(priceListAssignment1.getPriority()).compareTo(
				priceListAssignment2.getPriority());

		if (preCompareResult == 0) {
			return Long.valueOf(priceListAssignment1.getUidPk()).compareTo(
					priceListAssignment2.getUidPk());
		}
		return preCompareResult;

	}

}
