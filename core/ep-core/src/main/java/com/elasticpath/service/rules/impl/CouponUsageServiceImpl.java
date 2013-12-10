package com.elasticpath.service.rules.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.impl.CouponAssignmentActionImpl;
import com.elasticpath.domain.rules.impl.LimitedUseCouponCodeConditionImpl;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.rules.dao.CouponUsageDao;

/**
 * Implementation of {@code CouponUsageService}.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class CouponUsageServiceImpl implements CouponUsageService {
	private static final Logger LOG = Logger.getLogger(CouponUsageServiceImpl.class);
	
    private CouponUsageDao dao;
    private RuleService ruleService;
    private BeanFactory beanFactory;
    private CouponService couponService;
    private CouponConfigService couponConfigService;
    private TimeService timeService;

    @Override
	public CouponUsage add(final CouponUsage couponUsage) throws EpServiceException {
        CouponUsage newCouponUse = null;
        try {
            CouponConfig couponConfig = couponUsage.getCoupon().getCouponConfig();
            if (couponConfig != null && couponConfig.isLimitedDuration()) {
                couponUsage.setLimitedDurationStartDate(getTimeService().getCurrentTime());
            }
            newCouponUse = dao.add(couponUsage);
        } catch (Exception ex) {
            throw new EpServiceException("Failed to add " + couponUsage, ex);
        }
        return newCouponUse;
    }

    @Override
	public void delete(final CouponUsage couponUsage) throws EpServiceException {
        try {
            dao.delete(couponUsage);
        } catch (Exception ex) {
            throw new EpServiceException("Failed to delete " + couponUsage, ex);
        }
    }

    @Override
	public CouponUsage update(final CouponUsage newCouponUse) throws EpServiceException {
        CouponUsage updatedCouponUse = null;
        try {
            updatedCouponUse = dao.update(newCouponUse);
        } catch (Exception ex) {
            throw new EpServiceException("Failed to update " + newCouponUse, ex);
        }
        return updatedCouponUse;
    }

    /**
     * @param couponUsageDao
     *            the CouponUsageDao to set
     */
    public void setCouponUsageDao(final CouponUsageDao couponUsageDao) {
        this.dao = couponUsageDao;
    }

    @Override
	public List<CouponUsage> findByCode(final String couponCode) throws EpServiceException {
        return dao.findByCode(couponCode);
    }

    @Override
	public int getUseCountByCodeAndEmailAddress(final String couponCode, final String emailAddress) throws EpServiceException {
        return dao.getUseCountByCodeAndEmailAddress(couponCode, emailAddress);
    }

    @Override
	public void updateLimitedUsageCouponCurrentNumbers(final ShoppingCart shoppingCart, final Set<AppliedRule> appliedRules) {
        Collection<CouponUsage> couponUsagesToUpdate = resolveCouponUsages(shoppingCart, appliedRules);

        for (CouponUsage usage : couponUsagesToUpdate) {
            saveOrUpdateCouponUsage(usage);
        }
    }

    private Collection<CouponUsage> resolveCouponUsages(final ShoppingCart shoppingCart, final Collection<AppliedRule> appliedRules) {
        final Set<String> promotionCodes = shoppingCart.getPromotionCodes();
        Collection<CouponUsage> couponUsagesToUpdate = new ArrayList<CouponUsage>();
        for (AppliedRule appliedRule : appliedRules) {
            LOG.debug("Resolving coupon usage for rule " + appliedRule.getRuleName());
            Rule rule = getRuleService().get(appliedRule.getRuleUid());
            if (ruleContainsLimitedUseCouponCodeCondition(rule)) {

                Collection<Coupon> coupons = couponService.findCouponsForRuleCodeFromCouponCodes(rule.getCode(), promotionCodes);

                // update usage counts
                updateLimitedUsageCouponCurrentNumbers(couponUsagesToUpdate, shoppingCart, rule, appliedRule, coupons);
            }
        }
        return couponUsagesToUpdate;
    }

    @Override
	public Set<String> resolveCouponsInCart(final ShoppingCart shoppingCart) {
        Set<Long> appliedRuleIds = shoppingCart.getAppliedRules();
        Collection<AppliedRule> appliedRules = getAppliedRules(appliedRuleIds);

        Collection<CouponUsage> resolvedUsages = resolveCouponUsages(shoppingCart, appliedRules);
        Set<String> resolvedAppliedCoupons = new HashSet<String>();
        for (CouponUsage usage : resolvedUsages) {
            resolvedAppliedCoupons.add(usage.getCoupon().getCouponCode());
        }

        return resolvedAppliedCoupons;

    }

    private Collection<AppliedRule> getAppliedRules(final Set<Long> appliedRuleIds) {
        Collection<AppliedRule> appliedRules = new ArrayList<AppliedRule>();
        for (Long appliedRuleId : appliedRuleIds) {
            AppliedRule appliedRule = beanFactory.getBean(ContextIdNames.APPLIED_RULE);
            appliedRule.initialize(ruleService.load(appliedRuleId));
            appliedRules.add(appliedRule);
        }
        return appliedRules;
    }

    /**
     * Saves or update coupon usage.
     * 
     * @param usage
     *            the coupon usage.
     */
    protected void saveOrUpdateCouponUsage(final CouponUsage usage) {
        CouponUsage foundUsage = findByCodeAndType(usage.getCoupon().getCouponConfig(), usage.getCoupon().getCouponCode(),
                usage.getCustomerEmailAddress());
        if (foundUsage == null) {
            this.add(usage);
        } else {
            this.update(usage);
        }
    }

    private void updateLimitedUsageCouponCurrentNumbers(final Collection<CouponUsage> couponUsagesToUpdate, final ShoppingCart shoppingCart,
            final Rule rule, final AppliedRule appliedRule, final Collection<Coupon> coupons) {
        if (!coupons.isEmpty()) {
            int useCountRemaining = calculateUseCount(rule, shoppingCart);
            String customerEmail = shoppingCart.getShopper().getCustomer().getEmail();
            while (useCountRemaining > 0) {
                useCountRemaining = incrementUseCount(couponUsagesToUpdate, useCountRemaining, coupons, rule, appliedRule, customerEmail);
            }
        }
    }

    private boolean ruleContainsLimitedUseCouponCodeCondition(final Rule rule) {
        for (RuleCondition ruleCondition : rule.getConditions()) {
            if (ruleCondition instanceof LimitedUseCouponCodeConditionImpl) {
                return true;
            }
        }
        return false;
    }

    /**
     * Increment the use count for the coupon with the most usage yet below the
     * use limit.
     * 
     * @param couponUsagesToUpdate
     *            the coupon usages to update.
     * @param useCountRemaining
     *            the use count remaining to be allocated to a coupon.
     * @param appliedCoupons
     *            the coupons that were applied for this rule.
     * @param rule
     *            the rule that fired
     * @param appliedRule
     *            The representation of the rule that was applied from the
     *            order.
     * @param customerEmailAddress
     *            the customer email address that used the coupon
     * @return the new use count remaining after allocating some or all of
     *         useCountRemaining to a coupon usage.
     */
    protected int incrementUseCount(final Collection<CouponUsage> couponUsagesToUpdate, final int useCountRemaining,
            final Collection<Coupon> appliedCoupons, final Rule rule, final AppliedRule appliedRule, final String customerEmailAddress) {

        // Find the couponUsage we want to update
        // The candidate is the usage with the most usage while being smaller
        // than the limit.

        CandidateCouponUse candidate = new CandidateCouponUse();

        boolean foundCouponUsage = findCandidateCouponUsage(couponUsagesToUpdate, appliedCoupons, customerEmailAddress, candidate);

        CouponUsageType candidateUsageType = candidate.getCoupon().getCouponConfig().getUsageType();

        // Look for per user coupons where we found at least 1 coupon usage but
        // none that we found had any use count left.
        // This situation should not happen because of the checks made with
        // calculateAvailableDiscountQuantity but
        // this protects from an infinite loop.
        if (!CouponUsageType.LIMIT_PER_COUPON.equals(candidateUsageType) && foundCouponUsage && candidate.getUseCount() == -1) {
            throw new EpServiceException(
                "Could not find a coupon with any remaining coupon usage. Usage Type = " 
                + candidateUsageType.getName() + ", candidate = " + candidate);
        }

        int useCountAllocated = useCountRemaining;
        CouponConfig couponConfig = candidate.getCoupon().getCouponConfig();
        // Note that this code also handles where a couponUsage has not been
        // found
        // because it hasn't been created yet.
        if (candidate.getUseCount() + useCountRemaining > couponConfig.getUsageLimit()) {
            useCountAllocated = couponConfig.getUsageLimit() - candidate.getUseCount();
        }

        CouponUsage couponUsageToPersist = createOrUpdateCouponUsage(customerEmailAddress, candidate.getCoupon(), candidate.getCouponUsage(),
        		candidateUsageType, useCountAllocated);

        appliedRule.addAppliedCoupon(candidate.getCoupon().getCouponCode(), useCountAllocated);

        couponUsagesToUpdate.add(couponUsageToPersist);

        return useCountRemaining - useCountAllocated;
    }

	/**
	 * Find candidate coupon usage by looking through the applied coupons for the usage with
	 * the greatest use count that applies to the given email address.
	 *
	 * @param couponUsagesToUpdate the coupon usages to update
	 * @param appliedCoupons the applied coupons
	 * @param customerEmailAddress the customer email address
	 * @param candidate the coupon usage candidate
	 * @return true, if successful
	 */
	protected boolean findCandidateCouponUsage(final Collection<CouponUsage> couponUsagesToUpdate, final Collection<Coupon> appliedCoupons,
			final String customerEmailAddress, final CandidateCouponUse candidate) {
		
		CouponUsage couponUsage  = null;
		boolean foundCouponUsage = false;
        List<CouponUsage> candidateCouponUsages = new ArrayList<CouponUsage>();
        
        for (Coupon appliedCoupon : appliedCoupons) {
        	// Ensure that the first coupon is used as a default if a coupon usage isn't found
            if (candidate.getCoupon() == null) { 
            	candidate.setCoupon(appliedCoupon);
            }

            CouponConfig couponConfig = appliedCoupon.getCouponConfig();
            couponUsage = findByCodeAndType(appliedCoupon.getCouponConfig(), appliedCoupon.getCouponCode(), customerEmailAddress);
            if (couponUsage != null) {
                foundCouponUsage = true;
                candidateCouponUsages.add(couponUsage);
                
                if (couponUsage.getUseCount() > candidate.getUseCount() && couponUsage.getUseCount() < couponConfig.getUsageLimit()
                        && !couponUsagesToUpdate.contains(couponUsage)) {

                    candidate.setUseCount(couponUsage.getUseCount());
                    candidate.setCoupon(appliedCoupon);
                    candidate.setCouponUsage(couponUsage);
                }
            }
        }

        // all of the candidate coupon usages are out of limit, applies to the
        // one that with the smallest count.
        if (candidate.getCouponUsage() == null && !candidateCouponUsages.isEmpty()) {
            Collections.sort(candidateCouponUsages, new Comparator<CouponUsage>() {
                @Override
				public int compare(final CouponUsage usage1, final CouponUsage usage2) {
                    return ((Integer) usage1.getUseCount()).compareTo(usage2.getUseCount());
                }
            });

            candidate.setCouponUsage(candidateCouponUsages.get(0));
            candidate.setCoupon(candidate.getCouponUsage().getCoupon());
        }
		return foundCouponUsage;
	}

    private CouponUsage createOrUpdateCouponUsage(final String customerEmailAddress, final Coupon candidateCoupon,
            final CouponUsage candidateCouponUsage, final CouponUsageType candidateUsageType, final int useCountAllocated) {
        CouponUsage toReturn = candidateCouponUsage;

        if (toReturn == null) {
            toReturn = beanFactory.getBean(ContextIdNames.COUPON_USAGE);
            toReturn.setCoupon(candidateCoupon);
            toReturn.setUseCount(useCountAllocated);
            if (CouponUsageType.LIMIT_PER_ANY_USER.equals(candidateUsageType) 
            		|| CouponUsageType.LIMIT_PER_SPECIFIED_USER.equals(candidateUsageType)) {
                toReturn.setCustomerEmailAddress(customerEmailAddress);
            }
        } else {
            toReturn.setUseCount(toReturn.getUseCount() + useCountAllocated);
        }

        return toReturn;
    }

    /**
     * Calculate the use count of the applied coupon. For rules that give free
     * items, this will examine the shopping cart and calculate how many coupon
     * uses were required to get those items free.
     * 
     * @param rule
     *            the rule that is associated with the coupon
     * @param shoppingCart
     *            the shopping cart
     * @return the number of coupon uses.
     */
    protected int calculateUseCount(final Rule rule, final ShoppingCart shoppingCart) {
        int maximumCouponUsesRequired = 0;
        for (RuleAction action : rule.getActions()) {

            // Discount is polymorphic, descriminated by action type: using
            // strategy pattern
            DiscountRecord discountRecord = shoppingCart.getDiscountRecordForRuleAndAction(rule, action);

            // The default coupon usage is 1 - unless we have a discount record
            // which might indicate more.
            int couponUsesRequiredForAction = 1;
            if (discountRecord != null) {
                couponUsesRequiredForAction = discountRecord.getCouponUsesRequired(action, shoppingCart);
            }

            if (couponUsesRequiredForAction > maximumCouponUsesRequired) {
                maximumCouponUsesRequired = couponUsesRequiredForAction;
            }
        }
        return maximumCouponUsesRequired;
    }

    private CouponUsage getCouponUsageForNonUserSpecificCoupon(final String couponCode) {
        CouponUsage couponUsage = null;
        List<CouponUsage> couponUsages = findByCode(couponCode);
        if (!couponUsages.isEmpty()) {
            couponUsage = couponUsages.get(0);
        }
        return couponUsage;
    }

    @Override
	public Collection<CouponUsage> findByRuleCode(final String ruleCode) {
        return dao.findByRuleCode(ruleCode);
    }

    /**
     * @param ruleService
     *            the RuleService to set.
     */
    public void setRuleService(final RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @Override
	public boolean isValidPromoCode(final String customerEmailAddress, final Coupon coupon, final CouponConfig couponConfig,
            final CouponUsage couponUsage) {
        return checkUsage(couponConfig, couponUsage, customerEmailAddress) && checkDate(couponConfig, couponUsage)
                && checkSuspension(coupon, couponUsage);
    }

    /**
     * Check suspended status of coupon usage. If coupon is not on a per user
     * basis, find the coupon and check its status.
     * 
     * @param coupon
     *            coupon
     * @param couponUsage
     *            coupon usage. may be null if not a
     *            {@link CouponUsageType.LIMIT_PER_SPECIFIED_USER}
     * @return true if not suspended
     */
    protected boolean checkSuspension(final Coupon coupon, final CouponUsage couponUsage) {
        if (CouponUsageType.LIMIT_PER_SPECIFIED_USER.equals(coupon.getCouponConfig().getUsageType())) {
            return !couponUsage.isSuspended();
        }
        return !coupon.isSuspended();
    }

    /**
     * find the usage - taking into consideration coupon type.
     * 
     * @param couponConfig
     *            CouponConfig.
     * @param couponCode
     *            Code of coupon under test.
     * @param customerEmailAddress
     *            Email of user.
     * @return CouponUsage number.
     */
    @Override
	public CouponUsage findByCodeAndType(final CouponConfig couponConfig, final String couponCode, final String customerEmailAddress) {
        CouponUsage result = null;
        if (CouponUsageType.LIMIT_PER_COUPON.equals(couponConfig.getUsageType())) {
            result = getCouponUsageForNonUserSpecificCoupon(couponCode);
        } else if (!StringUtils.isEmpty(customerEmailAddress)) {
            // cannot lookup without an email
            result = dao.findByCouponCodeAndEmail(couponCode, customerEmailAddress);
        }
        return result;
    }

    /**
     * Check the Date is valid if a limited duration coupon and for appropriate
     * coupon usage.
     * 
     * @param couponConfig
     *            CouponConfig.
     * @param couponUsage
     *            CouponUsage.
     * @return true if the date is valid for circumstances.
     */
    protected boolean checkDate(final CouponConfig couponConfig, final CouponUsage couponUsage) {
        if (couponConfig.isLimitedDuration() && CouponUsageType.LIMIT_PER_SPECIFIED_USER.equals(couponConfig.getUsageType())
                && getTimeService().getCurrentTime().after(couponUsage.getLimitedDurationEndDate())) {
            return false;
        }
        return true;
    }

    /**
     * Check usage is valid for usage type.
     * 
     * @param couponConfig
     *            CouponConfig.
     * @param couponUsage
     *            CouponUsage.
     * @param customerEmailAddress
     *            the customerEmail
     * @return true if the usage is valid for coupon type.
     */
    protected boolean checkUsage(final CouponConfig couponConfig, final CouponUsage couponUsage, final String customerEmailAddress) {
        if (CouponUsageType.LIMIT_PER_ANY_USER.equals(couponConfig.getUsageType()) && (!StringUtils.isEmpty(customerEmailAddress))
                && (couponUsage == null || (couponUsage.getUseCount() < couponConfig.getUsageLimit()))) {
            // we don't care about couponUsage other than to run the query
            return true;
        } else if (CouponUsageType.LIMIT_PER_COUPON.equals(couponConfig.getUsageType())
                && ((couponUsage == null) || (couponUsage.getUseCount() < couponConfig.getUsageLimit()))) {
            // we don't care about customerEmailAddress being null or not
            // we don't care about couponUsage other than to run the query
            return true;
        } else if (CouponUsageType.LIMIT_PER_SPECIFIED_USER.equals(couponConfig.getUsageType()) && (!StringUtils.isEmpty(customerEmailAddress))
                && (couponUsage != null) && (couponUsage.getUseCount() < couponConfig.getUsageLimit())) {
            return true;
        }
        return false;
    }

    @Override
	public CouponUsage findByCouponCodeAndEmail(final String couponCode, final String customerEmailAddress) {
        return dao.findByCouponCodeAndEmail(couponCode, customerEmailAddress);
    }

    /**
     * 
     * @return The rule service.
     */
    protected RuleService getRuleService() {
        return ruleService;
    }

    /**
     * 
     * @param beanFactory
     *            The bean factory to set.
     */
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 
     * @param couponService
     *            The coupon service to st.
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
     * @param couponConfigService
     *            The coupon config service to set.
     */
    public void setCouponConfigService(final CouponConfigService couponConfigService) {
        this.couponConfigService = couponConfigService;
    }

    /**
     * @return the couponConfigService.
     */
    public CouponConfigService getCouponConfigService() {
        return couponConfigService;
    }

    @Override
	public Collection<CouponUsage> findByRuleCodeAndEmail(final String ruleCode, final String customerEmailAddress) {
        return dao.findByRuleCodeAndEmail(ruleCode, customerEmailAddress);
    }

    @Override
	public void processCouponCustomerAssignments(final Set<Long> appliedRuleUids, final String customerEmailAddress) {
        for (Long appliedRuleUid : appliedRuleUids) {
            Rule rule = getRuleService().get(appliedRuleUid);
            for (RuleAction action : rule.getActions()) {
                if (action instanceof CouponAssignmentActionImpl) {
                    CouponAssignmentActionImpl couponAssignmentAction = (CouponAssignmentActionImpl) action;
                    // note that if the customer already has a coupon for this
                    // rule then
                    // this query will return empty.
                    String targetRuleCode = couponAssignmentAction.getParamValue(RuleParameter.RULE_CODE_KEY);
                    CouponConfig couponConfig = couponConfigService.findByRuleCode(targetRuleCode);

                    // We don't care if this email is already associated with
                    // such a coupon.. rather just generate a unique code
                    // and create a new one.
                    Coupon unpopulatedCoupon = beanFactory.getBean(ContextIdNames.COUPON);
                    unpopulatedCoupon.setCouponConfig(couponConfig);
                    String couponCodePrefix = couponAssignmentAction.getParamValue(RuleParameter.COUPON_PREFIX);

                    Coupon coupon = couponService.addAndGenerateCode(unpopulatedCoupon, couponCodePrefix);

                    // add a coupon usage for this customer for this rule.
                    CouponUsage newCouponUsage = beanFactory.getBean(ContextIdNames.COUPON_USAGE);
                    newCouponUsage.setCustomerEmailAddress(customerEmailAddress);
                    newCouponUsage.setCoupon(coupon);
                    // Create the usage with a use count of zero so that the
                    // customer
                    // must enter the coupon to use it.
                    newCouponUsage.setUseCount(0);
                    newCouponUsage.setActiveInCart(true);
                    newCouponUsage.setSuspended(false);
                    this.add(newCouponUsage);
                }
            }
        }
    }

    @Override
	public Collection<CouponUsage> findEligibleUsagesByEmailAddress(final String emailAddress, final Long storeUidPk) {
        return this.findAllUsagesByEmailAddress(emailAddress, getTimeService().getCurrentTime(), storeUidPk);
    }

    @Override
	public Collection<CouponUsage> findAllUsagesByEmailAddress(final String emailAddress, final Date expirationDate, final Long storeUidPk) {
        Collection<CouponUsage> collection = dao.findEligibleUsagesByEmailAddressInStore(emailAddress, expirationDate, storeUidPk);
        Collection<CouponUsage> result = new ArrayList<CouponUsage>();
        for (CouponUsage couponUsage : collection) {
            final Rule rule = getRuleService().findByPromoCode(couponUsage.getCoupon().getCouponCode());
            if (rule.isEnabled() && rule.isWithinDateRange() && isLupNotOverLimit(rule)) {
                result.add(couponUsage);
            }
        }
        return result;
    }

    private boolean isLupNotOverLimit(final Rule rule) {
        Long allowedLimit = null;
        for (RuleElement ruleElement : rule.getRuleElements()) {
            String value = ruleElement.getParamValue(RuleParameter.ALLOWED_LIMIT);
            if (StringUtils.isNotBlank(value) && NumberUtils.isNumber(value)) {
                allowedLimit = NumberUtils.toLong(value);
                break;
            }
        }
        if (allowedLimit == null) { // not a limited usage promotion
            return true;
        }
        return (allowedLimit == 0) || (allowedLimit > rule.getCurrentLupNumber());
    }

    @Override
	public void deleteAllUsagesByCouponConfigGuid(final String couponConfigGuid) {
        dao.deleteAllUsagesByCouponConfigGuid(couponConfigGuid);
    }

    /**
     * Find the collection of Coupons associated with the given rule code. The
     * database query is sorted by {@code orderingFields} and only the records
     * starting at {@code startIndex} up to {@code startIndex} + pageSize are
     * returned.
     * 
     * @param couponConfigId
     *            the id of the coupon config whose coupons should be found
     * @param searchCriteria
     *            the criteria to search by.
     * @param startIndex
     *            The starting index
     * @param pageSize
     *            The size of the page
     * @param orderingFields
     *            The fields to order by
     * @return the collection of Coupons
     */
    @Override
	public Collection<CouponUsage> findCouponUsagesForCouponConfigId(final long couponConfigId, final SearchCriterion[] searchCriteria,
            final int startIndex, final int pageSize, final DirectedSortingField[] orderingFields) {
        return dao.findByCouponConfigId(couponConfigId, searchCriteria, startIndex, pageSize, orderingFields);
    }

    /**
     * Returns the number of coupons which match the search criteria (e.g.
     * {@code ruleCode}).
     * 
     * @param couponConfigId
     *            The rule code to search for.
     * @param searchCriteria
     *            the criteria to search by.
     * @return The count.
     */
    @Override
	public long getCountForSearchCriteria(final long couponConfigId, final SearchCriterion[] searchCriteria) {
        return dao.getCountForSearchCriteria(couponConfigId, searchCriteria);
    }

    /**
     * 
     * @param timeService
     *            the timeService to set
     */
    public void setTimeService(final TimeService timeService) {
        this.timeService = timeService;
    }

    /**
     * 
     * @return the timeService
     */
    public TimeService getTimeService() {
        return timeService;
    }

    @Override
	public List<CouponUsage> findByUids(final Collection<Long> uids) {
        return dao.findByUids(uids);
    }

    /** Container class for candidate coupon use. */
    private static class CandidateCouponUse {
        private int useCount = -1;
        private Coupon coupon;
        private CouponUsage couponUsage;
		public int getUseCount() {
			return useCount;
		}
		public void setUseCount(final int useCount) {
			this.useCount = useCount;
		}
		public Coupon getCoupon() {
			return coupon;
		}
		public void setCoupon(final Coupon coupon) {
			this.coupon = coupon;
		}
		public CouponUsage getCouponUsage() {
			return couponUsage;
		}
		public void setCouponUsage(final CouponUsage couponUsage) {
			this.couponUsage = couponUsage;
		}
		@Override
		public String toString() {
			return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("coupon", coupon.getCouponCode())
			.append("useCount", useCount)
			.append("email", couponUsage.getCustomerEmailAddress())
			.toString();
		}
    }
}
