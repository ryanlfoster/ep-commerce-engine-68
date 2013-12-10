package com.elasticpath.service.pricing.impl;

import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.ShoppingItemRecurringPrice;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.subscriptions.PaymentSchedule;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.pricing.PaymentScheduleHelper;

/**
 * Factory and helper for {@link PaymentSchedule}. 
 * This class uses SkuOptionValue as the source of the information to create the payment schedules for any given sku.
 */
public class PaymentScheduleHelperImpl implements PaymentScheduleHelper {
	private SkuOption frequencyOption;
	private SkuOptionService skuOptionService;
	private BeanFactory beanFactory;
	
	/**
	 * The option key to be used for accessing the sku options values for a payment schedule.
	 * 
	 */
	public static final String FREQUENCY_OPTION_KEY = "Frequency";
	
	/**
	 * The sku option value for skus that do not have recurring pricing, but the product type has recurring pricing.
	 */
	public static final String PAY_NOW_OPTION_VALUE_KEY = "NA";
	
	/**
	 * {@inheritDoc} The payment schedule will be created based on the sku option values. The implementation caches
	 * the results, and, therefore, returns the same instance of payment schedule for all subsequent calls with the same sku option value. 
	 */
	public PaymentSchedule getPaymentSchedule(final ProductSku productSku) {
		Map<String, SkuOptionValue> optionValueMap = productSku.getOptionValueMap();
		if (optionValueMap == null) {
			return null;
		}
		
		SkuOption frequencyOption = getFrequencyOption(productSku.getProduct());
		if (frequencyOption == null) {
			return null;
		}
		
		SkuOptionValue sov = productSku.getSkuOptionValue(frequencyOption);
		if (sov == null) {
			return null;
		}
		
		return createPaymentSchedule(sov);
	}
	

	@Override
	public PaymentSchedule getPaymentSchedule(final ShoppingItemRecurringPrice shoppingItemRecurringPrice) {
		String sovKey = shoppingItemRecurringPrice.getPaymentScheduleName();
		SkuOptionValue sov = getSkuOptionService().findOptionValueByKey(sovKey);
		if (sov == null) {
			PaymentSchedule paymentSchedule = beanFactory.getBean(ContextIdNames.PAYMENT_SCHEDULE);
			paymentSchedule.setName(sovKey);
			
			/* There need to be a number for the quantity, because the summary table for recurring prices keys 
			the rows based on both frequency unit, and amount. */
			Quantity freq = new Quantity(1, sovKey); 
			paymentSchedule.setPaymentFrequency(freq);
			return paymentSchedule;
		} 
		
		return createPaymentSchedule(sov);
	}

	
	/**
	 * Creates a PaymentSchedule based on the skuOptionValue.
	 * @param skuOptionValue the sku option value to be used
	 * @return a {@link PaymentSchedule} if the sku option value denotes a recurring price, <code>null</code> otherwise.
	 */
	protected PaymentSchedule createPaymentSchedule(final SkuOptionValue skuOptionValue) {
		if (isPurchaseTime(skuOptionValue)) {
			return null;
		}
		
		PaymentSchedule paymentSchedule = beanFactory.getBean(ContextIdNames.PAYMENT_SCHEDULE);
		paymentSchedule.setName(skuOptionValue.getOptionValueKey());
		
		/* There need to be a number for the quantity, because the summary table for recurring prices keys 
		the rows based on both frequency unit, and amount. */
		Quantity freq = new Quantity(1, skuOptionValue.getOptionValueKey()); 
		paymentSchedule.setPaymentFrequency(freq);
		paymentSchedule.setOrdering(skuOptionValue.getOrdering());
		return paymentSchedule;
	}


	/**
	 * Determines whether a sku option value represents a purchase-time schedule.
	 * @param skuOptionValue the sku option value to be checked. It should belong to a skuOption that represents
	 * payment schedule information.
	 * @return <code>true</code> iff the sku option values represents a purchase-time schedule
	 */
	protected boolean isPurchaseTime(final SkuOptionValue skuOptionValue) {
		return skuOptionValue == null || PAY_NOW_OPTION_VALUE_KEY.equals(skuOptionValue.getOptionValueKey());
	}

	/**
	 * Get the {@link SkuOption} that has the information about recurring price schedules for the given product. 
	 * In this implementation, the sku option is found based on a constant key (FREQUENCY_OPTION_KEY).
	 * @param product the product
	 * @return the sku option that has the information. Might be <code>null</code> if there is no such sku option.
	 */
	protected SkuOption getFrequencyOption(final Product product) {
		return getFrequencyOption();
	}
	
	private SkuOption getFrequencyOption() {
		if (frequencyOption == null) {
			frequencyOption = skuOptionService.findByKey(FREQUENCY_OPTION_KEY);
		}
		return frequencyOption;
	}
	
	/**
	 * Set the sku option service.
	 * @param skuOptionService the SkuOptionService instance
	 */
	public void setSkuOptionService(final SkuOptionService skuOptionService) {
		this.skuOptionService = skuOptionService;
	}
	
	/**
	 * @return the SkuOptionService instance
	 */
	protected SkuOptionService getSkuOptionService() {
		return skuOptionService;
	}

	/**
	 * {@inheritDoc} 
	 * The implementation relies on the SkuOptions being fetched on the ProductType class.
	 */
	public boolean isPaymentScheduleCapable(final Product product) {
		return product.getProductType().getSkuOptions().contains(getFrequencyOption(product));
	}


	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
