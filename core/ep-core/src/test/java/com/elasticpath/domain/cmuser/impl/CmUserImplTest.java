/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.cmuser.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.junit.Test;

import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpPasswordValidationException;
import com.elasticpath.commons.security.impl.CmPasswordPolicyImpl;
import com.elasticpath.commons.security.impl.MaximumAgePasswordPolicyImpl;
import com.elasticpath.domain.cmuser.UserPermission;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/**
 * Test of the public API of <code>CmUserImpl</code>.
 */
public class CmUserImplTest extends AbstractEPTestCase {

	private CmUserImpl cmUserImpl;

	private PasswordEncoder mockPasswordEncoder;

	/**
	 * Prepares for the next test.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Override
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	public void setUp() throws Exception {
		super.setUp();
		this.cmUserImpl = new CmUserImpl();
		this.cmUserImpl.setUserRoles(new HashSet<UserRole>());
		
		stubGetBean("CSR", UserPermissionImpl.class);
		
		this.mockPasswordEncoder = context.mock(PasswordEncoder.class);
		stubGetBean(ContextIdNames.CM_PASSWORDENCODER, mockPasswordEncoder);
		
		final SettingsService mockSettingsService = context.mock(SettingsService.class);
		final SettingValue mockMaxAgeValue = context.mock(SettingValue.class, "max age setting value");
		final SettingValue mockMaxLoginAttempts = context.mock(SettingValue.class, "max login attempts setting value");
		final SettingValue mockMinPasswordLength = context.mock(SettingValue.class, "min password length setting value");
		final SettingValue mockPasswordHistoryLength = context.mock(SettingValue.class, "password history length setting value");
		context.checking(new Expectations() {
			{
				allowing(mockMaxAgeValue).getValue();
				will(returnValue("90"));

				allowing(mockSettingsService).getSettingValue("COMMERCE/APPSPECIFIC/RCP/maximumPasswordAge");
				will(returnValue(mockMaxAgeValue));

				allowing(mockMaxLoginAttempts).getValue();
				will(returnValue("6"));

				allowing(mockSettingsService).getSettingValue("COMMERCE/APPSPECIFIC/RCP/accountLockoutThreshold");
				will(returnValue(mockMaxLoginAttempts));

				allowing(mockMinPasswordLength).getValue();
				will(returnValue("8"));

				allowing(mockSettingsService).getSettingValue("COMMERCE/APPSPECIFIC/RCP/minimumPasswordLength");
				will(returnValue(mockMinPasswordLength));

				allowing(mockPasswordHistoryLength).getValue();
				will(returnValue("3"));

				allowing(mockSettingsService).getSettingValue("COMMERCE/APPSPECIFIC/RCP/passwordHistoryLength");
				will(returnValue(mockPasswordHistoryLength));
			}
		});

		CmPasswordPolicyImpl cmPasswordPolicy = new CmPasswordPolicyImpl();
		cmPasswordPolicy.setSettingsService(mockSettingsService);
		cmPasswordPolicy.setBeanFactory(getBeanFactory());

		stubGetBean("cmPasswordPolicy", cmPasswordPolicy);

		MaximumAgePasswordPolicyImpl maximumAgePasswordPolicy = new MaximumAgePasswordPolicyImpl();
		maximumAgePasswordPolicy.setSettingsService(mockSettingsService);

		stubGetBean("maximumAgePasswordPolicy", maximumAgePasswordPolicy);
		stubGetBean("userPasswordHistoryItem", UserPasswordHistoryItemImpl.class);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.getUserName()'.
	 */
	@Test
	public void testGetSetUserName() {
		assertEquals(cmUserImpl.getUsername(), null);

		final String userName = "userName";
		cmUserImpl.setUserName(userName);
		assertSame(userName, cmUserImpl.getUsername());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.setEmail(String)'.
	 */
	@Test
	public void testSetEmail() {
		final String[] testData = new String[] { "aaaa@aaa.aaa", "", null };
		for (final String email : testData) {
			cmUserImpl.setEmail(email);
			assertSame("Check set email", email, cmUserImpl.getEmail());
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.getFirstName()'.
	 */
	@Test
	public void testGetSetFirstName() {
		assertEquals(cmUserImpl.getFirstName(), null);

		final String firstName = "first";
		cmUserImpl.setFirstName(firstName);
		assertSame(firstName, cmUserImpl.getFirstName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.getLastName()'.
	 */
	@Test
	public void testGetSetLastName() {
		assertEquals(cmUserImpl.getLastName(), null);

		final String firstName = "first";
		cmUserImpl.setLastName(firstName);
		assertSame(firstName, cmUserImpl.getLastName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.hasUserRole()'.
	 */
	@Test
	public void testHasUserRole() {
		final long userRoleId = 1000L;
		final long invalidUserRoleId = 1001L;
		final String testRoleName = "TestRole";
		final String invalidTestRoleName = "InvalidTestRole";

		final UserRole userRole = new UserRoleImpl();
		userRole.setUidPk(userRoleId);
		userRole.setName(testRoleName);

		cmUserImpl.addUserRole(userRole);
		assertTrue(cmUserImpl.hasUserRole(userRoleId));
		assertTrue(cmUserImpl.hasUserRole(testRoleName));

		assertFalse(cmUserImpl.hasUserRole(invalidTestRoleName));
		assertFalse(cmUserImpl.hasUserRole(invalidUserRoleId));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.hasPermission()'.
	 */
	@Test
	public void testHasPermission() {
		final long userRoleId = 1000L;
		final String testRoleName = "TestRole";
		final String authority = "CSR";
		final String invalidAuthority = "Invalid";

		final UserRole userRole = new UserRoleImpl();
		userRole.setUidPk(userRoleId);
		userRole.setName(testRoleName);

		final UserPermission userPermission = new UserPermissionImpl();
		userPermission.setAuthority(authority);
		Set<UserPermission> userPermissions = new HashSet<UserPermission>();
		userPermissions.add(userPermission);
		userRole.setUserPermissions(userPermissions);
		cmUserImpl.addUserRole(userRole);

		assertTrue(cmUserImpl.hasPermission(authority));
		assertFalse(cmUserImpl.hasPermission(invalidAuthority));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.getPassword()'.
	 */
	@Test
	public void testGetPassword() {
		assertEquals(null, cmUserImpl.getPassword());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.getPassword()'.
	 */
	@Test
	public void testGetEncryptedPassword() {
		assertEquals(cmUserImpl.getPassword(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.setClearTextPassword(String)'.
	 */
	@Test
	public void testSetClearTextPassword() {
		final String[] passwords = new String[] { "AbCdEfGhI", "AbCdEfGhIjKlMnOpQrS", "aA123_$@#^&", "", null };
		final String[] hashedPasswords = new String[] { "d60c7aaba158d8270ec509390438152ca931ec6a", "32a6ea3419c4d9653cf51c6500f3accef2012ab0",
				"e9d1d12fbb45ca95c496f3a33a40956c1a4da1ef", "adc83b19e793491b1c6ea0fd8b46cd9f32e592fc", null };

		for (int i = 0; i < passwords.length; i++) {
			final String password = passwords[i];
			final String hashedPassword = hashedPasswords[i];
		context.checking(new Expectations() {
			{
			allowing(mockPasswordEncoder).encodePassword(password, null);
			will(returnValue(hashedPassword));
			}
		});
			cmUserImpl.setClearTextPassword(password);
			cmUserImpl.setPasswordUsingPasswordEncoder(password);
			assertEquals(hashedPassword, cmUserImpl.getPassword());
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.getConfirmClearTextPassword()'.
	 */
	@Test
	public void testGetConfirmClearTextPassword() {
		assertEquals(null, cmUserImpl.getConfirmClearTextPassword());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CmUserImpl.setLastName(String)'.
	 */
	@Test
	public void testSetConfirmClearTextPassword() {
		final String[] passwords = new String[] { "AbCdEfGhI", "AbCdEfGhIjKlMnOpQrS", "aA123_$@#^&", "", null };
		for (final String password : passwords) {
			cmUserImpl.setConfirmClearTextPassword(password);
			assertSame(password, cmUserImpl.getConfirmClearTextPassword());
		}
	}

	/** 
	 * Test that isCmAccess returns true when a user has the CMUSER role. 
	 * @throws Exception on error.
	 */
	@Test
	public void testIsCmAccessTrue() throws Exception {
		UserRole cmUserRole = new UserRoleImpl();
		cmUserRole.setUserPermissions(new HashSet<UserPermission>());
		cmUserRole.setName(UserRole.CMUSER);
		cmUserImpl.addUserRole(cmUserRole);
		assertTrue(cmUserImpl.isCmAccess());
	}
	
	/** 
	 * Test that isCmAccess returns false when a user does not have the CMUSER role. 
	 * @throws Exception on error.
	 */
	@Test
	public void testIsCmAccessFalse() throws Exception {
		UserRole wsUserRole = new UserRoleImpl();
		wsUserRole.setUserPermissions(new HashSet<UserPermission>());
		wsUserRole.setName(UserRole.WSUSER);
		cmUserImpl.addUserRole(wsUserRole);
		assertFalse(cmUserImpl.isCmAccess());
	}
	
	/** 
	 * Test that isWsAccess returns true when a user has the WSUSER role. 
	 * @throws Exception on error.
	 */
	@Test
	public void testIsWsAccessTrue() throws Exception {
		UserRole wsUserRole = new UserRoleImpl();
		wsUserRole.setUserPermissions(new HashSet<UserPermission>());
		wsUserRole.setName(UserRole.WSUSER);
		cmUserImpl.addUserRole(wsUserRole);
		assertTrue(cmUserImpl.isWsAccess());
	}
	
	/** 
	 * Test that isWsAccess returns false when a user does not have the WSUSER role. 
	 * @throws Exception on error.
	 */
	@Test
	public void testIsWsAccessFalse() throws Exception {
		UserRole cmUserRole = new UserRoleImpl();
		cmUserRole.setUserPermissions(new HashSet<UserPermission>());
		cmUserRole.setName(UserRole.CMUSER);
		cmUserImpl.addUserRole(cmUserRole);
		assertFalse(cmUserImpl.isWsAccess());
	}
	
	/**
	 * Test the password expiration logic.
	 */
	@Test
	public void testIsPasswordExpired() {
		final int maxPasswordAge = 90;
		final Date date = new Date();
		
		CmUserImpl cmUser = new CmUserImpl();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -maxPasswordAge + 1);
		cmUser.setLastChangedPasswordDate(calendar.getTime());
		cmUser.setLastLoginDate(date);
		assertFalse("the password should be expired next day", cmUser.isPasswordExpired());
		
		calendar.add(Calendar.DATE, -1);
		cmUser.setLastChangedPasswordDate(calendar.getTime());
		cmUser.setLastLoginDate(date);
		assertTrue("the password expired today", cmUser.isPasswordExpired());
		
		calendar.add(Calendar.DATE, -1);
		cmUser.setLastChangedPasswordDate(calendar.getTime());
		cmUser.setLastLoginDate(date);
		assertTrue("the password was expired 1 day ago", cmUser.isPasswordExpired());
	}

	/**
	 * Check that user can't set new password equal to current one.
	 */
	@Test
	public void testSetCheckedClearTextPassword1() {
		final String currentPassword = "password1";
		final TimeService timeService = context.mock(TimeService.class);
		context.checking(new Expectations() {
			{
				allowing(timeService).getCurrentTime();
			}
		});
		stubGetBean("timeService", timeService);
		context.checking(new Expectations() {
			{
				allowing(mockPasswordEncoder).encodePassword(currentPassword, null);
				will(returnValue("encodedPassword"));
			}
		});
		cmUserImpl.setClearTextPassword(currentPassword);
		cmUserImpl.setPasswordUsingPasswordEncoder(currentPassword);
		try {
			cmUserImpl.setCheckedClearTextPassword(currentPassword);
			fail("CM User's new password can't be equal to his current password.");
		} catch (EpPasswordValidationException expected) {
			assertNotNull("Exception must be thrown to indicate password duplication.", expected.getMessage());
		}
	}
	
	/**
	 * Check that user can't set new password equal to one of passwords from history or to current
	 * but can set new password equal to old password which age is bigger then password history length.
	 * It is important also that passwords are held in order history is built.
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	@Test
	public void testSetCheckedClearTextPassword2() {
		final int passwordHistoryLength = 3;
		final TimeService timeService = context.mock(TimeService.class);
		context.checking(new Expectations() {
			{
				allowing(timeService).getCurrentTime();
			}
		});
		stubGetBean("timeService", timeService);
		assertEquals("There should be 0 passwords in history after first login.",
				0, cmUserImpl.getPasswordHistoryItems().size());

		final String initialPassword = "initialPassword0";
		context.checking(new Expectations() {
			{
				allowing(mockPasswordEncoder).encodePassword(initialPassword, null);
				will(returnValue("encodedInitialPassword"));
			}
		});
		cmUserImpl.setClearTextPassword(initialPassword);
		cmUserImpl.setPasswordUsingPasswordEncoder(initialPassword);

		final String newPassword1 = "newPassword1";
		final String encodedNewPassword1 = "encodedNewPassword1";
		context.checking(new Expectations() {
			{
				allowing(mockPasswordEncoder).encodePassword(newPassword1, null);
				will(returnValue(encodedNewPassword1));
			}
		});
		cmUserImpl.setCheckedClearTextPassword(newPassword1);

		final String newPassword2 = "newPassword2";
		final String encodedNewPassword2 = "encodedNewPassword2";
		context.checking(new Expectations() {
			{
				allowing(mockPasswordEncoder).encodePassword(newPassword2, null);
				will(returnValue(encodedNewPassword2));
			}
		});
		cmUserImpl.setCheckedClearTextPassword(newPassword2);

		final String newPassword3 = "newPassword3";
		final String encodedNewPassword3 = "encodedNewPassword3";
		context.checking(new Expectations() {
			{
				allowing(mockPasswordEncoder).encodePassword(newPassword3, null);
				will(returnValue(encodedNewPassword3));
			}
		});
		cmUserImpl.setCheckedClearTextPassword(newPassword3);

		assertEquals("Password history length should be equal to " + passwordHistoryLength,
				passwordHistoryLength - 1, cmUserImpl.getPasswordHistoryItems().size());

		try {
		context.checking(new Expectations() {
			{
			allowing(mockPasswordEncoder).encodePassword(newPassword1, null);
			will(returnValue(encodedNewPassword1));
			}
		});
			cmUserImpl.setCheckedClearTextPassword(newPassword1);
			fail("newPassword1 can't be used as new password because it still exists in history");
		} catch (EpPasswordValidationException expected) {
			assertNotNull(expected.getMessage());
		}
		assertTrue(cmUserImpl.getPasswordHistoryItems().get(0).getOldPassword().equals(encodedNewPassword1));
		
		try {
		context.checking(new Expectations() {
			{
			allowing(mockPasswordEncoder).encodePassword(newPassword2, null);
			will(returnValue(encodedNewPassword2));
			}
		});
			cmUserImpl.setCheckedClearTextPassword(newPassword2);
			fail("newPassword2 can't be used as new password because it still exists in history");
		} catch (EpPasswordValidationException expected) {
			assertNotNull(expected.getMessage());
		}
		assertTrue(cmUserImpl.getPasswordHistoryItems().get(1).getOldPassword().equals(encodedNewPassword2));
		
		try {
		context.checking(new Expectations() {
			{
			allowing(mockPasswordEncoder).encodePassword(newPassword3, null);
			will(returnValue(encodedNewPassword3));
			}
		});
			cmUserImpl.setCheckedClearTextPassword(newPassword3);
			fail("newPassword3 can't be used as new password because it is equal to current user's password");
		} catch (EpPasswordValidationException expected) {
			assertNotNull(expected.getMessage());
		}
		
		final String encodedInitialPassword = "encodedInitialPassword";
		context.checking(new Expectations() {
			{
				allowing(mockPasswordEncoder).encodePassword(initialPassword, null);
				will(returnValue(encodedInitialPassword));
			}
		});
		cmUserImpl.setCheckedClearTextPassword(initialPassword);
		assertEquals("New password should be set successfuly",
				encodedInitialPassword, cmUserImpl.getPassword());
	}
	
	/**
	 * Tests that the new password is moved to history right after setting it.
	 */
	@Test
	public void testSetCheckedClearTextPassword3() {
		final String initialPassword = "initialPassword";
		
		final TimeService timeService = context.mock(TimeService.class);
		context.checking(new Expectations() {
			{
				allowing(timeService).getCurrentTime();
			}
		});
		stubGetBean("timeService", timeService);
		context.checking(new Expectations() {
			{

				allowing(mockPasswordEncoder).encodePassword(initialPassword, null);
				will(returnValue("encodedInitialPassword"));
			}
		});
		cmUserImpl.setClearTextPassword(initialPassword);
		cmUserImpl.setPasswordUsingPasswordEncoder(initialPassword);

		assertEquals("There should be 0 passwords in history after first login.",
				0, cmUserImpl.getPasswordHistoryItems().size());

		final String oldPassword = cmUserImpl.getPassword();
		final String newPassword = "newPassword1";
		final String encodedNewPassword = "encodedNewPassword1";
		context.checking(new Expectations() {
			{
				allowing(mockPasswordEncoder).encodePassword(newPassword, null);
				will(returnValue(encodedNewPassword));
			}
		});
		cmUserImpl.setCheckedClearTextPassword(newPassword);
		assertEquals("After changing password old should be moved to history",
				1, cmUserImpl.getPasswordHistoryItems().size());
		assertEquals("Password in history should be equal to changed password",
				oldPassword, cmUserImpl.getPasswordHistoryItems().get(0).getOldPassword());
	}
}
