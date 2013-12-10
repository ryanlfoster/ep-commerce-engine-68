package com.elasticpath.service.customer;

import java.util.Date;
import java.util.List;

import com.elasticpath.domain.customer.CustomerSessionMemento;

/**
 * Methods that clean up {@link CustomerSession)s (stale or otherwise). They normally do not need to create an
 * instance of {@link CustomerSession).
 */
public interface CustomerSessionCleanupService {
	
	/**
	 * Checks to see if a specific persisted customerSessionGuid exists.
	 *
	 * @param customerSessionGuid customerSessionGuid to check.
	 * @return true if found, false otherwise.
	 */
	boolean checkPersistedCustomerSessionGuidExists(String customerSessionGuid);
	


	/**
	 * Delete customer sessions and associated carts for any sessions with empty carts
	 * that were last accessed before the given date.
	 * 
	 * @param guids list of sessions to delete
	 * @return the number of sessions deleted
	 */
	int deleteSessions(List<String> guids);

	/**
	 * Retrieves guids for all anonymous customer sessions that have not been accessed since the provided date.
	 *
	 * @param beforeDate the date that the session must have been accessed since.
	 * @param maxResults the maximum number of results to return
	 * @return the old anonymous session guids
	 */
	List<String> getOldCustomerSessionGuids(Date beforeDate, int maxResults);

	
	/**
	 * Retrieves {@link CustomerSessionMemento}s from a list of guids.  Used in the in-term while we wait for Customer
	 * to be moved onto Shopper.
	 * 
	 * See {@link com.elasticpath.domain.customer.impl.SessionCleanupJob#purgeSessionHistory()}.
	 * 
	 * TODO: Remove after Customer is moved to {@link com.elasticpath.domain.shopper.impl.ShopperImpl}.
	 *
	 * @param customerSessionGuids customerSessionGuid list.
	 * @return a list of {@link CustomerSessionMemento}s.
	 * @deprecated Once Customer can be found on the Shopper, this should be removed.
	 */
	@Deprecated
	List<CustomerSessionMemento> getCustomerSessionMementosFromGuidsInList(List<String> customerSessionGuids);
	
	
	/**
	 * Deletes customer session that have the shopper.
	 * @param shopperUids the list of shopper uidPks 
	 * @return the number of sessions deleted.
	 */
	int deleteByShopperUids(List<Long> shopperUids);
	
	
}