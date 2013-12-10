package com.elasticpath.sfweb.ajax.service.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.MessageSource;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceScheduleImpl;
import com.elasticpath.domain.catalog.impl.PricingSchemeImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.subscriptions.PaymentSchedule;
import com.elasticpath.domain.subscriptions.impl.PaymentScheduleImpl;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.sfweb.ajax.bean.GuidedSkuSelectionBean;
import com.elasticpath.sfweb.ajax.bean.impl.GuidedSkuSelectionBeanImpl;


/**
 * Initial limited test of {@code SkuConfigurationServiceImpl}.
 * 
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals" })
public class SkuConfigurationServiceImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final Currency defaultCurrency = Currency.getInstance("USD");
	private final StandardMoneyFormatter moneyFormatter = new StandardMoneyFormatter();
	
	
	/**
	 * test the get price method.
	 */
	@Test
	public void testAddPriceInfoToBean() {
		final Price price = context.mock(Price.class);
		SkuConfigurationServiceImpl configService = new SkuConfigurationServiceImpl() {
			@Override
			protected void runThroughRuleEngine(final ProductSku sku, final Price skuPrice, final Currency currency) {
				//DO NOTHING
			}
			@Override
			public String[] getPriceTierContents(final ProductSku productSku, final ShoppingCart cart) {
				return new String [0];
			}
			@Override
			protected Price getSkuPrice(final ProductSku selectedSku,
					final ShoppingCart cart) {
				return price;
			}
			
			
		};
		configService.setPriceBuilder(new PriceBuilder());
		configService.setMoneyFormatter(moneyFormatter);
		
		final StoreConfig storeConfig = context.mock(StoreConfig.class);
		configService.setStoreConfig(storeConfig);
		
		GuidedSkuSelectionBean skuSelectionBean = new GuidedSkuSelectionBeanImpl();
		final ProductSku selectedSku = context.mock(ProductSku.class);
		final Product product = context.mock(Product.class);
		final Catalog catalog = context.mock(Catalog.class);
		final Currency currency = Currency.getInstance("CAD");
		final Store store = context.mock(Store.class);
		final ShoppingCart cart = context.mock(ShoppingCart.class);
		final Money listPrice = MoneyFactory.createMoney(new BigDecimal("56.78"), currency);
		final Money lowestPrice = MoneyFactory.createMoney(new BigDecimal("12.34"), currency);
		final Money dollarSavings = MoneyFactory.createMoney(new BigDecimal("44.44"), currency);
		final PriceTier priceTier = context.mock(PriceTier.class);
		final SortedMap<Integer, PriceTier> priceTiers = new TreeMap<Integer, PriceTier>();
		final PricingScheme pricingScheme = context.mock(PricingScheme.class);
		priceTiers.put(1, priceTier);
		
		
		context.checking(new Expectations() { { 
			allowing(product).getCode(); will(returnValue("CODE"));
			allowing(selectedSku).getProduct(); will(returnValue(product));
			allowing(price).getListPrice(1); will(returnValue(listPrice));
			allowing(price).getLowestPrice(1); will(returnValue(lowestPrice));
			allowing(price).isLowestLessThanList(1); will(returnValue(true));
			allowing(price).getDollarSavings(1); will(returnValue(dollarSavings));
			allowing(price).getCurrency(); will(returnValue(defaultCurrency));
			allowing(price).getPriceTiers(); will(returnValue(priceTiers));
			allowing(price).getPricingScheme(); will(returnValue(pricingScheme));
			allowing(pricingScheme).getRecurringSchedules(); will(returnValue(Collections.emptySet()));
			allowing(priceTier).getMinQty(); will(returnValue(1));
			allowing(priceTier).getLowestPrice(); will(returnValue(new BigDecimal("12.34")));
			allowing(storeConfig).getStore(); will(returnValue(store));
			allowing(store).getCatalog(); will(returnValue(catalog));
			allowing(cart).getLocale(); will(returnValue(Locale.CANADA));
		} });
		
		configService.addPriceInfoToBean(cart, skuSelectionBean, selectedSku, 1);
		
		String string = "Price info should match pricelookupservice return.";
		assertEquals(string, "$56.78", skuSelectionBean.getListPrice());
		assertEquals(string, "$12.34", skuSelectionBean.getLowestPrice());
		assertEquals(string, true, skuSelectionBean.isLowestLessThanList());
		assertEquals(string, "$44.44", skuSelectionBean.getDollarSavings());
		
		assertEquals(string, "<span class=\"tier-level\">1 +  @&nbsp </span> <span class=\"sale-price\" id=\"tier-price-0\">" 
				+ "$12.34" + "</span>", skuSelectionBean.getPriceTierContents()[0]);
		assertEquals(string, 1, skuSelectionBean.getPriceTiers().get(0).getMinQty());
		assertEquals(string, "12.34", skuSelectionBean.getPriceTiers().get(0).getPrice().toString());
	}
	
	/**
	 * test the get price method.
	 */
	@Test
	public void testAddPriceInfoToBeanForRecurringItem() {
		final Price price = context.mock(Price.class);
		
		SkuConfigurationServiceImpl configService = new SkuConfigurationServiceImpl() {
			@Override
			protected void runThroughRuleEngine(final ProductSku sku, final Price skuPrice, final Currency currency) {
				//DO NOTHING
			}
			@Override
			public String[] getPriceTierContents(final ProductSku productSku, final ShoppingCart cart) {
				return new String [0];
			}
			@Override
			protected Price getSkuPrice(final ProductSku selectedSku,
					final ShoppingCart cart) {
				return price;
			}
			
			
		};
		
		GuidedSkuSelectionBean skuSelectionBean = new GuidedSkuSelectionBeanImpl();
		final ProductSku selectedSku = context.mock(ProductSku.class);
		final Product product = context.mock(Product.class);
		final Catalog catalog = context.mock(Catalog.class);
		final Currency currency = Currency.getInstance("USD");
		final Store store = context.mock(Store.class);
		final ShoppingCart cart = context.mock(ShoppingCart.class);
		final Money listPrice = MoneyFactory.createMoney(new BigDecimal("56.78"), currency);
		final Money lowestPrice = MoneyFactory.createMoney(new BigDecimal("12.34"), currency);
		final Money dollarSavings = MoneyFactory.createMoney(new BigDecimal("44.44"), currency);
		final PriceTier priceTier = context.mock(PriceTier.class);
		final SortedMap<Integer, PriceTier> priceTiers = new TreeMap<Integer, PriceTier>();
		final MessageSource messageSource = context.mock(MessageSource.class);

		final StoreConfig storeConfig = context.mock(StoreConfig.class);
		configService.setStoreConfig(storeConfig);
		configService.setMessageSource(messageSource);
		configService.setPriceBuilder(new PriceBuilder());
		configService.setMoneyFormatter(new StandardMoneyFormatter());

		
		final PricingScheme pricingScheme = new PricingSchemeImpl();
		priceTiers.put(1, priceTier);
		
		//setup recurring price 
		final PaymentSchedule paymentSchedule = new PaymentScheduleImpl();
		paymentSchedule.setName("Monthly");
		paymentSchedule.setPaymentFrequency(new Quantity(1, "month"));
		paymentSchedule.setScheduleDuration(new Quantity(1, "year"));
		
		PriceSchedule priceSchedule = new PriceScheduleImpl();
		priceSchedule.setType(PriceScheduleType.RECURRING);
		priceSchedule.setPaymentSchedule(paymentSchedule);
		
		Price recurringPrice = new PriceImpl();
		recurringPrice.setListPrice(MoneyFactory.createMoney(new BigDecimal("69.99"), currency));
		recurringPrice.setSalePrice(MoneyFactory.createMoney(new BigDecimal("59.99"), currency));
		recurringPrice.setCurrency(currency);

		pricingScheme.setPriceForSchedule(priceSchedule, recurringPrice);
		
		context.checking(new Expectations() { { 
			allowing(product).getCode(); will(returnValue("CODE"));
			allowing(selectedSku).getProduct(); will(returnValue(product));
			allowing(price).getListPrice(1); will(returnValue(listPrice));
			allowing(price).getLowestPrice(1); will(returnValue(lowestPrice));
			allowing(price).isLowestLessThanList(1); will(returnValue(true));
			allowing(price).getDollarSavings(1); will(returnValue(dollarSavings));
			allowing(price).getCurrency(); will(returnValue(defaultCurrency));
			allowing(price).getPriceTiers(); will(returnValue(priceTiers));
			allowing(price).getPricingScheme(); will(returnValue(pricingScheme));
			
			allowing(priceTier).getMinQty(); will(returnValue(1));
			allowing(priceTier).getLowestPrice(); will(returnValue(new BigDecimal("12.34")));
			allowing(storeConfig).getStore(); will(returnValue(store));
			allowing(store).getCatalog(); will(returnValue(catalog));
			allowing(cart).getLocale(); will(returnValue(Locale.getDefault()));
			allowing(messageSource).getMessage("productTemplate.recurringPrice." + paymentSchedule.getName(), 
					null, paymentSchedule.getName(), Locale.getDefault()); will(returnValue("per Month"));
		} });
		
		configService.addPriceInfoToBean(cart, skuSelectionBean, selectedSku, 1);
		
		String string = "Price info should match pricelookupservice return.";
		assertEquals(string, "$69.99 per Month", skuSelectionBean.getListPrice());
		assertEquals(string, "$59.99 per Month", skuSelectionBean.getLowestPrice());
		assertEquals(string, true, skuSelectionBean.isLowestLessThanList());
		assertEquals(string, "$10.00 per Month", skuSelectionBean.getDollarSavings());
		
		assertEquals(string, "<span class=\"tier-level\">1 +  @&nbsp </span> <span class=\"sale-price\" id=\"tier-price-0\">"
				+ "$59.99 per Month</span>", skuSelectionBean.getPriceTierContents()[0]);
		assertEquals(string, 1, skuSelectionBean.getPriceTiers().get(0).getMinQty());
		assertEquals(string, "59.99", skuSelectionBean.getPriceTiers().get(0).getPrice().toString());
	}

}
