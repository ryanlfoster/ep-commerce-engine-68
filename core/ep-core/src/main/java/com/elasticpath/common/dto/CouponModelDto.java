package com.elasticpath.common.dto;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Represents the pair of a coupon code and email address.
 */
public class CouponModelDto implements UniquelyIdentifiable, Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private String couponCode = ""; //$NON-NLS-1$
	private boolean suspended = false;
	private long uidPk;
	
	private static final int HASHCODE_MULTIPLY = 37;
	private static final int HASHCODE_START = 17;

	/**
	 * The default constructor.
	 */
	public CouponModelDto() {
		super();
	}
	
	/**
	 * Normal constructor.
	 * 
	 * @param uidPk The uid for the domain object that this DTO represents.
	 * @param couponCode The coupon code
	 * @param isSuspended True if the code is suspended
	 */
	public CouponModelDto(final long uidPk, final String couponCode, final boolean isSuspended) {
		this.couponCode = couponCode;
		this.suspended = isSuspended;
		this.uidPk = uidPk;
	}

	/**
	 * Setter for suspended.
	 * 
	 * @param suspended the suspended to set.
	 */
	public void setSuspended(final boolean suspended) {
		this.suspended = suspended;
	}

	/**
	 * 
	 * @return The coupon code
	 */
	public String getCouponCode() {
		return couponCode;
	}
	
	/**
	 * 
	 * @return The suspended flag.
	 */
	public boolean isSuspended() {
		return suspended;
	}
	
	/**
	 * 
	 * @param couponCode The coupon code to set.
	 */
	public void setCouponCode(final String couponCode) {
		this.couponCode = couponCode;
	}

	/**
	 * Generate the hash code.
	 *
	 * @return the hash code.
	 */
	@Override
	public int hashCode() {		
		int result = HASHCODE_START;
		return result * HASHCODE_MULTIPLY + ObjectUtils.hashCode(StringUtils.upperCase(couponCode)); 
	}

	/**
	 * Determines whether the given object is equal to this CouponUsage.
	 * Two CouponUsageModels are considered equal if their couponCodes and emailAddresses are equal.
	 * @param obj the object to which this one should be compared for equality
	 * @return true if the given object is equal to this one
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CouponModelDto)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
				
		return StringUtils.equalsIgnoreCase(this.couponCode,
				((CouponModelDto) obj).couponCode);
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Coupon Code:").append(getCouponCode());
		return stringBuilder.toString();
	}

	@Override
	public long getUidPk() {
		return uidPk;
	}	
}
