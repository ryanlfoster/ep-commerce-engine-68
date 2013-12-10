package com.elasticpath.csvimport.impl;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.rules.CouponConfigService;

/**
 * 
 * ImportDtoJobRUnnerCsvImpl for CouponUsage and CouponConfig.
 *
 */
public class ImportCouponUsageDtoRunnerCsvImpl extends ImportDtoJobRunnerCsvImpl<CouponUsage, CouponConfig> {
	// This class is just for generic declaration
	// how can make it by spring configuration?
	
	private CouponConfigService couponConfigService;
	
	/**
	 * Set the couponConfigService.
	 * 
	 * @param couponConfigService the couponConfigService
	 */
	public void setCouponConfigService(final CouponConfigService couponConfigService) {
		this.couponConfigService = couponConfigService;
	}

	/**
	 * Get the dependent object.
	 * 
	 * @return the couponConfig object.
	 */
	protected CouponConfig getDependentObjectGuid() {
		String parameterStr = getImportJobRequest().getParameter();
		String[] parameterNameAndValue = StringUtils.split(parameterStr, "=");
		String parameterName = parameterNameAndValue[0];
		String parameterValue = parameterNameAndValue[1];
		
		if ("COUPON_CONFIG_GUID".equals(parameterName)) {
			String couponConfigGuid = parameterValue;	
			return couponConfigService.findByCode(couponConfigGuid);
		} 

		throw new EpServiceException("Import parameter error, the parameter COUPON_CONFIG_GUID cannot be found");		 
	}
}
