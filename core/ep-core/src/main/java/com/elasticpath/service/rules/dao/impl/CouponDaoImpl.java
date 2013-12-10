package com.elasticpath.service.rules.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.impl.AbstractDaoImpl;
import com.elasticpath.service.DirectedSortingFieldException;
import com.elasticpath.service.rules.dao.CouponDao;

/**
 * Implementation of data access object for {@code Coupon}.
 */
public class CouponDaoImpl extends AbstractDaoImpl implements CouponDao {

	private static final String LIST_PARAM = "list";
	private PersistenceEngine persistenceEngine;
	
	@Override
	public Coupon get(final long couponUid) throws EpPersistenceException {
		return getPersistentBeanFinder().get(ContextIdNames.COUPON, couponUid);
	}
	
	
	@Override
	public void delete(final Coupon coupon) {
		getPersistenceEngine().delete(coupon);
		getPersistenceEngine().flush();
	}
	
	@Override
	public Coupon add(final Coupon coupon) throws EpPersistenceException {
		Coupon updatedCouponCode = null;
		try {
			updatedCouponCode = getPersistenceEngine().saveOrUpdate(coupon);
		} catch (Exception ex) {
			throw new EpPersistenceException("Exception on adding Coupon.", ex);
		}
		return updatedCouponCode;
	}
	
	/**
	 * Saves an updated Coupon. Persistence is not checked.
	 * 
	 * @param updatedCouponCode the Coupon that will replace the existing one
	 * @return the updated Coupon
	 * @throws EpPersistenceException if object is not persistent
	 */
	@Override
	public Coupon update(final Coupon updatedCouponCode) {
		if (!updatedCouponCode.isPersisted()) {
			throw new EpPersistenceException("Object is not persistent");
		}
		return getPersistenceEngine().saveOrUpdate(updatedCouponCode);
	}
	
	/**
	 * Sets the persistence engine to use.
	 *  
	 * @param persistenceEngine The persistence engine.
	 */
	@Override
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	/**
	 * Gets the persistence engine.
	 * 
	 * @return The persistence engine.
	 */
	@Override
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	@Override
	public Collection<Coupon> findByCouponCode(final String couponCode) {
		return getPersistenceEngine().retrieveByNamedQuery("COUPON_FIND_BY_COUPON_CODE", couponCode);
	}

	@Override
	public Collection<Coupon> findByRuleCode(final String ruleCode) {
		return getPersistenceEngine().retrieveByNamedQuery("COUPON_FIND_BY_RULE_CODE", ruleCode);
	}
	
	
	@Override
	public Collection<Coupon> findByCouponConfigId(final long configId, final SearchCriterion[] searchCriteria, final int startIndex,
			final int pageSize, final DirectedSortingField[] orderingFields) {
		if (ArrayUtils.isEmpty(orderingFields)) {			
			throw new DirectedSortingFieldException("Null-value/zero argument", "groupId", orderingFields, Long.toString(configId));
		}
		if (startIndex < 0 || pageSize < 0) {
			throw new IllegalArgumentException(
					String.format("Negative-value argument: startIndex=%d, maxResults=%d", startIndex, pageSize));
		}
		
		StringBuffer query = new StringBuffer("SELECT c FROM CouponImpl c WHERE c.couponConfig.uidPk =?1");
		List<Object> parameters = new ArrayList<Object>();		
		parameters.add(configId);
		
		addSearchClauses(searchCriteria, query, parameters);
		
		query.append(" ORDER BY ");
		for (DirectedSortingField directedSortingField : orderingFields) {
			if ("couponCode".equals(directedSortingField.getSortingField().getName())) {
				query.append("c.couponCode ");
			} else if ("status".equals(directedSortingField.getSortingField().getName())) {
				query.append("c.suspended ");
			}
			query.append(directedSortingField.getSortingDirection());
		}
		
		return persistenceEngine
				.retrieve(
						query.toString(),
						parameters.toArray(), startIndex, pageSize);
	}


	private void addSearchClauses(final SearchCriterion[] searchCriteria,
			final StringBuffer query, final List<Object> parameters) {
		int parameterIndex = 2;
		for (SearchCriterion criterion : searchCriteria) {
			if (criterion.getFieldName().equals("couponCode")) {
				query.append(" AND c.couponCode LIKE ?");
				query.append(parameterIndex++);
				parameters.add("%" + criterion.getFieldValue() + "%");
			} else if (criterion.getFieldName().equals("status")) {
				boolean searchValue = false;
				boolean isValidStatusValue = false;
				if ("suspended".equals(criterion.getFieldValue())) {
					searchValue = true;
					isValidStatusValue = true;
				} else if ("in_use".equals(criterion.getFieldValue())) {
					searchValue = false;
					isValidStatusValue = true;
				}
				if (isValidStatusValue) {
					query.append(" AND c.suspended = ?");
					query.append(parameterIndex++);
					parameters.add(searchValue);
				}				
			}
		} 
	}

	@Override
	public long getCountForSearchCriteria(final long couponConfigId, final SearchCriterion[] searchCriteria) {
		
		StringBuffer query = new StringBuffer("SELECT COUNT(c) FROM CouponImpl c WHERE c.couponConfig.uidPk =?1");
		List<Object> parameters = new ArrayList<Object>();		
		parameters.add(couponConfigId);
		
		addSearchClauses(searchCriteria, query, parameters);
		
		List<Long> result = persistenceEngine.retrieve(
				query.toString(),
				parameters.toArray());
		return result.iterator().next();
	}
	
	@Override
	public Collection<Coupon> findUnusedCouponsForRuleAndUser(
			final String ruleCode, final String customerEmailAddress) {
		return getPersistenceEngine().retrieveByNamedQuery("COUPON_FIND_UNUSED_COUPON_FOR_RULE_CODE_AND_EMAIL", ruleCode, customerEmailAddress);
	}

	@Override
	public Collection<Coupon> findCouponsForRuleCodeFromCouponCodes(final String ruleCode, final Set<String> couponCodes) {
		return getPersistenceEngine().retrieveByNamedQueryWithList("COUPON_FIND_BY_RULE_CODE_FROM_COUPON_CODES", LIST_PARAM, couponCodes, ruleCode);
	}
	
	@Override
	public Collection<Coupon> findCouponsForRuleFromCouponCodes(final long ruleId, final Set<String> couponCodes) {
		return getPersistenceEngine().retrieveByNamedQueryWithList("COUPON_FIND_BY_RULE_UID_FROM_COUPON_CODES", LIST_PARAM, couponCodes, ruleId);
	}

	@Override
	public boolean doesCouponCodeEmailPairExistForThisRuleCode(final String couponCode, final String email, final String exceptionRuleCode) {
		List<String> couponCodes = getPersistenceEngine().retrieveByNamedQuery("COUPON_USAGE_FIND_FOR_COUPON_EMAIL_RULE_CODE",
				couponCode,
				email,
				exceptionRuleCode);
		if (couponCodes == null || couponCodes.isEmpty()) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean doesCouponCodeOnlyExistForThisRuleCode(final String couponCode, final String exceptionRuleCode) {
		List<String> couponCodes = getPersistenceEngine().retrieveByNamedQuery("COUPON_CODES_BY_COUPON_CODE_NOT_RULE_CODE",
				couponCode,
				exceptionRuleCode);
		if (couponCodes == null || couponCodes.isEmpty()) {
			return true;
		}
		return false;	
	}
	
	@Override
	public void deleteCouponsByCouponConfigGuid(final String couponConfigGuid) {
		getPersistenceEngine().executeNamedQuery("COUPON_DELETE_BY_COUPON_CONFIG_GUID", couponConfigGuid);
	}

	@Override
	public Collection<String> findExistingCouponCodes(final Collection<String> codes) {
		return getPersistenceEngine().retrieveByNamedQueryWithList("COUPON_CODES_FIND_FROM_LIST", LIST_PARAM, codes);
	}

	@Override
	public Collection<String> findExistingCouponCodes(final Collection<String> codes, final String exceptionRuleCode) {
		return getPersistenceEngine().retrieveByNamedQueryWithList("COUPON_CODES_NOT_RULE_CODE_FIND_FROM_LIST", "list", codes, exceptionRuleCode);
	}
	
	@Override
	public List<String> findCouponCodesByRuleCode(final String ruleCode) {
		return getPersistenceEngine().retrieveByNamedQuery("COUPON_CODES_BY_RULE_CODE", ruleCode);
	}

	@Override
	public List<Coupon> findByUids(final Collection<Long> uids) {
		return getPersistenceEngine().retrieveByNamedQueryWithList("COUPONS_FIND_BY_UIDS", LIST_PARAM, uids);
	}


	@Override
	public Map<String, Coupon> findCouponsForCodes(final Collection<String> codes) {
		List<Object[]> results = getPersistenceEngine().retrieveByNamedQueryWithList("COUPON_MAP_BY_CODE", LIST_PARAM, codes);
		final Map<String, Coupon> couponMap = new HashMap<String, Coupon>();
		for (Object[] result : results) {
			couponMap.put((String) result[0], (Coupon) result[1]);
		}
		return couponMap;
	}

	@Override
	public Coupon getLastestCoupon(final String couponCodePrefix) {
		List<Object> parameters = new ArrayList<Object>();		
		parameters.add(couponCodePrefix + "%");
		List<Coupon> coupons = persistenceEngine.retrieveByNamedQuery("COUPON_LIKE_CODE_PREFIX_DESC", parameters.toArray(), 0, 1);
		if (coupons == null || coupons.isEmpty()) {
			return null;
		}
		return coupons.get(0);
	}

}
