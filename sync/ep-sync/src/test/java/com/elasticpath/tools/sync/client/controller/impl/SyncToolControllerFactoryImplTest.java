/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client.controller.impl;

import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tools.sync.client.SyncToolConfiguration;
import com.elasticpath.tools.sync.client.SyncToolControllerType;
import com.elasticpath.tools.sync.client.controller.SyncToolController;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * Test cases for {@link SyncToolControllerFactoryImpl}.
 */
public class SyncToolControllerFactoryImplTest {

	private SyncToolControllerFactoryImpl syncToolControllerFactory;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Sets up a test case.
	 */
	@Before
	public void setUp() {
		syncToolControllerFactory = new SyncToolControllerFactoryImpl();
	}

	/**
	 * Tests that a descriptive exception is thrown when no configuration has been set to the factory.
	 */
	@Test(expected = SyncToolConfigurationException.class)
	public void testCreateControllerNoConfigurationSet() {
		syncToolControllerFactory.createController();
	}

	/**
	 * Tests creating a controller when no registered controller of this type exists.
	 */
	@Test(expected = SyncToolConfigurationException.class)
	public void testCreateControllerNoRegisteredController() {
		final SyncToolConfiguration syncToolConfiguration = context.mock(SyncToolConfiguration.class);
		syncToolControllerFactory.setSyncToolConfiguration(syncToolConfiguration);
		
		context.checking(new Expectations() { { 
			oneOf(syncToolConfiguration).getControllerType();
			will(returnValue(SyncToolControllerType.EXPORT_CONTROLLER));
		} });
		syncToolControllerFactory.createController();
	}

	/**
	 * Tests creating a controller when it has been properly registered.
	 */
	@Test
	public void testCreateControllerHappyCase() {
		final SyncToolConfiguration syncToolConfiguration = context.mock(SyncToolConfiguration.class);
		syncToolControllerFactory.setSyncToolConfiguration(syncToolConfiguration);
		Map<String, SyncToolController> syncToolControllerBeans = new HashMap<String, SyncToolController>();
		SyncToolController syncController = context.mock(SyncToolController.class);
		syncToolControllerBeans.put(SyncToolControllerType.EXPORT_CONTROLLER.getName(), syncController);
		syncToolControllerFactory.setSyncToolControllerBeans(syncToolControllerBeans);
		context.checking(new Expectations() { { 
			oneOf(syncToolConfiguration).getControllerType();
			will(returnValue(SyncToolControllerType.EXPORT_CONTROLLER));
		} });
		SyncToolController result = syncToolControllerFactory.createController();
		
		assertSame(syncController, result);
	}

}
