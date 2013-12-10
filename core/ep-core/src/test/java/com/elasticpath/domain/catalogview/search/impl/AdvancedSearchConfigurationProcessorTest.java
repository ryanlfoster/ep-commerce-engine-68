package com.elasticpath.domain.catalogview.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.domain.attribute.impl.AbstractAttributeValueImpl;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.impl.AttributeValueFilterImpl;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test for AdvancedSearchConfigurationProviderImpl.
 *
 */
public class AdvancedSearchConfigurationProcessorTest {

	private static final int THREE = 3;
	private static final String ATTRIBUTE_KEY = "A00556";
	private static final String STORE_CODE = "testStore";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private AdvancedSearchConfigurationProviderImpl advancedSearchConfigurationProcessor;
	private Attribute attribute;
	private AttributeValueFilter attributeValueFilterTest1;
	private AttributeValueFilter attributeValueFilterTest2;

	private FilterFactory filterFactory;
	private String brandOneCode;
    private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Set up of data.
	 */
	@Before
	public void setUp() {
		advancedSearchConfigurationProcessor = new AdvancedSearchConfigurationProviderImpl();

		filterFactory = context.mock(FilterFactory.class);
		final AttributeService attributeService = context.mock(AttributeService.class);

		advancedSearchConfigurationProcessor.setAttributeService(attributeService);
		advancedSearchConfigurationProcessor.setFilterFactory(filterFactory);

		attribute = new AttributeImpl();
		attribute.setKey(ATTRIBUTE_KEY);
		attribute.setLocaleDependant(true);

		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		context.checking(new Expectations() {
            {
                allowing(filterFactory).getAllSimpleValuesMap(STORE_CODE);
                will(returnValue(initiateAndBuildSimpleValuesMap()));
            }
        });
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests that we get an attributeValueFilterMap containing the attributes defined in the factory as keys
	 * and the AttributeValueFilters associated with it.
	 */
	@Test
	public void getAttributeValueFilterMap() {
		Locale locale = Locale.ENGLISH;

		Map<Attribute, List<AttributeValueFilter>> resultMap =
			advancedSearchConfigurationProcessor.getAttributeValueFilterMap(STORE_CODE, locale);

		//only has 1 attribute
		assertEquals(1, resultMap.size());

		//make sure our attributeValue has 2 AttributeValueFilters
		List<AttributeValueFilter> attributeValueFilterList = resultMap.get(attribute);
		assertEquals(2, attributeValueFilterList.size());
		assertTrue(attributeValueFilterList.contains(attributeValueFilterTest1));
		assertTrue(attributeValueFilterList.contains(attributeValueFilterTest2));

	}

	private Map<String, AttributeValueFilter> initiateAndBuildSimpleValuesMap() {
		final Map<String, AttributeValueFilter> attributeSimpleValues = new HashMap<String, AttributeValueFilter>();

		AttributeValueFilter attributeValueFilterRoot = new AttributeValueFilterTesting();
		attributeValueFilterRoot.setAttribute(attribute);
		attributeValueFilterRoot.setId("atA00556");
		attributeValueFilterRoot.setLocalized(true);

		attributeValueFilterTest1 = new AttributeValueFilterTesting();
		attributeValueFilterTest1.setAttribute(attribute);
		attributeValueFilterTest1.setId("atA00556_01");
		attributeValueFilterTest1.setLocalized(true);
		attributeValueFilterTest1.setLocale(Locale.ENGLISH);
		attributeValueFilterTest1.setAttributeValue(createAttributeValue(attribute, "test1"));

		attributeValueFilterTest2 = new AttributeValueFilterTesting();
		attributeValueFilterTest2.setAttribute(attribute);
		attributeValueFilterTest2.setId("atA00556_02");
		attributeValueFilterTest2.setLocalized(true);
		attributeValueFilterTest2.setLocale(Locale.ENGLISH);
		attributeValueFilterTest2.setAttributeValue(createAttributeValue(attribute, "test2"));

		//the 3rd attribute is of a different locale
		AttributeValueFilter attributeValueFilterTest3 = new AttributeValueFilterTesting();
		attributeValueFilterTest3.setAttribute(attribute);
		attributeValueFilterTest3.setId("atA00556_03");
		attributeValueFilterTest3.setLocalized(true);
		attributeValueFilterTest3.setLocale(Locale.FRENCH);
		attributeValueFilterTest3.setAttributeValue(createAttributeValue(attribute, "test3"));

		attributeSimpleValues.put(attributeValueFilterRoot.getId(), attributeValueFilterRoot);
		attributeSimpleValues.put(attributeValueFilterTest1.getId(), attributeValueFilterTest1);
		attributeSimpleValues.put(attributeValueFilterTest2.getId(), attributeValueFilterTest2);
		attributeSimpleValues.put(attributeValueFilterTest3.getId(), attributeValueFilterTest3);

		return attributeSimpleValues;
	}

	private AttributeValueWithType createAttributeValue(final Attribute attribute, final Object value) {
		AttributeValueWithType attrvalue = new AbstractAttributeValueImpl() {
			private static final long serialVersionUID = -6005502686468293135L;

			public long getUidPk() {
				return 0;
			}

			public void setUidPk(final long uidPk) {
				// do nothing
			}
		};

		attrvalue.setAttribute(attribute);
		attrvalue.setAttributeType(AttributeType.SHORT_TEXT);
		attrvalue.setValue(value);
		return attrvalue;
	}

	/**
	 * Test getting a list of brands that are defined in the system.
	 */
	@Test
	public void testGetThreeBrandsInListSorted() {
		final BrandService brandService = context.mock(BrandService.class);


		advancedSearchConfigurationProcessor.setBrandService(brandService);

		final List<Brand> brandsInUse = new ArrayList<Brand>();

		Brand brandOne = new BrandImplTesting();

		brandOneCode = "brand1";
		brandOne.setCode(brandOneCode);

		Brand brandTwo = new BrandImplTesting();
		String brandTwoCode = "brand2";
		brandTwo.setCode(brandTwoCode);

		Brand brandThree = new BrandImplTesting();
		String brandThreeCode = "brand3";
		brandThree.setCode(brandThreeCode);

		brandsInUse.add(brandThree);
		brandsInUse.add(brandOne);
		brandsInUse.add(brandTwo);

		final Set<String> brandCodesFromConfiguration = new HashSet<String>();
		brandCodesFromConfiguration.add(brandThreeCode);
		brandCodesFromConfiguration.add(brandOneCode);
		brandCodesFromConfiguration.add(brandTwoCode);


		context.checking(new Expectations() {
			{
				allowing(brandService).getBrandInUseList();
				will(returnValue(brandsInUse));

				allowing(filterFactory).getDefinedBrandCodes(STORE_CODE);
				will(returnValue(brandCodesFromConfiguration));
			}
		});

		advancedSearchConfigurationProcessor.getBrandListSortedByName(Locale.ENGLISH, STORE_CODE);

		List<Brand> brandListSortedByName = advancedSearchConfigurationProcessor.
				getBrandListSortedByName(Locale.ENGLISH, STORE_CODE);

		assertEquals(THREE, brandListSortedByName.size());
		//make sure it's sorted
		assertTrue(brandListSortedByName.get(0).equals(brandOne));
		assertTrue(brandListSortedByName.get(1).equals(brandTwo));
		assertTrue(brandListSortedByName.get(2).equals(brandThree));
	}

	/**
	 * Having one brand only defined in the configuration with two brands in store will return only
	 * the brand in the store.
	 */
	@Test
	public void oneBrandOnlyInConfiguration() {
		final BrandService brandService = context.mock(BrandService.class);


		advancedSearchConfigurationProcessor.setBrandService(brandService);

		final List<Brand> brandsInUse = new ArrayList<Brand>();

		Brand brand1 = new BrandImplTesting();

		brand1.setCode(brandOneCode);

		String brandTwoCode = "brand2";

		brandsInUse.add(brand1);

		final Set<String> brandCodesFromConfiguration = new HashSet<String>();
		brandCodesFromConfiguration.add(brandOneCode);
		brandCodesFromConfiguration.add(brandTwoCode);

		context.checking(new Expectations() {
			{
				allowing(brandService).getBrandInUseList();
				will(returnValue(brandsInUse));

				allowing(filterFactory).getDefinedBrandCodes(STORE_CODE);
				will(returnValue(brandCodesFromConfiguration));
			}
		});

		advancedSearchConfigurationProcessor.getBrandListSortedByName(Locale.ENGLISH, STORE_CODE);

		List<Brand> brandListSortedByName = advancedSearchConfigurationProcessor.
				getBrandListSortedByName(Locale.ENGLISH, STORE_CODE);

		assertEquals(1, brandListSortedByName.size());
		assertTrue(brandListSortedByName.get(0).equals(brand1));
	}

	/**
	 * Having one brand only in the store with two brands defined in configuration will return only
	 * the brand in the store.
	 */
	@Test
	public void oneBrandOnlyInStore() {
		final BrandService brandService = context.mock(BrandService.class);


		advancedSearchConfigurationProcessor.setBrandService(brandService);

		final List<Brand> brandsInUse = new ArrayList<Brand>();

		Brand brand1 = new BrandImplTesting();

		brand1.setCode(brandOneCode);

		brandsInUse.add(brand1);

		final Set<String> brandCodesFromConfiguration = new HashSet<String>();
		brandCodesFromConfiguration.add(brandOneCode);

		context.checking(new Expectations() {
			{
				allowing(brandService).getBrandInUseList();
				will(returnValue(brandsInUse));

				allowing(filterFactory).getDefinedBrandCodes(STORE_CODE);
				will(returnValue(brandCodesFromConfiguration));
			}
		});

		advancedSearchConfigurationProcessor.getBrandListSortedByName(Locale.ENGLISH, STORE_CODE);

		List<Brand> brandListSortedByName = advancedSearchConfigurationProcessor.
				getBrandListSortedByName(Locale.ENGLISH, STORE_CODE);

		assertEquals(1, brandListSortedByName.size());
		assertTrue(brandListSortedByName.get(0).equals(brand1));
	}

	/**
	 * No predefined brands in configuration means the list will be empty.
	 */
	@Test
	public void noPreDefinedBrands() {
		final BrandService brandService = context.mock(BrandService.class);

		advancedSearchConfigurationProcessor.setBrandService(brandService);

		final List<Brand> brandsInUse = new ArrayList<Brand>();

		Brand brand1 = new BrandImplTesting();

		brand1.setCode(brandOneCode);

		brandsInUse.add(brand1);

		final Set<String> brandCodesFromConfiguration = new HashSet<String>();

		context.checking(new Expectations() {
			{
				allowing(brandService).getBrandInUseList();
				will(returnValue(brandsInUse));

				allowing(filterFactory).getDefinedBrandCodes(STORE_CODE);
				will(returnValue(brandCodesFromConfiguration));
			}
		});

		advancedSearchConfigurationProcessor.getBrandListSortedByName(Locale.ENGLISH, STORE_CODE);

		List<Brand> brandListSortedByName = advancedSearchConfigurationProcessor.
				getBrandListSortedByName(Locale.ENGLISH, STORE_CODE);

		assertEquals(0, brandListSortedByName.size());
	}

	/**
	 * Private class used for testing.
	 *
	 */
	private class BrandImplTesting extends BrandImpl {
		private static final long serialVersionUID = -7247891537434996950L;

		@Override
		public String toString() {
			return getCode();
		}

		@Override
		public String getDisplayName(final Locale locale, final boolean fallback) {
			return getCode();
		}
	}
	/**
	 * Private class for testing.
	 *
	 */
	private class AttributeValueFilterTesting extends AttributeValueFilterImpl {
		private static final long serialVersionUID = 8729083447455035454L;

		private String attributeKey;

		@Override
		public void setAttributeKey(final String attributeKey) {
			this.attributeKey = attributeKey;
		}

		@Override
		public String getAttributeKey() {
			return attributeKey;
		}
	}
}
