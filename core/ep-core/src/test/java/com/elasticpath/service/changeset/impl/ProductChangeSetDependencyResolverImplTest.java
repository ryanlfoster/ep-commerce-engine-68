package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * The unit test class for product change set dependency resolver.
 */
public class ProductChangeSetDependencyResolverImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final ProductChangeSetDependencyResolverImpl resolver = new ProductChangeSetDependencyResolverImpl();
	private ProductAssociationService productAssociationService;
	
	/**
	 * The set up method.
	 */
	@Before
	public void setUp() {
		productAssociationService = context.mock(ProductAssociationService.class);
		resolver.setProductAssociationService(productAssociationService);
	}
	
	/**
	 * Test changeset dependency of product for Brand.
	 */
	@Test
	public void testBrandDependency() {
		
		final Brand brand = context.mock(Brand.class);
		Product product = new ProductImpl();
		product.setBrand(brand);
		mockProductAssociation();

		Set< ? > dependencies = resolver.getChangeSetDependency(product);
		assertEquals("Brand wasn't returned from depependency list", brand, dependencies.iterator().next());
		
	}
	
	/**
	 * Test changeset dependency of product for ProductType.
	 */
	@Test
	public void testProductTypeDependency() {
		
		final ProductType productType = context.mock(ProductType.class);
		Product product = new ProductImpl();
		product.setProductType(productType);
		mockProductAssociation();
	
		Set< ? > dependencies = resolver.getChangeSetDependency(product);
		assertEquals("ProductType wasn't returned from depependency list", productType, dependencies.iterator().next());
		
	}

	private void mockProductAssociation() {
		final ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
		context.checking(new Expectations() { {
			oneOf(productAssociationService).findByCriteria(criteria, null);
		    will(returnValue(new LinkedList<ProductAssociation>()));
		} });
	}
	
	/**
	 * Test getting change set dependency for product.
	 */
	@Test
	public void testGetChangeSetDependency() {
		Object obj = new Object();
		Set< ? > dependencies = resolver.getChangeSetDependency(obj);
		assertTrue("Non-Product object should not be processed", dependencies.isEmpty());
		
		final Category category = new CategoryImpl();
		category.setCatalog(new CatalogImpl());
		Product product = new ProductImpl();
		product.addCategory(category);
		product.setCode("TEST PRODUCT");
		Product associatedProduct = new ProductImpl();
		final ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
		criteria.setSourceProductCode(product.getCode());
		criteria.setWithinCatalogOnly(false);
		final List<ProductAssociation> productAssociations = new LinkedList<ProductAssociation>();
		ProductAssociation productAssociation = new ProductAssociationImpl();
		productAssociation.setSourceProduct(product);
		productAssociation.setTargetProduct(associatedProduct);
		productAssociations.add(productAssociation);
		context.checking(new Expectations() { {
			oneOf(productAssociationService).findByCriteria(criteria, null);
		    will(returnValue(productAssociations));
		} });
		dependencies = resolver.getChangeSetDependency(product);
		assertEquals("the category is not found in the dependency list of the product.", category, CollectionUtils.get(dependencies, 0));
		assertEquals("the associated product is not found in the dependency list of the product.", 
				associatedProduct, CollectionUtils.get(dependencies, 1));
	}

}
