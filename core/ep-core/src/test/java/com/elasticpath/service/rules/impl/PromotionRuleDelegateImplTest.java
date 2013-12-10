/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;  // NOPMD

import static com.elasticpath.domain.shipping.impl.ShippingCostTestDataFactory.aCostCalculationParam;
import static com.elasticpath.domain.shipping.impl.ShippingCostTestDataFactory.someCalculationParams;
import static com.elasticpath.test.factory.ShoppingCartStubBuilder.aCart;
import static com.elasticpath.test.factory.ShoppingCartStubBuilder.aShoppingItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.impl.AttributeGroupImpl;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerGroupImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shipping.impl.FixedPriceMethodImpl;
import com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartMementoImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.service.catalogview.ProductRetrieveStrategy;
import com.elasticpath.service.rules.PromotionRuleExceptions;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/** Test cases for <code>PromotionRuleDelegateImpl</code>. */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class PromotionRuleDelegateImplTest {

	private static final long PRODUCT_UID = 1234L;

	private static final String PRODUCT_CODE = "1";

	private static final String BRAND_CODE = "brandCode";

	private static final long CUSTOMER_GROUP = 300L;

	private static final long CUSTOMER_UID = 100L;

	private static final long RULE_UID = 123L;

	private static final long ACTION_UID = 456L;

	private static final int TIME_UNIT = 1000000;

	private static final String PRODUCT1_CODE = "2";

	private static final String CATEGORY1_CODE = "123";

	private static final String CAD = "CAD";

	private static final Currency CANADIAN = Currency.getInstance(CAD);

	private static final String SKU_CODE1 = "SkuCode1";

	private static final String AT_LEAST_QUANTIFIER = "AT_LEAST";

	private static final String EXACTLY_QUANTIFIER = "EXACTLY";

	private PromotionRuleDelegateImpl ruleDelegate;

	private static final int QTY_3 = 3;

	private static final int QTY_5 = 5;

	private static final int QTY_10 = 10;

	private static final String DUMMY_EXCEPTION_STR = "CategoryCodes:ProductCodes:ProductSkuCodes:";

	private ProductRetrieveStrategy productRetrieveStrategy;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepare for each test.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PROMOTION_RULE_EXCEPTIONS, PromotionRuleExceptionsImpl.class);

		ruleDelegate = new PromotionRuleDelegateImpl();

		productRetrieveStrategy = context.mock(ProductRetrieveStrategy.class);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test that if a product is in the given category and not excluded from the rules,
	 * and we want it to be in the category, then catalogProductInCategory will return true.
	 */
	@Test
	public void testCatalogProductInCategoryNotExcludedTrue() {
		ruleDelegate.setProductRetrieveStrategy(productRetrieveStrategy);
		ProductInCategoryTestFixture testData = new ProductInCategoryTestFixture();

		final boolean productShouldBeInTheCategory = true;
		assertProductInCategory(testData, productShouldBeInTheCategory);
	}

	/**
	 * Test that if a product is in the given category and not excluded from the rules,
	 * and we DO NOT want it to be in the category, then catalogProductInCategory will return false.
	 */
	@Test
	public void testCatalogProductInCategoryNotExcludedFalse() {
		ruleDelegate.setProductRetrieveStrategy(productRetrieveStrategy);
		ProductInCategoryTestFixture testData = new ProductInCategoryTestFixture();

		final boolean productShouldBeInTheCategory = false;
		assertProductNotInCategory(testData, productShouldBeInTheCategory);
	}

	/**
	 * Test that if a product is in the given category but is excluded from the rules,
	 * and we want it to be in the category, then catalogProductInCategory will return false.
	 */
	@Test
	public void testCatalogProductInCategoryExcludedFalse() {
		ruleDelegate = new PromotionRuleDelegateImpl() {
			@Override
			PromotionRuleExceptions getPromotionRuleExceptions(final String exceptionStr) {
				return null; //not testing this
			}
			@Override
			boolean isProductExcludedFromRule(final Product product, final PromotionRuleExceptions ruleExceptions) {
				return true;
			}
			@Override
			boolean isProductInCategory(final Product product, final String categoryCode) {
				return true;
			}
		};
		final boolean productShouldBeInTheCategory = true;
		ProductInCategoryTestFixture testData = new ProductInCategoryTestFixture();
		assertProductNotInCategory(testData, productShouldBeInTheCategory);
	}

	/**
	 * Test that if a product is in the given category but is excluded from the rules,
	 * and we DO NOT want it to be in the category, then catalogProductInCategory will return true.
	 */
	@Test
	public void testCatalogProductInCategoryExcludedTrue() {
		ruleDelegate = new PromotionRuleDelegateImpl() {
			@Override
			PromotionRuleExceptions getPromotionRuleExceptions(final String exceptionStr) {
				return null; //not testing this
			}
			@Override
			boolean isProductExcludedFromRule(final Product product, final PromotionRuleExceptions ruleExceptions) {
				return true;
			}
			@Override
			boolean isProductInCategory(final Product product, final String categoryCode) {
				return true;
			}
		};
		final boolean productShouldBeInTheCategory = false;
		ProductInCategoryTestFixture testData = new ProductInCategoryTestFixture();
		assertProductInCategory(testData, productShouldBeInTheCategory);
	}

	/**
	 * Test that if a product is NOT in the given category, but we want it to be, then catalogProductInCategory will
	 * return false.
	 */
	@Test
	public void testCatalogProductInCategoryFalse() {
		ruleDelegate = new PromotionRuleDelegateImpl() {
			@Override
			PromotionRuleExceptions getPromotionRuleExceptions(final String exceptionStr) {
				return null; //not testing this
			}
			@Override
			boolean isProductExcludedFromRule(final Product product, final PromotionRuleExceptions ruleExceptions) {
				return false;
			}
			@Override
			boolean isProductInCategory(final Product product, final String categoryCode) {
				return false;
			}
		};
		final boolean productShouldBeInTheCategory = true;
		ProductInCategoryTestFixture testData = new ProductInCategoryTestFixture();
		assertProductNotInCategory(testData, productShouldBeInTheCategory);
	}

	/**
	 * Test that if a product is NOT in the given category, and we don't want it to be,
	 * then catalogProductInCategory will return true.
	 */
	@Test
	public void testCatalogProductInCategoryTrue() {
		ruleDelegate = new PromotionRuleDelegateImpl() {
			@Override
			PromotionRuleExceptions getPromotionRuleExceptions(final String exceptionStr) {
				return null; //not testing this
			}
			@Override
			boolean isProductExcludedFromRule(final Product product, final PromotionRuleExceptions ruleExceptions) {
				return false;
			}
			@Override
			boolean isProductInCategory(final Product product, final String categoryCode) {
				return false;
			}
		};
		final boolean productShouldBeInTheCategory = false;
		ProductInCategoryTestFixture testData = new ProductInCategoryTestFixture();
		assertProductInCategory(testData, productShouldBeInTheCategory);
	}

	/**
	 * Test that if a product is in the given category and not excluded from the rules,
	 * and we want it to be in the category, but product excluded from discount,
	 * then catalogProductInCategory will return false.
	 */
	@Test
	public void testCatalogProductInCategoryProductExcludedFromDiscount() {
		ruleDelegate = new PromotionRuleDelegateImpl() {
			@Override
			PromotionRuleExceptions getPromotionRuleExceptions(final String exceptionStr) {
				return null; //not testing this
			}
			@Override
			boolean isProductExcludedFromRule(final Product product, final PromotionRuleExceptions ruleExceptions) {
				return false;
			}
			@Override
			boolean isProductInCategory(final Product product, final String categoryCode) {
				return true;
			}
		};
		final boolean productShouldBeInTheCategory = true;
		ProductInCategoryTestFixture testData = new ProductInCategoryTestFixture();
		testData.getProduct().getProductType().setExcludedFromDiscount(true);
		assertProductNotInCategory(testData, productShouldBeInTheCategory);
	}

	private void assertProductInCategory(final ProductInCategoryTestFixture testData, final boolean productShouldBeInTheCategory) {
		assertTrue(ruleDelegate.catalogProductInCategory(
				testData.getProduct(), productShouldBeInTheCategory, testData.getCategory().getCompoundGuid(), null));
	}

	private void assertProductNotInCategory(final ProductInCategoryTestFixture testData, final boolean productShouldBeInTheCategory) {
		assertFalse(ruleDelegate.catalogProductInCategory(
				testData.getProduct(), productShouldBeInTheCategory, testData.getCategory().getCompoundGuid(), null));
	}

	/**
	 * Fixture for productInCategory tests, holds the appropriate catalog, category and products.
	 */
	private class ProductInCategoryTestFixture {
		private final Catalog catalog;
		private final Category category;
		private final Product product;

		public ProductInCategoryTestFixture() {
			expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER, FetchGroupLoadTunerImpl.class);
			product = new ProductImpl();
			getProduct().setProductType(new ProductTypeImpl());
			context.checking(new Expectations() { {
				allowing(productRetrieveStrategy).retrieveProduct(with(getProduct().getUidPk()), with(any(FetchGroupLoadTuner.class)));
				will(returnValue(getProduct()));
			} });
			catalog = new CatalogImpl();
			catalog.setCode("irrelevant catalog code");
			category = new CategoryImpl();
			category.setCatalog(catalog);
			category.setCode("irrelevent catagory code");
			product.addCategory(category);
		}

		public Catalog getCatalog() {
			return catalog;
		}

		public Category getCategory() {
			return category;
		}

		public Product getProduct() {
			return product;
		}

	}

	/**
	 * Test the product is condition.
	 */
	@Test
	public void testProductIs() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_GROUP, AttributeGroupImpl.class);

		ProductType productType = new ProductTypeImpl();
		productType.initialize();

		Product product = new ProductImpl();
		product.setUidPk(PRODUCT_UID);
		product.setCode(PRODUCT_CODE);
		product.setProductType(productType);

		assertTrue(ruleDelegate.catalogProductIs(product, true, PRODUCT_CODE, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.catalogProductIs(product, true, PRODUCT_CODE + 1, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.catalogProductIs(product, false, PRODUCT_CODE, DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.catalogProductIs(product, false, PRODUCT_CODE + 1, DUMMY_EXCEPTION_STR));
	}

	/**
	 * Test the brand is condition.
	 */
	@Test
	public void testBrandIs() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_GROUP, AttributeGroupImpl.class);

		ProductType productType = new ProductTypeImpl();
		productType.initialize();

		Brand brand = new BrandImpl();
		brand.setCode(BRAND_CODE);

		Product product = new ProductImpl();
		product.setUidPk(PRODUCT_UID);
		product.setBrand(brand);
		product.setProductType(productType);

		assertTrue(ruleDelegate.catalogBrandIs(product, true, BRAND_CODE, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.catalogBrandIs(product, true, BRAND_CODE + 1, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.catalogBrandIs(product, false, BRAND_CODE, DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.catalogBrandIs(product, false, BRAND_CODE + 1, DUMMY_EXCEPTION_STR));
	}

	/**
	 * Test for: Reduces the price of a catalog item by the specified percentage if the currency matches.
	 */
	@Test
	public void testApplyCatalogCurrencyDiscountPercent() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		List <Price> prices = new ArrayList <Price>();
		Price price1 = get10Cad();
		Price price2 = get10Cad();
		prices.add(price1);
		prices.add(price2);
		Set <Long> tracker = new HashSet <Long>();
		ruleDelegate.applyCatalogCurrencyDiscountAmount(tracker , 1, prices, CAD, CAD, "5.00");
		assertEquals(0, MoneyFactory.createMoney(new BigDecimal("5"), CANADIAN).compareTo(price1.getComputedPrice(1)));
		assertEquals(0, MoneyFactory.createMoney(new BigDecimal("5"), CANADIAN).compareTo(price2.getComputedPrice(1)));
		assertTrue(tracker.contains(1L));
		assertTrue(tracker.size() == 1);
	}



	/**
	 *
	 */
	@Test
	public void testDiscountPriceTierByAmount() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		Price price = get10Cad();
		// The shopping cart's currency is CAD, so the discount shall be applied
		ruleDelegate.discountPriceByAmount(new BigDecimal("5"), price);
		assertEquals(0, MoneyFactory.createMoney(new BigDecimal("5"), CANADIAN).compareTo(price.getComputedPrice(1)));
	}

	/**
	 *
	 */
	@Test
	public void testDiscountPriceTierByPercent() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		Price price = get10Cad();
		// The shopping cart's currency is CAD, so the discount shall be applied
		BigDecimal discountPercent = ruleDelegate.setDiscountPercentScale(new BigDecimal("50"));
		ruleDelegate.discountPriceByPercent(discountPercent, price);
		assertEquals(new BigDecimal("5.00"), price.getComputedPrice(1).getAmount());
	}

	private Price get10Cad() {
		Price price = new PriceImpl();
		price.setCurrency(CANADIAN);
		PriceTier priceTier = new PriceTierImpl();
		priceTier.setMinQty(1);
		priceTier.setListPrice(BigDecimal.TEN);
		price.addOrUpdatePriceTier(priceTier);
		return price;
	}


	/**
	 * Test for: Checks if the shopping cart subtotal is at least equal to the specified amount.
	 * Amount in this case is $40.00
	 */
	@Test
	public void testCartSubtotalAtLeast() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		Store store = new StoreImpl();
		Product product = new ProductImpl();

		ShoppingCart shoppingCart = aCart(context)
			.with(aShoppingItem(context).costing(new BigDecimal("40.00")).withProduct(product).thatsDiscountable())
			.forStore(store)
			.withSubtotal(new BigDecimal("40.00"))
			.withSubtotalDiscount(new BigDecimal("0.00"))
			.build();

		assertTrue(ruleDelegate.cartSubtotalAtLeast(shoppingCart, "0", DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.cartSubtotalAtLeast(shoppingCart, "39.99", DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.cartSubtotalAtLeast(shoppingCart, "40", DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.cartSubtotalAtLeast(shoppingCart, "40.01", DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.cartSubtotalAtLeast(shoppingCart, "500", DUMMY_EXCEPTION_STR));
	}

	/**
	 * Test shipping discount amount.
	 */
	@Test
	public void testApplyShippingDiscountAmount() {

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		final ShoppingCart shoppingCart = aCart(context)
		.withCurrency(Currency.getInstance(CAD))
		.build();

		final ShippingServiceLevel shippingServiceLevel = context.mock(ShippingServiceLevel.class);

		String discountAmount = "1";
		final Money discountAmountMoney = MoneyFactory.createMoney(new BigDecimal(discountAmount), Currency.getInstance(CAD));
		final String serviceLevelCode = "SSLCode001";

		// logic flow driver
		context.checking(new Expectations() { {
			oneOf(shippingServiceLevel).getCode(); will(returnValue(serviceLevelCode));

			oneOf(shoppingCart).getSelectedShippingServiceLevel();
			will(returnValue(shippingServiceLevel));
		} });

		// Actual test expectations
		context.checking(new Expectations() { {

			// The rule must set the shipping discount on the shipping service level
			oneOf(shippingServiceLevel).setShippingDiscount(with(discountAmountMoney));
			will(returnValue(true));

			// The rule must be recorded as being applied to the cart
			oneOf(shoppingCart).shippingRuleApplied(RULE_UID, ACTION_UID, BigDecimal.ONE);
		} });

		this.ruleDelegate.applyShippingDiscountAmount(shoppingCart, RULE_UID, ACTION_UID, discountAmount, serviceLevelCode);

	}
	/**
	 * Test shipping discount amount for selected shipping level in the shopping cart.
	 */
	@Test
	public void testApplyShippingDiscountPercent() {

		//Given
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		final ShoppingCart shoppingCart = aCart(context)
		.withCurrency(Currency.getInstance(CAD))
		.build();

		final String serviceLevelCode = "SSLCode002";
		final String discountPercent = "25";

		final Money discountedShippingCost = MoneyFactory.createMoney(new BigDecimal("2.50"), Currency.getInstance(CAD));

		final ShippingCostCalculationMethod shippingCostCalculationMethod
			= createFixedPriceShippingCostCalculationMethod(BigDecimal.TEN,  Currency.getInstance(CAD));

		final ShippingServiceLevel shippingServiceLevel = new ShippingServiceLevelImpl();
		shippingServiceLevel.setCode(serviceLevelCode);
		shippingServiceLevel.setShippingCostCalculationMethod(shippingCostCalculationMethod);
		shippingServiceLevel.calculateRegularPriceShippingCost(shoppingCart);


		final List<ShoppingItem> items = new ArrayList<ShoppingItem>();
		final ShoppingItem cartItem = context.mock(ShoppingItem.class);
		items.add(cartItem);

		final ProductSku sku = context.mock(ProductSku.class);

		// logic flow driver
		context.checking(new Expectations() { {

			oneOf(shoppingCart).getSelectedShippingServiceLevel();
			will(returnValue(shippingServiceLevel));

			oneOf(shoppingCart).getApportionedLeafItems();
			will(returnValue(items));

			oneOf(cartItem).getProductSku(); will(returnValue(sku));

			oneOf(sku).isShippable(); will(returnValue(true));

		} });

		// Actual test expectations
		context.checking(new Expectations() { {
			// The rule must be recorded as being applied to the cart
			final int expectedScale = 4;
			oneOf(shoppingCart).shippingRuleApplied(RULE_UID, ACTION_UID, discountedShippingCost.getAmount().setScale(expectedScale));
		} });

		//When
		this.ruleDelegate.applyShippingDiscountPercent(shoppingCart, RULE_UID, ACTION_UID, discountPercent, serviceLevelCode);

		//Then
		assertEquals(shippingServiceLevel.getShippingCost().getAmount(), new BigDecimal("7.50"));

	}


	/**
	 * Test shipping discount amount for shipping level in the shopping cart that is not selected but part of the shipping list.
	 */
	@Test
	public void testApplyShippingDiscountPercentForShippingLevelNotSelectedInShoppingCart() {
		//Given
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		final ShoppingCart shoppingCart = aCart(context)
		.withCurrency(Currency.getInstance(CAD))
		.build();

		final String serviceLevelCode1 = "SSLCode001";
		final String serviceLevelCode2 = "SSLCode002";
		final String discountPercent = "25";

		final ShippingCostCalculationMethod shippingCostCalculationMethod1
			= createFixedPriceShippingCostCalculationMethod(BigDecimal.TEN, Currency.getInstance(CAD));

		final ShippingServiceLevel shippingServiceLevel1 = new ShippingServiceLevelImpl();
		shippingServiceLevel1.setCode(serviceLevelCode1);
		shippingServiceLevel1.setShippingCostCalculationMethod(shippingCostCalculationMethod1);

		final ShippingCostCalculationMethod shippingCostCalculationMethod2
			= createFixedPriceShippingCostCalculationMethod(new BigDecimal("100.00"), Currency.getInstance(CAD));

		final ShippingServiceLevel shippingServiceLevel2 = new ShippingServiceLevelImpl();
		shippingServiceLevel2.setCode(serviceLevelCode2);
		shippingServiceLevel2.setShippingCostCalculationMethod(shippingCostCalculationMethod2);

		final List<ShippingServiceLevel> shippingServiceList = new ArrayList<ShippingServiceLevel>();
		shippingServiceList.add(shippingServiceLevel1);
		shippingServiceList.add(shippingServiceLevel2);

		final List<ShoppingItem> items = new ArrayList<ShoppingItem>();
		final ShoppingItem cartItem = context.mock(ShoppingItem.class);
		items.add(cartItem);

		final ProductSku sku = context.mock(ProductSku.class);

		// logic flow driver
		context.checking(new Expectations() { {

			oneOf(shoppingCart).getSelectedShippingServiceLevel();
			will(returnValue(shippingServiceLevel1));

			oneOf(shoppingCart).getShippingServiceLevelList();
			will(returnValue(shippingServiceList));

			oneOf(shoppingCart).getApportionedLeafItems();
			will(returnValue(items));

			oneOf(cartItem).getProductSku(); will(returnValue(sku));

			oneOf(sku).isShippable(); will(returnValue(true));

		} });

		//When
		this.ruleDelegate.applyShippingDiscountPercent(shoppingCart, RULE_UID, ACTION_UID, discountPercent, serviceLevelCode2);

		//Then
		assertEquals(shippingServiceLevel2.getShippingCost().getAmount(), new BigDecimal("75.00"));

	}



	/**
	 * Test for: Checks if the currency of a shopping cart matches the specified currency code.
	 */
	@Test
	public void testCartCurrencyMatches() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.SHOPPING_CART_MEMENTO, ShoppingCartMementoImpl.class);

		ShoppingCart shoppingCart = aCart(context)
		.withCurrency(Currency.getInstance(CAD))
		.build();

		assertTrue(ruleDelegate.cartCurrencyMatches(shoppingCart, CAD));
		assertFalse(ruleDelegate.cartCurrencyMatches(shoppingCart, "USD"));
	}

	/**
	 * Test for: Checks if the cart contains the specified sku.
	 */
	@Test
	public void testCartContainsSku() {
		final ShoppingCart shoppingCart = aCart(context)
			.with(aShoppingItem(context).withSkuCode(SKU_CODE1).withQuantity(QTY_5).withProduct(null).thatsDiscountable())
			.build();

		assertTrue(ruleDelegate.cartContainsSku(shoppingCart, SKU_CODE1, AT_LEAST_QUANTIFIER, 1));
		assertTrue(ruleDelegate.cartContainsSku(shoppingCart, SKU_CODE1, AT_LEAST_QUANTIFIER, QTY_5));
		assertTrue(ruleDelegate.cartContainsSku(shoppingCart, SKU_CODE1, EXACTLY_QUANTIFIER, QTY_5));
		assertFalse(ruleDelegate.cartContainsSku(shoppingCart, SKU_CODE1, EXACTLY_QUANTIFIER, QTY_5 - 1));
		assertFalse(ruleDelegate.cartContainsSku(shoppingCart, SKU_CODE1, AT_LEAST_QUANTIFIER, QTY_5 + 1));
	}

	/**
	 * Test for: Checks if the cart contains any sku.
	 */
	@Test
	public void testCartContainsAnySku() {
		Store store = new StoreImpl();
		store.setCatalog(new CatalogImpl());

		Product product = new ProductImpl();

		final ShoppingCart shoppingCart = aCart(context)
			.with(aShoppingItem(context).withSkuCode(SKU_CODE1).withQuantity(QTY_10).withProduct(product).thatsDiscountable())
			.forStore(store)
			.build();

		assertTrue(ruleDelegate.cartContainsAnySku(shoppingCart, AT_LEAST_QUANTIFIER, 1, DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.cartContainsAnySku(shoppingCart, AT_LEAST_QUANTIFIER, QTY_5, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.cartContainsAnySku(shoppingCart, EXACTLY_QUANTIFIER, QTY_5, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.cartContainsAnySku(shoppingCart, EXACTLY_QUANTIFIER, QTY_5 - 1, DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.cartContainsAnySku(shoppingCart, AT_LEAST_QUANTIFIER, QTY_5 + 1, DUMMY_EXCEPTION_STR));
	}

	/**
	 * Test for: Checks if the cart contains the specified product.
	 */
	@Test
	public void testCartContainsProduct() {
		Product product = new ProductImpl();
		product.setCode(PRODUCT1_CODE);

		final ShoppingCart shoppingCart = aCart(context)
			.with(aShoppingItem(context).withSkuCode(SKU_CODE1).withQuantity(QTY_5).withProduct(product).thatsDiscountable())
			.build();

		assertTrue(ruleDelegate.cartContainsProduct(shoppingCart, PRODUCT1_CODE, AT_LEAST_QUANTIFIER, 1, DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.cartContainsProduct(shoppingCart, PRODUCT1_CODE, AT_LEAST_QUANTIFIER, QTY_5, DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.cartContainsProduct(shoppingCart, PRODUCT1_CODE, EXACTLY_QUANTIFIER, QTY_5, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.cartContainsProduct(shoppingCart, PRODUCT1_CODE, EXACTLY_QUANTIFIER, QTY_5 - 1, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.cartContainsProduct(shoppingCart, PRODUCT1_CODE, AT_LEAST_QUANTIFIER, QTY_5 + 1, DUMMY_EXCEPTION_STR));
	}

	/**
	 * Test for: Checks if the cart contains the specified product.
	 */
	@Test
	public void testCartContainsItemsofCategory() {
		Store store = new StoreImpl();
		store.setCatalog(new CatalogImpl());

		Product product = new ProductImpl();

		final ShoppingCart shoppingCart = aCart(context)
		.with(aShoppingItem(context).withSkuCode(SKU_CODE1).withQuantity(QTY_3 + QTY_5).withProduct(product).thatsDiscountable())
		.forStore(store)
		.build();

		ruleDelegate = new PromotionRuleDelegateImpl() {

			// Stub out this method
			@Override
			public boolean catalogProductInCategory(final Product product, final boolean isIn, final String categoryCode, final String exceptionStr) {
				return true;
			}
		};

		assertTrue(ruleDelegate.cartContainsItemsOfCategory(shoppingCart, CATEGORY1_CODE, AT_LEAST_QUANTIFIER, 1, DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.cartContainsItemsOfCategory(shoppingCart, CATEGORY1_CODE, AT_LEAST_QUANTIFIER, QTY_5 + QTY_3, DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.cartContainsItemsOfCategory(shoppingCart, CATEGORY1_CODE, EXACTLY_QUANTIFIER, QTY_5 + QTY_3, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate
				.cartContainsItemsOfCategory(shoppingCart, CATEGORY1_CODE, EXACTLY_QUANTIFIER, QTY_5 + QTY_3 - 1, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.cartContainsItemsOfCategory(shoppingCart, CATEGORY1_CODE, AT_LEAST_QUANTIFIER, QTY_5 + QTY_3 + 1,
				DUMMY_EXCEPTION_STR));
	}

	/**
	 * Test for: Checks if the customer is in a given group.
	 */
	@Test
	public void testCustomerInGroup() {
		final CustomerSession customerSession = context.mock(CustomerSession.class);
		final Shopper shopper = context.mock(Shopper.class);
		final Customer customer = context.mock(Customer.class);
		final List<CustomerGroup> customerGroups = new ArrayList<CustomerGroup>();

		context.checking(new Expectations() { {
			allowing(customerSession).getShopper();
			will(returnValue(shopper));

			allowing(shopper).getCustomer();
			will(returnValue(customer));

			allowing(customer).getCustomerGroups();
			will(returnValue(customerGroups));
		} });

		CustomerGroup customerGroup = new CustomerGroupImpl();
		customerGroup.setUidPk(CUSTOMER_GROUP);

		customerGroups.add(customerGroup);

		assertTrue(ruleDelegate.customerInGroup(customerSession, CUSTOMER_GROUP));
		assertFalse(ruleDelegate.customerInGroup(customerSession, CUSTOMER_GROUP - 1));
	}

	/**
	 * Null customers obviously can't be existing customers.
	 */
	@Test
	public void testIsExistingCustomerDoesntExistIfNull() {
		assertFalse(ruleDelegate.isExistingCustomer(null));
	}

	/**
	 * Non persisted users don't count as existing customers.
	 */
	@Test
	public void testIsExistingCustomerDoesntExistIfUsingNonPersistedUidPk() {
		final Customer customer = context.mock(Customer.class);
		context.checking(new Expectations() { {
			oneOf(customer).getUidPk(); will(returnValue(Long.valueOf(0)));
		} });
		assertFalse(ruleDelegate.isExistingCustomer(customer));
	}

	/**
	 * Registered customers are exiting customers.
	 */
	@Test
	public void testIsExistingCustomerDoesExitIfIsRegisteredCustomer() {
		final Customer customer = context.mock(Customer.class);
		context.checking(new Expectations() { {
			oneOf(customer).getUidPk(); will(returnValue(Long.valueOf(CUSTOMER_UID)));
			oneOf(customer).isAnonymous(); will(returnValue(false));
		} });
		assertTrue(ruleDelegate.isExistingCustomer(customer));
	}

	/**
	 * Anonymous customers don't count as existing customers.
	 */
	@Test
	public void testIsExistingCustomerDoesntExitIfIsAnonymousCustomer() {
		final Customer customer = context.mock(Customer.class);
		context.checking(new Expectations() { {
			oneOf(customer).getUidPk(); will(returnValue(Long.valueOf(CUSTOMER_UID)));
			oneOf(customer).isAnonymous(); will(returnValue(true));
		} });
		assertFalse(ruleDelegate.isExistingCustomer(customer));
	}


	/**
	 * Test for <code>checkDateRange()</code>.
	 */
	@Test
	public void testCheckDateRange() {
		Date currentDate = new Date();
		Date beforeNow = new Date(currentDate.getTime() - 1);
		Date afterNow = new Date(currentDate.getTime() + TIME_UNIT);

		assertTrue(ruleDelegate.checkDateRange(String.valueOf(beforeNow.getTime()), String.valueOf(afterNow.getTime())));
		assertTrue(ruleDelegate.checkDateRange(String.valueOf(beforeNow.getTime()), "0"));
		assertFalse(ruleDelegate.checkDateRange(String.valueOf(afterNow.getTime()), String.valueOf(beforeNow.getTime())));
		assertFalse(ruleDelegate.checkDateRange(String.valueOf(beforeNow.getTime() - TIME_UNIT), String.valueOf(beforeNow.getTime())));
		assertTrue(ruleDelegate.checkDateRange("0", "0"));
		assertTrue(ruleDelegate.checkDateRange("0", String.valueOf(afterNow.getTime())));
		assertFalse(ruleDelegate.checkDateRange("0", String.valueOf(beforeNow.getTime())));
	}

	/**
	 * Test for <code>checkEnabled()</code>.
	 */
	@Test
	public void testCheckEnabled() {
		assertTrue(ruleDelegate.checkEnabled("true"));
		assertFalse(ruleDelegate.checkEnabled("false"));
	}

	/**
	 * Test that an item that is not discountable is not considered eligible for promotion.
	 */
	@Test
	public void testCartItemEligibilityOfNonDiscountableItem() {
		ProductInCategoryTestFixture testData = new ProductInCategoryTestFixture();
		ShoppingItem cartItem = new ShoppingItemImpl();
		ProductSku sku = new ProductSkuImpl();
		sku.setProduct(testData.getProduct());
		cartItem.setProductSku(sku);

		PromotionRuleExceptions exceptions = new PromotionRuleExceptionsImpl();

		testData.getProduct().getProductType().setExcludedFromDiscount(true);
		assertFalse("Item should not be eligible", ruleDelegate.cartItemContributesToPromotionCondition(cartItem, testData.getCatalog(), exceptions));
	}

	private ShippingCostCalculationMethod createFixedPriceShippingCostCalculationMethod(final BigDecimal price, final Currency currency) {
		List< Currency > currencyList = new ArrayList< Currency >();
		final Currency currencyCAD = Currency.getInstance("CAD");
		final Currency currencyGermany = Currency.getInstance(Locale.GERMANY);
		currencyList.add(currencyCAD);
		currencyList.add(currencyGermany);

		final ShippingCostCalculationMethod shippingCostCalculationMethod = new FixedPriceMethodImpl();

		shippingCostCalculationMethod.setParameters(
				someCalculationParams(
						aCostCalculationParam(ShippingCostCalculationParametersEnum.FIXED_PRICE, price, currency)));


		return shippingCostCalculationMethod;
	}

} // NOPMD
