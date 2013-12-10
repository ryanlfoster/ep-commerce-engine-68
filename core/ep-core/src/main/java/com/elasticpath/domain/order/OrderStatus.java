/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order;

import java.util.Collection;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Represents the order status.
 */
public class OrderStatus extends AbstractExtensibleEnum<OrderStatus> {

	private static final long serialVersionUID = 1L;

	/**
	 * The <code>OrderStatus</code> ordinal for order in progress.
	 */
	public static final int IN_PROGRESS_ORDINAL = 1;
	/**
	 * The <code>OrderStatus</code> instance for order in progress.
	 */
	public static final OrderStatus IN_PROGRESS = new OrderStatus(IN_PROGRESS_ORDINAL, "IN_PROGRESS", OrderStatus.class, "orderStatus.inProgress");

	/**
	 * The <code>OrderStatus</code> ordinal for orders partially shipped.
	 */
	public static final int PARTIALLY_SHIPPED_ORDINAL = 2;
	/**
	 * The <code>OrderStatus</code> instance for orders partially shipped.
	 */
	public static final OrderStatus PARTIALLY_SHIPPED = new
	OrderStatus(PARTIALLY_SHIPPED_ORDINAL, "PARTIALLY_SHIPPED", OrderStatus.class, "orderStatus.partialShip");

	/**
	 * The <code>OrderStatus</code> instance for order completed.
	 */
	public static final int COMPLETED_ORDINAL = 3;
	/**
	 * The <code>OrderStatus</code> instance for order completed.
	 */
	public static final OrderStatus COMPLETED = new OrderStatus(COMPLETED_ORDINAL, "COMPLETED", OrderStatus.class, "orderStatus.completed");

	/**
	 * The <code>OrderStatus</code> ordinal for order on hold.
	 */
	public static final int ONHOLD_ORDINAL = 4;
	/**
	 * The <code>OrderStatus</code> instance for order on hold.
	 */
	public static final OrderStatus ONHOLD = new OrderStatus(ONHOLD_ORDINAL, "ONHOLD", OrderStatus.class, "orderStatus.onHold");

	/**
	 * The <code>OrderStatus</code> ordinal for order cancelled.
	 */
	public static final int CANCELLED_ORDINAL = 5;
	/**
	 * The <code>OrderStatus</code> instance for order cancelled.
	 */
	public static final OrderStatus CANCELLED = new OrderStatus(CANCELLED_ORDINAL, "CANCELLED", OrderStatus.class, "orderStatus.cancelled");

	/**
	 * The <code>OrderStatus</code> ordinal for orders awaiting exchange.
	 */
	public static final int AWAITING_EXCHANGE_ORDINAL = 6;

	/**
	 * The <code>OrderStatus</code> instance for orders awaiting exchange.
	 */
	public static final OrderStatus AWAITING_EXCHANGE = new
	OrderStatus(AWAITING_EXCHANGE_ORDINAL, "AWAITING_EXCHANGE", OrderStatus.class, "orderStatus.awaitingExchange");

	/**
	 * The <code>OrderStatus</code> ordinal for orders which have failed.
	 */
	public static final int FAILED_ORDINAL = 7;

	/**
	 * The <code>OrderStatus</code> instance for orders which have failed.
	 */
	public static final OrderStatus FAILED = new OrderStatus(FAILED_ORDINAL, "FAILED", OrderStatus.class, "orderStatus.failed");

	private final String propertyKey;

	/**
	 * Constructor for this extensible enum.
	 * @param ordinal the ordinal value
	 * @param name the named value
	 * @param klass the Class Type
	 * @param propertyKey the property key for the enumarated value
	 * */
	public OrderStatus(final int ordinal, final String name, final Class<OrderStatus> klass, final String propertyKey) {
		super(ordinal, name, klass);
		this.propertyKey = propertyKey;
	}

	/**
	 * Find the enum value with the specified ordinal value.
	 * @param ordinal the ordinal value
	 * @return the enum value
	 */
	public static OrderStatus valueOf(final int ordinal) {
		return valueOf(ordinal, OrderStatus.class);
	}

	/**
	 * Find the enum value with the specified name.
	 * @param name the name
	 * @return the enum value
	 */
	public static OrderStatus valueOf(final String name) {
		return valueOf(name, OrderStatus.class);
	}

	/**
	 * Find all enum values for a particular enum type.
	 * @return the enum values
	 */
	public static Collection<OrderStatus> values() {
		return values(OrderStatus.class);
	}

	@Override
	protected Class<OrderStatus> getEnumType() {
		return OrderStatus.class;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * The property key for this enumarated type.
	 * @return the property key
	 * */
	public String getPropertyKey() {
		return propertyKey;
	}


}
