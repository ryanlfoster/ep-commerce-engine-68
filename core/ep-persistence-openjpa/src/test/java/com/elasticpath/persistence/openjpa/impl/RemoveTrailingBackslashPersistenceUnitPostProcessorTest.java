/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.persistence.openjpa.impl;


import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * Test that the RemoveTrailingBackslashPersistenceUnitPostProcessor behaves as expected.
 */
public class RemoveTrailingBackslashPersistenceUnitPostProcessorTest {

	private PersistenceUnitPostProcessor processor;

	/**
	 * Set up required for each test.
	 */
	@Before
	public void setUp() {
		processor = new RemoveTrailingBackslashPersistenceUnitPostProcessor();
	}

	/**
	 * Test that a trailing slash gets removed.
	 */
	@Test
	public void testSlashGetsRemoved() {
		MutablePersistenceUnitInfo pui = new MutablePersistenceUnitInfo();
		URL url = null;
		try {
			url = new URL("file:/root/somefile.jar/");
		} catch (MalformedURLException e) {
			fail("unexpected exception: " + e.getMessage());
		}
	    pui.setPersistenceUnitRootUrl(url);
		processor.postProcessPersistenceUnitInfo(pui);
	    assertEquals("The url should have the slash removed", "file:/root/somefile.jar", pui.getPersistenceUnitRootUrl().toString());
	}

	/**
	 * Test that if there is no trailing slash nothing gets changed.
	 */
	@Test
	public void testNothingHappensWithNoSlash() {
		MutablePersistenceUnitInfo pui = new MutablePersistenceUnitInfo();
		URL url = null;
		try {
			url = new URL("file:/root/somefile.jar");
		} catch (MalformedURLException e) {
			fail("unexpected exception: " + e.getMessage());
		}
	    pui.setPersistenceUnitRootUrl(url);
		processor.postProcessPersistenceUnitInfo(pui);
	    assertSame("The url not have changed if there was no slash", url, pui.getPersistenceUnitRootUrl());
	}


}
