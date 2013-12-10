/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductDeleted;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductDeletedImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.ProductDao;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test suite for <code>ProductServiceImpl</code>.
 */
public class ProductServiceImplOldTest extends AbstractEPServiceTestCase {

	private ProductServiceImpl productServiceImpl;

	private PersistenceEngine mockPersistenceEngine;

	private final ProductDao mockProductDao = context.mock(ProductDao.class);

	private ProductDeleted productDeleted;

	private final ProductSkuService mockProductSkuService = context.mock(ProductSkuService.class);

	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		productServiceImpl = new ProductServiceImpl();

		initializeProductServiceWithMocks(productServiceImpl);

		productDeleted = new ProductDeletedImpl();
		stubGetBean(ContextIdNames.PRODUCT_DELETED, productDeleted);
	}

	private void initializeProductServiceWithMocks(final ProductServiceImpl productService) {
		mockPersistenceEngine = getMockPersistenceEngine();
		productService.setPersistenceEngine(mockPersistenceEngine);

		productService.setProductDao(mockProductDao);
		productService.setProductSkuService(mockProductSkuService);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.ProductServiceImpl.setProductCategoryFeatured(long, long)'.
	 */
	@Test
	public void testSetProductCategoryFeatured() {
		// set up product and category
		final long productUid = 123L;
		final long categoryUid = 234L;
		final Product product = new ProductImpl();
		product.setUidPk(productUid);
		product.setGuid("productGuid");

		Category category = new CategoryImpl();
		category.setUidPk(categoryUid);
		category.setGuid("categoryGuid");
		category.setCatalog(new CatalogImpl());
		product.addCategory(category);

		// mock methods
		
		ArrayList<Integer> returnList = new ArrayList<Integer>();
		returnList.add(Integer.valueOf(2));
		context.checking(new Expectations() {
			{

				oneOf(mockProductDao).getWithCategories(with(any(long.class)));
				will(returnValue(product));

				oneOf(mockProductDao).getMaxFeaturedProductOrder(with(any(long.class)));
				will(returnValue(2));

				oneOf(mockProductDao).saveOrUpdate(with(same(product)));
			}
		});

		// run the service
		int newOrder = this.productServiceImpl.setProductCategoryFeatured(product.getUidPk(), category.getUidPk());

		// check result
		assertEquals(newOrder, 2 + 1);
	}

	/**
	 * Test that given one category and two products, the ProductServiceImpl.updateFeaturedProductOrder
	 * method will swap the preferred featured index of the two products in that category.
	 * e.g. product1 is the first featured item in the category and product2 is the second featured item
	 * in the category. After calling the method, product1 should be the second featured item, and
	 * product2 should be the first featured item.
	 */
	@Test
	public void testUpdateFeaturedProductOrder() {
		// set up product and category
		final long productUid = 123L;
		final long categoryUid = 234L;
		final Product firstProduct = new ProductImpl();
		firstProduct.setUidPk(productUid);
		firstProduct.setGuid(String.valueOf(productUid));

		Category category = new CategoryImpl();
		category.setUidPk(categoryUid);
		category.setGuid("categoryGuid");
		category.setCatalog(new CatalogImpl());
		//Add the
		firstProduct.addCategory(category);
		firstProduct.setFeaturedRank(category, 1);
		
		final long productUid2 = 12345L;
		final Product secondProduct = new ProductImpl();
		secondProduct.setUidPk(productUid2);
		secondProduct.setGuid(String.valueOf(productUid2));

		secondProduct.addCategory(category);
		secondProduct.setFeaturedRank(category, 2);

		// mock methods
		context.checking(new Expectations() {
			{

				oneOf(mockProductDao).getWithCategories(productUid);
				will(returnValue(firstProduct));

				oneOf(mockProductDao).getWithCategories(productUid2);
				will(returnValue(secondProduct));

				oneOf(mockProductDao).saveOrUpdate(with(same(firstProduct)));
				oneOf(mockProductDao).saveOrUpdate(with(same(secondProduct)));
			}
		});


		// run the service
		this.productServiceImpl.updateFeaturedProductOrder(firstProduct.getUidPk(), category.getUidPk(), secondProduct.getUidPk());
		
		// check result
		assertEquals("firstProduct should now have a preferred featuring order of 2",
				2, firstProduct.getFeaturedRank(category));
		assertEquals("secondProduct should now have a preferred featuring order of 1",
				1, secondProduct.getFeaturedRank(category));
	}
	
	/**
	 * Test that resetProductCategoryFeatured removes the "featuredness"
	 * of a product in a given category.
	 */
	@Test
	public final void testResetProductCategoryFeatured() {
		final Product mockProduct = context.mock(Product.class);
		final Category mockCategory = context.mock(Category.class);

		ProductServiceImpl service = new ProductServiceImpl() {
			@Override
			public Category getCategoryFromProductByUid(final Product product, final long categoryUid) {
				return mockCategory;
			}
		};
		
		service.setProductDao(mockProductDao);
		final long productUid = 123L;
		final long categoryUid = 234L;


		context.checking(new Expectations() {
			{
				//make sure it's set to 0
				oneOf(mockProduct).setFeaturedRank(mockCategory, 0);

				oneOf(mockProductDao).getWithCategories(with(any(long.class)));
				will(returnValue(mockProduct));

				oneOf(mockProductDao).saveOrUpdate(with(any(Product.class)));
				will(returnValue(mockProduct));
			}
		});
		service.resetProductCategoryFeatured(productUid, categoryUid);
	}

	/**
	 * Test that getCategoryFromProductByUid returns the product's default category,
	 * or null if the given category does not exist.
	 */
	@Test
	public final void testGetCategoryFromProductByUid() {
		final long categoryUid = 234L;
		final Category mockCategory = context.mock(Category.class);
		context.checking(new Expectations() {
			{
				allowing(mockCategory).getUidPk();
				will(returnValue(categoryUid));
			}
		});
		final Set<Category> categories = new HashSet<Category>();
		categories.add(mockCategory);
		final long productUid = 123L;
		final Product mockProduct = context.mock(Product.class);
		context.checking(new Expectations() {
			{
				allowing(mockProduct).getUidPk();
				will(returnValue(productUid));

				allowing(mockProduct).getCategories();
				will(returnValue(categories));
			}
		});
		final Product product = mockProduct;
		
		assertSame("Returned category should be the one the product belongs to", mockCategory,
				productServiceImpl.getCategoryFromProductByUid(product, categoryUid));
		
		assertNull("non-existent category should return a null result", productServiceImpl.getCategoryFromProductByUid(product, 0L));
		
	}

	/**
	 * Test method for {@link com.elasticpath.service.catalog.impl.ProductServiceImpl#notifyProductTypeUpdated(ProductType)}.
	 */
	@Test
	public final void testNotifyProductTypeUpdated() {
		final ProductType productType = context.mock(ProductType.class);
		context.checking(new Expectations() {
			{
				oneOf(mockProductDao).updateProductLastModifiedTime(with(same(productType)));
			}
		});
		productServiceImpl.notifyProductTypeUpdated(productType);
	}

	/**
	 * Test method for {@link com.elasticpath.service.catalog.impl.ProductServiceImpl#notifySkuUpdated(ProductSku)}.
	 */
	@Test
	public final void testNotifySkuUpdated() {
		final Product product = new ProductImpl();
		final ProductSku mockProductSku = context.mock(ProductSku.class);
		context.checking(new Expectations() {
			{
				oneOf(mockProductSku).getProduct();
				will(returnValue(product));

				oneOf(mockProductDao).updateProductLastModifiedTime(with(same(product)));
			}
		});
		productServiceImpl.notifySkuUpdated(mockProductSku);
	}

	/**
	 * Test method for {@link com.elasticpath.service.catalog.impl.ProductServiceImpl#notifyBrandUpdated(Brand)}.
	 */
	@Test
	public final void testNotifyBrandUpdated() {
		final Brand brand = context.mock(Brand.class);
		context.checking(new Expectations() {
			{
				oneOf(mockProductDao).updateProductLastModifiedTime(with(same(brand)));
			}
		});
		productServiceImpl.notifyBrandUpdated(brand);
	}

	/**
	 * Test method for {@link com.elasticpath.service.catalog.impl.ProductServiceImpl#notifyCategoryUpdated(Category)}.
	 */
	@Test
	public final void testNotifyCategoryUpdated() {
		final Category category = new CategoryImpl();
		final long categoryUid = 123L;
		category.setUidPk(categoryUid);
		final List<Long> productUids = new ArrayList<Long>();
		final List<Long> categoryUids = new ArrayList<Long>();
		categoryUids.add(categoryUid);
		context.checking(new Expectations() {
			{
				oneOf(mockProductDao).findUidsByCategoryUids(categoryUids);
				will(returnValue(productUids));

				oneOf(mockProductDao).updateLastModifiedTimes(with(same(productUids)));
			}
		});
		productServiceImpl.notifyCategoryUpdated(category);
	}

	/**
	 * Test method for {@link com.elasticpath.service.catalog.impl.ProductServiceImpl#canDelete(Product)}.
	 */
	@Test
	public final void testCanDelete() {
		ProductServiceImpl productService = new ProductServiceImpl() {
			@Override
			public boolean isInBundle(final Product product) {
				return false;
			}
		};
		
		initializeProductServiceWithMocks(productService);
		final Product mockProduct = context.mock(Product.class);
		final String guid = "SKUGUID";
		final ProductSku mockProductSku = context.mock(ProductSku.class);
		final Map<String, ProductSku> productSkus = new HashMap<String, ProductSku>();
		productSkus.put(guid, mockProductSku);
		context.checking(new Expectations() {
			{
				oneOf(mockProduct).getProductSkus();
				will(returnValue(productSkus));

				oneOf(mockProductSkuService).canDelete(with(same(mockProductSku)));
				will(returnValue(true));
			}
		});
		assertTrue("Should be able to delete a product not in any orders", productService.canDelete(mockProduct));
	}

}
