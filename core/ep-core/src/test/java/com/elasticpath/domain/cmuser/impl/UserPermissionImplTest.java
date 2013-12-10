package com.elasticpath.domain.cmuser.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.EpDomainException;

/**
 * Test of the public API of <code>UserPermissionImpl</code>.
 */
public class UserPermissionImplTest {
	private static final String EP_DOMAIN_EXCEPTION_EXPECTED = "EpDomainException expected.";
	
	private static final String AUTHORITY = "createOrder";

	private UserPermissionImpl userPermissionImpl;
	
	/**
	 * Prepares for the next test.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Before
	public void setUp() throws Exception {
		userPermissionImpl = new UserPermissionImpl();
	}
	
	/**
	 * Test case for init method.
	 */
	@Test
	public void testInit() {
		try {
			userPermissionImpl.init();
			fail(EP_DOMAIN_EXCEPTION_EXPECTED);
		} catch (final EpDomainException e) {
			assertNotNull(e);
			// Success!
		}
		
		try {
			userPermissionImpl.init();
			fail(EP_DOMAIN_EXCEPTION_EXPECTED);
		} catch (final EpDomainException e) {
			assertNotNull(e);
			// Success!
		}
		
		userPermissionImpl.setAuthority(AUTHORITY);
		assertSame(AUTHORITY, userPermissionImpl.getAuthority());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.UserPermissionImpl.getAuthority()'.
	 */
	@Test
	public void testGetAuthority() {
		assertEquals(userPermissionImpl.getAuthority(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.UserPermissionImpl.setAuthority(String)'.
	 */
	@Test
	public void testSetAuthority() {
		userPermissionImpl.setAuthority(AUTHORITY);
		assertSame(userPermissionImpl.getAuthority(), AUTHORITY);
	}
}

