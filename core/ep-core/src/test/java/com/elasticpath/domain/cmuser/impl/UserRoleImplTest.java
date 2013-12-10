/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.cmuser.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.cmuser.UserPermission;
import com.elasticpath.domain.cmuser.UserRole;

/**
 * Test of the public API of <code>UserRoleImpl</code>.
 */
public class UserRoleImplTest {

	private UserRoleImpl userRoleImpl;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Prepares for the next test.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Before
	public void setUp() throws Exception {
		this.userRoleImpl = new UserRoleImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.UserRoleImpl.getName()'.
	 */
	@Test
	public void testGetName() {
		assertEquals(userRoleImpl.getName(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.UserRoleImpl.setName(String)'.
	 */
	@Test
	public void testSetName() {
		final String roleName = "CSR";
		userRoleImpl.setName(roleName);
		assertSame(userRoleImpl.getName(), roleName);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.UserRoleImpl.getUserPermissionMap()'.
	 */
	@Test
	public void testGetUserPermissionMap() {
		final Set<UserPermission> userPermissions = userRoleImpl.getUserPermissions();
		assertTrue("Check getUserPermissions", userPermissions.isEmpty());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.UserRoleImpl.isSuperUserRole()'.
	 */
	@Test
	public void testIsSuperUserRole() {
		assertFalse(userRoleImpl.isSuperUserRole());

		userRoleImpl.setName(UserRole.SUPERUSER);
		assertTrue(userRoleImpl.isSuperUserRole());
	}
	
	/**
	 * Test that the WSUSER role is permanent.
	 */
	@Test
	public void testIsPermanentRoleWsUser() {
		userRoleImpl.setName(UserRole.WSUSER);
		assertTrue(userRoleImpl.isUnmodifiableRole());		
	}
	
	/**
	 * Test that the CMUSER role is permanent.
	 */
	@Test
	public void testIsPermanentRoleCmUser() {
		userRoleImpl.setName(UserRole.CMUSER);
		assertTrue(userRoleImpl.isUnmodifiableRole());
	}

	/**
	 * Test that UserRole can add a user permission.
	 */
	@Test
	public void testAddUserPermission() {
		UserPermission userPermission = context.mock(UserPermission.class);
		
		userRoleImpl.addUserPermission(userPermission);

		assertTrue(userRoleImpl.getUserPermissions().contains(userPermission));
	}

	/**
	 * Test that UserRole can remove a user permission.
	 */
	@Test
	public void testRemoveUserPermission() {
		UserPermission userPermission = context.mock(UserPermission.class);
		
		userRoleImpl.addUserPermission(userPermission);
		userRoleImpl.removeUserPermission(userPermission);
		assertFalse(userRoleImpl.getUserPermissions().contains(userPermission));
	}
}
