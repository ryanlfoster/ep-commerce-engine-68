/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping.impl;

import static com.elasticpath.domain.shipping.impl.ShippingCostTestDataFactory.aCostCalculationParam;
import static com.elasticpath.domain.shipping.impl.ShippingCostTestDataFactory.someCalculationParams;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.exception.SCCMCurrencyMissingException;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.BrandLocalizedPropertyValueImpl;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;

/** Test cases for <code>ShippingServiceLevelImpl</code>. */
public class ShippingServiceLevelImplTest {

	private static final double TWELVE = 12.00;

	private static final double FIFTEEN = 15.0;

	private static final double THREE = 3.0;

	private static final double ELEVEN = 11.00;

	private ShippingServiceLevelImpl shippingServiceLevelImpl;
	private ProductSku mockProductSku;
	
	private static final Locale DEFAULT_LOCALE = Locale.US;
	private static final String DISPLAYNAME_DEFAULT_LOCALE = "defaultDisplayName";
	private static final Locale OTHER_LOCALE = Locale.GERMANY;
	private static final String DISPLAYNAME_OTHER_LOCALE = "otherDisplayName";
	
	private static final Currency CURRENCY_USD = Currency.getInstance(Locale.US);
	private static final Currency CURRENCY_FRANCE = Currency.getInstance(Locale.FRANCE);
	private static final Currency CURRENCY_ENGLISH = Currency.getInstance(Locale.UK);

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Prepare for each test.
	 * 
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		this.shippingServiceLevelImpl = new ShippingServiceLevelImpl();
		mockProductSku = context.mock(ProductSku.class);
		context.checking(new Expectations() { {
			allowing(mockProductSku).isShippable(); will(returnValue(true));
		} });
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl.getShippingRegion()'.
	 */
	@Test
	public void testGetSetShippingRegion() {
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		this.shippingServiceLevelImpl.setShippingRegion(shippingRegion);
		assertEquals(shippingRegion, this.shippingServiceLevelImpl.getShippingRegion());
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl.getStore()'.
	 */
	@Test
	public void testGetSetStore() {
		final Store store = new StoreImpl();
		store.setCode("some code");
		this.shippingServiceLevelImpl.setStore(store);
		assertEquals(store, this.shippingServiceLevelImpl.getStore());
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl.getShippingCostCalculationMethod()'.
	 */
	@Test
	public void testGetSetShippingCostCalculationMethod() {
		final ShippingCostCalculationMethod shippingCostCalculationMethod = new FixedBaseAndOrderTotalPercentageMethodImpl();
		this.shippingServiceLevelImpl.setShippingCostCalculationMethod(shippingCostCalculationMethod);
		assertEquals(shippingCostCalculationMethod, this.shippingServiceLevelImpl.getShippingCostCalculationMethod());
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl.getCarrier()'.
	 */
	@Test
	public void testGetSetCarrier() {
		final String testCarrier = "Fed Ex";
		this.shippingServiceLevelImpl.setCarrier(testCarrier);
		assertEquals(testCarrier, this.shippingServiceLevelImpl.getCarrier());
	}
	
	private LocalizedProperties createLocalizedPropertiesWithDisplayNameInDefaultLocale() {
		//give it a reference to the Utility class so that it can broaden the locale
		LocalizedProperties localizedProperties = new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = 7789997879611928448L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}

			@Override
			public Utility getUtility() {
				return new UtilityImpl();
			}
		};
		localizedProperties.setValue(ShippingServiceLevel.LOCALIZED_PROPERTY_NAME, DEFAULT_LOCALE, DISPLAYNAME_DEFAULT_LOCALE);
		return localizedProperties;
	}
	
	private LocalizedProperties createLocalizedPropertiesWithDisplayNameInDefaultLocaleAndOtherLocale() {
		LocalizedProperties localizedProperties = new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = 406419152977594590L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		};
		localizedProperties.setValue(ShippingServiceLevel.LOCALIZED_PROPERTY_NAME, DEFAULT_LOCALE, DISPLAYNAME_DEFAULT_LOCALE);
		localizedProperties.setValue(ShippingServiceLevel.LOCALIZED_PROPERTY_NAME, OTHER_LOCALE, DISPLAYNAME_OTHER_LOCALE);
		return localizedProperties;
	}
	
	private ShippingServiceLevelImpl createShippingServiceLevelImplWithDisplayNameDefaultLocaleOnly() {
		return createShippingServiceLevelImplForTesting(createLocalizedPropertiesWithDisplayNameInDefaultLocale());
	}
	
	private ShippingServiceLevelImpl createShippingServiceLevelImplWithDisplayNameInBothLocales() {
		return createShippingServiceLevelImplForTesting(createLocalizedPropertiesWithDisplayNameInDefaultLocaleAndOtherLocale());
	}
	
	private ShippingServiceLevelImpl createShippingServiceLevelImplForTesting(final LocalizedProperties localizedProperties) {
		return new ShippingServiceLevelImpl() {
			private static final long serialVersionUID = 6032980036419946630L;
			
			@Override
			public LocalizedProperties getLocalizedProperties() {
				return localizedProperties;
			}
			
			@Override
			public Locale getStoreDefaultLocale() {
				return DEFAULT_LOCALE;
			}
		};
	}
	
	/**
	 * Test that getDisplayName falls back if necessary, but not if it has been forbidden.
	 */
	@Test
	public void testGetDisplayNameFallsBackIfNecessaryButNotIfForbidden() {
		ShippingServiceLevelImpl shippingServiceLevelImpl = createShippingServiceLevelImplWithDisplayNameDefaultLocaleOnly();
		assertEquals("Should fall back if necessary", DISPLAYNAME_DEFAULT_LOCALE, shippingServiceLevelImpl.getDisplayName(OTHER_LOCALE, true));
		assertEquals("Should not fall back if it's forbidden", null, shippingServiceLevelImpl.getDisplayName(OTHER_LOCALE, false));
	}

	/**
	 * Test that getDisplayName doesn't fall back if a match is found
	 * for the requested locale.
	 */
	@Test
	public void testGetDisplayNameDoesNotFallBackIfNotNecessary() {
		ShippingServiceLevelImpl shippingServiceLevelImpl = createShippingServiceLevelImplWithDisplayNameInBothLocales();
		assertEquals("Should not fall back if not necessary", DISPLAYNAME_OTHER_LOCALE, shippingServiceLevelImpl.getDisplayName(OTHER_LOCALE, true));
	}
	
	/**
	 * Tests that when the calculateRegularPriceShippingCost with no currency set on the 
	 * fixed price cost calculation method an exception will be thrown.
	 */
	@Test(expected = SCCMCurrencyMissingException.class)
	public void testCalculateRegularPriceShippingCostWithNullCurrencyThrowsException() {
		
		ShippingServiceLevelImpl shippingServiceLevelImpl = createShippingServiceLevelImplWithDisplayNameDefaultLocaleOnly();
		ShippingCostCalculationMethod shippingCostCalculationMethod = new FixedPriceMethodImpl();
		
		//no currency set
		ShippingCostCalculationParameter param1 
			= aCostCalculationParam(ShippingCostCalculationParametersEnum.FIXED_PRICE, new BigDecimal(ELEVEN));
		
		
		Set <ShippingCostCalculationParameter> params = someCalculationParams(param1);
		
		shippingCostCalculationMethod.setParameters(params);
		
		shippingServiceLevelImpl.setShippingCostCalculationMethod(shippingCostCalculationMethod);
		
		shippingServiceLevelImpl.calculateRegularPriceShippingCost(aShoppingItemList(), CURRENCY_USD);
	}
	
	/**
	 * Tesst that calculateRegularPriceShippingCost with no matching currency set on the 
	 * fixed price cost calculation method throws an exception will be thrown.
	 */
	@Test(expected = SCCMCurrencyMissingException.class)
	public void testCalculateRegularPriceShippingCostWithNoMatchingCurrencyThrowsException() {
		
		ShippingServiceLevelImpl shippingServiceLevelImpl = createShippingServiceLevelImplWithDisplayNameDefaultLocaleOnly();
		ShippingCostCalculationMethod shippingCostCalculationMethod = new FixedPriceMethodImpl();
		
		Set <ShippingCostCalculationParameter> params 
			= someCalculationParams(
					aCostCalculationParam(ShippingCostCalculationParametersEnum.FIXED_PRICE, 
							new BigDecimal(ELEVEN), CURRENCY_ENGLISH), 
					aCostCalculationParam(ShippingCostCalculationParametersEnum.FIXED_PRICE, 
							new BigDecimal(ELEVEN), CURRENCY_FRANCE));
		
		shippingCostCalculationMethod.setParameters(params);
		
		shippingServiceLevelImpl.setShippingCostCalculationMethod(shippingCostCalculationMethod);
		
		shippingServiceLevelImpl.calculateRegularPriceShippingCost(aShoppingItemList(), CURRENCY_USD);		
	}
	
	/**
	 * Test that calculateRegularPriceShippingCost with a matching currency set on the 
	 * fixed price cost calculation method returns the correct value.
	 */
	@Test
	public void testCalculateRegularPriceShippingCostWithMatchingCurrency() {
		
		ShippingServiceLevelImpl shippingServiceLevelImpl = createShippingServiceLevelImplWithDisplayNameDefaultLocaleOnly();
		ShippingCostCalculationMethod shippingCostCalculationMethod = new FixedPriceMethodImpl();
		
		BigDecimal expectedShippingCost = new BigDecimal("15.00");
		
		Set <ShippingCostCalculationParameter> params = 
			someCalculationParams(
					aCostCalculationParam(ShippingCostCalculationParametersEnum.FIXED_PRICE, 
							new BigDecimal(ELEVEN), CURRENCY_ENGLISH), 
					aCostCalculationParam(ShippingCostCalculationParametersEnum.FIXED_PRICE, 
							expectedShippingCost, CURRENCY_FRANCE));
		
		shippingCostCalculationMethod.setParameters(params);
		
		shippingServiceLevelImpl.setShippingCostCalculationMethod(shippingCostCalculationMethod);
		
		Money actualShippingCostMoney 
			= shippingServiceLevelImpl.calculateRegularPriceShippingCost(aShoppingItemList(), CURRENCY_FRANCE);
		
		assertEquals("Expected value does not equal returned value from calculateRegularPriceShippingCost", 
				expectedShippingCost, actualShippingCostMoney.getAmount());
	}
	
	
	/**
	 */
	@Test
	public void testCalculateShippingCostWithDiscountApplied() {
		
		ShippingServiceLevelImpl shippingServiceLevelImpl = createShippingServiceLevelImplWithDisplayNameDefaultLocaleOnly();
		ShippingCostCalculationMethod shippingCostCalculationMethod = new FixedPriceMethodImpl();
		
		//set a discount of 3 (euros)
		shippingServiceLevelImpl.setShippingDiscount(MoneyFactory.createMoney(new BigDecimal(THREE), CURRENCY_FRANCE));
		
		//set the initial item price to 15 
		Set <ShippingCostCalculationParameter> params = 
			someCalculationParams(
					aCostCalculationParam(ShippingCostCalculationParametersEnum.FIXED_PRICE, 
							new BigDecimal(FIFTEEN), CURRENCY_FRANCE));
		
		shippingCostCalculationMethod.setParameters(params);
		
		shippingServiceLevelImpl.setShippingCostCalculationMethod(shippingCostCalculationMethod);
		
		Money actualShippingCostMoney 
			= shippingServiceLevelImpl.calculateShippingCost(aShoppingItemList(), CURRENCY_FRANCE);
		
		//price was 15 with discount of 3 applied so expect 12 ...
		Money expected = MoneyFactory.createMoney(new BigDecimal(TWELVE), CURRENCY_FRANCE);
		
		assertEquals("Expected value does not equal returned value from calculateShippingCost (discount applied)", 
				expected, actualShippingCostMoney);
	}

	/**
	 */
	@Test
	public void testIsApplicableReturnsFalseWhenInactive() {
		ShippingServiceLevel shippingServiceLevel = new ShippingServiceLevelImpl();
		shippingServiceLevel.setEnabled(false);

		String anyStoreCode = "doesn't matter store code";
		Address anyAddress = context.mock(Address.class, "doesn't matter address");
		assertFalse(shippingServiceLevel.isApplicable(anyStoreCode, anyAddress));
	}

	/**
	 */
	@Test
	public void testIsApplicableReturnsFalseWhenStoreCodeMismatch() {
		ShippingServiceLevel shippingServiceLevel = new ShippingServiceLevelImpl();
		shippingServiceLevel.setEnabled(true);
		final Store store = createStore("test store code");
		shippingServiceLevel.setStore(store);

		Address anyAddress = context.mock(Address.class, "doesn't matter address");
		assertFalse(shippingServiceLevel.isApplicable("non-matching store code", anyAddress));
	}

	/**
	 */
	@Test
	public void testIsApplicableReturnsFalseWhenAddressNotInRegion() {
		ShippingServiceLevel shippingServiceLevel = new ShippingServiceLevelImpl();
		shippingServiceLevel.setEnabled(true);

		final String storeCode = "test store code";
		final Store store = createStore(storeCode);
		shippingServiceLevel.setStore(store);

		final ShippingRegion shippingRegion = createShippingRegion(false);
		shippingServiceLevel.setShippingRegion(shippingRegion);

		Address address = createAddress("US", "WA");
		assertFalse(shippingServiceLevel.isApplicable(storeCode, address));
	}

	/**
	 */
	@Test
	public void testIsApplicableReturnsTrueWhenEnabledMatchingStoreAndInRegion() {
		ShippingServiceLevel shippingServiceLevel = new ShippingServiceLevelImpl();
		shippingServiceLevel.setEnabled(true);

		final String storeCode = "test store code";
		final Store store = createStore(storeCode);
		shippingServiceLevel.setStore(store);

		final ShippingRegion shippingRegion = createShippingRegion(true);
		shippingServiceLevel.setShippingRegion(shippingRegion);

		Address address = createAddress("US", "WA");
		assertTrue(shippingServiceLevel.isApplicable(storeCode, address));
	}

	private Store createStore(final String storeCode) {
		final Store store = context.mock(Store.class);
		context.checking(new Expectations() {
			{
				oneOf(store).getCode();
				will(returnValue(storeCode));
			}
		});
		return store;
	}

	private Address createAddress(final String country, final String subCountry) {
		final Address address = context.mock(Address.class);
		context.checking(new Expectations() {
			{
				allowing(address).getCountry();
				will(returnValue(country));

				allowing(address).getSubCountry();
				will(returnValue(subCountry));
			}
		});
		return address;
	}

	private ShippingRegion createShippingRegion(final boolean inShippingRegion) {
		final ShippingRegion shippingRegion = context.mock(ShippingRegion.class);
		context.checking(new Expectations() {
			{
				oneOf(shippingRegion).isInShippingRegion(with(any(Address.class)));
				will(returnValue(inShippingRegion));
			}
		});
		return shippingRegion;
	}

	private List <ShoppingItem> aShoppingItemList() {
		
		List <ShoppingItem> items = new ArrayList<ShoppingItem>();
		
		items.add(aShoppingItemWithPriceAndCurrency(2.0, 1.0, CURRENCY_USD));
		
		return items;
	}
	
	/**
	 * Creates a Price with a list and sale price for the given currency.
	 *
	 * @param listPrice listPrice
	 * @param salePrice salePrice
	 * @param currency currency
	 * @return the created Price
	 */
	private ShoppingItem aShoppingItemWithPriceAndCurrency(final double listPrice, final double salePrice, final Currency currency) {
		
		Price price = new PriceImpl();
		price.setCurrency(currency);
		price.setListPrice(MoneyFactory.createMoney(new BigDecimal(listPrice), currency));
		price.setSalePrice(MoneyFactory.createMoney(new BigDecimal(salePrice), currency));
		
		ShoppingItem shoppingItem = new ShoppingItemImpl();
		shoppingItem.setPrice(1, price);
		shoppingItem.setProductSku(mockProductSku);
		return shoppingItem;
	}
	
}
