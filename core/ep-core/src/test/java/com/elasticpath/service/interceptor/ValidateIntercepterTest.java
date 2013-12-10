/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.interceptor;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.validation.Errors;
import org.springmodules.validation.commons.DefaultBeanValidator;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.customer.Customer;

/**
 *
 * Test cases for <code>ValidateInterceptor</code>. Note that
 * DefaultBeanValidator is not an interface so it can't be easily mocked.
 *
 */
@SuppressWarnings("synthetic-access")
public class ValidateIntercepterTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private DefaultBeanValidator defaultBeanValidator;

	private ValidateInterceptor validateInterceptor;

	/**
	 * Set up the test cases.
	 *
	 * @throws Exception
	 *             if an errror occurs.
	 */
	@Before
	public void setUp() throws Exception {
		validateInterceptor = new ValidateInterceptor();
		defaultBeanValidator = new MockDefaultBeanValidator();
		validateInterceptor.setDefaultBeanValidator(defaultBeanValidator);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.interceptor.ValidateInterceptor.invoke(MethodInvocation)'.
	 *
	 * @throws Throwable in case of error
	 */
	@Test
	public final void testInvoke() throws Throwable {
		final MethodInvocation methodInvocationMock = context.mock(MethodInvocation.class);
		final EpDomain epDomainMock = context.mock(EpDomain.class);
		final Object[] args = new Object[1];
		EpDomain epDomainProxy = epDomainMock;
		args[0] = epDomainProxy;
		context.checking(new Expectations() {
			{
				allowing(epDomainMock).getClass();
				will(returnValue(Customer.class));

				oneOf(methodInvocationMock).getArguments();
				will(returnValue(args));

				oneOf(methodInvocationMock).proceed();
			}
		});

		validateInterceptor.invoke(methodInvocationMock);
	}

	/** Extends DefaultBeanValidator to stub out methods. */
	private class MockDefaultBeanValidator extends DefaultBeanValidator {

		@SuppressWarnings("rawtypes")
		public boolean supports(final Class clazz) {
			return true;
		}

		public void validate(final Object object, final Errors errors) {
			// don't throw exception to simulate success
		}
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.interceptor.ValidateInterceptor.invoke(MethodInvocation)'.
	 * @throws Throwable in case of error
	 */
	@Test
	public final void testInvokeNoArgs() throws Throwable {
		final MethodInvocation methodInvocationMock = context.mock(MethodInvocation.class);
		final Object[] args = new Object[0];
		context.checking(new Expectations() {
			{
				oneOf(methodInvocationMock).getArguments();
				will(returnValue(args));

				oneOf(methodInvocationMock).proceed();
			}
		});

		validateInterceptor.invoke(methodInvocationMock);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.interceptor.ValidateInterceptor.setDefaultBeanValidator(DefaultBeanValidator)'.
	 */
	@Test
	public final void testSetDefaultBeanValidator() {

		EpDomain epDomainMock = context.mock(EpDomain.class);

		validateInterceptor.setDefaultBeanValidator(null);
		try {
			validateInterceptor.validate(epDomainMock);
			fail("Should have thrown EPServiceException");
		} catch (EpDomainException e) {
			assertNotNull(e);
		}
	}

}
