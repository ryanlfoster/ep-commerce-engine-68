package com.elasticpath.common.dto;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Represents the pair of a coupon code and email address.
 */
public class CouponUsageModelDto extends CouponModelDto {

	private static final long serialVersionUID = 1L;

	private String emailAddress = ""; //$NON-NLS-1$

	private static final int HASHCODE_MULTIPLY = 37;

	/**
	 * The default constructor.
	 */
	public CouponUsageModelDto() {
		super();
	}

	/**
	 * Normal constructor.
	 * @param uidPk The uidpk of the domain object we're representing
	 * @param couponCode The coupon code
	 * @param emailAddress The email address
	 * @param isSuspended Whether this coupon usage is suspended
	 */
	public CouponUsageModelDto(final long uidPk, final String couponCode, final String emailAddress, final boolean isSuspended) {
		super(uidPk, couponCode, isSuspended);
		this.emailAddress = emailAddress;
	}

	/**
	 * 
	 * @return The email address.
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * 
	 * @param emailAddress The email address to set.
	 */
	public void setEmailAddress(final String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Generate the hash code.
	 *
	 * @return the hash code.
	 */
	@Override
	public int hashCode() {
		final int result = super.hashCode();
		return result * HASHCODE_MULTIPLY + ObjectUtils.hashCode(StringUtils.upperCase(emailAddress));
	}

	/**
	 * Determines whether the given object is equal to this CouponUsage.
	 * Two CouponUsageModels are considered equal if their couponCodes and emailAddresses are equal.
	 * @param obj the object to which this one should be compared for equality
	 * @return true if the given object is equal to this one
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CouponUsageModelDto)) {
			return false;
		}
		if (this == obj) {
			return true;
		}

		return super.equals(obj) && StringUtils.equalsIgnoreCase(emailAddress, ((CouponUsageModelDto) obj).emailAddress);
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(super.toString());
		if (StringUtils.isNotBlank(emailAddress)) {
			stringBuilder.append(",").append("email address:").append(emailAddress);
		}
		return stringBuilder.toString();
	}

	/**
	 * Compares {@code CouponUsageModel}s by the coupon code.
	 */
	public static class CodeComparator implements Comparator<CouponModelDto>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public int compare(final CouponModelDto object1, final CouponModelDto object2) {
			return object1.getCouponCode().compareTo(object2.getCouponCode());
		}
	};

	/**
	 * Compares {@code CouponUsageModel}s by the email address.
	 */
	public static class EmailComparator implements Comparator<CouponModelDto>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public int compare(final CouponModelDto object1, final CouponModelDto object2) {
			if (object1 instanceof CouponUsageModelDto && object2 instanceof CouponUsageModelDto) {
				return ((CouponUsageModelDto) object1).getEmailAddress().compareTo(((CouponUsageModelDto) object2).getEmailAddress());
			}

			return 0;
		}
	};

}
