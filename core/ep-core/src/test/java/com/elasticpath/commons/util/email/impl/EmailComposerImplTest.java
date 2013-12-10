/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.util.email.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.mail.EmailException;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.util.StoreMessageSource;
import com.elasticpath.commons.util.VelocityEngineInstanceFactory;
import com.elasticpath.commons.util.email.EmailContextFactory;
import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.domain.misc.impl.EmailPropertiesImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.impl.ThreadLocalStorageImpl;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;

/** Test cases for <code>EmailServiceImpl</code>. */
public class EmailComposerImplTest {

	private static final String UNEXPECTED_EXCEPTION = "unexpected exception";

	private EmailComposerImpl emailComposer;

	private EmailProperties emailProperties;

	// private Mailer mockMailer;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private StoreService mockStoreService;
	private SettingsReader mockSettingService;
	private StoreMessageSource mockStoreMessageSource;
	private EmailContextFactory emailContextFactory;

	/**
	 * Prepare for the tests.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {

		emailContextFactory = context.mock(EmailContextFactory.class);
		mockSettingService = context.mock(SettingsReader.class);
		mockStoreService = context.mock(StoreService.class);
		mockStoreMessageSource = context.mock(StoreMessageSource.class);

		// Create an email service that is stubbed for testing
		emailComposer = new EmailComposerImpl() {
			@Override
			protected String mergeTemplateIntoString(final String txtTemplate, final Map<String, Object> velocityTemplateResources,
					final String storeCode) throws VelocityException {
				return "Processed Template";
			}
		};
		emailComposer.setEmailContextFactory(emailContextFactory);
		emailComposer.setStoreConfig(new ThreadLocalStorageImpl());
		emailComposer.setStoreService(mockStoreService);
		emailComposer.setStoreMessageSource(mockStoreMessageSource);
		emailComposer.setVelocityEngineFactory(new VelocityEngineInstanceFactory() {
			/** stubbed **/
			public VelocityEngine getVelocityEngine(final String storeCode) {
				return null;
			}
		});

		emailProperties  = createAndInitializeEmailProperties();

		assertNotNull(emailProperties.getStoreCode()); // can't be null for tests
	}

	private EmailProperties createAndInitializeEmailProperties() {
		EmailProperties emailProperties = new EmailPropertiesImpl();
		emailProperties.setTextTemplate("text template");
		emailProperties.setHtmlTemplate("html template");
		emailProperties.setDefaultSubject("defaultSubject");
		emailProperties.setStoreCode("test code");
		emailProperties.setTextOnly(true);
		emailProperties.setLocaleDependentSubjectKey(null);
		emailProperties.setEmailLocale(null);

		return emailProperties;
	}

	/**
	 * Test that if the given StoreCode does not map to a Store then an EpServiceException will be thrown.
	 */
	@Test
	public void testSendEmailListStoreSpecificBadStoreCode() {
		List<String> recipientList = new ArrayList<String>();
		recipientList.add("address1@foo.com");
		recipientList.add("address2@foo.com");
		recipientList.add("address3@foo.com");
		emailProperties.setStoreCode("some store");
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockStoreService).findStoreWithCode(emailProperties.getStoreCode());
				will(returnValue(null));
			}
		});

		try {
			emailComposer.composeMessage(emailProperties);
			fail("emailComposer should throw exception for non-existing store code");
		} catch (EpServiceException e) { // NOPMD -- nothing to assert, empty block OK
			// pass
		} catch (EmailException e) {
			fail(UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.misc.impl.emailComposerImpl.sendMail(EmailProperties)'.
	 */
	@Test
	public void testComposeTextEmail() {
		// lets change this to make sure we get the right results
		emailProperties.setHtmlTemplate(null);
		emailProperties.setRecipientAddress("whatever@asif.com");

		// test existing store
		//final long storeUid = 1234L;
		//Store store = new StoreImpl();
		//store.setUidPk(storeUid);
		//store.setCode(emailProperties.getStoreCode());
		//store.setEmailSenderAddress("test@elasticpath.com");
		//store.setEmailSenderName("test");

		final Store mockStore = context.mock(Store.class);
		context.checking(new Expectations() {
			{
				oneOf(mockStore).getEmailSenderAddress();
				will(returnValue("test@elasticpath.com"));

				oneOf(mockStore).getEmailSenderName();
				will(returnValue("test"));

				oneOf(mockStore).getContentEncoding();
				will(returnValue("UTF-8"));

				oneOf(mockStoreService).findStoreWithCode(emailProperties.getStoreCode());
				will(returnValue(mockStore));

				allowing(emailContextFactory).createVelocityContext(mockStore, emailProperties); will(returnValue(Collections.emptyMap()));
			}
		});
		try {
			emailComposer.composeMessage(emailProperties);
		} catch (EmailException e) {
			fail(UNEXPECTED_EXCEPTION);
		}

		// test non-existing store
		emailProperties.setStoreCode("some store");
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockStoreService).findStoreWithCode(emailProperties.getStoreCode());
				will(returnValue(null));

		// oneOf(mockSettingService).getSettingValue()
		// will(returnValue(settingValue));
			}
		});

		try {
			emailComposer.composeMessage(emailProperties);
			fail("emailComposer should throw exception for non-persisted store code");
		} catch (EpServiceException e) { // NOPMD -- nothing to assert, empty block OK
			// pass
		} catch (EmailException e) {
			fail(UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.misc.impl.emailComposerImpl.sendMail(EmailProperties)'.
	 */
	@Test
	public void testComposeHtmlEmail() {
		emailProperties.setRecipientAddress("whatever@asif.com");
		emailProperties.setTextOnly(false);

		final SettingValue settingValue = new SettingValueImpl();
		settingValue.setValue("false");
		context.checking(new Expectations() {
			{
				oneOf(mockSettingService).getSettingValue("COMMERCE/SYSTEM/EMAIL/emailTextTemplateEnabled");
				will(returnValue(settingValue));
			}
		});

		emailComposer.setSettingsReader(mockSettingService);

		// test existing store
		//final long storeUid = 1234L;
		//Store store = new StoreImpl();
		//store.setUidPk(storeUid);
		//store.setCode(emailProperties.getStoreCode());
		//store.setEmailSenderAddress("test@elasticpath.com");
		//store.setEmailSenderName("test");

		final Store mockStore = context.mock(Store.class);
		context.checking(new Expectations() {
			{
				oneOf(mockStore).getEmailSenderAddress();
				will(returnValue("test@elasticpath.com"));

				oneOf(mockStore).getEmailSenderName();
				will(returnValue("test"));

				oneOf(mockStoreService).findStoreWithCode(emailProperties.getStoreCode());
				will(returnValue(mockStore));

				allowing(emailContextFactory).createVelocityContext(mockStore, emailProperties); will(returnValue(Collections.emptyMap()));
			}
		});
		try {
			emailComposer.composeMessage(emailProperties);
		} catch (EmailException e) {
			fail(UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.misc.impl.emailComposerImpl.composeMessage(EmailProperties)'.
	 */
	@Test
	public void testUseGlobalTemplates() {
		emailProperties.setRecipientAddress("whatever@asif.com");
		emailProperties.setStoreCode(null);

		final SettingValue emailGlobalSenderAddressSettingValue = new SettingValueImpl();
		emailGlobalSenderAddressSettingValue.setValue("customerService@SomeDomain.com");
		context.checking(new Expectations() {
			{
				oneOf(mockSettingService).getSettingValue("COMMERCE/SYSTEM/EMAIL/emailGlobalSenderAddress");
				will(returnValue(emailGlobalSenderAddressSettingValue));
			}
		});

		final SettingValue emailGlobalSenderNameSettingValue = new SettingValueImpl();
		emailGlobalSenderNameSettingValue.setValue("Customer Service");
		context.checking(new Expectations() {
			{
				oneOf(mockSettingService).getSettingValue("COMMERCE/SYSTEM/EMAIL/emailGlobalSenderName");
				will(returnValue(emailGlobalSenderNameSettingValue));
			}
		});

		emailComposer.setSettingsReader(mockSettingService);

		context.checking(new Expectations() { {
			allowing(emailContextFactory).createVelocityContext(null, emailProperties); will(returnValue(Collections.emptyMap()));
		} });

		try {
			emailComposer.composeMessage(emailProperties);
		} catch (EmailException e) {
			fail(UNEXPECTED_EXCEPTION);
		}
	}
	/**
	 * Test method for 'com.elasticpath.service.misc.impl.emailComposerImpl.getEmailSubject(EmailProperties)'.
	 */
	@Test
	public void testGetEmailSubject() {
		final String defaultSubject = "my default subject";
		final EmailProperties emailProp = createAndInitializeEmailProperties();

		//only default message
		emailProp.setDefaultSubject(defaultSubject);
		assertEquals("No message key, will get the default message", defaultSubject, emailComposer.getEmailSubject(emailProp));

		//property key exists, but message not found
		emailProp.setLocaleDependentSubjectKey("my.english.key");
		emailProp.setEmailLocale(Locale.CANADA);
		context.checking(new Expectations() {
			{
				oneOf(mockStoreMessageSource).getMessage(
						emailProp.getStoreCode(), emailProp.getLocaleDependentSubjectKey(), emailProp.getEmailLocale());
				will(returnValue(null));
			}
		});

		assertEquals("Message key exists, but no message found in properties files", defaultSubject, emailComposer.getEmailSubject(emailProp));

		//property key exists, and message found
		final String englishTranslationMessage = "my meesage for en locale";
		context.checking(new Expectations() {
			{

				oneOf(mockStoreMessageSource).getMessage(
						emailProp.getStoreCode(), emailProp.getLocaleDependentSubjectKey(), emailProp.getEmailLocale());
				will(returnValue(englishTranslationMessage));
			}
		});

		assertEquals("Message key exists, and message found in properties files",
				englishTranslationMessage, emailComposer.getEmailSubject(emailProp));

	}

}
