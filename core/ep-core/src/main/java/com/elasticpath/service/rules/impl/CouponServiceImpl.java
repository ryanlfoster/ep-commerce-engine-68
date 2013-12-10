/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.service.rules.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.rules.CouponCodeGenerator;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.DuplicateCouponException;
import com.elasticpath.service.rules.dao.CouponDao;
import com.elasticpath.service.rules.dao.CouponUsageDao;
/**
 * Implementation of {@code CouponService}.
 */
public class CouponServiceImpl implements CouponService {
	
	private CouponDao couponDao;
	private CouponUsageDao couponUsageDao;
	private CouponCodeGenerator couponCodeGenerator;

	@Override
	public Coupon add(final Coupon coupon) throws EpServiceException {
		Coupon newCoupon = null;
		try {
			newCoupon = getCouponDao().add(coupon);
		} catch (Exception ex) {
			throw new EpServiceException("Failed to add " + coupon, ex);
		}
		return newCoupon;
	}

	@Override
	public Coupon addAndGenerateCode(final Coupon coupon, final String couponCodePrefix) {
		final int maxRetries = 5;
		int retry = 0;
		Coupon result = null;
		while (retry < maxRetries) {
			try {
				String couponCode = couponCodeGenerator.generateCouponCode(coupon, couponCodePrefix);
				coupon.setCouponCode(couponCode);
				result = add(coupon);
				break;
			} catch (Exception x) {
				retry++;
				result = null;
			}
		}
		return result;
	}

	/**
	 *
	 * @param couponCodeGenerator the code generator.
	 */
	public void setCouponCodeGenerator(final CouponCodeGenerator couponCodeGenerator) {
		this.couponCodeGenerator = couponCodeGenerator;
	}
	
	/**
	 *
	 * @return the CouponCodeGenerator
	 */
	public CouponCodeGenerator getCouponCodeGenerator() {
		return this.couponCodeGenerator;
	}
	
	@Override
	public void delete(final Coupon coupon) throws EpServiceException {
		try {
			getCouponDao().delete(coupon);
		} catch (Exception ex) {
			throw new EpServiceException("Failed to delete " + coupon, ex);
		}
	}

	@Override
	public Coupon update(final Coupon newCoupon)
			throws EpServiceException {
		Coupon updatedCoupon = null;
		try {
			updatedCoupon = getCouponDao().update(newCoupon);
		} catch (Exception ex) {
			throw new EpServiceException("Failed to update " + newCoupon.getCouponCode(), ex);
		}
		return updatedCoupon;
	}

	@Override
	public Coupon findByCouponCode(final String couponCode) {
		Collection<Coupon> coupons = getCouponDao().findByCouponCode(couponCode);
		if (coupons.isEmpty()) {
			return null;
		}
		if (coupons.size() > 1) {
			throw new DuplicateCouponException("Multiple coupons were found with the same coupon code : " + couponCode, null);
		}
		return coupons.iterator().next();
	}
	
	/**
	 * 
	 * @param couponDao The coupon dao.
	 */
	public void setCouponDao(final CouponDao couponDao) {
		this.couponDao = couponDao;
	}
	
	/**
	 * Get the coupon dao.
	 * 
	 * @return the couponDao
	 */
	protected CouponDao getCouponDao() {
		return couponDao;
	}

	@Override
	public Collection<Coupon> findCouponsForRuleCode(final String ruleCode) {
		return getCouponDao().findByRuleCode(ruleCode);
	}

	@Override
	public Collection<Coupon> findCouponsForCouponConfigId(final long configId, final SearchCriterion[] searchCriteria, final int startIndex,
			final int pageSize, final DirectedSortingField[] orderingFields) {
		return getCouponDao().findByCouponConfigId(configId, searchCriteria, startIndex, pageSize, orderingFields);
	}
	
	@Override
	public long getCountForSearchCriteria(final long couponConfigId, final SearchCriterion[] searchCriteria) {
		return getCouponDao().getCountForSearchCriteria(couponConfigId, searchCriteria);
	}
	
	@Override
	public Collection<Coupon> findCouponsForRuleCodeFromCouponCodes(final String ruleCode, final Set<String> couponCodes) {
		return getCouponDao().findCouponsForRuleCodeFromCouponCodes(ruleCode, couponCodes);
	}

	@Override
	public Collection<Coupon> findCouponsForRuleFromCouponCodes(final long ruleId, final Set<String> couponCodes) {
		return getCouponDao().findCouponsForRuleFromCouponCodes(ruleId, couponCodes);
	}

	@Override
	public void deleteCouponsByCouponConfigGuid(final String couponConfigGuid) {
		getCouponUsageDao().deleteAllUsagesByCouponConfigGuid(couponConfigGuid);
		getCouponDao().deleteCouponsByCouponConfigGuid(couponConfigGuid);
	}
	
	@Override
	public boolean doesCouponCodeEmailPairExistForThisRuleCode(final String couponCode, final String email, final String ruleCode) {
		return getCouponDao().doesCouponCodeEmailPairExistForThisRuleCode(couponCode, email, ruleCode);
	}

	@Override
	public boolean doesCouponCodeOnlyExistForThisRuleCode(final String couponCode, final String ruleCode) {
		return getCouponDao().doesCouponCodeOnlyExistForThisRuleCode(couponCode, ruleCode);
	}
	
	@Override
	public Collection<String> findExistingCouponCodes(final Collection<String> codes) {
		return getCouponDao().findExistingCouponCodes(codes);
	}
	
	@Override
	public Collection<String> findExistingCouponCodes(final Collection<String> codes, final String exceptionRuleCode) {
		return getCouponDao().findExistingCouponCodes(codes, exceptionRuleCode);
	}
	
	@Override
	public List<String> findCouponCodesByRuleCode(final String ruleCode) {
		return getCouponDao().findCouponCodesByRuleCode(ruleCode);
	}

	/**
	 * @param couponUsageDao the couponUsageDao to set
	 */
	public void setCouponUsageDao(final CouponUsageDao couponUsageDao) {
		this.couponUsageDao = couponUsageDao;
	}

	/**
	 * Get the coupon usage dao.
	 * 
	 * @return the couponUsageDao.
	 */
	protected CouponUsageDao getCouponUsageDao() {
		return couponUsageDao;
	}

	@Override
	public List<Coupon> findByUids(final Collection<Long> uids) {
		return getCouponDao().findByUids(uids);
	}

	@Override
	public Map<String, Coupon> findCouponsForCodes(final Collection<String> codes) {
		return getCouponDao().findCouponsForCodes(codes);
	}



}
