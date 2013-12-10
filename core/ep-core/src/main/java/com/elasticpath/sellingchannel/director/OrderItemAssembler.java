package com.elasticpath.sellingchannel.director;


import com.elasticpath.common.dto.OrderItemDto;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;

/**
 * Maps {@code OrderSku}s to {@code OrderSkuDto}s and vice versa.
 */
public interface OrderItemAssembler {
	
	/**
	 * Creates an {@code OrderItemDto} from an {@code OrderSku}.
	 * @param orderSku The {@code OrderSku} to read.
	 * @param shipment The {@code OrderShipment} to filter by
	 * @return The {@code OrderItemDto}.
	 */
	OrderItemDto createOrderItemDto(OrderSku orderSku, OrderShipment shipment);
}
