package com.elasticpath.importexport.exporter.exporters.impl;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Test for {@link LinkedCategoryDependentExporterImpl}.
 */
@RunWith(JMock.class)
@SuppressWarnings("PMD.NonStaticInitializer")
public class LinkedCategoryDependentExporterImplTest {
	private final LinkedCategoryDependentExporterImpl linkedCategoryExporter = new LinkedCategoryDependentExporterImpl();
	private CategoryService categoryService;
	private DependentExporterFilter dependentExporterFilter;
	private ExportContext exportContext;
	private final Mockery context = new JUnit4Mockery();
	private static final long CATALOG_UID = 14441;

	/**
	 * Test initialization.
	 * 
	 * @throws ConfigurationException in case of errors
	 */
	@Before
	public void setUp() throws ConfigurationException {
		categoryService = context.mock(CategoryService.class);
		linkedCategoryExporter.setCategoryService(categoryService);

		dependentExporterFilter = context.mock(DependentExporterFilter.class);
		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		linkedCategoryExporter.initialize(exportContext, dependentExporterFilter);
	}

	/** Tests finding dependent objects. */
	@Test
	public void testFindDependentObjects() {
		Category linkedCategory1 = context.mock(Category.class, "linkedCategory-1");
		Category linkedCategory2 = context.mock(Category.class, "linkedCategory-2");
		final List<Category> linkedCatgoryList = Arrays.asList(linkedCategory1, linkedCategory2);
		context.checking(new Expectations() {
			{
				one(categoryService).findLinkedCategories(CATALOG_UID);
				will(returnValue(linkedCatgoryList));
			}
		});

		assertEquals(linkedCatgoryList, linkedCategoryExporter.findDependentObjects(CATALOG_UID));
	}

	/** Tests finding dependent objects when we the {@link DependencyRegistry} supports {@link Catalog}s. */
	@Test
	public void testFindDependentObjectsNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Arrays.<Class<?>> asList(Catalog.class));
		exportContext.setDependencyRegistry(registry);

		final long catalog1Uid = 9;
		final long catalog2Uid = 4541;

		final Catalog catalog1 = context.mock(Catalog.class, "catalog-1");
		final Catalog catalog2 = context.mock(Catalog.class, "catalog-2");

		final Category category1 = context.mock(Category.class, "category-1");
		final Category category2 = context.mock(Category.class, "category-2");
		final Category category3 = context.mock(Category.class, "category-3");
		context.checking(new Expectations() {
			{
				allowing(catalog1).getUidPk();
				will(returnValue(catalog1Uid));
				allowing(catalog2).getUidPk();
				will(returnValue(catalog2Uid));

				allowing(category1).getCatalog();
				will(returnValue(catalog1));
				allowing(category2).getCatalog();
				will(returnValue(catalog2));
				allowing(category3).getCatalog();
				will(returnValue(catalog1));

				one(categoryService).findLinkedCategories(CATALOG_UID);
				will(returnValue(Arrays.asList(category1, category2, category3)));
			}
		});

		final int expectedCategories = 3;
		List<Category> result = linkedCategoryExporter.findDependentObjects(CATALOG_UID);
		assertThat("Missing category-1", result, hasItem(category1));
		assertThat("Missing category-2", result, hasItem(category2));
		assertThat("Missing category-3", result, hasItem(category3));
		assertEquals("Other Categories returned?", expectedCategories, result.size());

		Set<Long> catalogDependencies = registry.getDependentUids(Catalog.class);
		assertThat("Missing catalog1Uid", catalogDependencies, hasItem(catalog1Uid));
		assertThat("Missing catalog2Uid", catalogDependencies, hasItem(catalog2Uid));
		assertEquals("Other catalogs reutrned?", 2, catalogDependencies.size());
	}
}
