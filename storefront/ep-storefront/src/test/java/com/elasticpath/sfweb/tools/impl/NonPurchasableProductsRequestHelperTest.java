package com.elasticpath.sfweb.tools.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.catalog.ProductSkuService;
/**
 * A test suite for the {@link NonPurchasableProductsRequestHelper}. 
 */
@SuppressWarnings({ "serial", "unchecked" })
public class NonPurchasableProductsRequestHelperTest {

	private static final Locale ENGLISH = Locale.ENGLISH;
	private static final String NON_PURCHASABLE_ITEMS_PRODUCT_NAMES = "nonPurchasableItemsProductNames";
	private static final String NON_PURCHASABLE_ITEMS_WERE_REMOVED = "nonPurchasableItemsWereRemoved";
	private static final String SKU2_CODE = "sku2";
	private static final String SKU1_CODE = "sku1";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final ProductSkuService mockProductSkuService = context.mock(ProductSkuService.class);
	private NonPurchasableProductsRequestHelper helper;
	private final ShoppingCart mockShoppingCart = context.mock(ShoppingCart.class);
	
	private final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
	
	/**
	 * Set up default test data and behavior.
	 */
	@Before
	public void setUp() {
		 helper = new NonPurchasableProductsRequestHelper(mockProductSkuService) {
			 //for simplicity, let's return just a modified sku code as the product display name, so no need for
			 //a comlicated mock object graph setup to return the actual product display name
			@Override 
			protected String getProductDisplayName(final Locale locale, final ProductSku sku) {
				return composeDisplayName(sku.getSkuCode(), locale);
			}
		 };
		 
		 context.checking(new Expectations() { {
			allowing(mockShoppingCart).getLocale(); will(returnValue(ENGLISH)); 
		 } });
		 
		mockRequest.clearAttributes();
	}
	
	/**
	 * When there's no non-purchasable items in the cart, the request attributes shouldn't be modified.
	 */
	@Test
	public void testRequestNotModifiedWhenNotPurchasableItemsAreNotInCart() {
		context.checking(new Expectations() { {
			oneOf(mockShoppingCart).getNotPurchasableCartItemSkus(); will(returnValue(Collections.emptySet()));
		} });
		
		helper.setNonPurchasableProductsInfoToRequest(mockRequest, mockShoppingCart);
		assertNull(mockRequest.getAttribute(NON_PURCHASABLE_ITEMS_WERE_REMOVED));
		assertNull(mockRequest.getAttribute(NON_PURCHASABLE_ITEMS_PRODUCT_NAMES));
	}
	
	/**
	 * Here we test that when the shopping cart contains un-purchasable items sku code,
	 * their product display names should be set to request for further display on the SF page. 
	 */
	@Test 
	public void testAddNonPurchasableItemsInfoToRequestWhenNotPurchasableItemsAreInCart() {
		final Set<String> nonPurchasableItems = new HashSet<String>() { {
			add(SKU1_CODE);
			add(SKU2_CODE);
		} };
		
		final Collection<ProductSku> skus = new ArrayList<ProductSku>() { {
			add(createSku(SKU1_CODE));
			add(createSku(SKU2_CODE));
		} };
		
		context.checking(new Expectations() { {
			oneOf(mockShoppingCart).getNotPurchasableCartItemSkus(); will(returnValue(nonPurchasableItems));
			oneOf(mockProductSkuService).findBySkuCodes(nonPurchasableItems); will(returnValue(skus));
		} });
		
		helper.setNonPurchasableProductsInfoToRequest(mockRequest, mockShoppingCart);
		
		assertTrue("Non-purchasable items collection should be emptied after we set those itesm to request", 
				nonPurchasableItems.isEmpty());
		Collection<String> displayNamesInRequest = (Collection<String>) mockRequest.getAttribute(NON_PURCHASABLE_ITEMS_PRODUCT_NAMES);
		assertEquals("There should be two display names in the request attributes", 2, displayNamesInRequest.size());
		
		final String skuDisplayName1 = composeDisplayName(SKU1_CODE, ENGLISH);
		final String skuDisplayName2 = composeDisplayName(SKU2_CODE, ENGLISH);
		assertTrue("Request attribute should contain display name for sku 1", displayNamesInRequest.contains(skuDisplayName1));
		assertTrue("Request attribute should contain display name for sku 2", displayNamesInRequest.contains(skuDisplayName2));
	}

	private String composeDisplayName(final String skuCode, final Locale locale) {
		return skuCode + "_" + locale.getLanguage();
	}
	
	private ProductSku createSku(final String skuCode) {
		final ProductSku sku = new ProductSkuImpl();
		sku.setSkuCode(skuCode);
		return sku;
	}
}