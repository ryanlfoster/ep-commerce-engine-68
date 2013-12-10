package com.elasticpath.domain.contentspace.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.contentspace.Parameter;
import com.elasticpath.domain.contentspace.ParameterLocaleDependantValue;

/**
 * A test case for {@link ParameterValueImpl}.
 */
public class ParameterValueImplTest {
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final String TEST_VALUE = "test2";
	private ParameterValueImpl parameterValue;
	private ElasticPath elasticPath;
	private Parameter userInputParameter;

	/**
	 * Sets up the test case.
	 * 
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		
		elasticPath = context.mock(ElasticPath.class);
		
		userInputParameter = context.mock(Parameter.class, "userInputParameter");
		
		parameterValue = new ParameterValueImpl() {
			private static final long serialVersionUID = 8108527056961199725L;

			public ElasticPath getElasticPath() {
				return elasticPath;
			}
			
			protected ParameterLocaleDependantValue getNewParameterLocaleDependantValue() {
				return new ParameterLocaleDependantValueImpl();
			}
		};
	}

	/**
	 * Tests that even if the parameter value does not have associated 
	 * Parameter instance the result could still be retrieved.
	 */
	@Test
	public void testSetParameterValueNoParameter() {
		
		context.checking(new Expectations() { {
			oneOf(elasticPath).getBean(ContextIdNames.DYNAMIC_CONTENT_WRAPPER_USER_INPUT_PARAMETER);
			will(returnValue(userInputParameter));
			oneOf(userInputParameter).setParameterId(null);
			oneOf(userInputParameter).setDescription(null);
			oneOf(userInputParameter).setLocalizable(false);
			allowing(userInputParameter).isLocalizable(); will(returnValue(false));
		} });
		
		parameterValue.setValue("test123", "en");
		assertEquals("test123", parameterValue.getValue("en"));
	}
	
	/**
	 * Test that if a localized value exists then 
	 * it should be able to be retrieved. 
	 */
	@Test
	public void testGetSetValueWithSpecifiedLanguage() {
		context.checking(new Expectations() { {
			allowing(userInputParameter).isLocalizable(); will(returnValue(true));
		} });
		
		parameterValue.setParameter(userInputParameter);

		String locale = "en";
		String value = "testvalue";
		parameterValue.setValue(value, locale);
		
		assertEquals(value, parameterValue.getValue(locale));
	}

	/**
	 * Test that even when setting the value with a null language
	 * the value could be retrieved with the same value of the language.
	 */
	@Test
	public void testGetSetValueNullLanguage() {
		context.checking(new Expectations() { {
			allowing(userInputParameter).isLocalizable(); will(returnValue(true));
		} });
		
		parameterValue.setParameter(userInputParameter);
		
		String locale = null;
		String value = "testvalue";
		parameterValue.setValue(value, locale);
		
		assertEquals(value, parameterValue.getValue(locale));
	}


	/**
	 * Test that when setting the value of a non-localized parameter
	 * the value could be retrieved properly.
	 */
	@Test
	public void testGetSetValueNullLanguageNonLocalizedParameter() {
		context.checking(new Expectations() { {
			allowing(userInputParameter).isLocalizable(); will(returnValue(false));
		} });
		
		parameterValue.setParameter(userInputParameter);
		
		String locale = null;
		String value = "test";
		parameterValue.setValue(value, locale);
		
		assertEquals(value, parameterValue.getValue(locale));
	}

	/**
	 * Test that non required and non localizable parameter without value, return empty string
	 * instead null.
	 */
	@Test
	public void testGetEmptyValueFromNonRequeredNonLocalizableParameter() {
		context.checking(new Expectations() { {
			allowing(userInputParameter).isLocalizable(); will(returnValue(false));
			allowing(userInputParameter).isRequired(); will(returnValue(false));
		} });
		
		parameterValue.setParameter(userInputParameter);
		
		assertEquals("", parameterValue.getValue(null));
		
	}
	
	/**
	 * Test that required and non localizable parameter without value, return null 
	 * instead empty string.
	 */
	@Test
	public void testGetNullFromNonRequeredNonLocalizableParameter() {
		
		context.checking(new Expectations() { {
			allowing(userInputParameter).isLocalizable(); will(returnValue(false));
			allowing(userInputParameter).isRequired(); will(returnValue(true));
		} });
		
		parameterValue.setParameter(userInputParameter);
		assertEquals(null, parameterValue.getValue(null));
		
	}
	
	
	/**
	 * Test that non required and localizable parameter without value, return empty string
	 * instead null.
	 */
	@Test
	public void testGetEmptyValueFromNonRequeredLocalizableParameter() {
		
		context.checking(new Expectations() { {
			allowing(userInputParameter).isLocalizable(); will(returnValue(true));
			allowing(userInputParameter).isRequired(); will(returnValue(false));
		} });
		
		parameterValue.setParameter(userInputParameter);
		
		assertEquals("", parameterValue.getValue(null));
		
	}
	
	
	/**
	 * Test that non required and non localizable parameter with value, 
	 * return value  instead empty string. 
	 */
	@Test
	public void testGetValueFromNonRequeredNonLocalizableParameter() {
		
		context.checking(new Expectations() { {
			allowing(userInputParameter).isLocalizable(); will(returnValue(false));
			allowing(userInputParameter).isRequired(); will(returnValue(false));
		} });
		
		parameterValue.setParameter(userInputParameter);
		
		parameterValue.setValue("value", null);
		assertEquals("value", parameterValue.getValue(null));
	}
	
	/**
	 * Test that non required and localizable parameter with value, 
	 * return value, depends from locale,  instead empty string. 
	 */
	@Test
	public void testGetValueFromNonRequeredLocalizableParameter() {
		
		context.checking(new Expectations() { {
			allowing(userInputParameter).isLocalizable(); will(returnValue(true));
			allowing(userInputParameter).isRequired(); will(returnValue(false));
		} });
		
		parameterValue.setParameter(userInputParameter);
		
		parameterValue.setValue("value_en", "en");
		parameterValue.setValue("value_de", "de");
		assertEquals("value_en", parameterValue.getValue("en"));
		assertEquals("value_de", parameterValue.getValue("de"));
		
	}
	
	

	
	/**
	 * Test that when setting the value of a non-localized parameter
	 * multiple times with different languages the value still 
	 * could be retrieved properly and should equal to the last time
	 * it was invoked.
	 */
	@Test
	public void testGetSetValueNonLocalizedParameter() {
		
		context.checking(new Expectations() { {
			allowing(userInputParameter).isLocalizable(); will(returnValue(false));
			allowing(userInputParameter).isRequired(); will(returnValue(false));
		} });
		
		parameterValue.setParameter(userInputParameter);
		
		String locale = "de";
		String value = "test";
		parameterValue.setValue(value, locale);
		
		assertEquals(value, parameterValue.getValue(locale));
		
		parameterValue.setValue(TEST_VALUE, "en");
		
		assertEquals(TEST_VALUE, parameterValue.getValue(null));
		assertEquals(TEST_VALUE, parameterValue.getValue("en"));
		assertEquals(TEST_VALUE, parameterValue.getValue("de"));
	}

	/**
	 * Tests that toString() works as expected.
	 * Calling toString() without a parameter assigned should be error prone.
	 */
	@Test
	public void testToString() {
		
		context.checking(new Expectations() { {
			oneOf(elasticPath).getBean(ContextIdNames.DYNAMIC_CONTENT_WRAPPER_USER_INPUT_PARAMETER);
			will(returnValue(userInputParameter));
			oneOf(userInputParameter).setParameterId(null);
			oneOf(userInputParameter).setDescription(null);
			oneOf(userInputParameter).setLocalizable(false);
			allowing(userInputParameter).isLocalizable(); will(returnValue(false));
		} });
		
		parameterValue.setParameter(null);
		
		assertNotNull("toString() must return result which is not null", parameterValue.toString());
		
		parameterValue.setParameter(new TemplateParameterImpl());

		assertNotNull(parameterValue.toString());

	}
}
