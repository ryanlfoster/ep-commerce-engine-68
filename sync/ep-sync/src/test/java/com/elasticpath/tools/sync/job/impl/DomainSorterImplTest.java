package com.elasticpath.tools.sync.job.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.tools.sync.job.Command;
import com.elasticpath.tools.sync.job.JobEntry;

/**
 * Tests that <code>DomainSorterImpl</code> performs proper sorting of <code>JobEntry</code> list.
 */
public class DomainSorterImplTest {

	private DomainSorterImpl domainSorter;

	private GlobalEpDependencyDescriptorImpl globalEpDependencyDescriptor;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		domainSorter = new DomainSorterImpl();

		globalEpDependencyDescriptor = new GlobalEpDependencyDescriptorImpl();
		Map<Class<?>, Integer> domainClassOrdering = new HashMap<Class<?>, Integer>();
		domainClassOrdering.put(Catalog.class, 0);
		domainClassOrdering.put(Category.class, 1);
		domainClassOrdering.put(Product.class, 2);
		globalEpDependencyDescriptor.setDomainClassOrdering(domainClassOrdering);

		domainSorter.setGlobalEpDependencyDescriptor(globalEpDependencyDescriptor);
	}

	/**
	 * Tests sorting by command type.
	 */
	@Test
	public void testUpdateBeforeRemove() {
		final JobEntry updateEntry = new JobEntryImpl();
		updateEntry.setGuid("Doesn't matter");
		updateEntry.setType(Product.class);
		updateEntry.setCommand(Command.UPDATE);

		final JobEntry removeEntry = new JobEntryImpl();
		removeEntry.setGuid("Doesn't matter too");
		removeEntry.setType(Product.class);
		removeEntry.setCommand(Command.REMOVE);

		assertSortOrder(new JobEntry[] {updateEntry, removeEntry}, updateEntry, removeEntry);
	}

	/**
	 * Category should be removed before Catalog.
	 */
	@Test
	public void testCategoryShouldBeRemovedBeforeCatalog() {
		final JobEntry removeCatalogEntry = new JobEntryImpl();
		removeCatalogEntry.setGuid("Doesn't matter");
		removeCatalogEntry.setType(Catalog.class);
		removeCatalogEntry.setCommand(Command.REMOVE);

		final JobEntry removeCategoryEntry = new JobEntryImpl();
		removeCategoryEntry.setGuid("Doesn't matter too");
		removeCategoryEntry.setType(Category.class);
		removeCategoryEntry.setCommand(Command.REMOVE);


		assertSortOrder(new JobEntry [] {removeCategoryEntry, removeCatalogEntry}, removeCatalogEntry, removeCategoryEntry);
		assertSortOrder(new JobEntry [] {removeCategoryEntry, removeCatalogEntry}, removeCategoryEntry, removeCatalogEntry);
	}

	/**
	 * Catalog should be added/updated before Category.
	 */
	@Test
	public void testCatalogShouldBeAddedBeforeCategory() {
		final JobEntry updateCategoryEntry = new JobEntryImpl();
		updateCategoryEntry.setGuid("Doesn't matter");
		updateCategoryEntry.setType(Category.class);
		updateCategoryEntry.setCommand(Command.UPDATE);

		final JobEntry updateCatalogEntry = new JobEntryImpl();
		updateCatalogEntry.setGuid("Doesn't matter too");
		updateCatalogEntry.setType(Catalog.class);
		updateCatalogEntry.setCommand(Command.UPDATE);

		assertSortOrder(new JobEntry [] {updateCatalogEntry, updateCategoryEntry}, updateCategoryEntry, updateCatalogEntry);
		assertSortOrder(new JobEntry [] {updateCatalogEntry, updateCategoryEntry}, updateCatalogEntry, updateCategoryEntry);
	}

	private void assertSortOrder(final JobEntry [] expected, final JobEntry ... jobEntries) {
		List<JobEntry> epDomainObjects = Arrays.asList(jobEntries);

		domainSorter.sort(epDomainObjects);

		assertEquals("Number of expected items differs from sorted list", expected.length, epDomainObjects.size());
		for (int x = 0; x < expected.length; x++) {
			assertSame("Object not in expected order at expected[" + x + "]", expected[x], epDomainObjects.get(x));
		}
	}

}
