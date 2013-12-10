/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderLock;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderLockImpl;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.InvalidUnlockerException;
import com.elasticpath.service.order.OrderLockService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test cases for <code>OrderLockServiceImpl</code>.
 */
public class OrderLockServiceImplTest extends AbstractEPServiceTestCase {

	private static final String ORDER_LOCK_BY_ORDER_UID = "ORDER_LOCK_BY_ORDER_UID";

	private OrderLockService orderLockService;

	private OrderService mockOrderService;

	private TimeService mockTimeService;

	/**
	 * Prepare for the tests.
	 *
	 * @throws Exception on error
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		orderLockService = new OrderLockServiceImpl();
		orderLockService.setPersistenceEngine(getMockPersistenceEngine());
		mockOrderService = context.mock(OrderService.class);

		stubGetBean(ContextIdNames.ORDER_SERVICE, mockOrderService);
		stubGetBean(ContextIdNames.ORDER_LOCK_SERVICE, orderLockService);
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#obtainOrderLock(
	 * com.elasticpath.domain.order.Order, com.elasticpath.domain.cmuser.CmUser)}.
	 * Obtaining of order lock should failed because of existing orderLock in database.
	 */
	@Test
	public void testObtainOrderLock1() {
		final OrderLock orderLock = new OrderLockImpl();
		final Order order = new OrderImpl();
		final long uid = 1234L;
		order.setUidPk(uid);
		final List<OrderLock> results = new ArrayList<OrderLock>();
		results.add(orderLock);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(ORDER_LOCK_BY_ORDER_UID, order.getUidPk());
				will(returnValue(results));
			}
		});
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockOrderService).get(uid);
				will(returnValue(order));
			}
		});
		assertNull(orderLockService.obtainOrderLock(order, new CmUserImpl(), new Date()));
	}
	/**
	 * Test method for
	 * {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#obtainOrderLock(
	 * com.elasticpath.domain.order.Order, com.elasticpath.domain.cmuser.CmUser)}.
	 * Obtaining of order lock should failed because order was modified after
	 * is was opened in Order Editor.
	 */
	@Test
	public void testObtainOrderLock2() {
		final Order order = new OrderImpl();
		final long uid = 1234L;
		final long after = 20L;
		final long before = 10L;
		final Date orderLastModifiedDate = new Date(after);
		final Date openEditorDate = new Date(before);
		order.setLastModifiedDate(orderLastModifiedDate);
		order.setUidPk(uid);
		final List<OrderLock> results = new ArrayList<OrderLock>();
		results.add(null);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(ORDER_LOCK_BY_ORDER_UID, order.getUidPk());
				will(returnValue(results));

				allowing(mockOrderService).get(uid);
				will(returnValue(order));
			}
		});
		// expectations
		assertNull(orderLockService.obtainOrderLock(order, new CmUserImpl(), openEditorDate));
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#obtainOrderLock(
	 * com.elasticpath.domain.order.Order, com.elasticpath.domain.cmuser.CmUser)}.
	 * Order Lock should be obtained successfully.
	 */
	@Test
	public void testObtainOrderLock3() {
		OrderLock orderLock = new OrderLockImpl();
		final Order order = new OrderImpl();
		final long uid = 1234L;
		final long before = 10L;
		final long after = 20L;
		final Date orderLastModifiedDate = new Date(before);
		final Date openEditorDate = new Date(after);
		order.setLastModifiedDate(orderLastModifiedDate);
		order.setUidPk(uid);
		final CmUser user = new CmUserImpl();
		final List<OrderLock> results = new ArrayList<OrderLock>();
		// Lock doesn't exist in database
		results.add(null);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(ORDER_LOCK_BY_ORDER_UID, order.getUidPk());
				will(returnValue(results));

				allowing(mockOrderService).get(uid);
				will(returnValue(order));
			}
		});
		stubGetBean(ContextIdNames.ORDER_LOCK, orderLock);
		mockTimeService = context.mock(TimeService.class);
		final Date lockCreatedDate = new Date();
		context.checking(new Expectations() {
			{
				allowing(mockTimeService).getCurrentTime();
				will(returnValue(lockCreatedDate));
			}
		});
		orderLockService.setTimeService(mockTimeService);

		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).save(with(any(Persistable.class)));
			}
		});

		orderLock = orderLockService.obtainOrderLock(order, user, openEditorDate);

		assertTrue(orderLock.getCmUser().equals(user));
		assertTrue(orderLock.getOrder().equals(order));
		assertEquals(orderLock.getCreatedDate(), lockCreatedDate.getTime());
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#validateLock(
	 * com.elasticpath.domain.order.OrderLock, java.util.Date)}.
	 * Order is locked.
	 */
	@Test
	public void testValidateLock1() {
		final OrderLock orderLock = null;
		// expectations
		assertEquals(OrderLockService.ORDER_IS_LOCKED,
				orderLockService.validateLock(orderLock, new Date()));
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#validateLock(
	 * com.elasticpath.domain.order.OrderLock, java.util.Date)}.
	 * Order was unlocked.
	 */
	@Test
	public void testValidateLock2() {
		final OrderLock orderLock = new OrderLockImpl();
		final Order order = new OrderImpl();
		final long uid = 1234L;
		order.setUidPk(uid);
		orderLock.setOrder(order);
		context.checking(new Expectations() {
			{
				allowing(mockOrderService).get(uid);
				will(returnValue(order));
			}
		});
		final List<OrderLock> results = new ArrayList<OrderLock>();
		// emulate that order was unlocked.
		results.add(null);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(ORDER_LOCK_BY_ORDER_UID, order.getUidPk());
				will(returnValue(results));
			}
		});
		// expectations
		assertEquals(OrderLockService.ORDER_WAS_UNLOCKED,
				orderLockService.validateLock(orderLock, new Date()));
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#validateLock(
	 * com.elasticpath.domain.order.OrderLock, java.util.Date)}.
	 * Lock is alien.
	 */
	@Test
	public void testValidateLock3() {
		final OrderLock orderLock = new OrderLockImpl();
		final Order order = new OrderImpl();
		final long uid = 1234L;
		order.setUidPk(uid);
		orderLock.setOrder(order);
		context.checking(new Expectations() {
			{
				allowing(mockOrderService).get(uid);
				will(returnValue(order));
			}
		});
		final List<OrderLock> results = new ArrayList<OrderLock>();
		// emulate that order lock exists in database but dates of creation are different
		OrderLock freshOrderLock = new OrderLockImpl();
		final long before = 10L;
		final long after = 20L;
		freshOrderLock.setCreatedDate(before);
		orderLock.setCreatedDate(after);
		results.add(freshOrderLock);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(ORDER_LOCK_BY_ORDER_UID, order.getUidPk());
				will(returnValue(results));
			}
		});
		// expectations
		assertEquals(OrderLockService.LOCK_IS_ALIEN,
				orderLockService.validateLock(orderLock, new Date()));
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#validateLock(
	 * com.elasticpath.domain.order.OrderLock, java.util.Date)}.
	 * Order was modified before it was opened in editor.
	 */
	@Test
	public void testValidateLock4() {
		final OrderLock orderLock = new OrderLockImpl();
		final Order order = new OrderImpl();
		final long uid = 1234L;
		final long before = 10L;
		final long after = 20L;
		final Date openEditorDate = new Date(before);
		final Date orderLastModifiedDate = new Date(after);
		order.setLastModifiedDate(orderLastModifiedDate);
		order.setUidPk(uid);
		orderLock.setOrder(order);
		context.checking(new Expectations() {
			{
				allowing(mockOrderService).get(uid);
				will(returnValue(order));
			}
		});
		final List<OrderLock> results = new ArrayList<OrderLock>();
		// emulate situation when order was modified
		OrderLock freshOrderLock = new OrderLockImpl();
		final long sameDate = 10L;
		freshOrderLock.setCreatedDate(sameDate);
		orderLock.setCreatedDate(sameDate);
		results.add(freshOrderLock);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(ORDER_LOCK_BY_ORDER_UID, order.getUidPk());
				will(returnValue(results));
			}
		});
		// expectations
		assertEquals(OrderLockService.ORDER_WAS_MODIFIED,
				orderLockService.validateLock(orderLock, openEditorDate));
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#validateLock(
	 * com.elasticpath.domain.order.OrderLock, java.util.Date)}.
	 * Successful validation.
	 */
	@Test
	public void testValidateLock5() {
		final OrderLock orderLock = new OrderLockImpl();
		final Order order = new OrderImpl();
		final long uid = 1234L;
		final long before = 10L;
		final long after = 20L;
		final Date openEditorDate = new Date(after);
		final Date orderLastModifiedDate = new Date(before);
		order.setLastModifiedDate(orderLastModifiedDate);
		order.setUidPk(uid);
		orderLock.setOrder(order);
		context.checking(new Expectations() {
			{
				allowing(mockOrderService).get(uid);
				will(returnValue(order));
			}
		});
		final List<OrderLock> results = new ArrayList<OrderLock>();
		// emulate successful validation
		OrderLock freshOrderLock = new OrderLockImpl();
		final long sameDate = 10L;
		freshOrderLock.setCreatedDate(sameDate);
		orderLock.setCreatedDate(sameDate);
		results.add(freshOrderLock);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(ORDER_LOCK_BY_ORDER_UID, order.getUidPk());
				will(returnValue(results));
			}
		});
		// expectations
		assertEquals(OrderLockService.VALIDATED_SUCCESSFULLY,
				orderLockService.validateLock(orderLock, openEditorDate));
	}

	/**
	 * Test method for {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#getOrderLock(com.elasticpath.domain.order.Order)}.
	 */
	@Test
	public void testGetOrderLock() {
		final OrderLock orderLock = new OrderLockImpl();
		final Order order = new OrderImpl();
		final long uid = 1234L;
		order.setUidPk(uid);
		orderLock.setOrder(order);
		final List<OrderLock> results = new ArrayList<OrderLock>();
		results.add(orderLock);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).initialize(with(any(Object.class)));

				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(ORDER_LOCK_BY_ORDER_UID, order.getUidPk());
				will(returnValue(results));
			}
		});
		assertEquals(orderLock, orderLockService.getOrderLock(order));
	}

	/**
	 * Test method for {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#get(long)}.
	 */
	@Test
	public void testGet() {
		stubGetBean(ContextIdNames.ORDER_LOCK, OrderLockImpl.class);

		final OrderLock orderLock = new OrderLockImpl();
		final long orderLockUid = 1234L;
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).get(OrderLockImpl.class, orderLockUid);
				will(returnValue(orderLock));
			}
		});
		assertNotNull(((OrderLockServiceImpl) orderLockService).get(orderLockUid));
	}

	/**
	 * Test method for {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#releaseOrderLock
	 * (com.elasticpath.domain.order.OrderLock, com.elasticpath.domain.cmuser.CmUser)}.
	 */
	@Test
	public void testReleaseOrderLock1() {
		final OrderLock orderLock = new OrderLockImpl();
		final CmUser lokerUser = new CmUserImpl();
		orderLock.setCmUser(lokerUser);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).delete(orderLock);
			}
		});
		orderLockService.releaseOrderLock(orderLock, lokerUser);
	}

	/**
	 * Test method for {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#releaseOrderLock
	 * (com.elasticpath.domain.order.OrderLock, com.elasticpath.domain.cmuser.CmUser)}.
	 */
	@Test
	public void testReleaseOrderLock2() {
		final OrderLock orderLock = new OrderLockImpl();
		final CmUser lokerUser = new CmUserImpl();
		final long firstUid = 1234L;
		final long secondUid = 4321L;
		lokerUser.setUidPk(firstUid);
		orderLock.setCmUser(lokerUser);
		final Order order = new OrderImpl();
		final long uid = 11L;
		order.setUidPk(uid);
		orderLock.setOrder(order);
		final CmUser unlokerUser = new CmUserImpl();
		unlokerUser.setUidPk(secondUid);

		try {
			orderLockService.releaseOrderLock(orderLock, unlokerUser);
		} catch (InvalidUnlockerException exception) {
			assertNotNull(exception);
		}
	}

	/**
	 * Test method for {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#forceReleaseOrderLock(com.elasticpath.domain.order.OrderLock)}.
	 */
	@Test
	public void testForceReleaseOrderLock() {
		final OrderLock orderLock = new OrderLockImpl();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).delete(orderLock);
			}
		});
		orderLockService.forceReleaseOrderLock(orderLock);
	}

	/**
	 * Test method for {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#add(com.elasticpath.domain.order.OrderLock)}.
	 */
	@Test
	public void testAdd() {
		final OrderLock orderLock = new OrderLockImpl();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).save(orderLock);
			}
		});
		orderLockService.add(orderLock);
	}

	/**
	 * Test method for {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#remove(com.elasticpath.domain.order.OrderLock)}.
	 */
	@Test
	public void testRemove() {
		final OrderLock orderLock = new OrderLockImpl();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).delete(orderLock);
			}
		});
		orderLockService.remove(orderLock);
	}
}
