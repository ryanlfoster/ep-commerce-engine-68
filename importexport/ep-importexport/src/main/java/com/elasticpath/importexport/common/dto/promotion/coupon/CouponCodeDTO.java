package com.elasticpath.importexport.common.dto.promotion.coupon;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * Maps coupon code on XML.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CouponCodeDTO implements Dto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "coupon_code", required = true)
	private String couponCode;

	/**
	 * @return the couponCode
	 */
	public String getCouponCode() {
		return couponCode;
	}

	/**
	 * @param couponCode the couponCode to set
	 */
	public void setCouponCode(final String couponCode) {
		this.couponCode = couponCode;
	}
}
