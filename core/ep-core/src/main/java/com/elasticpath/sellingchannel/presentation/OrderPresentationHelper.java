package com.elasticpath.sellingchannel.presentation;

import java.util.List;
import java.util.Map;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPrice;

/**
 * Provides helper methods for preparing an Order for presentation with a templating technology.
 */
public interface OrderPresentationHelper {

	/**
	 * Creates a map of ShipmentNumber to OrderItemPresentationBean for the given Order.
	 * @param order an order.
	 * @return order item presentation bean map for an order.
	 */
	Map<Long, List< ? extends OrderItemPresentationBean>> createOrderItemFormBeanMap(Order order);
	
	/**
	 * Create a list of OrderItemPresentationBeans for this shipment.
	 *
	 * @param shipment the shipment
	 * @return a list of OrderItemPresentationBeans for this shipment
	 */
	List< ? extends OrderItemPresentationBean> createOrderItemFormBeanList(final OrderShipment shipment);
	
	
	/**
	 * Gets the frequency map for the order.
	 * @param order the order to use
	 * @return the frequency map for the order's shopping items.
	 */
	Map<Quantity, FrequencyAndRecurringPrice> getFrequencyMap(final Order order);
	
}
