package com.elasticpath.service.payment.gateway.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.impl.AddressDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.CardDetailsPaymentMethodImpl;
import com.elasticpath.plugin.payment.dto.impl.OrderPaymentDtoImpl;
import com.elasticpath.plugin.payment.exceptions.CardDeclinedException;
import com.elasticpath.plugin.payment.exceptions.CardErrorException;
import com.elasticpath.plugin.payment.exceptions.CardExpiredException;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;
import com.elasticpath.plugin.payment.transaction.PaymentTransactionResponse;
import com.elasticpath.plugin.payment.transaction.impl.AuthorizationTransactionRequestImpl;
import com.elasticpath.plugin.payment.transaction.impl.CaptureTransactionRequestImpl;
import com.elasticpath.plugin.payment.transaction.impl.CaptureTransactionResponseImpl;


/**
 * Test for {@link NullPaymentGatewayPluginImpl}.
 */
public class NullPaymentGatewayPluginImplTest {
	private static final String TEST_REQUEST_TOKEN = "testRequestToken";
	private NullPaymentGatewayPluginImpl nullPaymentGatewayPlugin;
	private OrderPaymentDto orderPaymentDto;
	
	/**
	 * Initialize object under test.
	 */
	@Before
	public void initializeObjectUnderTest() { 
		nullPaymentGatewayPlugin = new NullPaymentGatewayPluginImpl();
		NullPaymentGatewayPluginImpl.setFailOnCapture(false);
		NullPaymentGatewayPluginImpl.setFailOnPreAuthorize(false);
		NullPaymentGatewayPluginImpl.setFailOnReversePreAuthorization(false);
		NullPaymentGatewayPluginImpl.setFailOnSale(false);
		
		orderPaymentDto = getValidOrderPaymentDto();
	}

	/**
	 * Test pre authorize.
	 */
	@Test
	public void testPreAuthorize() {
		AuthorizationTransactionRequest authorizationRequest = createTestAuthorizationRequest("");
		PaymentTransactionResponse response = nullPaymentGatewayPlugin.preAuthorize(authorizationRequest, getValidBillingAddress(), null);
		assertPaymentIsAuthorized(response);
	}

	/**
	 * Test reverse pre authorization.
	 */
	@Test
	public void testReversePreAuthorization() {
		nullPaymentGatewayPlugin.reversePreAuthorization(orderPaymentDto);
		assertNotNull(orderPaymentDto.getReferenceId());
	}
		
	/**
	 * Test void capture or credit.
	 */
	@Test
	public void testVoidCaptureOrCredit() {
		nullPaymentGatewayPlugin.voidCaptureOrCredit(orderPaymentDto);
		assertNotNull(orderPaymentDto.getReferenceId());
	}
	
	/**
	 * Test capture.
	 */
	@Test
	public void testCapture() {
		CaptureTransactionRequest captureRequest = createTestCaptureTransactionRequest();
		
		CaptureTransactionResponse expectedCaptureResponse = new CaptureTransactionResponseImpl();
		expectedCaptureResponse.setRequestToken(TEST_REQUEST_TOKEN);
		
		CaptureTransactionResponse captureResponse = nullPaymentGatewayPlugin.capture(captureRequest);
		
		assertEquals("The capture response returned should be the same as expected capture response.", 
				expectedCaptureResponse, captureResponse);
	}	
	
	/**
	 * Test sale.
	 */
	@Test
	public void testSale() {
		nullPaymentGatewayPlugin.sale(orderPaymentDto, getValidBillingAddress(), null);
		assertPaymentIsAuthorized(orderPaymentDto);
	}	
	
	/**
	 * Test refund.
	 */
	@Test
	public void testRefund() {
		nullPaymentGatewayPlugin.refund(orderPaymentDto, getValidBillingAddress());
		assertPaymentIsAuthorized(orderPaymentDto);
	}
	
	/**
	 * Ensure supported card types not null.
	 */
	@Test
	public void ensureSupportedCardTypesNotNull() {
		List<String> responseList = nullPaymentGatewayPlugin.getSupportedCardTypes();
		assertNotNull("Supported Card Types should not be empty. ", responseList);
		assertFalse("Supported Card Types should not be empty. ", responseList.isEmpty());
	}
	
	/**
	 * Ensure exception thrown when pre authorization fail flag set.
	 */
	@Test(expected = CardExpiredException.class)
	public void ensureExceptionThrownWhenPreAuthorizationFailFlagSet() {
		NullPaymentGatewayPluginImpl.setFailOnPreAuthorize(true);
		AuthorizationTransactionRequest request = createTestAuthorizationRequest("");
		nullPaymentGatewayPlugin.preAuthorize(request, getValidBillingAddress(), null);
	}
	
	/**
	 * Ensure exception thrown when capture fail flag set.
	 */
	@Test(expected = CardErrorException.class)
	public void ensureExceptionThrownWhenCaptureFailFlagSet() {
		NullPaymentGatewayPluginImpl.setFailOnCapture(true);
		CaptureTransactionRequest request = createTestCaptureTransactionRequest();
		nullPaymentGatewayPlugin.capture(request);
	}	

	/**
	 * Ensure exception thrown when reverse pre authorization fail flag set.
	 */
	@Test(expected = CardErrorException.class)
	public void ensureExceptionThrownWhenReversePreAuthorizationFailFlagSet() {
		NullPaymentGatewayPluginImpl.setFailOnReversePreAuthorization(true);
		nullPaymentGatewayPlugin.reversePreAuthorization(orderPaymentDto);
	}		
	
	/**
	 * Ensure exception thrown when sale fail flag set.
	 */
	@Test(expected = CardErrorException.class)
	public void ensureExceptionThrownWhenSaleFailFlagSet() {
		NullPaymentGatewayPluginImpl.setFailOnSale(true);
		nullPaymentGatewayPlugin.sale(orderPaymentDto, getValidBillingAddress(), null);
	}		
	
	/**
	 * Ensure exception thrown from pre authorization on expired card.
	 */
	@Test(expected = CardExpiredException.class)
	public void ensureExceptionThrownFromPreAuthorizationOnExpiredCard() {
		AuthorizationTransactionRequest request = createTestAuthorizationRequest("EXP_AUTH");
		nullPaymentGatewayPlugin.preAuthorize(request, getValidBillingAddress(), null);
	}
	
	/**
	 * Ensure exception thrown from pre authorization on declined card.
	 */
	@Test(expected = CardDeclinedException.class)
	public void ensureExceptionThrownFromPreAuthorizationOnDeclinedCard() {
		AuthorizationTransactionRequest request = createTestAuthorizationRequest("DEC_AUTH");
		nullPaymentGatewayPlugin.preAuthorize(request, getValidBillingAddress(), null);
	}
	
	/**
	 * Ensure exception thrown from pre authorization on card error.
	 */
	@Test(expected = CardErrorException.class)
	public void ensureExceptionThrownFromPreAuthorizationOnCardError() {
		AuthorizationTransactionRequest request = createTestAuthorizationRequest("COM_AUTH");
		nullPaymentGatewayPlugin.preAuthorize(request, getValidBillingAddress(), null);
	}	
	
	/**
	 * Ensure exception thrown from reverse pre authorization on expired card.
	 */
	@Test(expected = CardExpiredException.class)
	public void ensureExceptionThrownFromReversePreAuthorizationOnExpiredCard() {
		orderPaymentDto.setCardHolderName("EXP_REV_AUTH");
		nullPaymentGatewayPlugin.reversePreAuthorization(orderPaymentDto);
	}
	
	/**
	 * Ensure exception thrown from reverse pre authorization on declined card.
	 */
	@Test(expected = CardDeclinedException.class)
	public void ensureExceptionThrownFromReversePreAuthorizationOnDeclinedCard() {
		orderPaymentDto.setCardHolderName("DEC_REV_AUTH");
		nullPaymentGatewayPlugin.reversePreAuthorization(orderPaymentDto);
	}
	
	/**
	 * Ensure exception thrown from reverse pre authorization on card error.
	 */
	@Test(expected = CardErrorException.class)
	public void ensureExceptionThrownFromReversePreAuthorizationOnCardError() {
		orderPaymentDto.setCardHolderName("COM_REV_AUTH");
		nullPaymentGatewayPlugin.reversePreAuthorization(orderPaymentDto);
	}	
	
	/**
	 * Ensure exception thrown from refund on expired card.
	 */
	@Test(expected = CardExpiredException.class)
	public void ensureExceptionThrownFromRefundOnExpiredCard() {
		orderPaymentDto.setCardHolderName("EXP_REFUND");
		nullPaymentGatewayPlugin.refund(orderPaymentDto, getValidBillingAddress());
	}
	
	/**
	 * Ensure exception thrown from refundn on declined card.
	 */
	@Test(expected = CardDeclinedException.class)
	public void ensureExceptionThrownFromRefundnOnDeclinedCard() {
		orderPaymentDto.setCardHolderName("DEC_REFUND");
		nullPaymentGatewayPlugin.refund(orderPaymentDto, getValidBillingAddress());
	}
	
	/**
	 * Ensure exception thrown from refund on card error.
	 */
	@Test(expected = CardErrorException.class)
	public void ensureExceptionThrownFromRefundOnCardError() {
		orderPaymentDto.setCardHolderName("COM_REFUND");
		nullPaymentGatewayPlugin.refund(orderPaymentDto, getValidBillingAddress());
	}	
	
	private void assertPaymentIsAuthorized(final OrderPaymentDto orderPaymentDto) {
		assertNotNull(orderPaymentDto.getReferenceId());
		assertNotNull(orderPaymentDto.getAuthorizationCode());
	}
	
	private void assertPaymentIsAuthorized(final PaymentTransactionResponse response) {
		assertNotNull(response.getReferenceId());
		assertNotNull(response.getAuthorizationCode());
	}

	private OrderPaymentDto getValidOrderPaymentDto() {
		OrderPaymentDto orderPaymentDto = new OrderPaymentDtoImpl();
		orderPaymentDto.setAmount(new BigDecimal("1.11"));
		return orderPaymentDto;
	}
	
	private AddressDto getValidBillingAddress() {
		return new AddressDtoImpl();
	}
	
	private AuthorizationTransactionRequest createTestAuthorizationRequest(final String cardHolderName) {
		AuthorizationTransactionRequest request = new AuthorizationTransactionRequestImpl();
		CardDetailsPaymentMethodImpl paymentMethod = new CardDetailsPaymentMethodImpl();
		paymentMethod.setCardHolderName(cardHolderName);
		
		request.setPaymentMethod(paymentMethod);
		return request;
	}
	
	private CaptureTransactionRequest createTestCaptureTransactionRequest() {
		CaptureTransactionRequest captureTransactionRequest = new CaptureTransactionRequestImpl();
		captureTransactionRequest.setRequestToken(TEST_REQUEST_TOKEN);
		return captureTransactionRequest;
	}
}
