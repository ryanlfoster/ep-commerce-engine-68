package com.elasticpath.service.rules;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Represents a duplicate coupon code.
 */
public class DuplicateCouponException extends EpServiceException {
	private static final long serialVersionUID = 2629723297676011164L;

	private final String couponCode;
	
	/**
	 * Constructor.
	 * @param message the error message.
	 * @param couponCode the coupon code that would be a duplicate.
	 */
	public DuplicateCouponException(final String message, final String couponCode) {
		super(message);
		this.couponCode = couponCode;
	}
	
	/**
	 * @return The coupon code that prompted the exception.
	 */
	public String getCouponCode() {
		return this.couponCode;
	}
}
