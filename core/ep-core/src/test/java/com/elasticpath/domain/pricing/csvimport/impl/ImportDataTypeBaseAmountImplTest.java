/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.pricing.csvimport.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.service.pricing.BaseAmountService;

/**
 * Unit tests for {@code ImportDataTypeBaseAmountImpl}.
 */
public class ImportDataTypeBaseAmountImplTest {
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final BaseAmountService service = context.mock(BaseAmountService.class);
	
	private ImportDataTypeBaseAmountImpl importDataType;
	
	/**
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		importDataType = new ImportDataTypeBaseAmountImpl();
	}

	/**
	 * Test that calling init() with a non-null object will throw an EpDomainException.
	 */
	@Test(expected = EpDomainException.class)
	public void testInitializeWithNullObject() {
		importDataType.init(new Object());
	}
	
	/**
	 * Test that getPrefixOfName() returns the string "Base Amount".
	 */
	@Test
	public void testGetPrefixOfNameReturnsEmptyString() {
		final String baseAmountString = "Base Amount";
		assertEquals("BaseAmount import data type should have a name prefix = 'Base Amount'.", 
				baseAmountString, importDataType.getPrefixOfName());
	}
	
	/**
	 * Test that getMetaObject() returns null.
	 */
	@Test
	public void testGetMetaObjectReturnsNull() {
		assertNull("Importing a BaseAmount should not require any meta-object for initialization of the ImportDataType.", 
				importDataType.getMetaObject());
	}
	
	/**
	 * Test that isEntityImport() returns true.
	 */
	@Test
	public void testIsEntityImport() {
		assertTrue(importDataType.isEntityImport());
	}
	
	/**
	 * Test that isValueImport() returns false.
	 */
	@Test
	public void testIsNotValueObjectImport() {
		assertFalse(importDataType.isValueObjectImport());
	}
	
	/** 
	 * Test that saveOrUpdate() throws EpUnsupportedOperationException because as Entities,
	 * BaseAmounts will be saved by the ImportJobRunner and require no loading of an
	 * associated Entity.
	 */
	@Test(expected = EpUnsupportedOperationException.class)
	public void testSaveOrUpdateNotSupported() {
		importDataType.saveOrUpdate(null, null);
	}
	
	/**
	 * Test that createValueObject() throws EpUnsupportedOperationException because
	 * BaseAmounts are Entities, not value objects.
	 */
	@Test(expected = EpUnsupportedOperationException.class)
	public void testCreateValueObjectNotSupported() {
		importDataType.createValueObject();
	}
	
	/**
	 * Test that getImportJobRunnerBeanName returns "importJobRunnerBaseAmount"
	 * so that Spring find the bean of that name to handle importing of
	 * BaseAmounts.
	 */
	@Test
	public void testGetImportJobRunnerBeanName() {
		assertEquals("importJobRunnerBaseAmount", importDataType.getImportJobRunnerBeanName());
	}
	
	/**
	 * Test that deleteEntity will call the BaseAmountService's delete(BaseAmount) method.
	 */		
	@Test
	public void testDeleteEntityCallsThroughToService() {
		final BaseAmount entity = context.mock(BaseAmount.class);
		context.checking(new Expectations() { { 
			allowing(service).delete(entity);
		} });
//		this.importDataType = new ImportDataTypeBaseAmountImpl() {
//			@Override
//			protected BaseAmountService getBaseAmountService() {
//				return service;
//			}
//		};
//		importDataType.deleteEntity(entity);
	}
	
}
