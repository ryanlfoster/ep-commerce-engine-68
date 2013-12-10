package com.elasticpath.sfweb.ajax.service.impl;

import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.catalogview.StoreSeoUrlBuilderFactory;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.search.query.ProductAutocompleteSearchCriteria;
import com.elasticpath.sfweb.ajax.bean.impl.AutocompletionRequestImpl;
import com.elasticpath.sfweb.view.helpers.PerStoreSettingHelper;

/**
 * Test for AutocompletionSearchProductServiceImpl.
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals" })
public class AutocompletionSearchProductServiceImplTest {
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private AutocompletionSearchProductServiceImpl service;
	private ShoppingCart shoppingCart;

	/**
	 * Test that if the price setting is disabled then price builder for retrieving map 
	 * of prices is not triggered.
	 */
	@Test
	public void testFindProductsPerformanceSwitch() {
		
		PerStoreSettingHelper diabledHelper = new PerStoreSettingHelper() {
			@SuppressWarnings("PMD.BooleanGetMethodName")
			public boolean getBoolean() {
				return false;
			}
			
		};
		PerStoreSettingHelper enabledHelper = new PerStoreSettingHelper() {
			@SuppressWarnings("PMD.BooleanGetMethodName")
			public boolean getBoolean() {
				return true;
			}
			
		};
		
		
		PerStoreSettingHelper priceEnabledHelper = diabledHelper;
		PerStoreSettingHelper thumbnailEnabledHelper = diabledHelper;
		PerStoreSettingHelper seoEnabledHelper = diabledHelper;
		PerStoreSettingHelper autocompletionEnabledHelper = enabledHelper;

		PerStoreSettingHelper numberOfResultsHelper = new PerStoreSettingHelper() {
			public int getInteger() {
				return 0;
			}	
		};

		
		shoppingCart = context.mock(ShoppingCart.class, "cart");
		
		service = new AutocompletionSearchProductServiceImpl() {
		
			ShoppingCart getShoppingCart() {
				return shoppingCart;
			}
			
			ProductAutocompleteSearchCriteria getProductSearchCriteria(
					final AutocompletionRequestImpl autocompletionRequest, final ShoppingCart shoppingCart) {
				return new ProductAutocompleteSearchCriteria();
			}
			
			List<Product> performSolrSearch(
					final ProductAutocompleteSearchCriteria productSearchCriteria,
					final int maxResults) {
				return Collections.emptyList();
			}
			
			Map<String, Price> getPriceMap(final ShoppingCart shoppingCart,
					final List<Product> productsList) {
				// if this is called then the builder will be called
				// no better solution of checking this for now.
				fail();
				return null;
			}
					
			
		};
		
		service.setPriceEnabledHelper(priceEnabledHelper);
		service.setNumberOfResultsHelper(numberOfResultsHelper);
		service.setThumbnailEnabledHelper(thumbnailEnabledHelper);
		service.setSeoEnabledHelper(seoEnabledHelper);
		service.setAutocompletionEnabledHelper(autocompletionEnabledHelper);
		
		final StoreSeoUrlBuilderFactory storeSeoUrlBuilderFactory = 
			context.mock(StoreSeoUrlBuilderFactory.class, "seoFactory");
		final SeoUrlBuilder seoUrlBuilder = context.mock(SeoUrlBuilder.class, "seoUrlBuilder");
		
		service.setStoreSeoUrlBuilderFactory(storeSeoUrlBuilderFactory);
		
		context.checking(new Expectations() { { 
			allowing(storeSeoUrlBuilderFactory).getStoreSeoUrlBuilder();
			will(returnValue(seoUrlBuilder));
			ignoring(shoppingCart);
			
		} });
		
		service.findProducts(new AutocompletionRequestImpl());
		
	}
	
}
