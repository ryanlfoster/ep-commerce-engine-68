package com.elasticpath.test.integration.cart;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.pricing.service.PriceListHelperService;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceScheduleImpl;
import com.elasticpath.domain.catalog.impl.PricingSchemeImpl;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemRecurringPrice;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.subscriptions.PaymentSchedule;
import com.elasticpath.domain.subscriptions.impl.PaymentScheduleImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.sellingchannel.impl.ShoppingItemRecurringPriceAssemblerImpl;
import com.elasticpath.service.pricing.PaymentScheduleHelper;
import com.elasticpath.service.shoppingcart.ShoppingItemService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.TaxTestPersister;

/**
 * Integration test for {@code ShoppingItemAssemblerImpl}. Concentrates on proving that deeply nested bundles
 * are processed correctly for the {@code AddToCartController}. <br/> 
 * 
 * Note that this test should probably be against
 * {@code CartDirector} so that the module interface is tested. However, at present I don't wish to test the 
 * other functionality of {@code CartDirector} (i.e. pricing).
 */
public class ShoppingItemRecurringPriceAssemblerIntegrationTest extends DbTestCase {
	private static final Quantity MONTHLY_QTY = new Quantity(1, "Monthly Payment Schedule");
	private static final Quantity ANNUALLY_QTY = new Quantity(1, "year");
	private static final Currency CURRENCY_CAD = Currency.getInstance(Locale.CANADA);

	@Autowired
	private PriceListHelperService priceListHelperService;

	private PaymentSchedule monthlyPaymentSchedule;
	private PriceSchedule monthlyPriceSchedule;
	private Price monthlyPrice;
	private Price monthlySimplePrice;
	private PricingScheme monthlyPricingScheme;
	
	@Autowired
	private ShoppingItemAssembler assembler;
	private ShoppingItemRecurringPriceAssemblerImpl recurringPriceAssembler; 
	private Set<ShoppingItemRecurringPrice> generatedRecurringPrices;
	
	@Autowired
	private ShoppingItemService shoppingItemService;

	
	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 * 
	 * @throws Exception on error.
	 */
	@Before
	public void setUp() throws Exception {
		monthlyPaymentSchedule = new PaymentScheduleImpl();
		monthlyPaymentSchedule.setName("Monthly Payment Schedule");
		monthlyPaymentSchedule.setPaymentFrequency(MONTHLY_QTY);
		monthlyPaymentSchedule.setScheduleDuration(ANNUALLY_QTY);
		
		monthlyPriceSchedule = new PriceScheduleImpl();
		monthlyPriceSchedule.setType(PriceScheduleType.RECURRING);
		monthlyPriceSchedule.setPaymentSchedule(monthlyPaymentSchedule);

		monthlySimplePrice = new PriceImpl();
		monthlySimplePrice.setListPrice(MoneyFactory.createMoney(new BigDecimal("10.00"), CURRENCY_CAD)); //the base simple price actually needs a value
		
		monthlyPricingScheme = new PricingSchemeImpl();
		monthlyPricingScheme.setPriceForSchedule(monthlyPriceSchedule, monthlySimplePrice);

		monthlyPrice = new PriceImpl();  //this is the "top" Price object
		monthlyPrice.setPricingScheme(monthlyPricingScheme);
		
		final PaymentScheduleHelper paymentScheduleHelper = getBeanFactory().getBean("paymentScheduleHelper");
		
		recurringPriceAssembler = new ShoppingItemRecurringPriceAssemblerImpl();
		recurringPriceAssembler.setBeanFactory(getBeanFactory());
		recurringPriceAssembler.setPaymentScheduleHelper(paymentScheduleHelper);
	}
	
	/**
	 * Tests calling {@code createShoppingItem} for a non-bundle product.
	 */
	@DirtiesDatabase
	@Test
	public void testCreateShoppingItemRecurringPrices() {
		persistProductWithSku();
		ShoppingItemDto shoppingItemDto = new ShoppingItemDto("skuCode", 1);
		
		Store store = getScenario().getStore();
		CustomerSessionImpl customerSession = getCustomerSession();
		ShoppingItem shoppingItem = assembler.createShoppingItem(shoppingItemDto);
		
		//now that the base shoppingItem is all setup - let's add recurringPrices to the price object
		shoppingItem.setPrice(1, monthlyPrice);
		
		//persist it as an add 
		shoppingItem = shoppingItemService.saveOrUpdate(shoppingItem);
		
		String guid = shoppingItem.getGuid(); //for retrieval from database
		
		//now retrieve it before we do this test part below
		ShoppingItem retrievedShoppingItem = shoppingItemService.findByGuid(guid, null); //null loadtuner
		
		generatedRecurringPrices = new HashSet<ShoppingItemRecurringPrice>();
		generatedRecurringPrices = recurringPriceAssembler.createShoppingItemRecurringPrices(retrievedShoppingItem.getPrice(), 1);

		ShoppingItemRecurringPrice retrievedShoppingItemRecurringPrice = generatedRecurringPrices.iterator().next();

		//these asserts are to check that the retrieved price object is the same values as the monthlyPrice object that was originally passed in.
		assertEquals(monthlySimplePrice.getListPrice().getAmount(), retrievedShoppingItemRecurringPrice.getSimplePrice().getListUnitPrice());
		assertEquals(MONTHLY_QTY, retrievedShoppingItemRecurringPrice.getPaymentFrequency());
	}
	
	private Product persistProductWithSku() {
		TaxCode taxCode = getPersisterFactory().getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS);
		Currency currency = priceListHelperService.getDefaultCurrencyFor(getScenario().getCatalog());
		int orderLimit = Integer.MAX_VALUE;
		Product product = getPersisterFactory().getCatalogTestPersister().persistProductWithSku(
				getScenario().getCatalog(), 
				getScenario().getCategory(), 
				getScenario().getWarehouse(), 
				BigDecimal.TEN, 
				currency, 
				"brandCode", 
				"productCode", 
				"productName", 
				"skuCode", 
				taxCode.getCode(), 
				AvailabilityCriteria.ALWAYS_AVAILABLE, 
				orderLimit);
		return product;
		
	}

	private CustomerSessionImpl getCustomerSession() {
		return new CustomerSessionImpl() {
			private static final long serialVersionUID = -5268177574493014610L;

			@Override
			public Currency getCurrency() {
				// TODO Auto-generated method stub
				return getScenario().getStore().getDefaultCurrency();
			}
		};
	}
}
