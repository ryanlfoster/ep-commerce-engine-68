package com.elasticpath.domain.shoppingcart.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.catalogview.StoreSeoUrlBuilderFactory;
import com.elasticpath.domain.shoppingcart.ViewHistoryProduct;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/** Test cases for <code>ViewHistoryImpl</code>. */
public class ViewHistoryImplTest {
	
	private static final String VIEW_HISTORY_PRODUCT = "viewHistoryProduct";
	private static final long UIDPK_1 = 1;	
	private static final int TEST_MAX_HISTORY_LENGTH = 5;
	
	private ViewHistoryImpl viewHistory;
	private ViewHistoryProduct viewHistoryProduct;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	private StoreSeoUrlBuilderFactory seoUrlBuilderFactory;
	private SeoUrlBuilder seoUrlBuilder;
	private Product product;
	
	/**
	 * Prepare for the tests.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		seoUrlBuilder = context.mock(SeoUrlBuilder.class);
		viewHistoryProduct = context.mock(ViewHistoryProduct.class);			
		product = context.mock(Product.class, "prodOne");
		seoUrlBuilderFactory = context.mock(StoreSeoUrlBuilderFactory.class);

		viewHistory = new ViewHistoryImpl();
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	
	/**
	 * Test for <code>createHistoryProduct</code>.
	 */
	@Test
	public void testCreateHistoryProduct() {
		
		// Set expectations
		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(ContextIdNames.STORE_SEO_URL_BUILDER_FACTORY);
				will(returnValue(seoUrlBuilderFactory));
				
				oneOf(seoUrlBuilderFactory).getStoreSeoUrlBuilder();
				will(returnValue(seoUrlBuilder));

				oneOf(beanFactory).getBean(VIEW_HISTORY_PRODUCT);
				will(returnValue(viewHistoryProduct));
				
				oneOf(viewHistoryProduct).loadProductInfo(product, seoUrlBuilder);								
			}
		});			
		
		ViewHistoryProduct createdViewHistoryProduct = viewHistory.createHistoryProduct(product);		
		assertNotNull(createdViewHistoryProduct);
		
		//viewHistory.getViewedProducts();		
	}
	
	/**
	 * Test for <code>getLastViewedHistoryProduct</code>.
	 */
	@Test
	public void testGetLastViewedHistoryProductNull() {
		ViewHistoryProduct lastViewedHistoryProduct = viewHistory.getLastViewedHistoryProduct();
		assertNull(lastViewedHistoryProduct);			
	}
	
	/**
	 * Test for <code>getLastViewedHistoryProduct</code>.
	 */
	@Test
	public void testGetLastViewedHistoryProduct() {
		
		// Set expectations
		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(ContextIdNames.STORE_SEO_URL_BUILDER_FACTORY);
				will(returnValue(seoUrlBuilderFactory));

				oneOf(seoUrlBuilderFactory).getStoreSeoUrlBuilder();
				will(returnValue(seoUrlBuilder));

				oneOf(beanFactory).getBean(VIEW_HISTORY_PRODUCT);
				will(returnValue(viewHistoryProduct));
				
				oneOf(viewHistoryProduct).loadProductInfo(product, seoUrlBuilder);								
			}
		});			
				
		viewHistory.addProduct(product);
		ViewHistoryProduct lastViewedHistoryProduct = viewHistory.getLastViewedHistoryProduct();
		assertNotNull(lastViewedHistoryProduct);		
		assertEquals(1, viewHistory.getViewedProducts().size());
	}
	
	/**
	 * Test for <code>addProduct</code>.
	 */
	@Test
	public void testAddOneProduct() {
		
		// Set expectations
		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(ContextIdNames.STORE_SEO_URL_BUILDER_FACTORY);
				will(returnValue(seoUrlBuilderFactory));

				oneOf(seoUrlBuilderFactory).getStoreSeoUrlBuilder();
				will(returnValue(seoUrlBuilder));

				allowing(beanFactory).getBean(VIEW_HISTORY_PRODUCT);
				will(returnValue(viewHistoryProduct));
				
				allowing(viewHistoryProduct).loadProductInfo(product, seoUrlBuilder);								
				
				allowing(viewHistoryProduct).getUidPk();
				will(returnValue(UIDPK_1));
				
				allowing(product).getUidPk();
				will(returnValue(UIDPK_1));
			}
		});			
		
		List<ViewHistoryProduct> viewedProducts = viewHistory.getViewedProducts();		
		assertEquals(0, viewedProducts.size());
		
		viewHistory.addProduct(product);		
		assertEquals(1, viewedProducts.size());
		
	}
	
	/**
	 * Test adding same product does not increase size of product view history.
	 */
	@Test
	public void testAddSameProduct() {
		
		// Set expectations
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.STORE_SEO_URL_BUILDER_FACTORY);
				will(returnValue(seoUrlBuilderFactory));

				allowing(seoUrlBuilderFactory).getStoreSeoUrlBuilder();
				will(returnValue(seoUrlBuilder));

				allowing(beanFactory).getBean(VIEW_HISTORY_PRODUCT);
				will(returnValue(viewHistoryProduct));
				
				allowing(viewHistoryProduct).loadProductInfo(product, seoUrlBuilder);								
				
				allowing(viewHistoryProduct).getUidPk();
				will(returnValue(UIDPK_1));
				
				allowing(product).getUidPk();
				will(returnValue(UIDPK_1));
			}
		});			
		
		List<ViewHistoryProduct> viewedProducts = viewHistory.getViewedProducts();		
		assertEquals(0, viewedProducts.size());
		
		viewHistory.addProduct(product);		
		assertEquals("Add product failed", 1, viewedProducts.size());
		
		viewHistory.addProduct(product);		
		assertEquals("Adding same product should not change history size", 1, viewedProducts.size());
	}
	
	/**
	 * Test to ensure max limit is not exceeded when adding products.
	 */
	@Test
	public void testAddProductMaxLimit() {				
		
		// Set expectations
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.STORE_SEO_URL_BUILDER_FACTORY);
				will(returnValue(seoUrlBuilderFactory));

				allowing(seoUrlBuilderFactory).getStoreSeoUrlBuilder();
				will(returnValue(seoUrlBuilder));

				allowing(beanFactory).getBean(VIEW_HISTORY_PRODUCT);
				will(returnValue(viewHistoryProduct));
				
				allowing(viewHistoryProduct).loadProductInfo(with(any(Product.class)), with(same(seoUrlBuilder)));								
				
				allowing(viewHistoryProduct).getUidPk();

			}
		});			
		
		List<ViewHistoryProduct> viewedProducts = viewHistory.getViewedProducts();		
		assertEquals(0, viewedProducts.size());
		
		final int lotsOfItems = TEST_MAX_HISTORY_LENGTH;
		for (int x = 1; x < lotsOfItems; x++) {
			viewHistory.addProduct(createProduct(x));
			assertEquals("View history size is incorrect", Math.min(x, TEST_MAX_HISTORY_LENGTH), viewedProducts.size());		
		}
	}

	/**
	 * Create product.
	 * @param uidpk
	 * @return product
	 */
	private Product createProduct(final long uidpk) {
		final Product product = context.mock(Product.class, String.valueOf(uidpk));
		context.checking(new Expectations() {
			{
				allowing(product).getUidPk(); will(returnValue(uidpk));
			}
		});
		return product;
	}	
}
