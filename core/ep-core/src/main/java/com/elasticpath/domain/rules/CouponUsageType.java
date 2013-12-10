/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.domain.rules;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Type safe extensible enumeration of coupon usage types.
 */
public class CouponUsageType extends AbstractExtensibleEnum<CouponUsageType> {

	/** 
	 * Serial version UID. 
	 */
	private static final long serialVersionUID = 5000000001L;
	
	/** Ordinal constant for LIMIT_PER_COUPON. */
	public static final int LIMIT_PER_COUPON_ORDINAL = 1;

	/** Coupon usage is limited to a specific number of uses per coupon. */ 
	public static final CouponUsageType LIMIT_PER_COUPON = new CouponUsageType(LIMIT_PER_COUPON_ORDINAL, "limitPerCoupon");
	
	/** Ordinal constant for LIMIT_PER_ANY_USER. */
	public static final int LIMIT_PER_ANY_USER_ORDINAL = 2;

	/** Coupon usage is limited to a specific number of uses per user, no restrictions on how many users. */
	public static final CouponUsageType LIMIT_PER_ANY_USER = new CouponUsageType(LIMIT_PER_ANY_USER_ORDINAL, "limitPerAnyUser");
	
	/** Ordinal constant for LIMIT_PER_SPECIFIED_USER. */
	public static final int LIMIT_PER_SPECIFIED_USER_ORDINAL = 3;

	/** Coupon usage is limited to a specific number of uses for a set of users associated to the coupon. */
	public static final CouponUsageType LIMIT_PER_SPECIFIED_USER = new CouponUsageType(LIMIT_PER_SPECIFIED_USER_ORDINAL, "limitPerSpecifiedUser");
	
	private final String usageName;
	
	/**
	 * Construct a new coupon usage type from the given name.
	 *
	 * @param ordinal the ordinal
	 * @param name the name of a coupon usage type.
	 */
	protected CouponUsageType(final int ordinal, final String name) {
		super(ordinal, name, CouponUsageType.class);
		usageName = name;
	}
	
	/**
	 * Get a coupon usage type by name.
	 * 
	 * @param name the name of the coupon usage type
	 * @return the {code CouponUsageType}
	 */
	public static CouponUsageType getEnum(final String name) {
		return valueOf(name, CouponUsageType.class);
	}

	/**
	 * Return the list of coupon usage types.
	 * 
	 * @return a list of {@code CouponUsageType}
	 */
	public static List<CouponUsageType> getEnumList() {
		return new ArrayList<CouponUsageType>(values(CouponUsageType.class));
	}

	@Override
	protected Class<CouponUsageType> getEnumType() {
		return CouponUsageType.class;
	}

	/**
	 * Get the enum value corresponding with the given name.
	 *
	 * @param name the name
	 * @return the CouponUsageType
	 */
	public static CouponUsageType valueOf(final String name) {
		return valueOf(name, CouponUsageType.class);
	}
	
	@Override
	public String getName() {
		return usageName;
	}
}
