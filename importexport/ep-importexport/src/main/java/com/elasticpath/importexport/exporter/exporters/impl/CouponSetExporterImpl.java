package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.promotion.coupon.CouponSet;
import com.elasticpath.importexport.common.adapters.promotion.coupon.CouponSetAdapter;
import com.elasticpath.importexport.common.dto.promotion.coupon.CouponSetDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.rules.RuleService;

/**
 * Coupon set exporter for exporting coupon set.
 */
public class CouponSetExporterImpl extends AbstractExporterImpl<CouponSet, CouponSetDTO, String> {

	private CouponConfigService couponConfigService;

	private RuleService ruleService;

	private CouponUsageService couponUsageService;

	private CouponService couponService;

	private CouponSetAdapter couponSetAdapter;

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		// do nothing
	}

	@Override
	protected List<String> getListExportableIDs() {
		List<Long> exportableIDs = new ArrayList<Long>(getContext().getDependencyRegistry().getDependentUids(Rule.class));
		return filterLimitedUseCouponRules(exportableIDs);
	}

	// Gets all the limited use coupon code condition rules.
	private List<String> filterLimitedUseCouponRules(final List<Long> limitedUseCouponRuleCodes) {
		List<String> results = new ArrayList<String>();
		for (Rule rule : ruleService.findByUids(limitedUseCouponRuleCodes)) {
			for (RuleElement ruleElement : rule.getRuleElements()) {
				if (ruleElement.getElementType().equals(RuleElementType.LIMITED_USE_COUPON_CODE_CONDITION)) {
					results.add(rule.getCode());
					break;
				}
			}
		}

		return results;
	}

	@Override
	protected List<CouponSet> findByIDs(final List<String> ruleCodes) {
		List<CouponSet> results = new ArrayList<CouponSet>();
		for (String ruleCode : ruleCodes) {
			CouponSet couponSet = getCouponSet(ruleCode);
			if (couponSet != null) {
				results.add(couponSet);
			}
		}

		return results;
	}

	private CouponSet getCouponSet(final String ruleCode) {
		CouponConfig config = couponConfigService.findByRuleCode(ruleCode);
		if (config == null) {
			return null;
		}

		CouponSet couponSet = new CouponSet();
		couponSet.setCouponConfig(config);

		Collection<Coupon> coupons = couponService.findCouponsForRuleCode(ruleCode);
		couponSet.setCoupons(coupons);

		for (Coupon coupon : coupons) {
			for (CouponUsage usage : couponUsageService.findByCode(coupon.getCouponCode())) {
				if (usage.getCoupon().getCouponConfig().getUsageType().equals(CouponUsageType.LIMIT_PER_SPECIFIED_USER)) {
					couponSet.addUsage(coupon.getCouponCode(), usage);
				}
			}
		}

		return couponSet;
	}

	@Override
	protected DomainAdapter<CouponSet, CouponSetDTO> getDomainAdapter() {
		return couponSetAdapter;
	}

	@Override
	protected Class<? extends CouponSetDTO> getDtoClass() {
		return CouponSetDTO.class;
	}

	@Override
	public Class< ? >[] getDependentClasses() {
		return new Class< ? >[] { CouponSet.class };
	}

	@Override
	public JobType getJobType() {
		return JobType.COUPONSET;
	}

	/**
	 * @param couponConfigService the couponConfigService to set
	 */
	public void setCouponConfigService(final CouponConfigService couponConfigService) {
		this.couponConfigService = couponConfigService;
	}

	/**
	 * @param ruleService the ruleService to set
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	/**
	 * @param couponUsageService the couponUsageService to set
	 */
	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}

	/**
	 * @param couponService the couponService to set
	 */
	public void setCouponService(final CouponService couponService) {
		this.couponService = couponService;
	}

	/**
	 * Setter for coupon set adapter.
	 * 
	 * @param couponSetAdapter {@link CouponSetAdapter}.
	 */
	public void setCouponSetAdapter(final CouponSetAdapter couponSetAdapter) {
		this.couponSetAdapter = couponSetAdapter;
	}
}
