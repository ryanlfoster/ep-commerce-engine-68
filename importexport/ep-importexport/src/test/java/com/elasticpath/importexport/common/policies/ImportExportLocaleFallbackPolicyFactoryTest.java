package com.elasticpath.importexport.common.policies;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.localization.LocaleFallbackPolicy;

/**
 * Test class for ImportExportLocaleFallbackPolicyFactory.
 */
@SuppressWarnings({ "PMD.NonStaticInitializer" })
public class ImportExportLocaleFallbackPolicyFactoryTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private ImportExportLocaleFallbackPolicyFactory factory;
	/**
	 * Setup for tests.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		factory = new ImportExportLocaleFallbackPolicyFactory();
		context.checking(new Expectations() {
			{
			    allowing(beanFactory).getBean(ContextIdNames.LOCALE_FALLBACK_POLICY_FACTORY); 
			    	will(returnValue(factory));
			}
		});
	}
	
	/**
	 * Test creating the LocaleFallbackPolicy so it contains only passing in locale, and fallback locale.
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
		assertEquals("Policy should contain exactly the selected locale, and products master calalog default locale.", 
				result.getLocales(), Arrays.asList(Locale.CANADA, Locale.US));
	}
	/**
	 * Test creating LocaleFallbackPolicy such that it contains only the passed in locale.
	 */
	@Test
	public void testCreateProductLocaleFallbackPolicyWithoutFallback() {
		LocaleFallbackPolicy result = factory.createProductLocaleFallbackPolicy(Locale.CANADA, false, context.mock(Product.class));	
		
		assertEquals("Policy should contain exactly the selected locale.", 
				result.getLocales(), Arrays.asList(Locale.CANADA));
	}
}
