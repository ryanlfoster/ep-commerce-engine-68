package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.action.CustomAction;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.test.BeanFactoryExpectationsFactory;


/**
 * Tests <code>ProductAssociationImpl</code>.
 *
 */
public class ProductAssociationServiceImpl2Test {
	
	private static final int ONE = 1;
	private static final int TEN = 10;
	private static final int TWENTY = 20;
	private static final int THIRTY = 30;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ProductAssociationServiceImpl productAssociationService;
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private StoreProduct targetProduct1;
	private StoreProduct targetProduct2;
	private StoreProduct targetProduct3;
	private StoreProduct targetProduct4;
	private ProductAssociation expectedProductAssociation1;
	private ProductAssociation expectedProductAssociation2;
	private ProductAssociation expectedProductAssociation3;
	private ProductAssociation expectedProductAssociation4;
	private StoreProduct storeProduct1;
	private StoreProduct storeProduct2;
	private Set<StoreProduct> storeProducts;
	
	/**
	 * Setup the ProductAssociationServiceImpl.
	 */
	@Before
	public void setUp()	{
		FetchPlanHelper mockFetchPlanHelper = context.mock(FetchPlanHelper.class);
		PersistenceEngine mockPersistenceEngine = context.mock(PersistenceEngine.class);
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		productAssociationService = new ProductAssociationServiceImpl();
		productAssociationService.setPersistenceEngine(mockPersistenceEngine);
		productAssociationService.setFetchPlanHelper(mockFetchPlanHelper);

		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.PRODUCT_ASSOCIATION);
				will(returnValue(new CustomAction("foo") {
					public Object invoke(final Invocation arg0) throws Throwable {
						return new ProductAssociationImpl();
					}
				}));
			}
		});

		targetProduct1 = mockStoreProduct("targetProduct1");
		targetProduct2 = mockStoreProduct("targetProduct2");
		targetProduct3 = mockStoreProduct("targetProduct3");
		targetProduct4 = mockStoreProduct("targetProduct4");
		
		allowingSalesCount(targetProduct1, THIRTY);
		allowingSalesCount(targetProduct2, TWENTY);
		allowingSalesCount(targetProduct3, TEN);
		allowingSalesCount(targetProduct4, ONE);
		
		expectedProductAssociation1 = mockProductAssociation("expectedProductAssociation1", targetProduct1);
		expectedProductAssociation2 = mockProductAssociation("expectedProductAssociation2", targetProduct2);
		expectedProductAssociation3 = mockProductAssociation("expectedProductAssociation3", targetProduct3);
		expectedProductAssociation4 = mockProductAssociation("expectedProductAssociation4", targetProduct4);
		
		storeProduct1 = mockStoreProduct("Product1", setOf(expectedProductAssociation1, expectedProductAssociation4));
		storeProduct2 = mockStoreProduct("Product2", setOf(expectedProductAssociation2, expectedProductAssociation3));
		
		storeProducts = setOf(storeProduct1, storeProduct2);

	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test that the <code>getProductAssociationsByType</code> returns the correct <code>ProductAssociation</code>s when the number of
	 * <code>ProductAssociation</code>s found is less than the maximum number specified.
	 */
	@Test
	public void testGetProductAssociationsByTypeLessThanMax() {
		final List<ProductAssociation> expectedProductAssociations = Arrays.asList(
				expectedProductAssociation1,
				expectedProductAssociation2,
				expectedProductAssociation3,
				expectedProductAssociation4);
		
		final List<ProductAssociation> productAssociations = productAssociationService.getProductAssociationsByType(storeProducts,
				ProductAssociation.CROSS_SELL, 10, Collections.<Product>emptySet());
		assertEquals("Didn't get expected product associations", expectedProductAssociations, productAssociations);
	}
	
	/**
	 * Test that the <code>getProductAssociationsByType</code> returns the correct <code>ProductAssociation</code>s when the number of
	 * <code>ProductAssociation</code>s found is greater than the maximum number specified.
	 */
	@Test
	public void testGetProductAssociationsByTypeMoreThanMax() {
		final List<ProductAssociation> expectedProductAssociations = Arrays.asList(
				expectedProductAssociation1,
				expectedProductAssociation2,
				expectedProductAssociation3);
		
		final List<ProductAssociation> productAssociations = productAssociationService.getProductAssociationsByType(storeProducts,
				ProductAssociation.CROSS_SELL, 3, Collections.<Product>emptySet());
		assertEquals("Didn't get expected product associations", expectedProductAssociations, productAssociations);
	}
	
	/**
	 * Test that the <code>getProductAssociationsByType</code> returns the correct <code>ProductAssociation</code>s when a set of
	 * <code>Product</code>s to exclude are specified.
	 */
	@Test
	public void testGetProductAssociationsByTypeFiltersExcludedProducts() {
		final List<ProductAssociation> expectedProductAssociations = Arrays.asList(
				expectedProductAssociation1,
				expectedProductAssociation2,
				expectedProductAssociation3);

		context.checking(new Expectations() {
			{
				allowing(storeProduct1).getAssociationsByType(ProductAssociation.CROSS_SELL, setOf((Product) targetProduct4));
				will(returnValue(setOf(expectedProductAssociation1)));

				allowing(storeProduct2).getAssociationsByType(ProductAssociation.CROSS_SELL, setOf((Product) targetProduct4));
				will(returnValue(setOf(expectedProductAssociation2, expectedProductAssociation3)));
			}
		});

		final List<ProductAssociation> productAssociations = productAssociationService.getProductAssociationsByType(storeProducts,
				ProductAssociation.CROSS_SELL, 10, setOf(targetProduct4));
		assertEquals("Didn't get expected product associations", expectedProductAssociations, productAssociations);
	}
	
	private void allowingSalesCount(final StoreProduct targetProduct, final int salesCount) {
		context.checking(new Expectations() {
			{
				allowing(targetProduct).getSalesCount();
				will(returnValue(salesCount));
			}
		});
	}

	private static <T> Set<T> setOf(final T... items) {
		return new HashSet<T>(Arrays.asList(items));
	}

	private ProductAssociation mockProductAssociation(final String name, final StoreProduct targetProduct) {
		final ProductAssociation productAssociation = context.mock(ProductAssociation.class, name);
		context.checking(new Expectations() {
			{
				allowing(productAssociation).getTargetProduct();
				will(returnValue(targetProduct));

				allowing(productAssociation).isValidProductAssociation();
				will(returnValue(true));
			}
		});
		return productAssociation;
	}

	private StoreProduct mockStoreProduct(final String name) {
		return mockStoreProduct(name, Collections.<ProductAssociation>emptySet());
	}
	
	private StoreProduct mockStoreProduct(final String name, final Set<ProductAssociation> productAssociations) {
		final StoreProduct storeProduct = context.mock(StoreProduct.class, name);
		context.checking(new Expectations() {
			{
				allowing(storeProduct).getAssociationsByType(ProductAssociation.CROSS_SELL, Collections.<Product>emptySet());
				will(returnValue(productAssociations));
			}
		});
		return storeProduct;
	}

}
