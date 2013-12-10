/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.impl.SeoUrlBuilderImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.sfweb.util.impl.SeoUrlValidatorImpl.UrlSegmentValidator;

/**
 * A test case for {@link SeoUrlValidatorImpl}.
 */
public class SeoUrlValidatorImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final String CONTEXT_PATH = "storefront";

	// these url patterns exist in the serviceSF.xml where they are injected in the SeoUrlValidatorImpl
	private static final String CATEGORY_URL_PATTERN = "^(.*)/c[^/]*\\.html[^/]*$";

	private static final String SITEMAP_URL_PATTERN = "^(.*)/sitemap[^/]*\\.html[^/]*$";

	/**
	 * Tests extract number method for various URLs.
	 */
	@Test
	public void testExtractCategorySeoPart() {
		SeoUrlValidatorImpl validator = new SeoUrlValidatorImpl();
		validator.setCategoryUrlPattern(CATEGORY_URL_PATTERN);

		assertEquals("URL category should be parent", "/test/parent", validator.extractCategorySeoPart("/test/parent/c90034df-p323.html"));
		assertEquals("URL part should be pacre-p55", "/test/pacre-p55", validator.extractCategorySeoPart("/test/pacre-p55/c90034df-p323.html"));
		assertEquals("URL part should be pacre-p55", "/test/pacre-p55", validator.extractCategorySeoPart("/test/pacre-p55/c90034df-p0.html"));

		// check with complex URLs
		assertEquals("Complex URL category should be binoculars", "/snapitupuk.elasticpath.com:8080/storefront/optics/binoculars",
				validator.extractCategorySeoPart("/snapitupuk.elasticpath.com:8080/storefront/optics/binoculars/c90000005-c90000014-p1.html"));

		// check with a URL with no page number set
		assertEquals("URL category should be binoculars", "//optics/binoculars", 
				validator.extractCategorySeoPart("//optics/binoculars/c90000005-c90000014-.html"));
		assertEquals("URL part should be binoculars", "//optics/binoculars", validator.extractCategorySeoPart("//optics/binoculars/c90000005.html"));
	}

	/**
	 * Tests that createProductUrlValidator() creates a validator that can validate an SEO URL part.
	 */
	@Test
	public void testCreateProductUrlValidator() {
		SeoUrlValidatorImpl validator = new SeoUrlValidatorImpl();

		final String productSeoUrl = "/fr/product-name/prod99090.html";
		validator.setSeoUrlBuilder(new SeoUrlBuilderImpl() {
			@Override
			public String productSeoUrl(final Product product, final Locale locale, final Category category) {
				return productSeoUrl;
			}
		});

		setupStoreWithLocales(validator, new String[] { "en", "de" });

		UrlSegmentValidator segmentValidator = validator.createProductUrlValidator(null, CONTEXT_PATH);

		assertTrue("Url should be /storefront/en/product-name", segmentValidator.validate("/storefront/en/product-name"));
	}

	private void setupStoreWithLocales(final SeoUrlValidatorImpl validator, final String[] localesStr) {

		final Store store = new StoreImpl() {
			private static final long serialVersionUID = -2836556277751767463L;

			@Override
			public Set<Locale> getSupportedLocales() {
				Set<Locale> locales = new HashSet<Locale>();
				for (String localeStr : localesStr) {
					locales.add(new Locale(localeStr));
				}
				return locales;
			}
		};

		final SfRequestHelper requestHelperMock = context.mock(SfRequestHelper.class);

		context.checking(new Expectations() {
			{
				oneOf(requestHelperMock).getStoreConfig();
				will(returnValue(new StoreConfig() {

					public SettingValue getSetting(final String path) {
						return null;
					}

					public Store getStore() {
						return store;
					}

					public String getStoreCode() {
						return null;
					}
				}));
			}
		});
		//
		validator.setRequestHelper(requestHelperMock);

	}
	/**
	 * Tests that createCategoryUrlValidator() creates a validator that can validate an SEO URL part.
	 */
	@Test
	public void testCreateCategoryUrlValidator() {
		SeoUrlValidatorImpl validator = new SeoUrlValidatorImpl();
		validator.setCategoryUrlPattern(CATEGORY_URL_PATTERN);

		final String categorySeoUrl = "/fr/category-name/c99090.html";
		validator.setSeoUrlBuilder(new SeoUrlBuilderImpl() {
			@Override
			public String categorySeoUrl(final Category category, final Locale locale, final int pageNumber) {
				return categorySeoUrl;
			}
		});

		setupStoreWithLocales(validator, new String[] { "en", "fr" });
		UrlSegmentValidator segmentValidator = validator.createCategoryUrlValidator(null, null, CONTEXT_PATH);

		assertTrue("Url should be /storefront/en/category-name", segmentValidator.validate("/storefront/en/category-name"));
	}

	/**
	 * Tests that createSitemapUrlValidator() creates a validator that can validate an SEO URL part.
	 */
	@Test
	public void testCreateSitemapUrlValidator() {
		SeoUrlValidatorImpl validator = new SeoUrlValidatorImpl();
		validator.setSitemapUrlPattern(SITEMAP_URL_PATTERN);

		final String sitemapSeoUrl = "/de/category-name/sitemap-99090.html";
		validator.setSeoUrlBuilder(new SeoUrlBuilderImpl() {
			@Override
			public String sitemapSeoUrl(final Category category, final Brand brand, final Locale locale, final int pageNumber) {
				return sitemapSeoUrl;
			}
		});

		setupStoreWithLocales(validator, new String[] { "en", "fr" });

		UrlSegmentValidator segmentValidator = validator.createSitemapUrlValidator(null, null, CONTEXT_PATH);

		assertTrue("Url should be /storefront/en/category-name", segmentValidator.validate("/storefront/en/category-name"));
	}
}