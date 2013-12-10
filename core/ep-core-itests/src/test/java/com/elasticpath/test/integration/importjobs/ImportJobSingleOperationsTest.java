package com.elasticpath.test.integration.importjobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.impl.AbstractImportDataTypeImpl;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.service.catalog.CategoryTypeService;
import com.elasticpath.service.dataimport.ImportJobExistException;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.util.Utils;

/**
 * Tests for simple operations with import jobs like import job saving, editing or removing.
 */
public class ImportJobSingleOperationsTest extends ImportJobTestCase {

	private final List<ImportDataType> simpleImportDataTypes = new ArrayList<ImportDataType>();

	@Autowired
	private CategoryTypeService categoryService;

	/**
	 * Get a reference to elastic path for use within the test.
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		simpleImportDataTypes.add((ImportDataType) getBeanFactory().getBean(ContextIdNames.IMPORT_DATA_TYPE_PRODUCT_CATEGORY_ASSOCIATION));
		simpleImportDataTypes.add((ImportDataType) getBeanFactory().getBean(ContextIdNames.IMPORT_DATA_TYPE_PRODUCT_ASSOCIATION));
		simpleImportDataTypes.add((ImportDataType) getBeanFactory().getBean(ContextIdNames.IMPORT_DATA_TYPE_CUSTOMER));
		simpleImportDataTypes.add((ImportDataType) getBeanFactory().getBean(ContextIdNames.IMPORT_DATA_TYPE_CUSTOMER_ADDRESS));
		simpleImportDataTypes.add((ImportDataType) getBeanFactory().getBean(ContextIdNames.IMPORT_DATA_TYPE_INVENTORY));
	}

	/**
	 * Tests 'com.elasticpath.service.dataimport.ImportService.saveOrUpdateImportJob' method.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveImportJob() {
		final ImportJob importJob = createDefaultImportJob(AbstractImportTypeImpl.INSERT_TYPE);

		ImportJob savedImportJob = importService.saveOrUpdateImportJob(importJob);
		assertNotNull(savedImportJob);
		assertFalse(savedImportJob.getUidPk() == 0);

		ImportJob newImportJob = createDefaultImportJob(AbstractImportTypeImpl.INSERT_TYPE);
		savedImportJob = importService.saveOrUpdateImportJob(newImportJob);
		assertNotNull(savedImportJob);
		assertFalse(savedImportJob.getUidPk() == 0);

		final String existingJobName = newImportJob.getName();
		ImportJob failedJob = createDefaultImportJob(AbstractImportTypeImpl.INSERT_TYPE);
		failedJob.setName(existingJobName);

		try {
			failedJob = importService.saveOrUpdateImportJob(failedJob);
			fail("Exception must be throws");
		} catch (ImportJobExistException exception) {
		}
	}

	/**
	 * Tests retrieving persisted import job. 'com.elasticpath.service.dataimport.ImportService.getImportJob' method
	 */
	@DirtiesDatabase
	@Test
	public void testImportJobRetrieval() {
		ImportJob importJob = createDefaultImportJob(AbstractImportTypeImpl.INSERT_TYPE);
		Map<String, Integer> mappings = new HashMap<String, Integer>();
		String mappingKey = "somemapping01";
		Integer mappingValue = Integer.valueOf(1);
		mappings.put(mappingKey, mappingValue);
		mappings.put("john@smith.com", 2);

		// make mappings not empty.
		importJob.setMappings(mappings);

		importJob = importService.saveOrUpdateImportJob(importJob);

		ImportJob retrievedImportJob = importService.getImportJob(importJob.getUidPk());

		assertEquals(retrievedImportJob.getUidPk(), importJob.getUidPk());
		assertNotNull(importJob.getStore());
		assertNotNull(importJob.getWarehouse());
		assertNotNull(importJob.getCatalog());
		assertNotNull(importJob.getMappings());
		assertNotNull(importJob.getCsvFileName());
		assertEquals(2, importJob.getMappings().size());
		assertNotNull(importJob.getMappings().get(mappingKey));
		assertEquals(mappingValue, importJob.getMappings().get(mappingKey));
	}

	/**
	 * Tests import job removing. 'com.elasticpath.service.dataimport.ImportService.remove' method.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveImportJob() {
		ImportJob importJob = createDefaultImportJob(AbstractImportTypeImpl.INSERT_TYPE);
		importJob = importService.saveOrUpdateImportJob(importJob);

		importService.remove(importJob);
		assertNull(importService.getImportJob(importJob.getUidPk()));
	}

	/**
	 * Tests persisting and updating import job from db.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveAndUpdateImportJob() {
		ImportJob importJob = createDefaultImportJob(AbstractImportTypeImpl.INSERT_TYPE);
		Map<String, Integer> mappings = new HashMap<String, Integer>();
		String mappingKey = "somemapping01";
		mappings.put(mappingKey, 1);

		importJob.setMappings(mappings);
		importJob = importService.saveOrUpdateImportJob(importJob);

		final String newName = Utils.uniqueCode("newJobName");
		Integer newMappingValue = Integer.valueOf(2);
		importJob.setName(newName);
		mappings = importJob.getMappings();
		mappings.put(mappingKey, newMappingValue);
		mappings.put("newValue", 3);
		importJob.setMappings(mappings);

		importJob = importService.saveOrUpdateImportJob(importJob);

		ImportJob retrievedImportJob = importService.getImportJob(importJob.getUidPk());

		assertEquals(newName, retrievedImportJob.getName());
		assertEquals(newName, importJob.getName());
		assertNotNull(importJob.getMappings());

		assertEquals(newMappingValue, importJob.getMappings().get(mappingKey));
		assertEquals(2, importJob.getMappings().size());
	}

	/**
	 * Test that category import data <code>ImportDataTypeCategoryImpl</code> type is retrieved from import job.
	 */
	@DirtiesDatabase
	@Test
	public void testFindImportDataTypeCategory() {
		ImportJob importJob = createDefaultImportJob(AbstractImportTypeImpl.INSERT_TYPE);
		final AbstractImportDataTypeImpl categoryImportDataType
			= getBeanFactory().getBean(ContextIdNames.IMPORT_DATA_TYPE_CATEGORY);

		final List<CategoryType> categoryTypes = categoryService.findAllCategoryTypeFromCatalog(scenario.getCatalog().getUidPk());
		importJob.setImportDataTypeName(categoryImportDataType.getPrefixOfName() + ImportDataType.SEPARATOR + categoryTypes.get(0).getName());
		importJob = importService.saveOrUpdateImportJob(importJob);

		ImportDataType importDataType = importService.findImportDataType(importJob.getImportDataTypeName());
		assertNotNull(importDataType);
	}

	/**
	 * Test that import data types can be retrieved from an import job.
	 */
	@DirtiesDatabase
	@Test
	public void testFindImportDataType() {
		for (ImportDataType importDataType : simpleImportDataTypes) {
			ImportJob importJob = createDefaultImportJob(AbstractImportTypeImpl.INSERT_TYPE);
			importJob.setImportDataTypeName(importDataType.getName());
			importJob = importService.saveOrUpdateImportJob(importJob);
			ImportDataType retrievedImportDataType = importService.findImportDataType(importJob.getImportDataTypeName());
			assertNotNull(retrievedImportDataType);
		}
	}
}
