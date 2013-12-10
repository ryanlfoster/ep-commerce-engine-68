package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpCategoryNotEmptyException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.LinkedCategoryImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.util.Utils;

/**
 * An integration test for the CategoryService to make sure categories are created and retrieved correctly.
 */
public class CategoryServiceImplTest extends BasicSpringContextTest {

	private static final String CHILD_CATEGORY_SHOULD_BE_PERSISTED = "Child category should be persisted";

	/** The main object under test. */
	@Autowired
    @Qualifier("categoryService")
	private CategoryService service;

	@Autowired
	private ProductService productService;

	/** Most test cases need a catalog. */
	private Catalog masterCatalog;
	private Catalog virtualCatalog;

	/** All categories need a type. */
	private CategoryType categoryType;

	private TestDataPersisterFactory persisterFactory;

	private Category rootCategory;

	private Product product;

	private Category subSubCategory;

	private Category subSubCategory2;

	private Category linkedRootCategory;

	private Category linkedSubSubCategory;

	private Catalog virtualCatalog2;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 * 
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		persisterFactory = getTac().getPersistersFactory();

		masterCatalog = persisterFactory.getCatalogTestPersister().persistCatalog(Utils.uniqueCode("Canada"), true);
		categoryType = persisterFactory.getCatalogTestPersister().
			persistCategoryType(Utils.uniqueCode("catType"), Utils.uniqueCode("catTypeTemplate"), masterCatalog);
		virtualCatalog = persisterFactory.getCatalogTestPersister().persistCatalog(Utils.uniqueCode("Canada"), false);
		virtualCatalog2 = persisterFactory.getCatalogTestPersister().persistCatalog(Utils.uniqueCode("USA"), false);
	}
	/**
	 * OpenJPA bug: [TCH-30] OpenJPA inability to load master categories for a hierarchy of linked 
	 * categories to a depth of more than 2. A patch was applied to OpenJPA for 6.1.2 to make the 
	 * fetch group depth setting work.  This test will prevent any regression. 
	 * 
	 * Create a category structure 4 levels deep. Create linked category of the top level master category. 
	 * Finally, retrieve the leave linked category and traverse up the graph (using LinkedCategoryImpl.getParent()) making sure 
	 * every linked category has a master category.
	 * 
	 * <pre>
	 * Catalog 1: Root      - Sub1 - Sub2 - Sub3 - Sub4
	 *            ^           ^      ^      ^      ^
	 *            |           |      |      |      |     <- master category association under test 
	 * Virtual 1: Root Link - Sub1 - Sub2 - Sub3 - Sub4
	 * </pre>
	 */
	@SuppressWarnings("PMD.AvoidDuplicateLiterals")
	@DirtiesDatabase
	@Test
	public void testAddAndGetLinkedCategoryMasters() {
		// (1) create a root category
		Category rootCategory = createNormalCategory();
		rootCategory = service.saveOrUpdate(rootCategory);
		assertNotSame("Object not persisted.", 0, rootCategory.getUidPk());

		// create a sub-category 1
		Category subCategory1 = createNormalCategory();
		rootCategory.addChild(subCategory1);
		subCategory1 = service.saveOrUpdate(subCategory1);
		assertNotSame("Object not persisted.", 0, subCategory1.getUidPk());
		
		// create a sub-category 2
		Category subCategory2 = createNormalCategory();
		subCategory1.addChild(subCategory2);
		subCategory2 = service.saveOrUpdate(subCategory2);
		assertNotSame("Object not persisted.", 0, subCategory2.getUidPk());
		
		// create a sub-category 3
		Category subCategory3 = createNormalCategory();
		subCategory2.addChild(subCategory3);
		subCategory3 = service.saveOrUpdate(subCategory3);
		assertNotSame("Object not persisted.", 0, subCategory3.getUidPk());
		
		// create a sub-category 4
		Category subCategory4 = createNormalCategory();
		subCategory3.addChild(subCategory4);
		subCategory4 = service.saveOrUpdate(subCategory4);
		assertNotSame("Object not persisted.", 0, subCategory4.getUidPk());

		// (2) create a root linked category
		Category linkedRoot = persisterFactory.getCatalogTestPersister().createLinkedCategory(virtualCatalog, rootCategory);
		linkedRoot = service.saveOrUpdate(linkedRoot);
		assertNotSame("Object not persisted.", 0, linkedRoot.getUidPk());
		assertNotNull(linkedRoot.getMasterCategory());
		
        // create sequence of linked categories		
		Category linkedSub1 = persisterFactory.getCatalogTestPersister().createLinkedCategory(virtualCatalog, subCategory1);
		linkedRoot.addChild(linkedSub1);
		linkedSub1 = service.saveOrUpdate(linkedSub1);
		assertNotSame("Object not persisted.", 0, linkedSub1.getUidPk());
		assertNotNull(linkedSub1.getMasterCategory());
		
		Category linkedSub2 = persisterFactory.getCatalogTestPersister().createLinkedCategory(virtualCatalog, subCategory2);
		linkedSub1.addChild(linkedSub2);
		linkedSub2 = service.saveOrUpdate(linkedSub2);
		assertNotSame("Object not persisted.", 0, linkedSub2.getUidPk());
		assertNotNull(linkedSub2.getMasterCategory());
		
		Category linkedSub3 = persisterFactory.getCatalogTestPersister().createLinkedCategory(virtualCatalog, subCategory3);
		linkedSub2.addChild(linkedSub3);
		linkedSub3 = service.saveOrUpdate(linkedSub3);
		assertNotSame("Object not persisted.", 0, linkedSub3.getUidPk());
		assertNotNull(linkedSub3.getMasterCategory());
		
		Category linkedSub4 = persisterFactory.getCatalogTestPersister().createLinkedCategory(virtualCatalog, subCategory4);
		linkedSub3.addChild(linkedSub4);
		linkedSub4 = service.saveOrUpdate(linkedSub4);
		assertNotSame("Object not persisted.", 0, linkedSub4.getUidPk());
		assertNotNull(linkedSub4.getMasterCategory());
		

		// Ensure the load tuner used to load the categories is set
		// to force eager loading of all parent cateories.
		CategoryLoadTuner categoryLoadTuner = getBeanFactory().getBean("CATEGORY_LOAD_TUNER_ALL");
		categoryLoadTuner.setParentCategoryLevel(-1);


		// reload the leave linked category and attempt to walk up the graph of 
		// parent categories and checking if master categories are loaded		
		Category retrievedLeaveChild = service.get(linkedSub4.getUidPk());
		assertNotNull("Unable to find leave linked category by UIDPK.", retrievedLeaveChild);
		
		// linked categories to a depth of 4
		int expectedLevels = 4;
		int actualLevels = 0;
		
		// walk up the linked category graph
		Category parent = retrievedLeaveChild;
		while (parent != null) {
			assertTrue("Linked category must be of type LinkedCategoryImpl.", parent instanceof LinkedCategoryImpl);
			LinkedCategoryImpl linkedCategory = (LinkedCategoryImpl) parent;
			
			assertTrue("Linked category isLinked() method must return true.", linkedCategory.isLinked());
			assertNotNull("A linked category must have a master category.", linkedCategory.getMasterCategory());
			
			parent = parent.getParent();
			
			if (parent != null) {
				// count the level of categories to ensure the parent associations are in place
				actualLevels++;
			}
		}
		
		assertEquals("Leave linked category parent levels not correctly persisted.", expectedLevels, actualLevels);
	}

	/**
	 * 
	 */
	@DirtiesDatabase
	@Test
	public void testAddAndGetNormalCategory() {
		Category category = createNormalCategory();
		service.add(category);

		assertNotSame(0, category.getUidPk());

		Category retrievedCategory = service.get(category.getUidPk());
		assertEquals(category.getUidPk(), retrievedCategory.getUidPk());
	}

	/**
	 *  
	 */
	@DirtiesDatabase
	@Test
	public void testGetSubCategory() {
		Category category = createNormalCategory();
		service.add(category);
		Category subCategory = createNormalCategory();
		category.addChild(subCategory);
		service.update(category);

		assertNotSame(0, category.getUidPk());

		Category retrievedCategory = service.get(category.getUidPk());
		assertEquals(category.getUidPk(), retrievedCategory.getUidPk());

		service.hasSubCategories(category.getUidPk());
		assertFalse(category.getChildren().isEmpty());
	}

	/**
	 * 
	 */
	@DirtiesDatabase
	@Test
	public void testAddAndGetLinkedCategory() {
		Category masterCategory = createNormalCategory();
		service.add(masterCategory);
		assertNotSame(0, masterCategory.getUidPk());

		// Second, create the category that links to the above master
		Category linkedCategory = persisterFactory.getCatalogTestPersister().createLinkedCategory(virtualCatalog, masterCategory);
		service.add(linkedCategory);
		assertNotSame(0, linkedCategory.getUidPk());

		Category retrievedCategory = service.get(linkedCategory.getUidPk());
		assertEquals(linkedCategory.getUidPk(), retrievedCategory.getUidPk());
	}

	/**
	 * 
	 */
	@DirtiesDatabase
	@Test
	public void testAddAndGetLinkedCategoryWithNamedQuery() {
		Category masterCategory = createNormalCategory();
		service.add(masterCategory);
		assertNotSame(0, masterCategory.getUidPk());

		// Second, create the category that links to the above master
		Category linkedCategory = persisterFactory.getCatalogTestPersister().createLinkedCategory(virtualCatalog, masterCategory);
		service.add(linkedCategory);
		assertNotSame(0, linkedCategory.getUidPk());

		List<Category> retrievedCategories = service.listRootCategories(virtualCatalog, false);
		assertNotNull(retrievedCategories);
		assertEquals(retrievedCategories.size(), 1);
		Category retrievedCategory = retrievedCategories.get(0);
		assertEquals(linkedCategory.getUidPk(), retrievedCategory.getUidPk());
	}

	/**
	 * Tests adding a linked category of a deep hierarchy category tree.
	 */
	@DirtiesDatabase
	@Test
	public void testAddLinkedCategoryOfDeepHierarchy() {
		
		setupScenario();
		
		List<Product> productsInMasterCategory = productService.findByCategoryUid(subSubCategory.getUidPk());
		assertEquals(1, productsInMasterCategory.size());
		assertEquals(product, productsInMasterCategory.iterator().next());

		// make sure that the linked category points to the corresponding master (3)
		assertEquals(linkedSubSubCategory.getMasterCategory(), subSubCategory);

		productsInMasterCategory = productService.findByCategoryUid(subSubCategory.getUidPk());
		assertEquals(1, productsInMasterCategory.size());
		assertEquals(product, productsInMasterCategory.iterator().next());

		// get all products for the linked category
		List<Product> products = productService.findByCategoryUid(linkedSubSubCategory.getUidPk());
		// we expect only one product which is the one added to the master category (3)
		assertEquals(1, products.size());
		assertEquals(product, products.iterator().next());
	}
	
	/**
	 * 
	 */
	@DirtiesDatabase
	@Test
	public void testAddAndGetLinkedSubCategory() {
		/*
		 * (1) <--link-- (3) \ \ (2) <--link-- (4)
		 */

		// (1) create a root category
		Category rootCategory = createNormalCategory();
		rootCategory = service.saveOrUpdate(rootCategory);
		assertNotSame(0, rootCategory.getUidPk());

		// (2) create a sub-category under root in (1)
		Category subCategory = createNormalCategory();
		rootCategory.addChild(subCategory);
		subCategory = service.saveOrUpdate(subCategory);
		assertNotSame(0, subCategory.getUidPk());
		
		// Save the root category
		rootCategory = service.saveOrUpdate(rootCategory);

		// Assert the parent-child relationship between the root and sub-category above
		Category retrievedRootCategory = service.get(rootCategory.getUidPk());
		assertTrue(retrievedRootCategory.getHasSubCategories());
		assertEquals(1, retrievedRootCategory.getChildren().size());
		for (Category currCategory : retrievedRootCategory.getChildren()) {
			assertEquals(currCategory, subCategory);
		}

		// (3) create a root category that links to the root in (1)
		Category linkedRootCategory = persisterFactory.getCatalogTestPersister().createLinkedCategory(virtualCatalog, rootCategory);
		linkedRootCategory = service.saveOrUpdate(linkedRootCategory);
		assertNotSame(0, linkedRootCategory.getUidPk());

		assertNotNull(linkedRootCategory.getMasterCategory());

		Category retrievedLinkedCategory = service.get(linkedRootCategory.getUidPk());
		// Assert that the category is linked
		assertTrue(retrievedLinkedCategory.isLinked());
		// Assert that the master category value was persisted
		assertNotNull(retrievedLinkedCategory.getMasterCategory());

		// (4) create a sub-category under the linked root in (3), that links to the sub-category in (2)
		Category linkedSubCategory = persisterFactory.getCatalogTestPersister().createLinkedCategory(virtualCatalog, subCategory);
		linkedRootCategory.addChild(linkedSubCategory);
		linkedSubCategory = service.saveOrUpdate(linkedSubCategory);
		assertNotSame(0, linkedSubCategory.getUidPk());
		
		// Save linked root category
		linkedRootCategory = service.saveOrUpdate(linkedRootCategory);

		Category retrievedLinkedSubCategory = service.get(linkedSubCategory.getUidPk());
		// Assert that the category is linked
		assertTrue(retrievedLinkedSubCategory.isLinked());
		// Assert that the master category value was persisted
		assertNotNull(retrievedLinkedSubCategory.getMasterCategory());

		// Assert the parent-child relationship between the linked root and linked sub-category
		Category retrievedLinkedRootCategory = service.get(linkedRootCategory.getUidPk());
		assertTrue(retrievedLinkedRootCategory.getHasSubCategories());
		assertEquals(1, retrievedLinkedRootCategory.getChildren().size());
		for (Category currCategory : retrievedLinkedRootCategory.getChildren()) {
			assertEquals(currCategory.getUidPk(), linkedSubCategory.getUidPk());
		}

		List<Category> rootCategories = service.listRootCategoriesWithTree(virtualCatalog, false);
		for (Category currCategory : rootCategories) {
			if (currCategory.isLinked()) {
				assertNotNull(currCategory.getMasterCategory());
				for (Category currChild : currCategory.getChildren()) {
					assertNotNull(currChild.getMasterCategory());
				}
			}
		}
	}
	
	/**
	 * Test that a category gets updated correctly.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateCategory() {
		final String categoryName = "Test Category";

		// Create a new persistent category
		Category category = service.saveOrUpdate(createNormalCategory());
		assertTrue("Category should be persistent", category.isPersisted());

		// Set the category display name and update
		category.setDisplayName(categoryName, Locale.ENGLISH);
		Category updatedCategory = service.update(category);
		assertEquals("Category should have the display name we set", categoryName, updatedCategory.getDisplayName(Locale.ENGLISH));

	}

	/**
	 * Tests that findCodeByUid() returns an empty string when no categories matching the uid are found.
	 */
	@DirtiesDatabase
	@Test
	public void testFindCodeByUidNoCodesReturned() {
		// Since no category has been added to the db, no code should be returned.
		String code = service.findCodeByUid(2L);
		assertEquals("The returned code should be an empty string", "", code);
	}
	
	/**
	 * Tests that findCodeByUid() returns the code associated with the category containing the uid searched for.
	 */
	@DirtiesDatabase
	@Test
	public void testFindCodeByUidOneCodeReturned() {
		// Add a category to the database
		Category category = createNormalCategory();
		category = service.add(category);
		assertTrue(CHILD_CATEGORY_SHOULD_BE_PERSISTED, category.isPersisted());

		// Find the code of the category, given the uidPk
		String code = service.findCodeByUid(category.getUidPk());
		assertEquals("The returned code should match the one of the returned category", 
				category.getCode(), code);
	}
	
	/**
	 * Tests that findAncestorCategoryUidsByCategoryUid() returns an empty Set
	 * when the category has no parents.
	 *
	 */
	@DirtiesDatabase
	@Test
	public void testFindAncestorCategoryUidsByCategoryUidNoParents() {
		// Create child category
		Category child = createNormalCategory();
		child = service.add(child);
		assertTrue(CHILD_CATEGORY_SHOULD_BE_PERSISTED, child.isPersisted());
		
		// Check that no parent category uids are returned
		Set<Long> ancestorUids = service.findAncestorCategoryUidsByCategoryUid(child.getUidPk());
		assertNotNull("The set of ancestor uids should not be null", ancestorUids);
		assertEquals("The set of ancestor uids should contain only one value.", 0, ancestorUids.size());
	}
	
	/**
	 * Tests that findAncestorCategoryUidsByCategoryUid() returns the uid of a category's parent.
	 * In this case only one parent was setup.
	 *
	 */
	@DirtiesDatabase
	@Test
	public void testFindOneAncestorCategoryUidsByCategoryUid() {
		// Create parent category
		Category parent = createNormalCategory();
		parent = service.add(parent);
		assertTrue("Parent category should be persisted", parent.isPersisted());
		
		// Create child category
		Category child = createNormalCategory();
		child.setParent(parent);
		child = service.add(child);
		assertTrue(CHILD_CATEGORY_SHOULD_BE_PERSISTED, child.isPersisted());
		
		// Find the category uid of the child's parent
		Set<Long> ancestorUids = service.findAncestorCategoryUidsByCategoryUid(child.getUidPk());
		assertNotNull("The set of ancestor uids should not be null", ancestorUids);
		assertEquals("The set of ancestor uids should contain only one value.", 1, ancestorUids.size());
		assertTrue("The set should contain the persisted parent uid", 
				ancestorUids.contains(parent.getUidPk()));
	}

	/**
	 * Tests that findAncestorCategoryCodesByCategoryUid() returns an empty Set
	 * when the category has no parents.
	 *
	 */
	@DirtiesDatabase
	@Test
	public void testFindAncestorCategoryCodesByCategoryUidNoParents() {
		// Create child category
		Category child = createNormalCategory();
		child = service.add(child);
		assertTrue(CHILD_CATEGORY_SHOULD_BE_PERSISTED, child.isPersisted());
		
		// Find the category code of the child's parent
		Set<String> ancestorCodes = service.findAncestorCategoryCodesByCategoryUid(child.getUidPk());
		assertNotNull("The set of ancestor codes should not be null", ancestorCodes);
		assertEquals("The set of ancestor codes should contain only one value.", 0, ancestorCodes.size());
	}
	
	/**
	 * Tests that findAncestorCategoryCodesByCategoryUid() returns the code of a category's parent.
	 * In this case only one parent was setup.
	 *
	 */
	@DirtiesDatabase
	@Test
	public void testFindOneAncestorCategoryCodesByCategoryUid() {
		// Create parent category
		Category parent = createNormalCategory();
		parent = service.add(parent);
		assertTrue("Parent category should be persisted", parent.isPersisted());
		
		// Create child category
		Category child = createNormalCategory();
		child.setParent(parent);
		child = service.add(child);
		assertTrue(CHILD_CATEGORY_SHOULD_BE_PERSISTED, child.isPersisted());
		
		// Find the category code of the child's parent
		Set<String> ancestorCodes = service.findAncestorCategoryCodesByCategoryUid(child.getUidPk());
		assertNotNull("The set of ancestor codes should not be null", ancestorCodes);
		assertEquals("The set of ancestor codes should contain only one value.", 1, ancestorCodes.size());
		assertTrue("The set should contain the persisted parent code", 
				ancestorCodes.contains(parent.getCode()));
	}
	
	/**
	 * Test that getMasterCatalog(Category) with a linked category will get the linked category's 
	 * master category's catalog. 
	 */
	@DirtiesDatabase
	@Test
	public void testGetMasterCatalogLinkedCategory() {
		//Create the master category
		Category masterCategory = createNormalCategory();
		masterCategory = service.add(masterCategory);

		// Create the linked category that links to the above master
		Category linkedCategory = persisterFactory.getCatalogTestPersister().createLinkedCategory(virtualCatalog, masterCategory);
		linkedCategory = service.add(linkedCategory);

		//Make sure you get the master catalog back
		assertEquals(this.masterCatalog, service.getMasterCatalog(linkedCategory));		
	}
	
	/**
	 * Test that getMasterCatalog(Category) with a non-linked category will just retrieve
	 * the catalog within which the category lives.
	 */
	@DirtiesDatabase
	@Test
	public void testGetMasterCatalog() {
		//Create the master category
		Category masterCategory = createNormalCategory();
		masterCategory = service.add(masterCategory);
		
		//Make sure you get the master catalog back
		assertEquals(this.masterCatalog, service.getMasterCatalog(masterCategory));		
	}
	
	private Category createNormalCategory() {
		Category masterCategory = getBeanFactory().getBean("category");
		masterCategory.setCategoryType(categoryType);
		masterCategory.setCatalog(masterCatalog);
		masterCategory.setCode(Utils.uniqueCode("category"));
		return masterCategory;
	}
	
	/**
	 * Tests finding a category by code and catalog. 
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCodeAndCatalog() {
		setupScenario();
		
		List<Product> productsInMasterCategory = productService.findByCategoryUid(subSubCategory.getUidPk());
		assertEquals(1, productsInMasterCategory.size());
		assertEquals(product, productsInMasterCategory.iterator().next());

		FetchGroupLoadTuner loadTuner = new FetchGroupLoadTunerImpl();
		loadTuner.addFetchGroup(FetchGroupConstants.INIFINITE_PARENT_CATEGORY_DEPTH, 
								FetchGroupConstants.CATEGORY_BASIC);
		Category foundLinkedCategory = service.findByGuid(linkedSubSubCategory.getCode(), virtualCatalog, loadTuner);
		
		rootCategory = foundLinkedCategory.getParent().getParent();
		assertEquals("1", rootCategory.getCode());

		assertEquals(linkedSubSubCategory, foundLinkedCategory);
	}
	
	/**
	 * Sets up a common scenario for this test case.
	 * <p>
	 * MASTER			VIRTUAL		  <br>
	 * (1) 				<--link-- (1A)<br> 
	 *  |							  <br>
	 *  +-(2)			<--link-- (2A)<br>
	 *     |						  <br>
	 *     +-(3)		<--link-- (3A)<br>
	 *     |  + product			  	  <br>
	 *     |						  <br>
	 *     +-(4)        <--link-- (4A)<br>
	 */
	void setupScenario() {

		// (1) create a root category
		rootCategory = createNormalCategory();
		rootCategory.setCode("1");
		rootCategory = service.saveOrUpdate(rootCategory);
		assertNotSame(0, rootCategory.getUidPk());

		// (2) create a sub-category under root in (1)
		Category subCategory = createNormalCategory();
		subCategory.setCode("2");
		rootCategory.addChild(subCategory);
		subCategory = service.saveOrUpdate(subCategory);
		assertNotSame(0, subCategory.getUidPk());

		// (3) create a sub-sub-category
		subSubCategory = createNormalCategory();
		subSubCategory.setCode("3");
		subSubCategory.setOrdering(0);
		subCategory.addChild(subSubCategory);
		subSubCategory = service.saveOrUpdate(subSubCategory);
		assertNotSame(0, subSubCategory.getUidPk());

		// add a product to (3)
		product = persisterFactory.getCatalogTestPersister().createSimpleProduct(
				"TestProductType", Utils.uniqueCode("product"), masterCatalog,
				getTac().getPersistersFactory().
				getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS), subSubCategory);
		// add the product to a new category and save it
		productService.saveOrUpdate(product);
		product = productService.load(product.getUidPk());
		
		assertTrue(product.isInCategory(subSubCategory.getCompoundGuid()));

		// (4) create another sub-sub-category
		subSubCategory2 = createNormalCategory();
		subSubCategory2.setCode("4");
		subSubCategory2.setOrdering(1);
		subCategory.addChild(subSubCategory2);
		subSubCategory2 = service.saveOrUpdate(subSubCategory2);
		assertNotSame(0, subSubCategory2.getUidPk());
		assertNotNull(service.findByGuid("4"));
		
		// Save the root category
		rootCategory = service.saveOrUpdate(rootCategory);
		
		// create the root linked category (1A)
		linkedRootCategory = service.addLinkedCategory(rootCategory.getUidPk(), -1, virtualCatalog.getUidPk());

		// get the sub sub category linked category (3A)
		for (Category linkedCategory : linkedRootCategory.getChildren().iterator().next().getChildren()) {
			if (linkedCategory.getCode().equals("3")) {
				linkedSubSubCategory = linkedCategory;
			}
		}
		
		service.findByGuid("4", virtualCatalog);

	}
	
	/**
	 * Tries to move category 3B down in virtual catalog 2.
	 * All categories are descendants of a linked category pointing to category (1).
	 * 
	 * <p>
	 * NOTE: This test fails from time to time as the implementation of the method depends on
	 * a certain order of the categories. This is a reported bug: NTRN-1037.
	 * <p>
	 * JVN: Updated scenario to provide an initial ordering of master categories 3 and 4 to solve
	 * the random test failures. The original "sequence" of categories retrieved from DB problem
	 * has not been resolved, but the test will pass consistently.
	 */
	@DirtiesDatabase
	@Test
	public void testMoveCategoryDown() {
		// create category and linked category structure as define by the scenario
		setupScenario();
		final Catalog vCatalog = this.virtualCatalog2;
		
		// create the root linked category (1B)
		linkedRootCategory = service.addLinkedCategory(rootCategory.getUidPk(), -1, vCatalog.getUidPk());
		
		Category linkedCategory4 = service.findByGuid("4", vCatalog);
		assertNotNull(linkedCategory4);
		Category linkedCategory3 = service.findByGuid("3", vCatalog);

		// the scenario will set the initial ordering of the master categories
		int orderingCategory3 = linkedCategory3.getOrdering();
		int orderingCategory4 = linkedCategory4.getOrdering();
		assertEquals("Unexpected ordering by scenario setup.", 0, orderingCategory3);
		assertEquals("Unexpected ordering by scenario setup.", 1, orderingCategory4);

		// swap category 3 and 4, ie change 3's order from 0 to 1, and 4's order from 1 to 0
		service.updateCategoryOrderDown(linkedCategory3);

		// refresh
		linkedCategory4 = service.findByGuid("4", vCatalog);
		linkedCategory3 = service.findByGuid("3", vCatalog);

		assertNotNull(linkedCategory4);
		assertNotNull(linkedCategory3);
		assertNotNull(orderingCategory3);
		assertNotNull(orderingCategory4);

		// check that the order has changed
		orderingCategory4 = linkedCategory4.getOrdering();
		orderingCategory3 = linkedCategory3.getOrdering();
		assertEquals("Incorrect ordering of category code 4.", orderingCategory4, 0);
		assertEquals("Incorrect ordering of category code 3.", orderingCategory3, 1);
	}	

	/**
	 * Tries to move category 3B up in virtual catalog 2.
	 * All categories are descendants of a linked category pointing to category (1).
	 * 
	 * <p>
	 * NOTE: This test fails from time to time as the implementation of the method depends on
	 * a certain order of the categories. This is a reported bug NTRN-1037.
	 *  
	 */
	@DirtiesDatabase
	@Test
	public void testMoveCategoryUp() {
		setupScenario();
		final Catalog vCatalog = this.virtualCatalog2;
		
		// create the root linked category (1B)
		linkedRootCategory = service.addLinkedCategory(rootCategory.getUidPk(), -1, vCatalog.getUidPk());
		
		Category linkedCategory4 = service.findByGuid("4", vCatalog);
		Category linkedCategory3 = service.findByGuid("3", vCatalog);

		// check if category4 is after category3
		int orderringCategory4 = linkedCategory4.getOrdering();
		int orderingCategory3 = linkedCategory3.getOrdering();
		assertTrue(orderringCategory4 >= orderingCategory3);

		service.updateCategoryOrderUp(linkedCategory3);

		linkedCategory4 = service.findByGuid("4", vCatalog);
		linkedCategory3 = service.findByGuid("3", vCatalog);

		assertNotNull(linkedCategory4);
		assertNotNull(linkedCategory3);
		
		orderringCategory4 = linkedCategory4.getOrdering();
		orderingCategory3 = linkedCategory3.getOrdering();

		// check if category4 is after category3
		assertTrue(orderringCategory4 > orderingCategory3);
	}	

	/**
	 * Test that removing a category will fail with an exception when there are still products in the category.
	 */
	@DirtiesDatabase
	@Test(expected = EpCategoryNotEmptyException.class)
	public void testRemoveCategoryTreeFail() {

		Category cat = createNormalCategory();
		cat.setCode("CAT");
		cat = service.saveOrUpdate(cat);
		Product prod = persisterFactory.getCatalogTestPersister().createSimpleProduct(
				"TestProductType", Utils.uniqueCode("product"), masterCatalog,
				getTac().getPersistersFactory().
				getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS), cat);
		
		productService.saveOrUpdate(prod);
		prod = productService.load(prod.getUidPk());
		Category linkedCat = service.addLinkedCategory(cat.getUidPk(), -1, virtualCatalog.getUidPk());

		service.removeCategoryTree(cat.getUidPk());
	}

	/**
	 * Test that removing a category will pass if the category is empty.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveEmptyCategoryTree() {
		
		Category cat = createNormalCategory();
		cat.setCode("CAT");
		cat = service.saveOrUpdate(cat);
		Product prod = persisterFactory.getCatalogTestPersister().createSimpleProduct(
				"TestProductType", Utils.uniqueCode("product"), masterCatalog,
				getTac().getPersistersFactory().
				getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS), cat);
		
		productService.saveOrUpdate(prod);
		prod = productService.load(prod.getUidPk());
		Category linkedCat = service.addLinkedCategory(cat.getUidPk(), -1, virtualCatalog.getUidPk());
		
		List<Product> findByCategoryUid = productService.findByCategoryUid(cat.getUidPk());
		List <Long> uidList = new ArrayList <Long>();
		for (Product product : findByCategoryUid) {
			uidList.add(product.getUidPk());
		}
		productService.removeProductList(uidList);
		service.removeCategoryTree(cat.getUidPk());
	}

	/**
	 * Test that adding a sub-category invalidates the parent category from the cache.
	 */
	@DirtiesDatabase
	@Test
	public void testAddingSubcategoryInvalidatesParentCache() {
		Category rootCategory = createNormalCategory();
		rootCategory = service.saveOrUpdate(rootCategory);
		
		FetchGroupLoadTuner fetchGroupLoadTuner = getBeanFactory().getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		fetchGroupLoadTuner.addFetchGroup(FetchGroupConstants.CATEGORY_BASIC,
										  FetchGroupConstants.CATALOG_DEFAULTS,
										  FetchGroupConstants.INIFINITE_PARENT_CATEGORY_DEPTH,
										  FetchGroupConstants.CATEGORY_CHILD_LEVEL_1
		);

		final Category subCategory = getBeanFactory().getBean(ContextIdNames.CATEGORY);
		subCategory.setCatalog(rootCategory.getCatalog());
		subCategory.setParent(rootCategory);
		subCategory.setVirtual(rootCategory.isVirtual());
		subCategory.setHidden(true);
		subCategory.setCode("SUB1");
		subCategory.setDisplayName("SUB 1", Locale.ENGLISH);
		subCategory.setStartDate(new Date());
		subCategory.setCategoryType(rootCategory.getCategoryType());
		service.saveOrUpdate(subCategory);
		
		List<Category> rootCategories = service.listRootCategories(masterCatalog, false, fetchGroupLoadTuner);
		assertEquals("There should be 1 root category", 1, rootCategories.size());
		
		Category parentCategory = rootCategories.get(0);
		assertEquals("The parent category should have 1 child", 1, parentCategory.getChildren().size());
		
	}

	/** Test finding a maximum ordering value of root {@link Category}s. */
	@Test
	@DirtiesDatabase
	public void testFindMaxRootOrdering() {
		Catalog catalog = persisterFactory.getCatalogTestPersister().persistCatalog(Utils.uniqueCode("ordering"), true);
		CategoryType categoryType = persisterFactory.getCatalogTestPersister().persistCategoryType("name", "template", catalog);

		final int maxOrdering = 10551;
		Category category1 = persisterFactory.getCatalogTestPersister().persistCategory("one", catalog, categoryType, "runaway", "zk");
		category1.setOrdering(maxOrdering);
		service.saveOrUpdate(category1);

		Category category2 = persisterFactory.getCatalogTestPersister().persistCategory("two", catalog, categoryType, "name it", "it");
		category2.setOrdering(0);
		service.saveOrUpdate(category2);
		
		Category child1 = persisterFactory.getCatalogTestPersister().persistCategory("childone", catalog, categoryType, category1);
		final int childOrdering = 20000;
		child1.setOrdering(childOrdering);
		service.saveOrUpdate(child1);

		Category child2 = persisterFactory.getCatalogTestPersister().persistCategory("childtwo", catalog, categoryType, category1);
		child2.setOrdering(1);
		service.saveOrUpdate(child2);

		assertEquals("Unexpected ordering", maxOrdering, service.findMaxRootOrdering(catalog.getUidPk()));
		assertEquals("Zero expected for missing catalog", 0, service.findMaxRootOrdering(-1));
	}

	/** Test finding a minimum ordering value of root {@link Category}s. */
	@Test
	@DirtiesDatabase
	public void testFindMinRootOrdering() {
		Catalog catalog = persisterFactory.getCatalogTestPersister().persistCatalog(Utils.uniqueCode("ordering"), true);
		CategoryType categoryType = persisterFactory.getCatalogTestPersister().persistCategoryType("name", "template", catalog);

		final int minOrdering = -668;
		Category category1 = persisterFactory.getCatalogTestPersister().persistCategory("one", catalog, categoryType, "ping", "en_US");
		category1.setOrdering(minOrdering);
		service.saveOrUpdate(category1);

		Category category2 = persisterFactory.getCatalogTestPersister().persistCategory("two", catalog, categoryType, "pong", "fr");
		category2.setOrdering(0);
		service.saveOrUpdate(category2);

		Category child1 = persisterFactory.getCatalogTestPersister().persistCategory("childone", catalog, categoryType, category1);
		final int childOrdering = -900;
		child1.setOrdering(childOrdering);
		service.saveOrUpdate(child1);

		Category child2 = persisterFactory.getCatalogTestPersister().persistCategory("childtwo", catalog, categoryType, category1);
		child2.setOrdering(1);
		service.saveOrUpdate(child2);

		assertEquals("Unexpected ordering", minOrdering, service.findMinRootOrdering(catalog.getUidPk()));
		assertEquals("Zero expected for missing catalog", 0, service.findMinRootOrdering(-1));
	}

	/** Test finding a maximum value where the parent category exists. */
	@Test
	@DirtiesDatabase
	public void testFindMaxChildOrdering() {
		Catalog catalog = persisterFactory.getCatalogTestPersister().persistCatalog(Utils.uniqueCode("ordering"), true);
		CategoryType categoryType = persisterFactory.getCatalogTestPersister().persistCategoryType("name", "template", catalog);

		Category parent = persisterFactory.getCatalogTestPersister().persistCategory("parent", catalog, categoryType, "poke", "fr");
		final int maxOrdering = 667;
		Category category1 = persisterFactory.getCatalogTestPersister().persistCategory("one", catalog, categoryType, parent);
		category1.setOrdering(maxOrdering);
		service.saveOrUpdate(category1);

		Category category2 = persisterFactory.getCatalogTestPersister().persistCategory("two", catalog, categoryType, parent);
		category2.setOrdering(0);
		service.saveOrUpdate(category2);

		assertEquals("Unexpected ordering", maxOrdering, service.findMaxChildOrdering(parent));
	}

	/** Test finding a minimum value where the parent category exists. */
	@Test
	public void testFindMinChildOrderingCatalog() {
		Catalog catalog = persisterFactory.getCatalogTestPersister().persistCatalog(Utils.uniqueCode("ordering"), true);
		CategoryType categoryType = persisterFactory.getCatalogTestPersister().persistCategoryType("name", "template", catalog);

		Category parent = persisterFactory.getCatalogTestPersister().persistCategory("parent", catalog, categoryType, "name", "en");
		final int minOrdering = -6871;
		Category category1 = persisterFactory.getCatalogTestPersister().persistCategory("one", catalog, categoryType, parent);
		category1.setOrdering(minOrdering);
		service.saveOrUpdate(category1);

		Category category2 = persisterFactory.getCatalogTestPersister().persistCategory("two", catalog, categoryType, parent);
		category2.setOrdering(0);
		service.saveOrUpdate(category2);

		assertEquals("Unexpected ordering", minOrdering, service.findMinChildOrdering(parent));
	}
}
