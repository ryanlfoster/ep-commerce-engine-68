/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.order;

import java.util.Date;
import java.util.List;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderLock;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.TimeService;

/**
 * Provides storage and access to <code>OrderLock</code> objects.
 */
public interface OrderLockService extends EpPersistenceService {
	
	/**
	 * Lock was validated successfully.
	 */
	int VALIDATED_SUCCESSFULLY = 0; 

	/**
	 * Order is locked by another user.
	 */
	int ORDER_IS_LOCKED = 1;
	
	/**
	 * Lock was removed by another user.
	 */
	int ORDER_WAS_UNLOCKED = 2;
	
	/**
	 * Order was modified since it was opened in editor.
	 */
	int ORDER_WAS_MODIFIED = 3;

	/**
	 * Lock exists but it is created by another user. 
	 */
	int LOCK_IS_ALIEN = 4;

	/**
	 * Adds the given order lock.
	 * 
	 * @param orderLock the order to add
	 * @return the persisted instance of OrderLock
	 * @throws EpServiceException - in case of any errors
	 */
	OrderLock add(final OrderLock orderLock) throws EpServiceException;
	
	/**
	 * Deletes the given order lock.
	 * 
	 * @param orderLock the order to be deleted	 
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final OrderLock orderLock) throws EpServiceException;
	
	/**
	 * Creates the order lock and returns the newly created order lock. The order lock will be returned only in case if none exists, else null is
	 * returned to indicate that order lock was not attainable.
	 * 
	 * @param order the order to be locked
	 * @param user the locker user
	 * @param openEditorDate when editor was opened
	 * @return newly created OrderLock or null if order is already locked.
	 * @throws EpServiceException - in case of any errors
	 */
	OrderLock obtainOrderLock(final Order order, final CmUser user, final Date openEditorDate) throws EpServiceException;
	
	/**
	 * Validate order lock obtained in order editor. 
	 * This returns an integer code (specified on this interface) indicating the Order's
	 * lock status. A status of zero means that the order is not locked. 
	 *
	 * @param orderLock order lock to be validated
	 * @param openEditorDate date when editor was opened
	 * @return result of validation
	 */
	int validateLock(final OrderLock orderLock, final Date openEditorDate);

	/**
	 * Returns the orderLock for an order if it exists, else returns null.
	 * 
	 * @param order the order
	 * @return orderLock if exists, null otherwise.
	 * @throws EpServiceException - in case of any errors
	 */
	OrderLock getOrderLock(final Order order) throws EpServiceException;

	/**
	 * Releases a specific order lock.
	 * 
	 * @param orderLock order lock to be released
	 * @param user the CmUser which is being unlocked the order.
	 * @throws EpServiceException - in case of any errors, 
	 * InvalidUnlockerException if when the orderLock was obtained not by the user, but by some other user.
	 */
	void releaseOrderLock(final OrderLock orderLock, final CmUser user) throws EpServiceException;

	/**
	 * Forces release a specific order lock.
	 * 
	 * @param orderLock order lock to be released
	 * @throws EpServiceException - in case of any errors
	 */
	void forceReleaseOrderLock(final OrderLock orderLock) throws EpServiceException;
	
	/**
	 * Finds all {@link OrderLock} objects that have been created before a given date.
	 *
	 * @param endTime - the time up until we want all the order locks for.
	 * @param firstResult - the first result to retrieve
	 * @param maxResult - the maximum result to retrieve
	 * @return all order locks before the given date
	 */
	List<OrderLock> findAllOrderLocksBeforeDate(final long endTime, final int firstResult, final int maxResult);
	
	/**
	 * Set the time service.
	 * 
	 * @param timeService the <code>TimeService</code> instance.
	 */
	void setTimeService(final TimeService timeService);
}