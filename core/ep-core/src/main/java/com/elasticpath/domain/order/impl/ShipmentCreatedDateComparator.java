package com.elasticpath.domain.order.impl;

import java.util.Comparator;

import com.elasticpath.domain.order.OrderShipment;

/**
 * Compares two {@link com.elasticpath.domain.order.OrderShipment}s based on their created date.
 *
 * Null is considered less than non-null in the case of both the shipment itself as well as the created date field.
 */
public class ShipmentCreatedDateComparator implements Comparator<OrderShipment> {
	@Override
	@SuppressWarnings("PMD.CompareObjectsWithEquals")
	public int compare(final OrderShipment shipment1, final OrderShipment shipment2) {
		if (shipment1 == shipment2) {
			return 0;
		}

		if (shipment1 == null) {
			return -1;
		}

		if (shipment2 == null) {
			return 1;
		}

		if (shipment1.getCreatedDate() == shipment2.getCreatedDate()) {
			return 0;
		}

		if (shipment1.getCreatedDate() == null) {
			return -1;
		}

		if (shipment2.getCreatedDate() == null) {
			return 1;
		}

		return shipment1.getCreatedDate().compareTo(shipment2.getCreatedDate());
	}
}
