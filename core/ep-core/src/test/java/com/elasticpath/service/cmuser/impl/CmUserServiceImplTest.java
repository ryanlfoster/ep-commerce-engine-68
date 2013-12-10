/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.cmuser.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;

import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EmailExistException;
import com.elasticpath.commons.exception.EmailNonExistException;
import com.elasticpath.commons.security.impl.CmPasswordPolicyImpl;
import com.elasticpath.commons.util.impl.PasswordGeneratorImpl;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.domain.misc.impl.CmUserEmailPropertyHelperImpl;
import com.elasticpath.domain.misc.impl.EmailPropertiesImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.cmuser.UserNameExistException;
import com.elasticpath.service.cmuser.UserRoleService;
import com.elasticpath.service.misc.EmailService;
import com.elasticpath.service.order.impl.OrderReturnAndExchangeServiceTest.DummyTimeService;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test <code>CmUserServiceImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class CmUserServiceImplTest extends AbstractEPServiceTestCase {
	private static final String CMUSER_FIND_BY_EMAIL = "CMUSER_FIND_BY_EMAIL";
	private static final String CMUSER_COUNT_BY_EMAIL = "CMUSER_COUNT_BY_EMAIL";

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private static final String EMAIL_ADDRESS = "aaa@aaa.aaa";

	private static final String USER_NAME = "thisUserName";

	private CmUserServiceImpl cmUserServiceImpl;

	private PersistenceEngine mockPersistenceEngine;

	private EmailService mockEmailService;

	private UserRoleService mockUserRoleService;

	private IndexNotificationService mockIndexNotificationService;


	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		stubGetBean(ContextIdNames.CM_PASSWORDENCODER, new ShaPasswordEncoder());
		stubGetBean(ContextIdNames.EMAIL_PROPERTY_HELPER_CMUSER, CmUserEmailPropertyHelperImpl.class);
		stubGetBean(ContextIdNames.EMAIL_PROPERTIES, EmailPropertiesImpl.class);
		stubGetBean(ContextIdNames.CMUSER, CmUserImpl.class);
		stubGetBean(ContextIdNames.EMAIL_PROPERTY_HELPER_CMUSER, CmUserEmailPropertyHelperImpl.class);

		cmUserServiceImpl = new CmUserServiceImpl();
		mockPersistenceEngine = getMockPersistenceEngine();
		cmUserServiceImpl.setPersistenceEngine(mockPersistenceEngine);
		mockIndexNotificationService = context.mock(IndexNotificationService.class);
		cmUserServiceImpl.setIndexNotificationService(mockIndexNotificationService);
		mockEmailService = context.mock(EmailService.class);
		context.checking(new Expectations() {
			{
				allowing(mockEmailService).sendMail(with(any(EmailProperties.class)));
			}
		});
		cmUserServiceImpl.setEmailService(mockEmailService);

		mockUserRoleService = context.mock(UserRoleService.class);

		final SettingsService mockSettingsService = context.mock(SettingsService.class);

		final SettingValue mockMaxAgeValue = context.mock(SettingValue.class, "max age setting");
		context.checking(new Expectations() {
			{
				allowing(mockMaxAgeValue).getValue();
				will(returnValue("90"));

				allowing(mockSettingsService).getSettingValue("COMMERCE/APPSPECIFIC/RCP/maximumPasswordAge");
				will(returnValue(mockMaxAgeValue));
			}
		});

		final SettingValue mockMaxLoginAttempts = context.mock(SettingValue.class, "max login attempts setting");
		context.checking(new Expectations() {
			{
				allowing(mockMaxLoginAttempts).getValue();
				will(returnValue("6"));

				allowing(mockSettingsService).getSettingValue("COMMERCE/APPSPECIFIC/RCP/accountLockoutThreshold");
				will(returnValue(mockMaxLoginAttempts));
			}
		});

		final SettingValue mockMinPasswordLength = context.mock(SettingValue.class, "min password length setting");
		context.checking(new Expectations() {
			{
				allowing(mockMinPasswordLength).getValue();
				will(returnValue("8"));

				allowing(mockSettingsService).getSettingValue("COMMERCE/APPSPECIFIC/RCP/minimumPasswordLength");
				will(returnValue(mockMinPasswordLength));
			}
		});

		final SettingValue mockPasswordHistoryLength = context.mock(SettingValue.class, "password history length setting");
		context.checking(new Expectations() {
			{
				allowing(mockPasswordHistoryLength).getValue();
				will(returnValue("4"));

				allowing(mockSettingsService).getSettingValue("COMMERCE/APPSPECIFIC/RCP/passwordHistoryLength");
				will(returnValue(mockPasswordHistoryLength));
			}
		});

		CmPasswordPolicyImpl cmPasswordPolicy = new CmPasswordPolicyImpl();
		cmPasswordPolicy.setSettingsService(mockSettingsService);
		cmPasswordPolicy.setPasswordGenerator(new PasswordGeneratorImpl());

		stubGetBean("cmPasswordPolicy", cmPasswordPolicy);

		cmUserServiceImpl.setSettingsService(mockSettingsService);
		cmUserServiceImpl.setTimeService(new DummyTimeService());
	}

	private void userRoleSetup() {
		context.checking(new Expectations() {
			{
				allowing(mockUserRoleService).add(with(any(UserRole.class)));
			}
		});
	}

	private void setDefaultValue(final CmUser cmUser) {
		cmUser.initialize();
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.setPersistenceEngine(PersistenceEngine)'.
	 */
	@Test
	public void testSetPersistenceEngine() {
		cmUserServiceImpl.setPersistenceEngine(null);
		try {
			cmUserServiceImpl.add(new CmUserImpl());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(cmUserServiceImpl.getPersistenceEngine());
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.add(CmUser)'.
	 */
	@Test
	public void testAdd() {
		final CmUser cmUser = new CmUserImpl();
		this.setDefaultValue(cmUser);
		cmUser.setUserName(USER_NAME);
		cmUser.setEmail(EMAIL_ADDRESS);
		// expectations
		final Object[] findByEmailParams = new Object[] { cmUser.getEmail() };
		final Object[] findByUserIdParams = new Object[] { cmUser.getUserName() };
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).save(with(same(cmUser)));

				allowing(mockPersistenceEngine).retrieveByNamedQuery(CMUSER_COUNT_BY_EMAIL, findByEmailParams);
				will(returnValue(Arrays.asList(0L)));

				allowing(mockPersistenceEngine).retrieveByNamedQuery("CMUSER_COUNT_BY_USERNAME", findByUserIdParams);
				will(returnValue(Arrays.asList(0L)));

				oneOf(mockIndexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CMUSER, cmUser.getUidPk());
			}
		});

		this.userRoleSetup();
		cmUserServiceImpl.add(cmUser);
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.add(CmUser)' throws exception.
	 */
	@Test
	public void testAddFails() {
		final CmUser cmUser = new CmUserImpl();
		this.setDefaultValue(cmUser);
		cmUser.setUserName(USER_NAME);
		cmUser.setEmail(EMAIL_ADDRESS);

		try {
			new CmUserServiceImpl() {
				@Override
				protected void sanityCheck() throws EpServiceException {
					// empty
				}
				@Override
				public boolean userNameExists(final String userName) throws EpServiceException {
					return true;
				}
			} .add(cmUser);
			fail("UserNameExistException must be thrown");
		} catch (UserNameExistException expected) {
			assertNotNull(expected);
		}

		try {
			new CmUserServiceImpl() {
				@Override
				protected void sanityCheck() throws EpServiceException {
					// empty
				}
				@Override
				public boolean userNameExists(final String userName) throws EpServiceException {
					return false;
				}
				@Override
				public boolean emailExists(final String email) throws EpServiceException {
					return true;
				}
			} .add(cmUser);
			fail("EmailExistException must be thrown");
		} catch (EmailExistException expected) {
			assertNotNull(expected);
		}
	}


	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.update(CmUser)'.
	 */
	@Test
	public void testUpdate() {
		final CmUser cmUser = new CmUserImpl();
		final CmUser updatedCmUser = new CmUserImpl();
		this.setDefaultValue(cmUser);
		this.setDefaultValue(updatedCmUser);
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).merge(with(same(cmUser)));
				will(returnValue(updatedCmUser));
			}
		});
		this.userRoleSetup();
		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CMUSER, cmUser.getUidPk());
			}
		});
		final CmUser returnedCmUser = cmUserServiceImpl.update(cmUser);
		assertSame(updatedCmUser, returnedCmUser);
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.update(CmUser)' throws exception.
	 */
	@Test
	public void testUpdateFails() {
		final CmUser cmUser = new CmUserImpl();
		this.setDefaultValue(cmUser);
		cmUser.setUserName(USER_NAME);
		cmUser.setEmail(EMAIL_ADDRESS);

		try {
			new CmUserServiceImpl() {
				@Override
				protected void sanityCheck() throws EpServiceException {
					// empty
				}
				@Override
				public boolean userNameExists(final CmUser cmUser) throws EpServiceException {
					return true;
				}
			} .update(cmUser);
			fail("UserNameExistException must be thrown");
		} catch (UserNameExistException expected) {
			assertNotNull(expected);
		}

		try {
			new CmUserServiceImpl() {
				@Override
				protected void sanityCheck() throws EpServiceException {
					// empty
				}
				@Override
				public boolean userNameExists(final CmUser cmUser) throws EpServiceException {
					return false;
				}
				@Override
				public boolean emailExists(final CmUser cmUser) throws EpServiceException {
					return true;
				}
			} .update(cmUser);
			fail("EmailExistException must be thrown");
		} catch (EmailExistException expected) {
			assertNotNull(expected);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.delete(CmUser)'.
	 */
	@Test
	public void testDelete() {
		final CmUser cmUser = new CmUserImpl();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).delete(with(same(cmUser)));
			}
		});
		cmUserServiceImpl.remove(cmUser);
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.userNameExists(String)'.
	 */
	@Test
	public void testUserNameExists() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery("CMUSER_COUNT_BY_USERNAME", USER_NAME);
				will(returnValue(Arrays.asList(0L)));
			}
		});
		cmUserServiceImpl.userNameExists(USER_NAME);

		final String anotherUserName = USER_NAME + USER_NAME;
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery("CMUSER_COUNT_BY_USERNAME", anotherUserName);
				will(returnValue(Arrays.asList(1L)));
			}
		});
		cmUserServiceImpl.userNameExists(anotherUserName);
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.emailExists(String)'.
	 */
	@Test
	public void testEmailExists() {
		final String existEmailAddress = "exist_email_address@xxx.xxx";
		final Object[] parameters = new Object[] { existEmailAddress };
		final CmUser cmUser = new CmUserImpl();
		cmUser.setEmail("exist_email_address@xxx.xxx");
		final List<Long> cmUserList = new ArrayList<Long>();
		cmUserList.add(1L);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(CMUSER_COUNT_BY_EMAIL, parameters);
				will(returnValue(cmUserList));
			}
		});
		assertTrue(cmUserServiceImpl.emailExists(existEmailAddress));
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.emailExists(String)'.
	 */
	@Test
	public void testEmailExistsWithNullReturn() {
		final String nonExistEmailAddress = "nonexist_email_address@xxx.xxx";
		final Object[] parameters = new Object[] { nonExistEmailAddress };
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(CMUSER_COUNT_BY_EMAIL, parameters);
				will(returnValue(Arrays.asList(0L)));
			}
		});
		assertFalse(cmUserServiceImpl.emailExists(nonExistEmailAddress));
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.list()'.
	 */
	@Test
	public void testList() {
		final CmUser cmUser1 = new CmUserImpl();
		cmUser1.setFirstName("aaa");
		final CmUser cmUser2 = new CmUserImpl();
		cmUser2.setFirstName("bbb");
		final List<CmUser> cmUserList = new ArrayList<CmUser>();
		cmUserList.add(cmUser1);
		cmUserList.add(cmUser2);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(with("CMUSER_SELECT_ALL"), with(any(Object[].class)));
				will(returnValue(cmUserList));
			}
		});
		final List<CmUser> retrievedCmUserList = cmUserServiceImpl.list();
		assertEquals(cmUserList, retrievedCmUserList);
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		final long uid = 1234L;
		final CmUser cmUser = new CmUserImpl();
		cmUser.setFirstName("aaa");
		cmUser.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).load(CmUserImpl.class, uid);
				will(returnValue(cmUser));
			}
		});
		final CmUser loadedCmUser = cmUserServiceImpl.load(uid);
		assertSame(cmUser, loadedCmUser);
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoadAnNonExistCmUser() {
		final long uid = 1234L;
		final CmUser cmUser = new CmUserImpl();
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).load(CmUserImpl.class, uid);
				will(returnValue(cmUser));
			}
		});
		assertSame(cmUser, cmUserServiceImpl.load(uid));
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.findByEmail(String)'.
	 */
	@Test
	public void testEmailExistsWithMultipleReturns() {
		final Object[] parameters = new Object[] { EMAIL_ADDRESS };
		final List<Long> countlist = new ArrayList<Long>();
		countlist.add(2L);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(CMUSER_COUNT_BY_EMAIL, parameters);
				will(returnValue(countlist));
			}
		});

		assertTrue(cmUserServiceImpl.emailExists(EMAIL_ADDRESS));
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.findByEmail(String)'.
	 */
	@Test
	public void testFindByEmail() {
		final String email = EMAIL_ADDRESS;
		final Object[] parameters = new Object[] { email };
		final CmUser cmUser = new CmUserImpl();
		cmUser.setEmail(email);
		final List<CmUser> cmUserList = new ArrayList<CmUser>();
		cmUserList.add(cmUser);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(CMUSER_FIND_BY_EMAIL, parameters);
				will(returnValue(cmUserList));
			}
		});

		final CmUser retrievedCmUser = cmUserServiceImpl.findByEmail(email);
		assertSame(cmUser, retrievedCmUser);
	}

	/**
	 * https://www.salesforce.com/login.jsp Test method for 'com.elasticpath.service.CmUserServiceImpl.findByEmail(String)'.
	 */
	@Test
	public void testFindByEmailWithNullEmail() {
		try {
			cmUserServiceImpl.findByEmail(null);
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.findByEmail(String)'.
	 */
	@Test
	public void testFindByEmailWithNullReturn() {
		final String email = EMAIL_ADDRESS;
		final Object[] parameters = new Object[] { email };

		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(CMUSER_FIND_BY_EMAIL, parameters);
				will(returnValue(Collections.emptyList()));
			}
		});
		assertNull(cmUserServiceImpl.findByEmail(email));
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.findByEmail(String)'.
	 */
	@Test
	public void testFindByEmailWithMultipleReturns() {
		final String email = EMAIL_ADDRESS;
		final Object[] parameters = new Object[] { email };
		final CmUser cmUser1 = new CmUserImpl();
		cmUser1.setEmail(email);
		final CmUser cmUser2 = new CmUserImpl();
		cmUser2.setEmail(email);
		final List<CmUser> cmUserList = new ArrayList<CmUser>();
		cmUserList.add(cmUser1);
		cmUserList.add(cmUser2);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(CMUSER_FIND_BY_EMAIL, parameters);
				will(returnValue(cmUserList));
			}
		});

		try {
			cmUserServiceImpl.findByEmail(email);
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.resetPassword(String)'.
	 */
	@Test
	public void testResetPassword() {
		final String email = "jeffrey.ai@elasticpath.com";
		final Object[] parameters = new Object[] { email };
		final CmUserImpl cmUser = new CmUserImpl();
		this.setDefaultValue(cmUser);
		cmUser.setEmail(email);

		final List<CmUser> cmUserList = new ArrayList<CmUser>();
		cmUserList.add(cmUser);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(CMUSER_FIND_BY_EMAIL, parameters);
				will(returnValue(cmUserList));


				oneOf(mockPersistenceEngine).merge(with(same(cmUser)));
				will(returnValue(cmUser));

				oneOf(mockIndexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CMUSER, cmUser.getUidPk());
			}
		});

		stubGetBean("cmUserService", cmUserServiceImpl);

		this.userRoleSetup();
		this.cmUserServiceImpl.resetUserPassword(email);
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.resetPassword(String)'.
	 */
	@Test
	public void testResetPasswordWithNonExistEmailAddress() {
		final String email = "jeffrey.ai@elasticpath.com";
		final Object[] parameters = new Object[] { email };

		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(CMUSER_FIND_BY_EMAIL, parameters);
				will(returnValue(Collections.emptyList()));
			}
		});

		try {
			this.cmUserServiceImpl.resetUserPassword(email);
			fail("Expects an EmailNonExistException.");
		} catch (final EmailNonExistException e) {
			// succeed
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.CmUserServiceImpl.removePriceListGuidsFrom(String)'.
	 */
	@Test
	public void testRemovePriceListGuidsFrom() {
		final CmUser cmUser = new CmUserImpl();
		final String priceListGuid = "test_guid";
		cmUser.addPriceList(priceListGuid);
		final List<CmUser> users = new ArrayList<CmUser>();
		users.add(cmUser);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(with("CMUSER_SELECT_ALL"), with(any(Object[].class)));
				will(returnValue(users));

				oneOf(mockPersistenceEngine).merge(with(same(cmUser)));
				will(returnValue(cmUser));

				oneOf(mockIndexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CMUSER, cmUser.getUidPk());
			}
		});

		cmUserServiceImpl.removePriceListFromUsers(priceListGuid);
	}

}
