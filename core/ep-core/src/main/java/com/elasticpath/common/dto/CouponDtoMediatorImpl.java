package com.elasticpath.common.dto;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.DuplicateCouponException;

/**
 * Mediator to persist coupon DTO collections by transforming to domain objects
 * and calling the coupon service to do the persisting.
 */
public class CouponDtoMediatorImpl implements CouponDtoMediator {

	private CouponService couponService;
	
	private CouponConfigService couponConfigService;
	
	private BeanFactory beanFactory;
	
	private CouponModelDtoAssembler couponAssembler;
	
	@Override
	public void add(final Collection<CouponModelDto> addedCoupons, final String ruleCode) throws EpServiceException {
		CouponConfig config = getCouponConfigService().findByRuleCode(ruleCode);
		for (CouponModelDto dto : addedCoupons) {
			final Coupon coupon = getCouponAssembler().assembleDomain(dto, config);
			getCouponService().add(coupon);		
		}		
	}

	@Override
	public void validate(final Collection<CouponModelDto> addedCouponItems, final String ruleCode) {
		for (CouponModelDto dto : addedCouponItems) {
			if (getCouponService().findByCouponCode(dto.getCouponCode()) != null) {
				throw new DuplicateCouponException("Coupon code is duplicated", dto.getCouponCode());
			}
		}
	}
	
	@Override
	public void update(final Collection<CouponModelDto> couponUpdates) throws EpServiceException {
		Map<Long, CouponModelDto> modelMap = new HashMap<Long, CouponModelDto>();
		for (CouponModelDto dto : couponUpdates) {
			modelMap.put(dto.getUidPk(), dto);
		}
		List<Coupon> coupons = getCouponService().findByUids(modelMap.keySet());
		for (Coupon coupon : coupons) {
			CouponModelDto dto = modelMap.get(coupon.getUidPk());
			assembleDomain(coupon, dto);
			getCouponService().update(coupon);		
		}
	}

	/**
	 * Assemble the domain object from the DTO.
	 *
	 * @param coupon the domain object to assemble
	 * @param dto the dto
	 */
	protected void assembleDomain(final Coupon coupon, final CouponModelDto dto) {
		coupon.setSuspended(dto.isSuspended());
		coupon.setCouponCode(dto.getCouponCode());
	}

	/**
	 * @param couponService the couponService to set
	 */
	public void setCouponService(final CouponService couponService) {
		this.couponService = couponService;
	}

	/**
	 * @return the couponService
	 */
	public CouponService getCouponService() {
		return couponService;
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
	 * @return the assembler
	 */
	private CouponModelDtoAssembler getCouponAssembler() {
		if (couponAssembler == null) {
			couponAssembler = new CouponModelDtoAssembler(getBeanFactory());
		}
		return couponAssembler;
	}

}
