/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.util;

import java.util.Comparator;

import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnType;

/**
 * The comparator factory which uses to create some comparators for returns and exchanges.
 */
public final class ReturnsAndExchangesComparatorFactory {

	private ReturnsAndExchangesComparatorFactory() {

	}

	/**
	 * Creates a returns and exchanges comparator.
	 *
	 * @return a returns and exchanges comparator instance
	 */
	public static Comparator<OrderReturn> getReturnsAndExchangesCompatator() {
		return new Comparator<OrderReturn>() {
			/**
			 * Compare by created date in descending order.
			 */
			public int compare(final OrderReturn arg0, final OrderReturn arg1) {
				String firstKey = arg0.getReturnType().getPropertyKey();
				String secondKey = arg1.getReturnType().getPropertyKey();
				if (!firstKey.equals(secondKey)) {
					if (firstKey.equals(OrderReturnType.RETURN.getPropertyKey())) {
						return -1;
					}
					return 1;
				}
				return arg0.getRmaCode().compareTo(arg1.getRmaCode());
			}
		};
	}

}
