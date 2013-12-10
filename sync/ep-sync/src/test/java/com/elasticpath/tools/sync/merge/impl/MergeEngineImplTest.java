package com.elasticpath.tools.sync.merge.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.MapKey;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.CategoryAttributeValueImpl;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductCategory;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductCategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tags.domain.impl.ConditionalExpressionImpl;
import com.elasticpath.tools.sync.MockInterface;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.merge.configuration.EntityFilter;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;
import com.elasticpath.tools.sync.merge.configuration.GuidLocator;
import com.elasticpath.tools.sync.merge.configuration.MergeBoundarySpecification;
import com.elasticpath.tools.sync.merge.configuration.ValueObjectMerger;
import com.elasticpath.tools.sync.merge.configuration.impl.ConditionalExpressionFilter;
import com.elasticpath.tools.sync.utils.SyncUtils;

/**
 * Tests recursive algorithms of merging differently structured domain objects.
 */
public class MergeEngineImplTest {

	private static final String PRODUCT_GUID = "PRODUCT_GUID";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final MergeEngineImpl mergeEngine = new MergeEngineImpl();

	private MergeBoundarySpecification mockMergeBoundary;

	private ValueObjectMerger mockValueObjectMerger;

	private EntityLocator mockEntityLocator;

	private GuidLocator mockGuidLocator;

	private SyncUtils mockSyncUtils;

	private Method mockMethod;

	/**
	 * Prepares mock objects and injects them into <code>mergeEngine</code> under test.
	 */
	@Before
	public void setUp() {
		mockMergeBoundary = context.mock(MergeBoundarySpecification.class);
		mergeEngine.setMergeBoundarySpecification(mockMergeBoundary);

		mockValueObjectMerger = context.mock(ValueObjectMerger.class);
		mergeEngine.setValueObjectMerger(mockValueObjectMerger);

		mockEntityLocator = context.mock(EntityLocator.class);
		mergeEngine.setEntityLocator(mockEntityLocator);

		mockGuidLocator = context.mock(GuidLocator.class);
		mergeEngine.setGuidLocator(mockGuidLocator);

		mockSyncUtils = context.mock(SyncUtils.class);
		mergeEngine.setSyncUtils(mockSyncUtils);

		final Map<String, EntityFilter> mergeFilters = new HashMap<String, EntityFilter>();
		mergeFilters.put("com.elasticpath.tags.domain.impl.ConditionalExpressionImpl", new ConditionalExpressionFilter());
		mergeEngine.setMergeFilters(mergeFilters);
	}

	/**
	 * Checks if getter method passed to <code>shouldNotMergeCollection</code> doesn't represent actual
	 * collection then <code>shouldNotMergeCollection</code> fails with exception.
	 *
	 * @throws NoSuchMethodException when one of mock objects cannot be prepared
	 * @throws SecurityException if something is wrong with reflection
	 */
	@Test(expected = SyncToolRuntimeException.class)
	public void testShouldNotMergeCollectionNotCollection() throws SecurityException, NoSuchMethodException  {
		mockMethod = ProductImpl.class.getMethod("getCode");
		mergeEngine.shouldNotMergeCollection(mockMethod);
	}

	/**
	 * Checks oneToMany association with target class contained in boundaries.
	 * MergeEngine returns true because collections of such classes are not supposed to be merged
	 *
	 * @throws NoSuchMethodException when one of mock objects cannot be prepared
	 * @throws SecurityException if something is wrong with reflection
	 */
	@Test
	public void testShouldNotMergeCollectionTrue() throws SecurityException, NoSuchMethodException {
		mockMethod = ProductImpl.class.getMethod("getAttributeValueMap");

		context.checking(new Expectations() { {
			oneOf(mockMergeBoundary).stopMerging(with(any(Class.class))); will(returnValue(true));
		} });

		assertTrue(mergeEngine.shouldNotMergeCollection(mockMethod));
	}

	/**
	 * Checks manyToMany association with target class contained in boundaries.
	 * MergeEngine returns false because collections of such classes should be merged but not just updated
	 *
	 * @throws NoSuchMethodException when one of mock objects cannot be prepared
	 * @throws SecurityException if something is wrong with reflection
	 */
	@Test
	public void testShouldNotMergeCollectionFalse() throws SecurityException, NoSuchMethodException {
		mockMethod = ProductTypeImpl.class.getMethod("getSkuOptions");

		context.checking(new Expectations() { {
			oneOf(mockMergeBoundary).stopMerging(with(any(Class.class))); will(returnValue(false));
		} });

		assertFalse(mergeEngine.shouldNotMergeCollection(mockMethod));
	}

	/**
	 * Tests that <code>updateValueObjectInCollection</code> method finds first object in
	 * a collection equal to source and updates it using <code>ObjectValueMerger</code>.
	 */
	@Test
	public void testUpdateValueObjectInCollection() {
		final Product product1 = new ProductImpl();
		final Category category1 = new CategoryImpl();
		final Category category2 = new CategoryImpl();

		final ProductCategory productCategory1 = new ProductCategoryImpl();
		productCategory1.setProduct(product1);
		productCategory1.setCategory(category1);

		final ProductCategory productCategory2 = new ProductCategoryImpl();
		productCategory2.setProduct(product1);
		productCategory2.setCategory(category2);

		final Collection<Object> collectionToUpdate = new ArrayList<Object>();
		collectionToUpdate.add(productCategory1);
		collectionToUpdate.add(productCategory2);

		final ProductCategory productCategory12 = new ProductCategoryImpl();
		productCategory12.setProduct(product1);
		productCategory12.setCategory(category1);

		final ProductCategory productCategory22 = new ProductCategoryImpl();
		productCategory22.setProduct(product1);
		productCategory22.setCategory(category2);

		context.checking(new Expectations() { {
			oneOf(mockValueObjectMerger).merge(productCategory12, productCategory1);
			oneOf(mockValueObjectMerger).merge(productCategory22, productCategory2);
		} });

		mergeEngine.updateValueObjectInCollection(collectionToUpdate, productCategory12);
		mergeEngine.updateValueObjectInCollection(collectionToUpdate, productCategory22);
	}

	/**
	 * SyncToolRuntimeException is thrown if <code>EntityLocator</code> cannot retrieve fresh instance for the given domain object.
	 */
	@Test(expected = SyncToolRuntimeException.class)
	public void testRetrieveFreshReferenceException() {
		final Product mockProduct = context.mock(Product.class);

		context.checking(new Expectations() { {
			oneOf(mockEntityLocator).locatePersistentReference(with(same(mockProduct))); will(returnValue(null));
			oneOf(mockGuidLocator).locateGuid(with(same(mockProduct))); will(returnValue(PRODUCT_GUID));
		} });

		mergeEngine.retrieveFreshReference(mockProduct);
	}

	/**
	 * If <code>EntityLocator</code> retrieves fresh object from database then it should be return from this method.
	 */
	@Test
	public void testRetrieveFreshReference() {
		final Product product = new ProductImpl();
		product.setGuid(PRODUCT_GUID);

		context.checking(new Expectations() { {
			oneOf(mockEntityLocator).locatePersistentReference(with(same(product))); will(returnValue(product));
		} });

		assertEquals(product, mergeEngine.retrieveFreshReference(product));
	}

	/**
	 * Checks that method <code>refreshCollection</code> determines that target object represents a collection
	 * but not a map, cleans target collection and puts refreshed domain objects from source collection into it.
	 */
	@Test
	public void testRefreshCollectionCollection() {
		final Product product = new ProductImpl();
		final Product originalTargetCollectionProduct = new ProductImpl();
		final Product freshProduct = new ProductImpl();
		product.setCode("product");
		originalTargetCollectionProduct.setCode("originalTargetCollectionProduct");
		freshProduct.setCode("freshProduct");

		final MockInterface mockInterface = context.mock(MockInterface.class);

		final Collection<Product> sourceCollection = Collections.singletonList(product);
		final Collection<Product> targetCollection = new ArrayList<Product>();
		targetCollection.add(originalTargetCollectionProduct);

		final MergeEngineImpl mergeEngine = new MergeEngineImpl() {
			@Override
			Persistable retrieveFreshReference(final Persistable sourceValue) throws SyncToolConfigurationException {
				return (Persistable) mockInterface.method(sourceValue);
			}
		};

		context.checking(new Expectations() { {
			oneOf(mockInterface).method(with(product)); will(returnValue(freshProduct));
		} });

		mergeEngine.refreshCollection(null, sourceCollection, targetCollection);

		assertFalse("Target collection must be cleared before adding new items; existing list items should no longer "
				+ "exist in the list", targetCollection.contains(originalTargetCollectionProduct));
		assertEquals("Target collection should contain only one item", 1, targetCollection.size());
		assertEquals("Target collection should contain the fresh product", freshProduct, targetCollection.iterator().next());
	}

	/**
	 * Checks that method <code>refreshCollection</code> determines that target object represents a map
	 * but not a collection, cleans target map and puts refreshed domain objects from source map into it.
	 *
	 * @throws NoSuchMethodException when one of mock objects cannot be prepared
	 * @throws SecurityException if something is wrong with reflection
	 */
	@Test
	public void testRefreshMap() throws SecurityException, NoSuchMethodException {
		final MockInterface mockInterface = context.mock(MockInterface.class);
		final Method getterMethod = ProductImpl.class.getDeclaredMethod("getAttributeValueMap");
		final String localizedAttributeKey = "ATTRIBUTE_KEY";
		final AttributeValue attributeValue = new CategoryAttributeValueImpl();
		final AttributeValue originalTargetMapAttributeValue = new CategoryAttributeValueImpl();
		final AttributeValue freshAttributeValue = new CategoryAttributeValueImpl();
		final Map<String, AttributeValue> sourceMap = Collections.singletonMap(localizedAttributeKey, attributeValue);
		final Map<String, AttributeValue> targetMap = new HashMap<String, AttributeValue>();
		final String originalTargetMapAttributeValueKey = "originalAttributeValue";
		targetMap.put(originalTargetMapAttributeValueKey, originalTargetMapAttributeValue);

		final MergeEngineImpl mergeEngine = new MergeEngineImpl() {
			@Override
			Persistable retrieveFreshReference(final Persistable sourceValue) throws SyncToolConfigurationException {
				return (Persistable) mockInterface.method(sourceValue);
			}
		};

		mergeEngine.setSyncUtils(mockSyncUtils);

		context.checking(new Expectations() { {
			oneOf(mockInterface).method(with(same(attributeValue))); will(returnValue(freshAttributeValue));
			oneOf(mockSyncUtils).getMapKey(getterMethod.getAnnotation(MapKey.class), freshAttributeValue); will(returnValue(localizedAttributeKey));
		} });

		mergeEngine.refreshCollection(getterMethod, sourceMap, targetMap);

		assertNull("Target map must be cleared before adding new items; existing list items should no longer "
				+ "exist in the list", targetMap.get(originalTargetMapAttributeValueKey));
		assertEquals("Target collection should contain only one item", 1, targetMap.size());
		assertEquals("Target collection should contain the fresh product", freshAttributeValue, targetMap.get(localizedAttributeKey));
	}

	/**
	 * If container to be refreshed is neither Collection nor Map then exception is thrown.
	 */
	@Test(expected = SyncToolRuntimeException.class)
	public void testRefreshCollectionException() {
		final Object unknownContainer = new Object();
		mergeEngine.refreshCollection(null, unknownContainer, null);
	}

	/**
	 * Make sure we can get merge entity filter.
	 */
	@Test
	public void testGetFilter() {
		final EntityFilter filter = mergeEngine.getMergeFilter(ConditionalExpressionImpl.class.getName());
		assertNotNull("Filter should be found for ConditionalExpressionImpl", filter);
	}

	/**
	 * Check if isMergable returns appropriate values for different types of objects.
	 */
	@Test
	public void testIsMergable() {
		final ConditionalExpressionImpl namedConditionalExpression = new ConditionalExpressionImpl();
		namedConditionalExpression.setNamed(true);
		assertFalse("Named conditional expression should not be mergable.", mergeEngine.isMergeable(namedConditionalExpression));

		final ConditionalExpressionImpl anonymousConditionalExpression = new ConditionalExpressionImpl();
		anonymousConditionalExpression.setNamed(false);
		assertTrue("Anonymous conditional expression should be mergable.", mergeEngine.isMergeable(anonymousConditionalExpression));
	}

	/**
	 * Check if isMergable returns true for types without a registered filter.
	 */
	@Test
	public void testIsMergableWithUnknownType() {
		final Product product = new ProductImpl();
		assertTrue("Unknown type should be mergable.", mergeEngine.isMergeable(product));
	}

}
