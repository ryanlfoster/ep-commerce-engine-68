/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpInvalidGuidBindException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.CategoryTypeImpl;
import com.elasticpath.domain.dataimport.CatalogImportField;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.dataimport.impl.ImportBadRowImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeCategoryImpl;
import com.elasticpath.domain.dataimport.impl.ImportFaultImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobRequestImpl;
import com.elasticpath.persistence.CsvFileReader;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.service.dataimport.ImportJobStatusHandler;
import com.elasticpath.service.dataimport.ImportService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Tests for {@link ImportJobRunnerCategoryImpl}.
 */
public class ImportJobRunnerCategoryImplTest {

	private static final String GUID_FIELD_NAME = "GUID";
	private static final String IMPORT_FIELD_NAME = ImportDataTypeCategoryImpl.PARENT_CATEGORY_CODE;
	
	
	private ImportJobRunnerCategoryImpl importJobRunnerCategory;
	private final BeanFactory beanFactory = new BeanFactory() {
		@SuppressWarnings("unchecked")
		@Override
		public <T> T getBean(final String name) {
			if (ContextIdNames.CATEGORY.equals(name)) {
				return (T) new CategoryImpl();
			} else if (ContextIdNames.CSV_FILE_READER.equals(name)) {
				final CsvFileReader mockCsvFileReader = context.mock(CsvFileReader.class);
				context.checking(new Expectations() {
					{
						allowing(mockCsvFileReader).open(with(aNull(String.class)), with(any(char.class)), with(any(char.class)));
						allowing(mockCsvFileReader).readNext();
						will(onConsecutiveCalls(
								returnValue(new String[] { GUID_FIELD_NAME, IMPORT_FIELD_NAME }), // column title
								returnValue(new String[] { "guid2", null }), // parent category
								returnValue(new String[] { "guid1", "guid2" }), // first category
								returnValue(new String[] { "guid3", "guid4" }), // category with no parent in this csv file
								returnValue(null) // end of file, no more lines
						));
						allowing(mockCsvFileReader).close();
					}
				});
				return (T) mockCsvFileReader;
			} else if (ContextIdNames.IMPORT_FAULT.equals(name)) {
				return (T) new ImportFaultImpl();
			} else if (ContextIdNames.IMPORT_BAD_ROW.equals(name)) {
				return (T) new ImportBadRowImpl();
			}
			throw new EpServiceException("unknown bean name: " + name);
		}

		@Override
		public <T> Class<T> getBeanImplClass(final String beanName) {
			return null;
		}
	};
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ImportDataType importDataTypeMock;
	private final Map<String, ImportField> importFields = new HashMap<String, ImportField>();
	private CatalogImportField mockImportField;
	private ImportJobStatusHandler mockImportJobStatusHandler;
	private ChangeSetService mockChangeSetService;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Sets up a test case.
	 * 
	 * @throws java.lang.Exception if error occurs
	 */
	@Before
	public void setUp() throws Exception {
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		importJobRunnerCategory = new ImportJobRunnerCategoryImpl();
		importDataTypeMock = context.mock(ImportDataType.class);
		context.checking(new Expectations() {
			{
		
				allowing(importDataTypeMock).getGuidFieldName();
				will(returnValue(GUID_FIELD_NAME));

				allowing(importDataTypeMock).getMetaObject();
				will(returnValue(new CategoryTypeImpl()));
			}
		});

		final ImportService importServiceMock = context.mock(ImportService.class);
		context.checking(new Expectations() {
			{
				allowing(importServiceMock).findImportDataType(with(aNull(String.class)));
				will(returnValue(importDataTypeMock));

				allowing(importServiceMock).initImportDataTypeLocalesAndCurrencies(with(any(ImportDataType.class)), with(any(ImportJob.class)));
			}
		});

		mockImportField = context.mock(CatalogImportField.class);
		importFields.put(IMPORT_FIELD_NAME, mockImportField);
		context.checking(new Expectations() {
			{

				allowing(importDataTypeMock).getImportFields();
				will(returnValue(importFields));
			}
		});

		importJobRunnerCategory.setImportService(importServiceMock);
		final ImportJobImpl importJob = new ImportJobImpl();
		HashMap<String, Integer> mappings = new HashMap<String, Integer>();
		mappings.put(GUID_FIELD_NAME, 0);
		mappings.put(IMPORT_FIELD_NAME, 1);
		
		importJob.setMappings(mappings);
		importJob.setCatalog(new CatalogImpl());
		
		importJobRunnerCategory.setImportJob(importJob);

		ImportJobRequest request = new ImportJobRequestImpl("requestId");
		request.setImportJob(importJob);
		importJobRunnerCategory.init(request, "id");
		
		mockImportJobStatusHandler = context.mock(ImportJobStatusHandler.class);
		ImportJobStatusHandler importJobStatusHandler = mockImportJobStatusHandler;
		importJobRunnerCategory.setImportJobStatusHandler(importJobStatusHandler);
		
		mockChangeSetService = context.mock(ChangeSetService.class);
		importJobRunnerCategory.setChangeSetService(mockChangeSetService);
		
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for {@link com.elasticpath.service.dataimport.impl.ImportJobRunnerCategoryImpl#createNewEntity(java.lang.Object)}.
	 */
	@Test
	public void testCreateNewEntity() {
		CategoryType catType = new CategoryTypeImpl();
		Category newEntity = (Category) importJobRunnerCategory.createNewEntity(catType);
		assertNotNull(newEntity);
		assertEquals(catType, newEntity.getCategoryType());
	}

	/**
	 * 
	 */
	@Test
	public void testUpdateEntityBeforeSave() {
		importJobRunnerCategory.updateEntityBeforeSave(new CategoryImpl());
	}

	/**
	 * Test validation of a category with GUID = 'guid4' that exists in the database.
	 */
	@Test
	public void testValidateFieldWithoutException() {
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockImportField).getName();
				will(returnValue(IMPORT_FIELD_NAME));

				atLeast(1).of(mockImportField).isCatalogObject();
				will(returnValue(true));

				atLeast(1).of(mockImportField).setCatalog(with(any(Catalog.class)));

				oneOf(mockChangeSetService).isChangeSetEnabled();
				will(returnValue(false));

				oneOf(mockImportJobStatusHandler).reportTotalRows(with(any(String.class)), with(any(int.class)));

				// do not throw exception on checkStringValue() which means that a category with this GUID exists
				atLeast(1).of(mockImportField).checkStringValue(with(any(Category.class)), with("guid4"), with(aNull(ImportGuidHelper.class)));
			}
		});
		

		
		List<ImportBadRow> badRows = importJobRunnerCategory.validate(Locale.CANADA);
		assertTrue(badRows.isEmpty());
	}

	/**
	 * Test validation of a category with GUID = 'guid4' that does not exist in the database.
	 */
	@Test
	public void testValidateFieldThrowException() {
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockImportField).getName();
				will(returnValue(IMPORT_FIELD_NAME));

				atLeast(1).of(mockImportField).isCatalogObject();
				will(returnValue(true));

				oneOf(mockImportField).setCatalog(with(any(Catalog.class)));

				oneOf(mockImportField).getType();
				will(returnValue("categoryType"));


				oneOf(mockChangeSetService).isChangeSetEnabled();
				will(returnValue(false));

				oneOf(mockImportJobStatusHandler).reportTotalRows(with(any(String.class)), with(any(int.class)));

				oneOf(mockImportField).checkStringValue(with(any(Category.class)), with("guid4"), with(aNull(ImportGuidHelper.class)));
				will(throwException(new EpInvalidGuidBindException("guid does not exist")));
			}
		});
		
		List<ImportBadRow> badRows = importJobRunnerCategory.validate(Locale.CANADA);
		assertEquals("There should be one bad row", 1, badRows.size());
	}

}
