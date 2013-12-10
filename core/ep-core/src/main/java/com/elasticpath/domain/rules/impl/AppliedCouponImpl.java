package com.elasticpath.domain.rules.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * 
 * Represents a Coupon that has been applied to an Order.  Generally managed as a collection
 * on an AppliedRule class. Semantics generally reflect the state of things when the order was made.
 *
 */
@Entity
@Table(name = AppliedCouponImpl.TABLE_NAME)
@DataCache(enabled = false)
public class AppliedCouponImpl extends AbstractPersistableImpl implements AppliedCoupon {
	private static final long serialVersionUID = 636963114773116632L;

	private long uidPk;
	private String couponCode;
	private int usageCount;

	/** Database Table. */
	public static final String TABLE_NAME = "TAPPLIEDRULECOUPONCODE";
	
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
	@Column(name = "COUPONCODE", nullable = false)
	public String getCouponCode() {
		return couponCode;
	}

	@Override
	@Basic
	@Column(name = "USECOUNT", nullable = false)
	public int getUsageCount() {
		return usageCount;
	}

	@Override
	public void setCouponCode(final String couponCode) {
		this.couponCode = couponCode;
	}

	@Override
	public void setUsageCount(final int usageCount) {
		this.usageCount = usageCount;
	}
}
