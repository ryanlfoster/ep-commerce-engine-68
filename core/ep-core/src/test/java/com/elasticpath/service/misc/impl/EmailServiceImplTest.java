/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.misc.impl;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.util.email.EmailComposer;
import com.elasticpath.commons.util.email.EmailSender;
import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.domain.misc.impl.EmailPropertiesImpl;
import com.elasticpath.service.notification.helper.EmailNotificationHelper;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;

/** Test cases for <code>EmailServiceImpl</code>. */
public class EmailServiceImplTest {

	private static final String EMAIL_FROM_NAME = "Me";
	private static final String EMAIL_FROM = "dong.yao@elasticpath.com";
	private static final String EMAIL_SUBJECT = "Test message";
	private static final String EMAIL_MESSAGE = "This is a simple test of commons-email";
	private static final String TEST_EMAIL_ADDRESS_TWO = "test2@loclahost.com";
	private static final String TEST_EMAIL_ADDRESS_ONE = "test1@loclahost.com";

	private static final String DUMMY_MESSAGE_ID = "messageId123";
	private static final String ORDER_NUMBER = "order123";

	private final Email email = new SimpleEmail();

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private EmailServiceImpl emailService;

	private final EmailProperties emailProperties = new EmailPropertiesImpl();

	private static final Logger LOG = Logger
			.getLogger(EmailServiceImplTest.class);

	/**
	 * Prepare for the tests.
	 * 
	 * @throws Exception
	 *             on error
	 */
	@Before
	public void setUp() throws Exception {
		emailService = new EmailServiceImpl() {
			protected boolean isEmailEnabled() {
				return true; 
			};
		};
	}

	/**
	 * Tests that sending an email to multiple users involves
	 * sending number of emails equal to the number of users the email is being sent to.
	 * 
	 * @throws EmailException when an email error occurs
	 * @throws AddressException when an {@link InternetAddress} cannot be constructed
	 */
	@Test
	public void testSendEmailToMultiplerUsers() throws EmailException, AddressException {
		EmailServiceImpl emailService = new EmailServiceImpl();
		
		final SettingsReader mockSettingsReader = context
				.mock(SettingsReader.class);

		final EmailComposer mockEmailComposer = context
				.mock(EmailComposer.class);

		// expectations
		context.checking(new Expectations() {
			{
				SettingValue settingValue = new SettingValueImpl();
				settingValue.setValue("true");
				oneOf(mockSettingsReader).getSettingValue(
						"COMMERCE/SYSTEM/emailEnabled");
				will(returnValue(settingValue));
			}
		});

		emailService.setSettingsReader(mockSettingsReader);
		emailService.setEmailComposer(mockEmailComposer);

		final Email testEmail = new SimpleEmail();
		testEmail.setFrom(EMAIL_FROM, EMAIL_FROM_NAME);
		testEmail.setSubject(EMAIL_SUBJECT);
		testEmail.setMsg(EMAIL_MESSAGE);

		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockEmailComposer).composeMessage(emailProperties);
				will(returnValue(testEmail));
			}
		});


		final EmailSender mockEmailSender = context.mock(EmailSender.class);
		emailService.setEmailSender(mockEmailSender);
		
		// expectations
		context.checking(new Expectations() {
			{
				testEmail.setTo(Arrays.asList(new InternetAddress(TEST_EMAIL_ADDRESS_ONE)));
				oneOf(mockEmailSender).sendEmail(testEmail);
				
				testEmail.setTo(Arrays.asList(new InternetAddress(TEST_EMAIL_ADDRESS_TWO)));
				oneOf(mockEmailSender).sendEmail(testEmail);
			}
		});

		emailService.sendMail(Arrays.asList(TEST_EMAIL_ADDRESS_ONE, TEST_EMAIL_ADDRESS_TWO), emailProperties);
		
	}

	/**
	 * Check that email is not sent when email is disabled.
	 * @throws EmailException if there is an unexpected failure.
	 */
	@Test
	public void testSendEmailWhenEmailSendingDisabled() throws EmailException {
		EmailServiceImpl service = new EmailServiceImpl() {
			protected boolean isEmailEnabled() {
				return false; 
			};
		};
		
		service.sendMail(emailProperties);
		
		// Nothing to assert, but nothing has been mocked so the
		// the test would fail if it actually did any work.
	}
	
	/**
	 * Check that email is not sent when email is disabled.
	 * @throws EmailException if there is an unexpected failure.
	 */
	@Test
	public void testSendOrderConfirmationEmailWhenEmailSendingDisabled() throws EmailException {
		EmailServiceImpl service = new EmailServiceImpl() {
			protected boolean isEmailEnabled() {
				return false; 
			};
		};

		final EmailNotificationHelper helper = context.mock(EmailNotificationHelper.class);
		service.setEmailNotificationHelper(helper);

		context.checking(new Expectations() {
			{
				allowing(helper).getOrderEmailProperties(ORDER_NUMBER);
			}
		});
		
		service.sendOrderConfirmationEmail(ORDER_NUMBER);
		
		// Nothing to assert, but nothing has been mocked so the
		// the test would fail if it actually did any work.
	}
	
	/**
	 * Check that email is not sent when email is disabled.
	 * @throws EmailException if there is an unexpected failure.
	 */
	@Test
	public void testSendGiftCertificateEmailsWhenEmailSendingDisabled() throws EmailException {
		EmailServiceImpl service = new EmailServiceImpl() {
			protected boolean isEmailEnabled() {
				return false; 
			};
		};
		final EmailNotificationHelper helper = context.mock(EmailNotificationHelper.class);
		service.setEmailNotificationHelper(helper);

		context.checking(new Expectations() {
			{
				allowing(helper).getGiftCertificateEmailProperties(ORDER_NUMBER);
			}
		});
		
		service.sendGiftCertificateEmails(ORDER_NUMBER);
		
		// Nothing to assert, but nothing has been mocked so the
		// the test would fail if it actually did any work.
	}
	
	/**
	 * Check that email is not sent when email is disabled.
	 * @throws EmailException if there is an unexpected failure.
	 */
	@Test
	public void testSendOrderShippedEmailWhenEmailSendingDisabled() throws EmailException {
		EmailServiceImpl service = new EmailServiceImpl() {
			protected boolean isEmailEnabled() {
				return false; 
			};
		};
		
		final EmailNotificationHelper helper = context.mock(EmailNotificationHelper.class);
		service.setEmailNotificationHelper(helper);

		context.checking(new Expectations() {
			{
				allowing(helper).getShipmentConfirmationEmailProperties(with(any(String.class)), with(any(String.class)));
			}
		});
		
		// Nothing to assert, but nothing has been mocked so the
		// the test would fail if it actually did any work.
	}
	
	/**
	 * Test send email.
	 * 
	 * @throws EmailException
	 *             an email exception
	 */
	@Test
	public void testSendEmail() throws EmailException {

		EmailServiceImpl emailService = new EmailServiceImpl() {
			protected boolean isEmailEnabled() {
				return true; 
			};
		};

		final EmailComposer mockEmailComposer = context.mock(EmailComposer.class);
		final EmailSender mockEmailSender = context.mock(EmailSender.class);

		emailService.setEmailComposer(mockEmailComposer);
		emailService.setEmailSender(mockEmailSender);

		final HtmlEmail testEmail = new HtmlEmail();

		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockEmailComposer).composeMessage(emailProperties);
				will(returnValue(testEmail));

				oneOf(mockEmailSender).sendEmail(testEmail);
			}
		});
				
		emailService.sendMail(emailProperties);
		
	}

	/**
	 * Check that email sending exceptions are wrapped and rethrown appropriately.
	 * @throws EmailException if there is an unexpected problem.
	 */
	@SuppressWarnings("PMD.EmptyCatchBlock")
	@Test
	public void testSendEmailExceptionHandling() throws EmailException {
	
		EmailServiceImpl emailService = new EmailServiceImpl() {
			protected boolean isEmailEnabled() {
				return true; 
			};
		};

		final EmailComposer mockEmailComposer = context.mock(EmailComposer.class);
		emailService.setEmailComposer(mockEmailComposer);

		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockEmailComposer).composeMessage(null);
				will(throwException(new UnsupportedOperationException()));
			}
		});
		
		try {
			emailService.sendMail(null);
			fail("Exception should have been thrown");
		} catch (EpServiceException expected) {
			// we expect this
		}
	}
	
	
	/**
	 * Test send order confirmation email success.
	 * 
	 * @throws EmailException
	 *             email exception
	 */
	@Test
	public void testSendOrderConfirmationEmailSuccess() throws EmailException {

		final EmailComposer emailComposer = context.mock(EmailComposer.class);
		final EmailSender emailSender = context.mock(EmailSender.class);
		final EmailNotificationHelper emailNotificationHelper = context
				.mock(EmailNotificationHelper.class);
		final EmailProperties mockEmailProperties = context
				.mock(EmailProperties.class);

		// Set expectations
		context.checking(new Expectations() {
			{
				allowing(emailNotificationHelper).getOrderEmailProperties(
						ORDER_NUMBER);
				will(returnValue(mockEmailProperties));

				allowing(emailComposer).composeMessage(mockEmailProperties);
				will(returnValue(email));

				allowing(emailSender).sendEmail(email);
				will(returnValue(DUMMY_MESSAGE_ID));
			}
		});

		emailService.setEmailComposer(emailComposer);
		emailService.setEmailNotificationHelper(emailNotificationHelper);
		emailService.setEmailSender(emailSender);

		try {

			emailService.sendOrderConfirmationEmail(ORDER_NUMBER);

		} catch (Exception e) {
			LOG.error("Exception ", e);
			fail("Send email failed");
		}
	}

	/**
	 * Test order confirmation email failure.
	 * 
	 * @throws EmailException
	 *             email exception
	 */
	@Test
	public void testOrderConfirmationEmailFailure() throws EmailException {

		final EmailComposer emailComposer = context.mock(EmailComposer.class);
		final EmailSender emailSender = context.mock(EmailSender.class);
		final EmailNotificationHelper emailNotificationHelper = context
				.mock(EmailNotificationHelper.class);
		final EmailProperties mockEmailProperties = context
				.mock(EmailProperties.class);

		// Set expectations
		context.checking(new Expectations() {
			{
				allowing(emailNotificationHelper).getOrderEmailProperties(
						ORDER_NUMBER);
				will(returnValue(mockEmailProperties));

				allowing(emailComposer).composeMessage(mockEmailProperties);
				will(returnValue(email));

				allowing(emailSender).sendEmail(email);
				will(throwException(new EmailException()));
			}
		});

		emailService.setEmailComposer(emailComposer);
		emailService.setEmailNotificationHelper(emailNotificationHelper);
		emailService.setEmailSender(emailSender);

		try {

			emailService.sendOrderConfirmationEmail(ORDER_NUMBER);

		} catch (EmailException ee) {
			LOG.error("Expected exception sending email ", ee);
		} catch (Exception e) {
			LOG.error("Unexpected exception sending mail ", e);
			fail("Send email threw unexpected exception");
		}
	}

	/**
	 * Test send order confirmation email success.
	 * 
	 * @throws EmailException
	 *             email exception
	 */
	@Test
	public void testSendGiftCertificateEmailSuccess() throws EmailException {
		final EmailComposer emailComposer = context.mock(EmailComposer.class);
		final EmailSender emailSender = context.mock(EmailSender.class);
		final EmailNotificationHelper emailNotificationHelper = context
				.mock(EmailNotificationHelper.class);
		final EmailProperties mockEmailProperties = context
				.mock(EmailProperties.class);

		final List<EmailProperties> emailPropertiesList = new ArrayList<EmailProperties>();
		emailPropertiesList.add(mockEmailProperties);

		context.checking(new Expectations() {
			{
				allowing(emailNotificationHelper).getGiftCertificateEmailProperties(ORDER_NUMBER);
				will(returnValue(emailPropertiesList));
				
				allowing(emailComposer).composeMessage(mockEmailProperties);
				will(returnValue(email));

				allowing(emailSender).sendEmail(email);
				will(returnValue(DUMMY_MESSAGE_ID));
			}
		});
		
		emailService.setEmailComposer(emailComposer);
		emailService.setEmailNotificationHelper(emailNotificationHelper);
		emailService.setEmailSender(emailSender);

		emailService.sendGiftCertificateEmails(ORDER_NUMBER);
		
	}
	
	/**
	 * Test send order shipped email success.
	 * 
	 * @throws EmailException
	 *             email exception
	 */
	@Test
	public void testSendOrderShippedEmailSuccess() throws EmailException {
		final String orderNumber = "1";
		final String shipmentNumber = "1";
		Properties properties = new Properties();
		properties.setProperty("orderNumber", orderNumber);
		properties.setProperty("shipmentNumber", shipmentNumber);
		
		final EmailComposer emailComposer = context.mock(EmailComposer.class);
		final EmailSender emailSender = context.mock(EmailSender.class);
		final EmailNotificationHelper emailNotificationHelper = context
				.mock(EmailNotificationHelper.class);
		final EmailProperties mockEmailProperties = context
				.mock(EmailProperties.class);

		context.checking(new Expectations() {
			{
				allowing(emailNotificationHelper).getShipmentConfirmationEmailProperties(orderNumber, shipmentNumber);
				will(returnValue(mockEmailProperties));
				
				allowing(emailComposer).composeMessage(mockEmailProperties);
				will(returnValue(email));

				allowing(emailSender).sendEmail(email);
				will(returnValue(DUMMY_MESSAGE_ID));
			}
		});
		
		emailService.setEmailComposer(emailComposer);
		emailService.setEmailNotificationHelper(emailNotificationHelper);
		emailService.setEmailSender(emailSender);

		emailService.sendOrderShippedEmail(properties);
		
	}

}