/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.view.helpers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalogview.impl.ThreadLocalStorageImpl;
import com.elasticpath.service.store.StoreService;

/**
 * Unit tests for the TopCategoriesHelperTest class.
 */
public class TopCategoriesHelperTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final CategoryService mockCategoryService = context.mock(CategoryService.class);
	private final StoreService mockStoreService = context.mock(StoreService.class);
	private CategoryService categoryService;
	private StoreService storeService;
	private ThreadLocalStorageImpl storeConfig;
	private TopCategoriesHelper tch;

	/**
	 * Setup test.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		categoryService = mockCategoryService;
		storeService = mockStoreService;
		storeConfig = new ThreadLocalStorageImpl();
		storeConfig.setStoreCode("SNAPITUP");
		storeConfig.setStoreService(storeService);
		tch = new TopCategoriesHelper();
		tch.setCategoryService(categoryService);
		tch.setStoreConfig(storeConfig);
	}
	
	/**
	 * Resolve top categories for a specific store that are not already cached.
	 */
	@Test
	public void testResolveTopCategoriesNotInCache() {
		// Try to resolve a top category that is not in the cache, call the database
		// and retrieve them then store them in the cache.
		final List<Category> topCategories = new ArrayList<Category>();
		final Store store = new StoreImpl();
		context.checking(new Expectations() {
			{
				oneOf(mockCategoryService).listRootCategories(with(aNull(Catalog.class)), with(any(boolean.class)));
				will(returnValue(topCategories));
				oneOf(mockStoreService).findStoreWithCode(with(any(String.class)));
				will(returnValue(store));
			}
		});
		List<Category> returnedCategories = tch.resolveTopCategories();
		assertEquals(topCategories.size(), returnedCategories.size());
		
	}
	
	/**
	 * Resolve top categories for a specific store that are already cached from a 
	 * previous call to resolveTopCategories.
	 */
	@Test
	public void testResolveTopCategoriesInCache() {
		// Try to resolve a top category that is not in the cache, call the database
		// and retrieve them then store them in the cache.
		final List<Category> topCategories = new ArrayList<Category>();
		Category cat = new CategoryImpl();
		topCategories.add(cat);
		context.checking(new Expectations() {
			{
				oneOf(mockCategoryService).listRootCategories(with(aNull(Catalog.class)), with(any(boolean.class)));
				will(returnValue(topCategories));
				Store store = new StoreImpl();
				oneOf(mockStoreService).findStoreWithCode(with(any(String.class)));
				will(returnValue(store));
			}
		});
		List<Category> returnedCategories = tch.resolveTopCategories();
		assertEquals(topCategories.size(), returnedCategories.size());

		// Try to resolve the topCategories once again, this time there is no need to call the database
		// as they are already present in the cache so the same methods are not called this time.
		returnedCategories = tch.resolveTopCategories();
		assertEquals(topCategories.size(), returnedCategories.size());
	}
	

}
