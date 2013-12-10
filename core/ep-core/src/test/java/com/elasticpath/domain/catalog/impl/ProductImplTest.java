/*
 * Copyright (c) Elastic Path Software Inc., 2006
 *
 */
package com.elasticpath.domain.catalog.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeValueGroupFactoryImpl;
import com.elasticpath.domain.attribute.impl.ExtAttributeValueFactoryTestImpl;
import com.elasticpath.domain.attribute.impl.ExtAttributeValueGroupFactoryTestImpl;
import com.elasticpath.domain.attribute.impl.ExtAttributeValueGroupTestImpl;
import com.elasticpath.domain.attribute.impl.ExtAttributeValueTestImpl;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductCategory;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.localization.LocaleFallbackPolicy;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;

/**
 * Test <code>ProductImpl</code>.
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.TooManyStaticImports", "PMD.TooManyMethods" })
public class ProductImplTest {

	private static final String GUID_1 = "test product 1";

	private static final String GUID_2 = "test product 2";

	private ProductImpl productImpl;

	private ProductType productType;

	/** The non-default locale must be a completely different language. */
	private static final Locale NON_DEFAULT_LOCALE = Locale.ITALIAN;
	private static final Locale CATALOG_DEFAULT_LOCALE = Locale.GERMANY;
	private static final Locale CATALOG_DEFAULT_LOCALE_LANGUAGE = Locale.GERMAN;
	private static final String DISPLAYNAME_DEFAULT_LOCALE = "TestDisplayName";

    @Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final String SALES_TAX_CODE_BOOKS = "BOOKS";

	private static final String SALES_TAX_CODE_DVDS = "DVDS";

	private static final String SALES_TAX_CODE_CLOTHING = "CLOTHING";
	
	private Catalog catalog;

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;


	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of error happens
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		productImpl = new ProductImpl();

		catalog = context.mock(Catalog.class);
		context.checking(new Expectations() {
			{
				allowing(catalog).isMaster(); will(returnValue(true));
				allowing(catalog).getUidPk(); will(returnValue(1L));
				allowing(catalog).getCode(); will(returnValue("master"));
			}
		});
		productImpl.initialize();
		productImpl.setCode(GUID_1);
		
		productType = new ProductTypeImpl();
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test class for the localeFallbackPolicy.
	 *
	 */
	private class PredefinedLocaleFallbackPolicy implements LocaleFallbackPolicy {
		@Override
		public void addLocale(final Locale locale) {
			// not needed
		}
		public java.util.List<Locale> getLocales() {
			return Arrays.asList(new Locale("en"), new Locale("fr"));
		};
		
		@Override
		public Locale getPrimaryLocale() {
			return new Locale("en");
		}
		@Override
		public void setPreferredLocales(final Locale... locales) {
			//not needed
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.getStartDate()'.
	 */
	@Test
	public void testGetStartDate() {
		assertNotNull(productImpl.getStartDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.setStartDate(Date)'.
	 */
	@Test
	public void testSetStartDate() {
		final Date date = new Date();
		productImpl.setStartDate(date);
		assertSame(date, productImpl.getStartDate());
	}


	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.getEndDate()'.
	 */
	@Test
	public void testGetEndDate() {
		assertNull(productImpl.getEndDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.setEndDate(Date)'.
	 */
	@Test
	public void testSetEndDate() {
		final Date date = new Date();
		productImpl.setEndDate(date);
		assertSame(date, productImpl.getEndDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.getLastModifiedDate()'.
	 */
	@Test
	public void testGetLastModifiedDate() {
		assertNull(productImpl.getLastModifiedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.setLastModifiedDate(Date)'.
	 */
	@Test
	public void testSetLastModifiedDate() {
		final Date date = new Date();
		productImpl.setLastModifiedDate(date);
		assertSame(date, productImpl.getLastModifiedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.getTemplate()'.
	 */
	@Test
	public void testGetTemplate() {
		final ProductType productType = new ProductTypeImpl();
		final String templateName = "testTempalte";
		productType.setTemplate(templateName);
		productImpl.setProductType(productType);
		assertSame(templateName, productImpl.getTemplate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.compareTo()'.
	 */
	@Test
	public void testCompareTo() {
		final ProductImpl productImpl2 = new ProductImpl();
		productImpl2.initialize();
		productImpl2.setCode((new RandomGuidImpl()).toString());

		// New products are different
		assertFalse(0 == productImpl.compareTo(productImpl2));

		// compare by guid
		productImpl.setGuid(GUID_1);
		productImpl2.setGuid(GUID_2);
		assertTrue(productImpl.compareTo(productImpl2) < 0);

		// compare the same one
		productImpl.setGuid(GUID_1);
		productImpl2.setGuid(GUID_1);
		assertEquals(0, productImpl.compareTo(productImpl2));
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.setDefaultValues()'.
	 */
	@Test
	public void testInitialize() {
		final Product productImpl = new ProductImpl();

		assertNull(productImpl.getGuid());
		assertNull(productImpl.getStartDate());
		productImpl.initialize();
		productImpl.setCode((new RandomGuidImpl()).toString());
		assertNotNull(productImpl.getGuid());
		assertNotNull(productImpl.getStartDate());

		// set default values again, no value should be changed.
		final Date date = productImpl.getStartDate();
		final String guid = productImpl.getGuid();
		productImpl.initialize();
		assertSame(guid, productImpl.getGuid());
		assertSame(date, productImpl.getStartDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.addCategory()'.
	 */
	@Test
	public void testAddCategory() {
		final Category category = context.mock(Category.class);
		context.checking(new Expectations() {
			{
				allowing(category).getCatalog(); will(returnValue(catalog));
			}
		});
		productImpl.addCategory(category);

		Set<ProductCategory> productCategories = productImpl.getProductCategories();
		assertEquals("There should be one product category", 1, productCategories.size());
		
		ProductCategory productCategoryResult = productCategories.iterator().next();
		assertSame("Category should be the one added", category, productCategoryResult.getCategory());
		assertSame("Product should be the one we added the category to", productImpl, productCategoryResult.getProduct());
		assertTrue("Category should be the default", productCategoryResult.isDefaultCategory());
		
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.addProductCategory()'.
	 */
	@Test
	public void testAddProductCategoryAgain() {
		final Category category = context.mock(Category.class);
		context.checking(new Expectations() {
			{
				allowing(category).getCatalog(); will(returnValue(catalog));
			}
		});
		productImpl.addCategory(category);
		final int size1 = productImpl.getProductCategories().size();

		productImpl.addCategory(category);
		final int size2 = productImpl.getProductCategories().size();

		assertEquals(size1, size2);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.addProductCategory()'.
	 */
	@Test
	public void testAddProductCategoryWithNull() {
		final int size1 = productImpl.getProductCategories().size();
		// add null should do nothing
		productImpl.addCategory(null);

		final int size2 = productImpl.getProductCategories().size();
		assertEquals(size1, size2);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.getProductCategories()'.
	 */
	@Test
	public void testGetProductCategories() {
		assertNotNull(productImpl.getProductCategories());
		assertEquals(0, productImpl.getProductCategories().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.getCategories()'.
	 */
	@Test
	public void testGetCategories() {
		final Category category = context.mock(Category.class);
		context.checking(new Expectations() {
			{
				allowing(category).getCatalog(); will(returnValue(catalog));
			}
		});
		productImpl.addCategory(category);
		assertSame(category, productImpl.getCategories().iterator().next());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.removeCategory()'.
	 */
	@Test
	public void testRemoveCategory() {
		final Category category = context.mock(Category.class);
		context.checking(new Expectations() {
			{
				allowing(category).getCatalog(); will(returnValue(catalog));
			}
		});
		productImpl.addCategory(category);

		Set<ProductCategory> productCategories = productImpl.getProductCategories();
		assertEquals("There should be one product category", 1, productCategories.size());
		
		ProductCategory productCategoryResult = productCategories.iterator().next();
		assertSame("Category should be the one added", category, productCategoryResult.getCategory());
		assertSame("Product should be the one we added the category to", productImpl, productCategoryResult.getProduct());
		
		boolean exception = false;
		try {
			productImpl.removeCategory(category);
		} catch (EpDomainException e) {
			exception = true;
		}
		assertTrue("Should not be able to remove category as it will be set as default", exception);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.addCategory()'.
	 */
	@Test
	public void testAddCategorySetsDefault() {
		final Category category = context.mock(Category.class);
		context.checking(new Expectations() {
			{
				allowing(category).getCatalog(); will(returnValue(catalog));
			}
		});
		productImpl.addCategory(category);

		assertSame(productImpl.getDefaultCategory(category.getCatalog()), category);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.removeProductCategory()'.
	 */
	@Test
	public void testRemoveProductWithNull() {
		final int size1 = productImpl.getProductCategories().size();
		// remove null should do nothing
		productImpl.removeCategory(null);

		final int size2 = productImpl.getProductCategories().size();
		assertEquals(size1, size2);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.setProductCategories()'.
	 */
	@Test
	public void testSetProductCategories() {
		final Set<ProductCategory> productCategories = new TreeSet<ProductCategory>();
		Category cat = new CategoryImpl();
		ProductCategory productCat = new ProductCategoryImpl();
		productCat.setCategory(cat);
		productCat.setProduct(productImpl);

		productCategories.add(productCat);

		productImpl.setProductCategories(productCategories);
		assertSame(productCategories, productImpl.getProductCategories());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.setCategories()'. Add and remove categories.
	 */
	@Test
	public void testSetCategories() {
		final long threeL = 3L;

		Category catOne = new CategoryImpl();
		catOne.setUidPk(1L);
		catOne.setGuid(String.valueOf(1L));
		catOne.setCatalog(catalog);

		Category catTwo = new CategoryImpl();
		catTwo.setUidPk(2L);
		catTwo.setGuid(String.valueOf(2L));
		catTwo.setCatalog(catalog);

		Category catThree = new CategoryImpl();
		catThree.setUidPk(threeL);
		catThree.setGuid(String.valueOf(threeL));
		catThree.setCatalog(catalog);

		// product originally has cat1 and cat2
		productImpl.addCategory(catOne);
		productImpl.addCategory(catTwo);

		final Set<Category> categories = new TreeSet<Category>();
		categories.add(catOne);
		categories.add(catThree);

		// set to cat1 and cat3
		productImpl.setCategories(categories);

		assertEquals(2, productImpl.getProductCategories().size());
		Set<Category> newCats = productImpl.getCategories();
		assertTrue(newCats.contains(catOne));
		assertTrue(newCats.contains(catThree));
	}
	
	/**
	 * Test that trying to set the collection of categories to some collection
	 * that doesn't contain the default category is forbidden.
	 */
	@Test
	public void testSetCategoriesNotIncludingDefaultCategoryForbidden() {
		final long defaultCategoryUid = 1L;
		final long newCategoryUid = 2L;
		// Set default MasterCategory
		final Category defaultCategory = new CategoryImpl();
		defaultCategory.setGuid("defaultCategory");
		defaultCategory.setUidPk(defaultCategoryUid);
		defaultCategory.setCatalog(catalog);
		
		productImpl.setCategoryAsDefault(defaultCategory);
		
		//Create a set of new categories
		Category newCategory = new CategoryImpl();
		newCategory.setUidPk(newCategoryUid);
		newCategory.setGuid("newCategory");
		Set<Category> newCategories = new HashSet<Category>();
		newCategories.add(newCategory);
		
		try {
			productImpl.setCategories(newCategories);
			fail("Should not be able to set the collection of categories to which this "
					+ "Product belongs if none of them are the default category");
		} catch (EpDomainException ex) {
			assertNotNull(ex);
		}
	}
	
	/**
	 * Test that you can set the collection of categories to some
	 * collection that DOES contain the default master category.
	 */
	@Test
	public void testSetCategoriesIncludingDefaultCategoryPermitted() {
		final Category defaultMasterCategory = new CategoryImpl();
		defaultMasterCategory.setCatalog(catalog);
		productImpl.setCategoryAsDefault(defaultMasterCategory);
		
		Set<Category> newCategories = new HashSet<Category>();
		newCategories.add(defaultMasterCategory);
		
		productImpl.setCategories(newCategories);

		assertTrue(productImpl.getCategories().contains(defaultMasterCategory));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.setDefaultCategory()'.
	 */
	@Test
	public void testSetCategoryAsDefault() {
		// Set default category
		final Category category1 = new CategoryImpl();
		category1.setCatalog(catalog);
		category1.setGuid("category1");
		productImpl.setCategoryAsDefault(category1);
		assertSame(category1, productImpl.getDefaultCategory(category1.getCatalog()));

		// Default category is also added to the category set
		assertEquals(1, productImpl.getProductCategories().size());

		// Set the default category in a different catalog and check that the two defaults are set separately
		final Category category2 = new CategoryImpl();
		category2.setGuid("category2");
		category2.setCatalog(catalog);
		productImpl.setCategoryAsDefault(category2);
		assertSame(category2, productImpl.getDefaultCategory(category2.getCatalog()));

		// The original default category will still be in the category set
		assertEquals(2, productImpl.getProductCategories().size());
	}

	/**
	 * Test that setting a category as a default will set all the other categories
	 * that are in that category's catalog as non-default.
	 */
	@Test
	public void testSetCategoryAsDefaultUnsetsOtherCategoriesInSameCatalog() {
		//Set a category as default
		Category firstDefaultCategory = new CategoryImpl();
		firstDefaultCategory.setGuid("FirstDefaultCategory");
		firstDefaultCategory.setCatalog(catalog);
		productImpl.setCategoryAsDefault(firstDefaultCategory);
		//Set a different category as default
		Category secondDefaultCategory = new CategoryImpl();
		secondDefaultCategory.setGuid("SecondDefaultCategory");
		secondDefaultCategory.setCatalog(catalog);
		productImpl.setCategoryAsDefault(secondDefaultCategory);
		//Check
		assertEquals("There should be two product category objects", 2, productImpl.getProductCategories().size());
		for (ProductCategory productCategory : productImpl.getProductCategories()) {
			if (productCategory.equals(firstDefaultCategory)) {
				assertFalse("First default should no longer be default", productCategory.isDefaultCategory());
			} else if (productCategory.equals(secondDefaultCategory)) {
				assertTrue("Second default should be a default", productCategory.isDefaultCategory());
			}
		}
	}
	
	/**
	 * Test that a new ProductImpl has no default category in the master
	 * catalog if none has been set.
	 */
	@Test
	public void testGetDefaultCategory() {
		assertNull(productImpl.getDefaultCategory(catalog));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.getFullAttributeValues()'.
	 */
	@Test
	public void testGetFullAttributeValues() {
		final ProductType productType = context.mock(ProductType.class);
		this.productImpl.setProductType(productType);
		
		final AttributeGroup attributeGroup = context.mock(AttributeGroup.class);

		context.checking(new Expectations() {
			{
				oneOf(productType).getProductAttributeGroup(); will(returnValue(attributeGroup));
				oneOf(attributeGroup).getAttributeGroupAttributes(); will(returnValue(Collections.emptySet()));
			}
		});
		
		this.productImpl.getFullAttributeValues(Locale.ENGLISH);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.getAttributeValues()'.
	 */
	@Test
	public void testGetAttributeValues() {
		final ProductType productType = context.mock(ProductType.class);
		this.productImpl.setProductType(productType);

		final AttributeGroup attributeGroup = context.mock(AttributeGroup.class);

		context.checking(new Expectations() {
			{
				oneOf(productType).getProductAttributeGroup(); will(returnValue(attributeGroup));
				oneOf(attributeGroup).getAttributeGroupAttributes(); will(returnValue(Collections.emptySet()));
			}
		});
		
		this.productImpl.getAttributeValues(Locale.ENGLISH);
	}
	
	/**
	 * Test method for {@link ProductImpl#getAttributeValueGroup()} when
	 * {@link ProductImpl#getAttributeValueMap()} is already populated.
	 */
	@Test
	public void testGetAttributeValueGroupWithMapPopulated() {
		final Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
		final AttributeValue attributeValue = context.mock(AttributeValue.class);
		attributeValueMap.put("some key", attributeValue);

		ProductImpl productImpl = new ProductImpl();
		productImpl.setAttributeValueMap(attributeValueMap);

		assertSame("Map should be set on group",
				attributeValueMap, productImpl.getAttributeValueGroup().getAttributeValueMap());
		assertSame("Sanity Check",
				attributeValue, productImpl.getAttributeValueGroup().getAttributeValue("some key", null));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.setLocaleDependantFieldsMap(Map)'.
	 */
	@Test
	public void testSetLocaleDependantFieldsMap() {
		assertNotNull(this.productImpl.getLocaleDependantFieldsMap());

		Map<Locale, LocaleDependantFields> map = new HashMap<Locale, LocaleDependantFields>();
		this.productImpl.setLocaleDependantFieldsMap(map);
		assertSame(map, this.productImpl.getLocaleDependantFieldsMap());
	}
	
	/**
	 * Test that the display name can be correctly retrieved.
	 */
	@Test
	public void testGetDisplayName() {
		final LocaleFallbackPolicy policy = context.mock(LocaleFallbackPolicy.class);
		final CatalogLocaleFallbackPolicyFactory factory = new CatalogLocaleFallbackPolicyFactory() {
			@Override
			public LocaleFallbackPolicy createProductLocaleFallbackPolicy(final Locale primaryLocale, final boolean fallback, final Product product) {
				return policy;
			}
		};
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.LOCALE_FALLBACK_POLICY_FACTORY, factory);

		//set up policy context
		context.checking(new Expectations() {
			{
				allowing(policy).getLocales(); will(returnValue(new ArrayList<Locale>(
						Arrays.asList(CATALOG_DEFAULT_LOCALE, CATALOG_DEFAULT_LOCALE_LANGUAGE))));
				ignoring(policy);
			}
		});
		
		productImpl.setLocaleDependantFieldsMap(createLdfMapWithOneLdf(DISPLAYNAME_DEFAULT_LOCALE, CATALOG_DEFAULT_LOCALE));
		assertEquals("Default locale display name should be same as product display name", 
				DISPLAYNAME_DEFAULT_LOCALE, productImpl.getDisplayName(CATALOG_DEFAULT_LOCALE));
	}
	
	/**
	 * Test that getDisplayName() falls back to the default locale if no name
	 * is available in the given locale.
	 */
	@Test
	public void testGetDisplayNameFallsBack() {
		
		final LocaleFallbackPolicy policy = context.mock(LocaleFallbackPolicy.class);
		final CatalogLocaleFallbackPolicyFactory factory = new CatalogLocaleFallbackPolicyFactory() {
			@Override
			public LocaleFallbackPolicy createProductLocaleFallbackPolicy(final Locale primaryLocale, final boolean fallback, final Product product) {
				return policy;
			}
		};
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.LOCALE_FALLBACK_POLICY_FACTORY, factory);
		//set up policy context
		context.checking(new Expectations() {
			{
				allowing(policy).getLocales(); will(returnValue(new ArrayList<Locale>(
						Arrays.asList(NON_DEFAULT_LOCALE, CATALOG_DEFAULT_LOCALE, CATALOG_DEFAULT_LOCALE_LANGUAGE))));
				ignoring(policy);
			}
		});
		productImpl.setLocaleDependantFieldsMap(createLdfMapWithOneLdf(DISPLAYNAME_DEFAULT_LOCALE, CATALOG_DEFAULT_LOCALE));
		assertEquals("Display name should fall back to default locale.", DISPLAYNAME_DEFAULT_LOCALE, productImpl.getDisplayName(NON_DEFAULT_LOCALE));
	}
	
	/**
	 * Test that getDisplayName(Locale, boolean) doesn't fall back when it's not supposed to,
	 * and returns a null String instead.
	 */
	@Test
	public void testGetDisplayNameFallbackForbidden() {
		final LocaleFallbackPolicy policy = context.mock(LocaleFallbackPolicy.class);
		final CatalogLocaleFallbackPolicyFactory factory = new CatalogLocaleFallbackPolicyFactory() {
			@Override
			public LocaleFallbackPolicy createProductLocaleFallbackPolicy(final Locale primaryLocale, final boolean fallback, final Product product) {
				return policy;
			}
		};
		productImpl.setCode(null);

		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.LOCALE_FALLBACK_POLICY_FACTORY); will(returnValue(factory));
				oneOf(policy).getLocales();
				will(returnValue(new ArrayList<Locale>(Arrays.asList(NON_DEFAULT_LOCALE))));
				oneOf(policy).getPrimaryLocale(); will(returnValue(NON_DEFAULT_LOCALE));
				ignoring(policy);
			}
		});

		productImpl.setLocaleDependantFieldsMap(createLdfMapWithOneLdf(DISPLAYNAME_DEFAULT_LOCALE, CATALOG_DEFAULT_LOCALE));
		assertNull(productImpl.getDisplayName(NON_DEFAULT_LOCALE, false));
	}	
	
	private LocaleDependantFields createLocaleDependantFieldsWithDisplayName(final Locale locale, final String displayName) {
		LocaleDependantFields ldf = new ProductLocaleDependantFieldsImpl();
		ldf.setLocale(locale);
		ldf.setDisplayName(displayName);
		return ldf;
	}

	private Map<Locale, LocaleDependantFields> createLdfMapWithOneLdf(final String displayName, final Locale locale) {		
		Map<Locale, LocaleDependantFields> ldfMap = new HashMap<Locale, LocaleDependantFields>();
		ldfMap.put(locale, createLocaleDependantFieldsWithDisplayName(locale, displayName));
		return ldfMap;
	}

	/**
	 * Test falling back to empty ldf.
	 */
	@Test
	public void testGetLocaleDependantFieldsFallbackToEmptyLDF() {
		final LocaleFallbackPolicy policy = new PredefinedLocaleFallbackPolicy();
		productImpl.setLocaleDependantFieldsMap(new HashMap<Locale, LocaleDependantFields>());
		LocaleDependantFields result;

		result = productImpl.getLocaleDependantFields(policy);
		assertSame("If no LDF has been set, display name should be the product code.",
			productImpl.getCode(), result.getDisplayName());
	}

	/**
	 * Test matching correct locales in policy.
	 */
	@Test
	public void testMatchCorrectLocaleInLocaleFallbackPolicy() {
		final LocaleFallbackPolicy policy = new PredefinedLocaleFallbackPolicy();
		final Map<Locale, LocaleDependantFields> ldfMap = new HashMap<Locale, LocaleDependantFields>();

		productImpl.setLocaleDependantFieldsMap(ldfMap);
		LocaleDependantFields frLdf = context.mock(LocaleDependantFields.class, "french");
		LocaleDependantFields enLdf = context.mock(LocaleDependantFields.class, "english");
		//put only the secondary ldf into the map
		ldfMap.put(new Locale("fr"), frLdf);

		LocaleDependantFields result = productImpl.getLocaleDependantFields(policy);
		assertEquals("LDF should match second locale in policy", frLdf, result);

		//put the primary ldf into the map
		ldfMap.put(new Locale("en"), enLdf);

		result = productImpl.getLocaleDependantFields(policy);
		assertEquals("LDF should match first locale in policy", enLdf, result);
	}

	/**
	 * Tests that a product with two skus can have one of the skus looked up by guid. 
	 */
	@Test
	public void testGetSkuByGuidWithTwoSkus() {
		
		// Create first test sku
		ProductSku productSku = new ProductSkuImpl();
		productSku.setGuid("123");
		productSku.setSkuCode("124");
		
		// Create second test sku
		ProductSku productSku2 = new ProductSkuImpl();
		productSku2.setGuid("223");
		productSku2.setSkuCode("224");

		// Add the skus to a test product
		Product product = new ProductImpl();
		product.addOrUpdateSku(productSku);
		product.addOrUpdateSku(productSku2);
		
		// Lookup the second test sku on the product
		assertThat("Test sku with guid 223 should be returned.",
				product.getSkuByGuid("223"), is(sameInstance(productSku2)));
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.getDefaultSku()'.
	 */
	@Test
	public void testGetSetDefaultSku() {
		assertNull(productImpl.getDefaultSku());
		ProductSku testSku = new ProductSkuImpl();
		productImpl.setDefaultSku(testSku);
		assertSame(testSku, productImpl.getDefaultSku());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.getDefaultSku()'.
	 */
	@Test
	public void testGetSetDefaultSku2() {
		assertNull(productImpl.getDefaultSku());
		ProductSku testSku = new ProductSkuImpl();
		Map<String, ProductSku> skuSet = new HashMap<String, ProductSku>();
		skuSet.put("sku1_guid", testSku);
		productImpl.setProductSkus(skuSet);
		assertSame(testSku, productImpl.getDefaultSku());
	}

	/**
	 * Test that setting the productSkus map to null will
	 * simply clear the map, it won't null the map. 
	 */
	@Test
	public void testSetProductSkusToNullClearsMap() {
		ProductImpl productImpl = new ProductImpl();
		productImpl.setProductSkus(null);
		assertNotNull(productImpl.getProductSkus());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.addOrUpdateSku()'.
	 */
	@Test
	public void testAddOrUpdateSku() {
		this.productImpl.setGuid("product");
		this.productImpl.initialize();
		assertEquals(0, this.productImpl.getProductSkus().size());
		ProductSku productSku = this.productImpl.getDefaultSku();
		assertNull(productSku);
		
		productSku = new ProductSkuImpl();

		productSku.setSkuCode("skucode");
		productSku.setHeight(BigDecimal.ZERO);
		this.productImpl.addOrUpdateSku(productSku);

		assertEquals(1, this.productImpl.getProductSkus().size());
		assertTrue(this.productImpl.getProductSkus().containsValue(productSku));
		assertEquals(this.productImpl, productSku.getProduct());

		// add another sku
		productSku = new ProductSkuImpl();
		assertNotNull(productSku);
		productSku.setSkuCode("skucode2");
		productSku.setHeight(BigDecimal.ONE);
		this.productImpl.addOrUpdateSku(productSku);

		assertEquals(2, this.productImpl.getProductSkus().size());
		assertTrue(this.productImpl.getProductSkus().containsValue(productSku));
		assertEquals(this.productImpl, productSku.getProduct());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.getBrand()'.
	 */
	@Test
	public void testGetSetBrand() {
		assertNull(productImpl.getBrand());
		final Brand brand = new BrandImpl();
		brand.setGuid("brandGuid");
		productImpl.setBrand(brand);
		assertEquals(brand, productImpl.getBrand());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.hasMultipleSkus()'.
	 */
	@Test
	public void testHasMultipleSkus() {
		this.productImpl.initialize();
		this.productImpl.setProductType(this.productType);
		assertFalse(productImpl.hasMultipleSkus());

		this.productImpl.getProductType().setWithMultipleSkus(true);
		assertTrue(productImpl.hasMultipleSkus());

		this.productImpl.getProductType().setWithMultipleSkus(false);
		assertFalse(productImpl.hasMultipleSkus());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.addOrUpdateSku()'.
	 */
	@Test
	public void testRemoveSku() {
		this.productImpl.setGuid("product");
		this.productImpl.initialize();
		assertEquals(0, this.productImpl.getProductSkus().size());
		ProductSku productSku = this.productImpl.getDefaultSku();
		assertNull(productSku);
		productSku = new ProductSkuImpl();
		
		productSku.setSkuCode("skucode");
		productSku.setHeight(BigDecimal.ZERO);
		this.productImpl.addOrUpdateSku(productSku);

		assertEquals(1, this.productImpl.getProductSkus().size());
		assertTrue(this.productImpl.getProductSkus().containsValue(productSku));
		assertEquals(this.productImpl, productSku.getProduct());

		this.productImpl.removeSku(productSku);
		assertEquals(0, this.productImpl.getProductSkus().size());
		assertFalse(this.productImpl.getProductSkus().containsValue(productSku));
		assertNull(productSku.getProduct());
	}

	
	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductImpl.getSalesCount()'.
	 */
	@Test
	public void testGetSetSalesCount() {
		final int salesCount = 1;
		productImpl.setSalesCount(salesCount);
		assertEquals(salesCount, productImpl.getSalesCount());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductImpl.setCode(String)'.
	 */
	@Test
	public void testGetSetCode() {
		assertNotNull(productImpl.getCode());
		final String code1 = "testProduct1";
		productImpl.setCode(code1);
		assertSame(code1, productImpl.getCode());
		assertSame(code1, productImpl.getGuid());

		final String code2 = "testProduct2";
		productImpl.setGuid(code2);
		assertSame(code2, productImpl.getCode());
		assertSame(code2, productImpl.getGuid());

	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductImpl.getTaxCode()'.
	 */
	@Test
	public void testGetSetTaxCode() {
		final ProductTypeImpl productTypeImpl = new ProductTypeImpl();
		productImpl.setProductType(productTypeImpl);

		final TaxCode bookTaxCode = new TaxCodeImpl();
		final long bookTaxCodeUid = 100L;
		bookTaxCode.setUidPk(bookTaxCodeUid);
		bookTaxCode.setCode(SALES_TAX_CODE_BOOKS);
		productTypeImpl.setTaxCode(bookTaxCode);
		assertEquals(SALES_TAX_CODE_BOOKS, productImpl.getTaxCode().getCode());

		final TaxCode dvdTaxCode = new TaxCodeImpl();
		final long dvdTaxCodeUid = 101L;
		dvdTaxCode.setUidPk(dvdTaxCodeUid);
		dvdTaxCode.setCode(SALES_TAX_CODE_DVDS);
		productImpl.setTaxCode(dvdTaxCode);
		assertEquals(SALES_TAX_CODE_DVDS, productImpl.getTaxCode().getCode());

		productImpl.setTaxCode(bookTaxCode);
		assertEquals(SALES_TAX_CODE_BOOKS, productImpl.getTaxCode().getCode());
		final TaxCode clothingTaxCode = new TaxCodeImpl();
		final long clothingTaxCodeUid = 102L;
		clothingTaxCode.setUidPk(clothingTaxCodeUid);
		clothingTaxCode.setCode(SALES_TAX_CODE_CLOTHING);
		productImpl.setTaxCode(clothingTaxCode);
		assertEquals(SALES_TAX_CODE_CLOTHING, productImpl.getTaxCode().getCode());


	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductImpl.getMaxFeaturedProductOrder()'.
	 */
	@Test
	public void testGetMaxFeaturedProductOrder() {
		final int maxOrder = 3;
		for (int i = 1; i <= maxOrder; i++) {
			ProductCategory productCategory = new ProductCategoryImpl();
			final Category category = context.mock(Category.class, "CATEGORY_" + i);
			productCategory.setCategory(category);
			productCategory.setProduct(productImpl);
			productCategory.setFeaturedProductOrder(i);
			productImpl.getProductCategories().add(productCategory);
		}
		assertEquals(productImpl.getMaxFeaturedProductOrder(), maxOrder);
	}
	
	/** Creates a new ProductSku that will pretend that it has the given set of option value keys (e.g. {"RED", "LARGE"}). */
	private ProductSku createProductSkuWithOptionValueKeys(final Collection<String> optionValueKeys) {
		final Set<String> optionValueKeySet = new HashSet<String>(optionValueKeys);
		final ProductSku productSku = context.mock(ProductSku.class, "ProductSku" + optionValueKeys.hashCode());
		context.checking(new Expectations() {
			{
				allowing(productSku).getOptionValueKeys(); will(returnValue(optionValueKeySet));
			}
		});
		return productSku;
	}
	
	/**
	 * Test that if a Product contains a sku with specific option value codes,
	 * the findSkuWithOptionValueCodes method will find and return it.
	 */
	@Test
	public void testFindSkuWithOptionValueCodes() {
		final String medium = "MM";
		final String large = "LL";
		final String yellow = "YY";
		final String blue = "BB";
		final String purple = "PP";
		
		Set<String> mediumYellow = new HashSet<String>();
		Collections.addAll(mediumYellow, medium, yellow);
		ProductSku pskuMediumYellow = createProductSkuWithOptionValueKeys(mediumYellow);
		
		Set<String> mediumBlue = new HashSet<String>();
		Collections.addAll(mediumBlue, medium, blue);
		ProductSku pskuMediumBlue = createProductSkuWithOptionValueKeys(mediumBlue);
				
		final Map<String, ProductSku> productSkuMap = new HashMap<String, ProductSku>();
		productSkuMap.put("pskuMediumYellow", pskuMediumYellow);
		productSkuMap.put("pskuMediumBlue", pskuMediumBlue);
		
		//Make sure that our mock product skus are returned with any call to getProductSkus(),
		//because that's what the method under test will call.
		ProductImpl product = new ProductImpl() {
			private static final long serialVersionUID = -8245958525676015421L;

			@Override
			public Map<String, ProductSku> getProductSkus() {
				return productSkuMap;
			}
		};
		
		assertEquals("Should find the Sku with the first combination of option values",
				pskuMediumYellow,
				product.findSkuWithOptionValueCodes(mediumYellow));
		assertEquals("Should find the Sku with the second combination of option values",
				pskuMediumBlue,
				product.findSkuWithOptionValueCodes(mediumBlue));
		
		Set<String> largePurple = new HashSet<String>();
		Collections.addAll(mediumYellow, large, purple);
		assertNull("Should not find a Sku with an unkonwn combination of option values",
				product.findSkuWithOptionValueCodes(largePurple));
		
		assertNull("Should not find a Sku with null option values",
				product.findSkuWithOptionValueCodes(null));
		assertNull("Should not find a Sku with the empty combination of option values",
				product.findSkuWithOptionValueCodes(Collections.<String>emptySet()));
		
		List<String> blueMedium = new ArrayList<String>();
		Collections.addAll(blueMedium, blue, medium);
		assertEquals("Should find the Sku regardless of the order in which the option values are specified in the given collection",
				pskuMediumBlue,
				product.findSkuWithOptionValueCodes(blueMedium));
	}
	
	/**
	 * Test that removing all category associations doesn't
	 * remove the product from its default category in its
	 * master catalog.
	 */
	@Test
	public void testRemoveAllCategoryAssociations() {
		//Create a default category in the master catalog
		final Category primaryCategory = this.createCategory(1L);
		primaryCategory.setCatalog(catalog);
		final ProductCategory defaultProductCategory = new ProductCategoryImpl();
		defaultProductCategory.setCategory(primaryCategory);
		defaultProductCategory.setDefaultCategory(true);
		//Create a nonDefault category
		final Category nonDefaultCategory = this.createCategory(2L);
		nonDefaultCategory.setCatalog(catalog);
		final ProductCategory productCategory = new ProductCategoryImpl();
		productCategory.setCategory(nonDefaultCategory);
		
		final Set<ProductCategory> productCategories = new HashSet<ProductCategory>();
		productCategories.add(defaultProductCategory);
		productCategories.add(productCategory);
		
		ProductImpl product = new ProductImpl();
		product.addCategory(primaryCategory);
		product.addCategory(nonDefaultCategory);

		product.removeAllCategories();
		
		assertTrue("Default Category association should not be removed", 
				product.getCategories().contains(primaryCategory));
		assertFalse("All non-default category associations should be removed", 
				product.getCategories().contains(nonDefaultCategory));
	}
	
	private Category createCategory(final long uid) {
		Category category = new CategoryImpl();
		category.setUidPk(uid);
		category.setCode("CatCode" + uid);
		return category;
	}	
	
	/**
	 * Test that you can retrieve a product's featured rank
	 * in any category in which the product has an assocation.
	 */
	@Test
	public void testGetFeaturedRankInCategory() {
		final Category category = createCategory(1L);
		category.setCatalog(catalog);
		final ProductCategory productCategory = new ProductCategoryImpl();
		productCategory.setCategory(category);
		final int featuredRank = 5;
		productCategory.setFeaturedProductOrder(featuredRank);
		//ProductImpl will get the ProductCategory object for
		//the given category and ask it for its feature index
		ProductImpl product = new ProductImpl();
		product.addCategory(category);
		product.setFeaturedRank(category, featuredRank);

		assertEquals(featuredRank, product.getFeaturedRank(category));
	}
	
	/**
	 * Test that trying to retrieve a product's preferred featured index
	 * in a category in which the product has NO association will throw
	 * an IllegalArgumentException.
	 */
	@Test
	public void testGetFeaturedRankInUnassociatedCategory() {
		final String failureMessage = "Should throw IllegalArgumentException if the given category is not associated with the product.";
		ProductImpl product = new ProductImpl();
		Category category = createCategory(1L);
		try {
			product.getFeaturedRank(category);
			fail(failureMessage);
		} catch (IllegalArgumentException ex) {
			assertNotNull(failureMessage, ex);
		}
	}
	
	/**
	 * Test that setting or getting a featured rank in a category that is
	 * not associated with a product will throw an IllegalArgumentException.
	 */
	@Test
	public void testSetFeaturedRankInUnassociatedCategory() {
		final String failureMessage = "Should throw IllegalArgumentException if the given category is not associated with the product.";
		ProductImpl product = new ProductImpl();
		Category category = createCategory(1L);
		try {
			product.setFeaturedRank(category, 0);
			fail(failureMessage);
		} catch (IllegalArgumentException ex) {
			assertNotNull(failureMessage, ex);
		}
	}
	
	/**
	 * Test that when setting a category as a default category, it will be added to the collection of categories if not already in it.
	 */
	@Test
	public void testSetCategoryAsDefaultAddsCategory() {
		Category category = new CategoryImpl();
		category.setCatalog(catalog);
		productImpl.setCategoryAsDefault(category);
		assertTrue("Setting a category as a default category should add it", productImpl.getCategories().contains(category));
	}
	
	/**
	 * Test that setting a featured rank in a category that *IS*
	 * associated with the product succeeds.
	 */
	@Test
	public void testSetFeaturedRankSucceeds() {
		Category category = createCategory(1L);
		category.setCatalog(catalog);
		productImpl.addCategory(category);
		final int rank = 2;
		productImpl.setFeaturedRank(category, rank);
		assertEquals("The category is associated with the product, so setting the preferred rank should succeed.", 
				rank, productImpl.getFeaturedRank(category));			
	}

	/**
	 * Tests {@link ProductImpl#ensureProductHasDefaultCategory(Category, List)}.
	 * Creates a product with a catalog and calls the method. Checks if the product has
	 * a default category for this catalog.
	 */
	@Test
	public void testEnsureProductHasDefaultCategory() {
		Category category = createCategory(1L);
		category.setCatalog(catalog);
		Category defaultCategory = createCategory(2L);
		defaultCategory.setCatalog(catalog);
		
		productImpl.addCategory(category);
		// instead of adding the category and then remove it we directly pretend it was there and then removed
		productImpl.ensureProductHasDefaultCategory(defaultCategory, new ArrayList<Category>());
		
		assertNotNull(productImpl.getDefaultCategory(catalog));
		assertEquals(category, productImpl.getDefaultCategory(catalog));
	}

	/**
	 * Test the checking of whether we have a sku within the start, end date range.
	 * 
	 * @throws ParseException in case of date parsing error.
	 */
	@Test
	public void testHasSkusWithinDateRange() throws ParseException {
        final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        final String dateToTest = "2010-01-01";
        productType.setWithMultipleSkus(false);
        productImpl.setProductType(productType);
		assertTrue("Single-sku product always has sku within range", productImpl.hasSkuWithinDateRange(yyyyMMdd.parse(dateToTest)));

		productType.setWithMultipleSkus(true);
		ProductSku productSku = new ProductSkuImpl();
		productSku.setStartDate(yyyyMMdd.parse("2010-12-31"));
		productImpl.addOrUpdateSku(productSku);
		assertFalse("Multi-sku product with only a future sku won't be in range", productImpl.hasSkuWithinDateRange(yyyyMMdd.parse(dateToTest)));

		ProductSku productSku2 = new ProductSkuImpl();
		productSku2.setStartDate(yyyyMMdd.parse("2009-01-01"));
		productSku2.setEndDate(yyyyMMdd.parse("2009-12-31"));
		productImpl.addOrUpdateSku(productSku2);
		assertFalse("Multi-sku product with an expires sku won't be in range", productImpl.hasSkuWithinDateRange(yyyyMMdd.parse(dateToTest)));
		
		ProductSku productSku3 = new ProductSkuImpl();
		productSku3.setStartDate(yyyyMMdd.parse("2009-01-01"));
		productSku2.setEndDate(yyyyMMdd.parse("2010-12-31"));
		productImpl.addOrUpdateSku(productSku3);
		assertTrue("Multi-sku product with a valid sku will in range", productImpl.hasSkuWithinDateRange(yyyyMMdd.parse(dateToTest)));
		
	}

	/**
	 * Test that extension classes can override the AttributeValueGroup and ProductAttributeValue implementation classes.
	 */
	@Test
	public void testThatExtensionClassesCanOverrideAttributeValueImplementations() {
		AttributeImpl attribute = new AttributeImpl();
		attribute.setAttributeType(AttributeType.SHORT_TEXT);
		attribute.setName("name");
		attribute.setKey("name");

		ExtProductImpl product = new ExtProductImpl();
		product.getAttributeValueGroup().setStringAttributeValue(attribute, null, "beanie-weenie");

		assertEquals("AttributeValueGroup implementation class should have been overridden",
						ExtAttributeValueGroupTestImpl.class, product.getAttributeValueGroup().getClass());
		assertEquals("AttributeValueImpl implementation class should have been overridden",
				ExtAttributeValueTestImpl.class,
				product.getAttributeValueGroup().getAttributeValue("name", null).getClass());
	}

	/**
	 * Tests that sub-classes can override the LocaleDependantFields implementation class.
	 */
	@Test
	public void testExtensionClassesCanOverrideLocaleDependantFieldsFactory() {
		final CatalogLocaleFallbackPolicyFactory factory = new CatalogLocaleFallbackPolicyFactory();
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.LOCALE_FALLBACK_POLICY_FACTORY, factory);

		ExtProductImpl product = new ExtProductImpl();
		LocaleDependantFields ldf = product.getLocaleDependantFieldsWithoutFallBack(Locale.ENGLISH);

		assertTrue("Extension classes should be able to override LocaleDependantFields impl",
				ldf instanceof ExtProductLocaleDependantFieldsImpl);
	}

	/**
	 * Ensures that extension classes can override the implementation of ProductCategory.
	 */
	@Test
	public void testExtensionClassesCanOverrideProductCategoryImplementation() {
		ExtProductImpl product = new ExtProductImpl();
		Category category = createCategory(1L);
		category.setCatalog(catalog);
		product.addCategory(category);

		assertTrue("Extension class should be able to override product/category impl class",
				product.getProductCategories().iterator().next() instanceof ExtProductCategoryImpl);
	}

	/**
	 * Faux product domain extension class.
	 */
	private static class ExtProductImpl extends ProductImpl {
		private static final long serialVersionUID = -9142717254154816937L;

		@Override
		protected AttributeValueGroupFactoryImpl getAttributeValueGroupFactory() {
			return new ExtAttributeValueGroupFactoryTestImpl(new ExtAttributeValueFactoryTestImpl());
		}

		/**
		 * Creates an appropriate LocaleDependantFields value object for the given locale.  Override this method
		 * if you need to change the implementation class in an extension project.
		 *
		 * @param locale the Locale this LDF applies to
		 * @return a LocaleDependentFields object
		 */
		@Override
		protected LocaleDependantFields createLocaleDependantFields(final Locale locale) {
			final LocaleDependantFields ldf = new ExtProductLocaleDependantFieldsImpl();
			ldf.setLocale(locale);
			ldf.setDisplayName(this.getCode());

			return ldf;
		}

		@Override
		protected ProductCategory createProductCategory(final Category category) {
			final ProductCategory productCategory = new ExtProductCategoryImpl();
			productCategory.setCategory(category);
			productCategory.setProduct(this);
			return productCategory;
		}
	}

	/**
	 * Faux productLDF extension class.
	 */
	private static class ExtProductLocaleDependantFieldsImpl extends ProductLocaleDependantFieldsImpl
			implements LocaleDependantFields {
		private static final long serialVersionUID = -2555038814992390654L;
	}

	/**
	 * Faux Product/Category extension class.
	 */
	private static class ExtProductCategoryImpl extends ProductCategoryImpl {
		private static final long serialVersionUID = -2555038814992390654L;
	}

}
