package com.elasticpath.service.payment.gateway.impl;

import java.util.HashSet;
import java.util.Set;

import org.dozer.CustomConverter;

import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.dto.OrderSkuDto;

/**
 * Many properties in OrderShipmentDto are physical shipment oriented.  This class
 * converts any OrderShipment into an OrderShipmentDto.
 *
 */
public class OrderShipmentDtoConverter implements CustomConverter {

	@Override
	public Object convert(final Object destination, final Object source, final Class<?> destClass, final Class<?> sourceClass) {
		if (source == null) {
			return null;
		}

		OrderShipmentDto orderShipmentDto = (OrderShipmentDto) destination;

		if (source instanceof PhysicalOrderShipment) {
			PhysicalOrderShipment physicalOrderShipment = (PhysicalOrderShipment) source;
			
			if (ShipmentType.PHYSICAL.equals(physicalOrderShipment.getOrderShipmentType())) {
				orderShipmentDto.setPhysical(true);
			}
			orderShipmentDto.setCarrier(physicalOrderShipment.getCarrier());
			orderShipmentDto.setTrackingCode(physicalOrderShipment.getTrackingCode());
			orderShipmentDto.setServiceLevel(physicalOrderShipment.getServiceLevel());
			orderShipmentDto.setShippingCost(physicalOrderShipment.getShippingCost());
			orderShipmentDto.setShippingTax(physicalOrderShipment.getShippingTax());
		}

		OrderShipment orderShipment = (OrderShipment) source;
		orderShipmentDto.setShipmentNumber(orderShipment.getShipmentNumber());
		orderShipmentDto.setExternalOrderNumber(orderShipment.getOrder().getExternalOrderNumber());
		if (null != orderShipment.getShipmentOrderSkus()) {
			Set<OrderSkuDto> orderSkuDtos = new HashSet<OrderSkuDto>();
			for (final OrderSku orderSku : orderShipment.getShipmentOrderSkus()) {
				OrderSkuDto orderSkuDto = PaymentGatewayPluginDtoConverter.toOrderSkuDto(orderSku);
				orderSkuDtos.add(orderSkuDto);
			}
			orderShipmentDto.setOrderSkuDtos(orderSkuDtos);
		}
		return orderShipmentDto;
	}

}
