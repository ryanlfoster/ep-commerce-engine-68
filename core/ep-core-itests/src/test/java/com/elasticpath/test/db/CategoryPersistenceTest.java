package com.elasticpath.test.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.service.misc.impl.OpenJPAFetchPlanHelperImpl;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.util.Utils;

/**
 * This class mainly tests category's children category retrieval ability by specifying the recursionDepth in fetch group. May need to add more test
 * methods later.
 */
public class CategoryPersistenceTest extends DbTestCase {

	private CategoryType categoryType = null;

	private Catalog catalog = null;

	private final OpenJPAFetchPlanHelperImpl fetchPlanHelper = new OpenJPAFetchPlanHelperImpl();

	private Category category1;

	private Category category2;

	private Category category3;

	private Category category4;

	
	/**
	 * Sets up the test case.
	 * 
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		category1 = createCategory(getCatalog(), createCategoryType(getCatalog()));
		category2 = createCategory(getCatalog(), createCategoryType(getCatalog()));
		category3 = createCategory(getCatalog(), createCategoryType(getCatalog()));
		category4 = createCategory(getCatalog(), createCategoryType(getCatalog()));

		category1.addChild(category2);
		category2.addChild(category3);
		category3.addChild(category4);
		
		getTxTemplate().execute(new TransactionCallback<Category>() {
			@Override
			public Category doInTransaction(final TransactionStatus arg0) {
				return getPersistenceEngine().saveOrUpdate(category1);
			}
		});

	}
	/**
	 * Tests category retrieving.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveCategory() {

		Category returnedCategory1 = getTxTemplate().execute(new TransactionCallback<Category>() {
			@Override
			public Category doInTransaction(final TransactionStatus arg0) {

				fetchPlanHelper.setPersistenceEngine(getPersistenceEngine());
				fetchPlanHelper.configureFetchGroupLoadTuner(getLoadTuner());

				return getPersistenceEngine().get(CategoryImpl.class, category1.getUidPk());
			}
		});

		assertNotNull("Category1 not retrieved - null", returnedCategory1);
		Category returnedCategory2 = returnedCategory1.getChildren().iterator().next();
		assertNotNull("Category2 not retrieved - null", returnedCategory2);
		
		// If OpenJPA data cache is enabled, all fields may be loaded
		if (!getPersistenceEngine().isCacheEnabled()) {
			assertNull("Category2's children isn't empty - category3 should not have been retrieved.", returnedCategory2.getChildren());
		}
	}
	
	/**
	 * Tests that a product that has a hierarchy of parent categories loads them all at time of loading the product.
	 * Scenario:
	 * 1. Load category4
	 * 2. Check the hierarchy of categories
	 * 3. Create a product
	 * 4. Add category4 to the product
	 * 5. Save the product
	 * 6. Load the product and check the hierarchy of its categories
	 */
	@DirtiesDatabase
	@Test
	public void testLoadProductWithInfiniteParentCategories() {
		Category returnedCategory4 = getTxTemplate().execute(new TransactionCallback<Category>() {
			@Override
			public Category doInTransaction(final TransactionStatus arg0) {

				fetchPlanHelper.setPersistenceEngine(getPersistenceEngine());
				fetchPlanHelper.configureFetchGroupLoadTuner(getLoadTuner());

				return getPersistenceEngine().get(CategoryImpl.class, category4.getUidPk());
			}
		});

		Category parentCategory3 = returnedCategory4.getParent();
		Category parentCategory2 = parentCategory3.getParent();
		Category parentCategory1 = parentCategory2.getParent();
		
		assertNotNull(parentCategory1);
		
		final Product product = createSimpleProduct();
		
		product.addCategory(returnedCategory4);
		final Product updatedProduct = getTxTemplate().execute(new TransactionCallback<Product>() {
			@Override
			public Product doInTransaction(final TransactionStatus arg0) {

				return getPersistenceEngine().saveOrUpdate(product);
			}
		});

		Product loadedProduct = getTxTemplate().execute(new TransactionCallback<Product>() {
			@Override
			public Product doInTransaction(final TransactionStatus arg0) {

				fetchPlanHelper.setPersistenceEngine(getPersistenceEngine());
				fetchPlanHelper.configureFetchGroupLoadTuner(getLoadTuner2());
				
				Product lProduct = getPersistenceEngine().get(ProductImpl.class, updatedProduct.getUidPk());
				
				return lProduct;
			}
		});

		// outside the session we should have all the categories loaded
		checkProductCategories(loadedProduct);
	}

	private void checkProductCategories(final Product loadedProduct) {
		assertEquals(1, loadedProduct.getCategories().size());
		Category productCategory4 = loadedProduct.getCategories().iterator().next();
		assertNotNull(productCategory4);
		
		Category parentProductCategory3 = productCategory4.getParent();
		assertNotNull(parentProductCategory3);
		
		// Fails here because of an OpenJPA issue not loading all the categories (only with caching off)
		if (getPersistenceEngine().isCacheEnabled()) {
			Category parentProductCategory2 = parentProductCategory3.getParent();
			assertNotNull(parentProductCategory2);
			Category parentProductCategory1 = parentProductCategory2.getParent();
			assertNotNull(parentProductCategory1);
		}
		
	}

	private FetchGroupLoadTuner getLoadTuner() {
			FetchGroupLoadTuner loadTuner = new FetchGroupLoadTunerImpl();
			loadTuner.addFetchGroup(FetchGroupConstants.CATEGORY_BASIC, FetchGroupConstants.CATALOG_DEFAULTS, // need default locale
					FetchGroupConstants.INIFINITE_PARENT_CATEGORY_DEPTH, // needed for SEO
					FetchGroupConstants.CATEGORY_CHILD_LEVEL_1);
		return loadTuner;
	}

	private FetchGroupLoadTuner getLoadTuner2() {
			FetchGroupLoadTuner loadTuner = new FetchGroupLoadTunerImpl();
			loadTuner.addFetchGroup(
					FetchGroupConstants.INIFINITE_PARENT_CATEGORY_DEPTH, // needed for SEO
					FetchGroupConstants.LINK_PRODUCT_CATEGORY
					);
		return loadTuner;
	}

	/**
	 * Callback class for transactional code.
	 */
	public class TransactionCallbackForMerge implements TransactionCallback<Persistable> {

		private Persistable model = null;

		/**
		 * The Constructor.
		 * 
		 * @param model object to persist.
		 */
		public TransactionCallbackForMerge(final Persistable model) {
			this.model = model;
		}

		@Override
		public Persistable doInTransaction(final TransactionStatus arg0) {
			this.model = getPersistenceEngine().saveOrUpdate(this.model);
			return this.model;

		}
	}

	/**
	 * Returns current catalog or creates new one.
	 * 
	 * @return catalog.
	 */
	public Catalog getCatalog() {
		if (this.catalog == null) {
			catalog = createPersistedCatalog();
		}
		return this.catalog;
	}

	/**
	 * Returns current catalog or creates new one.
	 * 
	 * @return category type.
	 */
	public CategoryType getCategoryType() {
		if (categoryType == null) {
			categoryType = createCategoryType(catalog);
		}
		return this.categoryType;
	}

	private Catalog createPersistedCatalog() {
		final Catalog catalog = new CatalogImpl();
		catalog.setCode(Utils.uniqueCode("catalog"));
		catalog.setDefaultLocale(Locale.getDefault());
		catalog.setName(catalog.getCode());

		getTxTemplate().execute(new TransactionCallback<Catalog>() {
			@Override
			public Catalog doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(catalog);
				return catalog;
			}
		});

		return catalog;
	}

}
