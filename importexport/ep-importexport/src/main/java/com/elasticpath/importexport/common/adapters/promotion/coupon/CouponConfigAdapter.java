package com.elasticpath.importexport.common.adapters.promotion.coupon;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.promotion.coupon.CouponConfigDTO;

/**
 * The coupon config adapter for import/export.
 */
public class CouponConfigAdapter extends AbstractDomainAdapterImpl<CouponConfig, CouponConfigDTO> {
	@Override
	public void populateDTO(final CouponConfig source, final CouponConfigDTO target) {
		target.setCouponConfigCode(source.getGuid());
		target.setDurationDays(source.getDurationDays());
		target.setLimitedDuration(source.isLimitedDuration());
		target.setRuleCode(source.getRuleCode());
		target.setUsageLimit(source.getUsageLimit());
		target.setUsageType(source.getUsageType().getName());
		target.setMultiUsePerOrder(source.isMultiUsePerOrder());
	}

	@Override
	public void populateDomain(final CouponConfigDTO source, final CouponConfig target) {
		target.setGuid(source.getCouponConfigCode());
		target.setLimitedDuration(source.isLimitedDuration());
		target.setUsageType(CouponUsageType.getEnum(source.getUsageType()));
		target.setDurationDays(source.getDurationDays());
		target.setRuleCode(source.getRuleCode());
		target.setUsageLimit(source.getUsageLimit());
		target.setMultiUsePerOrder(source.isMultiUsePerOrder());
	}
	
	@Override
	public CouponConfig createDomainObject() {
		return getBean(ContextIdNames.COUPON_CONFIG);
	}
	
	@Override
	public CouponConfigDTO createDtoObject() {
		return new CouponConfigDTO();
	}
}
