/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.service.rules.impl;

import java.util.Random;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.service.rules.CouponCodeGenerator;
import com.elasticpath.service.rules.dao.CouponDao;

/**
 * Sequential generator for unique coupon codes given a prefix.
 *
 */
public class SequentialCouponCodeGeneratorImpl implements CouponCodeGenerator {
	
	private CouponDao couponDao;
	
	private static final int SUFFIX_DIGITS = 10;
	
	@Override
	public String generateCouponCode(final Coupon coupon, final String couponCodePrefix) {
		Coupon lastCoupon = couponDao.getLastestCoupon(couponCodePrefix);
		return couponCodePrefix + String.format("%0" + Integer.toString(SUFFIX_DIGITS) + "d", getCouponSequenceId(lastCoupon, couponCodePrefix) + 1);
	}

	private int getCouponSequenceId(final Coupon coupon, final String prefix) {
		int sequenceId = 0;
		if (coupon != null) {
			String suffix = coupon.getCouponCode().substring(prefix.length());
			try {
				if (suffix.matches("[0-9]{" + Integer.toString(SUFFIX_DIGITS) + ",}")) {  // at least n digits
					sequenceId = Integer.parseInt(suffix);
				}
			} catch (NumberFormatException x) {
				Random random = new Random();
				sequenceId = random.nextInt(Integer.MAX_VALUE);
			}
		}
		return sequenceId;
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
}
