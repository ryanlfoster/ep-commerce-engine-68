package com.elasticpath.domain.catalog.impl;

import java.util.Iterator;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.test.BeanFactoryExpectationsFactory;


/**
 * Tests {@link BundleIteratorImpl}.
 */
public class BundleIteratorImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Set up required before each test.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests iterating a bundle which has a single constituent.
	 */
	@Test
	public void testIteratorWithSingleConstituent() {
		// given
		Product product = createProductWithSkuCode("A_PRODUCT_CODE", "A_SKU_CODE");
		ProductBundle bundle = new ProductBundleImpl();
		bundle.addConstituent(createBundleConstituent(product));

		// test
		BundleIteratorImpl bundleIterator = new BundleIteratorImpl(bundle);
		BundleConstituent constituent = bundleIterator.iterator().next();
		Assert.assertEquals(product.getCode(), constituent.getConstituent().getCode());
	}

	/**
	 * Tests iterating a bundle which has two constituents.
	 */
	@Test
	public void testIteratorWithTwoConstituents() {
		// given
		Product product1 = createProductWithSkuCode("A_PRODUCT_CODE", "A_SKU_CODE");
		Product product2 = createProductWithSkuCode("ANOTHER_PRODUCT_CODE", "ANOTHER_SKU_CODE");
		ProductBundle bundle = createBundleWithSkuCode("A_BUNDLE_CODE", "A_BUNDLE_SKU_CODE");

		bundle.addConstituent(createBundleConstituent(product1));
		bundle.addConstituent(createBundleConstituent(product2));

		// test
		BundleIteratorImpl bundleIterator = new BundleIteratorImpl(bundle);
		Iterator<BundleConstituent> iterator = bundleIterator.iterator();
		BundleConstituent constituent1 = iterator.next();
		Assert.assertEquals(product1.getCode(), constituent1.getConstituent().getCode());

		BundleConstituent constituent2 = iterator.next();
		Assert.assertEquals(product2.getCode(), constituent2.getConstituent().getCode());
	}

	/**
	 * Tests iterating a bundle which has a nested bundle.
	 */
	@Test
	public void testIteratorWithNestedBundle() {
		// given
		Product product1 = createProductWithSkuCode("A_PRODUCT", "A_SKU_CODE");
		Product product2 = createProductWithSkuCode("ANOTHER_PRODUCT", "ANOTHER_SKU_CODE");
		ProductBundle bundle = createBundleWithSkuCode("A_BUNDLE_CODE", "A_BUNDLE_SKU_CODE");
		ProductBundle nested = createBundleWithSkuCode("A_NESTED_BUNDLE_CODE", "A_NESTED_BUNDLE_SKU_CODE");

		nested.addConstituent(createBundleConstituent(product2));
		bundle.addConstituent(createBundleConstituent(product1));
		bundle.addConstituent(createBundleConstituent(nested));

		// test
		BundleIteratorImpl bundleIterator = new BundleIteratorImpl(bundle);
		Iterator<BundleConstituent> iterator = bundleIterator.iterator();
		BundleConstituent constituent1 = iterator.next();
		Assert.assertEquals(product1.getCode(), constituent1.getConstituent().getCode());

		BundleConstituent nestedConstituent = iterator.next();
		Assert.assertEquals(nested.getCode(), nestedConstituent.getConstituent().getCode());

		BundleConstituent constituent2 = iterator.next();
		Assert.assertEquals(product2.getCode(), constituent2.getConstituent().getCode());
	}

	private BundleConstituent createBundleConstituent(final Product product) {
		BundleConstituent bundleConstituent = new BundleConstituentImpl();
		bundleConstituent.setConstituent(product);
		bundleConstituent.setQuantity(1);
		return bundleConstituent;
	}

	private Product createProductWithSkuCode(final String productCode, final String skuCode) {
		ProductImpl product = new ProductImpl();
		product.setCode(productCode);

		ProductSku sku = new ProductSkuImpl();
		sku.setSkuCode(skuCode);
		product.addOrUpdateSku(sku);

		return product;
	}

	private ProductBundle createBundleWithSkuCode(final String productCode, final String skuCode) {
		ProductBundle bundle = new ProductBundleImpl();
		bundle.setCode(productCode);

		ProductSku sku = new ProductSkuImpl();
		sku.setSkuCode(skuCode);
		bundle.addOrUpdateSku(sku);

		return bundle;
	}

}
