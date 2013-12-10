package com.elasticpath.service.contentspace.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.ContentWrapperService;
import com.elasticpath.domain.contentspace.Parameter;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.service.contentspace.ParameterResolvingException;
import com.elasticpath.service.contentspace.ScriptEngine;
import com.elasticpath.service.contentspace.ScriptEngineFactory;
/**
 * Test for ParameterValueResolverImpl.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class ParameterValueResolverImplTest  {
	
	private static final String DOUBLE_VAR2 = "doubleVar2";
	private static final String DOUBLE_VAR1 = "doubleVar1";
	private static final String CODE = "code";
	private static final String PARAM4 = "param4";
	private static final String PARAM3 = "param3";
	private static final String GROOVY = "groovy";
	private static final String PARAM1 = "param1";
	private static final String VALUE1 = "value1";
	private ParameterValueResolverImpl resolver;
	private ScriptEngineFactory scriptEngineFactory;
	
	private List<ParameterValue> parameterValues;
	private List<Parameter> userParameters;
	private List<Parameter> tempParameters;
	private static final List<Parameter> NULL_PARAMETERS = null;
	private ContentWrapper contentWrapper;
	
	private static final String PRODUCT_CODE = "123456789";
	private static final int N_20 = 20;
	private static final int N_123 = 123;
	private static final int N_1 = 1;
	private static final int N_3 = 3;
	private static final int N_5 = 5;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Set up the test case.
	 */
	@Before
	public void setUp() {
		resolver = new ParameterValueResolverImpl();
		
		scriptEngineFactory = new ScriptEngineFactory();
		Map<String, Class< ? extends ScriptEngine> > engines = new HashMap<String, Class< ? extends ScriptEngine> >();
		engines.put(GROOVY, FutureTaskGroovyScriptEngineImpl.class);
		scriptEngineFactory.setValues(engines);		
	}
	
	/**
	 * test that template parameters and user settings are merging into 
	 * one list of parameters correctly.
	 * Test null template parameters and null user input parameters result in an empty list
	 */
	@Test
	public void testGetAllContentWrapperParametersAllNull() {
		
		final ContentWrapper contentWrapperNoParams = context.mock(ContentWrapper.class, "contentWrapperNoParams");
		context.checking(new Expectations() { {
			allowing(contentWrapperNoParams).getTemplateParameters(); will(returnValue(NULL_PARAMETERS));
			allowing(contentWrapperNoParams).getUserInputSettings(); will(returnValue(NULL_PARAMETERS));
		} });
		List<Parameter> resultNullNull;

		resultNullNull = resolver.getAllContentWrapperParameters(contentWrapperNoParams);
		assertNotNull(resultNullNull);
		assertEquals(0, resultNullNull.size());
		
	}
	
	/**
	 * test that template parameters and user settings are merging into 
	 * one list of parameters correctly.
	 * Test template parameters and null user input parameters result in list with template parameters
	 */
	@Test
	public void testGetAllContentWrapperParametersTemplateOnly() {

		final List<Parameter> withParameters = new ArrayList<Parameter>();		
		final Parameter parameter = context.mock(Parameter.class);
		withParameters.add(parameter);
		
		final ContentWrapper contentWrapperTemplateOnlyParams = context.mock(ContentWrapper.class, "contentWrapperTemplateOnlyParams");
		context.checking(new Expectations() { {
			allowing(contentWrapperTemplateOnlyParams).getTemplateParameters(); will(returnValue(withParameters));
			allowing(contentWrapperTemplateOnlyParams).getUserInputSettings(); will(returnValue(NULL_PARAMETERS));
		} });
		List<Parameter> resultTemplateOnly;

		resultTemplateOnly = resolver.getAllContentWrapperParameters(contentWrapperTemplateOnlyParams);
		assertNotNull(resultTemplateOnly);
		assertEquals(1, resultTemplateOnly.size());		
		
	}
	
	/**
	 * test that template parameters and user settings are merging into 
	 * one list of parameters correctly.
	 * Test null template parameters and user input parameters result in list with user input parameters 
	 */
	@Test
	public void testGetAllContentWrapperParametersUserInputOnly() {

		final List<Parameter> withParameters = new ArrayList<Parameter>();		
		final Parameter parameter = context.mock(Parameter.class);
		withParameters.add(parameter);
		
		final ContentWrapper contentWrapperUserInputOnlyParams = context.mock(ContentWrapper.class, "contentWrapperUserInputOnlyParams");
		context.checking(new Expectations() { {
			allowing(contentWrapperUserInputOnlyParams).getTemplateParameters(); will(returnValue(NULL_PARAMETERS));
			allowing(contentWrapperUserInputOnlyParams).getUserInputSettings(); will(returnValue(withParameters));
		} });
		List<Parameter> resultUserInputOnly;

		resultUserInputOnly = resolver.getAllContentWrapperParameters(contentWrapperUserInputOnlyParams);
		assertNotNull(resultUserInputOnly);
		assertEquals(1, resultUserInputOnly.size());		
		
	}
	
	/**
	 * test that template parameters and user settings are merging into 
	 * one list of parameters correctly.
	 * Test template parameters and user input parameters result in list with template and user input parameters 
	 */
	@Test
	public void testGetAllContentWrapperParametersBothOnly() {

		final List<Parameter> withParameters = new ArrayList<Parameter>();		
		final Parameter parameter = context.mock(Parameter.class);
		withParameters.add(parameter);
		
		final ContentWrapper contentWrapperUserInputOnlyParams = context.mock(ContentWrapper.class, "contentWrapperUserInputOnlyParams");
		context.checking(new Expectations() { {
			allowing(contentWrapperUserInputOnlyParams).getTemplateParameters(); will(returnValue(withParameters));
			allowing(contentWrapperUserInputOnlyParams).getUserInputSettings(); will(returnValue(withParameters));
		} });
		List<Parameter> resultUserInputOnly;

		resultUserInputOnly = resolver.getAllContentWrapperParameters(contentWrapperUserInputOnlyParams);
		assertNotNull(resultUserInputOnly);
		assertEquals(2, resultUserInputOnly.size());		
		
	}
	
	/**
	 * Test that if no parameters are supplied the result 
	 * will be an empty resolved parameter values map.
	 * 
	 * @throws ParameterResolvingException must not be raised
	 */
	@Test
	public void testResolveValuesNullArguments() throws ParameterResolvingException {
		String lang = null;
		final List<ParameterValue> parameterValues = null;
		final List<Parameter> parameters = null;
		final ContentWrapper contentWrapper = context.mock(ContentWrapper.class);
		context.checking(new Expectations() { {
			allowing(contentWrapper).getTemplateParameters(); will(returnValue(parameters));
			allowing(contentWrapper).getUserInputSettings(); will(returnValue(parameters));
		} });
		Map<String, String> result;

		result = resolver.resolveValues(contentWrapper, parameterValues, new HashMap<String, Object>(), lang);
		assertNotNull(result);
		assertTrue(result.isEmpty());			
	}

	/**
	 * Tests that if no lang and parameter values are provided but there is a parameter definition
	 * there will be a parameter with a null value.
	 * 
	 * @throws ParameterResolvingException should be thrown because parameters are required by default.
	 */
	@Test(expected = ParameterResolvingException.class)
	public void testResolveValuesNoLocaleNoParameterValues() throws ParameterResolvingException {
		String lang = null;
		
		final List<Parameter> withParameters = new ArrayList<Parameter>();		
		final Parameter parameter = context.mock(Parameter.class);
		withParameters.add(parameter);
		
		final ContentWrapper contentWrapper = context.mock(ContentWrapper.class);
		context.checking(new Expectations() { {
			allowing(parameter).getParameterId(); will(returnValue(PARAM1));
			allowing(parameter).getScriptExpression(); will(returnValue(null));
			allowing(parameter).isRequired(); will(returnValue(true));
			
			allowing(contentWrapper).getTemplateParameters(); will(returnValue(NULL_PARAMETERS));
			allowing(contentWrapper).getUserInputSettings(); will(returnValue(withParameters));
		} });
		
		resolver.resolveValues(contentWrapper, parameterValues, new HashMap<String, Object>(), lang);		
		
	}

	/**
	 * Tests that if parameter definition is not provided and parameters values exists 
	 * the result will be empty parameters value map. 
	 * @throws ParameterResolvingException should not be raised
	 */
	@Test
	public void testResolveValuesNoParameterDefinition() throws ParameterResolvingException {

		final String lang = "en";
		final ParameterValue parameterValue = context.mock(ParameterValue.class);
		
		final ContentWrapper contentWrapper = context.mock(ContentWrapper.class);
		context.checking(new Expectations() { {
			allowing(parameterValue).getValue(lang); will(returnValue(VALUE1));
			
			allowing(contentWrapper).getTemplateParameters(); will(returnValue(NULL_PARAMETERS));
			allowing(contentWrapper).getUserInputSettings(); will(returnValue(NULL_PARAMETERS));
		} });
		
		Map<String, String> result;
		
		List<ParameterValue> parameterValues = Arrays.asList(
				parameterValue
				);
		
		result = resolver.resolveValues(contentWrapper, parameterValues, new HashMap<String, Object>(), lang);
		assertNotNull(result);
		assertTrue(result.isEmpty());			
		
	}
	
	/**
	 * Test for resolve not localized parameters. I.e. provided lang is null 
	 * and result - resolved values for parameters.
	 * @throws ParameterResolvingException should not be thrown since parameters are not localizable by default.
	 */
	@Test
	public void testResolveValuesNoLocale() throws ParameterResolvingException {
		
		final String lang = null;
		final ParameterValue parameterValue = context.mock(ParameterValue.class);
		
		final Parameter parameter = context.mock(Parameter.class);
		List<ParameterValue> parameterValues = Arrays.asList(
				parameterValue
				);
		final List<Parameter> parameters = Arrays.asList(parameter);
		
		final ContentWrapper contentWrapper = context.mock(ContentWrapper.class);
		context.checking(new Expectations() { {
			allowing(parameterValue).getValue(lang); will(returnValue(VALUE1));
			allowing(parameterValue).getParameter(); will(returnValue(parameter));
			allowing(parameterValue).getParameterName(); will(returnValue(PARAM1));
			allowing(parameter).getParameterId(); will(returnValue(PARAM1));
			allowing(parameter).getScriptExpression(); will(returnValue(null));
			allowing(parameter).isRequired(); will(returnValue(true));
			allowing(parameter).isPassToTemplate(); will(returnValue(true));
			
			allowing(contentWrapper).getTemplateParameters(); will(returnValue(NULL_PARAMETERS));
			allowing(contentWrapper).getUserInputSettings(); will(returnValue(parameters));
		} });
		Map<String, String> result;

		result = resolver.resolveValues(contentWrapper, parameterValues, new HashMap<String, Object>(), lang);
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(PARAM1, result.keySet().iterator().next());
		assertEquals(VALUE1, result.values().iterator().next());			
		
	}
	
	/**
	 * Test for resolve localized parameters. I.e. provided lang is not null 
	 * and result - resolved values for parameters.
	 * @throws ParameterResolvingException should not be thrown
	 */
	@Test
	public void testResolveValuesWithLocale() throws ParameterResolvingException {
		
		final String lang = "en";
		final ParameterValue parameterValue = context.mock(ParameterValue.class);

		final Parameter parameter = context.mock(Parameter.class);
		final List<ParameterValue> parameterValues = Arrays.asList(
				parameterValue
				);
		final List<Parameter> parameters = Arrays.asList(parameter);
		
		final ContentWrapper contentWrapper = context.mock(ContentWrapper.class);
		context.checking(new Expectations() { {
			allowing(parameterValue).getValue(lang); will(returnValue(VALUE1));
			allowing(parameterValue).getParameter(); will(returnValue(parameter));
			allowing(parameterValue).getParameterName(); will(returnValue(PARAM1));
			allowing(parameter).getParameterId(); will(returnValue(PARAM1));
			allowing(parameter).getScriptExpression(); will(returnValue(null));
			allowing(parameter).isRequired(); will(returnValue(true));
			allowing(parameter).isPassToTemplate(); will(returnValue(true));
			
			allowing(contentWrapper).getTemplateParameters(); will(returnValue(NULL_PARAMETERS));
			allowing(contentWrapper).getUserInputSettings(); will(returnValue(parameters));
		} });
		Map<String, String> result;

		result = resolver.resolveValues(contentWrapper, parameterValues, new HashMap<String, Object>(), lang);
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(PARAM1, result.keySet().iterator().next());
		assertEquals(VALUE1, result.values().iterator().next());			
		
	}
	
	/**
	 * Test for resolve localized parameters with wrong lang. 
	 * I.e. values provided for one lang and not resolved for another lang. 
	 * Result - resolved values for parameters is null.
	 * @throws ParameterResolvingException should be thrown since no parameter vlaue for this locale is set
	 */
	@Test(expected = ParameterResolvingException.class)
	public void testResolveValuesWithWrongLocale() throws ParameterResolvingException {
		
		final String lang = "en";
		final String wrongLang = "pl";
		final ParameterValue parameterValue = context.mock(ParameterValue.class);

		final Parameter parameter = context.mock(Parameter.class);
		final List<ParameterValue> parameterValues = Arrays.asList(
				parameterValue
				);
		final List<Parameter> parameters = Arrays.asList(parameter);
		
		final ContentWrapper contentWrapper = context.mock(ContentWrapper.class);
		context.checking(new Expectations() { {
			allowing(parameterValue).getValue(lang); will(returnValue(VALUE1));
			allowing(parameterValue).getValue(wrongLang); will(returnValue(null));
			allowing(parameterValue).getParameter(); will(returnValue(parameter));
			allowing(parameterValue).getParameterName(); will(returnValue(PARAM1));
			allowing(parameter).getParameterId(); will(returnValue(PARAM1));
			allowing(parameter).getScriptExpression(); will(returnValue(null));
			allowing(parameter).isRequired(); will(returnValue(true));
			allowing(parameter).isPassToTemplate(); will(returnValue(true));
			
			allowing(contentWrapper).getTemplateParameters(); will(returnValue(NULL_PARAMETERS));
			allowing(contentWrapper).getUserInputSettings(); will(returnValue(parameters));
		} });
		
		resolver.resolveValues(contentWrapper, parameterValues, new HashMap<String, Object>(), wrongLang);
		
	}
	

	/**
	 * Tests that the result empty map of resolved parameters if the arguments are null.
	 * @throws ParameterResolvingException should not be thrown since no parameters are set
	 */
	@Test
	public void testIsParametersResolvedWithEitherArgumentNull() throws ParameterResolvingException {
		final ContentWrapper contentWrapper = context.mock(ContentWrapper.class);
		context.checking(new Expectations() { {
			allowing(contentWrapper).getTemplateParameters(); will(returnValue(NULL_PARAMETERS));
			allowing(contentWrapper).getUserInputSettings(); will(returnValue(NULL_PARAMETERS));
		} });
		
		assertNotNull(resolver.resolveValues(contentWrapper, new ArrayList<ParameterValue>(), new HashMap<String, Object>(), "pl"));
		assertNotNull(resolver.resolveValues(contentWrapper, null, new HashMap<String, Object>(), "pl"));
					
	}

	/**
	 * Tests to ensure that if the parameter definitions is empty, that the method returns
	 * true to indicate that the parameters have been resolved.
	 * @throws ParameterResolvingException should not be thrown since no parametrs are set
	 */
	@Test
	public void testParametersResolvedEmptyDefinitionsAndValues() throws ParameterResolvingException {
		final ContentWrapper contentWrapper = context.mock(ContentWrapper.class);
		context.checking(new Expectations() { {
			allowing(contentWrapper).getTemplateParameters(); will(returnValue(new ArrayList<Parameter>()));
			allowing(contentWrapper).getUserInputSettings(); will(returnValue(NULL_PARAMETERS));
		} });
		
		assertNotNull(resolver.resolveValues(contentWrapper, new ArrayList<ParameterValue>(), new HashMap<String, Object>(), "pl"));

	}
	
	/**
	 * Tests to ensure that if the parameter value is null for one of the matching parameter
	 * definitions for a content wrapper, that the method will return a false to indicate
	 * that all parameters have not been resolved. 
	 * @throws ParameterResolvingException must be thrown since value for one required parameter is null
	 */
	@Test(expected = ParameterResolvingException.class)
	public void testParametersResolvedWithNullParameterValue() throws ParameterResolvingException {
		
		final String lang = "pl";
		
		// create CW with two required parameters
		final Parameter param3 = context.mock(Parameter.class, PARAM3);
		final Parameter param4 = context.mock(Parameter.class, PARAM4);
		
		final List<Parameter> paramDefs = new ArrayList<Parameter>();
		paramDefs.add(param3);
		paramDefs.add(param4);
		

		
		// set one's value to null
		final ParameterValue paramValue3 = context.mock(ParameterValue.class, "paramValue3");
		// set one's value to a value		
		final ParameterValue paramValue4 = context.mock(ParameterValue.class, "paramValue4");
		
		List<ParameterValue> resolvedParameters = Arrays.asList(
				paramValue3,
				paramValue4);
		
		final ContentWrapper contentWrapper = context.mock(ContentWrapper.class);
		context.checking(new Expectations() { {
			allowing(contentWrapper).getTemplateParameters(); will(returnValue(paramDefs));
			allowing(contentWrapper).getUserInputSettings(); will(returnValue(NULL_PARAMETERS));
			
			allowing(paramValue3).getParameter(); will(returnValue(param3));
			allowing(paramValue3).getValue(lang); will(returnValue(null));
			allowing(paramValue3).getParameterName(); will(returnValue(PARAM3));
			allowing(param3).getParameterId(); will(returnValue(PARAM3));
			allowing(param3).getScriptExpression(); will(returnValue(null));
			allowing(param3).isRequired(); will(returnValue(true));
			allowing(param3).isPassToTemplate(); will(returnValue(true));
			
			allowing(paramValue4).getParameter(); will(returnValue(param4));
			allowing(paramValue4).getValue(lang); will(returnValue("value4"));
			allowing(paramValue4).getParameterName(); will(returnValue(PARAM4));
			allowing(param4).getParameterId(); will(returnValue(PARAM4));
			allowing(param4).getScriptExpression(); will(returnValue(null));
			allowing(param4).isRequired(); will(returnValue(true));
			allowing(param4).isPassToTemplate(); will(returnValue(true));
		} });
		
		resolver.resolveValues(contentWrapper, resolvedParameters, new HashMap<String, Object>(), lang);
		
	}
	
	/**
	 * Tests to ensure that if the parameter value has excess parameter values, and at least one
	 * parameter value for each parameter definition, that the method will return true indicating
	 * that all parameters have been resolved.
	 * @throws ParameterResolvingException must not be thrown since all parameters of CW are satisfied
	 */
	@Test
	public void testIsParameterResolvedWithExcessParameterValues() throws ParameterResolvingException {
		
		final String lang = "pl";
		
		// create CW with two required parameters
		final Parameter param3 = context.mock(Parameter.class, PARAM3);
		final Parameter param4 = context.mock(Parameter.class, PARAM4);
		final Parameter param5 = context.mock(Parameter.class, "param5");
		
		// set one's value to null
		final ParameterValue paramValue3 = context.mock(ParameterValue.class, "paramValue3");
		// set one's value to a value		
		final ParameterValue paramValue4 = context.mock(ParameterValue.class, "paramValue4");
		final ParameterValue paramValue5 = context.mock(ParameterValue.class, "paramValue5");
		
		final List<Parameter> paramDefs = new ArrayList<Parameter>();
		paramDefs.add(param3);
		paramDefs.add(param4);
		
		final List<ParameterValue> resolvedParameters = Arrays.asList(
				paramValue3,
				paramValue4,
				paramValue5);
		
		final ContentWrapper contentWrapper = context.mock(ContentWrapper.class);
		context.checking(new Expectations() { {
			allowing(contentWrapper).getTemplateParameters(); will(returnValue(paramDefs));
			allowing(contentWrapper).getUserInputSettings(); will(returnValue(NULL_PARAMETERS));
			
			allowing(paramValue3).getParameter(); will(returnValue(param3));
			allowing(paramValue3).getValue(lang); will(returnValue("value3"));
			allowing(paramValue3).getParameterName(); will(returnValue(PARAM3));
			allowing(param3).getParameterId(); will(returnValue(PARAM3));
			allowing(param3).getScriptExpression(); will(returnValue(null));
			allowing(param3).isRequired(); will(returnValue(true));
			allowing(param3).isPassToTemplate(); will(returnValue(true));
			
			allowing(paramValue4).getParameter(); will(returnValue(param4));
			allowing(paramValue4).getValue(lang); will(returnValue("value4"));
			allowing(paramValue4).getParameterName(); will(returnValue(PARAM4));
			allowing(param4).getParameterId(); will(returnValue(PARAM4));
			allowing(param4).getScriptExpression(); will(returnValue(null));
			allowing(param4).isRequired(); will(returnValue(true));
			allowing(param4).isPassToTemplate(); will(returnValue(true));
			
			allowing(paramValue5).getParameter(); will(returnValue(param5));
			allowing(paramValue5).getValue(lang); will(returnValue("value5"));
			allowing(paramValue5).getParameterName(); will(returnValue("param5"));
			allowing(param5).getParameterId(); will(returnValue("param5"));
			allowing(param5).getScriptExpression(); will(returnValue(null));
			allowing(param5).isRequired(); will(returnValue(true));
			allowing(param5).isPassToTemplate(); will(returnValue(true));
		} });
		
		Map<String, String> resolved = resolver.resolveValues(contentWrapper, resolvedParameters, new HashMap<String, Object>(), lang);
		
		assertNotNull(resolved);
		assertEquals(2, resolved.size());

	}
	

	/**
	 * Fill parameter - value list.
	 */
	private void fillParameterValues(final String lang) {
		
		final Parameter parameterName = context.mock(Parameter.class, "name");
		final Parameter parameterImage = context.mock(Parameter.class, "image");
		final Parameter parameterPrice = context.mock(Parameter.class, "price");
		
		final ParameterValue parameterValueCode = context.mock(ParameterValue.class, "codeVal");
		final Parameter parameterCode = context.mock(Parameter.class, CODE);		
		
		parameterValues = Arrays.asList(
				parameterValueCode
		);
		
		userParameters = Arrays.asList(
				parameterCode				
				);
		
		tempParameters = Arrays.asList(
				parameterName,
				parameterImage,
				parameterPrice
				);
		
		contentWrapper = context.mock(ContentWrapper.class);
		
		context.checking(new Expectations() { {
			allowing(contentWrapper).getTemplateParameters(); will(returnValue(tempParameters));
			allowing(contentWrapper).getUserInputSettings(); will(returnValue(userParameters));
			
			allowing(parameterName).getParameterId(); will(returnValue("name"));
			allowing(parameterName).getScriptExpression(); will(returnValue("product.getDisplayName(locale)"));
			allowing(parameterName).isRequired(); will(returnValue(true));
			allowing(parameterName).isPassToTemplate(); will(returnValue(true));
			
			allowing(parameterImage).getParameterId(); will(returnValue("image"));
			allowing(parameterImage).getScriptExpression(); will(returnValue("product.getImage()"));
			allowing(parameterImage).isRequired(); will(returnValue(true));
			allowing(parameterImage).isPassToTemplate(); will(returnValue(true));
			
			allowing(parameterPrice).getParameterId(); will(returnValue("price"));
			allowing(parameterPrice).getScriptExpression(); will(returnValue(
					"price = priceLookupService.getProductPrice(product, store.getCatalog(), store.getDefaultCurrency()).getLowestPrice(1);\n"
					+ "return moneyFormatter.formatCurrency(price, locale);"));
			allowing(parameterPrice).isRequired(); will(returnValue(true));
			allowing(parameterPrice).isPassToTemplate(); will(returnValue(true));
			
			allowing(parameterValueCode).getParameter(); will(returnValue(parameterCode));
			allowing(parameterValueCode).getValue(lang); will(returnValue(PRODUCT_CODE));
			allowing(parameterValueCode).getParameterName(); will(returnValue(CODE));
			allowing(parameterCode).getParameterId(); will(returnValue(CODE));
			allowing(parameterCode).getScriptExpression(); will(returnValue(null));
			allowing(parameterCode).isRequired(); will(returnValue(true));
			allowing(parameterCode).isPassToTemplate(); will(returnValue(true));
			
			
		} });
		

	}
	
	/**
	 * Minimal groovy test.
	 */
	@Test
	public void testGroovy() {
		Binding binding = new Binding();
		binding.setVariable("foo", Integer.valueOf(2));
		GroovyShell shell = new GroovyShell(binding);

		Object value = shell.evaluate("x = 123; return foo * 10");
		assertEquals(Integer.valueOf(N_20), value);
		assertEquals(binding.getVariable("x"), Integer.valueOf(N_123));
	}
	

	/**
	 * Test for detect dynamic parameter values in list.
	 */
	@Test
	public void testIsDynamicParameterValuesPresent() {
		
		fillParameterValues("en");
		
		ParameterValueResolverImpl parameterValueResolverImpl = new ParameterValueResolverImpl();
		
		List<Parameter> all = new ArrayList<Parameter>();
		all.addAll(tempParameters);
		all.addAll(userParameters);
		
		assertTrue(parameterValueResolverImpl.isDynamicParameterPresent(all));
		
		assertTrue(parameterValueResolverImpl.isDynamicParameterPresent(
				tempParameters));

		assertFalse(parameterValueResolverImpl.isDynamicParameterPresent(
				userParameters));
		
	}
	
	/**
	 * Test load static resolved values into the dynamic context.
	 */
	@Test
	public void testLoadStaticParameterValues() {
		
		fillParameterValues(null);
		
		ParameterValueResolverImpl parameterValueResolverImpl = new ParameterValueResolverImpl();
		
		Map<String, Object> binding = new HashMap<String, Object>();
		
		parameterValueResolverImpl.loadStaticParameterValues(parameterValues, null, binding);
		
		assertNotNull(binding.get(CODE));

		
	}
	
	/**
	 * Test for load spring bean into groovy context.
	 */
	@Test
	public void testLoadSpringBeansIntoGroovyBinding() {
		
		Map<String, Object> binding = new HashMap<String, Object>();
		
		final ContentWrapperService service1 = context.mock(ContentWrapperService.class, "service1");
		
		final ContentWrapper contentWrapper = context.mock(ContentWrapper.class);
		
		context.checking(new Expectations() { {
			allowing(contentWrapper).getTemplateParameters(); will(returnValue(NULL_PARAMETERS));
			allowing(contentWrapper).getUserInputSettings(); will(returnValue(NULL_PARAMETERS));
			allowing(contentWrapper).getServiceDefinitions(); will(returnValue(Arrays.asList(service1)));
			allowing(service1).getName(); will(returnValue("sName"));
			allowing(service1).getValue(); will(returnValue(ContextIdNames.PRICE_TIER));
		} });
		
		ParameterValueResolverImpl parameterValueResolverImpl = new ParameterValueResolverImpl() {
			@Override
			protected Object getServiceDefinitionBean(final String serviceBeanName) {
				return new PriceTierImpl();
			}
		};
		
		parameterValueResolverImpl.loadServiceDefinitions(contentWrapper, binding);
		
		assertNotNull(binding.get(service1.getName()));
		
		
		
	}
	
	/**
	 * Test for that init section in content wrapper is evaluated correctly and
	 * variables are preserved and accessible for use in template parameters.
	 * The below code is equivalent of the following content wrapper:
	 * 
	 * <pre>{@code
	 * <content-wrapper>
	 * 	<template-name>initSectionTest.vm</template-name>
	 * <wrapper-id>1003</wrapper-id>
	 * 	<wrapper-name>Init Sectin test</wrapper-name>
	 * 	<init>
	 * 		doubleVar1 = 1+1+1;
	 * 		doubleVar2 = Math.sqrt(4);
	 * 	</init>
	 * 	<template-parameters>
	 * 		<parameter-definition name="doubleVar1">
	 * 			<resolver>
	 * 				doubleVar1
	 * 			</resolver>
	 * 		</parameter-definition>
	 * 		<parameter-definition name="doubleVar2">
	 * 			<resolver>
	 * 				doubleVar2				
	 * 			</resolver>
	 * 		</parameter-definition>
	 * 		<parameter-definition name="rez">
	 * 			<resolver>
	 * 				doubleVar1 + doubleVar2
	 * 			</resolver>
	 * 		</parameter-definition>
	 * 	</template-parameters>
	 * </content-wrapper>
	 * }</pre>
	 * 
	 * @throws ParameterResolvingException should not be thrown
	 * 
	 */
	@Test
	public void testInitSectionAndDynamicValues() throws ParameterResolvingException {
		
		
		final Parameter parameterVar1 = context.mock(Parameter.class, DOUBLE_VAR1);
		final Parameter parameterVar2 = context.mock(Parameter.class, DOUBLE_VAR2);
		final Parameter parameterVar3 = context.mock(Parameter.class, "rez");
		
		final List<Parameter> parameters = Arrays.asList(
				parameterVar1,
				parameterVar2,
				parameterVar3
				);
		
		final ContentWrapper contentWrapper = context.mock(ContentWrapper.class);
		
		context.checking(new Expectations() { {
			allowing(contentWrapper).getTemplateParameters(); will(returnValue(parameters));
			allowing(contentWrapper).getUserInputSettings(); will(returnValue(NULL_PARAMETERS));
			allowing(contentWrapper).getInitSection(); will(returnValue("doubleVar1 = 1+1+1;\ndoubleVar2 = Math.sqrt(4);"));
			allowing(contentWrapper).getScriptLanguage(); will(returnValue(GROOVY));
			allowing(contentWrapper).getServiceDefinitions(); will(returnValue(null));
			
			allowing(parameterVar1).getParameterId(); will(returnValue(DOUBLE_VAR1));
			allowing(parameterVar1).getScriptExpression(); will(returnValue(DOUBLE_VAR1));
			allowing(parameterVar1).isRequired(); will(returnValue(true));
			allowing(parameterVar1).isPassToTemplate(); will(returnValue(true));
			
			allowing(parameterVar2).getParameterId(); will(returnValue(DOUBLE_VAR2));
			allowing(parameterVar2).getScriptExpression(); will(returnValue(DOUBLE_VAR2));
			allowing(parameterVar2).isRequired(); will(returnValue(true));
			allowing(parameterVar2).isPassToTemplate(); will(returnValue(true));
			
			allowing(parameterVar3).getParameterId(); will(returnValue("rez"));
			allowing(parameterVar3).getScriptExpression(); will(returnValue("doubleVar1 + doubleVar2"));
			allowing(parameterVar3).isRequired(); will(returnValue(true));
			allowing(parameterVar3).isPassToTemplate(); will(returnValue(true));
		} });

		ParameterValueResolverImpl parameterValueResolverImpl = new ParameterValueResolverImpl();
		
		parameterValueResolverImpl.setScriptEngineFactory(scriptEngineFactory);
		
		Map<String, String> resolvedValues;
		
		resolvedValues = parameterValueResolverImpl.resolveValues(
				contentWrapper, 
				new ArrayList<ParameterValue>(), 
				new HashMap<String, Object>(), null);
		assertEquals(N_3, resolvedValues.size());
		
		assertEquals(Integer.valueOf(N_3), Integer.valueOf(resolvedValues.get(DOUBLE_VAR1)));
		
		assertEquals(Float.valueOf(2), Float.valueOf(resolvedValues.get(DOUBLE_VAR2)));
		
		assertEquals(Float.valueOf(N_5), Float.valueOf(resolvedValues.get("rez")));
		
		
	}
	
	/**
	 * Test for that user input values provided for content wrapper is evaluated correctly and
	 * variables are preserved and accessible for use in template parameters.
	 * Two variable x ("hi") and y ("groovy") are user inputs will produce a concatenated string ("higroovy") 
	 * and store it in z variable for content wrapper template to use. The test also test that x and y
	 * variables are inaccessible to template since they both have passToTemplate == false.
	 */
	@Test
	public void testDynamicValues() {
		
		final String lang = "en";
		
		ParameterValueResolverImpl parameterValueResolverImpl = new ParameterValueResolverImpl();	
		parameterValueResolverImpl.setScriptEngineFactory(scriptEngineFactory);
		
		final Parameter paramX = context.mock(Parameter.class, "paramX");
		final Parameter paramY = context.mock(Parameter.class, "paramY");
		final Parameter paramZ = context.mock(Parameter.class, "paramZ");
		
		userParameters = Arrays.asList(
				paramX,
				paramY
		);
		
		tempParameters = Arrays.asList(
				paramZ
		);
		
		final ParameterValue parameterValueX = context.mock(ParameterValue.class, "x");
		final ParameterValue parameterValueY = context.mock(ParameterValue.class, "y");

		parameterValues = Arrays.asList(
				parameterValueX,
				parameterValueY
		);
	
		contentWrapper = context.mock(ContentWrapper.class);
		
		context.checking(new Expectations() { {
			allowing(contentWrapper).getTemplateParameters(); will(returnValue(tempParameters));
			allowing(contentWrapper).getUserInputSettings(); will(returnValue(userParameters));
			allowing(contentWrapper).getInitSection(); will(returnValue(null));
			allowing(contentWrapper).getScriptLanguage(); will(returnValue(GROOVY));
			allowing(contentWrapper).getServiceDefinitions(); will(returnValue(null));
			
			allowing(parameterValueX).getParameter(); will(returnValue(paramX));
			allowing(parameterValueX).getValue(lang); will(returnValue("hi"));
			allowing(parameterValueX).getParameterName(); will(returnValue("x"));
			allowing(paramX).getParameterId(); will(returnValue("x"));
			allowing(paramX).getScriptExpression(); will(returnValue(null));
			allowing(paramX).isRequired(); will(returnValue(true));
			allowing(paramX).isPassToTemplate(); will(returnValue(false));
			
			allowing(parameterValueY).getParameter(); will(returnValue(paramY));
			allowing(parameterValueY).getValue(lang); will(returnValue(GROOVY));
			allowing(parameterValueY).getParameterName(); will(returnValue("y"));
			allowing(paramY).getParameterId(); will(returnValue("y"));
			allowing(paramY).getScriptExpression(); will(returnValue(null));
			allowing(paramY).isRequired(); will(returnValue(true));
			allowing(paramY).isPassToTemplate(); will(returnValue(false));
			
			allowing(paramZ).getParameterId(); will(returnValue("z"));
			allowing(paramZ).getScriptExpression(); will(returnValue("x+y"));
			allowing(paramZ).isRequired(); will(returnValue(true));
			allowing(paramZ).isPassToTemplate(); will(returnValue(true));	
		} });
		
		try {
			

			Map<String, String> resolvedValues = parameterValueResolverImpl.resolveValues(
					contentWrapper, 
					parameterValues, 
					new HashMap<String, Object>(), lang);
			assertEquals(N_1, resolvedValues.size());
			assertEquals("higroovy", resolvedValues.get("z"));
		} catch (ParameterResolvingException e) {
			fail(e.getMessage());
		}

		
	}

}
