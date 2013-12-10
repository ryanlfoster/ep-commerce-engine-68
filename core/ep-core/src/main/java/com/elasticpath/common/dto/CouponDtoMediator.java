/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.common.dto;

import java.util.Collection;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Update and add coupon collections by mediating between the DTO and the service layer.
 */
public interface CouponDtoMediator {

	/**
	 * Update coupons given a collection of DTOs.
	 * The Coupons must exist or an EpServiceException is thrown.
	 *
	 * @param couponUpdates the Collection of CouponModelDto s.
	 * @throws EpServiceException Exception thrown if update fails.
	 */
	void update(final Collection<CouponModelDto> couponUpdates) throws EpServiceException;
	
	/**
	 * Add coupons given a collection of DTOs.
	 * The Coupons must not exist or an EpServiceException is thrown.
	 *
	 * @param addedCoupons the Collection of CouponModelDto s.
	 * @param ruleCode the rule code for the associated config.
	 * @throws EpServiceException Exception thrown if update fails.
	 */
	void add(final Collection<CouponModelDto> addedCoupons, final String ruleCode) throws EpServiceException;

	/**
	 * Validate model items for duplication.
	 *
	 * @param addedCouponItems coupons to add
	 * @param ruleCode rule code
	 */
	void validate(Collection<CouponModelDto> addedCouponItems, String ruleCode);

}
