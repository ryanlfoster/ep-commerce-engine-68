/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.common.dto;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.rules.DuplicateCouponException;

/**
/**
 * Mediator to persist coupon usage DTO collections by transforming to domain objects
 * and calling the coupon usage service to do the persisting.
 */
public class CouponUsageDtoMediatorImpl implements CouponUsageDtoMediator {

	private CouponService couponService;
	
	private CouponUsageService couponUsageService;
	
	private CouponConfigService couponConfigService;
	
	private BeanFactory beanFactory;
	
	private CouponModelDtoAssembler couponAssembler;
	
	private CouponUsageModelDtoAssembler couponUsageAssembler;
	
	@Override
	public void add(final Collection<CouponUsageModelDto> addedCouponUsages, final String ruleCode) throws EpServiceException {
		Set<String> codes = new HashSet<String>();
		for (CouponUsageModelDto dto : addedCouponUsages) {
			codes.add(dto.getCouponCode());
		}
		Map<String, Coupon> persistentCoupons = getCouponService().findCouponsForCodes(codes);
		persistentCoupons = toUpperCase(persistentCoupons);
		CouponConfig couponConfig = getCouponConfigService().findByRuleCode(ruleCode); 
		for (CouponUsageModelDto dto : addedCouponUsages) {
			Coupon coupon = persistentCoupons.get(dto.getCouponCode().toLowerCase());
			if (coupon == null) {
				coupon = getCouponAssembler().assembleDomain(dto, couponConfig);
				getCouponService().add(coupon);		
				persistentCoupons.put(dto.getCouponCode(), coupon);
			}
			CouponUsage couponUsage = getCouponUsageAssembler().assembleDomain(dto, coupon);
			getCouponUsageService().add(couponUsage);
		}
	}

	private Map <String, Coupon> toUpperCase(final Map<String, Coupon> persistentCoupons) {
		Map <String, Coupon> resultMap = new HashMap <String, Coupon>();
		for (Map.Entry<String, Coupon> entry : persistentCoupons.entrySet()) {
			resultMap.put(entry.getKey().toLowerCase(), entry.getValue());
		}
		return resultMap;
	}

	/**
	 * Validate coupons to be added.
	 * 
	 * There shouldn't be any existing coupons in the system that has the same coupon code but different rule code.
	 * 
	 * @param addCouponUsages coupon usages to add
	 * @param ruleCode rule code
	 */
	public void validate(final Collection<CouponUsageModelDto> addCouponUsages, final String ruleCode) {
		for (CouponUsageModelDto dto : addCouponUsages) {
			final Coupon findByCouponCode = getCouponService().findByCouponCode(dto.getCouponCode());
			if (findByCouponCode != null && !findByCouponCode.getCouponConfig().getRuleCode().equals(ruleCode)) {
				throw new DuplicateCouponException("Coupon code is duplicated", dto.getCouponCode());
			}
		}
	}
	
	@Override
	public void update(final Collection<CouponUsageModelDto> couponUsageUpdates) throws EpServiceException {
		Map<Long, CouponUsageModelDto> modelMap = new HashMap<Long, CouponUsageModelDto>();
		for (CouponUsageModelDto dto : couponUsageUpdates) {
			modelMap.put(dto.getUidPk(), dto);
		}
		List<CouponUsage> usages = getCouponUsageService().findByUids(modelMap.keySet());
		for (CouponUsage usage : usages) {
			CouponUsageModelDto dto = modelMap.get(usage.getUidPk());
			assembleDomain(usage, dto);
			getCouponUsageService().update(usage);
		}
	}
	
	/**
	 * Assemble the domain object from the DTO.
	 *
	 * @param couponUsage the domain object to assemble
	 * @param dto the dto
	 */
	protected void assembleDomain(final CouponUsage couponUsage, final CouponUsageModelDto dto) {
		couponUsage.setCustomerEmailAddress(dto.getEmailAddress());
		couponUsage.setSuspended(dto.isSuspended());
	}
	
	/**
	 *
	 * @param couponService the couponService to set
	 */
	public void setCouponService(final CouponService couponService) {
		this.couponService = couponService;
	}

	/**
	 *
	 * @return the couponService
	 */
	public CouponService getCouponService() {
		return couponService;
	}

	/**
	 *
	 * @param couponUsageService the couponUsageService to set
	 */
	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}

	/**
	 *
	 * @return the couponUsageService
	 */
	public CouponUsageService getCouponUsageService() {
		return couponUsageService;
	}

	/**
	 *
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 *
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 *
	 * @param couponConfigService the couponConfigService to set
	 */
	public void setCouponConfigService(final CouponConfigService couponConfigService) {
		this.couponConfigService = couponConfigService;
	}

	/**
	 *
	 * @return the couponConfigService
	 */
	public CouponConfigService getCouponConfigService() {
		return couponConfigService;
	}
	
	/**
	 * @return the assembler
	 */
	private CouponModelDtoAssembler getCouponAssembler() {
		if (couponAssembler == null) {
			couponAssembler = new CouponModelDtoAssembler(getBeanFactory());
		}
		return couponAssembler;
	}
	
	/**
	 * @return the assembler
	 */
	private CouponUsageModelDtoAssembler getCouponUsageAssembler() {
		if (couponUsageAssembler == null) {
			couponUsageAssembler = new CouponUsageModelDtoAssembler(getBeanFactory());
		}
		return couponUsageAssembler;
	}

}
