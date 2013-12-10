package com.elasticpath.integration.sorting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.LinkedCategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.tools.sync.job.Command;
import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.SortingPolicy;
import com.elasticpath.tools.sync.job.custom.CategorySortingPolicy;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.job.impl.DomainSorterImpl;
import com.elasticpath.tools.sync.job.impl.GlobalEpDependencyDescriptorImpl;
import com.elasticpath.tools.sync.job.impl.JobEntryImpl;

/**
 * Test job entry sorting is done according to configuration.
 * This ensures that sync job entities will make it into the 
 * destination system if entities they are dependent on are 
 * in the same job.
 * 
 * We are testing updating and removing entities and verifying the sorting 
 * of job entries into the expected order. Expected orders are specified 
 * as arrays in setup, which are then fed repeatedly in randomised order
 * to the sorting code and checked against the expected order.
 * 
 * The randomisation is intended to make sure our tests aren't dependent on
 * a special case of input ordering.
 * 
 * Master catalogs are currently not supported in change sets/DST so we don't 
 * test sorting both master catalogs and virtual catalogs.
 */
public class JobEntrySortingTest {

	private static final int RANDOM_PERUMTATIONS = 500;

	private final DomainSorterImpl sorter = new DomainSorterImpl();

	private Map<String, Category> guidToCategoryMap;

	private Catalog masterCatalog;

	private Catalog virtualCatalog;

	private Category cat1;
	private Category cat1v1;
	private Category cat1v1v1;
	private Category cat1v2;
	private Category cat1v3;

	private Category lnkCat1;
	private Category lnkCat1v1;
	private Category lnkCat1v1v1;
	private Category lnkCat1v2;
	private Category lnkCat1v3;

	private Product product; 
	
	/**
	 * Initialisation.
	 */
	@Before
	public void setUp() {
		guidToCategoryMap = new HashMap<String, Category>();
		
		masterCatalog = new CatalogImpl();
		masterCatalog.setMaster(true);
		masterCatalog.setCode("mastercatalog");
		
		virtualCatalog = new CatalogImpl();
		virtualCatalog.setMaster(false);
		virtualCatalog.setCode("virtualcatalog");

		cat1 = createCategory(masterCatalog, "cat_1");
		cat1v1 = createCategory(masterCatalog, "cat_1_1", cat1);
		cat1v1v1 = createCategory(masterCatalog, "cat_1_1_1", cat1v1);
		cat1v2 = createCategory(masterCatalog, "cat_1_2", cat1);
		cat1v3 = createCategory(masterCatalog, "cat_1_3", cat1);
		
		lnkCat1 = createLinkedCategory(cat1, virtualCatalog);
		lnkCat1v1 = createLinkedCategory(cat1v1, virtualCatalog, lnkCat1);
		lnkCat1v1v1 = createLinkedCategory(cat1v1v1, virtualCatalog, lnkCat1v1);
		lnkCat1v2 = createLinkedCategory(cat1v2, virtualCatalog, lnkCat1);
		lnkCat1v3 = createLinkedCategory(cat1v3, virtualCatalog, lnkCat1);
		
		product = new ProductImpl();
		
		GlobalEpDependencyDescriptorImpl globalEpDependencyDescriptor = new GlobalEpDependencyDescriptorImpl();
		Map<Class<?>, Integer> domainClassOrdering = new HashMap<Class<?>, Integer>();
		domainClassOrdering.put(Catalog.class, 0);
		domainClassOrdering.put(Category.class, 1);
		domainClassOrdering.put(Product.class, 2);
		globalEpDependencyDescriptor.setDomainClassOrdering(domainClassOrdering);

		CategorySortingPolicy categorySortingPolicy = new TestCategorySortingPolicyImpl(guidToCategoryMap);
		Map<Class< ? >, SortingPolicy> customSortingPolicy = new HashMap<Class< ? >, SortingPolicy>();
		customSortingPolicy.put(Category.class, categorySortingPolicy);
		sorter.setCustomSortingPolicy(customSortingPolicy);
		
		sorter.setGlobalEpDependencyDescriptor(globalEpDependencyDescriptor);
	}
	
	/**
	 * Test with simple setup, updates only.
	 */
	@Test
	public void testExpectedOrderForSimpleUpdateJobs() {
		JobEntry [] job = new JobEntry [] {
				createEntry(Command.UPDATE, masterCatalog, Catalog.class),
				createEntry(Command.UPDATE, cat1, Category.class),
				createEntry(Command.UPDATE, product, Product.class),
		};

		assertConsistentSortOrderWithRandomizedInputOrder(job);
	}
	
	/**
	 * Test with simple setup, removes only.
	 */
	@Test
	public void testExpectedOrderForSimpleRemoveJobs() {
		JobEntry [] job = new JobEntry [] {
				createEntry(Command.REMOVE, product, Product.class),
				createEntry(Command.REMOVE, cat1, Category.class),
				createEntry(Command.REMOVE, masterCatalog, Catalog.class),
		};

		assertConsistentSortOrderWithRandomizedInputOrder(job);
	}
	
	/**
	 * Simple test with updates and removes. Updates should always
	 * come before removes.
	 */
	@Test
	public void testExpectedOrderForSimpleMixedCommandJobs() {
		JobEntry [] job = new JobEntry [] {
				createEntry(Command.UPDATE, cat1, Category.class),
				createEntry(Command.REMOVE, product, Product.class),
				createEntry(Command.REMOVE, masterCatalog, Catalog.class),
		};
		
		assertConsistentSortOrderWithRandomizedInputOrder(job);
	}

	/**
	 * Test with several categories, catalog, product. Catalog before category, 
	 * parent category before children, products last. Categories which are not
	 * each others ancestors/descendants are sorted alphabetically. 
	 */
	@Test
	public void testExpectedOrderForJobsWithLotsOfCategories() {
		JobEntry [] job = new JobEntry [] {
				createEntry(Command.UPDATE, masterCatalog, Catalog.class),
				createEntry(Command.UPDATE, cat1, Category.class),
				createEntry(Command.UPDATE, cat1v1, Category.class),
				createEntry(Command.UPDATE, cat1v2, Category.class),
				createEntry(Command.UPDATE, cat1v1v1, Category.class),
				createEntry(Command.UPDATE, product, Product.class),
		};
		
		assertConsistentSortOrderWithRandomizedInputOrder(job);
	}
	
	/**
	 * Test removes with multiple hierarchical categories, product, master catalog.
	 * Should come in reverse order of UPDATE test.
	 */
	@Test
	public void testExpectedOrderForRemoveJobsWithLotsOfCategories() {
		JobEntry [] job = new JobEntry [] {
				createEntry(Command.REMOVE, product, Product.class),
				createEntry(Command.REMOVE, cat1v1v1, Category.class),
				createEntry(Command.REMOVE, cat1v2, Category.class),
				createEntry(Command.REMOVE, cat1v1, Category.class),
				createEntry(Command.REMOVE, cat1, Category.class),
				createEntry(Command.REMOVE, masterCatalog, Catalog.class),
		};
		
		assertConsistentSortOrderWithRandomizedInputOrder(job);
	}

	/**
	 * Test with just doing updates on linked categories.
	 */
	@Test
	public void testExpectedOrderUpdateLinkedCategories() {
		JobEntry [] job = new JobEntry [] {
				createEntry(Command.UPDATE, lnkCat1, Category.class),	
				createEntry(Command.UPDATE, lnkCat1v1, Category.class),	
				createEntry(Command.UPDATE, lnkCat1v2, Category.class),
				createEntry(Command.UPDATE, lnkCat1v3, Category.class),	
				createEntry(Command.UPDATE, lnkCat1v1v1, Category.class)	
		};
		
		assertConsistentSortOrderWithRandomizedInputOrder(job);
	}

	/**
	 * Test doing updates on a mixture of virtual and regular categories, with
	 * a virtual catalog and a product. Regular categories should come before 
	 * virtual.
	 */
	@Test
	public void testExpectedOrderUpdateRegularAndLinkedCategories() {
		JobEntry [] job = new JobEntry [] {
				createEntry(Command.UPDATE, virtualCatalog, Catalog.class),
				createEntry(Command.UPDATE, cat1, Category.class),
				createEntry(Command.UPDATE, cat1v1, Category.class),
				createEntry(Command.UPDATE, cat1v2, Category.class),
				createEntry(Command.UPDATE, cat1v1v1, Category.class),
				createEntry(Command.UPDATE, lnkCat1, Category.class),	
				createEntry(Command.UPDATE, lnkCat1v1, Category.class),	
				createEntry(Command.UPDATE, lnkCat1v2, Category.class),
				createEntry(Command.UPDATE, lnkCat1v3, Category.class),	
				createEntry(Command.UPDATE, lnkCat1v1v1, Category.class),	
				createEntry(Command.UPDATE, product, Product.class)
		};
		
		assertConsistentSortOrderWithRandomizedInputOrder(job);
	}
	
	/**
	 * Test doing updates on a mixture of virtual and regular categories, with
	 * a virtual catalog and a product. Regular categories should come before 
	 * virtual.
	 */
	@Test
	public void testExpectedOrderRemoveRegularAndLinkedCategories() {
		JobEntry [] job = new JobEntry [] {
				createEntry(Command.REMOVE, product, Product.class),
				createEntry(Command.REMOVE, lnkCat1v1v1, Category.class),	
				createEntry(Command.REMOVE, lnkCat1v3, Category.class),	
				createEntry(Command.REMOVE, lnkCat1v2, Category.class),
				createEntry(Command.REMOVE, lnkCat1v1, Category.class),	
				createEntry(Command.REMOVE, lnkCat1, Category.class),	
				createEntry(Command.REMOVE, cat1v1v1, Category.class),
				createEntry(Command.REMOVE, cat1v2, Category.class),
				createEntry(Command.REMOVE, cat1v1, Category.class),
				createEntry(Command.REMOVE, cat1, Category.class),
				createEntry(Command.REMOVE, virtualCatalog, Catalog.class)
		};
		
		assertConsistentSortOrderWithRandomizedInputOrder(job);
	}

	/**
	 * Test doing updates on a mixture of virtual and regular categories, with
	 * a virtual catalog and a product. Regular categories should come before 
	 * virtual.
	 */
	@Test
	public void testExpectedOrderMixedCommandsRegularAndLinkedCategories() {
		JobEntry [] job = new JobEntry [] {
				createEntry(Command.UPDATE, virtualCatalog, Catalog.class),
				createEntry(Command.UPDATE, cat1, Category.class),
				createEntry(Command.UPDATE, cat1v1, Category.class),
				createEntry(Command.UPDATE, cat1v2, Category.class),
				createEntry(Command.UPDATE, lnkCat1, Category.class),	
				createEntry(Command.UPDATE, lnkCat1v1, Category.class),	
				createEntry(Command.UPDATE, lnkCat1v3, Category.class),	
				createEntry(Command.REMOVE, product, Product.class),
				createEntry(Command.REMOVE, lnkCat1v1v1, Category.class),	
				createEntry(Command.REMOVE, lnkCat1v2, Category.class),
				createEntry(Command.REMOVE, cat1v1v1, Category.class),
		};
		
		assertConsistentSortOrderWithRandomizedInputOrder(job);
	}
	
	/**
	 * Test doing removes when we don't have access to the category objects any more 
	 * because they are deleted already. Expect such objects to appear with the removes
	 * in alphabetical order.
	 */
	@Test 
	public void testExpectedOrderWithRemovesWithCategoriesGone() {
		JobEntry [] job = new JobEntry [] {
				createEntry(Command.UPDATE, virtualCatalog, Catalog.class),
				createEntry(Command.UPDATE, cat1, Category.class),
				createEntry(Command.UPDATE, cat1v1, Category.class),
				createEntry(Command.UPDATE, cat1v2, Category.class),
				createEntry(Command.UPDATE, lnkCat1, Category.class),	
				createEntry(Command.UPDATE, lnkCat1v1, Category.class),	
				createEntry(Command.UPDATE, lnkCat1v3, Category.class),	
				createEntry(Command.REMOVE, product, Product.class),
				createEntry(Command.REMOVE, cat1v1v1, Category.class),
				createEntry(Command.REMOVE, lnkCat1v1v1, Category.class),	
				createEntry(Command.REMOVE, lnkCat1v2, Category.class),
		};
		
		guidToCategoryMap.remove(getEntryGuidFromEntity(lnkCat1v2));
		guidToCategoryMap.remove(getEntryGuidFromEntity(lnkCat1v1v1));
		guidToCategoryMap.remove(getEntryGuidFromEntity(cat1v1v1));
		
		assertConsistentSortOrderWithRandomizedInputOrder(job);
		
	}
	
	
	private Category createLinkedCategory(final Category masterCategory, final Catalog virtualCatalog) {
		LinkedCategoryImpl linkedCategory = new LinkedCategoryImpl();
		linkedCategory.setMasterCategory(masterCategory);
		linkedCategory.setCatalog(virtualCatalog);
		guidToCategoryMap.put(getEntryGuidFromEntity(linkedCategory), linkedCategory);
		return linkedCategory;
	}
	
	private Category createLinkedCategory(final Category masterCategory, final Catalog virtualCatalog, final Category parent) {
		Category linkedCategory = createLinkedCategory(masterCategory, virtualCatalog);
		linkedCategory.setParent(parent);
		return linkedCategory;
		
	}
	
	private Category createCategory(final Catalog catalog, final String code) {
		Category category = new CategoryImpl();
		category.setCatalog(catalog);
		category.setCode(code);
		guidToCategoryMap.put(getEntryGuidFromEntity(category), category);
		return category;
	}

	private Category createCategory(final Catalog catalog, final String code, final Category parent) {
		Category category = createCategory(catalog, code);
		category.setParent(parent);
		return category;
	}	
	
	
	private JobEntryImpl createEntry(final Command command, final Entity entity, final Class< ? > type) {
		JobEntryImpl jobEntry = new JobEntryImpl();
		jobEntry.setCommand(command);
		jobEntry.setGuid(getEntryGuidFromEntity(entity));
		jobEntry.setType(type);
		return jobEntry;
	}

	private String getEntryGuidFromEntity(final Entity entity) {
		if (Category.class.isAssignableFrom(entity.getClass())) {
			return entity.getGuid() + "|" + ((Category) entity).getCatalog().getGuid();
		}
		return entity.getGuid();
	}


	private void assertConsistentSortOrderWithRandomizedInputOrder(final JobEntry[] expectedOrder) {
		for (int x = 0; x < RANDOM_PERUMTATIONS; x++) {
			List<JobEntry> randomizedEntries = new ArrayList<JobEntry>(Arrays.asList(expectedOrder)); 
			Collections.shuffle(randomizedEntries);
			
			assertSortOrder(expectedOrder, randomizedEntries.toArray(new JobEntry [] {}));
		}
	}
	
	private void assertSortOrder(final JobEntry [] expected, final JobEntry ... jobEntries) {
		List<JobEntry> epDomainObjects = Arrays.asList(jobEntries);
	
		sorter.sort(epDomainObjects);

		assertEquals("Number of expected items differs from sorted list", expected.length, epDomainObjects.size());
		for (int x = 0; x < expected.length; x++) {
			assertSame("Object not in expected order at expected[" + x + "]", expected[x], epDomainObjects.get(x));
		}
	}	
	
	/**
	 * Stubs out the category lookup code for testing.
	 */
	private class TestCategorySortingPolicyImpl extends CategorySortingPolicy {
		
		private final Map<String, Category> typeToCategoryLookupMap;
		
		/**
		 * Constructor to initialise the map with any category objects to be used.
		 *
		 * @param map from category GUID to category object
		 */
		public TestCategorySortingPolicyImpl(final Map<String, Category> map) {
			typeToCategoryLookupMap = map;
		}
		
		@Override
		protected Category getCategory(final TransactionJobDescriptorEntry entry) {
			return typeToCategoryLookupMap.get(entry.getGuid());
		}
	}
}
