/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order;

import java.util.Collection;
import java.util.Iterator;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Represents the customer orderShipment status.
 *
 */
public class OrderShipmentStatus extends AbstractExtensibleEnum<OrderShipmentStatus> {

	private static final long serialVersionUID = 1L;

	/**
	 * The <code>OrderShipmentStatus</code> ordinal for awaiting inventory.
	 */
	public static final int AWAITING_INVENTORY_ORDINAL = 1;
	/**
	 * The <code>OrderShipmentStatus</code> instance for awaiting inventory.
	 */
	public static final OrderShipmentStatus AWAITING_INVENTORY = new OrderShipmentStatus(AWAITING_INVENTORY_ORDINAL, "AWAITING_INVENTORY",
			OrderShipmentStatus.class, "orderShipmentStatus.awaitingInventory");

	/**
	 * The <code>OrderShipmentStatus</code> ordinal for inventory assigned.
	 */
	public static final int INVENTORY_ASSIGNED_ORDINAL = 2;
	/**
	 * The <code>OrderShipmentStatus</code> instance for inventory assigned.
	 */
	public static final OrderShipmentStatus INVENTORY_ASSIGNED = new OrderShipmentStatus(INVENTORY_ASSIGNED_ORDINAL, "INVENTORY_ASSIGNED",
			OrderShipmentStatus.class, "orderShipmentStatus.inventoryAssigned");

	/**
	 * The <code>OrderShipmentStatus</code> ordinal for released.
	 */
	public static final int RELEASED_ORDINAL = 3;
	/**
	 * The <code>OrderShipmentStatus</code> instance for released.
	 */
	public static final OrderShipmentStatus RELEASED = new OrderShipmentStatus(RELEASED_ORDINAL, "RELEASED",
			OrderShipmentStatus.class, "orderShipmentStatus.releasedForPacking");

	/**
	 * The <code>OrderShipmentStatus</code> ordinal for shipped.
	 */
	public static final int SHIPPED_ORDINAL = 5;
	/**
	 * The <code>OrderShipmentStatus</code> instance for shipped.
	 */
	public static final OrderShipmentStatus SHIPPED = new OrderShipmentStatus(SHIPPED_ORDINAL, "SHIPPED",
			OrderShipmentStatus.class, "orderShipmentStatus.shipped");

	/**
	 * The <code>OrderShipmentStatus</code> ordinal for on hold.
	 */
	public static final int ONHOLD_ORDINAL = 6;
	/**
	 * The <code>OrderShipmentStatus</code> instance for on hold.
	 */
	public static final OrderShipmentStatus ONHOLD = new OrderShipmentStatus(ONHOLD_ORDINAL, "ONHOLD",
			OrderShipmentStatus.class, "orderShipmentStatus.onHold");

	/**
	 * The <code>OrderShipmentStatus</code> ordinal for canceled.
	 */
	public static final int CANCELLED_ORDINAL = 7;
	/**
	 * The <code>OrderShipmentStatus</code> instance for canceled.
	 */
	public static final OrderShipmentStatus CANCELLED = new OrderShipmentStatus(CANCELLED_ORDINAL, "CANCELLED",
			OrderShipmentStatus.class, "orderShipmentStatus.cancelled");

	/**
	 * The <code>OrderShipmentStatus</code> ordinal for failed order.
	 */
	public static final int FAILED_ORDER_ORDINAL = 8;
	/**
	 * The <code>OrderShipmentStatus</code> instance for failed order.
	 */
	public static final OrderShipmentStatus FAILED_ORDER = new OrderShipmentStatus(FAILED_ORDER_ORDINAL, "FAILED_ORDER",
			OrderShipmentStatus.class, "orderShipmentStatus.failedOrder");

	private final String propertyKey;

	/**
	 * Constructor for this extensible enum.
	 * @param ordinal the ordinal value
	 * @param name the named value
	 * @param klass the Class Type
	 * @param propertyKey the property key for the enumarated value
	 * */
	public OrderShipmentStatus(final int ordinal, final String name, final Class<OrderShipmentStatus> klass, final String propertyKey) {
		super(ordinal, name, klass);
		this.propertyKey = propertyKey;
	}

	/**
	 * Find the enum value with the specified ordinal value.
	 * @param ordinal the ordinal value
	 * @return the enum value
	 */
	public static OrderShipmentStatus valueOf(final int ordinal) {
		return valueOf(ordinal, OrderShipmentStatus.class);
	}

	/**
	 * Find the enum value with the specified name.
	 * @param name the name
	 * @return the enum value
	 */
	public static OrderShipmentStatus valueOf(final String name) {
		return valueOf(name, OrderShipmentStatus.class);
	}

	/**
	 * Find all enum values for a particular enum type.
	 * @return the enum values
	 */
	public static Collection<OrderShipmentStatus> values() {
		return values(OrderShipmentStatus.class);
	}

	@Override
	protected Class<OrderShipmentStatus> getEnumType() {
		return OrderShipmentStatus.class;
	}

	/**
	 * Convenient method to get an array of order shipment status.
	 * @return an array with all order shipment status enums
	 * */
	public static OrderShipmentStatus[] getOrderShipmentStatusArray() {
		final OrderShipmentStatus[] ossArray = new OrderShipmentStatus[OrderShipmentStatus.values().size()];
		final Iterator<OrderShipmentStatus> iterator = OrderShipmentStatus.values().iterator();
		int index = 0;
		while (iterator.hasNext()) {
			ossArray[index] = iterator.next();
			index++;
		}
		return ossArray;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Get the localization property key.
	 * 
	 * @return the localized property key
	 */
	public String getPropertyKey() {
		return propertyKey;
	}
}
