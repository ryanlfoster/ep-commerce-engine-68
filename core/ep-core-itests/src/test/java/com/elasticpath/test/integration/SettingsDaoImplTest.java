/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.settings.impl.SettingValueFactoryWithDefinitionImpl;
import com.elasticpath.settings.dao.SettingsDao;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingDefinitionImpl;

/**
 * Tests DAO operations on <code>SettingsDao</code>.
 */
public class SettingsDaoImplTest extends BasicSpringContextTest {

	@Autowired
	private SettingsDao settingsDao;
	
	private static final String VALUETYPE = "String";
	
	private static final String NEXTVALUETYPE = "int";
	
	private static final String DEFAULTVALUE = "admin@demo.elasticpath.com";
	
	private static final String ANOTHERVALUE = "admin2@demo.elasticpath.com";
	
	private static final String PATH = "HOME/DIR";
	
	private static final String CONTEXT = "SOMECONTEXT";
	
	private static final String CONTEXT2 = "SOMEOTHERCONTEXT";
	
	private static final String PATH2 = "HOME/DIR2";
	
	/**
	 * Test that you are given the appropriate response when the setting
	 * definition does not exist in the PersistenceEngine with the given path.
	 */
	@DirtiesDatabase
	@Test
	public void testFindSettingDefinition() {
		
		//create a new setting definition object
		//persist it by passing it into the settings dao
		//get it out again with the find method in the dao
		//check that it's the same
		SettingDefinition returnedDef = settingsDao.findSettingDefinition(PATH);
		assertNull(returnedDef);
	}
	
	/**
	 * Test findSettingDefinition when there is a settingDefinition that has been persisted, 
	 * and then using the path retrieve the settingDefinition and ensure all attributes were
	 * correctly stored.
	 */
	@DirtiesDatabase
	@Test
	public void testFindSettingDefinitionNotEmpty() {
		String testPath = "testFindSettingDefinitionsNotEmpty/DIR";
		//Create a SettingDefinition and add it to the database so it's persistent
		SettingDefinition def = new SettingDefinitionImpl();
		def.setPath(testPath);
		def.setValueType(VALUETYPE);
		def.setDefaultValue(DEFAULTVALUE);
		settingsDao.updateSettingDefinition(def);
		
		//Find the SettingDefinition that you added to the database, and ensure all attributes
		//were stored correctly
		SettingDefinition returnedDef = settingsDao.findSettingDefinition(testPath);
		assertNotNull(returnedDef);
		assertEquals(returnedDef.getPath(), testPath);
		assertEquals(returnedDef.getDefaultValue(), DEFAULTVALUE);
		assertEquals(returnedDef.getValueType(), VALUETYPE);
	}
	
	/**
	 * Test findSettingDefinitions when there has been settingDefinitions that have been persisted,
	 * and then using the partialPath retrieve a set of settingDefinitions and ensure all attributes
	 * were correctly stored.
	 */
	@DirtiesDatabase
	@Test
	public void testFindSettingDefinitions() {
		String testPathPartial = "testFindSettingDefinitions/";
		String testPath1 = testPathPartial + "DIR";
		//Create a few setting definitions, and add them to the database so they are persistent
		SettingDefinition def = new SettingDefinitionImpl();
		def.setPath(testPath1);
		def.setValueType(VALUETYPE);
		def.setDefaultValue(DEFAULTVALUE);
		
		String testPath2 = testPathPartial + 2;
		SettingDefinition def2 = new SettingDefinitionImpl();
		def2.setPath(testPath2);
		def2.setValueType(VALUETYPE);
		def2.setDefaultValue(DEFAULTVALUE);
		settingsDao.updateSettingDefinition(def);
		settingsDao.updateSettingDefinition(def2);
		
		//Get the two SettingDefinitions that were added to the database back by using the 
		//findSettingDefinitions method, then ensure all attributes are properly stored
		//and were persistent
		Set<SettingDefinition> returnedDefs = settingsDao.findSettingDefinitions(testPathPartial);
		assertEquals(2, returnedDefs.size());
		assertNotNull(returnedDefs);
		for (SettingDefinition sd : returnedDefs) {
			assertEquals(sd.getDefaultValue(), DEFAULTVALUE);
			assertEquals(sd.getValueType(), VALUETYPE);
		}
	}
	
	/**
	 * Test that findSettingDefinitions returns at least one setting definition (base-insert
	 * should have persisted many).
	 */
	@DirtiesDatabase
	@Test
	public void testFindAllSettingDefinitions() {
		Set<SettingDefinition> returnedDefs = settingsDao.findAllSettingDefinitions();
		assertNotNull(returnedDefs);
		assertFalse(returnedDefs.isEmpty());
	}
	/**
	 * Test findSettingValue where a settingValue has not been persisted with the defined PATH/CONTEXT arguments,
	 * then using both of these parameters try to retrieve a settingValue (which in this case should not return
	 * anything but null).
	 */
	@DirtiesDatabase
	@Test
	public void testFindSettingValue() {
		
		//Try to find a SettingValue that does not exist in the database, 
		//should return an null value
		SettingValue val = settingsDao.findSettingValue(PATH, CONTEXT);
		assertNull(val);
	}
	
	/**
	 * Test findSettingValue where that has been a settingValue persisted with a specific path/context, 
	 * then using these arguments try to retrieve it and ensure all attributes were stored as expected.
	 */
	@DirtiesDatabase
	@Test
	public void testFindSettingValueNotEmpty() {
		
		//Create a SettingValue, by first creating a SettingDefinition, then adding
		//this to the database making it persistent
		SettingDefinition def = new SettingDefinitionImpl();
		def.setPath(PATH);
		def.setValueType(VALUETYPE);
		def.setDefaultValue(DEFAULTVALUE);
		settingsDao.updateSettingDefinition(def);
		
		//Then create the SettingValue by wrapping the SettingDefinition with the SettingValue,
		//and setting the attributes to some values
		def = settingsDao.findSettingDefinition(PATH);
		SettingValueFactoryWithDefinitionImpl factory = new SettingValueFactoryWithDefinitionImpl();
		SettingValue val = factory.createSettingValue(def);
		val.setContext(CONTEXT);
		val.setValue(DEFAULTVALUE);
		
		//Update the database with the SettingValue and make it persistent, then try to pull
		//the SettingValue back out and ensure that the attributes are the same as they should 
		//be because the object is persistent
		settingsDao.updateSettingValue(val);
		SettingValue returnedVal = settingsDao.findSettingValue(PATH, CONTEXT);
		assertNotNull(returnedVal);
		assertEquals(returnedVal.getPath(), PATH);
		assertEquals(returnedVal.getValue(), DEFAULTVALUE);
		assertEquals(returnedVal.getDefaultValue(), DEFAULTVALUE);
		assertEquals(returnedVal.getContext(), CONTEXT);
		assertEquals(returnedVal.getValueType(), VALUETYPE);
	}
	
	/**
	 * Test findSettingValues when there have been a few settingValues persisted to the system,
	 * using the path and context arguments the method should retrieve all of the settingValues 
	 * then ensure that all attributes were persisted correctly.
	 */
	@DirtiesDatabase
	@Test
	public void testFindSettingValues() {

		//Create a few SettingDefinitions and add them to the database to make them 
		//persistent (assigning the proper values to attributes as well first)
		SettingDefinition def = new SettingDefinitionImpl();
		def.setPath(PATH);
		def.setValueType(VALUETYPE);
		def.setDefaultValue(DEFAULTVALUE);
		SettingDefinition def2 = new SettingDefinitionImpl();
		def2.setPath(PATH2);
		def2.setValueType(VALUETYPE);
		def2.setDefaultValue(DEFAULTVALUE);
		settingsDao.updateSettingDefinition(def);
		settingsDao.updateSettingDefinition(def2);

		//Find the SettingDefinitions that were just added to the database, because they are
		//persistent you can use them to create SettingValues then update the database and
		//make those SettingValues persistent as well
		def = settingsDao.findSettingDefinition(PATH);
		def2 = settingsDao.findSettingDefinition(PATH2);
		SettingValueFactoryWithDefinitionImpl factory = new SettingValueFactoryWithDefinitionImpl();
		SettingValue val = factory.createSettingValue(def);
		val.setValue(DEFAULTVALUE);
		SettingValue val2 = factory.createSettingValue(def2);
		val2.setValue(ANOTHERVALUE);
		settingsDao.updateSettingValue(val);
		settingsDao.updateSettingValue(val2);

		//Check that all the SettingValues that are being returned have the proper values
		Set<SettingValue> returnedSet = settingsDao.findSettingValues(PATH, CONTEXT);
		assertNotNull(returnedSet);
		for (SettingValue sv : returnedSet) {
			assertEquals(sv.getDefaultValue(), DEFAULTVALUE);
			assertEquals(sv.getValueType(), VALUETYPE);
		}
	}
	
	/**
	 * Test updateSettingDefinition by first making sure there is no SettingDefinition persisted,
	 * then persisting a created settingDefinition, updating the value with a new value, then finally
	 * ensuring that all attributes were persisted correctly.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateSettingDefinition() {
		
		//Make sure that there is no SettingDefinition already existing in the
		//database
		SettingDefinition returnedDef = settingsDao.findSettingDefinition(PATH);
		assertNull(returnedDef);
		
		//Create a new SettingDefinition and update the database to make the object 
		//persistent
		SettingDefinition def = new SettingDefinitionImpl();
		def.setPath(PATH);
		def.setValueType(VALUETYPE);
		def.setDefaultValue(DEFAULTVALUE);
		settingsDao.updateSettingDefinition(def);
		
		//Try to pull the SettingDefinition back out of the database, to make sure that
		//the addition was successful
		returnedDef = settingsDao.findSettingDefinition(PATH);
		assertNotNull(returnedDef);
		assertEquals(returnedDef.getPath(), PATH);
		assertEquals(returnedDef.getDefaultValue(), DEFAULTVALUE);
		assertEquals(returnedDef.getValueType(), VALUETYPE);
		def.setValueType(NEXTVALUETYPE);
		def.setDefaultValue("4");
		
		//Update the database with a new SettingDefinition and updating the
		//one that is persistent in the database
		returnedDef = settingsDao.updateSettingDefinition(def);
		assertNotNull(returnedDef);
		assertEquals(returnedDef.getPath(), PATH);
		assertEquals(returnedDef.getDefaultValue(), "4");
		assertEquals(returnedDef.getValueType(), NEXTVALUETYPE);
	}
	
	/**
	 * Test updateSettingValue by first making sure there is no SettingValue persisted,
	 * persisting a create settingValue, then updating the value with a new value, then finally
	 * ensuring that all attributes were persisted correctly.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateSettingValue() {
		
		//Create a SettingDefinition and setting the appropriate attributes
		SettingDefinition def = new SettingDefinitionImpl();
		def.setPath(PATH);
		def.setValueType(VALUETYPE);
		def.setDefaultValue(DEFAULTVALUE);
		
		//Try to pull the SettingValue back out of the database, it should not work because it is
		//not persistent and not in the database as of yet, then create a SettingValue with
		//a SettingDefinition wrapped in it
		SettingValue retValue = settingsDao.findSettingValue(PATH, CONTEXT);
		assertNull(retValue);
		settingsDao.updateSettingDefinition(def);
		def = settingsDao.findSettingDefinition(PATH);
		SettingValueFactoryWithDefinitionImpl factory = new SettingValueFactoryWithDefinitionImpl();
		SettingValue val = factory.createSettingValue(def);
		
		//Set some attributes for the SettingValue and commit the changes to the database to
		//make them persistent, then check if the SettingValue that you get back in the set
		//has the appropriate values
		val.setContext(CONTEXT);
		val.setValue(DEFAULTVALUE);
		settingsDao.updateSettingValue(val);
		Set<SettingValue> returnedSet = settingsDao.findSettingValues(PATH, CONTEXT);
		assertNotNull(returnedSet);
		for (SettingValue sv : returnedSet) {
			assertEquals(sv.getPath(), PATH);
			assertEquals(sv.getValue(), DEFAULTVALUE);
			assertEquals(sv.getContext(), CONTEXT);
			assertEquals(sv.getValueType(), VALUETYPE);
		}
		
		//Update the database with a new SettingValue and commit the changes, then check
		//to see if the changes have been made by checking the attributes
		val.setValue(ANOTHERVALUE);
		settingsDao.updateSettingValue(val);
		returnedSet = settingsDao.findSettingValues(PATH, CONTEXT);
		assertNotNull(returnedSet);
		for (SettingValue sv : returnedSet) {
			assertEquals(sv.getPath(), PATH);
			assertEquals(sv.getContext(), CONTEXT);
			assertEquals(sv.getValue(), ANOTHERVALUE);
			assertEquals(sv.getValueType(), VALUETYPE);
		}
	}
	
	/**
	 * Test that the setting definition is deleted in the PersistenceEngine when
	 * given the definition to delete if the definition already exists in the database.
	 */
	@DirtiesDatabase
	@Test
	public void testDeleteSettingDefinition() {
		
		//Create a new SettingDefinition, making sure that there is not one in the
		//database already
		SettingDefinition def = new SettingDefinitionImpl();
		SettingDefinition returnedDef = settingsDao.findSettingDefinition(PATH);
		assertNull(returnedDef);
		
		//Set the proper attributes for the SettingDefinition, then update the database
		//making the SettingDefinition persistent
		def.setPath(PATH);
		def.setValueType(VALUETYPE);
		def.setDefaultValue(DEFAULTVALUE);
		settingsDao.updateSettingDefinition(def);
		
		//Make sure that you can pull out the SettingDefinition from the database with
		//the proper attributes, then delete the SettingDefinition and make sure you are
		//no longer able to pull out the SettingDefinition from the database after that
		returnedDef = settingsDao.findSettingDefinition(PATH);
		assertNotNull(returnedDef);
		assertEquals(returnedDef.getPath(), PATH);
		assertEquals(returnedDef.getDefaultValue(), DEFAULTVALUE);
		assertEquals(returnedDef.getValueType(), VALUETYPE);
		settingsDao.deleteSettingDefinition(def);
		def = settingsDao.findSettingDefinition(PATH);
		assertNull(def);
	}
	
	/**
	 * Test that the setting value is deleted in the PersistenceEngine when the 
	 * value is given to delete if the value already exists in the database.
	 */
	@DirtiesDatabase
	@Test
	public void testDeleteSettingValue() {
		
		//Create a definition, set it's values then make it persistent in the database
		SettingDefinition def = new SettingDefinitionImpl();
		def.setPath(PATH);
		def.setValueType(VALUETYPE);
		def.setDefaultValue(DEFAULTVALUE);
		settingsDao.updateSettingDefinition(def);
		
		//Create a SettingValue, that wraps the SettingDefinition then make it persistent
		//in the database
		SettingValueFactoryWithDefinitionImpl factory = new SettingValueFactoryWithDefinitionImpl();
		SettingValue val = factory.createSettingValue(def);
		val.setContext(CONTEXT);
		val.setValue(DEFAULTVALUE);
		settingsDao.updateSettingValue(val);
		val = settingsDao.findSettingValue(PATH, CONTEXT);
		assertNotNull(val);
		assertEquals(val.getPath(), PATH);
		assertEquals(val.getValueType(), VALUETYPE);
		assertEquals(val.getDefaultValue(), DEFAULTVALUE);
		
		//Try to pull out the SettingValue and make sure you can't because it has been deleted
		settingsDao.deleteSettingValue(val);
		val = settingsDao.findSettingValue(PATH, CONTEXT);
		assertNull(val);
	}
	
	/**
	 * Tests deleteSettingValues by creating a two different settingValues both with the same path
	 * but with different contexts and then try to delete both in one method call.
	 */
	@DirtiesDatabase
	@Test
	public void testDeleteSettingValues() {
		//Create a definition, set it's values then make it persistent in the database
		SettingDefinition def = new SettingDefinitionImpl();
		def.setPath(PATH);
		def.setValueType(VALUETYPE);
		def.setDefaultValue(DEFAULTVALUE);
		settingsDao.updateSettingDefinition(def);
		
		//Create a SettingValue, that wraps the SettingDefinition then make it persistent
		//in the database
		SettingValueFactoryWithDefinitionImpl factory = new SettingValueFactoryWithDefinitionImpl();
		SettingValue val = factory.createSettingValue(def);
		val.setContext(CONTEXT);
		val.setValue(DEFAULTVALUE);
		settingsDao.updateSettingValue(val);
		val = settingsDao.findSettingValue(PATH, CONTEXT);
		assertNotNull(val);
		assertEquals(val.getPath(), PATH);
		assertEquals(val.getValueType(), VALUETYPE);
		assertEquals(val.getDefaultValue(), DEFAULTVALUE);
		
		//Create a SettingValue, that wraps the SettingDefinition then make it persistent
		//in the database
		SettingValue val2 = factory.createSettingValue(def);
		val2.setContext(CONTEXT2);
		val2.setValue(DEFAULTVALUE);
		settingsDao.updateSettingValue(val2);
		val2 = settingsDao.findSettingValue(PATH, CONTEXT2);
		assertNotNull(val2);
		assertEquals(val2.getPath(), PATH);
		assertEquals(val2.getValueType(), VALUETYPE);
		assertEquals(val2.getDefaultValue(), DEFAULTVALUE);
		
		//check again if the first SettingValue is still present as well
		val = settingsDao.findSettingValue(PATH, CONTEXT);
		assertNotNull(val);
		assertEquals(val.getPath(), PATH);
		assertEquals(val.getValueType(), VALUETYPE);
		assertEquals(val.getDefaultValue(), DEFAULTVALUE);
		
		//Try to delete the SettingDefinitions
		settingsDao.deleteSettingValues(PATH, CONTEXT, CONTEXT2);
		//Try to pull out the SettingValues and make sure you can't because they have been deleted
		val = settingsDao.findSettingValue(PATH, CONTEXT);
		assertNull(val);
		val2 = settingsDao.findSettingValue(PATH, CONTEXT2);
		assertNull(val2);
	}
	
	/**
	 * Test method that checks if the getSettingDefinitionCount works when there has been one SettingDefinition
	 * that has been Persisted, and then check if you can count the number of SettingDefinitions are 
	 * present in the database.
	 */
	@DirtiesDatabase
	@Test
	public void testGetSettingDefinitionCountOneElement() {
		
		//Create a definition, set it's values then make it persistent in the database
		SettingDefinition def = new SettingDefinitionImpl();
		def.setPath(PATH);
		def.setValueType(VALUETYPE);
		def.setDefaultValue(DEFAULTVALUE);
		settingsDao.updateSettingDefinition(def);
		
		//Check if the getSettingDefinitionCount can tell if there is one SettingDefinition with the given path
		assertEquals(settingsDao.getSettingDefinitionCount(PATH), 1);
		
	}
	
	/**
	 * Test method that checks if the getSettingDefinitionCount works when there has been no SettingDefinition
	 * that has been Persisted, and then check if you can count the number of SettingDefinitions that are 
	 * present in the database, which should be zero.
	 */
	@DirtiesDatabase
	@Test
	public void testGetSettingDefinitionCountZeroElement() {
		assertEquals(settingsDao.getSettingDefinitionCount(PATH), 0);
	}
	
	/**
	 * Test method that checks if the getSettingValueCount works when there has been a SettingValue
	 * that has been Persisted, it should count the number of SettingValues in the PersistenceEngine
	 * that have the specified path and context, in this case; one. 
	 */
	@DirtiesDatabase
	@Test
	public void testGetSettingValueCountOneElement() {
		//Create a definition, set it's values then make it persistent in the database
		SettingDefinition def = new SettingDefinitionImpl();
		def.setPath(PATH);
		def.setValueType(VALUETYPE);
		def.setDefaultValue(DEFAULTVALUE);
		settingsDao.updateSettingDefinition(def);
		
		//Create a settingValue, set it's context then make it persistent in the database
		SettingValueFactoryWithDefinitionImpl factory = new SettingValueFactoryWithDefinitionImpl();
		SettingValue val = factory.createSettingValue(def);
		val.setContext(CONTEXT);
		val.setValue(ANOTHERVALUE);
		settingsDao.updateSettingValue(val);
		//Check if the getSettingValueCount can tell if there is one SettingValue with the given path
		assertEquals(settingsDao.getSettingValueCount(PATH, CONTEXT), 1);
	}
	
	/**
	 * Test method that checks if the getSettingValueCount works when there has been no SettingValue
	 * that has been Persisted, it should count the number of SettingValues in the PersistenceEngine
	 * that have the specified path and context, in this case zero. 
	 */
	@DirtiesDatabase
	@Test
	public void testGetSettingValueCountZeroElements() {
		assertEquals(settingsDao.getSettingValueCount(PATH, CONTEXT), 0);
	}
	                                                
	/**
	 * Test method that checks if the getSettingDefinitionCount works when there has been one SettingDefinition
	 * that has been Persisted, and then check if you can count the number of SettingDefinitions are 
	 * present in the database.
	 */
	@DirtiesDatabase
	@Test
	public void testGetSettingDefinitionMaxOverrideValues() {
		
		//Create a definition, set it's values then make it persistent in the database
		SettingDefinition def = new SettingDefinitionImpl();
		def.setPath(PATH);
		def.setValueType(VALUETYPE);
		def.setDefaultValue(DEFAULTVALUE);
		def.setMaxOverrideValues(-1);
		settingsDao.updateSettingDefinition(def);
		
		//Check if the getSettingDefinitionCount can tell if there is one SettingDefinition with the given path
		assertEquals(settingsDao.getSettingDefinitionMaxOverrideValues(PATH), -1);
		
	}
}
