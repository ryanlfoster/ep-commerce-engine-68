/**
 * 
 */
package com.elasticpath.sellingchannel.presentation.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.common.dto.OrderItemDto;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPrice;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPriceFactory;
import com.elasticpath.sellingchannel.director.OrderDirector;
import com.elasticpath.sellingchannel.presentation.OrderItemDtoListMapper;
import com.elasticpath.sellingchannel.presentation.OrderItemPresentationBean;
import com.elasticpath.sellingchannel.presentation.OrderPresentationHelper;

/**
 * Provides functions to help translate Orders into beans for presentation in Velocity templates
 * and other templates that can't deal with recursively traversing a tree of nested items.
 */
public class OrderPresentationHelperImpl implements OrderPresentationHelper {

	private OrderDirector orderDirector;

	private OrderItemDtoListMapper orderItemDtoListMapper;
	
	/**
	 * Creates a map of ShipmentNumber to OrderItemPresentationBean for the given Order.
	 * @param order an order.
	 * @return order item presentation bean map for an order.
	 */
	public Map<Long, List< ? extends OrderItemPresentationBean>> createOrderItemFormBeanMap(final Order order) {
		Map<Long, List< ? extends OrderItemPresentationBean>> orderItemPresentationBeanMap = 
			new HashMap<Long, List< ? extends OrderItemPresentationBean>>();

		for (OrderShipment shipment : order.getAllShipments()) {
			orderItemPresentationBeanMap.put(shipment.getUidPk(), createOrderItemFormBeanList(shipment));
		}

		return orderItemPresentationBeanMap;
	}

	/**
	 * Create a list of OrderItemPresentationBeans for this shipment.
	 *
	 * @param shipment the shipment
	 * @return a list of OrderItemPresentationBeans for this shipment
	 */
	public List< ? extends OrderItemPresentationBean> createOrderItemFormBeanList(final OrderShipment shipment) {
		List<OrderItemDto> orderItemDtoList = orderDirector.createOrderItemDtoList(shipment);
		return orderItemDtoListMapper.mapFrom(orderItemDtoList);
	}

	/**
	 * @param orderDirector the orderDirector to set
	 */
	public void setOrderDirector(final OrderDirector orderDirector) {
		this.orderDirector = orderDirector;
	}

	/**
	 * @param orderItemDtoListMapper the orderItemDtoListMapper to set
	 */
	public void setOrderItemDtoListMapper(final OrderItemDtoListMapper orderItemDtoListMapper) {
		this.orderItemDtoListMapper = orderItemDtoListMapper;
	}
	
	
	/**
	 * Gets the frequency map for the order.
	 * @param order the order to use
	 * @return the frequency map for the order's shopping items.
	 */
	public Map<Quantity, FrequencyAndRecurringPrice> getFrequencyMap(final Order order) {
		if (order == null) {
			return null;
		}
		return new FrequencyAndRecurringPriceFactory().getFrequencyMap(order.getRootShoppingItems());
	}
}
