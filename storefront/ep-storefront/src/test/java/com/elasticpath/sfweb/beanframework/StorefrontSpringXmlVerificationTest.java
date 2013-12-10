package com.elasticpath.sfweb.beanframework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test the validity of the Spring XML files.
 */
public class StorefrontSpringXmlVerificationTest {

	private ConfigurableApplicationContext context;

	/** Test spring xml. */
	@Test
	public void testSpringXml() {
		context = new ClassPathXmlApplicationContext("/spring/storefront-spring-verification.xml");
		assertTrue("The bean count should be greater than 0", context.getBeanDefinitionCount() > 0);
		String applicationName = (String) context.getBean("applicationName");
		assertEquals("The storefront application name should have been found", "Storefront", applicationName);
	}

	/** Closes a context after tests. */
	@After
	public void closeContext() {
		if (context != null) {
			context.close();
		}
	}

}

