package com.elasticpath.service.rules;

import com.elasticpath.commons.pagination.SortingField;
import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Fields for sorting CouponUsageModelDtos by.
 */
public class CouponUsageModelDtoSortingField extends AbstractExtensibleEnum<CouponUsageModelDtoSortingField> implements SortingField {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** Ordinal constant for COUPON_CODE. */
	public static final int COUPON_CODE_ORDINAL = 1;

	/**
	 * Coupon code sorting field.
	 */
	public static final SortingField COUPON_CODE = new CouponUsageModelDtoSortingField(COUPON_CODE_ORDINAL, "couponCode");

	/** Ordinal constant for EMAIL_ADDRESS. */
	public static final int EMAIL_ADDRESS_ORDINAL = 2;

	/**
	 * Status sorting field.
	 */
	public static final SortingField EMAIL_ADDRESS = new CouponUsageModelDtoSortingField(EMAIL_ADDRESS_ORDINAL, "emailAddress");

	/** Ordinal constant for STATUS. */
	public static final int STATUS_ORDINAL = 3;

	/**
	 * Status sorting field.
	 */
	public static final SortingField STATUS = new CouponUsageModelDtoSortingField(STATUS_ORDINAL, "status");

	private final String fieldName;
	
	/**
	 * Instantiates a new coupon usage model dto sorting field.
	 *
	 * @param ordinal the ordinal
	 * @param name the name
	 */
	protected CouponUsageModelDtoSortingField(final int ordinal, final String name) {
		super(ordinal, name, CouponUsageModelDtoSortingField.class);
		this.fieldName = name;
	}
	
	@Override
	public String getName() {
		return fieldName;
	}

	@Override
	protected Class<CouponUsageModelDtoSortingField> getEnumType() {
		return CouponUsageModelDtoSortingField.class;
	}

	/**
	 * Get the enum value corresponding with the given name.
	 *
	 * @param name the name
	 * @return the CouponUsageModelDtoSortingField
	 */
	public static CouponUsageModelDtoSortingField valueOf(final String name) {
		return valueOf(name, CouponUsageModelDtoSortingField.class);
	}
}
