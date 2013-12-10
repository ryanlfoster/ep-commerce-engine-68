package com.elasticpath.commons.util.email.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.ServerSocket;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;

/**
 * Test for com.elasticpath.commons.util.email.impl.EmailSenderImpl.
 * @author epdev 
 */
public class EmailSenderImplTest {

	private static final int TIMES_TO_RETRY = 10;
	private static final int TIMEOUT = 100;
	
	private static final String EMAIL_CONTENTS = "This is a simple test of commons-email";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final int DEFAULT_MAIL_PORT = 2525;

	private GreenMail greenMail;

	private int availablePortNumber;

	/**
	 * Prepare for the tests.
	 * 
	 * @throws Exception
	 *             on error
	 */
	@Before
	public void setUp() throws Exception {
		availablePortNumber = getAvailablePortNumber();
		ServerSetup serverSetup = new ServerSetup(availablePortNumber, null, ServerSetup.PROTOCOL_SMTP);
		greenMail = new GreenMail(serverSetup); // uses test ports by default
		greenMail.setUser("login@test.com", "login", "password");
		greenMail.start();
		
		waitForSmtpServerToStart(greenMail);
	}

	/**
	 * Gets an available port number.
	 * @return a port number
	 */
	public static int getAvailablePortNumber() {
		try {
			ServerSocket serverSocket = new ServerSocket(0);
			
			// get the available port number
			int port = serverSocket.getLocalPort();
			// close the socket
			serverSocket.close();
			// return the port number
			return port;
		} catch (IOException e) {
			fail("Cannot find a free socket. Reason: " + e.getLocalizedMessage());
		}

		return DEFAULT_MAIL_PORT;
	}

	/**
	 * Waits for the server to start.
	 * 
	 * @param greenMail greem mail instance
	 * @throws InterruptedException on error
	 */
	public static void waitForSmtpServerToStart(final GreenMail greenMail) throws InterruptedException {
		for (int i = 0; i < TIMES_TO_RETRY && !greenMail.getSmtp().isAlive(); i++) {
			Thread.sleep(TIMEOUT);
		}
	}

	/**
	 * Tear down the test.
	 */
	@After
	public void tearDown() {
		greenMail.stop();
	}

	/**
	 * Tests that an email is sent and received by green mail.
	 * 
	 * @throws EmailException an email exception
	 */
	@Test
	public void testSendEmail() throws EmailException {

		final SettingsReader mockSettingsReader = context
				.mock(SettingsReader.class);

		Email testEmail = createEmail();		
		 
		context.checking(new Expectations() {
			{
				SettingValue mailHostSettingValue = new SettingValueImpl();
				mailHostSettingValue.setValue("localhost");
				oneOf(mockSettingsReader).getSettingValue("COMMERCE/SYSTEM/EMAIL/mailHost");
				will(returnValue(mailHostSettingValue));

				SettingValue mailPortSettingValue = new SettingValueImpl();
				mailPortSettingValue.setValue(String.valueOf(availablePortNumber));
				oneOf(mockSettingsReader).getSettingValue("COMMERCE/SYSTEM/EMAIL/mailPort");
				will(returnValue(mailPortSettingValue));
			}
		});

		EmailSenderImpl emailSender = new EmailSenderImpl();
		emailSender.setSettingsReader(mockSettingsReader);

		emailSender.sendEmail(testEmail);

		assertEquals(EMAIL_CONTENTS, GreenMailUtil
				.getBody(greenMail.getReceivedMessages()[0]));		
	}	

	private Email createEmail() throws EmailException {
		Email testEmail = new SimpleEmail();
		testEmail.addTo("test@elasticpath.com", "Holden Caulfield");
		testEmail.setFrom("test@elasticpath.com", "Me");
		testEmail.setSubject("Test message");
		testEmail.setMsg(EMAIL_CONTENTS);
		return testEmail;
	}

}
