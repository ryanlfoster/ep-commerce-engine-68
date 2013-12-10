package com.elasticpath.tools.sync.merge.impl; // NOPMD

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.elasticpath.domain.attribute.impl.AbstractAttributeValueImpl;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueImpl;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * Tests for SimpleMergeFilter class.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class SimpleMergeFilterTest {

	/**
	 * Tests <code>containsInHierarchy</code> method.
	 */
	@Test
	public void testContainsInHierarchy() {
		SimpleMergeFilter filter = new SimpleMergeFilter();
		assertFalse(filter.containsInHierarchy(ProductImpl.class, CategoryImpl.class));
		assertFalse(filter.containsInHierarchy(ProductImpl.class, AbstractEntityImpl.class));

		assertTrue(filter.containsInHierarchy(AbstractAttributeValueImpl.class, ProductAttributeValueImpl.class));
		assertTrue(filter.containsInHierarchy(AbstractEntityImpl.class, ProductImpl.class));
	}

	/**
	 * Tests <code>getConfigurationClass</code>.
	 */
	@Test
	public void testGetConfigurationClass() {
		SimpleMergeFilter filter = new SimpleMergeFilter() {
			@Override
			boolean containsInHierarchy(final Class< ? > classToCompare, final Class< ? > configClass) {
				return configClass.equals(ProductImpl.class);
			}
		};

		final Set<Class<?>> configClasses = new HashSet<Class<?>>(Arrays.<Class<?>> asList(CategoryImpl.class, ProductImpl.class));
		assertEquals(ProductImpl.class, filter.getConfigurationClass(ProductImpl.class, configClasses));

		assertNull(filter.getConfigurationClass(CatalogImpl.class, new HashSet<Class< ? >>()));
	}

	/**
	 * Tests <code>getMethodsSet</code>.
	 */
	@Test
	public void testGetMethodsSet() {
		SimpleMergeFilter filter = new SimpleMergeFilter() {
			@Override
			Class< ? > getConfigurationClass(final Class< ? > clazz, final Set<Class< ? >> configClasses) {
				return ProductImpl.class;
			}
		};

		Map<Class< ? >, Set<String>> methodsMap = new HashMap<Class< ? >, Set<String>>();
		Set<String> testSet = new HashSet<String>();
		methodsMap.put(ProductImpl.class, testSet);
		methodsMap.put(CategoryImpl.class, Collections.<String> emptySet());

		assertSame(testSet, filter.getMethodsSet(ProductImpl.class, methodsMap));
		assertSame(Collections.emptySet(), filter.getMethodsSet(ProductImpl.class, new HashMap<Class< ? >, Set<String>>()));
	}

	/**
	 * Tests <code>isMergePermitted</code>.
	 */
	@Test
	public void testIsMergePermitted() {
		SimpleMergeFilter filter = new SimpleMergeFilter() {
			@Override
			Set<String> getMethodsSet(final Class< ? > clazz, final Map<Class< ? >, Set<String>> methods) {
				Set<String> resultSet = new HashSet<String>();
				if (clazz.equals(ProductImpl.class)) {
					resultSet.add("getBrand");
				} else if (clazz.equals(CategoryImpl.class)) {
					resultSet.add("getCategoryType");
				}
				return resultSet;
			}
		};

		filter.setMergeAll(false);
		assertFalse(filter.isMergePermitted(ProductImpl.class, "getProductCode"));
		assertTrue(filter.isMergePermitted(ProductImpl.class, "getBrand"));
		assertTrue(filter.isMergePermitted(CategoryImpl.class, "getCategoryType"));
		assertFalse(filter.isMergePermitted(CategoryImpl.class, "getCategoryCode"));
		assertFalse(filter.isMergePermitted(CatalogImpl.class, "getCatalogCode"));

		filter.setMergeAll(true);
		assertTrue(filter.isMergePermitted(ProductImpl.class, "getProductCode"));
		assertFalse(filter.isMergePermitted(ProductImpl.class, "getBrand"));
		assertFalse(filter.isMergePermitted(CategoryImpl.class, "getCategoryType"));
		assertTrue(filter.isMergePermitted(CategoryImpl.class, "getCategoryCode"));
		assertTrue(filter.isMergePermitted(CatalogImpl.class, "getCatalogCode"));

	}
}
