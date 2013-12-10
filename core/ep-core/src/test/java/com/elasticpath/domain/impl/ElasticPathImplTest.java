/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.impl;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.beanframework.MessageSource;


/**
 * Test cases for <code>ElasticPathImpl</code>.
 */
public class ElasticPathImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ElasticPathImpl elasticPathImpl;
	


	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of any error
	 */
	@Before
	public void setUp() throws Exception {
		elasticPathImpl = new ElasticPathImpl();
	}

	/**
	 * Test the getBean calls are simply delegated to the underlying bean 
	 * factory.
	 */
	@Test
	public void testGetBean() {
		final String name = "testBean";
		final BeanFactory mockBeanFactory = context.mock(BeanFactory.class);
		context.checking(new Expectations() {
			{
				oneOf(mockBeanFactory).getBean(name);
			}
		});
		elasticPathImpl.setBeanFactory(mockBeanFactory);
		
		// Run the test.
		elasticPathImpl.getBean(name);
	}

	/**
	 * Test method for 'com.elasticpath.commons.context.impl.ElasticPathImpl.getBean(String)'.
	 */
	@Test
	public void testGetMessage() {
		final MessageSource mockMessageSource = context.mock(MessageSource.class);
		final String msgCode = "account.email";
		final Object[] args = new Object[] {};
		final String defaultMessage = "Email";
		final Locale locale = Locale.CANADA;
		context.checking(new Expectations() {
			{
				oneOf(mockMessageSource).getMessage(msgCode, args, defaultMessage, locale);
			}
		});
		elasticPathImpl.setMessageSource(mockMessageSource);
		
		// Run the test
		elasticPathImpl.getMessage(msgCode, args, defaultMessage, locale);
	}

	/**
	 * Test for 'com.elasticpath.commons.context.impl.ElasticPathImpl.getBoolMap()'.
	 */
	@Test
	public void testBoolMap() {
		assertNotNull(elasticPathImpl.getBoolMap().get(Boolean.TRUE));
	}

	
	/**
	 * Create a map from a vararg list of strings.
	 * @param keyThenValue key then value, key then value.
	 * @return a map with the keys tied to their value as appropriate.
	 */
	Map<String, String> createProps(final String... keyThenValue) {
		Map<String, String> testProperties = new HashMap<String, String>();
		for (int x = 0; x < keyThenValue.length; x = x + 2) {
			testProperties.put(keyThenValue[x], keyThenValue[x + 1]);	
		}
		return testProperties;
	}

}
