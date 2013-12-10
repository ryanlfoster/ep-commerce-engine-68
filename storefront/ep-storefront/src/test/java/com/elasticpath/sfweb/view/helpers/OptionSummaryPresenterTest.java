/**
 * 
 */
package com.elasticpath.sfweb.view.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.impl.AttributeRangeFilterImpl;
import com.elasticpath.domain.catalogview.impl.AttributeValueFilterImpl;
import com.elasticpath.domain.catalogview.impl.PriceFilterImpl;

/**
 *
 */
public class OptionSummaryPresenterTest {

	private OptionSummaryPresenter optionSummaryPresenter;
	private Locale locale;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final Currency currency = Currency.getInstance(Locale.US);
	
	/**
	 * Set up. 
	 */
	@Before
	public void setUp()  {
		optionSummaryPresenter = new OptionSummaryPresenter();
		locale = Locale.CANADA;
	}
	
	/**
	 * Will only return one brand in option summary string.
	 */
	@Test
	public void testNoBrandsInOptionSummary() {
		List<Filter< ? >> brandFilters = new ArrayList<Filter< ? >>();
				
		assertEquals("", optionSummaryPresenter.buildBrandOptionSummary(brandFilters, locale));
	}
	
	/**
	 * Will only return one brand in option summary string.
	 */
	@Test
	public void testOneBrandInOptionSummary() {
		List<Filter< ? >> brandFilters = new ArrayList<Filter< ? >>();
		final BrandFilter brandFilter = context.mock(BrandFilter.class);
		brandFilters.add(brandFilter);
		final Set<Brand> brands = new HashSet<Brand>();
		BrandImplTest brand1 = new BrandImplTest();
		brand1.setCode("BRAND1");
		brand1.setDisplayName("Canon");
		
		brands.add(brand1);
		
		context.checking(new Expectations() { {															
			allowing(brandFilter).getBrands();
			will(returnValue(brands));
			
			}
		});
		
		assertEquals("Canon,", optionSummaryPresenter.buildBrandOptionSummary(brandFilters, locale));
	}
	/**
	 * Two brand filter options will give us the summary string with the two brands.
	 */
	@Test
	public void testTwoBrandsInOptionSummary() {
		List<Filter< ? >> brandFilters = new ArrayList<Filter< ? >>();
		final BrandFilter brandFilter = context.mock(BrandFilter.class);
		brandFilters.add(brandFilter);
		final Set<Brand> brands = new LinkedHashSet<Brand>();
		BrandImplTest brand1 = new BrandImplTest();
		brand1.setCode("BRAND1");
		brand1.setDisplayName("Canon");
		BrandImplTest brand2 = new BrandImplTest();
		brand2.setCode("BRAND2");
		brand2.setDisplayName("Samsung");
		
		brands.add(brand1);
		brands.add(brand2);
				
		context.checking(new Expectations() { {															
			allowing(brandFilter).getBrands();
			will(returnValue(brands));
			
			}
		});
		
		assertEquals("Canon / Samsung,", optionSummaryPresenter.buildBrandOptionSummary(brandFilters, locale));
	}
	
	/**
	 * Testing a price range from 125 to 250 will show up in the price summary string.
	 */
	@Test
	public void testPriceInOptionSummaryIntegerValues() {
		List<Filter< ? >> priceFilterOptions = new ArrayList<Filter< ? >>();
		PriceFilterImpl priceFilter = new PriceFilterImpl();
		priceFilter.setLowerValue(new BigDecimal("125"));
		priceFilter.setUpperValue(new BigDecimal("250"));
		priceFilterOptions.add(priceFilter);
		
		assertEquals("USD125-USD250,", optionSummaryPresenter.buildPriceOptionSummary(priceFilterOptions, currency));
	}
	
	/**
	 * Testing a price range from 125 to 250 in CAD will show up in the price summary string.
	 */
	@Test
	public void testPriceInOptionSummaryIntegerValuesInCAD() {
		List<Filter< ? >> priceFilterOptions = new ArrayList<Filter< ? >>();
		PriceFilterImpl priceFilter = new PriceFilterImpl();
		priceFilter.setLowerValue(new BigDecimal("125"));
		priceFilter.setUpperValue(new BigDecimal("250"));
		priceFilterOptions.add(priceFilter);
		
		assertEquals("CAD125-CAD250,", optionSummaryPresenter.buildPriceOptionSummary(
				priceFilterOptions, Currency.getInstance(Locale.CANADA)));
	}
	
	/**
	 * Testing a price range from 125.50 to 250.99 will show up in the price summary string.
	 */
	@Test
	public void testPriceInOptionSummaryDecimalValues() {
		List<Filter< ? >> priceFilterOptions = new ArrayList<Filter< ? >>();
		PriceFilterImpl priceFilter = new PriceFilterImpl();
		priceFilter.setLowerValue(new BigDecimal("125.50"));
		priceFilter.setUpperValue(new BigDecimal("250.99"));
		priceFilterOptions.add(priceFilter);
		
		assertEquals("USD125.50-USD250.99,", optionSummaryPresenter.buildPriceOptionSummary(priceFilterOptions, currency));
	}
	
	/**
	 * No attribute filters in the list of filters will return an empty map.
	 */
	@Test
	public void testNoAttributeFilters() {
		List<Filter< ? >> attributeFilters = new ArrayList<Filter< ? >>();		
		assertTrue(optionSummaryPresenter.buildAttributeKeyValueMap(attributeFilters, locale).isEmpty());
	}
	
	/**
	 * One attribute value filter in the filters list will return map with 1 item.
	 */
	@Test
	public void testOneAttributeValueFilter() {
		String attributeKey = "A00001";
		String displayName = "CCD";
		
		List<Filter< ? >> attributeFilters = new ArrayList<Filter< ? >>();
		AttributeValueFilterImplTest attributeValueFilter = new AttributeValueFilterImplTest();
		attributeValueFilter.setAttributeKey(attributeKey);
		attributeValueFilter.setDisplayName(displayName);
		attributeFilters.add(attributeValueFilter);
		
		assertEquals(1, optionSummaryPresenter.buildAttributeKeyValueMap(attributeFilters, locale).size());
		assertEquals(displayName, optionSummaryPresenter.buildAttributeKeyValueMap(attributeFilters, locale).get(attributeKey));
	}

	
	/**
	 * One attribute range filter in the filters list will return map with 1 item.
	 */
	@Test
	public void testOneAttributeRangeFilter() {
		String attributeKey = "A00001";
		String displayName = "1-2";
		
		List<Filter< ? >> attributeFilters = new ArrayList<Filter< ? >>();
		AttributeRangeFilterImplTest attributeRangeFilter = new AttributeRangeFilterImplTest();
		attributeRangeFilter.setAttributeKey(attributeKey);
		attributeRangeFilter.setDisplayName(displayName);
		attributeFilters.add(attributeRangeFilter);
		
		assertEquals(1, optionSummaryPresenter.buildAttributeKeyValueMap(attributeFilters, locale).size());
		assertEquals(displayName, optionSummaryPresenter.buildAttributeKeyValueMap(attributeFilters, locale).get(attributeKey));

	}
	
	/**
	 * Test that multiple attribute filters (mixed AttributeRangeFilter and AttributeValueFilter).
	 */
	@Test
	public void testMultipleAttributeFilters() {
		String attributeValueKey = "A00001";
		String displayValueName = "CCD";
		
		String attributeRangeKey = "A00002";
		String displayRangeName = "1-2";
		
		List<Filter< ? >> attributeFilters = new ArrayList<Filter< ? >>();
		
		AttributeValueFilterImplTest attributeValueFilter = new AttributeValueFilterImplTest();
		attributeValueFilter.setAttributeKey(attributeValueKey);
		attributeValueFilter.setDisplayName(displayValueName);
		attributeValueFilter.setId("atA0001");
		attributeFilters.add(attributeValueFilter);
		
		AttributeRangeFilterImplTest attributeRangeFilter = new AttributeRangeFilterImplTest();
		attributeRangeFilter.setAttributeKey(attributeRangeKey);
		attributeRangeFilter.setDisplayName(displayRangeName);
		attributeRangeFilter.setId("arA00002");
		attributeFilters.add(attributeRangeFilter);
		
		assertEquals(2, optionSummaryPresenter.buildAttributeKeyValueMap(attributeFilters, locale).size());
		assertEquals(displayRangeName, optionSummaryPresenter.buildAttributeKeyValueMap(attributeFilters, locale).get(attributeRangeKey));
		assertEquals(displayValueName, optionSummaryPresenter.buildAttributeKeyValueMap(attributeFilters, locale).get(attributeValueKey));
	}

	
	/**
	 * Private BrandImpl extension class for testing.
	 */
	private class BrandImplTest extends BrandImpl {
		private static final long serialVersionUID = -4415448932199828220L;
		private String displayName;
		
		@Override
		public String getDisplayName(final Locale locale, final boolean fallback) {
			return displayName;
		}
 
		public void setDisplayName(final String displayName) {
			this.displayName = displayName;
		}
	}
	
	/**
	 * Private AttributeValueFilterImpl class for testing.
	 */
	private class AttributeValueFilterImplTest extends AttributeValueFilterImpl {
		private static final long serialVersionUID = 1500000598536887161L;
		private String attributeKey;
		
		@Override
		public void setAttributeKey(final String attributeKey) {
			this.attributeKey = attributeKey;
		}
		
		@Override
		public String getAttributeKey() {
			return this.attributeKey;
		}
	}
	
	/**
	 * Private AttributeRangeFilterImpl class for testing.
	 */
	private class AttributeRangeFilterImplTest extends AttributeRangeFilterImpl {
		private static final long serialVersionUID = 1538920784950362108L;
		private String displayName;
		private String attributeKey;
		
		public void setDisplayName(final String displayName) {
			this.displayName = displayName;
		}
		
		@Override
		public String getDisplayName(final Locale locale) {
			return displayName;
		}		
		
		@Override
		public void setAttributeKey(final String attributeKey) {
			this.attributeKey = attributeKey;
		}
		
		@Override
		public String getAttributeKey() {
			return this.attributeKey;
		}
	}

}
