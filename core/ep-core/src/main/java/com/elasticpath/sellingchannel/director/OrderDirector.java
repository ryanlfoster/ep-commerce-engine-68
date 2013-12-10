package com.elasticpath.sellingchannel.director;

import java.util.List;

import com.elasticpath.common.dto.OrderItemDto;
import com.elasticpath.domain.order.OrderShipment;

/**
 * Director for co-ordinating the message flow when displaying {@code Orders}. Note that this class should have 
 * as little actual logic as possible.
 */
public interface OrderDirector {

	/**
	 * Creates a list of {@code OrderItemDto}s for the {@code OrderSku}s in {@code shipment}.
	 * 
	 * @param shipment The shipment to retrieve the orders for.
	 * @return The list.
	 */
	List<OrderItemDto> createOrderItemDtoList(OrderShipment shipment);

}
