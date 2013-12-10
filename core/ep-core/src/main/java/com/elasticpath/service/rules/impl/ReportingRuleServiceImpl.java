package com.elasticpath.service.rules.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.ReportingRuleService;
import com.elasticpath.service.rules.RuleService;

/**
 * 
 * Report usage service implementation.
 *
 */
public class ReportingRuleServiceImpl implements ReportingRuleService {
	
	private static final Logger LOG = Logger.getLogger(ReportingRuleServiceImpl.class);
	
	private static final String PLACEHOLDER_FOR_LIST = "list";
	
	private static final int CURRENCY_IDX = 0;	
	private static final int STORECODE_IDX = 1;	
	private static final int RULEID_IDX = 2;
	private static final int RULENAME_IDX = 3;
	private static final int USAGE_TYPE_IDX = 4;		
	private static final int START_DATETIME_IDX = 5;	
	private static final int END_DATETIME_IDX = 6;
	private static final int SCENARIO_IDX = 7;	
	private static final int TOTALAMOUNT_IDX = 8;
	private static final int ORDERS_QTY_IDX = 9;	
	private static final int ORDERS_PERCENT_IDX = 10;
	private static final int TATAL_COLUMNS = 11;	
	private static final int TOTAL_ORDERS_QTY_IDX = 2;
	private static final int SCALE = 2;
	private static final float ONE_HUNDRED = 100.00f;
	
	private PersistenceEngine persistenceEngine;
	
	private CouponConfigService couponConfigService;
	
	private RuleService ruleService;
	
	
	
	@Override
	public List<Object[]> getPromotionUsageData(
			final Collection<Long> storeUids,
			final Currency currency,
			final Date startDate,
			final Date endDate,
			final Boolean withCouponCodesOnly) {

		String queryName;		
		if (withCouponCodesOnly) {
			queryName = "RULES_USAGE_COUPONS_ONLY";
		} else {
			queryName = "RULES_USAGE_ALL"; 
		}
		
		final List<Object[]> rawResult = new ArrayList<Object[]>();

		final List<Object[]> revenueRecords = getPersistenceEngine().retrieveByNamedQueryWithList(queryName,
				PLACEHOLDER_FOR_LIST,
				storeUids,
				currency,
				startDate,
				endDate,
				1);
		
		rawResult.addAll(revenueRecords);
		
		rawResult.addAll(getNonRevenuePromotions(revenueRecords, storeUids, currency, withCouponCodesOnly));
		
		enrichWithTotalPercents(storeUids, currency, startDate, endDate, rawResult);
		
		enrichWithCartScenarioDatesAndUsageType(rawResult);
		
		return rawResult;		
		
	}
	
	@Override
	public List<Object[]> getPromotionDetailsData(final long storeUid, final Currency currency, final Date startDate, final Date endDate,
			final String ruleCode, final String couponCode) {
		return getPersistenceEngine().retrieveByNamedQuery("PROMOTION_DETAILS", storeUid, currency, startDate, endDate, ruleCode, couponCode);
	}

	/**
	 * Enrich raw result with % total orders data.
	 * @param storeUids store uids to wich user has access.
	 * @param currency currency code or null for all currencies
	 * @param startDate start date or null 
	 * @param endDate end date. Not nullable parameter
	 * @param rawResult
	 */
	private void enrichWithTotalPercents(final Collection<Long> storeUids,
			final Currency currency, final Date startDate, final Date endDate,
			final List<Object[]> rawResult) {
		
		final Map<Pair<String, String>, Long> totalResult = getCurrencyStoreTotalOrders(
				storeUids, currency, startDate, endDate);
		
		for (Object[] objects : rawResult) {			
			Pair<String, String> key = new Pair<String, String>((String) objects[CURRENCY_IDX], (String) objects[STORECODE_IDX]);			
			if (totalResult.get(key) == null) {				
				objects[ORDERS_PERCENT_IDX] = BigDecimal.ZERO;			
			} else {
				double totalOrdersQty = totalResult.get(key).longValue();
				BigDecimal totalPercent = new BigDecimal((Long) objects[ORDERS_PERCENT_IDX] / totalOrdersQty * ONE_HUNDRED);
				objects[ORDERS_PERCENT_IDX] = totalPercent.setScale(SCALE, BigDecimal.ROUND_HALF_UP);
			}
		}
	}

	/**
	 * @return mpa of currency, store code - total quantity of orders.
	 */
	private Map<Pair<String, String>, Long> getCurrencyStoreTotalOrders(
			final Collection<Long> storeUids, final Currency currency,
			final Date startDate, final Date endDate) {
		final List<Object[]> rawTotalResult = getPersistenceEngine().retrieveByNamedQueryWithList("RULES_USAGE_TOTAL_ORDERS",
				PLACEHOLDER_FOR_LIST,
				storeUids,
				currency,
				startDate,
				endDate);
		
		final Map<Pair<String, String>, Long> totalResult = new HashMap<Pair<String, String>, Long>(rawTotalResult.size());
		for (Object[] objects : rawTotalResult) {
			totalResult.put(
					new Pair<String, String>((String) objects[CURRENCY_IDX], (String) objects[STORECODE_IDX]), 
					(Long) objects[TOTAL_ORDERS_QTY_IDX]);
		}
		return totalResult;
	}
	

	private List<Object[]> getNonRevenuePromotions(
			final List<Object[]> rawResult,
			final Collection<Long> storeUids,
			final Currency currency,			
			final Boolean withCouponCodesOnly) {
		
		Collection<Long> promoUidPks = new ArrayList<Long>();
		for (Object[] objects : rawResult) {
			promoUidPks.add((Long) (objects[RULEID_IDX]));
		}

		Collection<Rule> nonRevenue = Collections.emptyList();
		if (promoUidPks.isEmpty()) {
			nonRevenue = getPersistenceEngine().retrieveByNamedQueryWithList("RULE_FIND_IN_STORE_LIST", PLACEHOLDER_FOR_LIST, storeUids);
		} else {
			Map<String, Collection<Long>> parameterValueMap = new HashMap<String, Collection<Long>>();

			parameterValueMap.put("list", promoUidPks);
			parameterValueMap.put("store_uids", storeUids);

			nonRevenue = getPersistenceEngine().retrieveByNamedQueryWithList("RULE_FIND_BY_NOT_UIDS_IN_STORE", parameterValueMap);
		}

		List<Object[]> nonRevenuePromos = new ArrayList<Object[]>(nonRevenue.size());
		for (Rule rule : nonRevenue) {
			if (matchToFilter(currency, withCouponCodesOnly, rule)) {
				Object[] reportObject = constructNonRevenueRecord(rule);
				nonRevenuePromos.add(reportObject);
			}
		}
		return nonRevenuePromos;		
	}

	private boolean matchToFilter(final Currency currency,
			final Boolean withCouponCodesOnly, 
			final Rule rule) {
		CouponConfig couponConfig = couponConfigService.findByRuleCode(rule.getCode());
		return !(couponConfig == null && withCouponCodesOnly) 
			&& currency == null 
			&& rule.getStore() != null;
	}
	
	
	
	

	private Object[] constructNonRevenueRecord(final Rule rule) {
		Object[] reportObject = new Object[TATAL_COLUMNS];
		reportObject[CURRENCY_IDX] = StringUtils.EMPTY;			
		reportObject[STORECODE_IDX] = rule.getStore().getCode();
		reportObject[SCENARIO_IDX] = RuleScenarios.CART_SCENARIO;						
		reportObject[RULEID_IDX] = new Long(rule.getUidPk());
		reportObject[TOTALAMOUNT_IDX] = BigDecimal.ZERO;
		reportObject[ORDERS_QTY_IDX] = BigDecimal.ZERO;
		reportObject[ORDERS_PERCENT_IDX] = BigDecimal.ZERO;
		reportObject[RULENAME_IDX] = rule.getName();
		return reportObject;
	}

	/**
	 * Enrich with usage type and dates.
	 * @param rawResult raw result to enrich with shopping cart promo dates.
	 */
	private void enrichWithCartScenarioDatesAndUsageType(final List<Object[]> rawResult) {		 
		for (Object[] objects : rawResult) {
			Rule rule = getRuleService().get((Long) objects[RULEID_IDX]);
			CouponConfig couponConfig = couponConfigService.findByRuleCode(rule.getCode());
			objects[USAGE_TYPE_IDX] = getUsageTypeName(couponConfig);			
			if (RuleScenarios.CART_SCENARIO == (Integer) objects[SCENARIO_IDX]) {
				objects[START_DATETIME_IDX] = rule.getStartDateFromSellingContext();
				objects[END_DATETIME_IDX] = rule.getEndDateFromSellingContext();				
			}
		}
	}
	
	private String getUsageTypeName(final CouponConfig couponConfig) {
		if (couponConfig != null) {
			if (CouponUsageType.LIMIT_PER_COUPON == couponConfig.getUsageType()) {
				return "Per Coupon";				
			} else if (CouponUsageType.LIMIT_PER_ANY_USER == couponConfig.getUsageType()) {
				return "Per Any User";				
			} else if (CouponUsageType.LIMIT_PER_SPECIFIED_USER == couponConfig.getUsageType()) {
				return "Per Specified User";				
			}
		}
		return "None";
	}
	
	
	
	/**
	 * Sets the persistence engine.
	 *
	 * @param persistenceEngine the persistence engine to set.
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Persistence engine initialized ... " + persistenceEngine);
		}
	}

	/**
	 * Returns the persistence engine.
	 *
	 * @return the persistence engine.
	 */
	public PersistenceEngine getPersistenceEngine() {
		return this.persistenceEngine;
	}
	
	
	/**
	 * Set the coupon config service.
	 * 
	 * @param couponConfigService the couponConfigService to set
	 */
	public void setCouponConfigService(final CouponConfigService couponConfigService) {
		this.couponConfigService = couponConfigService;
	}

	/**
	 * Get the coupon config service.
	 * 
	 * @return the couponConfigService
	 */
	public CouponConfigService getCouponConfigService() {
		return couponConfigService;
	}



	/**
	 * 
	 * @return rule service.
	 */
	public RuleService getRuleService() {
		return ruleService;
	}



	/**
	 * Set rule service to use.
	 * @param ruleService rule service to use.
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

}
