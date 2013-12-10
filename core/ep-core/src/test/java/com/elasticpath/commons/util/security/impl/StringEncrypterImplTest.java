package com.elasticpath.commons.util.security.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.settings.SettingsService;

/**
 * Test case for <code>StringEncrypterImpl</code>.
 */
public class StringEncrypterImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final String ENCRYPTION_KEY_1 = "Five or more slices of ginger root";
	private static final String ENCRYPTION_KEY_2 = "Fuzzy Wuzzy was a woman?";
	
	private static final String TEST_STRING_1 = "testString1";
	private static final String TEST_STRING_2 = "29873j skskk3((( 33";
	private static final String TEST_STRING_3 = "m/`23`#$#@18slkjl----------";
	

	/**
	 * Test that encryption keys shorter than 24 characters will throw an EpDomainException.
	 */
	@Test
	public void testShortKey() {
		StringEncrypterImpl encrypter = new StringEncrypterImpl() {
			@Override
			String getEncryptionKey() {
				return "shortKey";
			}
		};
		
		try {
			encrypter.encrypt(TEST_STRING_1);
			fail("Expected EpDomainException for an encryption key shorter than 24 characters");
		} catch (EpDomainException ex) {
			assertNotNull(ex);
		}
	}
	
	/**
	 * Test that null encryption key or setting path and/or context will throw an EpDomainException.
	 */
	@Test
	public void testEncryptErrors() {
		StringEncrypterImpl encrypter = new StringEncrypterImpl();
		
		encrypter.setEncryptionKeyPath(null);
		try {
			encrypter.encrypt(TEST_STRING_1);
			fail("Expected EpDomainException for a null encryption key setting path.");
		} catch (EpDomainException ex) {
			assertNotNull(ex);
		}
		
		encrypter.setEncryptionKeyPath("");
		try {
			encrypter.encrypt(TEST_STRING_1);
			fail("Expected EpDomainException for a null encryption key setting path.");
		} catch (EpDomainException ex) {
			assertNotNull(ex);
		}
		
		final SettingsService mockSettingsService = context.mock(SettingsService.class);
		context.checking(new Expectations() {
			{
				allowing(mockSettingsService).getSettingValue(with(any(String.class)));
				will(throwException(new EpServiceException("null value")));
			}
		});
		encrypter.setSettingsService(mockSettingsService);
		try {
			encrypter.encrypt(TEST_STRING_1);
			fail("Expected EpDomainException for a null encryption key");
		} catch (EpDomainException ex) {
			assertNotNull(ex);
		}
	}
	
	/**
	 * Tests multiple encrypters with different keys:
	 * <p>
	 * 1. Encrypting the same input string using 2 different keys must return different results.
	 * 2. And, of course, decrypting the different results using the same original keys should return the same string input.
	 */
	@Test
	public void testMultipleEncrypters() {
		// create 2 encrypter with different keys
		StringEncrypterImpl encrypter1 = new StringEncrypterImpl() {
			@Override
			String getEncryptionKey() {
				return ENCRYPTION_KEY_1;
			}
		};
		
		StringEncrypterImpl encrypter2 = new StringEncrypterImpl() {
			@Override
			String getEncryptionKey() {
				return ENCRYPTION_KEY_2;
			}
		};

		// test encryption
		String enc1 = encrypter1.encrypt(TEST_STRING_1);
		String enc2 = encrypter2.encrypt(TEST_STRING_1);
		assertFalse("Two encryption keys should produce different results: " 
				+ enc1 + ", " + enc2, enc1.equals(enc2));
		
		// test decryption
		assertEquals("Decrypter 1 did not return the original input string.", 
				TEST_STRING_1, encrypter1.decrypt(enc1));
		assertEquals("Decrypter 2 did not return the original input string.", 
				TEST_STRING_1, encrypter2.decrypt(enc2));
	}

	/**
	 * Tests standard encryption/decryption use AES.
	 */
	@Test
	public void testEncryptAndDecryptAES() {
		StringEncrypterImpl encrypter = new StringEncrypterImpl() {
			@Override
			String getEncryptionKey() {
				return ENCRYPTION_KEY_1;
			}
		};
		
		String enc = encrypter.encrypt(TEST_STRING_1);
		assertNotSame("Encrypted String 1", TEST_STRING_1, enc);
		assertEquals("Decrypt String 1", TEST_STRING_1, encrypter.decrypt(enc));

		enc = encrypter.encrypt(TEST_STRING_2);
		assertNotSame("Encrypted String 2", TEST_STRING_2, enc);
		assertEquals("Decrypt String 2", TEST_STRING_2, encrypter.decrypt(enc));

		enc = encrypter.encrypt(TEST_STRING_3);
		assertNotSame("Encrypted String 3", TEST_STRING_3, enc);
		assertEquals("Decrypt String 3", TEST_STRING_3, encrypter.decrypt(enc));
	}
	
	/**
	 * Ensure the encrypter impl is thread-safe.
	 * Ensures fix for MSC-4498
	 * 
	 * To test we create several threads which loop through encrypting and 
	 * decrypting test strings.
	 */
	@Test
	public void testThreadSafety() {
		final StringEncrypterImpl encrypter = new StringEncrypterImpl() {
			@Override
			String getEncryptionKey() {
				return ENCRYPTION_KEY_1;
			}
		};

		final int failureArraySize = 1;
		final boolean [] failed = new boolean[failureArraySize];
		
		/** Runnable used in test threads. */		
		class EncrypterClient implements Runnable {
			/** Loop through several encypt/decrypt calls. */
			public void run() {
				final int numLoops = 20;
				try {
					// Loop to make thread longer-lived so it can conflict 
					// with the other threads
					for (int i = 0; i < numLoops; i++) {
						String testString1Encrypted = encrypter.encrypt(TEST_STRING_1);
						assertNotSame("Thread-safe encryption failed", TEST_STRING_1, testString1Encrypted);
						String testString1Decrypted = encrypter.decrypt(testString1Encrypted);
						assertEquals("Thread-safe decryption failed", TEST_STRING_1, testString1Decrypted);
					}
				} catch (Exception e) {
					failed[0] = true;
				}
			}
		}
		
		// Set up the thread and start them
		final int numThreads = 10;
		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < numThreads; i++) {
			threads.add(new Thread(new EncrypterClient()));
			threads.get(i).start();
		}
		
		// Wait for the threads to all finish
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException ie) {
				// Ignore - very unlikely to happen and won't affect the test 
			}
		}
		assertFalse("Encrypter/Decrypter not thread-safe", failed[0]);
	}

}