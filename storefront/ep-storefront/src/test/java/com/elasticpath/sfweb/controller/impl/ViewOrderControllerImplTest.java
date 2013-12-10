package com.elasticpath.sfweb.controller.impl;

import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.sfweb.exception.EpRequestParameterBindingException;


/**
 * Tests {@link ViewOrderControllerImpl}.
 */
public class ViewOrderControllerImplTest {
	private static final String ORDER_NUMBER = "orderNumber";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private OrderService orderService;

	private final ViewOrderControllerImpl controller = new ViewOrderControllerImpl();
	/**
	 * Runs before every test case.
	 */
	@Before
	public void setUp() {
		orderService = context.mock(OrderService.class);
		controller.setOrderService(orderService);
	}

	/**
	 * Tests {@link ViewOrderControllerImpl#findOrderByOrderId(String)} for the case of a
	 * <code>null</code> orderId.
	 */
	@Test(expected = EpRequestParameterBindingException.class)
	public void testFindOrderByOrderIdNullOrderId() {
		controller.findOrderByOrderId(null);
	}

	/**
	 * Tests {@link ViewOrderControllerImpl#findOrderByOrderId(String)} for the case of a
	 * an orderId resulting in no orders.
	 */
	@Test(expected = EpRequestParameterBindingException.class)
	public void testFindOrderByOrderIdNoOrder() {
		final String orderId = "badOrderId";
		context.checking(new Expectations() {
			{
				oneOf(orderService).findOrder(ORDER_NUMBER, orderId, true); will(returnValue(Collections.emptyList()));
			}
		});

		controller.findOrderByOrderId(orderId);
	}

	/**
	 * Tests {@link ViewOrderControllerImpl#findOrderByOrderId(String)} for the failed orders.
	 */
	@Test(expected = EpRequestParameterBindingException.class)
	public void testFindOrderByOrderIdFailedOrder() {
		final String orderId = "badOrderId";
		final Order order = context.mock(Order.class);
		context.checking(new Expectations() {
			{
				oneOf(orderService).findOrder(ORDER_NUMBER, orderId, true); will(returnValue(Arrays.asList(order)));
				allowing(order).getStatus(); will(returnValue(OrderStatus.FAILED));
			}
		});

		controller.findOrderByOrderId(orderId);
	}

	/**
	 * Tests {@link ViewOrderControllerImpl#findOrderByOrderId(String)} for the happy path case.
	 */
	@Test
	public void testFindOrderByOrderId() {
		final String orderId = "badOrderId";
		final Order order = context.mock(Order.class);
		context.checking(new Expectations() {
			{
				oneOf(orderService).findOrder(ORDER_NUMBER, orderId, true); will(returnValue(Arrays.asList(order)));
				ignoring(order);
			}
		});

		Order orderFromController = controller.findOrderByOrderId(orderId);
		assertSame(order, orderFromController);
	}

}
