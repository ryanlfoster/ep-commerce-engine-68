/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.util;

import java.util.Comparator;

import com.elasticpath.domain.order.OrderSku;

/**
 * The comparator factory which uses to create order sku comparators.
 */
public final class OrderSkuComparatorFactory {
	
	private OrderSkuComparatorFactory() {
	}

	/**
	 * Compare by order sku code in ascending order.
	 * 
	 * @return comparator
	 */
	public static Comparator<OrderSku> getOrderSkuCodeComparator() {
		return new Comparator<OrderSku>() {
			/**
			 * Compare by sku code in ascending order.
			 */
			public int compare(final OrderSku arg0, final OrderSku arg1) {
				return arg0.getSkuCode().compareTo(arg1.getSkuCode());
			}
		};
	}
}