/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.sfweb.exception.EpRequestParameterBindingException;

/**
 * The Spring MVC controller for displaying an order.
 */
public class ViewOrderControllerImpl extends AbstractViewOrderControllerImpl {
	private String requestType;

	private OrderService orderService;

	/**
	 * Process request for displaying an order.
	 *
	 * @param request -the request
	 * @param response -the response
	 * @return - the ModelAndView instance
	 * @throws Exception if anything goes wrong.
	 */
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		// load requested order from database
		final String orderID = getRequestHelper().getStringParameterOrAttribute(request, WebConstants.REQUEST_OID, null);
		Order order = findOrderByOrderId(orderID);

		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);

		// If customer is anonymous AND is requesting "View Order Details" screen, then throw an exception
		if (order.getCustomer().isAnonymous() && getRequestType().equalsIgnoreCase("ViewOrderDetails")) {
			throw new EpRequestParameterBindingException("Only registered customers can access this screen");
		}

		// If order does not belong to customer, then throw an exception
		final long orderCustomerID = order.getCustomer().getUidPk();
		final long customerID = customerSession.getShopper().getCustomer().getUidPk();
		if (orderCustomerID != customerID) {
			throw new EpRequestParameterBindingException("Order does not belong to customer");
		}

		final Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("orderItemFormBeanMap", createOrderItemFormBeanMap(order));
		modelMap.put("order", order);
		modelMap.put("frequencyMap", getOrderPresentationHelper().getFrequencyMap(order));

		return new ModelAndView(getSuccessView(), modelMap);
	}

	/**
	 * Finds an order by order ID.
	 *
	 * @param orderID the order id
	 * @return the order
	 * @throws EpRequestParameterBindingException in the case of a validation error
	 */
	protected Order findOrderByOrderId(final String orderID) throws EpRequestParameterBindingException {
		if (orderID == null) {
			throw new EpRequestParameterBindingException("Order ID is not specified.");
		}
		final List<Order> orders = orderService.findOrder("orderNumber", orderID, true);
		if (orders.size() != 1) {
			throw new EpRequestParameterBindingException("Specified order does not exist or orderNumber is not unique: " + orderID);
		}
		Order order = orders.get(0);

		if (OrderStatus.FAILED.equals(order.getStatus())) {
			throw new EpRequestParameterBindingException("The order is failed. The customer should not be able to see it. orderId=" + orderID);
		}
		return order;
	}

	/**
	 * Sets the order service.
	 *
	 * @param orderService the order service
	 */
	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * Sets the request type.
	 *
	 * @param requestType type of request (either "ViewOrderDetails" or "ViewPrintReceipt")
	 */
	public final void setRequestType(final String requestType) {
		this.requestType = requestType;
	}

	/**
	 * Gets the request type.
	 *
	 * @return type of request (either "ViewOrderDetails" or "ViewPrintReceipt")
	 */
	public String getRequestType() {
		return this.requestType;
	}
}
