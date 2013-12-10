package com.elasticpath.persistence.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.elasticpath.persistence.dao.PropertyLoaderAware;

/** Test class for {@link PropertiesDaoLoaderFactoryImpl}. */
public class PropertiesDaoLoaderFactoryImplTest {

	private ApplicationContext appContext;

	/** Test initialization. */
	@Before
	public void initialize() {
		appContext = new ClassPathXmlApplicationContext("/properties-dao/property-dao-beans.xml");
	}

	/** Checks that singleton is confingurable. */
	@Test
	public void testSingleton() {
		Object object1 = appContext.getBean("singleton");
		Object object2 = appContext.getBean("singleton");
		assertSame(object1, object2);

		Object object3 = appContext.getBean("nonSingleton");
		Object object4 = appContext.getBean("nonSingleton");
		assertNotSame(object3, object4);
	}

	/** Loading should not falter if no property patterns are given. */
	@Test
	public void testNoPropertyFilesDefined() {
		assertNotNull(appContext.getBean("noPropertyFile"));
	}

	/** Tests loading when there is a single explicit file described. */
	@Test
	public void testLoadingSinglePropertyFile() {
		PropertyLoaderAware aware = appContext.getBean("singlePropertyFile", PropertyLoaderAware.class);
		assertNotNull(aware);
		assertEquals("Property file didn't get loaded/set correctly", "single value", aware.getProperty("single"));
	}

	/** Tests loading when there are multiple property files. */
	@Test
	public void testLoadingMultiplePropertyFiles() {
		PropertyLoaderAware aware = appContext.getBean("multiplePropertyFiles", PropertyLoaderAware.class);
		assertNotNull(aware);
		assertEquals("single.properties not loaded", "single value", aware.getProperty("single"));
		assertEquals("double.properties not loaded", "another value", aware.getProperty("double"));
	}

	/** Tests loading when there are multiple files matched via patterns. */
	@Test
	public void testLoadingMultiplePatternFile() {
		PropertyLoaderAware aware = appContext.getBean("multiplePropertyFiles", PropertyLoaderAware.class);
		assertNotNull(aware);
		assertEquals("single.properties not loaded in pattern", "single value", aware.getProperty("single"));
		assertEquals("double.properties not loaded in pattern", "another value", aware.getProperty("double"));
	}

	/** Implementation for tests. */
	public static class TestPropertyLoaderAwareBean extends AbstractPropertyLoaderAwareImpl {
		private static final long serialVersionUID = 1L;

	}
}
