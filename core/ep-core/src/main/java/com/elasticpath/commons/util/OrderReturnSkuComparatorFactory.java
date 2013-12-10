/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.util;

import java.util.Comparator;

import com.elasticpath.domain.order.OrderReturnSku;

/**
 * The comparator factory which uses to create order return sku comparators.
 */
public final class OrderReturnSkuComparatorFactory {
	
	private OrderReturnSkuComparatorFactory() {
	}

	/**
	 * Compare by return sku code in ascending order.
	 * 
	 * @return comparator
	 */
	public static Comparator<OrderReturnSku> getOrderReturnSkuComparator() {
		return new Comparator<OrderReturnSku>() {
			/**
			 * Compare by return sku code in ascending order.
			 */
			public int compare(final OrderReturnSku arg0, final OrderReturnSku arg1) {
				return arg0.getOrderSku().getSkuCode().compareTo(arg1.getOrderSku().getSkuCode());
			}
		};
	}
}
