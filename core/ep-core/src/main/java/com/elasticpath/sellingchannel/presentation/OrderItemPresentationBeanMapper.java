package com.elasticpath.sellingchannel.presentation;

import com.elasticpath.common.dto.OrderItemDto;

/**
 * Maps from {@code OrderItemDto} to {@code OrderItemFormBean}.
 */
public interface OrderItemPresentationBeanMapper {
	
	/**
	 * 
	 * @param orderItemDto The dto to map from.
	 * @return The form bean to map to.
	 */
	OrderItemPresentationBean mapFrom(OrderItemDto orderItemDto);
}
