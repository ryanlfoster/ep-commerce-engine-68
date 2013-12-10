/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.common.dto;

import java.util.Collection;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Update and add coupon usage collections by mediating between the DTO and the service layer.
 */
public interface CouponUsageDtoMediator {

	/**
	 * Add couponUsages given a collection of DTOs.
	 * The CouponUsages must not exist or an EpServiceException is thrown.
	 *
	 * @param addedCouponUsages the Collection of CouponUsageModelDto s.
	 * @param ruleCode the rule code
	 * @throws EpServiceException Exception thrown if add fails.
	 */
	void add(final Collection<CouponUsageModelDto> addedCouponUsages, final String ruleCode) throws EpServiceException;

	/**
	 * Update a couponUsages for a collection of CouponUsageModelDto s.
	 * @param couponUsageUpdates the dto s.
	 * @throws EpServiceException Exception thrown if update fails.
	 */
	void update(final Collection<CouponUsageModelDto> couponUsageUpdates) throws EpServiceException;

	/**
	 * Validation before add.
	 * @param addCouponUsages coupon usages to add
	 * @param ruleCode rule code
	 */
	void validate(final Collection<CouponUsageModelDto> addCouponUsages, final String ruleCode);
}
