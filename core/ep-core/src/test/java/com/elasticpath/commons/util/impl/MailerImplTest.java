/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.util.MailMessage;

/**
 * Test <code>MailerlImpl</code>.
 */
public class MailerImplTest {

	private static final String UTF_8 = "UTF-8";

	private static final String SUBJECT = "Test Subject";

	private static final String BODY = "Test Body";

	private static final String MAIL_ADDRESS = "aaa@aaa.aaa";

	private static final String MAIL_HOST = "a.non-existing.mail.host";

	private static final String FROM_NAME = "jeffrey";

	private static final String HTML_BODY = "<h1>Test Body</h1>";

	private static final String TO_NAME = "to_jeffrey";

	private static final String BAD_MAIL_ADDRESS = "I am an incorrect email address.";

	private static final String EP_SYSTEM_EXCEPTION_EXPECTED = "EpSystemException expected.";

	private MailerImpl email;

	private MailMessage mailMessage;


	/**
	 * Set up the test case.
	 * 
	 * @throws Exception in case of failure
	 */
	@Before
	public void setUp() throws Exception {
		this.email = new MailerImpl();
		this.mailMessage = new MailMessageImpl();
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.EmailImpl.sendHtmlEmail()'.
	 */
	@Test
	public void testSendHtmlEmail() {
		final String emailAddress = MAIL_ADDRESS;
		
		getMailerBean().setAddressBCC(emailAddress);
		getMailerBean().setAddressCC(emailAddress);
		getMailerBean().setAddressFrom(emailAddress);
		getMailerBean().setAddressTo(emailAddress);
		getMailerBean().setBodyHtml(HTML_BODY);
		getMailerBean().setFromName(FROM_NAME);
		getMailerBean().setMailHost(MAIL_HOST);
		getMailerBean().setReturnPath(emailAddress);
		getMailerBean().setSubject(SUBJECT);
		getMailerBean().setToName(TO_NAME);
		

		//Check that no exception is thrown so that the 
		//system can continue to work (the error will be logged)
		email.sendHtmlEmail(getMailerBean());

	}

	/**
	 *
	 * @return
	 */
	private MailMessage getMailerBean() {
		return mailMessage;
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.EmailImpl.sendHtmlEmail()'.
	 */
	@Test
	public void testSendHtmlEmailWithBodyAndHtmlBody() {
		final String emailAddress = MAIL_ADDRESS;

		getMailerBean().setAddressBCC(emailAddress);
		getMailerBean().setAddressCC(emailAddress);
		getMailerBean().setAddressFrom(emailAddress);
		getMailerBean().setAddressTo(emailAddress);
		getMailerBean().setBody(BODY);
		getMailerBean().setBodyHtml(HTML_BODY);
		getMailerBean().setFromName(FROM_NAME);
		getMailerBean().setMailHost(MAIL_HOST);
		getMailerBean().setReturnPath(emailAddress);
		getMailerBean().setSubject(SUBJECT);
		getMailerBean().setToName(TO_NAME);
		
		
		//Check that no exception is thrown so that the 
		//system can continue to work (the error will be logged)
		this.email.sendHtmlEmail(getMailerBean());


	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.EmailImpl.sendEmail()'.
	 */
	@Test
	public void testSendEmailWithoutAddressFrom() {
		final String emailAddress = MAIL_ADDRESS;

		getMailerBean().setAddressBCC(emailAddress);
		getMailerBean().setAddressCC(emailAddress);
		getMailerBean().setAddressTo(emailAddress);
		getMailerBean().setBody(BODY);
		getMailerBean().setBodyHtml(HTML_BODY);
		getMailerBean().setFromName(FROM_NAME);
		getMailerBean().setMailHost(MAIL_HOST);
		getMailerBean().setReturnPath(emailAddress);
		getMailerBean().setSubject(SUBJECT);

		try {
			this.email.sendEmail(UTF_8, getMailerBean());
			fail(EP_SYSTEM_EXCEPTION_EXPECTED);
		} catch (final EpSystemException e) {
			// succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.EmailImpl.sendEmail()'.
	 */
	@Test
	public void testSendEmailWithoutAddressTo() {
		final String emailAddress = MAIL_ADDRESS;

		getMailerBean().setAddressBCC(emailAddress);
		getMailerBean().setAddressCC(emailAddress);
		getMailerBean().setAddressFrom(emailAddress);
		getMailerBean().setBody(BODY);
		getMailerBean().setBodyHtml(HTML_BODY);
		getMailerBean().setFromName(FROM_NAME);
		getMailerBean().setMailHost(MAIL_HOST);
		getMailerBean().setReturnPath(emailAddress);
		getMailerBean().setSubject(SUBJECT);

		try {
			this.email.sendEmail(UTF_8, getMailerBean());
			fail(EP_SYSTEM_EXCEPTION_EXPECTED);
		} catch (final EpSystemException e) {
			// succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.EmailImpl.sendEmail()'.
	 */
	@Test
	public void testSendEmailWithoutSubject() {
		final String emailAddress = MAIL_ADDRESS;

		getMailerBean().setAddressBCC(emailAddress);
		getMailerBean().setAddressCC(emailAddress);
		getMailerBean().setAddressFrom(emailAddress);
		getMailerBean().setAddressTo(emailAddress);
		getMailerBean().setBody(BODY);
		getMailerBean().setBodyHtml(HTML_BODY);
		getMailerBean().setFromName(FROM_NAME);
		getMailerBean().setMailHost(MAIL_HOST);
		getMailerBean().setReturnPath(emailAddress);

		try {
			this.email.sendEmail(UTF_8, getMailerBean());
			fail(EP_SYSTEM_EXCEPTION_EXPECTED);
		} catch (final EpSystemException e) {
			// succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.EmailImpl.sendEmail()'.
	 */
	@Test
	public void testSendEmailWithoutBody() {
		final String emailAddress = MAIL_ADDRESS;

		getMailerBean().setAddressBCC(emailAddress);
		getMailerBean().setAddressCC(emailAddress);
		getMailerBean().setAddressFrom(emailAddress);
		getMailerBean().setAddressTo(emailAddress);
		getMailerBean().setBodyHtml(HTML_BODY);
		getMailerBean().setFromName(FROM_NAME);
		getMailerBean().setMailHost(MAIL_HOST);
		getMailerBean().setReturnPath(emailAddress);
		getMailerBean().setSubject(SUBJECT);

		try {
			this.email.sendEmail(UTF_8, getMailerBean());
			fail(EP_SYSTEM_EXCEPTION_EXPECTED);
		} catch (final EpSystemException e) {
			// succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.EmailImpl.sendEmail()'.
	 */
	@Test
	public void testSendEmailWithoutHtmlBody() {
		final String emailAddress = MAIL_ADDRESS;

		getMailerBean().setAddressBCC(emailAddress);
		getMailerBean().setAddressCC(emailAddress);
		getMailerBean().setAddressFrom(emailAddress);
		getMailerBean().setAddressTo(emailAddress);
		getMailerBean().setBody(BODY);
		getMailerBean().setFromName(FROM_NAME);
		getMailerBean().setMailHost(MAIL_HOST);
		getMailerBean().setReturnPath(emailAddress);
		getMailerBean().setSubject(SUBJECT);

		try {
			this.email.sendHtmlEmail(getMailerBean());
			fail(EP_SYSTEM_EXCEPTION_EXPECTED);
		} catch (final EpSystemException e) {
			// succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.EmailImpl.sendEmail()'.
	 */
	@Test
	public void testSendEmailWithoutMailhost() {
		final String emailAddress = MAIL_ADDRESS;

		getMailerBean().setAddressBCC(emailAddress);
		getMailerBean().setAddressCC(emailAddress);
		getMailerBean().setAddressFrom(emailAddress);
		getMailerBean().setAddressTo(emailAddress);
		getMailerBean().setBody(BODY);
		getMailerBean().setFromName(FROM_NAME);
		getMailerBean().setReturnPath(emailAddress);
		getMailerBean().setSubject(SUBJECT);
		getMailerBean().setBody(HTML_BODY);

		try {
			this.email.sendHtmlEmail(getMailerBean());
			fail(EP_SYSTEM_EXCEPTION_EXPECTED);
		} catch (final EpSystemException e) {
			// succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.EmailImpl.sendEmail()'.
	 */
	@Test
	public void testSendEmailWithBadAddress() {
		final String emailAddress = BAD_MAIL_ADDRESS;

		try {
			getMailerBean().setAddressBCC(emailAddress);
			getMailerBean().setAddressCC(emailAddress);
			getMailerBean().setAddressFrom(emailAddress);
			getMailerBean().setAddressTo(emailAddress);
			getMailerBean().setBody(BODY);
			getMailerBean().setBodyHtml(HTML_BODY);
			getMailerBean().setFromName(FROM_NAME);
			getMailerBean().setMailHost(MAIL_HOST);
			getMailerBean().setReturnPath(emailAddress);
			getMailerBean().setSubject(SUBJECT);
			getMailerBean().setToName(TO_NAME);

			this.email.sendEmail(UTF_8, getMailerBean());
			fail(EP_SYSTEM_EXCEPTION_EXPECTED);
		} catch (final EpSystemException e) {
			// succeed
			assertNotNull(e);
		}

	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.EmailImpl.sendEmail()'.
	 */
	@Test
	public void testSendEmail() {
		final String emailAddress = MAIL_ADDRESS;

		getMailerBean().setAddressBCC(emailAddress);
		getMailerBean().setAddressCC(emailAddress);
		getMailerBean().setAddressFrom(emailAddress);
		getMailerBean().setAddressTo(emailAddress);
		getMailerBean().setBody(BODY);
		getMailerBean().setBodyHtml(HTML_BODY);
		getMailerBean().setFromName(FROM_NAME);
		getMailerBean().setMailHost(MAIL_HOST);
		getMailerBean().setReturnPath(emailAddress);
		getMailerBean().setSubject(SUBJECT);
		getMailerBean().setToName(TO_NAME);

		//Check that no exception is thrown so that the 
		//system can continue to work (the error will be logged)
		this.email.sendEmail(UTF_8, getMailerBean());


			
	}
}
