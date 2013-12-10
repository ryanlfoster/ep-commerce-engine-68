/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.cmuser.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.CmUserSession;
import com.elasticpath.domain.dataimport.ImportJob;

/**
 * Test <code>CmUserSessionImpl</code>.
 */
public class CmUserSessionImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private CmUserSession cmUserSession;

	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception -- in case of any errors
	 */
	@Before
	public void setUp() throws Exception {
		cmUserSession = new CmUserSessionImpl();
	}

	/**
	 * Test case for get/set cm user.
	 */
	@Test
	public void testGetSetCmUser() {
		CmUser cmUserMock = context.mock(CmUser.class);
		CmUser cmUserProxy = cmUserMock;
		assertNull(cmUserSession.getCmUser());
		cmUserSession.setCmUser(cmUserProxy);
		assertEquals(cmUserSession.getCmUser(), cmUserProxy);
	}

	/**
	 * Test case for get/set import job.
	 */
	@Test
	public void testGetSetImportJob() {
		ImportJob importJobMock = context.mock(ImportJob.class);
		ImportJob importJobProxy = importJobMock;
		assertNull(cmUserSession.getImportJob());
		cmUserSession.setImportJob(importJobProxy);
		assertEquals(cmUserSession.getImportJob(), importJobProxy);
	}

}
