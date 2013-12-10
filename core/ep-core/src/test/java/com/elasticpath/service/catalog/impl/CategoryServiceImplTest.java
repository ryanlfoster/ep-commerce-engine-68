/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.set.ListOrderedSet;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.commons.exception.IllegalOperationException;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.impl.AttributeGroupImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.AbstractCategoryImpl;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CatalogLocaleImpl;
import com.elasticpath.domain.catalog.impl.CategoryDeletedImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.CategoryLoadTunerImpl;
import com.elasticpath.domain.catalog.impl.CategoryTypeImpl;
import com.elasticpath.domain.catalog.impl.LinkedCategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.ProductDao;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.test.util.matchers.GetterSetterMatcherAction;

/** Test cases for <code>CategoryServiceImpl</code>. */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessiveClassLength",
					"PMD.TooManyStaticImports", "PMD.ExcessiveImports" })
public class CategoryServiceImplTest {

	private static final String CATALOG_CODE = "catalog_code";

	private static final String CATEGORY_CODE = "category_code";

	private static final String NEW_FETCH_PLAN_HELPER = "new FetchPlanHelper";

	private static final String CATEGORY_CODE_CATALOG_CODE = "category_code|catalog_code";

    private static final int FOUR = 4;

	private static final String CATEGORY_FIND_BY_GUID_CATALOG = "CATEGORY_FIND_BY_GUID_CATALOG";

	private static final String CATEGORY_UID_SELECT_BY_CHILDREN_UIDS = "CATEGORY_UID_SELECT_BY_CHILDREN_UIDS";

	private static final String PLACEHOLDER_FOR_LIST = "list";

	private static final long EXISTING_CATEGORY_UID = 12345;

	private static final String CATEGORY_LIST_SUBCATEGORY = "CATEGORY_LIST_SUBCATEGORY";

	private CategoryServiceImpl categoryService;

	private Category category;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;

	private BeanFactoryExpectationsFactory beanFactoryExpectationsFactory;

	private ProductService productService;

	private Catalog catalog;

	private CatalogService catalogService;

	private IndexNotificationService indexNotificationService;

	private Catalog masterCatalog;

	private FetchPlanHelper fetchPlanHelper;

	private PersistenceEngine persistenceEngine;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		beanFactoryExpectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		beanFactoryExpectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ABSTRACT_CATEGORY, AbstractCategoryImpl.class);
		beanFactoryExpectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CATEGORY, CategoryImpl.class);
		beanFactoryExpectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CATALOG_LOCALE, CatalogLocaleImpl.class);
		beanFactoryExpectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER, FetchGroupLoadTunerImpl.class);
		beanFactoryExpectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);

		fetchPlanHelper = context.mock(FetchPlanHelper.class);
		context.checking(new Expectations() {
			{
				allowing(fetchPlanHelper).configureCategoryFetchPlan(with(anyOf(any(CategoryLoadTuner.class), aNull(CategoryLoadTuner.class))));
				allowing(fetchPlanHelper).clearFetchPlan();
			}
		});

		persistenceEngine = context.mock(PersistenceEngine.class);

		categoryService = new CategoryServiceImpl();
		categoryService.setPersistenceEngine(persistenceEngine);

		categoryService.setFetchPlanHelper(fetchPlanHelper);

		productService = context.mock(ProductService.class);
		categoryService.setProductService(productService);

		catalog = context.mock(Catalog.class);
		context.checking(new Expectations() {
			{
				allowing(catalog).getUidPk(); will(returnValue(2L));
				allowing(beanFactory).getBeanImplClass(ContextIdNames.ABSTRACT_CATEGORY); will(returnValue(AbstractCategoryImpl.class));
			}
		});

		catalogService = context.mock(CatalogService.class);
		categoryService.setCatalogService(catalogService);

		indexNotificationService = context.mock(IndexNotificationService.class);

		beanFactoryExpectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.INDEX_NOTIFICATION_SERVICE, indexNotificationService);

		setupCategory();
	}

	@After
	public void tearDown() {
		beanFactoryExpectationsFactory.close();
	}

	private void setupCategory() {
		category = getCategory();
		category.setUidPk(1L);
	}

	/**
	 * Test the behaviour when persistence engine is not set.
	 */
	@Test(expected = EpServiceException.class)
	public void testPersistenceEngineIsNull() {
		categoryService.setPersistenceEngine(null);
		categoryService.add(category);
	}

	/**
	 * Test the simple success case of adding a non-linked category with a code that
	 * doesn't already exist.
	 * Check that add() calls save() and then calls the product service to
	 * notify that the category was updated.
	 */
	@Test
	public void testAddNonLinkedSucceeds() {
		category.setCode("testCode1");
		//expect that save() is called on the persistence engine
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).save(category);
				oneOf(productService).notifyCategoryUpdated(category);
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CATEGORY, category.getUidPk());
			}
		});
		//pretend that this master category doesn't already exist - the add should succeed
		final CategoryServiceImpl serviceImpl = new CategoryServiceImpl() {
			@Override
			protected boolean masterCategoryExists(final String categoryCode) {
				return false;
			}
		};
		serviceImpl.setPersistenceEngine(persistenceEngine);
		serviceImpl.setProductService(productService);
		assertNotNull(serviceImpl.add(category));
	}

	/**
	 * Test that trying to add a non-linked category with a code that already exists fails.
	 */
	@Test(expected = DuplicateKeyException.class)
	public void testAddNonLinkedPreExistingFails() {
		final CategoryServiceImpl serviceImpl = new CategoryServiceImpl() {
			@Override
			protected boolean masterCategoryExists(final String categoryCode) {
				return true;
			}
		};
		serviceImpl.setPersistenceEngine(persistenceEngine);
		serviceImpl.add(category);
	}

	/**
	 * Test that adding a linked category with a code that already exists in the DB will fail.
	 */
	@Test(expected = DuplicateKeyException.class)
	public void testAddLinkedPreExistingFails() {
		final CategoryServiceImpl serviceImpl = new CategoryServiceImpl() {
			@Override
			protected boolean linkedCategoryExists(final String categoryCode, final String catalogCode) {
				return true;
			}
		};
		serviceImpl.setPersistenceEngine(persistenceEngine);
		final Category linkedCategory = context.mock(Category.class);
		context.checking(new Expectations() {
			{
				allowing(linkedCategory).isLinked(); will(returnValue(true));
				ignoring(linkedCategory).getCode();
				allowing(linkedCategory).getCatalog(); will(returnValue(getCatalog()));
			}
		});
		serviceImpl.add(linkedCategory);
	}

	/**
	 * Test that masterCategoryExists() will call the CATEGORY_COUNT_BY_CODE named query and will
	 * return true if the returned list's first element (the count) is > 1.
	 */
	@Test
	public void testMasterCategoryExists() {
		final String testCode = "myCode";
		final CategoryServiceImpl serviceImpl = new CategoryServiceImpl();
		serviceImpl.setPersistenceEngine(persistenceEngine);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_COUNT_BY_CODE", testCode);
				will(returnValue(Arrays.asList(1L)));
			}
		});
		assertTrue("Count returns 1, so the category exists", serviceImpl.masterCategoryExists(testCode));

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_COUNT_BY_CODE", testCode);
				will(returnValue(Arrays.asList(0L)));
			}
		});
		assertFalse("Count returns 0, so the category doesn't exist", serviceImpl.masterCategoryExists(testCode));
	}

	/**
	 * Test that linkedCategoryExists() will call the LINKED_CATEGORY_COUNT_BY_CODE named query and will
	 * return true if the returned lists's first element (the count) is > 1.
	 */
	@Test
	public void testLinkedCategoryExists() {
		final String testCode = "myCode";
		final CategoryServiceImpl serviceImpl = new CategoryServiceImpl();
		serviceImpl.setPersistenceEngine(persistenceEngine);
		//Test true
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("LINKED_CATEGORY_COUNT_BY_CODE", testCode, null);
				will(returnValue(Arrays.asList(1L)));
			}
		});
		assertTrue(serviceImpl.linkedCategoryExists(testCode, null));
		//Test false
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("LINKED_CATEGORY_COUNT_BY_CODE", testCode, null);
				will(returnValue(Arrays.asList(0L)));
			}
		});
		assertFalse(serviceImpl.linkedCategoryExists(testCode, null));
	}

	/**
	 * Test that update() will call persistenceEngine.update() and will also call ProductService with the
	 * updated category to notify it that the category was updated.
	 */
	@Test
	public void testUpdate() {
		final Category updatedCategory = new CategoryImpl();
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).update(category); will(returnValue(updatedCategory));
				oneOf(productService).notifyCategoryUpdated(updatedCategory);
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CATEGORY, updatedCategory.getUidPk());
			}
		});
		assertEquals("update must return the object retrieved from the persistence engine on update",
				updatedCategory, categoryService.update(category));
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		final long uid = 1234L;
		final Category category = new CategoryImpl();
		category.setUidPk(uid);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).load(AbstractCategoryImpl.class, uid); will(returnValue(category));
			}
		});
		assertSame(category, categoryService.load(uid));

		final long nonExistUid = 3456L;
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).load(AbstractCategoryImpl.class, nonExistUid); will(returnValue(null));
			}
		});
		assertNull(categoryService.load(nonExistUid));

		context.checking(new Expectations() {
			{
				never(persistenceEngine);
			}
		});
		assertEquals(0, categoryService.load(0).getUidPk());
	}

	/**
	 * Test method for {@link CategoryServiceImpl#load(long, CategoryLoadTuner).
	 */
	@Test
	public void testLoadWithCategoryLoadTuner() {
		final long uid = 1234L;
		final Category category = new CategoryImpl();
		category.setUidPk(uid);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).load(AbstractCategoryImpl.class, uid); will(returnValue(category));
			}
		});
		assertSame(category, categoryService.load(uid, (CategoryLoadTuner) null));

		final long nonExistUid = 3456L;
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).load(AbstractCategoryImpl.class, nonExistUid); will(returnValue(null));
			}
		});
		assertNull(categoryService.load(nonExistUid, (CategoryLoadTuner) null));

		context.checking(new Expectations() {
			{
				never(persistenceEngine);
			}
		});
		assertEquals(0, categoryService.load(0, (CategoryLoadTuner) null).getUidPk());
	}

	/**
	 * Test method for {@link CategoryServiceImpl#load(long, FetchGroupLoadTuner).
	 */
	@Test
	public void testLoadWithFGLoadTuner() {
		final long uid = 1234L;
		final Category category = context.mock(Category.class);
		final FetchGroupLoadTuner fGLoadTuner = context.mock(FetchGroupLoadTuner.class);
		final FetchPlanHelper fetchPlanHelper = context.mock(FetchPlanHelper.class, NEW_FETCH_PLAN_HELPER);
		categoryService.setFetchPlanHelper(fetchPlanHelper);

		context.checking(new Expectations() {
			{
				allowing(category).getUidPk(); will(returnValue(uid));
				oneOf(persistenceEngine).load(AbstractCategoryImpl.class, uid); will(returnValue(category));
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(fGLoadTuner);
				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});
		assertSame(category, categoryService.load(uid, fGLoadTuner));

		final long nonExistUid = 3456L;
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).load(AbstractCategoryImpl.class, nonExistUid); will(returnValue(null));
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(fGLoadTuner);
				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});
		assertNull(categoryService.load(nonExistUid, fGLoadTuner));

		context.checking(new Expectations() {
			{
				never(persistenceEngine);
				never(fetchPlanHelper);
			}
		});
		assertEquals(0, categoryService.load(0, fGLoadTuner).getUidPk());
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryServiceImpl.listRootCategories()'.
	 */
	@Test
	public void testListRootCategories() {
		final List<Category> categories = new ArrayList<Category>();

		// expectations
		final FetchPlanHelper fetchPlanHelper = context.mock(FetchPlanHelper.class, NEW_FETCH_PLAN_HELPER);
		categoryService.setFetchPlanHelper(fetchPlanHelper);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(with("CATEGORY_LIST_ROOT"), with(any(Object[].class))); will(returnValue(categories));
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));
				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});
		assertEquals(categories, categoryService.listRootCategories(catalog, false));
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryServiceImpl.listRootCategories()'.
	 */
	@Test
	public void testListAvailableRootCategories() {
		final List<Category> categories = new ArrayList<Category>();

		// expectations
		final FetchPlanHelper fetchPlanHelper = context.mock(FetchPlanHelper.class, NEW_FETCH_PLAN_HELPER);
		categoryService.setFetchPlanHelper(fetchPlanHelper);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(with("CATEGORY_LIST_AVAILABLE_ROOT"), with(any(Object[].class)));
					will(returnValue(categories));
				oneOf(persistenceEngine).retrieveByNamedQuery(with("LINKED_CATEGORY_LIST_AVAILABLE_ROOT"), with(any(Object[].class)));
					will(returnValue(categories));
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));
				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});

		assertEquals(categories, categoryService.listRootCategories(catalog, true));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.CategoryServiceImpl.listRootCategoriesWithTree()'.
	 */
	@Test
	public void testListRootCategoriesWithTree() {
		final List<Category> categories = new ArrayList<Category>();

		// expectations
		final FetchPlanHelper fetchPlanHelper = context.mock(FetchPlanHelper.class, NEW_FETCH_PLAN_HELPER);
		categoryService.setFetchPlanHelper(fetchPlanHelper);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(with("CATEGORY_LIST_ROOT"), with(any(Object[].class)));
					will(returnValue(categories));
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));
				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});

		assertEquals(categories, categoryService.listRootCategoriesWithTree(catalog, false));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.CategoryServiceImpl.listRootCategoriesWithTree()'.
	 */
	@Test
	public void testListAvailableRootCategoriesWithTree() {
		final List<Category> categories = new ArrayList<Category>();

		// expectations
		final FetchPlanHelper fetchPlanHelper = context.mock(FetchPlanHelper.class, NEW_FETCH_PLAN_HELPER);
		categoryService.setFetchPlanHelper(fetchPlanHelper);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(with("CATEGORY_LIST_AVAILABLE_ROOT"), with(any(Object[].class)));
					will(returnValue(categories));
				oneOf(persistenceEngine).retrieveByNamedQuery(with("LINKED_CATEGORY_LIST_AVAILABLE_ROOT"), with(any(Object[].class)));
					will(returnValue(categories));
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));
				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});

		assertEquals(categories, categoryService.listRootCategoriesWithTree(catalog, true));
	}

	/**
	 * Test method for {@link CategoryServiceImpl#findByGuid(String, String)}.
	 */
	@Test(expected = EpServiceException.class)
	public void testFindByGuidCatalogGuid() {
		final String categoryGuid = "alsdfj;asf";
		final String catalogGuid = "zzzzzzzz";
		final List<Category> categories = new ArrayList<Category>();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_SELECT_BY_GUID_AND_CATALOG_GUID", categoryGuid, catalogGuid);
					will(returnValue(categories));
				oneOf(persistenceEngine).retrieveByNamedQuery("LINKED_CATEGORY_FIND_BY_GUID_CATALOG_GUID",
						categoryGuid, catalogGuid);
				will(returnValue(categories));
			}
		});
		assertNull(categoryService.findByGuid(categoryGuid, catalogGuid));

		categories.add(category);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_SELECT_BY_GUID_AND_CATALOG_GUID", categoryGuid, catalogGuid);
				will(returnValue(categories));
			}
		});
		assertSame(category, categoryService.findByGuid(categoryGuid, catalogGuid));

		categories.add(category);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_SELECT_BY_GUID_AND_CATALOG_GUID", categoryGuid, catalogGuid);
				will(returnValue(categories));
			}
		});

		assertSame(category, categoryService.findByGuid(categoryGuid, catalogGuid));
	}

	/**
	 * Test method for {@link CategoryServiceImpl#findByGuid(String, Catalog)}.
	 */
	@Test
	public void testFindByGuid() {
		final String guid = "abcde";
		final List<Category> categories = new ArrayList<Category>();

		// expectations
		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(CATEGORY_FIND_BY_GUID_CATALOG, guid, getCatalog().getUidPk());
					will(returnValue(categories));
				allowing(persistenceEngine).retrieveByNamedQuery("LINKED_CATEGORY_FIND_BY_GUID_CATALOG",
						guid, getCatalog().getUidPk());
					will(returnValue(categories));
			}
		});

		// No category with the given GUID
		assertNull(categoryService.findByGuid(guid, getCatalog()));

		// One category with the given GUID
		categories.add(category);
		assertSame(category, categoryService.findByGuid(guid, getCatalog()));

		// More than one category with the given GUID
		categories.add(category);
		try {
			assertSame(category, categoryService.findByGuid(guid, getCatalog()));
			fail("EpServiceException expected.");
		} catch (final EpServiceException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryServiceImpl.saveOrUpdate(Category)'.
	 */
	@Test
	public void testSaveOrUpdate() {
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).saveOrMerge(category); will(returnValue(category));
				oneOf(productService).notifyCategoryUpdated(category);
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CATEGORY, category.getUidPk());
			}
		});
		categoryService.saveOrUpdate(category);
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryServiceImpl.isProductInCategory(long, long)'.
	 */
	@Test
	public void testIsProductInCategory() {
		final long productUid = 1L;
		final long categoryUid = 2L;
		final List<Long> results = new ArrayList<Long>();
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("SELECT_PRODUCT_CATEGORY_ASSOCIATION", productUid, categoryUid);
				will(returnValue(results));
			}
		});
		assertFalse(categoryService.isProductInCategory(productUid, categoryUid));

		results.add(2L);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("SELECT_PRODUCT_CATEGORY_ASSOCIATION", productUid, categoryUid);
				will(returnValue(results));
			}
		});
		assertTrue(categoryService.isProductInCategory(productUid, categoryUid));
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryServiceImpl.getCategoryWithAttribute(Long)'.
	 */
	@Test
	public void testGetCategoryWithAttribute() {
		final long uid = 1234L;
		category.setUidPk(uid);

		final CategoryType categoryType = new CategoryTypeImpl();
		final AttributeGroup attributeGroup = new AttributeGroupImpl();
		attributeGroup.setAttributeGroupAttributes(new HashSet<AttributeGroupAttribute>());
		categoryType.setAttributeGroup(attributeGroup);
		category.setCategoryType(categoryType);

		// expectations
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).load(AbstractCategoryImpl.class, uid); will(returnValue(category));
			}
		});

		final Category loadedCategory = categoryService.getCategoryWithAttribute(uid);
		assertSame(category, loadedCategory);
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryServiceImpl.findCategoryByCriterias(MAP)'.
	 */
	@Test
	public void testFindCategoryByCriterias() {
		/*
		 * Map messageMap = new HashMap(); messageMap.put("searchFilter", "name"); messageMap.put("matchType", "MatchContain");
		 * messageMap.put(ACTIVE_STRING, TRUE_STRING); messageMap.put(INACTIVE_STRING, TRUE_STRING); messageMap.put(SEARCH_VALUE, "watch"); ArrayList
		 * argList = new ArrayList(); argList.add("%" + messageMap.get("searchValue").toString().toUpperCase() + "%");
		 * argList.add(getElasticPath().getDefaultLocale());
		 * getMockPersistenceEngine().expects(once()).method(RETRIEVE).with(COMPOSED_CATEGORY_SEARCH_QTY, argList.toArray(), 0,
		 * eq(getElasticPath().getMaxCmSearchResultCount())).will(returnValue(new ArrayList())); categoryService.findCategoryByCriteria(messageMap);
		 */
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryServiceImpl.removeCategoryTree(long)'.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testRemoveCategoryTree() {
		beanFactoryExpectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CATEGORY_DELETED, CategoryDeletedImpl.class);

		// Compose a category branch
		final Category subCategory = getCategory();
		subCategory.setParent(category);

		final int categoryNodesInTheBranch = 2;

		// expectations
		context.checking(new Expectations() {
			{
				// load the top category once
				oneOf(persistenceEngine).load(AbstractCategoryImpl.class, category.getUidPk()); will(returnValue(category));

				// delete the category
				exactly(categoryNodesInTheBranch).of(persistenceEngine).delete(with(any(AbstractCategoryImpl.class)));

				// save the category deleted record
				exactly(categoryNodesInTheBranch).of(persistenceEngine).save(with(any(CategoryDeletedImpl.class)));

				oneOf(persistenceEngine).retrieveByNamedQuery(with("LINKED_CATEGORY_SELECT_BY_MASTER_CATEGORY_UID"), with(any(Object[].class)));
				will(returnValue(new ArrayList<Category>()));

				oneOf(persistenceEngine).evictObjectFromCache(with(any(AbstractCategoryImpl.class)));
				oneOf(productService).hasProductsInCategory(category.getUidPk()); will(returnValue(false));
				oneOf(productService).hasProductsInCategory(subCategory.getUidPk()); will(returnValue(false));
			}
		});

		categoryService.removeCategoryTree(category.getUidPk());
	}

	/**
	 * Ensure that order is preserved when updating {@link Category}s which has the same ordering as another when they
	 * are not a root category.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateOrderingNonRootSameOrdering() {
		final long category1Uid = 1244;
		final long category2Uid = 55125;

		final Category category1 = context.mock(Category.class, "category-1");
		final Category category2 = context.mock(Category.class, "category-2");

		context.checking(new Expectations() {
			{
				Category parentCategory = context.mock(Category.class, "parentCategory");

				allowing(persistenceEngine).load(with(any(Class.class)), with(equalTo(category1Uid)));
				will(returnValue(category1));
				allowing(persistenceEngine).load(with(any(Class.class)), with(equalTo(category2Uid)));
				will(returnValue(category2));
				atLeast(1).of(persistenceEngine).saveOrMerge(category1);
				will(returnValue(category1));
				atLeast(1).of(persistenceEngine).saveOrMerge(category2);
				will(returnValue(category2));
				allowing(persistenceEngine).evictObjectFromCache(parentCategory);
				allowing(productService);
				allowing(indexNotificationService);

				allowing(category1).getUidPk();
				allowing(category1).getCatalog();
				allowing(category1).getParent();
				will(returnValue(parentCategory));
				allowing(category1).compareTo(category2);
				will(returnValue(-1));

				allowing(category2).getUidPk();
				allowing(category2).getCatalog();
				allowing(category2).getParent();
				will(returnValue(parentCategory));
				allowing(category2).compareTo(category1);
				will(returnValue(1));

				// order matters here, this is the reverse of the ordering of the compareTo methods above
				Set<Category> resultSet = new ListOrderedSet();
				resultSet.add(category2);
				resultSet.add(category1);

				allowing(parentCategory).getChildren();
				will(returnValue(resultSet));

				final int sameOrdering = 5515;
				GetterSetterMatcherAction<Integer> category1Ordering = new GetterSetterMatcherAction<Integer>(sameOrdering);
				GetterSetterMatcherAction<Integer> category2Ordering = new GetterSetterMatcherAction<Integer>(sameOrdering);
				allowing(category1).getOrdering();
				will(category1Ordering);
				allowing(category2).getOrdering();
				will(category2Ordering);

				// once to reorder, once to swap
				exactly(2).of(category1).setOrdering(with(category1Ordering));
				exactly(2).of(category2).setOrdering(with(category2Ordering));
			}
		});

		// must use compareTo because we know ordering is the same before swapping
		assertTrue("Expectations were setup incorrectly", category1.compareTo(category2) < 0);
		categoryService.updateOrder(category1Uid, category2Uid);
		assertTrue("Ordering was not swapped", category1.getOrdering() > category2.getOrdering());
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryServiceImpl.updateOrder(long, long)'. Tests the case where reordering a couple of root
	 * categories that haven't been ordered before.
	 */
	@Test
	public void testUpdateOrder() {
		final long oneUid = 1L;
		final long twoUid = 2L;

		// these categories don't have a parent, so must be root categories
		final Category catOne = new CategoryImpl();
		catOne.setUidPk(oneUid);
		catOne.setGuid(String.valueOf(oneUid));
		catOne.setCatalog(catalog);

		final Category catTwo = new CategoryImpl();
		catTwo.setUidPk(twoUid);
		catTwo.setGuid(String.valueOf(twoUid));
		catTwo.setCatalog(catalog);

		// override category service
		CategoryServiceImpl categoryService = new CategoryServiceImpl() {
			// need to override this so that root categories get populated correctly.
			@Override
			public List<Category> listRootCategories(final Catalog catalog, final boolean availableOnly) {
				final List<Category> children = new ArrayList<Category>();
				children.add(catOne);
				children.add(catTwo);
				return children;
			}
		};
		categoryService.setPersistenceEngine(persistenceEngine);
		categoryService.setProductService(productService);
		categoryService.setFetchPlanHelper(fetchPlanHelper);

		context.checking(new Expectations() {
			{
				exactly(FOUR).of(indexNotificationService).addNotificationForEntityIndexUpdate(with(IndexType.CATEGORY), with(any(Long.class)));
				allowing(productService).notifyCategoryUpdated(with(any(AbstractCategoryImpl.class)));
				oneOf(persistenceEngine).load(with(AbstractCategoryImpl.class), with(any(Long.class))); will(returnValue(catOne));
				oneOf(persistenceEngine).load(with(AbstractCategoryImpl.class), with(any(Long.class))); will(returnValue(catTwo));
				atLeast(1).of(persistenceEngine).saveOrMerge(catOne); will(returnValue(catOne));
				atLeast(1).of(persistenceEngine).saveOrMerge(catTwo); will(returnValue(catTwo));
			}
		});
		categoryService.updateOrder(1, 2);
	}

	/**
	 * Test method for
	 * {@link CategoryServiceImpl#updateOrder(long, long) where categories are in different catalogs.
	 */
	@Test(expected = EpServiceException.class)
	public void testUpdateOrderInDifferentCatalog() {
		final long oneUid = 1L;
		final long twoUid = 2L;

		final Catalog catalog1 = context.mock(Catalog.class, "catalog1");
		final Catalog catalog2 = context.mock(Catalog.class, "catalog2");
		context.checking(new Expectations() {
			{
				allowing(catalog1).getUidPk(); will(returnValue(1L));
				allowing(catalog2).getUidPk(); will(returnValue(2L));
			}
		});

		// these categories don't have a parent, so must be root categories
		final Category catOne = new CategoryImpl();
		catOne.setUidPk(oneUid);
		catOne.setGuid(String.valueOf(oneUid));
		catOne.setCatalog(catalog1);

		final Category catTwo = new CategoryImpl();
		catTwo.setUidPk(twoUid);
		catTwo.setGuid(String.valueOf(twoUid));
		catTwo.setCatalog(catalog2);

		// override category service
		CategoryServiceImpl categoryService = new CategoryServiceImpl() {
			// need to override this so that root categories get populated correctly.
			@Override
			public List<Category> listRootCategories(final Catalog catalog, final boolean availableOnly) {
				final List<Category> children = new ArrayList<Category>();
				children.add(catOne);
				children.add(catTwo);
				return children;
			}
		};
		categoryService.setFetchPlanHelper(fetchPlanHelper);
		categoryService.setPersistenceEngine(persistenceEngine);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).load(AbstractCategoryImpl.class, oneUid); will(returnValue(catOne));
				oneOf(persistenceEngine).load(AbstractCategoryImpl.class, twoUid); will(returnValue(catTwo));
			}
		});

		categoryService.updateOrder(1, 2);
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryServiceImpl.listAllCategories()'.
	 */
	@Test
	public void testUpdatePosition() {
		final long oneUid = 1L;
		final long twoUid = 2L;
		final long testUid = 3L;

		// these categories don't have a parent, so must be root categories
		final Category catOne = new CategoryImpl();
		catOne.setUidPk(oneUid);
		catOne.setGuid(String.valueOf(oneUid));
		catOne.setOrdering(0);
		catOne.setCatalog(catalog);

		final Category catTwo = new CategoryImpl();
		catTwo.setUidPk(twoUid);
		catTwo.setGuid(String.valueOf(twoUid));
		catTwo.setOrdering(1);
		catTwo.setCatalog(catalog);

		CategoryServiceImpl categoryService = new CategoryServiceImpl() {
			// need to override this so that we get sub cats back.
			@Override
			public List<Category> getSubCategories(final long categoryUid) {
				final List<Category> children = new ArrayList<Category>();
				children.add(catOne);
				children.add(catTwo);
				return children;
			}

			// updateAndSave is called on all categories ... too trivial to check
			@Override
			public Category saveOrUpdate(final Category category) throws EpServiceException {
				// do nothing
				return category;
			}
		};
		categoryService.setPersistenceEngine(persistenceEngine);
		categoryService.setFetchPlanHelper(fetchPlanHelper);

		final Category catTest = new CategoryImpl();
		context.checking(new Expectations() {
			{
				allowing(catalog).getCode(); will(returnValue("myCategoryCode"));
				oneOf(persistenceEngine).load(AbstractCategoryImpl.class, testUid); will(returnValue(catTest));
			}
		});
		catTest.setUidPk(testUid);
		catTest.setGuid(String.valueOf(testUid));
		catTest.setOrdering(1);
		catTest.setCatalog(catalog);
		catTest.setParent(catOne); // old parent

		// move test from under one to after one
		categoryService.updatePosition(testUid, oneUid, 0, new Long(oneUid));
		assertNull("new (root) Parent", catTest.getParent());
		assertEquals(0, catOne.getOrdering());
		assertEquals(1, catTest.getOrdering());
		assertEquals(2, catTwo.getOrdering());
	}

	/**
	 * Tests that findCodeByUid() returns an empty string when no codes are returned by the retrieval query.
	 */
	@Test
	public void testFindCodeByUidNoCodesReturned() {
		final List<String> codes = new ArrayList<String>();
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_CODE_SELECT_BY_UID", 2L);
				will(returnValue(codes));
				oneOf(persistenceEngine).retrieveByNamedQuery("LINKED_CATEGORY_CODE_SELECT_BY_UID", 2L);
				will(returnValue(codes));
			}
		});
		final String result = categoryService.findCodeByUid(2L);
		assertEquals("The result should contain the code returned by the query", "", result);
	}

	/**
	 * Tests that findCodeByUid() returns the code returned by the retrieval query.
	 */
	@Test
	public void testFindCodeByUidOneCodeReturned() {
		final List<String> codes = new ArrayList<String>();
		codes.add("CODE");
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_CODE_SELECT_BY_UID", 2L);
				will(returnValue(codes));
			}
		});
		final String result = categoryService.findCodeByUid(2L);
		assertEquals("The result should contain the code returned by the query", codes.get(0), result);
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryServiceImpl.findByUids(categoryUids)'.
	 */
	@Test
	public void testFindByUids() {
		final long three = 3;
		final List<Long> categoryUids = new ArrayList<Long>();
		categoryUids.add(1L);
		categoryUids.add(2L);
		categoryUids.add(three);

		// expectations
		final List<Category> categories = new ArrayList<Category>();
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQueryWithList("CATEGORY_BY_UIDS", PLACEHOLDER_FOR_LIST, categoryUids);
				will(returnValue(categories));
			}
		});
		assertSame(categories, categoryService.findByUids(categoryUids));

		// Should return an empty list if no product UID is given.
		final List<Category> result = categoryService.findByUids(new ArrayList<Long>());
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryServiceImpl.findByUids(categoryUids, CategoryLoadTuner)'.
	 */
	@Test
	public void testFindByUidsWithCategoryLoadTuner() {
		final long three = 3;
		final List<Long> categoryUids = new ArrayList<Long>();
		categoryUids.add(1L);
		categoryUids.add(2L);
		categoryUids.add(three);

		// expectations
		final List<Category> categories = new ArrayList<Category>();
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQueryWithList("CATEGORY_BY_UIDS", PLACEHOLDER_FOR_LIST, categoryUids);
				will(returnValue(categories));
			}
		});
		final CategoryLoadTuner categoryLoadTuner = new CategoryLoadTunerImpl();
		assertSame(categories, categoryService.findByUids(categoryUids, categoryLoadTuner));

		// Should return an empty list if no product UID is given.
		final List<Category> result = categoryService.findByUids(new ArrayList<Long>(), categoryLoadTuner);
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Tests that findAncestorCategoryUidsByCategoryUid() returns an empty Set
	 * when the category has no parents.
	 *
	 */
	@Test
	public void testFindAncestorCategoryUidsByCategoryUidNoParents() {
		final List<Long> categoryUids = new ArrayList<Long>();
		final long categoryUid = 123L;
		categoryUids.add(new Long(categoryUid));

		// expectations
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQueryWithList(CATEGORY_UID_SELECT_BY_CHILDREN_UIDS, PLACEHOLDER_FOR_LIST, categoryUids);
				will(returnValue(Collections.emptyList()));
			}
		});

		final Set<Long> result = categoryService.findAncestorCategoryUidsByCategoryUid(categoryUid);
		assertEquals("No parent category uids should be returned.", 0, result.size());
	}

	/**
	 * Tests that findAncestorCategoryUidsByCategoryUid() returns the uid of a category's parent.
	 * In this case only one parent was setup.
	 */
	@Test
	public void testFindAncestorCategoryUidsByCategoryUid() {
		final List<Long> categoryUids = new ArrayList<Long>();
		final long categoryUid = 123L;
		categoryUids.add(new Long(categoryUid));

		final List<Long> ancestorUids = new ArrayList<Long>();
		final long ancestorUid = 234L;
		final Long ancestorUidInLong = new Long(ancestorUid);
		ancestorUids.add(ancestorUidInLong);

		// expectations
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQueryWithList(CATEGORY_UID_SELECT_BY_CHILDREN_UIDS, PLACEHOLDER_FOR_LIST, categoryUids);
					will(returnValue(ancestorUids));
				oneOf(persistenceEngine).retrieveByNamedQueryWithList(CATEGORY_UID_SELECT_BY_CHILDREN_UIDS, PLACEHOLDER_FOR_LIST, ancestorUids);
					will(returnValue(Collections.emptyList()));
			}
		});

		final Set<Long> result = categoryService.findAncestorCategoryUidsByCategoryUid(categoryUid);
		assertEquals(1, result.size());
		assertTrue(result.contains(ancestorUidInLong));
	}

	/**
	 * Tests that findAncestorCategoryCodesByCategoryUid() returns an empty Set
	 * when the category has no parents.
	 *
	 */
	@Test
	public void testFindAncestorCategoryCodesByCategoryUidNoParents() {
		final long childUid = 123L;
		final List<Long> childUids = new ArrayList<Long>();
		childUids.add(childUid);

		// First the child's immediate parents are retrieved (in this case none)
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQueryWithList(CATEGORY_UID_SELECT_BY_CHILDREN_UIDS, PLACEHOLDER_FOR_LIST, childUids);
					will(returnValue(Collections.emptyList()));
				oneOf(persistenceEngine).retrieveByNamedQueryWithList("CATEGORY_CODES_SELECT_BY_UIDS", PLACEHOLDER_FOR_LIST, Collections.emptyList());
					will(returnValue(Collections.emptyList()));
			}
		});

		final Set<String> ancestorCodesResults = categoryService.findAncestorCategoryCodesByCategoryUid(childUid);
		assertEquals("No parent category codes should be returned.", 0, ancestorCodesResults.size());
	}

	/**
	 * Tests that findAncestorCategoryCodesByCategoryUid() returns the code of a category's parent.
	 * In this case only one parent was setup.
	 */
	@Test
	public void testFindAncestorCategoryCodesByCategoryUid() {
		final long childUid = 123L;
		final List<Long> childUids = new ArrayList<Long>();
		childUids.add(childUid);

		final Long ancestorUid = new Long(234L);
		final List<Long> ancestorUids = new ArrayList<Long>();
		ancestorUids.add(ancestorUid);

		final String ancestorCode = "900000243";
		final List<String> ancestorCodes = new ArrayList<String>();
		ancestorCodes.add(ancestorCode);

		context.checking(new Expectations() {
			{
				// First the child's immediate parents are retrieved
				oneOf(persistenceEngine).retrieveByNamedQueryWithList(CATEGORY_UID_SELECT_BY_CHILDREN_UIDS, PLACEHOLDER_FOR_LIST, childUids);
					will(returnValue(ancestorUids));

				// Next the parents of the child's parent is retrieved.
				// In this case there are no parents of the child's parent.
				oneOf(persistenceEngine).retrieveByNamedQueryWithList(CATEGORY_UID_SELECT_BY_CHILDREN_UIDS, PLACEHOLDER_FOR_LIST, ancestorUids);
				will(returnValue(Collections.emptyList()));

				// Finally, get the category codes of the parent uids
				oneOf(persistenceEngine).retrieveByNamedQueryWithList("CATEGORY_CODES_SELECT_BY_UIDS", PLACEHOLDER_FOR_LIST, ancestorUids);
					will(returnValue(ancestorCodes));
			}
		});

		final Set<String> ancestorCodesResults = categoryService.findAncestorCategoryCodesByCategoryUid(childUid);
		assertEquals("The set of ancestor codes should contain only one value.", 1, ancestorCodesResults.size());
		assertTrue("The set should contain the child's parent code", ancestorCodesResults.contains(ancestorCode));
	}

	/**
	 * Test that {@link CategoryServiceImpl#addLinkedCategory(long, long, long)} loads
	 * the categories and the catalog and then calls
	 * {@link CategoryServiceImpl#addLinkedCategory(Category, Category, Catalog)}.
	 */
	@Test
	public void testAddLinkedCategoryLoadsObjects() {
		final long masterCategoryUid = 524L;
		final long parentCategoryUid = 4353L;
		final long catalogUid = 12141L;

		final Category parentCategory = new CategoryImpl();
		parentCategory.setUidPk(parentCategoryUid);

		final Category masterCategory = new CategoryImpl();
		masterCategory.setUidPk(masterCategoryUid);

		final Category addedCategory = new CategoryImpl();

		final CategoryServiceImpl serviceImpl = new CategoryServiceImpl() {
			@Override
			protected Category addLinkedCategory(
					final Category masterCategory, final Category parentCategory, final Catalog catalog) {
				return addedCategory; //we can ensure that this method is called
			}

			@Override //set expectations - assume any load tuner will work
			public Category load(final long uid, final FetchGroupLoadTuner loadTuner) {
				if (parentCategoryUid == uid && loadTuner != null) {
					return parentCategory;
				} else if (masterCategoryUid == uid && loadTuner != null) {
					return masterCategory;
				}
				return null; //load wasn't called with any expected arguments
			}
		};
		serviceImpl.setPersistenceEngine(persistenceEngine);
		serviceImpl.setFetchPlanHelper(fetchPlanHelper);
		context.checking(new Expectations() {
			{
				allowing(catalogService).load(with(any(Long.class)), with(any(FetchGroupLoadTuner.class)), with(any(Boolean.class)));
				will(returnValue(catalog));
			}
		});
		serviceImpl.setCatalogService(catalogService);

		assertEquals("Should return result of calling protected method with loaded objects",
				addedCategory, serviceImpl.addLinkedCategory(masterCategoryUid, parentCategoryUid, catalogUid));
	}

	/**
	 * Test that {@link CategoryServiceImpl#addLinkedCategory(long, long, long)} sets
	 * the default category of the contained products to the new linked category.
	 * {@link CategoryServiceImpl#addLinkedCategory(Category, Category, Catalog)}.
	 */
	@Test
	public void testUpdateProductsWithNewLinkedCategoryAndProductIsDefault() {
		final long masterCategoryUid = 524L;
		final long virtualCatalogUid = 12141L;

		final Catalog virtualCatalog = new CatalogImpl();
		virtualCatalog.setUidPk(virtualCatalogUid);
		virtualCatalog.setCode("virtual");
		final Catalog masterCatalog = new CatalogImpl();
		masterCatalog.setUidPk(1);
		masterCatalog.setCode("master");

		final Category masterCategory = new CategoryImpl();
		masterCategory.setUidPk(masterCategoryUid);
		masterCategory.setCatalog(masterCatalog);

		final Category addedCategory = new CategoryImpl();
		addedCategory.setCatalog(virtualCatalog);

		final Product product = new ProductImpl();
		product.setCategoryAsDefault(masterCategory);

		final CategoryServiceImpl serviceImpl = new CategoryServiceImpl() {
			@Override //set expectations - assume any load tuner will work
			public Category load(final long uid, final FetchGroupLoadTuner loadTuner) {
				if (masterCategoryUid == uid && loadTuner != null) {
					return masterCategory;
				}
				return null; //load wasn't called with any expected arguments
			}
		};

		final LinkedCategoryImpl linkedCategory = new LinkedCategoryImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public Category getMasterCategory() {
				return masterCategory;
			}

			@Override
			public Catalog getCatalog() {
				return virtualCatalog;
			}
		};

		context.checking(new Expectations() {
			{
				allowing(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)), with(any(Boolean.class)));
			}
		});

		serviceImpl.setPersistenceEngine(persistenceEngine);
		serviceImpl.setFetchPlanHelper(fetchPlanHelper);
		serviceImpl.setProductService(new TestProductService(product));

		// call updateProducts
		serviceImpl.updateProductsWithNewLinkedCategory(linkedCategory);

		//expect that the product has a default category for the new virtual catalog
		assertEquals("Product must have a default category for the new virtual catalog it was created in",
		    linkedCategory, product.getDefaultCategory(virtualCatalog));
	}

	/**
	 * Test that {@link CategoryServiceImpl#addLinkedCategory(Category, Category, Catalog)} calls
	 * {@link CategoryServiceImpl#addLinkedCategory(Category, Category, Catalog, int)} with a depth
	 * of zero.
	 */
	@Test
	public void testAddLinkedCategoryWithObjectsStartsWithZeroDepth() {
		final Category addedCategory = new CategoryImpl();
		final CategoryServiceImpl serviceImpl = new CategoryServiceImpl() {
			@Override
			protected Category addLinkedCategory(final Category master, final Category parent, final Catalog catalog, final int depth) {
				assertEquals("The initial depth of the tree of linked categories passed into the recursive method "
						+ "should be zero because there aren't any yet. ", 0, depth);
				return addedCategory;
			}
		};
		final Category fakeCategory = context.mock(Category.class, "fakeCategory");
		final Catalog fakeCatalog = context.mock(Catalog.class, "fakeCatalog");
		assertEquals(addedCategory, serviceImpl.addLinkedCategory(fakeCategory, fakeCategory, fakeCatalog));
	}

	/**
	 * Test that the recursive {@link CategoryServiceImpl#addLinkedCategory(Category, Category, Catalog, int)}
	 * throws a DuplicateKeyException if a linked category for the given master category already exists
	 * in the given catalog.
	 */
	@Test(expected = DuplicateKeyException.class)
	public void testAddLinkedCategoryRecursiveChecksForDuplicates() {
		final CategoryServiceImpl serviceImpl = new CategoryServiceImpl() {
			@Override
			protected boolean linkedCategoryExists(final String categoryCode, final String catalogCode) {
				return true;
			}
		};
		final Category category = context.mock(Category.class, "category");
		context.checking(new Expectations() {
			{
				ignoring(category).getCode();
				ignoring(catalog).getCode();
				allowing(catalog).isMaster(); will(returnValue(false));
			}
		});

		serviceImpl.addLinkedCategory(category, null, catalog);
	}

	/**
	 * Test that {@link CategoryServiceImpl#updateProductsWithNewLinkedCategory(Category)}
	 * adds the given category to every product in the given category's master category.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testUpdateProductsWithNewLinkedCategory() {
		//Create a master category
		final Category masterCategory = context.mock(Category.class, "category");
		//Create a linked category, linked to the master category
		final Category linkedCategory = context.mock(Category.class, "linkedCategory");

		//Create a couple products to be in the master category
		final Collection<Product> products = new ArrayList<Product>();
		final Product product1 = new ProductImpl();
		final Product product2 = new ProductImpl();
		products.add(product1);
		products.add(product2);
		//Ensure that the product service returns the products when asked
		context.checking(new Expectations() {
			{
				allowing(masterCategory).getUidPk(); will(returnValue(1L));
				allowing(masterCategory).getCatalog(); will(returnValue(new CatalogImpl()));

				allowing(linkedCategory).getUidPk(); will(returnValue(2L));
				allowing(linkedCategory).getMasterCategory(); will(returnValue(masterCategory));
				allowing(linkedCategory).getGuid(); will(returnValue("LINKED_GUID"));
				allowing(linkedCategory).getCatalog(); will(returnValue(new CatalogImpl()));

				allowing(productService).findByCategoryUid(with(any(Long.class)), with(any(FetchGroupLoadTuner.class)));
					will(returnValue(products));
				exactly(products.size()).of(productService).saveOrUpdate(with(any(Product.class)));
				oneOf(productService).notifyCategoryUpdated(with(any(Category.class)));

				allowing(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)), with(any(Boolean.class)));
			}
		});

		final CategoryServiceImpl serviceImpl = new CategoryServiceImpl();
		serviceImpl.setProductService(productService);
		serviceImpl.setFetchPlanHelper(fetchPlanHelper);

		//TEST
		serviceImpl.updateProductsWithNewLinkedCategory(linkedCategory);

		for (final Product product : products) {
			assertTrue("Every product should now have the linked category",
					product.getCategories().contains(linkedCategory));
		}
	}

	/**
	 * Test that the recursive {@link CategoryServiceImpl#addLinkedCategory(Category, Category, Catalog, int)}
	 * recurses through every subcategory of the master category and creates a new linked category for each,
	 * setting the parent of every linked category properly.
	 */
	@Test
	public void testAddLinkedCategoryRecursiveRecurses() {
		beanFactoryExpectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.LINKED_CATEGORY, LinkedCategoryImpl.class);

		final String masterCategoryCode = "MASTER";
		final String masterSubCategoryCode = "SUB";
		final String catalogCode = "CATALOG";
		final int masterCategoryOrder = 5;
		final int masterSubCategoryOrder = 10;

		//Create a master category
		final Category masterCategory = context.mock(Category.class, "masterCategory");

		//Create a sub category for the master category
		final Category masterSubCategory = context.mock(Category.class, "masterSubCategory");

		//add the subcategory as a child to the master category
		final Set<Category> children = new HashSet<Category>();
		children.add(masterSubCategory);

		context.checking(new Expectations() {
			{
				allowing(catalog).getCode(); will(returnValue(catalogCode));
				allowing(catalog).isMaster(); will(returnValue(false));

				allowing(masterCategory).getCode(); will(returnValue(masterCategoryCode));
				allowing(masterCategory).getOrdering(); will(returnValue(masterCategoryOrder));

				allowing(masterSubCategory).getCode(); will(returnValue(masterSubCategoryCode));
				allowing(masterSubCategory).getOrdering(); will(returnValue(masterSubCategoryOrder));
				allowing(masterSubCategory).getChildren(); will(returnValue(Collections.emptySet()));
				allowing(masterSubCategory).getParent(); will(returnValue(masterCategory));

				allowing(masterCategory).getChildren(); will(returnValue(children));
			}
		});
		//Create a parent category
		final Category parentCategory = new CategoryImpl();
		parentCategory.setCatalog(catalog);

		final CategoryServiceImpl serviceImpl = new CategoryServiceImpl() {
			@Override
			protected boolean linkedCategoryExists(final String categoryCode, final String catalogCode) {
				return false; //not testing this
			}
			@Override
			protected void updateProductsWithNewLinkedCategory(final Category subCategory) {
				//not testing this
			}
			@Override
			protected Category saveOrMerge(final Category newLinkedCategory) {
				//instead of calling persistence engine, just return what we were given
				return newLinkedCategory;
			}
		};

		//Call the test method
		final Category topLinkedCategory = serviceImpl.addLinkedCategory(masterCategory, parentCategory, catalog);

		//Check the top linked category
		assertNotNull(topLinkedCategory); // fail fast
		assertTrue("Top Linked Category must be of type linked", topLinkedCategory.isLinked());
		assertEquals("Top Linked Category should be in the same catalog.", catalog, topLinkedCategory.getCatalog());
		assertEquals("Top Linked Category should have the master category that was passed in.",
				masterCategory, topLinkedCategory.getMasterCategory());
		assertEquals("Top Linked Category should have the parent category that was passed in.",
				parentCategory, topLinkedCategory.getParent());
		assertTrue("Top Linked Category should be 'included'.", topLinkedCategory.isIncluded());
		assertEquals("Top Linked Category should have the same ordering as its master category",
				masterCategory.getOrdering(), topLinkedCategory.getOrdering());
		//Check the top linked category's subcategory
		final Category newSubCat = topLinkedCategory.getChildren().iterator().next();
		assertNotNull(newSubCat);
		assertTrue("Linked subCategory must be of type linked", newSubCat.isLinked());
		assertEquals("Linked subCategory should be in the same catalog.", catalog, newSubCat.getCatalog());
		assertEquals("Linked subCategory should have the subcategory of the master category that was passed in.",
				masterSubCategory, newSubCat.getMasterCategory());
		assertEquals("Linked subCategory should have the top linked category as its parent category.",
				topLinkedCategory, newSubCat.getParent());
		assertTrue("Linked subCategory should be 'included'.", newSubCat.isIncluded());
		assertEquals("Linked subCategoryshould have the same ordering as its master category (the master's category's subcategory)",
				masterSubCategory.getOrdering(), newSubCat.getOrdering());
	}

	/**
	 * Test method for {@link CategoryServiceImpl#addLinkedCategoryProducts(Category)}.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testAddLinkedCategoryProducts() {
		final long categoryUid = 3L;
		final long subCategoryUid = 44L;

		final Category category = context.mock(Category.class, "linked category");
		final Category subCategory = context.mock(Category.class, "sub category/master category");
		final Product product = context.mock(Product.class);
		final ProductDao productDao = context.mock(ProductDao.class);

		final FetchPlanHelper fetchPlanHelper = context.mock(FetchPlanHelper.class, NEW_FETCH_PLAN_HELPER);
		categoryService.setFetchPlanHelper(fetchPlanHelper);
		categoryService.setProductDao(productDao);
		context.checking(new Expectations() {
			{
				allowing(category).getUidPk(); will(returnValue(categoryUid));
				allowing(category).isLinked(); will(returnValue(true));
				oneOf(category).setIncluded(true);
				allowing(subCategory).getUidPk(); will(returnValue(subCategoryUid));
				allowing(subCategory).isLinked(); will(returnValue(true));
				oneOf(subCategory).setIncluded(true);
				allowing(category).getChildren(); will(returnValue(Collections.<Category>singleton(subCategory)));
				allowing(category).getMasterCategory(); will(returnValue(subCategory));
				allowing(subCategory).getMasterCategory(); will(returnValue(subCategory));
				allowing(subCategory).getChildren(); will(returnValue(Collections.emptySet()));

				// once for the category and once for the sub-category
				exactly(2).of(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)), with(true));
				exactly(2).of(fetchPlanHelper).clearFetchPlan();

				exactly(2).of(product).addCategory(category);
				oneOf(productService).findByCategoryUid(with(subCategoryUid), with(any(FetchGroupLoadTuner.class)));
					will(returnValue(Arrays.asList(product, product)));
				oneOf(productService).findByCategoryUid(with(subCategoryUid), with(any(FetchGroupLoadTuner.class)));
					will(returnValue(Collections.emptyList()));
				exactly(2).of(productDao).saveOrUpdate(product); will(returnValue(product));
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CATEGORY, subCategoryUid);
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CATEGORY, categoryUid);
				oneOf(persistenceEngine).saveOrMerge(category); will(returnValue(category));
				oneOf(persistenceEngine).saveOrMerge(subCategory); will(returnValue(subCategory));
				exactly(2).of(productService).notifyCategoryUpdated(category);
				exactly(2).of(productService).notifyCategoryUpdated(subCategory);

				// New expectations with [BN-468]
				allowing(category).getParent(); will(returnValue(null));
				allowing(subCategory).getParent(); will(returnValue(category));
				oneOf(persistenceEngine).evictObjectFromCache(category);
			}
		});

		categoryService.addLinkedCategoryProducts(category);
	}

	/**
	 * Test method for {@link CategoryServiceImpl#removeCategoryProducts(Category)}.
	 */
	@Test
	public void testRemoveCategoryProducts() {
		final long categoryUid = 3L;
		final long subCategoryUid = 44L;

		final Category category = context.mock(Category.class, "linked category");
		final Category subCategory = context.mock(Category.class, "sub category/master category");

		final FetchPlanHelper fetchPlanHelper = context.mock(FetchPlanHelper.class, NEW_FETCH_PLAN_HELPER);
		categoryService.setFetchPlanHelper(fetchPlanHelper);
		context.checking(new Expectations() {
			{
				allowing(category).getUidPk(); will(returnValue(categoryUid));
				allowing(category).isLinked(); will(returnValue(true));
				oneOf(category).setIncluded(false);
				allowing(subCategory).getUidPk(); will(returnValue(subCategoryUid));
				allowing(subCategory).isLinked(); will(returnValue(true));
				oneOf(subCategory).setIncluded(false);
				allowing(category).getChildren(); will(returnValue(Collections.<Category>singleton(subCategory)));
				allowing(category).getMasterCategory(); will(returnValue(subCategory));
				allowing(subCategory).getMasterCategory(); will(returnValue(subCategory));
				allowing(subCategory).getChildren(); will(returnValue(Collections.emptySet()));

				oneOf(persistenceEngine).executeNamedQuery("PRODUCTCATEGORY_DELETE_BY_CATEGORY_UID", categoryUid);
					will(returnValue(1));
				oneOf(persistenceEngine).executeNamedQuery("PRODUCTCATEGORY_DELETE_BY_CATEGORY_UID", subCategoryUid);
					will(returnValue(1));
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CATEGORY, categoryUid);
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.CATEGORY, subCategoryUid);
				oneOf(persistenceEngine).saveOrMerge(category); will(returnValue(category));
				oneOf(persistenceEngine).saveOrMerge(subCategory); will(returnValue(subCategory));
				exactly(2).of(productService).notifyCategoryUpdated(category);
				exactly(2).of(productService).notifyCategoryUpdated(subCategory);

				// New expectations with [BN-468]
				allowing(category).getParent(); will(returnValue(null));
				allowing(subCategory).getParent(); will(returnValue(category));
				oneOf(persistenceEngine).evictObjectFromCache(category);
			}
		});

		categoryService.removeCategoryProducts(category);
	}

	/**
	 * Tests the findByCompoundGuid method.
	 */
	@Test
	public void testFindByCompoundGuid() {
		final String code = CATEGORY_CODE_CATALOG_CODE;
		final Category category = new CategoryImpl();
		CategoryService categoryService = new CategoryServiceImpl() {
			 @Override
			public Category findByGuid(final String categoryGuid, final String catalogGuid) {
				assertEquals(CATEGORY_CODE, categoryGuid);
				assertEquals(CATALOG_CODE, catalogGuid);
				return category;
			}

		};

		assertSame(category, categoryService.findByCompoundGuid(code));

		categoryService = new CategoryServiceImpl() {
			 @Override
			public Category findByGuid(final String categoryGuid, final String catalogGuid) {
				 throw new EpServiceException("Expected exception");
			}

		};

		assertNull(categoryService.findByCompoundGuid(code));
	}



	/**
	 * Tests the findUidByCompoundCategoryGuid method.
	 */
	@Test
	public void testFindUidByCompoundCategoryGuid() {
		final String code = CATEGORY_CODE_CATALOG_CODE;
		final Long uid = 1L;
		final CategoryService categoryService = new CategoryServiceImpl() {
			@Override
			public Category findByCompoundGuid(final String compoundGuid) {
				assertEquals(code, compoundGuid);
				final Category category = new CategoryImpl();
				category.setUidPk(uid);
				return category;
			}
		};

		assertEquals(uid, categoryService.findUidByCompoundGuid(code));
	}

	/**
	 * Tests the categoryExistsWithCompoundGuid method.
	 */
	@Test
	public void testCategoryExistsWithCompoundGuid() {
	    final String code = CATEGORY_CODE_CATALOG_CODE;

	    //category exists
	    final Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("categoryCode", CATEGORY_CODE);
	    parameters.put("catalogCode", CATALOG_CODE);
	    context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_EXISTS_FOR_COMPOUND_GUID", parameters);
				will(returnValue(Collections.singletonList(1L)));
			}
		});

	    final boolean result = categoryService.categoryExistsWithCompoundGuid(code);
	    assertTrue("Category Should exist", result);

	}

	/**
	 * Tests the categoryExistsWithCompoundGuid method.
	 */
	@Test
	public void testLinkedCategoryExistsWithCompoundGuid() {
	    final String code = CATEGORY_CODE_CATALOG_CODE;
	    //category doesn't exist, but linked category does.
	    final Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("categoryCode", CATEGORY_CODE);
	    parameters.put("catalogCode", CATALOG_CODE);
	    context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_EXISTS_FOR_COMPOUND_GUID", parameters);
				will(returnValue(Collections.emptyList()));

				oneOf(persistenceEngine).retrieveByNamedQuery("LINKED_CATEGORY_EXISTS_FOR_COMPOUND_GUID", parameters);
				will(returnValue(Collections.singletonList(1L)));
			}
		});

	    final boolean result = categoryService.categoryExistsWithCompoundGuid(code);
	    assertTrue("Category Should exist", result);
	}

	/**
	 * Tests the categoryExistsWithCompoundGuid method.
	 */
	@Test
	public void testCategoryDoesNotExistsWithCompoundGuid() {

	    //neither category nor linked category exists.
	    final String code = CATEGORY_CODE_CATALOG_CODE;

	    final Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("categoryCode", CATEGORY_CODE);
	    parameters.put("catalogCode", CATALOG_CODE);
	    context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_EXISTS_FOR_COMPOUND_GUID", parameters);
				will(returnValue(Collections.emptyList()));

				oneOf(persistenceEngine).retrieveByNamedQuery("LINKED_CATEGORY_EXISTS_FOR_COMPOUND_GUID", parameters);
				will(returnValue(Collections.emptyList()));
			}
		});

	    final boolean result = categoryService.categoryExistsWithCompoundGuid(code);
	    assertFalse("Category Should not exist", result);

	}

	/**
	 * Tests findByCompoundGuid() method.
	 */
	@Test
	public void testFindByCompoundGuidWithFetchGroupLoadTuner() {
		final String catalogGuid = "catalog";
		final String categoryGuid = "category";
		final String compoundGuid = categoryGuid + "|" + catalogGuid;
		final FetchGroupLoadTuner fetchGroupLoadTuner = new FetchGroupLoadTunerImpl();
		final List<Category> categories = new ArrayList<Category>();
		categories.add(new CategoryImpl());

		context.checking(new Expectations() {
			{
				exactly(2).of(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));
				oneOf(catalogService).findByCode(catalogGuid); will(returnValue(catalog));
				oneOf(persistenceEngine).retrieveByNamedQuery(CATEGORY_FIND_BY_GUID_CATALOG, categoryGuid, catalog.getUidPk());
					will(returnValue(categories));
			}
		});

		final Category category = categoryService.findByCompoundGuid(compoundGuid, fetchGroupLoadTuner);
		assertNotNull(category);
	}

	/**
	 * Test assertions when category UID is persisted.
	 */
	@Test
	public void testFindDirectDescendantCategoriesWithExistingCategoryUid() {
		final List<Category> expectedCategories = new ArrayList<Category>();
		expectedCategories.add(new CategoryImpl());

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(CATEGORY_LIST_SUBCATEGORY, EXISTING_CATEGORY_UID);
				will(returnValue(expectedCategories));
			}
		});

		final List<Category> categories = categoryService.findDirectDescendantCategories(EXISTING_CATEGORY_UID);
		assertEquals("The returned categories should be the same as expected", expectedCategories, categories);
	}

	/**
	 * Test assertions when category UID is not persisted.
	 */
	@Test
	public void testFindDirectDescendantCategoriesWithZeroCategoryUid() {
		final List<Category> categories = categoryService.findDirectDescendantCategories(0);
		assertNotNull(categories);
		assertTrue("There should be no categories returned.", categories.isEmpty());
	}

	/**
	 * Tests method CategoryServiceImpl.findAncestorCategoryUidsWithTreeOrder().
	 */
	@Test
	public void testFindAncestorCategoryUidsWithTreeOrder() {
		final List<Long> expectedReturnByNamedQuery = new ArrayList<Long>(Arrays.asList(1L, 2L));
		final Set<Long> expectedInputForSecondTime = new HashSet<Long>(expectedReturnByNamedQuery);

		final Set<Long> categoryUidSet = new HashSet<Long>();
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQueryWithList("CATEGORY_UID_SELECT_BY_CHILDREN_UIDS", "list", categoryUidSet);
					will(returnValue(expectedReturnByNamedQuery));
				oneOf(persistenceEngine).retrieveByNamedQueryWithList("CATEGORY_UID_SELECT_BY_CHILDREN_UIDS", "list", expectedInputForSecondTime);
					will(returnValue(Collections.emptyList()));
			}
		});

		final Set<Long> resultSet = categoryService.findAncestorCategoryUidsWithTreeOrder(categoryUidSet);

		final Set<Long> expectedSet = new LinkedHashSet<Long>(Arrays.asList(1L, 2L));
		assertEquals(resultSet.size(), expectedSet.size());

		//check the correct order of UIDs, because implementation of returned set is a LinkedHashSet
		final Iterator<Long> resultIterator = resultSet.iterator();
		final Iterator<Long> expecedIterator = expectedSet.iterator();
		assertEquals(expecedIterator.next(), resultIterator.next());
		assertEquals(expecedIterator.next(), resultIterator.next());
	}

	/**
	 * Tests that adding a linked category to a master catalog is disabled.
	 */
	@Test(expected = IllegalOperationException.class)
	public void testAddingLinkedCategoryToMasterCatalog() {
		CategoryServiceImpl service = new CategoryServiceImpl();

		Category masterCategory = context.mock(Category.class);
		Category parentCategory = context.mock(Category.class, "parentCategory");

		context.checking(new Expectations() { {
			oneOf(catalog).isMaster(); will(returnValue(true));
		} });

		service.addLinkedCategory(masterCategory, parentCategory, catalog, 0);
	}

	/** Test finding a maximum value where the catalog exists. */
	@Test
	public void testFindMaxRootOrderingCatalogExists() {
		final long catalogUid = 5515;
		final int ordering = 151;
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_ROOT_MAX_ORDERING", catalogUid);
				will(returnValue(Collections.singletonList(ordering)));
			}
		});

		assertEquals("Unexpected ordering", ordering, categoryService.findMaxRootOrdering(catalogUid));
	}

	/** Test finding a maximum value where the catalog does not exist. */
	@Test
	public void testFindMaxRootOrderingCatalogNotExists() {
		final long catalogUid = 55689;
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_ROOT_MAX_ORDERING", catalogUid);
				will(returnValue(Collections.emptyList()));
			}
		});

		assertEquals("Zero should be returned for unknown catalogs", 0, categoryService.findMaxRootOrdering(catalogUid));
	}

	/** Test finding a minimum value where the catalog exists. */
	@Test
	public void testFindMinRootOrderingCatalogExists() {
		final long catalogUid = 1245561;
		final int ordering = 88;
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_ROOT_MIN_ORDERING", catalogUid);
				will(returnValue(Collections.singletonList(ordering)));
			}
		});

		assertEquals("Unexpected ordering", ordering, categoryService.findMinRootOrdering(catalogUid));
	}

	/** Test finding a minimum value where the catalog does not exist. */
	@Test
	public void testFindMinRootOrderingCatalogNotExists() {
		final long catalogUid = 7872;
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_ROOT_MIN_ORDERING", catalogUid);
				will(returnValue(Collections.emptyList()));
			}
		});

		assertEquals("Zero should be returned for unknown catalogs", 0, categoryService.findMinRootOrdering(catalogUid));
	}

	/** Test finding a maximum value where the parent category exists. */
	@Test
	public void testFindMaxChildOrderingParentExists() {
		final Category category = context.mock(Category.class);
		final int ordering = 151;
		context.checking(new Expectations() {
			{
				final long categoryUid = 14515;
				allowing(category).getUidPk();
				will(returnValue(categoryUid));

				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_CHILD_MAX_ORDERING", categoryUid);
				will(returnValue(Collections.singletonList(ordering)));
			}
		});

		assertEquals("Ordering was not returned", ordering, categoryService.findMaxChildOrdering(category));
	}

	/** Test finding a maximum value where the parent category does not exists. */
	@Test
	public void testFindMaxChildOrderingParentNotExists() {
		final Category category = context.mock(Category.class);
		context.checking(new Expectations() {
			{
				final long categoryUid = 14515;
				allowing(category).getUidPk();
				will(returnValue(categoryUid));

				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_CHILD_MAX_ORDERING", categoryUid);
				will(returnValue(Collections.emptyList()));
			}
		});

		assertEquals("Zero should be returned for unknown parents", 0, categoryService.findMaxChildOrdering(category));
	}

	/** Test finding a minimum value where the parent category exists. */
	@Test
	public void testFindMinChildOrderingParentExists() {
		final Category category = context.mock(Category.class);
		final int ordering = 88;
		context.checking(new Expectations() {
			{
				final long categoryUid = 56008;
				allowing(category).getUidPk();
				will(returnValue(categoryUid));

				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_CHILD_MIN_ORDERING", categoryUid);
				will(returnValue(Collections.singletonList(ordering)));
			}
		});

		assertEquals("Ordering was not returned", ordering, categoryService.findMinChildOrdering(category));
	}

	/** Test finding a minimum value where the parent category does not exist. */
	@Test
	public void testFindMinChildOrderingParentNotExists() {
		final Category category = context.mock(Category.class);
		context.checking(new Expectations() {
			{
				final long categoryUid = 600018;
				allowing(category).getUidPk();
				will(returnValue(categoryUid));

				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_CHILD_MIN_ORDERING", categoryUid);
				will(returnValue(Collections.emptyList()));
			}
		});

		assertEquals("Zero should be returned for unknown parents", 0, categoryService.findMinChildOrdering(category));
	}

	/**
	 * A test implementation of the product service.
	 */
	private final class TestProductService extends ProductServiceImpl {
		private final Product product;

		protected TestProductService(final Product product) {
			this.product = product;
		}

		@Override
		public Collection<Product> findByCategoryUid(final long categoryUid, final FetchGroupLoadTuner loadTuner) {
			return Arrays.asList(product);
		}

		@Override
		public Product saveOrUpdate(final Product product) throws EpServiceException {
			return product;
		}

		@Override
		public void notifyCategoryUpdated(final Category category) {
			// does nothing
		}
	}

	/**
	 * @return the master catalog singleton
	 */
	private Catalog getCatalog() {
		if (masterCatalog == null) {
			masterCatalog = new CatalogImpl();
			masterCatalog.setMaster(true);
			masterCatalog.setCode("irrelevent catalog code");
		}

		return masterCatalog;
	}

	/**
	 * @return a new <code>Category</code> instance.
	 */
	protected Category getCategory() {
		final Category category = new CategoryImpl();
		category.initialize();
		category.setCode((new RandomGuidImpl()).toString());
		category.setCatalog(getCatalog());

		return category;
	}
}
