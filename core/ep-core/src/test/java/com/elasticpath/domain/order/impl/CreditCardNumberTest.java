/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.util.security.CreditCardEncrypter;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for <code>CreditCardNumber</code>.
 */
public class CreditCardNumberTest {
	private static final String UNENCRYPTED_CARD_NUMBER = "4012888888881881";
	private static final String ENCRYPTED_CARD_NUMBER = "encrypted number";
	private static final String MASKED_CARD_NUMBER = "************1881";

	private CreditCardNumber creditCardNumber;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepare for the tests.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		// Mock out the underlying encrypter
		expectationsFactory.allowingBeanFactoryGetBean("creditCardEncrypter",
				new CreditCardEncrypter() {

					public String mask(final String objectToMask) {
						return MASKED_CARD_NUMBER;
					}

					public String encrypt(final String unencryptedString) {
						return ENCRYPTED_CARD_NUMBER;
					}

					public String decrypt(final String encryptedString) {
						return UNENCRYPTED_CARD_NUMBER;
					}

					public String decryptAndMask(final String encryptedCreditCardNumber) {
						return null;
					}
				});

		creditCardNumber = new CreditCardNumber();
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderPaymentImpl.getCardNumber()', using an encrypted card number.
	 */
	@Test
	public void testGetSetCardNumberWithEncrypted() {
		creditCardNumber.setPersistedEncryptedCardNumber(ENCRYPTED_CARD_NUMBER);
		creditCardNumber.postLoad();
		creditCardNumber.setShouldPersistEncrypted(true);
		assertEquals(ENCRYPTED_CARD_NUMBER, creditCardNumber.getPersistedEncryptedCardNumber());
	}

	/**
	 *
	 */
	@Test
	public void testGetEncryptedBySettingUnencrypted() {
		creditCardNumber.setFullCardNumber(UNENCRYPTED_CARD_NUMBER);
		creditCardNumber.setShouldPersistEncrypted(true);
		assertEquals(ENCRYPTED_CARD_NUMBER, creditCardNumber.getPersistedEncryptedCardNumber());
		creditCardNumber.setShouldPersistEncrypted(false);
		assertNull(creditCardNumber.getPersistedEncryptedCardNumber());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderPaymentImpl.getPersistedEncryptedCardNumber()', using a masked card number.
	 */
	@Test
	public void testGetSetCardNumberWithMasked() {
		creditCardNumber.setPersistedEncryptedCardNumber(MASKED_CARD_NUMBER);
		creditCardNumber.setShouldPersistEncrypted(false);
		assertEquals(null, creditCardNumber.getPersistedEncryptedCardNumber());
	}

	/**
	 * Test method for {@link CreditCardNumber#getMaskedCardNumber()}, after setting the encrypted card number.
	 */
	@Test
	public void testGetMaskedCardNumberWithEncrypted() {
		// First, set the encrypted card number
		creditCardNumber.setPersistedEncryptedCardNumber(ENCRYPTED_CARD_NUMBER);
		creditCardNumber.postLoad();
		creditCardNumber.setShouldPersistEncrypted(true);
		assertEquals(ENCRYPTED_CARD_NUMBER, creditCardNumber.getPersistedEncryptedCardNumber());
	}

	/**
	 * Test method for {@link CreditCardNumber#getMaskedCardNumber()}, after setting the masked
	 * card number.
	 */
	@Test
	public void testGetMaskedCardNumberWithMasked() {
		creditCardNumber.setPersistedEncryptedCardNumber(MASKED_CARD_NUMBER);
		creditCardNumber.setShouldPersistEncrypted(false);
		assertNull(creditCardNumber.getPersistedEncryptedCardNumber());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderPaymentImpl.getUnencryptedCardNumber()', after setting the encrypted
	 * card number.
	 */
	@Test
	public void testGetUnencryptedCardNumberWithEncrypted() {
		// First, set the OrderPayment's card number with an encrypted card number, but do not set the payment method to CreditCard yet
		creditCardNumber.setPersistedEncryptedCardNumber(ENCRYPTED_CARD_NUMBER);
		creditCardNumber.postLoad();
		creditCardNumber.setShouldPersistEncrypted(true);
		assertEquals(UNENCRYPTED_CARD_NUMBER, creditCardNumber.getFullCardNumber());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderPaymentImpl.getUnencryptedCardNumber()', after setting the masked
	 * card number.
	 */
	@Test
	public void testGetUnencryptedCardNumberWithMasked() {
		// First, set the OrderPayment's card number with a masked card number but do not set the payment method to CreditCard yet
		creditCardNumber.setPersistedEncryptedCardNumber(MASKED_CARD_NUMBER);
		creditCardNumber.setShouldPersistEncrypted(false);

		assertNull(creditCardNumber.getFullCardNumber());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderPaymentImpl.setUnencryptedCardNumber()', after setting the encrypted
	 * card number.
	 */
	@Test
	public void testSetGetUnencryptedCardNumberWithEncrypted() {
		// Set the OrderPayment's unencrypted card number
		creditCardNumber.setFullCardNumber(UNENCRYPTED_CARD_NUMBER);
		assertEquals(UNENCRYPTED_CARD_NUMBER, creditCardNumber.getFullCardNumber());

		// Set the OrderPayment's encrypted card number, but do not set the payment method to CreditCard
		creditCardNumber.setPersistedEncryptedCardNumber(ENCRYPTED_CARD_NUMBER);
		creditCardNumber.setShouldPersistEncrypted(true);
		assertEquals(UNENCRYPTED_CARD_NUMBER, creditCardNumber.getFullCardNumber());
	}
}
