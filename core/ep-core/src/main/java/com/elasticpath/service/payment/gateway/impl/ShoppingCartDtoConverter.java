package com.elasticpath.service.payment.gateway.impl;

import org.dozer.CustomConverter;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.ShoppingCartDto;

/**
 * Customer converter for Shopping Cart Dto.
 *
 */
public class ShoppingCartDtoConverter implements CustomConverter {

	@Override
	public Object convert(final Object destination, final Object source, final Class<?> destClass, final Class<?> sourceClass) {
		if (source == null) {
			return null;
		}

		ShoppingCartDto shoppingCartDto = (ShoppingCartDto) destination;

		if (source instanceof ShoppingCart) {
			ShoppingCart shoppingCart = (ShoppingCart) source;
			
			shoppingCartDto.setCountry(shoppingCart.getLocale().getCountry());
			shoppingCartDto.setCurrencyCode(shoppingCart.getCurrency().getCurrencyCode());
			shoppingCartDto.setTotalAmount(shoppingCart.getTotal());
			shoppingCartDto.setRequiresShipping(shoppingCart.requiresShipping());

			Address billingAddress = shoppingCart.getBillingAddress();
			if (billingAddress != null) {
				AddressDto addressDto = PaymentGatewayPluginDtoConverter.toAddressDto(billingAddress);
			shoppingCartDto.setShippingAddress(addressDto);
			}
			
		} else {
			throw new EpSystemException("Cannot convert a: " + sourceClass.toString());
		}
		return shoppingCartDto;
	}


}
