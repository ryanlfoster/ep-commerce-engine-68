package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductCharacteristics;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductCharacteristicsService;

/**
 * Test that {@link CachingProductCharacteristicsServiceImpl} behaves as expected.
 */
public class CachingProductCharacteristicsServiceImplTest {

	private static final String RESULTS_SHOULD_MATCH_DELEGATE_RESULTS = "The service should return the results returned from the delegate";
	private static final String SKU_CODE = "SKU1";
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ProductCharacteristicsService service;
	private ProductCharacteristicsService delegate;
	private Ehcache cache;
	
	/**
	 * Setup required for each test.
	 */
	@Before
	public void setUp() {
		service = new CachingProductCharacteristicsServiceImpl();
		
		delegate = context.mock(ProductCharacteristicsService.class);
		cache = context.mock(Ehcache.class);
		
		((CachingProductCharacteristicsServiceImpl) service).setDelegateService(delegate);
		((CachingProductCharacteristicsServiceImpl) service).setCache(cache);
	}
	
	/**
	 * Test that get product characteristics by product delegates to the delegate.
	 */
	@Test
	public void testGetProductCharacteristicsProduct() {
		final Product product = context.mock(Product.class);
		final ProductCharacteristics productCharacteristics = context.mock(ProductCharacteristics.class);
		context.checking(new Expectations() {
			{
				oneOf(delegate).getProductCharacteristics(product); will(returnValue(productCharacteristics));
				never(cache);
			}
		});
		ProductCharacteristics result = service.getProductCharacteristics(product);
		assertEquals(RESULTS_SHOULD_MATCH_DELEGATE_RESULTS, productCharacteristics, result);
	}

	/**
	 * Test the behaviour of get product characteristics product sku delegates to the delegate.
	 */
	@Test
	public void testGetProductCharacteristicsProductSku() {
		final ProductSku sku = context.mock(ProductSku.class);
		final ProductCharacteristics productCharacteristics = context.mock(ProductCharacteristics.class);
		context.checking(new Expectations() {
			{
				oneOf(delegate).getProductCharacteristics(sku); will(returnValue(productCharacteristics));
				never(cache);
			}
		});
		ProductCharacteristics result = service.getProductCharacteristics(sku);
		assertEquals(RESULTS_SHOULD_MATCH_DELEGATE_RESULTS, productCharacteristics, result);
	}

	/**
	 * Test that get product characteristics for sku code looks in the cache.
	 */
	@Test
	public void testGetProductCharacteristicsForSkuCodeInCache() {
		final ProductCharacteristics productCharacteristics = context.mock(ProductCharacteristics.class);
		final Element element = new Element(SKU_CODE, productCharacteristics);
		context.checking(new Expectations() {
			{
				never(delegate).getProductCharacteristicsForSkuCode(SKU_CODE);
				oneOf(cache).get(SKU_CODE); will(returnValue(element));
			}
		});
		ProductCharacteristics result = service.getProductCharacteristicsForSkuCode(SKU_CODE);
		assertEquals("The service should return the results returned from the cache", productCharacteristics, result);
	}
	
	/**
	 * Test that get product characteristics for sku code when the cache has expired calls the delegate.
	 */
	@Test
	public void testGetProductCharacteristicsForSkuCodeExpired() {
		final ProductCharacteristics productCharacteristics = context.mock(ProductCharacteristics.class);
		final Element element = new Element(SKU_CODE, productCharacteristics) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isExpired() {
				return true;
			};
		};
		context.checking(new Expectations() {
			{
				oneOf(delegate).getProductCharacteristicsForSkuCode(SKU_CODE); will(returnValue(productCharacteristics));
				oneOf(cache).get(SKU_CODE); will(returnValue(element));
				oneOf(cache).put(element);
			}
		});
		ProductCharacteristics result = service.getProductCharacteristicsForSkuCode(SKU_CODE);
		assertEquals(RESULTS_SHOULD_MATCH_DELEGATE_RESULTS, productCharacteristics, result);
	}

	/**
	 * Test that get product characteristics for sku code when the value is not cached calls the delegate.
	 */
	@Test
	public void testGetProductCharacteristicsForSkuCodeNotCached() {
		final ProductCharacteristics productCharacteristics = context.mock(ProductCharacteristics.class);
		final Element element = new Element(SKU_CODE, productCharacteristics);
		context.checking(new Expectations() {
			{
				oneOf(delegate).getProductCharacteristicsForSkuCode(SKU_CODE); will(returnValue(productCharacteristics));
				oneOf(cache).get(SKU_CODE); will(returnValue(null));
				oneOf(cache).put(element);
			}
		});
		ProductCharacteristics result = service.getProductCharacteristicsForSkuCode(SKU_CODE);
		assertEquals(RESULTS_SHOULD_MATCH_DELEGATE_RESULTS, productCharacteristics, result);
	}

}
