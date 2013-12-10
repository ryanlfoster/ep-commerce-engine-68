package com.elasticpath.importexport.common.dto.promotion.coupon;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.common.dto.Dto;

/**
 * Maps coupon set on XML.
 */
@XmlRootElement(name = CouponSetDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
public class CouponSetDTO implements Dto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Root element for Coupon set. */
	public static final String ROOT_ELEMENT = "coupon_set";

	@XmlElement(name = "coupon_config", required = true)
	private CouponConfigDTO couponConfigDTO;

	@XmlElementWrapper(name = "coupons")
	@XmlElement(name = "coupon_code")
	private List<String> couponCodes = new ArrayList<String>();

	@XmlElementWrapper(name = "coupon_usages")
	@XmlElement(name = "coupon_usage")
	private List<CouponUsageDTO> couponUsageDTO = new ArrayList<CouponUsageDTO>();

	/**
	 * @param couponUsageDTO the couponUsageDTO to set
	 */
	public void setCouponUsageDTO(final List<CouponUsageDTO> couponUsageDTO) {
		this.couponUsageDTO = couponUsageDTO;
	}

	/**
	 * @return the couponCodes
	 */
	public List<String> getCouponCodes() {
		return couponCodes;
	}

	/**
	 * Setter for coupon codes.
	 * 
	 * @param couponCodes coupon codes.
	 */
	public void setCouponCodes(final List<String> couponCodes) {
		this.couponCodes = couponCodes;
	}

	/**
	 * @return {@link CouponConfigDTO}.
	 */
	public CouponConfigDTO getCouponConfigDTO() {
		return couponConfigDTO;
	}

	/**
	 * Setter for {@link CouponConfigDTO}.
	 * 
	 * @param couponConfigDTO {@link CouponConfigDTO}.
	 */
	public void setCouponConfigDTO(final CouponConfigDTO couponConfigDTO) {
		this.couponConfigDTO = couponConfigDTO;
	}

	/**
	 * @return coupon usage dtos
	 */
	public List<CouponUsageDTO> getCouponUsageDTO() {
		return couponUsageDTO;
	}
}
