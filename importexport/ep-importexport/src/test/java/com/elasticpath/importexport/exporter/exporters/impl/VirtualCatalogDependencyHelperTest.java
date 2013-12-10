package com.elasticpath.importexport.exporter.exporters.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;

/**
 * Tests building dependencies process performed by <code>VirtualCatalogDependencyHelper</code>.
 */
public class VirtualCatalogDependencyHelperTest {

	private VirtualCatalogDependencyHelper dependencyHelper;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private Catalog catalog;
	private Category category;
	private CategoryType categoryType;

	private DependencyRegistry dependencyRegistry;

	/**
	 * Prepares meta objects.
	 */
	@Before
	public void setUp() {
		dependencyHelper = new VirtualCatalogDependencyHelper();

		catalog = context.mock(Catalog.class);
		category = context.mock(Category.class);
		categoryType = context.mock(CategoryType.class);

		dependencyRegistry = new DependencyRegistry(Arrays.<Class<?>>asList(Catalog.class, Category.class, CategoryType.class, Object.class));
	}

	/**
	 * Checks that guard prevents further execution in case Master Category is contained in master catalog.
	 */
	@Test
	public void testAddInfluencingWhenMasterCatalog() {
		context.checking(new Expectations() {
			{
				allowing(category).getCatalog();
				will(returnValue(catalog));

				oneOf(catalog).isMaster();
				will(returnValue(true));
			}
		});
		dependencyHelper.addInfluencingCatalogs(category, null);
	}

	/**
	 * Check that master catalog containing category type is added into dependency registry when category is contained in virtual catalog.
	 */
	@Test
	public void testAddInfluencingWhenVirtualCatalog() {
		context.checking(new Expectations() {
			{
				allowing(category).getCatalog();
				will(returnValue(catalog));

				oneOf(catalog).isMaster();
				will(returnValue(false));
				oneOf(category).getCategoryType();
				will(returnValue(categoryType));
			}
		});

		final Long masterCatalogUidToPut = 1234L;
		final Catalog mockMasterCatalog = context.mock(Catalog.class, "master catalog");
		context.checking(new Expectations() {
			{
				oneOf(categoryType).getCatalog();
				will(returnValue(mockMasterCatalog));
				oneOf(mockMasterCatalog).getUidPk();
				will(returnValue(masterCatalogUidToPut));
			}
		});

		dependencyHelper.addInfluencingCatalogs(category, dependencyRegistry);
		final Set<Long> influencingCatalogs = dependencyRegistry.getDependentUids(Catalog.class);
		assertEquals(1, influencingCatalogs.size());
		assertTrue(influencingCatalogs.contains(masterCatalogUidToPut));
	}
}
