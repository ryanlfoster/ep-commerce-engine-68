package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalog.impl.CategoryTypeImpl;
import com.elasticpath.importexport.common.adapters.catalogs.AttributeAdapter;
import com.elasticpath.importexport.common.adapters.catalogs.BrandAdapter;
import com.elasticpath.importexport.common.adapters.catalogs.CatalogAdapter;
import com.elasticpath.importexport.common.adapters.catalogs.CategoryTypeAdapter;
import com.elasticpath.importexport.common.adapters.pricing.TestAdapterHelper;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.catalogs.AttributeDTO;
import com.elasticpath.importexport.common.dto.catalogs.BrandDTO;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.dto.catalogs.CategoryTypeDTO;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.CategoryTypeService;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.search.SynonymGroupService;

/**
 * Test for <code>CatalogImporterImpl</code>.
 */
public class CatalogImporterImplTest {

	private CatalogImporterImpl catalogImporter;

	private TestAdapterHelper helper;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	private CatalogService mockCatalogService;

	@Mock
	private CachingService mockCachingService;

	@Mock
	private BrandService mockBrandService;

	@Mock
	private CategoryTypeService mockCategoryTypeService;

	@Mock
	private AttributeService mockAttributeService;

	@Mock
	private PersistenceEngine mockSession;

	@Mock
	private SynonymGroupService mockSynonymGroupService;

	@Mock
	private SkuOptionService skuOptionService;

	@Mock
	private ProductTypeService productTypeService;

	private CatalogDTO catalogDTO;

	private Catalog catalog;

	private SavingStrategy<Catalog, CatalogDTO> savingStrategy;


	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of error happens
	 */
	@Before
	public void setUp() throws Exception {
		helper = new TestAdapterHelper();
		final String ctCode = "ct_code";
		catalogDTO = helper.createCatalogDTO(ctCode);
		catalog = helper.createCatalog(ctCode);
		catalog.setUidPk(1L);

		catalogImporter = new CatalogImporterImpl();
		context.checking(new Expectations() {
			{
				allowing(mockCachingService).findCatalogByCode(ctCode);
				will(returnValue(catalog));

				allowing(mockCatalogService).load(with(any(long.class)), with(any(FetchGroupLoadTuner.class)), with(any(boolean.class)));
				will(returnValue(catalog));
			}
		});
		catalogImporter.setCachingService(mockCachingService);
		catalogImporter.setCatalogService(mockCatalogService);

		final Brand brand = new BrandImpl();
		brand.setCode("b_code");
		brand.setCatalog(catalog);
		brand.setUidPk(1L);
		context.checking(new Expectations() {
			{
				allowing(mockBrandService).findByCode("b_code");
				will(returnValue(brand));
				allowing(mockBrandService).findAllBrandsFromCatalog(with(any(long.class)));
				will(returnValue(Collections.emptyList()));
			}
		});
		
		final CategoryType categoryType = new CategoryTypeImpl();
		categoryType.setName("cat_name");
		categoryType.setUidPk(1L);
		categoryType.setCatalog(catalog);
		context.checking(new Expectations() {
			{
				allowing(mockCategoryTypeService).findCategoryType("cat_name");
				will(returnValue(categoryType));
				allowing(mockCategoryTypeService).findAllCategoryTypeFromCatalog(with(any(long.class)));
				will(returnValue(Collections.emptyList()));
			
				allowing(mockAttributeService).findByKey(with(any(String.class)));
				will(returnValue(null));
				allowing(mockAttributeService).findAllCatalogOrGlobalAttributes(with(any(long.class)));
				will(returnValue(Collections.emptyList()));

				allowing(skuOptionService).findAllSkuOptionFromCatalog(with(any(long.class)));
				will(returnValue(Collections.emptyList()));
				allowing(productTypeService).findAllProductTypeFromCatalog(with(any(long.class)));
				will(returnValue(Collections.emptyList()));
				allowing(mockSynonymGroupService).findAllSynonymGroupForCatalog(with(any(long.class)));
				will(returnValue(Collections.emptyList()));
			}
		});


		catalogImporter.setCategoryTypeService(mockCategoryTypeService);
		catalogImporter.setBrandService(mockBrandService);
		catalogImporter.setAttributeService(mockAttributeService);
		catalogImporter.setSynonymGroupService(mockSynonymGroupService);
		catalogImporter.setSkuOptionService(skuOptionService);
		catalogImporter.setProductTypeService(productTypeService);
		context.checking(new Expectations() {
			{
				allowing(mockSession).update(catalog);
				will(returnValue(catalog));
				allowing(mockSession).update(brand);
				will(returnValue(brand));
				allowing(mockSession).update(categoryType);
				will(returnValue(categoryType));
				allowing(mockSession).save(with(any(Persistable.class)));
			}
		});

		SavingManager<Catalog> savingManager = new SavingManager<Catalog>() {
			@Override
			public void save(final Catalog persistable) {
				mockSession.save(persistable);
			}

			@Override
			public Catalog update(final Catalog persistable) {
				return mockSession.update(persistable);
			}
		};
		savingStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT_OR_UPDATE, savingManager);

		catalogImporter.setCatalogAdapter(new MockCatalogAdapter());
		catalogImporter.setBrandAdapter(new MockBrandAdapter());
		catalogImporter.setAttributeAdapter(new MockAttributeAdapter());
		catalogImporter.setCategoryTypeAdapter(new MockCategoryTypeAdapter());
		catalogImporter.setStatusHolder(new ImportStatusHolder());
	}

	/**
	 * Check an import with non-initialized importer.
	 */
	@Test
	public void testExecuteNonInitializedImport() {
		try {
			catalogImporter.executeImport(catalogDTO);
		} catch (ImportRuntimeException e) {
			return;
		}
		fail();
	}

	/**
	 * Check an import of one product.
	 */
	@Test
	public void testExecuteImport() {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<JobType, ImporterConfiguration>());
		context.checking(new Expectations() {
			{
				oneOf(mockCatalogService).saveOrUpdate(catalog);
				will(returnValue(null));
			}
		});
		catalogImporter.initialize(new ImportContext(importConfiguration), savingStrategy);
		catalogImporter.executeImport(catalogDTO);
		assertEquals("catalog", catalogImporter.getImportedObjectName());
		assertNotNull(catalogImporter.getCatalogService());
		assertNotNull(catalogImporter.getSavingStrategy());
	}

	/** The import classes should at least contain the DTO class we are operating on. */
	@Test
	public void testDtoClass() {
		assertEquals("Incorrect DTO class", CatalogDTO.class, catalogImporter.getDtoClass());
	}

	/** The auxiliary JAXB class list must not be null (can be empty). */
	@Test
	public void testAuxiliaryJaxbClasses() {
		assertNotNull(catalogImporter.getAuxiliaryJaxbClasses());
	}

	/**
	 * Mock catalog adapter.
	 */
	private class MockCatalogAdapter extends CatalogAdapter {

		@Override
		public void populateDomain(final CatalogDTO catalogDTO, final Catalog catalog) {
			// do nothing
		}

		@Override
		public void populateDTO(final Catalog catalog, final CatalogDTO catalogDTO) {
			// do nothing
		}

	}

	/**
	 * Mock brand adapter.
	 */
	private class MockBrandAdapter extends BrandAdapter {

		@Override
		public void populateDomain(final BrandDTO source, final Brand target) {
			// do nothing
		}

		@Override
		public void populateDTO(final Brand source, final BrandDTO target) {
			// do nothing
		}
	}
	
	/**
	 * Mock for attribute adapter.
	 */
	private class MockAttributeAdapter extends AttributeAdapter {

		@Override
		public void populateDomain(final AttributeDTO attributeDTO, final Attribute attribute) {
			// do nothing	
		}

		@Override
		public void populateDTO(final Attribute attribute, final AttributeDTO attributeDTO) {
			// do nothing
		}

		@Override
		public Attribute createDomainObject() {
			return new AttributeImpl();
		}
		
		
	}
	
	/**
	 * Mock for category type attribute.
	 */
	private class MockCategoryTypeAdapter extends CategoryTypeAdapter {

		@Override
		public void populateDomain(final CategoryTypeDTO categoryTypeDTO, final CategoryType categoryType) {
			// do nothing		
		}

		@Override
		public void populateDTO(final CategoryType categoryType, final CategoryTypeDTO categoryTypeDTO) {
			// do nothing
		}
		
	}
}
