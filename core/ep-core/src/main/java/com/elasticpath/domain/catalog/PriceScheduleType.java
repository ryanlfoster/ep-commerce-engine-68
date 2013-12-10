/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.catalog;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Represents a type of price schedule.
 */
public class PriceScheduleType extends AbstractExtensibleEnum<PriceScheduleType> implements Comparable<PriceScheduleType> {

	private static final long serialVersionUID = 1L;

	/** ordinal static for purchase time type. */
	public static final int PURCHASE_TIME_ORDINAL = 0;

	/** ordinal static for recurring type. */
	public static final int RECURRING_ORDINAL = 1;

	/** Price is relevant one time at purchase. */
	public static final PriceScheduleType PURCHASE_TIME = new PriceScheduleType(PURCHASE_TIME_ORDINAL, "PURCHASE_TIME", false);

	/** Price is on a recurring basis. */
	public static final PriceScheduleType RECURRING = new PriceScheduleType(RECURRING_ORDINAL, "RECURRING", true);

	private final boolean paymentSchedule;

	/**
	 * Create a new enum value.
	 * 
	 * @param ordinal the unique ordinal value
	 * @param name the named value for this extensible enum
	 * @param hasPaymentSchedule if this type has a payment schedule
	 */
	protected PriceScheduleType(final int ordinal, final String name, final boolean hasPaymentSchedule) {
		super(ordinal, name, PriceScheduleType.class);
		paymentSchedule = hasPaymentSchedule;
	}

	@Override
	protected Class<PriceScheduleType> getEnumType() {
		return PriceScheduleType.class;
	}

	/**
	 * Indicate whether this schedule type has a payment schedule.
	 * 
	 * @return the paymentSchedule
	 */
	public boolean hasPaymentSchedule() {
		return paymentSchedule;
	}

	@Override
	public int compareTo(final PriceScheduleType other) {
		final int ordinal1 = getOrdinal();
		final int ordinal2 = other.getOrdinal();
		if (ordinal1 < ordinal2) {
			return -1;
		}
		if (ordinal1 > ordinal2) {
			return 1;
		}
		return 0;
	}

}