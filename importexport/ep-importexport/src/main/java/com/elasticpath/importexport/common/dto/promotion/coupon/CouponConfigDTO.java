package com.elasticpath.importexport.common.dto.promotion.coupon;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * Maps <code>CouponConfig</code> on XML.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CouponConfigDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "rule_code", required = true)
	private String ruleCode;

	@XmlElement(name = "coupon_config_code", required = true)
	private String couponConfigCode;

	@XmlElement(name = "coupon_usage_type", required = true)
	private String usageType;

	@XmlElement(name = "usage_limit", required = false, defaultValue = "0")
	private Integer usageLimit = 0;

	@XmlElement(name = "duration_days", required = false, defaultValue = "0")
	private Integer durationDays = 0;

	@XmlElement(name = "limited_duration", required = false, defaultValue = "false")
	private Boolean limitedDuration = Boolean.FALSE;

	@XmlElement(name = "multi_use_per_order", required = false, defaultValue = "false")
	private Boolean multiUsePerOrder = Boolean.FALSE;

	/**
	 * @return coupon config code.
	 */
	public String getCouponConfigCode() {
		return couponConfigCode;
	}

	/**
	 * @return duration days.
	 */
	public int getDurationDays() {
		return durationDays;
	}

	/**
	 * @return rule code.
	 */
	public String getRuleCode() {
		return ruleCode;
	}

	/**
	 * @return coupon usage type.
	 */
	public String getUsageType() {
		return usageType;
	}

	/**
	 * @return usage limit.
	 */
	public int getUsageLimit() {
		return usageLimit;
	}

	/**
	 * @return limited duration.
	 */
	public boolean isLimitedDuration() {
		return limitedDuration;
	}

	/**
	 * @return multi use per order.
	 */
	public boolean isMultiUsePerOrder() {
		return multiUsePerOrder;
	}

	/**
	 * Setter for coupon config code.
	 * 
	 * @param code coupon config code.
	 */
	public void setCouponConfigCode(final String code) {
		this.couponConfigCode = code;
	}

	/**
	 * Setter for duration days.
	 * 
	 * @param durationDays duration days.
	 */
	public void setDurationDays(final int durationDays) {
		this.durationDays = durationDays;
	}

	/**
	 * Setter for limited duration.
	 * 
	 * @param limitedDuration limited duration.
	 */
	public void setLimitedDuration(final boolean limitedDuration) {
		this.limitedDuration = limitedDuration;
	}

	/**
	 * Setter for multi use per order.
	 * 
	 * @param multiUsePerOrder True if this coupon should only be used once for any order.
	 */
	public void setMultiUsePerOrder(final boolean multiUsePerOrder) {
		this.multiUsePerOrder = multiUsePerOrder;
	}

	/**
	 * Setter for rule code.
	 * 
	 * @param ruleCode rule code.
	 */
	public void setRuleCode(final String ruleCode) {
		this.ruleCode = ruleCode;
	}

	/**
	 * Setter for coupon usage type.
	 * 
	 * @param usageType coupon usage type.
	 */
	public void setUsageType(final String usageType) {
		this.usageType = usageType;
	}

	/**
	 * Setter for usage limit.
	 * 
	 * @param usageLimit usage limit.
	 */
	public void setUsageLimit(final int usageLimit) {
		this.usageLimit = usageLimit;
	}
}
