/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shoppingcart.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.impl.CustomerProfileValueImpl;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.CatalogLocaleImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerAuthenticationImpl;
import com.elasticpath.domain.customer.impl.CustomerProfileImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.BrandLocalizedPropertyValueImpl;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.domain.tax.TaxValue;
import com.elasticpath.domain.tax.impl.TaxCategoryImpl;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.domain.tax.impl.TaxJurisdictionImpl;
import com.elasticpath.domain.tax.impl.TaxRegionImpl;
import com.elasticpath.domain.tax.impl.TaxValueImpl;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.impl.InventoryDtoImpl;
import com.elasticpath.plugin.payment.exceptions.PaymentProcessingException;
import com.elasticpath.service.catalog.impl.GiftCertificateServiceImpl;
import com.elasticpath.service.catalog.impl.ProductInventoryManagementServiceImpl;
import com.elasticpath.service.order.impl.AllocationServiceImpl;
import com.elasticpath.service.rules.EpRuleEngine;
import com.elasticpath.service.shoppingcart.impl.OrderSkuFactoryImpl;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;
import com.elasticpath.service.tax.impl.DefaultTaxCalculationServiceImpl;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;
import com.elasticpath.service.tax.impl.TaxJurisdictionServiceImpl;
import com.elasticpath.settings.impl.SettingsServiceImpl;
import com.elasticpath.test.factory.TestCustomerSessionFactory;
import com.elasticpath.test.factory.TestShopperFactory;
import com.elasticpath.test.jmock.AbstractCatalogDataTestCase;

/**
 * Test <code>ShoppingCartImpl</code>.
 */
@SuppressWarnings({ "PMD.NonStaticInitializer", "PMD.ExcessiveClassLength", "PMD.ExcessiveImports", "PMD.TooManyMethods",
		"PMD.CouplingBetweenObjects", "PMD.AvoidDuplicateLiterals" })
public class ShoppingCartImplTest extends AbstractCatalogDataTestCase {

	private static final String CODE = "Code ";
	private static final int NUM_UNIQUE_RULES = 3;
	private static final Locale DEFAULT_LOCALE = Locale.CANADA;
	private static final String EP_DOMAIN_EXCEPTION_EXPECTED = "EpDomainException expected.";
	private static final String PRICE_1 = "5";
	private static final String PRICE_2 = "10";
	private static final int INVALID_UID_2 = 987654;
	private static final int INVALID_UID_1 = -23;
	private static final int QTY_3 = 3;
	private static final int QTY_5 = 5;
	private ShoppingCartImpl shoppingCart;
	private static final String CAD = "CAD";
	private static final long TEST_SHIPPINGSERVICELEVEL_UID = 100;
	private static final long TEST_INVALID_SHIPPINGSERVICELEVEL_UID = 101;
	private static final long RULE_UID_1 = 1;
	private static final long RULE_UID_2 = 2;
	private static final long RULE_UID_3 = 3;
	private static final BigDecimal EXCLUSIVE_SUBTOTAL = new BigDecimal("65").setScale(2);
	private static final BigDecimal EXCLUSIVE_BEFORE_TAX_SHIPPING_COST = new BigDecimal("11.50").setScale(2);
	private static final BigDecimal EXCLUSIVE_BEFORE_TAX_TOTAL = new BigDecimal("76.50").setScale(2);
	private static final BigDecimal EXCLUSIVE_TAX = new BigDecimal("8.37").setScale(2);
	private static final BigDecimal EXCLUSIVE_TOTAL = new BigDecimal("84.87").setScale(2);
	private static final BigDecimal INCLUSIVE_SUBTOTAL_BEFORE_TAX = new BigDecimal("58.66").setScale(2);
	private static final BigDecimal INCLUSIVE_BEFORE_TAX_SHIPPING_COST = new BigDecimal("11.11").setScale(2);
	private static final BigDecimal INCLUSIVE_BEFORE_TAX_TOTAL = new BigDecimal("69.77").setScale(2);
	private static final BigDecimal INCLUSIVE_ITEM_TAX = new BigDecimal("6.34").setScale(2);
	private static final BigDecimal INCLUSIVE_TAX = new BigDecimal("6.73").setScale(2);
	private static final BigDecimal INCLUSIVE_TOTAL = new BigDecimal("76.50").setScale(2);

	private static final String REGION_CODE_CA = "CA";
	private static final String REGION_CODE_BC = "BC";
	private static final String SALES_TAX_CODE_GOODS = "GOODS";
	private static final String GST_TAX_CODE = "GST";
	private static final String PST_TAX_CODE = "PST";
	private static final BigDecimal GST_TAX_PERCENTAGE = new BigDecimal("6");
	private static final BigDecimal PST_TAX_PERCENTAGE = new BigDecimal("7");
	private static final long RULE_ID = 123L;
	private static final long ACTION_ID = 456L;


	private long nextUid = 1;

	private TaxCalculationService taxCalculationService;
	private TaxCalculationResult taxCalculationResult;
	private StoreService storeService;

	/**
	 * Prepare for tests.
	 *
	 * @throws Exception -- in case of any errors
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception {
		super.setUp();
		stubGetBean(ContextIdNames.CATALOG_LOCALE, CatalogLocaleImpl.class);
		stubGetBean(ContextIdNames.CUSTOMER_AUTHENTICATION, CustomerAuthenticationImpl.class);
		stubGetBean(ContextIdNames.CUSTOMER_PROFILE, CustomerProfileImpl.class);
		stubGetBean(ContextIdNames.CUSTOMER_PROFILE_VALUE, CustomerProfileValueImpl.class);
		stubGetBean(ContextIdNames.LOCALIZED_PROPERTIES, LocalizedPropertiesImpl.class);
		stubGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		stubGetBean(ContextIdNames.SHOPPING_CART_MEMENTO, ShoppingCartMementoImpl.class);
		stubGetBean(ContextIdNames.TAX_CATEGORY, TaxCategoryImpl.class);
		stubGetBean(ContextIdNames.TAX_CALCULATION_RESULT, TaxCalculationResultImpl.class);
		stubGetBean(ContextIdNames.TAX_JURISDICTION, TaxJurisdictionImpl.class);

		stubGetBean(ContextIdNames.UTILITY, new UtilityImpl());
		stubGetBean("settingsService", new SettingsServiceImpl());
		stubGetBean("orderSkuFactory", new OrderSkuFactoryImpl() {
			@Override
			protected OrderSku createSimpleOrderSku() {
				return new OrderSkuImpl();
			}
		});

		mockGetBeanSearchResultHistory();

		shoppingCart = new ShoppingCartImpl();

		initializeShoppingCart(shoppingCart);

		// TODO: Remove tax calculation service from shopping cart during refactor of shopping cart
		// set up tax calculation service to return null as the
		// TaxCalculationResult
		taxCalculationResult = context.mock(TaxCalculationResult.class);
		taxCalculationService = context.mock(TaxCalculationService.class);
		context.checking(new Expectations() {
			{
				allowing(taxCalculationResult).applyTaxes(with(any(Collection.class)));

				allowing(taxCalculationService).calculateTaxes(with(any(String.class)), with(any(Address.class)),
						with(any(Currency.class)), with(any(Money.class)),
						with(any(Collection.class)), with(any(Money.class)));
				will(returnValue(taxCalculationResult));
				allowing(taxCalculationService).calculateTaxesAndAddToResult(
						with(any(TaxCalculationResult.class)),
						with(any(String.class)),
						with(aNull(Address.class)),
						with(any(Currency.class)),
						with(any(Money.class)),
						with(any((Collection.class))),
						with(any(Money.class)));
				will(returnValue(taxCalculationResult));
			}
		});
		stubGetBean(ContextIdNames.TAX_CALCULATION_SERVICE, taxCalculationService);
		shoppingCart.setTaxCalculationService(taxCalculationService);

		final EpRuleEngine ruleEngine = context.mock(EpRuleEngine.class);
		context.checking(new Expectations() {
			{
				allowing(ruleEngine);
			}
		});
		stubGetBean(ContextIdNames.EP_RULE_ENGINE, ruleEngine);
		stubGetBean(ContextIdNames.GIFT_CERTIFICATE_SERVICE,
				new GiftCertificateServiceImpl() {
			@Override
			public BigDecimal getBalance(final GiftCertificate giftCertificate) {
				return giftCertificate.getPurchaseAmount();
			}
		});

		AllocationServiceImpl allocationServiceImpl = new AllocationServiceImpl();
		allocationServiceImpl.setProductInventoryManagementService(new ProductInventoryManagementServiceImpl());
		stubGetBean(ContextIdNames.ALLOCATION_SERVICE, allocationServiceImpl);

		final Warehouse warehouse = context.mock(Warehouse.class);
		context.checking(new Expectations() {
			{
				allowing(warehouse).getUidPk();
				will(returnValue(0L));
			}
		});
		getMockedStore().setWarehouses(Arrays.asList(warehouse));

		storeService = context.mock(StoreService.class);
		context.checking(new Expectations() {
			{
				allowing(storeService).findStoreWithCode(getMockedStore().getCode()); will(returnValue(getMockedStore()));
			}
		});
	}

	/**
	 * Initializes a shopping cart instance. Helps to initialize an instance of any shopping cart in the test case.
	 */
	private void initializeShoppingCart(final ShoppingCart shoppingCart) {
		final Shopper shopper = TestShopperFactory.getInstance().createNewShopperWithMemento();
		final CustomerSession customerSession = TestCustomerSessionFactory.getInstance().createNewCustomerSessionWithContext(shopper);

		shoppingCart.setCustomerSession(customerSession);
		shoppingCart.setCurrency(Currency.getInstance(CAD));
		shoppingCart.setLocale(DEFAULT_LOCALE);
		shoppingCart.setStore(getMockedStore());
	}

	private void mockGetBeanSearchResultHistory() {
		final CatalogViewResultHistory catalogViewResultHistory = context.mock(CatalogViewResultHistory.class);
		stubGetBean(ContextIdNames.CATALOG_VIEW_RESULT_HISTORY, catalogViewResultHistory);

		context.checking(new Expectations() {
			{
				allowing(catalogViewResultHistory).getResultList();
				will(returnValue(Collections.emptyList()));
			}
		});
	}


	@Override
	protected Product newProductImpl() {
		return new ProductImpl() {
			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getDisplayName(final Locale locale) {
				return "Test Display Name";
			}
		};
	}

	/**
	 * Get a list of cart items for testing a shopping cart.
	 *
	 * @param shoppingCart shopping cart
	 * @return a list of CartItems
	 */
	public List<ShoppingItem> addCartItemsTo(final ShoppingCart shoppingCart) {

		List<ShoppingItem> cartItems = new ArrayList<ShoppingItem>();
		ShoppingItem cartItem = new ShoppingItemImpl() {
			private static final long serialVersionUID = -5224338510242755735L;

			@Override
			public String getProductTypeName() {
				return "SomeProductTypeThatsNotAGiftCertificate";
			}
		};
		cartItem.setUidPk(getUniqueUid());

		ProductSkuImpl productSkuImpl = new ProductSkuImpl();
		productSkuImpl.initialize();
		long warehouseUid = shoppingCart.getStore().getWarehouse().getUidPk();
		InventoryDto inventoryDto = new InventoryDtoImpl();
		inventoryDto.setWarehouseUid(warehouseUid);
		productSkuImpl.setUidPk(this.getUniqueUid());
		productSkuImpl.setSkuCode(CODE + this.getUniqueUid());
		productSkuImpl.setImage("image");
		Product productImpl = newProductImpl();
		productSkuImpl.setProduct(productImpl);
		productSkuImpl.setStartDate(new Date());

		productImpl.setGuid("guid1");
		productImpl.addCategory(getCategory());

		final ProductType productType = context.mock(ProductType.class, "ProductType-" + getUniqueUid());
		context.checking(new Expectations() {
			{
				allowing(productType).getTaxCode();
			}
		});
		productImpl.setProductType(productType);

		productSkuImpl.setProduct(productImpl);
		productSkuImpl.getProduct().setAvailabilityCriteria(AvailabilityCriteria.ALWAYS_AVAILABLE);

		cartItem.setProductSku(productSkuImpl);
		Price zeroPrice = new PriceImpl();
		zeroPrice.setCurrency(Currency.getInstance(CAD));
		zeroPrice.setListPrice(MoneyFactory.createMoney(BigDecimal.ZERO, Currency.getInstance(CAD)));
		zeroPrice.setSalePrice(MoneyFactory.createMoney(BigDecimal.ZERO, Currency.getInstance(CAD)));

		cartItem.setPrice(QTY_5, zeroPrice);

		shoppingCart.addCartItem(cartItem);
		cartItems.add(cartItem);

		Price price = new PriceImpl();
		price.setCurrency(Currency.getInstance(CAD));
		price.setListPrice(MoneyFactory.createMoney(new BigDecimal(PRICE_2), Currency.getInstance(CAD)));
		price.setSalePrice(MoneyFactory.createMoney(new BigDecimal(PRICE_2), Currency.getInstance(CAD)));
		cartItem = new ShoppingItemImpl() {
			private static final long serialVersionUID = 4239790648963955465L;

			@Override
			public String getProductTypeName() {
				return "SomeProductTypeThatsNotAGiftCertificate";
			}
		};
		cartItem.setUidPk(getUniqueUid());
		ProductSkuImpl productSkuImpl2 = new ProductSkuImpl();
		productSkuImpl2.initialize();
		productSkuImpl2.setProduct(productImpl);
		InventoryDto inventory2 = new InventoryDtoImpl();
		inventory2.setWarehouseUid(warehouseUid);
		inventory2.setSkuCode(productSkuImpl2.getSkuCode());
		productSkuImpl2.getProduct().setAvailabilityCriteria(AvailabilityCriteria.ALWAYS_AVAILABLE);
		productSkuImpl2.setUidPk(this.getUniqueUid());
		productSkuImpl2.setSkuCode(CODE + this.getUniqueUid());
		productSkuImpl2.setImage("image");
		productSkuImpl2.setProduct(getProduct());
		productSkuImpl2.setStartDate(new Date());

		cartItem.setProductSku(productSkuImpl2);
		cartItem.setPrice(QTY_3, price);
		shoppingCart.addCartItem(cartItem);
		cartItems.add(cartItem);

		return cartItems;
	}

	/**
	 * Test Get the cart items in the shopping cart.
	 */
	@Test
	public void testGetSetCartItems() {

		List<ShoppingItem> cartItems = addCartItemsTo(shoppingCart);

		assertEquals(cartItems, shoppingCart.getCartItems());
	}

	/**
	 * Test clearing the shopping cart.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testClearItems() {
		final TaxCalculationResult taxCalculationResult = new TaxCalculationResultImpl() {
			private static final long serialVersionUID = -1601366344699530100L;

			@Override
			public void applyTaxes(final Collection< ? extends ShoppingItem> shoppingItems) {
				// do nothing
			}
		};

		final TaxCalculationService taxCalculationService = context.mock(TaxCalculationService.class, "TaxCalculationService-1");
		context.checking(new Expectations() {
			{
				allowing(taxCalculationService).calculateTaxes(with(any(String.class)), with(any(Address.class)),
						with(any(Currency.class)), with(any(Money.class)),
						with(any(Collection.class)), with(any(Money.class)));
				will(returnValue(taxCalculationResult));
				allowing(taxCalculationService).calculateTaxesAndAddToResult(
						with(any(TaxCalculationResult.class)),
						with(any(String.class)),
						with(aNull(Address.class)),
						with(any(Currency.class)),
						with(any(Money.class)),
						with(any(Collection.class)),
						with(any(Money.class)));
				will(returnValue(taxCalculationResult));
			}
		});

		stubGetBean(ContextIdNames.TAX_CALCULATION_SERVICE, taxCalculationService);
		shoppingCart.setTaxCalculationService(taxCalculationService);

		Currency defaultCurrency = Currency.getInstance(Locale.CANADA);
		taxCalculationResult.setDefaultCurrency(defaultCurrency);
		taxCalculationResult.setBeforeTaxSubTotal(MoneyFactory.createMoney(BigDecimal.TEN, defaultCurrency));
		addCartItemsTo(shoppingCart);
		assertTrue(shoppingCart.getNumItems() > 0);
		assertTrue(shoppingCart.getTotal().compareTo(BigDecimal.ZERO) > 0);

		shoppingCart.clearItems();

		assertEquals(0, shoppingCart.getNumItems());
		assertEquals(BigDecimal.ZERO, shoppingCart.getTotal());
	}

	/**
	 * Test Add an item to the cart.
	 */
	@Test
	public void testAddCartItem() {
		addCartItemsTo(shoppingCart);

		ShoppingItem newItem = new ShoppingItemImpl();
		ProductSku productSku = this.getProductSku();
		productSku.setUidPk(this.getUniqueUid());
		productSku.setSkuCode(CODE + this.getUniqueUid());
		newItem.setProductSku(productSku);
		Price price = new PriceImpl();
		price.setCurrency(Currency.getInstance(CAD));
		price.setListPrice(MoneyFactory.createMoney(new BigDecimal(PRICE_2), Currency.getInstance(CAD)));
		price.setSalePrice(MoneyFactory.createMoney(new BigDecimal(PRICE_2), Currency.getInstance(CAD)));
		newItem.setPrice(1, price);
		shoppingCart.addCartItem(newItem);
		assertTrue(shoppingCart.getCartItems().contains(newItem));
	}

	/**
	 * Test Remove an item from the cart.
	 */
	@Test
	public void testRemoveCartItem() {
		List<ShoppingItem> cartItems = addCartItemsTo(shoppingCart);
		int numCartItemObjects = cartItems.size();

		// Check that invalid uids don't cause anything to be removed
		shoppingCart.removeCartItem(INVALID_UID_1);
		shoppingCart.removeCartItem(INVALID_UID_2);

		assertEquals(numCartItemObjects, shoppingCart.getCartItems().size());

		long skuUidToRemove = cartItems.get(0).getUidPk();
		shoppingCart.removeCartItem(skuUidToRemove);
		shoppingCart.removeCartItem(skuUidToRemove);

		assertEquals(numCartItemObjects - 1, shoppingCart.getCartItems().size());
	}

	/**
	 * Test Remove an item from the cart.
	 */
	@Test
	public void testRemoveIndependentCartItem() {
		List<ShoppingItem> cartItems = addCartItemsTo(shoppingCart);
		ShoppingItem primaryItem = cartItems.get(0);
		ShoppingItem dependentItem = cartItems.get(1);

		// Test non-dependent behaviour
		assertEquals(2, shoppingCart.getCartItems().size());
		shoppingCart.removeCartItem(primaryItem.getUidPk());

		assertEquals(1, shoppingCart.getCartItems().size());
		shoppingCart.removeCartItem(dependentItem.getUidPk());
		assertEquals(0, shoppingCart.getCartItems().size());
	}

	/**
	 * Test Return the number of items in the shopping cart.
	 */
	@Test
	public void testGetNumItems() {
		addCartItemsTo(shoppingCart);
		assertEquals(shoppingCart.getNumItems(), QTY_3 + QTY_5);
		shoppingCart.clearItems();
		assertEquals(shoppingCart.getNumItems(), 0);
	}

	/**
	 * Get the subtotal of all items in the cart.
	 */
	@Test
	public void testGetSubTotal() {
		applyTaxCalculationResult(shoppingCart);

		assertNotNull(shoppingCart.getSubtotalMoney());
		assertTrue(BigDecimal.ZERO.compareTo(shoppingCart.getSubtotalMoney().getAmount()) < 0);
	}

	/**
	 * Tests that getTotal() retrieves the total properly.
	 */
	@Test
	public void testGetTotal() {
		applyTaxCalculationResult(shoppingCart);

		assertNotNull(shoppingCart.getTotalMoney());
		assertTrue(BigDecimal.ZERO.compareTo(shoppingCart.getTotalMoney().getAmount()) < 0);
	}

	/**
	 * Tests that getTotal() retrieves the total properly.
	 */
	@Test
	public void testGetTotalBiggerGCAmount() {
		applyTaxCalculationResult(shoppingCart);

		final StoreImpl store = new StoreImpl();

		shoppingCart.setStore(store);

		GiftCertificate giftCertificate = mockGiftCertificate(store, new BigDecimal("1000"), shoppingCart.getCurrency());
		shoppingCart.applyGiftCertificate(giftCertificate);

		assertNotNull(shoppingCart.getTotalMoney());
		assertEquals(0, BigDecimal.ZERO.compareTo(shoppingCart.getTotalMoney().getAmount()));
	}

	private long getUniqueUid() {
		return nextUid++;
	}

	/**
	 * Test setLastCategory(Category) method.
	 */
	@Test
	public void testSetLastCategory() {
		final Category category = new CategoryImpl();
		this.shoppingCart.setLastCategory(category);
		assertSame(category, this.shoppingCart.getLastCategory());
	}

	/**
	 * Test getSearchResultHistory() method.
	 */
	@Test
	public void testGetSearchResultHistory() {
		CatalogViewResultHistory catalogViewResultHistory = this.shoppingCart.getSearchResultHistory();
		assertNotNull(catalogViewResultHistory);

		// Should get the same search results next timet
		assertSame(catalogViewResultHistory, this.shoppingCart.getSearchResultHistory());
		assertSame(catalogViewResultHistory, this.shoppingCart.getCatalogViewResultHistory());
	}

	/**
	 * Test getShippingCost() method.
	 */
	@Test
	public void testGetShippingCost() {
		assertNotNull(this.shoppingCart.getShippingCost());
		assertEquals("Shipping cost should compare to zero.",
				0, BigDecimal.ZERO.compareTo(this.shoppingCart.getShippingCost().getAmount()));
	}

	/**
	 * Test setShippingServiceLevelList() method.
	 */
	@Test
	public void testGetSetShippingServiceLevelList() {
		assertNotNull(this.shoppingCart.getShippingServiceLevelList());
		final List<ShippingServiceLevel> shippingServiceLevleList = new ArrayList<ShippingServiceLevel>();
		shoppingCart.setShippingServiceLevelList(shippingServiceLevleList);
		assertEquals(shippingServiceLevleList, this.shoppingCart.getShippingServiceLevelList());
	}

	/**
	 * Test get/setSelectedShippingServiceLevelUid() method.
	 */
	@Test
	public void testGetSetSelectedShippingServiceLevelUid() {

		shoppingCart = new ShoppingCartImpl() {
			private static final long serialVersionUID = 9092521286498956021L;

			@Override
			public boolean requiresShipping() {
				return true;
			}
		};
		initializeShoppingCart(shoppingCart);

		shoppingCart.setLocale(DEFAULT_LOCALE);

		shoppingCart.setCurrency(Currency.getInstance(CAD));

		assertNull(this.shoppingCart.getSelectedShippingServiceLevel());

		// expectation
		try {
			shoppingCart.setSelectedShippingServiceLevelUid(TEST_SHIPPINGSERVICELEVEL_UID);
			fail(EP_DOMAIN_EXCEPTION_EXPECTED);
		} catch (final EpDomainException e) {
			assertNotNull(e);
			// Success!
		}

		// set valid shippingServiceLevelList
		final Money shippingCost = MoneyFactory.createMoney(new BigDecimal(PRICE_1), Currency.getInstance(CAD));
		this.shoppingCart.setShippingServiceLevelList(getShippingServiceLevelList(shippingCost));

		// expectation
		try {
			shoppingCart.setSelectedShippingServiceLevelUid(TEST_INVALID_SHIPPINGSERVICELEVEL_UID);
			fail(EP_DOMAIN_EXCEPTION_EXPECTED);
		} catch (final EpDomainException e) {
			assertNotNull(e);
			// Success!
		}

		shoppingCart.setSelectedShippingServiceLevelUid(TEST_SHIPPINGSERVICELEVEL_UID);
		assertTrue("could not set a 100% discount", shoppingCart.getSelectedShippingServiceLevel().setShippingDiscount(shippingCost));
		assertFalse("existing discount < than new discount", shoppingCart.getSelectedShippingServiceLevel().setShippingDiscount(shippingCost));


		final Money discountedShippingCost = MoneyFactory.createMoney(BigDecimal.ZERO, Currency.getInstance(CAD));
		shoppingCart.getSelectedShippingServiceLevel().setShippingDiscount(discountedShippingCost);
		assertEquals("discount not applied?", discountedShippingCost, shoppingCart.getShippingCost());

		// reset the list, should not lose selected level, but should clear the discount
		this.shoppingCart.setShippingServiceLevelList(getShippingServiceLevelList(shippingCost));
		assertNotNull("resetting service levels lost the selected level", shoppingCart.getSelectedShippingServiceLevel());
		assertNotSame("shipping discount found?", discountedShippingCost, shoppingCart.getShippingCost());
		assertEquals("shipping discount not cleared", shippingCost, shoppingCart.getShippingCost());

		shoppingCart.getSelectedShippingServiceLevel().setShippingDiscount(discountedShippingCost);
		shoppingCart.fireRules(); // should clear the discount
		assertNotNull("firing rules lost the selected level", shoppingCart.getSelectedShippingServiceLevel());
		assertNotSame("shipping discount found?", discountedShippingCost, shoppingCart.getShippingCost());
		assertEquals("shipping discount not cleared", shippingCost, shoppingCart.getShippingCost());

	}

	/**
	 * Get a list of shipping service levels.
	 *
	 * @param shippingCost the shipping cost
	 * @return a list containing a service level with the specified shipping cost
	 */
	@SuppressWarnings("unchecked")
	private List<ShippingServiceLevel> getShippingServiceLevelList(final Money shippingCost) {
		final List<ShippingServiceLevel> shippingServiceLevelList = new ArrayList<ShippingServiceLevel>();
		final ShippingServiceLevel shippingServiceLevel = new ShippingServiceLevelImpl();
		shippingServiceLevel.setUidPk(TEST_SHIPPINGSERVICELEVEL_UID);

		final ShippingCostCalculationMethod shippingCostCalculationMethod = context.mock(ShippingCostCalculationMethod.class,
				"ShippingCostCalculationMethod-" + getUniqueUid());
		context.checking(new Expectations() {
			{
				allowing(shippingCostCalculationMethod).calculateShippingCost(with(any(Collection.class)),
						with(equal(shoppingCart.getCurrency())));
				will(returnValue(shippingCost));
			}
		});

		shippingServiceLevel.setShippingCostCalculationMethod(shippingCostCalculationMethod);
		shippingServiceLevelList.add(shippingServiceLevel);
		return shippingServiceLevelList;
	}

	/**
	 * Test getBrowsingResultHistory() method.
	 */
	@Test
	public void testGetBrowsingResultHistory() {
		CatalogViewResultHistory catalogViewResultHistory = this.shoppingCart.getBrowsingResultHistory();
		assertNotNull(catalogViewResultHistory);
		assertEquals(0, this.shoppingCart.getSearchResultHistory().getResultList().size());

		// Switch to search result history will clear the browsing history.
		CatalogViewResultHistory searchResultHistory = this.shoppingCart.getSearchResultHistory();
		assertSame(searchResultHistory, this.shoppingCart.getCatalogViewResultHistory());
	}


	/**
	 * Test that if any item in the shopping cart is shippable then the shopping cart requires shipping.
	 */
	@Test
	public void testRequiresShipping() {
		ShoppingCart cart = shippableItemsCart(true);
		assertTrue(cart.requiresShipping());

		cart = shippableItemsCart(true, false);
		assertTrue(cart.requiresShipping());

		cart = shippableItemsCart(true, true, false);
		assertTrue(cart.requiresShipping());

		cart = shippableItemsCart(true, false, true);
		assertTrue(cart.requiresShipping());

		cart = shippableItemsCart(false, false, true);
		assertTrue(cart.requiresShipping());

		cart = shippableItemsCart(false, false, false);
		assertFalse(cart.requiresShipping());

		cart = shippableItemsCart();
		assertFalse(cart.requiresShipping());

	}

	private ShoppingCart shippableItemsCart(final boolean ... items) {
		final List<ShoppingItem> cartItems = new ArrayList<ShoppingItem>();

		for (final boolean shippable : items) {
			final ShoppingItem item = context.mock(ShoppingItem.class, "ShoppingItem-" + getUniqueUid());
			context.checking(new Expectations() {
				{
					allowing(item).isShippable();
					will(returnValue(shippable));
				}
			});
			cartItems.add(item);
		}
		ShoppingCartImpl cart = new ShoppingCartImpl() {
			private static final long serialVersionUID = -8497118209873625320L;

			@Override
			public List<ShoppingItem> getCartItems() {
				return cartItems;
			}
		};
		return cart;
	}

	/**
	 * Test method for calculateShoppingCartTaxAndBeforeTaxPrices.
	 */
	@Test
	public void testCalculateShoppingCartTaxAndBeforeTaxPricesInclusive() {
		// The default shopping cart will have a subtotal $65.00,
		// cartItem1: qty=3; listprice=salePrice=$5.00; salesTaxCode:BOOKS.
		// cartItem2: qty=5; listprice=salePrice=$10.00; salesTaxCode:DVDS.
		// with FixedBaseAndOrderTotalPercentageMethod for shipping(base: $5.00 and 10% of subtotal).
		// Billing Address: Joe Doe, 1295 Charleston Road, Mountain View, CA, US, 94043
		ShoppingCart shoppingCart = getShoppingCart();

		// shoppingCart.setSubtotalDiscount(ORDER_DISCOUNT);

		// TaxCode.SALES_TAX_CODE_SHIPPING: 3.5
		TaxCategory taxCategory1 = new TaxCategoryImpl();

		Currency cadCurrency = Currency.getInstance(CAD);
		TaxCalculationResult taxCalculationResult = new TaxCalculationResultImpl();
		taxCalculationResult.setDefaultCurrency(cadCurrency);
		taxCalculationResult.setTaxInclusive(true);
		Money taxValue = MoneyFactory.createMoney(INCLUSIVE_TAX, cadCurrency);
		taxCalculationResult.setTaxValue(taxCategory1, taxValue);
		Money inclusiveSubTotalMoney = MoneyFactory.createMoney(INCLUSIVE_SUBTOTAL_BEFORE_TAX, cadCurrency);
		taxCalculationResult.addShippingTax(MoneyFactory.createMoney(INCLUSIVE_TAX, cadCurrency));
		Money inclusiveBeforeTaxShippingCost = MoneyFactory.createMoney(INCLUSIVE_BEFORE_TAX_SHIPPING_COST, cadCurrency);
		taxCalculationResult.setBeforeTaxShippingCost(inclusiveBeforeTaxShippingCost);
		taxCalculationResult.setBeforeTaxSubTotal(inclusiveSubTotalMoney);
		taxCalculationResult.addItemTax("SKUCODE", MoneyFactory.createMoney(INCLUSIVE_ITEM_TAX, cadCurrency));
		shoppingCart.setTaxCalculationResult(taxCalculationResult);

		// shoppingCart.setTaxJurisdiction(taxJurisdiction);
		// shoppingCart.calculateShoppingCartTaxAndBeforeTaxPrices();
		assertEquals(INCLUSIVE_SUBTOTAL_BEFORE_TAX, shoppingCart.getBeforeTaxSubTotal().getAmount());
		assertEquals(INCLUSIVE_BEFORE_TAX_SHIPPING_COST, shoppingCart.getBeforeTaxShippingCost().getAmount());
		assertEquals(INCLUSIVE_BEFORE_TAX_TOTAL, shoppingCart.getBeforeTaxTotal().getAmount());
		// tax = 0.39 (shipping tax) + 0.88 (item1 tax: 15/1.0625 * 0.0625) + 5.46 (item2 tax: 50/1.1225 * 0.1225) = 6.73
		assertEquals(INCLUSIVE_TAX, shoppingCart.getTaxMap().get(taxCategory1).getAmount());
		assertEquals(INCLUSIVE_TOTAL, shoppingCart.getTotal());
	}

	/**
	 * Test method for calculateShoppingCartTaxAndBeforeTaxPrices.
	 *
	 * The default shopping cart will have a subtotal $65.00,
	 * cartItem1: qty=3; listprice=salePrice=$5.00; salesTaxCode:BOOKS.
	 * cartItem2: qty=5; listprice=salePrice=$10.00; salesTaxCode:DVDS.
	 * with FixedBaseAndOrderTotalPercentageMethod for shipping(base: $5.00 and 10% of subtotal).
	 * Billing Address: Joe Doe, 1295 Charleston Road, Mountain View, CA, US, 94043
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCalculateShoppingCartTaxAndBeforeTaxPricesExclusive() {
		ShoppingCart shoppingCart = getShoppingCart();

		// TaxCode.SALES_TAX_CODE_SHIPPING: 3.5
		TaxCategory taxCategory1 = new TaxCategoryImpl();

		Currency cadCurrency = Currency.getInstance(CAD);
		final TaxCalculationResult taxCalculationResult = createTaxCalculationResult(taxCategory1, cadCurrency);
		taxCalculationResult.setBeforeTaxSubTotal(MoneyFactory.createMoney(EXCLUSIVE_SUBTOTAL, cadCurrency));
		taxCalculationResult.setBeforeTaxShippingCost(MoneyFactory.createMoney(EXCLUSIVE_BEFORE_TAX_SHIPPING_COST, cadCurrency));

		final TaxCalculationService taxCalculationService = context.mock(TaxCalculationService.class, "TaxCalculationService-1");
		context.checking(new Expectations() {
			{
				allowing(taxCalculationService).calculateTaxes(with(any(String.class)), with(any(Address.class)),
						with(any(Currency.class)), with(any(Money.class)),
						with(any(Collection.class)), with(any(Money.class)));
				will(returnValue(taxCalculationResult));
				allowing(taxCalculationService).calculateTaxesAndAddToResult(
						with(any(TaxCalculationResult.class)),
						with(any(String.class)),
						with(any(Address.class)),
						with(any(Currency.class)),
						with(any(Money.class)),
						with(any(Collection.class)),
						with(any(Money.class)));
				will(returnValue(taxCalculationResult));
			}
		});

		stubGetBean(ContextIdNames.TAX_CALCULATION_SERVICE, taxCalculationService);
		((ShoppingCartImpl) shoppingCart).setTaxCalculationService(taxCalculationService);

		shoppingCart.calculateShoppingCartTaxAndBeforeTaxPrices();

		assertEquals(EXCLUSIVE_SUBTOTAL, shoppingCart.getBeforeTaxSubTotal().getAmount().setScale(2));
		assertEquals(EXCLUSIVE_BEFORE_TAX_SHIPPING_COST, shoppingCart.getBeforeTaxShippingCost().getAmount());
		assertEquals(EXCLUSIVE_BEFORE_TAX_TOTAL, shoppingCart.getBeforeTaxTotal().getAmount());
		// tax = 11.50 * 0.035 (shipping tax) + (15 + 50) * 0.1225 = 8.37
		assertEquals(EXCLUSIVE_TAX, shoppingCart.getTaxMap().get(taxCategory1).getAmount());
		assertEquals(EXCLUSIVE_TOTAL, shoppingCart.getTotal());
	}

	private TaxCalculationResult createTaxCalculationResult(final TaxCategory taxCategory, final Currency cadCurrency) {
		TaxCalculationResult taxCalculationResult = new TaxCalculationResultImpl() {
			private static final long serialVersionUID = 6251210027242439881L;

			@Override
			public void applyTaxes(final Collection< ? extends ShoppingItem> shoppingItems) {
				// do nothing
			}
		};
		taxCalculationResult.setDefaultCurrency(cadCurrency);
		taxCalculationResult.setTaxInclusive(false);
		Money taxValue = MoneyFactory.createMoney(EXCLUSIVE_TAX, cadCurrency);
		taxCalculationResult.setTaxValue(taxCategory, taxValue);
		Money inclusiveSubTotalMoney = MoneyFactory.createMoney(EXCLUSIVE_SUBTOTAL, cadCurrency);
		taxCalculationResult.setBeforeTaxSubTotal(inclusiveSubTotalMoney);
		taxCalculationResult.setTaxInItemPrice(taxValue);
		return taxCalculationResult;
	}

// FIXME: Reimplement tests once the deprecated addAssociatedCartItem method is fixed (but remains deprecated)
//	/** Test for addAssociatied cart item -- not source product dependent. */
//	public void testAddAssociatedCartItem() {
//		setupTaxCalculationServiceReturnNull();
//
//		ShoppingCart shoppingCart = getShoppingCart();
//		ProductSku targetSku = this.getProductSku();
//		targetSku.setUidPk(1);
//		targetSku.setGuid("1");
//		Product targetProduct = this.getProduct();
//		targetProduct.addOrUpdateSku(targetSku);
//		targetProduct.setDefaultSku(targetSku);
//		targetSku.setProduct(targetProduct);
//
//		ProductAssociation productAssociation = new ProductAssociationImpl();
//		productAssociation.setTargetProduct(targetProduct);
//		productAssociation.setDefaultQuantity(QTY_3);
//		productAssociation.setSourceProductDependent(false);
//
//		ProductSku parentSku = this.getProductSku();
//		parentSku.setUidPk(2);
//		parentSku.setGuid("2");
//
//		CartItem parentCartItem = shoppingCart.addCartItem(parentSku, 1);
//		CartItem addedCartItem = shoppingCart.addAssociatedCartItem(productAssociation, parentCartItem);
//
//		assertEquals(addedCartItem.getProductSku(), targetSku);
//		assertEquals(QTY_3, addedCartItem.getQuantity());
//		assertTrue(parentCartItem.getDependentCartItems() == null || parentCartItem.getDependentCartItems().size() == 0);
//		assertNull(addedCartItem.getParentCartItem());
//
//		// Check that duplicate cart items are not created
//		int numCartItems = shoppingCart.getCartItems().size();
//		CartItem existingCartItem = shoppingCart.addAssociatedCartItem(productAssociation, parentCartItem);
//		assertEquals(numCartItems, shoppingCart.getCartItems().size());
//		assertEquals(QTY_3 * 2, existingCartItem.getQuantity());
//
//	}
//
//	/** Test for addCartItem -- test dependent cart item case. */
//	public void testAddAssociatedCartItem2() {
//		setupTaxCalculationServiceReturnNull();
//
//		ShoppingCart shoppingCart = getShoppingCart();
//		ProductSku targetSku = this.getProductSku();
//		Product targetProduct = this.getProduct();
//		targetProduct.addOrUpdateSku(targetSku);
//		targetProduct.setDefaultSku(targetSku);
//		targetSku.setProduct(targetProduct);
//
//		ProductAssociation productAssociation = new ProductAssociationImpl();
//		productAssociation.setTargetProduct(targetProduct);
//		productAssociation.setDefaultQuantity(QTY_3);
//		productAssociation.setSourceProductDependent(true);
//
//		ProductSku parentSku = this.getProductSku();
//		parentSku.setUidPk(1);
//		parentSku.setGuid("1");
//
//		CartItem parentCartItem = shoppingCart.addCartItem(parentSku, QTY_5);
//		CartItem addedCartItem = shoppingCart.addAssociatedCartItem(productAssociation, parentCartItem);
//
//
//		assertEquals(addedCartItem.getProductSku(), targetSku);
//		assertEquals(QTY_5, addedCartItem.getQuantity());
//		assertEquals(1, parentCartItem.getDependentCartItems().size());
//		assertEquals(parentCartItem, addedCartItem.getParentCartItem());
//
//		// Dependant cart items can have duplicates, IF parents are different.
//		// Quantity will be updated for dependent cart item to the new quantity of the parent cart item.
//		int numCartItems = shoppingCart.getCartItems().size();
//		CartItem existingCartItem = shoppingCart.addAssociatedCartItem(productAssociation, parentCartItem);
//		assertEquals(numCartItems, shoppingCart.getCartItems().size());
//		assertEquals(QTY_5, existingCartItem.getQuantity());
//	}

	/**
	 * Test getTotalWeight method.
	 */
	@Test
	public void testGetTotalWeight() {
		final BigDecimal weight1 = new BigDecimal("7.0");
		final int quantity1 = 3;

		final ShoppingItem shippableItem = context.mock(ShoppingItem.class, "ShoppingItem (shippable)");
		context.checking(new Expectations() {
			{
				ProductSku shippableProductSku = context.mock(ProductSku.class, "ProductSku (shippable)");
				allowing(shippableProductSku).isShippable();
				will(returnValue(true));
				allowing(shippableProductSku).getWeight();
				will(returnValue(weight1));

				allowing(shippableItem).getProductSku();
				will(returnValue(shippableProductSku));
				allowing(shippableItem).getQuantity();
				will(returnValue(quantity1));
			}
		});

		final ShoppingItem nonShippableItem = context.mock(ShoppingItem.class, "ShoppingItem (non-shippable)");
		context.checking(new Expectations() {
			{
				ProductSku nonShippableProductSku = context.mock(ProductSku.class, "ProductSku (non-shippable)");
				allowing(nonShippableProductSku).isShippable();
				will(returnValue(false));

				allowing(nonShippableItem).getProductSku();
				will(returnValue(nonShippableProductSku));
			}
		});


		final List<ShoppingItem> cartItems = new ArrayList<ShoppingItem>();
		cartItems.add(shippableItem);
		cartItems.add(nonShippableItem);

		ShoppingCartImpl cart = new ShoppingCartImpl() {
			private static final long serialVersionUID = -9139751788820519457L;

			@Override
			public List<ShoppingItem> getCartItems() {
				return cartItems;
			}
		};

		assertEquals(new BigDecimal("21.0"), cart.getTotalWeight());
	}


	/**
	 * Test case for the code tracking the ids of the rules applied.
	 */
	@Test
	public void testAppliedRules() {
		ShoppingCart shoppingCart = getShoppingCart();
		assertNotNull(shoppingCart.getAppliedRules());
		shoppingCart.ruleApplied(RULE_UID_1, 0, null, null, 0);
		shoppingCart.ruleApplied(RULE_UID_2, 0, null, null, 0);
		shoppingCart.ruleApplied(RULE_UID_2, 0, null, null, 0);
		shoppingCart.ruleApplied(RULE_UID_2, 0, null, null, 0);
		shoppingCart.ruleApplied(RULE_UID_3, 0, null, null, 0);
		assertEquals(NUM_UNIQUE_RULES, shoppingCart.getAppliedRules().size());
		Long appliedRuleUid = shoppingCart.getAppliedRules().iterator().next();
		assertNotNull(appliedRuleUid);
	}

	/**
	 * Test method for setting the subtotal discount.
	 */
	@Test
	public void testSetSubtotalDiscount1() {
		ShoppingCart shoppingCart = getShoppingCart();
		applyTaxCalculationResult(shoppingCart);

		BigDecimal highDiscount = new BigDecimal("6");
		BigDecimal lowDiscount = new BigDecimal("5");

		shoppingCart.setSubtotalDiscount(highDiscount, RULE_ID, ACTION_ID);
		shoppingCart.setSubtotalDiscount(lowDiscount, RULE_ID, ACTION_ID);

		assertEquals(highDiscount, shoppingCart.getSubtotalDiscount());
	}

	/**
	 * Test method for setting the subtotal discount.
	 */
	@Test
	public void testSetSubtotalDiscount2() {
		ShoppingCart shoppingCart = getShoppingCart();
		applyTaxCalculationResult(shoppingCart);

		BigDecimal lowDiscount = new BigDecimal("5");
		BigDecimal highDiscount = new BigDecimal("6");

		shoppingCart.setSubtotalDiscount(highDiscount, RULE_ID, ACTION_ID);
		shoppingCart.setSubtotalDiscount(lowDiscount, RULE_ID, ACTION_ID);

		assertEquals(highDiscount, shoppingCart.getSubtotalDiscount());
	}

	/**
	 * Test method for setting the subtotal discount.
	 */
	@Test
	public void testSetSubtotalDiscount3() {
		ShoppingCart shoppingCart = getShoppingCart();
		applyTaxCalculationResult(shoppingCart);

		BigDecimal highDiscount = BigDecimal.TEN.setScale(2);
		BigDecimal lowDiscount = new BigDecimal("5.00");

		shoppingCart.setSubtotalDiscount(highDiscount, RULE_ID, ACTION_ID);
		shoppingCart.clearItems();
		shoppingCart.setSubtotalDiscount(lowDiscount, RULE_ID, ACTION_ID);

		// Cart subtotal is zero, so can't set the subtotal discount above.
		assertEquals(BigDecimal.ZERO, shoppingCart.getSubtotalDiscount());
	}

	/**
	 * Test method for setting the subtotal discount.
	 */
	@Test
	public void testSetSubtotalDiscount4() {
		ShoppingCart shoppingCart = getShoppingCart();
		applyTaxCalculationResult(shoppingCart);

		BigDecimal hugeDiscount = new BigDecimal("1000000");
		BigDecimal subtotal = shoppingCart.getSubtotal();

		shoppingCart.setSubtotalDiscount(hugeDiscount, RULE_ID, ACTION_ID);

		assertEquals(subtotal, shoppingCart.getSubtotalDiscount());
	}

	/**
	 * Test method for apply gift certificate redeem.
	 */
	@Test
	public void testApplyGiftCertificateRedeem() {

		ShoppingCart shoppingCart = getShoppingCart();

		GiftCertificate giftCertificate = mockGiftCertificate(shoppingCart.getStore(), BigDecimal.TEN, shoppingCart.getCurrency());
		shoppingCart.applyGiftCertificate(giftCertificate);
		assertTrue(shoppingCart.getAppliedGiftCertificates().contains(giftCertificate));
	}

	/**
	 * Test method for apply gift certificate redeem.
	 */
	@Test
	public void testApplyGiftCertificateRedeemTwice() {
		ShoppingCart shoppingCart = getShoppingCart();

		GiftCertificate giftCertificate = mockGiftCertificate(shoppingCart.getStore(), BigDecimal.TEN, shoppingCart.getCurrency());
		shoppingCart.applyGiftCertificate(giftCertificate);
		assertTrue(shoppingCart.getAppliedGiftCertificates().contains(giftCertificate));
		assertEquals(shoppingCart.getAppliedGiftCertificates().size(), 1);

		shoppingCart.applyGiftCertificate(giftCertificate);
		assertTrue(shoppingCart.getAppliedGiftCertificates().contains(giftCertificate));
		assertEquals(shoppingCart.getAppliedGiftCertificates().size(), 1);
	}

	/**
	 * Test applying two gift certificates with the same amount.
	 */
	@Test
	public void testApplyTwoGiftCertificatesWithSameAmount() {
		final ShoppingCart shoppingCart = getShoppingCart();

		final GiftCertificate giftCertificate1 = mockGiftCertificate(shoppingCart.getStore(), BigDecimal.TEN, shoppingCart.getCurrency());
		final GiftCertificate giftCertificate2 = mockGiftCertificate(shoppingCart.getStore(), BigDecimal.TEN, shoppingCart.getCurrency());

		shoppingCart.applyGiftCertificate(giftCertificate1);
		assertTrue(shoppingCart.getAppliedGiftCertificates().contains(giftCertificate1));
		assertEquals(1, shoppingCart.getAppliedGiftCertificates().size());

		shoppingCart.applyGiftCertificate(giftCertificate2);
		assertTrue(shoppingCart.getAppliedGiftCertificates().contains(giftCertificate2));
		assertTrue(shoppingCart.getAppliedGiftCertificates().contains(giftCertificate1));
		assertEquals("Two gift certificates should have been applied", 2, shoppingCart.getAppliedGiftCertificates().size());

	}

	/**
	 * Test method for apply gift certificate redeem.
	 */
	@Test
	public void testApplyGiftCertificateRedeemWithZeroBalance() {
		final BigDecimal balance = BigDecimal.ZERO;

		stubGetBean(ContextIdNames.GIFT_CERTIFICATE_SERVICE,
				new GiftCertificateServiceImpl() {
					@Override
					public BigDecimal getBalance(final GiftCertificate giftCertificate) {
						return balance;
					}
				});

		ShoppingCart shoppingCart = getShoppingCart();

		GiftCertificate giftCertificate = mockGiftCertificate(shoppingCart.getStore(), balance, shoppingCart.getCurrency());
		try {
			shoppingCart.applyGiftCertificate(giftCertificate);
			fail("Expected domain exception due to zero balance");
		} catch (PaymentProcessingException ppe) {
			// Success
			assertNotNull(ppe);
		}

	}

	/**
	 * Test method for apply gift certificate redeem.
	 */
	@Test
	public void testApplyGiftCertificateRedeemWrongCurrency() {
		ShoppingCart shoppingCart = getShoppingCart();

		Currency currency = Currency.getInstance(Locale.ITALY);
		Assert.assertThat("Shopping cart currency must not be the gift certificate currency", shoppingCart.getCurrency(),
				Matchers.not(equalTo(currency)));
		GiftCertificate giftCertificate = mockGiftCertificate(shoppingCart.getStore(), BigDecimal.TEN, currency);

		try {
			shoppingCart.applyGiftCertificate(giftCertificate);
			fail("Expected domain exception due to mismatch currency code");
		} catch (PaymentProcessingException ppe) {
			// Success
			assertNotNull(ppe);
		}

	}

	/**
	 * Test method for apply gift certificate redeem.
	 */
	@Test
	public void testGetGiftCertificateDiscountWhenTotalIsLess() {
		ShoppingCartImpl shoppingCart = newShoppingCartForGiftCertificateTests(BigDecimal.ONE);

		shoppingCart.setStore(new StoreImpl());
		shoppingCart.setTaxCalculationService(new DefaultTaxCalculationServiceImpl() {

		});
		GiftCertificate giftCertificate = mockGiftCertificate(shoppingCart.getStore(), BigDecimal.TEN, shoppingCart.getCurrency());
		shoppingCart.applyGiftCertificate(giftCertificate);

		assertEquals("The redeemed GC amount should be not more than the shopping cart total",
				BigDecimal.ONE, shoppingCart.getGiftCertificateDiscount());
	}

	/**
	 * Test method for apply gift certificate redeem.
	 */
	@Test
	public void testGetGiftCertificateDiscountWhenTotalIsMore() {
		ShoppingCartImpl shoppingCart = newShoppingCartForGiftCertificateTests(new BigDecimal("15"));

		shoppingCart.setStore(new StoreImpl());
		shoppingCart.setTaxCalculationService(new DefaultTaxCalculationServiceImpl() {

		});

		GiftCertificate giftCertificate = mockGiftCertificate(shoppingCart.getStore(), BigDecimal.TEN, shoppingCart.getCurrency());
		shoppingCart.applyGiftCertificate(giftCertificate);

		assertEquals("The redeemed GC amount should exactly its amount when total > GC amount",
				BigDecimal.TEN, shoppingCart.getGiftCertificateDiscount());
	}


	private ShoppingCartImpl newShoppingCartForGiftCertificateTests(final BigDecimal totalBeforeRedeem) {
		ShoppingCartImpl shoppingCart = new ShoppingCartImpl() {
			private static final long serialVersionUID = 1955987907161856611L;

			@Override
			public BigDecimal getTotalBeforeRedeem() {
				return totalBeforeRedeem;
			}

			@Override
			public Currency getCurrency() {
				return Currency.getInstance(Locale.CANADA);
			}
		};
		return shoppingCart;
	}

	/**
	 * Test method for apply gift certificate redeem, sort by their balance.
	 */
	@Test
	public void testApplyGiftCertificateRedeems() {
		final long uidPkGc1 = 1L;
		final long uidPkGc2 = 2L;
		ShoppingCart shoppingCart = getShoppingCart();

		final GiftCertificate giftCertificate = new GiftCertificateImpl();
		giftCertificate.setUidPk(uidPkGc1);
		giftCertificate.setPurchaseAmount(BigDecimal.TEN);
		giftCertificate.setCurrencyCode(shoppingCart.getCurrency().getCurrencyCode());
		giftCertificate.setStore(shoppingCart.getStore());

		shoppingCart.applyGiftCertificate(giftCertificate);

		final GiftCertificate giftCertificate2 = new GiftCertificateImpl();
		giftCertificate2.setUidPk(uidPkGc2);
		giftCertificate2.setPurchaseAmount(BigDecimal.ONE);
		giftCertificate2.setCurrencyCode(shoppingCart.getCurrency().getCurrencyCode());
		giftCertificate2.setStore(shoppingCart.getStore());

		stubGetBean(ContextIdNames.GIFT_CERTIFICATE_SERVICE,
				new GiftCertificateServiceImpl() {
			@Override
			public BigDecimal getBalance(final GiftCertificate giftCert) {
				if (giftCert.getUidPk() == uidPkGc1) {
					return giftCertificate.getPurchaseAmount();
				} else if (giftCert.getUidPk() == uidPkGc2) {
					return giftCertificate2.getPurchaseAmount();
				}
				return null;
			}
		});

		shoppingCart.applyGiftCertificate(giftCertificate2);

		assertEquals(shoppingCart.getAppliedGiftCertificates().size(), 2);
		// GiftCertificate 2 with the smallest balance will be the first one to be redeemed.
		assertEquals(shoppingCart.getAppliedGiftCertificates().iterator().next(), giftCertificate2);

	}

	/**
	 * Test Scenario: Add electronic item to cart and make the tax calculation service calculate the taxes.
	 * The shipping and billing address are different and therefore other taxes apply.
	 * For digital products there are no shipping taxes.
	 *
	 * The store should have the tax codes enabled on both goods and shipping
	 * =====================================================================================
	 *                  | eBooks                   | DVDs
	 * =====================================================================================
	 * Price            | $15.00                   | $50.00
	 * -----------------+--------------------------+----------------------------------------
	 * Item Tax         | $0.90 (6%)               | $5.00 (10%)
	 * -----------------+--------------------------+----------------------------------------
	 * Shipping Cost    | $0.00 (digital product)  | $10.00 ($5.00 fixed + 10% from value)
	 * -----------------+--------------------------+----------------------------------------
	 * Shipping Tax     | $0.00                    | $0.50 (5%)
	 * -----------------+--------------------------+----------------------------------------
	 * Tax Address      | Canada (Billing)         | USA (Shipping)
	 * =====================================================================================
	 * SUBTOTAL $15.00 $60.00 (price + shipping)
	 * -------------------------------------------------------------------------------------
	 * SUBTOTAL + TAXES $15.90 $65.50
	 * =====================================================================================
	 * TOTAL $81.40
	 */
	@Test
	public void testCalculateTaxes1() {
		final ShoppingCart shoppingCart = getShoppingCart();
		Set <TaxCode> taxCodes = new HashSet <TaxCode>();
		taxCodes.add(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxCodes.add(createTaxCode(SALES_TAX_CODE_DVDS));
		taxCodes.add(createTaxCode(SALES_TAX_CODE_BOOKS));
		shoppingCart.getStore().setTaxCodes(taxCodes);

		// set first cart item to be an electronic product (i.e. eBook)
		shoppingCart.getCartItems().get(0).getProductSku().setShippable(false);
		// set billing address to be Canada
		shoppingCart.setBillingAddress(getBillingAddress());
		// set shipping address to be US
		shoppingCart.setShippingAddress(getShippingAddress());

		DefaultTaxCalculationServiceImpl taxCalculationService = new DefaultTaxCalculationServiceImpl();
		taxCalculationService.setStoreService(storeService);
		TaxJurisdictionServiceImpl taxJurisdictionService = new TaxJurisdictionServiceImpl();
		taxJurisdictionService.setPersistenceEngine(getPersistenceEngine());
		(taxCalculationService).setTaxJurisdictionService(taxJurisdictionService);

		((ShoppingCartImpl) shoppingCart).setTaxCalculationService(taxCalculationService);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("TAX_JURISDICTIONS_FROM_STORE_BY_COUNTRY_CODE",
						shoppingCart.getStore().getCode(), shoppingCart.getShippingAddress().getCountry());
				will(returnValue(getTaxJurisdictionsListForUS()));


				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("TAX_JURISDICTIONS_FROM_STORE_BY_COUNTRY_CODE",
						shoppingCart.getStore().getCode(), shoppingCart.getBillingAddress().getCountry());
				will(returnValue(getTaxJurisdictionsListForCA()));

				allowing(getMockPersistenceEngine()).isCacheEnabled();
				will(returnValue(false));
			}
		});

		// calculate tax and price
		shoppingCart.calculateShoppingCartTaxAndBeforeTaxPrices();

		TaxCalculationResult taxCalculationResult = shoppingCart.getTaxCalculationResult();
		Money shippingCostNoTaxes = taxCalculationResult.getBeforeTaxShippingCost();
		assertEquals(new BigDecimal("10.00"), shippingCostNoTaxes.getAmount());
		Money totalTaxes = taxCalculationResult.getTotalTaxes();
		Money totalTaxInItemPrice = taxCalculationResult.getTaxInItemPrice();
		assertFalse(taxCalculationResult.isTaxInclusive());
		assertNull(totalTaxInItemPrice);

		Money shippingTax = taxCalculationResult.getShippingTax();

		assertEquals(new BigDecimal("06.40"), totalTaxes.getAmount());
		assertEquals(new BigDecimal("00.50"), shippingTax.getAmount());
		assertEquals(new BigDecimal("81.40"), shoppingCart.getTotal());

		// assertEquals(new BigDecimal("05.90"), shoppingCart.get); //TODO check the total item tax amount
		assertEquals(new BigDecimal("10.00"), shoppingCart.getShippingCost().getAmount());
		assertEquals(new BigDecimal("65.00"), shoppingCart.getBeforeTaxSubTotal().getAmount());

	}

	private TaxCode createTaxCode(final String taxCodeName) {
		final TaxCode result = context.mock(TaxCode.class, "TaxCode-" + getUniqueUid());
		context.checking(new Expectations() {
			{
				allowing(result).getCode();
				will(returnValue(taxCodeName));
			}
		});
		return result;
	}

	/**
	 * Returns a newly created address.
	 *
	 * @return a newly created address
	 */
	protected Address getBillingAddress() {
		Address address = new CustomerAddressImpl();
		address.setFirstName("Joe");
		address.setLastName("Doe");
		address.setCountry("CA");
		address.setStreet1("1295 Charleston Road");
		address.setCity("Vancouver");
		address.setSubCountry("CA");
		address.setZipOrPostalCode("V5T 4H3");
		return address;
	}

	private Address getShippingAddress() {
		Address address = new CustomerAddressImpl();
		address.setFirstName("Joe");
		address.setLastName("Doe");
		address.setCountry("US");
		address.setStreet1("1295 Charleston Road");
		address.setCity("New York");
		address.setSubCountry("US");
		address.setZipOrPostalCode("12343");

		return address;
	}

	/**
	 * Tax Jurisdiction - CA Used only the matching strategy for matching the country code. Tax Values for Tax Category 'CA' ======================
	 * Code | Value ====================== Books | 6% -----------+------------ DVDs | 6% ---------------------- SHIPPING | 6% TAX |
	 * ======================
	 */
	private List<TaxJurisdiction> getTaxJurisdictionsListForCA() {
		return getCaTaxJurisdictionsList();
	}

	/**
	 * Tax Jurisdiction - US. Match strategy - matches the country code. Tax Values for Tax Category 'US' ====================== Code | Value
	 * ====================== Books | 10% -----------+------------ DVDs | 10% ---------------------- SHIPPING | 5% TAX | ======================
	 *
	 * @return List of {@link TaxJurisdiction}
	 */
	public static List<TaxJurisdiction> getTaxJurisdictionsListForUS() {
		List<TaxJurisdiction> jurisdictions = new ArrayList<TaxJurisdiction>();
		TaxJurisdiction taxJurisdictionUSA = new TaxJurisdictionImpl();
		taxJurisdictionUSA.setPriceCalculationMethod(TaxJurisdiction.PRICE_CALCULATION_EXCLUSIVE);
		taxJurisdictionUSA.setRegionCode("US");
		taxJurisdictionUSA.setGuid("1002");

		// category
		TaxCategory taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY);
		taxCategory.setName("US");

		TaxRegion taxRegion = new TaxRegionImpl();

		Map<String, TaxValue> taxValueMap = new HashMap<String, TaxValue>();

		TaxValue taxValue = new TaxValueImpl();
		final TaxCode booksTaxCode = new TaxCodeImpl();
		booksTaxCode.setCode(SALES_TAX_CODE_BOOKS);
		booksTaxCode.setGuid(SALES_TAX_CODE_BOOKS);
		taxValue.setTaxCode(booksTaxCode);
		taxValue.setTaxValue(BigDecimal.TEN);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		final TaxCode dvdTaxCode = new TaxCodeImpl();
		dvdTaxCode.setCode(SALES_TAX_CODE_DVDS);
		dvdTaxCode.setGuid(SALES_TAX_CODE_DVDS);
		taxValue.setTaxCode(dvdTaxCode);
		taxValue.setTaxValue(BigDecimal.TEN);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		final TaxCode shippingTaxCode = new TaxCodeImpl();
		shippingTaxCode.setCode(TaxCode.TAX_CODE_SHIPPING);
		shippingTaxCode.setGuid(TaxCode.TAX_CODE_SHIPPING);
		taxValue.setTaxCode(shippingTaxCode);
		taxValue.setTaxValue(new BigDecimal("5"));

		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxRegion.setTaxValuesMap(taxValueMap);
		taxRegion.setRegionName("US");

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdictionUSA.addTaxCategory(taxCategory);
		jurisdictions.add(taxJurisdictionUSA);

		return jurisdictions;
	}

	/**
	 * Tests isCartItemRemoved().
	 */
	@Test
	public void testIsCartItemRemoved() {
		ShoppingCart shoppingCart = getShoppingCart();

		ShoppingItem cartItemToRemove = shoppingCart.getCartItems().iterator().next();

		assertFalse(shoppingCart.isCartItemRemoved(cartItemToRemove.getProductSku().getSkuCode()));

		shoppingCart.removeCartItem(cartItemToRemove.getUidPk());

		assertTrue(shoppingCart.isCartItemRemoved(cartItemToRemove.getProductSku().getSkuCode()));
	}

	/**
	 * Tests that a hasSubtotalDiscount() works as expected.
	 */
	@Test
	public void testHasSubtotalDiscountWithZeroValue() {
		shoppingCart.setSubtotalDiscount(BigDecimal.ZERO.setScale(2), RULE_ID, ACTION_ID);
		assertFalse("The discount is 0 and therefore no discount exists", shoppingCart.hasSubtotalDiscount());

	}

	/**
	 * Tests that a hasSubtotalDiscount() cannot be set to null.
	 */
	@Test
	public void testSetSubtotalDiscountNullValue() {
		try {
			shoppingCart.setSubtotalDiscount(null, RULE_ID, ACTION_ID);
			fail("should not be possible to set discount to null");
		} catch (EpServiceException exc) {
			assertNotNull(exc);
		}
		assertFalse("The discount was not and therefore no discount exists", shoppingCart.hasSubtotalDiscount());
		assertNotNull(shoppingCart.getSubtotalDiscount());
	}

	/**
	 * Tests that subtotal discount can be set to some value.
	 */
	@Test
	public void testSubtotalDiscountNonZeroValue() {
		applyTaxCalculationResult(shoppingCart);

		assertTrue(BigDecimal.ZERO.compareTo(shoppingCart.getSubtotal()) < 0);

		shoppingCart.setSubtotalDiscount(BigDecimal.ONE, RULE_ID, ACTION_ID);
		assertTrue("The discount is 1 and therefore discount exists", shoppingCart.hasSubtotalDiscount());
	}

	/**
	 *
	 */
	private TaxCalculationResult applyTaxCalculationResult(final ShoppingCart shoppingCart) {
		final TaxCalculationResultImpl taxCalculationResult = new TaxCalculationResultImpl();
		final Currency currency = shoppingCart.getCurrency();
		taxCalculationResult.setDefaultCurrency(currency);
		taxCalculationResult.setBeforeTaxSubTotalWithoutDiscount(MoneyFactory.createMoney(BigDecimal.TEN, currency));
		taxCalculationResult.setBeforeTaxSubTotal(MoneyFactory.createMoney(new BigDecimal("9"), currency));
		shoppingCart.setTaxCalculationResult(taxCalculationResult);

		return taxCalculationResult;
	}

	/**
	 * Tests the getAllItems() method for not being modifiable.
	 */
	@Test
	public void testGetAllItems() {
		assertTrue(shoppingCart.getAllItems().isEmpty());

		try {
			shoppingCart.getAllItems().clear();
			fail("The items collection should be unmodifiable");
		} catch (Exception exc) {
			assertNotNull(exc);
		}
	}

	/**
	 * Tests getLocalizedTaxMap() and that it returns sorted map of tax category name to tax value.
	 */
	@Test
	public void testLocalizedTaxMap() {
		shoppingCart.setLocale(Locale.UK);
		TaxCalculationResult result = applyTaxCalculationResult(shoppingCart);
		Money amount = MoneyFactory.createMoney(BigDecimal.TEN, Currency.getInstance(Locale.UK));
		TaxCategory taxCategory = newTaxCategory("c123");
		result.addTaxValue(taxCategory, amount);
		TaxCategory taxCategory2 = newTaxCategory("b123");
		result.addTaxValue(taxCategory2, amount);

		TaxCategory taxCategory3 = newTaxCategory("a123");
		result.addTaxValue(taxCategory3, amount);

		Map<String, Money> localizedMap = shoppingCart.getLocalizedTaxMap();

		final Iterator<Entry<String, Money>> taxIterator = localizedMap.entrySet().iterator();

		assertEquals("a123", taxIterator.next().getKey());
		assertEquals("b123", taxIterator.next().getKey());
		assertEquals("c123", taxIterator.next().getKey());
	}

	/**
	 */
	private TaxCategory newTaxCategory(final String displayName) {
		TaxCategory taxCategory = new TaxCategoryImpl();
		LocalizedProperties localizedProperties = new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = -1892978990192008917L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		};
		localizedProperties.setValue(TaxCategory.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.UK, displayName);

		taxCategory.setLocalizedProperties(localizedProperties);
		return taxCategory;
	}

// FIXME: When promotions are once again considered in the context of ShoppingItem, these tests must ensure that
	// cart promotions are cleared and refired when items in the cart change. We probably should also fix it so
	//that firing cart promotion rules does not also fire catalog promotion rules
//	/**
//	 * Tests that when a SKU is added to the cart, computed prices on all SKUs currently in the cart are cleared.
//	 */
//	public void testClearComputedPriceOnAllSKUsInTheCart() {
//		ShoppingCart cart = getEmptyShoppingCart();
//
//		ProductSku sku1 = getProductSku();
//		sku1.addCatalogPrice(getCatalog(), getPrice(Currency.getInstance(Locale.CANADA), new BigDecimal("80"), null));
//		cart.addCartItem(sku1, 1);
//
//		// set computed price
//		final MoneyImpl computedPrice = MoneyFactory.createMoney(BigDecimal.TEN, Currency.getInstance(Locale.CANADA));
//
//		Price price = sku1.getCatalogSkuPrice(getCatalog(), Currency.getInstance(Locale.CANADA));
//		price.setComputedPrice(computedPrice);
//
//		ProductSku sku2 = getProductSku();
//		sku2.addCatalogPrice(getCatalog(), getPrice(Currency.getInstance(Locale.CANADA), new BigDecimal("90"), null));
//
//		// test computed price is 10
//		assertEquals(computedPrice.getAmount(), price.getComputedPrice().getAmount());
//		cart.addCartItem(sku2, 1);
//
//		// test after adding new product to shopping cart, the computed price has been cleared.
//		assertNull(price.getComputedPrice());
//	}
//
//	/**
//	 * Tests that when a multiSKU product is added to the cart, computed prices on all SKUs currently in the cart are cleared.
//	 */
//	public void testClearComputedPriceOnAllMultiSKUProductsInTheCart() {
//		ShoppingCart cart = getEmptyShoppingCart();
//
//		ProductSku sku1 = getProductSku();
//		sku1.addCatalogPrice(getCatalog(), getPrice(Currency.getInstance(Locale.CANADA), new BigDecimal("80"), null));
//		cart.addCartItem(sku1, 1);
//
//		// set computed price
//		final MoneyImpl computedPrice = MoneyFactory.createMoney(BigDecimal.TEN, Currency.getInstance(Locale.CANADA));
//
//		Price price = sku1.getCatalogSkuPrice(getCatalog(), Currency.getInstance(Locale.CANADA));
//		price.setComputedPrice(computedPrice);
//
//		ProductSku sku2 = getProductSku();
//		sku2.addCatalogPrice(getCatalog(), getPrice(Currency.getInstance(Locale.CANADA), new BigDecimal("90"), null));
//		sku2.setProduct(sku1.getProduct());
//
//		ProductSku sku3 = getProductSku();
//		sku3.addCatalogPrice(getCatalog(), getPrice(Currency.getInstance(Locale.CANADA), new BigDecimal("100"), null));
//
//		// test computed price is 10
//		assertEquals(computedPrice.getAmount(), price.getComputedPrice().getAmount());
//		cart.addCartItem(sku3, 1);
//
//		// test after adding new product to shopping cart, the computed price has been cleared.
//		assertNull(price.getComputedPrice());
//	}

	/**
	 * Creates CA tax jurisdiction list of the single jurisdiction. That jurisdiction is:<br>
	 * Region code: CA<br>
	 * Country category: GST<br>
	 * Tax Region: CA<br>
	 * Tax Values: SHIPPING==6%, GOODS==6%<br>
	 *
	 * Subcountry category: PST<br>
	 * Tax Region: BC<br>
	 * Tax Values: SHIPPING==7%, GOODS==7%<br>
	 *
	 * Subcountry category: ANOTHER_CATEGORY<br>
	 * Tax Region: VANCOUVER<br>
	 * Tax Values: SHIPPING==7%, GOODS==7%<br>
	 *
	 * The last category musn't be taken into account while calculating taxes.
	 * This category doesn't match the shipping address.
	 * @return List
	 */
	public List<TaxJurisdiction> getCaTaxJurisdictionsList() {
		List<TaxJurisdiction> list = new ArrayList<TaxJurisdiction>();
		TaxJurisdiction taxJurisdiction = new TaxJurisdictionImpl();
		taxJurisdiction.setPriceCalculationMethod(TaxJurisdiction.PRICE_CALCULATION_EXCLUSIVE);
		taxJurisdiction.setRegionCode(REGION_CODE_CA);
		taxJurisdiction.setGuid("1001");

		// 1) category
		TaxCategory taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY);
		taxCategory.setName(GST_TAX_CODE);

		TaxRegion taxRegion = new TaxRegionImpl();

		Map<String, TaxValue> taxValueMap = new HashMap<String, TaxValue>();

		TaxValue taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_GOODS));
		taxValue.setTaxValue(GST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_BOOKS));
		taxValue.setTaxValue(GST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_DVDS));
		taxValue.setTaxValue(GST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxValue.setTaxValue(GST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxRegion.setTaxValuesMap(taxValueMap);
		taxRegion.setRegionName(REGION_CODE_CA);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		// 2) category
		taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_SUBCOUNTRY);
		taxCategory.setName(PST_TAX_CODE);

		taxValueMap = new HashMap<String, TaxValue>();

		taxRegion = new TaxRegionImpl();
		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_GOODS));
		taxValue.setTaxValue(PST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxValue.setTaxValue(PST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxRegion.setTaxValuesMap(taxValueMap);

		taxRegion.setRegionName(REGION_CODE_BC);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);
		// 3) category - musn't be used
		taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_SUBCOUNTRY);
		taxCategory.setName("ANOTHER_CATEGORY");

		taxValueMap = new HashMap<String, TaxValue>();

		taxRegion = new TaxRegionImpl();
		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_GOODS));
		taxValue.setTaxValue(PST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxValue.setTaxValue(PST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxRegion.setTaxValuesMap(taxValueMap);

		taxRegion.setRegionName("VANCOUVER");

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		list.add(taxJurisdiction);
		return list;
	}

	private GiftCertificate mockGiftCertificate(final Store store, final BigDecimal amount, final Currency currency) {
		final GiftCertificate certificate = context.mock(GiftCertificate.class, "GiftCertificate-" + getUniqueUid());
		context.checking(new Expectations() {
			{
				allowing(certificate).getStore();
				will(returnValue(store));
				allowing(certificate).getCurrencyCode();
				will(returnValue(currency.getCurrencyCode()));
				allowing(certificate).getPurchaseAmount();
				will(returnValue(amount));
			}
		});
		return certificate;
	}
}

