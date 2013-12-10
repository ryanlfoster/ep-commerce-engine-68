package com.elasticpath.util.cryptotool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.util.security.impl.SimpleEncryption;

/**
 * Test the SimpleEncryption class.
 *
 * @author lstewart
 */
public class SimpleEncryptionTest {

	private final String[] plaintext = {
			"she sells sea shells by the sea",
			"when pigs fly",
			"1111111111111111",
			"4111444444441234",
			"123"
	};

	private SimpleEncryption[] enc;

	/**
	 * Setup the test.
	 */
	@Before
	public void setUp() {
		enc = new SimpleEncryption[1];
		enc[0] = new SimpleEncryption("We recommend you use a mirror to download");
	}

	/**
	 * Test the case where the pass phrase is empty.
	 */
	@Test
	public void testEmptyPassPhrase() {
		SimpleEncryption enc = null;
		try {
			enc = new SimpleEncryption(null);
			fail("exception should be thrown");
		} catch (Exception ex) {
			assertNotNull(ex);
		}

		assertNull(enc);

		enc = new SimpleEncryption("");
		assertNotNull(enc);
	}

	private static final int FOUR_YOU_CHECKSTYLE = 4;

	/**
	 * Make sure short Strings can be used as pass phrases.
	 */
	@Test
	public void testShortPassPhrase() throws Exception {
		enc[0].encrypt(plaintext[FOUR_YOU_CHECKSTYLE]);
	}

	/**
	 * Test just encrypting/decrypting a single string.
	 */
	@Test
	public void testEncryptDecryptSingle() throws Exception {
		String cyphertext = enc[0].encrypt(plaintext[0]);
		assertFalse(StringUtils.isBlank(cyphertext));
		assertFalse(plaintext[0].equals(cyphertext));
		String result = enc[0].decrypt(cyphertext);
		assertFalse(StringUtils.isBlank(result));
		assertTrue(plaintext[0].equals(result));
	}

	/**
	 * Try encr-ing/decr-ing a few strings.
	 */
	@Test
	public void testEncryptDecryptAll() throws Exception {
		for (int ii = 0; ii < plaintext.length; ii++) {
			String cyphertext = enc[0].encrypt(plaintext[ii]);
			assertFalse(StringUtils.isBlank(cyphertext));
			assertFalse(cyphertext.equals(plaintext[ii]));
			String result = enc[0].decrypt(cyphertext);
			assertFalse(StringUtils.isBlank(result));
			assertTrue(plaintext[ii].equals(result));
		}
	}

	/**
	 * Try to decrypt a plaintext String. We expect an exception.
	 */
	@Test(expected = Exception.class)
	public void testDecryptBeforeEncrypt() throws Exception {
		enc[0].decrypt(plaintext[0]);
	}
}
