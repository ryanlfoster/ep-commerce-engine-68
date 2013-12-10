package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.impl.CategoryImpl;

/**
 * The unit test class for category change set dependency resolver.
 */
public class CategoryChangeSetDependencyResolverImplTest {
	
	private final CategoryChangeSetDependencyResolverImpl resolver = new CategoryChangeSetDependencyResolverImpl();

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Test getting change set dependency for category.
	 */
	@Test
	public void testGetChangeSetDependency() {
		Object obj = new Object();
		Set< ? >  dependencies = resolver.getChangeSetDependency(obj);
		assertTrue("Non-Category object should not be processed", dependencies.isEmpty());
		
		final Category parentCategory = new CategoryImpl();
		Category category = new CategoryImpl() {
			private static final long serialVersionUID = 925139444391087544L;

			public Category getParent() {
				return parentCategory;
			}
		};
		dependencies = resolver.getChangeSetDependency(category);
		assertEquals("parent category is not found in dependency list", parentCategory, dependencies.iterator().next());
		
		final Category masterCategory = new CategoryImpl();
		category = new CategoryImpl() {
			private static final long serialVersionUID = 293738477416763576L;

			public boolean isLinked() {
				return true;
			}
			public Category getMasterCategory() {
				return masterCategory;
			}
		};
		dependencies = resolver.getChangeSetDependency(category);
		assertEquals("linked category is not found in dependency list", masterCategory, dependencies.iterator().next());
		
	}
	
	/**
	 * Test dependency on category type.
	 */
	@Test
	public void testDependencyOnCategoryType() {

		Category category = new CategoryImpl();
		CategoryType catType = context.mock(CategoryType.class);
		category.setCategoryType(catType);
		
		Set< ? >  dependencies = resolver.getChangeSetDependency(category);
		assertEquals("category type is not found in dependency list", catType , dependencies.iterator().next());
		
		
	}
	

}
