/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductLoadTunerImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.query.QueryCriteria;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.impl.QueryResultImpl;

/**
 * Test that EhCache is used as expected to cache products.
 */
public class EhCacheProductRetrieveStrategyImplTest {

	private static final String PRODUCT1 = "Product1";
	
	private static final String PRODUCT2 = "Product2";

	private static final String THE_SECOND_PRODUCT_SHOULD_BE_IN_THE_POPULATED_PRODUCTS = "The second product should be in the populated products";

	private static final String THE_FIRST_PRODUCT_SHOULD_BE_IN_THE_POPULATED_PRODUCTS = "The first product should be in the populated products";

	private static final String THERE_SHOULD_BE_TWO_PRODUCTS_IN_THE_LIST = "There should be two products in the list";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private EhCacheProductRetrieveStrategyImpl strategy;
	private ProductService productService;
	private QueryService<Product> productQueryService; 
	private Ehcache cache;
	private Ehcache productBundleCache;
	private Ehcache cacheForFetchGroupLoadTuner;
	private final ProductLoadTuner loadTuner = new ProductLoadTunerImpl();
	
	/**
	 * Set up required by each test.
	 * 
	 * @throws java.lang.Exception in case of errors during setup
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		productService = context.mock(ProductService.class);
		productQueryService = context.mock(QueryService.class);
		cache = context.mock(Ehcache.class, "productCache");
		productBundleCache = context.mock(Ehcache.class, "productBundleCache");
		cacheForFetchGroupLoadTuner = context.mock(Ehcache.class, "cacheForFetchGroupLoadTuner");

		strategy = new EhCacheProductRetrieveStrategyImpl();
		strategy.setProductService(productService);
		strategy.setProductQueryService(productQueryService);
		strategy.setCache(cache);
		strategy.setProductBundleCache(productBundleCache);
		strategy.setProductWithFetchGroupLoadTunerCache(cacheForFetchGroupLoadTuner);
	}

	/**
	 * Test that a request to cache a product puts they given product in the cache,
	 * keyed by product uid and code. 
	 */
	@Test
	public void testCacheProduct() {
		final Product product = context.mock(Product.class);
		final long productUid = 123L;
		final Pair<Product, ProductLoadTuner> productWithTuner = new Pair<Product, ProductLoadTuner>(product, loadTuner);
		final Element element = new Element(productUid, productWithTuner);
		
		context.checking(new Expectations() {
			{
				oneOf(product).getUidPk(); will(returnValue(productUid));
				oneOf(cache).put(element);
			}
		});
		strategy.cacheProduct(product, loadTuner);
	}

	/**
	 * Test that a request to cache a collection of products puts each one in the cache
	 * keyed by product uid and store code.
	 */
	@Test
	public void testCacheProducts() {
		final long product1Uid = 123L;
		final long product2Uid = 345L;

		final List<Product> products = new ArrayList<Product>();
		final Product product1 = context.mock(Product.class, PRODUCT1);
		final Product product2 = context.mock(Product.class, PRODUCT2);
		products.add(product1);
		products.add(product2);
		
		final Pair<Product, ProductLoadTuner> product1WithTuner = new Pair<Product, ProductLoadTuner>(product1, loadTuner);
		final Pair<Product, ProductLoadTuner> product2WithTuner = new Pair<Product, ProductLoadTuner>(product2, loadTuner);
		final Element element1 = new Element(product1Uid, product1WithTuner);
		final Element element2 = new Element(product2Uid, product2WithTuner);
		
		context.checking(new Expectations() {
			{
				oneOf(product1).getUidPk(); will(returnValue(product1Uid));
				oneOf(product2).getUidPk(); will(returnValue(product2Uid));
				oneOf(cache).put(element1);
				oneOf(cache).put(element2);
			}
		});
		
		strategy.cacheProducts(products, loadTuner);
	}
	
	/**
	 * Test that a request to cache a collection of product bundles puts each one in the cache
	 * keyed by product uid and store code.
	 */
	@Test
	public void testProductBundleCacheProducts() {
		final long product1Uid = 123L;
		final long product2Uid = 345L;

		final List<Product> products = new ArrayList<Product>();
		final ProductBundle product1 = context.mock(ProductBundle.class, PRODUCT1);
		final ProductBundle product2 = context.mock(ProductBundle.class, PRODUCT2);
		products.add(product1);
		products.add(product2);
		
		final Pair<Product, ProductLoadTuner> product1WithTuner = new Pair<Product, ProductLoadTuner>(product1, loadTuner);
		final Pair<Product, ProductLoadTuner> product2WithTuner = new Pair<Product, ProductLoadTuner>(product2, loadTuner);
		final Element element1 = new Element(product1Uid, product1WithTuner);
		final Element element2 = new Element(product2Uid, product2WithTuner);
		
		context.checking(new Expectations() {
			{
				oneOf(product1).getUidPk(); will(returnValue(product1Uid));
				oneOf(product2).getUidPk(); will(returnValue(product2Uid));
				oneOf(productBundleCache).put(element1);
				oneOf(productBundleCache).put(element2);
			}
		});
		
		strategy.cacheProducts(products, loadTuner);
	}

	/**
	 * Test that a valid cache call will return a product.
	 */
	@Test
	public void testFetchFromCacheIfValid() {
		final Product product = context.mock(Product.class);
		final long productUid = 123L;
		final Pair<Product, ProductLoadTuner> productWithTuner = new Pair<Product, ProductLoadTuner>(product, loadTuner);
		final Element element = new Element(productUid, productWithTuner);
		
		context.checking(new Expectations() {
			{
				oneOf(cache).get(productUid); will(returnValue(element));
			}
		});
		Pair<Product, LoadTuner> result = strategy.fetchFromCacheIfValid(productUid, loadTuner);
		assertEquals("The fetch should have returned the cached product", product, result.getFirst());
	}

	/**
	 * Test that a fetch for a product not in the cache will return null.
	 */
	@Test
	public void testFetchFromCacheNoEntry() {
		final long productUid = 123L;
		
		context.checking(new Expectations() {
			{
				oneOf(cache).get(productUid); will(returnValue(null));
				oneOf(productBundleCache).get(productUid); will(returnValue(null));
			}
		});
		Pair<Product, LoadTuner> result = strategy.fetchFromCacheIfValid(productUid, loadTuner);
		assertNull("The fetch should have returned null", result);
	}
	
	/**
	 * Test that a fetch of an expired cache item will return null.
	 */
	@SuppressWarnings("serial")
	@Test
	public void testFetchFromCacheExpired() {
		final long productUid = 123L;
		final Product product = context.mock(Product.class);
		final Pair<Product, ProductLoadTuner> productWithTuner = new Pair<Product, ProductLoadTuner>(product, loadTuner);
		final Element element = new Element(productUid, productWithTuner) {
			@Override
			public boolean isExpired() {
				return true;
			};
		};
		context.checking(new Expectations() {
			{
				oneOf(cache).get(productUid); will(returnValue(element));
			}
		});
		assertTrue("Element should have expired", element.isExpired());
		Pair<Product, LoadTuner> result = strategy.fetchFromCacheIfValid(productUid, loadTuner);
		assertNull("The fetch should have returned null", result);
	}

	/**
	 * Test that invalidate removes all entries form the cache.
	 */
	@Test
	public void testInvalidate() {
		context.checking(new Expectations() {
			{
				oneOf(cache).removeAll();
				oneOf(productBundleCache).removeAll();
				oneOf(cacheForFetchGroupLoadTuner).removeAll();
			}
		});
		strategy.invalidate();
	}

	/**
	 * Test that the service is never hit for a cached product. 
	 */
	@Test
	public void testRetrieveCachedProduct() {
		final long productUid = 123L;
		final Product product = context.mock(Product.class);
		final Pair<Product, ProductLoadTuner> productWithTuner = new Pair<Product, ProductLoadTuner>(product, loadTuner);
		final Element element = new Element(productUid, productWithTuner);
		
		context.checking(new Expectations() {
			{
				oneOf(cache).get(productUid); will(returnValue(element));
			}
		});
		Product result = strategy.retrieveProduct(productUid, loadTuner);
		assertEquals("The returned product is the one from the cache", product, result);
	}

	/**
	 * Test that the service is never hit when retrieving cached products.
	 */
	@Test
	public void testRetrieveCachedProducts() {
		final List<Long> productUids = new ArrayList<Long>();
		final long product1Uid = 123L;
		final long product2Uid = 345L;
		productUids.add(product1Uid);
		productUids.add(product2Uid);

		final Product product1 = context.mock(Product.class, PRODUCT1);
		final Product product2 = context.mock(Product.class, PRODUCT2);
		final Pair<Product, ProductLoadTuner> product1WithTuner = new Pair<Product, ProductLoadTuner>(product1, loadTuner);
		final Pair<Product, ProductLoadTuner> product2WithTuner = new Pair<Product, ProductLoadTuner>(product2, loadTuner);
		final Element element1 = new Element(product1Uid, product1WithTuner);
		final Element element2 = new Element(product2Uid, product2WithTuner);

		context.checking(new Expectations() {
			{
				oneOf(cache).get(product1Uid); will(returnValue(element1));
				oneOf(cache).get(product2Uid); will(returnValue(element2));
				
				allowing(product1).getUidPk(); will(returnValue(product1Uid));
				allowing(product2).getUidPk(); will(returnValue(product2Uid));
			}
		});

		List<Product> results = strategy.retrieveProducts(productUids, loadTuner);
		assertEquals(THERE_SHOULD_BE_TWO_PRODUCTS_IN_THE_LIST, 2, results.size());
		assertTrue(THE_FIRST_PRODUCT_SHOULD_BE_IN_THE_POPULATED_PRODUCTS, results.contains(product1));
		assertTrue(THE_SECOND_PRODUCT_SHOULD_BE_IN_THE_POPULATED_PRODUCTS, results.contains(product2));
	}

	
	/**
	 * Test product duplication in case if there are cached and uncached products.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testRetrieveCachedProductsWithCachedAndUncachedProds() {
		
		final List<Long> productUids = new ArrayList<Long>();
		final List<Long> firstProductUid = new ArrayList<Long>();
		
		final long product1Uid = 123L;
		final long product2Uid = 345L;
		productUids.add(product1Uid);
		productUids.add(product2Uid);
		
		firstProductUid.add(product1Uid);

		final List<Product> products = new ArrayList<Product>();
		final List<Product> firstProduct = new ArrayList<Product>();
		
		final Product product1 = new ProductImpl();
		product1.setCode("Product_1");
		product1.setUidPk(product1Uid);
		final Product product2 = new ProductImpl();
		product2.setCode("Product_2");
		product2.setUidPk(product2Uid);
		
		products.add(product1);
		products.add(product2);
		
		firstProduct.add(product1);
		
		final Pair<Product, ProductLoadTuner> product2WithTuner = new Pair<Product, ProductLoadTuner>(product2, loadTuner);
		final Element element2 = new Element(product2Uid, product2WithTuner);

		final QueryResultImpl<Product> queryResult = new QueryResultImpl<Product>();
		queryResult.setResults(firstProduct);
		context.checking(new Expectations() {
			{
				oneOf(cache).get(product1Uid); will(returnValue(null));
				oneOf(productBundleCache).get(product1Uid); will(returnValue(null));
				oneOf(cache).get(product2Uid); will(returnValue(element2));
				
				allowing(cache).put(with(any(Element.class)));

				allowing(productQueryService).query(with(any(QueryCriteria.class)));
				will(returnValue(queryResult));
			}
		});

		List<Product> results = strategy.retrieveProducts(productUids, loadTuner);
		assertEquals(THERE_SHOULD_BE_TWO_PRODUCTS_IN_THE_LIST, 2, results.size());
		assertTrue(THE_FIRST_PRODUCT_SHOULD_BE_IN_THE_POPULATED_PRODUCTS, results.contains(product1));
		assertTrue(THE_SECOND_PRODUCT_SHOULD_BE_IN_THE_POPULATED_PRODUCTS, results.contains(product2));
	}
	
	
	/**
	 * Test that retrieving a product with a more inclusive load tuner than the one cached
	 * will force a reload from the DB and the newly (more inclusive) loaded product to
	 * be put into the cache.
	 */
	@Test
	public void testFetchFromCacheWithMoreInclusiveTuner() {
		final long productUid = 123L;
		final Product product = context.mock(Product.class);
		final Pair<Product, ProductLoadTuner> productWithTuner = new Pair<Product, ProductLoadTuner>(product, loadTuner);
		final Element element = new Element(productUid, productWithTuner);
		
		final ProductLoadTuner mergedTuner = context.mock(ProductLoadTuner.class, "mergedTuner");
		final ProductLoadTuner moreInclusiveLoadTuner = new ProductLoadTunerImpl() {
			private static final long serialVersionUID = 2515193624744791421L;

			@Override
			public ProductLoadTuner merge(final ProductLoadTuner productLoadTuner) {
				return mergedTuner;
			}
		};
		moreInclusiveLoadTuner.setLoadingAttributeValue(true);
		final Pair<Product, ProductLoadTuner> productWithNewTuner = new Pair<Product, ProductLoadTuner>(product, mergedTuner);
		final Element updatedElement = new Element(productUid, productWithNewTuner);
		
		context.checking(new Expectations() {
			{
				oneOf(cache).get(productUid); will(returnValue(element));
				oneOf(productService).getTuned(productUid, mergedTuner); will(returnValue(product));
				oneOf(product).getUidPk(); will(returnValue(productUid));
				oneOf(cache).put(updatedElement);
			}
		});
		Product result = strategy.retrieveProduct(productUid, moreInclusiveLoadTuner);
		assertEquals("The product should have been retrieved", product, result);
	}
	
	/**
	 * Test get the correct cache.
	 */
	@Test
	public void testGetCorrectCache() {
		Ehcache resolvedCache = strategy.getCacheByLoadTuner(loadTuner);
		assertEquals("Get wrong cache", cache, resolvedCache);
		
		FetchGroupLoadTuner fetchGroupLoadTuner = new FetchGroupLoadTunerImpl();
		resolvedCache = strategy.getCacheByLoadTuner(fetchGroupLoadTuner);
		assertEquals("Get wrong cache", cacheForFetchGroupLoadTuner, resolvedCache);
	}
}
