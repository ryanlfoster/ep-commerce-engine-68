package com.elasticpath.importexport.exporter.exporters.impl;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.elasticpath.importexport.common.adapters.DomainAdapter;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.CategoryAttributeValueImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.CategoryTypeImpl;
import com.elasticpath.importexport.common.dto.category.CategoryDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.impl.SummaryImpl;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.ExporterConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Category exporter test.
 */
public class CategoryExporterImplTest {

	private static final String QUERY = "FIND Category WHERE CatalogCode='1000500'";

	private static final long CATEGORY_UID = 1L;
	private static final long CATALOG_UID = 2;
	private static final long CATEGORY_TYPE_UID = 3;
	private static final long ATTRIBUTE_UID = 4;
	private CategoryExporterImpl categoryExporter;
	private ExportContext exportContext;
	private ElasticPath elasticPath;
	private CategoryService categoryService;
	private ImportExportSearcher importExportSearcher;
	private ExportConfiguration exportConfiguration;
	private SearchConfiguration searchConfiguration;
	private CategoryLoadTuner categoryLoadTuner;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of error happens
	 */
	@Before
	public void setUp() throws Exception {
		elasticPath = context.mock(ElasticPath.class);
		categoryService = context.mock(CategoryService.class);
		@SuppressWarnings("unchecked")
		final DomainAdapter<Category, CategoryDTO> categoryAdapter = context.mock(DomainAdapter.class);
		importExportSearcher = context.mock(ImportExportSearcher.class);
		categoryLoadTuner = context.mock(CategoryLoadTuner.class);

		final Category category = new CategoryImpl();
		category.setUidPk(CATEGORY_UID);
		Catalog catalog = new CatalogImpl();
		catalog.setUidPk(CATALOG_UID);
		AttributeValue attributeValue = new CategoryAttributeValueImpl();
		Attribute attribute = new AttributeImpl();
		attribute.setUidPk(ATTRIBUTE_UID);
		attributeValue.setAttribute(attribute);
		CategoryType categoryType = new CategoryTypeImpl();
		categoryType.setUidPk(CATEGORY_TYPE_UID);
		categoryType.setCatalog(catalog);
		category.setAttributeValueMap(Collections.singletonMap("atr_key", attributeValue));
		category.setCategoryType(categoryType);
		category.setCatalog(catalog);

		final CategoryDTO dto = new CategoryDTO();
		context.checking(new Expectations() {
			{
				allowing(categoryService).load(CATEGORY_UID, categoryLoadTuner);
				will(returnValue(category));
				allowing(categoryService).findLinkedCategories(CATEGORY_UID);
				will(returnValue(new ArrayList<Category>()));
				allowing(categoryService).findAncestorCategoryUidsWithTreeOrder(Collections.singleton(category.getUidPk()));
				will(returnValue(new HashSet<Long>()));
				allowing(categoryService).findDescendantCategoryUids(category.getUidPk());
				will(returnValue(new ArrayList<Long>()));
				allowing(categoryAdapter).createDtoObject();
				will(returnValue(dto));
				allowing(categoryAdapter).populateDTO(category, dto);
			}
		});

		categoryExporter = new CategoryExporterImpl();
		categoryExporter.setElasticPath(elasticPath);
		categoryExporter.setImportExportSearcher(importExportSearcher);
		categoryExporter.setCategoryService(categoryService);
		categoryExporter.setCategoryLoadTuner(categoryLoadTuner);
		categoryExporter.setCategoryAdapter(categoryAdapter);
	}

	/**
	 * Check that during initialization exporter prepares the list of UidPk for categories to be exported.
	 */
	@Test
	public void testExporterInitialization() throws ConfigurationException {
		final List<Long> categoryUidPkList = new ArrayList<Long>();
		categoryUidPkList.add(CATEGORY_UID);

		exportConfiguration = new ExportConfiguration();
		searchConfiguration = new SearchConfiguration();

		ExporterConfiguration exporterConfiguration = new ExporterConfiguration();
		exportConfiguration.setExporterConfiguration(exporterConfiguration);

		searchConfiguration.setEpQLQuery(QUERY);
		exportContext = new ExportContext(exportConfiguration, searchConfiguration);
		exportContext.setSummary(new SummaryImpl());
		exportContext.setDependencyRegistry(new DependencyRegistry(Arrays
				.asList(new Class< ? >[] { Category.class, Catalog.class, CategoryType.class, Attribute.class })));

		context.checking(new Expectations() {
			{
				oneOf(importExportSearcher).searchUids(searchConfiguration, EPQueryType.CATEGORY);
				will(returnValue(categoryUidPkList));
			}
		});

		categoryExporter.initialize(exportContext);
	}

	/**
	 * Check an export of one product without export criteria.
	 */
	@Test
	public void testProcessExportWithoutCriteria() throws ConfigurationException {
		testExporterInitialization();
		categoryExporter.processExport(System.out);
		Summary summary = categoryExporter.getContext().getSummary();
		assertEquals(1, summary.getCounters().size());
		assertNotNull(summary.getCounters().get(JobType.CATEGORY));
		assertEquals(1, summary.getCounters().get(JobType.CATEGORY).intValue());
		assertEquals(0, summary.getFailures().size());
		assertNotNull(summary.getStartDate());
		assertNotNull(summary.getElapsedTime());
		assertNotNull(summary.getElapsedTime().toString());
		DependencyRegistry registry = exportContext.getDependencyRegistry();
		assertThat("Missing catalog dependency", registry.getDependentUids(Catalog.class), hasItem(CATALOG_UID));
		assertEquals("Extra catalog uid in dependency registry", 1, registry.getDependentUids(Catalog.class).size());
		assertThat("Missing category type dependency", registry.getDependentUids(CategoryType.class), hasItem(CATEGORY_TYPE_UID));
		assertEquals("Extra category type uid in dependency registry", 1, registry.getDependentUids(CategoryType.class).size());
		assertThat("Missing attribute dependency", registry.getDependentUids(Attribute.class), hasItem(ATTRIBUTE_UID));
		assertEquals("Extra attribute uid in dependency registry", 1, registry.getDependentUids(Attribute.class).size());
	}

}
