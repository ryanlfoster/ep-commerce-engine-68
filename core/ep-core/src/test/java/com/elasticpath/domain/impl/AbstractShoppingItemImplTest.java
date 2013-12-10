package com.elasticpath.domain.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
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
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceScheduleImpl;
import com.elasticpath.domain.catalog.impl.PricingSchemeImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemRecurringPrice;
import com.elasticpath.domain.shoppingcart.impl.CartItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemRecurringPriceImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemSimplePrice;
import com.elasticpath.domain.subscriptions.PaymentSchedule;
import com.elasticpath.domain.subscriptions.impl.PaymentScheduleImpl;
import com.elasticpath.sellingchannel.ShoppingItemRecurringPriceAssembler;
import com.elasticpath.sellingchannel.impl.ShoppingItemRecurringPriceAssemblerImpl;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.pricing.impl.PaymentScheduleHelperImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Tests the {@code AbstractShoppingItemImpl} class.
 */
@SuppressWarnings({"PMD.TooManyMethods" })
public class AbstractShoppingItemImplTest {

	private static final String PRODUCT_SKU3 = "productSku3";
	private static final String PRODUCT_SKU = "productSku";
	private static final String PRODUCT2 = "product2";
	private static final String PRODUCT_SKU2 = "productSku2";

	private static final Quantity MONTHLY_QTY = new Quantity(1, "month");
	private static final Quantity BI_MONTHLY_QTY = new Quantity(2, "month");
	private static final Quantity ANNUALLY_QTY = new Quantity(1, "year");
	private static final Quantity BI_ANNUALLY_QTY = new Quantity(2, "year");

	private static final Currency CURRENCY_CAD = Currency.getInstance(Locale.CANADA);

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private ShoppingItemRecurringPriceAssemblerImpl recurringPriceAssembler;
	/**
	 * Set up required before each test.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
	    recurringPriceAssembler = new ShoppingItemRecurringPriceAssemblerImpl();
	    recurringPriceAssembler.setBeanFactory(beanFactory);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.SHOPPING_ITEM_RECURRING_PRICE_ASSEMBLER, recurringPriceAssembler);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.SHOPPING_ITEM_RECURRING_PRICE, ShoppingItemRecurringPriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		final PaymentScheduleHelperImpl paymentScheduleHelper = getPaymentScheduleHelper();

		final SkuOptionService skuOptionService = context.mock(SkuOptionService.class);
		context.checking(new Expectations() {
			{
				allowing(skuOptionService).findOptionValueByKey("shoppingItemRecurringPrice 1"); will(returnValue(null));
				allowing(skuOptionService).findOptionValueByKey("shoppingItemRecurringPrice 2"); will(returnValue(null));
			}
		});
		paymentScheduleHelper.setSkuOptionService(skuOptionService);

		recurringPriceAssembler.setPaymentScheduleHelper(paymentScheduleHelper);

	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests that having an item with a sku for product A cannot be change to a sku for product B.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testChangeSkuToDifferentProduct() {
		ShoppingItem cartItem = new ShoppingItemImpl();

		final ProductSku productSku = context.mock(ProductSku.class);
		final ProductSku productSku2 = context.mock(ProductSku.class, PRODUCT_SKU2);
		final Product product = context.mock(Product.class);
		final Product product2 = context.mock(Product.class, PRODUCT2);

		context.checking(new Expectations() { {
			allowing(productSku).getProduct(); will(returnValue(product));
			oneOf(product).getGuid(); will(returnValue("1234"));
			allowing(productSku2).getProduct(); will(returnValue(product2));
			oneOf(product2).getGuid(); will(returnValue("5678"));
		} });

		cartItem.setProductSku(productSku);

		cartItem.setProductSku(productSku2);
	}

	/**
	 * Tests that having an item with a sku for product A can be changed to a sku for product A.
	 */
	@Test
	public void testChangeSkuToSameProduct() {
		ShoppingItem cartItem = new ShoppingItemImpl();

		final ProductSku productSku = context.mock(ProductSku.class);
		final ProductSku productSku2 = context.mock(ProductSku.class, PRODUCT_SKU2);
		final Product product = context.mock(Product.class);
		final Product product2 = context.mock(Product.class, PRODUCT2);

		context.checking(new Expectations() { {
			allowing(productSku).getProduct(); will(returnValue(product));
			oneOf(product).getGuid(); will(returnValue("1234"));
			allowing(productSku2).getProduct(); will(returnValue(product2));
			oneOf(product2).getGuid(); will(returnValue("12" + "34")); //using different string literals so that != would fail the test
		} });

		cartItem.setProductSku(productSku);

		cartItem.setProductSku(productSku2);
	}

	/**
	 * Test method for ShoppingCartItemImpl.hashCode().
	 */
	@Test
	public void testHashCode() {
		final String guid1 = "guid1";
		ShoppingItem cartItemImpl = new ShoppingItemImpl();
		cartItemImpl.setGuid(guid1);

		final String guid2 = "guid2";
		ShoppingItem cartItemImpl2 = new ShoppingItemImpl();
		cartItemImpl2.setGuid(guid2);

		assertEquals(cartItemImpl.hashCode(), cartItemImpl.hashCode());

		assertNotSame(cartItemImpl.hashCode(), cartItemImpl2.hashCode());

		Map<ShoppingItem, String> testMap = new HashMap<ShoppingItem, String>();

		testMap.put(cartItemImpl, "1");
		testMap.put(cartItemImpl2, "2");
		testMap.put(cartItemImpl, "4");

		assertEquals(Integer.parseInt("2"), testMap.size());
		assertEquals("4", testMap.get(cartItemImpl));
		assertEquals("2", testMap.get(cartItemImpl2));
	}

	/**
	 * Test for ShoppingCartItemImpl.equals().
	 */
	@SuppressWarnings("PMD.UseAssertEqualsInsteadOfAssertTrue")
	@Test
	public void testEquals() {
		final String guid1 = "guid1";
		ShoppingItem cartItemImpl = new ShoppingItemImpl();
		cartItemImpl.setGuid(guid1);

		final String guid2 = "guid2";
		ShoppingItem cartItemImpl2 = new ShoppingItemImpl();
		cartItemImpl2.setGuid(guid2);

		assertTrue(cartItemImpl.equals(cartItemImpl));
		assertTrue(cartItemImpl2.equals(cartItemImpl2));
		assertFalse(cartItemImpl.equals(cartItemImpl2));
	}

	/**
	 * Tests that two children with the same sku can be added as bundle items.
	 **/
	@Test
	public void testAddingTwoBundleItemsWithSameSku() {
		ShoppingItemImpl item = new ShoppingItemImpl();
		ProductSku itemSku = new ProductSkuImpl();
		itemSku.setProduct(new ProductBundleImpl());
		item.setProductSku(itemSku);
		ShoppingItemImpl child1 = new ShoppingItemImpl();
		ProductSku sku1 = new ProductSkuImpl();
		sku1.setSkuCode("skuA");
		child1.setProductSku(sku1);

		ShoppingItemImpl child2 = new ShoppingItemImpl();
		ProductSku sku2 = new ProductSkuImpl();
		sku2.setSkuCode("skuA");
		child2.setProductSku(sku2);

		item.addChildItem(child1);
		item.addChildItem(child2);

		assertEquals("Should be two separate items", 2, item
				.getBundleItems().size());
	}

	/** Test for testHasDependentCartItems(). */
	@Test
	public void testHasBundleItems() {
		CartItem cartItemImpl = new ShoppingItemImpl();
		ProductSku sku = new ProductSkuImpl();
		sku.setProduct(new ProductBundleImpl());
		cartItemImpl.setProductSku(sku);
		CartItem dependentCartItem = new ShoppingItemImpl();
		assertFalse(cartItemImpl.hasBundleItems());
		cartItemImpl.addChildItem(dependentCartItem);
		assertTrue(cartItemImpl.hasBundleItems());
	}

	/**
	 * Bundle should be shippable if it's constituents all are.
	 */
	@Test
	public void testIsShippableFully() {
		ShoppingItem cartItem = mockBundleCartItem(true, PRODUCT_SKU);
		ShoppingItem dependentCartItem = mockCartItem(true, PRODUCT_SKU2);

		cartItem.addChildItem(dependentCartItem);

		assertTrue("Bundle should be shippable if it's constituents all are.", cartItem.isShippable());
	}

	/**
	 * Test for bundle with one constituent shippable, one not. Bundle should be considered shippable.
	 */
	@Test
	public void testIsShippablePartial() {
		ShoppingItem cartItem = mockBundleCartItem(false, PRODUCT_SKU);
		ShoppingItem dependentCartItem = mockCartItem(true, PRODUCT_SKU2);
		ShoppingItem dependentItem2 = mockCartItem(false, PRODUCT_SKU3);

		cartItem.addChildItem(dependentCartItem);
		cartItem.addChildItem(dependentItem2);

		assertTrue("Bundle should be considered shippable if one constituent shippable, one not", cartItem.isShippable());
	}

	/**
	 * Test for bundle marked as shippable but constituents marked as unshippable.
	 */
	@Test
	public void testIsShippableBundleButNotConstituents() {
		ShoppingItem cartItem = mockBundleCartItem(true, PRODUCT_SKU);
		ShoppingItem dependentCartItem = mockCartItem(false, PRODUCT_SKU2);

		cartItem.addChildItem(dependentCartItem);

		assertFalse("bundle marked as shippable but constituents marked as unshippable should be unshippable.", cartItem.isShippable());
	}

	/**
	 * Test for shippable when neither the bundle nor the constituent are shippable.
	 */
	@Test
	public void testNoPartShippable() {
		ShoppingItem cartItem = mockBundleCartItem(false, PRODUCT_SKU);
		ShoppingItem dependentCartItem = mockCartItem(false, PRODUCT_SKU2);

		cartItem.addChildItem(dependentCartItem);

		assertFalse("Bundle cart item with self and constituents not shippable should not be shippable.", cartItem.isShippable());
	}


	private ShoppingItem mockBundleCartItem(final boolean shippable, final String name) {
		final ProductBundle bundle = context.mock(ProductBundle.class, name + "_bundle");
		final ProductSku productSku = context.mock(ProductSku.class, name);

		context.checking(new Expectations() { {
			allowing(productSku).isShippable(); will(returnValue(shippable));
			allowing(productSku).setProduct(bundle);
			allowing(productSku).getProduct(); will(returnValue(bundle));
		} });

		ShoppingItem cartItem = new ShoppingItemImpl();
		productSku.setProduct(bundle);
		cartItem.setProductSku(productSku);
		return cartItem;
	}

	private ShoppingItem mockCartItem(final boolean shippable, final String name) {
		final Product product = context.mock(Product.class, name + "_product");
		final ProductSku productSku = context.mock(ProductSku.class, name);

		context.checking(new Expectations() { {
			allowing(productSku).isShippable(); will(returnValue(shippable));
			allowing(productSku).setProduct(product);
			allowing(productSku).getProduct(); will(returnValue(product));
		} });

		ShoppingItem cartItem = new ShoppingItemImpl();
		productSku.setProduct(product);
		cartItem.setProductSku(productSku);
		return cartItem;
	}

	/**
	 * Test that the lowest a cartItem amount can be is zero (never negative),
	 * regardless of the discount amount.
	 */
	@Test
	public void testLowestAmountIsZero() {
		final BigDecimal lowestPrice = BigDecimal.ONE;
		final BigDecimal discount = BigDecimal.TEN;
		Currency currency = Currency.getInstance("CAD");
		AbstractShoppingItemImpl item = prepareShoppingItem(lowestPrice, currency , false);
		item.applyDiscount(discount);
		assertEquals("The cartItem amount should never be less than zero regardless of the discount amount",
				item.calculateItemTotal().compareTo(BigDecimal.ZERO), 0);
	}


	/**
	 *	test up of internal ShoppingItemRecurringPrice SET.
	 *	need to create at least two prices/x/x/x/ objects with no SimplePrice object in them
	 *	make sure to call setRecurringPrices to exercise the set dirty flag
	 */
	@Test
	public void testGetLowestPriceAndCalculateItemTotalWithRecurringPrices() {
		ShoppingItemRecurringPrice shoppingItemRecurringPrice1 = new ShoppingItemRecurringPriceImpl();
		shoppingItemRecurringPrice1.setPaymentScheduleName("shoppingItemRecurringPrice 1");
		shoppingItemRecurringPrice1.setPaymentFrequency(MONTHLY_QTY);
		shoppingItemRecurringPrice1.setScheduleDuration(ANNUALLY_QTY);
		shoppingItemRecurringPrice1.setSimplePrice(
				new ShoppingItemSimplePrice(new BigDecimal("10.99"), new BigDecimal("8.99"), new BigDecimal("7.99")));

		ShoppingItemRecurringPrice shoppingItemRecurringPrice2 = new ShoppingItemRecurringPriceImpl();
		shoppingItemRecurringPrice1.setPaymentScheduleName("shoppingItemRecurringPrice 2");
		shoppingItemRecurringPrice1.setPaymentFrequency(BI_MONTHLY_QTY);
		shoppingItemRecurringPrice1.setScheduleDuration(BI_ANNUALLY_QTY);
		shoppingItemRecurringPrice1.setSimplePrice(
				new ShoppingItemSimplePrice(new BigDecimal("7.77"), new BigDecimal("6.66"), new BigDecimal("5.55")));

		Set<ShoppingItemRecurringPrice> recurringPrices = new HashSet<ShoppingItemRecurringPrice>();

		recurringPrices.add(shoppingItemRecurringPrice1);
		recurringPrices.add(shoppingItemRecurringPrice2);

		AbstractShoppingItemImpl item = prepareShoppingItemWithRecurringPrice(recurringPrices, CURRENCY_CAD);

		assertEquals("0.00", item.getLowestUnitPrice().getAmount().toString());  //unit price SHOULD return 0.00 for recurring priced items
		assertEquals("0.00", item.calculateItemTotal().toString());  //unit price SHOULD return 0.00 for calculating the item total too
	}

	private AbstractShoppingItemImpl prepareShoppingItemWithRecurringPrice(final Set<ShoppingItemRecurringPrice> recurringPrices,
			final Currency currency) {
		final ProductSku productSku = context.mock(ProductSku.class);

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICING_SCHEME, PricingSchemeImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_SCHEDULE, PriceScheduleImpl.class);

		AbstractShoppingItemImpl item = new ShoppingItemImpl() {  //no overriding of get prices
			private static final long serialVersionUID = -2100849016179282868L;

			@Override
			public ProductSku getProductSku() {
				return productSku;
			}
			@Override
			public Currency getCurrency() {
				return currency;
			}

			@Override
			protected ShoppingItemRecurringPriceAssembler getShoppingItemRecurringPriceAssembler() {
				return recurringPriceAssembler;
			}
		};
		item.setRecurringPrices(recurringPrices);

		return item;
	}


	/**
	 * Test that the amount is equal to the shoppingItem price multiplied by the quantity of the cart item
	 * minus the discount applied to the item.
	 */
	@Test
	public void testAmountCalculation() {
		final int quantity = 10;
		final int shoppingItemPrice = 20;
		final int discount = 1;
		final BigDecimal lowestPrice = new BigDecimal(shoppingItemPrice);
		Currency currency = Currency.getInstance("CAD");
		AbstractShoppingItemImpl item = prepareShoppingItem(lowestPrice, currency , false);
		item.applyDiscount(new BigDecimal(discount));
		item.setQuantity(quantity);
		assertEquals(((shoppingItemPrice * quantity) - discount), item.calculateItemTotal().intValue());
	}

	/**
	 * Test that the amount is equal to the shoppingItem price multiplied by the quantity of the cart item
	 * minus the discount applied to the item.
	 */
	@Test
	public void testAmountCalculationDiscountSkipped() {
		final int quantity = 10;
		final int shoppingItemPrice = 20;
		final int discount = 1;
		final BigDecimal lowestPrice = new BigDecimal(20);
		Currency currency = Currency.getInstance("CAD");
		AbstractShoppingItemImpl item = prepareShoppingItem(lowestPrice, currency , true);
		item.applyDiscount(new BigDecimal(discount));
		item.setQuantity(quantity);

		assertEquals(shoppingItemPrice * quantity, item.calculateItemTotal().intValue());
	}


	/**.*/
	@Test
	public void testSetNullFieldValues() {
		AbstractShoppingItemImpl item = new ShoppingItemImpl();
		item.mergeFieldValues(null);
		assertEquals(0, item.getItemData().size());
	}

	/**.*/
	@Test
	public void testSetTwoFieldValues() {
		AbstractShoppingItemImpl item = new ShoppingItemImpl();
		Map<String, String> values = new HashMap<String, String>();
		values.put("key1", "value1");
		values.put("key2", "value2");
		item.mergeFieldValues(values);
		assertEquals(2, item.getItemData().size());
		assertEquals("value1", item.getFieldValue("key1"));
		assertEquals("value2", item.getFieldValue("key2"));
	}

	private AbstractShoppingItemImpl prepareShoppingItem(final BigDecimal lowestUnitPrice,
			final Currency currency, final boolean excludedFromDiscount) {
		final ProductType productType = context.mock(ProductType.class);
		final Product product = context.mock(Product.class);
		final ProductSku productSku = context.mock(ProductSku.class);
		AbstractShoppingItemImpl item = new ShoppingItemImpl() {
			private static final long serialVersionUID = -4910510721160996614L;

			@Override
			public BigDecimal findLowestUnitPrice() {
				return lowestUnitPrice;
			}
			@Override
			public ProductSku getProductSku() {
				return productSku;
			}
			@Override
			public Currency getCurrency() {
				return currency;
			}
		};

		context.checking(new Expectations() { {
				allowing(productType).isExcludedFromDiscount(); will(returnValue(excludedFromDiscount));
				allowing(product).getProductType(); will(returnValue(productType));
				allowing(productSku).getProduct(); will(returnValue(product));
			} }
		);
		return item;
	}

	private PaymentScheduleHelperImpl getPaymentScheduleHelper() {
		final PaymentScheduleHelperImpl paymentScheduleHelper = new PaymentScheduleHelperImpl();
		paymentScheduleHelper.setBeanFactory(beanFactory);

		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.PAYMENT_SCHEDULE); will(returnValue(new PaymentScheduleImpl()));
			}
		});

		return paymentScheduleHelper;
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as not discountable.
	 */
	@Test
	public void testCanReceiveCartPromotionNonDiscountable() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(false);

		assertFalse(shoppingItem.canReceiveCartPromotion());
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as not discountable, and has a purchase-time price.
	 */
	@Test
	public void testCanReceiveCartPromotionNonDiscountablePurchaseTimePrice() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(false);

		PriceImpl price = createSimplePrice(BigDecimal.TEN);
		shoppingItem.setPrice(1, price);
		assertFalse(shoppingItem.canReceiveCartPromotion());
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as not discountable, and has a recurring price.
	 */
	@Test
	public void testCanReceiveCartPromotionNonDiscountableRecurringPrice() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(false);

		PriceImpl price = createSimplePrice(BigDecimal.ZERO);
		price.setPricingScheme(getRecurringScheme());
		shoppingItem.setPrice(1, price);
		assertFalse(shoppingItem.canReceiveCartPromotion());
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as not discountable, and has a mixed
	 * recurring and purchase-time price.
	 */
	@Test
	public void testCanReceiveCartPromotionNonDiscountableMixedPrice() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(false);

		PriceImpl price = createSimplePrice(BigDecimal.TEN);
		price.setPricingScheme(getRecurringScheme());
		shoppingItem.setPrice(1, price);
		assertFalse(shoppingItem.canReceiveCartPromotion());
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as discountable.
	 */
	@Test
	public void testCanReceiveCartPromotionDiscountable() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(true);

		assertTrue(shoppingItem.canReceiveCartPromotion());
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as discountable, and has a purchase-time price.
	 */
	@Test
	public void testCanReceiveCartPromotionDiscountablePurchaseTimePrice() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(true);

		PriceImpl price = createSimplePrice(BigDecimal.TEN);
		shoppingItem.setPrice(1, price);
		assertTrue(shoppingItem.canReceiveCartPromotion());
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as discountable, and has a recurring price.
	 */
	@Test
	public void testCanReceiveCartPromotionDiscountableRecurringPrice() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(true);

		PriceImpl price = createSimplePrice(BigDecimal.ZERO);
		price.setPricingScheme(getRecurringScheme());
		shoppingItem.setPrice(1, price);
		assertFalse(shoppingItem.canReceiveCartPromotion());
	}

	/**
	 * Tests the canReceiveCartPromotion() method, when the item is set as discountable, and has a mixed
	 * recurring and purchase-time price.
	 */
	@Test
	public void testCanReceiveCartPromotionDiscountableMixedPrice() {
		AbstractShoppingItemImpl shoppingItem = createShoppingItem(true);

		PriceImpl price = createSimplePrice(BigDecimal.TEN);
		price.setPricingScheme(getRecurringScheme());
		shoppingItem.setPrice(1, price);
		assertTrue(shoppingItem.canReceiveCartPromotion());
	}




	private PriceImpl createSimplePrice(final BigDecimal amount) {
		PriceImpl price = new PriceImpl();
		price.setListPrice(MoneyFactory.createMoney(amount, Currency.getInstance(Locale.US)));
		return price;
	}

	private PricingScheme getRecurringScheme() {
		PricingScheme pricingScheme = new PricingSchemeImpl();
		PriceSchedule priceSchedule = new PriceScheduleImpl();
		priceSchedule.setType(PriceScheduleType.RECURRING);
		PaymentSchedule paymentSchedule = new PaymentScheduleImpl();
		paymentSchedule.setName("Monthly");
		priceSchedule.setPaymentSchedule(paymentSchedule);
		pricingScheme.setPriceForSchedule(priceSchedule, createSimplePrice(BigDecimal.TEN));
		return pricingScheme;
	}


	private AbstractShoppingItemImpl createShoppingItem(final boolean discountable) {
		AbstractShoppingItemImpl shoppingItem = new ShoppingItemImpl() {
			private static final long serialVersionUID = -6875611418637317084L;

			public boolean isDiscountable() {
				return discountable;
			};
		};
		return shoppingItem;
	}
}
