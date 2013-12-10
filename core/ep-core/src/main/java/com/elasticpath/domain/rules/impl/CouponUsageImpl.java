package com.elasticpath.domain.rules.impl;

import java.util.Calendar;
import java.util.Date;

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
import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * Represents the usage count of a coupon.
 */
@Entity
@Table(name = CouponUsageImpl.TABLE_NAME)
@DataCache(enabled = false)
public class CouponUsageImpl extends AbstractEntityImpl implements CouponUsage {

	private static final long serialVersionUID = -2387874741200566371L;

	private long uidPk;
	private Coupon coupon;
	private int useCount;
	private String customerEmailAddress;
	private boolean activeInCart;
	private Date limitedDurationEndDate;
	private boolean suspended;

	/** Database Table. */
	public static final String TABLE_NAME = "TCOUPONUSAGE";

	private static final int HASHCODE_MULTIPLY = 37;
	private static final int HASHCODE_START = 17;

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
	@ManyToOne(targetEntity = CouponImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "COUPON_UID")
	@ForeignKey
	public Coupon getCoupon() {
		return coupon;
	}

	@Override
	@Basic
	@Column(name = "USECOUNT")
	public int getUseCount() {
		return useCount;
	}

	@Override
	public void setCoupon(final Coupon coupon) {
		this.coupon = coupon;
	}

	@Override
	public void setUseCount(final int useCount) {
		this.useCount = useCount;
	}

	@Override
	@Basic
	@Column(name = "CUSTOMER_EMAIL_ADDRESS", nullable = false)
	public String getCustomerEmailAddress() {
		return customerEmailAddress;
	}

	@Override
	public void setCustomerEmailAddress(final String customerEmailAddress) {
		this.customerEmailAddress = customerEmailAddress;
	}

	/**
	 * Generate the hash code.
	 *
	 * @return the hash code.
	 */
	@Override
	public int hashCode() {
		int result = HASHCODE_START;
		result = result * HASHCODE_MULTIPLY + ObjectUtils.hashCode(coupon);
		return result * HASHCODE_MULTIPLY + ObjectUtils.hashCode(customerEmailAddress);
	}

	/**
	 * Determines whether the given object is equal to this BaseAmount.
	 * Two BaseAmounts are considered equal if their GUIDs are equal.
	 * @param obj the object to which this one should be compared for equality
	 * @return true if the given object is equal to this one
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CouponUsageImpl)) {
			return false;
		}
		if (this == obj) {
			return true;
		}

		return ObjectUtils.equals(coupon, ((CouponUsageImpl) obj).coupon)
			&& ObjectUtils.equals(customerEmailAddress, ((CouponUsageImpl) obj).customerEmailAddress);
	}

	@Override
	@Basic
	@Column(name = "ACTIVE_IN_CART", nullable = false)
	public boolean isActiveInCart() {
		return activeInCart;
	}

	@Override
	public void setActiveInCart(final boolean activeInCart) {
		this.activeInCart = activeInCart;
	}

	@Override
	@Transient
	public boolean isSuspended() {
		return getCoupon().isSuspended() || isSuspendedInternal();
	}

	@Basic
	@Column(name = "SUSPENDED", nullable = false)
	protected boolean isSuspendedInternal() {
		return suspended;
	}

	/**
	 * @param suspended if coupon usage is suspended.
	 */
	protected void setSuspendedInternal(final boolean suspended) {
		this.suspended = suspended;
	}

	@Override
	public void setSuspended(final boolean suspended) {
		setSuspendedInternal(suspended);
	}

	@Override
	@Basic
	@Column(name = "LIMITED_DURATION_END_DATE")
	public Date getLimitedDurationEndDate() {
		return limitedDurationEndDate;
	}

	public void setLimitedDurationEndDate(final Date limitedDurationEndDate) {
		this.limitedDurationEndDate = limitedDurationEndDate;
	}

	@Override
	public void setLimitedDurationStartDate(final Date startDate) {
		final int fiftyNine = 59;
		final int twentyThree = 23;
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(startDate);
		endDate.add(Calendar.DATE, getCoupon().getCouponConfig().getDurationDays());
		endDate.set(Calendar.HOUR_OF_DAY, twentyThree);
		endDate.set(Calendar.MINUTE, fiftyNine);
		endDate.set(Calendar.SECOND, fiftyNine);
		setLimitedDurationEndDate(endDate.getTime());
	}

	//FIXME use a proper GUID.
	//This GUID does not obey the contract of GUIDs in our system. Consider either persisting the GUID
	//or making this a dependent value-object instead of an entity.
	/**
	 * The guid of a usage record is a combination of coupon code and email.
	 *
	 * @return the guid
	 */
	@Override
	@Transient
	public String getGuid() {
		StringBuilder guid = new StringBuilder(getCoupon().getCouponCode());
		if (!StringUtils.isEmpty(getCustomerEmailAddress())) {
			guid.append('|').append(getCustomerEmailAddress());
		}
		return guid.toString();
	}

	@Override
	public void setGuid(final String guid) {
		//do nothing: the GUID is a derived field.
	}

	@Override
	public void initialize() {
		// don't call super or else setGuid will be called
		// super.initialize();
		setActiveInCart(true);
		setSuspended(false);
		setCustomerEmailAddress(""); // not null!
	}

}
