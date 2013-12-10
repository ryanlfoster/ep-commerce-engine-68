/*
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.domain.shipping;

import java.util.Collection;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Define types of shipments.
 */
public class ShipmentType extends AbstractExtensibleEnum<ShipmentType> {

	private static final long serialVersionUID = 1L;

	/** Used for discriminator, which requires a static final string, so it's all defined in one place. */
	public static final String ELECTRONIC_STRING = "ELECTRONIC";

	/** Used for discriminator, which requires a static final string, so it's all defined in one place. */
	public static final String PHYSICAL_STRING = "PHYSICAL";

	/** Used for discriminator, which requires a static final string, so it's all defined in one place. */
	public static final String SERVICE_STRING = "SERVICE";

	/** Physical shipment shipment type ordinal. */
	public static final int PHYSICAL_ORDINAL = 0;

	/** Physical shipment shipment type. */
	public static final ShipmentType PHYSICAL = new ShipmentType(PHYSICAL_ORDINAL, "PHYSICAL");

	/** Electronic shipment shipment type ordinal. */
	public static final int ELECTRONIC_ORDINAL = 1;

	/** Electronic shipment shipment type. */
	public static final ShipmentType ELECTRONIC = new ShipmentType(ELECTRONIC_ORDINAL, "ELECTRONIC");

	/** Service shipment shipment type ordinal. */
	public static final int SERVICE_ORDINAL = 2;

	/** Service shipment type. */
	public static final ShipmentType SERVICE = new ShipmentType(SERVICE_ORDINAL, "SERVICE");

	/**
	 * Instantiates a new discount type.
	 *
	 * @param ordinal the ordinal
	 * @param name the name
	 */
	public ShipmentType(final int ordinal, final String name) {
		super(ordinal, name, ShipmentType.class);
	}

	@Override
	protected Class<ShipmentType> getEnumType() {
		return ShipmentType.class;
	}

	/**
	 * Find all enum values.
	 *
	 * @return the enum values
	 */
	public static Collection<ShipmentType> values() {
		return values(ShipmentType.class);
	}
}
