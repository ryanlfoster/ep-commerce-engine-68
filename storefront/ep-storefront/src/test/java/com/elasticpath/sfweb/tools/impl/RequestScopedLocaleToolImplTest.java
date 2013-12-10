package com.elasticpath.sfweb.tools.impl;

import static org.junit.Assert.assertEquals;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.sfweb.tools.LocaleLinksTool;

/**
 * Tests the RequestScopedLocaleToolImpl class.
 */
public class RequestScopedLocaleToolImplTest {
	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();

	private RequestScopedLocaleToolImpl localeTool;
	private LocaleLinksTool localeLinksTool;
	private StoreConfig storeConfig;
	private Store store;
	private List<Locale> supportedLocales;
	private Map<Locale, String> expectedLinks;

	@Before
	public void setUp() throws Exception {
		store = context.mock(Store.class);
		storeConfig = context.mock(StoreConfig.class);
		localeLinksTool = context.mock(LocaleLinksTool.class);

		localeTool = new RequestScopedLocaleToolImpl();
		localeTool.setStoreConfig(storeConfig);
		localeTool.setLocaleLinksTool(localeLinksTool);

		supportedLocales = Arrays.asList(Locale.CANADA, Locale.US, Locale.CHINA);

		expectedLinks = new HashMap<Locale, String>();
		expectedLinks.put(Locale.CANADA, null);
		expectedLinks.put(Locale.US, "/moveToThePromisedLand");
		expectedLinks.put(Locale.CHINA, "/weLoveOurNewOverlords");
	}

	@Test
	public void testLocaleLinksForCurrentStore() {

		context.checking(new Expectations() { {
			allowing(store).getSupportedLocales();
			will(returnValue(supportedLocales));

			allowing(storeConfig).getStore(); will(returnValue(store));

			allowing(localeLinksTool).getLocaleLinks(supportedLocales, Locale.CANADA);
				will(returnValue(expectedLinks));
		} });

		Locale currentLocale = Locale.CANADA;
		Map<Locale, String> localeMap = localeTool.getLocaleLinksForCurrentStore(currentLocale);
		assertEquals("We should get the expected links back", expectedLinks, localeMap);
	}

	@Test
	public void testLocaleLinksForCategory() {
		// Given
		final Category category = new CategoryImpl();
		final int pageNumber = 1;
		final boolean isSeoEnabled = true;

		context.checking(new Expectations() { {
			allowing(store).getSupportedLocales();
			will(returnValue(supportedLocales));

			allowing(storeConfig).getStore(); will(returnValue(store));

			atLeast(1).of(localeLinksTool).getLocaleLinksForCategory(
					supportedLocales, Locale.CANADA, category, pageNumber, isSeoEnabled);
			will(returnValue(expectedLinks));
		} });

		// When
		Map<Locale, String> localeMap = localeTool.getLocaleLinksForCategory(
				Locale.CANADA, category, pageNumber, isSeoEnabled);

		// Then
		assertEquals("Locale Links should be retrieved for the given category",
				expectedLinks, localeMap);
	}

	@Test
	public void testLocaleLinksForProduct() {
		// Given
		final Category category = new CategoryImpl();
		final Product product = new ProductImpl();
		final boolean isSeoEnabled = true;

		context.checking(new Expectations() { {
			allowing(store).getSupportedLocales();
			will(returnValue(supportedLocales));

			allowing(storeConfig).getStore(); will(returnValue(store));

			atLeast(1).of(localeLinksTool).getLocaleLinksForProduct(
					supportedLocales, Locale.CANADA, category, product, isSeoEnabled);
			will(returnValue(expectedLinks));
		} });

		// When
		Map<Locale, String> localeMap = localeTool.getLocaleLinksForProduct(
				Locale.CANADA, category, product, isSeoEnabled);

		// Then
		assertEquals("Locale Links should be retrieved for the given category",
				expectedLinks, localeMap);
	}
}
