/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;

/** Test cases for <code>PromotionRuleExceptions</code>. * */
public class PromotionRuleExceptionsImplTest  {

	private PromotionRuleExceptionsImpl promotionRuleExceptions;

	private static final String EXCEPTION_STR_1 = "CategoryCodes:7,ProductCodes:ProductSkuCodes:Sku,";

	private static final String EXCEPTION_STR_2 = "CategoryCodes:8,ProductCodes:ProductSkuCodes:NotSku,";

	private static final String EXCEPTION_STR_3 = "CategoryCodes:1,20,ProductCodes:3,4,ProductSkuCodes:A001,B002,";

	private static final String CATEGORY_CODE1 = "1";

	private static final String CATEGORY_CODE2 = "20";

	private static final String CATEGORY_CODE3 = "3";

	private static final String PRODUCT_CODE1 = "5";

	private static final String SKU_CODE1 = "A001";

	private static final String SKU_CODE2 = "B002";

	private static final String SKU_CODE3 = "C003";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Prepare for the tests.
	 */
	@Before
	public void setUp() {
		promotionRuleExceptions = new PromotionRuleExceptionsImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.PromotionRuleExceptionsImpl.isSkuExcluded(ProductSku)'.
	 */
	@Test
	public void testIsSkuExcluded1() {
		promotionRuleExceptions.populateFromExceptionStr(EXCEPTION_STR_1);

		ProductSku sku = new ProductSkuImpl();
		final Product mockProduct = context.mock(Product.class);
		context.checking(new Expectations() { {
			oneOf(mockProduct).isInCategory(with(any(String.class))); will(returnValue(true));
			oneOf(mockProduct).getProductSkus(); will(returnValue(Collections.unmodifiableMap(Collections.emptyMap())));
			oneOf(mockProduct).addOrUpdateSku(with(any(ProductSku.class)));
		} });
		sku.setProduct(mockProduct);

		assertTrue(promotionRuleExceptions.isSkuExcluded(sku));
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.PromotionRuleExceptionsImpl.isSkuExcluded(ProductSku)'.
	 */
	@Test
	public void testIsSkuExcluded2() {
		promotionRuleExceptions.populateFromExceptionStr(EXCEPTION_STR_2);

		ProductSku sku = new ProductSkuImpl();
		final Product mockProduct = context.mock(Product.class);
		context.checking(new Expectations() { {
			oneOf(mockProduct).isInCategory(with(any(String.class))); will(returnValue(false));
			oneOf(mockProduct).getProductSkus(); will(returnValue(Collections.unmodifiableMap(Collections.emptyMap())));
			oneOf(mockProduct).addOrUpdateSku(with(any(ProductSku.class)));
		} });
		sku.setProduct(mockProduct);

		assertFalse(promotionRuleExceptions.isSkuExcluded(sku));
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.PromotionRuleExceptionsImpl.populateFromExceptionStr(exceptionStr)'.
	 */
	@Test
	public void testPopulateFromExceptionStr() {
		promotionRuleExceptions.populateFromExceptionStr(EXCEPTION_STR_3);

		Category category = new CategoryImpl();
		category.setCode(CATEGORY_CODE1);
		assertTrue(promotionRuleExceptions.isCategoryExcluded(category));
		category.setCode(CATEGORY_CODE2);
		assertTrue(promotionRuleExceptions.isCategoryExcluded(category));
		category.setCode(CATEGORY_CODE3);
		assertFalse(promotionRuleExceptions.isCategoryExcluded(category));

		final Product mockProduct = context.mock(Product.class);
		context.checking(new Expectations() { {
			atLeast(1).of(mockProduct).isInCategory(with(any(String.class))); will(returnValue(false));
			atLeast(1).of(mockProduct).getCode(); will(returnValue(PRODUCT_CODE1));

		} });
		assertFalse(promotionRuleExceptions.isProductExcluded(mockProduct));

		final ProductSku productSku = new ProductSkuImpl();
		context.checking(new Expectations() { {
			allowing(mockProduct).getProductSkus();
			will(returnValue(Collections.unmodifiableMap(Collections.emptyMap())));
		
			allowing(mockProduct).addOrUpdateSku(with(productSku));
		} });
		
		productSku.setProduct(mockProduct);
		productSku.setSkuCode(SKU_CODE1);
		assertTrue(promotionRuleExceptions.isSkuExcluded(productSku));
		productSku.setSkuCode(SKU_CODE2);
		assertTrue(promotionRuleExceptions.isSkuExcluded(productSku));
		productSku.setSkuCode(SKU_CODE3);
		assertFalse(promotionRuleExceptions.isSkuExcluded(productSku));
	}

}
