package com.elasticpath.importexport.exporter.exporters.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.importexport.common.adapters.productcategories.ProductCategoryAdapter;
import com.elasticpath.importexport.common.dto.productcategory.ProductCategoriesDTO;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.impl.SummaryImpl;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.service.catalog.ProductService;

/**
 * Product category exporter test.
 */
@RunWith(JMock.class)
@SuppressWarnings("PMD.NonStaticInitializer")
public class ProductCategoryExporterImplTest {

	private static final Long PRODUCT_UID = 1L;

	private ProductCategoryExporterImpl productCategoryExporter;
	private ExportContext exportContext;
	private ProductService productService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of error happens
	 */
	@Before
	public void setUp() throws Exception {
		productService = context.mock(ProductService.class);

		final Catalog catalog = new CatalogImpl();
		catalog.setCode("CATALOG");
		
		final Category category = new CategoryImpl();
		category.setCatalog(catalog);
		category.setUidPk(1L);
		final Product product = new ProductImpl();
		product.setUidPk(PRODUCT_UID);
		product.addCategory(category);

		context.checking(new Expectations() {
			{
				allowing(productService).findByUids(with(Collections.singletonList(PRODUCT_UID)), with(aNull(ProductLoadTuner.class)));
				will(returnValue(Arrays.asList(product)));
			}
		});

		productCategoryExporter = new ProductCategoryExporterImpl();
		productCategoryExporter.setProductService(productService);
		productCategoryExporter.setProductCategoryAdapter(new MockProductCategoryAdapter());
	}
	
	/**
	 * Check that during initialization exporter prepares the list of UidPk for product categories to be exported.
	 */
	@Test
	public void testExporterInitialization() throws Exception {
		final List<Long> productUidPkList = new ArrayList<Long>();
		productUidPkList.add(PRODUCT_UID);

		ExportConfiguration exportConfiguration = new ExportConfiguration();
		SearchConfiguration searchConfiguration = new SearchConfiguration();
		
		exportContext = new ExportContext(exportConfiguration, searchConfiguration);
		exportContext.setSummary(new SummaryImpl());
		
		List<Class< ? >> dependentClasses = new ArrayList<Class< ? >>();
		dependentClasses.add(Product.class);
		dependentClasses.add(Category.class);
		DependencyRegistry dependencyRegistry = new DependencyRegistry(dependentClasses);
		
		dependencyRegistry.addUidDependencies(Product.class, new HashSet<Long>(productUidPkList));
		exportContext.setDependencyRegistry(dependencyRegistry);
		
		productCategoryExporter.initialize(exportContext);
	}

	/**
	 * Check an export of product categories.
	 */
	@Test
	public void testProcessExport() throws Exception {
		testExporterInitialization();
		productCategoryExporter.processExport(System.out);
		Summary summary = productCategoryExporter.getContext().getSummary();
		assertEquals(1, summary.getCounters().size());
		assertNotNull(summary.getCounters().get(JobType.PRODUCTCATEGORYASSOCIATION));
		assertEquals(1, summary.getCounters().get(JobType.PRODUCTCATEGORYASSOCIATION).intValue());
		assertEquals(0, summary.getFailures().size());
		assertNotNull(summary.getStartDate());
		assertNotNull(summary.getElapsedTime());
		assertNotNull(summary.getElapsedTime().toString());

		Set<Long> dependentUids = exportContext.getDependencyRegistry().getDependentUids(Category.class);
		assertEquals(dependentUids, Collections.singleton(1L));
	}
	
	/**
	 * Mock product category adapter.
	 */
	private class MockProductCategoryAdapter extends ProductCategoryAdapter {

		@Override
		public void populateDomain(final ProductCategoriesDTO productCategoriesDTO, final Product product) {
			// do nothing
		}

		@Override
		public void populateDTO(final Product product, final ProductCategoriesDTO productCategoriesDTO) {
			// do nothing
		}

	}

}
