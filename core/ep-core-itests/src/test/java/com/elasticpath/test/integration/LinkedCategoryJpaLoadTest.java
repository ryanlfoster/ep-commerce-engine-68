/**
 * 
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.dao.ProductDao;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.rules.PromotionRuleDelegate;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.util.Utils;

/**
 * <p>This test verifies the fix for the bug in Jira item TCH-106.</p> 
 */
public class LinkedCategoryJpaLoadTest extends DbTestCase {
	
	/**
	 * <p>Tests that a linked category's parent and master category are loaded.
	 * 
	 * <p>Steps to reproduce the bug exactly:<br>
	 * <nl><li>Create a master category in a master catalog</li>
	 * <li>Create a subcategory for the master category (a new category with its parent set to the master category)</li>
	 * <li>Create a product and add it to the subcategory</li>
	 * <li>Create a virtual catalog</li>
	 * <li>Create a linked category within the virtual catalog, linked to the master category</li>
	 * <li>Load the product with a fetchgroup load tuner that contains the named CATEGORY_INDEX fetch group defined in LinkedCategoryImpl,
	 * and call "product.isInCategory()" with some random category name so that the recursive method runs its course.
	 * This will cause a call that's basically product.getCategory().getParentCategory().getMasterCategory(). The problem is that
	 * this call will get subCategory->LinkedCategory->MasterCategory and MasterCategory is not loaded so the call will return null.</li></nl>
	 * </p>
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveProductWithCategories() {
		ProductDao productDao = getBeanFactory().getBean("productDao");
		Category masterCategory = setupTestAndRetrieveCategory(productDao, 
				"productIndex", 
				"categoryIndex", 
				"infiniteParentCategoryDepth",
				"productHashMinimal", 
				"catalog");
		assertNotNull("When a product contained within a linked category's linked subcategory is loaded, "
				+ "the subcategory's parent category's master category should be loaded"
				+ "as well when the specified fetchgroup load tuner says to load a linked category's master category.", masterCategory);
	}
	
	/**
	 * Run the test steps and return the product's linked category's parent category's master category.
	 * @param productDao the productDao to use to load the product once the database is setup.
	 * @return
	 */
	private Category setupTestAndRetrieveCategory(final ProductDao productDao, final String... fetchGroups) {
		//Create a master catalog and category and subcategory
		Catalog masterCatalog = persisterFactory.getCatalogTestPersister().persistCatalog(Utils.uniqueCode("Canada"), true);
		CategoryType categoryType = persisterFactory.getCatalogTestPersister().persistCategoryType(
				Utils.uniqueCode("catType"), Utils.uniqueCode("catTypeTemplate"), masterCatalog);
		final String categoryGuid = "categoryGuid";
		Category masterCategory = persisterFactory.getCatalogTestPersister().persistCategory(
				categoryGuid, masterCatalog, categoryType, "masterCategory", "US");
		Category secondLevelCategory = persisterFactory.getCatalogTestPersister().persistCategory(
				"secondLevel", masterCatalog, categoryType, "secondLevelName", "US");
		secondLevelCategory.setParent(masterCategory);
		CategoryService categoryService = getBeanFactory().getBean("categoryService");
		categoryService.saveOrUpdate(secondLevelCategory);
		//Create a product in the master category's subcategory
		List<Product> products = persisterFactory.getCatalogTestPersister().persistDefaultShippableProducts(
				masterCatalog, secondLevelCategory, scenario.getWarehouse());

		//Create a virtual catalog and a linked category, linked to the master category
		Catalog virtualCatalog = persisterFactory.getCatalogTestPersister().persistCatalog("virtualCatalog", false);
		categoryService.addLinkedCategory(masterCategory.getUidPk(), -1, virtualCatalog.getUidPk());
		
		//Use the load tuner that contains the category_index fetch group defined in linkedcategoryimpl
		FetchGroupLoadTuner loadTuner = new FetchGroupLoadTunerImpl();
		loadTuner.addFetchGroup(fetchGroups);
		Product retrievedProduct = productDao.getTuned(products.get(1).getUidPk(), loadTuner);
		
		return retrievedProduct.getCategories(virtualCatalog).iterator().next().getParent().getMasterCategory();
	}
	
	/**
	 * The root cause of the linked category load bug was due to method 
	 * {@link PromotionRuleDelegate#catalogProductInCategory(Product, boolean, String, String)}
	 * assuming that the Product was loaded with all of its parent categories, including linked categories.
	 * 
	 * This test checks that the method will now ensure this is the case. 
	 */
	@DirtiesDatabase
	@Test
	public void testPromotionRuleDelegateEnsuresFullTreeLoaded() {
		//Create a master catalog and category and subcategory
		Catalog masterCatalog = persisterFactory.getCatalogTestPersister().persistCatalog(Utils.uniqueCode("Canada"), true);
		CategoryType categoryType = persisterFactory.getCatalogTestPersister().persistCategoryType(
				Utils.uniqueCode("catType"), Utils.uniqueCode("catTypeTemplate"), masterCatalog);
		final String categoryGuid = "categoryGuid";
		Category masterCategory = persisterFactory.getCatalogTestPersister().persistCategory(
				categoryGuid, masterCatalog, categoryType, "masterCategory", "US");
		Category secondLevelCategory = persisterFactory.getCatalogTestPersister().persistCategory(
				"secondLevel", masterCatalog, categoryType, "secondLevelName", "US");
		secondLevelCategory.setParent(masterCategory);
		CategoryService categoryService = getBeanFactory().getBean("categoryService");
		categoryService.saveOrUpdate(secondLevelCategory);
		//Create a product in the master category's subcategory
		List<Product> products = persisterFactory.getCatalogTestPersister().persistDefaultShippableProducts(
				masterCatalog, secondLevelCategory, scenario.getWarehouse());

		//Create a virtual catalog and a linked category, linked to the master category
		Catalog virtualCatalog = persisterFactory.getCatalogTestPersister().persistCatalog("virtualCatalog", false);
		categoryService.addLinkedCategory(masterCategory.getUidPk(), -1, virtualCatalog.getUidPk());
		
		//Use no load tuner to load the product. This ensures that the category hierarchy is not loaded before we call the PromotionRuleDelegate
		ProductDao productDao = getBeanFactory().getBean("productDao");
		Product retrievedProduct = productDao.get(products.get(1).getUidPk());
		
		final String nonMatchingExceptionString = null;
		PromotionRuleDelegate promotionRuleDelegate = getBeanFactory().getBean(ContextIdNames.PROMOTION_RULE_DELEGATE);
		assertTrue(promotionRuleDelegate.catalogProductInCategory(retrievedProduct, true, masterCategory.getCompoundGuid(), 
				nonMatchingExceptionString));
	}
}
