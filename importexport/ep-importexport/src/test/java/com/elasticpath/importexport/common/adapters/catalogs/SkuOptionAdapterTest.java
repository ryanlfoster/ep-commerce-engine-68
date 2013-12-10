package com.elasticpath.importexport.common.adapters.catalogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.lang.LocaleUtils;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.catalogs.SkuOptionDTO;
import com.elasticpath.importexport.common.dto.catalogs.SkuOptionValueDTO;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * Verify that SkuOptionAdapterTest (from Catalogs) populates catalog domain object from DTO properly and vice versa. 
 * Nested adapters should be tested separately.
 */
public class SkuOptionAdapterTest {

	private static final String SKU_CODE = "sku_code";

	private static final Integer ORDERING = 7;

	private static final String LANGUAGE = "en";
	
	private static final DisplayValue DISPLAY_VALUE1 = new DisplayValue(LANGUAGE, "sku_name1");

	private static final DisplayValue DISPLAY_VALUE2 = new DisplayValue(LANGUAGE, "sku_name2");
	
	private static final Locale LANGUAGE_LOCALE = LocaleUtils.toLocale(LANGUAGE);
	
	private static final String SKU_OPTION_VALUE_CODE = "option_value_code";

	private static final String SKU_OPTION_VALUE_IMAGE = "image";
	
	private SkuOptionAdapter skuOptionAdapter;
	
	private SkuOptionValueAdapter skuOptionValueAdapter;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ElasticPath mockElasticPath;

	private CachingService mockCachingService;

	@Mock
	private SkuOptionService skuOptionService;
	
	@Before
	public void setUp() throws Exception {
		mockElasticPath = context.mock(ElasticPath.class);
		mockCachingService = context.mock(CachingService.class);
		
		skuOptionValueAdapter = new SkuOptionValueAdapter();
		skuOptionValueAdapter.setElasticPath(mockElasticPath);
		skuOptionValueAdapter.setCachingService(mockCachingService);
		
		context.checking(new Expectations() {
			{
				allowing(skuOptionService).optionValueKeyExists(with(any(String.class)));
				will(returnValue(false));
			}
		});
		
		skuOptionAdapter = new SkuOptionAdapter();
		skuOptionAdapter.setSkuOptionValueAdapter(skuOptionValueAdapter);
		skuOptionAdapter.setElasticPath(mockElasticPath);
		skuOptionAdapter.setCachingService(mockCachingService);
		skuOptionAdapter.setSkuOptionService(skuOptionService);

		context.checking(new Expectations() {
			{
				allowing(mockElasticPath).getBean(ContextIdNames.SKU_OPTION);
				will(returnValue(new SkuOptionImpl()));
			}
		});
	}

	/**
	 * Check that all required fields for Dto object are being set during domain population.
	 */
	@Test
	public void testPopulateDTO() {

		final SkuOption mockDomain = context.mock(SkuOption.class);
		final Catalog mockCatalog = context.mock(Catalog.class);
		final SkuOptionValue mockSkuOptionValue = context.mock(SkuOptionValue.class);

		context.checking(new Expectations() {
			{
				oneOf(mockDomain).getOptionKey();
				will(returnValue(SKU_CODE));
				atLeast(1).of(mockDomain).getCatalog();
				will(returnValue(mockCatalog));

				allowing(mockCatalog).getSupportedLocales();
				will(returnValue(Arrays.asList(LANGUAGE_LOCALE)));

				oneOf(mockDomain).getDisplayName(LANGUAGE_LOCALE, false);
				will(returnValue(DISPLAY_VALUE1.getValue()));

				oneOf(mockDomain).getOptionValues();
				will(returnValue(Arrays.asList(mockSkuOptionValue)));

				allowing(mockElasticPath).getBean(ContextIdNames.SKU_OPTION_VALUE);
				will(returnValue(mockSkuOptionValue));

				oneOf(mockSkuOptionValue).getOptionValueKey();
				will(returnValue(SKU_OPTION_VALUE_CODE));
				oneOf(mockSkuOptionValue).getImage();
				will(returnValue(SKU_OPTION_VALUE_IMAGE));
				oneOf(mockSkuOptionValue).getOrdering();
				will(returnValue(ORDERING));

				oneOf(mockSkuOptionValue).getSkuOption();
				will(returnValue(mockDomain));

				oneOf(mockSkuOptionValue).getDisplayName(LANGUAGE_LOCALE, false);
				will(returnValue(DISPLAY_VALUE2.getValue()));
			}
		});
		
		SkuOptionDTO dto = skuOptionAdapter.createDtoObject();
		skuOptionAdapter.populateDTO(mockDomain, dto);
		
		assertEquals(SKU_CODE, dto.getCode());
		assertEquals(1, dto.getNameValues().size());
		assertEquals(DISPLAY_VALUE1.getValue(), dto.getNameValues().get(0).getValue());
		assertEquals(1, dto.getSkuOptionValues().size());
		
		SkuOptionValueDTO skuOptionValueDTO = dto.getSkuOptionValues().get(0);
		
		assertEquals(SKU_OPTION_VALUE_CODE, skuOptionValueDTO.getCode());
		assertEquals(ORDERING, skuOptionValueDTO.getOrdering());
		assertEquals(SKU_OPTION_VALUE_IMAGE, skuOptionValueDTO.getImage());
		assertEquals(1, skuOptionValueDTO.getNameValues().size());
		assertEquals(DISPLAY_VALUE2.getValue(), skuOptionValueDTO.getNameValues().get(0).getValue());
	}

	/**
	 * Check that all required fields for domain object are being set during domain population.
	 */
	@Test
	public void testPopulateDomain() {
		SkuOptionValueDTO skuOptionValueDTO = new SkuOptionValueDTO();
		skuOptionValueDTO.setCode(SKU_OPTION_VALUE_CODE);
		skuOptionValueDTO.setImage(SKU_OPTION_VALUE_IMAGE);
		skuOptionValueDTO.setNameValues(Arrays.asList(DISPLAY_VALUE2));
		
		SkuOptionDTO dto = skuOptionAdapter.createDtoObject();
		dto.setCode(SKU_CODE);
		dto.setNameValues(Arrays.asList(DISPLAY_VALUE1));
		dto.setSkuOptionValues(Arrays.asList(skuOptionValueDTO));
		
		final SkuOption mockDomain = context.mock(SkuOption.class);
		final Catalog catalog = context.mock(Catalog.class);
		final SkuOption skuOption = context.mock(SkuOption.class, "second sku option");
		final SkuOptionValue mockSkuOptionValue = context.mock(SkuOptionValue.class);
		context.checking(new Expectations() {
			{
				allowing(catalog).getSupportedLocales();
				will(returnValue(Arrays.asList(LANGUAGE_LOCALE)));

				allowing(skuOption).getCatalog();
				will(returnValue(catalog));

				oneOf(mockDomain).setOptionKey(SKU_CODE);
				oneOf(mockDomain).setDisplayName(DISPLAY_VALUE1.getValue(), LANGUAGE_LOCALE);
				oneOf(mockDomain).getOptionValues();
				will(returnValue(Collections.emptySet()));
				oneOf(mockDomain).getOptionValue(SKU_OPTION_VALUE_CODE);
				will(returnValue(null));
				oneOf(mockDomain).getCatalog();
				will(returnValue(catalog));

				allowing(mockElasticPath).getBean(ContextIdNames.SKU_OPTION_VALUE);
				will(returnValue(mockSkuOptionValue));

				oneOf(mockSkuOptionValue).setOptionValueKey(SKU_OPTION_VALUE_CODE);
				oneOf(mockSkuOptionValue).setImage(SKU_OPTION_VALUE_IMAGE);
				oneOf(mockSkuOptionValue).setDisplayName(LANGUAGE_LOCALE, DISPLAY_VALUE2.getValue());
				oneOf(mockSkuOptionValue).getSkuOption();
				will(returnValue(skuOption));

				oneOf(mockDomain).addOptionValue(mockSkuOptionValue);
			}
		});
		
		skuOptionAdapter.populateDomain(dto, mockDomain);
	}
	
	/**
	 * Check that CreateDtoObject works. 
	 */
	@Test
	public void testCreateDomainObject() {
		assertNotNull(skuOptionAdapter.createDomainObject());
	}

	/**
	 * Check that createDomainObject works.
	 */
	@Test
	public void testCreateDtoObject() {
		assertNotNull(skuOptionAdapter.createDtoObject());
	}
}
