/**
 * 
 */
package com.elasticpath.settings.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Tests for com.elasticpath.settings.dao.impl.SettingsDaoImpl.
 */
public class SettingsDaoImplTest {

	//global variables
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final PersistenceEngine mockPersistenceEngine = context.mock(PersistenceEngine.class);
	private PersistenceEngine persistEngine;
	private final SettingsDaoImpl settingsDao = new SettingsDaoImpl();
	private static final String PATH = "SOME/PATH";
	private static final int ARRAYSIZE = 3;
	private static final String CONTEXT = "SOMECONTEXT";
	
	
	/**
	 * Setup task for all tests.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		persistEngine = mockPersistenceEngine;
		settingsDao.setPersistenceEngine(persistEngine);
	}

	/**
	 * Test that the findSettingDefinition method calls the "retrieveByNamedQuery" function and returns a null SettingDefinition 
	 * because there currently is no settingDefinition that matches the path in the database.
	 */
	@Test
	public void testFindSettingDefinition() {
		final List<SettingDefinition> defs = new ArrayList<SettingDefinition>();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(defs));
			}
		});
		SettingDefinition returnedDef = settingsDao.findSettingDefinition(PATH);
		assertNull(returnedDef);
	}
	
	/**
	 * Tests the findSettingDefinition is called with the "PATH" argument and it passes the call to the service method 
	 * "retrieveByNamedQuery" once, and returns the SettingDefinition accordingly.
	 */
	@Test
	public void testFindSettingDefinitionNotEmpty() {
		final List<SettingDefinition> defs = new ArrayList<SettingDefinition>();
		SettingDefinition inputDef = context.mock(SettingDefinition.class);
		defs.add(inputDef);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(defs));
			}
		});
		SettingDefinition returnedDef = settingsDao.findSettingDefinition(PATH);
		assertEquals(returnedDef, inputDef);
	}

	/**
	 * Test method findSettingDefinitions is called with the "PATH" argument and it passes the call to the service method
	 * "retrieveByNamedQuery" once, and returns a Set of SettingDefinitions. 
	 */
	@Test
	public void testFindSettingDefinitions() {
		final List<SettingDefinition> defs = new ArrayList<SettingDefinition>();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(defs));
			}
		});
		Set<SettingDefinition> newSet = new HashSet<SettingDefinition>();
		Set<SettingDefinition> returnedSet = settingsDao.findSettingDefinitions(PATH);
		assertEquals(newSet, returnedSet);
	}
	
	/**
	 * Test method findAllSettingDefinitions to check if the dao calls the retrieveByNamedQuery method
	 * with the appropriate query and a Set of SettingDefitions is returned.
	 */
	@Test
	public void testFindAllSettingDefinitions() {
		final List<SettingDefinition> defs = new ArrayList<SettingDefinition>();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(defs));
			}
		});
		Set<SettingDefinition> returnedSet = settingsDao.findAllSettingDefinitions();
		Set<SettingDefinition> matchDefs = new HashSet<SettingDefinition>();
		assertEquals(matchDefs, returnedSet);
		
	}
	
	/**
	 * Test method findSettingValue to check if the dao calls the "retrieveByNamedQuery" method and the appropriate dao response is returned,
	 * using the arguments passed from the dao to the PersistenceEngine.
	 */
	@Test
	public void testFindSettingValue() {
		String settingsContext = "SOMECONTEXT";
		final List<Object> objs = new ArrayList<Object>();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(objs));
			}
		});
		SettingValue returnedSettingValue = settingsDao.findSettingValue(PATH, settingsContext);
		assertNull(returnedSettingValue);
	}
	
	/**
	 * Tests the findSettingValues method calls the "retrieveByNamedQuery" function once, and has PATH/CONTEXT as arguments
	 * then returns the SettingValue properly when the list is not empty.
	 */
	@Test
	public void testFindSettingValuesNotEmpty() {
		String settingsContext = "SOMECONTEXT";
		final List<Object> objs = new ArrayList<Object>();
		SettingValue val = context.mock(SettingValue.class);
		objs.add(val);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(objs));
			}
		});
		SettingValue returnedSettingValue = settingsDao.findSettingValue(PATH, settingsContext);
		assertEquals(returnedSettingValue, val);
	}
	
	/**
	 * Test that the findSettingValues method calls retrieveByNamedQuery if an empty array of contexts is passed in
	 * with a defined PATH.
	 */
	@Test
	public void testFindSettingValuesWithEmptyArray() {
		final List<SettingValue> value = new ArrayList<SettingValue>();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(value));
			}
		});
		Set<SettingValue> newValues = new HashSet<SettingValue>();
		Set<SettingValue> returnedSet = settingsDao.findSettingValues(PATH);
		assertEquals(newValues, returnedSet);
	}

	/**
	 * Test that the findSettingValues method calls retrieveByNamedQueryWithList if a non-empty array of contexts is passed in
	 * as well as a path argument.
	 */
	@Test
	public void testFindSettingValues() {
		final List<SettingValue> value = new ArrayList<SettingValue>();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQueryWithList(
						with(any(String.class)), with(any(String.class)),
						with(Collections.<String>singletonList(CONTEXT)), with(any(Object[].class)));
				will(returnValue(value));
			}
		});
		Set<SettingValue> newValues = new HashSet<SettingValue>();
		Set<SettingValue> returnedSet = settingsDao.findSettingValues(PATH, CONTEXT);
		assertEquals(newValues, returnedSet);
	}

	/**
	 * Test that the findSettingValues method calls retrieveByNamedQueryWithList if a null contexts is passed in
	 * as well as a path argument.
	 */
	@Test
	public void testFindSettingValuesWithNullArray() {
		final List<SettingValue> value = new ArrayList<SettingValue>();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(value));
			}
		});
		Set<SettingValue> newValues = new HashSet<SettingValue>();
		Set<SettingValue> returnedSet = settingsDao.findSettingValues(PATH);
		assertEquals(newValues, returnedSet);
	}
	
	
	/**
	 * Test that the updateSettingDefinition calls the "saveOrUpdate" method once with the SettingDefinition in the PersistenceEngine.
	 */
	@Test
	public void testUpdateSettingDefinition() {
		final SettingDefinition definition = context.mock(SettingDefinition.class);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).saveOrUpdate(definition);
				will(returnValue(definition));
			}
		});
		SettingDefinition returnedDefinition = settingsDao.updateSettingDefinition(definition);
		assertSame(returnedDefinition, definition);
		
	}

	/**
	 * Test that the updateSettingValue calls the "saveOrUpdate" method once with the SettingValue in the PersistenceEngine.
	 */
	@Test
	public void testUpdateSettingValue() {
		final SettingValue value = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).saveOrUpdate(value);
				will(returnValue(value));
			}
		});
		SettingValue returnedValue = settingsDao.updateSettingValue(value);
		assertSame(returnedValue, value);
	}

	/**
	 * Test that the deleteSettingDefinition calls the "delete" method once with a void return in the PersistenceEngine,
	 * with a settingDefinition as an argument.
	 */
	@Test
	public void testDeleteSettingDefinition() {
		final SettingDefinition definition = context.mock(SettingDefinition.class);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).delete(definition);
			}
		});
		settingsDao.deleteSettingDefinition(definition);
	}

	/**
	 * Test that the deleteSettingValue calls the "delete" method once with a void return in the PersistenceEngine,
	 * with a settingValue as an argument.
	 */
	@Test
	public void testDeleteSettingValue() {
		final SettingValue value = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).delete(value);
			}
		});
		settingsDao.deleteSettingValue(value);
	}

	/**
	 * Test that the deleteSettingValues calls the "executeNamedQueryWithList" method once with a correct return value in the PersistenceEngine.
	 */
	@Test
	public void testDeleteSettingValues() {
		final String [] contexts = new String[ARRAYSIZE];
		contexts[0] = "SOMECONTEXT1";
		contexts[1] = "SOMECONTEXT2";
		contexts[2] = "SOMECONTEXT3";
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQueryWithList(
						with("SETTING_VALUE_UIDS_BY_PATH_AND_CONTEXTS"),
						with("list"),
						with(Arrays.<String>asList(contexts)),
						with(any(Object[].class)));
				will(returnValue(null));

				oneOf(mockPersistenceEngine).executeNamedQueryWithList(
						with(any(String.class)), with(any(String.class)), with((Collection<?>) null), with(any(Object[].class)));
				will(returnValue(0));
			}
		});
		settingsDao.deleteSettingValues(PATH, contexts);
	}
	
	/**
	 * Test that the getSettingDefinitionCount calls the "retrieveByNamedQuery" PersistenceEngine method with the appropriate arguments
	 * and the values returned can be parsed correctly into an integer value.
	 */
	@Test
	public void testGetSettingDefinitionCount() {
		final Object [] objs = new Object[] { PATH };
		final List<Object> list = new ArrayList<Object>();
		Long value = 1L;
		list.add(value);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery("SETTING_DEFINITIONS_COUNT_BY_PATH", objs);
				will(returnValue(list));
			}
		});
		int returnedVal = settingsDao.getSettingDefinitionCount(PATH);
		assertEquals(returnedVal, 1);
	}
	
	/**
	 * Test that the getSettingValueCount calls the appropriate PersistenceEngine method with the appropriate arguments
	 * and the values returned can be parsed correctly into an integer value.
	 */
	@Test
	public void testGetSettingValueCount() {
		final Object [] objs = new Object[] { PATH, CONTEXT };
		final List<Object> list = new ArrayList<Object>();
		Long value = 0L;
		list.add(value);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery("SETTING_VALUES_COUNT_BY_PATH_AND_CONTEXT", objs);
				will(returnValue(list));
			}
		});
		int returnedVal = settingsDao.getSettingValueCount(PATH, CONTEXT);
		assertEquals(returnedVal, 0);
	}

	/**
	 * Test that the getSettingDefinitionMaxOverrideValues calls the appropriate PersistenceEngine method with the appropriate arguments
	 * and the values returned can be parsed correctly into an integer value.
	 */
	@Test
	public void testGetSettingDefinitionMaxOverrideValues() {
		final Object [] objs = new Object[] { PATH };
		final List<Integer> list = new ArrayList<Integer>();
		Integer value = 0;
		list.add(value);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery("SETTING_DEFINITION_MAX_OVERRIDE_VALUES", objs);
				will(returnValue(list));
			}
		});
		int returnedVal = settingsDao.getSettingDefinitionMaxOverrideValues(PATH);
		assertEquals(returnedVal, 0);
	}
}
