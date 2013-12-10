package com.elasticpath.service.payment.gateway.impl;

import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.CardDetailsPaymentMethod;
import com.elasticpath.plugin.payment.dto.Money;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.dto.OrderSkuDto;
import com.elasticpath.plugin.payment.dto.PayerAuthValidationValueDto;
import com.elasticpath.plugin.payment.dto.ShoppingCartDto;
import com.elasticpath.plugin.payment.dto.TokenPaymentMethod;
import com.elasticpath.plugin.payment.dto.impl.AddressDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.CardDetailsPaymentMethodImpl;
import com.elasticpath.plugin.payment.dto.impl.MoneyImpl;
import com.elasticpath.plugin.payment.dto.impl.OrderShipmentDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.OrderSkuDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.PayerAuthValidationValueDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.ShoppingCartDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.TokenPaymentMethodImpl;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.impl.AuthorizationTransactionRequestImpl;
import com.elasticpath.plugin.payment.transaction.impl.CaptureTransactionRequestImpl;
import com.elasticpath.service.payment.gateway.GiftCertificateAuthorizationRequest;
import com.elasticpath.service.payment.gateway.GiftCertificateCaptureRequest;

/**
 * Factory to create new DTO objects for payment gateway plugins.
 */
public final class PaymentGatewayDtoFactory {
	private static final PaymentGatewayDtoFactory FACTORY = new PaymentGatewayDtoFactory();
	
	public static PaymentGatewayDtoFactory getFactory() {
		return FACTORY;
	}
	
	/**
	 * Ensure that class is not instantiated.
	 */
	private PaymentGatewayDtoFactory() {
	}
	
	/**
	 * Instantiate AddressDTO.
	 * @return instance of AddressDTO.
	 */
	public AddressDto createAddressDto() {
		return new AddressDtoImpl();
	}
	
	/**
	 * Instantiate OrderSkuDto.
	 * @return instance of OrderSkuDto.
	 */
	public OrderSkuDto createOrderSkuDto() {
		return new OrderSkuDtoImpl();
	}
	
	/**
	 * Instantiate OrderPaymentDto.
	 * @return instance of OrderPaymentDto.
	 */
	public OrderPaymentDto createGiftCertificateOrderPaymentDto() {
		OrderPaymentDto orderPaymentDto = new GiftCertificateOrderPaymentDtoImpl();
		
		PayerAuthValidationValueDto payerAuthValidationValueDto = new PayerAuthValidationValueDtoImpl();
		orderPaymentDto.setPayerAuthValidationValueDto(payerAuthValidationValueDto);
		
		return orderPaymentDto;
	}
	
	/**
	 * Instantiate OrderShipmentDto.
	 * @return instance of OrderShipmentDto.
	 */
	public OrderShipmentDto createOrderShipmentDto() {
		return new OrderShipmentDtoImpl();
	}
	
	/**
	 * Instantiate ShoppingCartDto.
	 * @return instance of ShoppingCartDto.
	 */
	public ShoppingCartDto createShoppingCartDto() {
		return new ShoppingCartDtoImpl();
	}
	
	/**
	 * Instantiate Money.
	 * @return instance of {@link Money}.
	 */
	public Money createMoney() {
		return new MoneyImpl();
	}
	
	/**
	 * Instantiate CardDetailsPaymentMethod.
	 *
	 * @return instance of {@link CardDetailsPaymentMethod}.
	 */
	public CardDetailsPaymentMethod createCardDetailsPaymentMethod() {
		return new CardDetailsPaymentMethodImpl();
	}
	
	/**
	 * Instantiates a new {@link AuthorizationTransactionRequest}.
	 *
	 * @return instance of {@link AuthorizationTransactionRequest}
	 */
	public AuthorizationTransactionRequest createAuthorizationTransactionRequest() {
		return new AuthorizationTransactionRequestImpl();
	}
	
	/**
	 * Instantiates a new {@link CaptureTransactionRequest}.
	 *
	 * @return instance of {@link CaptureTransactionRequest}
	 */
	public CaptureTransactionRequest createCaptureTransactionRequest() {
		return new CaptureTransactionRequestImpl();
	}
	
	/**
	 * Instantiates a new {@link GiftCertificateAuthorizationRequest}.
	 *
	 * @return instance of {@link GiftCertificateAuthorizationRequest}
	 */
	public GiftCertificateAuthorizationRequest createGiftCertificateAuthorizationRequest() {
		return new GiftCertificateAuthorizationRequestImpl();
	}
	
	/**
	 * Instantiates a new {@link GiftCertificateCaptureRequest}.
	 *
	 * @return instance of {@link GiftCertificateCaptureRequest}
	 */
	public GiftCertificateCaptureRequest createGiftCertificateCaptureRequest() {
		return new GiftCertificateCaptureRequestImpl();
	}
	
	/**
	 * Instantiates a new {@link TokenPaymentMethod}.
	 * @param token the token
	 * @return instance of {@link TokenPaymentMethod}
	 */
	public TokenPaymentMethod createTokenPaymentMethod(final String token) {
		return new TokenPaymentMethodImpl(token);
	}
}
