package com.elasticpath.domain.shoppingcart.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/** Test cases for <code>ViewHistoryProduct</code>. */
public class ViewHistoryProductImplTest {

	private static final int LOCALE_SIZE_3 = 3;
	private static final String TEST_IMAGE = "products/sfa.jpg";
	private static final long TEST_UIDPK = 100L;
	private static final String TEST_GUID = "guid100";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	// Our class under test
	private ViewHistoryProductImpl viewHistoryProduct;
		
	private SeoUrlBuilder singleStoreSeoUrlBuilder;
	private Product product;
	private Brand brand;	
	private Store store;
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private List<Locale> supportedLocales;
	private StoreConfig storeConfig;
	private LocaleDependantFields usLocaleDependantFields;		
	private LocaleDependantFields cadLocaleDependantFields;
	
	/**
	 * Prepare for the tests.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		viewHistoryProduct = new ViewHistoryProductImpl();

		singleStoreSeoUrlBuilder = context.mock(SeoUrlBuilder.class);
		product = context.mock(Product.class);		
		brand = context.mock(Brand.class);
		store = context.mock(Store.class);
		storeConfig = context.mock(StoreConfig.class);
		
		cadLocaleDependantFields = context.mock(LocaleDependantFields.class, "cadLdfs");
		usLocaleDependantFields = context.mock(LocaleDependantFields.class, "usLdfs");
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test case for <code>loadProductInfo</code>.
	 */
	@Test
	public void testLoadProductInfo() {	
		
		final String seoUrl = "/seo/url/cad";					
				
		supportedLocales = new ArrayList<Locale>();
		supportedLocales.add(Locale.CANADA);							
				
		// Set expectations
		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean("threadLocalStorage");
				will(returnValue(storeConfig));
				
				oneOf(storeConfig).getStore();
				will(returnValue(store));
				
				oneOf(store).getSupportedLocales();
				will(returnValue(supportedLocales));
				
				oneOf(product).getUidPk();
				will(returnValue(TEST_UIDPK));
				
				oneOf(product).getBrand();
				will(returnValue(brand));
				
				oneOf(product).getGuid();
				will(returnValue(TEST_GUID));
				
				oneOf(product).getImage();
				will(returnValue(TEST_IMAGE));
				
				oneOf(product).getLocaleDependantFields(Locale.CANADA);
				will(returnValue(cadLocaleDependantFields));							
								
				oneOf(singleStoreSeoUrlBuilder).productSeoUrl(product, Locale.CANADA);
				will(returnValue(seoUrl));													
			}
		});						
		
		viewHistoryProduct.loadProductInfo(product, singleStoreSeoUrlBuilder);				
					
		// Make sure the basic properties have been set for the product
		assertSame(brand, viewHistoryProduct.getBrand());
		assertSame(TEST_UIDPK, viewHistoryProduct.getUidPk());
		assertSame(TEST_IMAGE, viewHistoryProduct.getImage());
			
		// Make sure there is a single ldf object in our map
		assertNotNull(viewHistoryProduct.getLdf(Locale.CANADA));
		assertEquals(1, viewHistoryProduct.getLocaleDependantFieldsMap().size());			
		
		// Make sure there is a single seo url in our map
		assertNotNull(viewHistoryProduct.getSeoUrl(Locale.CANADA));		
		assertEquals(1, viewHistoryProduct.getSeoUrlMap().size());
		
	}
	
	/**
	 * Test method for <code>createSeoUrls</code>.
	 */
	@Test
	public void testCreateSeoUrls() {
		
		final String canadaSeoUrl = "/seo/url/cad";					
		final String usSeoUrl = "/seo/url/us";
		
		supportedLocales = new ArrayList<Locale>();
		supportedLocales.add(Locale.CANADA);							
		supportedLocales.add(Locale.US);
		
		// Set expectations
		context.checking(new Expectations() {
			{								
				
				oneOf(singleStoreSeoUrlBuilder).productSeoUrl(product, Locale.CANADA);
				will(returnValue(canadaSeoUrl));													
				
				oneOf(singleStoreSeoUrlBuilder).productSeoUrl(product, Locale.US);
				will(returnValue(usSeoUrl));
			}
		});	
	
		viewHistoryProduct.createSeoUrls(product, supportedLocales, singleStoreSeoUrlBuilder);
		
		assertEquals(2, viewHistoryProduct.getSeoUrlMap().size());
		assertEquals(canadaSeoUrl, viewHistoryProduct.getSeoUrl(Locale.CANADA));
		assertEquals(usSeoUrl, viewHistoryProduct.getSeoUrl(Locale.US));
		
	}	
	
	/**
	 * Test method for <code>createProductLocaleDependantFields</code>.
	 */
	@Test
	public void testCreateProductLocaleDependantFields() {
		
		supportedLocales = new ArrayList<Locale>();
		supportedLocales.add(Locale.CANADA);							
		supportedLocales.add(Locale.US);			
		
		// Set expectations
		context.checking(new Expectations() {
			{											
				allowing(product).getLocaleDependantFields(Locale.CANADA);
				will(returnValue(cadLocaleDependantFields));
				
				allowing(product).getLocaleDependantFields(Locale.US);
				will(returnValue(usLocaleDependantFields));
				
				allowing(product).getLocaleDependantFields(Locale.UK);
				will(returnValue(null));
			}
		});				
		
		viewHistoryProduct.createProductLocaleDependantFields(product, supportedLocales);
		
		assertEquals(cadLocaleDependantFields, viewHistoryProduct.getLdf(Locale.CANADA));					
		assertEquals(usLocaleDependantFields, viewHistoryProduct.getLdf(Locale.US));
		assertNull(viewHistoryProduct.getLdf(Locale.UK));
		
	}
	
	/**
	 * Test for <code>getLocales</code>.
	 */
	@Test
	public void testGetLocales() {			
		
		supportedLocales = new ArrayList<Locale>();
		supportedLocales.add(Locale.CANADA);
		supportedLocales.add(Locale.US);
		supportedLocales.add(Locale.UK);
		
		// Set expectations
		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean("threadLocalStorage");
				will(returnValue(storeConfig));

				oneOf(storeConfig).getStore();
				will(returnValue(store));
				
				oneOf(store).getSupportedLocales();
				will(returnValue(supportedLocales));
			}
		});				
		
		Collection<Locale> locales = viewHistoryProduct.getLocales();
		assertEquals(LOCALE_SIZE_3, locales.size());
		
	}
	
	/**
	 * Test for <code>getUidpk</code>.
	 */
	@Test
	public void testGetUidPk() {
		
		viewHistoryProduct.setUidPk(TEST_UIDPK);
		assertEquals(TEST_UIDPK, viewHistoryProduct.getUidPk());
	}
	
	/**
	 * Test for <code>getBrand</code>.
	 */
	@Test
	public void testBrand() {
				
		viewHistoryProduct.setBrand(brand);
		assertSame(brand, viewHistoryProduct.getBrand());
	}
	
	/**
	 * Test for <code>getImage</code>.
	 */
	@Test
	public void testImage() {
	
		viewHistoryProduct.setImage(TEST_IMAGE);
		assertEquals(TEST_IMAGE, viewHistoryProduct.getImage());
	}
}

