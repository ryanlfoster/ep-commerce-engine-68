package com.elasticpath.sellingchannel.presentation.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.common.dto.OrderItemDto;
import com.elasticpath.sellingchannel.presentation.OrderItemDtoListMapper;
import com.elasticpath.sellingchannel.presentation.OrderItemPresentationBean;

/**
 * Default implementation of {@link OrderItemDtoListMapper}.
 */
public class OrderItemDtoListMapperImpl implements OrderItemDtoListMapper {

	private OrderItemPresentationBeanMapperImpl itemMapper;

	@Override
	public List<OrderItemPresentationBean> mapFrom(final List<OrderItemDto> orderItemDtoList) {
		
		List<OrderItemPresentationBean> formBeanList =  new ArrayList<OrderItemPresentationBean>();
		for (OrderItemDto dto : orderItemDtoList) {
			OrderItemPresentationBean document = itemMapper.mapFrom(dto);
			document.setOrderItemFields(dto.getItemFields());
			formBeanList.add(document);
		}
		
		return formBeanList;
	}

	/**
	 * 
	 * @param itemMapper The mapper to use for each item.
	 */
	public void setOrderItemPresentationBeanMapper(final OrderItemPresentationBeanMapperImpl itemMapper) {
		this.itemMapper = itemMapper;
	}

}
