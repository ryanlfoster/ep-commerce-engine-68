/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.util;

import java.util.Comparator;

import com.elasticpath.domain.order.OrderShipment;

/**
 * The comparator factory which uses to create order shipment comparators.
 */
public final class OrderShipmentComparatorFactory {
	
	private OrderShipmentComparatorFactory() {
	}

	/**
	 * Compare by shipment number in ascending order.
	 * 
	 * @return comparator
	 */
	public static Comparator<OrderShipment> getOrderShipmentNumberComparator() {
		return new Comparator<OrderShipment>() {
			/**
			 * Compare by shipment number in ascending order.
			 */
			public int compare(final OrderShipment arg0, final OrderShipment arg1) {
				return arg0.getShipmentNumber().compareTo(arg1.getShipmentNumber());
			}
		};
	}
}

