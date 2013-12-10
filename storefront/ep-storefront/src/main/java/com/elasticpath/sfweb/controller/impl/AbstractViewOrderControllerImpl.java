package com.elasticpath.sfweb.controller.impl;

import java.util.List;
import java.util.Map;

import com.elasticpath.domain.order.Order;
import com.elasticpath.sellingchannel.presentation.OrderItemPresentationBean;
import com.elasticpath.sellingchannel.presentation.OrderPresentationHelper;

/**
 * An abstract class for viewing orders.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractViewOrderControllerImpl extends AbstractEpControllerImpl {
	private String successView;

	private OrderPresentationHelper orderPresentationHelper;
	
	/**
	 * @param order an order.
	 * @return order item presentation bean map for an order.
	 */
	protected Map<Long, List< ? extends OrderItemPresentationBean>> createOrderItemFormBeanMap(final Order order) {
		return getOrderPresentationHelper().createOrderItemFormBeanMap(order);
	}

	/**
	 * Sets the static view name.
	 * 
	 * @param successView name of the success view
	 */
	public final void setSuccessView(final String successView) {
		this.successView = successView;
	}

	/**
	 * Sets the success view name.
	 * 
	 * @return name of the success view
	 */
	public String getSuccessView() {
		return this.successView;
	}

	/**
	 * @param orderPresentationHelper the orderPresentationHelper to set
	 */
	public void setOrderPresentationHelper(final OrderPresentationHelper orderPresentationHelper) {
		this.orderPresentationHelper = orderPresentationHelper;
	}

	/**
	 * @return the orderPresentationHelper
	 */
	public OrderPresentationHelper getOrderPresentationHelper() {
		return orderPresentationHelper;
	}
}
