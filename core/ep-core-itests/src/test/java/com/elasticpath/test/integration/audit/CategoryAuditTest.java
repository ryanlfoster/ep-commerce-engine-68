package com.elasticpath.test.integration.audit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.ThreadLocalMap;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.persistence.openjpa.ChangeType;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.util.Utils;

/**
 * CategoryAuditImplTest. 
 */
public class CategoryAuditTest extends AbstractAuditTestSupport {
	
	@Autowired
	private CategoryService categoryService;
	
	private TestDataPersisterFactory persisterFactory;
	
	@Autowired
	private ThreadLocalMap<String, Object> metadata;

	
	/**
	 * Setup tests.
	 * @throws Exception on error
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		persisterFactory = getTac().getPersistersFactory();
	}
	
	/**
	 * Test create category with audit.
	 */
	@DirtiesDatabase
	@Test
	public void testCreateCategoryWithAuditing() {
		Category masterCategory = createCategory();		
		Category category = categoryService.saveOrUpdate(masterCategory);
		
		// Second, create the category that links to the above master
		// Catalog virtualCatalog = persisterFactory.getCatalogTestPersister().persistCatalog(Utils.uniqueCode("Canada"), false);
		// Category linkedCategory = persisterFactory.getCatalogTestPersister().createLinkedCategory(virtualCatalog, masterCategory);
		// categoryService.add(linkedCategory);
		
		int expectedChangeOperationNumber = 1;
		verifyAuditData(null, category, category.getGuid(), ChangeType.CREATE, expectedChangeOperationNumber);
	}	
	
	/**
	 * Test remove category with audit.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveCategoryWithAuditing() {
		Category masterCategory = createCategory();		
		Category category = categoryService.saveOrUpdate(masterCategory);		
		
		categoryService.removeCategoryTree(category.getUidPk());
		
		int expectedChangeOperationNumber = 1;
		verifyAuditData(category, null, category.getGuid(), ChangeType.DELETE, expectedChangeOperationNumber);
		
	}
	
	/**
	 * Test update category with audit.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateCategoryWithAuditing() {
		Category masterCategory = createCategory();		
		Category category = categoryService.saveOrUpdate(masterCategory);		
		
		Category categoryBack = categoryService.findByGuid(category.getGuid(), category.getCatalog());
		
		category.setOrdering(2);
		Category updatedCategory = categoryService.saveOrUpdate(category);
		
		int expectedChangeOperationNumber = 1;
		verifyAuditData(categoryBack, updatedCategory, updatedCategory.getGuid(),  ChangeType.UPDATE, expectedChangeOperationNumber);
	}	

	/**
	 * AuditService is the audit service.
	 * @return a new catalog
	 */
	private Category createCategory() {
		
		registerMetaData();		
		
		Catalog catalog = persisterFactory.getCatalogTestPersister().persistCatalog(Utils.uniqueCode("Canada"), true);					
		CategoryType categoryType = persisterFactory.getCatalogTestPersister()
									.persistCategoryType(Utils.uniqueCode("catType"),
									Utils.uniqueCode("catTypeTemplate"), catalog);
				
		Category masterCategory = getBeanFactory().getBean("category");
		masterCategory.setCategoryType(categoryType);
		masterCategory.setCatalog(catalog);
		masterCategory.setCode(Utils.uniqueCode("category"));
		return masterCategory;
	}

	private void registerMetaData() {
		metadata.put("changeSetGuid", "csGuid1");
		metadata.put("userGuid", "uGuid1");
	}
	
	
	
}
