/*
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.domain.catalogview.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;

/** Test for StoreProduct. */
public class StoreProductTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private StoreProductImpl storeProduct;
	private static final String DEFAULT_SKU_CODE = "defaultSkuCode";
	private static final String OTHER_SKU_CODE = "otherSkuCode";
	private final Product product = context.mock(Product.class);
	private final ProductSku defaultProductSku = new ProductSkuImpl();
	private final ProductSku otherProductSku = new ProductSkuImpl();
	private final Map<String, ProductSku> skuMap = new LinkedHashMap<String, ProductSku>();

    /** .*/
    @Before
    public void setUp() {
    	defaultProductSku.setSkuCode(DEFAULT_SKU_CODE);
    	otherProductSku.setSkuCode(OTHER_SKU_CODE);

    	context.checking(new Expectations() { {
			oneOf(product).getProductSkus(); will(returnValue(skuMap));
			oneOf(product).setDefaultSku(null);
		} });
    	storeProduct = new StoreProductImpl(product);
    	storeProduct.setDefaultSku(null);
    }

    /** The default SKU of the product has inventory and is within date range. */
    @Test
    public void testDefaultSkuIsAValidSku() {
    	storeProduct.setSkuAvailability(DEFAULT_SKU_CODE, true);
    	skuMap.put(DEFAULT_SKU_CODE, defaultProductSku);

        Assert.assertTrue("Returns the code of the default sku",
        		storeProduct.getDefaultSku().getSkuCode() == DEFAULT_SKU_CODE);
    }

    /** The default SKU of the product doesn't have inventory or isn't within date range. but the product is single-sku.*/
    @Test
    public void testDefaultSkuIsNotValidSkuButProductIsSingleSku() {
    	storeProduct.setSkuAvailability(DEFAULT_SKU_CODE, false);
    	skuMap.put(DEFAULT_SKU_CODE, defaultProductSku);

    	context.checking(new Expectations() { {
			oneOf(product).getDefaultSku(); will(returnValue(defaultProductSku));
		} });

    	Assert.assertTrue("Returns the code of the default sku",
        		storeProduct.getDefaultSku().getSkuCode() == DEFAULT_SKU_CODE);
    }

    /** The default SKU of the product doesn't have inventory or isn't within date range, but the product is multi-sku.    */
    @Test
    public void testDefaultSkuIsNotValidSkuButProductIsMultiSku() {
    	storeProduct.setSkuAvailability(DEFAULT_SKU_CODE, false);
    	storeProduct.setSkuAvailability(OTHER_SKU_CODE, true);
    	skuMap.put(DEFAULT_SKU_CODE, defaultProductSku);
    	skuMap.put(OTHER_SKU_CODE, otherProductSku);

        Assert.assertTrue("Returns the code of the another sku",
        		storeProduct.getDefaultSku().getSkuCode() == OTHER_SKU_CODE);
    }


    /** Product is multi-sku, but none of it's sku's are valid.*/
    @Test
    public void testNoneOfProductSkusAreValid() {
    	storeProduct.setSkuAvailability(DEFAULT_SKU_CODE, false);
    	storeProduct.setSkuAvailability(OTHER_SKU_CODE, false);
    	skuMap.put(DEFAULT_SKU_CODE, defaultProductSku);
    	skuMap.put(OTHER_SKU_CODE, otherProductSku);

    	context.checking(new Expectations() { {
    		oneOf(product).getDefaultSku(); will(returnValue(defaultProductSku));
		} });

        Assert.assertTrue("Returns the code of the default sku",
        		storeProduct.getDefaultSku().getSkuCode() == DEFAULT_SKU_CODE);
    }
}
