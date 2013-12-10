package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.ProductBundleDao;

/**
 * Tests for the ProductBundleServiceImpl class.
 */
public class ProductBundleServiceImplTest {
	private static final long BUNDLE_1_ID = 1L;
	private static final long BUNDLE_2_ID = 2L;
	private static final long BUNDLE_3_ID = 3L;
	private static final String BUNDLE_1_CODE = "bundle1";
	private static final String BUNDLE_2_CODE = "bundle2";
	private static final String BUNDLE_3_CODE = "bundle3";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final ProductBundleDao dao = context.mock(ProductBundleDao.class);
	private final PersistenceEngine persistenceEngine = context.mock(PersistenceEngine.class);
	private ProductBundleServiceImpl service;
	
	/**
	 * Setup the test.
	 */
	@Before
	public void setUp() {
		service = new ProductBundleServiceImpl();
		service.setProductBundleDao(dao);
		service.setPersistenceEngine(persistenceEngine);
	}

	/**
	 * Test find by guid with fetch group load tuner with product bundle guid.
	 */
	@Test
	public void testFindByGuidWithFetchGroupLoadTunerWithProductBundleGuid() {
		final String productBundleGuid = "productbundle_guid";
		final ProductBundleImpl productBundle = new ProductBundleImpl();
		
		context.checking(new Expectations() { {
			oneOf(dao).findByGuid(productBundleGuid, null);
			will(returnValue(productBundle));
		} });
		
		final ProductBundle returnedObject = service.findByGuidWithFetchGroupLoadTuner(productBundleGuid, null);
		assertEquals("Product bundle doesn't match: ", productBundle, returnedObject);
	}

	/**
	 * Test find by guid with fetch group load tuner with product guid.
	 * Should return null.
	 */
	@Test
	public void testFindByGuidWithFetchGroupLoadTunerWithProductGuid() {
		final String productGuid = "product_guid";

		context.checking(new Expectations() { {
				oneOf(dao).findByGuid(productGuid, null); will(returnValue(new ProductImpl()));
		} });
		
		final ProductBundle productBundle = service.findByGuidWithFetchGroupLoadTuner(productGuid, null);
		assertNull("Return value should be null since a product guid was passed in", productBundle);
	}

	@Test
	public void testFindProductBundleUidsContainingProduct() {
		final ProductSku sku1 = new ProductSkuImpl();
		sku1.setSkuCode("sku1");
		final ProductSku sku2 = new ProductSkuImpl();
		sku2.setSkuCode("sku2");

		final Product product = new ProductImpl();
		product.setCode("foo");
		product.addOrUpdateSku(sku1);
		product.addOrUpdateSku(sku2);

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(new Object[] {product.getCode()}));
				will(returnValue(Collections.singletonList(new Object[] {BUNDLE_1_ID, BUNDLE_1_CODE})));
			oneOf(persistenceEngine).retrieveByNamedQueryWithList(
					with(any(String.class)), with("list"), with(Arrays.asList(sku1.getSkuCode(), sku2.getSkuCode())),
					with(new Object[] {}));
				will(returnValue(Collections.singletonList(new Object[] {BUNDLE_2_ID, BUNDLE_2_CODE})));
		} });

		Set<Long> uidPks = service.findProductBundleUidsContainingProduct(product);
		assertEquals("Return value should return both sku and product collections",
				new HashSet<Long>(Arrays.asList(BUNDLE_1_ID, BUNDLE_2_ID)), uidPks);
	}


	@Test
	public void testFindAllProductBundleUidsContainingProduct() {
		final ProductSku sku1 = new ProductSkuImpl();
		sku1.setSkuCode("sku1");
		final ProductSku sku2 = new ProductSkuImpl();
		sku2.setSkuCode("sku2");

		final Product product = new ProductImpl();
		product.setCode("foo");
		product.addOrUpdateSku(sku1);
		product.addOrUpdateSku(sku2);

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(new Object[] {product.getCode()}));
			will(returnValue(Collections.singletonList(new Object[] {BUNDLE_1_ID, BUNDLE_1_CODE})));
			oneOf(persistenceEngine).retrieveByNamedQueryWithList(
					with(any(String.class)), with("list"), with(Arrays.asList(sku1.getSkuCode(), sku2.getSkuCode())),
					with(new Object[]{}));
			will(returnValue(Collections.singletonList(new Object[] {BUNDLE_2_ID, BUNDLE_2_CODE})));
			oneOf(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(new Object[]{BUNDLE_1_CODE}));
			will(returnValue(Collections.singletonList(new Object[] {BUNDLE_3_ID, BUNDLE_3_CODE})));
			oneOf(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(new Object[]{BUNDLE_2_CODE}));
			will(returnValue(Collections.emptyList()));
			oneOf(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(new Object[]{BUNDLE_3_CODE}));
			will(returnValue(Collections.emptyList()));
		} });

		Set<Long> uidPks = service.findAllProductBundleUidsContainingProduct(product);
		assertEquals("Return value should return both sku and product collections, plus nested bundle collections",
				new HashSet<Long>(Arrays.asList(BUNDLE_1_ID, BUNDLE_2_ID, BUNDLE_3_ID)), uidPks);
	}


	@Test
	public void testFindAllProductBundleUidsContainingProductHandlesBundleCycles() {
		final ProductSku sku1 = new ProductSkuImpl();
		sku1.setSkuCode("sku1");
		final ProductSku sku2 = new ProductSkuImpl();
		sku2.setSkuCode("sku2");

		final Product product = new ProductImpl();
		product.setCode("foo");
		product.addOrUpdateSku(sku1);
		product.addOrUpdateSku(sku2);

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(new Object[] {product.getCode()}));
			will(returnValue(Collections.singletonList(new Object[] {BUNDLE_1_ID, BUNDLE_1_CODE})));
			oneOf(persistenceEngine).retrieveByNamedQueryWithList(
					with(any(String.class)), with("list"), with(Arrays.asList(sku1.getSkuCode(), sku2.getSkuCode())),
					with(new Object[] {}));
			will(returnValue(Collections.singletonList(new Object[] {BUNDLE_2_ID, BUNDLE_2_CODE})));
			oneOf(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(new Object[] {BUNDLE_1_CODE}));
			will(returnValue(Collections.singletonList(new Object[] {BUNDLE_3_ID, BUNDLE_3_CODE})));
			oneOf(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(new Object[] {BUNDLE_2_CODE}));
			will(returnValue(Collections.emptyList()));
			oneOf(persistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(new Object[] {BUNDLE_3_CODE}));
			will(returnValue(Collections.singletonList(new Object[]{BUNDLE_1_ID, BUNDLE_1_CODE})));
		} });

		Set<Long> uidPks = service.findAllProductBundleUidsContainingProduct(product);
		assertEquals("Return value should return both sku and product collections, plus nested bundle collections",
				new HashSet<Long>(Arrays.asList(BUNDLE_1_ID, BUNDLE_2_ID, BUNDLE_3_ID)), uidPks);
	}
}
