/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.service.customer.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.customer.AnonymousCustomerCleanupService;
import com.elasticpath.service.shopper.ShopperCleanupService;

/**
 * Service to clean up anonymous customer usage.
 */
public class AnonymousCustomerCleanupServiceImpl implements AnonymousCustomerCleanupService {

	private static final Logger LOG = Logger.getLogger(AnonymousCustomerCleanupServiceImpl.class);

	private PersistenceEngine persistenceEngine;

	private ShopperCleanupService shopperCleanupService;

	/**
	 * Delete anonymous customers.
	 *
	 * @param removalDate the removal date
	 * @param maxResults the max number of results to return
	 * @return the number of deleted customers
	 * @inheritDoc
	 *
	 * In addition to the removal date placed upon the customer creation date, an anonymous customer is a candidate for removal
	 * only if there has been no order placed for the anonymous customer.
	 */
	@Override
	public int deleteAnonymousCustomers(final Date removalDate, final int maxResults) {
		if (removalDate == null) {
			throw new EpServiceException("removalDate must be supplied.");
		}

		int result = 0;
		List<Long> anonymousCustomerUids = findUidsOfOldAnonymousCustomersWithoutOrders(removalDate, maxResults);
		if (CollectionUtils.isNotEmpty(anonymousCustomerUids)) {
			LOG.debug(String.format("The following anonymous customers are removal candidates: %s", anonymousCustomerUids));
			result = deleteAnonymousCustomersByUidWithAssociatedEntities(anonymousCustomerUids);
		}

		return result;
	}

	private List<Long> findUidsOfOldAnonymousCustomersWithoutOrders(final Date removalDate, final int maxResults) {
		return getPersistenceEngine().<Long> retrieveByNamedQuery("FIND_UIDS_OF_ANONYMOUS_CUSTOMERS_WITHOUT_ORDERS_AND_LAST_MODIFIED_BEFORE_DATE",
				new Object[] { removalDate },
				0,
				maxResults);
	}

	private int deleteAnonymousCustomersByUidWithAssociatedEntities(final List<Long> customerUids) {
		// can't assume that other cleanup jobs will have performed removal of foreign key references for us
		// so we do it here explicitly so that we ensure referential integrity
		List<Long> shopperUidsToDelete = getShopperUidsAssociatedWithCustomerUids(customerUids);
		LOG.debug(String.format("The following shopper uids are associated with customers to be deleted: %s", shopperUidsToDelete));
		shopperCleanupService.removeShoppersByUidListAndTheirDependents(shopperUidsToDelete);

		return deleteCustomersByUids(customerUids);
	}

	private List<Long> getShopperUidsAssociatedWithCustomerUids(final List<Long> customerUids) {
		return getPersistenceEngine().retrieveByNamedQueryWithList("FIND_SHOPPER_UIDS_BY_CUSTOMER_UIDS", "list", customerUids);
	}

	private int deleteCustomersByUids(final List<Long> customerUids) {
		return getPersistenceEngine().executeNamedQueryWithList("DELETE_CUSTOMER_BY_UID_LIST", "list", customerUids);
	}


	/**
	 * Get the persistence Engine.
	 *
	 * @return the persistence engine
	 */
	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	/**
	 * Set the persistence Engine.
	 *
	 * @param persistenceEngine the persistence engine
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	/**
	 * Gets the shopper cleanup service.
	 *
	 * @return the shopper cleanup service
	 */
	public ShopperCleanupService getShopperCleanupService() {
		return shopperCleanupService;
	}

	/**
	 * Sets the shopper cleanup service.
	 *
	 * @param shopperCleanupService the new shopper cleanup service
	 */
	public void setShopperCleanupService(final ShopperCleanupService shopperCleanupService) {
		this.shopperCleanupService = shopperCleanupService;
	}
}
