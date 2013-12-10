package com.elasticpath.commons.beanframework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test the validity of the Spring XML files.
 */
public class CoreSpringXmlVerificationTest {

	private ConfigurableApplicationContext context;

	/** Test spring xml. */
	@Test
	public void testSpringXml() {
		context = new ClassPathXmlApplicationContext("/spring/core-spring-verification.xml");
		assertTrue("The bean count should be greater than 0", context.getBeanDefinitionCount() > 0);
		String applicationName = (String) context.getBean("applicationName");
		assertEquals("The core application name should have been found", "CoreOSGiBundle", applicationName);
	}

	/** Closes a context after tests. */
	@After
	public void closeContext() {
		if (context != null) {
			context.close();
		}
	}

	/** Test spring xml. */
	@Test
	public void testSpringOsgi() {
		context = new ClassPathXmlApplicationContext("/spring/core-osgi-verification.xml");
		assertTrue("The bean count should be greater than 0", context.getBeanDefinitionCount() > 0);
		String applicationName = (String) context.getBean("applicationName");
		assertEquals("The core application name should have been found", "CoreOSGiBundle", applicationName);
	}
}