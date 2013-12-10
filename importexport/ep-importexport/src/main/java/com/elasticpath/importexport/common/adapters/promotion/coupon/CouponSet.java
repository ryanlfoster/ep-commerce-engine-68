package com.elasticpath.importexport.common.adapters.promotion.coupon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * A domain object to express a set of coupon for import/export. A coupon set contains one {@link CouponConfig} and a set of {@link Coupon}s.
 */
public class CouponSet extends AbstractEntityImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CouponConfig couponConfig;

	private Collection<Coupon> coupons = new ArrayList<Coupon>();

	private  Map<String, Collection<CouponUsage>> usagesMap = new HashMap<String, Collection<CouponUsage>>();
	
	private String guid;

	/**
	 * @param usagesMap the usagesMap to set
	 */
	public void setUsagesMap(final Map<String, Collection<CouponUsage>> usagesMap) {
		this.usagesMap = usagesMap;
	}

	private long uidpk;

	/**
	 * @return the usagesMap
	 */
	public Map<String, Collection<CouponUsage>> getUsagesMap() {
		return usagesMap;
	}

	/**
	 * Adds coupon usage.
	 * 
	 * @param couponCode coupon code.
	 * @param couponUsage coupon usage.
	 */
	public void addUsage(final String couponCode, final CouponUsage couponUsage) {
		Collection<CouponUsage> usage = usagesMap.get(couponCode);
		if (usage == null) {
			usage = new ArrayList<CouponUsage>();
			usagesMap.put(couponCode, usage);
		}

		usage.add(couponUsage);
	}

	/**
	 * @return {@link CouponConfig}.
	 */
	public CouponConfig getCouponConfig() {
		return couponConfig;
	}

	/**
	 * Setter for {@link CouponConfig}.
	 * 
	 * @param couponConfig {@link CouponConfig}.
	 */
	public void setCouponConfig(final CouponConfig couponConfig) {
		this.couponConfig = couponConfig;
	}

	/**
	 * @return A set of {@link Coupon}.
	 */
	public Collection<Coupon> getCoupons() {
		return coupons;
	}

	/**
	 * Setter for a set of {@link Coupon}.
	 * 
	 * @param coupons a set of {@link Coupon}.
	 */
	public void setCoupons(final Collection<Coupon> coupons) {
		this.coupons = coupons;
	}

	@Override
	public long getUidPk() {
		return uidpk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidpk = uidPk;
	}

	@Override
	public String getGuid() {
		return guid;
	}

	@Override
	public void setGuid(final String guid) {
		this.guid = guid;		
	}
}
