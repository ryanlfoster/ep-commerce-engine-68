package com.elasticpath.importexport.exporter.exporters.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elasticpath.domain.attribute.impl.AttributeImpl;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.search.SynonymGroup;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.impl.SummaryImpl;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.CategoryTypeService;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.search.SynonymGroupService;

/**
 * Catalog exporter test.
 */
public class CatalogExporterImplTest {

	private static final long CATALOG_UID = 1L;

	private CatalogExporterImpl catalogExporter = null;

	private ExportContext exportContext;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ElasticPath elasticPath;

	private CatalogService catalogService;

	private DomainAdapter<Catalog, CatalogDTO> catalogAdapter;
	
	private ImportExportSearcher importExportSearcher;

	private AttributeService attributeService;


	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of error happens
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		elasticPath = context.mock(ElasticPath.class);
		catalogService = context.mock(CatalogService.class);
		catalogAdapter = context.mock(DomainAdapter.class);
		importExportSearcher = context.mock(ImportExportSearcher.class);

		final CatalogDTO catalogDTO = new CatalogDTO();
		final Catalog catalog = new CatalogImpl();
		catalog.setUidPk(CATALOG_UID);
		context.checking(new Expectations() {
			{
				allowing(catalogService).getCatalog(CATALOG_UID);
				will(returnValue(catalog));
				allowing(catalogAdapter).populateDTO(catalog, catalogDTO);
				allowing(catalogAdapter).createDtoObject();
				will(returnValue(catalogDTO));
			}
		});

		final CategoryTypeService mockCategoryTypeService = context.mock(CategoryTypeService.class);
		final ProductTypeService mockProductTypeService = context.mock(ProductTypeService.class);
		final SkuOptionService mockSkuOptionService = context.mock(SkuOptionService.class);
		final SynonymGroupService mockSynonymGroupService = context.mock(SynonymGroupService.class);
		final BrandService mockBrandService = context.mock(BrandService.class);
		attributeService = context.mock(AttributeService.class);

		context.checking(new Expectations() {
			{
				allowing(mockBrandService).findAllBrandsFromCatalog(CATALOG_UID);
				will(returnValue(new ArrayList<Brand>()));
				allowing(attributeService).findAllCatalogOrGlobalAttributes(CATALOG_UID);
				will(returnValue(new ArrayList<Attribute>()));
				allowing(mockCategoryTypeService).findAllCategoryTypeFromCatalog(CATALOG_UID);
				will(returnValue(new ArrayList<CategoryType>()));
				allowing(mockProductTypeService).findAllProductTypeFromCatalog(CATALOG_UID);
				will(returnValue(new ArrayList<ProductType>()));
				allowing(mockSkuOptionService).findAllSkuOptionFromCatalog(CATALOG_UID);
				will(returnValue(new ArrayList<SkuOption>()));
				allowing(mockSynonymGroupService).findAllSynonymGroupForCatalog(CATALOG_UID);
				will(returnValue(new ArrayList<SynonymGroup>()));

				allowing(elasticPath).getBean(ContextIdNames.CATEGORY_TYPE_SERVICE);
				will(returnValue(mockCategoryTypeService));
				allowing(elasticPath).getBean(ContextIdNames.PRODUCT_TYPE_SERVICE);
				will(returnValue(mockProductTypeService));
				allowing(elasticPath).getBean(ContextIdNames.SKU_OPTION_SERVICE);
				will(returnValue(mockSkuOptionService));
				allowing(elasticPath).getBean(ContextIdNames.SYNONYM_GROUP_SERVICE);
				will(returnValue(mockSynonymGroupService));
				allowing(elasticPath).getBean(ContextIdNames.BRAND_SERVICE);
				will(returnValue(mockBrandService));
				allowing(elasticPath).getBean(ContextIdNames.ATTRIBUTE_SERVICE);
				will(returnValue(attributeService));
			}
		});

		catalogExporter = new CatalogExporterImpl();
		catalogExporter.setElasticPath(elasticPath);
		catalogExporter.setImportExportSearcher(importExportSearcher);
		catalogExporter.setCatalogService(catalogService);
		catalogExporter.setCatalogAdapter(catalogAdapter);
	}

	/**
	 * Check that during initialization exporter prepares the list of UidPk for catalogs to be exported.
	 */
	@Test
	public void testExporterInitialization() throws Exception {
		final List<Long> catalogUidPkList = new ArrayList<Long>();
		catalogUidPkList.add(CATALOG_UID);

		ExportConfiguration exportConfiguration = new ExportConfiguration();
		final SearchConfiguration searchConfiguration = new SearchConfiguration();
		searchConfiguration.setEpQLQuery("FIND Catalog");

		context.checking(new Expectations() {
			{
				oneOf(importExportSearcher).searchUids(searchConfiguration, EPQueryType.CATALOG);
				will(returnValue(catalogUidPkList));
			}
		});
		exportContext = new ExportContext(exportConfiguration, searchConfiguration);
		exportContext.setSummary(new SummaryImpl());

		exportContext.setDependencyRegistry(new DependencyRegistry(Arrays.asList(new Class<?>[]{Catalog.class})));

		catalogExporter.initialize(exportContext);
	}

	/**
	 * Check an export of one catalog with export criteria.
	 */
	@Test
	public void testProcessExportWithCriteria() throws Exception {
		testExporterInitialization();
		catalogExporter.processExport(System.out);
		Summary summary = catalogExporter.getContext().getSummary();
		assertEquals(1, summary.getCounters().size());
		assertNotNull(summary.getCounters().get(JobType.CATALOG));
		assertEquals(1, summary.getCounters().get(JobType.CATALOG).intValue());
		assertEquals(0, summary.getFailures().size());
		assertNotNull(summary.getStartDate());
		assertNotNull(summary.getElapsedTime());
		assertNotNull(summary.getElapsedTime().toString());
	}

	/**
	 * Tests that calling get attributes returns global attributes. (BB-1211)
	 */
	@Test
	public void testGetAttributesForGlobalAttributes() {
		CatalogExporterImpl exporter = new CatalogExporterImpl();

		final List<Attribute> attributeList = new ArrayList<Attribute>();
		AttributeImpl attribute = new AttributeImpl();
		// Note that no catalog is set on the attribute.
		attributeList.add(attribute);

		context.checking(new Expectations() {
			{
				allowing(attributeService).findAllCatalogOrGlobalAttributes(0L);
				will(returnValue(attributeList));
			}
		});
		// Set here so that we get the expectations
		exporter.setElasticPath(elasticPath);
		AttributeDependentExporterImpl attributeExporter = new AttributeDependentExporterImpl();
		attributeExporter.setAttributeService(attributeService);

		List<Attribute> actualList = attributeExporter.getByCatalog(0L);
		assertEquals("The attribute above should be here", 1, actualList.size());
		assertEquals("The attribute above should be here", attribute, actualList.get(0));
	}
}
