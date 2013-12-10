package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.CategoryAttributeValueImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.importexport.common.adapters.category.CategoryAdapter;
import com.elasticpath.importexport.common.adapters.pricing.TestAdapterHelper;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.category.CategoryDTO;
import com.elasticpath.importexport.common.dto.category.LinkedCategoryDTO;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Category importer test.
 */
public class CategoryImporterImplTest {

	private static final String CATEGORY_CODE = "category code";
	private CategoryImporterImpl categoryImporter;

	private TestAdapterHelper helper;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private CategoryService mockCategoryService;

	private CategoryDTO categoryDTO;

	private Category category;

	private CachingService mockCachingService;

	@Before
	public void setUp() throws Exception {
		helper = new TestAdapterHelper();
		categoryDTO = new CategoryDTO();
		categoryDTO.setCategoryCode(CATEGORY_CODE);
		categoryDTO.setCatalogCode("catalog");
		category = helper.createCategory(CATEGORY_CODE);
		category.setUidPk(1L);
		categoryImporter = new CategoryImporterImpl();
		mockCategoryService = context.mock(CategoryService.class);
		context.checking(new Expectations() {
			{
				allowing(mockCategoryService).saveOrUpdate(category);
				will(returnValue(category));
			}
		});
		categoryImporter.setCategoryService(mockCategoryService);
		categoryImporter.setCategoryAdapter(new MockCategoryAdapter());
		categoryImporter.setStatusHolder(new ImportStatusHolder());

		mockCachingService = context.mock(CachingService.class);
		context.checking(new Expectations() {
			{
				allowing(mockCachingService).findCategoryByCode(CATEGORY_CODE, "catalog");
				will(returnValue(category));
			}
		});

	}

	/**
	 * Check an import with non-initialized importer.
	 */
	@Test(expected = ImportRuntimeException.class)
	public void testExecuteNonInitializedImport() {
		categoryImporter.executeImport(categoryDTO);
	}

	/**
	 * Check an import of one product.
	 */
	@Test
	public void testExecuteImport() {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<JobType, ImporterConfiguration>());
		SavingStrategy<Category, CategoryDTO> importStrategy = AbstractSavingStrategy.createStrategy(
				ImportStrategyType.INSERT_OR_UPDATE, null);

		categoryImporter.initialize(new ImportContext(importConfiguration), importStrategy);

		List<LinkedCategoryDTO> linkedCategoryDTOList = new ArrayList<LinkedCategoryDTO>();
		LinkedCategoryDTO linkedCategoryDTO = new LinkedCategoryDTO();
		linkedCategoryDTO.setOrder(1);
		linkedCategoryDTO.setVirtualCatalogCode("virtualCatalog");
		linkedCategoryDTOList.add(linkedCategoryDTO);
		categoryDTO.setLinkedCategoryDTOList(linkedCategoryDTOList);

		context.checking(new Expectations() {
			{
				oneOf(mockCategoryService).get(1L);
				will(returnValue(category));
				oneOf(mockCategoryService).findByGuid(CATEGORY_CODE, "virtualCatalog");
				will(returnValue(null));
			}
		});

		final Catalog catalog = new CatalogImpl();
		catalog.setUidPk(1L);
		categoryImporter.setCachingService(mockCachingService);
		context.checking(new Expectations() {
			{
				allowing(mockCachingService).findCatalogByCode("virtualCatalog");
				will(returnValue(catalog));
				allowing(mockCategoryService).addLinkedCategory(1L, -1L, 1L);
			}
		});



		categoryImporter.executeImport(categoryDTO);
		assertEquals("category", categoryImporter.getImportedObjectName());
		assertNotNull(categoryImporter.getCategoryService());
		assertNotNull(categoryImporter.getSavingStrategy());
	}

	/**
	 * Test clear collection strategy for category attributes.
	 */
	@Test
	public void testCollectionsStrategy() {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<JobType, ImporterConfiguration>());
		SavingStrategy<Category, CategoryDTO> importStrategy = AbstractSavingStrategy.createStrategy(
				ImportStrategyType.INSERT_OR_UPDATE, null);

		categoryImporter.initialize(new ImportContext(importConfiguration), importStrategy);

		CollectionsStrategy<Category, CategoryDTO> collectionsStrategy = categoryImporter.getCollectionsStrategy();
		assertTrue(collectionsStrategy.isForPersistentObjectsOnly());

		Category newCategory = new CategoryImpl();
		Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
		attributeValueMap.put("attr_key", new CategoryAttributeValueImpl());
		newCategory.setAttributeValueMap(attributeValueMap);

		assertEquals(newCategory.getAttributeValueMap().size(), 1);
		collectionsStrategy.prepareCollections(newCategory, new CategoryDTO());
		assertTrue(newCategory.getAttributeValueMap().isEmpty());
	}

	/** The dto class must be present and correct. */
	@Test
	public void testDtoClass() {
		assertEquals("Incorrect DTO class", CategoryDTO.class, categoryImporter.getDtoClass());
	}

	/** The auxiliary JAXB class list must not be null (can be empty). */
	@Test
	public void testAuxiliaryJaxbClasses() {
		assertNotNull(categoryImporter.getAuxiliaryJaxbClasses());
	}

	/**
	 * Mock category adapter.
	 */
	private class MockCategoryAdapter extends CategoryAdapter {

		@Override
		public void populateDomain(final CategoryDTO categoryDTO, final Category category) {
			// do nothing
		}

		@Override
		public void populateDTO(final Category category, final CategoryDTO categoryDTO) {
			// do nothing
		}

		@Override
		public Category createDomainObject() {
			return new CategoryImpl();
		}
	}
}
