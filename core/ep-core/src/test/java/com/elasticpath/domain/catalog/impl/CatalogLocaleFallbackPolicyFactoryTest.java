package com.elasticpath.domain.catalog.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.localization.LocaleFallbackPolicy;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test LocaleFallbackPolicyFactory.
 *
 */
@SuppressWarnings({ "PMD.NonStaticInitializer" })
public class CatalogLocaleFallbackPolicyFactoryTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private CatalogLocaleFallbackPolicyFactory factory;
	/**
	 * Setup for tests.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		factory = new CatalogLocaleFallbackPolicyFactory();
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.LOCALE_FALLBACK_POLICY_FACTORY, factory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.UTILITY, UtilityImpl.class);

	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test creating the LocaleFallbackPolicy such that it will fallback to the products master catalog's default locale.
	 */
	@Test
	public void testCreateProductLocaleFallbackPolicyWithFallback() {
		final Product product = context.mock(Product.class);
		final Catalog catalog = context.mock(Catalog.class);
		
		context.checking(new Expectations() {
			{
				oneOf(product).getMasterCatalog(); will(returnValue(catalog));
				oneOf(catalog).getDefaultLocale(); will(returnValue(Locale.US));
			}
		});
		LocaleFallbackPolicy result = factory.createProductLocaleFallbackPolicy(Locale.CANADA, true, product);
		assertThat("Policy should contain passed in locale, and products master catalog locale.", 
				result.getLocales(), hasItems(Locale.CANADA, Locale.US));
	}
	/**
	 * Test creating the LocaleFallbackPolicy such that it will not fallback to any defaults.
	 */
	@Test
	public void testCreateProductLocaleFallbackPolicyWithoutFallback() {
		LocaleFallbackPolicy result = factory.createProductLocaleFallbackPolicy(Locale.CANADA, false, context.mock(Product.class));	
		
		assertThat("Policy should contain only the passed in locale.", result.getLocales(), hasItems(Locale.CANADA));
	}
	/**
	 * Test creating the LocaleFallbackPolicy such that it will fallback to the category's catalog's default locale.
	 */
	@Test
	public void testCreateCategoryLocaleFallbackPolicyWithFallback() {
		final Catalog catalog = context.mock(Catalog.class);
		final Category category = context.mock(Category.class);
		context.checking(new Expectations() {
			{
				oneOf(category).getCatalog(); will(returnValue(catalog));
				oneOf(catalog).getDefaultLocale(); will(returnValue(Locale.US));
			}
		});
		LocaleFallbackPolicy result = factory.createCategoryLocaleFallbackPolicy(Locale.CANADA, true, category);
		assertThat("Policy should contain selected locale, and category's catalog default locale",
				result.getLocales(), hasItems(Locale.CANADA, Locale.US));
	}
	/**
	 * Test creating the category's LocaleFallbackPolicy such that it will not fallback to any defaults.
	 */
	@Test
	public void testCreateCategoryLocaleFallbackPolicyWithoutFallback() {
		LocaleFallbackPolicy result = factory.createCategoryLocaleFallbackPolicy(Locale.CANADA, false, context.mock(Category.class));
		
		assertThat("Policy should only contain selected locale.", result.getLocales(), hasItems(Locale.CANADA));
	}
}
