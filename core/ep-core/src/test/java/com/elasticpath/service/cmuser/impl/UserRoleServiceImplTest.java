/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.cmuser.impl;

import static org.junit.Assert.assertSame;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.domain.cmuser.impl.UserRoleImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/**
 * Test <code>UserRoleServiceImpl</code>.
 */
public class UserRoleServiceImplTest extends AbstractEPTestCase {

	private UserRoleServiceImpl userRoleServiceImpl;

	private PersistenceEngine mockPersistenceEngine;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		userRoleServiceImpl = new UserRoleServiceImpl();
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		userRoleServiceImpl.setPersistenceEngine(mockPersistenceEngine);
	}

	/**
	 * Test method for {@link com.elasticpath.service.cmuser.impl.UserRoleServiceImpl#update(com.elasticpath.domain.cmuser.UserRole)}.
	 */
	@Test
	public void testUpdate() {
		final UserRole userRole = new UserRoleImpl();
		final UserRole updatedUserRole = new UserRoleImpl();
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).merge(with(same(userRole)));
				will(returnValue(updatedUserRole));
			}
		});
		final UserRole returnedUserRole = userRoleServiceImpl.update(userRole);
		assertSame(updatedUserRole, returnedUserRole);
	}

}
