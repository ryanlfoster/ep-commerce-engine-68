package com.elasticpath.sellingchannel.director.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.SimplePrice;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceScheduleImpl;
import com.elasticpath.domain.catalog.impl.PricingSchemeImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemRecurringPrice;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemRecurringPriceImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemSimplePrice;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.subscriptions.PaymentSchedule;
import com.elasticpath.domain.subscriptions.impl.PaymentScheduleImpl;
import com.elasticpath.sellingchannel.ShoppingItemFactory;
import com.elasticpath.sellingchannel.impl.ShoppingItemRecurringPriceAssemblerImpl;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.pricing.impl.PaymentScheduleHelperImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Verifies the behaviour of the ShoppingItemRecurringPriceAssembler.
 */
public class ShoppingItemRecurringPriceAssemblerImplTest {
	private static final String MONTHLY = "Monthly";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;

	private BeanFactoryExpectationsFactory expectationsFactory;

	private static final Quantity MONTHLY_QTY = new Quantity(1, MONTHLY);

	private static final Currency CURRENCY_CAD = Currency.getInstance(Locale.CANADA);

	private PaymentSchedule monthlyPaymentSchedule;
	private PriceSchedule monthlyPriceSchedule;
	private Price monthlyPrice;
	private Price monthlySimplePrice;
	private PricingScheme monthlyPricingScheme;

	private ShoppingItemRecurringPriceAssemblerImpl recurringPriceAssembler;
	private Set<ShoppingItemRecurringPrice> generatedRecurringPrices;

	/**
	 * Set up required before each test.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		monthlyPaymentSchedule = new PaymentScheduleImpl();
		monthlyPaymentSchedule.setName(MONTHLY);
		monthlyPaymentSchedule.setPaymentFrequency(MONTHLY_QTY);

		monthlyPriceSchedule = new PriceScheduleImpl();
		monthlyPriceSchedule.setType(PriceScheduleType.RECURRING);
		monthlyPriceSchedule.setPaymentSchedule(monthlyPaymentSchedule);

		monthlySimplePrice = new PriceImpl();
		monthlySimplePrice.setListPrice(
				MoneyFactory.createMoney(new BigDecimal("10.00"), CURRENCY_CAD)); //the base simple price actually needs a value

		monthlyPricingScheme = new PricingSchemeImpl();
		monthlyPricingScheme.setPriceForSchedule(monthlyPriceSchedule, monthlySimplePrice);

		monthlyPrice = new PriceImpl();  //this is the "top" Price object
		monthlyPrice.setPricingScheme(monthlyPricingScheme);

		recurringPriceAssembler = new ShoppingItemRecurringPriceAssemblerImpl();
		recurringPriceAssembler.setBeanFactory(beanFactory);

		final PaymentScheduleHelperImpl paymentScheduleHelper = new PaymentScheduleHelperImpl();
		final SkuOptionService skuOptionService = context.mock(SkuOptionService.class);
		paymentScheduleHelper.setSkuOptionService(skuOptionService);
		paymentScheduleHelper.setBeanFactory(beanFactory);
		context.checking(new Expectations() {
			{
				allowing(skuOptionService).findOptionValueByKey(MONTHLY); will(returnValue(null));
				allowing(beanFactory).getBean(ContextIdNames.PAYMENT_SCHEDULE); will(returnValue(new PaymentScheduleImpl()));
			}
		});

		recurringPriceAssembler.setPaymentScheduleHelper(paymentScheduleHelper);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.SHOPPING_ITEM_RECURRING_PRICE_ASSEMBLER, recurringPriceAssembler);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests that called createShoppingItem will find the sku, find the price and create the shopping item.
	 */
	@Test
	public void testCreateShoppingItemRecurringPrices() {

		final ShoppingItem actualShoppingItem = createShoppingItem(); //all the setup stuff to get to this point

		//now that the base shoppingItem is all setup - let's add recurringPrices to the price object
		actualShoppingItem.setPrice(1, monthlyPrice);

		generatedRecurringPrices = new HashSet<ShoppingItemRecurringPrice>();
		generatedRecurringPrices = recurringPriceAssembler.createShoppingItemRecurringPrices(monthlyPrice, 1);
		final ShoppingItemRecurringPrice retrievedShoppingItemRecurringPrice = generatedRecurringPrices.iterator().next();

		assertEquals(monthlySimplePrice.getListPrice().getAmount(), retrievedShoppingItemRecurringPrice.getSimplePrice().getListUnitPrice());
		assertEquals(MONTHLY_QTY, retrievedShoppingItemRecurringPrice.getPaymentFrequency());
	}


	/**
	 * the reverse of createShoppingItemRecurringPrices.
	 */
	@Test
	public void testAssemblePrice() {
		final Price priceObjectToBeAssembled = new PriceImpl();
		priceObjectToBeAssembled.setCurrency(CURRENCY_CAD); //this is the minimum for the top level Price object.

		context.checking(new Expectations() {
			{
				expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
				expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICING_SCHEME, PricingSchemeImpl.class);
				expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_SCHEDULE, PriceScheduleImpl.class);
				expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
			}
		});
		final ShoppingItemSimplePrice simplePrice = new ShoppingItemSimplePrice(monthlySimplePrice, 1);

		final Set<ShoppingItemRecurringPrice> recurringPriceSet = new HashSet<ShoppingItemRecurringPrice>();

		final ShoppingItemRecurringPrice shoppingItemRecurringPrice = new ShoppingItemRecurringPriceImpl();
		shoppingItemRecurringPrice.setSimplePrice(simplePrice);
		shoppingItemRecurringPrice.setPaymentScheduleName(MONTHLY);
		shoppingItemRecurringPrice.setPaymentFrequency(MONTHLY_QTY);

		recurringPriceSet.add(shoppingItemRecurringPrice);

		recurringPriceAssembler.assemblePrice(priceObjectToBeAssembled, recurringPriceSet);

		final PriceSchedule priceScheduleFromAssebledPriceObject =
			priceObjectToBeAssembled.getPricingScheme().getSchedules(PriceScheduleType.RECURRING).iterator().next();
		final SimplePrice simplePriceFromAssembled =
			priceObjectToBeAssembled.getPricingScheme().getSimplePriceForSchedule(priceScheduleFromAssebledPriceObject);

		final SimplePrice expectedSimplePrice = monthlyPricingScheme.getSimplePriceForSchedule(monthlyPriceSchedule);

		assertEquals(expectedSimplePrice.getListPrice(1), simplePriceFromAssembled.getListPrice(1));
	}


	/**
	 * test a create and then call the assemble method right after to assert everything is still the same.
	 */
	@Test
	public void testCreateAndThenAssemble() {
		final ShoppingItem actualShoppingItem = createShoppingItem(); //all the setup stuff to get to this point

		//now that the base shoppingItem is all setup - let's add recurringPrices to the price object
		actualShoppingItem.setPrice(1, monthlyPrice);

		generatedRecurringPrices = new HashSet<ShoppingItemRecurringPrice>();
		generatedRecurringPrices = recurringPriceAssembler.createShoppingItemRecurringPrices(monthlyPrice, 1);

		final Price newPriceObjectToBeAssembled = new PriceImpl();
		newPriceObjectToBeAssembled.setCurrency(CURRENCY_CAD); //this is the minimum for the top level Price object.

		context.checking(new Expectations() {
			{
				expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
				expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICING_SCHEME, PricingSchemeImpl.class);
				expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE_SCHEDULE, PriceScheduleImpl.class);
				expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRICE, PriceImpl.class);
			}
		});

		recurringPriceAssembler.assemblePrice(newPriceObjectToBeAssembled, generatedRecurringPrices);

		final PriceSchedule priceScheduleFromAssebledPriceObject =
			newPriceObjectToBeAssembled.getPricingScheme().getSchedules(PriceScheduleType.RECURRING).iterator().next();

		final SimplePrice simplePriceFromAssembled =
			newPriceObjectToBeAssembled.getPricingScheme().getSimplePriceForSchedule(priceScheduleFromAssebledPriceObject);

		final SimplePrice expectedSimplePrice = monthlyPricingScheme.getSimplePriceForSchedule(monthlyPriceSchedule);

		assertEquals(expectedSimplePrice.getListPrice(1), simplePriceFromAssembled.getListPrice(1));
	}

	/**
	 * all the setup stuff to get a ShoppingItem object.
	 * @return ShoppingItem to use
	 */
	private ShoppingItem createShoppingItem() {
		final ProductSku sku = new ProductSkuImpl();
		final Product product = new ProductImpl();
		sku.setProduct(product);

		final ShoppingItemAssemblerImpl assembler = new ShoppingItemAssemblerImpl() {
			@Override
			protected boolean verifySelectionRulesFollowed(final Product product, final ShoppingItemDto shoppingItemDto) {
				return true;
			}

			@Override
			protected boolean verifyDtoStructureEqualsBundleStructure(final Product product, final ShoppingItemDto dtoNode) {
				return true;
			}

			@Override
			ProductSku getProductSku(final String currentSkuGuid) {
				// not testing this part
				return sku;
			}
		};

		final ShoppingItemFactory cartItemFactory = context.mock(ShoppingItemFactory.class);
		assembler.setShoppingItemFactory(cartItemFactory);

		final Catalog catalog = new CatalogImpl();
		final Store store = new StoreImpl();
		store.setCatalog(catalog);

		final ShoppingItem shoppingItem = new ShoppingItemImpl();
		context.checking(new Expectations() {
			{
				oneOf(cartItemFactory).createShoppingItem(sku, null, 2, 0, Collections.<String, String>emptyMap()); will(returnValue(shoppingItem));
				expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
				expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.SHOPPING_ITEM_RECURRING_PRICE, ShoppingItemRecurringPriceImpl.class);
			}
		});

		final ShoppingItemDto shoppingItemDto = new ShoppingItemDto("some sku", 2);
		shoppingItemDto.setSelected(true);

		final ShoppingItem actualShoppingItem = assembler.createShoppingItem(shoppingItemDto);

		assertEquals("The cartItem from the delegate should equal the cart item from the factory", shoppingItem, actualShoppingItem);


		return actualShoppingItem;
	}

}
