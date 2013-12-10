/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.tools.sync.beanfactory;

import static org.junit.Assert.assertSame;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * Test cases for {@link ContextInitializerFactory}.
 */
public class ContextInitializerFactoryTest {

	private ContextInitializerFactory contextInitializerFactory;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 *
	 */
	@Before
	public void setUp() {
		contextInitializerFactory = new ContextInitializerFactory();
	}

	/**
	 * Tests that the arguments are required.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateNoConnectionType() {
		contextInitializerFactory.create(null, "something");
	}

	/**
	 * Tests that the arguments are required.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateNoDestinationType() {
		contextInitializerFactory.create("test", null);
	}

	/**
	 * If no context initializers are specified an exception is expected.
	 */
	@Test(expected = SyncToolConfigurationException.class)
	public void testCreateContextInitializerWhenNoneRegistered() {
		contextInitializerFactory.create("remote", "source");
	}

	/**
	 * If no context initializer matches the criteria are specified an exception is expected.
	 */
	@Test(expected = SyncToolConfigurationException.class)
	public void testCreateContextInitializerWhenNoneMatches() {
		contextInitializerFactory.setContextInitializersMap(Collections.<String, ContextInitializer>emptyMap());
		contextInitializerFactory.create("local", "source");
	}

	/**
	 * If an initializer matches the criteria then that's the one expected.
	 */
	@Test
	public void testCreateContextInitializerHappyCase() {
		Map<String, ContextInitializer> registered = new HashMap<String, ContextInitializer>();
		ContextInitializer contextInitializer = context.mock(ContextInitializer.class);
		registered.put("local.target", contextInitializer);
		contextInitializerFactory.setContextInitializersMap(registered);
		ContextInitializer result = contextInitializerFactory.create("local", "target");
		assertSame("The registered initializer is expected", contextInitializer, result);
	}

	/**
	 * If the initializer is badly registered (null value) an exception is expected.
	 */
	@Test(expected = SyncToolConfigurationException.class)
	public void testCreateContextInitializerBadRegistree() {
		Map<String, ContextInitializer> registered = new HashMap<String, ContextInitializer>();
		ContextInitializer contextInitializer = null;
		registered.put("local.target", contextInitializer);
		contextInitializerFactory.setContextInitializersMap(registered);
		contextInitializerFactory.create("local", "target");
	}

}
