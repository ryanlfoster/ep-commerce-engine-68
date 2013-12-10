package com.elasticpath.domain.rules.impl;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * Coupon Code implementation to represents the coupon codes that can be used with
 * a coupon rule element.
 */
@Entity
@Table(name = CouponImpl.TABLE_NAME)
@DataCache(enabled = false)
public class CouponImpl extends AbstractEntityImpl implements Coupon, Cloneable {
	private static final long serialVersionUID = -5310755752099810444L;

	private long uidPk;
	private String couponCode;
	private CouponConfig couponConfig;
	private boolean suspended;

	/** Database Table. */
	public static final String TABLE_NAME = "TCOUPON";
	
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	@Basic
	@Column(name = "COUPONCODE")
	public String getCouponCode() {		
		return couponCode;
	}

	@Override
	public void setCouponCode(final String couponCode) {
		this.couponCode = couponCode;
	}
	
	@Override
	@Transient
	public String getGuid() {
		return getCouponCode();
	}

	@Override
	public void setGuid(final String guid) {
		this.setCouponCode(guid);
	}
	
	@Override
	@ManyToOne(targetEntity = CouponConfigImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinColumn(name = "COUPON_CONFIG_UID", nullable = false)
	@ForeignKey
	public CouponConfig getCouponConfig() {
		return couponConfig;
	}

	@Override
	public void setCouponConfig(final CouponConfig couponConfig) {
		this.couponConfig = couponConfig;
	}

	@Override
	@Basic
	@Column(name = "SUSPENDED")	
	public boolean isSuspended() {
		return suspended;
	}

	@Override
	public void setSuspended(final boolean suspended) {
		this.suspended = suspended;
	}
	
	@Override
	public void initialize() {
		// don't call super or else setGuid will be called
		//super.initialize();
		setSuspended(false);
	}
	
	/**
	 * Generate the hash code.
	 *
	 * @return the hash code.
	 */
	@Override
	public int hashCode() {
		final int prime = 37;
		return prime + ObjectUtils.hashCode(couponCode); 
	}
	

	/**
	 * Determines whether the given object is equal to this Coupon.
	 * Two CouponCodes are considered equal if their hashcodes and rulecodes are equal.
	 * @param obj the object to which this one should be compared for equality
	 * @return true if the given object is equal to this one
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CouponImpl)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
				
		return ObjectUtils.equals(this.couponCode, ((CouponImpl) obj).couponCode);
	}
}
