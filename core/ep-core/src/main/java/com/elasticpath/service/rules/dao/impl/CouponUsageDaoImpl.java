package com.elasticpath.service.rules.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.impl.AbstractDaoImpl;
import com.elasticpath.service.DirectedSortingFieldException;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.rules.dao.CouponUsageDao;

/**
 * Implementation of data access object for {@code CouponUsage}.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class CouponUsageDaoImpl extends AbstractDaoImpl implements CouponUsageDao {

	private PersistenceEngine persistenceEngine;
	private TimeService timeService;
	
	private static final String PER_COUPON_USAGE = "perCouponUsage";
	
	@Override
	public CouponUsage get(final long couponUsageUid) throws EpPersistenceException {
		return getPersistentBeanFinder().get(ContextIdNames.COUPON_USAGE, couponUsageUid);
	}
	
	@Override
	public void delete(final CouponUsage couponUsage) {
		getPersistenceEngine().delete(couponUsage);
		getPersistenceEngine().flush();
	}

	@Override
	public CouponUsage add(final CouponUsage couponUsage) throws EpPersistenceException {
		CouponUsage updatedCouponUse = null;
		try {
			if (StringUtils.isEmpty(couponUsage.getCustomerEmailAddress())) {
				couponUsage.setCustomerEmailAddress(PER_COUPON_USAGE);
			}
			updatedCouponUse = getPersistenceEngine().saveOrUpdate(couponUsage);
		} catch (Exception ex) {
			throw new EpPersistenceException("Exception on adding CouponUsage.", ex);
		}
		return updatedCouponUse;
	}

	/**
	 * Saves an updated CouponUsage. Persistence is not checked.
	 * 
	 * @param updatedCouponUse the CouponUsage that will replace the existing one
	 * @return the updated CouponUsage
	 * @throws EpPersistenceException if object is not persistent
	 */
	public CouponUsage update(final CouponUsage updatedCouponUse) {
		if (!updatedCouponUse.isPersisted()) {
			throw new EpPersistenceException("Object is not persistent");
		}
		if (StringUtils.isEmpty(updatedCouponUse.getCustomerEmailAddress())) {
			updatedCouponUse.setCustomerEmailAddress(PER_COUPON_USAGE);
		}
		return getPersistenceEngine().saveOrUpdate(updatedCouponUse);
	}

	/**
	 * Sets the persistence engine to use.
	 * 
	 * @param persistenceEngine The persistence engine.
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	/**
	 * Gets the persistence engine.
	 * 
	 * @return The persistence engine.
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	@Override
	public List<CouponUsage> findByCode(final String couponCode) throws EpServiceException {
		sanityCheck();
		if (couponCode == null) {
			throw new EpServiceException("Cannot retrieve null coupon code.");
		}

		return getPersistenceEngine().retrieveByNamedQuery("COUPON_USAGE_FIND_BY_COUPON_CODE", couponCode);
	}

	/**
	 * Sanity check of this service instance.
	 * 
	 * @throws EpServiceException - if something goes wrong.
	 */
	public void sanityCheck() throws EpServiceException {
		if (getPersistenceEngine() == null) {
			throw new EpServiceException("The persistence engine is not correctly initialized.");
		}
	}

	@Override
	public int getUseCountByCodeAndEmailAddress(final String couponCode, final String emailAddress) {
		sanityCheck();
		if (couponCode == null) {
			throw new EpServiceException("Cannot retrieve null coupon code.");
		}

		String nonEmptyEmailAddress = emailAddress;
		if (StringUtils.isEmpty(nonEmptyEmailAddress)) {
			nonEmptyEmailAddress = PER_COUPON_USAGE;
		}
		List<Long> results = getPersistenceEngine().retrieveByNamedQuery("COUPON_USECOUNT_FIND_BY_COUPON_CODE_AND_EMAIL_ADDRESS",
				couponCode,
				nonEmptyEmailAddress);
		int useCount = 0;
		if (!results.isEmpty()) {
			if (results.get(0) == null) {
				useCount = 0;
			} else {
				useCount = results.get(0).intValue();
			}
		}

		return useCount;
	}
	
	@Override
	public Collection<CouponUsage> findByRuleCode(final String ruleCode) {
		if (ruleCode == null) {
			throw new EpServiceException("Cannot retrieve null coupon code.");
		}

		List<CouponUsage> results = getPersistenceEngine().retrieveByNamedQuery("COUPON_USAGE_FIND_BY_RULE_CODE", ruleCode);
		if (results.isEmpty()) {
			return Collections.emptyList();
		}
		return results;
	}

	@Override
	public CouponUsage findByCouponCodeAndEmail(final String couponCode,
			final String customerEmailAddress) {
		if (couponCode == null) {
			throw new EpServiceException("Cannot retrieve null coupon code.");
		}
		
		if (StringUtils.isEmpty(customerEmailAddress)) {
			throw new EpServiceException("Cannot retrieve null/blank customer email address.");
		}

		List<CouponUsage> results = getPersistenceEngine().retrieveByNamedQuery("COUPON_USAGE_FIND_BY_COUPON_CODE_AND_EMAIL_ADDRESS",
				couponCode,
				customerEmailAddress);
		if (!results.isEmpty()) {
			return results.get(0);
		}
		return null;

	}
	
	@Override
	public Collection<CouponUsage> findByRuleCodeAndEmail(final String ruleCode, final String customerEmailAddress) {
		String nonEmptyEmailAddress = customerEmailAddress;
		if (StringUtils.isEmpty(nonEmptyEmailAddress)) {
			nonEmptyEmailAddress = PER_COUPON_USAGE;
		}
		return getPersistenceEngine().retrieveByNamedQuery("COUPON_USAGE_FIND_BY_RULE_CODE_AND_EMAIL_ADDRESS", ruleCode, nonEmptyEmailAddress);
	}


	
	@Override
	public Collection<CouponUsage> findEligibleUsagesByEmailAddressInStore(final String emailAddress, 
			final Date expirationDate, final Long storeUidPk) {
		String nonNullEmailAddress = emailAddress;
		if (StringUtils.isEmpty(nonNullEmailAddress)) {
			nonNullEmailAddress = PER_COUPON_USAGE;
		}
		return getPersistenceEngine().retrieveByNamedQuery("COUPON_USAGE_FIND_BY_EMAIL_ELIGIBLE_IN_STORE", emailAddress, expirationDate, storeUidPk);
	}	

	@Override
	public void deleteAllUsagesByCouponConfigGuid(final String couponConfigGuid) {
		getPersistenceEngine().executeNamedQuery("COUPON_USAGE_DELETE_BY_COUPON_CONFIG_GUID", couponConfigGuid);
	}
	
	@Override
	public Collection<CouponUsage> findByCouponConfigId(final long couponConfigId, final SearchCriterion[] searchCriteria, final int startIndex,
			final int pageSize, final DirectedSortingField[] orderingFields) {
		if (ArrayUtils.isEmpty(orderingFields)) {			
			throw new DirectedSortingFieldException("Null-value/zero argument", "groupId", orderingFields, Long.toString(couponConfigId));
		}
		if (startIndex < 0 || pageSize < 0) {
			throw new IllegalArgumentException(
					String.format("Negative-value argument: startIndex=%d, maxResults=%d", startIndex, pageSize));
		}
		
		StringBuffer query = new StringBuffer("SELECT cu FROM CouponUsageImpl cu WHERE cu.coupon.couponConfig.uidPk =?1");
		List<Object> parameters = new ArrayList<Object>();		
		parameters.add(couponConfigId);
		
		addSearchClauses(searchCriteria, query, parameters);
		
		query.append(" ORDER BY ");
		for (DirectedSortingField directedSortingField : orderingFields) {
			if ("couponCode".equals(directedSortingField.getSortingField().getName())) {
				query.append("cu.coupon.couponCode ");
			} else if ("status".equals(directedSortingField.getSortingField().getName())) {
				query.append("cu.suspendedInternal ");
			} else if ("emailAddress".equals(directedSortingField.getSortingField().getName())) {
				query.append("cu.customerEmailAddress ");
			}
			query.append(directedSortingField.getSortingDirection());
		}
		
		return persistenceEngine.retrieve(query.toString(), parameters.toArray(), startIndex, pageSize);
	}

	private void addSearchClauses(final SearchCriterion[] searchCriteria,
			final StringBuffer query, final List<Object> parameters) {
		int parameterIndex = 2;
		for (SearchCriterion criterion : searchCriteria) {
			if (criterion.getFieldName().equals("couponCode")) {
				query.append(" AND cu.coupon.couponCode LIKE ?");
				query.append(parameterIndex++);
				parameters.add("%" + criterion.getFieldValue() + "%");
			} else if (criterion.getFieldName().equals("emailAddress")) {
				query.append(" AND cu.customerEmailAddress LIKE ?");
				query.append(parameterIndex++);
				parameters.add("%" + criterion.getFieldValue() + "%");
			} else if (criterion.getFieldName().equals("status")) {
				// A coupon usage is suspended if either the coupon or the coupon usage is suspended.
				// Therefore a coupon can only be in use if both coupon and coupon usage are not suspended.
				if ("suspended".equals(criterion.getFieldValue())) {
					query.append(" AND (cu.suspendedInternal = TRUE OR cu.coupon.suspended = TRUE)");
				} else if ("in_use".equals(criterion.getFieldValue())) {
					query.append(" AND (cu.suspendedInternal = FALSE AND cu.coupon.suspended = FALSE)");
				}
			
			}
		}
	}

	@Override
	public long getCountForSearchCriteria(final long couponConfigId, final SearchCriterion[] searchCriteria) {

		StringBuffer query = new StringBuffer("SELECT COUNT(cu) FROM CouponUsageImpl cu WHERE cu.coupon.couponConfig.uidPk =?1");
		List<Object> parameters = new ArrayList<Object>();		
		parameters.add(couponConfigId);
		
		addSearchClauses(searchCriteria, query, parameters);
		
		List<Long> result = persistenceEngine.retrieve(
				query.toString(),
				parameters.toArray());
		return result.iterator().next();
	}
	
	/**
	 * @param timeService the timeService to set
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * @return the timeService
	 */
	public TimeService getTimeService() {
		return timeService;
	}

	@Override
	public List<CouponUsage> findByUids(final Collection<Long> uids) {
		return getPersistenceEngine().retrieveByNamedQueryWithList("COUPON_USAGE_FIND_BY_UIDS", "list", uids);
	}
}
