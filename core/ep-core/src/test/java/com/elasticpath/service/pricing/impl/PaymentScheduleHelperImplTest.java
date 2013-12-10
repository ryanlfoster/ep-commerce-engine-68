package com.elasticpath.service.pricing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl;
import com.elasticpath.domain.subscriptions.PaymentSchedule;
import com.elasticpath.domain.subscriptions.impl.PaymentScheduleImpl;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * Tests {@link PaymentScheduleHelperImpl}.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class PaymentScheduleHelperImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Test createPaymentSchedule method for empty option value map.
	 */
	@Test
	public void testGetPaymentSchedule1() {
		final ProductSku sku = context.mock(ProductSku.class);
		final PaymentScheduleHelperImpl psh = new PaymentScheduleHelperImpl();
		
		context.checking(new Expectations() {
			{
				oneOf(sku).getOptionValueMap(); will(returnValue(null));
			}
		});
		
		assertNull(psh.getPaymentSchedule(sku));
	}
	
	/**
	 * Test createPaymentSchedule method for null frequency option.
	 */
	@Test
	public void testGetPaymentSchedule2() {
		final ProductSku sku = context.mock(ProductSku.class);
		final Product product = context.mock(Product.class);
		final SkuOptionService skuOptionService = context.mock(SkuOptionService.class);
		final PaymentScheduleHelperImpl psh = new PaymentScheduleHelperImpl();
		psh.setSkuOptionService(skuOptionService);
		
		context.checking(new Expectations() {
			{
				oneOf(sku).getOptionValueMap(); will(returnValue(Collections.emptyMap()));
				oneOf(sku).getProduct(); will(returnValue(product));
				oneOf(skuOptionService).findByKey(PaymentScheduleHelperImpl.FREQUENCY_OPTION_KEY); will(returnValue(null));
			}
		});
		
		assertNull(psh.getPaymentSchedule(sku));
	}
	
	/**
	 * Test createPaymentSchedule method for no sku option value.
	 */
	@Test
	public void testGetPaymentSchedule3() {
		final ProductSku sku = context.mock(ProductSku.class);
		final Product product = context.mock(Product.class);
		final SkuOptionService skuOptionService = context.mock(SkuOptionService.class);
		final SkuOption skuOption = context.mock(SkuOption.class);
		final PaymentScheduleHelperImpl psh = new PaymentScheduleHelperImpl();
		psh.setSkuOptionService(skuOptionService);
		
		context.checking(new Expectations() {
			{
				oneOf(sku).getOptionValueMap(); will(returnValue(Collections.emptyMap()));
				oneOf(sku).getProduct(); will(returnValue(product));
				oneOf(skuOptionService).findByKey(PaymentScheduleHelperImpl.FREQUENCY_OPTION_KEY); will(returnValue(skuOption));
				oneOf(sku).getSkuOptionValue(skuOption); will(returnValue(null));
			}
		});
		
		assertNull(psh.getPaymentSchedule(sku));
	}

	/**
	 * Test createPaymentSchedule method for sku option value equal to NA.
	 */
	@Test
	public void testGetPaymentSchedule4() {
		final ProductSku sku = context.mock(ProductSku.class);
		final Product product = context.mock(Product.class);
		final SkuOptionService skuOptionService = context.mock(SkuOptionService.class);
		final SkuOption skuOption = context.mock(SkuOption.class);
		final SkuOptionValue sov = context.mock(SkuOptionValue.class);
		final PaymentScheduleHelperImpl psh = new PaymentScheduleHelperImpl();
		psh.setSkuOptionService(skuOptionService);
		final Map<String, SkuOptionValue> optionMap = new HashMap<String, SkuOptionValue>();
		optionMap.put(PaymentScheduleHelperImpl.FREQUENCY_OPTION_KEY, sov);
		context.checking(new Expectations() {
			{
				oneOf(sku).getOptionValueMap(); will(returnValue(optionMap));
				oneOf(sku).getProduct(); will(returnValue(product));
				oneOf(skuOptionService).findByKey(PaymentScheduleHelperImpl.FREQUENCY_OPTION_KEY); will(returnValue(skuOption));
				oneOf(sov).getOptionValueKey(); will(returnValue(PaymentScheduleHelperImpl.PAY_NOW_OPTION_VALUE_KEY));
				oneOf(sku).getSkuOptionValue(skuOption); will(returnValue(sov));
			}
		});
		
		assertNull(psh.getPaymentSchedule(sku));
	}
	
	/**
	 * Test createPaymentSchedule method for a "Monthly" option value.
	 */
	@Test
	public void testGetPaymentSchedule5() {
		final ProductSku sku = context.mock(ProductSku.class);
		final Product product = context.mock(Product.class);
		final SkuOptionService skuOptionService = context.mock(SkuOptionService.class);
		final SkuOption skuOption = context.mock(SkuOption.class);
		final SkuOptionValue sov = context.mock(SkuOptionValue.class);
		final PaymentScheduleHelperImpl psh = new PaymentScheduleHelperImpl();
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		psh.setBeanFactory(beanFactory);
		psh.setSkuOptionService(skuOptionService);
		final Map<String, SkuOptionValue> optionMap = new HashMap<String, SkuOptionValue>();
		optionMap.put(PaymentScheduleHelperImpl.FREQUENCY_OPTION_KEY, sov);
		final String optionValueKey = "Monthly";
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.PAYMENT_SCHEDULE); will(returnValue(new PaymentScheduleImpl()));
				allowing(sku).getOptionValueMap(); will(returnValue(optionMap));
				allowing(sku).getProduct(); will(returnValue(product));
				oneOf(skuOptionService).findByKey(PaymentScheduleHelperImpl.FREQUENCY_OPTION_KEY); will(returnValue(skuOption));
				allowing(sov).getOptionValueKey(); will(returnValue(optionValueKey));
				allowing(sov).getOrdering(); will(returnValue(0));
				allowing(sku).getSkuOptionValue(skuOption); will(returnValue(sov));
			}
		});
		
		PaymentSchedule paymentSchedule = psh.getPaymentSchedule(sku);
		assertNotNull(paymentSchedule);
		assertSame(paymentSchedule.getName(), optionValueKey);
		assertNotNull(paymentSchedule.getPaymentFrequency());
		assertNull(paymentSchedule.getScheduleDuration());
		assertNotNull(paymentSchedule.getPaymentFrequency().getUnit());
		assertSame(paymentSchedule.getPaymentFrequency().getUnit(), optionValueKey);
		
		assertEquals(paymentSchedule, psh.getPaymentSchedule(sku));
	}
	
	/**
	 * Test createPaymentSchedule method for non-recurring items.
	 */
	@Test
	public void testCreatePaymentSchedule1() {
		PaymentScheduleHelperImpl psh = new PaymentScheduleHelperImpl() {
			@Override
			protected boolean isPurchaseTime(final SkuOptionValue skuOptionValue) {
				return true;
			}
		};
		final SkuOptionValue sov = context.mock(SkuOptionValue.class);
		assertNull(psh.createPaymentSchedule(sov));
	}
	
	/**
	 * Test createPaymentSchedule method for recurring items.
	 */
	@Test
	public void testCreatePaymentSchedule2() {
		PaymentScheduleHelperImpl psh = new PaymentScheduleHelperImpl() {
			@Override
			protected boolean isPurchaseTime(final SkuOptionValue skuOptionValue) {
				return false;
			}
		};
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.PAYMENT_SCHEDULE); will(returnValue(new PaymentScheduleImpl()));
			}
		});
		psh.setBeanFactory(beanFactory);
	
		SkuOptionValue sov = new SkuOptionValueImpl();
		sov.setOptionValueKey("sov1");
		PaymentSchedule paymentSchedule = psh.createPaymentSchedule(sov);
		assertNotNull(paymentSchedule);
		assertEquals(sov.getOptionValueKey(), paymentSchedule.getName());
		assertNotNull(paymentSchedule.getPaymentFrequency());
		assertEquals(paymentSchedule.getPaymentFrequency().getAmount(), 1);
		assertEquals(paymentSchedule.getPaymentFrequency().getUnit(), sov.getOptionValueKey());
	
		assertEquals(paymentSchedule, psh.createPaymentSchedule(sov));
	}
	

	
	/**
	 * Test isPurchaseTime method for recurring and non-recurring items.
	 */
	@Test
	public void testIsPurchaseTime() {
		PaymentScheduleHelperImpl psh = new PaymentScheduleHelperImpl();
		
		assertTrue(psh.isPurchaseTime(null));
		
		SkuOptionValue sov = new SkuOptionValueImpl();
		sov.setOptionValueKey("sov1");
		assertFalse(psh.isPurchaseTime(sov));
		
		sov.setOptionValueKey(PaymentScheduleHelperImpl.PAY_NOW_OPTION_VALUE_KEY);
		assertTrue(psh.isPurchaseTime(sov));
	}

	/**
	 * Test getFrequencyOption method.
	 */
	@Test
	public void testGetFrequencyOption() {
		PaymentScheduleHelperImpl psh = new PaymentScheduleHelperImpl();
		final SkuOptionService skuOptionService = context.mock(SkuOptionService.class);
		psh.setSkuOptionService(skuOptionService);
		final SkuOption skuOption = context.mock(SkuOption.class);
		
		context.checking(new Expectations() {
			{
				oneOf(skuOptionService).findByKey(PaymentScheduleHelperImpl.FREQUENCY_OPTION_KEY); will(returnValue(skuOption));
			}
		});
	
		SkuOption frequencyOption = psh.getFrequencyOption(null);
		assertSame(frequencyOption, skuOption);
		assertSame(psh.getFrequencyOption(context.mock(Product.class)), frequencyOption);
		
	}

	/**
	 * Test isPaymentScheduleCapable for a recurring item.
	 */
	@Test
	public void testIsPaymentScheduleCapable1() {
		final SkuOptionService skuOptionService = context.mock(SkuOptionService.class);
		final Product product = context.mock(Product.class);
		final ProductType productType = context.mock(ProductType.class);
		final SkuOption skuOption = new SkuOptionImpl();
		skuOption.setOptionKey(PaymentScheduleHelperImpl.FREQUENCY_OPTION_KEY);
		final Set<SkuOption> soSet = new HashSet<SkuOption>();
		soSet.add(skuOption);
		
		PaymentScheduleHelperImpl psh = new PaymentScheduleHelperImpl();
		psh.setSkuOptionService(skuOptionService);
		context.checking(new Expectations() {
			{
				oneOf(skuOptionService).findByKey(PaymentScheduleHelperImpl.FREQUENCY_OPTION_KEY); will(returnValue(skuOption));
				oneOf(product).getProductType(); will(returnValue(productType));
				oneOf(productType).getSkuOptions(); will(returnValue(soSet));
			}
		});
		
		assertTrue(psh.isPaymentScheduleCapable(product));
	}
	
	/**
	 * Test isPaymentScheduleCapable for a non-recurring item.
	 */
	@Test
	public void testIsPaymentScheduleCapable2() {
		final SkuOptionService skuOptionService = context.mock(SkuOptionService.class);
		final Product product = context.mock(Product.class);
		final ProductType productType = context.mock(ProductType.class);
		final SkuOption skuOption = new SkuOptionImpl();
		skuOption.setOptionKey(PaymentScheduleHelperImpl.FREQUENCY_OPTION_KEY);

		final SkuOption skuOption2 = new SkuOptionImpl();
		skuOption2.setOptionKey("Something else");
		final Set<SkuOption> soSet = new HashSet<SkuOption>();
		soSet.add(skuOption2);

		PaymentScheduleHelperImpl psh = new PaymentScheduleHelperImpl();
		psh.setSkuOptionService(skuOptionService);
		
		context.checking(new Expectations() {
			{
				oneOf(skuOptionService).findByKey(PaymentScheduleHelperImpl.FREQUENCY_OPTION_KEY); will(returnValue(skuOption));
				oneOf(product).getProductType(); will(returnValue(productType));
				oneOf(productType).getSkuOptions(); will(returnValue(soSet));
			}
		});
		
		assertFalse(psh.isPaymentScheduleCapable(product));
	}

}
