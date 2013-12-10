package com.elasticpath.domain.payment.impl;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.payment.gateway.impl.UnresolvablePaymentGatewayPluginImpl;
import com.elasticpath.service.payment.gateway.impl.UnresolvablePaymentGatewayPluginImpl.UnresolvedPluginException;

/**
 * Test for {@link UnresolvablePaymentGatewayPlugin}.
 */
public class UnresolvablePaymentGatewayPluginTest {
	private static final String TEST_PLUGIN_TYPE = "testPluginType";
	private UnresolvablePaymentGatewayPluginImpl unresolvablePaymentGatewayPlugin;
	
	/**
	 * Initialize object under test.
	 */
	@Before
	public void initializeObjectUnderTest() {
		unresolvablePaymentGatewayPlugin = new UnresolvablePaymentGatewayPluginImpl(TEST_PLUGIN_TYPE);
	}
	
	/**
	 * Ensure plugin type returned is requested plugin type.
	 */
	@Test
	public void ensurePluginTypeReturnedIsRequestedPluginType() {
		assertEquals("Error: plug in types do not match", TEST_PLUGIN_TYPE, unresolvablePaymentGatewayPlugin.getPluginType());
	}

	/**
	 * Ensure supported credit card list is empty.
	 */
	@Test
	public void ensureSupportedCreditCardListIsEmpty() {
		assertTrue("No supported credit cards should be returned", unresolvablePaymentGatewayPlugin.getSupportedCardTypes().isEmpty());
	}
	
	/**
	 * Ensure configuration parameter list is empty.
	 */
	@Test
	public void ensureConfigurationParameterListIsEmpty() {
		assertTrue("No configuration parameters should be returned", unresolvablePaymentGatewayPlugin.getConfigurationParameters().isEmpty());
	}

	/**
	 * Ensure payment type is correct.
	 */
	@Test
	public void ensurePaymentTypeIsCorrect() {
		assertEquals("Error: plug in types do not match", PaymentType.CREDITCARD, unresolvablePaymentGatewayPlugin.getPaymentType());
	}
	
	/**
	 * Ensure plugin is not resolved.
	 */
	@Test
	public void ensurePluginIsNotResolved() {
		assertFalse("Unresolved plugin should not be resolved", unresolvablePaymentGatewayPlugin.isResolved());
	}
	
	/**
	 * Ensure unsupported methods throw exception.
	 *
	 * @throws Exception any exception thrown by reflection framework.
	 */
	@Test
	public void ensureUnsupportedMethodsThrowException() throws Exception {
		
		List<String> unsupportedMethodNames = new ArrayList<String>(Arrays.asList(
				"isCvv2ValidationEnabled", 
				"checkEnrollment",
				"validateAuthentication"));
		
		assertPluginHasMethods(unsupportedMethodNames);
		assertUnsupportedMethodsThrowException(unsupportedMethodNames);
	}

	private void assertPluginHasMethods(final List<String> unsupportedMethodNames) {
		List<String> methodNames = getMethodNamesFromClass(unresolvablePaymentGatewayPlugin.getClass());
		
		for (String unsupportedMethodName : unsupportedMethodNames) {
			assertTrue("Missing method: " + unsupportedMethodName, methodNames.contains(unsupportedMethodName));
		}
	}

	private List<String> getMethodNamesFromClass(final Class<?> clazz) {
		List<Method> methods = Arrays.asList(clazz.getMethods());
		
		List<String> methodNames = new ArrayList<String>();
		for (Method method : methods) {
			methodNames.add(method.getName());
		}
		return methodNames;
	}

	private void assertUnsupportedMethodsThrowException(final List<String> unsupportedMethodNames) throws IllegalAccessException {
		List<Method> methods = Arrays.asList(unresolvablePaymentGatewayPlugin.getClass().getMethods());
		
		for (Method method : methods) {
			String methodNameUnderTest = method.getName();
			if (unsupportedMethodNames.contains(methodNameUnderTest)) {
				assertMethodThrowsCorrectException(method);					
			}
		}
	}

	private void assertMethodThrowsCorrectException(final Method method)
			throws IllegalAccessException {
		try {
			Class<?>[] parameterTypes = method.getParameterTypes();
			method.invoke(unresolvablePaymentGatewayPlugin, placeholderParameters(parameterTypes));
			fail("Method " + method.getName() + " should thrown an exception");
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			assertTrue(cause instanceof UnresolvedPluginException);
			String exceptionMessage = cause.getMessage();
			assertThat("Incorrect exception message", exceptionMessage, containsString(TEST_PLUGIN_TYPE));
		}
	}

	private Object[] placeholderParameters(final Class<?>[] parameterTypes) {
		return new Object[parameterTypes.length];
	}
}
