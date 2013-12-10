package com.elasticpath.sellingchannel.presentation;

import java.util.List;

import com.elasticpath.common.dto.OrderItemDto;

/**
 * Maps from a list of {@code OrderItemDto}s to a list of {@code OrderItemDocument}s.
 */
public interface OrderItemDtoListMapper {

	/**
	 * Maps from a list of {@code OrderItemDto}s to a list of {@code OrderItemDocument}s.
	 * @param orderItemDtoList The input
	 * @return The output.
	 */
	List<OrderItemPresentationBean> mapFrom(List<OrderItemDto> orderItemDtoList);

}
