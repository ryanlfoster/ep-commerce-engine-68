package com.elasticpath.test.persister;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.rules.RuleService;

/**
 * Persister allows to create and save into database coupon dependent domain objects.
 */
public class CouponTestPersister {
	private final BeanFactory beanFactory;

	private final CouponService couponService;

	private final CouponUsageService couponUsageService;

	private final CouponConfigService couponConfigService;

	private final RuleService ruleService;

	/**
	 * Constructor initializes necessary services and beanFactory.
	 * 
	 * @param beanFactory Elastic Path factory for creating instances of beans.
	 */
	public CouponTestPersister(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		this.couponService = beanFactory.getBean(ContextIdNames.COUPON_SERVICE);
		this.couponUsageService = beanFactory.getBean(ContextIdNames.COUPON_USAGE_SERVICE);
		this.couponConfigService = beanFactory.getBean(ContextIdNames.COUPON_CONFIG_SERVICE);
		this.ruleService = beanFactory.getBean(ContextIdNames.RULE_SERVICE);
	}

	/**
	 * Creates and persists coupon based on a {@link CouponConfig}.
	 * 
	 * @param couponConfig {@link CouponConfig}.
	 * @param couponCode code of the coupon.
	 * @return the added {@link Coupon}.
	 */
	public Coupon createAndPersistCoupon(final CouponConfig couponConfig, final String couponCode) {
		Coupon coupon = beanFactory.getBean(ContextIdNames.COUPON);
		coupon.setCouponCode(couponCode);
		coupon.setCouponConfig(couponConfig);

		return couponService.add(coupon);
	}
	
	/**
	 * Creates and persists coupon based on a {@link CouponConfig}.
	 * 
	 * @param couponConfig {@link CouponConfig}.
	 * @param couponCode code of the coupon.
	 * @param suspended True if the coupon is suspended from use
	 * @return the added {@link Coupon}.
	 */
	public Coupon createAndPersistCoupon(final CouponConfig couponConfig, final String couponCode, final boolean suspended) {
		Coupon coupon = beanFactory.getBean(ContextIdNames.COUPON);
		coupon.setCouponCode(couponCode);
		coupon.setCouponConfig(couponConfig);
		coupon.setSuspended(suspended);

		return couponService.add(coupon);
	}	

	/**
	 * Creates and persists a {@link CouponConfig} with specified promotion code, limited usage and whether it's customer specific.
	 * 
	 * @param promotionCode the promotion code.
	 * @param limitedUsage the limited usage.
	 * @param usageType the coupon usage type
	 * @param limitedDuration whether the coupon has a limited duration
	 * @param duration the duration in days
	 * @return {@link CouponConfig}.
	 */
	public CouponConfig createAndPersistCouponConfig(final String promotionCode, final int limitedUsage, final CouponUsageType usageType, 
			final boolean limitedDuration, final int duration, final boolean multiUsePerOrder) {
		CouponConfig couponConfig = beanFactory.getBean(ContextIdNames.COUPON_CONFIG);
		couponConfig.setRuleCode(promotionCode);
		couponConfig.setUsageLimit(limitedUsage);
		couponConfig.setUsageType(usageType);
		couponConfig.setLimitedDuration(limitedDuration);
		couponConfig.setDurationDays(duration);
		couponConfig.setMultiUsePerOrder(multiUsePerOrder);

		return couponConfigService.add(couponConfig);
	}

	/**
	 * Creates and persists a {@link CouponConfig} with specified promotion code, limited usage and whether it's customer specific.
	 * 
	 * @param promotionCode the promotion code.
	 * @param limitedUsage the limited usage.
	 * @param usageType the coupon usage type
	 * @return {@link CouponConfig}.
	 */
	public CouponConfig createAndPersistCouponConfig(final String promotionCode, final int limitedUsage, final CouponUsageType usageType) {
		return createAndPersistCouponConfig(promotionCode, limitedUsage, usageType, false, 0, false);
	}

	/**
	 * Creates and persists a coupon usage based on a {@link Coupon}.
	 * 
	 * @param coupon the {@link Coupon}.
	 * @param email the email.
	 * @return {@link CouponUsage}.
	 */
	public CouponUsage createAndPersistCouponUsage(final Coupon coupon, final String email) {
		return createAndPersistCouponUsage(coupon, email, 0);
	}

	/**
	 * Creates and persists a coupon usage based on a {@link Coupon}.
	 * 
	 * @param coupon the {@link Coupon}.
	 * @param email the email.
	 * @param suspended True if the coupon is suspended
	 * @return {@link CouponUsage}.
	 */
	public CouponUsage createAndPersistCouponUsage(final Coupon coupon, final String email, final boolean suspended) {
		return createAndPersistCouponUsage(coupon, email, 0, true, suspended);
	}
	
	/**
	 * Creates and persists a coupon usage based on a {@link Coupon}.
	 * Note: default coupons to be active in user's cart.
	 * 
	 * @param coupon the {@link Coupon}.
	 * @param email the email.
	 * @param useCount use count.
	 * @return {@link CouponUsage}.
	 */
	public CouponUsage createAndPersistCouponUsage(final Coupon coupon, final String email, final int useCount) {
		return createAndPersistCouponUsage(coupon, email, useCount, true, false);
	}

	/**
	 * Creates and persists a coupon usage based on a {@link Coupon}.
	 * 
	 * @param coupon the {@link Coupon}.
	 * @param email the email.
	 * @param useCount use count.
	 * @param activeInCart whether its active in cart.
	 * @param suspended True if the coupon usage is suspended
	 * @return {@link CouponUsage}.
	 */
	public CouponUsage createAndPersistCouponUsage(final Coupon coupon,
			final String email, final int useCount, final boolean activeInCart,
			final boolean suspended) {
		CouponUsage couponUsage = beanFactory.getBean(ContextIdNames.COUPON_USAGE);
		couponUsage.setCoupon(coupon);
		couponUsage.setCustomerEmailAddress(email);
		couponUsage.setUseCount(useCount);
		couponUsage.setActiveInCart(activeInCart);
		couponUsage.setSuspended(suspended);
		return couponUsageService.add(couponUsage);
	}
	
	/**
	 * Find coupon for code.
	 * 
	 * @param couponCode the coupon code
	 * @return the coupon
	 */
	public Coupon findCouponByCouponCode(final String couponCode) {
		return couponService.findByCouponCode(couponCode);
	}
	
	/**
	 * Find coupon for rule code.
	 * 
	 * @param ruleCode the coupon code
	 * @return the coupon
	 */
	public Collection<Coupon> findCouponsByRuleCode(final String ruleCode) {
		return couponService.findCouponsForRuleCode(ruleCode);
	}
	
	
	
	/**
	 * Finds a coupon usage by the coupon code.
	 * 
	 * @param couponCode the coupon code
	 * @return the coupon usage
	 */
	public List<CouponUsage> findCouponUsageByCouponCode(final String couponCode) {
		return couponUsageService.findByCode(couponCode);
	}

	/**
	 * Finds a coupon config by a rule code.
	 * 
	 * @param ruleCode the rule code.
	 * @return {@link CouponConfig}.
	 */
	public CouponConfig findCouponConfigByRuleCode(final String ruleCode) {
		return couponConfigService.findByRuleCode(ruleCode);
	}

	/**
	 * Finds a rule by rule code.
	 * 
	 * @param ruleCode the rule code.
	 * @return {@link Rule}.
	 */
	public Rule findRuleByCode(final String ruleCode) {
		return ruleService.findByRuleCode(ruleCode);
	}
	
	/**
	 * Finds a coupon usage by coupon code and email address.
	 * 
	 * @param couponCode the coupon code.
	 * @param emailAddress the email address.
	 * @return {@link CouponUsage}.
	 */
	public CouponUsage findCouponUsageByCouponCodeAndEmail(final String couponCode, final String emailAddress) {
		return couponUsageService.findByCouponCodeAndEmail(couponCode, emailAddress);
	}

	/**
	 * Updates coupon usage.
	 * 
	 * @param usageToUpdate the coupon usage to update. 
	 * @return the new coupon usage.
	 */
	public CouponUsage updateCouponUsage(final CouponUsage usageToUpdate) {
		return couponUsageService.update(usageToUpdate);
	}

	/**
	 * Updates coupon.
	 * 
	 * @param coupon to update. 
	 * @return the new coupon.
	 */
	public Coupon updateCoupon(final Coupon coupon) {
		return couponService.update(coupon);
	}

	/**
	 * Start the coupon duration for any limited duration coupons for the given email address.
	 * 
	 * @param email the email address associated with coupons that should be started
	 * @param storeUidPk store uid
	 * @param shoppingDate the date to start at
	 */
	public void startCouponDurationsForEmail(final String email, final Date shoppingDate, final Long storeUidPk) {
		Collection<CouponUsage> usages = findCouponUsageByEmail(email, storeUidPk);
		for (CouponUsage usage : usages) {
			if (usage.getCoupon().getCouponConfig().isLimitedDuration()) {
				usage.setLimitedDurationStartDate(shoppingDate);
				couponUsageService.update(usage);
			}
		}
	}
	
	/** 
	 * Find the collection of all coupon usage records by email address.
	 * 
	 * @param emailAddress the email address
	 * @param storeUidPk store uid
	 * @return the collection of coupon usages
	 */
	public Collection<CouponUsage> findCouponUsageByEmail(final String emailAddress, final Long storeUidPk) {
		return couponUsageService.findEligibleUsagesByEmailAddress(emailAddress, storeUidPk);
	}
	
	/** 
	 * Find the coupon usage records by email address and coupon code.
	 * 
	 * @param emailAddress the email address
	 * @param couponCode the coupon code
	 * @return the collection of coupon usages
	 */
	public CouponUsage findCouponUsageByEmail(final String emailAddress, final String couponCode) {
		return couponUsageService.findByCouponCodeAndEmail(couponCode, emailAddress);
	}
	
	/**
	 * @param ruleCode the promotion rule code
	 * @return collection of found coupon usages for rule
	 */
	public Collection<CouponUsage> findCouponUsageByRuleCode(final String ruleCode) {
		return couponUsageService.findByRuleCode(ruleCode);
	}
	
}
