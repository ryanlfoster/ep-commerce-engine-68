package com.elasticpath.importexport.common.adapters.catalogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.lang.LocaleUtils;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.catalogs.BrandDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;

/**
 * Tests BrandAdapter.
 */
public class BrandAdapterTest {

	private static final String LANGUAGE = "en";

	private static final Locale LANGUAGE_LOCALE = LocaleUtils.toLocale(LANGUAGE);

	private static final String BRAND_DISPLAY_NAME = "Apple";

	private static final DisplayValue DISPLAY_VALUE = new DisplayValue(LANGUAGE, BRAND_DISPLAY_NAME);

	private static final String BRAND_CODE = "apple_code";

	private static final String BRAND_IMAGE = "path/to/apple";

	private BrandAdapter brandAdapter;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ElasticPath mockElasticPath;

	private CachingService mockCachingService;

	@Before
	public void setUp() throws Exception {
		mockElasticPath = context.mock(ElasticPath.class);
		mockCachingService = context.mock(CachingService.class);

		brandAdapter = new BrandAdapter();

		context.checking(new Expectations() {
			{
				allowing(mockElasticPath).getBean(ContextIdNames.BRAND);
				will(returnValue(new BrandImpl()));
			}
		});

		brandAdapter.setElasticPath(mockElasticPath);
		brandAdapter.setCachingService(mockCachingService);
	}

	@Test
	public void testPopulateDTO() {

		BrandDTO dto = brandAdapter.createDtoObject();

		final LocalizedProperties mockLocalizedProperties = context.mock(LocalizedProperties.class);
		final Catalog catalog = context.mock(Catalog.class);
		final Brand mockBrand = context.mock(Brand.class);
		context.checking(new Expectations() {
			{
				oneOf(mockLocalizedProperties).getValueWithoutFallBack(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, LANGUAGE_LOCALE);
				will(returnValue(BRAND_DISPLAY_NAME));

				oneOf(catalog).getSupportedLocales();
				will(returnValue(Arrays.asList(LANGUAGE_LOCALE)));


				oneOf(mockBrand).getCode();
				will(returnValue(BRAND_CODE));
				oneOf(mockBrand).getImageUrl();
				will(returnValue(BRAND_IMAGE));
				oneOf(mockBrand).getCatalog();
				will(returnValue(catalog));
				oneOf(mockBrand).getLocalizedProperties();
				will(returnValue(mockLocalizedProperties));
			}
		});

		brandAdapter.populateDTO(mockBrand, dto);

		assertEquals(BRAND_CODE, dto.getCode());
		assertEquals(BRAND_IMAGE, dto.getImage());

		final List<DisplayValue> nameValues = dto.getNameValues();
		assertEquals(1, nameValues.size());
		assertEquals(LANGUAGE, nameValues.get(0).getLanguage());
		assertEquals(BRAND_DISPLAY_NAME, nameValues.get(0).getValue());

	}

	@Test(expected = PopulationRollbackException.class)
	public void testPopulateDomainWithExpectedRollBack() {
		BrandDTO dto = brandAdapter.createDtoObject();

		dto.setCode("");
		brandAdapter.populateDomain(dto, null);
	}

	@Test

	public void testPopulateDomain() {
		BrandDTO dto = brandAdapter.createDtoObject();
		dto.setCode(BRAND_CODE);
		dto.setImage(BRAND_IMAGE);
		dto.setNameValues(Arrays.asList(DISPLAY_VALUE));

		final LocalizedProperties mockLocalizedProperties = context.mock(LocalizedProperties.class);
		final Brand mockBrand = context.mock(Brand.class);
		final Catalog mockCatalog = context.mock(Catalog.class);
		context.checking(new Expectations() {
			{
				oneOf(mockLocalizedProperties).setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, LANGUAGE_LOCALE, BRAND_DISPLAY_NAME);

				oneOf(mockBrand).setCode(BRAND_CODE);
				oneOf(mockBrand).setImageUrl(BRAND_IMAGE);
				oneOf(mockBrand).getLocalizedProperties();
				will(returnValue(mockLocalizedProperties));
				oneOf(mockBrand).setLocalizedProperties(mockLocalizedProperties);
				oneOf(mockBrand).getCatalog();
				will(returnValue(mockCatalog));
				oneOf(mockCatalog).getSupportedLocales();
				will(returnValue(Arrays.asList(LANGUAGE_LOCALE)));
			}
		});

		brandAdapter.populateDomain(dto, mockBrand);
	}

	/**
	 * Tests setLocalizedProperty.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testSetLocalizedPropertyWithExpectedRollback() {
		LocalizedProperties mockLocalizedProperties = context.mock(LocalizedProperties.class);
		final Brand mockBrand = context.mock(Brand.class);

		brandAdapter.setLocalizedProperty(mockLocalizedProperties, new DisplayValue("zuzu", "zuzu"), mockBrand);
	}
	@Test
	public void testSetLocalizedProperty() {
		final LocalizedProperties mockLocalizedProperties = context.mock(LocalizedProperties.class);
		final Catalog mockCatalog = context.mock(Catalog.class);
		final Brand mockBrand = context.mock(Brand.class);

		context.checking(new Expectations() {
			{
				oneOf(mockBrand).getCatalog();
				will(returnValue(mockCatalog));
				oneOf(mockCatalog).getSupportedLocales();
				will(returnValue(Arrays.asList(LANGUAGE_LOCALE)));

				oneOf(mockLocalizedProperties).setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, new Locale("en"), "hello");
			}
		});

		brandAdapter.setLocalizedProperty(mockLocalizedProperties, new DisplayValue("en", "hello"), mockBrand);
	}

	/**
	 * Tests creation of Domain Object.
	 */
	@Test
	public void testCreateDomainObject() {
		assertNotNull(brandAdapter.createDomainObject());
	}

	/**
	 * Tests creation of DTO Object.
	 */
	@Test
	public void testCreateDtoObject() {
		assertNotNull(brandAdapter.createDtoObject());
	}
}
