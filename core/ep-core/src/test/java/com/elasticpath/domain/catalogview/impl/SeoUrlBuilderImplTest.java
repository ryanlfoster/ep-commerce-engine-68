/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalogview.impl;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import com.elasticpath.domain.catalog.impl.CatalogLocaleFallbackPolicyFactory;
import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.UrlUtilityImpl;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalog.impl.CatalogLocaleImpl;
import com.elasticpath.domain.catalog.impl.CategoryLocaleDependantFieldsImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductLocaleDependantFieldsImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.BrandLocalizedPropertyValueImpl;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.test.jmock.AbstractCatalogDataTestCase;

/**
 * Test the SingleStoreSeoUrlBuilderImpl class works as expected.
 */
public class SeoUrlBuilderImplTest extends AbstractCatalogDataTestCase {

	/** The object under test. */
	private SeoUrlBuilderImpl urlBuilder;

	private static final int TEST_BRAND_ID_15 = 15;
	private static final String TEST_BRAND_GUID_15 = "GUID15";
	private static final int TEST_CATEGORY_ID_11 = 11;
	private static final int TEST_CATEGORY_ID_15 = 15;
	private static final Locale STORES_DEFAULT_LOCALE = Locale.GERMAN;

	/** A test uidPk value for the product we get URLs for. */
	private static final long PRODUCT_UIDPK = 123;

	// A small hierarchy of categories to check that the correct URL path 
	// is created for parent categories. 
	private Category parentCategory;
	private Category childCategory1;
	private Category childCategory2;
	
	private StoreImpl storeImpl;

	private final Utility utility = new UtilityImpl();
	

	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of error happens
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		stubGetBean(ContextIdNames.CATALOG_LOCALE, CatalogLocaleImpl.class);
		stubGetBean(ContextIdNames.LOCALIZED_PROPERTIES, LocalizedPropertiesImpl.class);
		stubGetBean(ContextIdNames.UTILITY, UtilityImpl.class);

		CatalogLocaleFallbackPolicyFactory localePolicyFactory = new CatalogLocaleFallbackPolicyFactory();
		stubGetBean(ContextIdNames.LOCALE_FALLBACK_POLICY_FACTORY, localePolicyFactory);

		urlBuilder = new SeoUrlBuilderImpl();
		
		UrlUtilityImpl urlUtility = new UrlUtilityImpl();
		urlUtility.setCharacterEncoding("UTF-8");
		
		urlBuilder.setUrlUtility(urlUtility);
		urlBuilder.setUtility(utility);
		
		storeImpl = new StoreImpl();
		storeImpl.setDefaultLocale(STORES_DEFAULT_LOCALE);
		
		urlBuilder.setStore(storeImpl);
		storeImpl.setDefaultLocale(STORES_DEFAULT_LOCALE);

		urlBuilder.setFieldSeparator(SeoConstants.DEFAULT_SEPARATOR_BETWEEN_TOKENS);
		
		// Create parent category
		parentCategory = getCategory();
		addLdfToCategory(parentCategory, STORES_DEFAULT_LOCALE, "parent-de", "parent-de");
		addLdfToCategory(parentCategory, Locale.CANADA, "parent", "parent");
		
		// Create a first child category
		childCategory1 = getCategory();
		addLdfToCategory(childCategory1, STORES_DEFAULT_LOCALE, "branch1-de", "branch1-de");
		addLdfToCategory(childCategory1, Locale.CANADA, "branch1", "branch1");
		childCategory1.setParent(parentCategory);
		
		// Create a second child category
		childCategory2 = getCategory();
		addLdfToCategory(childCategory2, STORES_DEFAULT_LOCALE, "branch2-de", "branch2-de");
		addLdfToCategory(childCategory2, Locale.CANADA, "branch2", "branch2");
		childCategory2.setParent(parentCategory);
	}
	
	/**
	 * Test the SeoUrlBuilder correctly builds product seo urls.
	 */
	@Test
	public void testProductSeoUrlTypicalUse() {

		// Associate product to category1
		ProductImpl productImpl = (ProductImpl) getProduct();
		productImpl.addCategory(childCategory1);
		
		storeImpl.setCatalog(productImpl.getMasterCatalog());

		// Associate product to category2 as a default category
		productImpl.setCategoryAsDefault(childCategory2);
		addLdfToProduct(productImpl, STORES_DEFAULT_LOCALE, "me-url-de", "me-de");
		addLdfToProduct(productImpl, Locale.CANADA, "me-url", "me");
		productImpl.setUidPk(PRODUCT_UIDPK);
		productImpl.setGuid("demo");

		// Check using the store's default locale
		assertEquals("Correct seo url", "parent-de/branch1-de/me-url-de/proddemo.html", 
				                        urlBuilder.productSeoUrl(productImpl, STORES_DEFAULT_LOCALE, childCategory1));
		
		assertEquals("Correct seo url", "parent-de/branch2-de/me-url-de/proddemo.html", 
				                        urlBuilder.productSeoUrl(productImpl, STORES_DEFAULT_LOCALE, childCategory2));
		
		assertEquals("Correct seo url", "parent-de/branch2-de/me-url-de/proddemo.html", 
				                        urlBuilder.productSeoUrl(productImpl, STORES_DEFAULT_LOCALE, null));
		
		// Test using a locale that isn't the store's default
		assertEquals("en_ca/parent/branch1/me-url/proddemo.html", 
				     urlBuilder.productSeoUrl(productImpl, Locale.CANADA, childCategory1));
		
		assertEquals("en_ca/parent/branch2/me-url/proddemo.html", 
				     urlBuilder.productSeoUrl(productImpl, Locale.CANADA, childCategory2));
		
		assertEquals("en_ca/parent/branch2/me-url/proddemo.html", 
				     urlBuilder.productSeoUrl(productImpl, Locale.CANADA, null));
	}
	
	/**
	 * Test that SEO URLs for products without a guid fallback to their UIDs.
	 * NOTE: Not sure this is really the correct behaviour, because we shouldn't
	 * be externalising our UIDs.
	 */
	@Test
	public void testProductSeoUrlWithoutGuid() {
		// Test another product without Guid
		final Product productWithoutGuid = getProduct();
		productWithoutGuid.addCategory(childCategory1);
		productWithoutGuid.setCategoryAsDefault(childCategory2);
		addLdfToProduct(productWithoutGuid, STORES_DEFAULT_LOCALE, "me-url-de", "me-de");
		addLdfToProduct(productWithoutGuid, Locale.CANADA, "me-url", "me");
		productWithoutGuid.setUidPk(PRODUCT_UIDPK);
		productWithoutGuid.setGuid(null);
		
		storeImpl.setCatalog(productWithoutGuid.getMasterCatalog());
		
		// Check for the store's default locale
		assertEquals("parent-de/branch1-de/me-url-de/prod123.html", 
				urlBuilder.productSeoUrl(productWithoutGuid, STORES_DEFAULT_LOCALE, childCategory1));
		assertEquals("parent-de/branch2-de/me-url-de/prod123.html", 
				urlBuilder.productSeoUrl(productWithoutGuid, STORES_DEFAULT_LOCALE, childCategory2));
		assertEquals("parent-de/branch2-de/me-url-de/prod123.html", 
				urlBuilder.productSeoUrl(productWithoutGuid, STORES_DEFAULT_LOCALE, null));
		// Test for a locale that isn't the store's default
		assertEquals("en_ca/parent/branch1/me-url/prod123.html", urlBuilder.productSeoUrl(productWithoutGuid, Locale.CANADA, childCategory1));
		assertEquals("en_ca/parent/branch2/me-url/prod123.html", urlBuilder.productSeoUrl(productWithoutGuid, Locale.CANADA, childCategory2));
		assertEquals("en_ca/parent/branch2/me-url/prod123.html", urlBuilder.productSeoUrl(productWithoutGuid, Locale.CANADA, null));
	}
		
	
	/** 
	 * Test that when a product has no LDF in the given locale, the productUrl 
	 * portion will be the product displayName.
	 */
	@Test
	public void testProductSeoUrlNoLdfUrl() {
		final Product productWithoutLDFurl = getProduct();
		productWithoutLDFurl.addCategory(childCategory1);
		productWithoutLDFurl.setCategoryAsDefault(childCategory2);		
		productWithoutLDFurl.setUidPk(PRODUCT_UIDPK);
		productWithoutLDFurl.setGuid("demo");
		productWithoutLDFurl.setDisplayName("myDisplayName-de", STORES_DEFAULT_LOCALE);
		productWithoutLDFurl.setDisplayName("myDisplayName", Locale.CANADA);
		
		storeImpl.setCatalog(productWithoutLDFurl.getMasterCatalog());
		
		// Check for the store's default locale.
		assertEquals("parent-de/branch1-de/mydisplayname-de/proddemo.html", 
				urlBuilder.productSeoUrl(productWithoutLDFurl, STORES_DEFAULT_LOCALE, childCategory1));
		assertEquals("parent-de/branch2-de/mydisplayname-de/proddemo.html", 
				urlBuilder.productSeoUrl(productWithoutLDFurl, STORES_DEFAULT_LOCALE, childCategory2));
		assertEquals("parent-de/branch2-de/mydisplayname-de/proddemo.html", 
				urlBuilder.productSeoUrl(productWithoutLDFurl, STORES_DEFAULT_LOCALE, null));		
		// Check for a locale that isn't the store's default.
		assertEquals("en_ca/parent/branch1/mydisplayname/proddemo.html", 
				urlBuilder.productSeoUrl(productWithoutLDFurl, Locale.CANADA, childCategory1));
		assertEquals("en_ca/parent/branch2/mydisplayname/proddemo.html", 
				urlBuilder.productSeoUrl(productWithoutLDFurl, Locale.CANADA, childCategory2));
		assertEquals("en_ca/parent/branch2/mydisplayname/proddemo.html", 
				urlBuilder.productSeoUrl(productWithoutLDFurl, Locale.CANADA, null));
	}
	
	/**
	 * Test that a product without a display name or seo url will fall back 
	 * to it's GUID.
	 */
	@Test
	public void testProductSeoUrlFallbackToGuid() {
		final Product productFallbackToGuid = getProduct();
		productFallbackToGuid.addCategory(childCategory1);
		productFallbackToGuid.setCategoryAsDefault(childCategory2);		
		productFallbackToGuid.setUidPk(PRODUCT_UIDPK);
		productFallbackToGuid.setGuid("demo");
		productFallbackToGuid.setDisplayName(null, STORES_DEFAULT_LOCALE);
		productFallbackToGuid.setDisplayName(null, Locale.CANADA);
		
		storeImpl.setCatalog(productFallbackToGuid.getMasterCatalog());
		
		// Check for the store's default locale.
		assertEquals("parent-de/branch1-de/demo/proddemo.html", 
				urlBuilder.productSeoUrl(productFallbackToGuid, STORES_DEFAULT_LOCALE, childCategory1));
		assertEquals("parent-de/branch2-de/demo/proddemo.html", 
				urlBuilder.productSeoUrl(productFallbackToGuid, STORES_DEFAULT_LOCALE, childCategory2));
		assertEquals("parent-de/branch2-de/demo/proddemo.html", 
				urlBuilder.productSeoUrl(productFallbackToGuid, STORES_DEFAULT_LOCALE, null));		
		// Check for a locale that isn't the store's default
		assertEquals("en_ca/parent/branch1/demo/proddemo.html", 
				urlBuilder.productSeoUrl(productFallbackToGuid, Locale.CANADA, childCategory1));
		assertEquals("en_ca/parent/branch2/demo/proddemo.html", 
				urlBuilder.productSeoUrl(productFallbackToGuid, Locale.CANADA, childCategory2));
		assertEquals("en_ca/parent/branch2/demo/proddemo.html", 
				urlBuilder.productSeoUrl(productFallbackToGuid, Locale.CANADA, null));		
	}
	
	private void addLdfToCategory(final Category category, final Locale locale, final String url, final String displayName) {
		category.addOrUpdateLocaleDependantFields(createCategoryLdf(locale, url, displayName));
	}

	private void addLdfToProduct(final Product product, final Locale locale, final String url, final String displayName) {
		LocaleDependantFields ldf = new ProductLocaleDependantFieldsImpl();
		populateLdf(ldf, locale, url, displayName);
		product.addOrUpdateLocaleDependantFields(ldf);
	}

	private LocaleDependantFields createCategoryLdf(final Locale locale, final String url,
			final String displayName) {
		LocaleDependantFields ldf = new CategoryLocaleDependantFieldsImpl();
		populateLdf(ldf, locale, url, displayName);
		return ldf;
	}

	private void populateLdf(final LocaleDependantFields ldf, final Locale locale,
			final String url, final String displayName) {
		ldf.setLocale(locale);
		ldf.setUrl(url);
		ldf.setDisplayName(displayName);
	}
	
	/**
	 * Test that the SeoUrlBuilder correctly builds category seo urls.
	 */
	@Test
	public void testCategorySeoUrl() {

		Category categoryImpl = getCategory();
		Category parentImpl = getCategory();
		LocaleDependantFields ldfOfMe = createCategoryLdf(Locale.CANADA, "aaa", "display name aaa");
		categoryImpl.addOrUpdateLocaleDependantFields(ldfOfMe);
		final long uidPk = 123;
		categoryImpl.setUidPk(uidPk);
		categoryImpl.setGuid("XXX");

		CategoryLocaleDependantFieldsImpl ldfOfParent = new CategoryLocaleDependantFieldsImpl();
		ldfOfParent.setLocale(Locale.CANADA);
		ldfOfParent.setUrl("bbb");         
		ldfOfParent.setDisplayName("display name bbb");
		parentImpl.addOrUpdateLocaleDependantFields(ldfOfParent);
		final long parentUidPk = 345;
		parentImpl.setUidPk(parentUidPk);
		parentImpl.setGuid(String.valueOf(parentUidPk));

		categoryImpl.setParent(parentImpl);
		
		// Check in a locale that is not the store's default.
		assertEquals("en_ca/bbb/aaa/c345-cXXX-p0.html", urlBuilder.categorySeoUrl(categoryImpl, Locale.CANADA, 0));
		assertEquals("bbb/aaa", urlBuilder.categorySeoUrlWithoutSuffix(categoryImpl, Locale.CANADA));
		assertEquals("en_ca/bbb/c345-p1.html", urlBuilder.categorySeoUrl(parentImpl, Locale.CANADA, 1));
		assertEquals("bbb", urlBuilder.categorySeoUrlWithoutSuffix(parentImpl, Locale.CANADA));

		assertEquals("345/xxx/c345-cXXX-p0.html", urlBuilder.categorySeoUrl(categoryImpl, STORES_DEFAULT_LOCALE, 0));
		assertEquals("345/xxx", urlBuilder.categorySeoUrlWithoutSuffix(categoryImpl, STORES_DEFAULT_LOCALE));
		assertEquals("345/c345-p1.html", urlBuilder.categorySeoUrl(parentImpl, STORES_DEFAULT_LOCALE, 1));
		assertEquals("345", urlBuilder.categorySeoUrlWithoutSuffix(parentImpl, STORES_DEFAULT_LOCALE));
		
		// Test with a different field separator
		urlBuilder.setFieldSeparator(":");
		assertEquals("345/xxx/c345:cXXX:p0.html", urlBuilder.categorySeoUrl(categoryImpl, STORES_DEFAULT_LOCALE, 0));
		assertEquals("345/xxx", urlBuilder.categorySeoUrlWithoutSuffix(categoryImpl, STORES_DEFAULT_LOCALE));
		assertEquals("345/c345:p1.html", urlBuilder.categorySeoUrl(parentImpl, STORES_DEFAULT_LOCALE, 1));
		assertEquals("345", urlBuilder.categorySeoUrlWithoutSuffix(parentImpl, STORES_DEFAULT_LOCALE));
		
		// If a category's SEO URL is null we should fall back to the categories display name
		urlBuilder.setFieldSeparator("-");
		ldfOfMe.setUrl(null);
		ldfOfParent.setUrl(null);
		assertEquals("en_ca/display-name-bbb/display-name-aaa/c345-cXXX-p0.html", urlBuilder.categorySeoUrl(categoryImpl, Locale.CANADA, 0));
		assertEquals("display-name-bbb/display-name-aaa", urlBuilder.categorySeoUrlWithoutSuffix(categoryImpl, Locale.CANADA));
		assertEquals("en_ca/display-name-bbb/c345-p1.html", urlBuilder.categorySeoUrl(parentImpl, Locale.CANADA, 1));
		assertEquals("display-name-bbb", urlBuilder.categorySeoUrlWithoutSuffix(parentImpl, Locale.CANADA));
		
		// If a category's SEO URL is null and the display name is null we should fall back to the categories GUID
		urlBuilder.setFieldSeparator("-");
		ldfOfMe.setUrl(null);
		ldfOfParent.setUrl(null);
		ldfOfMe.setDisplayName(null);
		assertEquals("en_ca/display-name-bbb/xxx/c345-cXXX-p0.html", urlBuilder.categorySeoUrl(categoryImpl, Locale.CANADA, 0));
		assertEquals("display-name-bbb/xxx", urlBuilder.categorySeoUrlWithoutSuffix(categoryImpl, Locale.CANADA));
		assertEquals("en_ca/display-name-bbb/c345-p1.html", urlBuilder.categorySeoUrl(parentImpl, Locale.CANADA, 1));
		assertEquals("display-name-bbb", urlBuilder.categorySeoUrlWithoutSuffix(parentImpl, Locale.CANADA));
		
	}
	
	/**
	 * Test that brand seo urls are built correctly.
	 */
	@Test
	public void testBrandSeoUrl() {
		BrandImpl brand = (BrandImpl) getBrand();

		assertEquals("", urlBuilder.brandSeoUrlWithoutSuffix(brand, Locale.CANADA));
		
		brand.setLocalizedProperties(new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = 9014105915204395933L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		});
		LocalizedProperties props = brand.getLocalizedProperties();
		props.setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.CANADA, "my brand");
		assertEquals("my-brand", urlBuilder.brandSeoUrlWithoutSuffix(brand, Locale.CANADA));
		
	}
	
	/**
	 * Test that sitemap urls are built correctly when no brand or category is specified.
	 */
	@Test
	public void testSitemapSeoUrlNoBrandNoCategory() {
		BrandImpl brand = (BrandImpl) getBrand();
		assertEquals("", urlBuilder.sitemapSeoUrl(null, brand, Locale.CANADA, -1));
	}
	
	/**
	 * Test that sitemap urls are built correctly when only the brand is specified.
	 */
	@Test
	public void testSitemapSeoUrlJustBrand() {
		Brand brand = getBrand();
		brand.setUidPk(TEST_BRAND_ID_15);
		brand.setGuid(TEST_BRAND_GUID_15);
		assertEquals(
				"Default locale wrong",
				"sitemap-b" + TEST_BRAND_GUID_15 + "-p1.html",
				urlBuilder.sitemapSeoUrl(null, brand, STORES_DEFAULT_LOCALE, 1));
		assertEquals(
				"Non-default locale wrong",
				"fr_fr/sitemap-b" + TEST_BRAND_GUID_15 + "-p1.html",
				urlBuilder.sitemapSeoUrl(null, brand, Locale.FRANCE, 1));
	}

	/**
	 * Test that sitemap urls are built correctly when only the brand is specified
	 * and we use a non-standard field separator.
	 */
	@Test
	public void testSitemapSeoUrlJustBrandNonStandardFieldSeparator() {
		Brand brand = getBrand();
		brand.setUidPk(TEST_BRAND_ID_15);
		brand.setGuid(TEST_BRAND_GUID_15);
		urlBuilder.setFieldSeparator("+++");
		assertEquals("sitemap+++b" + TEST_BRAND_GUID_15 + "+++p1.html", urlBuilder.sitemapSeoUrl(null, brand, STORES_DEFAULT_LOCALE, 1));
		assertEquals("fr_fr/sitemap+++b" + TEST_BRAND_GUID_15 + "+++p1.html", urlBuilder.sitemapSeoUrl(null, brand, Locale.FRANCE, 1));
		assertEquals("en_ca/sitemap+++b" + TEST_BRAND_GUID_15 + "+++p1.html", urlBuilder.sitemapSeoUrl(null, brand, Locale.CANADA, 1));
	}	
	
	/**
	 * Test that sitemap urls are built correctly when only the category is
	 * specified.
	 */
	@Test
	public void testSitemapSeoUrlJustCategory() {
		Category category = getCategory();
		category.setCode(String.valueOf(TEST_CATEGORY_ID_15));
		category.setUidPk(TEST_CATEGORY_ID_15);
		assertEquals("15/sitemap-c15-p2.html", urlBuilder.sitemapSeoUrl(category, null, STORES_DEFAULT_LOCALE, 2));
		assertEquals("en_ca/15/sitemap-c15-p2.html", urlBuilder.sitemapSeoUrl(category, null, Locale.CANADA, 2));
	}
	
	/**
	 * Test that sitemap urls are built correctly when only the category is
	 * specified and a non-standard field separator is used.
	 */
	@Test
	public void testSitemapSeoUrlJustCategoryNonStandardFieldSeparator() {
		Category category = getCategory();
		category.setCode(String.valueOf(TEST_CATEGORY_ID_15));
		category.setUidPk(TEST_CATEGORY_ID_15);
		urlBuilder.setFieldSeparator("]");
		assertEquals("15/sitemap]c15]p2.html", urlBuilder.sitemapSeoUrl(category, null, STORES_DEFAULT_LOCALE, 2));
		assertEquals("en_ca/15/sitemap]c15]p2.html", urlBuilder.sitemapSeoUrl(category, null, Locale.CANADA, 2));
	}	
	
	/**
	 * Test that sitemap urls are build correctly when only the category is
	 * specified - but that category has a parent category.
	 */
	@Test
	public void testSitemapSeoUrlJustCategoryWithParentCategory() {
		Category parentCategory = getCategory();
		parentCategory.setCode("11");
		parentCategory.setUidPk(TEST_CATEGORY_ID_11);
		
		Category category = getCategory();
		category.setCode(String.valueOf(TEST_CATEGORY_ID_15));
		category.setUidPk(TEST_CATEGORY_ID_15);
		category.setParent(parentCategory);

		assertEquals("11/15/sitemap-c15-p2.html", urlBuilder.sitemapSeoUrl(category, null, STORES_DEFAULT_LOCALE, 2));		
		assertEquals("en_ca/11/15/sitemap-c15-p2.html", urlBuilder.sitemapSeoUrl(category, null, Locale.CANADA, 2));
	}
	
	/**
	 * Test that if the entity's LDF (SEO Name) is an empty string, the builder will
	 * fall back to the entity's DisplayName (URL-Escaped).
	 */
	@Test
	public void testFindMostPreferableSeoNameEmptyLDF() {
		final String escapedDisplayName = new UtilityImpl().escapeName2UrlFriendly("myDisplayName");
		final LocaleDependantFields mockLDF = context.mock(LocaleDependantFields.class);
		context.checking(new Expectations() {
			{
				allowing(mockLDF).getUrl();
				will(returnValue(""));

				allowing(mockLDF).getDisplayName();
				will(returnValue(utility.escapeName2UrlFriendly(escapedDisplayName)));
			}
		});
		assertEquals("When the LDF URL is an empty string, should fall back to the display name.", 
				escapedDisplayName, urlBuilder.findMostPreferableSeoName(null, STORES_DEFAULT_LOCALE, mockLDF));
	}
	
	/**
	 * Test that if the entity's LDF (SEO Name) is null, the builder will
	 * fall back to the entity's DisplayName (URL-Escaped).
	 */
	@Test
	public void testFindMostPreferableSeoNameNullLDF() {
		final String escapedDisplayName = new UtilityImpl().escapeName2UrlFriendly("myDisplayName");
		final LocaleDependantFields mockLDF = context.mock(LocaleDependantFields.class);
		context.checking(new Expectations() {
			{
				allowing(mockLDF).getUrl();
				will(returnValue(null));

				allowing(mockLDF).getDisplayName();
				will(returnValue(utility.escapeName2UrlFriendly(escapedDisplayName)));
			}
		});
		assertEquals("When the LDF URL is null, should fall back to the display name.", 
				escapedDisplayName, urlBuilder.findMostPreferableSeoName(null, STORES_DEFAULT_LOCALE, mockLDF));
	}
	
	/**
	 * Test that if the entity's LDF (SEO Name) is null, and the entity's
	 * DisplayName is null, the builder will fall back to the entity's GUID.
	 */
	@Test
	public void testFindMostPreferableSeoNameNullDisplayName() {
		final String escapedGuid = new UtilityImpl().escapeName2UrlFriendly("myGuid");
		final Entity mockEntity = context.mock(Entity.class);
		final LocaleDependantFields mockLDF = context.mock(LocaleDependantFields.class);
		context.checking(new Expectations() {
			{
				allowing(mockEntity).getGuid();
				will(returnValue(utility.escapeName2UrlFriendly(escapedGuid)));

				allowing(mockLDF).getUrl();
				will(returnValue(null));

				allowing(mockLDF).getDisplayName();
				will(returnValue(null));
			}
		});
		assertEquals("When the LDF URL is null and the display name is null, should fall back to the GUID.", 
				escapedGuid, urlBuilder.findMostPreferableSeoName(mockEntity, 
						STORES_DEFAULT_LOCALE, mockLDF));
	}
	
	/**
	 * Test that if the entity's LDF (SEO Name) is null, and the entity's
	 * DisplayName is an empty string, the builder will fall back to the entity's GUID.
	 */
	@Test
	public void testFindMostPreferableSeoNameEmptyDisplayName() {
		final String escapedGuid = new UtilityImpl().escapeName2UrlFriendly("myGuid");
		final Entity mockEntity = context.mock(Entity.class);
		final LocaleDependantFields mockLDF = context.mock(LocaleDependantFields.class);
		context.checking(new Expectations() {
			{
				allowing(mockEntity).getGuid();
				will(returnValue(utility.escapeName2UrlFriendly(escapedGuid)));

				allowing(mockLDF).getUrl();
				will(returnValue(null));

				allowing(mockLDF).getDisplayName();
				will(returnValue(""));
			}
		});
		assertEquals("When the LDF URL is null and the display name is empty, should fall back to the GUID.", 
				escapedGuid, urlBuilder.findMostPreferableSeoName(mockEntity, 
						STORES_DEFAULT_LOCALE, mockLDF));
	}
	
	/**
	 * @return a new <code>Brand</code> instance
	 */
	private Brand getBrand() {
		return new BrandImpl();
	}	
}
