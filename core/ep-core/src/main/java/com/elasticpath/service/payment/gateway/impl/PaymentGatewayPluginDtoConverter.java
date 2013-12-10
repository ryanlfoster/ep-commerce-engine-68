package com.elasticpath.service.payment.gateway.impl;

import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.misc.PayerAuthenticationEnrollmentResult;
import com.elasticpath.domain.misc.impl.PayerAuthenticationEnrollmentResultImpl;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.Money;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.dto.OrderSkuDto;
import com.elasticpath.plugin.payment.dto.PayerAuthenticationEnrollmentResultDto;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.plugin.payment.dto.ShoppingCartDto;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;

/**
 * Converts entities into payment gateway plugin DTOs.
 */
public final class PaymentGatewayPluginDtoConverter {

	/**
	 * Ensure that class is not instantiated.
	 */
	private PaymentGatewayPluginDtoConverter() {
	}

	/**
	 * Assembles an {@link AddresssDto} from an Address.
	 * 
	 * @param address
	 *            the address
	 * @return an assembled address dto
	 */
	public static AddressDto toAddressDto(final Address address) {
		PaymentGatewayDtoFactory factory = PaymentGatewayDtoFactory
				.getFactory();
		AddressDto addressDto = factory.createAddressDto();

		Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
		mapper.map(address, addressDto);

		return addressDto;
	}

	/**
	 * Assembles an {@link OrderSkuDto} from an OrderSku.
	 * 
	 * @param orderSku
	 *            the orderSku
	 * @return an assembled orderSkuDto
	 */
	public static OrderSkuDto toOrderSkuDto(final OrderSku orderSku) {
		PaymentGatewayDtoFactory factory = PaymentGatewayDtoFactory
				.getFactory();
		OrderSkuDto orderSkuDto = factory.createOrderSkuDto();

		Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
		mapper.map(orderSku, orderSkuDto);

		return orderSkuDto;
	}

	/**
	 * Assembles an {@link OrderPaymentDto} from an OrderPayment.
	 * 
	 * @param orderPayment
	 *            the orderPayment
	 * @return an assembled orderPaymentDto
	 */
	public static OrderPaymentDto toOrderPaymentDto(
			final OrderPayment orderPayment) {
		PaymentGatewayDtoFactory factory = PaymentGatewayDtoFactory.getFactory();
		OrderPaymentDto orderPaymentDto = factory.createGiftCertificateOrderPaymentDto();

		Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
		mapper.map(orderPayment, orderPaymentDto);

		return orderPaymentDto;
	}

	/**
	 * Assembles an {@link OrderPayment} from an {@link OrderPaymentDto}.
	 * 
	 * @param orderPaymentDto
	 *            the orderPaymentDto
	 * @return an assembled orderPayment
	 */
	public static OrderPayment toOrderPayment(
			final OrderPaymentDto orderPaymentDto) {
		OrderPayment orderPayment = new OrderPaymentImpl();

		Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
		mapper.map(orderPaymentDto, orderPayment);

		return orderPayment;
	}

	/**
	 * Assembles an {@link OrderShipmentDto} from an OrderShipment.
	 * 
	 * @param orderShipment
	 *            the orderShipment
	 * @return an assembled orderShipmentDto
	 */
	public static OrderShipmentDto toOrderShipmentDto(
			final OrderShipment orderShipment) {
		PaymentGatewayDtoFactory factory = PaymentGatewayDtoFactory
				.getFactory();
		OrderShipmentDto orderShipmentDto = factory.createOrderShipmentDto();

		Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
		mapper.map(orderShipment, orderShipmentDto);

		return orderShipmentDto;
	}

	/**
	 * Assembles an {@link ShoppingCartDto} from an shoppingCart.
	 * 
	 * @param shoppingCart
	 *            the shoppingCart
	 * @return an assembled shoppingCartDto
	 */
	public static ShoppingCartDto toShoppingCartDto(
			final ShoppingCart shoppingCart) {
		PaymentGatewayDtoFactory factory = PaymentGatewayDtoFactory
				.getFactory();
		ShoppingCartDto shoppingCartDto = factory.createShoppingCartDto();

		Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
		mapper.map(shoppingCart, shoppingCartDto);

		return shoppingCartDto;
	}

	/**
	 * Assembles a {@link PayerAuthenticationEnrollmentResult} from a
	 * PayerAuthenticationEnrollmentResultDto.
	 * 
	 * @param dto
	 *            the PayerAuthenticationEnrollmentResultDto
	 * @return the payer authentication enrollment result
	 */
	public static PayerAuthenticationEnrollmentResult toResultDomain(
			final PayerAuthenticationEnrollmentResultDto dto) {
		PayerAuthenticationEnrollmentResult result = new PayerAuthenticationEnrollmentResultImpl();
		Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
		mapper.map(dto, result);

		return result;
	}
	
	/**
	 * Assembles an {@link AuthorizationTransactionRequest} from an {@link OrderPayment}.
	 *
	 * @param orderPayment the {@link OrderPayment}
	 * @return the {@link AuthorizationTransactionRequest} built
	 */
	public static AuthorizationTransactionRequest toAuthorizationRequest(final OrderPayment orderPayment) {
		Money money = toMoney(orderPayment);
		PaymentMethod paymentMethod = toPaymentMethod(orderPayment);
		
		PaymentGatewayDtoFactory factory = PaymentGatewayDtoFactory.getFactory();
		
		AuthorizationTransactionRequest authorizationTransactionRequest;
		if (orderPayment.getPaymentMethod() == PaymentType.GIFT_CERTIFICATE) {
			authorizationTransactionRequest = factory.createGiftCertificateAuthorizationRequest();
		} else {
			authorizationTransactionRequest = factory.createAuthorizationTransactionRequest();
		}
		
		authorizationTransactionRequest.setMoney(money);
		authorizationTransactionRequest.setPaymentMethod(paymentMethod);
		
		Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
		mapper.map(orderPayment, authorizationTransactionRequest);
		
		return authorizationTransactionRequest;
	}

	/**
	 * Assembles an {@link CaptureTransactionRequest} from an {@link OrderPayment}.
	 *
	 * @param orderPayment the {@link OrderPayment}
	 * @return the {@link CaptureTransactionRequest} built
	 */
	public static CaptureTransactionRequest toCaptureRequest(final OrderPayment orderPayment) {
		Money money = toMoney(orderPayment);
		
		PaymentGatewayDtoFactory factory = PaymentGatewayDtoFactory.getFactory();
		CaptureTransactionRequest captureTransactionRequest;
		if (orderPayment.getPaymentMethod() == PaymentType.GIFT_CERTIFICATE) {
			captureTransactionRequest = factory.createGiftCertificateCaptureRequest();
		} else {
			captureTransactionRequest = factory.createCaptureTransactionRequest();
		}
		
		captureTransactionRequest.setMoney(money);
		
		Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
		mapper.map(orderPayment, captureTransactionRequest);
		
		return captureTransactionRequest;
	}

	/**
	 * Creates a Money based on an input {@link OrderPayment}.
	 * @param orderPayment the order payment to base the new Money on
	 * @return the newly created money
	 */
	public static Money toMoney(final OrderPayment orderPayment) {
		PaymentGatewayDtoFactory factory = PaymentGatewayDtoFactory.getFactory();
		Money money = factory.createMoney();
		money.setAmount(orderPayment.getAmount());
		money.setCurrencyCode(orderPayment.getCurrencyCode());
		return money;
	}
	
	/**
	 * Creates a {@link PaymentMethod} on an input {@link OrderPayment}.
	 *
	 * @param orderPayment the order payment to base the payment method on
	 * @return the newly created {@link PaymentMethod}
	 */
	public static PaymentMethod toPaymentMethod(final OrderPayment orderPayment) {
		PaymentGatewayDtoFactory factory = PaymentGatewayDtoFactory.getFactory();
		
		PaymentMethod paymentMethod;
		if (orderPayment.getPaymentMethod() == PaymentType.GIFT_CERTIFICATE) {
			paymentMethod = factory.createGiftCertificateOrderPaymentDto();
		} else if (orderPayment.getPaymentMethod() == PaymentType.PAYMENT_TOKEN) {
			paymentMethod = factory.createTokenPaymentMethod(orderPayment.extractPaymentToken().getValue());
		} else {
			paymentMethod = factory.createCardDetailsPaymentMethod();
		}
		
		Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
		mapper.map(orderPayment, paymentMethod);
		return paymentMethod;
	}
}
