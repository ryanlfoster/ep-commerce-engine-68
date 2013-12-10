package com.elasticpath.domain.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.context.ApplicationContext;


/**
 * Test for the ApplicationContextBackedBeanFactoryImpl that backs the ElasticPathImpl object.
 */
public class ApplicationContextBackedBeanFactoryImplTest {

	private static final String TEST_BEAN_NAME = "bean";

	private static final Integer TEST_INSTANCE = 1;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ApplicationContext mockApplicationContext;

	/**
	 * Object under test.
	 */
	private ApplicationContextBackedBeanFactoryImpl factory;
	
	/**
	 * Set up the mock domain bean factory to help testing.
	 */
	@Before
	public void setUp() {
		mockApplicationContext = context.mock(ApplicationContext.class);

		factory = new ApplicationContextBackedBeanFactoryImpl();
		factory.setApplicationContext(mockApplicationContext);
	}
		
	/**
	 * Test that a bean is retrieved correctly from the delegate.
	 */
	@Test
	public void testGetBeanForExistingNonPrototypeBean() {
		context.checking(new Expectations() {
			{
				allowing(mockApplicationContext).getBean(with(any(String.class)));
				will(returnValue(TEST_INSTANCE));
			}
		});
		assertSame(TEST_INSTANCE, factory.getBean(TEST_BEAN_NAME));
	}
	
	/**
	 * Make sure we get 'null' for non-registered bean.
	 */
	@Test
	public void testGetBeanNonRegisteredBean() {
		context.checking(new Expectations() {
			{
				allowing(mockApplicationContext).getBean(with(any(String.class)));
				will(returnValue(null));
			}
		});
		assertNull(factory.getBean(TEST_BEAN_NAME));
	}
	
	/**
	 * Test that getting an implementation class for a non-registered bean
	 * returns 'null'.
	 */
	@Test
	public void testGetBeanImplClassNonRegisteredBean() {
		context.checking(new Expectations() {
			{
				allowing(mockApplicationContext).getType(with(any(String.class)));
				will(returnValue(null));
			}
		});
		assertNull("Should get null for a non-registered object.", factory.getBeanImplClass(TEST_BEAN_NAME));
	}
	
	/**
	 * Test that a non-prototype bean has it's class returned correctly.
	 */
	@Test
	public void testGetBeanImplClassRegisteredNonPrototypeBean() {
		context.checking(new Expectations() {
			{
				allowing(mockApplicationContext).getType(with(any(String.class)));
				will(returnValue(TEST_INSTANCE.getClass()));
			}
		});
		assertEquals(Integer.class, factory.getBeanImplClass(TEST_BEAN_NAME));
	}
	
}
