/**
 * Copyright (c) Elastic Path Software Inc., 2012.
 */
package com.elasticpath.domain.catalog;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Extensible Enum that indicates availability status.
 * 
 * @since 6.5.0
 */
public class Availability extends AbstractExtensibleEnum<Availability> {

	private static final long serialVersionUID = 650L;

	/** Ordinal value for the constant AVAILABLE. */
	public static final int AVAILABLE_ORDINAL = 0;

	/** The Constant AVAILABLE. */
	public static final Availability AVAILABLE = new Availability(AVAILABLE_ORDINAL, "AVAILABLE");

	/** Ordinal value for the constant AVAILABLE_FOR_PRE_ORDER. */
	public static final int AVAILABLE_FOR_PRE_ORDER_ORDINAL = 1;

	/** The Constant AVAILABLE_FOR_PRE_ORDER. */
	public static final Availability AVAILABLE_FOR_PRE_ORDER = new Availability(AVAILABLE_FOR_PRE_ORDER_ORDINAL, "AVAILABLE_FOR_PRE_ORDER");

	/** Ordinal value for the constant AVAILABLE_FOR_BACK_ORDER. */
	public static final int AVAILABLE_FOR_BACK_ORDER_ORDINAL = 2;

	/** The Constant AVAILABLE_FOR_BACK_ORDER. */
	public static final Availability AVAILABLE_FOR_BACK_ORDER = new Availability(AVAILABLE_FOR_BACK_ORDER_ORDINAL, "AVAILABLE_FOR_BACK_ORDER");

	/** Ordinal value for the constant NOT_AVAILABLE. */
	public static final int NOT_AVAILABLE_ORDINAL = 3;

	/** The Constant NOT_AVAILABLE. */
	public static final Availability NOT_AVAILABLE = new Availability(NOT_AVAILABLE_ORDINAL, "NOT_AVAILABLE");

	/**
	 * Instantiates a new availability.
	 *
	 * @param ordinal the ordinal
	 * @param name the name
	 */
	public Availability(final int ordinal, final String name) {
		super(ordinal, name, Availability.class);
	}

	@Override
	protected Class<Availability> getEnumType() {
		return Availability.class;
	}

}
