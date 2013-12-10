package com.elasticpath.sellingchannel.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.elasticpath.common.dto.OrderItemDto;
import com.elasticpath.sellingchannel.presentation.OrderItemPresentationBean;
import com.elasticpath.sellingchannel.presentation.impl.OrderItemDtoListMapperImpl;
import com.elasticpath.sellingchannel.presentation.impl.OrderItemPresentationBeanMapperImpl;
import com.elasticpath.sellingchannel.presentation.impl.OrderItemPresentationBeanMapperImpl.CopyFunctor;

/**
 * Tests the {@code OrderItemDtoListMapperImpl}.
 */
public class OrderItemDtoListMapperImplTest {
	/** Required for testing. */
	private class CopyFunctorTestable extends CopyFunctor {
		@Override
		protected boolean isGC(final OrderItemDto sourceNode) {
			return true;
		}
	}

	/** Required for testing. */
	private class OrderItemPresentationBeanMapperTestable extends OrderItemPresentationBeanMapperImpl {
		public OrderItemPresentationBeanMapperTestable() {
			super();
			setFunctor(new CopyFunctorTestable());
		}
	}
	
	/**
	 * Tests that mapping from {@code OrderItemDto} list to {@code OrderItemPresentationBean} list works.
	 */
	@Test
	public void testListMapping() {
		OrderItemDtoListMapperImpl mapper = new OrderItemDtoListMapperImpl();
		OrderItemPresentationBeanMapperTestable itemMapper = new OrderItemPresentationBeanMapperTestable();
		mapper.setOrderItemPresentationBeanMapper(itemMapper);
		
		List<OrderItemDto> orderItemDtoList = new ArrayList<OrderItemDto>();
		OrderItemDto dto1 = new OrderItemDto();
		OrderItemDto dto2 = new OrderItemDto();
		orderItemDtoList.add(dto1);
		orderItemDtoList.add(dto2);
		
		List<OrderItemPresentationBean> presentationBeanList = mapper.mapFrom(orderItemDtoList);
		
		assertEquals("Expect 2 members - 1 for each dto", 2, presentationBeanList.size());
	}
	

}
