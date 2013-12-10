package com.elasticpath.sfweb.ajax.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.util.impl.StoreThemeMessageSource;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.SimplePrice;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.MoneyFormatter;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.sfweb.ajax.bean.AutocompletionSearchResult;

/**
 * Test case for {@link AutocompletionSearchResultBuilder}.
 */
public class AutocompletionSearchResultBuilderTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final Locale locale = Locale.getDefault();

	private final SeoUrlBuilder seoUrlBuilder = context.mock(SeoUrlBuilder.class);

	private static final String BASE_URL = "TEST_BASE_URL";

	private static final String TEST_SEO_URL = "TEST_SEO_URL";

	private static final String TEST_CODE = "TEST_CODE";

	private static final String TEST_PRICE = "$1.00";

	private static final long TEST_UIDPK = 123;

	private static final String TEST_IMAGE = "TEST_IMAGE";

	private static final String LDF_DISPLAY_NAME = "LDF_DISPLAY_NAME";
	
	private static final String LDF_DESCRIPTION = "LDF_DESCRIPTION";
	
	private static final String TEST_BIG_INT_STRING = "1.00";
	
	private final Price testPrice = context.mock(Price.class); 
	
	private final StoreProduct product = context.mock(StoreProduct.class);
	
	private final Map<String, Price> prices = new HashMap<String, Price>();

	private Money money;
	
	private final List<Product> products = new ArrayList<Product>();
	
	private final LocaleDependantFields localeDependantFields = context.mock(LocaleDependantFields.class);

	private boolean seoEnabled;
	
	private boolean showThumbnail;
	
	private final StoreThemeMessageSource messageSource = new StoreThemeMessageSource();
	private final PricingScheme pricingScheme = context.mock(PricingScheme.class);
	private final PriceSchedule lowestPriceSchedule = context.mock(PriceSchedule.class);
	private final SimplePrice lowestPrice = context.mock(SimplePrice.class);
	private MoneyFormatter moneyFormatter;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		money = MoneyFactory.createMoney(new BigDecimal(TEST_BIG_INT_STRING), Currency.getInstance("CAD"));
		moneyFormatter = new StandardMoneyFormatter();

		context.checking(new Expectations() {
			{
				allowing(localeDependantFields).getDisplayName(); will(returnValue(LDF_DISPLAY_NAME));
				allowing(localeDependantFields).getDescription(); will(returnValue(LDF_DESCRIPTION));
				allowing(product).getLocaleDependantFields(with(locale)); will(returnValue(localeDependantFields));
				allowing(product).getCode(); will(returnValue(TEST_CODE));
				allowing(product).getImage(); will(returnValue(TEST_IMAGE));
				allowing(product).getUidPk(); will(returnValue(TEST_UIDPK));
				allowing(product).getGuid(); will(returnValue(TEST_CODE));
				allowing(testPrice).getLowestPrice(); will(returnValue(money));
				allowing(testPrice).getPricingScheme(); will(returnValue(pricingScheme));
				allowing(pricingScheme).getScheduleForLowestPrice(); will(returnValue(lowestPriceSchedule));				
				allowing(pricingScheme).getSimplePriceForSchedule(with(lowestPriceSchedule)); will(returnValue(lowestPrice));
				allowing(lowestPrice).getLowestPrice(); will(returnValue(money));
				allowing(lowestPriceSchedule).getType(); will(returnValue(PriceScheduleType.PURCHASE_TIME));
				allowing(seoUrlBuilder).productSeoUrl(product, locale); will(returnValue(TEST_SEO_URL));
			}
		});

		prices.put(TEST_CODE, testPrice);
		products.add(product);
	}

	/**
	 * Tests basic properties of built by AutocompletionSearchResultBuilder.
	 */
	@Test
	public void testBuildMainProperties() {
		final int maxLength = 40;
		List<AutocompletionSearchResult> resultList = 
			AutocompletionSearchResultBuilder.build(
					products, 
					prices, 
					new AutocompletionSearchResultConfiguration(
							showThumbnail, 
							seoEnabled,
							maxLength, 
							maxLength), 
					locale, seoUrlBuilder, BASE_URL, messageSource, moneyFormatter);
		assertEquals(1, resultList.size());
		AutocompletionSearchResult autocompletionSearchResult = resultList.get(0);
		assertEquals(TEST_PRICE, autocompletionSearchResult.getPrice());
		assertEquals(TEST_CODE, autocompletionSearchResult.getGuid());
		assertEquals(LDF_DESCRIPTION, autocompletionSearchResult.getDescription());
		assertEquals(LDF_DISPLAY_NAME, autocompletionSearchResult.getName());
	}

	/**
	 * Tests url and image properties of built by AutocompletionSearchResultBuilder.
	 */
	@Test
	public void testBuildSeoEnabledFalseImageNotNull() {
		seoEnabled = Boolean.FALSE;
		String expectedUrl = BASE_URL + "/product-view.ep?pID=" + TEST_CODE;
		showThumbnail = Boolean.TRUE;
		final int maxLength = 40;
		
		List<AutocompletionSearchResult> resultList = 
			AutocompletionSearchResultBuilder.build(
					products, 
					prices, 
					new AutocompletionSearchResultConfiguration(
							showThumbnail, 
							seoEnabled,
							maxLength, 
							maxLength), 
					locale, seoUrlBuilder, BASE_URL, messageSource, moneyFormatter);
		assertEquals(1, resultList.size());
		AutocompletionSearchResult autocompletionSearchResult = resultList.get(0);
		assertEquals(expectedUrl, autocompletionSearchResult.getUrl());
		assertNotNull(autocompletionSearchResult.getImage());
	}

	/**
	 * Tests url and image properties of built by AutocompletionSearchResultBuilder.
	 */	
	@Test
	public void testBuildSeoEnabledTrueImageNull() {
		seoEnabled = Boolean.TRUE;
		String expectedUrl = BASE_URL + "/" + TEST_SEO_URL;
		showThumbnail = Boolean.FALSE;
		final int maxLength = 40;
		
		List<AutocompletionSearchResult> resultList = 
			AutocompletionSearchResultBuilder.build(
					products, 
					prices, 
					new AutocompletionSearchResultConfiguration(
							showThumbnail, 
							seoEnabled,
							maxLength, 
							maxLength), 
					locale, seoUrlBuilder, BASE_URL, messageSource, moneyFormatter);
		assertEquals(1, resultList.size());
		AutocompletionSearchResult autocompletionSearchResult = resultList.get(0);
		assertEquals(expectedUrl, autocompletionSearchResult.getUrl());
		assertEquals(StringUtils.EMPTY, autocompletionSearchResult.getImage());
	}
	
	
	
	
}
