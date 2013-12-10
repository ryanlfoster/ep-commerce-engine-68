package com.elasticpath.importexport.common.adapters.promotion.coupon;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.promotion.coupon.CouponUsageDTO;

/**
* The coupon usage adapter for import/export.
*/
public class CouponUsageAdapter extends AbstractDomainAdapterImpl<CouponUsage, CouponUsageDTO> {

	@Override
	public void populateDTO(final CouponUsage source, final CouponUsageDTO target) {
		target.setActiveInCart(source.isActiveInCart());
		target.setCouponCode(source.getCoupon().getCouponCode());
		target.setEmail(source.getCustomerEmailAddress());
	}

	@Override
	public void populateDomain(final CouponUsageDTO source, final CouponUsage target) {
		target.setCustomerEmailAddress(source.getEmail());
		target.setActiveInCart(source.isActiveInCart());
	}

	/**
	 * Creates a coupon usage domain.
	 * @return instance of CouponUsage
	 */
	public CouponUsage createDomain() {
		return getBean(ContextIdNames.COUPON_USAGE);
	}
	
	/**
	 * Creates a coupon usage dto.
	 * @return instance of CouponUsageDTO
	 */
	public CouponUsageDTO createDtoObject() {
		return new CouponUsageDTO();
	}
}
