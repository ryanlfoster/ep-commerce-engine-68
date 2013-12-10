package com.elasticpath.importexport.common.adapters.category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.category.CategoryAvailabilityDTO;
import com.elasticpath.importexport.common.dto.category.CategoryDTO;
import com.elasticpath.importexport.common.dto.products.AttributeGroupDTO;
import com.elasticpath.importexport.common.dto.products.SeoDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * Verify that CategoryAdapter populates category domain object from DTO properly and vice versa. 
 * <br>Nested adapters should be tested separately.
 */
public class CategoryAdapterTest {

	private static final Date START_DATE = new Date(1);

	private static final Date END_DATE = new Date(2);

	private static final Locale LOCALE_EN = new Locale("en");

	private static final DisplayValue DISPLAY_VALUE_ZUZU_LOCALE = new DisplayValue("zuzu", "zuzu");

	private static final DisplayValue DISPLAY_VALUE_EN_LOCALE = new DisplayValue("en", "hope");

	private static final String PARENT_CATEGORY_CODE = "parentCategoryCode";

	private static final String CATALOG_CODE = "catalogCode";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ElasticPath mockElasticPath;
	
	private CachingService mockCachingService;

	private CategoryAdapter categoryAdapter;
	
	@Before
	public void setUp() throws Exception {

		mockElasticPath = context.mock(ElasticPath.class);
		mockCachingService = context.mock(CachingService.class);

		categoryAdapter = new CategoryAdapter();
		categoryAdapter.setElasticPath(mockElasticPath);
		categoryAdapter.setCachingService(mockCachingService);
	}
	
	/**
	 * Tests populateDTO.
	 */
	@Test
	public void testPopulateDTO() {
		final boolean storeVisible = false;
		final int categoryOrder = 1;
		final String categoryCode = "categoryCode";
		final String categoryTypeCode = "categoryTypeCode";
		
		final CategoryDTO categoryDto = new CategoryDTO();

		final Catalog mockCatalog = context.mock(Catalog.class);
		final Category mockCategory = context.mock(Category.class);
		final CategoryType mockCategoryType = context.mock(CategoryType.class);
		final Category mockParentCategory = context.mock(Category.class, "parent category");

		context.checking(new Expectations() {
			{
				oneOf(mockCategory).getCatalog();
				will(returnValue(mockCatalog));
				oneOf(mockCategory).getParent();
				will(returnValue(mockParentCategory));
				oneOf(mockCategory).getCode();
				will(returnValue(categoryCode));
				oneOf(mockCategory).getCategoryType();
				will(returnValue(mockCategoryType));
				oneOf(mockCategory).getOrdering();
				will(returnValue(categoryOrder));
				oneOf(mockCategory).isHidden();
				will(returnValue(!storeVisible));
				oneOf(mockCategory).getStartDate();
				will(returnValue(START_DATE));
				oneOf(mockCategory).getEndDate();
				will(returnValue(END_DATE));

				oneOf(mockCatalog).getCode();
				will(returnValue(CATALOG_CODE));
				oneOf(mockCategoryType).getName();
				will(returnValue(categoryTypeCode));
				oneOf(mockParentCategory).getCode();
				will(returnValue(PARENT_CATEGORY_CODE));
			}
		});
		
		categoryAdapter = new CategoryAdapter() {
			@Override
			void populateDtoNameValues(final Category category, final CategoryDTO categoryDTO) {
				assertNotNull(category);
				categoryDTO.setNameValues(Arrays.asList(DISPLAY_VALUE_EN_LOCALE));
			}
			@Override
			void populateNestedDto(final Category category, final CategoryDTO categoryDto) {
				assertNotNull(category);
				assertNotNull(categoryDto);
				categoryDto.setAttributeGroupDTO(new AttributeGroupDTO());
				categoryDto.setSeoDto(new SeoDTO());
			}
		};
		categoryAdapter.setElasticPath(mockElasticPath);
		categoryAdapter.setCachingService(mockCachingService);
		categoryAdapter.populateDTO(mockCategory, categoryDto);
	
		assertEquals(categoryCode, categoryDto.getCategoryCode());
		assertEquals(categoryOrder, categoryDto.getOrder());
		assertEquals(CATALOG_CODE, categoryDto.getCatalogCode());
		assertEquals(PARENT_CATEGORY_CODE, categoryDto.getParentCategoryCode());
		assertEquals(categoryTypeCode, categoryDto.getCategoryType());
		
		final CategoryAvailabilityDTO categoryAvailabilityDTO = categoryDto.getCategoryAvailabilityDTO();
		assertEquals(storeVisible, categoryAvailabilityDTO.isStoreVisible());
		assertEquals(START_DATE, categoryAvailabilityDTO.getStartDate());
		assertEquals(END_DATE, categoryAvailabilityDTO.getEndDate());
		
		assertNotNull(categoryDto.getAttributeGroupDTO());
		assertNotNull(categoryDto.getSeoDto());
	}

	/**
	 * Tests population of category DTO with names when category is held in master catalog.
	 */
	@Test
	public void testPopulateDtoNameValuesWhenCatalogIsMaster() {
		final CategoryDTO categoryDto = new CategoryDTO();
		final Category mockCategory = context.mock(Category.class);
		final LocaleDependantFields mockDependantFields = context.mock(LocaleDependantFields.class);
		
		final Catalog mockCatalog = context.mock(Catalog.class);
		context.checking(new Expectations() {
			{
				oneOf(mockCategory).getCatalog();
				will(returnValue(mockCatalog));
				oneOf(mockCatalog).getSupportedLocales();
				will(returnValue(Arrays.asList(LOCALE_EN)));

				oneOf(mockCategory).getLocaleDependantFieldsWithoutFallBack(LOCALE_EN);
				will(returnValue(mockDependantFields));

				oneOf(mockDependantFields).getDisplayName();
				will(returnValue(DISPLAY_VALUE_EN_LOCALE.getValue()));
			}
		});

		categoryAdapter.populateDtoNameValues(mockCategory, categoryDto);

		final List<DisplayValue> nameValues = categoryDto.getNameValues();
		assertEquals(DISPLAY_VALUE_EN_LOCALE.getLanguage(), nameValues.get(0).getLanguage());
		assertEquals(DISPLAY_VALUE_EN_LOCALE.getValue(), nameValues.get(0).getValue());
		assertEquals(1, nameValues.size());
	}

	/**
	 * Tests population of category DTO with names when category is held in virtual catalog.
	 */
	@Test
	public void testPopulateDtoNameValuesWhenCatalogIsVirtual() {
		final CategoryDTO categoryDto = new CategoryDTO();
		final Category mockCategory = context.mock(Category.class);
		final LocaleDependantFields mockDependantFields = context.mock(LocaleDependantFields.class);
		
		final Catalog mockCatalog = context.mock(Catalog.class);
		context.checking(new Expectations() {
			{
				oneOf(mockCategory).getCatalog();
				will(returnValue(mockCatalog));
				oneOf(mockCatalog).getSupportedLocales();
				will(returnValue(Arrays.asList(LOCALE_EN)));

				oneOf(mockCategory).getLocaleDependantFieldsWithoutFallBack(LOCALE_EN);
				will(returnValue(mockDependantFields));

				oneOf(mockDependantFields).getDisplayName();
				will(returnValue(DISPLAY_VALUE_EN_LOCALE.getValue()));
			}
		});

		categoryAdapter.populateDtoNameValues(mockCategory, categoryDto);

		final List<DisplayValue> nameValues = categoryDto.getNameValues();
		assertEquals(DISPLAY_VALUE_EN_LOCALE.getLanguage(), nameValues.get(0).getLanguage());
		assertEquals(DISPLAY_VALUE_EN_LOCALE.getValue(), nameValues.get(0).getValue());
		assertEquals(1, nameValues.size());
	}

	/**
	 * Tests populateDomain.
	 */
	@Test
	public void testPopulateDomain() {
		final boolean storeVisible = false;
		final int categoryOrder = 1;
		final String categoryCode = "categoryCode";
		final String categoryTypeCode = "categoryTypeCode";
		final Map<Locale, LocaleDependantFields> localeDependantFieldsMap = new HashMap<Locale, LocaleDependantFields>();
		final Catalog catalog = context.mock(Catalog.class);
		final CategoryType categoryType = context.mock(CategoryType.class);
		
		final Category mockCategory = context.mock(Category.class);
		final CategoryDTO categoryDto = new CategoryDTO();
		
		CategoryAvailabilityDTO categoryAvailabilityDTO = new CategoryAvailabilityDTO();
		categoryAvailabilityDTO.setStoreVisible(storeVisible);
		categoryAvailabilityDTO.setStartDate(START_DATE);
		categoryAvailabilityDTO.setEndDate(END_DATE);
		
		categoryDto.setCategoryCode(categoryCode);
		categoryDto.setOrder(categoryOrder);
		categoryDto.setCatalogCode(CATALOG_CODE);
		categoryDto.setParentCategoryCode(PARENT_CATEGORY_CODE);
		categoryDto.setCategoryType(categoryTypeCode);
		categoryDto.setCategoryAvailabilityDTO(categoryAvailabilityDTO);
		categoryDto.setAttributeGroupDTO(new AttributeGroupDTO());
		categoryDto.setSeoDto(new SeoDTO());

		context.checking(new Expectations() {
			{
				oneOf(mockCategory).setCode(categoryCode);
				oneOf(mockCategory).setOrdering(categoryOrder);
				oneOf(mockCategory).setCatalog(catalog);
				oneOf(mockCategory).setParent(null);
				oneOf(mockCategory).setCategoryType(categoryType);
				oneOf(mockCategory).setLocaleDependantFieldsMap(localeDependantFieldsMap);
				oneOf(mockCategory).setHidden(!storeVisible);
				oneOf(mockCategory).setStartDate(START_DATE);
				oneOf(mockCategory).setEndDate(END_DATE);
			}
		});
		
		categoryAdapter = new CategoryAdapter() {
			@Override
			void populateMasterCatalog(final Category category, final String catalogCode) {
				assertEquals(CATALOG_CODE, catalogCode);
				category.setCatalog(catalog);
			}
			@Override
			void populateParentCategory(final Category category, final CategoryDTO categoryDto) {
				assertEquals(PARENT_CATEGORY_CODE, categoryDto.getParentCategoryCode());
				category.setParent(null); // Could not find parent category with code
			}
			@Override
			void populateDomainNameValues(final Category category, final CategoryDTO categoryDto) {
				assertNotNull(categoryDto.getNameValues());
				category.setLocaleDependantFieldsMap(localeDependantFieldsMap);
			}
			@Override
			void populateCategoryType(final Category category, final String categoryTypeName) {
				assertEquals(categoryTypeCode, categoryTypeName);
				category.setCategoryType(categoryType);
			}
			@Override
			void populateAvailability(final Category category, final CategoryAvailabilityDTO categoryAvailabilityDTO) {
				category.setHidden(!categoryAvailabilityDTO.isStoreVisible());
				category.setStartDate(categoryAvailabilityDTO.getStartDate());
				category.setEndDate(categoryAvailabilityDTO.getEndDate());
			}
			@Override
			void populateNestedDomainObjects(final Category category, final CategoryDTO categoryDTO) {
				assertNotNull(category);
				assertNotNull(categoryDto);
			}
		};
		categoryAdapter.setElasticPath(mockElasticPath);
		categoryAdapter.setCachingService(mockCachingService);
		categoryAdapter.populateDomain(categoryDto, mockCategory);
	}
	
	/**
	 * Tests populateDomain.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testPopulateDomainThrowsOnWrongCode() {
		final Category mockCategory = context.mock(Category.class);
		final CategoryDTO categoryDto = new CategoryDTO();

		categoryDto.setCategoryCode("");
		categoryAdapter.populateDomain(categoryDto, mockCategory);
	}

	/**
	 * Tests populateMasterCatalog.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testPopulateMasterCatalog() {
		final String goodCatalogCode = "goodCatalog";
		final String baddCatalogCode = "baddCatalog";
		
		final Catalog mockCatalog = context.mock(Catalog.class);
		final Category mockCategory = context.mock(Category.class);

		context.checking(new Expectations() {
			{
				oneOf(mockCachingService).findCatalogByCode(goodCatalogCode);
				will(returnValue(mockCatalog));
				oneOf(mockCachingService).findCatalogByCode(baddCatalogCode);
				will(returnValue(null));
				oneOf(mockCatalog).isMaster();
				will(returnValue(true));

				oneOf(mockCategory).setCatalog(mockCatalog);
				oneOf(mockCategory).setVirtual(false);
			}
		});

		categoryAdapter.populateMasterCatalog(mockCategory , goodCatalogCode);
		
		categoryAdapter.populateMasterCatalog(mockCategory, baddCatalogCode);
	}

	/**
	 * Tests populateAvailability.
	 */
	@Test
	public void testPopulateAvailability() {
		final boolean storeVisible = true;
		CategoryAvailabilityDTO categoryAvailabilityDTO = new CategoryAvailabilityDTO();
		categoryAvailabilityDTO.setStoreVisible(storeVisible);
		categoryAvailabilityDTO.setStartDate(START_DATE);
		categoryAvailabilityDTO.setEndDate(END_DATE);
		
		final Category mockCategory = context.mock(Category.class);
		context.checking(new Expectations() {
			{
				oneOf(mockCategory).setHidden(!storeVisible);
				oneOf(mockCategory).setStartDate(START_DATE);
				oneOf(mockCategory).setEndDate(END_DATE);
			}
		});
		
		categoryAdapter.populateAvailability(mockCategory, categoryAvailabilityDTO);
		categoryAdapter.populateAvailability(mockCategory, null); // should work
	}
	
	/**
	 * Tests isAvailabilityDatesCorrect.
	 */
	@Test
	public void testIsAvailabilityDatesCorrect() {
		Date date1 = new Date(1);
		Date date2 = new Date(2);
		assertTrue(categoryAdapter.isAvailabilityDatesCorrect(date1, date2));
		
		date1.setTime(2);
		assertFalse(categoryAdapter.isAvailabilityDatesCorrect(date1, date2));
		
		date1.setTime(2 + 1);
		assertFalse(categoryAdapter.isAvailabilityDatesCorrect(date1, date2));
		
		assertTrue(categoryAdapter.isAvailabilityDatesCorrect(date1, null));
	}
	
	/**
	 * Tests populateCategoryType.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testPopulateCategoryType() {
		final String goodCategoryType = "goodCategoryType";
		final String baddCategoryType = "baddCategoryType";
		
		final CategoryType mockCategoryType = context.mock(CategoryType.class);
		final Category mockCategory = context.mock(Category.class);

		context.checking(new Expectations() {
			{
				oneOf(mockCachingService).findCategoryTypeByName(goodCategoryType);
				will(returnValue(mockCategoryType));
				oneOf(mockCachingService).findCategoryTypeByName(baddCategoryType);
				will(returnValue(null));
			}
		});
		
		final Catalog mockCatalog = context.mock(Catalog.class);
		context.checking(new Expectations() {
			{
				oneOf(mockCatalog).getCode();
				will(returnValue("code"));
				allowing(mockCategory).getCatalog();
				will(returnValue(mockCatalog));
				oneOf(mockCategory).setCategoryType(mockCategoryType);
				oneOf(mockCategoryType).getCatalog();
				will(returnValue(mockCatalog));
			}
		});
		
		categoryAdapter.populateCategoryType(mockCategory , goodCategoryType);
		
		categoryAdapter.populateCategoryType(mockCategory, baddCategoryType);
	}

	/**
	 * Tests populateParentCategory.
	 */
	@Test
	public void testPopulateParentCategory() {
		final Category mockCategory = context.mock(Category.class);
		
		final CategoryDTO categoryDto = new CategoryDTO();
		
		categoryDto.setParentCategoryCode(null);
		categoryAdapter.populateParentCategory(mockCategory, categoryDto);
		
		categoryDto.setParentCategoryCode("");
		categoryAdapter.populateParentCategory(mockCategory, categoryDto);

		context.checking(new Expectations() {
			{
				oneOf(mockCachingService).findCategoryByCode(PARENT_CATEGORY_CODE, CATALOG_CODE);
				will(returnValue(null));

				oneOf(mockCategory).setParent(null);
			}
		});
		
		categoryDto.setParentCategoryCode(PARENT_CATEGORY_CODE);
		categoryDto.setCatalogCode(CATALOG_CODE);
		categoryAdapter.populateParentCategory(mockCategory, categoryDto);
	}

	/**
	 * Tests populateDumainNameValues.
	 */
	@Test(expected = PopulationRuntimeException.class)
	public void testPopulateDomainNameValues() {
		final Category mockCategory = context.mock(Category.class);
		final LocaleDependantFields mockDependantFields = context.mock(LocaleDependantFields.class);
		
		Map<Locale, LocaleDependantFields> localeDependantFieldsMap = new HashMap<Locale, LocaleDependantFields>();
		
		final CategoryDTO categoryDto = new CategoryDTO();		
		categoryDto.setNameValues(Arrays.asList(DISPLAY_VALUE_EN_LOCALE, DISPLAY_VALUE_ZUZU_LOCALE));

		context.checking(new Expectations() {
			{
				oneOf(mockCategory).getLocaleDependantFieldsWithoutFallBack(LOCALE_EN);
				will(returnValue(mockDependantFields));

				oneOf(mockDependantFields).setDisplayName(DISPLAY_VALUE_EN_LOCALE.getValue());
			}
		});
		
		localeDependantFieldsMap.put(LOCALE_EN, mockDependantFields);
				
		//oneOf(mockCategory).setLocaleDependantFieldsMap(localeDependantFieldsMap);
		
		categoryAdapter.populateDomainNameValues(mockCategory, categoryDto);
	}


	/**
	 * Check that CreateDtoObject works. 
	 */
	@Test
	public void testCreateDomainObject() {
		context.checking(new Expectations() {
			{
				allowing(mockElasticPath).getBean(ContextIdNames.CATEGORY);
				will(returnValue(new CategoryImpl()));
			}
		});
		assertNotNull(categoryAdapter.createDomainObject());
	}

	/**
	 * Check that createDomainObject works.
	 */
	@Test
	public void testCreateDtoObject() {
		assertNotNull(categoryAdapter.createDtoObject());
	}

}
