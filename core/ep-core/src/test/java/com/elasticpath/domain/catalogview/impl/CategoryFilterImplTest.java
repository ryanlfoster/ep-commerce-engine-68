package com.elasticpath.domain.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>CategoryFilterImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class CategoryFilterImplTest  {

	private static final String EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED = "EpBindExceptionSearchRequest expected.";

	private static final Locale TEST_LOCALE = Locale.US;

	private static final String TEST_DISPLAY_NAME = "test display name";

	private static final long TEST_CATEGORY_UID = 8;

	private static final String TEST_CATEGORY_GUID = "8";

	private static final String TEST_ID = SeoConstants.CATEGORY_PREFIX + TEST_CATEGORY_UID;

	private CategoryFilterImpl categoryFilter;

	private CategoryService categoryService;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	private Catalog catalog;
	
	private FetchGroupLoadTuner loadTuner;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		categoryService = context.mock(CategoryService.class);
		loadTuner = context.mock(FetchGroupLoadTuner.class);
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.CATEGORY_SERVICE); will(returnValue(categoryService));
				allowing(beanFactory).getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER); will(returnValue(loadTuner));
				ignoring(loadTuner);
			}
		});
		catalog = context.mock(Catalog.class);

		this.categoryFilter = new CategoryFilterImpl();
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.CategoryFilterImpl.getId()'.
	 */
	@Test
	public void testGetId() {
		assertNull(categoryFilter.getId());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.CategoryFilterImpl.getDisplayName(Locale)'.
	 */
	@Test
	public void testGetDisplayName() {
		final Category category = getCategory();
		category.setUidPk(TEST_CATEGORY_UID);
		category.setGuid(TEST_CATEGORY_GUID);

		context.checking(new Expectations() {
			{
				allowing(categoryService).findByGuid(TEST_CATEGORY_GUID, null, loadTuner);
				will(returnValue(category));
			}
		});
		
		this.categoryFilter.initialize(TEST_ID);
		assertEquals(TEST_ID, categoryFilter.getId());
		assertEquals(TEST_DISPLAY_NAME, this.categoryFilter.getDisplayName(TEST_LOCALE));

		final CategoryFilterImpl anotherCategoryFilter = new CategoryFilterImpl();
		anotherCategoryFilter.initialize(TEST_ID);
		assertEquals(categoryFilter, anotherCategoryFilter);
		assertEquals(categoryFilter.hashCode(), anotherCategoryFilter.hashCode());
		assertFalse(categoryFilter.equals(new Object()));
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.CategoryFilterImpl.getDisplayName(Locale)'.
	 */
	@Test
	public void testGetDisplayNameWithoutInitialization() {
		try {
			this.categoryFilter.getDisplayName(TEST_LOCALE);
			fail("EpDomainException expected.");
		} catch (final EpDomainException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.CategoryFilterImpl.initialize()'.
	 */
	@Test
	public void testInitializeWithBadId() {
		try {
			this.categoryFilter.initialize("bad filter id");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			context.checking(new Expectations() {
				{
					allowing(categoryService).findByGuid(with(any(String.class)), with((Catalog) null), with(any(FetchGroupLoadTuner.class)));
					will(returnValue(null));
				}
			});
			this.categoryFilter.initialize("caaa");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.categoryFilter.initialize("cat");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.categoryFilter.initialize("aaa-333");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.categoryFilter.initialize("cat-3");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}
	}
	
	/**
	 * Test that a category filter can be initialized with a category code and a catalog.
	 */
	@Test
	public void testInitializeWithCode() {
		final String validCategoryCode = "validCode";
		final String invalidCategoryCode = "invalidCode";

		context.checking(new Expectations() {
			{
				oneOf(categoryService).findByGuid(validCategoryCode, catalog, loadTuner); will(returnValue(getCategory()));
			}
		});
		categoryFilter.initializeWithCode(validCategoryCode, catalog);

		// test invalid code
		context.checking(new Expectations() {
			{
				oneOf(categoryService).findByGuid(invalidCategoryCode, catalog, loadTuner);	will(returnValue(null));
			}
		});
		try {
			categoryFilter.initializeWithCode(invalidCategoryCode, catalog);
			fail("Expected EpCatalogViewRequestBindException for invalid catalog code.");
		} catch (EpCatalogViewRequestBindException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Returns a new <code>Category</code> instance.
	 * 
	 * @return a new <code>Category</code> instance.
	 */
	protected Category getCategory() {
		final Category category = new CategoryImpl() {
			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getDisplayName(final Locale locale) {
				return TEST_DISPLAY_NAME;
			}
		};
		category.setCode((new RandomGuidImpl()).toString());
		category.setCatalog(catalog);

		return category;
	}
}
