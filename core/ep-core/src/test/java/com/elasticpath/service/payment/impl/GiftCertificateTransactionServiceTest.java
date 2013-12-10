/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.payment.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.hamcrest.Description;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.GiftCertificateCodeGenerator;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.payment.GiftCertificateTransaction;
import com.elasticpath.domain.payment.impl.GiftCertificateTransactionImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.plugin.payment.dto.Money;
import com.elasticpath.plugin.payment.dto.impl.MoneyImpl;
import com.elasticpath.plugin.payment.exceptions.GiftCertificateException;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.payment.GiftCertificateTransactionService;
import com.elasticpath.service.payment.gateway.GiftCertificateAuthorizationRequest;
import com.elasticpath.service.payment.gateway.GiftCertificateCaptureRequest;
import com.elasticpath.service.payment.gateway.GiftCertificateOrderPaymentDto;
import com.elasticpath.service.payment.gateway.impl.GiftCertificateAuthorizationRequestImpl;
import com.elasticpath.service.payment.gateway.impl.GiftCertificateCaptureRequestImpl;
import com.elasticpath.service.payment.gateway.impl.GiftCertificateOrderPaymentDtoImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test case for <code>GiftCertificateTransactionService</code>.
 */
public class GiftCertificateTransactionServiceTest {

	private static final String INVALID_AUTH_CODE = "invalid auth code";

	private static final String TEST_CURRENCY = "USD";

	private static final String GIFT_CERTIFICATE_TRANSACTIONS = "GIFT_CERTIFICATE_TRANSACTIONS";

	private static final BigDecimal BALANCE = new BigDecimal(51);

	private static final BigDecimal CAPTURE = new BigDecimal(34);

	private static final BigDecimal AUTH_CAPTURED = new BigDecimal(35);

	private static final BigDecimal AUTH_REVERTED = new BigDecimal(25);

	private static final BigDecimal REGULAR_AUTH = new BigDecimal(15);

	private static final String AUTH_CODE3 = "12345";

	private static final String AUTH_CODE2 = "1234";

	private static final String AUTH_CODE1 = "123";

	private static final int GC_PURCHASE = 100;

	private static final long GC_UID = 7L;

	private final List<GiftCertificateTransaction> dataBase = new ArrayList<GiftCertificateTransaction>();

	private GiftCertificateTransactionService transactionService;

	private GiftCertificate giftCertificate;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PersistenceEngine mockPersistenceEngine;

	private BeanFactory beanFactory;

	private BeanFactoryExpectationsFactory expectationsFactory;

	private TimeService mockTimeService;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {

		giftCertificate = new GiftCertificateImpl();
		giftCertificate.setPurchaseAmount(new BigDecimal(GC_PURCHASE));
		giftCertificate.setUidPk(GC_UID);

		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		mockGiftCertificateTransactionService();
		mockFillDB();

		// transactionService.setElasticPath(getElasticPath());
		context.checking(new Expectations() {
			{

				allowing(beanFactory).getBean(ContextIdNames.GIFT_CERTIFICATE_TRANSACTION);
				will(returnValue(new GiftCertificateTransactionImpl()));
			}
		});
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}


	/**
	 * Test successfully capturing a gift certificate amount.
	 */
	@Test
	public final void testSuccessfulCapture() {
		mockGetGCTransactions();

		GiftCertificateCaptureRequest captureRequest = new GiftCertificateCaptureRequestImpl();
		captureRequest.setGiftCertificate(giftCertificate);
		Money captureMoney = new MoneyImpl(REGULAR_AUTH, TEST_CURRENCY);
		captureRequest.setMoney(captureMoney);
		captureRequest.setAuthorizationCode(AUTH_CODE1);
		
		transactionService.capture(captureRequest);
		
		assertEquals(BALANCE, transactionService.getBalance(giftCertificate));
	}
	
	/**
	 * Test capture with an invalid authorization code throws {@link GiftCertificateException}.
	 */
	@Test(expected = GiftCertificateException.class)
	public void testCaptureWithInvalidAuthCode() {
		GiftCertificateCaptureRequest captureRequest = new GiftCertificateCaptureRequestImpl();
		captureRequest.setGiftCertificate(giftCertificate);
		Money captureMoney = new MoneyImpl(REGULAR_AUTH, TEST_CURRENCY);
		captureRequest.setMoney(captureMoney);
		captureRequest.setAuthorizationCode(INVALID_AUTH_CODE);
		
		mockGetGCTransactions();
		
		transactionService.capture(captureRequest);
	}
	
	/**
	 * Test that a multiple follow on captures on an authorization throws a {@link GiftCertificateException}.
	 */
	@Test(expected = GiftCertificateException.class)
	public void testCaptureOfAlreadyCapturedTransaction() {
		mockGetGCTransactions();

		GiftCertificateCaptureRequest captureRequest = new GiftCertificateCaptureRequestImpl();
		captureRequest.setGiftCertificate(giftCertificate);
		Money captureMoney = new MoneyImpl(REGULAR_AUTH, TEST_CURRENCY);
		captureRequest.setMoney(captureMoney);
		captureRequest.setAuthorizationCode(AUTH_CODE1);
		
		try {
			transactionService.capture(captureRequest);
		} catch (GiftCertificateException e) {
			fail("The first transaction should succeed.");
		}
		
		transactionService.capture(captureRequest);
	}
	
	/**
	 * Test successfully pre-authorizing a gift certificate amount.
	 */
	@Test
	public final void testSuccessfulPreAuthorize() {
		mockGetGCTransactions();

		GiftCertificateAuthorizationRequest authorizationRequest = new GiftCertificateAuthorizationRequestImpl();
		authorizationRequest.setGiftCertificate(giftCertificate);
		Money authorizationMoney = new MoneyImpl(REGULAR_AUTH, TEST_CURRENCY);
		authorizationRequest.setMoney(authorizationMoney);
		
		transactionService.preAuthorize(authorizationRequest, null);
		
		assertEquals(BALANCE.subtract(REGULAR_AUTH), transactionService.getBalance(giftCertificate));
	}

	/**
	 * Test a successful reverse pre-authorization of a gift certificate amount.
	 */
	@Test
	public final void testSuccessfulReversePreAuthorization() {
		mockGetGCTransactions();

		GiftCertificateOrderPaymentDto orderPayment = new GiftCertificateOrderPaymentDtoImpl();
		orderPayment.setGiftCertificate(giftCertificate);
		orderPayment.setAmount(REGULAR_AUTH);
		orderPayment.setAuthorizationCode(AUTH_CODE1);
		
		transactionService.reversePreAuthorization(orderPayment);
		// now auth1 is captured
		assertEquals(BALANCE.add(REGULAR_AUTH), transactionService.getBalance(giftCertificate));
	}
	
	/**
	 * Test reverse preauthorization with an invalid authorization code throws a {@link GiftCertificateException}.
	 */
	@Test(expected = GiftCertificateException.class)
	public void testReversePreAuthorizationWithInvalidAuthCode() {
		mockGetGCTransactions();

		GiftCertificateOrderPaymentDto orderPayment = new GiftCertificateOrderPaymentDtoImpl();
		orderPayment.setGiftCertificate(giftCertificate);
		orderPayment.setAmount(REGULAR_AUTH);
		orderPayment.setAuthorizationCode(INVALID_AUTH_CODE);

		transactionService.reversePreAuthorization(orderPayment);
	}
	
	/**
	 * Test reverse pre-authorization with an amount not equal to original amount authorized throws a {@link GiftCertificateException}.
	 */
	@Test(expected = GiftCertificateException.class)
	public void testReversePreAuthorizationWithInvalidAmount() {
		mockGetGCTransactions();
		BigDecimal invalidAuthorizationAmount = REGULAR_AUTH.subtract(BigDecimal.ONE);

		GiftCertificateOrderPaymentDto orderPayment = new GiftCertificateOrderPaymentDtoImpl();
		orderPayment.setGiftCertificate(giftCertificate);
		orderPayment.setAmount(invalidAuthorizationAmount);
		orderPayment.setAuthorizationCode(AUTH_CODE1);
		
		transactionService.reversePreAuthorization(orderPayment);
	}
	
	/**
	 * Test successfully retrieving a balance on a gift certificate.
	 */
	@Test
	public final void testGetBalance() {
		mockGetGCTransactions();
		assertEquals(BALANCE, transactionService.getBalance(giftCertificate));
	}

	private void mockGetGCTransactions() {
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(GIFT_CERTIFICATE_TRANSACTIONS, GC_UID);
				will(new Action() {
					@Override
					public Object invoke(final Invocation invocation) throws Throwable {
						return getAllGiftCertificateTransactions(GC_UID);
					}

					@Override
					public void describeTo(final Description description) {
						description.appendText("getAllGiftCertificateTransactions");
					}
				});
			}
		});
	}

	private void mockGiftCertificateTransactionService() {
		final GiftCertificateCodeGenerator mockGiftCertificateCodeGenerator = context.mock(GiftCertificateCodeGenerator.class);
		context.checking(new Expectations() {
			{
				allowing(mockGiftCertificateCodeGenerator).generateCode();
				will(returnValue("GCCODE"));
			}
		});

		transactionService = new GiftCertificateTransactionServiceImpl() {
			@Override
			public GiftCertificateTransaction add(final GiftCertificateTransaction giftCertificateTransaction) throws EpServiceException {
				addToDB(giftCertificateTransaction);
				return giftCertificateTransaction;
			}
			@Override
			protected GiftCertificateTransactionService getGiftCertificateTransactionService() {
				return this;
			}

			@Override
			public GiftCertificateCodeGenerator getGiftCertificateCodeGenerator() {
				return mockGiftCertificateCodeGenerator;
			}
		};
		transactionService.setPersistenceEngine(mockPersistenceEngine);

		mockTimeService = context.mock(TimeService.class);
		context.checking(new Expectations() {
			{
				allowing(mockTimeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});
		transactionService.setTimeService(mockTimeService);
	}

	private void mockFillDB() {
		// auth transaction, not yet captured, nor reverted.
		GiftCertificateTransaction authTransaction = new GiftCertificateTransactionImpl();
		authTransaction.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		authTransaction.setAuthorizationCode(AUTH_CODE1);
		authTransaction.setAmount(REGULAR_AUTH);
		addToDB(authTransaction);

		// auth transaction, but reverted.
		GiftCertificateTransaction authTransactionWithReverse = new GiftCertificateTransactionImpl();
		authTransactionWithReverse.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		authTransactionWithReverse.setAuthorizationCode(AUTH_CODE2);
		authTransactionWithReverse.setAmount(AUTH_REVERTED);
		addToDB(authTransactionWithReverse);

		GiftCertificateTransaction reverseTransaction = new GiftCertificateTransactionImpl();
		reverseTransaction.setTransactionType(OrderPayment.REVERSE_AUTHORIZATION);
		reverseTransaction.setAuthorizationCode(AUTH_CODE2);
		reverseTransaction.setAmount(AUTH_REVERTED);
		addToDB(reverseTransaction);

		// auth transaction of $35, but it will be captured only 34
		GiftCertificateTransaction authTransactionWithCapture = new GiftCertificateTransactionImpl();
		authTransactionWithCapture.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		authTransactionWithCapture.setAuthorizationCode(AUTH_CODE3);
		authTransactionWithCapture.setAmount(AUTH_CAPTURED);
		addToDB(authTransactionWithCapture);

		GiftCertificateTransaction captureTransaction = new GiftCertificateTransactionImpl();
		captureTransaction.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		captureTransaction.setAuthorizationCode(AUTH_CODE3);
		captureTransaction.setAmount(CAPTURE);
		addToDB(captureTransaction);

	}

	private void addToDB(final GiftCertificateTransaction giftCertificateTransaction) {
		giftCertificateTransaction.setGiftCertificate(giftCertificate);
		dataBase.add(giftCertificateTransaction);
	}

	private List<GiftCertificateTransaction> getAllGiftCertificateTransactions(final long giftCertificateUid) {
		List<GiftCertificateTransaction> list = new ArrayList<GiftCertificateTransaction>();
		for (GiftCertificateTransaction transaction : dataBase) {
			if (transaction.getGiftCertificate().getUidPk() == giftCertificateUid) {
				list.add(transaction);
			}
		}
		return list;
	}
}
