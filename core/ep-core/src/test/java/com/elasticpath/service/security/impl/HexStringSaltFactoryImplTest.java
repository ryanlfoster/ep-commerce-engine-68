package com.elasticpath.service.security.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Test that {@link HexStringSaltFactoryImpl} behaves as expected.
 */
public class HexStringSaltFactoryImplTest {

	private HexStringSaltFactoryImpl saltFactory;
	
	/**
	 * Setup required for each test.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		saltFactory = new HexStringSaltFactoryImpl();
	}

	/**
	 * Test that an appropriate salt is created.
	 */
	@Test
	public void testCreateSalt() {
		final int saltBytes = 32;
		saltFactory.setNumberOfBytes(saltBytes);
		String salt = saltFactory.createSalt();
		assertNotNull("The salt should not be null", salt);
		assertEquals("The salt should be 32 bytes = 64 character hex representation", saltBytes * 2, salt.length());
	}

}
