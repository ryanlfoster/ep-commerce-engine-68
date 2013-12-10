package com.elasticpath.tools.sync.job.custom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.LinkedCategoryImpl;


/**
 * Test class for CategorySortingPolicy.
 * 
 * Expected behaviour:
 * <ol>
 *	 <li>Master categories comes before linked categories</li>
 * 	 <li>Parent categories come before their descendants</li>
 *   <li>Peers order is arbitrary, to aid testing we'll sort them according to code</li>
 *   <li>Categories which have been deleted and so do not have their associated object 
 *   	 available are sorted according to code</li>
 * </ol>
 */
public class CategorySortingPolicyTest {

	private static final String ALPHABETICAL_AT_SAME_LEVEL = "Alphabetical at same level";
	private static final String PARENTS_BEFORE_CHILDREN = "Parents before children";
	private static final String ANCESTOR_SHOULD_COME_FIRST = "Ancestor should come first";
	private final CategorySortingPolicy categorySortingPolicy = new CategorySortingPolicy();
	private final Catalog catalog = new CatalogImpl();
	
	/**
	 * Init.
	 */
	@Before
	public void setUp() {
		catalog.setCode("catalog");
	}
	
	/**
	 * Ensure equal categories return 0. 
	 */
	@Test
	public void testCompareEqualCategoriesReturn0() {

		Category unlinkedCategory = createCategory(catalog, "unlinked");
		Category linkedCategory = new LinkedCategoryImpl();
		
		linkedCategory.setMasterCategory(unlinkedCategory);

		assertEquals("Two equal categories returns 0", 0, categorySortingPolicy.compare(linkedCategory, linkedCategory));
		assertEquals("Two equal categories returns 0", 0, categorySortingPolicy.compare(unlinkedCategory, unlinkedCategory));
	}


	/**
	 * Ensure master categories come before linked categories.
	 */
	@Test
	public void testMasterComesBeforeLinked() {

		Category unlinkedCategory = createCategory(catalog, "unlinked");
		Category linkedCategory = new LinkedCategoryImpl();
		
		linkedCategory.setMasterCategory(unlinkedCategory);

		assertEquals("Master should come first", 1, categorySortingPolicy.compare(linkedCategory, unlinkedCategory));
		assertEquals("Master should come first", -1, categorySortingPolicy.compare(unlinkedCategory, linkedCategory));
	}	
	
	/**
	 * Two categories which are either siblings or unrelated (i.e. they aren't each other's ancestors/descendants) can have
	 * arbitrary order (doesn't matter which comes first) so long as it is consistent (the same one must always come first).
	 */
	@Test
	public void testArbitraryIsConsistent() {
		Category unrelatedNotEqual1 = createCategory(catalog, "unrelated1");
		Category unrelatedNotEqual2 = createCategory(catalog, "unrelated2");
		int compare1 = categorySortingPolicy.compare(unrelatedNotEqual1, unrelatedNotEqual2);
		int compare2 = categorySortingPolicy.compare(unrelatedNotEqual2, unrelatedNotEqual1);
		assertEquals("Compare of same elements in reversed order should produce reversed result.", compare1, -compare2);
	}
	
	/**
	 * Ensure ancestors always come before descendants.
	 */
	@Test
	public void testCompareParentWithDescendant() {
		
		Category parent = createCategory(catalog, "parent");
		Category child = createCategory(catalog, "child", parent);
		Category grandchild = createCategory(catalog, "grandchild", child);
		Category greatgrandchild = createCategory(catalog, "grandgrandchild", grandchild);

		assertTrue(ANCESTOR_SHOULD_COME_FIRST, categorySortingPolicy.compare(parent, child) < 0);
		assertTrue(ANCESTOR_SHOULD_COME_FIRST, categorySortingPolicy.compare(child, parent) > 0);

		assertTrue(ANCESTOR_SHOULD_COME_FIRST, categorySortingPolicy.compare(parent, grandchild) < 0);
		assertTrue(ANCESTOR_SHOULD_COME_FIRST, categorySortingPolicy.compare(grandchild, parent) > 0);

		assertTrue(ANCESTOR_SHOULD_COME_FIRST, categorySortingPolicy.compare(parent, greatgrandchild) < 0);
		assertTrue(ANCESTOR_SHOULD_COME_FIRST, categorySortingPolicy.compare(greatgrandchild, parent) > 0);

	}
	
	/**
	 * Make sure all parents and children have the correct relationship based on their depth. 
	 */
	@Test
	public void testCompareByDepth() {
		
		// parent < child < child2
		Category parent = createCategory(catalog, "parent");
		Category child = createCategory(catalog, "child", parent);
		Category child2 = createCategory(catalog, "child2", parent);
		
		//danotherParent < anotherChild
		Category danotherParent = createCategory(catalog, "danotherParent");
		Category anotherChild = createCategory(catalog, "anotherChild", danotherParent);
		
		assertTrue(PARENTS_BEFORE_CHILDREN, categorySortingPolicy.compare(danotherParent, child) < 0);
		assertTrue(ALPHABETICAL_AT_SAME_LEVEL, categorySortingPolicy.compare(anotherChild, child) < 0);
		assertTrue(ALPHABETICAL_AT_SAME_LEVEL, categorySortingPolicy.compare(anotherChild, child) < 0);
		assertTrue(PARENTS_BEFORE_CHILDREN, categorySortingPolicy.compare(danotherParent, anotherChild) < 0);
		assertTrue(PARENTS_BEFORE_CHILDREN, categorySortingPolicy.compare(parent, anotherChild) < 0);
		assertTrue(PARENTS_BEFORE_CHILDREN, categorySortingPolicy.compare(parent, child) < 0);
		assertTrue(PARENTS_BEFORE_CHILDREN, categorySortingPolicy.compare(parent, child2) < 0);
	}
	
	
	/**
	 * Verify that the comparator is transitive by going through a set of categories and ensuring that
	 * if a < b < c then a < c.
	 */
	@Test
	public void testCompareIsTransitive() {
		Category parent = createCategory(catalog, "parent");
		Category child = createCategory(catalog, "child", parent);
		Category child2 = createCategory(catalog, "child2", parent);
		Category grandchild = createCategory(catalog, "grandchild", child);
		Category greatgrandchild = createCategory(catalog, "grandgrandchild", grandchild);
		
		//danotherParent < anotherChild
		Category danotherParent = createCategory(catalog, "danotherParent");
		Category anotherChild = createCategory(catalog, "anotherChild", danotherParent);
		
		Category[] categories = {parent, child, child2, danotherParent, anotherChild, grandchild, greatgrandchild};
		for (Category category1 : categories) {
			for (Category category2 : categories) {
				for (Category category3 : categories) {
					if (categorySortingPolicy.compare(category1, category2) < 0 && categorySortingPolicy.compare(category2, category3) < 0) {
						assertTrue(categorySortingPolicy.compare(category1, category3) < 0);
					}
				}
			}
		}
	}

	
	private Category createCategory(final Catalog catalog, final String code) {
		Category category = new CategoryImpl();
		category.setCatalog(catalog);
		category.setCode(code);
		return category;
	}

	private Category createCategory(final Catalog catalog, final String code, final Category parent) {
		Category category = createCategory(catalog, code);
		category.setParent(parent);
		return category;
	}
	
}
