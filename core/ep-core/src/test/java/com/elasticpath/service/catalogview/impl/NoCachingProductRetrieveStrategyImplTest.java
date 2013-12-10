/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.query.QueryCriteria;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.QueryService;

/**
 * Test that the product retrieval strategy with no caching behaves as expected.
 */
public class NoCachingProductRetrieveStrategyImplTest {

	private NoCachingProductRetrieveStrategyImpl strategy;
	private ProductService productService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@SuppressWarnings("unchecked")
	private final QueryService<Product> productQueryService = context.mock(QueryService.class);

	private ProductLoadTuner loadTuner;
	
	/**
	 * Set up required for each test.
	 * 
	 * @throws Exception in case of errors
	 */
	@Before
	public void setUp() throws Exception {
		productService = context.mock(ProductService.class);
		loadTuner = context.mock(ProductLoadTuner.class);
		
		strategy = new NoCachingProductRetrieveStrategyImpl();
		strategy.setProductService(productService);
		strategy.setProductQueryService(productQueryService);
	}

	/**
	 * Test that retrieveProduct() simply defers to the product service. 
	 */
	@Test
	public void testRetrieveProduct() {
		final long productUid = 123L;
		final Product expectedProduct = context.mock(Product.class);
		context.checking(new Expectations() {
			{
				oneOf(productService).getTuned(productUid, loadTuner); will(returnValue(expectedProduct));
			}
		});
		Product result = strategy.retrieveProduct(productUid, loadTuner);
		assertSame("The strategy should return the expected product", expectedProduct, result);
	}

	/**
	 * Test that retrieving a list of products will get them from the product service and then run them
	 * through the product service to wrap them as products.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testRetrieveProducts() {
		final List<Long> productUids = new ArrayList<Long>();
		final long product1Uid = 123L;
		final long product2Uid = 345L;
		productUids.add(product1Uid);
		productUids.add(product2Uid);

		final Product product1 = context.mock(Product.class, "product1");
		final Product product2 = context.mock(Product.class, "product2");
		final List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);

		final QueryResult<Product> queryResult = context.mock(QueryResult.class);

		context.checking(new Expectations() {
			{
				oneOf(productQueryService).query(with(any(QueryCriteria.class))); will(returnValue(queryResult));
				oneOf(queryResult).getResults(); will(returnValue(products));
			}
		});

		List<Product> result = strategy.retrieveProducts(productUids, loadTuner);
		assertEquals("The products returned should be the ones expected", products, result);
	}
}
