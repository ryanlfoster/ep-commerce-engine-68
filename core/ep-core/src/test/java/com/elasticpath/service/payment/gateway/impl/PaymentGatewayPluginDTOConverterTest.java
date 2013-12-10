package com.elasticpath.service.payment.gateway.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.misc.PayerAuthValidationValue;
import com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.service.payment.gateway.GiftCertificateOrderPaymentDto;

/**
 * Tests for the PaymentGatewayPluginDTOConverter to ensure that the Dozer configuration is working properly.
 */
public class PaymentGatewayPluginDTOConverterTest {
	/**
	 * Test converting a OrderPayment to an OrderPaymentDto.
	 */
	@Test
	public void convertOrderPaymentToOrderPaymentDto() {
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setAmount(BigDecimal.TEN);
		orderPayment.setAuthorizationCode("authCode");
		orderPayment.setCardHolderName("John Doe");
		orderPayment.setCardType("VISA");
		orderPayment.setCurrencyCode("USD");
		orderPayment.setCvv2Code("111");
		orderPayment.setEmail("john.doe@elasticpath.com");
		orderPayment.setExpiryMonth("11");
		orderPayment.setExpiryYear("2000");
		orderPayment.setReferenceId("referenceId");
		GiftCertificate giftCertificate = new GiftCertificateImpl();
		giftCertificate.setPurchaseAmount(BigDecimal.ONE);
		PayerAuthValidationValue payerAuthValidationValue = new PayerAuthValidationValueImpl();
		payerAuthValidationValue.setAAV("AAV");
		orderPayment.setPayerAuthValidationValue(payerAuthValidationValue);
		orderPayment.setGiftCertificate(giftCertificate);
		GiftCertificateOrderPaymentDto orderPaymentDto = (GiftCertificateOrderPaymentDto)
				PaymentGatewayPluginDtoConverter.toOrderPaymentDto(orderPayment);
		assertEquals(BigDecimal.TEN, orderPaymentDto.getAmount());
		assertEquals("authCode", orderPaymentDto.getAuthorizationCode());
		assertEquals("John Doe", orderPaymentDto.getCardHolderName());
		assertEquals("VISA", orderPaymentDto.getCardType());
		assertEquals("111", orderPaymentDto.getCvv2Code());
		assertEquals("john.doe@elasticpath.com", orderPaymentDto.getEmail());
		assertEquals("11", orderPaymentDto.getExpiryMonth());
		assertEquals("2000", orderPaymentDto.getExpiryYear());
		assertEquals("referenceId", orderPaymentDto.getReferenceId());
		assertEquals(BigDecimal.ONE, orderPaymentDto.getGiftCertificate().getPurchaseAmount());
		assertEquals("AAV", orderPaymentDto.getPayerAuthValidationValueDto().getAAV());
	}
}
