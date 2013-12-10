package com.elasticpath.notification.impl;

import org.apache.commons.mail.EmailException;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.EmailService;
import com.elasticpath.service.notification.NotificationData;
import com.elasticpath.service.notification.NotificationType;
import com.elasticpath.service.notification.impl.EmailNotificationServiceImpl;

/**
 * Test for com.elasticpath.service.notification.impl.EmailNotificationServiceImplTest.
 */
public class EmailNotificationServiceImplTest {
	
	private static final String ORDER_NUMBER = "order123";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private EmailNotificationServiceImpl emailNotificationService;

	private final EmailService emailService = context.mock(EmailService.class);
	
	/**
	 * Setup method.
	 * 
	 * @throws Exception general exception
	 */
	@Before
	public void setUp() throws Exception {
		emailNotificationService = new EmailNotificationServiceImpl();
	}

	/**
	 * Test send notification.
	 * @throws EmailException email exception
	 */
	@Test
	public void testSendNewOrderNotificationSuccess() throws EmailException {

		context.checking(new Expectations() {
			{
				allowing(emailService).sendOrderConfirmationEmail(ORDER_NUMBER);
				will(returnValue(true));
				
				allowing(emailService).sendGiftCertificateEmails(ORDER_NUMBER);
				will(returnValue(true));
			}
		});
		
		emailNotificationService.setEmailService(emailService);
				
		emailNotificationService.onNotify(NotificationType.NEW_ORDER_NOTIFICATION_TYPE, new NotificationData(ORDER_NUMBER, null, false));
	}
	
	/**
	 * Test send notification failure.
	 * This applies to the order return/exchange scenario where orderId will be null.
	 * @throws EmailException email exception.
	 */
	@Test (expected = EpServiceException.class)
	public void testSendNewOrderNotificationFailure() throws EmailException {		

		context.checking(new Expectations() {
			{
				allowing(emailService).sendOrderConfirmationEmail(null);
				will(throwException(new EmailException()));
				
				allowing(emailService).sendGiftCertificateEmails(null);
				will(throwException(new EmailException()));
			}
		});
		
		emailNotificationService.setEmailService(emailService);
		emailNotificationService.onNotify(NotificationType.NEW_ORDER_NOTIFICATION_TYPE, new NotificationData(null, null, false));
	}
	

	
	/**
	 * Test send notification.
	 * @throws EmailException email exception
	 */
	@Test
	public void testSendOrderShippedNotificationSuccess() throws EmailException {
		final NotificationData data = new NotificationData(null, null, true);
		
		context.checking(new Expectations() {
			{
				allowing(emailService).sendOrderShippedEmail(data);
				will(returnValue(true));
			}
		});
		
		emailNotificationService.setEmailService(emailService);
				
		emailNotificationService.onNotify(NotificationType.ORDER_SHIPPED_NOTIFICATION_TYPE, data);
		
	}
	
	/**
	 * Test send notification.
	 * @throws EmailException email exception
	 */
	@Test (expected = EpServiceException.class)
	public void testSendOrderShippedNotificationFailure() throws EmailException {
		final NotificationData data = new NotificationData(null, null, true);
		context.checking(new Expectations() {
			{
				allowing(emailService).sendOrderShippedEmail(data);
				will(throwException(new EmailException()));
			}
		});
		
		emailNotificationService.setEmailService(emailService);
				
		emailNotificationService.onNotify(NotificationType.ORDER_SHIPPED_NOTIFICATION_TYPE, data);
	}
}
