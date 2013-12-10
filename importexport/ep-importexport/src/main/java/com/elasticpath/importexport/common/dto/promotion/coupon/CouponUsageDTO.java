package com.elasticpath.importexport.common.dto.promotion.coupon;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * Maps <code>CouponUsage</code> on XML.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CouponUsageDTO implements Dto {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "coupon_code", required = true)
	private String couponCode;
	
	@XmlElement(name = "active_in_cart", required = true)
	private boolean activeInCart;

	// only limited specific user coupon usages are imported.
	@XmlElement(name = "email", required = true)
	private String email;
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * @return coupon code
	 */
	public String getCouponCode() {
		return couponCode;
	}

	/**
	 * @return the activeInCart
	 */
	public boolean isActiveInCart() {
		return activeInCart;
	}
	
	/**
	 * @param activeInCart the activeInCart to set
	 */
	public void setActiveInCart(final boolean activeInCart) {
		this.activeInCart = activeInCart;
	}

	/**
	 * @param couponCode coupon code
	 */
	public void setCouponCode(final String couponCode) {
		this.couponCode = couponCode;
	}
}
