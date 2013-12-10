package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.importexport.common.adapters.productcategories.ProductCategoryAdapter;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.productcategory.CatalogCategoriesDTO;
import com.elasticpath.importexport.common.dto.productcategory.ProductCategoriesDTO;
import com.elasticpath.importexport.common.dto.productcategory.ProductCategoryDTO;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.service.catalog.ProductService;

/**
 * Test for <code>ProductCategoryImporterImpl</code>.
 */
public class ProductCategoryImporterImplTest {
	private static final String CATALOG_CODE = "catalogCode";

	private static final String CATEGORY_CODE = "categoryCode";
	private static final String CATEGORY_CODE2 = "categoryCode2";
	private static final String CATEGORY_CODE3 = "categoryCode3";

	private static final String PRODUCT_CODE = "productCode";

	private ProductCategoryImporterImpl productCategoryImporter;

	private SavingStrategy<Product, ProductCategoriesDTO> savingStrategy;

	private ProductService productService;
	
	private Catalog masterCatalog;
	private CategoryImpl category, category2, category3;

	private ProductCategoriesDTO productCategoriesDTO;

	private Product product;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private CachingService cachingService;

	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of error happens
	 */
	@Before
	public void setUp() throws Exception {
		masterCatalog = new CatalogImpl();
		masterCatalog.setCode(CATALOG_CODE);
		masterCatalog.setMaster(true);

		category = new CategoryImpl();
		category.setCode(CATEGORY_CODE);
		category.setCatalog(masterCatalog);

		category2 = new CategoryImpl();
		category2.setCode(CATEGORY_CODE2);
		category2.setCatalog(masterCatalog);

		category3 = new CategoryImpl();
		category3.setCode(CATEGORY_CODE3);
		category3.setCatalog(masterCatalog);

		productCategoriesDTO = new ProductCategoriesDTO();
		product = new ProductImpl();
		product.setCode(PRODUCT_CODE);
		productCategoryImporter = new ProductCategoryImporterImpl();

		savingStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT, new SavingManager<Product>() {

			@Override
			public void save(final Product persistable) {
				// do nothing
			}

			@Override
			public Product update(final Product persistable) {
				return null;
			}

		});

		productService = context.mock(ProductService.class);
		context.checking(new Expectations() {
			{
				allowing(productService).findByGuid(with(aNull(String.class)), with(aNull(ProductLoadTuner.class)));
				will(returnValue(product));
			}
		});
		productCategoryImporter.setProductService(productService);
		
		cachingService = context.mock(CachingService.class);
		ProductCategoryAdapter productCategoryAdapter = new ProductCategoryAdapter();
		productCategoryAdapter.setCachingService(cachingService);
		productCategoryImporter.setProductCategoryAdapter(productCategoryAdapter);
		
		productCategoryImporter.setStatusHolder(new ImportStatusHolder());
	}

	/**
	 * Check an import with non-initialized importer.
	 */
	@Test(expected = ImportRuntimeException.class)
	public void testExecuteNonInitializedImport() {
		productCategoryImporter.executeImport(productCategoriesDTO);
	}

	/**
	 * Check an import of product categories.
	 */
	@Test
	public void testExecuteImport() {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<JobType, ImporterConfiguration>());

		productCategoryImporter.initialize(new ImportContext(importConfiguration), savingStrategy);
		productCategoryImporter.executeImport(productCategoriesDTO);
		assertEquals("product", productCategoryImporter.getImportedObjectName());
		assertNotNull(productCategoryImporter.getProductService());
		assertNotNull(productCategoryImporter.getSavingStrategy());
	}

	/**
	 * Test count objects qty.
	 */
	@Test
	public void testCountObjectsQty() {		
		ProductCategoriesDTO categoriesDTO = new ProductCategoriesDTO();
		List<CatalogCategoriesDTO> catalogCategoriesList = new ArrayList<CatalogCategoriesDTO>();
		CatalogCategoriesDTO catalogCategoriesDTO = new CatalogCategoriesDTO();

		List<ProductCategoryDTO> productCategoriesList = new ArrayList<ProductCategoryDTO>();
		productCategoriesList.add(new ProductCategoryDTO());
		productCategoriesList.add(new ProductCategoryDTO());

		catalogCategoriesDTO.setProductCategoryDTOList(productCategoriesList);
		catalogCategoriesList.add(catalogCategoriesDTO);

		categoriesDTO.setCatalogCategoriesDTOList(catalogCategoriesList);
		assertEquals(2, productCategoryImporter.getObjectsQty(categoriesDTO));
	}

	/**
	 * Test clear collection strategy for categories. 
	 */
	@Test
	public void testCollectionsStrategyForCategoryWhenCategoryNotFound() {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<JobType, ImporterConfiguration>());
		
		productCategoryImporter.initialize(new ImportContext(importConfiguration), savingStrategy);

		ProductCategoriesDTO categoriesDTO = createProductCategoriesDtoWithTwoCategories(
				masterCatalog.getCode(), CATEGORY_CODE, "unknownCategoryCode");

		product.addCategory(category);
		product.addCategory(category2);
		assertEquals("Sanity Check", category, product.getDefaultCategory(masterCatalog));
		
		context.checking(new Expectations() {
			{
				oneOf(cachingService).findCategoryByCode(CATEGORY_CODE, CATALOG_CODE); will(returnValue(category));
				oneOf(cachingService).findCategoryByCode("unknownCategoryCode", CATALOG_CODE); will(returnValue(null));
			}
		});
		CollectionsStrategy<Product, ProductCategoriesDTO> collectionsStrategy = productCategoryImporter.getCollectionsStrategy();
		assertTrue(collectionsStrategy.isForPersistentObjectsOnly());
		collectionsStrategy.prepareCollections(product, categoriesDTO);

		assertEquals(
				"Category [categoryCode] should stay, [categoryCode2] should go, and [unknownCategoryCode] is not found?! so it should not be added",
				1, product.getCategories().size());
		assertTrue("Master Category common, so it should still be present", product.getCategories().contains(category));
	}

	/**
	 * Test clear collection strategy for categories.
	 */
	@Test
	public void testCollectionsStrategyForCategoryStandardCase() {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<JobType, ImporterConfiguration>());
		
		productCategoryImporter.initialize(new ImportContext(importConfiguration), savingStrategy);

		ProductCategoriesDTO categoriesDTO = createProductCategoriesDtoWithTwoCategories(masterCatalog.getCode(), CATEGORY_CODE, CATEGORY_CODE3);

		product.addCategory(category);
		product.addCategory(category2);
		assertEquals("Sanity Check", category, product.getDefaultCategory(masterCatalog));

		context.checking(new Expectations() {
			{
				oneOf(cachingService).findCategoryByCode(CATEGORY_CODE, CATALOG_CODE); will(returnValue(category));
				oneOf(cachingService).findCategoryByCode(CATEGORY_CODE3, CATALOG_CODE); will(returnValue(category3));
			}
		});
		CollectionsStrategy<Product, ProductCategoriesDTO> collectionsStrategy = productCategoryImporter.getCollectionsStrategy();
		assertTrue(collectionsStrategy.isForPersistentObjectsOnly());
		collectionsStrategy.prepareCollections(product, categoriesDTO);

		assertEquals("Category [categoryCode] should stay, [categoryCode2] should go, and [categoryCode3] is found so it should be added",
				2, product.getCategories().size());
		assertTrue("Master Category common, so it should still be present", product.getCategories().contains(category));
		assertTrue("Category 3 new, so it should still be present", product.getCategories().contains(category3));
	}

	private ProductCategoriesDTO createProductCategoriesDtoWithTwoCategories(
			final String catalogCode, final String categoryCode1, final String categoryCode2) {
		ProductCategoriesDTO categoriesDTO = new ProductCategoriesDTO();
		List<CatalogCategoriesDTO> catalogCategoriesList = new ArrayList<CatalogCategoriesDTO>();
		CatalogCategoriesDTO catalogCategoriesDTO = new CatalogCategoriesDTO();
		catalogCategoriesDTO.setCatalogCode(catalogCode);

		List<ProductCategoryDTO> productCategoriesList = new ArrayList<ProductCategoryDTO>();
		final ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();
		productCategoryDTO.setCategoryCode(categoryCode1);

		ProductCategoryDTO newProductCategoryDTO = new ProductCategoryDTO();
		newProductCategoryDTO.setCategoryCode(categoryCode2);

		productCategoriesList.add(productCategoryDTO);
		productCategoriesList.add(newProductCategoryDTO);

		catalogCategoriesDTO.setProductCategoryDTOList(productCategoriesList);
		catalogCategoriesList.add(catalogCategoriesDTO);
		categoriesDTO.setCatalogCategoriesDTOList(catalogCategoriesList);
		return categoriesDTO;
	}

	/** The dto class must be present and correct. */
	@Test
	public void testDtoClass() {
		assertEquals("Incorrect DTO class", ProductCategoriesDTO.class, productCategoryImporter.getDtoClass());
	}

	/** The auxiliary JAXB class list must not be null (can be empty). */
	@Test
	public void testAuxiliaryJaxbClasses() {
		assertNotNull(productCategoryImporter.getAuxiliaryJaxbClasses());
	}
}
