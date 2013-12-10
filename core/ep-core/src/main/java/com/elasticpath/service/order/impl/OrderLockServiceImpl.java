/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.order.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderLock;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.InvalidUnlockerException;
import com.elasticpath.service.order.OrderLockService;
import com.elasticpath.service.order.OrderService;

/**
 * Provides storage and access to <code>OrderLock</code> objects. 
 */
public class OrderLockServiceImpl extends AbstractEpPersistenceServiceImpl implements OrderLockService {

	private static final Logger LOG = Logger.getLogger(OrderLockServiceImpl.class);
	
	private TimeService timeService;
	
	// for messaging purpose only
	private int orderIsLockedOrModified = VALIDATED_SUCCESSFULLY;
	
	@Override
	public OrderLock obtainOrderLock(final Order order, final CmUser cmUser, final Date openEditorDate) throws EpServiceException {
		OrderLock orderLock = getOrderLock(order);
		OrderService orderService = getBean(ContextIdNames.ORDER_SERVICE);
		final Order fresh = orderService.get(order.getUidPk());
		// order is locked.
		if (orderLock != null) {
			orderIsLockedOrModified = ORDER_IS_LOCKED;
			return null;
		}
		// Order was modified before user opened this order in editor.
		if (fresh.getLastModifiedDate().after(openEditorDate)) {
			orderIsLockedOrModified = ORDER_WAS_MODIFIED;
			return null;
		}
		// Obtain order lock.
		orderLock = getBean(ContextIdNames.ORDER_LOCK);
		orderLock.setOrder(order);
		orderLock.setCmUser(cmUser);
		orderLock.setCreatedDate(timeService.getCurrentTime().getTime());
		OrderLockService orderLockService = getBean(ContextIdNames.ORDER_LOCK_SERVICE);
		orderLock = orderLockService.add(orderLock);
		orderIsLockedOrModified = VALIDATED_SUCCESSFULLY;
		return orderLock;
	}
	
	@Override
	public int validateLock(final OrderLock orderLock, final Date openEditorDate) {
		if (orderLock == null) {
			if (ORDER_WAS_MODIFIED == orderIsLockedOrModified) {
				return ORDER_WAS_MODIFIED;
			}
			return ORDER_IS_LOCKED;
		}
		final Order freshOrder = ((OrderService) getBean(ContextIdNames.ORDER_SERVICE)).get(orderLock.getOrder().getUidPk());
		final OrderLock freshOrderLock = this.getOrderLock(freshOrder);
		if (orderLock != null && freshOrderLock == null) {
			return ORDER_WAS_UNLOCKED;
		}
		if (orderLock != null && freshOrderLock.getCreatedDate() != orderLock.getCreatedDate()) {
			return LOCK_IS_ALIEN;
		}
		if (freshOrder.getLastModifiedDate().after(openEditorDate)) {
			return ORDER_WAS_MODIFIED;
		}
		return VALIDATED_SUCCESSFULLY;
	}

	@Override
	public OrderLock getOrderLock(final Order order) throws EpServiceException {
		final List<OrderLock> results = getPersistenceEngine().retrieveByNamedQuery("ORDER_LOCK_BY_ORDER_UID", order.getUidPk());
		if (!results.isEmpty()) {
			return results.get(0);
			/** there can only be one order lock for the specified order. */
		}

		return null;
	}

	@Override
	public void releaseOrderLock(final OrderLock orderLock, final CmUser user) throws EpServiceException {
		if (!checkOrderLockSanity(orderLock, user)) {
			LOG.debug("The order lock for order " + orderLock.getOrder().getUidPk() + " was obtained for user " + orderLock.getOrder().getUidPk()
					+ " but is being released by user " + user);
			throw new InvalidUnlockerException("The order lock for order " + orderLock.getOrder().getUidPk()
					+ " is being released by incorrect user");
		}
		OrderLockService orderLockService = getBean(ContextIdNames.ORDER_LOCK_SERVICE);		
		orderLockService.remove(orderLock);
	}

	@Override
	public void forceReleaseOrderLock(final OrderLock orderLock) throws EpServiceException {
		OrderLockService orderLockService = getBean(ContextIdNames.ORDER_LOCK_SERVICE);		
		orderLockService.remove(orderLock);
	}
	
	@Override
	public List<OrderLock> findAllOrderLocksBeforeDate(final long endTime, final int firstResult, final int maxResult) {
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_LOCK_BY_TIME", new Object[] { endTime }, firstResult, maxResult);
	}

	// FIXME! add check date.
	private boolean checkOrderLockSanity(final OrderLock orderLock, final CmUser unlockerUser) {

		CmUser lockerUser = orderLock.getCmUser();

		if (unlockerUser != null && lockerUser != null && unlockerUser.getUidPk() == lockerUser.getUidPk()) {
			/** order is locked as expected AND BY CORRECT USER!!! */
			return true;
		}

		/**
		 * order is locked but user is incorrect. To test this: lock the order by user1 (by editing order), then crash application (this will leave
		 * order locked in DB) then modify order by user2 (actually this can�t be done since order is already locked, but force this to test) then
		 * call this method to test the last condition Also we can use multiple RCP applications!
		 */

		return false;

	}

	/**
	 * Generic get method for all persistable domain models.
	 * 
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	/**
	 * Get the order lock with the given UID. Return null if no matching record exists.
	 * 
	 * @param orderLockUid the order lock UID
	 * @return the order lock if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public OrderLock get(final long orderLockUid) throws EpServiceException {
		sanityCheck();
		OrderLock orderLock = null;
		if (orderLockUid <= 0) {
			orderLock = getBean(ContextIdNames.ORDER_LOCK);
		} else {
			orderLock = getPersistentBeanFinder().get(ContextIdNames.ORDER_LOCK, orderLockUid);
		}
		return orderLock;
	}

	/**
	 * Adds the given order lock.
	 * 
	 * @param orderLock the order to add
	 * @return the persisted instance of OrderLock
	 * @throws EpServiceException - in case of any errors
	 */
	public OrderLock add(final OrderLock orderLock) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().save(orderLock);
		return orderLock;

	}
	
	/**
	 * Deletes the given order lock.
	 * 
	 * @param orderLock the order to be deleted	 
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final OrderLock orderLock) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(orderLock);		
	}
	
	/**
	 * Set the time service.
	 * 
	 * @param timeService the <code>TimeService</code> instance.
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

}
