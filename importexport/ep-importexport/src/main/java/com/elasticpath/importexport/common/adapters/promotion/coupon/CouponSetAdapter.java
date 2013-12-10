package com.elasticpath.importexport.common.adapters.promotion.coupon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.promotion.coupon.CouponConfigDTO;
import com.elasticpath.importexport.common.dto.promotion.coupon.CouponSetDTO;
import com.elasticpath.importexport.common.dto.promotion.coupon.CouponUsageDTO;

/**
 * Coupon set adapter for import/export.
 */
public class CouponSetAdapter extends AbstractDomainAdapterImpl<CouponSet, CouponSetDTO> {
	private CouponConfigAdapter couponConfigAdapter;

	private CouponUsageAdapter couponUsageAdapter;

	@Override
	public void populateDTO(final CouponSet source, final CouponSetDTO target) {
		target.setCouponConfigDTO(extractCouponConfig(source));
		target.setCouponCodes(extractCouponCodes(source));
		target.setCouponUsageDTO(extractCouponUsages(source));
	}

	private List<CouponUsageDTO> extractCouponUsages(final CouponSet source) {
		List<CouponUsageDTO> usageDTOs = new ArrayList<CouponUsageDTO>();
		for (Collection<CouponUsage> usages : source.getUsagesMap().values()) {
			for (CouponUsage usage : usages) {
				CouponUsageDTO usageDTO = couponUsageAdapter.createDtoObject();
				couponUsageAdapter.populateDTO(usage , usageDTO);
				usageDTOs.add(usageDTO);
			}
		}
		return usageDTOs;
	}

	private CouponConfigDTO extractCouponConfig(final CouponSet source) {
		CouponConfigDTO configDTO = couponConfigAdapter.createDtoObject();
		couponConfigAdapter.populateDTO(source.getCouponConfig(), configDTO);
		return configDTO;
	}

	private List<String> extractCouponCodes(final CouponSet source) {
		List<String> couponCodes = new ArrayList<String>();
		for (Coupon coupon : source.getCoupons()) {
			couponCodes.add(coupon.getCouponCode());
		}
		return couponCodes;
	}

	@Override
	public void populateDomain(final CouponSetDTO source, final CouponSet target) {
		CouponConfigDTO couponConfigDTO = source.getCouponConfigDTO();

		// populate coupon config
		CouponConfig couponConfig = populateCouponConfig(couponConfigDTO);
		target.setCouponConfig(couponConfig);

		// populate coupon code
		for (String couponCode : source.getCouponCodes()) {
			Coupon coupon = populateCoupon(couponCode);
			target.getCoupons().add(coupon);
		}

		// populate coupon usage
		for (CouponUsageDTO couponUsageDTO : source.getCouponUsageDTO()) {
			CouponUsage couponUsage = couponUsageAdapter.createDomain();
			couponUsageAdapter.populateDomain(couponUsageDTO, couponUsage);
			target.addUsage(couponUsageDTO.getCouponCode(), couponUsage);
		}
	}

	/**
	 * Populates coupon based on coupon code.
	 * 
	 * @param couponCode coupon code.
	 * @return {@link Coupon}.
	 */
	protected Coupon populateCoupon(final String couponCode) {
		Coupon coupon = createCouponDomain();
		coupon.setCouponCode(couponCode);
		return coupon;
	}

	/**
	 * Populates coupon config based on {@link CouponConfigDTO}.
	 * 
	 * @param couponConfigDTO {@link CouponConfigDTO}.
	 * @return {@link CouponConfigDTO}.
	 */
	protected CouponConfig populateCouponConfig(final CouponConfigDTO couponConfigDTO) {
		CouponConfig couponConfig = couponConfigAdapter.createDomainObject();
		couponConfigAdapter.populateDomain(couponConfigDTO, couponConfig);
		return couponConfig;
	}

	@Override
	public CouponSet createDomainObject() {
		return new CouponSet();
	}

	@Override
	public CouponSetDTO createDtoObject() {
		return new CouponSetDTO();
	}

	/**
	 * Setter for {@link CouponConfigAdapter}.
	 * 
	 * @param couponConfigAdapter {@link CouponConfigAdapter}.
	 */
	public void setCouponConfigAdapter(final CouponConfigAdapter couponConfigAdapter) {
		this.couponConfigAdapter = couponConfigAdapter;
	}

	/**
	 * Gets {@link Coupon} by coupon code. Creates a new one if there is none.
	 * 
	 * @return {@link Coupon}.
	 */
	private Coupon createCouponDomain() {
		return getBean(ContextIdNames.COUPON);
	}

	/**
	 * @param couponUsageAdapter the couponUsageAdapter to set
	 */
	public void setCouponUsageAdapter(final CouponUsageAdapter couponUsageAdapter) {
		this.couponUsageAdapter = couponUsageAdapter;
	}
	
}
