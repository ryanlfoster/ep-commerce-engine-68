package com.elasticpath.service.customer.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.domain.customer.CustomerSessionMemento;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.customer.CustomerSessionCleanupService;

/**
 * Clean up customer sessions using JPQL queries.
 */
public class CustomerSessionCleanupServiceImpl implements CustomerSessionCleanupService {

	private PersistenceEngine persistenceEngine;
	private static final Logger LOG = Logger.getLogger(CustomerSessionCleanupServiceImpl.class);

	@Override
	public boolean checkPersistedCustomerSessionGuidExists(final String customerSessionGuid) {
		if (customerSessionGuid == null) {
			throw new EpServiceException("Cannot retrieve null guid.");
		}

		final List<CustomerSessionMemento> results = getPersistenceEngine().retrieveByNamedQuery("CUSTOMER_SESSION_CONFIRM_GUID_EXISTS_BY_GUID",
				customerSessionGuid);

		if (results.isEmpty()) {
			return false;
		}
		
		return true;
	}

	@Override
	public int deleteByShopperUids(final List<Long> shopperUids) {
		return getPersistenceEngine().executeNamedQueryWithList("CUSTOMER_SESSION_DELETE_BY_SHOPPER_UID_LIST", "list", shopperUids);
		
	}

	@Override
	public int deleteSessions(final List<String> guids) {
		int sessionsCleaned = 0;
		if (!guids.isEmpty()) {
			sessionsCleaned = getPersistenceEngine().executeNamedQueryWithList("CUSTOMER_SESSION_DELETE_BY_GUID_LIST", "list", guids);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleted " + sessionsCleaned + " records from the Session history");
		}

		return sessionsCleaned;
	}

	@Override
	public List<CustomerSessionMemento> getCustomerSessionMementosFromGuidsInList(final List<String> customerSessionGuids) {
        if (customerSessionGuids == null) {
            throw new EpServiceException("customerSessionGuids must be supplied.");
        }

        return getPersistenceEngine().<CustomerSessionMemento>retrieveByNamedQuery("CUSTOMER_SESSIONS_FROM_GUIDS_IN_LIST",
				customerSessionGuids);
	}

	@Override
	public List<String> getOldCustomerSessionGuids(final Date beforeDate, final int maxResults) {
        if (beforeDate == null) {
            throw new EpServiceException("beforeDate must be supplied.");
        }

        return getPersistenceEngine().<String>retrieveByNamedQuery("OLD_CUSTOMER_SESSION_GUID", new Object[] { beforeDate }, 0, maxResults);
	}

	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}
}
