package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.dataimport.ImportMapping;
import com.elasticpath.domain.dataimport.ImportType;

/**
 * Test <code>ImportJobImpl</code>.
 */
public class ImportJobImplTest {

    private ImportJobImpl importJob;

    /**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of any error
	 */
	@Before
	public void setUp() throws Exception {
		this.importJob = new ImportJobImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.getName()'.
	 */
	@Test
	public void testGetName() {
		assertNull(this.importJob.getName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.setName(String)'.
	 */
    @Test
	public void testSetName() {
		final String name = "test";
		importJob.setName(name);
		assertSame(name, importJob.getName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.getCsvFileName()'.
	 */
    @Test
	public void testGetCsvFileName() {
		assertNull(importJob.getCsvFileName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.setCsvFileName(String)'.
	 */
    @Test
	public void testSetCsvFileName() {
		final String csvFileName = "test";
		importJob.setCsvFileName(csvFileName);
		assertSame(csvFileName, importJob.getCsvFileName());
	}

	/**
	 * Test that if a column delimiter was not set or is empty,
	 * a space will be used.
	 */
    @Test
	public void testGetCsvFileColDelimeter() {
		assertEquals(' ', importJob.getCsvFileColDelimeter());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.setCsvFileColDelimeter(String)'.
	 */
    @Test
	public void testSetCsvFileColDelimeter() {
		final char colDelimeter = '|';
		importJob.setCsvFileColDelimeter(colDelimeter);
		assertEquals(colDelimeter, importJob.getCsvFileColDelimeter());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.getCsvFileTextQualifier()'.
	 */
    @Test
	public void testGetCsvFileTextQualifier() {
		assertEquals('"', importJob.getCsvFileTextQualifier());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.setCsvFileTextQualifier(String)'.
	 */
    @Test
	public void testSetCsvFileTextQualifier() {
		final char textQualifier = '"';
		importJob.setCsvFileTextQualifier(textQualifier);
		assertEquals(textQualifier, importJob.getCsvFileTextQualifier());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.getImportObjectName()'.
	 */
    @Test
	public void testGetImportObjectName() {
		assertNull(importJob.getImportDataTypeName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.setImportObjectName(String)'.
	 */
    @Test
	public void testSetImportObjectName() {
		final String importObjectName = "test";
		importJob.setImportDataTypeName(importObjectName);
		assertSame(importObjectName, importJob.getImportDataTypeName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.getImportType()'.
	 */
    @Test
	public void testGetImportType() {
		importJob.initialize();
		assertNotNull(importJob.getImportType());
		assertEquals(AbstractImportTypeImpl.INSERT_UPDATE_TYPE, importJob.getImportType());
		assertEquals(AbstractImportTypeImpl.INSERT_UPDATE_TYPE.getTypeId(), importJob.getImportTypeId());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.setImportType(ImportType)'.
	 */
    @Test
	public void testSetImportType() {
		final ImportType importType = AbstractImportTypeImpl.UPDATE_TYPE;
		importJob.setImportType(importType);
		assertSame(importType, importJob.getImportType());
		assertEquals(importType.getTypeId(), importJob.getImportTypeId());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.getImportTypeId()'.
	 */
    @Test
	public void testGetImportTypeId() {
        importJob.initialize();
		assertEquals(AbstractImportTypeImpl.INSERT_UPDATE_TYPE.getTypeId(), importJob.getImportTypeId());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.setImportType(ImportType)'.
	 */
    @Test
	public void testSetImportTypeId() {
		final ImportType importType = AbstractImportTypeImpl.UPDATE_TYPE;
		importJob.setImportTypeId(importType.getTypeId());
		assertSame(importType, importJob.getImportType());
		assertEquals(importType.getTypeId(), importJob.getImportTypeId());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.getMaxAllowErrors()'.
	 */
    @Test
	public void testGetMaxAllowErrors() {
		assertEquals(0, importJob.getMaxAllowErrors());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.setMaxAllowErrors(int)'.
	 */
    @Test
	public void testSetMaxAllowErrors() {
		final int maxAllowErrors = 5;

		importJob.setMaxAllowErrors(maxAllowErrors);
		assertEquals(maxAllowErrors, importJob.getMaxAllowErrors());

	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.getMappings()'.
	 */
    @Test
	public void testGetMappings() {
		assertNull(importJob.getMappings());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.setMappings(Map)'.
	 */
    @Test
	public void testSetMappings() {
		final Map<String, Integer> mappings = new HashMap<String, Integer>();
		importJob.setMappings(mappings);
		assertSame(mappings, importJob.getMappings());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.getMetaMappings()'.
	 */
    @Test
	public void testGetMetaMappings() {
		assertNotNull(importJob.getMetaMappings());
		assertEquals(0, importJob.getMetaMappings().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportJobImpl.setMetaMappings(Map)'.
	 */
    @Test
	public void testSetMetaMappings() {
		final Map<String, ImportMapping> metaMappings = new HashMap<String, ImportMapping>();

		final ImportMapping importMapping1 = new ImportMappingImpl();
		final String name1 = "name1";
		importMapping1.setName(name1);
		importMapping1.setColNumber(Integer.valueOf(1));
		metaMappings.put(name1, importMapping1);

		final ImportMapping importMapping2 = new ImportMappingImpl();
		final String name2 = "name2";
		importMapping2.setName(name2);
		importMapping2.setColNumber(Integer.valueOf(2));
		metaMappings.put(name2, importMapping2);

		importJob.setMetaMappings(metaMappings);
		assertSame(metaMappings, importJob.getMetaMappings());

		// Test the mappings also get changed by setting meta-mappings.
		final Map<String, Integer> mappings = importJob.getMappings();
		assertEquals(Integer.valueOf(1), mappings.get(name1));
		assertEquals(Integer.valueOf(2), mappings.get(name2));
	}
}
