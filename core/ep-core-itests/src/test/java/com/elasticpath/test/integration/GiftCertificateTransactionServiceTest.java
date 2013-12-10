package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.plugin.payment.dto.impl.MoneyImpl;
import com.elasticpath.plugin.payment.exceptions.GiftCertificateException;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.payment.GiftCertificateTransactionService;
import com.elasticpath.service.payment.gateway.GiftCertificateAuthorizationRequest;
import com.elasticpath.service.payment.gateway.GiftCertificateCaptureRequest;
import com.elasticpath.service.payment.gateway.GiftCertificateOrderPaymentDto;
import com.elasticpath.service.payment.gateway.impl.GiftCertificateAuthorizationRequestImpl;
import com.elasticpath.service.payment.gateway.impl.GiftCertificateCaptureRequestImpl;
import com.elasticpath.service.payment.gateway.impl.GiftCertificateOrderPaymentDtoImpl;
import com.elasticpath.service.payment.impl.GiftCertificateTransactionResponse;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.impl.CreateGiftCertificatesCheckoutAction;
import com.elasticpath.test.db.DbTestCase;

/**
 * Integration tests for Gift Certificate Transactions.
 */
public class GiftCertificateTransactionServiceTest extends DbTestCase {

	private static final String INVALID_AUTH_CODE = "blah-blah";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final BigDecimal BALANCE = new BigDecimal("51.00");

	private static final String AUTH_CODE1 = "123";

	private static final BigDecimal REGULAR_AUTH = new BigDecimal("15.00");

	/** The main object under test. */
	@Autowired
	private GiftCertificateTransactionService transactionService;

	@Autowired
	private GiftCertificateService giftCertificateService;

	private GiftCertificate giftCertificate;

	private GiftCertificateOrderPaymentDto orderPayment;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		giftCertificate = persisterFactory.getStoreTestPersister().persistGiftCertificate(scenario.getStore(), BALANCE);
		orderPayment = createOrderPayment("qwe@qwe.ru");
		orderPayment.setGiftCertificate(giftCertificate);
		orderPayment.setAmount(REGULAR_AUTH);
	}

	/**
	 * Asserts that a gift certificate in the DB can be rolled back using the CreateGiftCertificatesCheckoutAction.
	 */
	@DirtiesDatabase
	@Test
	public void testRollback() {
		final CheckoutActionContext mockCheckoutActionContext = context.mock(CheckoutActionContext.class);
		final Order mockOrder = context.mock(Order.class);
		final OrderSku mockOrderSku = context.mock(OrderSku.class);
		
		final String gcCode = giftCertificate.getGiftCertificateCode();
		final Set<OrderSku> orderSkus = new HashSet<OrderSku>();
		orderSkus.add(mockOrderSku);
		
		context.checking(new Expectations() {
			{
				oneOf(mockCheckoutActionContext).getOrder();
				will(returnValue(mockOrder));
				oneOf(mockOrder).getOrderSkus();
				will(returnValue(orderSkus));

				oneOf(mockOrderSku).getProductTypeName();
				will(returnValue(GiftCertificate.KEY_PRODUCT_TYPE));
				oneOf(mockOrderSku).getFieldValue(GiftCertificate.KEY_CODE);
				will(returnValue(gcCode));
				allowing(mockOrderSku).setFieldValue(with(any(String.class)), with(aNull(String.class)));
			}
		});
		
		assertNotNull(giftCertificateService.findByGiftCertificateCode(gcCode));
		
		CreateGiftCertificatesCheckoutAction createGCAction = new CreateGiftCertificatesCheckoutAction();
		createGCAction.setGiftCertificateService(giftCertificateService);
		createGCAction.rollback(mockCheckoutActionContext);
		
		assertNull(giftCertificateService.findByGiftCertificateCode(gcCode));
	}

	/**
	 * Tests a successful pre-authorization of a gift certificate amount.
	 */
	@DirtiesDatabase
	@Test
	public void testPreAuthorize() {
		GiftCertificateAuthorizationRequest authorizationRequest = createAuthorizationRequestFromOrderPayment(orderPayment);
		
		transactionService.preAuthorize(authorizationRequest, null);
		assertEquals(BALANCE.subtract(REGULAR_AUTH), transactionService.getBalance(giftCertificate));
	}


	/**
	 * Tests a successful capture of a gift certificate amount.
	 */
	@DirtiesDatabase
	@Test
	public void testSuccessfulCapture() {
		orderPayment.setAuthorizationCode(AUTH_CODE1);
		GiftCertificateAuthorizationRequest authorizationRequest = createAuthorizationRequestFromOrderPayment(orderPayment);
		GiftCertificateTransactionResponse response = transactionService.preAuthorize(authorizationRequest, null);
		orderPayment.setAuthorizationCode(response.getAuthorizationCode());
		orderPayment.setReferenceId(response.getGiftCertificateCode());

		GiftCertificateCaptureRequest followOnCaptureRequest = createCaptureRequestFromOrderPayment(orderPayment);
		transactionService.capture(followOnCaptureRequest);

		assertEquals(BALANCE.subtract(REGULAR_AUTH), transactionService.getBalance(giftCertificate));
		}
	
	/**
	 * Tests that capturing with an invalid authorization code throws a {@link GiftCertificateException}.
	 */
	@DirtiesDatabase
	@Test(expected = GiftCertificateException.class)
	public void testCaptureWithInvalidAuthorizationCode() {
		orderPayment.setAuthorizationCode(INVALID_AUTH_CODE);
		
		GiftCertificateCaptureRequest captureRequest = createCaptureRequestFromOrderPayment(orderPayment);

		transactionService.capture(captureRequest);
	}

	/**
	 * Tests that attempting to re-capture an authorized amount that was previously captured throws a {@link GiftCertificateException}.
	 */
	@DirtiesDatabase
	@Test(expected = GiftCertificateException.class)
	public void testCaptureOfAlreadyCapturedTransaction() {
		orderPayment.setAuthorizationCode(AUTH_CODE1);
		GiftCertificateAuthorizationRequest authorizationRequest = createAuthorizationRequestFromOrderPayment(orderPayment);
		GiftCertificateTransactionResponse response = transactionService.preAuthorize(authorizationRequest, null);
		orderPayment.setAuthorizationCode(response.getAuthorizationCode());
		orderPayment.setReferenceId(response.getGiftCertificateCode());

		GiftCertificateCaptureRequest captureRequest = createCaptureRequestFromOrderPayment(orderPayment);
		try {
			transactionService.capture(captureRequest);
		} catch (GiftCertificateException e) {
			fail("The fist capture should be successful.");
		}
		
		transactionService.capture(captureRequest);
	}

	/**
	 * Test a successful reverse pre-authorization of a gift certificate amount.
	 */
	@DirtiesDatabase
	@Test
	public void testSuccessfulReversePreAuthorization() {
		orderPayment.setAuthorizationCode(AUTH_CODE1);
		GiftCertificateAuthorizationRequest authorizationRequest = createAuthorizationRequestFromOrderPayment(orderPayment);

		GiftCertificateTransactionResponse response = transactionService.preAuthorize(authorizationRequest, null);
		
		orderPayment.setAuthorizationCode(response.getAuthorizationCode());
		orderPayment.setReferenceId(response.getGiftCertificateCode());

		orderPayment.setAmount(REGULAR_AUTH);
			transactionService.reversePreAuthorization(orderPayment);

		assertEquals(BALANCE, transactionService.getBalance(giftCertificate));
		}

	/**
	 * Test that a reverse pre-authorization with an invalid authorization code throws a {@link GiftCertificateException}.
	 */
	@DirtiesDatabase
	@Test(expected = GiftCertificateException.class)
	public void testReversePreAuthorizationWithInvalidAuthorizationCode() {
		orderPayment.setAuthorizationCode(INVALID_AUTH_CODE);

			transactionService.reversePreAuthorization(orderPayment);
		}

	/**
	 * Test that a reverse pre-authorization with an invalid amount throws a {@link GiftCertificateException}.
	 */
	@DirtiesDatabase
	@Test(expected = GiftCertificateException.class)
	public void testReversePreAuthorizationWithInvalidAmount() {
		orderPayment.setAuthorizationCode(AUTH_CODE1);
		GiftCertificateAuthorizationRequest authorizationRequest = createAuthorizationRequestFromOrderPayment(orderPayment);

		GiftCertificateTransactionResponse response = transactionService.preAuthorize(authorizationRequest, null);
		
		orderPayment.setAuthorizationCode(response.getAuthorizationCode());
		orderPayment.setReferenceId(response.getGiftCertificateCode());
		
		BigDecimal invalidAmount = REGULAR_AUTH.subtract(BigDecimal.ONE);
		orderPayment.setAmount(invalidAmount); 
		transactionService.reversePreAuthorization(orderPayment);
	}

	private GiftCertificateOrderPaymentDto createOrderPayment(final String customerEmail) {
		GiftCertificateOrderPaymentDto orderPayment = new GiftCertificateOrderPaymentDtoImpl();		
		orderPayment.setCardHolderName("test test");
		orderPayment.setCardType("001");
		orderPayment.setCurrencyCode("USD");
		orderPayment.setEmail(customerEmail);
		orderPayment.setExpiryMonth("09");
		orderPayment.setExpiryYear("10");
		orderPayment.setCvv2Code("1111");
		orderPayment.setUnencryptedCardNumber("4111111111111111");
		return orderPayment;
	}

	private GiftCertificateCaptureRequest createCaptureRequestFromOrderPayment(final GiftCertificateOrderPaymentDto orderPayment) {
		GiftCertificateCaptureRequest captureRequest = new GiftCertificateCaptureRequestImpl();
		captureRequest.setGiftCertificate(orderPayment.getGiftCertificate());
		captureRequest.setMoney(new MoneyImpl(orderPayment.getAmount(), orderPayment.getCurrencyCode()));
		captureRequest.setAuthorizationCode(orderPayment.getAuthorizationCode());
		return captureRequest;
	}
	
	private GiftCertificateAuthorizationRequest createAuthorizationRequestFromOrderPayment(final GiftCertificateOrderPaymentDto orderPayment) {
		GiftCertificateAuthorizationRequest authorizationRequest = new GiftCertificateAuthorizationRequestImpl();
		authorizationRequest.setGiftCertificate(orderPayment.getGiftCertificate());
		authorizationRequest.setMoney(new MoneyImpl(orderPayment.getAmount(), orderPayment.getCurrencyCode()));
		return authorizationRequest;
	}
}
