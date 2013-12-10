package com.elasticpath.web.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

import com.elasticpath.web.security.ParameterConfiguration.Policy;

/**
 * Test class for {@link DatabinderNamespaceHandler}. This test depends spring to automatically recognize and use our
 * handler during bean creation.
 */
public class DatabinderNamespaceHandlerTest {
	private static final int ESAPI_DEFAULT_MAXLENGTH = 2000;
	private static final Policy ESAPI_DEFAULT_POLICY = Policy.BLANK;
	private DefaultListableBeanFactory beanFactory;

	/** Test initialization. */
	@Before
	public void setUp() {
		beanFactory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
		reader.loadBeanDefinitions(new ClassPathResource("testDatabinderNamespace.xml"));
	}

	/** Makes sure defaults are setup correctly when not specified. */
	@Test
	public void testDefaults() {
		ParameterConfiguration config = (ParameterConfiguration) beanFactory.getBean("defaults");
		assertFalse("Using defaults should create a custom validator", config.isCustomValidator());
		assertEquals("Default max length strays from ESAPI defaults", ESAPI_DEFAULT_MAXLENGTH, config.getMaxLength());
		assertFalse("Default empties strays from ESAPI defaults", config.isAllowNulls());
		assertEquals("Policy strays from ESAPI defaults", ESAPI_DEFAULT_POLICY, config.getPolicy());
	}

	/** Makes sure all fields are found correctly. */
	@Test
	public void testAllSpecified() {
		ParameterConfiguration config = (ParameterConfiguration) beanFactory.getBean("all-specified");
		assertEquals("Validator rule was not set", "rule", config.getValidator());
		assertEquals("Max length was not set", 1, config.getMaxLength());
		assertTrue("Pattern was not set", config.matchesParameter("pattern"));
		assertTrue("Allow nulls was not set", config.isAllowNulls());
		assertEquals("Policy was not set", Policy.IGNORE, config.getPolicy());
	}
}
