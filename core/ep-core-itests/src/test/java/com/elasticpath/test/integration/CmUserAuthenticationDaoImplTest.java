/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.impl.CmUserAuthenticationDaoImpl;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.cmuser.UserRoleService;

/**
 * A test case for verifying that the {@link CmUserAuthenticationDaoImpl}
 * handles cm user login requests properly.
 */
public class CmUserAuthenticationDaoImplTest extends BasicSpringContextTest {

	private CmUserAuthenticationDaoImpl cmUserAuthenticationDao;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	@Qualifier("cmUserServiceWithFakeEmail")
	private CmUserService cmUserService;

	@Autowired
	@Qualifier("persistenceEngine")
	private PersistenceEngine persistenceEngine;

	@Before
	public void setUp() {
		cmUserAuthenticationDao = new CmUserAuthenticationDaoImpl();
		 cmUserAuthenticationDao.setPersistenceEngine(persistenceEngine);
	}

	/**
	 * Tests that the {@link CmUserAuthenticationDaoImpl} would recognize users
	 * in a case insensitive mode.
	 */
	@DirtiesDatabase
	@Test
	public void testLoadUserByUsername() {
		
		persistDefaultCmUser("UseR1");
		UserDetails userDetails = cmUserAuthenticationDao.loadUserByUsername("user1");
		assertNotNull(userDetails);
		
		userDetails = cmUserAuthenticationDao.loadUserByUsername("usEr1");
		assertNotNull(userDetails);
	}

	/**
	 *
	 * @return
	 */
	private CmUser persistDefaultCmUser(final String userName) {
		String email = "test@testuser.com";
		String password = "abcd1234";
		CmUser cmUser = getNewCmUser(userName, email, password);
		
		cmUser = cmUserService.add(cmUser);
		return cmUser;
	}

	/**
	 * Create a new {@link CmUser} object with required attributes set.
	 */
	private CmUser getNewCmUser(final String userName, final String email, final String password) {
		CmUser cmUser = getBeanFactory().getBean(ContextIdNames.CMUSER);
		
		cmUser.setClearTextPassword(password);
		cmUser.setConfirmClearTextPassword(password);
		cmUser.setCreationDate(new Date());
		cmUser.setEmail(email);
		cmUser.setEnabled(true);
		cmUser.setFirstName("test");
		cmUser.setLastName("test");
		cmUser.setUserName(userName);
		Collection<UserRole> userRoles = userRoleService.list();
		cmUser.setUserRoles(userRoles);
		
		return cmUser;
	}

}
