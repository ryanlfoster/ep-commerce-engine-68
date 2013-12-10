/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.cmuser.UserRoleService;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * A test case for verifying the functionality provided by
 * the CmUserServiceImpl.
 */
public class CmUserServiceImplTest extends BasicSpringContextTest {

	@Autowired
	@Qualifier("cmUserServiceWithFakeEmail")
	private CmUserService service;

	@Autowired
	private UserRoleService userRoleService;
	private SimpleStoreScenario scenario;

	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
	}

	/**
	 * Test that the service is capable of adding a new user.
	 */
	@DirtiesDatabase
	@Test
	public void testAdd() {
		CmUser cmUser = persistDefaultCmUser();
		
//		cmUser.addCatalog(scenario.getCatalog());
//		cmUser.addStore(scenario.getStore());
//		cmUser.addWarehouse(scenario.getWarehouse());
		
		CmUser retrievedCmUser = service.findByUserName(cmUser.getUserName());
		
		assertEquals(cmUser, retrievedCmUser);
		
	}

	/**
	 * Test that the update() method works properly and the CmUser object 
	 * holds all the data it is supposed to after the update.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdate() {
		CmUser cmUser = persistDefaultCmUser();
		
		cmUser.addStore(scenario.getStore());
		
		cmUser = service.update(cmUser);
		assertTrue(cmUser.getStores().contains(scenario.getStore()));

		cmUser = service.findByUserNameWithAccessInfo(cmUser.getUserName());
		assertTrue(cmUser.getStores().contains(scenario.getStore()));

		// load the catalog in another transaction
		CatalogService catalogService = getBeanFactory().getBean(ContextIdNames.CATALOG_SERVICE);
		Catalog catalog = catalogService.getCatalog(scenario.getCatalog().getUidPk());
		cmUser.addCatalog(catalog);
		
		cmUser = service.update(cmUser);
		assertTrue(cmUser.getCatalogs().contains(catalog));

		cmUser = service.findByUserNameWithAccessInfo(cmUser.getUserName());
		assertTrue(cmUser.getCatalogs().contains(catalog));

		// check setting a warehouse
		cmUser.addWarehouse(scenario.getWarehouse());
		
		cmUser = service.update(cmUser);
		assertTrue(cmUser.getWarehouses().contains(scenario.getWarehouse()));

		cmUser = service.findByUserNameWithAccessInfo(cmUser.getUserName());
		assertTrue(cmUser.getWarehouses().contains(scenario.getWarehouse()));

	}

	/**
	 * Tests that the service delete works properly.
	 */
	@DirtiesDatabase
	@Test
	public void testRemove() {
		CmUser cmUser = persistDefaultCmUser();
		cmUser.addStore(scenario.getStore());
		
		cmUser = service.update(cmUser);
		
		service.remove(cmUser);
		
		assertNull(service.findByUserName(cmUser.getUserName()));
	}

	/**
	 * Tests that emailExists() is not case sensitive.
	 */
	@DirtiesDatabase
	@Test
	public void testEmailExistsString() {
		CmUser cmUser = persistDefaultCmUser();

		String lowerCaseEmail = cmUser.getEmail().toLowerCase();
		assertTrue(service.emailExists(lowerCaseEmail));
	}

	/**
	 * Tests that userNameExists(String) is not case sensitive. 
	 */
	@DirtiesDatabase
	@Test
	public void testUserNameExistsString() {
		CmUser cmUser = persistDefaultCmUser();

		// convert to lower case to check case insensitivity
		String lowerCaseUserName = cmUser.getUsername().toLowerCase();
		assertTrue(service.userNameExists(lowerCaseUserName));
	}

	/**
	 * Tests that userNameExists(CmUser) is not case sensitive.
	 */
	@DirtiesDatabase
	@Test
	public void testUserNameExistsCmUser() {
		CmUser cmUser = persistDefaultCmUser();

		// convert to lower case to check case insensitivity
		String lowerCaseUserName = cmUser.getUsername().toLowerCase();
		String lowerCaseEmail = cmUser.getEmail().toLowerCase();
		CmUser newCmUser = getNewCmUser(lowerCaseUserName, lowerCaseEmail, "1111111111");
		assertTrue(service.userNameExists(newCmUser));
	}

	/**
	 * Tests that find by email is not case sensitive.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByEmail() {
		CmUser cmUser = persistDefaultCmUser();

		// convert to lower case to check case insensitivity
		String lowerCaseEmail = cmUser.getEmail().toLowerCase();
		
		assertEquals(cmUser, service.findByEmail(lowerCaseEmail));
	}

	/**
	 * Tests that findByEmail() gives correct result for a non-existent email.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByEmailNonExistentEmail() {
		assertNull(service.findByEmail("anEmail.that@doesnotexist.com"));
	}

	/**
	 * Tests that looking for CM user by username is case insensitive.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByUserName() {
		CmUser cmUser = persistDefaultCmUser();
		
		CmUser foundCmUser = service.findByUserName("useR1");
		
		assertNotNull(foundCmUser);
		assertEquals(cmUser, foundCmUser);


		foundCmUser = service.findByUserName("uSEr1");
		
		assertNotNull(foundCmUser);
		assertEquals(cmUser, foundCmUser);

	}
	
	/**
	 * Persists a CmUser.
	 */
	private CmUser persistDefaultCmUser() {
		String email = "tEst@TestUser.com";
		String userName = "UseR1";
		String password = "abcd1234";
		CmUser cmUser = getNewCmUser(userName, email, password);
		
		cmUser = service.add(cmUser);
		return cmUser;
	}

	/**
	 * Creates a new {@link CmUser}.
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
