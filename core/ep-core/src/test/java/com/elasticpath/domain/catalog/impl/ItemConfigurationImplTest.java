package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.ItemConfiguration;
import com.elasticpath.service.catalog.ItemConfigurationBuilder;
import com.elasticpath.service.catalog.ItemConfigurationValidationResult;
import com.elasticpath.service.catalog.ItemConfigurationValidator;


/**
 * Tests {@link ItemConfigurationImpl}.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class ItemConfigurationImplTest {

	private static final String INVALID_PATH = "invalid path";
	private static final String INVALID_PATH_RETURNS_NULL = "getting a child with an invalid path should return null";
	private static final String SKU1A = "SKU1A";
	private static final String SKU1 = "SKU1";
	private static final String SKU2 = "SKU2";
	private static final String PARENTSKU1 = "PARENTSKU1";
	private static final String SKU3 = "SKU3";
	private static final String SKU4 = "SKU4";

	private static final String PARENTSKU2 = "PARENTSKU2";
	private static final String PARENT1 = "parent1";
	private static final String PARENT2 = "parent2";
	private static final String CHILD1 = "child1";
	private static final String CHILD2 = "child2";
	private static final String CHILD3 = "child3";
	private static final String CHILD4 = "child4";
	private static final String GRANDPARENTSKU = "GRANDPA";

	private Map<String, ItemConfiguration> children1;
	private List<ItemConfiguration> children1List;

	private ItemConfiguration child1;
	private ItemConfiguration child2;
	private ItemConfiguration parent1;


	private ItemConfiguration child3;
	private ItemConfiguration child4;
	private ItemConfiguration parent2;

	private ItemConfiguration grandParent;

	private static ItemConfigurationValidator dummyValidator = new ItemConfigurationValidator() {
		@Override
		public ItemConfigurationValidationResult validate(final ItemConfiguration itemConfiguration) {
			return ItemConfigurationValidationResult.SUCCESS;
		}
	};


	/**
	 * Runs before every test case.
	 *
	 * Creates a structure of items with the following structure:
	 * * grandParent
	 * ** parent1
	 * *** child1
	 * *** child2
	 * ** parent2
	 * *** child3
	 * *** child4
	 * *** child1
	 */
	@Before
	public void setUp() {
		child1 = new ItemConfigurationImpl(SKU1,
				Collections.<String, ItemConfiguration>emptyMap(), true, CHILD1);
		child2 = new ItemConfigurationImpl(SKU2,
				Collections.<String, ItemConfiguration>emptyMap(), true, CHILD2);
		Map<String, ItemConfiguration> children = new HashMap<String, ItemConfiguration>();
		children.put(CHILD1, child1);
		children.put(CHILD2, child2);
		this.children1 = Collections.unmodifiableMap(children);
		this.children1List = Collections.unmodifiableList(new ArrayList<ItemConfiguration>(children.values()));
		parent1 = new ItemConfigurationImpl(PARENTSKU1, children1, true, PARENT1);

		child3 = new ItemConfigurationImpl(SKU3,
				Collections.<String, ItemConfiguration>emptyMap(), true, CHILD3);
		child4 = new ItemConfigurationImpl(SKU4,
				Collections.<String, ItemConfiguration>emptyMap(), true, CHILD4);
		Map<String, ItemConfiguration> children2 = new HashMap<String, ItemConfiguration>();
		children2.put(CHILD3, child3);
		children2.put(CHILD4, child4);
		children2.put(CHILD1, child1);
		parent2 = new ItemConfigurationImpl(PARENTSKU2, children2, true, PARENT2);

		Map<String, ItemConfiguration> parents = new HashMap<String, ItemConfiguration>();
		parents.put(PARENT1, parent1);
		parents.put(PARENT2, parent2);
		grandParent = new ItemConfigurationImpl(GRANDPARENTSKU, parents, true, null);
	}

	/**
	 * Tests cloning the item.
	 *
	 * @throws CloneNotSupportedException shouldn't throw it!
	 */
	@Test
	public void testClone() throws CloneNotSupportedException {
		ItemConfigurationImpl itemConfiguration = new ItemConfigurationImpl(PARENTSKU1,
				Collections.<String, ItemConfiguration>emptyMap(), true, null);
		ItemConfigurationImpl cloned = itemConfiguration.clone();
		assertCloned(itemConfiguration, cloned);
	}

	/**
	 * Tests cloning the item with its children. Ensures it is doing a deep clone.
	 *
	 * @throws CloneNotSupportedException shouldn't throw it!
	 */
	@Test
	public void testDeepClone() throws CloneNotSupportedException {
		ItemConfigurationImpl itemConfiguration = new ItemConfigurationImpl(PARENTSKU1, children1, true, null);
		ItemConfigurationImpl cloned = itemConfiguration.clone();
		assertCloned(itemConfiguration, cloned);

		assertCloned(itemConfiguration.getChildById(CHILD1), cloned.getChildById(CHILD1));
		assertCloned(itemConfiguration.getChildById(CHILD2), cloned.getChildById(CHILD2));

	}

	/**
	 * Tests getting children given their IDs.
	 */
	@Test
	public void testGetChildByIds() {
		assertSame("an empty path should return the root",
				parent1, parent1.getChildByPath(Collections.<String>emptyList()));
		assertSame("getting immediate child passing the ID should return the right child",
				child1, parent1.getChildById(CHILD1));
		assertSame("getting immediate child using the path should return the right child",
				child1, parent1.getChildByPath(Arrays.asList(CHILD1)));
	}

	/**
	 * Tests getting children given invalid IDs.
	 */
	@Test
	public void testGetChildByInvalidIds() {
		assertNull(INVALID_PATH_RETURNS_NULL, parent1.getChildById(INVALID_PATH));
		assertNull(INVALID_PATH_RETURNS_NULL, parent1.getChildByPath(Arrays.asList(INVALID_PATH)));
	}

	/**
	 * Tests getting children given their IDs.
	 */
	@Test
	public void testGetSecondLevelChildByIds() {
		assertSame("Getting parent1 > child1 should return child1",
				child1, grandParent.getChildByPath(Arrays.asList(PARENT1, CHILD1)));
		assertSame("Getting parent1 > child2 should return child2",
				child2, grandParent.getChildByPath(Arrays.asList(PARENT1, CHILD2)));
		assertSame("Getting parent2 > child3 should return child3",
				child3, grandParent.getChildByPath(Arrays.asList(PARENT2, CHILD3)));
		assertSame("Getting parent2 > child1 should return child1",
				child1, grandParent.getChildByPath(Arrays.asList(PARENT2, CHILD1)));
	}

	/**
	 * Tests getting children given invalid IDs.
	 */
	@Test
	public void testGetSecondLevelChildByInvalidIds() {
		// child3 is the child of parent2, not parent1
		assertNull(INVALID_PATH_RETURNS_NULL, grandParent.getChildByPath(Arrays.asList(PARENT1, CHILD3)));

		// child2 is the child of parent1, not parent2
		assertNull(INVALID_PATH_RETURNS_NULL, grandParent.getChildByPath(Arrays.asList(PARENT2, CHILD2)));

		assertNull(INVALID_PATH_RETURNS_NULL, grandParent.getChildByPath(Arrays.asList(PARENT1, INVALID_PATH)));
	}

	/**
	 * Tests the builder.
	 */
	@Test
	public void testBuilder() {
		ItemConfigurationImpl itemConfiguration = new ItemConfigurationImpl(PARENTSKU1,
				Collections.<String, ItemConfiguration>emptyMap(), true, null);

		ItemConfigurationBuilder builder = new ItemConfigurationImpl.Builder(itemConfiguration, dummyValidator);
		ItemConfiguration built = builder.build();
		assertSame(itemConfiguration, built);
		assertEquals("SKU code of the built item should be equal to that of the input", PARENTSKU1, built.getSkuCode());
		assertTrue("The children of the built item should be equal to that of the input.", built.getChildren().isEmpty());

		builder.select(Collections.<String>emptyList(), SKU2);
		ItemConfiguration built2 = builder.build();
		assertEquals("SKU code of the built item should reflect the change made through the builder.",
				SKU2, built2.getSkuCode());
		assertTrue("The children of the built item should be untouched.", built.getChildren().isEmpty());

		ItemConfiguration originalItemConfiguration = new ItemConfigurationImpl(PARENTSKU1,
				Collections.<String, ItemConfiguration>emptyMap(), true, null);

		assertEquals("Building a new modified item should not change anything about the previously built items.",
				built, originalItemConfiguration);
	}

	/**
	 * Tests the builder.
	 */
	@Test
	public void testBuilderItemWithChildren() {
		ItemConfigurationImpl itemConfiguration = new ItemConfigurationImpl(PARENTSKU1, children1, true, null);

		ItemConfigurationBuilder builder = new ItemConfigurationImpl.Builder(itemConfiguration, dummyValidator);
		ItemConfiguration built = builder.build();
		assertSame(itemConfiguration, built);
		assertEquals("SKU code of the built item should be equal to that of the input", PARENTSKU1, built.getSkuCode());
		assertEquals("The children of the built item should be equal to that of the input.",
				children1List, built.getChildren());

		builder.select(Arrays.asList(CHILD1), SKU1A);
		ItemConfiguration built2 = builder.build();
		assertFalse("The newly built item should reflect the change made.", built.equals(built2));

		ItemConfiguration changedChild1 = built2.getChildById(CHILD1);
		assertNotSame("The child1 should be changed in the new item.", child1, changedChild1);
		assertEquals("The new child should have the changed SKU code", SKU1A, changedChild1.getSkuCode());
	}

	/**
	 * Asserts the cloned object is not the same instance. Also verifies the two are equal.
	 *
	 * @param object the object
	 * @param cloned the clone of the object
	 */
	private static void assertCloned(final Object object, final Object cloned) {
		assertNotSame("the cloned instance should be a new object.", object, cloned);
		assertEquals("The cloned item should equal the item configuration.", object, cloned);
	}

}
