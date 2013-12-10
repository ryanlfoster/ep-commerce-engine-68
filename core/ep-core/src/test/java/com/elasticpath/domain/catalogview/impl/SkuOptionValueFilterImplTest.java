package com.elasticpath.domain.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.SkuOptionValueFilter;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * The junit test class for sku option value filter.
 */
public class SkuOptionValueFilterImplTest {

	private static final String OPTION_VALUE_KEY_RED = "Red";
	private static final String OPTION_VALUE_KEY_BLUE = "Blue";
	private static final String SEO_URL_FOR_RED = SeoConstants.SKU_OPTION_VALUE_PREFIX + OPTION_VALUE_KEY_RED;
	private static final String SEO_URL_FOR_RED_BLUE = SeoConstants.SKU_OPTION_VALUE_PREFIX 
														+ OPTION_VALUE_KEY_RED 
														+ SeoConstants.DEFAULT_SEPARATOR_IN_TOKEN 
														+ OPTION_VALUE_KEY_BLUE;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final SkuOptionService mockSkuOptionService = context.mock(SkuOptionService.class); 
	
	/**
	 * Test initialize sku option value filter with single sku option value.
	 */
	@Test
	public void testInitializeForSingle() {
		SkuOptionValueFilter filter = getSkuOptionValueFilterWithMockService();
		
		final SkuOptionValue skuOptionValue = context.mock(SkuOptionValue.class);
		
		context.checking(new Expectations() { {
			oneOf(mockSkuOptionService).findOptionValueByKey(OPTION_VALUE_KEY_RED);	will(returnValue(skuOptionValue));
		} });
		
		filter.initialize(SEO_URL_FOR_RED);
		
		assertEquals("sku option value is not expected", skuOptionValue, filter.getSkuOptionValues().iterator().next());
	}
	
	/**
	 * Test initialize() and getSeoId() for SKU option value codes with underscore.
	 */
	@Test
	public void testInitializeAndGetSeoIdForSkuOptionCodesWithUnderscore() {
		final String optionValueKeyWithUnderscore = "TEST_KEY1";
		final String optionValueKeyWithUnderscore2 = "TEST_KEY2";
		final String separatorInToken = "%%%";
		String seoUrl = SeoConstants.SKU_OPTION_VALUE_PREFIX + optionValueKeyWithUnderscore + separatorInToken + optionValueKeyWithUnderscore2;
		
		SkuOptionValueFilter filter = getSkuOptionValueFilterWithMockService();
		filter.setSeparatorInToken(separatorInToken);
		
		final SkuOptionValue mockSkuOptionValue = context.mock(SkuOptionValue.class);
		
		context.checking(new Expectations() { {
			allowing(mockSkuOptionService).findOptionValueByKey(optionValueKeyWithUnderscore);	will(returnValue(mockSkuOptionValue));
		} });
		
		Set<SkuOptionValue> skuOptionValueSet = createSkuOptionValueSet(optionValueKeyWithUnderscore, optionValueKeyWithUnderscore2);
		Map<String, Object> propertiesMapForInitialize = getPropertiesMapForInitial(skuOptionValueSet);
		filter.initialize(propertiesMapForInitialize);
		
		assertEquals(seoUrl, filter.getSeoId());
	}
	
	private Set<SkuOptionValue> createSkuOptionValueSet(final String optionValueKey1, final String optionValueKey2) {
		Set<SkuOptionValue> skuOptionValueSet = new LinkedHashSet<SkuOptionValue>();
		SkuOptionImpl skuOption = new SkuOptionImpl();
		
		final SkuOptionValue skuOptionValue1 = new SkuOptionValueImpl();
		skuOptionValue1.setOptionValueKey(optionValueKey1);
		skuOptionValue1.setSkuOption(skuOption);
		skuOptionValueSet.add(skuOptionValue1);
		
		final SkuOptionValue skuOptionValue2 = new SkuOptionValueImpl();
		skuOptionValue2.setOptionValueKey(optionValueKey2);
		skuOptionValue2.setSkuOption(skuOption);
		skuOptionValueSet.add(skuOptionValue2);
		
		return skuOptionValueSet;
	}

	/**
	 * Test initialize sku option value filter with multiple sku option value.
	 */
	@Test
	public void testInitializeForMultiple() {
		SkuOptionValueFilter skuOptionValueFilter = getSkuOptionValueFilterWithMockService();
		
		final SkuOptionValue skuOptionValueForRed = context.mock(SkuOptionValue.class, OPTION_VALUE_KEY_RED);
		final SkuOptionValue skuOptionValueForBlue = context.mock(SkuOptionValue.class, OPTION_VALUE_KEY_BLUE);
		final SkuOption skuOption = context.mock(SkuOption.class);
		
		context.checking(new Expectations() { {
			oneOf(mockSkuOptionService).findOptionValueByKey(OPTION_VALUE_KEY_RED);
			will(returnValue(skuOptionValueForRed));
			
			oneOf(mockSkuOptionService).findOptionValueByKey(OPTION_VALUE_KEY_BLUE);
			will(returnValue(skuOptionValueForBlue));
			
			oneOf(skuOptionValueForRed).getSkuOption();
			will(returnValue(skuOption));
			
			oneOf(skuOptionValueForBlue).getSkuOption();
			will(returnValue(skuOption));
		} });
		
		skuOptionValueFilter.initialize(SEO_URL_FOR_RED_BLUE);
		
		Iterator<SkuOptionValue> returnSkuOptionValuesIterator = skuOptionValueFilter.getSkuOptionValues().iterator();
		assertEquals("should get sku option value for red", skuOptionValueForRed, returnSkuOptionValuesIterator.next());
		assertEquals("should get sku option value for blue", skuOptionValueForBlue, returnSkuOptionValuesIterator.next());
	}
	
	/**
	 * Test initialize sku option value filter with a seo string and the getSeoId() method should return the same seo string.
	 */
	@Test
	public void testInitializeForMultipleAndGetSameSeoId() {
		SkuOptionValueFilter skuOptionValueFilter = getSkuOptionValueFilterWithMockService();
		
		final SkuOptionValue skuOptionValueForRed = context.mock(SkuOptionValue.class, OPTION_VALUE_KEY_RED);
		final SkuOptionValue skuOptionValueForBlue = context.mock(SkuOptionValue.class, OPTION_VALUE_KEY_BLUE);
		final SkuOption skuOption = context.mock(SkuOption.class);
		
		context.checking(new Expectations() { {
			oneOf(mockSkuOptionService).findOptionValueByKey(OPTION_VALUE_KEY_RED);
			will(returnValue(skuOptionValueForRed));
			
			oneOf(mockSkuOptionService).findOptionValueByKey(OPTION_VALUE_KEY_BLUE);
			will(returnValue(skuOptionValueForBlue));
			
			oneOf(skuOptionValueForRed).getSkuOption();
			will(returnValue(skuOption));
			
			oneOf(skuOptionValueForBlue).getSkuOption();
			will(returnValue(skuOption));
			
			oneOf(skuOptionValueForRed).getOptionValueKey();
			will(returnValue(OPTION_VALUE_KEY_RED));
			
			oneOf(skuOptionValueForBlue).getOptionValueKey();
			will(returnValue(OPTION_VALUE_KEY_BLUE));
		} });
		
		skuOptionValueFilter.initialize(SEO_URL_FOR_RED_BLUE);
		
		assertEquals("the seo ", SEO_URL_FOR_RED_BLUE, skuOptionValueFilter.getSeoId());
	}
	
	/**
	 * Test initialize sku option value filter with sku option values from different sku options.
	 * An exception is expected here
	 */
	@Test(expected = EpCatalogViewRequestBindException.class)
	public void testInitializeForMultipleDifferentSkuOption() {
		SkuOptionValueFilter skuOptionValueFilter = getSkuOptionValueFilterWithMockService();
		
		final SkuOptionValue skuOptionValueForRed = context.mock(SkuOptionValue.class, "Red");
		final SkuOptionValue skuOptionValueForBlue = context.mock(SkuOptionValue.class, "Blue");
		final SkuOption skuOption1 = context.mock(SkuOption.class, "sku option 1");
		final SkuOption skuOption2 = context.mock(SkuOption.class, "sku option 2");
		
		context.checking(new Expectations() { {
			oneOf(mockSkuOptionService).findOptionValueByKey(OPTION_VALUE_KEY_RED);
			will(returnValue(skuOptionValueForRed));
			
			oneOf(mockSkuOptionService).findOptionValueByKey(OPTION_VALUE_KEY_BLUE);
			will(returnValue(skuOptionValueForBlue));
			
			oneOf(skuOptionValueForRed).getSkuOption();
			will(returnValue(skuOption1));
			
			oneOf(skuOptionValueForBlue).getSkuOption();
			will(returnValue(skuOption2));
		} });
		
		skuOptionValueFilter.initialize(SEO_URL_FOR_RED_BLUE);
		
		fail("should get exception");
	}
	
	/**
	 * Test get seo id for single sku option value.
	 */
	@Test
	public void testGetSeoIdForSingle() {
		SkuOptionValueFilter skuOptionValueFilter = new SkuOptionValueFilterImpl();
		
		SkuOptionValue skuOptionValue = new SkuOptionValueImpl();
		skuOptionValue.setOptionValueKey(OPTION_VALUE_KEY_RED);
		Set<SkuOptionValue> skuOptionValueSet = new HashSet<SkuOptionValue>();
		skuOptionValueSet.add(skuOptionValue);
		
		skuOptionValueFilter.initialize(getPropertiesMapForInitial(skuOptionValueSet));
		
		assertEquals("seo url is not correct", SEO_URL_FOR_RED, skuOptionValueFilter.getSeoId());
	}
	
	/**
	 * Test get seo id for multiple sku option values.
	 */
	@Test
	public void testGetSeoIdForMultiple() {
		SkuOptionValueFilter skuOptionValueFilter = new SkuOptionValueFilterImpl();
		
		final SkuOption skuOption = context.mock(SkuOption.class);
		
		
		Set<SkuOptionValue> skuOptionValueSet = new LinkedHashSet<SkuOptionValue>();
		final SkuOptionValue skuOptionValue1 = new SkuOptionValueImpl();
		skuOptionValue1.setOptionValueKey(OPTION_VALUE_KEY_RED);
		skuOptionValueSet.add(skuOptionValue1);
		
		final SkuOptionValue skuOptionValue2 = new SkuOptionValueImpl();
		skuOptionValue2.setOptionValueKey(OPTION_VALUE_KEY_BLUE);
		skuOptionValueSet.add(skuOptionValue2);
		
		context.checking(new Expectations() { {
			oneOf(skuOption).getOptionValue(OPTION_VALUE_KEY_RED);
			will(returnValue(skuOptionValue1));
			
			allowing(skuOption).getOptionValue(OPTION_VALUE_KEY_BLUE);
			will(returnValue(skuOptionValue2));
		} });
		
		skuOptionValue1.setSkuOption(skuOption);
		skuOptionValue2.setSkuOption(skuOption);
		
		skuOptionValueFilter.initialize(getPropertiesMapForInitial(skuOptionValueSet));
		
		assertEquals("seo url is not correct", SEO_URL_FOR_RED_BLUE, skuOptionValueFilter.getSeoId());
	}

	private Map<String, Object> getPropertiesMapForInitial(
			final Set<SkuOptionValue> skuOptionValueSet) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(SkuOptionValueFilter.SKU_OPTION_VALUES_PROPERTY_KEY, skuOptionValueSet);
		return properties;
	}
	
	/**
	 * Test get display name method.
	 */
	@Test
	public void testDisplayName() {
		SkuOptionValueFilter skuOptionValueFilter = new SkuOptionValueFilterImpl();
		
		final SkuOption skuOption = context.mock(SkuOption.class);
		
		
		final SkuOptionValue skuOptionValue1 = new SkuOptionValueImpl() {
			private static final long serialVersionUID = -9016340191720168183L;

			@Override
			public String getDisplayName(final Locale locale, final boolean fallback) {
				return getOptionValueKey();
			}
		};
		skuOptionValue1.setOptionValueKey(OPTION_VALUE_KEY_RED);
		Set<SkuOptionValue> skuOptionValueSet = new LinkedHashSet<SkuOptionValue>();
		skuOptionValueSet.add(skuOptionValue1);
		
		final SkuOptionValue skuOptionValue2 = new SkuOptionValueImpl() {
			private static final long serialVersionUID = -8311131892300081200L;

			@Override
			public String getDisplayName(final Locale locale, final boolean fallback) {
				return getOptionValueKey();
			}
		};
		skuOptionValue2.setOptionValueKey(OPTION_VALUE_KEY_BLUE);
		skuOptionValueSet.add(skuOptionValue2);
		
		context.checking(new Expectations() { {
			oneOf(skuOption).getOptionValue(OPTION_VALUE_KEY_RED);
			will(returnValue(skuOptionValue1));
			
			oneOf(skuOption).getOptionValue(OPTION_VALUE_KEY_BLUE);
			will(returnValue(skuOptionValue2));
			
			oneOf(skuOption).getDisplayName(Locale.CANADA, true);
			will(returnValue("Color"));
		} });
		
		skuOptionValue1.setSkuOption(skuOption);
		skuOptionValue2.setSkuOption(skuOption);
		
		skuOptionValueFilter.initialize(getPropertiesMapForInitial(skuOptionValueSet));
		
		assertEquals("display name is wrong", "Color:Red,Blue", skuOptionValueFilter.getDisplayName(Locale.CANADA));
	}
	
	/**
	 * Test get seo name method.
	 */
	@Test
	public void testGetSeoName() {
		final String seoName = "Color:Red,Blue";
		final Utility utility = context.mock(Utility.class);
		
		SkuOptionValueFilter skuOptionValueFilter = new SkuOptionValueFilterImpl() {
			private static final long serialVersionUID = 7590648970546450969L;

			@Override
			public Utility getUtility() {
				return utility;
			}
		};
		
		final SkuOption skuOption = context.mock(SkuOption.class);
		
		
		final SkuOptionValue skuOptionValue1 = new SkuOptionValueImpl() {
			private static final long serialVersionUID = -3977023265709590707L;

			@Override
			public String getDisplayName(final Locale locale, final boolean fallback) {
				return getOptionValueKey();
			}
		};
		skuOptionValue1.setOptionValueKey(OPTION_VALUE_KEY_RED);
		Set<SkuOptionValue> skuOptionValueSet = new LinkedHashSet<SkuOptionValue>();
		skuOptionValueSet.add(skuOptionValue1);
		
		final SkuOptionValue skuOptionValue2 = new SkuOptionValueImpl() {
			private static final long serialVersionUID = -5404555888339089589L;

			@Override
			public String getDisplayName(final Locale locale, final boolean fallback) {
				return getOptionValueKey();
			}
		};
		skuOptionValue2.setOptionValueKey(OPTION_VALUE_KEY_BLUE);
		skuOptionValueSet.add(skuOptionValue2);
		
		context.checking(new Expectations() { {
			oneOf(skuOption).getOptionValue(OPTION_VALUE_KEY_RED);
			will(returnValue(skuOptionValue1));
			
			oneOf(skuOption).getOptionValue(OPTION_VALUE_KEY_BLUE);
			will(returnValue(skuOptionValue2));
			
			oneOf(skuOption).getDisplayName(Locale.CANADA, true);
			will(returnValue("Color"));
			
			oneOf(utility).escapeName2UrlFriendly(seoName, Locale.CANADA);
			will(returnValue(seoName));
		} });
		
		skuOptionValue1.setSkuOption(skuOption);
		skuOptionValue2.setSkuOption(skuOption);
		
		skuOptionValueFilter.initialize(getPropertiesMapForInitial(skuOptionValueSet));
		
		assertEquals("seo name is wrong", seoName, skuOptionValueFilter.getSeoName(Locale.CANADA));
	}

	private SkuOptionValueFilter getSkuOptionValueFilterWithMockService() {
		SkuOptionValueFilter skuOptionValueFilter = new SkuOptionValueFilterImpl() {
			private static final long serialVersionUID = -7212028579035012801L;

			@Override
			protected SkuOptionService getSkuOptionService() {
				return mockSkuOptionService;
			}
		};
		return skuOptionValueFilter;
	}
}
