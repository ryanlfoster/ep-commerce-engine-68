package com.elasticpath.domain.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.CatalogViewConstants;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.BrandLocalizedPropertyValueImpl;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>BrandFilterImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class BrandFilterImplTest {

	private static final String EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED = "EpBindExceptionSearchRequest expected.";

	private BrandFilterImpl brandFilter;

	private long brand1Uid;

	private long brand2Uid;

	private BrandService brandService;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		setupBrands();
		brandService = context.mock(BrandService.class);
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean("brandService"); will(returnValue(brandService));
			}
		});
		brandFilter = new BrandFilterImpl() {
			private static final long serialVersionUID = -3408046289528768744L;

			@Override
			public BrandService getBrandService() {
				return brandService;
			}
			
		};
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.BrandFilterImpl.getId()'.
	 */
	@Test
	public void testGetId() {
		assertNull(brandFilter.getId());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.BrandFilterImpl.getDisplayName(String)'.
	 */
	@Test
	public void testGetDisplayName() {
		final String brandId = SeoConstants.BRAND_FILTER_PREFIX + brand1Uid;
		
		final String brandName = "sfsf";
		final Brand brand = getBrand();
		brand.setUidPk(brand1Uid);
		brand.setLocalizedProperties(new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = 4144393323391300692L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		});

		brand.getLocalizedProperties().setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.US, brandName);
		brand.setCode(String.valueOf(brand1Uid));
		
		context.checking(new Expectations() {
			{
				allowing(brandService).get(brand1Uid); will(returnValue(brand));
				allowing(brandService).findByCode(String.valueOf(brand1Uid)); will(returnValue(brand));
			}
		});
		
		
		brandFilter.initialize(brandId);
		assertEquals(brandId, brandFilter.getId());
		assertEquals(brandName, brandFilter.getDisplayName(Locale.US));

		final String brandIdOthers = SeoConstants.BRAND_FILTER_PREFIX + CatalogViewConstants.BRAND_FILTER_OTHERS;
		brandFilter.initialize(brandIdOthers);
		assertEquals(brandIdOthers, brandFilter.getId());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.BrandFilterImpl.getDisplayName(String)'.
	 */
	@Test
	public void testGetDisplayNameWithoutInitialization() {
		assertEquals(CatalogViewConstants.BRAND_FILTER_OTHERS, brandFilter.getDisplayName(Locale.US));
	}

	private void setupBrands() {
		// Create brand1
		brand1Uid = 1;
		final String brand1Name = "Test Brand name 1";
		final Brand brand1 = getBrand();
		brand1.setGuid(brand1Name);
		brand1.setUidPk(brand1Uid);
		brand1.setLocalizedProperties(new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = -721098376315688491L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		});
		brand1.getLocalizedProperties().setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.US, brand1Name);

		// Create brand2
		brand2Uid = 2;
		final String brand2Name = "Test Brand name 2";
		final Brand brand2 = getBrand();
		brand2.setUidPk(brand2Uid);
		brand2.setGuid(brand2Name);
		brand2.setLocalizedProperties(new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = 4717852796285485537L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		});
		brand2.getLocalizedProperties().setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.US, brand2Name);

	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.BrandFilterImpl.initialize(String)'.
	 */
	@Test
	public void testInitializeWithBadId() {
		
		context.checking(new Expectations() {
			{
				allowing(brandService).findByCode("rand");  will(throwException(new EpCatalogViewRequestBindException("")));
				allowing(brandService).findByCode("rand-aaa"); will(throwException(new EpCatalogViewRequestBindException("")));
				allowing(brandService).findByCode("aaa-333"); will(throwException(new EpCatalogViewRequestBindException("")));
			}
		});		
		
		try {
			this.brandFilter.initialize("some bad filter id");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.brandFilter.initialize("brand-aaa");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.brandFilter.initialize("brand");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.brandFilter.initialize("aaa-333");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		// Mock the persistence engine
		context.checking(new Expectations() {
			{
				oneOf(brandService).findByCode(String.valueOf(brand1Uid)); 
					will(throwException(new EpCatalogViewRequestBindException("")));				
			}
		});
		try {
			final String brandId = SeoConstants.BRAND_FILTER_PREFIX + brand1Uid;
			brandFilter.initialize(brandId);
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.BrandFilterImpl.equals()'.
	 */
	@Test
	public void testEquals() {
		
		final String brandName = "sfsf";
		final Brand brand = getBrand();
		brand.setUidPk(brand1Uid);
		brand.setLocalizedProperties(new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = 8970837694878603618L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		});

		brand.getLocalizedProperties().setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.US, brandName);
		brand.setCode(String.valueOf(brand1Uid));
		
		
		final String brandId = SeoConstants.BRAND_FILTER_PREFIX + brand1Uid;
		context.checking(new Expectations() {
			{
//				oneOf(brandService).get(brand1Uid); will(returnValue(getBrand()));
				oneOf(brandService).findByCode(String.valueOf(brand1Uid)); will(returnValue(brand));				
			}
		});
		
		brandFilter.initialize(brandId);

		final BrandFilterImpl anotherBrandFilter = new BrandFilterImpl();
		context.checking(new Expectations() {
			{
				oneOf(brandService).findByCode(String.valueOf(brand1Uid)); will(returnValue(brand));
			}
		});
		anotherBrandFilter.initialize(brandId);

		assertEquals(brandFilter, anotherBrandFilter);
		assertEquals(brandFilter.hashCode(), anotherBrandFilter.hashCode());
		assertFalse(brandFilter.equals(new Object()));
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.BrandFilterImpl.getSeoId()'.
	 */
	@Test
	public void testGetSeoId() {
		final String brandId = SeoConstants.BRAND_FILTER_PREFIX + brand1Uid;
		final Brand brand = getBrand();
		brand.setLocalizedProperties(new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = -1658284157363676060L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		});

		brand.setUidPk(brand1Uid);
		brand.setCode(String.valueOf(brand1Uid));
		
		final String brandName = "some name";
		brand.getLocalizedProperties().setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.US, brandName);
		context.checking(new Expectations() {
			{
				oneOf(brandService).findByCode(String.valueOf(brand1Uid)); will(returnValue(brand));
			}
		});
		brandFilter.initialize(brandId);
		assertEquals(SeoConstants.BRAND_FILTER_PREFIX + brand1Uid, brandFilter.getSeoId());
		assertEquals(brandName.toLowerCase(Locale.US), brandFilter.getSeoName(Locale.US));

		final String brandIdOthers = SeoConstants.BRAND_FILTER_PREFIX + CatalogViewConstants.BRAND_FILTER_OTHERS;
		brandFilter.initialize(brandIdOthers);
		assertEquals(SeoConstants.BRAND_FILTER_PREFIX + CatalogViewConstants.BRAND_FILTER_OTHERS, brandFilter.getSeoId());
		assertEquals(CatalogViewConstants.BRAND_FILTER_OTHERS, brandFilter.getSeoName(Locale.US));
	}
	
	/**
	 * Test method for {@link BrandFilterImpl#initializeWithCode(String)}.
	 */
	@Test
	public void testInitializeWithCode() {
		final String validBrandCode = "valid brand code";
		final String invalidBrandCode = "invalid brand code";

		context.checking(new Expectations() {
			{
				allowing(brandService).findByCode(validBrandCode); will(returnValue(getBrand()));

				allowing(brandService).findByCode(invalidBrandCode); will(returnValue(null));
			}
		});
		brandFilter.initializeWithCode(validBrandCode);
		
		try {
			brandFilter.initializeWithCode(invalidBrandCode);
			fail("Expected EpCatalogViewRequestBindException for invalid brand code.");
		} catch (EpCatalogViewRequestBindException e) {
			assertNotNull(e);
		}
	}
	
	/**
	 * Test method for {@link BrandFilterImpl#initializeWithCode(String)}.
	 */
	@Test
	public void testInitializeWithMultipleValidCodes() {
		final String validBrandCode = "valid brand code";
		final String separatorInToken = "%%%";

		context.checking(new Expectations() {
			{
				allowing(brandService).findByCode(validBrandCode); will(returnValue(getBrand()));
			}
		});
		
		brandFilter.setSeparatorInToken(separatorInToken);
		brandFilter.initializeWithCode(new String[] { validBrandCode, validBrandCode });
		
		String brandFilterId = brandFilter.getId();
		String[] splitBrandFilterId = brandFilterId.split(separatorInToken);
		int expectedBrandCodeTokens = 2;
		assertEquals(expectedBrandCodeTokens, splitBrandFilterId.length);
	}
	
	/**
	 * Test brand code with underscore.  Ensure initialize and parse work correctly.
	 */
	@Test
	public void testInitializeAndParseWithBrandCodeWithUnderscore() {
		final String brandCodeWithUnderscore = "TEST_CODE";
		final String separatorInToken = "%%%";

		context.checking(new Expectations() {
			{
				allowing(brandService).findByCode(brandCodeWithUnderscore); will(returnValue(getBrand(brandCodeWithUnderscore)));
			}
		});
		
		brandFilter.setSeparatorInToken(separatorInToken);
		brandFilter.initializeWithCode(new String[] { brandCodeWithUnderscore });
		
		String brandFilterId = brandFilter.getId();
		String[] splitBrandFilterId = brandFilterId.split(separatorInToken);
		int expectedBrandCodeTokens = 1;
		assertEquals(expectedBrandCodeTokens, splitBrandFilterId.length);
		
		Map<String, Object> parsedFilterString = brandFilter.parseFilterString(brandFilterId);
		
		@SuppressWarnings("unchecked")
		Set<Brand> brands = (Set<Brand>) parsedFilterString.get(BrandFilter.BRAND_PROPERTY_KEY);
		assertEquals("There should only be one brand", 1, brands.size());
		
		Brand firstBrand = (Brand) brands.toArray()[0];
		assertEquals(brandCodeWithUnderscore, firstBrand.getCode());
	}

	/**
	 * Returns a new <code>Brand</code> instance.
	 * 
	 * @return a new <code>Customer</code> instance
	 */
	protected Brand getBrand() {
		final BrandImpl brand = new BrandImpl() {
			private static final long serialVersionUID = -667392687197767816L;

			@Override
			public String toString() {
				return getCode();
			}	
			
		};
		brand.setGuid((new RandomGuidImpl()).toString());
		brand.setLocalizedProperties(new LocalizedPropertiesImpl());
		return brand;
	}
	
	private Brand getBrand(final String code) {
		final BrandImpl brand = new BrandImpl();
		brand.setCode(code);
		return brand;
	}
}
