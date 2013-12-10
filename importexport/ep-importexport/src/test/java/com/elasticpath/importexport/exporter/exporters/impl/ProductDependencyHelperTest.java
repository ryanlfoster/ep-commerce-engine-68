package com.elasticpath.importexport.exporter.exporters.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * Tests of the ProductDependencyHelper.
 */
public class ProductDependencyHelperTest {

	private ProductDependencyHelper helper;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ProductAssociationService mockProductAssociationService;

	private List<Long> uidList;

	private DependencyRegistry registry;

	private CatalogImpl fakeCatalog;

	private BrandImpl fakeBrand;
	
	private BrandImpl fakeBrand2;
	
	private CategoryImpl fakeCategory;

	@Before
	public void setUp() throws Exception {
		List<Class< ? >> dependentTypes = new ArrayList<Class< ? >>();
		dependentTypes.addAll(Arrays.asList(new ProductExporterImpl().getDependentClasses()));
		dependentTypes.addAll(Arrays.asList(new CatalogExporterImpl().getDependentClasses()));
		dependentTypes.addAll(Arrays.asList(new ProductAssociationExporterImpl().getDependentClasses()));

		registry = new DependencyRegistry(dependentTypes);
		uidList = new ArrayList<Long>();
		mockProductAssociationService = context.mock(ProductAssociationService.class);
		helper = new ProductDependencyHelper(registry, uidList, null, mockProductAssociationService, null);

		fakeCatalog = new CatalogImpl();
		fakeCatalog.setUidPk(1);
		
		fakeBrand = new BrandImpl();
		fakeBrand.setUidPk(1);
		fakeBrand.setCatalog(fakeCatalog);
		
		fakeBrand2 = new BrandImpl();
		fakeBrand2.setUidPk(2);
		fakeBrand2.setCatalog(fakeCatalog);

		fakeCategory = new CategoryImpl();
		fakeCategory.setUidPk(1);
		fakeCategory.setCatalog(fakeCatalog);
	}

	/**
	 * Test registering dependencies for single unassociated product.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSingleProductDependencies() throws Exception {
		Product product1 = createProduct(1, fakeBrand, fakeCategory);
		uidList.add(product1.getUidPk());
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		context.checking(new Expectations() {
			{
				allowing(mockProductAssociationService).findByCriteria(with(any(ProductAssociationSearchCriteria.class)));
				will(returnValue(new ArrayList<ProductAssociation>()));
			}
		});
		
		helper.addDependencies(products);
		assertTrue(registry.getDependentUids(Brand.class).contains(Long.valueOf(fakeBrand.getUidPk())));
	}
	
	/**
	 * Test registering dependencies for multiple unassociated products.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMultipleProductCatalogDependencies() throws Exception {
		Product product1 = createProduct(1, fakeBrand, fakeCategory);
		uidList.add(product1.getUidPk());
		Product product2 = createProduct(2, fakeBrand2, fakeCategory);
		uidList.add(product2.getUidPk());
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
		context.checking(new Expectations() {
			{
				allowing(mockProductAssociationService).findByCriteria(with(any(ProductAssociationSearchCriteria.class)));
				will(returnValue(new ArrayList<ProductAssociation>()));
			}
		});
		
		helper.addDependencies(products);
		assertTrue(registry.getDependentUids(Brand.class).contains(Long.valueOf(fakeBrand.getUidPk())));
		assertTrue(registry.getDependentUids(Brand.class).contains(Long.valueOf(fakeBrand2.getUidPk())));
	}

	/**
	 * Test registering dependencies for associated products.
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testAssociatedProductDependencies() throws Exception {
		Product product1 = createProduct(1, fakeBrand, fakeCategory);
		uidList.add(product1.getUidPk());
		Product product2 = createProduct(2, fakeBrand2, fakeCategory);
		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		
		final ProductAssociationImpl association = new ProductAssociationImpl();
		association.setUidPk(1);
		association.setSourceProduct(product1);
		association.setTargetProduct(product2);
		final List<ProductAssociation> associations = new ArrayList<ProductAssociation>();
		associations.add(association);
		context.checking(new Expectations() {
			{
				allowing(mockProductAssociationService).findByCriteria(with(any(ProductAssociationSearchCriteria.class)));
				will(returnValue(associations));
			}
		});
		
		helper.addDependencies(products);
		assertTrue(registry.getDependentUids(ProductAssociation.class).contains(Long.valueOf(association.getUidPk())));
		assertTrue(uidList.contains(Long.valueOf(product2.getUidPk())));
	}
	
	private Product createProduct(final long uidPk, final Brand brand, final Category category) {
		Product product1 = new ProductImpl();
		product1.setUidPk(uidPk);
		product1.setBrand(brand);
		Set<Category> categories = new HashSet<Category>();
		categories.add(category);
		product1.setCategories(categories);
		return product1;
	}
}
