package com.elasticpath.domain.event.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.MaskingOnlyCreditCardEncrypterImpl;
import com.elasticpath.commons.util.security.CreditCardEncrypter;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.event.OrderEventPaymentDetailFormatter;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;



/**
 * Test for {@link OrderEventGiftCertificateDetailsFormatter}.
 */
public class OrderEventGiftCertificateDetailsFormatterTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private final CreditCardEncrypter mockCreditCardEncrypter = new MaskingOnlyCreditCardEncrypterImpl();
	
	/**
	 * Sets the mock credit card encrypter in the bean factory.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		BeanFactoryExpectationsFactory bfef = new BeanFactoryExpectationsFactory(context, beanFactory);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.CARD_ENCRYPTER, mockCreditCardEncrypter);
	}
	
	/**
	 * Ensure correct format for gift certificate.
	 */
	@Test
	public void ensureCorrectFormatForGiftCertificate() {
		GiftCertificate giftCertificate = new GiftCertificateImpl();
		String testGiftCertificateCode = "testGiftCerficateCode";
		giftCertificate.setGiftCertificateCode(testGiftCertificateCode);
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setGiftCertificate(giftCertificate);
		OrderEventPaymentDetailFormatter formatter = new OrderEventGiftCertificateDetailsFormatter();
		
		String paymentDetails = formatter.formatPaymentDetails(orderPayment);
		assertThat("Details should have masked gift certificate code", paymentDetails, 
				containsString(mockCreditCardEncrypter.mask(testGiftCertificateCode)));
	}

}
