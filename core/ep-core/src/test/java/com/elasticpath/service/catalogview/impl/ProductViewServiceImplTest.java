package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalogview.StoreProductService;

/**
 * ProductViewServiceImpl unit tests.
 */
public class ProductViewServiceImplTest {
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final ProductService productService = context.mock(ProductService.class);
	private final StoreProductService storeProductService = context.mock(StoreProductService.class);
	private final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
	private final StoreProductLoadTuner storeProductLoadTuner = context.mock(StoreProductLoadTuner.class);
	private final Store store = context.mock(Store.class);
	
	private static final String TEST_PRODUCT_CODE = "test-product-code";
	
	private ProductViewServiceImpl service;
	

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		service = new ProductViewServiceImpl();
		service.setStoreProductService(storeProductService);
		service.setProductService(productService);
		context.checking(new Expectations() { {
			allowing(shoppingCart).getStore(); will(returnValue(store));
		} });
	}
	
	/**
	 * ProductService returning a product UIDPK of zero, returns null product.
	 */
	@Test
	public void testGetProductZeroProductUid() {
		context.checking(new Expectations() { {
			oneOf(productService).findUidById(TEST_PRODUCT_CODE); will(returnValue(0L));
		} });
		assertNull(service.getProduct(TEST_PRODUCT_CODE, storeProductLoadTuner, shoppingCart));
	}
	
	/**
	 * StoreProductService returning a NULL product, returns null product.
	 */
	@Test
	public void testGetProductNullProduct() {
		context.checking(new Expectations() { {
			oneOf(productService).findUidById(TEST_PRODUCT_CODE); will(returnValue(1L));
			oneOf(storeProductService).getProductForStore(1L, shoppingCart.getStore(), storeProductLoadTuner); will(returnValue(null));
		} });
		assertNull(service.getProduct(TEST_PRODUCT_CODE, storeProductLoadTuner, shoppingCart));
	}
}