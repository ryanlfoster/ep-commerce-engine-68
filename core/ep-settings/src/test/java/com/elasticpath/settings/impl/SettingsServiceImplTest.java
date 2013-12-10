package com.elasticpath.settings.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.settings.SettingMaxOverrideException;
import com.elasticpath.settings.dao.SettingsDao;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Tests for com.elasticpath.settings.impl.SettingsServiceImpl.
 */
@SuppressWarnings("PMD.AvoidCatchingNPE")
public class SettingsServiceImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final SettingsDao mockSettingsDao = context.mock(SettingsDao.class);
	private SettingsDao dao;
	private final SettingsServiceImpl service = new SettingsServiceImpl();
	private static final int ARRAYSIZE = 3;
	private static final String PATH = "SOME/PATH";
	private static final String CONTEXT = "SOMECONTEXT";

	/**
	 * Setup tasks for all tests.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		dao = mockSettingsDao;
		service.setSettingsDao(dao);
	}

	/**
	 * Test that the given partial path is passed to the DAO when calling "findSettingDefinitions" method, 
	 * and that the DAO's response is wrapped in an modifiable set.
	 */
	@Test
	public void testGetSettingDefinitions() {
		final Set<SettingDefinition> definitions = new HashSet<SettingDefinition>();
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).findSettingDefinitions(PATH);
				will(returnValue(definitions));
			}
		});
		
		Set<SettingDefinition> returnedDefinitions = service.getSettingDefinitions(PATH);
		
		//check that it is modifiable
		boolean value = returnedDefinitions.add(context.mock(SettingDefinition.class));
		assertTrue(value);
	}
	
	/**
	 * Test that calling getSettingDefinitions with a null partialPath throws
	 * a NPE before calling the dao method "findSettingDefinitions". 
	 */
	@Test(expected = NullPointerException.class)
	public void testGetSettingDefinitionsNullPath() {
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).findSettingDefinitions(null);
				will(returnValue(null));
			}
		});
		service.getSettingDefinitions(null);
	}

	/**
	 * Test that the given path is passed to the DAO when calling "findSettingDefinition", and that the dao's response is
	 * a returned SettingDefinition for the given path.
	 */
	@Test
	public void testGetSettingDefinition() {
		final SettingDefinition definition = context.mock(SettingDefinition.class);
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).findSettingDefinition(PATH);
				will(returnValue(definition));
			}
		});
		SettingDefinition returnedDefinition = service.getSettingDefinition(PATH);
		assertSame(definition, returnedDefinition);
	}
	
	/**
	 * Test that the getAllSettingDefinitions method appropriately calls the "findAllSettingDefinitions"
	 * method with no parameters and is returned a set of setting definitions (modifiable set).
	 */
	@Test
	public void testGetAllSettingDefinitions() {
		final Set<SettingDefinition> defs = new HashSet<SettingDefinition>();
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).findAllSettingDefinitions();
				will(returnValue(defs));
			}
		});
		Set<SettingDefinition> returnedDefs = service.getAllSettingDefinitions();
		assertNotNull(returnedDefs);
		assertEquals(returnedDefs, defs);
		
		//check that the set is modifiable
		boolean value = returnedDefs.add(context.mock(SettingDefinition.class));
		assertTrue(value);

	}

	/**
	 * Test that the given path and context are passed to the DAO method call for "findSettingValue", and 
	 * that the dao's response is returned in the form of a SettingValue. 
	 */
	@Test
	public void testGetSettingValue() {
		final String settingContext = "SOMECONTEXT";
		final SettingValue value = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).findSettingValue(PATH, settingContext);
				will(returnValue(value));
			}
		});
		SettingValue returnedDefinition = service.getSettingValue(PATH, settingContext);
		assertSame(value, returnedDefinition);
	}
	
	/**
	 * Test that the null path and null context arguments will result in an NullPointerException
	 * when passed to the service method "getSettingValue". 
	 */
	@Test
	public void testGetSettingValueNullArguments() {
		try {
			service.getSettingValue(null, null);
			fail("Expected a IllegalArgumentException when both arguments were null");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}
	
	/**
	 * Test that a null path argument with a defined context argument will result in an NullPointerException
	 * when passed to the service method "getSettingValue". 
	 */
	@Test
	public void testGetSettingValuePathNullArgument() {
		try {
			service.getSettingValue(null, CONTEXT);
			fail("Expected a IllegalArgumentException when both arguments were null");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}
	
	/**
	 * Test that a defined path argument with a null context argument will result in an NullPointerException
	 * when passed to the service method "getSettingValue". 
	 */
	@Test
	public void testGetSettingValueContextNullArgument() {
		try {
			service.getSettingValue(null, PATH);
			fail("Expected a IllegalArgumentException when both arguments were null");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}
	
	/**
	 * Test that getSettingValues calls the dao method "findSettingValues" with the same arguments that were passed into the service method.
	 * Also check that the returned set is modifiable.
	 */
	@Test
	public void testGetSettingValues() {
		final String[] contexts = new String[ARRAYSIZE];
		contexts[0] = "SOMECONTEXT1";
		contexts[1] = "SOMECONTEXT2";
		contexts[2] = "SOMECONTEXT3";
		final Set<SettingValue> values = new HashSet<SettingValue>();
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).findSettingValues(PATH, contexts);
				will(returnValue(values));
			}
		});
		
		Set<SettingValue> returnedValues = service.getSettingValues(PATH, contexts);
		//Check that it's modifiable
		boolean value = returnedValues.add(context.mock(SettingValue.class));
		assertTrue(value);
	}
	
	/**
	 * Test that calling getSettingValues with a null context and defined path still returns a set of SettingValues from the dao
	 * when it calls "findSettingValues".
	 */
	@Test
	public void testGetSettingValuesNullContext() {
		final Set<SettingValue> values = new HashSet<SettingValue>();
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).findSettingValues(PATH);
				will(returnValue(values));
			}
		});
		Set<SettingValue> returnedValues = service.getSettingValues(PATH);
		assertNotNull(returnedValues);
	}

	/**
	 * Test that calling getSettingValues with a empty context array still returns a set of SettingValues from the dao
	 * when the service method "findSettingValues" is called with context and path arguments.
	 */
	@Test
	public void testGetSettingValuesEmptyContexts() {
		final Set<SettingValue> values = new HashSet<SettingValue>();
		final String [] cons = new String[] {};
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).findSettingValues(PATH, cons);
				will(returnValue(values));
			}
		});
		Set<SettingValue> returnedValues = service.getSettingValues(PATH, cons);
		assertNotNull(returnedValues);
	}
	
	/**
	 * Test that deleteSettingDefinition calls the dao method "deleteSettingDefinitions" with 
	 * the same argument that was passed into the service method.
	 */
	@Test
	public void testDeleteSettingDefinition() {
		final SettingDefinition mockSettingDefinition = context.mock(SettingDefinition.class);
		context.checking(new Expectations() {
			{
				allowing(mockSettingsDao).deleteSettingDefinition(mockSettingDefinition);
			}
		});
		service.deleteSettingDefinition(mockSettingDefinition);
	}
	
	/**
	 * Test that deleteSettingValue calls the dao method "deleteSettingValue" with the same argument that was passed into the service method.
	 */
	@Test
	public void testDeleteSettingValue() {
		final SettingValue value = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).deleteSettingValue(value);
			}
		});
		service.deleteSettingValue(value);
	}

	/**
	 * Test that deleteSettingValues calls the dao method "deleteSettingValues" with the same arguments that were passed into the service method,
	 * and that it returns the number of setting values that were deleted.
	 */
	@Test
	public void testDeleteSettingValues() {
		final String[] contexts = new String[ARRAYSIZE];
		contexts[0] = "SOMECONTEXT1";
		contexts[1] = "SOMECONTEXT2";
		contexts[2] = "SOMECONTEXT3";
		//assume the dao reports that all requested settingValues were deleted
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).deleteSettingValues(PATH, contexts);
				will(returnValue(contexts.length));
			}
		});
		service.deleteSettingValues(PATH, contexts);
	}

	/**
	 * Test that updateSettingDefinition, when passed a SettingDefinition that is persistent,
	 * calls the dao method "updateSettingDefinition" with the same argument that was passed into the service method.
	 */
	@Test
	public void testUpdateSettingDefinition() {
		
		//The method checks if the setting definition is already persisted, in our case it is true
		//and then the update of the definition should proceed without a problem.
		final SettingDefinition mockSettingDefinition = context.mock(SettingDefinition.class);
		context.checking(new Expectations() {
			{
				allowing(mockSettingDefinition).isPersisted();
				will(returnValue(true));

				oneOf(mockSettingsDao).updateSettingDefinition(mockSettingDefinition);
				will(returnValue(mockSettingDefinition));
			}
		});
		SettingDefinition returnedDef = service.updateSettingDefinition(mockSettingDefinition);
		assertSame(mockSettingDefinition, returnedDef);
	}

	/**
	 * Test that calling updateSettingDefinition with a SettingDefinition that is not persistent
	 * and has a PATH of a SettingDefinition that is already persistent will throw an EpServiceException.
	 */
	@Test
	public void testUpdateSettingDefinitionDuplicate() {
		
		//The method checks if the setting definition is already persisted, in this case it is 
		//not persisted, thus checks if the setting definition already exists
		final SettingDefinition mockSettingDefinition = context.mock(SettingDefinition.class);
		context.checking(new Expectations() {
			{
				allowing(mockSettingDefinition).isPersisted();
				will(returnValue(false));

				allowing(mockSettingDefinition).getPath();
				will(returnValue(PATH));

				oneOf(mockSettingsDao).getSettingDefinitionCount(PATH);
				will(returnValue(1));
			}
		});
		
		try {
			service.updateSettingDefinition(mockSettingDefinition);
			fail("Was supposed to throw an EpServiceException error, SettingDefinition already exists.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}	
	
	/**
	 * Test that updateSettingValue calls the dao method "updateSettingValue" with the same argument that was passed into the service method,
	 * and a SettingValue is returned.
	 */
	@Test
	public void testUpdateSettingValue() {
		
		final SettingValue mockSettingValue = context.mock(SettingValue.class);
		//Obtain the maximum number of overrides that are allowed for a setting definition, in this case we allow zero overrides
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).getSettingDefinitionMaxOverrideValues(PATH);
				will(returnValue(0));

				allowing(mockSettingValue).getPath();
				will(returnValue(PATH));

				//The method checks if the setting value is already persisted, in our case it is true
				//and then the update of the value should proceed without a problem.
				allowing(mockSettingValue).isPersisted();
				will(returnValue(true));

				oneOf(mockSettingsDao).updateSettingValue(mockSettingValue);
				will(returnValue(mockSettingValue));
			}
		});
		SettingValue returnedValue = service.updateSettingValue(mockSettingValue);
		assertEquals(returnedValue, mockSettingValue);
	}

	/**
	 * Test that after the addition of one SettingValue to the settingsDao, if another
	 * SettingValue with the same path and context is created to be added it will get 
	 * rejected by the dao and throw an EpServiceException.
	 */
	@Test
	public void testDuplicateSettingValue() {
		final SettingValue mockSettingValue = context.mock(SettingValue.class);
		//Obtain the maximum number of overrides that are allowed for a setting definition, in this case we allow zero overrides
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).getSettingDefinitionMaxOverrideValues(PATH);
				will(returnValue(0));

				//The method checks if the setting value is already persisted, in our case it is false
				allowing(mockSettingValue).isPersisted();
				will(returnValue(false));

				//The method then checks if the setting value exists and in our case we return true
				//and an exception is thrown because a duplicate setting value exists
				allowing(mockSettingValue).getPath();
				will(returnValue(PATH));

				allowing(mockSettingValue).getContext();
				will(returnValue(CONTEXT));

				oneOf(mockSettingsDao).getSettingValueCount(PATH, CONTEXT);
				will(returnValue(1));
			}
		});
		
		try {
			service.updateSettingValue(mockSettingValue);
			fail("Was supposed to throw an EpServiceException error, SettingValue already exists.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}
	
	/**
	 *  Test that ensures that an error is thrown if trying to add a new setting value for a specific
	 *  setting but the maximum number of value overrides are already present, causing an error to be thrown. 
	 */
	@Test
	public void testMaxOverrideSettingValueExists() {
		final SettingValue mockSettingValue = context.mock(SettingValue.class);
		//Obtain the maximum number of overrides that are allowed for a setting definition, in this case we allow one overrides
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).getSettingDefinitionMaxOverrideValues(PATH);
				will(returnValue(1));

				//The method checks if the setting value is already persisted, in our case it is false
				allowing(mockSettingValue).isPersisted();
				will(returnValue(false));

				//The method then checks if the setting value exists and in our case we return false
				allowing(mockSettingValue).getPath();
				will(returnValue(PATH));

				allowing(mockSettingValue).getContext();
				will(returnValue(CONTEXT));

				oneOf(mockSettingsDao).getSettingValueCount(PATH, CONTEXT);
				will(returnValue(0));

				//The method then checks if the setting's value that are currently persisted exceed the number
				//that are allowed, in this case we are at the limit and throw an exception when trying to add another
				oneOf(mockSettingsDao).getSettingValueCount(PATH, null);
				will(returnValue(1));
			}
		});
		
		try {
			service.updateSettingValue(mockSettingValue);
			fail("Was supposed to throw an EpServiceException error, SettingValue already exists.");
		} catch (SettingMaxOverrideException e) {
			assertNotNull(e);
		}
	}
	
	/**
	 *  Test that addition of a setting value proceeds when the maximum override value is set to negative one
	 *  meaning that there are an infinite number of overrides possible for a setting. 
	 */
	@Test
	public void testMaxOverrideSettingValueInfinite() {
		final SettingValue mockSettingValue = context.mock(SettingValue.class);
		//Obtain the maximum number of overrides that are allowed for a setting definition, in this case we allow unlimited overrides
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockSettingsDao).getSettingDefinitionMaxOverrideValues(PATH);
				will(onConsecutiveCalls(returnValue(-1), returnValue(-1)));

				//The method checks if the setting value is already persisted, in our case it is false
				allowing(mockSettingValue).isPersisted();
				will(returnValue(false));

				//The method then checks if the setting value exists and in our case we return false
				allowing(mockSettingValue).getPath();
				will(returnValue(PATH));

				allowing(mockSettingValue).getContext();
				will(returnValue(CONTEXT));

				oneOf(mockSettingsDao).getSettingValueCount(PATH, CONTEXT);
				will(returnValue(0));

				//The method then checks if the setting's value that are currently persisted exceed the number
				//that are allowed, in this case we are under the limit so the new setting value is persisted
				oneOf(mockSettingsDao).updateSettingValue(mockSettingValue);
				will(returnValue(mockSettingValue));
			}
		});
		SettingValue returnedValue = service.updateSettingValue(mockSettingValue);
		assertEquals(returnedValue, mockSettingValue);
	}
	
	/**
	 * Test that getObject throws an unsupportedOperationException because the service supports more than one type of object.
	 */
	@Test
	public void testGetObjectLong() {
		final long uid = 22222222;
		try {
			service.getObject(uid);
			fail("method did not return exception as expected");
		} catch (UnsupportedOperationException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Tests the method settingDefinitionExists, make sure that the arguments are passed to the 
	 * dao, and the appropriate values are returned when a SettingDefinition does exist already with a specified path.
	 */
	@Test
	public void testSettingDefinitionExists() {
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).getSettingDefinitionCount(PATH);
				will(returnValue(1));
			}
		});
		boolean value = service.settingDefinitionExists(PATH);
		assertEquals(value, true);
	}
	
	/**
	 * Tests the method settingDefinitionExists, makes sure that the arguments passed to the dao, and
	 * the appropriate values are returned when a SettingDefinition doesn't exist already with a specified path.
	 */
	@Test
	public void testSettingDefinitionDoesntExist() {
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).getSettingDefinitionCount(PATH);
				will(returnValue(0));
			}
		});
		boolean value = service.settingDefinitionExists(PATH);
		assertEquals(value, false);
	}
	
	/**
	 * Tests the method settingValueExists, makes sure that the arguments are passed to the 
	 * dao, and the appropriate values are returned when a SettingValue does exists with a specific
	 * path and context.
	 */
	@Test
	public void testSettingValueExists() {
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).getSettingValueCount(PATH, CONTEXT);
				will(returnValue(1));
			}
		});
		boolean value = service.settingValueExists(PATH, CONTEXT);
		assertEquals(value, true);
	}
	
	/**
	 * Tests the method settingValueExists, makes sure that the arguments are passed to the
	 * dao, and the appropriate values are returned when a SettingValue does not exist with a specific
	 * path and context.
	 */
	@Test
	public void testSettingValueDoesntExist() {
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsDao).getSettingValueCount(PATH, CONTEXT);
				will(returnValue(0));
			}
		});
		boolean value = service.settingValueExists(PATH, CONTEXT);
		assertEquals(value, false);
	}
}
